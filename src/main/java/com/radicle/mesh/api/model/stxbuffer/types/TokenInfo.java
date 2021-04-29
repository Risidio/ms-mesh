package com.radicle.mesh.api.model.stxbuffer.types;

import java.math.BigInteger;
import java.util.Map;

import org.springframework.data.annotation.TypeAlias;

import com.radicle.mesh.api.model.stxbuffer.ClarityType;

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
@TypeAlias(value = "TokenInfo")
public class TokenInfo {
	
	private Long seriesOriginal;
	private Long appTimestamp;
	private Long edition;
	private Long mintBlockHeight;
	private Long maxEditions;
	private Long editionCost;
	private String assetHash;
	private String gaiaUsername;
	
	public static TokenInfo fromMap(Map<String, Object> map) {
		
		TokenInfo t = new TokenInfo();

		t.assetHash = (String) ((ClarityType)map.get("asset-hash")).getValueHex();

		t.gaiaUsername = (String) ((ClarityType)map.get("gaia-username")).getValue();

		ClarityType ct = (ClarityType)map.get("max-editions");
		t.maxEditions = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("edition-cost");
		t.editionCost = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("edition");
		t.edition = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("mint-block-height");
		t.mintBlockHeight = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("series-original");
		t.seriesOriginal = ((BigInteger)ct.getValue()).longValue();

		return t;
	}
}
