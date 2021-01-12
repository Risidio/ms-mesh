package com.radicle.mesh.service.mining.domain;

import java.io.IOException;

import org.springframework.data.annotation.TypeAlias;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

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
@TypeAlias(value = "StacksBlockWinner")
public class StacksBlockWinner {

	private Long stacks_block_height;
	private String stx_address;
	private String btc_address;
	private Long burn_fee;
	
	public StacksBlockWinner() {
		super();
	}

	public static class Deserializer extends StdDeserializer<StacksBlockWinner> {

		public Deserializer() {
			this(null);
		}

		Deserializer(Class<?> vc) {
			super(vc);
		}

		@Override
		public StacksBlockWinner deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			JsonNode node = jp.getCodec().readTree(jp);
			StacksBlockWinner im = new StacksBlockWinner();
			im.setStacks_block_height(node.get("stacks_block_height").asLong());
			im.setStx_address(node.get("stx_address").asText());
			im.setBtc_address(node.get("btc_address").asText());
			im.setBurn_fee(node.get("burn_fee").asLong());
			return im;
		}
	}
}
