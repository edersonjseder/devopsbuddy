package com.devopsbuddy.web.controllers;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.enums.ForgotMyPasswordEnum;
import com.devopsbuddy.utils.UserUtils;
import com.devopsbuddy.backend.service.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by root on 11/06/17.
 */
@Controller
public class ForgotMyPasswordController {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";

    // Path for Spring framework to map via @RequestMapping to redirect to the change password
    // page in the link sent by email
    public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";


    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18NService;

    @Value("${webmaster.email}")
    private String webmasterEmail;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
    public String forgotPasswordGet() {
        return ForgotMyPasswordEnum.EMAIL_ADDRESS_VIEW_NAME.getPath();
    }

    /**
     * This method gets the user data related with the passwordResetToken object using the
     * 'email' parameter put on the email form by the user to make the proper link, so the
     * user can change his password
     *
     * @param request to be used to generate the forgot password link
     * @param email The user's email parameter
     * @param model The model that manipulates the data to be shown on the html page
     * @return the page with the message success or fail if no user is found
     */
    @RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
    public String forgotPasswordPost(HttpServletRequest request,
                                     @RequestParam("email") String email,
                                     ModelMap model) {

        // Get user by email through the relational password reset token object
        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);

        // Verify if exists the user in database with the email set by the user
        if (null == passwordResetToken) {
            LOG.warn("Couldn't find a password reset token for email {}", email);

        } else {

            // If not, it gets the user and it gets the token from the object.
            User user = passwordResetToken.getUser();
            String token = passwordResetToken.getToken();

            // Creates a URL password reset link to be sent by email
            String resetPasswordUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            LOG.debug("===>> Reset password URL {}", resetPasswordUrl);

            // This is the email text to be shown on user's email
            String emailText = i18NService.getMessage(ForgotMyPasswordEnum.EMAIL_MESSAGE_TEXT_PROPERTY_NAME.getPath(), request.getLocale());

            /**
             * Block that sets the mail info and send the link by email to the user
             * */
            SimpleMailMessage mailMessage = new SimpleMailMessage(); // Creates the object
            mailMessage.setTo(user.getEmail()); // The user email in which the message will e sent
            mailMessage.setSubject("[Devopsbuddy]: How to Reset Your Password"); // The email subject
            mailMessage.setText(emailText + "\r\n" + resetPasswordUrl); // The email text + link
            mailMessage.setFrom(webmasterEmail); // The email address from which the email is sent

            // This service that sends the email
            emailService.sendGenericEmailMessage(mailMessage);
            LOG.info("Email message {}", mailMessage.toString());
        }

        // The object to be shown on the page indicating that the email was sent successfully
        model.addAttribute(ForgotMyPasswordEnum.MAIL_SENT_KEY.getPath(), "true");

        return ForgotMyPasswordEnum.EMAIL_ADDRESS_VIEW_NAME.getPath();
    }

    /**
     * This method will get the user object using the parameters 'token' and 'user id' from
     * the link generated in the user's email.
     *
     * @param id The user id in the link
     * @param token The token in the passwordResetToken object that was on the link
     * @param locale The locale to be used to get the message.properties value
     * @param model The model that manipulates the data to be shown on the html page
     * @return The page in which the user will set his new password
     */
    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.GET)
    public String changeUserPasswordGet(@RequestParam("id") int id, @RequestParam("token") String token,
                                        Locale locale, ModelMap model) {

        /**
         * Uses the Spring framework class 'StringUtils' to verify if the given link
         * token value is empty through the method 'isEmpty' or the id value is zero
         * */
        if (StringUtils.isEmpty(token) || id == 0) {
            LOG.error("Invalid user id {} or token value {}", id, token);
            model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "false");
            model.addAttribute(ForgotMyPasswordEnum.MESSAGE_ATTRIBUTE_NAME.getPath(), "Invalid user id or token value");
            return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();
        }

        // If not then it will use the service to find the object PasswordResetToken by token
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

        /**
         * Verifies if the service didn't return null PasswordResetToken object
         * */
        if (null == passwordResetToken) {
            LOG.warn("A token couldn't be found with the value {}", token);
            model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "false");
            model.addAttribute(ForgotMyPasswordEnum.MESSAGE_ATTRIBUTE_NAME.getPath(), "Token not found.");
            return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();

        }

        // Otherwise we'll get the user associated with PasswordResetToken object
        User user = passwordResetToken.getUser();

        /**
         * Check if the searched user id is different than
         * the link corresponding id associated with token
         * */
        if (user.getId() != id) {
            LOG.error("The user id {} passed as parameter doesn't match the user id {} associated with the token {}", id, user.getId(), token);
            model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "false");
            model.addAttribute(ForgotMyPasswordEnum.MESSAGE_ATTRIBUTE_NAME.getPath(), i18NService.getMessage("resetPassword.token.invalid", locale));
            return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();

        }

        /**
         * Verifies if the token is not expired by using 'LocalDateTime.now()', the class
         * checks if the current date is after the expiry date in PasswordResetTime object.
         * */
        if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
            LOG.error("The token {} has expired.", token);
            model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "false");
            model.addAttribute(ForgotMyPasswordEnum.MESSAGE_ATTRIBUTE_NAME.getPath(), i18NService.getMessage("resetPassword.token.expired", locale));
            return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();

        }

        // Id set in the input hidden element to be used when user press the submit form button
        model.addAttribute("principalId", user.getId());

        /**
         * Ok to proceed. We auto-authenticate the user so that in the POST request we can check
         * if the user is authenticated.
         * */
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();
    }

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.POST)
    public String changeUserPasswordPost(@RequestParam("principal_id") int userId,
                                         @RequestParam("password") String password,
                                         ModelMap model) {

        // Getting an authentication object from Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            LOG.error("An unauthenticated user tried to invoke the reset password POST method.");
            model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "false");
            model.addAttribute(ForgotMyPasswordEnum.MESSAGE_ATTRIBUTE_NAME.getPath(), "You are not authorized to perform this request.");

            return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();

        }

        User user = (User) authentication.getPrincipal();

        if (user.getId() != userId){
            LOG.error("Security breach! User {} is trying to make a password reset request on behalf of {}", user.getId(), userId);
            model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "false");
            model.addAttribute(ForgotMyPasswordEnum.MESSAGE_ATTRIBUTE_NAME.getPath(), "You are not authorized to perform this request.");

            return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();
        }

        userService.updateUserPassword(userId, password);
        LOG.info("Password successfully updated for user {}", user.getUsername());

        model.addAttribute(ForgotMyPasswordEnum.PASSWORD_RESET_ATTRIBUTE_NAME.getPath(), "true");

        return ForgotMyPasswordEnum.CHANGE_PASSWORD_VIEW_NAME.getPath();
    }
}
