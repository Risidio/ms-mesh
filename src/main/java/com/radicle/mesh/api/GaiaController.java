package com.radicle.mesh.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.api.model.stxbuffer.ContractReader;
import com.radicle.mesh.api.model.stxbuffer.GaiaHubReader;
import com.radicle.mesh.api.model.stxbuffer.gaia.RootFilePostModel;
import com.radicle.mesh.api.model.stxbuffer.types.AppMapContract;
import com.radicle.mesh.api.model.stxbuffer.types.Application;
import com.radicle.mesh.api.model.stxbuffer.types.Token;
import com.radicle.mesh.api.model.stxbuffer.types.TokenContract;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class GaiaController {

	@Autowired private GaiaHubReader gaiaHubReader;
	@Autowired private ContractReader contractReader;

//    @PostConstruct
//    public void init() throws JsonProcessingException {
//    	collectGaiaHubRecords();
//    }

	@Scheduled(fixedDelay=10000)
	public void collectGaiaHubRecords() throws JsonProcessingException {
		AppMapContract registry = contractReader.getRegistry();
		if (registry != null && registry.getApplications() != null) {
			for (Application a : registry.getApplications()) {
				TokenContract tc = a.getTokenContract();
				if (tc.getTokens() != null) {
					for (Token token : tc.getTokens()) {
						String gaiaUsername = token.getTokenInfo().getGaiaUsername();
						gaiaHubReader.read(a.getAppOrigin(), a.getGaiaFilename(), gaiaUsername);
					}
				}
			}
		}
	}

	/**
	 * Get the rootFile.json file for a given app origin and uploader / username
	 * @param request
	 * @param rootFilePostModel
	 * @return
	 */
	@PostMapping(value = "/v2/gaia/rootFile")
	public String rootFile1(HttpServletRequest request, @RequestBody RootFilePostModel rootFilePostModel) {
		Map<String, String> registry = gaiaHubReader.getAppData();
		String key = rootFilePostModel.getAppOrigin() + "__" + rootFilePostModel.getGaiaUsername();
		return registry.get(key);
	}
	
	@PostMapping(value = "/v2/gaia/rootFilesByDomain")
	public Map<String, String> rootFile2(HttpServletRequest request, @RequestBody RootFilePostModel rootFilePostModel) {
		Map<String, String> registry = gaiaHubReader.getAppData();
		Map<String, String> appOriginFiles = new HashMap<>();
		for (String key : registry.keySet()) {
			if (key.startsWith(rootFilePostModel.getAppOrigin())) {
				appOriginFiles.put(key, registry.get(key));
			}
		}
		return appOriginFiles;
	}
	
	/**
	 * Get the rootFile.json file for a given app origin and uploader / username
	 * @param request
	 * @param rootFilePostModel
	 * @return
	 */
	@PostMapping(value = "/v2/gaia/rootFiles")
	public Map<String, String> allRootFiles(HttpServletRequest request) {
		Map<String, String> registry = gaiaHubReader.getAppData();
		return registry;
	}
}
