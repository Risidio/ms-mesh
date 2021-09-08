package com.radicle.mesh.stacksmate.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.stacksmate.service.domain.StacksMateTransaction;

@Repository
public interface StacksMateRepository extends MongoRepository<StacksMateTransaction, String> {

	public StacksMateTransaction findByNonce(Long nonce);

	public StacksMateTransaction findTopByOrderByNonceDesc();

	public StacksMateTransaction findByPaymentId(String paymentId);

	public List<StacksMateTransaction> findByRecipient(String recipient, Sort sort);

}
