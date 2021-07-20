package com.radicle.mesh.stacks.service.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.TypeAlias;

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
@TypeAlias(value = "TokenContract")
public class TokenContract {
	
	private String administrator;
	private String baseTokenUri;
	private String tokenName;
	private String tokenSymbol;
	private Long mintCounter;
	private Long mintPrice;
	private Long platformFee;
	private List<Token> tokens = new ArrayList();

	public static TokenContract fromMap(Map<String, Object> registry) {
		
		TokenContract tc = new TokenContract();
		
		Map<String, Object> contractData = (Map)registry.get("get-contract-data");
		
		ClarityType ct = (ClarityType) contractData.get("administrator");
		tc.setAdministrator((String)ct.getValueHex());
		
		ct = (ClarityType) contractData.get("baseTokenUri");
		tc.setBaseTokenUri((String)ct.getValue());
		
		ct = (ClarityType) contractData.get("tokenName");
		tc.setTokenName((String)ct.getValue());
		
		ct = (ClarityType) contractData.get("tokenSymbol");
		tc.setTokenSymbol((String)ct.getValue());
		
		ct = (ClarityType) contractData.get("mintCounter");
		tc.setMintCounter(((BigInteger)ct.getValue()).longValue());
		
		ct = (ClarityType) contractData.get("mintPrice");
		tc.setMintPrice(((BigInteger)ct.getValue()).longValue());
		
		ct = (ClarityType) contractData.get("platformFee");
		tc.setPlatformFee(((BigInteger)ct.getValue()).longValue());
		
		return tc;
	}
	
	public void addToken(Token token) {
		if (tokens == null) {
			tokens = new ArrayList();
		}
		if (token != null) tokens.add(token);
	}

}
