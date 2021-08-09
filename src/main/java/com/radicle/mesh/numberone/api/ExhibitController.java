package com.radicle.mesh.numberone.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.email.service.EmailService;
import com.radicle.mesh.numberone.ExhibitRequestRepository;
import com.radicle.mesh.numberone.domain.ExhibitRequest;
import com.radicle.mesh.privilege.service.AuthorisationRepository;
import com.radicle.mesh.privilege.service.domain.Authorisation;

@RestController
@EnableAsync
@EnableScheduling
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class ExhibitController {

	@Autowired
	private ExhibitRequestRepository exhibitRequestRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private AuthorisationRepository authorisationRepository;

	@GetMapping(value = "/v2/exhibit-requests/{status}")
	public List<ExhibitRequest> findByStatus(@PathVariable Integer status) {
		return exhibitRequestRepository.findByStatus(status);
	}
	
	@GetMapping(value = "/v2/exhibit-requests")
	public List<ExhibitRequest> find() {
		return exhibitRequestRepository.findAll();
	}
	
	@GetMapping(value = "/v2/exhibit-request/{stxAddress}")
	public ExhibitRequest find(@PathVariable String stxAddress) {
		return exhibitRequestRepository.findByStxAddress(stxAddress);
	}
	
	@PostMapping(value = "/v2/register-to-exhibit")
	public ExhibitRequest registerExhibitRequest(HttpServletRequest request, @RequestBody ExhibitRequest exhibitRequest) {
		exhibitRequest = exhibitRequestRepository.save(exhibitRequest);
		emailService.sendExhibitRequest(exhibitRequest);
		return exhibitRequest;
	}
	
	@PutMapping(value = "/v2/register-to-exhibit")
	public ExhibitRequest updateExhibitRequest(HttpServletRequest request, @RequestBody ExhibitRequest exhibitRequest) {
		Optional<ExhibitRequest> er = exhibitRequestRepository.findById(exhibitRequest.getId());
		exhibitRequest.setId(er.get().getId());
		exhibitRequest = exhibitRequestRepository.save(exhibitRequest);
		emailService.sendExhibitRequest(exhibitRequest);
		return exhibitRequest;
	}
	
	@PutMapping(value = "/v2/change-exhibit-status")
	public ExhibitRequest changeStatus(HttpServletRequest request, @RequestBody ExhibitRequest exhibitRequest) {
		Optional<ExhibitRequest> dbER = exhibitRequestRepository.findById(exhibitRequest.getId());
		if (dbER.isPresent()) {
			ExhibitRequest e2 = dbER.get();
//			if (e2.getStatus() == 2) {
//				addPrivilege(e2, "can-upload");
//			} else {
//				removePrivilege(e2, "can-upload");
//			}
			e2.setStatus(exhibitRequest.getStatus());
			exhibitRequest = exhibitRequestRepository.save(e2);
		} else {
			exhibitRequest = exhibitRequestRepository.save(exhibitRequest);
		}
		return exhibitRequest;
	}
	
	private void addPrivilege(ExhibitRequest exReq, String priv) {
		Authorisation authorisation = authorisationRepository.findByStxAddress(exReq.getStxAddress());
		authorisation.addPrivilege(exReq.getDomain(), priv);
	}
	
	private void removePrivilege(ExhibitRequest exReq, String priv) {
		Authorisation authorisation = authorisationRepository.findByStxAddress(exReq.getStxAddress());
		authorisation.addPrivilege(exReq.getDomain(), priv);
	}
}
