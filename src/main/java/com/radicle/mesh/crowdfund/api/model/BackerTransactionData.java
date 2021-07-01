package com.radicle.mesh.crowdfund.api.model;

import org.springframework.data.annotation.TypeAlias;

import com.radicle.mesh.crowdfund.service.domain.crowdfund.Asset;
import com.radicle.mesh.payments.service.domain.Payment;

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
@TypeAlias(value = "BackerTransactionData")
public class BackerTransactionData {

	private Asset perk;
	private Asset asset;
	private Payment payment;
	
}
