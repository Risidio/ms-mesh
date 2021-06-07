package com.radicle.mesh.prom.service.domain;

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
@TypeAlias(value = "KeywordModel")
public class KeywordModel {

	private String id;
	private String name;
	private Integer level;
	private String parent;

}
