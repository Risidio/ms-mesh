package com.radicle.mesh.loopbomb.service.domain;

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
@TypeAlias(value = "LoopbombConfig")
@Document
public class LoopbombConfig {

	@Id private String id;
	private Integer spinsPerDay;
	private String currentRunKey;
	private String currentRun;
	private Integer versionLimit;
	private Integer tokenCount;

}
