package com.radicle.mesh.stacksmate.api;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.radicle.mesh.stacksmate.service.StacksMateRepository;
import com.radicle.mesh.stacksmate.service.domain.StacksMateTransaction;

@RestController
public class StacksMateController {

    private static final Logger logger = LogManager.getLogger(StacksMateController.class);
	@Autowired private StacksMateRepository stacksMateRepository;

	@PostMapping(value = "/v2/stacksmate/transactions")
	public StacksMateTransaction save(@RequestBody StacksMateTransaction stacksMateTransaction)
			throws JsonMappingException, JsonProcessingException {
		stacksMateTransaction = stacksMateRepository.save(stacksMateTransaction);
		return stacksMateTransaction;
	}

	@PutMapping(value = "/v2/stacksmate/transactions")
	public StacksMateTransaction update(@RequestBody StacksMateTransaction stacksMateTransaction)
			throws JsonMappingException, JsonProcessingException {
		Optional<StacksMateTransaction> smt = stacksMateRepository.findById(stacksMateTransaction.getId());
		if (smt.isPresent()) {
			stacksMateTransaction.setId(smt.get().getId());
		}
		stacksMateTransaction = stacksMateRepository.save(stacksMateTransaction);
		return stacksMateTransaction;
	}

	@DeleteMapping(value = "/v2/stacksmate/transactions/{nonce}")
	public Boolean delTransaction(@PathVariable Long nonce) {
		StacksMateTransaction smt = stacksMateRepository.findByNonce(nonce);
		stacksMateRepository.deleteById(smt.getId());
		return true;
	}

	@GetMapping(value = "/v2/stacksmate/transaction-by-nonce/{nonce}")
	public StacksMateTransaction getTransactions(@PathVariable Long nonce) {
		StacksMateTransaction smt = stacksMateRepository.findByNonce(nonce);
		return smt;
	}

	@GetMapping(value = "/v2/stacksmate/transactions/{recipient}")
	public List<StacksMateTransaction> getTransactions(@PathVariable String recipient) {
		logger.info("SM-TRANSACTIONS: getTransactions - recipient=", recipient);
		Sort sort = Sort.by("timeSent").descending();
		List<StacksMateTransaction> smt = stacksMateRepository.findByRecipient(recipient, sort);
		return smt;
	}

	@GetMapping(value = "/v2/stacksmate/transaction-recent")
	public StacksMateTransaction getRecentTransaction() {
		logger.info("SM-TRANSACTIONS: getTransactions - findTopByOrderByNonceDesc");
		StacksMateTransaction smt = stacksMateRepository.findTopByOrderByNonceDesc();
		return smt;
	}

	@GetMapping(value = "/v2/stacksmate/transactions")
	public List<StacksMateTransaction> getTransactions() {
		logger.info("SM-TRANSACTIONS: getTransactions - findAll");
		List<StacksMateTransaction> smts = stacksMateRepository.findAll();
		return smts;
	}
}
