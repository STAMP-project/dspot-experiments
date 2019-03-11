package org.baeldung;


import HttpHeaders.AUTHORIZATION;
import HttpStatus.OK;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;


public class UserInfoEndpointLiveTest {
    @Test
    public void givenAccessToken_whenAccessUserInfoEndpoint_thenSuccess() {
        String accessToken = obtainAccessTokenUsingAuthorizationCodeFlow("john", "123");
        Response response = RestAssured.given().header(AUTHORIZATION, ("Bearer " + accessToken)).get("http://localhost:8081/auth/user/me");
        Assert.assertEquals(OK.value(), response.getStatusCode());
        Assert.assertEquals("john", response.jsonPath().get("name"));
    }
}

