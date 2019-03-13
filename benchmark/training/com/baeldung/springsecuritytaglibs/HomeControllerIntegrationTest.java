package com.baeldung.springsecuritytaglibs;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SpringBootSecurityTagLibsApplication.class)
public class HomeControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenUserIsAuthenticatedThenAuthenticatedSectionsShowOnSite() throws Exception {
        String body = this.restTemplate.withBasicAuth("testUser", "password").getForEntity("/", String.class).getBody();
        // test <sec:authorize access="!isAuthenticated()">
        Assert.assertFalse(body.contains("Login"));
        // test <sec:authorize access="isAuthenticated()">
        Assert.assertTrue(body.contains("Logout"));
        // test <sec:authorize access="hasRole('ADMIN')">
        Assert.assertTrue(body.contains("Manage Users"));
        // test <sec:authentication property="principal.username" />
        Assert.assertTrue(body.contains("testUser"));
        // test <sec:authorize url="/adminOnlyURL">
        Assert.assertTrue(body.contains("<a href=\"/userManagement\">"));
        // test <sec:csrfInput />
        Assert.assertTrue(body.contains("<input type=\"hidden\" name=\"_csrf\" value=\""));
        // test <sec:csrfMetaTags />
        Assert.assertTrue(body.contains("<meta name=\"_csrf_parameter\" content=\"_csrf\" />"));
    }

    @Test
    public void whenUserIsNotAuthenticatedThenOnlyAnonymousSectionsShowOnSite() throws Exception {
        String body = this.restTemplate.getForEntity("/", String.class).getBody();
        // test <sec:authorize access="!isAuthenticated()">
        Assert.assertTrue(body.contains("Login"));
        // test <sec:authorize access="isAuthenticated()">
        Assert.assertFalse(body.contains("Logout"));
    }
}

