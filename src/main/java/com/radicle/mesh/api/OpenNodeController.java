package com.radicle.mesh.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.api.model.opennode.FetchPayment;

@RestController
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class OpenNodeController {

	@Autowired private RestOperations restTemplate;
	@Value("${opennode.api.apiEndpoint}") String apiEndpoint;
	@Value("${opennode.api.apiKey}") String apiKey;
	@Autowired private ObjectMapper mapper;

	@PostMapping(value = "/v2/fetchPayment")
	public String fetchPayment(HttpServletRequest request, @RequestBody FetchPayment fetchPayment) {
		String url = apiEndpoint +  "/v1/charges";
		ResponseEntity<String> response = null;
		String jsonInString = convertMessage(fetchPayment);
		HttpEntity<String> entity = new HttpEntity<String>(jsonInString, getHeaders());
		response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		return response.getBody();
	}

	@PostMapping(value = "/v2/checkPayment/{paymentId}")
	public String checkPayment(HttpServletRequest request, @PathVariable String paymentId) {
		String url = apiEndpoint +  "/v1/charge/" + paymentId;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(new HttpHeaders()), String.class);
		return response.getBody();
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
//		String auth = "Authorization" + ":" + val;
//		String encodedAuth = new String(Base64.getEncoder().encode(auth.getBytes(Charset.forName("UTF8"))));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", apiKey);
		// headers.setContentLength(jsonInString.length());
		return headers;
	}
}
