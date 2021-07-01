package com.radicle.mesh.payments.service;

import com.radicle.mesh.payments.api.model.ProjectPaymentTotals;

public interface PaymentService
{
	public ProjectPaymentTotals getProjectPaymentTotals(String projectId);
}
