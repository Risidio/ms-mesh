package com.radicle.mesh.privilege.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.privilege.service.domain.Authorisation;

@Repository
public interface AuthorisationRepository extends MongoRepository<Authorisation, String> {

	Authorisation findByStxAddress(String stxAddress);

}
