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
@TypeAlias(value = "Application")
public class Application {
	
	private String contractId;
	private Long appIndex;
	private int storageModel;
	private int status;
	private TokenContract tokenContract;
	
	public static Application fromMap(long appIndex, Map<String, Object> registry) {
		
		Application a = new Application();
		a.setAppIndex(appIndex);
		ClarityType ct = (ClarityType) registry.get("status");
		a.setStatus(((BigInteger)ct.getValue()).intValue());
		
		ct = (ClarityType) registry.get("app-contract-id");
		a.setContractId((String)ct.getValue());
		
		return a;
	}
}
