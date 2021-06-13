package com.radicle.mesh.payments.service.domain;

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
public class SquarePaymentRequest {

	private String nonce;
	private String locationId;
	private String idempotencyKey;
	private Long amountFiat;
	private String currency;

	public SquarePaymentRequest() {
		super();
	}
}
