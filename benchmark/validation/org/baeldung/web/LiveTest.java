package org.baeldung.web;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;


public class LiveTest {
    @Test
    public void whenSendRequestToFooResource_thenOK() {
        final Response response = RestAssured.get("http://localhost:8080/foos/1");
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenSendRequest_thenHeaderAdded() {
        final Response response = RestAssured.get("http://localhost:8080/foos/1");
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("TestSample", response.getHeader("Test"));
    }
}

