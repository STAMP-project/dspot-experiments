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
package org.springframework.web.reactive.function.server;


import HttpMethod.HEAD;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class DefaultServerRequestBuilderTests {
    private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    @Test
    public void from() {
        MockServerHttpRequest request = MockServerHttpRequest.post("http://example.com").header("foo", "bar").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        ServerRequest other = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        Flux<DataBuffer> body = Flux.just("baz").map(( s) -> s.getBytes(StandardCharsets.UTF_8)).map(dataBufferFactory::wrap);
        ServerRequest result = ServerRequest.from(other).method(HEAD).headers(( httpHeaders) -> httpHeaders.set("foo", "baar")).cookies(( cookies) -> cookies.set("baz", org.springframework.http.ResponseCookie.from("baz", "quux").build())).body(body).build();
        Assert.assertEquals(HEAD, result.method());
        Assert.assertEquals(1, result.headers().asHttpHeaders().size());
        Assert.assertEquals("baar", result.headers().asHttpHeaders().getFirst("foo"));
        Assert.assertEquals(1, result.cookies().size());
        Assert.assertEquals("quux", result.cookies().getFirst("baz").getValue());
        StepVerifier.create(result.bodyToFlux(String.class)).expectNext("baz").verifyComplete();
    }
}

