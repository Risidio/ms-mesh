package com.radicle.mesh.crowdfund.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.crowdfund.service.domain.CrowdFund;

@Repository
public interface CrowdFundRepository extends MongoRepository<CrowdFund, String> {

}
