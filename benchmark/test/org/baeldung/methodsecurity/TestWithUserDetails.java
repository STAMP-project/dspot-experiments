package org.baeldung.methodsecurity;


import org.baeldung.methodsecurity.entity.CustomUser;
import org.baeldung.methodsecurity.service.UserRoleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration
public class TestWithUserDetails {
    @Autowired
    UserRoleService userService;

    @Configuration
    @ComponentScan("org.baeldung.methodsecurity.*")
    public static class SpringConfig {}

    @Test
    @WithUserDetails(value = "john", userDetailsServiceBeanName = "userDetailService")
    public void whenJohn_callLoadUserDetail_thenOK() {
        CustomUser user = userService.loadUserDetail("jane");
        Assert.assertEquals("jane", user.getNickName());
    }

    @Test
    @WithUserDetails(value = "jane", userDetailsServiceBeanName = "userDetailService")
    public void givenJane_callSecuredLoadUserDetailWithJane_thenOK() {
        CustomUser user = userService.securedLoadUserDetail("jane");
        Assert.assertEquals("jane", user.getNickName());
        Assert.assertEquals("jane", user.getUsername());
    }

    @Test(expected = AccessDeniedException.class)
    @WithUserDetails(value = "john", userDetailsServiceBeanName = "userDetailService")
    public void givenJohn_callSecuredLoadUserDetailWithJane_thenAccessDenied() {
        userService.securedLoadUserDetail("jane");
    }

    @Test(expected = AccessDeniedException.class)
    @WithUserDetails(value = "john", userDetailsServiceBeanName = "userDetailService")
    public void givenJohn_callSecuredLoadUserDetailWithJohn_thenAccessDenied() {
        userService.securedLoadUserDetail("john");
    }
}

