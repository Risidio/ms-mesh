package com.radicle.mesh.payments.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.payments.service.PaymentRepository;
import com.radicle.mesh.payments.service.domain.Payment;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "http://localhost:8085", "https://prom.risidio.com" }, maxAge = 6000)
public class PaymentController {

    private static final Logger logger = LogManager.getLogger(PaymentController.class);
	@Autowired
	private PaymentRepository paymentRepository;

	@GetMapping(value = "/v2/payment/{id}")
	public Optional<Payment> paymentById(@PathVariable String id) {
		Optional<Payment> o = paymentRepository.findById(id);
		return o;
	}
	
	@GetMapping(value = "/v2/payments")
	public List<Payment> findAll() {
		List<Payment> payments = paymentRepository.findAll();
		return payments;
	}
	
	@PostMapping(value = "/v2/payment")
	public Payment post(HttpServletRequest request, @RequestBody Payment payment) {
		return paymentRepository.save(payment);
	}

	@PutMapping(value = "/v2/payment")
	public Payment put(HttpServletRequest request, @RequestBody Payment payment) {
		return paymentRepository.save(payment);
	}

}
