package com.bidlogix.mesh.service;

import java.util.Optional;

import com.bidlogix.mesh.service.domain.Configuration;


public interface ConfigurationService
{
	public Configuration save(Configuration configuration);
	public Optional<? extends Configuration> findById(String configId);
}
