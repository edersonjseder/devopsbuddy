package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;
import com.devopsbuddy.web.domain.frontend.BasicAccountPayload;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by root on 10/06/17.
 */
public class UserUtils {

    /**
     * Non instantiable
     */
    private UserUtils(){
        throw new AssertionError("Non instantiable");
    }

    /**
     * Creates a user with basic attributes set.
     *
     * @param username The username parameter.
     * @param email The email parameter.
     * @return The User object.
     */
    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("secret");
        user.setEmail(email);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("123456789");
        user.setCountry("GB");
        user.setEnabled(true);
        user.setDescription("A basic user");
        user.setProfileImageUrl("https://blablabla.images.com/basicuser");
        return user;
    }

    /**
     * Builds and returns the URL to reset the user password.
     *
     * @param request The Http Servlet Request.
     * @param userId The user id.
     * @param token The token
     * @return the URL to reset the user password.
     */
    public static String createPasswordResetUrl(HttpServletRequest request, int userId, String token) {

        String passwordResetUrl =
                request.getScheme() +
                        "://" +
                        request.getServerName() +
                        ":" +
                        request.getServerPort() +
                        request.getContextPath() +
                        ForgotMyPasswordController.CHANGE_PASSWORD_PATH +
                        "?id=" +
                        userId +
                        "&token=" +
                        token;

        return passwordResetUrl;
    }

    /**
     * Method that accepts any object that extends the BasicAccountPayload class coming
     * from the web layer and returns a User entity object filled
     *
     * @param frontEndPayload the web layer class parameter got by the frontend
     * @param <T> the generic class type that extends from BasicAccountPayload
     * @return the user entity filled with data.
     */
    public static <T extends BasicAccountPayload> User fromWebUserToDomainUser(T frontEndPayload) {

        User user = new User();

        user.setUsername(frontEndPayload.getUsername());
        user.setPassword(frontEndPayload.getPassword());
        user.setFirstName(frontEndPayload.getFirstName());
        user.setLastName(frontEndPayload.getLastName());
        user.setEmail(frontEndPayload.getEmail());
        user.setPhoneNumber(frontEndPayload.getPhoneNumber());
        user.setCountry(frontEndPayload.getCountry());
        user.setEnabled(true);
        user.setDescription(frontEndPayload.getDescription());

        return user;
    }
}
