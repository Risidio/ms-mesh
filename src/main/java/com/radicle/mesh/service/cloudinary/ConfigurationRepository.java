package com.radicle.mesh.service.cloudinary;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.service.cloudinary.domain.Configuration;

@Repository
public interface ConfigurationRepository extends MongoRepository<Configuration, String> {
}
