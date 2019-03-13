/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.metrics.web.reactive.server;


import HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;
import HttpStatus.BAD_GATEWAY;
import HttpStatus.BAD_REQUEST;
import HttpStatus.CONTINUE;
import HttpStatus.MOVED_PERMANENTLY;
import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import io.micrometer.core.instrument.Tag;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;


/**
 * Tests for {@link WebFluxTags}.
 *
 * @author Brian Clozel
 * @author Michael McFadyen
 */
public class WebFluxTagsTests {
    private MockServerWebExchange exchange;

    private PathPatternParser parser = new PathPatternParser();

    @Test
    public void uriTagValueIsBestMatchingPatternWhenAvailable() {
        this.exchange.getAttributes().put(BEST_MATCHING_PATTERN_ATTRIBUTE, this.parser.parse("/spring"));
        this.exchange.getResponse().setStatusCode(MOVED_PERMANENTLY);
        Tag tag = WebFluxTags.uri(this.exchange);
        assertThat(tag.getValue()).isEqualTo("/spring");
    }

    @Test
    public void uriTagValueIsRedirectionWhenResponseStatusIs3xx() {
        this.exchange.getResponse().setStatusCode(MOVED_PERMANENTLY);
        Tag tag = WebFluxTags.uri(this.exchange);
        assertThat(tag.getValue()).isEqualTo("REDIRECTION");
    }

    @Test
    public void uriTagValueIsNotFoundWhenResponseStatusIs404() {
        this.exchange.getResponse().setStatusCode(NOT_FOUND);
        Tag tag = WebFluxTags.uri(this.exchange);
        assertThat(tag.getValue()).isEqualTo("NOT_FOUND");
    }

    @Test
    public void uriTagToleratesCustomResponseStatus() {
        this.exchange.getResponse().setStatusCodeValue(601);
        Tag tag = WebFluxTags.uri(this.exchange);
        assertThat(tag.getValue()).isEqualTo("root");
    }

    @Test
    public void uriTagValueIsRootWhenRequestHasNoPatternOrPathInfo() {
        Tag tag = WebFluxTags.uri(this.exchange);
        assertThat(tag.getValue()).isEqualTo("root");
    }

    @Test
    public void uriTagValueIsRootWhenRequestHasNoPatternAndSlashPathInfo() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Tag tag = WebFluxTags.uri(exchange);
        assertThat(tag.getValue()).isEqualTo("root");
    }

    @Test
    public void uriTagValueIsUnknownWhenRequestHasNoPatternAndNonRootPathInfo() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/example").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Tag tag = WebFluxTags.uri(exchange);
        assertThat(tag.getValue()).isEqualTo("UNKNOWN");
    }

    @Test
    public void methodTagToleratesNonStandardHttpMethods() {
        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        BDDMockito.given(exchange.getRequest()).willReturn(request);
        BDDMockito.given(request.getMethodValue()).willReturn("CUSTOM");
        Tag tag = WebFluxTags.method(exchange);
        assertThat(tag.getValue()).isEqualTo("CUSTOM");
    }

    @Test
    public void outcomeTagIsUnknownWhenResponseStatusIsNull() {
        this.exchange.getResponse().setStatusCode(null);
        Tag tag = WebFluxTags.outcome(this.exchange);
        assertThat(tag.getValue()).isEqualTo("UNKNOWN");
    }

    @Test
    public void outcomeTagIsInformationalWhenResponseIs1xx() {
        this.exchange.getResponse().setStatusCode(CONTINUE);
        Tag tag = WebFluxTags.outcome(this.exchange);
        assertThat(tag.getValue()).isEqualTo("INFORMATIONAL");
    }

    @Test
    public void outcomeTagIsSuccessWhenResponseIs2xx() {
        this.exchange.getResponse().setStatusCode(OK);
        Tag tag = WebFluxTags.outcome(this.exchange);
        assertThat(tag.getValue()).isEqualTo("SUCCESS");
    }

    @Test
    public void outcomeTagIsRedirectionWhenResponseIs3xx() {
        this.exchange.getResponse().setStatusCode(MOVED_PERMANENTLY);
        Tag tag = WebFluxTags.outcome(this.exchange);
        assertThat(tag.getValue()).isEqualTo("REDIRECTION");
    }

    @Test
    public void outcomeTagIsClientErrorWhenResponseIs4xx() {
        this.exchange.getResponse().setStatusCode(BAD_REQUEST);
        Tag tag = WebFluxTags.outcome(this.exchange);
        assertThat(tag.getValue()).isEqualTo("CLIENT_ERROR");
    }

    @Test
    public void outcomeTagIsServerErrorWhenResponseIs5xx() {
        this.exchange.getResponse().setStatusCode(BAD_GATEWAY);
        Tag tag = WebFluxTags.outcome(this.exchange);
        assertThat(tag.getValue()).isEqualTo("SERVER_ERROR");
    }
}

