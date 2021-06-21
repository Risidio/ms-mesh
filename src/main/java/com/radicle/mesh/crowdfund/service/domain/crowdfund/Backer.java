package com.radicle.mesh.crowdfund.service.domain.crowdfund;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.radicle.mesh.payments.service.domain.Payment;

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
	private String email;
	private String profileUrl;
    @DBRef
	private List<Payment> payments;
    @DBRef
	private List<Asset> assets;
    @DBRef
	private List<Perk> perks;

    public boolean addPayment(Payment payment) {
    	if (payments == null) {
    		payments = new ArrayList<Payment>();
    	}
    	return payments.add(payment);
    }
    
    public boolean addPayment(Asset asset) {
    	if (assets == null) {
    		assets = new ArrayList<Asset>();
    	}
    	return assets.add(asset);
    }
    
    public boolean addPayment(Perk perk) {
    	if (perks == null) {
    		perks = new ArrayList<Perk>();
    	}
    	return perks.add(perk);
    }
}
