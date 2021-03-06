package com.radicle.mesh.stacks.service.domain;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.radicle.mesh.stacks.service.ClarityType;

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
@Document
public class Token {
	
	@Id private String id;
	private Long nftIndex;
	private Long offerCounter;
	private Long bidCounter;
	private Long editionCounter;
	private SaleData saleData;
	private TokenInfo tokenInfo;
	Map<String, List<ClarityType>> beneficiaries;
	private List<Offer> offerHistory;
	private List<Bid> bidHistory;
	private String owner;
	private String contractId;
	
	public static Token fromMap(long tokenIndex, Map<String, Object> map, String contractId) {
		
		Token t = new Token();
		t.nftIndex = tokenIndex;
		t.contractId = contractId;
		t.owner = (String) ((ClarityType)map.get("owner")).getValueHex();

		if (map.containsKey("beneficiaryData")) {
			try {
				Map<String, List<ClarityType>> bene = (Map<String, List<ClarityType>>)map.get("beneficiaryData");
				t.beneficiaries = bene;
			} catch (Exception e) {
				// if no benneficiaries returns ClarityType(type=9) OptionalNone.
				// this happens when this token is edition 2 or greater.
			}
		}

		ClarityType ct = (ClarityType)map.get("nftIndex");
		t.nftIndex = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("offerCounter");
		t.offerCounter = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("bidCounter");
		t.bidCounter = ((BigInteger)ct.getValue()).longValue();

		ct = (ClarityType)map.get("editionCounter");
		t.editionCounter = ((BigInteger)ct.getValue()).longValue();

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

		return t;
	}
}
