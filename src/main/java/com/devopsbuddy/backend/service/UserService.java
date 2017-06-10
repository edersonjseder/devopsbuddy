package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
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

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

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

        user = userRepository.save(user);

        return user;

    }
}
