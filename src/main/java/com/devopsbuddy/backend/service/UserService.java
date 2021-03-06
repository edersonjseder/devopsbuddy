package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by root on 10/06/17.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

        User localUser = userRepository.findByEmail(user.getEmail());

        if (localUser != null){
            LOG.info("User with username {} and email {} already exist. Nothing will be done. ", user.getUsername(), user.getEmail());
        } else {

            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            Plan plan = new Plan(plansEnum);

            // It makes sure plans exist in the database.
            if (!planRepository.exists(plansEnum.getId())){
                plan = planRepository.save(plan);
            }

            user.setPlan(plan);

            /** Saves the other side of the user to roles
             * relationship by persisting all roles in
             * the UserRoles collection */
            for (UserRole ur : userRoles) {

                roleRepository.save(ur.getRole());
            }

            /** Adding the object collection of user roles to user entity
             * (Always call the get method of Set collection to add objects in JPA)*/
            user.getUserRoles().addAll(userRoles);

            localUser = userRepository.save(user);

        }

        return localUser;

    }

    /**
     * Retrieves the user for the given username
     *
     * @param username the username parameter to look for the user object
     * @return a User given the username passed or null if none is found
     */
    public User findByUserName(String username){
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a user for the given email.
     * @param email The email to look for the user object
     * @return a User given the email passed or null if none is found.
     */
    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves a user for the given id.
     * @param userId The id to look for the user object
     * @return a User given the id passed or null if none is found.
     */
    public User findUserById(int userId){
        return userRepository.findOne(userId);
    }

    @Transactional
    public void updateUserPassword(int userId, String password) {
        password = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, password);
        LOG.debug("Password updated successfully for user id {}", userId);

        Set<PasswordResetToken> resetTokens = passwordResetTokenRepository.findAllByUserId(userId);
        if (!resetTokens.isEmpty()){
            passwordResetTokenRepository.delete(resetTokens);
        }
    }
}
