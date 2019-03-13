package com.baeldung.springbootsecurityrest;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import com.baeldung.springbootsecurityrest.basicauth.SpringBootSecurityApplication;
import com.baeldung.springbootsecurityrest.vo.User;
import java.io.IOException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootSecurityApplication.class)
public class BasicAuthConfigurationIntegrationTest {
    TestRestTemplate restTemplate;

    URL base;

    @LocalServerPort
    int port;

    @Test
    public void givenCorrectCredentials_whenLogin_ThenSuccess() throws IOException, IllegalStateException {
        restTemplate = new TestRestTemplate();
        User user = new User();
        user.setUserName("user");
        user.setPassword("password");
        ResponseEntity<String> response = restTemplate.postForEntity(((base.toString()) + "/login"), user, String.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("true"));
    }

    @Test
    public void givenWrongCredentials_whenLogin_ThenReturnFalse() throws IOException, IllegalStateException {
        restTemplate = new TestRestTemplate();
        User user = new User();
        user.setUserName("user");
        user.setPassword("wrongpassword");
        ResponseEntity<String> response = restTemplate.postForEntity(((base.toString()) + "/login"), user, String.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("false"));
    }

    @Test
    public void givenLoggedInUser_whenRequestsHomePage_ThenSuccess() throws IOException, IllegalStateException {
        ResponseEntity<String> response = restTemplate.getForEntity(((base.toString()) + "/user"), String.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("user"));
    }

    @Test
    public void givenWrongCredentials_whenRequestsHomePage_ThenUnauthorized() throws IOException, IllegalStateException {
        restTemplate = new TestRestTemplate("user", "wrongpassword");
        ResponseEntity<String> response = restTemplate.getForEntity(((base.toString()) + "/user"), String.class);
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("Unauthorized"));
    }
}

