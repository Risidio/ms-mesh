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
public class Bid {

	private String bidder;
	private Integer saleCycle;
	private Long whenBid;
	private Long amount;

	public static Bid fromMap(Map<String, Object> map) {
		
		Bid sd = new Bid();

		try {

			ClarityType ct = (ClarityType)map.get("bidder");
			sd.setBidder((String) ((ClarityType)map.get("bidder")).getValueHex());

			ct = (ClarityType)map.get("amount");
			sd.setAmount(((BigInteger)ct.getValue()).longValue());
			
			ct = (ClarityType)map.get("when-bid");
			sd.setWhenBid(((BigInteger)ct.getValue()).longValue());

			ct = (ClarityType)map.get("sale-cycle");
			sd.setSaleCycle(((BigInteger)ct.getValue()).intValue());

		} catch (Exception e) {
			// empty map
		}

		return sd;
	}

}
