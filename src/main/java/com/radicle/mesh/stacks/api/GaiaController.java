package com.radicle.mesh.stacks.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.GaiaHubReader;
import com.radicle.mesh.stacks.service.domain.AppMapContract;
import com.radicle.mesh.stacks.service.domain.Application;
import com.radicle.mesh.stacks.service.domain.Token;
import com.radicle.mesh.stacks.service.domain.TokenContract;

@RestController
@EnableAsync
@EnableScheduling
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8080", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084", "http://localhost:8085", "http://localhost:8086", "http://localhost:8087", "http://localhost:8088", "http://localhost:8089", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class GaiaController {

    private static final Logger logger = LogManager.getLogger(GaiaController.class);
	@Autowired private GaiaHubReader gaiaHubReader;
	@Autowired private ContractReader contractReader;

//    @PostConstruct
//    public void init() throws JsonProcessingException {
//    	collectGaiaHubRecords();
//    }

	@Scheduled(fixedDelay=3600000)
	public void collectGaiaHubRecords() throws JsonProcessingException {
		AppMapContract registry = contractReader.getRegistry();
		if (registry != null && registry.getApplications() != null) {
			for (Application a : registry.getApplications()) {
				TokenContract tc = a.getTokenContract();
				if (tc.getTokens() != null) {
					for (Token token : tc.getTokens()) {
						// gaiaHubReader.read(a.getAppOrigin(), a.getGaiaFilename(), gaiaUsername);
						gaiaHubReader.read(a, token);
					}
				}
			}
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

	/**
	 * Get the rootFile.json file for a given app origin and uploader / username
	 * @param request
	 * @param rootFilePostModel
	 * @return
	 */
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
	
	@GetMapping(value = "/v2/gaia/indexFiles")
	public Map<String, Map<String, String>> indexFiles(HttpServletRequest request) throws JsonProcessingException {
		collectGaiaHubRecords();
		Map<String, Map<String, String>> registry = gaiaHubReader.getAppData();
		return registry;
	}
	
	@GetMapping(value = "/v2/meta-data")
	public Map<String, Map<String, String>> metaData(HttpServletRequest request) {
		Map<String, Map<String, String>> registry = gaiaHubReader.getAppData();
		return registry;
	}

	@GetMapping(value = "/v2/meta-data/{contractId}")
	public Map<String, Map<String, String>> contractMetaData(HttpServletRequest request, @PathVariable String contractId) {
		Map<String, Map<String, String>> registry = gaiaHubReader.getAppData();
		Map<String, Map<String, String>> reg = new HashMap<String, Map<String,String>>();
		reg.put(contractId, registry.get(contractId));
		return reg;
	}
}
