package com.radicle.mesh.stacksactions.service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(value = "StacksTransaction")
@Document
public class StacksTransaction {

	@Id private String id;
	private long timestamp;
	private Long nftIndex;
	private String contractId;
	private String functionName;
	private String assetHash;
	private String txId;
	private String type;
	private String txStatus;

}
