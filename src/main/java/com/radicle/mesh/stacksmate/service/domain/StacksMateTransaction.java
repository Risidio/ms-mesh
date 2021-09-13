package com.radicle.mesh.stacksmate.service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

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
@TypeAlias(value = "LoopRun")
@Document
public class StacksMateTransaction {

	@Id private String id;
	private String paymentUrl;
	private String paymentOrderId;
	private String paymentStatus;
	private String paymentId;
	private String paymentTx;
	private String paymentCode;
	private String paymentAmount;
	private String paymentCurrency;
	private String stxAddress;
	private String txId;
	private String txStatus;
	private Long nonce;
	private Long timeSent;
	private Long microstx;
	private String recipient;

}
