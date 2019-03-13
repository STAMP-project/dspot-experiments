/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.aop.interceptor;


import MonitorFactory.EXCEPTIONS_LABEL;
import com.jamonapi.MonitorFactory;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 *
 *
 * @author Steve Souza
 * @since 4.1
 */
public class JamonPerformanceMonitorInterceptorTests {
    private final JamonPerformanceMonitorInterceptor interceptor = new JamonPerformanceMonitorInterceptor();

    private final MethodInvocation mi = Mockito.mock(MethodInvocation.class);

    private final Log log = Mockito.mock(Log.class);

    @Test
    public void testInvokeUnderTraceWithNormalProcessing() throws Throwable {
        BDDMockito.given(mi.getMethod()).willReturn(String.class.getMethod("toString"));
        interceptor.invokeUnderTrace(mi, log);
        Assert.assertTrue("jamon must track the method being invoked", ((MonitorFactory.getNumRows()) > 0));
        Assert.assertTrue("The jamon report must contain the toString method that was invoked", MonitorFactory.getReport().contains("toString"));
    }

    @Test
    public void testInvokeUnderTraceWithExceptionTracking() throws Throwable {
        BDDMockito.given(mi.getMethod()).willReturn(String.class.getMethod("toString"));
        BDDMockito.given(mi.proceed()).willThrow(new IllegalArgumentException());
        try {
            interceptor.invokeUnderTrace(mi, log);
            Assert.fail("Must have propagated the IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals("Monitors must exist for the method invocation and 2 exceptions", 3, MonitorFactory.getNumRows());
        Assert.assertTrue("The jamon report must contain the toString method that was invoked", MonitorFactory.getReport().contains("toString"));
        Assert.assertTrue(("The jamon report must contain the generic exception: " + (MonitorFactory.EXCEPTIONS_LABEL)), MonitorFactory.getReport().contains(EXCEPTIONS_LABEL));
        Assert.assertTrue("The jamon report must contain the specific exception: IllegalArgumentException'", MonitorFactory.getReport().contains("IllegalArgumentException"));
    }
}

