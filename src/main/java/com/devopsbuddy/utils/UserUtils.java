package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;

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
}
