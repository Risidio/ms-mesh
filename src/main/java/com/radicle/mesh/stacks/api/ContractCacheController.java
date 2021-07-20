package com.radicle.mesh.stacks.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheUpdate;
import com.radicle.mesh.stacks.service.AppMapContractRepository;
import com.radicle.mesh.stacks.service.ApplicationRepository;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.domain.AppMapContract;
import com.radicle.mesh.stacks.service.domain.Application;
import com.radicle.mesh.stacks.service.domain.Token;

@RestController
public class ContractCacheController {

	@Autowired private RestOperations restTemplate;
	private static final Logger logger = LogManager.getLogger(ContractCacheController.class);
	@Value("${radicle.stax.base-path}") String basePath;
	@Value("${radicle.stax.sidecar-path}") String sidecarPath;
	@Autowired private ObjectMapper mapper;
	@Autowired private ContractReader contractReader;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	private Set<String> contractIds = new HashSet<>();
	private Map<String, Set<String>> contractIdNftIndexes = new HashMap<>();
	@Autowired private AppMapContractRepository appMapContractRepository;
	@Autowired private ApplicationRepository applicationRepository;

	@PostMapping(value = "/v2/cache/update")
	public Token cacheUpdate(HttpServletRequest request, @RequestBody CacheUpdate cacheUpdate) throws JsonMappingException, JsonProcessingException {
		Application application = applicationRepository.findByContractId(cacheUpdate.getContractId());
		Token token = null;
		if (application != null) {
			if (cacheUpdate.getNftIndex() != null && cacheUpdate.getNftIndex() > -1) {
				token = contractReader.readSpecificToken(application, cacheUpdate.getNftIndex());
			} else {
				token = contractReader.readSpecificToken(application, cacheUpdate.getAssetHash());
			}
			for (Token t : application.getTokenContract().getTokens()) {
				if (token != null && t.getTokenInfo() != null && token.getTokenInfo().getAssetHash().equals(t.getTokenInfo().getAssetHash())) {
					t = token;
				}
			}
			applicationRepository.save(application);
		}
		logger.info("Read cached token: " + token);
		if (token != null) {
			simpMessagingTemplate.convertAndSend("/queue/contract-news-" + cacheUpdate.getContractId() + "-" + cacheUpdate.getAssetHash(), token);
		}
		return token;
	}

	@GetMapping(value = "/v2/registry")
	public AppMapContract appmap(HttpServletRequest request) {
		return contractReader.getRegistry();
	}

	@GetMapping(value = "/v2/build-cache")
	public AppMapContract registrate() throws JsonProcessingException {
		AppMapContract registry = contractReader.buildCache();
		return registry;
	}

	@GetMapping(value = "/v2/registry/{contractId}")
	public AppMapContract appmap(HttpServletRequest request, @PathVariable String contractId) throws JsonProcessingException {
		AppMapContract registry = new AppMapContract();
		if (contractReader.getRegistry() == null) {
			registrate();
		}
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

	@GetMapping(value = "/v2/assets/{contractId}/{stxAddress}")
	public List<Token> appmap(HttpServletRequest request, @PathVariable String contractId, @PathVariable String stxAddress) {
		AppMapContract registry = new AppMapContract();
		List<Token> tokens = new ArrayList<Token>();
		Application application = getApplication(contractId);
		if (application != null) {
			for (Token t : application.getTokenContract().getTokens()) {
				if (t.getOwner().equalsIgnoreCase(stxAddress)) {
					tokens.add(t);
				}
			}
		}
		return tokens;
	}

	@GetMapping(value = "/v2/registry/{contractId}/{assetHash}")
	public Token getAssetByHash(HttpServletRequest request, @PathVariable String contractId, @PathVariable String assetHash) {
		Set<String> indexes = contractIdNftIndexes.get(contractId);
		if (indexes == null) {
			indexes = new HashSet<>();
		}
		indexes.add(assetHash);
		contractIdNftIndexes.put(contractId, indexes);
		return getToken(contractId, assetHash);
	}

	@GetMapping(value = "/v2/registry/{contractId}/{nftIndex}")
	public Token getAssetByNftIndex(HttpServletRequest request, @PathVariable String contractId, @PathVariable Long nftIndex) {
		return getToken(contractId, nftIndex);
	}

	/**
	 * Read user account information from the local node 
	 * (instance of stacks blockchain running on localhost)
	 * @param request
	 * @param principal
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
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

	/**
	 * Broadcast a signed transaction to the local node
	 * (instance of stacks blockchain running on localhost)
	 * @param request
	 * @param payload
	 * @return
	 * @throws IOException
	 */
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

	private Token getToken(String contractId, Long nftIndex) {
		AppMapContract registry = contractReader.getRegistry();
		if (registry == null || registry.getApplications() == null) return null;
		for (Application a : registry.getApplications()) {
			if (a.getContractId().contentEquals(contractId)) {
				for (Token t : a.getTokenContract().getTokens()) {
					if (t.getTokenInfo() != null && t.getNftIndex() == nftIndex) {
						return t;
					}
				}
			}
		}
		return null;
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
