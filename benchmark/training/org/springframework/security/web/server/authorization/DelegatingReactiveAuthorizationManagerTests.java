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
package org.springframework.security.web.server.authorization;


import ServerWebExchangeMatcher.MatchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegatingReactiveAuthorizationManagerTests {
    @Mock
    ServerWebExchangeMatcher match1;

    @Mock
    ServerWebExchangeMatcher match2;

    @Mock
    AuthorityReactiveAuthorizationManager<AuthorizationContext> delegate1;

    @Mock
    AuthorityReactiveAuthorizationManager<AuthorizationContext> delegate2;

    @Mock
    ServerWebExchange exchange;

    @Mock
    Mono<Authentication> authentication;

    @Mock
    AuthorizationDecision decision;

    DelegatingReactiveAuthorizationManager manager;

    @Test
    public void checkWhenFirstMatchesThenNoMoreMatchersAndNoMoreDelegatesInvoked() {
        Mockito.when(match1.matches(ArgumentMatchers.any())).thenReturn(MatchResult.match());
        Mockito.when(delegate1.check(ArgumentMatchers.eq(authentication), ArgumentMatchers.any(AuthorizationContext.class))).thenReturn(Mono.just(decision));
        assertThat(manager.check(authentication, exchange).block()).isEqualTo(decision);
        Mockito.verifyZeroInteractions(match2, delegate2);
    }

    @Test
    public void checkWhenSecondMatchesThenNoMoreMatchersAndNoMoreDelegatesInvoked() {
        Mockito.when(match1.matches(ArgumentMatchers.any())).thenReturn(MatchResult.notMatch());
        Mockito.when(match2.matches(ArgumentMatchers.any())).thenReturn(MatchResult.match());
        Mockito.when(delegate2.check(ArgumentMatchers.eq(authentication), ArgumentMatchers.any(AuthorizationContext.class))).thenReturn(Mono.just(decision));
        assertThat(manager.check(authentication, exchange).block()).isEqualTo(decision);
        Mockito.verifyZeroInteractions(delegate1);
    }
}

