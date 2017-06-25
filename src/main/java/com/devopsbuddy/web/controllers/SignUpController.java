package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.PlanService;
import com.devopsbuddy.backend.service.S3Service;
import com.devopsbuddy.backend.service.StripeService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.enums.SignUpEnum;
import com.devopsbuddy.exceptions.S3Exception;
import com.devopsbuddy.exceptions.StripeException;
import com.devopsbuddy.utils.StripeUtils;
import com.devopsbuddy.utils.UserUtils;
import com.devopsbuddy.web.domain.frontend.BasicAccountPayload;
import com.devopsbuddy.web.domain.frontend.ProAccountPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

/**
 * Sign Up Controller to control the creation of user information and credentials
 *
 * Created by root on 15/06/17.
 */
@Controller
public class SignUpController {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(SignUpController.class);
    private static final String GENERIC_ERROR_VIEW_NAME = "error/genericError";

    // Spring instantiates the object through DI
    @Autowired
    private UserService userService;

    // Spring instantiates the object through DI
    @Autowired
    private PlanService planService;

    // Spring instantiates the object through DI
    @Autowired
    private S3Service s3Service;

    // Spring instantiates the object through DI
    @Autowired
    private StripeService stripeService;

    public static final String SIGNUP_URL_MAPPING = "/signup";
    public static final String PAYLOAD_MODEL_KEY_NAME = "payload";

    @RequestMapping(value = SIGNUP_URL_MAPPING, method = RequestMethod.GET)
    public String signUpGet(@RequestParam("planId") int planId, ModelMap model) {

        if((planId != PlansEnum.BASIC.getId()) && (planId != PlansEnum.PRO.getId())){
            throw new IllegalArgumentException("Plan is not valid");
        }

        model.addAttribute(SignUpEnum.PAYLOAD_MODEL_KEY_NAME.getValue(), new ProAccountPayload());

        return SignUpEnum.SUBSCRIPTION_VIEW_NAME.getValue();
    }

    /**
     * - Method invoked by Sign Up button on the form HTML page -
     *
     * It creates an user based on the information inserted by the use on the HTML page
     * and saves it in the database
     *
     * @param planId The plan id chosen by the user, if it's Basic or Pro
     * @param file The profile image file uploaded by the user to Amazon S3 Cloud bucket
     * @param payload The front end pojo in which the user inserted his data
     * @param model The Spring Model that manipulates data objects in the screen
     * @return The Subscription view name with the message to the user if was successful or not
     * @throws IOException The exception if an error occurred while saving the image file on Amazon S3
     */
    @RequestMapping(value = SIGNUP_URL_MAPPING, method = RequestMethod.POST)
    public String signUpPost(@RequestParam(name = "planId", required = true) int planId,
                             @RequestParam(name = "file", required = false) MultipartFile file,
                             @ModelAttribute(PAYLOAD_MODEL_KEY_NAME) @Valid ProAccountPayload payload,
                             ModelMap model) throws IOException {

        // Verifies if the plan exists according to the plan id passed as parameter
        if ((planId != PlansEnum.BASIC.getId()) && (planId != PlansEnum.PRO.getId())){
            model.addAttribute(SignUpEnum.SIGNED_UP_MESSAGE_KEY.getValue(), "false");
            model.addAttribute(SignUpEnum.ERROR_MESSAGE_KEY.getValue(), "Plan doesn't exist");

            return SignUpEnum.SUBSCRIPTION_VIEW_NAME.getValue();
        }

        // Checks if there is already an user object by email or username conditions
        this.checkForDuplicates(payload, model);

        // Variable to set true or false if there is a duplicated username or email
        boolean duplicates = false;

        // List of error messages to be shown to the user on the bootstrap alert on HTML page0
        // A list is necessary because we check if the username and the email are duplicated
        List<String> errorMessages = new ArrayList<>();

        if (model.containsKey(SignUpEnum.DUPLICATED_USERNAME_KEY.getValue())){
            LOG.warn("The username already exists. Displaying error to the user");
            model.addAttribute(SignUpEnum.SIGNED_UP_MESSAGE_KEY.getValue(), "false");
            errorMessages.add("Username already exists");
            duplicates = true;
        }

        if (model.containsKey(SignUpEnum.DUPLICATED_EMAIL_KEY.getValue())){
            LOG.warn("The email already exists. Displaying error to the user");
            model.addAttribute(SignUpEnum.SIGNED_UP_MESSAGE_KEY.getValue(), "false");
            errorMessages.add("Email already exists");
            duplicates = true;
        }

        // Check if the duplicated flag was set to true or is kept in false
        if (duplicates){

            model.addAttribute(SignUpEnum.ERROR_MESSAGE_KEY.getValue(), errorMessages);

            return SignUpEnum.SUBSCRIPTION_VIEW_NAME.getValue();
        }

        // There are certain info that the user doesn't set, such as profile image URL, Stripe
        // customer id, plans and roles
        LOG.debug("Transforming user payload into user domain object");
        User user = UserUtils.fromWebUserToDomainUser(payload);

        // Stores the profile image on Amazon S3 and stores the URL in the user's record
        if ((file != null) && (!file.isEmpty())){

            String profileImageUrl = s3Service.storeProfileImage(file, payload.getUsername());

            if (profileImageUrl != null){
                user.setProfileImageUrl(profileImageUrl);

            } else {
                LOG.warn("There was a problem uploading the profile image to S3. The user's profile will be created without the image.");
            }
        }

        // Sets the plan and the roles (depending on the chosen plan)
        LOG.debug("Retrieving plan from the database");
        Plan selectedPlan = planService.findPlanById(planId);
        if (selectedPlan == null){
            LOG.error("The plan id {} could not be found. Throwing exception.", planId);
            model.addAttribute(SignUpEnum.SIGNED_UP_MESSAGE_KEY.getValue(), "false");
            model.addAttribute(SignUpEnum.ERROR_MESSAGE_KEY.getValue(), "Plan id not found");

            return SignUpEnum.SUBSCRIPTION_VIEW_NAME.getValue();
        }
        user.setPlan(selectedPlan);

        User registeredUser = null;

        // By default users get BASIC ROLE
        Set<UserRole> userRoles = new HashSet<>();
        if (planId == PlansEnum.BASIC.getId()){
            userRoles.add(new UserRole(user, new Role(RolesEnum.BASIC)));
            registeredUser = userService.createUser(user, PlansEnum.BASIC, userRoles);

        } else {
            // If the user choose Pro plan, it gets the credit card info to process the purchase
            userRoles.add(new UserRole(user, new Role(RolesEnum.PRO)));

            // Extra precaution in case the POST method is invoked programmatically
            if (StringUtils.isEmpty(payload.getCardCode()) ||
                    StringUtils.isEmpty(payload.getCardNumber()) ||
                    StringUtils.isEmpty(payload.getCardMonth()) ||
                    StringUtils.isEmpty(payload.getCardYear())) {

                LOG.error("One or more credit card fields is null or empty. Returning error to the user.");
                model.addAttribute(SignUpEnum.SIGNED_UP_MESSAGE_KEY.getValue(), "false");
                model.addAttribute(SignUpEnum.ERROR_MESSAGE_KEY.getValue(), "One or more credit card fields is null or empty");

                return SignUpEnum.SUBSCRIPTION_VIEW_NAME.getValue();
            }

            // If the user has selected Pro Account, creates the Stripe customer to store
            // the Stripe customer id in the database
            Map<String, Object> stripeTokenParms = StripeUtils.extractTokenParamsFromSignUpPayload(payload);
            Map<String, Object> customerParams = new HashMap<>();

            customerParams.put("description", "DevOps Buddy Customer. Username: " + payload.getUsername());
            customerParams.put("email", payload.getEmail());
//            customerParams.put("plan", selectedPlan.getId());

            LOG.info("Subscribing the customer to plan {}", selectedPlan.getName());
            String  stripeCustomerId = stripeService.createCustomer(stripeTokenParms, customerParams);
            LOG.info("Username: {} has been subscribed to Stripe", payload.getUsername());

            user.setStripeCustomerId(stripeCustomerId);

            registeredUser = userService.createUser(user, PlansEnum.PRO, userRoles);
            LOG.debug(payload.toString());
        }

        // Auto logins the registered user after subscribe him
        Authentication auth = new UsernamePasswordAuthenticationToken(
                registeredUser, null, registeredUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        LOG.info("User created successfully");

        // Set the message key to show the correct bootstrap alert message on the screen
        model.addAttribute(SignUpEnum.SIGNED_UP_MESSAGE_KEY.getValue(), "true");

        return SignUpEnum.SUBSCRIPTION_VIEW_NAME.getValue();

    }

    /**
     * - Invoked by signUpPost method -
     *
     * Checks if the username/email are duplicates and sets error flags in the model.
     * Side effect: the method might set attributes on model
     *
     * @param payload
     * @param model
     */
    private void checkForDuplicates(BasicAccountPayload payload, ModelMap model) {
        // Username
        if (userService.findByUserName(payload.getUsername()) != null){
            // Add the 'true' value to the attribute of the model
            model.addAttribute(SignUpEnum.DUPLICATED_USERNAME_KEY.getValue(), "true");
        }

        if (userService.findUserByEmail(payload.getEmail()) != null){
            // Add the 'true' value to the attribute of the model
            model.addAttribute(SignUpEnum.DUPLICATED_EMAIL_KEY.getValue(), "true");
        }
    }

    /**
     * Using the Spring MVC Exception Handling to handle the StripeException and S3Exception classes
     * because both exceptions are only on the journey of sign up users to the database via the
     * Spring Controller.
     * It handles the exceptions created to the Stripe service and the Amazon S3 Cloud service
     *
     * @param request The HttpServletRequest object for manipulate the view
     * @param exception The exception that Spring will set automatically if it occurs
     * @return the ModelAndView object(combination of a ModelMap to a View name) with the data
     * filled to be shown on generalError HTML page
     */
    @ExceptionHandler({StripeException.class, S3Exception.class})
    public ModelAndView signUpException(HttpServletRequest request, Exception exception){
        LOG.error("Request {} raised exception {}", request.getRequestURL(), exception);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("timestamp", LocalDate.now(Clock.systemUTC()));
        mav.setViewName(SignUpEnum.GENERIC_ERROR_VIEW_NAME.getValue());

        return mav;
    }
}
