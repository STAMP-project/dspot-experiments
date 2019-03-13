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


import HttpStatus.BAD_GATEWAY;
import HttpStatus.BAD_REQUEST;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class DefaultClientResponseBuilderTests {
    private DataBufferFactory dataBufferFactory;

    @Test
    public void normal() {
        Flux<DataBuffer> body = Flux.just("baz").map(( s) -> s.getBytes(StandardCharsets.UTF_8)).map(dataBufferFactory::wrap);
        ClientResponse response = ClientResponse.create(BAD_GATEWAY, ExchangeStrategies.withDefaults()).header("foo", "bar").cookie("baz", "qux").body(body).build();
        Assert.assertEquals(BAD_GATEWAY, response.statusCode());
        HttpHeaders responseHeaders = response.headers().asHttpHeaders();
        Assert.assertEquals("bar", responseHeaders.getFirst("foo"));
        Assert.assertNotNull("qux", response.cookies().getFirst("baz"));
        Assert.assertEquals("qux", response.cookies().getFirst("baz").getValue());
        StepVerifier.create(response.bodyToFlux(String.class)).expectNext("baz").verifyComplete();
    }

    @Test
    public void from() {
        Flux<DataBuffer> otherBody = Flux.just("foo", "bar").map(( s) -> s.getBytes(StandardCharsets.UTF_8)).map(dataBufferFactory::wrap);
        ClientResponse other = ClientResponse.create(BAD_REQUEST, ExchangeStrategies.withDefaults()).header("foo", "bar").cookie("baz", "qux").body(otherBody).build();
        Flux<DataBuffer> body = Flux.just("baz").map(( s) -> s.getBytes(StandardCharsets.UTF_8)).map(dataBufferFactory::wrap);
        ClientResponse result = ClientResponse.from(other).headers(( httpHeaders) -> httpHeaders.set("foo", "baar")).cookies(( cookies) -> cookies.set("baz", org.springframework.http.ResponseCookie.from("baz", "quux").build())).body(body).build();
        Assert.assertEquals(BAD_REQUEST, result.statusCode());
        Assert.assertEquals(1, result.headers().asHttpHeaders().size());
        Assert.assertEquals("baar", result.headers().asHttpHeaders().getFirst("foo"));
        Assert.assertEquals(1, result.cookies().size());
        Assert.assertEquals("quux", result.cookies().getFirst("baz").getValue());
        StepVerifier.create(result.bodyToFlux(String.class)).expectNext("baz").verifyComplete();
    }
}

