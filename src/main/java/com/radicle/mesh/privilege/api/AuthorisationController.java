package com.radicle.mesh.privilege.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.radicle.mesh.privilege.service.AuthorisationRepository;
import com.radicle.mesh.privilege.service.PrivilegeRepository;
import com.radicle.mesh.privilege.service.domain.Authorisation;
import com.radicle.mesh.privilege.service.domain.Domain;
import com.radicle.mesh.privilege.service.domain.Privilege;

@RestController
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class AuthorisationController {

	private static final Logger logger = LogManager.getLogger(AuthorisationController.class);
	@Autowired
	private AuthorisationRepository authorisationRepository;
	@Autowired
	private PrivilegeRepository privilegeRepository;

	@PostMapping(value = "/v2/auth/authorise")
	public Authorisation authorise(HttpServletRequest request, @RequestBody Authorisation newAuthorisation)
			throws JsonMappingException, JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(newAuthorisation.getStxAddress());
		if (authorisation != null) {
			authorisation.setDomains(newAuthorisation.getDomains());
			authorisation.setRoles(newAuthorisation.getRoles());
		} else {
			authorisation = newAuthorisation;
		}
		authorisation = authorisationRepository.save(authorisation);
		return authorisation;
	}

	@PostMapping(value = "/v2/auth/add/{stxAddress}/{privilege}/{hostname}")
	public Authorisation add(HttpServletRequest request, @PathVariable String stxAddress, @PathVariable String privilege, @PathVariable String hostname) throws JsonMappingException, JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		if (authorisation != null) {
			authorisation.addPrivilege(hostname, privilege);
		} else {
			authorisation = new Authorisation();
			authorisation.setStxAddress(stxAddress);
			authorisation.addPrivilege(hostname, privilege);
		}
		authorisation = authorisationRepository.save(authorisation);
		return authorisation;
	}

	@PostMapping(value = "/v2/auth/remove/{stxAddress}/{privilege}/{hostname}")
	public Authorisation remove(HttpServletRequest request, @PathVariable String stxAddress, @PathVariable String privilege, @PathVariable String hostname) throws JsonMappingException, JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		if (authorisation != null) {
			authorisation.removePrivilege(hostname, privilege);
			authorisation = authorisationRepository.save(authorisation);
		}
		return authorisation;
	}

	@GetMapping(value = "/v2/auth/getAuthorisations")
	public List<Authorisation> getAuthorisations(HttpServletRequest request) {
		List<Authorisation> authorisations = authorisationRepository.findAll();
		return authorisations;
	}

	@GetMapping(value = "/v2/auth/getPrivileges")
	public List<Privilege> getPrivileges(HttpServletRequest request) {
		List<Privilege> privileges = privilegeRepository.findAll();
		return privileges;
	}

	@GetMapping(value = "/v2/auth/getAuthorisation/{stxAddress}")
	public Authorisation getAuthorisation(HttpServletRequest request, @PathVariable String stxAddress)
			throws JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		return authorisation;
	}

	@GetMapping(value = "/v2/auth/getAuthorisation/{stxAddress}/{domain}")
	public Domain getAuthorisation(HttpServletRequest request, @PathVariable String stxAddress, @PathVariable String domain)
			throws JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		Domain d = null;
		for (Domain dom : authorisation.getDomains()) {
			if (dom.getHost().equals(domain)) {
				d = dom;
			}
		}
		return d;
	}

	@GetMapping(value = "/v2/auth/isAuthorised/{stxAddress}/{domain}/{privilege}")
	public Boolean isAuthorised(HttpServletRequest request, @PathVariable String stxAddress,
			@PathVariable String domain, @PathVariable String privilege) throws JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		if (authorisation == null)
			return false;
		return authorisation.hasPrivilege(domain, privilege);
	}

}
