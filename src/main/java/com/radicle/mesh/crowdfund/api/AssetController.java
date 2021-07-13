package com.radicle.mesh.crowdfund.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.crowdfund.service.AssetRepository;
import com.radicle.mesh.crowdfund.service.domain.crowdfund.Asset;

@RestController
@EnableAsync
@EnableScheduling
// @CrossOrigin(origins = { "http://localhost:8085", "http://localhost:8082", "http://localhost:8080", "https://prom.risidio.com", "https://thisisnumberone.com", "https://staging.thisisnumberone.com", "https://tchange.risidio.com", "https://tchange.risidio.com", "https://xchange.risidio.com", "https://truma.risidio.com", "https://ruma.risidio.com", "https://loopbomb.risidio.com", "https://stacks.loopbomb.com", "https://stacksmate.com", "https://test.stacksmate.com" }, maxAge = 6000)
public class AssetController {

    private static final Logger logger = LogManager.getLogger(AssetController.class);
	@Autowired
	private AssetRepository assetRepository;

	@GetMapping(value = "/v2/asset/{id}")
	public Optional<Asset> assetById(@PathVariable String id) {
		Optional<Asset> o = assetRepository.findById(id);
		return o;
	}
	
	@GetMapping(value = "/v2/assetByMeshId/{meshId}")
	public List<Asset> assetByMeshId(@PathVariable String meshId) {
		List<Asset> assets = assetRepository.findByMeshId(meshId);
		return assets;
	}
	
	@GetMapping(value = "/v2/assets")
	public List<Asset> findAll() {
		List<Asset> assets = assetRepository.findAll();
		return assets;
	}
	
	@PostMapping(value = "/v2/asset")
	public Asset post(HttpServletRequest request, @RequestBody Asset asset) {
		return assetRepository.save(asset);
	}

	@PostMapping(value = "/v2/assets")
	public List<Asset> post(HttpServletRequest request, @RequestBody List<Asset> assets) {
		return assetRepository.saveAll(assets);
	}

	@PutMapping(value = "/v2/asset")
	public Asset put(HttpServletRequest request, @RequestBody Asset inventory) {
		return assetRepository.save(inventory);
	}

}
