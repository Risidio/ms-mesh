package com.radicle.mesh.api.registration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.radicle.mesh.service.registration.EmailService;
import com.radicle.mesh.service.registration.RegistrationRepository;
import com.radicle.mesh.service.registration.domain.Registration;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class RegistrationController {

	@Autowired private RegistrationRepository registrationRepository;
    @Autowired private EmailService emailService;

	@PostMapping(value = "/v2/register")
	public Boolean accounts(HttpServletRequest request, @RequestBody Registration registration) throws JsonMappingException, JsonProcessingException {
		registration.setStatus(0);
		registrationRepository.save(registration);
		emailService.sendEmail(registration.getEmail());
		return true;
	}

}
