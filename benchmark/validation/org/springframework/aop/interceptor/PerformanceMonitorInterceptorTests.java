/**
 * Copyright 2002-2013 the original author or authors.
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


import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 *
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Chris Beams
 */
public class PerformanceMonitorInterceptorTests {
    @Test
    public void testSuffixAndPrefixAssignment() {
        PerformanceMonitorInterceptor interceptor = new PerformanceMonitorInterceptor();
        Assert.assertNotNull(interceptor.getPrefix());
        Assert.assertNotNull(interceptor.getSuffix());
        interceptor.setPrefix(null);
        interceptor.setSuffix(null);
        Assert.assertNotNull(interceptor.getPrefix());
        Assert.assertNotNull(interceptor.getSuffix());
    }

    @Test
    public void testSunnyDayPathLogsPerformanceMetricsCorrectly() throws Throwable {
        MethodInvocation mi = Mockito.mock(MethodInvocation.class);
        BDDMockito.given(mi.getMethod()).willReturn(String.class.getMethod("toString", new Class[0]));
        Log log = Mockito.mock(Log.class);
        PerformanceMonitorInterceptor interceptor = new PerformanceMonitorInterceptor(true);
        interceptor.invokeUnderTrace(mi, log);
        Mockito.verify(log).trace(ArgumentMatchers.anyString());
    }

    @Test
    public void testExceptionPathStillLogsPerformanceMetricsCorrectly() throws Throwable {
        MethodInvocation mi = Mockito.mock(MethodInvocation.class);
        BDDMockito.given(mi.getMethod()).willReturn(String.class.getMethod("toString", new Class[0]));
        BDDMockito.given(mi.proceed()).willThrow(new IllegalArgumentException());
        Log log = Mockito.mock(Log.class);
        PerformanceMonitorInterceptor interceptor = new PerformanceMonitorInterceptor(true);
        try {
            interceptor.invokeUnderTrace(mi, log);
            Assert.fail("Must have propagated the IllegalArgumentException.");
        } catch (IllegalArgumentException expected) {
        }
        Mockito.verify(log).trace(ArgumentMatchers.anyString());
    }
}

