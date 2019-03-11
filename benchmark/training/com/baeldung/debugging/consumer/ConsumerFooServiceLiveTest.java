package com.baeldung.debugging.consumer;


import com.baeldung.debugging.consumer.service.FooService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;


public class ConsumerFooServiceLiveTest {
    FooService service = new FooService();

    private static final String BASE_URL = "http://localhost:8082";

    private static final String DEBUG_HOOK_ON = (ConsumerFooServiceLiveTest.BASE_URL) + "/debug-hook-on";

    private static final String DEBUG_HOOK_OFF = (ConsumerFooServiceLiveTest.BASE_URL) + "/debug-hook-off";

    private static WebTestClient client;

    @Test
    public void whenRequestingDebugHookOn_thenObtainExpectedMessage() {
        ResponseSpec response = ConsumerFooServiceLiveTest.client.get().uri(ConsumerFooServiceLiveTest.DEBUG_HOOK_ON).exchange();
        response.expectStatus().isOk().expectBody(String.class).isEqualTo("DEBUG HOOK ON");
    }

    @Test
    public void whenRequestingDebugHookOff_thenObtainExpectedMessage() {
        ResponseSpec response = ConsumerFooServiceLiveTest.client.get().uri(ConsumerFooServiceLiveTest.DEBUG_HOOK_OFF).exchange();
        response.expectStatus().isOk().expectBody(String.class).isEqualTo("DEBUG HOOK OFF");
    }
}

