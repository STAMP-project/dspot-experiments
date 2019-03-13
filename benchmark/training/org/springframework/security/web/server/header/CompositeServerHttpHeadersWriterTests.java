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
package org.springframework.security.web.server.header;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeServerHttpHeadersWriterTests {
    @Mock
    ServerHttpHeadersWriter writer1;

    @Mock
    ServerHttpHeadersWriter writer2;

    CompositeServerHttpHeadersWriter writer;

    ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

    @Test
    public void writeHttpHeadersWhenErrorNoErrorThenError() {
        Mockito.when(writer1.writeHttpHeaders(exchange)).thenReturn(Mono.error(new RuntimeException()));
        Mockito.when(writer2.writeHttpHeaders(exchange)).thenReturn(Mono.empty());
        Mono<Void> result = writer.writeHttpHeaders(exchange);
        StepVerifier.create(result).expectError().verify();
        Mockito.verify(writer1).writeHttpHeaders(exchange);
        Mockito.verify(writer2).writeHttpHeaders(exchange);
    }

    @Test
    public void writeHttpHeadersWhenErrorErrorThenError() {
        Mockito.when(writer1.writeHttpHeaders(exchange)).thenReturn(Mono.error(new RuntimeException()));
        Mockito.when(writer2.writeHttpHeaders(exchange)).thenReturn(Mono.error(new RuntimeException()));
        Mono<Void> result = writer.writeHttpHeaders(exchange);
        StepVerifier.create(result).expectError().verify();
        Mockito.verify(writer1).writeHttpHeaders(exchange);
        Mockito.verify(writer2).writeHttpHeaders(exchange);
    }

    @Test
    public void writeHttpHeadersWhenNoErrorThenNoError() {
        Mockito.when(writer1.writeHttpHeaders(exchange)).thenReturn(Mono.empty());
        Mockito.when(writer2.writeHttpHeaders(exchange)).thenReturn(Mono.empty());
        Mono<Void> result = writer.writeHttpHeaders(exchange);
        StepVerifier.create(result).expectComplete().verify();
        Mockito.verify(writer1).writeHttpHeaders(exchange);
        Mockito.verify(writer2).writeHttpHeaders(exchange);
    }
}

