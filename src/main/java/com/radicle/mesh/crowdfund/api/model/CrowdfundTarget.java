package com.radicle.mesh.crowdfund.api.model;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

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

	private List<DonationData> donationData;
	private BackerData backerData;
	
}
