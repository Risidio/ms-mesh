package com.radicle.mesh.crowdfund.service.domain.crowdfund;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@TypeAlias(value = "Inventory")
public class Inventory {

	private String meshId;
	private String listPrice;

}
