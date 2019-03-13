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
 * Unit tests for the {@link DebugInterceptor} class.
 *
 * @author Rick Evans
 * @author Chris Beams
 */
public class DebugInterceptorTests {
    @Test
    public void testSunnyDayPathLogsCorrectly() throws Throwable {
        MethodInvocation methodInvocation = Mockito.mock(MethodInvocation.class);
        Log log = Mockito.mock(Log.class);
        BDDMockito.given(log.isTraceEnabled()).willReturn(true);
        DebugInterceptor interceptor = new DebugInterceptorTests.StubDebugInterceptor(log);
        interceptor.invoke(methodInvocation);
        checkCallCountTotal(interceptor);
        Mockito.verify(log, Mockito.times(2)).trace(ArgumentMatchers.anyString());
    }

    @Test
    public void testExceptionPathStillLogsCorrectly() throws Throwable {
        MethodInvocation methodInvocation = Mockito.mock(MethodInvocation.class);
        IllegalArgumentException exception = new IllegalArgumentException();
        BDDMockito.given(methodInvocation.proceed()).willThrow(exception);
        Log log = Mockito.mock(Log.class);
        BDDMockito.given(log.isTraceEnabled()).willReturn(true);
        DebugInterceptor interceptor = new DebugInterceptorTests.StubDebugInterceptor(log);
        try {
            interceptor.invoke(methodInvocation);
            Assert.fail("Must have propagated the IllegalArgumentException.");
        } catch (IllegalArgumentException expected) {
        }
        checkCallCountTotal(interceptor);
        Mockito.verify(log).trace(ArgumentMatchers.anyString());
        Mockito.verify(log).trace(ArgumentMatchers.anyString(), ArgumentMatchers.eq(exception));
    }

    @SuppressWarnings("serial")
    private static final class StubDebugInterceptor extends DebugInterceptor {
        private final Log log;

        public StubDebugInterceptor(Log log) {
            super(true);
            this.log = log;
        }

        @Override
        protected Log getLoggerForInvocation(MethodInvocation invocation) {
            return log;
        }
    }
}

