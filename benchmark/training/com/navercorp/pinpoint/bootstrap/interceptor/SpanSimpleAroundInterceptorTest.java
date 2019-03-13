/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.interceptor;


import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpanSimpleAroundInterceptorTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void lifeCycle() throws Exception {
        Trace trace = newTrace();
        TraceContext context = newTraceContext(trace);
        SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor interceptor = new SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor(context);
        checkSpanInterceptor(context, interceptor);
    }

    @Test
    public void beforeExceptionLifeCycle() throws Exception {
        Trace trace = newTrace();
        TraceContext context = newTraceContext(trace);
        SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor interceptor = new SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor(context) {
            @Override
            protected void doInBeforeTrace(SpanRecorder trace, Object target, Object[] args) {
                touchBefore();
                throw new RuntimeException();
            }
        };
        checkSpanInterceptor(context, interceptor);
    }

    @Test
    public void afterExceptionLifeCycle() throws Exception {
        Trace trace = newTrace();
        TraceContext context = newTraceContext(trace);
        SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor interceptor = new SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor(context) {
            @Override
            protected void doInAfterTrace(SpanRecorder trace, Object target, Object[] args, Object result, Throwable throwable) {
                touchAfter();
                throw new RuntimeException();
            }
        };
        checkSpanInterceptor(context, interceptor);
    }

    @Test
    public void beforeAfterExceptionLifeCycle() throws Exception {
        Trace trace = newTrace();
        TraceContext context = newTraceContext(trace);
        SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor interceptor = new SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor(context) {
            @Override
            protected void doInBeforeTrace(SpanRecorder recorder, Object target, Object[] args) {
                touchBefore();
                throw new RuntimeException();
            }

            @Override
            protected void doInAfterTrace(SpanRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
                touchAfter();
                throw new RuntimeException();
            }
        };
        checkSpanInterceptor(context, interceptor);
    }

    @Test
    public void traceCreateFail() {
        TraceContext context = Mockito.mock(TraceContext.class);
        Mockito.when(context.newTraceObject()).thenReturn(null);
        SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor interceptor = new SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor(context);
        checkTraceCreateFailInterceptor(context, interceptor);
    }

    public static class TestSpanSimpleAroundInterceptor extends SpanSimpleAroundInterceptor {
        private int beforeTouchCount;

        private int afterTouchCount;

        public TestSpanSimpleAroundInterceptor(TraceContext traceContext) {
            super(traceContext, null, SpanSimpleAroundInterceptorTest.TestSpanSimpleAroundInterceptor.class);
        }

        @Override
        protected Trace createTrace(Object target, Object[] args) {
            return traceContext.newTraceObject();
        }

        @Override
        protected void doInBeforeTrace(SpanRecorder recorder, Object target, Object[] args) {
            touchBefore();
        }

        protected void touchBefore() {
            (beforeTouchCount)++;
        }

        public int getAfterTouchCount() {
            return afterTouchCount;
        }

        @Override
        protected void doInAfterTrace(SpanRecorder recorder, Object target, Object[] args, Object result, Throwable throwable) {
            touchAfter();
        }

        protected void touchAfter() {
            (afterTouchCount)++;
        }

        public int getBeforeTouchCount() {
            return beforeTouchCount;
        }

        @Override
        protected void deleteTrace(Trace trace, Object target, Object[] args, Object result, Throwable throwable) {
        }
    }
}

