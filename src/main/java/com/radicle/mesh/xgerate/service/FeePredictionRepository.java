package com.radicle.mesh.xgerate.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.xgerate.service.domain.FeeRatePrediction;

@Repository
public interface FeePredictionRepository extends MongoRepository<FeeRatePrediction, String> {

}
