package com.radicle.mesh.crowdfund.service;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.crowdfund.service.domain.crowdfund.Backer;

@Repository
public interface BackerRepository extends MongoRepository<Backer, String> {

	Optional<Backer> findByStxAddress(String keyValue);

	Optional<Backer> findByEmail(String keyValue);

	Optional<Backer> findByUsername(String keyValue);

}
