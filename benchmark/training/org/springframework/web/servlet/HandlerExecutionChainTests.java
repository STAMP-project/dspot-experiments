/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.web.servlet;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * A test fixture with HandlerExecutionChain and mock handler interceptors.
 *
 * @author Rossen Stoyanchev
 */
public class HandlerExecutionChainTests {
    private HandlerExecutionChain chain;

    private Object handler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private AsyncHandlerInterceptor interceptor1;

    private AsyncHandlerInterceptor interceptor2;

    private AsyncHandlerInterceptor interceptor3;

    @Test
    public void successScenario() throws Exception {
        ModelAndView mav = new ModelAndView();
        BDDMockito.given(this.interceptor1.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor2.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor3.preHandle(this.request, this.response, this.handler)).willReturn(true);
        this.chain.applyPreHandle(request, response);
        this.chain.applyPostHandle(request, response, mav);
        this.chain.triggerAfterCompletion(this.request, this.response, null);
        Mockito.verify(this.interceptor1).postHandle(this.request, this.response, this.handler, mav);
        Mockito.verify(this.interceptor2).postHandle(this.request, this.response, this.handler, mav);
        Mockito.verify(this.interceptor3).postHandle(this.request, this.response, this.handler, mav);
        Mockito.verify(this.interceptor3).afterCompletion(this.request, this.response, this.handler, null);
        Mockito.verify(this.interceptor2).afterCompletion(this.request, this.response, this.handler, null);
        Mockito.verify(this.interceptor1).afterCompletion(this.request, this.response, this.handler, null);
    }

    @Test
    public void successAsyncScenario() throws Exception {
        BDDMockito.given(this.interceptor1.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor2.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor3.preHandle(this.request, this.response, this.handler)).willReturn(true);
        this.chain.applyPreHandle(request, response);
        this.chain.applyAfterConcurrentHandlingStarted(request, response);
        this.chain.triggerAfterCompletion(this.request, this.response, null);
        Mockito.verify(this.interceptor1).afterConcurrentHandlingStarted(request, response, this.handler);
        Mockito.verify(this.interceptor2).afterConcurrentHandlingStarted(request, response, this.handler);
        Mockito.verify(this.interceptor3).afterConcurrentHandlingStarted(request, response, this.handler);
    }

    @Test
    public void earlyExitInPreHandle() throws Exception {
        BDDMockito.given(this.interceptor1.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor2.preHandle(this.request, this.response, this.handler)).willReturn(false);
        this.chain.applyPreHandle(request, response);
        Mockito.verify(this.interceptor1).afterCompletion(this.request, this.response, this.handler, null);
    }

    @Test
    public void exceptionBeforePreHandle() throws Exception {
        this.chain.triggerAfterCompletion(this.request, this.response, null);
        Mockito.verifyZeroInteractions(this.interceptor1, this.interceptor2, this.interceptor3);
    }

    @Test
    public void exceptionDuringPreHandle() throws Exception {
        Exception ex = new Exception("");
        BDDMockito.given(this.interceptor1.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor2.preHandle(this.request, this.response, this.handler)).willThrow(ex);
        try {
            this.chain.applyPreHandle(request, response);
        } catch (Exception actual) {
            Assert.assertSame(ex, actual);
        }
        this.chain.triggerAfterCompletion(this.request, this.response, ex);
        Mockito.verify(this.interceptor1).afterCompletion(this.request, this.response, this.handler, ex);
        Mockito.verify(this.interceptor3, Mockito.never()).preHandle(this.request, this.response, this.handler);
    }

    @Test
    public void exceptionAfterPreHandle() throws Exception {
        Exception ex = new Exception("");
        BDDMockito.given(this.interceptor1.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor2.preHandle(this.request, this.response, this.handler)).willReturn(true);
        BDDMockito.given(this.interceptor3.preHandle(this.request, this.response, this.handler)).willReturn(true);
        this.chain.applyPreHandle(request, response);
        this.chain.triggerAfterCompletion(this.request, this.response, ex);
        Mockito.verify(this.interceptor3).afterCompletion(this.request, this.response, this.handler, ex);
        Mockito.verify(this.interceptor2).afterCompletion(this.request, this.response, this.handler, ex);
        Mockito.verify(this.interceptor1).afterCompletion(this.request, this.response, this.handler, ex);
    }
}

