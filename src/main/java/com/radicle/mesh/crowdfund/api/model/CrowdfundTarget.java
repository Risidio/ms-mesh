package com.radicle.mesh.crowdfund.api.model;

import org.springframework.data.annotation.TypeAlias;

import com.radicle.mesh.payments.api.model.ProjectPaymentTotals;

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
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(value = "CrowdfundTarget")
public class CrowdfundTarget {

	private ProjectPaymentTotals projectPaymentTotals;
	private BackerData backerData;
	
}
