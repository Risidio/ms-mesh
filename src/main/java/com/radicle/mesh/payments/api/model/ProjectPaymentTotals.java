package com.radicle.mesh.payments.api.model;

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
@TypeAlias(value = "ProjectPaymentTotals")
public class ProjectPaymentTotals {

	private Long stacksCount;
	private Long fiatCount;
	private Long opennodeCount;
	private Long paypalCount;
	private Long stacksTotal;
	private Long fiatTotal;
	private Long opennodeTotal;
	private Long paypalTotal;

}
