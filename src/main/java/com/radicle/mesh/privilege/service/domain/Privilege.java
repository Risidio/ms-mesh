package com.radicle.mesh.privilege.service.domain;

import org.springframework.data.annotation.TypeAlias;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@TypeAlias(value = "Privilege")
public class Privilege {

	public String name;
	public Integer level;
	
}
