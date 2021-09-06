package com.radicle.mesh.loopbomb.api;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.loopbomb.service.LoopRunService;
import com.radicle.mesh.loopbomb.service.LoopbombRepository;
import com.radicle.mesh.loopbomb.service.domain.LoopbombConfig;
import com.radicle.mesh.stacks.service.domain.Token;

@RestController
public class LoopbombController {

    private static final Logger logger = LogManager.getLogger(LoopbombController.class);
	@Autowired private LoopbombRepository loopbombRepository;
	@Autowired private LoopRunService loopRunService;

	@GetMapping(value = "/v2/loopbomb/config")
	public LoopbombConfig getConfig() {
		List<LoopbombConfig> loopbombConfigs = loopbombRepository.findAll();
		LoopbombConfig lc = loopbombConfigs.get(loopbombConfigs.size() - 1);
		List<Token> tokens = loopRunService.getTokensByVersion(lc.getCurrentRunKey());
		lc.setTokenCount(tokens.size());
		return lc;
	}

	@GetMapping(value = "/v2/looprun/{currentRunKey}")
	public LoopbombConfig getConfig(@PathVariable String currentRunKey) {
		List<LoopbombConfig> loopbombConfigs = loopbombRepository.findByCurrentRunKey(currentRunKey);
		LoopbombConfig lc = loopbombConfigs.get(loopbombConfigs.size() - 1);
		List<Token> tokens = loopRunService.getTokensByVersion(lc.getCurrentRunKey());
		lc.setTokenCount(tokens.size());
		return lc;
	}
	
	@GetMapping(value = "/v2/looprun/tokens/{currentRunKey}")
	public List<Token> getTokens(@PathVariable String currentRunKey) {
		List<Token> tokens = loopRunService.getTokensByVersion(currentRunKey);
		return tokens;
	}
}
