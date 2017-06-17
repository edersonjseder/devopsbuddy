package com.devopsbuddy.utils;

import com.devopsbuddy.web.domain.frontend.ProAccountPayload;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 17/06/17.
 */
public class StripeUtils {

    public static final String STRIPE_CARD_NUMBER_KEY = "number";
    public static final String STRIPE_EXPIRY_MONTH_KEY = "exp_month";
    public static final String STRIPE_EXPIRY_YEAR_KEY = "exp_year";
    public static final String STRIPE_CVC_KEY = "cvc";
    public static final String STRIPE_CARD_KEY = "card";

    private StripeUtils(){
        throw new AssertionError("Non instantiable");
    }

    /**
     * Given the card info provided by the user in front-end, it returns a parameter map
     * to obtain a Stripe token.
     *
     * @param payload The info provided by user during registration
     * @return A parameter Map tha can be used to obtain a Stripe token
     */
    public static Map<String, Object> extractTokenParamsFromSignUpPayload(ProAccountPayload payload){
        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();

        cardParams.put(STRIPE_CARD_NUMBER_KEY, payload.getCardNumber());
        cardParams.put(STRIPE_EXPIRY_MONTH_KEY, Integer.valueOf(payload.getCardMonth()));
        cardParams.put(STRIPE_EXPIRY_YEAR_KEY, Integer.valueOf(payload.getCardYear()));
        cardParams.put(STRIPE_CVC_KEY, payload.getCardCode());
        tokenParams.put(STRIPE_CARD_KEY, cardParams);

        return tokenParams;
    }
}
