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
@TypeAlias(value = "Offer")
public class Offer {

	private Integer accepted;
	private String offerer;
	private Integer saleCycle;
	private Long madeDate;
	private Long amount;

	public static Offer fromMap(Map<String, Object> map) {
		
		Offer sd = new Offer();
		try {
			ClarityType ct = (ClarityType)map.get("offerer");
			sd.setOfferer((String) ct.getValueHex());

			ct = (ClarityType)map.get("amount");
			sd.setAmount(((BigInteger)ct.getValue()).longValue());
			
			ct = (ClarityType)map.get("made-date");
			sd.setMadeDate(((BigInteger)ct.getValue()).longValue());

			ct = (ClarityType)map.get("sale-cycle");
			sd.setSaleCycle(((BigInteger)ct.getValue()).intValue());

			ct = (ClarityType)map.get("accepted");
			sd.setAccepted(((BigInteger)ct.getValue()).intValue());

		} catch (Exception e) {
			return null;
		}
		return sd;
	}

}
