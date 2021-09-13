package com.radicle.mesh.loopbomb.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.loopbomb.service.domain.LoopSpin;

/**
 * A loop spin is a counter for keeping track of the number of spins
 * a user has made in a given day. This is a useful metric as it measures both the
 * total count, the distribution across accounts and the distribution across
 * days of the year. It also helps controls the scarcity of 'good' artwork 
 * minted using a generative art application.
 * @author mikey
 *
 */
@Repository
public interface LoopSpinRepository extends MongoRepository<LoopSpin, String> {
    
    List<LoopSpin> findByStxAddress(String stxAddress);
    List<LoopSpin> findByStxAddressAndDayOfYearAndYear(String stxAddress, Integer dayOfYear, Integer year);
}
