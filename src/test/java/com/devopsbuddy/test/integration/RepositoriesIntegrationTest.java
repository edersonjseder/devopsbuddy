package com.devopsbuddy.test.integration;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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

    private static final int BASIC_PLAN_ID = 1;
    private static final int BASIC_ROLE_ID = 1;

    @Before
    public void init() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        Plan retrievePlan = planRepository.findOne(BASIC_PLAN_ID);
        Assert.assertNotNull(retrievePlan);

    }

    @Test
    public void testCreateNewRole() throws Exception {
        Role basicRole = createBasicRole();
        roleRepository.save(basicRole);
        Role retrieveRole = roleRepository.findOne(BASIC_ROLE_ID);
        Assert.assertNotNull(retrieveRole);

    }

    @Test
    public void testCreateNewUser() throws Exception {
        /** Creates a plan first */
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);

        /** Creates the user and add the plan object as a foreign key */
        User basicUser = createBasicUser();
        basicUser.setPlan(basicPlan);

        /** Creates a role */
        Role basicRole = createBasicRole();
        /** Creates a Set collection of roles due to the
         * one to many relationship between entities */
        Set<UserRole> userRoles = new HashSet<>();
        /** Creates the object that represent the one to many
         * relationship between user and role entities and add
         * both objects on entity as foreign key */
        UserRole userRole = new UserRole();
        userRole.setUser(basicUser);
        userRole.setRole(basicRole);

        userRoles.add(userRole);

        /** Adding the object collection of user roles to user entity
         * (Always call the get method of Set collection to add objects in JPA)*/
        basicUser.getUserRoles().addAll(userRoles);

        /** Saves the other side of the user to roles
         * relationship by persisting all roles in
         * the UserRoles collection */
        for (UserRole ur : userRoles) {

            roleRepository.save(ur.getRole());
        }

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

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("Basic");
        return plan;
    }

    private Role createBasicRole() {
        Role role = new Role();
        role.setId(BASIC_ROLE_ID);
        role.setName("ROLE_USER");
        return role;
    }

    private User createBasicUser() {
        User user = new User();
        user.setUsername("basicUser");
        user.setPassword("secret");
        user.setEmail("me@example.com");
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
