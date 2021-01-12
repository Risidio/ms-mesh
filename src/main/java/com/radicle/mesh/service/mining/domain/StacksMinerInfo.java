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
@TypeAlias(value = "StacksMinerInfo")
public class StacksMinerInfo {

	private String stx_address;
	private String btc_address;
	private Long actual_win;
	private Long total_win;
	private Long total_mined;
	private Long miner_burned;

	public StacksMinerInfo() {
		super();
	}

	public static class Deserializer extends StdDeserializer<StacksMinerInfo> {

		public Deserializer() {
			this(null);
		}

		Deserializer(Class<?> vc) {
			super(vc);
		}

		@Override
		public StacksMinerInfo deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			JsonNode node = jp.getCodec().readTree(jp);
			StacksMinerInfo im = new StacksMinerInfo();
			im.setStx_address(node.get("stx_address").asText());
			im.setBtc_address(node.get("btc_address").asText());
			im.setActual_win(node.get("actual_win").asLong());
			im.setTotal_win(node.get("total_win").asLong());
			im.setTotal_mined(node.get("total_mined").asLong());
			im.setMiner_burned(node.get("miner_burned").asLong());
			return im;
		}
	}
}
