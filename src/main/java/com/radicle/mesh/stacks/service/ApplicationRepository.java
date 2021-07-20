package com.radicle.mesh.stacks.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacks.service.domain.Application;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {

	public Application findByContractId(String contractId);
}
