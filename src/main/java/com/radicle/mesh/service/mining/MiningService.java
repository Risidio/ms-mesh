package com.radicle.mesh.service.mining;

import java.util.List;

import com.radicle.mesh.service.mining.domain.StacksBlockWinner;
import com.radicle.mesh.service.mining.domain.StacksMinerInfo;


public interface MiningService
{
	public List<StacksBlockWinner> findBlockWinners(Integer limit);

	public List<StacksMinerInfo> findMinerInfo(Integer limit);
}
