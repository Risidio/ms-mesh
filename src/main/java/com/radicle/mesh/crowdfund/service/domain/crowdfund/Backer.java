package com.radicle.mesh.crowdfund.service.domain.crowdfund;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
@TypeAlias(value = "Backer")
@Document
public class Backer {

	@Id	private String id;
	private Integer status;
	private String stxAddress;
	private String username;
	private String email;
	private String profileUrl;
    @DBRef
	private List<Asset> assets;
    @DBRef
	private List<Perk> perks;

}
