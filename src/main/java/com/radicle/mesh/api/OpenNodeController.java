package com.radicle.mesh.api;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.api.model.opennode.FetchPayment;

@RestController
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class OpenNodeController {

    private static final Logger logger = LogManager.getLogger(OpenNodeController.class);
	@Autowired private RestOperations restTemplate;
	@Value("${opennode.api.apiEndpoint}") String apiEndpoint;
	@Autowired private ObjectMapper mapper;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
    private static final String HMAC_SHA512 = "HmacSHA512";
	@Autowired private Environment environment;

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
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(getHeaders()), String.class);
			return response.getBody();
		} catch (RestClientException e) {
			logger.error("No payment for paymentId: " + url + " Error: " + e.getMessage());
			return null;
		}
	}

	@PostMapping(value = "/v2/charge/callback")
	public void chargeCallback(@RequestBody String charge) {
		logger.info("Received callback: " + charge);
		// id=a2023596-80eb-4891-bf92-9007293b4e76&callback_url=https%3A%2F%2Ftapi.risidio.com%2Fmesh%2Fv2%2Fcharge%2Fcallback&success_url=https%3A%2F%2Fopennode.com&status=processing&order_id=N%2FA&description=Simulating+webhook+processing&price=100000000&fee=0&auto_settle=0&hashed_order=ed145226b96572625933b4ec52d35722534bb93c9c43eecfdd248cb7e80e9ffe
		String [] params = charge.split("&");
		Map<String, String> items = new HashMap<String, String>();
		for (String param : params) {
			String [] nv = param.split("=");
			items.put(nv[0], nv[1]);
		}
		simpMessagingTemplate.convertAndSend("/queue/payment-news-" + items.get("id"), items);
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
		headers.set("Authorization", environment.getProperty("OPENNODE_API_KEY"));
		// headers.setContentLength(jsonInString.length());
		return headers;
	}
	
	private String doSHA() {
        Mac sha512Hmac;
        String result = null;
        final String key = "Welcome1";

        try {
            final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            sha512Hmac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal("My message".getBytes(StandardCharsets.UTF_8));

            // Can either base64 encode or put it right into hex
            result = Base64.getEncoder().encodeToString(macData);
            // result = bytesToHex(macData);
            return result;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return result;
        }
    }

}
