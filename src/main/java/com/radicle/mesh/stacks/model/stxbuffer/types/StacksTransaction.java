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
@TypeAlias(value = "StacksTransaction")
public class StacksTransaction {

	private long timestamp;
	private String contractId;
	private String assetHash;
	private String txId;
	private String type;
	private int status;

}
