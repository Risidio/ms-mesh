package com.radicle.mesh.prom.service.domain;

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
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "Inventory")
@Document
public class Backer {

	@Id	private String id;
	private Integer status;
	private String username;
	private String listPrice;
	private String assetHash;
	private String assetName;
	private String owner;
	private Long nftIndex;

}
