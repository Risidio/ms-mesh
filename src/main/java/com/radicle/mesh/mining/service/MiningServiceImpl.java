package com.radicle.mesh.mining.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.radicle.mesh.mining.service.domain.StacksBlockWinner;
import com.radicle.mesh.mining.service.domain.StacksMinerInfo;

@Service
public class MiningServiceImpl implements MiningService {

	@Autowired private MongoTemplate mongoTemplate;
	@Autowired private StacksBlockWinnerRepository stacksBlockWinnerRepository;

	@Override
	public List<StacksBlockWinner> findBlockWinners(Integer limit) {
		Query query = new Query();
		query.with(Sort.by(Sort.Direction.ASC, "stacksBlockHeight"));
		if (limit != null) query.limit(limit);
		List<StacksBlockWinner> rates = mongoTemplate.find(query, StacksBlockWinner.class);
		return rates;
	}

	@Override
	public List<StacksMinerInfo> findMinerInfo(Integer limit) {
		Query query = new Query();
		query.with(Sort.by(Sort.Direction.ASC, "actualWin"));
		if (limit != null) query.limit(limit);
		List<StacksMinerInfo> rates = mongoTemplate.find(query, StacksMinerInfo.class);
		return rates;
	}
}
