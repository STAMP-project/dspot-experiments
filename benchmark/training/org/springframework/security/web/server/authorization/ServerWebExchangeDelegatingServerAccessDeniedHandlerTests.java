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
package org.springframework.security.web.server.authorization;


import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static MatchResult.match;
import static MatchResult.notMatch;


public class ServerWebExchangeDelegatingServerAccessDeniedHandlerTests {
    private ServerWebExchangeDelegatingServerAccessDeniedHandler delegator;

    private List<ServerWebExchangeDelegatingServerAccessDeniedHandler.DelegateEntry> entries;

    private ServerAccessDeniedHandler accessDeniedHandler;

    private ServerWebExchange exchange;

    @Test
    public void handleWhenNothingMatchesThenOnlyDefaultHandlerInvoked() {
        ServerAccessDeniedHandler handler = Mockito.mock(ServerAccessDeniedHandler.class);
        ServerWebExchangeMatcher matcher = Mockito.mock(ServerWebExchangeMatcher.class);
        Mockito.when(matcher.matches(this.exchange)).thenReturn(notMatch());
        Mockito.when(handler.handle(this.exchange, null)).thenReturn(Mono.empty());
        Mockito.when(this.accessDeniedHandler.handle(this.exchange, null)).thenReturn(Mono.empty());
        this.entries.add(new ServerWebExchangeDelegatingServerAccessDeniedHandler.DelegateEntry(matcher, handler));
        this.delegator = new ServerWebExchangeDelegatingServerAccessDeniedHandler(this.entries);
        this.delegator.setDefaultAccessDeniedHandler(this.accessDeniedHandler);
        this.delegator.handle(this.exchange, null).block();
        Mockito.verify(this.accessDeniedHandler).handle(this.exchange, null);
        Mockito.verify(handler, Mockito.never()).handle(this.exchange, null);
    }

    @Test
    public void handleWhenFirstMatchesThenOnlyFirstInvoked() {
        ServerAccessDeniedHandler firstHandler = Mockito.mock(ServerAccessDeniedHandler.class);
        ServerWebExchangeMatcher firstMatcher = Mockito.mock(ServerWebExchangeMatcher.class);
        ServerAccessDeniedHandler secondHandler = Mockito.mock(ServerAccessDeniedHandler.class);
        ServerWebExchangeMatcher secondMatcher = Mockito.mock(ServerWebExchangeMatcher.class);
        Mockito.when(firstMatcher.matches(this.exchange)).thenReturn(match());
        Mockito.when(firstHandler.handle(this.exchange, null)).thenReturn(Mono.empty());
        Mockito.when(secondHandler.handle(this.exchange, null)).thenReturn(Mono.empty());
        this.entries.add(new ServerWebExchangeDelegatingServerAccessDeniedHandler.DelegateEntry(firstMatcher, firstHandler));
        this.entries.add(new ServerWebExchangeDelegatingServerAccessDeniedHandler.DelegateEntry(secondMatcher, secondHandler));
        this.delegator = new ServerWebExchangeDelegatingServerAccessDeniedHandler(this.entries);
        this.delegator.setDefaultAccessDeniedHandler(this.accessDeniedHandler);
        this.delegator.handle(this.exchange, null).block();
        Mockito.verify(firstHandler).handle(this.exchange, null);
        Mockito.verify(secondHandler, Mockito.never()).handle(this.exchange, null);
        Mockito.verify(this.accessDeniedHandler, Mockito.never()).handle(this.exchange, null);
        Mockito.verify(secondMatcher, Mockito.never()).matches(this.exchange);
    }

    @Test
    public void handleWhenSecondMatchesThenOnlySecondInvoked() {
        ServerAccessDeniedHandler firstHandler = Mockito.mock(ServerAccessDeniedHandler.class);
        ServerWebExchangeMatcher firstMatcher = Mockito.mock(ServerWebExchangeMatcher.class);
        ServerAccessDeniedHandler secondHandler = Mockito.mock(ServerAccessDeniedHandler.class);
        ServerWebExchangeMatcher secondMatcher = Mockito.mock(ServerWebExchangeMatcher.class);
        Mockito.when(firstMatcher.matches(this.exchange)).thenReturn(notMatch());
        Mockito.when(secondMatcher.matches(this.exchange)).thenReturn(match());
        Mockito.when(firstHandler.handle(this.exchange, null)).thenReturn(Mono.empty());
        Mockito.when(secondHandler.handle(this.exchange, null)).thenReturn(Mono.empty());
        this.entries.add(new ServerWebExchangeDelegatingServerAccessDeniedHandler.DelegateEntry(firstMatcher, firstHandler));
        this.entries.add(new ServerWebExchangeDelegatingServerAccessDeniedHandler.DelegateEntry(secondMatcher, secondHandler));
        this.delegator = new ServerWebExchangeDelegatingServerAccessDeniedHandler(this.entries);
        this.delegator.handle(this.exchange, null).block();
        Mockito.verify(secondHandler).handle(this.exchange, null);
        Mockito.verify(firstHandler, Mockito.never()).handle(this.exchange, null);
        Mockito.verify(this.accessDeniedHandler, Mockito.never()).handle(this.exchange, null);
    }
}

