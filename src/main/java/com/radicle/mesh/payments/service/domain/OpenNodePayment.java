package com.radicle.mesh.payments.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class OpenNodePayment {

	private String amount;
	private String description;
	private String callback_url;
	private Boolean routeHints;

	public OpenNodePayment() {
		super();
	}
	
	@JsonProperty("route_hints")
	public Boolean getRouteHints() {
	    return routeHints;
	}

}
