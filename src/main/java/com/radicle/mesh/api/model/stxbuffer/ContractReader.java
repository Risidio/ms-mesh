package com.radicle.mesh.api.model.stxbuffer;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.api.model.PostData;
import com.radicle.mesh.api.model.Principal;
import com.radicle.mesh.api.model.stxbuffer.types.AppMapContract;
import com.radicle.mesh.api.model.stxbuffer.types.Application;
import com.radicle.mesh.api.model.stxbuffer.types.Token;
import com.radicle.mesh.api.model.stxbuffer.types.TokenContract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "ContractReader")
public class ContractReader {

	private static String  subPath = "/v2/contracts/call-read/";
	private static String SLASH = "/";
	@Autowired private RestOperations restTemplate;
	@Value("${radicle.stax.base-path}") String basePath;
	@Value("${radicle.stax.sidecar-path}") String sidecarPath;
	@Value("${radicle.stax.admin-contract-address}") String adminContractAddress;
	@Value("${radicle.stax.admin-contract-name}") String adminContractName;
	@Autowired private ObjectMapper mapper;
	@Autowired private ClarityDeserialiser clarityDeserialiser;
	@Autowired private ClaritySerialiser claritySerialiser;
	
	public AppMapContract read() throws JsonProcessingException {
		AppMapContract appMapContract = new AppMapContract();
		readAppMap(appMapContract, adminContractAddress + "." + adminContractName, ReadOnlyFunctionNames.GET_CONTRACT_DATA);
		readApplications(appMapContract);
		List<Application> applications = appMapContract.getApplications();
		for (Application application : applications) {
			readTokenContract(application);
			readTokens(application);
		}
		return appMapContract;
	}

	private void readAppMap(AppMapContract appMapContract, String contractId, ReadOnlyFunctionNames fname) throws JsonMappingException, JsonProcessingException {
		String path = path(fname.getName());
		String response = readFromStacks(path, new String[0]);
		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
		Map<String, Object> data1 = (Map)data.get(fname.getName());
		ClarityType ct = (ClarityType)data1.get("appCounter");
		appMapContract.setAppCounter(((BigInteger)ct.getValue()).longValue());
		ct = (ClarityType)data1.get("administrator");
		appMapContract.setAdministrator(((String)ct.getValueHex()));
	}

	private void readApplications(AppMapContract appMapContract) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_APP;
		String path = path(fname.getName());
		for (long i = 0; i < appMapContract.getAppCounter(); i++) {
			String arg1 = claritySerialiser.serialiseInt(BigInteger.valueOf(i));
			String response = readFromStacks(path, new String[] {arg1});
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			Application a = Application.fromMap(i, (Map)data.get(fname.getName()));
			appMapContract.addApplication(a);
		}
	}

	private void readTokenContract(Application application) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_CONTRACT_DATA;
		String path = path(application.getContractId(), fname.getName());
		String response = readFromStacks(path, new String[0]);
		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
		TokenContract tc = TokenContract.fromMap(data);
		application.setTokenContract(tc);
	}

	private void readTokens(Application application) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_INDEX;
		String path = path(application.getContractId(), fname.getName());
		TokenContract tokenContract = application.getTokenContract();
		for (long index = 0; index < tokenContract.getMintCounter(); index++) {
			// BigInteger unsigned = ClaritySerialiser.convertTwosCompliment(BigInteger.valueOf(index));
			String arg1 = claritySerialiser.serialiseUInt(BigInteger.valueOf(index));
			String response = readFromStacks(path, new String[] {arg1});
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			if (data != null) {
				Map<String, Object> data1 = (Map)data.get(fname.getName());
				if (data1 != null) {
	 				Token token = Token.fromMap(index, (Map)data.get(fname.getName()));
					tokenContract.addToken(token);
				}
			}
		}
	}

	private String readFromStacks(String path, String[] args) throws JsonProcessingException  {
		Principal p = new Principal("POST", path, new PostData(adminContractAddress, args));
		String response = readFromStacks(p);
		return response;
	}

	private String path(String function)  {
		String path = basePath + subPath + adminContractAddress + SLASH + adminContractName + SLASH + function;
		return path;
	}

	private String path(String contractId, String function)  {
		String[] cparts = contractId.split("\\.");
		String path = basePath + subPath + cparts[0] + SLASH + cparts[1] + SLASH + function;
		return path;
	}

	private String readFromStacks(Principal principal) throws JsonProcessingException {
		String jsonInString = mapper.writeValueAsString(principal.getPostData());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(jsonInString, headers);
		
		ResponseEntity<String> response = null;
		response = restTemplate.exchange(principal.getPath(), HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}

}
