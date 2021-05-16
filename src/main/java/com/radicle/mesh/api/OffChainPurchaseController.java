package com.radicle.mesh.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.api.model.stxbuffer.types.StacksTransaction;
import com.radicle.mesh.service.registration.EmailService;
import com.radicle.mesh.service.registration.OffChainOfferRepository;
import com.radicle.mesh.service.registration.RegistrationRepository;
import com.radicle.mesh.service.registration.StacksTransactionRepository;
import com.radicle.mesh.service.registration.domain.OffChainOffer;
import com.radicle.mesh.service.registration.domain.Registration;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class OffChainPurchaseController {

	@Autowired
	private StacksTransactionRepository stacksTransactionRepository;
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private OffChainOfferRepository offChainOfferRepository;
	@Autowired
	private EmailService emailService;

	@PostMapping(value = "/v2/register/email")
	public Boolean registerEmail(HttpServletRequest request, @RequestBody Registration registration) {
		registration.setStatus(0);
		registrationRepository.save(registration);
		emailService.sendRegisterInterestEmail(registration.getEmail());
		return true;
	}

	@PostMapping(value = "/v2/register/offer")
	public Boolean registerOffer(HttpServletRequest request, @RequestBody OffChainOffer offChainOffer) {
		offChainOfferRepository.save(offChainOffer);
		emailService.sendOfferRegisteredEmail(offChainOffer);
		return true;
	}

	@PostMapping(value = "/v2/register/transaction")
	public Boolean registerTransaction(HttpServletRequest request, @RequestBody StacksTransaction stacksTransaction) {
		stacksTransactionRepository.save(stacksTransaction);
		return true;
	}

	@PostMapping(value = "/v2/secure/fetch/transactions")
	public List<StacksTransaction> fetchTransaction(HttpServletRequest request) {
		return stacksTransactionRepository.findAll();
	}

	@PostMapping(value = "/v2/secure/fetch/offers")
	public List<OffChainOffer> fetchOffers(HttpServletRequest request) {
		return offChainOfferRepository.findAll();
	}

}
