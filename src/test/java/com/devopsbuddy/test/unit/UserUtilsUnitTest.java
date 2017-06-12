package com.devopsbuddy.test.unit;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.utils.UserUtils;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

/**
 * Created by root on 11/06/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserUtilsUnitTest {

    private MockHttpServletRequest mockHttpServletRequest;

    @Before
    public void init() {
        mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    public void testPasswordResetEmailUrlConstruction() throws Exception {

        mockHttpServletRequest.setServerPort(8080); // Default is 80

        String token = UUID.randomUUID().toString();
        int userId = 123456;

        String expectedUrl = "http://localhost:8080" +
                ForgotMyPasswordController.CHANGE_PASSWORD_PATH + "?id=" + userId + "&token=" + token;

        String actualUrl = UserUtils.createPasswordResetUrl(mockHttpServletRequest, userId, token);

        Assert.assertEquals(expectedUrl, actualUrl);
    }
}
