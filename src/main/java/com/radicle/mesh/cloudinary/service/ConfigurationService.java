package com.radicle.mesh.cloudinary.service;

import java.util.Optional;

import com.radicle.mesh.cloudinary.service.domain.Configuration;


public interface ConfigurationService
{
	public Configuration save(Configuration configuration);
	public Optional<? extends Configuration> findById(String configId);
}
