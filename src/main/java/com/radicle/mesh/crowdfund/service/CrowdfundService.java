package com.radicle.mesh.crowdfund.service;

import com.radicle.mesh.crowdfund.api.model.CrowdfundTarget;

public interface CrowdfundService
{
	public CrowdfundTarget getCrowdfundTarget(String projectId);
}
