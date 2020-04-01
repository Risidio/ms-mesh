package com.bidlogix.mesh.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bidlogix.mesh.service.domain.Configuration;

@Repository
public interface ConfigurationRepository extends MongoRepository<Configuration, String> {
}
