package org.baeldung.web;


import org.baeldung.custom.Application;
import org.baeldung.custom.persistence.dao.UserRepository;
import org.baeldung.custom.persistence.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
@WebAppConfiguration
public class CustomUserDetailsServiceIntegrationTest {
    private static final String USERNAME = "user";

    private static final String PASSWORD = "pass";

    private static final String USERNAME2 = "user2";

    @Autowired
    private UserRepository myUserRepository;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 
    @Test
    public void givenExistingUser_whenAuthenticate_thenRetrieveFromDb() {
        User user = new User();
        user.setUsername(CustomUserDetailsServiceIntegrationTest.USERNAME);
        user.setPassword(passwordEncoder.encode(CustomUserDetailsServiceIntegrationTest.PASSWORD));
        myUserRepository.save(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(CustomUserDetailsServiceIntegrationTest.USERNAME, CustomUserDetailsServiceIntegrationTest.PASSWORD);
        Authentication authentication = authenticationProvider.authenticate(auth);
        Assert.assertEquals(authentication.getName(), CustomUserDetailsServiceIntegrationTest.USERNAME);
    }

    @Test(expected = BadCredentialsException.class)
    public void givenIncorrectUser_whenAuthenticate_thenBadCredentialsException() {
        User user = new User();
        user.setUsername(CustomUserDetailsServiceIntegrationTest.USERNAME);
        user.setPassword(passwordEncoder.encode(CustomUserDetailsServiceIntegrationTest.PASSWORD));
        myUserRepository.save(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(CustomUserDetailsServiceIntegrationTest.USERNAME2, CustomUserDetailsServiceIntegrationTest.PASSWORD);
        authenticationProvider.authenticate(auth);
    }
}

