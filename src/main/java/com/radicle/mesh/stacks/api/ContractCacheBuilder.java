package com.radicle.mesh.stacks.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.stacks.model.stxbuffer.types.CacheUpdateResult;
import com.radicle.mesh.stacks.service.AppMapContractRepository;
import com.radicle.mesh.stacks.service.ApplicationRepository;
import com.radicle.mesh.stacks.service.ContractReader;
import com.radicle.mesh.stacks.service.TokenRepository;
import com.radicle.mesh.stacks.service.domain.AppMapContract;
import com.radicle.mesh.stacks.service.domain.Application;
import com.radicle.mesh.stacks.service.domain.Token;

@Configuration
@EnableScheduling
public class ContractCacheBuilder {

	@Autowired private ContractReader contractReader;
	@Autowired private ApplicationRepository applicationRepository;
	@Autowired private AppMapContractRepository appMapContractRepository;
	@Autowired private TokenRepository tokenRepository;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	@Value("${radicle.stax.admin-contract-address}") String adminContractAddress;
	@Value("${radicle.stax.admin-contract-name}") String adminContractName;

	/**
	 * read the entire contract every 12 hours
	 * @throws JsonProcessingException
	 */
	@Scheduled(fixedDelay=43200000)
	public void buildCache() throws JsonProcessingException {
		contractReader.buildCache();
	}

	/**
	 * check for new NFT every 5 seconds
	 * @throws JsonProcessingException
	 */
	@Scheduled(fixedDelay=15000)
	public void checkCache() throws JsonProcessingException {
		AppMapContract appMapContract = contractReader.readAppMap(null);
		AppMapContract appMapContractDb = appMapContractRepository.findByAdminContractAddressAndAdminContractName(adminContractAddress, adminContractName);
		if (appMapContract.getAppCounter() != appMapContractDb.getAppCounter()) {
			// TODO new applications to cache - just rebuild the whole cache and improve this strategy later
			contractReader.buildCache();
			return;
		}
		for (long i = 0; i < appMapContract.getAppCounter(); i++) {
			Application application = contractReader.readApplication(i);
			Long numbTokensDb = tokenRepository.countByContractId(application.getContractId());
			Long numbTokens = application.getTokenContract().getMintCounter();
			if (numbTokensDb < numbTokens) {
				List<Token> tokens = new ArrayList<Token>();
				for (long index = numbTokensDb; index < numbTokens; index++) {
					Token token = contractReader.readSpecificToken(application.getContractId(), index);
					tokens.add(token);
				}
				CacheUpdateResult cr = new CacheUpdateResult(tokens, null);
				simpMessagingTemplate.convertAndSend("/queue/contract-news-" + application.getContractId(), cr);
			}
		}
	}
}
