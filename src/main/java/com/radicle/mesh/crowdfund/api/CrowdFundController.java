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

import com.radicle.mesh.crowdfund.service.CrowdFundRepository;
import com.radicle.mesh.crowdfund.service.domain.CrowdFund;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "http://localhost:8085", "https://prom.risidio.com" }, maxAge = 6000)
public class CrowdFundController {

    private static final Logger logger = LogManager.getLogger(CrowdFundController.class);
	@Autowired
	private CrowdFundRepository crowdFundRepository;

	@GetMapping(value = "/v2/crowdFund/{id}")
	public Optional<CrowdFund> paymentById(@PathVariable String id) {
		Optional<CrowdFund> o = crowdFundRepository.findById(id);
		return o;
	}
	
	@GetMapping(value = "/v2/crowdFunds")
	public List<CrowdFund> findAll() {
		List<CrowdFund> crowdFunds = crowdFundRepository.findAll();
		return crowdFunds;
	}
	
	@PostMapping(value = "/v2/crowdFund")
	public CrowdFund post(HttpServletRequest request, @RequestBody CrowdFund crowdFund) {
		return crowdFundRepository.save(crowdFund);
	}

	@PutMapping(value = "/v2/crowdFund")
	public CrowdFund put(HttpServletRequest request, @RequestBody CrowdFund crowdFund) {
		return crowdFundRepository.save(crowdFund);
	}

}
