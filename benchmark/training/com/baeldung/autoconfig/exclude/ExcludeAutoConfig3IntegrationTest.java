package com.baeldung.autoconfig.exclude;


import HttpStatus.OK;
import com.baeldung.boot.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class ExcludeAutoConfig3IntegrationTest {
    @Test
    public void givenSecurityConfigExcluded_whenAccessHome_thenNoAuthenticationRequired() {
        int statusCode = io.restassured.RestAssured.get("http://localhost:8080/").statusCode();
        Assert.assertEquals(OK.value(), statusCode);
    }
}

