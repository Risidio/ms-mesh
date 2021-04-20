package com.radicle.mesh.service.registration;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.api.model.stxbuffer.types.StacksTransaction;

@Repository
public interface StacksTransactionRepository extends MongoRepository<StacksTransaction, String> {

}
