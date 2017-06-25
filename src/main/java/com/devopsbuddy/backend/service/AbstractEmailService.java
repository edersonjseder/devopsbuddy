package com.devopsbuddy.backend.service;

import com.devopsbuddy.web.domain.frontend.FeedBackPojo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

/**
 * This class is the implementation of the email sending
 * it gets the user information from the front-end form
 * and stores in the Feedback object to be sent by e-mail.
 * Created by root on 04/06/17.
 */
public abstract class AbstractEmailService implements EmailService {

    @Value("${default.to.address}")//spring sets the value from the application.properties value to the string variable
    private String defaultToAddress;
    /**
     * Creates a Simple Mail Message from feedback pojo
     * @param feedBackPojo The feedback pojo
     * @return
     */
    protected SimpleMailMessage prepareSimpleMailMessageFromFeedBackPojo(FeedBackPojo feedBackPojo) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(defaultToAddress);
        message.setFrom(feedBackPojo.getEmail());
        message.setSubject("[DevOps Buddy]: Feedback received from " + feedBackPojo.getFirstName() + " " + feedBackPojo.getLastName() + "!");
        message.setText(feedBackPojo.getFeedback());

        return message;
    }

    @Override
    public void sendFeedbackEmail(FeedBackPojo feedBackPojo) {
        sendGenericEmailMessage(prepareSimpleMailMessageFromFeedBackPojo(feedBackPojo));
    }
}
