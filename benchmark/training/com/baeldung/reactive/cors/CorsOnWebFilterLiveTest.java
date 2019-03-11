package com.baeldung.reactive.cors;


import SpringBootTest.WebEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CorsOnWebFilterLiveTest {
    private static final String BASE_URL = "http://localhost:8083";

    private static final String BASE_REGULAR_URL = "/web-filter-on-annotated";

    private static final String BASE_EXTRA_CORS_CONFIG_URL = "/web-filter-and-more-on-annotated";

    private static final String BASE_FUNCTIONALS_URL = "/web-filter-on-functional";

    private static final String CORS_DEFAULT_ORIGIN = "http://allowed-origin.com";

    private static WebTestClient client;

    @Test
    public void whenRequestingRegularEndpoint_thenObtainResponseWithCorsHeaders() {
        ResponseSpec response = CorsOnWebFilterLiveTest.client.put().uri(((CorsOnWebFilterLiveTest.BASE_REGULAR_URL) + "/regular-put-endpoint")).exchange();
        response.expectHeader().valueEquals("Access-Control-Allow-Origin", CorsOnWebFilterLiveTest.CORS_DEFAULT_ORIGIN);
    }

    @Test
    public void whenRequestingRegularDeleteEndpoint_thenObtainForbiddenResponse() {
        ResponseSpec response = CorsOnWebFilterLiveTest.client.delete().uri(((CorsOnWebFilterLiveTest.BASE_REGULAR_URL) + "/regular-delete-endpoint")).exchange();
        response.expectStatus().isForbidden();
    }

    @Test
    public void whenPreflightDeleteEndpointWithExtraConfigs_thenObtainForbiddenResponse() {
        ResponseSpec response = CorsOnWebFilterLiveTest.client.options().uri(((CorsOnWebFilterLiveTest.BASE_EXTRA_CORS_CONFIG_URL) + "/further-mixed-config-endpoint")).header("Access-Control-Request-Method", "DELETE").exchange();
        response.expectStatus().isForbidden();
    }

    @Test
    public void whenRequestDeleteEndpointWithExtraConfigs_thenObtainForbiddenResponse() {
        ResponseSpec response = CorsOnWebFilterLiveTest.client.delete().uri(((CorsOnWebFilterLiveTest.BASE_EXTRA_CORS_CONFIG_URL) + "/further-mixed-config-endpoint")).exchange();
        response.expectStatus().isForbidden();
    }

    @Test
    public void whenPreflightFunctionalEndpoint_thenObtain404Response() {
        ResponseSpec response = CorsOnWebFilterLiveTest.client.options().uri(((CorsOnWebFilterLiveTest.BASE_FUNCTIONALS_URL) + "/functional-endpoint")).header("Access-Control-Request-Method", "PUT").exchange();
        response.expectHeader().valueEquals("Access-Control-Allow-Origin", CorsOnWebFilterLiveTest.CORS_DEFAULT_ORIGIN);
        response.expectHeader().valueEquals("Access-Control-Allow-Methods", "PUT");
        response.expectHeader().valueEquals("Access-Control-Max-Age", "8000");
    }

    @Test
    public void whenRequestFunctionalEndpoint_thenObtainResponseWithCorsHeaders() {
        ResponseSpec response = CorsOnWebFilterLiveTest.client.put().uri(((CorsOnWebFilterLiveTest.BASE_FUNCTIONALS_URL) + "/functional-endpoint")).exchange();
        response.expectHeader().valueEquals("Access-Control-Allow-Origin", CorsOnWebFilterLiveTest.CORS_DEFAULT_ORIGIN);
    }
}

