package com.radicle.mesh.service.registration;

import sibModel.SendSmtpEmail;

public interface EmailService
{
    public String sendEmail(String to);
    public String sendEmail(SendSmtpEmail emailMessage);
}
