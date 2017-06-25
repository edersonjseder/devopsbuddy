package com.devopsbuddy.enums;

/**
 * Created by root on 24/06/17.
 */
public enum SignUpEnum {

    DUPLICATED_USERNAME_KEY("duplicatedUsername"),
    DUPLICATED_EMAIL_KEY("duplicatedEmail"),
    SIGNED_UP_MESSAGE_KEY("signedUp"),
    ERROR_MESSAGE_KEY("message"),

    PAYLOAD_MODEL_KEY_NAME("payload"),
    SUBSCRIPTION_VIEW_NAME("registration/signup"),
    GENERIC_ERROR_VIEW_NAME("error/genericError");

    private String value;

    private SignUpEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
