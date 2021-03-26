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
@TypeAlias(value = "Token")
public class Token {
	
	private Long tokenIndex;
	private Long nftIndex;
	private Long offerCounter;
	private Long bidCounter;
	private Long editionCounter;
	private Long transferCounter;
	private Map<String, Object> saleData;
	private Map<String, Object> bidHistory;
	private Map<String, Object> transferMap;
	private Map<String, Object> transferHistoryMap;
	private Map<String, Object> tokenInfo;
	private Map<String, Object> offers;
	private String owner;
	
	public static Token fromMap(long tokenIndex, Map<String, Object> map) {
		
		Token t = new Token();
		t.tokenIndex = tokenIndex;
		t.owner = (String) ((ClarityType)map.get("owner")).getValueHex();

		ClarityType ct = (ClarityType)map.get("nftIndex");
		t.nftIndex = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("offerCounter");
		t.offerCounter = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("bidCounter");
		t.bidCounter = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("editionCounter");
		t.editionCounter = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("transferCounter");
		t.transferCounter = ((BigInteger)ct.getValue()).longValue();

		Map<String, Object> info = (Map)map.get("tokenInfo");
		t.tokenInfo = info;

		info = (Map)map.get("offers");
		t.offers = info;

		info = (Map)map.get("saleData");
		t.saleData = info;

		info = (Map)map.get("bidHistory");
		t.bidHistory = info;

		info = (Map)map.get("transferMap");
		t.transferMap = info;

		info = (Map)map.get("transferHistoryMap");
		t.transferHistoryMap = info;

		return t;
	}
}
