package com.radicle.mesh.service.rates;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.service.rates.domain.TickerRate;

@Repository
public interface TickerRatesRepository extends MongoRepository<TickerRate, String> {

}
