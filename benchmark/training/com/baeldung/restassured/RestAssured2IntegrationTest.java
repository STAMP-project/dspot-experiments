package com.baeldung.restassured;


import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RestAssured2IntegrationTest {
    private static final int PORT = 8084;

    private static WireMockServer wireMockServer = new WireMockServer(RestAssured2IntegrationTest.PORT);

    private static final String EVENTS_PATH = "/odds";

    private static final String APPLICATION_JSON = "application/json";

    private static final String ODDS = RestAssured2IntegrationTest.getJson();

    @Test
    public void givenUrl_whenVerifiesOddPricesAccuratelyByStatus_thenCorrect() {
        get("/odds").then().body("odds.findAll { it.status > 0 }.price", Matchers.hasItems(5.25F, 1.2F));
    }

    @Test
    public void whenRequestedPost_thenCreated() {
        RestAssured.with().body(new Odd(5.25F, 1, 13.1F, "X")).when().request("POST", "/odds/new").then().statusCode(201);
    }
}

