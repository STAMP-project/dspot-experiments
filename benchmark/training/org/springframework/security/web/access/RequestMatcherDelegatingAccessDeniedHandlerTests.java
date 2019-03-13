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
package org.springframework.security.web.access;


import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 *
 *
 * @author Josh Cummings
 */
public class RequestMatcherDelegatingAccessDeniedHandlerTests {
    private RequestMatcherDelegatingAccessDeniedHandler delegator;

    private LinkedHashMap<RequestMatcher, AccessDeniedHandler> deniedHandlers;

    private AccessDeniedHandler accessDeniedHandler;

    private HttpServletRequest request;

    @Test
    public void handleWhenNothingMatchesThenOnlyDefaultHandlerInvoked() throws Exception {
        AccessDeniedHandler handler = Mockito.mock(AccessDeniedHandler.class);
        RequestMatcher matcher = Mockito.mock(RequestMatcher.class);
        Mockito.when(matcher.matches(this.request)).thenReturn(false);
        this.deniedHandlers.put(matcher, handler);
        this.delegator = new RequestMatcherDelegatingAccessDeniedHandler(this.deniedHandlers, this.accessDeniedHandler);
        this.delegator.handle(this.request, null, null);
        Mockito.verify(this.accessDeniedHandler).handle(this.request, null, null);
        Mockito.verify(handler, Mockito.never()).handle(this.request, null, null);
    }

    @Test
    public void handleWhenFirstMatchesThenOnlyFirstInvoked() throws Exception {
        AccessDeniedHandler firstHandler = Mockito.mock(AccessDeniedHandler.class);
        RequestMatcher firstMatcher = Mockito.mock(RequestMatcher.class);
        AccessDeniedHandler secondHandler = Mockito.mock(AccessDeniedHandler.class);
        RequestMatcher secondMatcher = Mockito.mock(RequestMatcher.class);
        Mockito.when(firstMatcher.matches(this.request)).thenReturn(true);
        this.deniedHandlers.put(firstMatcher, firstHandler);
        this.deniedHandlers.put(secondMatcher, secondHandler);
        this.delegator = new RequestMatcherDelegatingAccessDeniedHandler(this.deniedHandlers, this.accessDeniedHandler);
        this.delegator.handle(this.request, null, null);
        Mockito.verify(firstHandler).handle(this.request, null, null);
        Mockito.verify(secondHandler, Mockito.never()).handle(this.request, null, null);
        Mockito.verify(this.accessDeniedHandler, Mockito.never()).handle(this.request, null, null);
        Mockito.verify(secondMatcher, Mockito.never()).matches(this.request);
    }

    @Test
    public void handleWhenSecondMatchesThenOnlySecondInvoked() throws Exception {
        AccessDeniedHandler firstHandler = Mockito.mock(AccessDeniedHandler.class);
        RequestMatcher firstMatcher = Mockito.mock(RequestMatcher.class);
        AccessDeniedHandler secondHandler = Mockito.mock(AccessDeniedHandler.class);
        RequestMatcher secondMatcher = Mockito.mock(RequestMatcher.class);
        Mockito.when(firstMatcher.matches(this.request)).thenReturn(false);
        Mockito.when(secondMatcher.matches(this.request)).thenReturn(true);
        this.deniedHandlers.put(firstMatcher, firstHandler);
        this.deniedHandlers.put(secondMatcher, secondHandler);
        this.delegator = new RequestMatcherDelegatingAccessDeniedHandler(this.deniedHandlers, this.accessDeniedHandler);
        this.delegator.handle(this.request, null, null);
        Mockito.verify(secondHandler).handle(this.request, null, null);
        Mockito.verify(firstHandler, Mockito.never()).handle(this.request, null, null);
        Mockito.verify(this.accessDeniedHandler, Mockito.never()).handle(this.request, null, null);
    }
}

