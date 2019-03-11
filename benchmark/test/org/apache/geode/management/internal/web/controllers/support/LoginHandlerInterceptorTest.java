/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.web.controllers.support;


import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import org.apache.geode.internal.security.SecurityService;
import org.apache.geode.test.junit.rules.ConcurrencyRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.servlet.HandlerInterceptor;


public class LoginHandlerInterceptorTest {
    @Mock
    private SecurityService securityService;

    private HandlerInterceptor interceptor;

    @Rule
    public TestName name = new TestName();

    @Rule
    public ConcurrencyRule runConcurrently = new ConcurrencyRule();

    @Test
    public void beforeFirstCallToPreHandle_environmentIsEmpty() {
        assertThat(LoginHandlerInterceptor.getEnvironment()).isEmpty();
    }

    @Test
    public void preHandle_createsNewEnvironmentInstance() throws Exception {
        HttpServletRequest request = LoginHandlerInterceptorTest.RequestBuilder.request().build();
        Map<String, String> environmentBeforePreHandle = LoginHandlerInterceptor.getEnvironment();
        interceptor.preHandle(request, null, null);
        assertThat(LoginHandlerInterceptor.getEnvironment()).isNotSameAs(environmentBeforePreHandle);
    }

    @Test
    public void preHandle_copiesPrefixedRequestParametersIntoEnvironment() throws Exception {
        HttpServletRequest request = LoginHandlerInterceptorTest.RequestBuilder.request().withParameter(((LoginHandlerInterceptor.ENVIRONMENT_VARIABLE_REQUEST_PARAMETER_PREFIX) + "prefixed"), "prefixed value").withParameter("not-prefixed", "not-prefixed value").build();
        interceptor.preHandle(request, null, null);
        assertThat(LoginHandlerInterceptor.getEnvironment()).containsOnly(entry("prefixed", "prefixed value")).doesNotContain(entry("not-prefixed", "not-prefixed value"));
    }

    @Test
    public void preHandle_returnsTrue() throws Exception {
        HttpServletRequest request = LoginHandlerInterceptorTest.RequestBuilder.request().build();
        assertThat(interceptor.preHandle(request, null, null)).isTrue();
    }

    @Test
    public void preHandle_logsInWithUserNameAndPasswordFromRequestHeaders() throws Exception {
        HttpServletRequest request = LoginHandlerInterceptorTest.RequestBuilder.request().withHeader(USER_NAME, "expected user-name").withHeader(PASSWORD, "expected password").build();
        interceptor.preHandle(request, null, null);
        Properties expectedLoginProperties = new Properties();
        expectedLoginProperties.setProperty(USER_NAME, "expected user-name");
        expectedLoginProperties.setProperty(PASSWORD, "expected password");
        Mockito.verify(securityService, Mockito.times(1)).login(expectedLoginProperties);
    }

    @Test
    public void afterCompletion_clearsTheEnvironment() throws Exception {
        HttpServletRequest request = LoginHandlerInterceptorTest.RequestBuilder.request().withParameter(((LoginHandlerInterceptor.ENVIRONMENT_VARIABLE_REQUEST_PARAMETER_PREFIX) + "variable"), "value").build();
        // Call preHandle() to put values into the environment
        interceptor.preHandle(request, null, null);
        interceptor.afterCompletion(request, null, null, null);
        assertThat(LoginHandlerInterceptor.getEnvironment()).isEmpty();
    }

    @Test
    public void afterCompletion_logsOut() throws Exception {
        HttpServletRequest request = LoginHandlerInterceptorTest.RequestBuilder.request().build();
        interceptor.afterCompletion(request, null, null, null);
        Mockito.verify(securityService, Mockito.times(1)).logout();
    }

    @Test
    public void eachRequestThreadEnvironmentIsConfinedToItsThread() {
        Semaphore thread1Permit = new Semaphore(0);
        Semaphore thread2Permit = new Semaphore(0);
        Callable<Void> request1Task = () -> processRequest("thread 1", thread1Permit, thread2Permit);
        Callable<Void> request2Task = () -> processRequest("thread 2", thread2Permit, thread1Permit);
        runConcurrently.setTimeout(Duration.ofMinutes(1));
        runConcurrently.add(request1Task);
        runConcurrently.add(request2Task);
        thread1Permit.release();
        runConcurrently.executeInParallel();
    }

    static class RequestBuilder {
        private final Map<String, String> parameters = new HashMap<>();

        private final Map<String, String> headers = new HashMap<>();

        private String name = "request";

        static LoginHandlerInterceptorTest.RequestBuilder request() {
            return new LoginHandlerInterceptorTest.RequestBuilder();
        }

        HttpServletRequest build() {
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class, name);
            headers.keySet().forEach(( k) -> Mockito.when(request.getHeader(k)).thenReturn(headers.get(k)));
            Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(parameters.keySet()));
            parameters.keySet().forEach(( k) -> Mockito.when(request.getParameter(k)).thenReturn(parameters.get(k)));
            return request;
        }

        LoginHandlerInterceptorTest.RequestBuilder named(String name) {
            this.name = name;
            return this;
        }

        LoginHandlerInterceptorTest.RequestBuilder withHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        LoginHandlerInterceptorTest.RequestBuilder withParameter(String name, String value) {
            parameters.put(name, value);
            return this;
        }
    }
}

