package com.radicle.mesh.service.registration;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.service.registration.domain.OffChainOffer;

@Repository
public interface OffChainOfferRepository extends MongoRepository<OffChainOffer, String> {

}
