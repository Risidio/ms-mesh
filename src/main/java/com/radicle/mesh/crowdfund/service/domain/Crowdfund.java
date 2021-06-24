package com.radicle.mesh.crowdfund.service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.radicle.mesh.crowdfund.service.domain.crowdfund.NftMedia;
import com.radicle.mesh.payments.service.domain.Money;

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
@TypeAlias(value = "Crowdfund")
@Document
public class Crowdfund {

	@Id private String id;
	private String projectId;
	private String name;
	private String description;
	private Money target;
	private Money runningTotal;
	private NftMedia nftMedia;
}
