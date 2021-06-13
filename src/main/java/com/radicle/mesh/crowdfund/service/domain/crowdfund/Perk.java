package com.radicle.mesh.crowdfund.service.domain.crowdfund;

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
@TypeAlias(value = "Perk")
@Document
public class Perk {

	@Id	private String id;
	private Integer status;
	private String name;
	private String description;
	private String imageUrl;
	private String type;

}
