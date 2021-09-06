package com.radicle.mesh.loopbomb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.radicle.mesh.stacks.service.domain.Token;

@Service
public class LoopRunServiceImpl implements LoopRunService {

	@Autowired
	private MongoTemplate mongoTemplate;
 
	public List<Token> getTokensByVersion(String versionId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("tokenInfo.metaDataUrl").regex("/" + versionId + "/"));
		List<Token> tokens = mongoTemplate.find(query, Token.class);
		return tokens;
	}
}