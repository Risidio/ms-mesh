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

import com.radicle.mesh.prom.service.InventoryRepository;
import com.radicle.mesh.prom.service.domain.Inventory;

@RestController
@EnableAsync
@EnableScheduling
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class InventoryController {

	@Autowired
	private InventoryRepository inventoryRepository;

	@GetMapping(value = "/v2/inventory/{assetHash}")
	public Optional<Inventory> findOne(@PathVariable String assetHash) {
		Optional<Inventory> o = inventoryRepository.findById(assetHash);
		return o;
	}
	
	@GetMapping(value = "/v2/inventory")
	public List<Inventory> findAll() {
		return inventoryRepository.findAll();
	}
	
	@PostMapping(value = "/v2/inventory")
	public Inventory post(HttpServletRequest request, @RequestBody Inventory inventory) {
		return inventoryRepository.save(inventory);
	}

	@PutMapping(value = "/v2/inventory")
	public Inventory put(HttpServletRequest request, @RequestBody Inventory inventory) {
		return inventoryRepository.save(inventory);
	}

}
