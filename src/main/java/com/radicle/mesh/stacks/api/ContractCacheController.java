package com.radicle.mesh.stacks.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheQuery;
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheUpdate;
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheUpdateResult;
import com.radicle.mesh.stacks.service.AppMapContractRepository;
import com.radicle.mesh.stacks.service.ApplicationRepository;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.TokenRepository;
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
	@Autowired private AppMapContractRepository appMapContractRepository;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private TokenRepository tokenRepository;
	@Value("${radicle.stax.admin-contract-address}") String adminContractAddress;
	@Value("${radicle.stax.admin-contract-name}") String adminContractName;

	@PostMapping(value = "/v2/cache/update")
	public Token cacheUpdate(HttpServletRequest request, @RequestBody CacheUpdate cacheUpdate) throws JsonMappingException, JsonProcessingException {
		Token token = null;
		if (cacheUpdate.getFunctionName().startsWith("mint-")) {
			Long tokenCount = tokenRepository.countByContractId(cacheUpdate.getContractId());
			token = contractReader.readSpecificToken(cacheUpdate.getContractId(), tokenCount);
		} else if (cacheUpdate.getNftIndex() != null && cacheUpdate.getNftIndex() > -1) {
			token = contractReader.readSpecificToken(cacheUpdate.getContractId(), cacheUpdate.getNftIndex());
		} else if (cacheUpdate.getAssetHash() != null) {
			token = contractReader.readSpecificToken(cacheUpdate.getContractId(), cacheUpdate.getAssetHash());
		}
		logger.info("Read cached token: " + token);
		if (token != null) {
			List<Token> tokens = new ArrayList<Token>();
			tokens.add(token);
			CacheUpdateResult cr = new CacheUpdateResult(tokens, cacheUpdate);
			simpMessagingTemplate.convertAndSend("/queue/contract-news-" + cacheUpdate.getContractId(), cr);
		}
		return token;
	}

	@GetMapping(value = "/v2/build-cache")
	public String buildCache() throws JsonProcessingException {
		contractReader.buildCacheAsync();
		return "Cache builder called...";
	}

	@GetMapping(value = "/v2/registry")
	public AppMapContract tokensAllProjects(HttpServletRequest request) {
		AppMapContract ac = appMapContractRepository.findByAdminContractAddressAndAdminContractName(adminContractAddress, adminContractName);
		List<Application> applications = applicationRepository.findAll();
		ac.setApplications(applications);
		return ac;
	}

	@GetMapping(value = "/v2/registry/{contractId}")
	public AppMapContract tokensAllProjects(HttpServletRequest request, @PathVariable String contractId) {
		AppMapContract ac = appMapContractRepository.findByAdminContractAddressAndAdminContractName(adminContractAddress, adminContractName);
		Application application = applicationRepository.findByContractId(contractId);
		List<Application> apps = new ArrayList<Application>();
		apps.add(application);
		ac.setApplications(apps);
		return ac;
	}

	@PostMapping(value = "/v2/tokensByQuery")
	public List<Token> tokensByQuery(HttpServletRequest request, @RequestBody CacheQuery cacheQuery) throws JsonProcessingException {
		// TODO: move this query inside mongo
		List<Token> tokens = tokenRepository.findByContractId(cacheQuery.getContractId());
		List<Token> listOutput =
				tokens.stream()
			           .filter(e -> cacheQuery.getHashes().stream().anyMatch(assetHash -> assetHash.equals(e.getTokenInfo().getAssetHash())))
			           .collect(Collectors.toList());
		return listOutput;
	}

	@GetMapping(value = "/v2/tokens")
	public List<Token> tokensByContractIdAndEdition(HttpServletRequest request) throws JsonProcessingException {
		List<Token> tokens = tokenRepository.findAll();
		return tokens;
	}

	@GetMapping(value = "/v2/tokensByContractId/{contractId}")
	public List<Token> tokensByContractIdAndEdition(HttpServletRequest request, @PathVariable String contractId) throws JsonProcessingException {
		List<Token> tokens = tokenRepository.findByContractId(contractId);
		return tokens;
	}

	@GetMapping(value = "/v2/tokensByContractIdAndEdition/{contractId}/{edition}")
	public List<Token> tokensByContractIdAndEdition(HttpServletRequest request, @PathVariable String contractId, @PathVariable Long edition) throws JsonProcessingException {
		List<Token> tokens = tokenRepository.findByContractIdAndEdition(contractId, edition);
		return tokens;
	}

	@GetMapping(value = "/v2/tokensByProject/{contractId}")
	public List<Token> tokensByProject(HttpServletRequest request, @PathVariable String contractId) throws JsonProcessingException {
		List<Token> tokens = tokenRepository.findByContractId(contractId);
		return tokens;
	}

	@GetMapping(value = "/v2/tokensByProjectAndOwner/{contractId}/{stxAddress}")
	public List<Token> tokensByProjectAndOwner(HttpServletRequest request, @PathVariable String contractId, @PathVariable String stxAddress) {
		List<Token> tokens = tokenRepository.findByContractIdAndOwner(contractId, stxAddress);
		return tokens;
	}

	@GetMapping(value = "/v2/tokenByHash/{assetHash}")
	public Token tokenByHash(HttpServletRequest request, @PathVariable String contractId, @PathVariable String assetHash) {
		Token token = tokenRepository.findByAssetHash(assetHash);
		return token;
	}

	@GetMapping(value = "/v2/tokenByIndex/{contractId}/{nftIndex}")
	public Token tokenByIndex(HttpServletRequest request, @PathVariable String contractId, @PathVariable Long nftIndex) {
		Token token = tokenRepository.findByContractIdAndNftIndex(contractId, nftIndex);
		return token;
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
