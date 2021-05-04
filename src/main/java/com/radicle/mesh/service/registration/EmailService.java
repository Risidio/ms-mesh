package com.radicle.mesh.service.registration;

import com.radicle.mesh.service.registration.domain.OffChainOffer;

public interface EmailService
{
    public String sendOfferRegisteredEmail(OffChainOffer offChainOffer);
    public String sendEmail(String to);
}
