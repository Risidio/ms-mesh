package com.bidlogix.mesh.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinarySettings {
	private String cloudName;
	private String apiKey;
	private String apiSecret;
	private String envVar;
	private String baseDeliveryUrl;
	private String secureDeliveryUrl;
	private String apiBaseUrl;

	public String getCloudName() {
		return cloudName;
	}

	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	public String getEnvVar() {
		return envVar;
	}

	public void setEnvVar(String envVar) {
		this.envVar = envVar;
	}

	public String getBaseDeliveryUrl() {
		return baseDeliveryUrl;
	}

	public void setBaseDeliveryUrl(String baseDeliveryUrl) {
		this.baseDeliveryUrl = baseDeliveryUrl;
	}

	public String getSecureDeliveryUrl() {
		return secureDeliveryUrl;
	}

	public void setSecureDeliveryUrl(String secureDeliveryUrl) {
		this.secureDeliveryUrl = secureDeliveryUrl;
	}

	public String getApiBaseUrl() {
		return apiBaseUrl;
	}

	public void setApiBaseUrl(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}

}
