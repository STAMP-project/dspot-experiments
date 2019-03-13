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
package org.springframework.security.config.annotation.method.configuration;


import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.util.context.Context;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class EnableReactiveMethodSecurityTests {
    @Autowired
    ReactiveMessageService messageService;

    ReactiveMessageService delegate;

    TestPublisher<String> result = TestPublisher.create();

    Context withAdmin = ReactiveSecurityContextHolder.withAuthentication(new TestingAuthenticationToken("admin", "password", "ROLE_USER", "ROLE_ADMIN"));

    Context withUser = ReactiveSecurityContextHolder.withAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));

    @Test
    public void notPublisherPreAuthorizeFindByIdThenThrowsIllegalStateException() {
        assertThatThrownBy(() -> this.messageService.notPublisherPreAuthorizeFindById(1L)).isInstanceOf(IllegalStateException.class).extracting(Throwable::getMessage).isEqualTo("The returnType class java.lang.String on public abstract java.lang.String org.springframework.security.config.annotation.method.configuration.ReactiveMessageService.notPublisherPreAuthorizeFindById(long) must return an instance of org.reactivestreams.Publisher (i.e. Mono / Flux) in order to support Reactor Context");
    }

    @Test
    public void monoWhenPermitAllThenAopDoesNotSubscribe() {
        Mockito.when(this.delegate.monoFindById(1L)).thenReturn(Mono.from(result));
        this.delegate.monoFindById(1L);
        result.assertNoSubscribers();
    }

    @Test
    public void monoWhenPermitAllThenSuccess() {
        Mockito.when(this.delegate.monoFindById(1L)).thenReturn(Mono.just("success"));
        StepVerifier.create(this.delegate.monoFindById(1L)).expectNext("success").verifyComplete();
    }

    @Test
    public void monoPreAuthorizeHasRoleWhenGrantedThenSuccess() {
        Mockito.when(this.delegate.monoPreAuthorizeHasRoleFindById(1L)).thenReturn(Mono.just("result"));
        Mono<String> findById = this.messageService.monoPreAuthorizeHasRoleFindById(1L).subscriberContext(withAdmin);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void monoPreAuthorizeHasRoleWhenNoAuthenticationThenDenied() {
        Mockito.when(this.delegate.monoPreAuthorizeHasRoleFindById(1L)).thenReturn(Mono.from(result));
        Mono<String> findById = this.messageService.monoPreAuthorizeHasRoleFindById(1L);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void monoPreAuthorizeHasRoleWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.monoPreAuthorizeHasRoleFindById(1L)).thenReturn(Mono.from(result));
        Mono<String> findById = this.messageService.monoPreAuthorizeHasRoleFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void monoPreAuthorizeBeanWhenGrantedThenSuccess() {
        Mockito.when(this.delegate.monoPreAuthorizeBeanFindById(2L)).thenReturn(Mono.just("result"));
        Mono<String> findById = this.messageService.monoPreAuthorizeBeanFindById(2L).subscriberContext(withAdmin);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void monoPreAuthorizeBeanWhenNotAuthenticatedAndGrantedThenSuccess() {
        Mockito.when(this.delegate.monoPreAuthorizeBeanFindById(2L)).thenReturn(Mono.just("result"));
        Mono<String> findById = this.messageService.monoPreAuthorizeBeanFindById(2L);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void monoPreAuthorizeBeanWhenNoAuthenticationThenDenied() {
        Mockito.when(this.delegate.monoPreAuthorizeBeanFindById(1L)).thenReturn(Mono.from(result));
        Mono<String> findById = this.messageService.monoPreAuthorizeBeanFindById(1L);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void monoPreAuthorizeBeanWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.monoPreAuthorizeBeanFindById(1L)).thenReturn(Mono.from(result));
        Mono<String> findById = this.messageService.monoPreAuthorizeBeanFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void monoPostAuthorizeWhenAuthorizedThenSuccess() {
        Mockito.when(this.delegate.monoPostAuthorizeFindById(1L)).thenReturn(Mono.just("user"));
        Mono<String> findById = this.messageService.monoPostAuthorizeFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectNext("user").verifyComplete();
    }

    @Test
    public void monoPostAuthorizeWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.monoPostAuthorizeBeanFindById(1L)).thenReturn(Mono.just("not-authorized"));
        Mono<String> findById = this.messageService.monoPostAuthorizeBeanFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
    }

    @Test
    public void monoPostAuthorizeWhenBeanAndAuthorizedThenSuccess() {
        Mockito.when(this.delegate.monoPostAuthorizeBeanFindById(2L)).thenReturn(Mono.just("user"));
        Mono<String> findById = this.messageService.monoPostAuthorizeBeanFindById(2L).subscriberContext(withUser);
        StepVerifier.create(findById).expectNext("user").verifyComplete();
    }

    @Test
    public void monoPostAuthorizeWhenBeanAndNotAuthenticatedAndAuthorizedThenSuccess() {
        Mockito.when(this.delegate.monoPostAuthorizeBeanFindById(2L)).thenReturn(Mono.just("anonymous"));
        Mono<String> findById = this.messageService.monoPostAuthorizeBeanFindById(2L);
        StepVerifier.create(findById).expectNext("anonymous").verifyComplete();
    }

    @Test
    public void monoPostAuthorizeWhenBeanAndNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.monoPostAuthorizeBeanFindById(1L)).thenReturn(Mono.just("not-authorized"));
        Mono<String> findById = this.messageService.monoPostAuthorizeBeanFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
    }

    // Flux tests
    @Test
    public void fluxWhenPermitAllThenAopDoesNotSubscribe() {
        Mockito.when(this.delegate.fluxFindById(1L)).thenReturn(Flux.from(result));
        this.delegate.fluxFindById(1L);
        result.assertNoSubscribers();
    }

    @Test
    public void fluxWhenPermitAllThenSuccess() {
        Mockito.when(this.delegate.fluxFindById(1L)).thenReturn(Flux.just("success"));
        StepVerifier.create(this.delegate.fluxFindById(1L)).expectNext("success").verifyComplete();
    }

    @Test
    public void fluxPreAuthorizeHasRoleWhenGrantedThenSuccess() {
        Mockito.when(this.delegate.fluxPreAuthorizeHasRoleFindById(1L)).thenReturn(Flux.just("result"));
        Flux<String> findById = this.messageService.fluxPreAuthorizeHasRoleFindById(1L).subscriberContext(withAdmin);
        StepVerifier.create(findById).consumeNextWith(( s) -> AssertionsForClassTypes.assertThat(s).isEqualTo("result")).verifyComplete();
    }

    @Test
    public void fluxPreAuthorizeHasRoleWhenNoAuthenticationThenDenied() {
        Mockito.when(this.delegate.fluxPreAuthorizeHasRoleFindById(1L)).thenReturn(Flux.from(result));
        Flux<String> findById = this.messageService.fluxPreAuthorizeHasRoleFindById(1L);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void fluxPreAuthorizeHasRoleWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.fluxPreAuthorizeHasRoleFindById(1L)).thenReturn(Flux.from(result));
        Flux<String> findById = this.messageService.fluxPreAuthorizeHasRoleFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void fluxPreAuthorizeBeanWhenGrantedThenSuccess() {
        Mockito.when(this.delegate.fluxPreAuthorizeBeanFindById(2L)).thenReturn(Flux.just("result"));
        Flux<String> findById = this.messageService.fluxPreAuthorizeBeanFindById(2L).subscriberContext(withAdmin);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void fluxPreAuthorizeBeanWhenNotAuthenticatedAndGrantedThenSuccess() {
        Mockito.when(this.delegate.fluxPreAuthorizeBeanFindById(2L)).thenReturn(Flux.just("result"));
        Flux<String> findById = this.messageService.fluxPreAuthorizeBeanFindById(2L);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void fluxPreAuthorizeBeanWhenNoAuthenticationThenDenied() {
        Mockito.when(this.delegate.fluxPreAuthorizeBeanFindById(1L)).thenReturn(Flux.from(result));
        Flux<String> findById = this.messageService.fluxPreAuthorizeBeanFindById(1L);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void fluxPreAuthorizeBeanWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.fluxPreAuthorizeBeanFindById(1L)).thenReturn(Flux.from(result));
        Flux<String> findById = this.messageService.fluxPreAuthorizeBeanFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void fluxPostAuthorizeWhenAuthorizedThenSuccess() {
        Mockito.when(this.delegate.fluxPostAuthorizeFindById(1L)).thenReturn(Flux.just("user"));
        Flux<String> findById = this.messageService.fluxPostAuthorizeFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectNext("user").verifyComplete();
    }

    @Test
    public void fluxPostAuthorizeWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.fluxPostAuthorizeBeanFindById(1L)).thenReturn(Flux.just("not-authorized"));
        Flux<String> findById = this.messageService.fluxPostAuthorizeBeanFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
    }

    @Test
    public void fluxPostAuthorizeWhenBeanAndAuthorizedThenSuccess() {
        Mockito.when(this.delegate.fluxPostAuthorizeBeanFindById(2L)).thenReturn(Flux.just("user"));
        Flux<String> findById = this.messageService.fluxPostAuthorizeBeanFindById(2L).subscriberContext(withUser);
        StepVerifier.create(findById).expectNext("user").verifyComplete();
    }

    @Test
    public void fluxPostAuthorizeWhenBeanAndNotAuthenticatedAndAuthorizedThenSuccess() {
        Mockito.when(this.delegate.fluxPostAuthorizeBeanFindById(2L)).thenReturn(Flux.just("anonymous"));
        Flux<String> findById = this.messageService.fluxPostAuthorizeBeanFindById(2L);
        StepVerifier.create(findById).expectNext("anonymous").verifyComplete();
    }

    @Test
    public void fluxPostAuthorizeWhenBeanAndNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.fluxPostAuthorizeBeanFindById(1L)).thenReturn(Flux.just("not-authorized"));
        Flux<String> findById = this.messageService.fluxPostAuthorizeBeanFindById(1L).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
    }

    // Publisher tests
    @Test
    public void publisherWhenPermitAllThenAopDoesNotSubscribe() {
        Mockito.when(this.delegate.publisherFindById(1L)).thenReturn(result);
        this.delegate.publisherFindById(1L);
        result.assertNoSubscribers();
    }

    @Test
    public void publisherWhenPermitAllThenSuccess() {
        Mockito.when(this.delegate.publisherFindById(1L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("success"));
        StepVerifier.create(this.delegate.publisherFindById(1L)).expectNext("success").verifyComplete();
    }

    @Test
    public void publisherPreAuthorizeHasRoleWhenGrantedThenSuccess() {
        Mockito.when(this.delegate.publisherPreAuthorizeHasRoleFindById(1L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("result"));
        Publisher<String> findById = Flux.from(this.messageService.publisherPreAuthorizeHasRoleFindById(1L)).subscriberContext(withAdmin);
        StepVerifier.create(findById).consumeNextWith(( s) -> AssertionsForClassTypes.assertThat(s).isEqualTo("result")).verifyComplete();
    }

    @Test
    public void publisherPreAuthorizeHasRoleWhenNoAuthenticationThenDenied() {
        Mockito.when(this.delegate.publisherPreAuthorizeHasRoleFindById(1L)).thenReturn(result);
        Publisher<String> findById = this.messageService.publisherPreAuthorizeHasRoleFindById(1L);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void publisherPreAuthorizeHasRoleWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.publisherPreAuthorizeHasRoleFindById(1L)).thenReturn(result);
        Publisher<String> findById = Flux.from(this.messageService.publisherPreAuthorizeHasRoleFindById(1L)).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void publisherPreAuthorizeBeanWhenGrantedThenSuccess() {
        Mockito.when(this.delegate.publisherPreAuthorizeBeanFindById(2L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("result"));
        Publisher<String> findById = Flux.from(this.messageService.publisherPreAuthorizeBeanFindById(2L)).subscriberContext(withAdmin);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void publisherPreAuthorizeBeanWhenNotAuthenticatedAndGrantedThenSuccess() {
        Mockito.when(this.delegate.publisherPreAuthorizeBeanFindById(2L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("result"));
        Publisher<String> findById = this.messageService.publisherPreAuthorizeBeanFindById(2L);
        StepVerifier.create(findById).expectNext("result").verifyComplete();
    }

    @Test
    public void publisherPreAuthorizeBeanWhenNoAuthenticationThenDenied() {
        Mockito.when(this.delegate.publisherPreAuthorizeBeanFindById(1L)).thenReturn(result);
        Publisher<String> findById = this.messageService.publisherPreAuthorizeBeanFindById(1L);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void publisherPreAuthorizeBeanWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.publisherPreAuthorizeBeanFindById(1L)).thenReturn(result);
        Publisher<String> findById = Flux.from(this.messageService.publisherPreAuthorizeBeanFindById(1L)).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
        result.assertNoSubscribers();
    }

    @Test
    public void publisherPostAuthorizeWhenAuthorizedThenSuccess() {
        Mockito.when(this.delegate.publisherPostAuthorizeFindById(1L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("user"));
        Publisher<String> findById = Flux.from(this.messageService.publisherPostAuthorizeFindById(1L)).subscriberContext(withUser);
        StepVerifier.create(findById).expectNext("user").verifyComplete();
    }

    @Test
    public void publisherPostAuthorizeWhenNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.publisherPostAuthorizeBeanFindById(1L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("not-authorized"));
        Publisher<String> findById = Flux.from(this.messageService.publisherPostAuthorizeBeanFindById(1L)).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
    }

    @Test
    public void publisherPostAuthorizeWhenBeanAndAuthorizedThenSuccess() {
        Mockito.when(this.delegate.publisherPostAuthorizeBeanFindById(2L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("user"));
        Publisher<String> findById = Flux.from(this.messageService.publisherPostAuthorizeBeanFindById(2L)).subscriberContext(withUser);
        StepVerifier.create(findById).expectNext("user").verifyComplete();
    }

    @Test
    public void publisherPostAuthorizeWhenBeanAndNotAuthenticatedAndAuthorizedThenSuccess() {
        Mockito.when(this.delegate.publisherPostAuthorizeBeanFindById(2L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("anonymous"));
        Publisher<String> findById = this.messageService.publisherPostAuthorizeBeanFindById(2L);
        StepVerifier.create(findById).expectNext("anonymous").verifyComplete();
    }

    @Test
    public void publisherPostAuthorizeWhenBeanAndNotAuthorizedThenDenied() {
        Mockito.when(this.delegate.publisherPostAuthorizeBeanFindById(1L)).thenReturn(EnableReactiveMethodSecurityTests.publisherJust("not-authorized"));
        Publisher<String> findById = Flux.from(this.messageService.publisherPostAuthorizeBeanFindById(1L)).subscriberContext(withUser);
        StepVerifier.create(findById).expectError(AccessDeniedException.class).verify();
    }

    @EnableReactiveMethodSecurity
    static class Config {
        ReactiveMessageService delegate = Mockito.mock(ReactiveMessageService.class);

        @Bean
        public DelegatingReactiveMessageService defaultMessageService() {
            return new DelegatingReactiveMessageService(delegate);
        }

        @Bean
        public Authz authz() {
            return new Authz();
        }
    }
}

