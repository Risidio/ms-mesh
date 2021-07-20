package com.radicle.mesh.stacks.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.domain.AppMapContract;

@EnableScheduling
public class ContractCacheBuilder {

	@Autowired private ContractReader contractReader;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;

	@Scheduled(fixedDelay=240000)
	public void buildCache() throws JsonProcessingException {
		AppMapContract registry = contractReader.buildCache();
		simpMessagingTemplate.convertAndSend("/queue/contract-news", registry);
	}
}
