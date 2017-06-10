package com.devopsbuddy.backend.service;

import com.devopsbuddy.web.domain.frontend.FeedBackPojo;
import org.springframework.mail.SimpleMailMessage;

/**
 * Contract for email service
 * Created by root on 04/06/17.
 */
public interface EmailService {

    /**
     * Sends an email with the content in the Feedback pojo
     * @param feedBackPojo The feedback pojo
     */
    public void sendFeedbackEmail(FeedBackPojo feedBackPojo);

    /**
     * Sends an email with the content of the Simple Mail Message object
     * @param message The object containing the email content
     */
    public void sendGenericEmailMessage(SimpleMailMessage message);
}
