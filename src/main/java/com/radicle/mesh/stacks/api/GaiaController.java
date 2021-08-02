package com.radicle.mesh.stacks.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.stacks.service.GaiaHubReader;

@RestController
@EnableAsync
@EnableScheduling
public class GaiaController {

	@Autowired private GaiaHubReader gaiaHubReader;

	@Scheduled(fixedDelay=3600000)
	public void collectGaiaHubRecords() throws JsonProcessingException {
		gaiaHubReader.buildSearchIndex();
	}

	@GetMapping(value = "/v2/gaia/indexFiles")
	public String indexFiles(HttpServletRequest request) throws JsonProcessingException {
		gaiaHubReader.buildSearchIndexAsync();
		return "indexing gaia data now..";
	}

//	@PostMapping(value = "/v2/gaia/rootFile")
//	public String rootFile1(HttpServletRequest request, @RequestBody RootFilePostModel rootFilePostModel) {
//		Map<String, String> registry = gaiaHubReader.getAppData();
//		String key = rootFilePostModel.getAppOrigin() + "__" + rootFilePostModel.getGaiaUsername();
//		return registry.get(key);
//	}
//	
//	@PostMapping(value = "/v2/gaia/rootFilesByDomain")
//	public Map<String, String> rootFile2(HttpServletRequest request, @RequestBody RootFilePostModel rootFilePostModel) {
//		Map<String, Map<String, String>> registry = gaiaHubReader.getAppData();
//		Map<String, String> appOriginFiles = new HashMap<>();
//		for (String key : registry.keySet()) {
//			if (key.startsWith(rootFilePostModel.getAppOrigin())) {
//				appOriginFiles.put(key, registry.get(key));
//			}
//		}
//		return appOriginFiles;
//	}
		
//	@GetMapping(value = "/v2/meta-data")
//	public Map<String, Map<String, String>> metaData(HttpServletRequest request) {
//		Map<String, Map<String, String>> registry = gaiaHubReader.getAppData();
//		return registry;
//	}
//
//	@GetMapping(value = "/v2/meta-data/{contractId}")
//	public Map<String, Map<String, String>> contractMetaData(HttpServletRequest request, @PathVariable String contractId) {
//		Map<String, Map<String, String>> registry = gaiaHubReader.getAppData();
//		Map<String, Map<String, String>> reg = new HashMap<String, Map<String,String>>();
//		reg.put(contractId, registry.get(contractId));
//		return reg;
//	}
}
