package com.baeldung;


import MediaType.APPLICATION_JSON;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;


public class LiveTest {
    private static String APP_ROOT = "http://localhost:8081";

    @Test
    public void givenUser_whenResourceCreatedWithNullName_then400BadRequest() {
        final Response response = givenAuth("user", "pass").contentType(APPLICATION_JSON.toString()).body(resourceWithNullName()).post(((LiveTest.APP_ROOT) + "/foos"));
        Assert.assertEquals(400, response.getStatusCode());
    }

    @Test
    public void givenUser_whenResourceCreated_then201Created() {
        final Response response = givenAuth("user", "pass").contentType(APPLICATION_JSON.toString()).body(resourceString()).post(((LiveTest.APP_ROOT) + "/foos"));
        Assert.assertEquals(201, response.getStatusCode());
    }
}

