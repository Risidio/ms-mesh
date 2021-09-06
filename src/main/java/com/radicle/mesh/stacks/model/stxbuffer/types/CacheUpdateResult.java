package com.radicle.mesh.stacks.model.stxbuffer.types;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import com.radicle.mesh.stacks.service.domain.Token;
import com.radicle.mesh.stacksactions.service.domain.StacksTransaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(value = "CacheUpdateResult")
public class CacheUpdateResult {

	private List<Token >tokens;
	private StacksTransaction stacksTransaction;

}
