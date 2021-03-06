package com.radicle.mesh.crowdfund.service.domain.crowdfund;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(value = "DonationData")
@Document
public class Asset {

	@Id private String id;
	private String projectId;
	private String name;
	private String description;
	private Integer edition;
	private Integer maxEditions;
	private Long created;
	private Long updated;
	private String owner;
	private String uploader;
	private String assetHash;
	private String image;
	private String externalUrl;
	private String metaDataUrl;
	private String currentRunKey;
	private String privacy;
	private String artist;
	private String objType;
	private String domain;
	private Map<String, String> metaData;
	private KeywordModel category;
	private List<KeywordModel> keywords;
	private String status;
	private Long tokenId;
	private Attributes attributes;
	private Inventory inventory;
	
}
