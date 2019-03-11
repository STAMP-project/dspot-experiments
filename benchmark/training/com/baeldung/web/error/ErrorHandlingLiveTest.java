package com.baeldung.web.error;


import HttpHeaders.ACCEPT;
import MediaType.APPLICATION_JSON_VALUE;
import MediaType.APPLICATION_XML_VALUE;
import com.baeldung.Consts;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ErrorHandlingLiveTest {
    private static final String BASE_URL = ("http://localhost:" + (Consts.APPLICATION_PORT)) + "/spring-boot-rest";

    private static final String EXCEPTION_ENDPOINT = (ErrorHandlingLiveTest.BASE_URL) + "/exception";

    private static final String ERROR_RESPONSE_KEY_PATH = "error";

    private static final String XML_RESPONSE_KEY_PATH = "xmlkey";

    private static final String LOCALE_RESPONSE_KEY_PATH = "locale";

    private static final String CAUSE_RESPONSE_KEY_PATH = "cause";

    private static final String RESPONSE_XML_ROOT = "Map";

    private static final String XML_RESPONSE_KEY_XML_PATH = ((ErrorHandlingLiveTest.RESPONSE_XML_ROOT) + ".") + (ErrorHandlingLiveTest.XML_RESPONSE_KEY_PATH);

    private static final String LOCALE_RESPONSE_KEY_XML_PATH = ((ErrorHandlingLiveTest.RESPONSE_XML_ROOT) + ".") + (ErrorHandlingLiveTest.LOCALE_RESPONSE_KEY_PATH);

    private static final String CAUSE_RESPONSE_KEY_XML_PATH = ((ErrorHandlingLiveTest.RESPONSE_XML_ROOT) + ".") + (ErrorHandlingLiveTest.CAUSE_RESPONSE_KEY_PATH);

    private static final String CAUSE_RESPONSE_VALUE = "Error in the faulty controller!";

    private static final String XML_RESPONSE_VALUE = "the XML response is different!";

    @Test
    public void whenRequestingFaultyEndpointAsJson_thenReceiveDefaultResponseWithConfiguredAttrs() {
        RestAssured.given().header(ACCEPT, APPLICATION_JSON_VALUE).get(ErrorHandlingLiveTest.EXCEPTION_ENDPOINT).then().body("$", Matchers.hasKey(ErrorHandlingLiveTest.LOCALE_RESPONSE_KEY_PATH)).body(ErrorHandlingLiveTest.CAUSE_RESPONSE_KEY_PATH, Matchers.is(ErrorHandlingLiveTest.CAUSE_RESPONSE_VALUE)).body("$", Matchers.not(Matchers.hasKey(ErrorHandlingLiveTest.ERROR_RESPONSE_KEY_PATH))).body("$", Matchers.not(Matchers.hasKey(ErrorHandlingLiveTest.XML_RESPONSE_KEY_PATH)));
    }

    @Test
    public void whenRequestingFaultyEndpointAsXml_thenReceiveXmlResponseWithConfiguredAttrs() {
        RestAssured.given().header(ACCEPT, APPLICATION_XML_VALUE).get(ErrorHandlingLiveTest.EXCEPTION_ENDPOINT).then().body(ErrorHandlingLiveTest.LOCALE_RESPONSE_KEY_XML_PATH, Matchers.isA(String.class)).body(ErrorHandlingLiveTest.CAUSE_RESPONSE_KEY_XML_PATH, Matchers.is(ErrorHandlingLiveTest.CAUSE_RESPONSE_VALUE)).body(ErrorHandlingLiveTest.RESPONSE_XML_ROOT, Matchers.not(Matchers.hasKey(ErrorHandlingLiveTest.ERROR_RESPONSE_KEY_PATH))).body(ErrorHandlingLiveTest.XML_RESPONSE_KEY_XML_PATH, Matchers.is(ErrorHandlingLiveTest.XML_RESPONSE_VALUE));
    }

    @Test
    public void whenRequestingFaultyEndpointAsHtml_thenReceiveWhitelabelPageResponse() throws Exception {
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            HtmlPage page = webClient.getPage(ErrorHandlingLiveTest.EXCEPTION_ENDPOINT);
            assertThat(page.getBody().asText()).contains("Whitelabel Error Page");
        }
    }
}

