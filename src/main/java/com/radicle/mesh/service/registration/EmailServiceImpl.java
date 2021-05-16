package com.radicle.mesh.service.registration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.radicle.mesh.service.registration.domain.OffChainOffer;

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

    @Value("${sendinblue.api.emailTemplate1}")
    private String emailTemplate1Name;
    private String template1;
    
    private static final String BODY_HTML = "</body></html>";
	private static final String HTML_HEAD_BODY = "<html><head></head><body style=\"background: #000; color: #fff;\">";
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	@Autowired private MongoTemplate mongoTemplate;
//	@Autowired private TransactionalEmailsApi apiInstance;
//    @Autowired private MandrillApi mandrillApi;
	@Value("${sendinblue.api.key}")
	String sendInBlueApiKey;


	@Override
	public String sendOfferRegisteredEmail(OffChainOffer offChainOffer) {
    	SendSmtpEmail message = new SendSmtpEmail();
    	message.setSubject("NFTs at #1");
    	String htmlContent = getTemplate(emailTemplate1Name);
    	htmlContent = htmlContent.replaceAll("CLIENT_TEXT1", "Offer Made");
    	htmlContent = htmlContent.replaceAll("CLIENT_TEXT2", "Your offer for " + offChainOffer.getAmount() + " STX has been registered.");
    	message.setHtmlContent(htmlContent);;
		SendSmtpEmail sendSmtpEmail = getSmtpEmail(message, offChainOffer.getEmail());
    	return send(sendSmtpEmail);
	}

	@Override
	public String sendRegisterInterestEmail(String to) {
    	SendSmtpEmail message = new SendSmtpEmail();
    	message.setSubject("NFT's at #1");
    	String htmlContent = getTemplate(emailTemplate1Name);
    	htmlContent = htmlContent.replaceAll("CLIENT_TEXT1", "");
    	htmlContent = htmlContent.replaceAll("CLIENT_TEXT2", "Thanks for registering your interest.");
    	message.setHtmlContent(htmlContent);;
    	// message.setHtmlContent("Thanks for registering your interest.");
		SendSmtpEmail sendSmtpEmail = getSmtpEmail(message, to);
    	return send(sendSmtpEmail);
	}

    private String getTemplate(String templateName) {
    	try {
    		String template = null;
        	if (templateName.equals(emailTemplate1Name)) {
        		if (template1 != null) {
        			template = template1;
        		} else {
        			template =loadTemplate(emailTemplate1Name);
        		}
        	} else {
        		throw new RuntimeException("Template not supported");
        	}
        	return template;
    	} catch (Exception e) {
    	    logger.error("SIB: " + e.getMessage());
    	    return "template not found";
    	}
    }

    private String loadTemplate(String templateName) {
        InputStream inputStream = EmailServiceImpl.class.getResourceAsStream("/static/" + templateName);
        template1 = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        return template1;
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

    private SendSmtpEmail getSmtpEmail(SendSmtpEmail message, String email) {
    	
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