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
package org.springframework.security.web.server.csrf;


import HttpStatus.FORBIDDEN;
import MediaType.APPLICATION_FORM_URLENCODED;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CsrfWebFilterTests {
    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerCsrfTokenRepository repository;

    private CsrfToken token = new DefaultCsrfToken("csrf", "CSRF", "a");

    private CsrfWebFilter csrfFilter = new CsrfWebFilter();

    private MockServerWebExchange get = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    private MockServerWebExchange post = MockServerWebExchange.from(MockServerHttpRequest.post("/"));

    @Test
    public void filterWhenGetThenSessionNotCreatedAndChainContinues() {
        PublisherProbe<Void> chainResult = PublisherProbe.empty();
        Mockito.when(this.chain.filter(this.get)).thenReturn(chainResult.mono());
        Mono<Void> result = this.csrfFilter.filter(this.get, this.chain);
        StepVerifier.create(result).verifyComplete();
        Mono<Boolean> isSessionStarted = this.get.getSession().map(WebSession::isStarted);
        StepVerifier.create(isSessionStarted).expectNext(false).verifyComplete();
        chainResult.assertWasSubscribed();
    }

    @Test
    public void filterWhenPostAndNoTokenThenCsrfException() {
        Mono<Void> result = this.csrfFilter.filter(this.post, this.chain);
        StepVerifier.create(result).verifyComplete();
        assertThat(this.post.getResponse().getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void filterWhenPostAndEstablishedCsrfTokenAndRequestMissingTokenThenCsrfException() {
        this.csrfFilter.setCsrfTokenRepository(this.repository);
        Mockito.when(this.repository.loadToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        Mono<Void> result = this.csrfFilter.filter(this.post, this.chain);
        StepVerifier.create(result).verifyComplete();
        assertThat(this.post.getResponse().getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void filterWhenPostAndEstablishedCsrfTokenAndRequestParamInvalidTokenThenCsrfException() {
        this.csrfFilter.setCsrfTokenRepository(this.repository);
        Mockito.when(this.repository.loadToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        this.post = MockServerWebExchange.from(MockServerHttpRequest.post("/").body(((((this.token.getParameterName()) + "=") + (this.token.getToken())) + "INVALID")));
        Mono<Void> result = this.csrfFilter.filter(this.post, this.chain);
        StepVerifier.create(result).verifyComplete();
        assertThat(this.post.getResponse().getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void filterWhenPostAndEstablishedCsrfTokenAndRequestParamValidTokenThenContinues() {
        PublisherProbe<Void> chainResult = PublisherProbe.empty();
        Mockito.when(this.chain.filter(ArgumentMatchers.any())).thenReturn(chainResult.mono());
        this.csrfFilter.setCsrfTokenRepository(this.repository);
        Mockito.when(this.repository.loadToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        Mockito.when(this.repository.generateToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        this.post = MockServerWebExchange.from(MockServerHttpRequest.post("/").contentType(APPLICATION_FORM_URLENCODED).body((((this.token.getParameterName()) + "=") + (this.token.getToken()))));
        Mono<Void> result = this.csrfFilter.filter(this.post, this.chain);
        StepVerifier.create(result).verifyComplete();
        chainResult.assertWasSubscribed();
    }

    @Test
    public void filterWhenPostAndEstablishedCsrfTokenAndHeaderInvalidTokenThenCsrfException() {
        this.csrfFilter.setCsrfTokenRepository(this.repository);
        Mockito.when(this.repository.loadToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        this.post = MockServerWebExchange.from(MockServerHttpRequest.post("/").header(this.token.getHeaderName(), ((this.token.getToken()) + "INVALID")));
        Mono<Void> result = this.csrfFilter.filter(this.post, this.chain);
        StepVerifier.create(result).verifyComplete();
        assertThat(this.post.getResponse().getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void filterWhenPostAndEstablishedCsrfTokenAndHeaderValidTokenThenContinues() {
        PublisherProbe<Void> chainResult = PublisherProbe.empty();
        Mockito.when(this.chain.filter(ArgumentMatchers.any())).thenReturn(chainResult.mono());
        this.csrfFilter.setCsrfTokenRepository(this.repository);
        Mockito.when(this.repository.loadToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        Mockito.when(this.repository.generateToken(ArgumentMatchers.any())).thenReturn(Mono.just(this.token));
        this.post = MockServerWebExchange.from(MockServerHttpRequest.post("/").header(this.token.getHeaderName(), this.token.getToken()));
        Mono<Void> result = this.csrfFilter.filter(this.post, this.chain);
        StepVerifier.create(result).verifyComplete();
        chainResult.assertWasSubscribed();
    }
}

