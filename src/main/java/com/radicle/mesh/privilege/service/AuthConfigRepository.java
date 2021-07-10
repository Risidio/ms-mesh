package com.radicle.mesh.privilege.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.privilege.service.domain.AuthConfig;

@Repository
public interface AuthConfigRepository extends MongoRepository<AuthConfig, String> {


}
