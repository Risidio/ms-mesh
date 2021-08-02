package com.radicle.mesh.numberone;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.numberone.domain.ExhibitRequest;

@Repository
public interface ExhibitRequestRepository extends MongoRepository<ExhibitRequest, String> {

	public List<ExhibitRequest> findByStatus(int status);
	public ExhibitRequest findByStxAddress(String stxAddress);
}
