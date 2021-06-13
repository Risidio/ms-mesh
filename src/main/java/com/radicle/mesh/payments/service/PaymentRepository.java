package com.radicle.mesh.payments.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.payments.service.domain.Payment;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

}
