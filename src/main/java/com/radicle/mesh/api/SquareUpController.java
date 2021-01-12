package com.radicle.mesh.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.api.model.square.PaymentRequest;
import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.Money;
import com.squareup.square.models.Payment;

@RestController
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class SquareUpController {

    private static final Logger logger = LogManager.getLogger(SquareUpController.class);
	@Value("${squareup.api.applicationName}") String applicationName;
	@Value("${squareup.api.locationId}") String locationId;
	@Value("${squareup.api.redirectUrl}") String redirectUrl;
	@Value("${squareup.api.applicationId}") String applicationId;
	@Value("${squareup.api.applicationSecret}") String applicationSecret;
	@Autowired private SquareClient squareClient;
	@Autowired private ObjectMapper mapper;

	@GetMapping(value = "/oauth-redirect")
	public String fetchPayment(HttpServletRequest request) {
		return "success";
	}

	@PostMapping(value = "/v1/square/charge")
	public Payment charge(@RequestBody PaymentRequest paymentRequest) throws ApiException, IOException {
		Money bodyAmountMoney = new Money.Builder()
			    .amount(paymentRequest.getAmountFiat())
			    .currency(paymentRequest.getCurrency())
			    .amount(paymentRequest.getAmountFiat())
			    .build();
		CreatePaymentRequest body = new CreatePaymentRequest.Builder(
				paymentRequest.getNonce(),
				paymentRequest.getIdempotencyKey(),
		        bodyAmountMoney)
		    .autocomplete(true)
		    .locationId(locationId)
		    .build();
		PaymentsApi paymentsApi = squareClient.getPaymentsApi();
		CreatePaymentResponse cpr = paymentsApi.createPayment(body);
		if (cpr.getErrors() == null || cpr.getErrors().size() == 0) {
			return cpr.getPayment();
		}
		throw new RuntimeException(mapper.writeValueAsString(cpr.getErrors()));
	}
}
