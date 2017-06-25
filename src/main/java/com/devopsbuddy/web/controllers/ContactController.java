package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.enums.ContactEnum;
import com.devopsbuddy.web.domain.frontend.FeedBackPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by root on 04/06/17.
 */
@Controller
public class ContactController {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(ContactController.class);

    /** The key which identifies the feedback payload in the model. */
    public static final String FEEDBACK_MODEL_KEY = "feedback";

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contactGet(ModelMap model){

        FeedBackPojo feedBackPojo = new FeedBackPojo();
        model.addAttribute(ContactEnum.FEEDBACK_MODEL_KEY.getValue(), feedBackPojo);

        return ContactEnum.CONTACT_US_VIEW_NAME.getValue();
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contactPost(@ModelAttribute(FEEDBACK_MODEL_KEY) FeedBackPojo feedback){
        LOG.debug("Feedback POJO content: {}", feedback);
        emailService.sendFeedbackEmail(feedback);
        return ContactEnum.CONTACT_US_VIEW_NAME.getValue();
    }
}
