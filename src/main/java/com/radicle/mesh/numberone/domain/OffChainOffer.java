package com.radicle.mesh.numberone.domain;

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
@TypeAlias(value = "OffChainOffer")
@Document
public class OffChainOffer {

	@Id	private String id;
	private Integer status;
	private String domain;
	private String email;
	private String contractAddress;
	private String contractName;
	private String assetHash;
	private String offerer;
	private Long nftIndex;
	private Integer saleCycle;
	private Long appTimestamp;
	private Long amount;
	private String emailContent;

}
