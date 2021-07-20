package com.radicle.mesh.privilege.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.privilege.service.domain.Privilege;

@Repository
public interface PrivilegeRepository extends MongoRepository<Privilege, String> {


}
