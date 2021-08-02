package com.radicle.mesh.stacks.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.stacks.service.ContractReader;

@EnableScheduling
public class ContractCacheBuilder {

	@Autowired private ContractReader contractReader;

	@Scheduled(fixedDelay=240000)
	public void buildCache() throws JsonProcessingException {
		contractReader.buildCache();
	}
}
