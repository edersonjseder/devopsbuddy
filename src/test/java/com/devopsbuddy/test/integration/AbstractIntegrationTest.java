package com.devopsbuddy.test.integration;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by root on 11/06/17.
 */
public abstract class AbstractIntegrationTest {

    @Autowired
    protected PlanRepository planRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;


    protected User createUser(String username, String email) {

        /** Creates a plan first */
        Plan basicPlan = createBasicPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);

        /** Creates the user and add the plan object as a foreign key */
        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(basicPlan);

        /** Creates a role */
        Role basicRole = createBasicRole(RolesEnum.BASIC);
        roleRepository.save(basicRole);

        /** Creates a Set collection of roles due to the
         * one to many relationship between entities */
        Set<UserRole> userRoles = new HashSet<>();

        /** Creates the object that represent the one to many
         * relationship between user and role entities and add
         * both objects on entity as foreign key */
        UserRole userRole = new UserRole(basicUser, basicRole);
        userRoles.add(userRole);

        /** Adding the object collection of user roles to user entity
         * (Always call the get method of Set collection to add objects in JPA)*/
        basicUser.getUserRoles().addAll(userRoles);
        basicUser = userRepository.save(basicUser);

        return basicUser;
    }

    protected Plan createBasicPlan(PlansEnum plansEnum) {
        return new Plan(plansEnum);
    }

    protected Role createBasicRole(RolesEnum rolesEnum) {
        return new Role(rolesEnum);
    }

    protected User createUser(TestName testName) {
        return createUser(testName.getMethodName(), testName.getMethodName() + "@devopsbuddy.com");
    }
}
