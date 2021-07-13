package com.radicle.mesh.crowdfund.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.crowdfund.api.model.BackerTransactionData;
import com.radicle.mesh.crowdfund.api.model.CrowdfundTarget;
import com.radicle.mesh.crowdfund.service.AssetRepository;
import com.radicle.mesh.crowdfund.service.BackerRepository;
import com.radicle.mesh.crowdfund.service.CrowdfundService;
import com.radicle.mesh.crowdfund.service.domain.crowdfund.Backer;
import com.radicle.mesh.crowdfund.service.domain.crowdfund.Transaction;
import com.radicle.mesh.payments.service.PaymentRepository;

@RestController
@EnableAsync
@EnableScheduling
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class BackerController {

    private static final Logger logger = LogManager.getLogger(BackerController.class);
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private AssetRepository assetRepository;
	@Autowired
	private BackerRepository backerRepository;
	@Autowired
	private CrowdfundService crowdfundService;
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@GetMapping(value = "/v2/backerByUserKey/{keyName}/{keyValue}")
	public Optional<Backer> backerByUserKey(@PathVariable String keyName, @PathVariable String keyValue) {
		Optional<Backer> o = null;
		if (keyName.equals("stxAddress")) {
			o = backerRepository.findByStxAddress(keyValue);
		} else if (keyName.equals("email")) {
			o = backerRepository.findByUsername(keyValue);
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
	
	@GetMapping(value = "/v2/backersByProjectId/{projectId}")
	public List<Backer> backersByProjectId(@PathVariable String projectId) {
		List<Backer> backers = backerRepository.findByProjectId(projectId);
		return backers;
	}
	
	@PostMapping(value = "/v2/backer/{projectId}")
	public Backer post(HttpServletRequest request, @PathVariable String projectId, @RequestBody Backer backer) {
		if (backer.getProjectId() == null || backer.getUsername() == null) {
			throw new RuntimeException("Project id / username must not be null");
		}
		Optional<Backer> odbBacker = null;
		if (backer.getId() != null) {
			odbBacker = backerRepository.findById(backer.getId());
		} else {
			odbBacker = backerRepository.findByProjectIdAndUsername(backer.getProjectId(), backer.getUsername());
		}
		Backer dbBacker = null;
		if (odbBacker != null && odbBacker.isPresent()) {
			dbBacker = odbBacker.get();
			if (backer.getTransactions() != null) dbBacker.getTransactions().addAll(backer.getTransactions());
		} else {
			dbBacker = backer;
		}
		backerRepository.save(dbBacker);
		CrowdfundTarget ct = crowdfundService.getCrowdfundTarget(projectId);
		simpMessagingTemplate.convertAndSend("/queue/payment-news-" + projectId, ct);
		return dbBacker;
	}

	@GetMapping(value = "/v2/backerTransactionData/{backerId}")
	public List<BackerTransactionData> backerTransactionData(HttpServletRequest request, @PathVariable String backerId) {
		Optional<Backer> odbBacker = backerRepository.findById(backerId);
		List<BackerTransactionData> data = new ArrayList<BackerTransactionData>();
		if (odbBacker.isPresent()) {
			for (Transaction transaction : odbBacker.get().getTransactions()) {
				try {
					BackerTransactionData btd = new BackerTransactionData();
					if (transaction.getPaymentId() != null) {
						btd.setPayment(paymentRepository.findById(transaction.getPaymentId()).get());
					}
					if (transaction.getPerkId() != null) {
						btd.setPerk(assetRepository.findById(transaction.getPerkId()).get());
					}
					if (transaction.getAssetId() != null) {
						btd.setAsset(assetRepository.findById(transaction.getAssetId()).get());
					}
					data.add(btd);
				} catch (Exception e) {
					logger.info("Wrong number of payments?");
				}				
			}
		}
		return data;
	}

	@PutMapping(value = "/v2/backer")
	public Backer put(HttpServletRequest request, @RequestBody Backer backer) {
		if (backer.getProjectId() == null) {
			throw new RuntimeException("Project Id must not be null");
		}
		return backerRepository.save(backer);
	}

}
