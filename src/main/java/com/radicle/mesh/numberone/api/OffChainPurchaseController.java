package com.radicle.mesh.numberone.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.email.service.EmailService;
import com.radicle.mesh.numberone.OffChainOfferRepository;
import com.radicle.mesh.numberone.RegistrationRepository;
import com.radicle.mesh.numberone.domain.OffChainOffer;
import com.radicle.mesh.numberone.domain.Registration;
import com.radicle.mesh.stacks.service.StacksTransactionRepository;
import com.radicle.mesh.stacks.service.domain.StacksTransaction;

@RestController
@EnableAsync
@EnableScheduling
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
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
		emailService.sendRegisterInterestEmail(registration.getEmail(), registration.getEmailContent());
		return true;
	}

	@PostMapping(value = "/v2/register/offer")
	public Boolean registerOffer(HttpServletRequest request, @RequestBody OffChainOffer offChainOffer) {
		offChainOfferRepository.save(offChainOffer);
		emailService.sendOfferRegisteredEmail(offChainOffer, offChainOffer.getEmailContent());
		return true;
	}

	@PostMapping(value = "/v2/register/transaction")
	public Boolean registerTransaction(HttpServletRequest request, @RequestBody StacksTransaction stacksTransaction) {
		stacksTransactionRepository.save(stacksTransaction);
		return true;
	}

	@GetMapping(value = "/v2/fetch/transactions")
	public List<StacksTransaction> fetchTransactions(HttpServletRequest request) {
		return stacksTransactionRepository.findAll();
	}

	@GetMapping(value = "/v2/fetch/transactions/{assetHash}")
	public List<StacksTransaction> fetchTransactions(HttpServletRequest request, @PathVariable String assetHash) {
		return stacksTransactionRepository.findByAssetHash(assetHash);
	}

	@GetMapping(value = "/v2/fetch/transactions/{assetHash}/{functionName}")
	public List<StacksTransaction> fetchTransactions(HttpServletRequest request, @PathVariable String assetHash,
			@PathVariable String functionName) {
		return stacksTransactionRepository.findByAssetHashAndType(assetHash, functionName);
	}

	@GetMapping(value = "/v2/fetch/offers")
	public List<OffChainOffer> fetchOffersSecure(HttpServletRequest request) {
		return offChainOfferRepository.findAll();
	}

	@GetMapping(value = "/v2/email/templates")
	public String fetchEmailTemplates(HttpServletRequest request) {
		String template = emailService.loadEmailTemplates();
		return template;
	}

}
