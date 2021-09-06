package com.radicle.mesh.stacksactions.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacksactions.service.domain.StacksTransaction;

@Repository
public interface StacksTransactionRepository extends MongoRepository<StacksTransaction, String> {

	public StacksTransaction findByTxId(String txId);
	public List<StacksTransaction> findByTxStatus(String txStatus);
	public List<StacksTransaction> findByContractIdAndTxStatus(String contractId, String txStatus);
	public List<StacksTransaction> findByContractIdAndNftIndexAndTxStatus(String contractId, Long nftIndex, String txStatus);
	public List<StacksTransaction> findByContractIdAndAssetHash(String contractId, String assetHash);
	public List<StacksTransaction> findByContractIdAndNftIndex(String contractId, Long nftIndex);
	public List<StacksTransaction> findByContractIdAndFunctionName(String contractId, String functionName);
}
