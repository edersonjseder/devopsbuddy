package com.devopsbuddy.backend.service;

import com.devopsbuddy.exceptions.StripeException;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by root on 16/06/17.
 */
@Service
public class StripeService {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(StripeService.class);

    @Autowired
    private String stripeKey;

    /**
     * Creates a Stripe customer and returns the Stripe customer id
     *
     * @param tokenParams The credit card details to obtain a token. These will never be stored in DB
     * @param customerParams The parameters which identifiers the customer
     * @return The Stripe customer id which can then be used to perform billing operations at a later stage
     * @throws com.devopsbuddy.exceptions.StripeException If na error occurred while interacting with Stripe
     */
    public String createCustomer(Map<String, Object> tokenParams, Map<String, Object> customerParams) {

        Stripe.apiKey = stripeKey;

        String stripeCustomerId = null;

        try {
            Token token = Token.create(tokenParams);
            customerParams.put("source", token.getId());
            Customer customer = Customer.create(customerParams);
            stripeCustomerId = customer.getId();

        } catch (AuthenticationException e){
            LOG.error("An authentication exception occurred while creating the Stripe customer", e);
            throw new StripeException(e);
        } catch (InvalidRequestException e){
            LOG.error("An invalid request exception occurred while creating the Stripe customer", e);
            throw new StripeException(e);
        } catch (APIConnectionException e){
            LOG.error("An API connection exception occurred while creating the Stripe customer", e);
            throw new StripeException(e);
        } catch (CardException e){
            LOG.error("An Credit card exception occurred while creating the Stripe customer", e);
        } catch (APIException e){
            LOG.error("An API exception occurred while creating the Stripe customer", e);
            throw new StripeException(e);
        }

        return stripeCustomerId;
    }
}
