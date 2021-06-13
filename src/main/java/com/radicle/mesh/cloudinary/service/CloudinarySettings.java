package com.radicle.mesh.cloudinary.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinarySettings {
	private String cloudName;
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
