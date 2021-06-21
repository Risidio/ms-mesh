package com.radicle.mesh.stacks.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.stacks.model.Principal;
import com.radicle.mesh.stacks.model.Shaker;
import com.radicle.mesh.stacks.model.stxbuffer.ContractReader;
import com.radicle.mesh.stacks.model.stxbuffer.types.AppMapContract;
import com.radicle.mesh.stacks.model.stxbuffer.types.Application;
import com.radicle.mesh.stacks.model.stxbuffer.types.Token;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class StaxController {

	@Autowired private RestOperations restTemplate;
	@Value("${radicle.stax.base-path}") String basePath;
	@Value("${radicle.stax.sidecar-path}") String sidecarPath;
	@Autowired private ObjectMapper mapper;
	@Autowired private ContractReader contractReader;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	private Set<String> contractIds = new HashSet<>();
	private Map<String, Set<String>> contractIdNftIndexes = new HashMap<>();

//    @PostConstruct
//    public void init() throws JsonProcessingException {
//    	contractReader.read();
//    }

	@Scheduled(fixedDelay=240000)
	public void pushData() throws JsonProcessingException {
		AppMapContract registry = contractReader.read();
		simpMessagingTemplate.convertAndSend("/queue/contract-news", registry);
	}

	@Scheduled(fixedDelay=10000)
	public void pushDataAboutContract() throws JsonProcessingException {
		AppMapContract registry = contractReader.getRegistry();
		if (registry == null) return;
		AppMapContract registry1 = new AppMapContract();
		registry1.setAdministrator(registry.getAdministrator());
		registry1.setAppCounter(1);
		for (String contractId : contractIds) {
			Application application = getApplication(contractId);
			if (application != null) {
				List<Application> apps = new ArrayList<Application>();
				apps.add(application);
				registry1.setApplications(apps);
				simpMessagingTemplate.convertAndSend("/queue/contract-news-" + contractId, registry1);
			}
		}
	}
	
	@Scheduled(fixedDelay=5000)
	public void pushDataAboutNft() throws JsonProcessingException {
		for (String contractId : contractIdNftIndexes.keySet()) {
			Set<String> hashes = contractIdNftIndexes.get(contractId);
			if (hashes != null) {
				for (String hash : hashes) {
					Token t = getToken(contractId, hash);
					if (t != null) {
						simpMessagingTemplate.convertAndSend("/queue/contract-news-" + contractId + "-" + hash, t);
					}
				}
			}
		}
	}
	
	@PostMapping(value = "/v1/secure/shaker")
	public Shaker superAdmin(HttpServletRequest request) {
		String user = (String) request.getSession().getAttribute("USERNAME");
		if (user == null || !user.equals("mijoco.id.blockstack")) {
			return null;
		}
		return new Shaker();
	}

	private Application getApplication(String contractId) {
		AppMapContract registry = contractReader.getRegistry();
		if (registry == null || registry.getApplications() == null) return null;
		Application application = null;
		for (Application a : registry.getApplications()) {
			if (a.getContractId().contentEquals(contractId)) {
				application = a;
			}
		}
		return application;
	}

	private Token getToken(String contractId, String assetHash) {
		AppMapContract registry = contractReader.getRegistry();
		if (registry == null || registry.getApplications() == null) return null;
		Token token = null;
		for (Application a : registry.getApplications()) {
			if (a.getContractId().contentEquals(contractId)) {
				for (Token t : a.getTokenContract().getTokens()) {
					if (t.getTokenInfo() != null && t.getTokenInfo().getAssetHash().equals(assetHash)) {
						token = t;
					}
				}
			}
		}
		return token;
	}

	@GetMapping(value = "/v2/registry")
	public AppMapContract appmap(HttpServletRequest request) {
		return contractReader.getRegistry();
	}

	@GetMapping(value = "/v2/registrate")
	public AppMapContract registrate(HttpServletRequest request) throws JsonProcessingException {
		AppMapContract registry = contractReader.read();
		return registry;
	}

	@GetMapping(value = "/v2/registry/{contractId}")
	public AppMapContract appmap(HttpServletRequest request, @PathVariable String contractId) {
		AppMapContract registry = new AppMapContract();
		registry.setAdministrator(contractReader.getRegistry().getAdministrator());
		registry.setAppCounter(contractReader.getRegistry().getAppCounter());
		Application application = getApplication(contractId);
		if (application != null) {
			contractIds.add(contractId);
			List<Application> apps = new ArrayList<Application>();
			apps.add(application);
			registry.setApplications(apps);
		}
		return registry;
	}

	@GetMapping(value = "/v2/registry/{contractId}/{assetHash}")
	public Token register(HttpServletRequest request, @PathVariable String contractId, @PathVariable String assetHash) {
		Set<String> indexes = contractIdNftIndexes.get(contractId);
		if (indexes == null) {
			indexes = new HashSet<>();
		}
		indexes.add(assetHash);
		contractIdNftIndexes.put(contractId, indexes);
		return getToken(contractId, assetHash);
	}

	@PostMapping(value = "/v2/accounts")
	public String accounts(HttpServletRequest request, @RequestBody Principal principal) throws JsonMappingException, JsonProcessingException {

		String url = basePath + principal.getPath();
		if (principal.getPath().indexOf("/sidecar/v1") > -1) {
			url = sidecarPath + principal.getPath();
		}
		ResponseEntity<String> response = null;
		if (principal.getHttpMethod() != null && principal.getHttpMethod().equalsIgnoreCase("POST")) {
			response = restTemplate.exchange(url, HttpMethod.POST, getRequestEntity(principal), String.class);
		} else {
			response = restTemplate.exchange(url, HttpMethod.GET, getRequestEntity(principal), String.class);
		}
		return response.getBody();
	}

//	@PostMapping(value = "/v2/contract/read")
//	public String contractRead(HttpServletRequest request, @RequestBody ReadContract txOptions) {
//		String url = basePath + "/v2/contracts/call-read/" + txOptions.getContractAddress() + "/" + txOptions.getContractName() + "/" + txOptions.getFunctionName();
//		ResponseEntity<String> response = null;
//		String jsonInString = convertMessage(txOptions.getRcp());
//		HttpEntity<String> e = new HttpEntity<String>(jsonInString, getHeaders());
//		response = restTemplate.exchange(url, HttpMethod.POST, e, String.class);
//		return response.getBody();
//	}

	@PostMapping(value = "/v2/broadcast", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public String broadcast(HttpServletRequest request, @RequestBody byte[] payload) throws IOException {

	    //byte [] content = payload.getBytes();
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/octet-stream");
	    headers.set("Accept", "text/plain");
	    HttpEntity<byte[]> requestEntity = new HttpEntity<>(payload, headers);
		String url = basePath + "/v2/transactions";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}

	@PostMapping(value = "/v2/clarity")
	public Principal clarity(HttpServletRequest request, @RequestBody Principal principal) {

		System.out.println("AssetController.clarity()");
		return principal;
	}

	private HttpEntity<String> getRequestEntity(Principal principal) {
		try {
			if (principal.getHttpMethod() == null || !principal.getHttpMethod().equalsIgnoreCase("POST")) {
				return new HttpEntity<String>(new HttpHeaders());
			} else {
				String jsonInString = convertMessage(principal.getPostData());
				return new HttpEntity<String>(jsonInString, getHeaders());
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	private String convertMessage(Object model) {
		try {
			return mapper.writeValueAsString(model);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private HttpHeaders getHeaders() {
//		String val = " "; // environment.getProperty("BTC_ACCESS_KEY_ID");
//		String auth = "BTC_ACCESS_KEY_ID" + ":" + val;
//		String encodedAuth = new String(Base64.getEncoder().encode(auth.getBytes(Charset.forName("UTF8"))));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		// headers.set("Authorization", "Basic " + encodedAuth.toString());
		// headers.setContentLength(jsonInString.length());
		return headers;
	}
}
