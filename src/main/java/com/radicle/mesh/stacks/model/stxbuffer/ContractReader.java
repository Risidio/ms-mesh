package com.radicle.mesh.stacks.model.stxbuffer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.stacks.model.PostData;
import com.radicle.mesh.stacks.model.Principal;
import com.radicle.mesh.stacks.model.stxbuffer.types.AppMapContract;
import com.radicle.mesh.stacks.model.stxbuffer.types.Application;
import com.radicle.mesh.stacks.model.stxbuffer.types.Bid;
import com.radicle.mesh.stacks.model.stxbuffer.types.Offer;
import com.radicle.mesh.stacks.model.stxbuffer.types.Token;
import com.radicle.mesh.stacks.model.stxbuffer.types.TokenContract;

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

    private static final String POST = "POST";
	private static final String ADMINISTRATOR = "administrator";
	private static final String APP_COUNTER = "appCounter";
	private static final Logger logger = LogManager.getLogger(ContractReader.class);
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
	@Autowired private GaiaHubReader gaiaHubReader;
	private AppMapContract registry;

	public AppMapContract getRegistry() {
		return registry;
	}

	public AppMapContract read() throws JsonProcessingException {
		AppMapContract appMapContract = new AppMapContract();
		appMapContract.setAdminContractAddress(adminContractAddress);
		appMapContract.setAdminContractName(adminContractName);
		readAppMap(appMapContract, adminContractAddress + "." + adminContractName, ReadOnlyFunctionNames.GET_CONTRACT_DATA);
		readApplications(appMapContract);
		List<Application> applications = appMapContract.getApplications();
		if (applications != null) {
			for (Application application : applications) {
				logger.info("Applications -> " + application.toString());
				if (application.getStatus() > -1) {
					readTokenContract(application);
					readTokens(application);
				}
			}
		}
		this.registry = appMapContract;
		// logger.info("Applications -> registry -> " + registry);
		return this.registry;
	}

	public Application read(AppMapContract appMapContract, String contractId) throws JsonProcessingException {
		Application appl = null;
		if (appMapContract.getApplications() == null) return null;
		for (Application application : appMapContract.getApplications()) {
			if (application.getContractId().equals(contractId)) {
				readTokenContract(application);
				readTokens(application);
				appl = application;
			}
		}
		return appl;
	}

	public Token read(AppMapContract appMapContract, String contractId, long nftIndex) throws JsonProcessingException {
		List<Application> applications = appMapContract.getApplications();
		if (applications == null) return null;
		Token t = null;
		for (Application application : applications) {
			if (application.getContractId().equals(contractId)) {
				for (Token token : application.getTokenContract().getTokens()) {
					if (token.getNftIndex() == nftIndex) {
						token = readToken(application, nftIndex);
						t = token;
					}
				}
			}
		}
		return t;
	}

	public Token read(AppMapContract appMapContract, String contractId, String assetHash) throws JsonProcessingException {
		List<Application> applications = appMapContract.getApplications();
		if (applications == null) return null;
		Token t = null;
		for (Application application : applications) {
			if (application.getContractId().equals(contractId)) {
				for (Token token : application.getTokenContract().getTokens()) {
					if (token.getTokenInfo().getAssetHash().equals(assetHash)) {
						token = readToken(application, assetHash);
						t = token;
					}
				}
			}
		}
		return t;
	}

	private void readAppMap(AppMapContract appMapContract, String contractId, ReadOnlyFunctionNames fname) throws JsonMappingException, JsonProcessingException {
		String path = path(fname.getName());
		String response = readFromStacks(path, new String[0]);
		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
		Map<String, Object> data1 = (Map)data.get(fname.getName());
		if (data1 != null) {
			if (data1.containsKey(APP_COUNTER)) {
				ClarityType ct = (ClarityType) data1.get(APP_COUNTER);
				appMapContract.setAppCounter(((BigInteger)ct.getValue()).longValue());
				ct = (ClarityType)data1.get(ADMINISTRATOR);
				appMapContract.setAdministrator(((String)ct.getValueHex()));
			} else {
				logger.info("No mapping for appCounter?");
			}
		}
	}

	private void readApplications(AppMapContract appMapContract) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_APP;
		String path = path(fname.getName());
		for (long i = 0; i < appMapContract.getAppCounter(); i++) {
			String arg1 = claritySerialiser.serialiseInt(BigInteger.valueOf(i));
			String response = readFromStacks(path, new String[] {arg1});
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			Application a = Application.fromMap(i, (Map)data.get(fname.getName()));
			if (a != null && a.getStatus() > -1) appMapContract.addApplication(a);
		}
	}

	private void readTokenContract(Application application) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_CONTRACT_DATA;
		String path = path(application.getContractId(), fname.getName());
		String response = readFromStacks(path, new String[0]);
		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
		TokenContract tc = TokenContract.fromMap(data);
		logger.info("Applications -> Token Contract -> " + tc.toString());
		application.setTokenContract(tc);
	}

	private void readTokens(Application application) throws JsonMappingException, JsonProcessingException {
		TokenContract tokenContract = application.getTokenContract();
		for (long index = 0; index < tokenContract.getMintCounter(); index++) {
			Token token = readToken(application, index);
			if (token != null) {
				// logger.info("Applications -> Token Contract -> Token -> " + token.toString());
				tokenContract.addToken(token);
				readMetaData(application, token);
			}
		}
	}
	
	public Token readSpecificToken(Application application, Long nftIndex) throws JsonMappingException, JsonProcessingException {
		TokenContract tokenContract = application.getTokenContract();
		Token token = readToken(application, nftIndex);
		if (token != null) {
			// logger.info("Applications -> Token Contract -> Token -> " + token.toString());
			tokenContract.addToken(token);
			readMetaData(application, token);
		}
		return token;
	}
	
	@Async
	public void readMetaData(Application application, Token token) {
		try {
			gaiaHubReader.read(application, token);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
	}

	private Token readToken(Application application, long index) throws JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_INDEX;
		String arg1 = claritySerialiser.serialiseUInt(BigInteger.valueOf(index));
		String path = path(application.getContractId(), fname.getName());
		String response = readFromStacks(path, new String[] {arg1});
		Token t = null;
		try {
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			if (data != null) {
				Map<String, Object> data1 = (Map)data.get(fname.getName());
				if (data1 != null) {
					Token token = Token.fromMap(index, (Map)data.get(fname.getName()));
					try {
						token.setOfferHistory(readOffers(application, index, token.getOfferCounter()));
						token.setBidHistory(readBids(application, index, token.getBidCounter()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t = token;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return t;
	}

	private Token readToken(Application application, String assetHash) throws JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_HASH;
		// String arg1 = claritySerialiser.serialiseUInt(assetHash);
		String path = path(application.getContractId(), fname.getName());
		String arg1 = claritySerialiser.serialiseHexString(assetHash);
		String response = readFromStacks(path, new String[] { arg1 });
		Token t = null;
		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
		if (data != null) {
			Map<String, Object> data1 = (Map)data.get(fname.getName());
			if (data1 != null) {
				ClarityType ct = (ClarityType)data1.get("nftIndex");
				long nftIndex = ((BigInteger)ct.getValue()).longValue();
 				Token token = Token.fromMap(nftIndex, (Map)data.get(fname.getName()));
				token.setOfferHistory(readOffers(application, nftIndex, token.getOfferCounter()));
				token.setBidHistory(readBids(application, nftIndex, token.getBidCounter()));
				t = token;
			}
		}
		return t;
	}

	private List<Offer> readOffers(Application application, long nftIndex, long offerCounter) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_OFFER_AT_INDEX;
		String path = path(application.getContractId(), fname.getName());
		String arg1 = claritySerialiser.serialiseUInt(BigInteger.valueOf(nftIndex));
		List<Offer> offers = new ArrayList();
		for (long index = 0; index < offerCounter; index++) {
			String arg2 = claritySerialiser.serialiseUInt(BigInteger.valueOf(index));
			String response = readFromStacks(path, new String[] {arg1, arg2});
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			if (data != null) {
				Map<String, Object> data1 = (Map)data.get(fname.getName());
				if (data1 != null) {
	 				Offer offer = Offer.fromMap((Map)data.get(fname.getName()));
	 				offers.add(offer);
				}
			}
		}
		return offers;
	}

	private List<Bid> readBids(Application application, long nftIndex, long bidCounter) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_BID_AT_INDEX;
		String path = path(application.getContractId(), fname.getName());
		String arg1 = claritySerialiser.serialiseUInt(BigInteger.valueOf(nftIndex));
		List<Bid> bids = new ArrayList();
		for (long index = 0; index < bidCounter; index++) {
			String arg2 = claritySerialiser.serialiseUInt(BigInteger.valueOf(index));
			String response = readFromStacks(path, new String[] {arg1, arg2});
			try {
				Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
				if (data != null) {
					Map<String, Object> data1 = (Map)data.get(fname.getName());
					if (data1 != null) {
						Bid bid = Bid.fromMap((Map)data.get(fname.getName()));
						bids.add(bid);
					}
				}
			} catch (Exception e) {
				// Optional none some how returned for the bid at this index;
				// ?bids.add(new Bid());
			}
		}
		return bids;
	}

	private String readFromStacks(String path, String[] args) throws JsonProcessingException  {
		Principal p = new Principal(POST, path, new PostData(adminContractAddress, args));
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
