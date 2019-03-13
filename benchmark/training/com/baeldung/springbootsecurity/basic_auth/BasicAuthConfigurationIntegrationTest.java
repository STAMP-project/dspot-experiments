package com.baeldung.springbootsecurity.basic_auth;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import java.io.IOException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
    public void whenLoggedUserRequestsHomePage_ThenSuccess() throws IOException, IllegalStateException {
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);
        Assert.assertEquals(OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("Baeldung"));
    }

    @Test
    public void whenUserWithWrongCredentialsRequestsHomePage_ThenUnauthorizedPage() throws IOException, IllegalStateException {
        restTemplate = new TestRestTemplate("user", "wrongpassword");
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("Unauthorized"));
    }
}

