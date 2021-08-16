package com.radicle.mesh.payments.api;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.payments.service.domain.SquarePaymentRequest;
import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.Money;
import com.squareup.square.models.Payment;

@RestController
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class SquareStacksMateController {

    private static final Logger logger = LogManager.getLogger(SquareStacksMateController.class);
	@Autowired private SquareClient squareStacksMateClient;
	@Autowired private ObjectMapper mapper;
	@Autowired private Environment environment;

	@PostMapping(value = "/v2/stacksmate/square/charge")
	public Payment charge(@RequestBody SquarePaymentRequest paymentRequest) throws ApiException, IOException {
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
		    .locationId(environment.getProperty("SQUARE_SM_LOCATION_ID"))
		    .build();
		PaymentsApi paymentsApi = squareStacksMateClient.getPaymentsApi();
		CreatePaymentResponse cpr = paymentsApi.createPayment(body);
		if (cpr.getErrors() == null || cpr.getErrors().size() == 0) {
			return cpr.getPayment();
		}
		throw new RuntimeException(mapper.writeValueAsString(cpr.getErrors()));
	}
}
