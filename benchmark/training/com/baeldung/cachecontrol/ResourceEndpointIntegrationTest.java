package com.baeldung.cachecontrol;


import ContentType.JSON;
import SpringBootTest.WebEnvironment;
import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = AppRunner.class)
public class ResourceEndpointIntegrationTest {
    @LocalServerPort
    private int serverPort;

    @Test
    public void whenGetRequestForUser_shouldRespondWithDefaultCacheHeaders() {
        RestAssured.given().when().get(((getBaseUrl()) + "/default/users/Michael")).then().headers("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate").header("Pragma", "no-cache");
    }

    @Test
    public void whenGetRequestForUser_shouldRespondMaxAgeCacheControl() {
        RestAssured.given().when().get(((getBaseUrl()) + "/users/Michael")).then().header("Cache-Control", "max-age=60");
    }

    @Test
    public void givenServiceEndpoint_whenGetRequestForUser_shouldResponseWithCacheControlMaxAge() {
        RestAssured.given().when().get(((getBaseUrl()) + "/users/Michael")).then().contentType(JSON).and().statusCode(200).and().header("Cache-Control", "max-age=60");
    }

    @Test
    public void givenServiceEndpoint_whenGetRequestForNotCacheableContent_shouldResponseWithCacheControlNoCache() {
        RestAssured.given().when().get(((getBaseUrl()) + "/timestamp")).then().contentType(JSON).and().statusCode(200).and().header("Cache-Control", "no-store");
    }

    @Test
    public void givenServiceEndpoint_whenGetRequestForPrivateUser_shouldResponseWithSecurityDefaultCacheControl() {
        RestAssured.given().when().get(((getBaseUrl()) + "/private/users/Michael")).then().contentType(JSON).and().statusCode(200).and().header("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    }
}

