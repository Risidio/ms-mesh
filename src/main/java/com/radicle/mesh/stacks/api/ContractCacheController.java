package com.radicle.mesh.stacks.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.radicle.mesh.stacks.model.Principal;
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheQuery;
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheUpdateResult;
import com.radicle.mesh.stacks.service.AppMapContractRepository;
import com.radicle.mesh.stacks.service.ApplicationRepository;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.GaiaHubReader;
import com.radicle.mesh.stacks.service.TokenFilterRepository;
import com.radicle.mesh.stacks.service.TokenRepository;
import com.radicle.mesh.stacks.service.domain.AppMapContract;
import com.radicle.mesh.stacks.service.domain.Application;
import com.radicle.mesh.stacks.service.domain.Token;
import com.radicle.mesh.stacks.service.domain.TokenFilter;
import com.radicle.mesh.stacksactions.service.domain.StacksTransaction;

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
	@Autowired private TokenFilterRepository tokenFilterRepository;
	@Value("${radicle.stax.admin-contract-address}") String adminContractAddress;
	@Value("${radicle.stax.admin-contract-name}") String adminContractName;
	@Autowired private GaiaHubReader gaiaHubReader;

	@PostMapping(value = "/v2/cache/update")
	public Token cacheUpdate(HttpServletRequest request, @RequestBody StacksTransaction stacksTransaction) throws JsonMappingException, JsonProcessingException {
		Token token = null;
		try {
			if (stacksTransaction.getFunctionName().startsWith("mint-")) {
				Long tokenCount = tokenRepository.countByContractId(stacksTransaction.getContractId());
				token = contractReader.readSpecificToken(stacksTransaction.getContractId(), tokenCount);
			} else if (stacksTransaction.getNftIndex() != null && stacksTransaction.getNftIndex() > -1) {
				token = contractReader.readSpecificToken(stacksTransaction.getContractId(), stacksTransaction.getNftIndex());
			} else if (stacksTransaction.getAssetHash() != null) {
			    // PageRequest pr = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "nftIndex"));
			    List<Token> tokenPage = tokenRepository.findByAssetHashAndEdition(stacksTransaction.getAssetHash(), 1L);
				if (tokenPage != null && !tokenPage.isEmpty()) {
					token = contractReader.readSpecificToken(stacksTransaction.getContractId(), tokenPage.get(0).getNftIndex());
				}
			}
		} catch (Exception e) {
			// cache miss - no need to report this as just means the client has asked for a asset thats not yet minted;
		}
		logger.info("Read cached token: " + token);
		if (token != null) {
			gaiaHubReader.index(token);
			List<Token> tokens = new ArrayList<Token>();
			tokens.add(token);
			CacheUpdateResult cr = new CacheUpdateResult(tokens, stacksTransaction);
			simpMessagingTemplate.convertAndSend("/queue/contract-news-" + stacksTransaction.getContractId(), cr);
		}
		return token;
	}

	@GetMapping(value = "/v2/build-cache")
	public String buildCache() throws JsonProcessingException {
		contractReader.buildCacheAsync();
		return "Cache builder called...";
	}

	@GetMapping(value = "/v2/build-cache/{contractId}")
	public String buildCache(@PathVariable String contractId) throws JsonProcessingException {
		contractReader.buildCacheAsync(contractId);
		return "Cache builder called for contract: " + contractId;
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

	@GetMapping(value = "/v2/token-filters")
	public List<TokenFilter> filters() {
		List<TokenFilter> filters = tokenFilterRepository.findAll();
		return filters;
	}

	@DeleteMapping(value = "/v2/token-filter/{filterId}")
	public Boolean deleteFilter(@PathVariable String filterId) {
		tokenFilterRepository.deleteById(filterId);
		return true;
	}

	@PostMapping(value = "/v2/token-filter")
	public TokenFilter postFilter(@RequestBody TokenFilter tokenFilter) {
		tokenFilter = tokenFilterRepository.save(tokenFilter);
		return tokenFilter;
	}

	@PutMapping(value = "/v2/token-filter")
	public TokenFilter putFilter(@RequestBody TokenFilter tokenFilter) {
		if (tokenFilter.getId() != null) {
			Optional<TokenFilter> tf = tokenFilterRepository.findById(tokenFilter.getId());
			if (tf.isPresent()) {
				tokenFilter.setId(tf.get().getId());
			}
		}
		tokenFilter = tokenFilterRepository.save(tokenFilter);
		return tokenFilter;
	}

	@PostMapping(value = "/v2/tokenFirstsByQuery")
	public List<Token> tokenFirstsByQuery(HttpServletRequest request, @RequestBody CacheQuery cacheQuery) throws JsonProcessingException {
		return tokens(cacheQuery);
	}

	@PostMapping(value = "/v2/tokenFirstsByQueryAsString")
	public Map<String, String> tokenFirstsByQueryAsString(HttpServletRequest request, @RequestBody CacheQuery cacheQuery) throws JsonProcessingException {
		List<Token> tokens = tokens(cacheQuery);
		Map<String, String> tokenMap = new HashMap<String, String>();
		ObjectWriter ow = mapper.writer();
		for (Token token : tokens) {
			tokenMap.put(token.getTokenInfo().getAssetHash(), ow.writeValueAsString(token));
		}
		return tokenMap;
	}
	
	private List<Token> tokens(CacheQuery cacheQuery) {
		List<Token> tokens = new ArrayList<Token>();
		for (String assetHash : cacheQuery.getHashes()) {
		    // PageRequest request = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "nftIndex"));
		    List<Token> tokenPage = tokenRepository.findByAssetHashAndEdition(assetHash, 1L);
			if (tokenPage != null && !tokenPage.isEmpty()) tokens.add(tokenPage.get(0));
		}
//		List<Token> tokens = tokenRepository.findByContractIdAndEdition(cacheQuery.getContractId(), 1L);
//		List<Token> listOutput =
//				tokens.stream()
//			           .filter(e -> cacheQuery.getHashes().stream().anyMatch(assetHash -> assetHash.equals(e.getTokenInfo().getAssetHash())))
//			           .collect(Collectors.toList());
		return tokens;
	}

	@GetMapping(value = "/v2/tokens")
	public List<Token> tokens(HttpServletRequest request) throws JsonProcessingException {
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

	@GetMapping(value = "/v2/tokenByAssetHashAndEdition/{assetHash}/{edition}")
	public Token tokensByAssetHashAndEdition(HttpServletRequest request, @PathVariable String assetHash, @PathVariable Long edition) throws JsonProcessingException {
	    List<Token> tokens = tokenRepository.findByAssetHashAndEdition(assetHash, edition);
		Token token = null;
		if (tokens != null && !tokens.isEmpty()) token = tokens.get(0);
		return token;
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
	public Token tokenByHash(HttpServletRequest request, @PathVariable String assetHash) {
	    // PageRequest pr = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "nftIndex"));
	    List<Token> tokenPage = tokenRepository.findByAssetHashAndEdition(assetHash, 1L);
		Token token = null;
		if (tokenPage != null && !tokenPage.isEmpty()) token = tokenPage.get(0);
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
