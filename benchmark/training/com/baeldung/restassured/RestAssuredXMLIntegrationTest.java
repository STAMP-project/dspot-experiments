package com.baeldung.restassured;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RestAssuredXMLIntegrationTest {
    private static final int PORT = 8081;

    private static WireMockServer wireMockServer = new WireMockServer(RestAssuredXMLIntegrationTest.PORT);

    private static final String EVENTS_PATH = "/employees";

    private static final String APPLICATION_XML = "application/xml";

    private static final String EMPLOYEES = RestAssuredXMLIntegrationTest.getXml();

    @Test
    public void givenUrl_whenXmlResponseValueTestsEqual_thenCorrect() {
        post("/employees").then().assertThat().body("employees.employee.first-name", Matchers.equalTo("Jane"));
    }

    @Test
    public void givenUrl_whenMultipleXmlValuesTestEqual_thenCorrect() {
        post("/employees").then().assertThat().body("employees.employee.first-name", Matchers.equalTo("Jane")).body("employees.employee.last-name", Matchers.equalTo("Daisy")).body("employees.employee.sex", Matchers.equalTo("f"));
    }

    @Test
    public void givenUrl_whenMultipleXmlValuesTestEqualInShortHand_thenCorrect() {
        post("/employees").then().assertThat().body("employees.employee.first-name", Matchers.equalTo("Jane"), "employees.employee.last-name", Matchers.equalTo("Daisy"), "employees.employee.sex", Matchers.equalTo("f"));
    }

    @Test
    public void givenUrl_whenValidatesXmlUsingXpath_thenCorrect() {
        post("/employees").then().assertThat().body(hasXPath("/employees/employee/first-name", Matchers.containsString("Ja")));
    }

    @Test
    public void givenUrl_whenValidatesXmlUsingXpath2_thenCorrect() {
        post("/employees").then().assertThat().body(hasXPath("/employees/employee/first-name[text()='Jane']"));
    }
}

