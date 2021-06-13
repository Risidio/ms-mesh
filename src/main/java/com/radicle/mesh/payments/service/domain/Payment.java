package com.radicle.mesh.payments.service.domain;

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
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(value = "Payment")
@Document
public class Payment {

	@Id private String id;
	private String projectId;
	private String paymentType;
	private MoneyModal amountMoney;
	private MoneyModal totalMoney;
	private String createdAt;
	private String squareId;
	private String customerId;
	private String locationId;
	private String orderId;
	private String sourceType;
	private String receiptNumber;
	private String receiptUrl;
	private String updatedAt;
	
}
