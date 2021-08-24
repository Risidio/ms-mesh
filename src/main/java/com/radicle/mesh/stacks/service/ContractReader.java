package com.radicle.mesh.stacks.service;

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
import com.radicle.mesh.stacks.service.domain.AppMapContract;
import com.radicle.mesh.stacks.service.domain.Application;
import com.radicle.mesh.stacks.service.domain.Bid;
import com.radicle.mesh.stacks.service.domain.Offer;
import com.radicle.mesh.stacks.service.domain.Token;
import com.radicle.mesh.stacks.service.domain.TokenContract;
import com.radicle.mesh.stacks.service.domain.TokenFilter;

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
	@Autowired private AppMapContractRepository appMapContractRepository;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private TokenRepository tokenRepository;
	@Autowired private TokenFilterRepository tokenFilterRepository;
	private AppMapContract registry;

	@Async
	public void buildCacheAsync() throws JsonProcessingException {
		buildCache();
	}
	
	public AppMapContract buildCache() throws JsonProcessingException {
		AppMapContract appMapContract = appMapContractRepository.findByAdminContractAddressAndAdminContractName(adminContractAddress, adminContractName);
		if (appMapContract == null) {
			appMapContract = new AppMapContract();
		}
		appMapContract.setAdminContractAddress(adminContractAddress);
		appMapContract.setAdminContractName(adminContractName);
		appMapContractRepository.deleteAll();
		readAppMap(appMapContract, adminContractAddress + "." + adminContractName, ReadOnlyFunctionNames.GET_CONTRACT_DATA);
		appMapContractRepository.save(appMapContract);
		readApplications(appMapContract);
		List<Application> applications = applicationRepository.findAll();
		if (applications != null) {
			for (Application application : applications) {
				logger.info("Applications -> " + application.toString());
				if (application.getStatus() > -1) {
					readTokens(application);
				}
			}
		}
		this.registry = appMapContract;
		return this.registry;
	}

	@SuppressWarnings("unchecked")
	private void readAppMap(AppMapContract appMapContract, String contractId, ReadOnlyFunctionNames fname) throws JsonMappingException, JsonProcessingException {
		String path = path(fname.getName());
		String response = readFromStacks(path, new String[0]);
		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
		Map<String, Object> data1 = (Map<String, Object>)data.get(fname.getName());
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
		applicationRepository.deleteAll();
		for (long i = 0; i < appMapContract.getAppCounter(); i++) {
			String arg1 = claritySerialiser.serialiseInt(BigInteger.valueOf(i));
			String response = readFromStacks(path, new String[] {arg1});
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			Application a = Application.fromMap(i, (Map)data.get(fname.getName()));
			readTokenContract(a);
			applicationRepository.save(a);
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
		Token token = null;
		tokenRepository.deleteByContractId(application.getContractId());
		for (long index = 0; index < tokenContract.getMintCounter(); index++) {
			token = readToken(application.getContractId(), index);
			logger.info("Applications -> Token Contract -> Token -> " + token.toString());
			if (token != null) {
				tokenRepository.save(token);
			}
		}
	}
	
	public Token readSpecificToken(String contractId, Long nftIndex) throws JsonMappingException, JsonProcessingException {
		Token token = readToken(contractId, nftIndex);
		if (token != null) {
			// do this is separate thread
			// see GaiaHubReader.buildSearchIndex
			// readMetaData(token);
			saveTokenToMongo(token);
		}
		return token;
	}
	
//	public Token readSpecificToken(String contractId, String assetHash) throws JsonMappingException, JsonProcessingException {
//		Token token = readToken(contractId, assetHash);
//		if (token != null) {
//			// do this is separate thread
//			// see GaiaHubReader.buildSearchIndex
//			// readMetaData(token);
//		}
//		return token;
//	}
	
	@Async
	public void readMetaData(Token token) {
		try {
			if (token.getTokenInfo().getEdition() == 1) {
				// no need to index editions as they all share the same meta data 
				logger.info("Reading meta data: #" + token.getNftIndex() + " for " + token.getEditionCounter() + " current editions.");
				gaiaHubReader.index(token);
			}
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
	}

	private Token readToken(String contractId, long index) throws JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_INDEX;
		String arg1 = claritySerialiser.serialiseUInt(BigInteger.valueOf(index));
		String path = path(contractId, fname.getName());
		String response = readFromStacks(path, new String[] {arg1});
		Token t = null;
		try {
			Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
			if (data != null) {
				Map<String, Object> data1 = (Map)data.get(fname.getName());
				if (data1 != null) {
					Token token = Token.fromMap(index, (Map)data.get(fname.getName()), contractId);
					try {
						token.setOfferHistory(readOffers(contractId, index, token.getOfferCounter()));
						token.setBidHistory(readBids(contractId, index, token.getBidCounter()));
					} catch (Exception e) {
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
	
	private void saveTokenToMongo(Token token) {
		TokenFilter tf = tokenFilterRepository.findByContractIdAndAssetHash(token.getContractId(), token.getTokenInfo().getAssetHash());
		if (tf != null) {
			return;
		}
		if (token != null) {
			Token t = tokenRepository.findByContractIdAndNftIndex(token.getContractId(), token.getNftIndex());
			if (t != null) {
				// an update existing as opposed to a save new
				token.setId(t.getId());
			}
		}
		try {
			tokenRepository.save(token);
		} catch (Exception e) {
			if (token != null && token.getTokenInfo() != null) {
				logger.info("Filter out duplicates: " + token.getNftIndex() + " : " + token.getTokenInfo().getAssetHash() + " : " + token.getTokenInfo().getEdition() + " : " + token.getContractId());
			} else {
				logger.info("Filter out duplicates: " + e.getMessage());
			}
		}
	}

//	private Token readToken(String contractId, String assetHash) throws JsonProcessingException {
//		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_HASH;
//		// String arg1 = claritySerialiser.serialiseUInt(assetHash);
//		String path = path(contractId, fname.getName());
//		String arg1 = claritySerialiser.serialiseHexString(assetHash);
//		String response = readFromStacks(path, new String[] { arg1 });
//		Token t = null;
//		Map<String, Object> data = clarityDeserialiser.deserialise(fname.getName(), response);
//		if (data != null) {
//			Map<String, Object> data1 = (Map)data.get(fname.getName());
//			if (data1 != null) {
//				ClarityType ct = (ClarityType)data1.get("nftIndex");
//				long nftIndex = ((BigInteger)ct.getValue()).longValue();
// 				Token token = Token.fromMap(nftIndex, (Map)data.get(fname.getName()), contractId);
//				token.setOfferHistory(readOffers(contractId, nftIndex, token.getOfferCounter()));
//				token.setBidHistory(readBids(contractId, nftIndex, token.getBidCounter()));
//				t = token;
//				saveTokenToMongo(token);
//			}
//		}
//		return t;
//	}

	private List<Offer> readOffers(String contractId, long nftIndex, long offerCounter) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_OFFER_AT_INDEX;
		String path = path(contractId, fname.getName());
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

	private List<Bid> readBids(String contractId, long nftIndex, long bidCounter) throws JsonMappingException, JsonProcessingException {
		ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_BID_AT_INDEX;
		String path = path(contractId, fname.getName());
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
