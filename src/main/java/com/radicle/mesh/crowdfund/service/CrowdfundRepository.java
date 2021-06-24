package com.radicle.mesh.crowdfund.service;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.crowdfund.service.domain.Crowdfund;

@Repository
public interface CrowdfundRepository extends MongoRepository<Crowdfund, String> {

	Optional<Crowdfund> findByProjectId(String projectId);

}
