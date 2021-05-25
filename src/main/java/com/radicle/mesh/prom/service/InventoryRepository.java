package com.radicle.mesh.prom.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.prom.service.domain.Inventory;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {

}
