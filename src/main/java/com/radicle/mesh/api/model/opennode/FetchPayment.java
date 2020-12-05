package com.radicle.mesh.api.model.opennode;

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
public class FetchPayment {

	private String amount;
	private String description;
	private Boolean routeHints;

	public FetchPayment() {
		super();
	}
	
	@JsonProperty("route_hints")
	public Boolean getRouteHints() {
	    return routeHints;
	}

}
