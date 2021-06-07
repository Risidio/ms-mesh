package com.radicle.mesh.prom.service.domain;

import org.springframework.data.annotation.TypeAlias;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "Inventory")
public class Inventory {

	private String meshId;
	private String listPrice;

}
