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
package org.springframework.security.web.server.transport;


import ServerWebExchangeMatcher.MatchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;


/**
 * Tests for {@link HttpsRedirectWebFilter}
 *
 * @author Josh Cummings
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpsRedirectWebFilterTests {
    HttpsRedirectWebFilter filter;

    @Mock
    WebFilterChain chain;

    @Test
    public void filterWhenExchangeIsInsecureThenRedirects() {
        ServerWebExchange exchange = get("http://localhost");
        this.filter.filter(exchange, this.chain).block();
        assertThat(statusCode(exchange)).isEqualTo(302);
        assertThat(redirectedUrl(exchange)).isEqualTo("https://localhost");
    }

    @Test
    public void filterWhenExchangeIsSecureThenNoRedirect() {
        ServerWebExchange exchange = get("https://localhost");
        this.filter.filter(exchange, this.chain).block();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    public void filterWhenExchangeMismatchesThenNoRedirect() {
        ServerWebExchangeMatcher matcher = Mockito.mock(ServerWebExchangeMatcher.class);
        Mockito.when(matcher.matches(ArgumentMatchers.any(ServerWebExchange.class))).thenReturn(MatchResult.notMatch());
        this.filter.setRequiresHttpsRedirectMatcher(matcher);
        ServerWebExchange exchange = get("http://localhost:8080");
        this.filter.filter(exchange, this.chain).block();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    public void filterWhenExchangeMatchesAndRequestIsInsecureThenRedirects() {
        ServerWebExchangeMatcher matcher = Mockito.mock(ServerWebExchangeMatcher.class);
        Mockito.when(matcher.matches(ArgumentMatchers.any(ServerWebExchange.class))).thenReturn(MatchResult.match());
        this.filter.setRequiresHttpsRedirectMatcher(matcher);
        ServerWebExchange exchange = get("http://localhost:8080");
        this.filter.filter(exchange, this.chain).block();
        assertThat(statusCode(exchange)).isEqualTo(302);
        assertThat(redirectedUrl(exchange)).isEqualTo("https://localhost:8443");
        Mockito.verify(matcher).matches(ArgumentMatchers.any(ServerWebExchange.class));
    }

    @Test
    public void filterWhenRequestIsInsecureThenPortMapperRemapsPort() {
        PortMapper portMapper = Mockito.mock(PortMapper.class);
        Mockito.when(portMapper.lookupHttpsPort(314)).thenReturn(159);
        this.filter.setPortMapper(portMapper);
        ServerWebExchange exchange = get("http://localhost:314");
        this.filter.filter(exchange, this.chain).block();
        assertThat(statusCode(exchange)).isEqualTo(302);
        assertThat(redirectedUrl(exchange)).isEqualTo("https://localhost:159");
        Mockito.verify(portMapper).lookupHttpsPort(314);
    }

    @Test
    public void filterWhenInsecureRequestHasAPathThenRedirects() {
        ServerWebExchange exchange = get("http://localhost:8080/path/page.html?query=string");
        this.filter.filter(exchange, this.chain).block();
        assertThat(statusCode(exchange)).isEqualTo(302);
        assertThat(redirectedUrl(exchange)).isEqualTo("https://localhost:8443/path/page.html?query=string");
    }

    @Test
    public void setRequiresTransportSecurityMatcherWhenSetWithNullValueThenThrowsIllegalArgument() {
        assertThatCode(() -> this.filter.setRequiresHttpsRedirectMatcher(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setPortMapperWhenSetWithNullValueThenThrowsIllegalArgument() {
        assertThatCode(() -> this.filter.setPortMapper(null)).isInstanceOf(IllegalArgumentException.class);
    }
}

