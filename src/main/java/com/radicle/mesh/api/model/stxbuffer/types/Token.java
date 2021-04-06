package com.radicle.mesh.api.model.stxbuffer.types;

import java.math.BigInteger;
import java.util.List;
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
	
	private Long nftIndex;
	private Long offerCounter;
	private Long bidCounter;
	private Long editionCounter;
	private Long transferCounter;
	private SaleData saleData;
	private TokenInfo tokenInfo;
	private List<Offer> offerHistory;
	private List<Bid> bidHistory;
	private String owner;
	private Map<String, Object> transferMap;
	private Map<String, Object> transferHistoryMap;
	
	public static Token fromMap(long tokenIndex, Map<String, Object> map) {
		
		Token t = new Token();
		t.nftIndex = tokenIndex;
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

		Map<String, Object> info = null;

		try {
			info = (Map)map.get("tokenInfo");
			TokenInfo sd = TokenInfo.fromMap(info);
			t.tokenInfo = sd;
		} catch (Exception e1) {
			// empty map
		}

		try {
			info = (Map)map.get("saleData");
			SaleData sd = SaleData.fromMap(info);
			t.saleData = sd;
		} catch (Exception e) {
			// empty map
		}

		try {
			info = (Map)map.get("transferMap");
			t.transferMap = info;
		} catch (Exception e) {
			// empty map
		}

		try {
			info = (Map)map.get("transferHistoryMap");
			t.transferHistoryMap = info;
		} catch (Exception e) {
			// empty map
		}

		return t;
	}
}
