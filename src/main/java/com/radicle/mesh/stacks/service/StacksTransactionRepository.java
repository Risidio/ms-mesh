package com.radicle.mesh.stacks.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacks.model.stxbuffer.types.StacksTransaction;

@Repository
public interface StacksTransactionRepository extends MongoRepository<StacksTransaction, String> {

	public List<StacksTransaction> findByAssetHash(String assetHash);
	public List<StacksTransaction> findByAssetHashAndType(String assetHash, String type);
}
