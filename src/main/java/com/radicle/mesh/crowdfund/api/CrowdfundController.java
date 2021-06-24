package com.radicle.mesh.crowdfund.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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

import com.radicle.mesh.crowdfund.api.model.CrowdfundTarget;
import com.radicle.mesh.crowdfund.api.model.DonationData;
import com.radicle.mesh.crowdfund.service.CrowdfundRepository;
import com.radicle.mesh.crowdfund.service.CrowdfundService;
import com.radicle.mesh.crowdfund.service.domain.Crowdfund;
import com.radicle.mesh.payments.service.PaymentRepository;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "http://localhost:8085", "https://prom.risidio.com" }, maxAge = 6000)
public class CrowdfundController {

	@Autowired
	private CrowdfundRepository crowdfundRepository;
	@Autowired
	private CrowdfundService crowdfundService;
	@Autowired
	private PaymentRepository paymentRepository;

	@GetMapping(value = "/v2/crowdfund/{id}")
	public Optional<Crowdfund> crowdfund(@PathVariable String id) {
		Optional<Crowdfund> o = crowdfundRepository.findById(id);
		return o;
	}
	
	@GetMapping(value = "/v2/crowdfundRaised/{projectId}")
	public List<DonationData> crowdfundRaised(@PathVariable String projectId) {
		List<DonationData> dd = paymentRepository.findTotals("square");
		return dd;
	}
	
	@GetMapping(value = "/v2/crowdfundTotals/{projectId}")
	public CrowdfundTarget crowdfundTotals(@PathVariable String projectId) {
		return crowdfundService.getCrowdfundTarget(projectId);
	}
	
	@GetMapping(value = "/v2/crowdfundByProjectId/{projectId}")
	public Optional<Crowdfund> crowdfundByProjectId(@PathVariable String projectId) {
		Optional<Crowdfund> o = crowdfundRepository.findByProjectId(projectId);
		return o;
	}
	
	@GetMapping(value = "/v2/crowdfunds")
	public List<Crowdfund> findAll() {
		List<Crowdfund> crowdfunds = crowdfundRepository.findAll();
		return crowdfunds;
	}
	
	@PostMapping(value = "/v2/crowdfund")
	public Crowdfund post(HttpServletRequest request, @RequestBody Crowdfund crowdfund) {
		return crowdfundRepository.save(crowdfund);
	}

	@PutMapping(value = "/v2/crowdfund")
	public Crowdfund put(HttpServletRequest request, @RequestBody Crowdfund crowdfund) {
		return crowdfundRepository.save(crowdfund);
	}

}
