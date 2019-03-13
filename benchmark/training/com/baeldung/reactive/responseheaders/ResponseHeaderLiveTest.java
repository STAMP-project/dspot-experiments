package com.baeldung.reactive.responseheaders;


import SpringBootTest.WebEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ResponseHeaderLiveTest {
    private static final String BASE_URL = "http://localhost:8080";

    private static final String ANNOTATION_BASE_URL = (ResponseHeaderLiveTest.BASE_URL) + "/response-header";

    private static final String FUNCTIONAL_BASE_URL = (ResponseHeaderLiveTest.BASE_URL) + "/functional-response-header";

    private static final String SERVICE_SINGLE_RESPONSE_HEADER = "Baeldung-Example-Header";

    private static final String SERVICE_FILTER_RESPONSE_HEADER = "Baeldung-Example-Filter-Header";

    private static final String SERVICE_FILTER_RESPONSE_HEADER_VALUE = "Value-Filter";

    private static WebTestClient client;

    @Test
    public void whenUsingResponseEntityBuilderRequest_thenObtainResponseWithCorrectHeaders() {
        ResponseHeaderLiveTest.client = WebTestClient.bindToServer().baseUrl(ResponseHeaderLiveTest.BASE_URL).build();
        ResponseSpec response = ResponseHeaderLiveTest.client.get().uri(((ResponseHeaderLiveTest.ANNOTATION_BASE_URL) + "/response-entity")).exchange();
        response.expectHeader().valueEquals(ResponseHeaderLiveTest.SERVICE_SINGLE_RESPONSE_HEADER, "Value-ResponseEntityBuilder").expectHeader().valueEquals(ResponseHeaderLiveTest.SERVICE_FILTER_RESPONSE_HEADER, ResponseHeaderLiveTest.SERVICE_FILTER_RESPONSE_HEADER_VALUE);
    }

    @Test
    public void whenUsingServerHttpResponseRequest_thenObtainResponseWithCorrectHeaders() {
        ResponseSpec response = ResponseHeaderLiveTest.client.get().uri(((ResponseHeaderLiveTest.ANNOTATION_BASE_URL) + "/server-http-response")).exchange();
        response.expectHeader().valueEquals(ResponseHeaderLiveTest.SERVICE_SINGLE_RESPONSE_HEADER, "Value-ServerHttpResponse").expectHeader().valueEquals(ResponseHeaderLiveTest.SERVICE_FILTER_RESPONSE_HEADER, ResponseHeaderLiveTest.SERVICE_FILTER_RESPONSE_HEADER_VALUE);
    }

    @Test
    public void whenUsingFunctionalHandlerRequest_thenObtainResponseWithCorrectHeaders() {
        ResponseSpec response = ResponseHeaderLiveTest.client.get().uri(((ResponseHeaderLiveTest.FUNCTIONAL_BASE_URL) + "/single-handler")).exchange();
        response.expectHeader().valueEquals(ResponseHeaderLiveTest.SERVICE_SINGLE_RESPONSE_HEADER, "Value-Handler").expectHeader().valueEquals(ResponseHeaderLiveTest.SERVICE_FILTER_RESPONSE_HEADER, ResponseHeaderLiveTest.SERVICE_FILTER_RESPONSE_HEADER_VALUE);
    }
}

