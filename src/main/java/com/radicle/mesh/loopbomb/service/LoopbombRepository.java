package com.radicle.mesh.loopbomb.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.loopbomb.service.domain.LoopbombConfig;

@Repository
public interface LoopbombRepository extends MongoRepository<LoopbombConfig, String> {
    
    List<LoopbombConfig> findByCurrentRunKey(String currentRunKey);

}
