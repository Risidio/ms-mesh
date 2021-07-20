package com.radicle.mesh.stacks.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacks.service.domain.AppMapContract;

@Repository
public interface AppMapContractRepository extends MongoRepository<AppMapContract, String> {

	public AppMapContract findByAdminContractAddressAndAdminContractName(String adminContractAddress, String adminContractName);
}
