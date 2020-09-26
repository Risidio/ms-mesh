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
public class ReadContractParameters {

	private String sender;
	private String[] arguments;

	public ReadContractParameters() {
		super();
	}
}
