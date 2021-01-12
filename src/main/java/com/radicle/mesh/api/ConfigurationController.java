package com.radicle.mesh.api;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.service.cloudinary.CloudinarySettings;
import com.radicle.mesh.service.cloudinary.domain.CloudinaryConfig;

@RestController
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class ConfigurationController {

	@Autowired private CloudinarySettings cloudinarySettings;

	@GetMapping(value = "/configuration/cloudinary")
	public CloudinaryConfig get(HttpServletRequest request) throws IllegalAccessException, InvocationTargetException {
		CloudinaryConfig config = new CloudinaryConfig();
		BeanUtils.copyProperties(config, cloudinarySettings);
		return config;
	}
}
