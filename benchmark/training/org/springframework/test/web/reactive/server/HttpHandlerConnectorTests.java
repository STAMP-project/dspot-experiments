/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.test.web.reactive.server;


import HttpHeaders.COOKIE;
import HttpHeaders.SET_COOKIE;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpStatus.OK;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.support.DataBufferTestUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link HttpHandlerConnector}.
 *
 * @author Rossen Stoyanchev
 */
public class HttpHandlerConnectorTests {
    @Test
    public void adaptRequest() throws Exception {
        HttpHandlerConnectorTests.TestHttpHandler handler = new HttpHandlerConnectorTests.TestHttpHandler(( response) -> {
            response.setStatusCode(OK);
            return response.setComplete();
        });
        new HttpHandlerConnector(handler).connect(POST, URI.create("/custom-path"), ( request) -> {
            request.getHeaders().put("custom-header", Arrays.asList("h0", "h1"));
            request.getCookies().add("custom-cookie", new HttpCookie("custom-cookie", "c0"));
            return request.writeWith(Mono.just(toDataBuffer("Custom body")));
        }).block(Duration.ofSeconds(5));
        MockServerHttpRequest request = ((MockServerHttpRequest) (handler.getSavedRequest()));
        Assert.assertEquals(POST, request.getMethod());
        Assert.assertEquals("/custom-path", request.getURI().toString());
        HttpHeaders headers = request.getHeaders();
        Assert.assertEquals(Arrays.asList("h0", "h1"), headers.get("custom-header"));
        Assert.assertEquals(new HttpCookie("custom-cookie", "c0"), request.getCookies().getFirst("custom-cookie"));
        Assert.assertEquals(Collections.singletonList("custom-cookie=c0"), headers.get(COOKIE));
        DataBuffer buffer = request.getBody().blockFirst(Duration.ZERO);
        Assert.assertEquals("Custom body", DataBufferTestUtils.dumpString(buffer, StandardCharsets.UTF_8));
    }

    @Test
    public void adaptResponse() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("custom-cookie", "c0").build();
        HttpHandlerConnectorTests.TestHttpHandler handler = new HttpHandlerConnectorTests.TestHttpHandler(( response) -> {
            response.setStatusCode(OK);
            response.getHeaders().put("custom-header", Arrays.asList("h0", "h1"));
            response.addCookie(cookie);
            return response.writeWith(Mono.just(toDataBuffer("Custom body")));
        });
        ClientHttpResponse response = new HttpHandlerConnector(handler).connect(GET, URI.create("/custom-path"), ReactiveHttpOutputMessage::setComplete).block(Duration.ofSeconds(5));
        Assert.assertEquals(OK, response.getStatusCode());
        HttpHeaders headers = response.getHeaders();
        Assert.assertEquals(Arrays.asList("h0", "h1"), headers.get("custom-header"));
        Assert.assertEquals(cookie, response.getCookies().getFirst("custom-cookie"));
        Assert.assertEquals(Collections.singletonList("custom-cookie=c0"), headers.get(SET_COOKIE));
        DataBuffer buffer = response.getBody().blockFirst(Duration.ZERO);
        Assert.assertEquals("Custom body", DataBufferTestUtils.dumpString(buffer, StandardCharsets.UTF_8));
    }

    private static class TestHttpHandler implements HttpHandler {
        private ServerHttpRequest savedRequest;

        private final Function<ServerHttpResponse, Mono<Void>> responseMonoFunction;

        public TestHttpHandler(Function<ServerHttpResponse, Mono<Void>> function) {
            this.responseMonoFunction = function;
        }

        public ServerHttpRequest getSavedRequest() {
            return this.savedRequest;
        }

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            this.savedRequest = request;
            return this.responseMonoFunction.apply(response);
        }
    }
}

