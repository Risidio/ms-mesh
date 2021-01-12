package com.radicle.mesh.api.model.square;

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
public class PaymentRequest {

	private String nonce;
	private String locationId;
	private String idempotencyKey;
	private Long amountFiat;
	private String currency;

	public PaymentRequest() {
		super();
	}
}
