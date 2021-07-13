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
import com.radicle.mesh.privilege.service.AuthConfigRepository;
import com.radicle.mesh.privilege.service.AuthorisationRepository;
import com.radicle.mesh.privilege.service.domain.AuthConfig;
import com.radicle.mesh.privilege.service.domain.Authorisation;

@RestController
//@CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class AuthorisationController {

	private static final Logger logger = LogManager.getLogger(AuthorisationController.class);
	@Autowired
	private AuthorisationRepository authorisationRepository;
	@Autowired
	private AuthConfigRepository authConfigRepository;

	@PostMapping(value = "/v2/secure/auth/authorise")
	public Authorisation authorise(HttpServletRequest request, @RequestBody Authorisation newAuthorisation) throws JsonMappingException, JsonProcessingException {
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

	@GetMapping(value = "/v2/secure/auth/getAuthorisations")
	public List<Authorisation> getAuthorisations(HttpServletRequest request) throws JsonProcessingException {
		List<Authorisation> authorisations = authorisationRepository.findAll();
		return authorisations;
	}

	@GetMapping(value = "/v2/auth/getAuthConfig")
	public List<AuthConfig> getAuthConfig(HttpServletRequest request, @PathVariable String stxAddress) throws JsonProcessingException {
		List<AuthConfig> authConfigs = authConfigRepository.findAll();
		return authConfigs;
	}

	@GetMapping(value = "/v2/auth/getAuthorisation/{stxAddress}")
	public Authorisation getAuthorisation(HttpServletRequest request, @PathVariable String stxAddress) throws JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		return authorisation;
	}

	@GetMapping(value = "/v2/auth/isAuthorised/{stxAddress}/{domain}/{privilege}")
	public Boolean isAuthorised(HttpServletRequest request, @PathVariable String stxAddress, @PathVariable String domain, @PathVariable String privilege) throws JsonProcessingException {
		Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
		return authorisation.hasPrivilege(domain, privilege);
	}

}
