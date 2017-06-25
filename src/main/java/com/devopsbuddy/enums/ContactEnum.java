package com.devopsbuddy.enums;

/**
 * Created by root on 24/06/17.
 */
public enum ContactEnum {

    /** The key which identifies the feedback payload in the model. */
    FEEDBACK_MODEL_KEY("feedback"),

    /** The Contact Us view name */
    CONTACT_US_VIEW_NAME("contact/contact");

    private String path;

    private ContactEnum(String path){
        this.path = path;
    }

    public String getValue() {
        return path;
    }
}
