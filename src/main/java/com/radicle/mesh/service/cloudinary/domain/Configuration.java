package com.radicle.mesh.service.cloudinary.domain;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;

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
@TypeAlias(value = "Configuration")
public class Configuration implements Serializable {

	private static final long serialVersionUID = 4828120761532114347L;
	@Id private String id;
	private String uuid;
	private Long updated;
	private CloudinaryConfig cloudinaryConfig;

	public Configuration() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}
} 
