package com.radicle.mesh.service.rates.domain;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import com.radicle.mesh.service.mining.domain.StacksBlockWinner;
import com.radicle.mesh.service.mining.domain.StacksMinerInfo;

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
@TypeAlias(value = "MinerList")
public class MinerList {

	private List<StacksBlockWinner> stacksBlockWinner;
	private List<StacksMinerInfo> stacksMinerInfo;

	public MinerList() {
		super();
	}
}
