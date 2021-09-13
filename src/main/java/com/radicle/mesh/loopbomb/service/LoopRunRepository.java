package com.radicle.mesh.loopbomb.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.loopbomb.service.domain.LoopRun;

/**
 * A loop run is a set of config params for a specific version or run 
 * of artwork. For example run_1 is a unique key that will be added to
 * the meta data of each NFT minted in this run - this might refer to
 * 300 artworks featuring the artwork of artist X.
 * @author mikey
 *
 */
@Repository
public interface LoopRunRepository extends MongoRepository<LoopRun, String> {
    
    List<LoopRun> findByCurrentRunKey(String currentRunKey);

}
