package com.radicle.mesh.service.cloudinary;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radicle.mesh.service.cloudinary.domain.Configuration;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired private ConfigurationRepository configurationRepository;

	@Override
	public Configuration save(Configuration configuration) {
		configuration.setUpdated(System.currentTimeMillis());
		return configurationRepository.save(configuration);
	}

	@Override
	public Optional<? extends Configuration> findById(String configId) {
		Optional<Configuration> configuration = configurationRepository.findById(configId);
		return configuration;
	}
}
