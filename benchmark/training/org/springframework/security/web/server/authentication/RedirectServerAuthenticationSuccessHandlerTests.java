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
package org.springframework.security.web.server.authentication;


import HttpStatus.FOUND;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.test.publisher.PublisherProbe;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RedirectServerAuthenticationSuccessHandlerTests {
    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerRedirectStrategy redirectStrategy;

    @Mock
    private Authentication authentication;

    private URI location = URI.create("/");

    private RedirectServerAuthenticationSuccessHandler handler = new RedirectServerAuthenticationSuccessHandler();

    @Test(expected = IllegalArgumentException.class)
    public void constructorStringWhenNullLocationThenException() {
        new RedirectServerAuthenticationEntryPoint(null);
    }

    @Test
    public void successWhenNoSubscribersThenNoActions() {
        this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
        this.handler.onAuthenticationSuccess(new org.springframework.security.web.server.WebFilterExchange(this.exchange, this.chain), this.authentication);
        assertThat(this.exchange.getResponse().getHeaders().getLocation()).isNull();
        assertThat(this.exchange.getSession().block().isStarted()).isFalse();
    }

    @Test
    public void successWhenSubscribeThenStatusAndLocationSet() {
        this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
        this.handler.onAuthenticationSuccess(new org.springframework.security.web.server.WebFilterExchange(this.exchange, this.chain), this.authentication).block();
        assertThat(this.exchange.getResponse().getStatusCode()).isEqualTo(FOUND);
        assertThat(this.exchange.getResponse().getHeaders().getLocation()).isEqualTo(this.location);
    }

    @Test
    public void successWhenCustomLocationThenCustomLocationUsed() {
        PublisherProbe<Void> redirectResult = PublisherProbe.empty();
        Mockito.when(this.redirectStrategy.sendRedirect(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(redirectResult.mono());
        this.handler.setRedirectStrategy(this.redirectStrategy);
        this.exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
        this.handler.onAuthenticationSuccess(new org.springframework.security.web.server.WebFilterExchange(this.exchange, this.chain), this.authentication).block();
        redirectResult.assertWasSubscribed();
        Mockito.verify(this.redirectStrategy).sendRedirect(ArgumentMatchers.any(), ArgumentMatchers.eq(this.location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRedirectStrategyWhenNullThenException() {
        this.handler.setRedirectStrategy(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLocationWhenNullThenException() {
        this.handler.setLocation(null);
    }
}

