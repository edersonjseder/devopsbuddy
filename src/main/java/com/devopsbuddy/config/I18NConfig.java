package com.devopsbuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Created by root on 04/06/17.
 */
@Configuration
public class I18NConfig {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){

        ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();

        resourceBundleMessageSource.setBasename("classpath:i18n/messages");
        // Checks for new messages every 30 minutes
        resourceBundleMessageSource.setCacheSeconds(1800);

        return resourceBundleMessageSource;
    }
}
