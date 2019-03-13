/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.test.context.web;


import ServletTestExecutionListener.ACTIVATE_LISTENER;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * Unit tests for {@link ServletTestExecutionListener}.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 3.2.6
 */
public class ServletTestExecutionListenerTests {
    private static final String SET_UP_OUTSIDE_OF_STEL = "setUpOutsideOfStel";

    private final WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);

    private final MockServletContext mockServletContext = new MockServletContext();

    private final TestContext testContext = Mockito.mock(TestContext.class);

    private final ServletTestExecutionListener.ServletTestExecutionListener listener = new ServletTestExecutionListener.ServletTestExecutionListener();

    @Test
    public void standardApplicationContext() throws Exception {
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(getClass());
        BDDMockito.given(testContext.getApplicationContext()).willReturn(Mockito.mock(ApplicationContext.class));
        listener.beforeTestClass(testContext);
        assertSetUpOutsideOfStelAttributeExists();
        listener.prepareTestInstance(testContext);
        assertSetUpOutsideOfStelAttributeExists();
        listener.beforeTestMethod(testContext);
        assertSetUpOutsideOfStelAttributeExists();
        listener.afterTestMethod(testContext);
        assertSetUpOutsideOfStelAttributeExists();
    }

    @Test
    public void legacyWebTestCaseWithoutExistingRequestAttributes() throws Exception {
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(ServletTestExecutionListenerTests.LegacyWebTestCase.class);
        RequestContextHolder.resetRequestAttributes();
        assertRequestAttributesDoNotExist();
        listener.beforeTestClass(testContext);
        listener.prepareTestInstance(testContext);
        assertRequestAttributesDoNotExist();
        Mockito.verify(testContext, Mockito.times(0)).setAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE, Boolean.TRUE);
        BDDMockito.given(testContext.getAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE)).willReturn(null);
        listener.beforeTestMethod(testContext);
        assertRequestAttributesDoNotExist();
        Mockito.verify(testContext, Mockito.times(0)).setAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE, Boolean.TRUE);
        listener.afterTestMethod(testContext);
        Mockito.verify(testContext, Mockito.times(1)).removeAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE);
        assertRequestAttributesDoNotExist();
    }

    @Test
    public void legacyWebTestCaseWithPresetRequestAttributes() throws Exception {
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(ServletTestExecutionListenerTests.LegacyWebTestCase.class);
        listener.beforeTestClass(testContext);
        assertSetUpOutsideOfStelAttributeExists();
        listener.prepareTestInstance(testContext);
        assertSetUpOutsideOfStelAttributeExists();
        Mockito.verify(testContext, Mockito.times(0)).setAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE, Boolean.TRUE);
        BDDMockito.given(testContext.getAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE)).willReturn(null);
        listener.beforeTestMethod(testContext);
        assertSetUpOutsideOfStelAttributeExists();
        Mockito.verify(testContext, Mockito.times(0)).setAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE, Boolean.TRUE);
        BDDMockito.given(testContext.getAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE)).willReturn(null);
        listener.afterTestMethod(testContext);
        Mockito.verify(testContext, Mockito.times(1)).removeAttribute(RESET_REQUEST_CONTEXT_HOLDER_ATTRIBUTE);
        assertSetUpOutsideOfStelAttributeExists();
    }

    @Test
    public void atWebAppConfigTestCaseWithoutExistingRequestAttributes() throws Exception {
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(ServletTestExecutionListenerTests.AtWebAppConfigWebTestCase.class);
        RequestContextHolder.resetRequestAttributes();
        listener.beforeTestClass(testContext);
        assertRequestAttributesDoNotExist();
        assertWebAppConfigTestCase();
    }

    @Test
    public void atWebAppConfigTestCaseWithPresetRequestAttributes() throws Exception {
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(ServletTestExecutionListenerTests.AtWebAppConfigWebTestCase.class);
        listener.beforeTestClass(testContext);
        assertRequestAttributesExist();
        assertWebAppConfigTestCase();
    }

    /**
     *
     *
     * @since 4.3
     */
    @Test
    public void activateListenerWithoutExistingRequestAttributes() throws Exception {
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(ServletTestExecutionListenerTests.NoAtWebAppConfigWebTestCase.class);
        BDDMockito.given(testContext.getAttribute(ACTIVATE_LISTENER)).willReturn(true);
        RequestContextHolder.resetRequestAttributes();
        listener.beforeTestClass(testContext);
        assertRequestAttributesDoNotExist();
        assertWebAppConfigTestCase();
    }

    static class LegacyWebTestCase {}

    @WebAppConfiguration
    static class AtWebAppConfigWebTestCase {}

    static class NoAtWebAppConfigWebTestCase {}
}

