package com.stackify.services;


import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class UserServiceLiveTest {
    @Test
    public void whenAddUser_thenGetUserOk() {
        RestAssured.baseURI = "http://localhost:8080/rest-server";
        // @formatter:off
        String json = "{\"email\":\"john@gmail.com\",\"name\":\"John\"}";
        when().post("/users").then().statusCode(200);
        when().get("/users").then().contentType("application/json").body("name", CoreMatchers.hasItem("John")).body("email", CoreMatchers.hasItem("john@gmail.com"));
        // @formatter:on
    }
}

