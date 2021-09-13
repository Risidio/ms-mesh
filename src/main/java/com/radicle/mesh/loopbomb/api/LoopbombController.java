package com.radicle.mesh.loopbomb.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.loopbomb.service.LoopRunRepository;
import com.radicle.mesh.loopbomb.service.LoopRunService;
import com.radicle.mesh.loopbomb.service.LoopSpinRepository;
import com.radicle.mesh.loopbomb.service.domain.LoopRun;
import com.radicle.mesh.loopbomb.service.domain.LoopSpin;
import com.radicle.mesh.stacks.service.domain.Token;

@RestController
public class LoopbombController {

    private static final Logger logger = LogManager.getLogger(LoopbombController.class);
	@Autowired private LoopRunRepository loopRunRepository;
	@Autowired private LoopSpinRepository loopSpinRepository;
	@Autowired private LoopRunService loopRunService;

	@GetMapping(value = "/v2/loopbomb/config")
	public Map<String, Object> getConfig() {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("loopRun", getLoopRun());
		return resp;
	}

	@GetMapping(value = "/v2/loopbomb/config/{stxAddress}/{dayOfYear}/{year}")
	public Map<String, Object> getUserConfig(@PathVariable String stxAddress, @PathVariable Integer dayOfYear, @PathVariable Integer year) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("loopRun", getLoopRun());
		List<LoopSpin> loopSpins = loopSpinRepository.findByStxAddressAndDayOfYearAndYear(stxAddress, dayOfYear, year);
		if (loopSpins != null) {
			resp.put("loopSpinsToday", loopSpins.size());
		} else {
			resp.put("loopSpinsToday", 0);
		}
		return resp;
	}

	private LoopRun getLoopRun() {
		List<LoopRun> loopbombConfigs = loopRunRepository.findAll();
		LoopRun loopRun = loopbombConfigs.get(loopbombConfigs.size() - 1);
		List<Token> tokens = loopRunService.getTokensByRunKey(loopRun.getCurrentRunKey());
		loopRun.setTokenCount(tokens.size());
		return loopRun;
	}

	@GetMapping(value = "/v2/looprun/{currentRunKey}")
	public LoopRun getConfig(@PathVariable String currentRunKey) {
		List<LoopRun> loopbombConfigs = loopRunRepository.findByCurrentRunKey(currentRunKey);
		LoopRun lc = loopbombConfigs.get(loopbombConfigs.size() - 1);
		List<Token> tokens = loopRunService.getTokensByRunKey(lc.getCurrentRunKey());
		lc.setTokenCount(tokens.size());
		return lc;
	}
	
	@PutMapping(value = "/v2/loopspin")
	public LoopSpin getConfig(@RequestBody LoopSpin loopSpin) {
		loopSpin = loopSpinRepository.save(loopSpin);
		return loopSpin;
	}
	
	@GetMapping(value = "/v2/loopspin/{stxAddress}")
	public List<LoopSpin> loopspin(@PathVariable String stxAddress) {
		List<LoopSpin> loopSpins = loopSpinRepository.findByStxAddress(stxAddress);
		return loopSpins;
	}
	
	@GetMapping(value = "/v2/loopspin/{stxAddress}/{dayOfYear}/{year}")
	public List<LoopSpin> loopspin(@PathVariable String stxAddress, @PathVariable Integer dayOfYear, @PathVariable Integer year) {
		List<LoopSpin> loopSpins = loopSpinRepository.findByStxAddressAndDayOfYearAndYear(stxAddress, dayOfYear, year);
		return loopSpins;
	}
	
	@GetMapping(value = "/v2/looprun/tokens/{currentRunKey}")
	public List<Token> getTokens(@PathVariable String currentRunKey) {
		List<Token> tokens = loopRunService.getTokensByRunKey(currentRunKey);
		return tokens;
	}
}
