package com.devopsbuddy.utils;

import com.devopsbuddy.backend.persistence.domain.backend.User;

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
}
