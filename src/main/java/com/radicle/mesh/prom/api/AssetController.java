package com.radicle.mesh.prom.api;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.prom.service.AssetRepository;
import com.radicle.mesh.prom.service.domain.Asset;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class AssetController {

	@Autowired
	private AssetRepository assetRepository;

	@GetMapping(value = "/v2/inventory/{assetHash}")
	public Optional<Asset> findOne(@PathVariable String assetHash) {
		Optional<Asset> o = assetRepository.findById(assetHash);
		return o;
	}
	
	@GetMapping(value = "/v2/inventory")
	public List<Asset> findAll() {
		return assetRepository.findAll();
	}
	
	@PostMapping(value = "/v2/inventory")
	public Asset post(HttpServletRequest request, @RequestBody Asset inventory) {
		return assetRepository.save(inventory);
	}

	@PostMapping(value = "/v2/inventories")
	public List<Asset> post(HttpServletRequest request, @RequestBody List<Asset> inventories) {
		return assetRepository.saveAll(inventories);
	}

	@PutMapping(value = "/v2/inventory")
	public Asset put(HttpServletRequest request, @RequestBody Asset inventory) {
		return assetRepository.save(inventory);
	}

}
