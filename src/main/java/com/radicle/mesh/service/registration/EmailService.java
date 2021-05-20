package com.radicle.mesh.service.registration;

import com.radicle.mesh.service.registration.domain.OffChainOffer;

public interface EmailService
{
    public String sendOfferRegisteredEmail(OffChainOffer offChainOffer, String content);
    public String sendRegisterInterestEmail(String to, String content);
	public String loadEmailTemplates();
}
