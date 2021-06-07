package com.radicle.mesh.prom.service.domain;

import java.util.List;
import java.util.Map;

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
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "Asset")
@Document
public class Asset {

	private String projectId;
	private String name;
	private String description;
	private Inventory inventory;
	private Long created;
	private Long updated;
	private String owner;
	private String uploader;
	private String assetHash;
	private String imageUrl;
	private String assetUrl;
	private String assetProjectUrl;
	private String metaDataUrl;
	private String privacy;
	private String artist;
	private String objType;
	private String domain;
	private Map<String, String> metaData;
	private KeywordModel category;
	private List<KeywordModel> keywords;
	private String status;
	private Long tokenId;
	private NftMedia nftMedia;
	
}
