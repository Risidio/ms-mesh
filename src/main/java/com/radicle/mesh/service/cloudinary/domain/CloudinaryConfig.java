package com.radicle.mesh.service.cloudinary.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CloudinaryConfig {

	private String uuid;
	private String cloudName;
	private String apiKey;
	private String apiSecret;
	private String envVar;
	private String baseDeliveryUrl;
	private String secureDeliveryUrl;
	private String apiBaseUrl;

	public CloudinaryConfig() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}
} 
