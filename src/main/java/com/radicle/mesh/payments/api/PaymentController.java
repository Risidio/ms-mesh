package com.radicle.mesh.payments.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.payments.api.model.ProjectPaymentTotals;
import com.radicle.mesh.payments.service.PaymentRepository;
import com.radicle.mesh.payments.service.PaymentService;
import com.radicle.mesh.payments.service.domain.Payment;

@RestController
@EnableAsync
@EnableScheduling
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class PaymentController {

    private static final Logger logger = LogManager.getLogger(PaymentController.class);
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private PaymentRepository paymentRepository;

//	@GetMapping(value = "/v2/payment/{id}")
//	public Optional<Payment> paymentById(@PathVariable String id) {
//		Optional<Payment> o = paymentRepository.findById(id);
//		return o;
//	}
	
//	@GetMapping(value = "/v2/payments")
//	public List<Payment> findAll() {
//		List<Payment> payments = paymentRepository.findAll();
//		return payments;
//	}

//	@GetMapping(value = "/v2/payments/{projectId}/{paymentType}")
//	public List<Payment> findByProjectIdAndPaymentType(@PathVariable String projectId,
//			@PathVariable String paymentType) {
//		List<Payment> payments = paymentRepository.findByProjectIdAndPaymentType(projectId, paymentType);
//		return payments;
//	}

	@GetMapping(value = "/v2/payments/{projectId}")
	public ProjectPaymentTotals getProjectPaymentTotals(@PathVariable String projectId) {
		ProjectPaymentTotals ppt = paymentService.getProjectPaymentTotals(projectId);
		return ppt;
	}

	@PostMapping(value = "/v2/payment")
	public Payment post(HttpServletRequest request, @RequestBody Payment payment) {
		paymentRepository.save(payment);
		return paymentRepository.save(payment);
	}

	@PutMapping(value = "/v2/payment")
	public Payment put(HttpServletRequest request, @RequestBody Payment payment) {
		return paymentRepository.save(payment);
	}

}
