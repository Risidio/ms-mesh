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
	@Value("${radicle.stax.admin-contract-address}") String adminContractAddress;
	@Value("${radicle.stax.admin-contract-name}") String adminContractName;

	@PostMapping(value = "/v2/cache/update")
	public Token cacheUpdate(HttpServletRequest request, @RequestBody CacheUpdate cacheUpdate) throws JsonMappingException, JsonProcessingException {
		Application application = applicationRepository.findByContractId(cacheUpdate.getContractId());
		Token token = null;
		boolean newToken = false;
		if (application != null) {
			if (cacheUpdate.getFunctionName().startsWith("mint-")) {
				Integer index = application.getTokenContract().getTokens().size();
				token = contractReader.readSpecificToken(application, index.longValue());
				application.getTokenContract().getTokens().add(token);
				newToken = true;
			} else if (cacheUpdate.getNftIndex() != null && cacheUpdate.getNftIndex() > -1) {
				token = contractReader.readSpecificToken(application, cacheUpdate.getNftIndex());
			} else if (cacheUpdate.getAssetHash() != null) {
				token = contractReader.readSpecificToken(application, cacheUpdate.getAssetHash());
			}
			if (!newToken) {
				for (Token t : application.getTokenContract().getTokens()) {
					if (token != null && t.getTokenInfo() != null && token.getTokenInfo().getAssetHash().equals(t.getTokenInfo().getAssetHash())) {
						t = token;
					}
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

	@GetMapping(value = "/v2/build-cache")
	public AppMapContract buildCache() throws JsonProcessingException {
		AppMapContract registry = contractReader.buildCache();
		return registry;
	}

	@GetMapping(value = "/v2/tokensAllProjects")
	public AppMapContract tokensAllProjects(HttpServletRequest request) {
		AppMapContract ac = appMapContractRepository.findByAdminContractAddressAndAdminContractName(adminContractAddress, adminContractName);
		List<Application> applications = applicationRepository.findAll();
		ac.setApplications(applications);
		return ac;
	}

	@GetMapping(value = "/v2/tokensByProject/{contractId}")
	public AppMapContract appmap(HttpServletRequest request, @PathVariable String contractId) throws JsonProcessingException {
		AppMapContract registry = new AppMapContract();
		if (contractReader.getRegistry() == null) {
			buildCache();
		}
		registry.setAdministrator(contractReader.getRegistry().getAdministrator());
		registry.setAppCounter(contractReader.getRegistry().getAppCounter());
		Application application = applicationRepository.findByContractId(contractId);
		if (application != null) {
			contractIds.add(contractId);
			List<Application> apps = new ArrayList<Application>();
			apps.add(application);
			registry.setApplications(apps);
		}
		return registry;
	}

	@GetMapping(value = "/v2/tokensByProjectAndOwner/{contractId}/{stxAddress}")
	public List<Token> appmap(HttpServletRequest request, @PathVariable String contractId, @PathVariable String stxAddress) {
		List<Token> tokens = new ArrayList<Token>();
		Application application = applicationRepository.findByContractId(contractId);
		if (application != null) {
			for (Token t : application.getTokenContract().getTokens()) {
				if (t.getOwner().equalsIgnoreCase(stxAddress)) {
					tokens.add(t);
				}
			}
		}
		return tokens;
	}

	@GetMapping(value = "/v2/tokenByHash/{contractId}/{assetHash}")
	public Token getAssetByHash(HttpServletRequest request, @PathVariable String contractId, @PathVariable String assetHash) {
		Application application = applicationRepository.findByContractId(contractId);
		Token t = null;
		if (application.getTokenContract() != null && application.getTokenContract().getTokens() != null) {
			for (Token token : application.getTokenContract().getTokens()) {
				if (token.getTokenInfo().getAssetHash().equals(assetHash)) {
					t = token;
				}
			}
		}
		return t;
	}

	@GetMapping(value = "/v2/tokenByIndex/{contractId}/{nftIndex}")
	public Token getAssetByNftIndex(HttpServletRequest request, @PathVariable String contractId, @PathVariable Long nftIndex) {
		Application application = applicationRepository.findByContractId(contractId);
		Token t = null;
		if (application.getTokenContract() != null && application.getTokenContract().getTokens() != null) {
			for (Token token : application.getTokenContract().getTokens()) {
				if (token.getNftIndex() == nftIndex) {
					t = token;
				}
			}
		}
		return t;
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
