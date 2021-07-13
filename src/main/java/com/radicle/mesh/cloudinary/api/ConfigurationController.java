package com.radicle.mesh.cloudinary.api;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.cloudinary.service.CloudinarySettings;
import com.radicle.mesh.cloudinary.service.domain.CloudinaryConfig;

@RestController
// @CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class ConfigurationController {

	@Autowired private CloudinarySettings cloudinarySettings;

	@GetMapping(value = "/configuration/cloudinary")
	public CloudinaryConfig get(HttpServletRequest request) throws IllegalAccessException, InvocationTargetException {
		CloudinaryConfig config = new CloudinaryConfig();
		BeanUtils.copyProperties(config, cloudinarySettings);
		return config;
	}
}
