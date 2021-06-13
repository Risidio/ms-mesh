package com.radicle.mesh.stacks.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacks.model.stxbuffer.types.StacksTransaction;

@Repository
public interface StacksTransactionRepository extends MongoRepository<StacksTransaction, String> {

}
