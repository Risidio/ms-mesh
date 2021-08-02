package com.radicle.mesh.email.service;

import com.radicle.mesh.numberone.domain.ExhibitRequest;
import com.radicle.mesh.numberone.domain.OffChainOffer;

public interface EmailService
{
    public String sendExhibitRequest(ExhibitRequest exhibitRequest);
    public String sendOfferRegisteredEmail(OffChainOffer offChainOffer, String content);
    public String sendRegisterInterestEmail(String to, String content);
	public String loadEmailTemplates();
}
