package com.radicle.mesh.service.registration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	@Autowired private MongoTemplate mongoTemplate;
//	@Autowired private TransactionalEmailsApi apiInstance;
//    @Autowired private MandrillApi mandrillApi;
	@Value("${sendinblue.api.key}")
	String sendInBlueApiKey;


	@Override
	public String sendEmail(String to) {
		SendSmtpEmail sendSmtpEmail = getSmtpEmail(to);
    	return send(sendSmtpEmail);
	}

	public String sendEmail(SendSmtpEmail sendSmtpEmail) {
    	return send(sendSmtpEmail);
	}

    private String send(SendSmtpEmail sendSmtpEmail) {
    	
    	try {
        	ApiClient defaultClient = sendinblue.Configuration.getDefaultApiClient();
        	ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        	apiKey.setApiKey(sendInBlueApiKey);

        	TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
    	    CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
    	    System.out.println(result);
    	    return result.getMessageId();
    	} catch (ApiException e) {
    	    logger.error("SIB: " + e.getMessage());
    	    return null;
    	}
    }

    private SendSmtpEmail getSmtpEmail(String email) {
    	
    	SendSmtpEmail message = new SendSmtpEmail();
    	
    	message.setSubject("NFTs at #1");
    	message.setHtmlContent("Thanks for registering your interest.");
    	
    	List<SendSmtpEmailTo> to = new ArrayList<>();
    	SendSmtpEmailTo sset = new SendSmtpEmailTo();
    	sset.setEmail(email);
    	to.add(sset);
    	message.setTo(to);
    	
    	SendSmtpEmailSender sses = new SendSmtpEmailSender();
    	sses.setEmail("info@thisisnumberone.com");
    	sses.setName("The #1 Team");
    	message.setSender(sses);
    	
        return message;
    }

//    public void sendEmail(TransacEmail emailMessage) {
//
//        MandrillMessage message = new MandrillMessage();
//        message.setSubject(emailMessage.getSubject());
//        message.setText(emailMessage.getBody());
//        message.setAutoText(true);
//        message.setFromEmail(emailMessage.getFromEmail());
//        message.setFromName(emailMessage.getFromName());
//
//        ArrayList<MandrillMessage.Recipient> recipients = new ArrayList<>();
//        for (String email : emailMessage.getTo()) {
//            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
//            recipient.setEmail(email);
//            //recipient.setName("optional name");
//            recipient.setType(MandrillMessage.Recipient.Type.TO);
//            recipients.add(recipient);
//        }
//
//        for (String email : emailMessage.getCc()) {
//            MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
//            recipient.setEmail(email);
//            recipient.setType(MandrillMessage.Recipient.Type.CC);
//            recipients.add(recipient);
//        }
//        message.setTo(recipients);
//        message.setPreserveRecipients(true);
//        try {
//            logger.info("Sending email to - {} with subject {}", emailMessage.getTo(), emailMessage.getSubject());
//            MandrillMessageStatus[] messageStatusReports = mandrillApi.messages().send(message, false);
//            for (MandrillMessageStatus messageStatusReport : messageStatusReports) {
//                final String status = messageStatusReport.getStatus();
//                logger.info("MessageStatusReports = " + status);
//                if (status.equalsIgnoreCase("rejected") || status.equalsIgnoreCase("invalid")) {
//                    logger.error("Could not send email to {} status {}", emailMessage.getTo(), status);
//                }
//            }
//        } catch (MandrillApiError mandrillApiError) {
//            logger.error("MandrillApiError: " + mandrillApiError.getMandrillErrorAsJson());
//            logger.error("MandrillApiError sending email - " + emailMessage.getTo(), mandrillApiError);
//            throw new RuntimeException("MandrillApiError sending email - " + emailMessage.getTo(), mandrillApiError);
//        } catch (IOException e) {
//            logger.error("IOException sending email - " + emailMessage.getTo(), e);
//            throw new RuntimeException("IOException sending email - " + emailMessage.getTo(), e);
//        }
//    }
}