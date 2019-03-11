package com.baeldung.boot.autoconfig;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import com.baeldung.boot.Application;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class AutoConfigIntegrationTest {
    @Test
    public void givenNoAuthentication_whenAccessHome_thenUnauthorized() {
        int statusCode = io.restassured.RestAssured.get("http://localhost:8080/").statusCode();
        Assert.assertEquals(UNAUTHORIZED.value(), statusCode);
    }

    @Test
    public void givenAuthentication_whenAccessHome_thenOK() {
        int statusCode = io.restassured.RestAssured.given().auth().basic("john", "123").get("http://localhost:8080/").statusCode();
        Assert.assertEquals(OK.value(), statusCode);
    }
}

