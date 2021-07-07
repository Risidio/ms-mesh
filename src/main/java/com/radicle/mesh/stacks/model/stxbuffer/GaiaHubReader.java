package com.radicle.mesh.stacks.model.stxbuffer;

import java.util.HashMap;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.stacks.model.stxbuffer.gaia.AppsModel;
import com.radicle.mesh.stacks.model.stxbuffer.gaia.UserAppMaps;
import com.radicle.mesh.stacks.model.stxbuffer.types.Application;
import com.radicle.mesh.stacks.model.stxbuffer.types.Token;

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
	private Map<String, UserAppMaps> readUrls = new HashMap<>();
	private Map<String, String> appData = new HashMap<>();
	private Map<String, Map<String, String>> metaData = new HashMap<>();

	public Map<String, Map<String, String>> getAppData()  {
		return metaData;
	}
	
//	public Map<String, String> getAppData()  {
//		return appData;
//	}
	
	public void read(Application application, Token token) throws JsonProcessingException {
		Map<String, String> contractData = metaData.get(application.getContractId());
		if (contractData == null) {
			contractData = new HashMap<String, String>();
		}
		String metaDataUrl = token.getTokenInfo().getMetaDataUrl();
		try {
			HttpEntity he = new HttpEntity<String>(new HttpHeaders());
			HttpEntity<String> response = restTemplate.exchange(metaDataUrl, HttpMethod.GET, he, String.class);
			String assetJson = response.getBody();
			contractData.put(token.getTokenInfo().getAssetHash(), assetJson);
			metaData.put(application.getContractId(), contractData);
			sendToSearch(application.getContractId(), assetJson);
		} catch (RestClientException e) {
			logger.error("Nothing found at: " + metaDataUrl);
		}
	}
	
	public void read(String appOrigin, String gaiaFilename, String gaiaUsername) throws JsonProcessingException {
		readHubUrls(gaiaUsername);
		readAppData(appOrigin, gaiaFilename, gaiaUsername);
	}
	
	private void readAppData(String appOrigin, String gaiaFilename, String gaiaUsername) throws JsonProcessingException {
		try {
			UserAppMaps hubUrls = readUrls.get(gaiaUsername);
			Map<String, AppsModel> apps = hubUrls.getApps();
			AppsModel appsModel = apps.get(appOrigin);
			String path = appsModel.getStorage() + gaiaFilename;
			
			String response = readFromStacks(path);
			String key = appOrigin + "__" + gaiaUsername;
			// logger.info("App data response " + response);
			appData.put(key, response);
		} catch (Exception e) {
		}
	}
	
	private void readHubUrls(String gaiaUsername) throws JsonProcessingException {
		String path = basePath + gaiaUsername;
		String response = readFromStacks(path);
		UserAppMaps uam = parseApps(response, gaiaUsername);
		readUrls.put(gaiaUsername, uam);
	}
	
	private String sendToSearch(String projectId, String jsonBlob) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(jsonBlob, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(indexUrl + projectId, HttpMethod.POST, requestEntity, String.class);
		return response.getBody();
	}
	
	private String readFromStacks(String path) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response = null;
		response = restTemplate.exchange(path, HttpMethod.GET, requestEntity, String.class);
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
