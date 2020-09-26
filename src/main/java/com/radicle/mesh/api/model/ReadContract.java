package com.radicle.mesh.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class ReadContract {

	private String contractAddress;
	private String contractName;
	private String functionName;
	private ReadContractParameters rcp;

	public ReadContract() {
		super();
	}
}
