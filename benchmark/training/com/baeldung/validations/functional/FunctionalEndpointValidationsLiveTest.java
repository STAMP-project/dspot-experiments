package com.baeldung.validations.functional;


import SpringBootTest.WebEnvironment;
import com.baeldung.validations.functional.model.AnnotatedRequestEntity;
import com.baeldung.validations.functional.model.CustomRequestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FunctionalEndpointValidationsLiveTest {
    private static final String BASE_URL = "http://localhost:8080";

    private static final String COMPLEX_EP_URL = (FunctionalEndpointValidationsLiveTest.BASE_URL) + "/complex-handler-functional-validation";

    private static final String DRY_EP_URL = (FunctionalEndpointValidationsLiveTest.BASE_URL) + "/dry-functional-validation";

    private static final String ANNOTATIONS_EP_URL = (FunctionalEndpointValidationsLiveTest.BASE_URL) + "/annotated-functional-validation";

    private static WebTestClient client;

    @Test
    public void whenRequestingDryEPWithInvalidBody_thenObtainBadRequest() {
        CustomRequestEntity body = new CustomRequestEntity("name", "123");
        ResponseSpec response = FunctionalEndpointValidationsLiveTest.client.post().uri(FunctionalEndpointValidationsLiveTest.DRY_EP_URL).body(reactor.core.publisher.Mono.just(body), CustomRequestEntity.class).exchange();
        response.expectStatus().isBadRequest();
    }

    @Test
    public void whenRequestingComplexEPWithInvalidBody_thenObtainBadRequest() {
        CustomRequestEntity body = new CustomRequestEntity("name", "123");
        ResponseSpec response = FunctionalEndpointValidationsLiveTest.client.post().uri(FunctionalEndpointValidationsLiveTest.COMPLEX_EP_URL).body(reactor.core.publisher.Mono.just(body), CustomRequestEntity.class).exchange();
        response.expectStatus().isBadRequest();
    }

    @Test
    public void whenRequestingAnnotatedEPWithInvalidBody_thenObtainBadRequest() {
        AnnotatedRequestEntity body = new AnnotatedRequestEntity("user", "passwordlongerthan7digits");
        ResponseSpec response = FunctionalEndpointValidationsLiveTest.client.post().uri(FunctionalEndpointValidationsLiveTest.ANNOTATIONS_EP_URL).body(reactor.core.publisher.Mono.just(body), AnnotatedRequestEntity.class).exchange();
        response.expectStatus().isBadRequest();
    }

    @Test
    public void whenRequestingDryEPWithValidBody_thenObtainBadRequest() {
        CustomRequestEntity body = new CustomRequestEntity("name", "1234567");
        ResponseSpec response = FunctionalEndpointValidationsLiveTest.client.post().uri(FunctionalEndpointValidationsLiveTest.DRY_EP_URL).body(reactor.core.publisher.Mono.just(body), CustomRequestEntity.class).exchange();
        response.expectStatus().isOk();
    }

    @Test
    public void whenRequestingComplexEPWithValidBody_thenObtainBadRequest() {
        CustomRequestEntity body = new CustomRequestEntity("name", "1234567");
        ResponseSpec response = FunctionalEndpointValidationsLiveTest.client.post().uri(FunctionalEndpointValidationsLiveTest.COMPLEX_EP_URL).body(reactor.core.publisher.Mono.just(body), CustomRequestEntity.class).exchange();
        response.expectStatus().isOk();
    }

    @Test
    public void whenRequestingAnnotatedEPWithValidBody_thenObtainBadRequest() {
        AnnotatedRequestEntity body = new AnnotatedRequestEntity("user", "12345");
        ResponseSpec response = FunctionalEndpointValidationsLiveTest.client.post().uri(FunctionalEndpointValidationsLiveTest.ANNOTATIONS_EP_URL).body(reactor.core.publisher.Mono.just(body), AnnotatedRequestEntity.class).exchange();
        response.expectStatus().isOk();
    }
}

