/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.reactive.function.client;


import HttpStatus.ACCEPTED;
import MediaType.APPLICATION_JSON;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.AbstractDataBufferAllocatingTestCase;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * WebClient integration tests focusing on data buffer management.
 *
 * @author Rossen Stoyanchev
 */
public class WebClientDataBufferAllocatingTests extends AbstractDataBufferAllocatingTestCase {
    private static final Duration DELAY = Duration.ofSeconds(5);

    private MockWebServer server;

    private WebClient webClient;

    private ReactorResourceFactory factory;

    @Test
    public void bodyToMonoVoid() {
        this.server.enqueue(new MockResponse().setResponseCode(201).setHeader("Content-Type", "application/json").setChunkedBody("{\"foo\" : {\"bar\" : \"123\", \"baz\" : \"456\"}}", 5));
        Mono<Void> mono = this.webClient.get().uri("/json").accept(APPLICATION_JSON).retrieve().bodyToMono(Void.class);
        StepVerifier.create(mono).expectComplete().verify(Duration.ofSeconds(3));
        Assert.assertEquals(1, this.server.getRequestCount());
    }

    // SPR-17482
    @Test
    public void bodyToMonoVoidWithoutContentType() {
        this.server.enqueue(new MockResponse().setResponseCode(ACCEPTED.value()).setChunkedBody("{\"foo\" : \"123\",  \"baz\" : \"456\", \"baz\" : \"456\"}", 5));
        Mono<Map<String, String>> mono = this.webClient.get().uri("/sample").accept(APPLICATION_JSON).retrieve().bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, String>>() {});
        StepVerifier.create(mono).expectError(UnsupportedMediaTypeException.class).verify(Duration.ofSeconds(3));
        Assert.assertEquals(1, this.server.getRequestCount());
    }

    @Test
    public void onStatusWithBodyNotConsumed() {
        RuntimeException ex = new RuntimeException("response error");
        testOnStatus(ex, ( response) -> Mono.just(ex));
    }

    @Test
    public void onStatusWithBodyConsumed() {
        RuntimeException ex = new RuntimeException("response error");
        testOnStatus(ex, ( response) -> response.bodyToMono(Void.class).thenReturn(ex));
    }

    // SPR-17473
    @Test
    public void onStatusWithMonoErrorAndBodyNotConsumed() {
        RuntimeException ex = new RuntimeException("response error");
        testOnStatus(ex, ( response) -> Mono.error(ex));
    }

    @Test
    public void onStatusWithMonoErrorAndBodyConsumed() {
        RuntimeException ex = new RuntimeException("response error");
        testOnStatus(ex, ( response) -> response.bodyToMono(Void.class).then(Mono.error(ex)));
    }
}

