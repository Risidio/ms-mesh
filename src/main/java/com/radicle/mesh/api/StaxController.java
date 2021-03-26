package com.radicle.mesh.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.api.model.Principal;
import com.radicle.mesh.api.model.Shaker;
import com.radicle.mesh.api.model.stxbuffer.ContractReader;
import com.radicle.mesh.api.model.stxbuffer.types.AppMapContract;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class StaxController {

	@Autowired private RestOperations restTemplate;
	@Value("${radicle.stax.base-path}") String basePath;
	@Value("${radicle.stax.sidecar-path}") String sidecarPath;
	@Value("${radicle.stax.admin-contract-address}") String adminContractAddress;
	@Value("${radicle.stax.admin-contract-name}") String adminContractName;
	@Autowired private ObjectMapper mapper;
	@Autowired private ContractReader contractReader;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	private AppMapContract appMapContract;

	@Scheduled(fixedDelay=10000)
	public void pushData() throws JsonProcessingException {
		this.appMapContract = contractReader.read();
		simpMessagingTemplate.convertAndSend("/queue/contract-news", appMapContract);
	}


	@GetMapping(value = "/v2/appmap")
	public AppMapContract appmap(HttpServletRequest request) {
		return appMapContract;
	}

	@PostMapping(value = "/v1/shaker")
	public Shaker superAdmin(HttpServletRequest request) {
		String user = (String) request.getSession().getAttribute("USERNAME");
		if (user == null || !user.equals("mijoco.id.blockstack")) {
			return null;
		}
		return new Shaker();
	}

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

//	@PostMapping(value = "/v2/contract/read")
//	public String contractRead(HttpServletRequest request, @RequestBody ReadContract txOptions) {
//		String url = basePath + "/v2/contracts/call-read/" + txOptions.getContractAddress() + "/" + txOptions.getContractName() + "/" + txOptions.getFunctionName();
//		ResponseEntity<String> response = null;
//		String jsonInString = convertMessage(txOptions.getRcp());
//		HttpEntity<String> e = new HttpEntity<String>(jsonInString, getHeaders());
//		response = restTemplate.exchange(url, HttpMethod.POST, e, String.class);
//		return response.getBody();
//	}

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

	@PostMapping(value = "/v2/clarity")
	public Principal clarity(HttpServletRequest request, @RequestBody Principal principal) {

		System.out.println("StaxController.clarity()");
		return principal;
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
