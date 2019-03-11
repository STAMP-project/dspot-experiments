package org.baeldung.web;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;


public class SwaggerLiveTest {
    private static final String URL_PREFIX = "http://localhost:8080/spring-security-rest/api";

    @Test
    public void whenVerifySpringFoxIsWorking_thenOK() {
        final Response response = RestAssured.get(((SwaggerLiveTest.URL_PREFIX) + "/v2/api-docs"));
        Assert.assertEquals(200, response.statusCode());
        System.out.println(response.asString());
    }
}

