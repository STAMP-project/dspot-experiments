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
package org.springframework.security.authorization;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedReactiveAuthorizationManagerTests {
    @Mock
    Authentication authentication;

    AuthenticatedReactiveAuthorizationManager<Object> manager = AuthenticatedReactiveAuthorizationManager.authenticated();

    @Test
    public void checkWhenAuthenticatedThenReturnTrue() {
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        boolean granted = manager.check(Mono.just(authentication), null).block().isGranted();
        assertThat(granted).isTrue();
    }

    @Test
    public void checkWhenNotAuthenticatedThenReturnFalse() {
        boolean granted = manager.check(Mono.just(authentication), null).block().isGranted();
        assertThat(granted).isFalse();
    }

    @Test
    public void checkWhenEmptyThenReturnFalse() {
        boolean granted = manager.check(Mono.empty(), null).block().isGranted();
        assertThat(granted).isFalse();
    }

    @Test
    public void checkWhenAnonymousAuthenticatedThenReturnFalse() {
        AnonymousAuthenticationToken anonymousAuthenticationToken = Mockito.mock(AnonymousAuthenticationToken.class);
        boolean granted = manager.check(Mono.just(anonymousAuthenticationToken), null).block().isGranted();
        assertThat(granted).isFalse();
    }

    @Test
    public void checkWhenErrorThenError() {
        Mono<AuthorizationDecision> result = manager.check(Mono.error(new RuntimeException("ooops")), null);
        StepVerifier.create(result).expectError().verify();
    }
}

