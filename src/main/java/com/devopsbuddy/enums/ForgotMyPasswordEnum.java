package com.devopsbuddy.enums;

/**
 * Created by root on 15/06/17.
 */
public enum ForgotMyPasswordEnum {

    EMAIL_ADDRESS_VIEW_NAME("forgotmypassword/emailForm"),

    MAIL_SENT_KEY("mailSent"),

    EMAIL_MESSAGE_TEXT_PROPERTY_NAME("forgotmypassword.email.text"),

    // Constant representing the url to be redirected to the specified page
    CHANGE_PASSWORD_VIEW_NAME("forgotmypassword/changePassword"),

    // Constant representing the passwordReset object as a condition verification to the th:if value
    // on the changePassword.html page
    PASSWORD_RESET_ATTRIBUTE_NAME("passwordReset"),

    // Message to be shown on changePassword.html page
   MESSAGE_ATTRIBUTE_NAME("message");

    private String path;

    private ForgotMyPasswordEnum(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
