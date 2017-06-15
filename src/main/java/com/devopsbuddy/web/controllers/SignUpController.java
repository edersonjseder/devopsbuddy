package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.service.PlanService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 15/06/17.
 */
@Controller
public class SignUpController {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(SignUpController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PlanService planService;

    public static final String DUPLICATED_USERNAME_KEY = "duplicatedUsername";
    public static final String DUPLICATED_EMAIL_KEY = "duplicatedEmail";
    public static final String SIGNED_UP_MESSAGE_KEY = "signedUp";
    public static final String ERROR_MESSAGE_KEY = "message";

    public static final String SIGNUP_URL_MAPPING = "/signup";
    public static final String PAYLOAD_MODEL_KEY_NAME = "payload";
    public static final String SUBSCRIPTION_VIEW_NAME = "registration/signup";

    @RequestMapping(value = SIGNUP_URL_MAPPING, method = RequestMethod.GET)
    public String signUpGet(@RequestParam("planId") int planId, ModelMap model) {

        if((planId != PlansEnum.BASIC.getId()) && (planId != PlansEnum.PRO.getId())){
            throw new IllegalArgumentException("Plan is not valid");
        }

        model.addAttribute(PAYLOAD_MODEL_KEY_NAME, new ProAccountPayload());

        return SUBSCRIPTION_VIEW_NAME;
    }

    @RequestMapping(value = SIGNUP_URL_MAPPING, method = RequestMethod.POST)
    public String signUpPost(@RequestParam(name = "planId", required = true) int planId,
                             @RequestParam(name = "file", required = false) MultipartFile file,
                             @ModelAttribute(PAYLOAD_MODEL_KEY_NAME) @Valid ProAccountPayload payload,
                             ModelMap model) throws IOException {

        if ((planId != PlansEnum.BASIC.getId()) && (planId != PlansEnum.PRO.getId())){
            model.addAttribute(SIGNED_UP_MESSAGE_KEY, "false");
            model.addAttribute(ERROR_MESSAGE_KEY, "Plan doesn't exist");

            return SUBSCRIPTION_VIEW_NAME;
        }

        this.checkForDuplicates(payload, model);

        boolean duplicates = false;

        List<String> errorMessages = new ArrayList<>();

        if (model.containsKey(DUPLICATED_USERNAME_KEY)){
            LOG.warn("The username already exists. Displaying error to the user");
            model.addAttribute(SIGNED_UP_MESSAGE_KEY, "false");
            errorMessages.add("Username already exists");
            duplicates = true;
        }

        if (model.containsKey(DUPLICATED_EMAIL_KEY)){
            LOG.warn("The email already exists. Displaying error to the user");
            model.addAttribute(SIGNED_UP_MESSAGE_KEY, "false");
            errorMessages.add("Email already exists");
            duplicates = true;
        }

        if (duplicates){

            model.addAttribute(ERROR_MESSAGE_KEY, errorMessages);

            return SUBSCRIPTION_VIEW_NAME;
        }

        // There are certain info that the user doesn't set, such as profile image URL, Stripe
        // customer id, plans and roles
        LOG.debug("Transforming user payload into user domain object");
        User user = UserUtils.fromWebUserToDomainUser(payload);

        // Stores the profile image on Amazon S3 and stores the URL in the user's record
        if ((file != null) && (!file.isEmpty())){
            String profileImageUrl = null;

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
            model.addAttribute(SIGNED_UP_MESSAGE_KEY, "false");
            model.addAttribute(ERROR_MESSAGE_KEY, "Plan id not found");

            return SUBSCRIPTION_VIEW_NAME;
        }
        user.setPlan(selectedPlan);

        User registeredUser = null;

        // By default users get BASIC ROLE
        Set<UserRole> userRoles = new HashSet<>();
        if (planId == PlansEnum.BASIC.getId()){
            userRoles.add(new UserRole(user, new Role(RolesEnum.BASIC)));
            registeredUser = userService.createUser(user, PlansEnum.BASIC, userRoles);

        } else {
            userRoles.add(new UserRole(user, new Role(RolesEnum.PRO)));
            registeredUser = userService.createUser(user, PlansEnum.PRO, userRoles);
            LOG.debug(payload.toString());
        }

        // Auto logins the registered user
        Authentication auth = new UsernamePasswordAuthenticationToken(
                registeredUser, null, registeredUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        LOG.info("User created successfully");

        model.addAttribute(SIGNED_UP_MESSAGE_KEY, "true");

        return SUBSCRIPTION_VIEW_NAME;

    }

    /**
     * Checks if the username/email are duplicates and sets error flags in the model.
     * Side effect: the method might set attributes on model
     * @param payload
     * @param model
     */
    private void checkForDuplicates(BasicAccountPayload payload, ModelMap model) {
        // Username
        if (userService.findByUserName(payload.getUsername()) != null){
            model.addAttribute(DUPLICATED_USERNAME_KEY, "true");
        }

        if (userService.findUserByEmail(payload.getEmail()) != null){
            model.addAttribute(DUPLICATED_EMAIL_KEY, "true");
        }
    }
}
