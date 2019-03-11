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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.reactive.server.WebTestClientBuilder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationWebFilterTests {
    @Mock
    private ServerAuthenticationSuccessHandler successHandler;

    @Mock
    private ServerAuthenticationConverter authenticationConverter;

    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    @Mock
    private ServerAuthenticationFailureHandler failureHandler;

    @Mock
    private ServerSecurityContextRepository securityContextRepository;

    private AuthenticationWebFilter filter;

    @Test
    public void filterWhenDefaultsAndNoAuthenticationThenContinues() {
        this.filter = new AuthenticationWebFilter(this.authenticationManager);
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        EntityExchangeResult<String> result = client.get().uri("/").exchange().expectStatus().isOk().expectBody(String.class).consumeWith(( b) -> assertThat(b.getResponseBody()).isEqualTo("ok")).returnResult();
        Mockito.verifyZeroInteractions(this.authenticationManager);
        assertThat(result.getResponseCookies()).isEmpty();
    }

    @Test
    public void filterWhenDefaultsAndAuthenticationSuccessThenContinues() {
        Mockito.when(this.authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(Mono.just(new TestingAuthenticationToken("test", "this", "ROLE")));
        this.filter = new AuthenticationWebFilter(this.authenticationManager);
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        EntityExchangeResult<String> result = client.get().uri("/").headers(( headers) -> headers.setBasicAuth("test", "this")).exchange().expectStatus().isOk().expectBody(String.class).consumeWith(( b) -> assertThat(b.getResponseBody()).isEqualTo("ok")).returnResult();
        assertThat(result.getResponseCookies()).isEmpty();
    }

    @Test
    public void filterWhenDefaultsAndAuthenticationFailThenUnauthorized() {
        Mockito.when(this.authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(Mono.error(new BadCredentialsException("failed")));
        this.filter = new AuthenticationWebFilter(this.authenticationManager);
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        EntityExchangeResult<Void> result = client.get().uri("/").headers(( headers) -> headers.setBasicAuth("test", "this")).exchange().expectStatus().isUnauthorized().expectHeader().valueMatches("WWW-Authenticate", "Basic realm=\"Realm\"").expectBody().isEmpty();
        assertThat(result.getResponseCookies()).isEmpty();
    }

    @Test
    public void filterWhenConvertEmptyThenOk() {
        Mockito.when(this.authenticationConverter.convert(ArgumentMatchers.any())).thenReturn(Mono.empty());
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        client.get().uri("/").exchange().expectStatus().isOk().expectBody(String.class).consumeWith(( b) -> assertThat(b.getResponseBody()).isEqualTo("ok")).returnResult();
        Mockito.verify(this.securityContextRepository, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.authenticationManager, this.successHandler, this.failureHandler);
    }

    @Test
    public void filterWhenConvertErrorThenServerError() {
        Mockito.when(this.authenticationConverter.convert(ArgumentMatchers.any())).thenReturn(Mono.error(new RuntimeException("Unexpected")));
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        client.get().uri("/").exchange().expectStatus().is5xxServerError().expectBody().isEmpty();
        Mockito.verify(this.securityContextRepository, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.authenticationManager, this.successHandler, this.failureHandler);
    }

    @Test
    public void filterWhenConvertAndAuthenticationSuccessThenSuccess() {
        Mono<Authentication> authentication = Mono.just(new TestingAuthenticationToken("test", "this", "ROLE_USER"));
        Mockito.when(this.authenticationConverter.convert(ArgumentMatchers.any())).thenReturn(authentication);
        Mockito.when(this.authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(authentication);
        Mockito.when(this.successHandler.onAuthenticationSuccess(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        Mockito.when(this.securityContextRepository.save(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(( a) -> Mono.just(a.getArguments()[0]));
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        client.get().uri("/").exchange().expectStatus().isOk().expectBody().isEmpty();
        Mockito.verify(this.successHandler).onAuthenticationSuccess(ArgumentMatchers.any(), ArgumentMatchers.eq(authentication.block()));
        Mockito.verify(this.securityContextRepository).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.failureHandler);
    }

    @Test
    public void filterWhenConvertAndAuthenticationEmptyThenServerError() {
        Mono<Authentication> authentication = Mono.just(new TestingAuthenticationToken("test", "this", "ROLE_USER"));
        Mockito.when(this.authenticationConverter.convert(ArgumentMatchers.any())).thenReturn(authentication);
        Mockito.when(this.authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(Mono.empty());
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        client.get().uri("/").exchange().expectStatus().is5xxServerError().expectBody().isEmpty();
        Mockito.verify(this.securityContextRepository, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.successHandler, this.failureHandler);
    }

    @Test
    public void filterWhenNotMatchAndConvertAndAuthenticationSuccessThenContinues() {
        this.filter.setRequiresAuthenticationMatcher(( e) -> ServerWebExchangeMatcher.MatchResult.notMatch());
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        EntityExchangeResult<String> result = client.get().uri("/").headers(( headers) -> headers.setBasicAuth("test", "this")).exchange().expectStatus().isOk().expectBody(String.class).consumeWith(( b) -> assertThat(b.getResponseBody()).isEqualTo("ok")).returnResult();
        assertThat(result.getResponseCookies()).isEmpty();
        Mockito.verifyZeroInteractions(this.authenticationConverter, this.authenticationManager, this.successHandler);
    }

    @Test
    public void filterWhenConvertAndAuthenticationFailThenEntryPoint() {
        Mono<Authentication> authentication = Mono.just(new TestingAuthenticationToken("test", "this", "ROLE_USER"));
        Mockito.when(this.authenticationConverter.convert(ArgumentMatchers.any())).thenReturn(authentication);
        Mockito.when(this.authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(Mono.error(new BadCredentialsException("Failed")));
        Mockito.when(this.failureHandler.onAuthenticationFailure(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mono.empty());
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        client.get().uri("/").exchange().expectStatus().isOk().expectBody().isEmpty();
        Mockito.verify(this.failureHandler).onAuthenticationFailure(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(this.securityContextRepository, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.successHandler);
    }

    @Test
    public void filterWhenConvertAndAuthenticationExceptionThenServerError() {
        Mono<Authentication> authentication = Mono.just(new TestingAuthenticationToken("test", "this", "ROLE_USER"));
        Mockito.when(this.authenticationConverter.convert(ArgumentMatchers.any())).thenReturn(authentication);
        Mockito.when(this.authenticationManager.authenticate(ArgumentMatchers.any())).thenReturn(Mono.error(new RuntimeException("Failed")));
        WebTestClient client = WebTestClientBuilder.bindToWebFilters(this.filter).build();
        client.get().uri("/").exchange().expectStatus().is5xxServerError().expectBody().isEmpty();
        Mockito.verify(this.securityContextRepository, Mockito.never()).save(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(this.successHandler, this.failureHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setRequiresAuthenticationMatcherWhenNullThenException() {
        this.filter.setRequiresAuthenticationMatcher(null);
    }
}

