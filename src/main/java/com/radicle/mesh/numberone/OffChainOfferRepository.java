package com.radicle.mesh.numberone;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.numberone.domain.OffChainOffer;

@Repository
public interface OffChainOfferRepository extends MongoRepository<OffChainOffer, String> {

}
