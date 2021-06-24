package com.radicle.mesh.crowdfund.service.domain.crowdfund;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

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
@TypeAlias(value = "Backer")
@Document
public class Backer {

	@Id	private String id;
	private Integer status;
	private String stxAddress;
	private String username;
	private String nickname;
	private String profileUrl;
	private List<String> paymentIds;
	private List<String> assetIds;
	private List<String> perkIds;

    public boolean addPayment(String paymentId) {
    	if (paymentIds == null) {
    		paymentIds = new ArrayList<String>();
    	}
    	return paymentIds.add(paymentId);
    }
    
    public boolean addAsset(String assetId) {
    	if (assetIds == null) {
    		assetIds = new ArrayList<String>();
    	}
    	return assetIds.add(assetId);
    }
    
    public boolean addPerk(String perkId) {
    	if (perkIds == null) {
    		perkIds = new ArrayList<String>();
    	}
    	return perkIds.add(perkId);
    }
}
