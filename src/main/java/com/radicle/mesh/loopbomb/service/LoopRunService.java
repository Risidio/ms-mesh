package com.radicle.mesh.loopbomb.service;

import java.util.List;

import com.radicle.mesh.stacks.service.domain.Token;

public interface LoopRunService
{
	public List<Token> getTokensByVersion(String versionId);
}
