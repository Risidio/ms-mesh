package com.radicle.mesh.mining.service;

import java.util.List;

import com.radicle.mesh.mining.service.domain.StacksBlockWinner;
import com.radicle.mesh.mining.service.domain.StacksMinerInfo;


public interface MiningService
{
	public List<StacksBlockWinner> findBlockWinners(Integer limit);

	public List<StacksMinerInfo> findMinerInfo(Integer limit);
}
