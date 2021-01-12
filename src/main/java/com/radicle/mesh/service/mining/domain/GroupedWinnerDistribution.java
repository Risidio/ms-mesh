package com.radicle.mesh.service.mining.domain;

import org.springframework.data.annotation.TypeAlias;

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
@TypeAlias(value = "GroupedWinnerDistribution")
public class GroupedWinnerDistribution {

	private Long _id;
	private Long count;
	private Long totalBurned;
	private Long totalBlocksMined;

	public GroupedWinnerDistribution() {
		super();
	}

}
