package com.radicle.mesh.crowdfund.service.domain.crowdfund;

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
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "Transaction")
public class Transaction {

	private String paymentId;
	private String assetId;
	private String perkId;

}
