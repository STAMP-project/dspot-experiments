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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.test.web.reactive.server.WebTestClientBuilder;
import org.springframework.security.test.web.reactive.server.WebTestHandler;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpHeaderWriterWebFilterTests {
    @Mock
    ServerHttpHeadersWriter writer;

    HttpHeaderWriterWebFilter filter;

    @Test
    public void filterWhenCompleteThenWritten() {
        WebTestClient rest = WebTestClientBuilder.bindToWebFilters(filter).build();
        rest.get().uri("/foo").exchange();
        Mockito.verify(writer).writeHttpHeaders(ArgumentMatchers.any());
    }

    @Test
    public void filterWhenNotCompleteThenNotWritten() {
        WebTestHandler handler = WebTestHandler.bindToWebFilters(filter);
        WebTestHandler.WebHandlerResult result = handler.exchange(MockServerHttpRequest.get("/foo"));
        Mockito.verify(writer, Mockito.never()).writeHttpHeaders(ArgumentMatchers.any());
        result.getExchange().getResponse().setComplete();
        Mockito.verify(writer).writeHttpHeaders(ArgumentMatchers.any());
    }
}

