package com.radicle.mesh.stacks.service;

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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.stacks.model.stxbuffer.gaia.AppsModel;
import com.radicle.mesh.stacks.model.stxbuffer.gaia.UserAppMaps;
import com.radicle.mesh.stacks.service.domain.Application;
import com.radicle.mesh.stacks.service.domain.Token;

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
@TypeAlias(value = "GaiaHubReader")
public class GaiaHubReader {

    private static final Logger logger = LogManager.getLogger(GaiaHubReader.class);
	@Value("${radicle.gaia.huburl}") String basePath;
	@Value("${radicle.search.indexurl}") String indexUrl;
	@Autowired private RestOperations restTemplate;
	@Autowired private ObjectMapper mapper;
	@Autowired private TokenRepository tokenRepository;
	@Autowired private ApplicationRepository applicationRepository;

	@Async
	public void buildSearchIndexAsync() throws JsonProcessingException {
		buildSearchIndex();
	}
	
	public void buildSearchIndex() throws JsonProcessingException {
		List<Application> applications = applicationRepository.findAll();
		if (applications != null) {
			for (Application application : applications) {
				if (application.getStatus() > -1) {
					List<Token> tokens = tokenRepository.findByContractIdAndEdition(application.getContractId(), 1L);
					for (Token token : tokens) {
						logger.info("Indexing: Token #" + token.getNftIndex() + " Edition: " + token.getTokenInfo().getEdition());
						index(token);
					}
				}
			}
		}
	}

	public void index(Token token) throws JsonProcessingException {
		String metaDataUrl = token.getTokenInfo().getMetaDataUrl();
		try {
			HttpEntity<String> he = new HttpEntity<String>(new HttpHeaders());
			HttpEntity<String> response = restTemplate.exchange(metaDataUrl, HttpMethod.GET, he, String.class);
			String assetJson = response.getBody();
			sendToSearch(token.getContractId(), assetJson);
		} catch (RestClientException e) {
			// logger.error("Nothing found at: " + metaDataUrl);
		}
	}
	
	private String sendToSearch(String projectId, String jsonBlob) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(jsonBlob, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(indexUrl + projectId, HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}
	
	public UserAppMaps parseApps(String profile, String gaiaUsername) throws JsonMappingException, JsonProcessingException {
		Map<String, Object> rates = mapper.readValue(profile, new TypeReference<Map<String, Object>>() {});
		String sbw = mapper.writeValueAsString(rates.get(gaiaUsername));
		Map<String, Object> rates2 = mapper.readValue(sbw, new TypeReference<Map<String, Object>>() {});
		sbw = mapper.writeValueAsString(rates2.get("profile"));
		Map<String, Object> rates3 = mapper.readValue(sbw, new TypeReference<Map<String, Object>>() {});
		sbw = mapper.writeValueAsString(rates3.get("apps"));
		Map<String, Object> visitedAppMapFormat1 = mapper.readValue(sbw, new TypeReference<Map<String, Object>>() {});
		sbw = mapper.writeValueAsString(rates3.get("appsMeta"));
		Map<String, AppsModel> visitedAppMapFormat2 = mapper.readValue(sbw, new TypeReference<Map<String, AppsModel>>() {});
		
		for (String key : visitedAppMapFormat1.keySet()) {
			if (!visitedAppMapFormat2.containsKey(key)) {
				AppsModel appsModel = new AppsModel();
				appsModel.setStorage((String) visitedAppMapFormat1.get(key));
				visitedAppMapFormat2.put(key, appsModel);
			}
		}
		UserAppMaps uam = new UserAppMaps();
		uam.setApps(visitedAppMapFormat2);
		return uam;
	}
}
