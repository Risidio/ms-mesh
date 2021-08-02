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
@TypeAlias(value = "ExhibitRequest")
@Document
public class ExhibitRequest {

	@Id
	private String id;
	private int status;
	private String domain;
	private String email;
	private String name;
	private String stxAddress;
}
