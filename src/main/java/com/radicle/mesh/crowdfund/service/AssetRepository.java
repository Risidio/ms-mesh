package com.radicle.mesh.crowdfund.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.crowdfund.service.domain.crowdfund.Asset;

@Repository
public interface AssetRepository extends MongoRepository<Asset, String> {

    @Query(value = "{ 'inventory.meshId' : ?#{[0]} }")
    List<Asset> findByMeshId(String meshId);

}
