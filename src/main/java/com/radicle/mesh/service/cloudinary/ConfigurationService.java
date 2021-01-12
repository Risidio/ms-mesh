package com.radicle.mesh.service.cloudinary;

import java.util.Optional;

import com.radicle.mesh.service.cloudinary.domain.Configuration;


public interface ConfigurationService
{
	public Configuration save(Configuration configuration);
	public Optional<? extends Configuration> findById(String configId);
}
