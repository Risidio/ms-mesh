package com.radicle.mesh.stacks.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacks.service.domain.TokenFilter;


@Repository
public interface TokenFilterRepository extends MongoRepository<TokenFilter, String> {

	public TokenFilter findByContractIdAndAssetHash(String contractId, String assetHash);

}
