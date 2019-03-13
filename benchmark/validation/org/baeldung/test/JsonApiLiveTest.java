package org.baeldung.test;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;


public class JsonApiLiveTest {
    private static final String URL_PREFIX = "http://localhost:8082/spring-katharsis/users";

    @Test
    public void whenGettingAllUsers_thenCorrect() {
        final Response response = RestAssured.get(JsonApiLiveTest.URL_PREFIX);
        Assert.assertEquals(200, response.statusCode());
        System.out.println(response.asString());
    }
}

