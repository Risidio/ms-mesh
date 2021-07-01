package com.radicle.mesh.crowdfund.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.crowdfund.service.domain.crowdfund.Backer;

@Repository
public interface BackerRepository extends MongoRepository<Backer, String> {

	List<Backer> findByProjectId(String projectId);

	Optional<Backer> findByProjectIdAndUsername(String projectId, String username);

	Optional<Backer> findByStxAddress(String stxAddress);

	Optional<Backer> findByUsername(String username);

}
