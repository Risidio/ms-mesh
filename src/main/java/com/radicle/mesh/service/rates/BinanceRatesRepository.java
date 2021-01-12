package com.radicle.mesh.service.rates;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.service.rates.domain.BinanceRate;

@Repository
public interface BinanceRatesRepository extends MongoRepository<BinanceRate, String> {

}
