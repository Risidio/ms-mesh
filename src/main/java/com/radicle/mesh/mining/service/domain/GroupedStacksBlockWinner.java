package com.radicle.mesh.mining.service.domain;

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
@TypeAlias(value = "GroupedStacksBlockWinner")
public class GroupedStacksBlockWinner {

	private Long count;
	private String _id;
	private Long totalBurnFee;
	
	public GroupedStacksBlockWinner() {
		super();
	}

}
