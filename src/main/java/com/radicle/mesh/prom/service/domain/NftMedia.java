package com.radicle.mesh.prom.service.domain;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@TypeAlias(value = "NftMedia")
public class NftMedia {

	private String coverArtist;
	private MediaObject artworkFile;
	private MediaObject artworkClip;
	private MediaObject coverImage;
	private List<MediaObject> mainImages;
	private List<MediaObject> thumbnails;
}
