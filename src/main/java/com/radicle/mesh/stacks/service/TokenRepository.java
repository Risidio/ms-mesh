package com.radicle.mesh.stacks.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacks.service.domain.Token;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {

    @Query(value = "{ 'tokenInfo.assetHash' : ?#{[0]}, 'tokenInfo.edition' : ?#{[1]} }")
    // public Page<Token> findByAssetHashAndEdition(String assetHash, Long edition, Pageable pageable);
	public List<Token> findByAssetHashAndEdition(String assetHash, Long edition);

    @Query(value = "{ 'contractId' : ?#{[0]}, 'tokenInfo.edition' : ?#{[1]} }")
	public List<Token> findByContractIdAndEdition(String contractId, Long edition);
    
	public Token findByContractIdAndNftIndex(String contractId, Long nftIndex);
	
	public Long countByContractId(String contractId);
	
	public List<Token> findByContractIdAndOwner(String contractId, String owner);
	
	public List<Token> findByContractId(String contractId);

	public void deleteByContractId(String contractId);
}
