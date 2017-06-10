package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by root on 09/06/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoriesIntegrationTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Rule public TestName testName = new TestName();

    @Before
    public void init() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createBasicPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);
        Plan retrievePlan = planRepository.findOne(PlansEnum.BASIC.getId());
        Assert.assertNotNull(retrievePlan);

    }

    @Test
    public void testCreateNewRole() throws Exception {
        Role basicRole = createBasicRole(RolesEnum.BASIC);
        roleRepository.save(basicRole);
        Role retrieveRole = roleRepository.findOne(RolesEnum.BASIC.getId());
        Assert.assertNotNull(retrieveRole);

    }

    @Test
    public void testDeleteUser() throws Exception {

        String username = testName.getMethodName();
        String email = testName.getMethodName() + "@devopsbuddy.com";

        User basicUser = createUser(username, email);
        userRepository.delete(basicUser.getId());
    }

    @Test
    public void testCreateNewUser() throws Exception {

        String username = testName.getMethodName();
        String email = testName.getMethodName() + "@devopsbuddy.com";

        User basicUser = createUser(username, email);

        /** Now that all relationship entities have been
         * saved, it saves the user entity */
        basicUser = userRepository.save(basicUser);

        /** Getting a user in database */
        User newlyCreatedUser = userRepository.findOne(basicUser.getId());

        // Tests ============================================>>>
        Assert.assertNotNull(newlyCreatedUser);
        Assert.assertTrue(newlyCreatedUser.getId() != 0);
        Assert.assertNotNull(newlyCreatedUser.getPlan());
        Assert.assertNotNull(newlyCreatedUser.getPlan().getId());

        Set<UserRole> newlyCreatedUserUserRoles = newlyCreatedUser.getUserRoles();
        for (UserRole ur : newlyCreatedUserUserRoles) {
            Assert.assertNotNull(ur.getRole());
            Assert.assertNotNull(ur.getRole().getId());
        }

    }

    private User createUser(String username, String email) {

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

    private Plan createBasicPlan(PlansEnum plansEnum) {
        return new Plan(plansEnum);
    }

    private Role createBasicRole(RolesEnum rolesEnum) {
        return new Role(rolesEnum);
    }

}
