package com.devopsbuddy.exceptions;

import com.stripe.exception.APIException;

/**
 * Created by root on 16/06/17.
 */
public class StripeException extends RuntimeException {

    public StripeException(Throwable e) {
        super(e);
    }
}
