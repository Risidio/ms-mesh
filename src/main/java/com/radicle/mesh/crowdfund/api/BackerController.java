package com.radicle.mesh.crowdfund.api;

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

import com.radicle.mesh.crowdfund.service.AssetRepository;
import com.radicle.mesh.crowdfund.service.BackerRepository;
import com.radicle.mesh.crowdfund.service.domain.crowdfund.Asset;
import com.radicle.mesh.crowdfund.service.domain.crowdfund.Backer;
import com.radicle.mesh.payments.service.PaymentRepository;
import com.radicle.mesh.payments.service.domain.Payment;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "http://localhost:8085", "https://prom.risidio.com" }, maxAge = 6000)
public class BackerController {

    private static final Logger logger = LogManager.getLogger(BackerController.class);
	@Autowired
	private BackerRepository backerRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private AssetRepository assetRepository;

	@GetMapping(value = "/v2/backerByUserKey/{keyName}/{keyValue}")
	public Optional<Backer> backerByUserKey(@PathVariable String keyName, @PathVariable String keyValue) {
		Optional<Backer> o = null;
		if (keyName.equals("stxAddress")) {
			o = backerRepository.findByStxAddress(keyValue);
		} else if (keyName.equals("email")) {
			o = backerRepository.findByEmail(keyValue);
		} else {
			o = backerRepository.findByUsername(keyValue);
		}
		return o;
	}
	
	@GetMapping(value = "/v2/backer/{id}")
	public Optional<Backer> backerById(@PathVariable String id) {
		Optional<Backer> o = backerRepository.findById(id);
		return o;
	}
	
	@GetMapping(value = "/v2/backers")
	public List<Backer> findAll() {
		List<Backer> backers = backerRepository.findAll();
		return backers;
	}
	
	@PostMapping(value = "/v2/backer")
	public Backer post(HttpServletRequest request, @RequestBody Backer backer) {
		if (backer.getPayments() != null) {
			for (Payment payment : backer.getPayments()) {
				payment = paymentRepository.save(payment);
			}
		}
		if (backer.getAssets() != null) {
			for (Asset asset : backer.getAssets()) {
				asset = assetRepository.findById(asset.getId()).get();
			}
		}
		return backerRepository.save(backer);
	}

	@PostMapping(value = "/v2/backers")
	public List<Backer> post(HttpServletRequest request, @RequestBody List<Backer> backers) {
		return backerRepository.saveAll(backers);
	}

	@PutMapping(value = "/v2/backer")
	public Backer put(HttpServletRequest request, @RequestBody Backer backer) {
		return backerRepository.save(backer);
	}

}
