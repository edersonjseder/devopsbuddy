package com.devopsbuddy.config;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.SmtpEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Configures this class to send message in a real email service.
 *
 * Created by root on 04/06/17.
 */
@Configuration
@Profile("prod")
@PropertySource("file:///${user.home}/.devopsbuddy/application-prod.properties")//gets the data info from the file in local directory
public class ProductionConfig {

    /**
     * Returns The Stmp Email Service instance
     * @return An instance of the Stmp Email Service class
     */
    @Bean
    public EmailService emailService() {
        return new SmtpEmailService();
    }
}
