package com.radicle.mesh.api.model.stxbuffer;

import java.util.HashMap;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.api.model.stxbuffer.gaia.AppsModel;
import com.radicle.mesh.api.model.stxbuffer.gaia.UserAppMaps;

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

	@Value("${radicle.gaia.huburl}") String basePath;
	@Autowired private RestOperations restTemplate;
	@Autowired private ObjectMapper mapper;
	private Map<String, UserAppMaps> readUrls = new HashMap<>();
	private Map<String, String> appData = new HashMap<>();

	public Map<String, String> getAppData()  {
		return appData;
	}
	
	public void read(String appOrigin, String gaiaFilename, String gaiaUsername) throws JsonProcessingException {
		readHubUrls(gaiaUsername);
		readAppData(appOrigin, gaiaFilename, gaiaUsername);
	}
	
	private void readAppData(String appOrigin, String gaiaFilename, String gaiaUsername) throws JsonProcessingException {
		UserAppMaps hubUrls = readUrls.get(gaiaUsername);
		Map<String, AppsModel> apps = hubUrls.getApps();
		AppsModel appsModel = apps.get(appOrigin);
		String path = appsModel.getStorage() + gaiaFilename;
		
		try {
			String response = readFromStacks(path);
			String key = appOrigin + "__" + gaiaUsername;
			appData.put(key, response);
		} catch (JsonProcessingException e) {
			// probably a 404 for the json file at this domain. Carry on...
		}
	}
	
	private void readHubUrls(String gaiaUsername) throws JsonProcessingException {
		String path = basePath + gaiaUsername;
		String response = readFromStacks(path);
		UserAppMaps uam = parseApps(response, gaiaUsername);
		readUrls.put(gaiaUsername, uam);
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
