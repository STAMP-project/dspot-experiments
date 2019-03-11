package com.baeldung.restassured;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RestAssuredXML2IntegrationTest {
    private static final int PORT = 8082;

    private static WireMockServer wireMockServer = new WireMockServer(RestAssuredXML2IntegrationTest.PORT);

    private static final String EVENTS_PATH = "/teachers";

    private static final String APPLICATION_XML = "application/xml";

    private static final String TEACHERS = RestAssuredXML2IntegrationTest.getXml();

    @Test
    public void givenUrl_whenVerifiesScienceTeacherFromXml_thenCorrect() {
        get("/teachers").then().body("teachers.teacher.find { it.@department == 'science' }.subject", Matchers.hasItems("math", "physics"));
    }
}

