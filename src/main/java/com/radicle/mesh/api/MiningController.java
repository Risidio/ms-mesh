package com.radicle.mesh.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radicle.mesh.service.mining.MiningService;
import com.radicle.mesh.service.mining.StacksBlockWinnerRepository;
import com.radicle.mesh.service.mining.StacksMinerInfoRepository;
import com.radicle.mesh.service.mining.domain.GroupedStacksBlockWinner;
import com.radicle.mesh.service.mining.domain.GroupedWinnerDistribution;
import com.radicle.mesh.service.mining.domain.StacksBlockWinner;
import com.radicle.mesh.service.mining.domain.StacksMinerInfo;
import com.radicle.mesh.service.rates.domain.MinerList;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class MiningController {

	@Autowired private RestOperations restTemplate;
	@Autowired private StacksBlockWinnerRepository stacksBlockWinnerRepository;
	@Autowired private StacksMinerInfoRepository stacksMinerInfoRepository;
	@Autowired private MiningService miningService;
	@Value("${radicle.stx-mining.mining-path}") String miningPath;
	@Autowired private ObjectMapper mapper;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	private boolean gatherMiningStats = false;

	@Scheduled(fixedDelay=60000)
	public void refreshMinerList() throws JsonProcessingException {
		if (gatherMiningStats) {
			try {
				MinerList minerList = fetchMiningInfoInternal();
				stacksBlockWinnerRepository.deleteAll();
				stacksBlockWinnerRepository.saveAll(minerList.getStacksBlockWinner());
				stacksMinerInfoRepository.deleteAll();
				stacksMinerInfoRepository.saveAll(minerList.getStacksMinerInfo());
			} catch (Exception e) {
				// exception because Daemon end point is down.
			}
		}
	}

	@Scheduled(fixedDelay=60000)
	public void pushData() throws JsonProcessingException {
		if (gatherMiningStats) {
			simpMessagingTemplate.convertAndSend("/queue/stacks-mining-news", miningNews());
		}
	}

	@GetMapping(value = "/v1/stacks-mining/toggle-collector")
	public Boolean toggleCollector() {
		gatherMiningStats = !gatherMiningStats;
		return gatherMiningStats;
	}

	@GetMapping(value = "/v1/stacks-mining-news")
	public Map<String, Object> miningNews() throws JsonProcessingException {
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("groupByWinners", groupByWinners());
		results.put("groupByDistribution", groupByDistribution());
		results.put("groupByActualWins", groupByActualWins());
		results.put("findMinerInfo", findMinerInfo(6000));
		results.put("findBlockWinners", findBlockWinners(3000));
		return results;
	}

	@GetMapping(value = "/v1/stacks-mining/winners-grouped")
	public List<GroupedStacksBlockWinner> groupByWinners() {
		List<GroupedStacksBlockWinner> rates = stacksBlockWinnerRepository.groupByWinners();
		return rates;
	}

	@GetMapping(value = "/v1/stacks-mining/distribution")
	public List<GroupedStacksBlockWinner> groupByDistribution() {
		List<GroupedStacksBlockWinner> rates = stacksBlockWinnerRepository.groupByDistribution();
		return rates;
	}

	@GetMapping(value = "/v1/stacks-mining/groupByActualWins")
	public List<GroupedWinnerDistribution> groupByActualWins() {
		List<GroupedWinnerDistribution> rates = stacksMinerInfoRepository.groupByActualWins();
		return rates;
	}

	@GetMapping(value = "/v1/stacks-mining/all-blocks/{limit}")
	public List<StacksBlockWinner> findBlockWinners(@PathVariable Integer limit) {
		List<StacksBlockWinner> rates = miningService.findBlockWinners(limit);
		return rates;
	}

	@GetMapping(value = "/v1/stacks-mining/actual-wins/{limit}")
	public List<StacksMinerInfo> findMinerInfo(@PathVariable Integer limit) {
		List<StacksMinerInfo> rates = miningService.findMinerInfo(limit);
		return rates;
	}

	private MinerList fetchMiningInfoInternal() throws JsonMappingException, JsonProcessingException {
		HttpEntity<String> e = new HttpEntity<String>(getHeaders());
		ResponseEntity<String> response = restTemplate.exchange(miningPath, HttpMethod.GET, e, String.class);
		String jsonResp = response.getBody();
		Map<String, Object> rates = mapper.readValue(jsonResp, new TypeReference<Map<String, Object>>() {});
		MinerList minerList = new MinerList();
		
		String sbw = mapper.writeValueAsString(rates.get("mining_info"));
		List<StacksBlockWinner> sbwList = mapper.readValue(sbw, new TypeReference<List<StacksBlockWinner>>() {});

		String smi = mapper.writeValueAsString(rates.get("miner_info"));
		List<StacksMinerInfo> smiList = mapper.readValue(smi, new TypeReference<List<StacksMinerInfo>>() {});

		minerList.setStacksBlockWinner(sbwList);
		minerList.setStacksMinerInfo(smiList);
		return minerList;
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}
