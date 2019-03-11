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
package org.springframework.security.test.context.support;


import WithSecurityContextTestExecutionListener.SECURITY_CONTEXT_ATTR_NAME;
import java.lang.reflect.Method;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import static TestExecutionEvent.TEST_EXECUTION;
import static TestExecutionEvent.TEST_METHOD;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = WithSecurityContextTestExecutionListenerTests.NoOpConfiguration.class)
public class WithSecurityContextTestExecutionListenerTests {
    @ClassRule
    public static final SpringClassRule spring = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethod = new SpringMethodRule();

    @Autowired
    private ApplicationContext applicationContext;

    @Mock
    private TestContext testContext;

    private WithSecurityContextTestExecutionListener listener = new WithSecurityContextTestExecutionListener();

    @Test
    public void beforeTestMethodWhenWithMockUserTestExecutionDefaultThenSecurityContextSet() throws Exception {
        Method testMethod = WithSecurityContextTestExecutionListenerTests.TheTest.class.getMethod("withMockUserDefault");
        Mockito.when(this.testContext.getApplicationContext()).thenReturn(this.applicationContext);
        Mockito.when(this.testContext.getTestMethod()).thenReturn(testMethod);
        this.listener.beforeTestMethod(this.testContext);
        assertThat(TestSecurityContextHolder.getContext().getAuthentication()).isNotNull();
        Mockito.verify(this.testContext, Mockito.never()).setAttribute(ArgumentMatchers.eq(SECURITY_CONTEXT_ATTR_NAME), ArgumentMatchers.any(SecurityContext.class));
    }

    @Test
    public void beforeTestMethodWhenWithMockUserTestMethodThenSecurityContextSet() throws Exception {
        Method testMethod = WithSecurityContextTestExecutionListenerTests.TheTest.class.getMethod("withMockUserTestMethod");
        Mockito.when(this.testContext.getApplicationContext()).thenReturn(this.applicationContext);
        Mockito.when(this.testContext.getTestMethod()).thenReturn(testMethod);
        this.listener.beforeTestMethod(this.testContext);
        assertThat(TestSecurityContextHolder.getContext().getAuthentication()).isNotNull();
        Mockito.verify(this.testContext, Mockito.never()).setAttribute(ArgumentMatchers.eq(SECURITY_CONTEXT_ATTR_NAME), ArgumentMatchers.any(SecurityContext.class));
    }

    @Test
    public void beforeTestMethodWhenWithMockUserTestExecutionThenTestContextSet() throws Exception {
        Method testMethod = WithSecurityContextTestExecutionListenerTests.TheTest.class.getMethod("withMockUserTestExecution");
        Mockito.when(this.testContext.getApplicationContext()).thenReturn(this.applicationContext);
        Mockito.when(this.testContext.getTestMethod()).thenReturn(testMethod);
        this.listener.beforeTestMethod(this.testContext);
        assertThat(TestSecurityContextHolder.getContext().getAuthentication()).isNull();
        Mockito.verify(this.testContext).setAttribute(ArgumentMatchers.eq(SECURITY_CONTEXT_ATTR_NAME), ArgumentMatchers.any(SecurityContext.class));
    }

    @Test
    public void beforeTestExecutionWhenTestContextNullThenSecurityContextNotSet() throws Exception {
        this.listener.beforeTestExecution(this.testContext);
        assertThat(TestSecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void beforeTestExecutionWhenTestContextNotNullThenSecurityContextSet() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken("user", "passsword", "ROLE_USER"));
        Mockito.when(this.testContext.removeAttribute(SECURITY_CONTEXT_ATTR_NAME)).thenReturn(securityContext);
        this.listener.beforeTestExecution(this.testContext);
        assertThat(TestSecurityContextHolder.getContext().getAuthentication()).isEqualTo(securityContext.getAuthentication());
    }

    @Configuration
    static class NoOpConfiguration {}

    static class TheTest {
        @WithMockUser(setupBefore = TEST_EXECUTION)
        public void withMockUserTestExecution() {
        }

        @WithMockUser(setupBefore = TEST_METHOD)
        public void withMockUserTestMethod() {
        }

        @WithMockUser
        public void withMockUserDefault() {
        }
    }
}

