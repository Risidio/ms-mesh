package com.radicle.mesh.cloudinary.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.cloudinary.service.domain.Configuration;

@Repository
public interface ConfigurationRepository extends MongoRepository<Configuration, String> {
}
