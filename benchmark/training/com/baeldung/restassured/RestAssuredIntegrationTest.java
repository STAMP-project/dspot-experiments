package com.baeldung.restassured;


import SchemaVersion.DRAFTV4;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.module.jsv.JsonSchemaValidatorSettings;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RestAssuredIntegrationTest {
    private static final int PORT = 8083;

    private static WireMockServer wireMockServer = new WireMockServer(RestAssuredIntegrationTest.PORT);

    private static final String EVENTS_PATH = "/events?id=390";

    private static final String APPLICATION_JSON = "application/json";

    private static final String GAME_ODDS = RestAssuredIntegrationTest.getEventJson();

    @Test
    public void givenUrl_whenCheckingFloatValuePasses_thenCorrect() {
        get("/events?id=390").then().assertThat().body("odd.ck", Matchers.equalTo(12.2F));
    }

    @Test
    public void givenUrl_whenSuccessOnGetsResponseAndJsonHasRequiredKV_thenCorrect() {
        get("/events?id=390").then().statusCode(200).assertThat().body("id", Matchers.equalTo("390"));
    }

    @Test
    public void givenUrl_whenJsonResponseHasArrayWithGivenValuesUnderKey_thenCorrect() {
        get("/events?id=390").then().assertThat().body("odds.price", Matchers.hasItems("1.30", "5.25", "2.70", "1.20"));
    }

    @Test
    public void givenUrl_whenJsonResponseConformsToSchema_thenCorrect() {
        get("/events?id=390").then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("event_0.json"));
    }

    @Test
    public void givenUrl_whenValidatesResponseWithInstanceSettings_thenCorrect() {
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();
        get("/events?id=390").then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("event_0.json").using(jsonSchemaFactory));
    }

    @Test
    public void givenUrl_whenValidatesResponseWithStaticSettings_thenCorrect() {
        get("/events?id=390").then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("event_0.json").using(JsonSchemaValidatorSettings.settings().with().checkedValidation(false)));
    }
}

