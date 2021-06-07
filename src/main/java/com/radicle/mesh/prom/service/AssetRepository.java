package com.radicle.mesh.prom.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.prom.service.domain.Asset;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {

}
