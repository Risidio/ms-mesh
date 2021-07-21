package com.radicle.mesh.stacks.model.stxbuffer.types;

import org.springframework.data.annotation.TypeAlias;

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
@TypeAlias(value = "CacheUpdate")
public class CacheUpdate {

	private String functionName;
	private String type;
	private String contractId;
	private String assetHash;
	private String txId;
	private Long nftIndex;

}
