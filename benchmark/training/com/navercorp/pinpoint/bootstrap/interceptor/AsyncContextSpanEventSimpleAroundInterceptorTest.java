/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.interceptor;


import AsyncContext.ASYNC_TRACE_SCOPE;
import com.navercorp.pinpoint.bootstrap.async.AsyncContextAccessor;
import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.common.trace.ServiceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncContextSpanEventSimpleAroundInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private Trace trace;

    @Mock
    private MethodDescriptor methodDescriptor;

    @Mock
    private SpanEventRecorder spanEventRecorder;

    @Mock
    private AsyncContextAccessor asyncContextAccessor;

    @Mock
    private AsyncContext asyncContext;

    @Test
    public void propagation_fail() throws Exception {
        AroundInterceptor interceptor = mockSpanAsyncEventSimpleInterceptor(traceContext, methodDescriptor);
        interceptor.before(asyncContextAccessor, null);
        Mockito.verify(asyncContext, Mockito.never()).continueAsyncTraceObject();
        Mockito.verify(asyncContext, Mockito.never()).currentAsyncTraceObject();
    }

    @Test
    public void asyncTraceCreate() throws Exception {
        Mockito.when(asyncContextAccessor._$PINPOINT$_getAsyncContext()).thenReturn(asyncContext);
        Mockito.when(asyncContext.continueAsyncTraceObject()).thenReturn(trace);
        AroundInterceptor interceptor = mockSpanAsyncEventSimpleInterceptor(traceContext, methodDescriptor);
        interceptor.before(asyncContextAccessor, null);
        Mockito.verify(asyncContext).continueAsyncTraceObject();
        Mockito.verify(trace).getScope(ASYNC_TRACE_SCOPE);
    }

    @Test
    public void nestedAsyncTraceCreate() throws Exception {
        Mockito.when(asyncContextAccessor._$PINPOINT$_getAsyncContext()).thenReturn(asyncContext);
        // when(asyncContext.continueAsyncTraceObject()).thenReturn(trace);
        AroundInterceptor interceptor = mockSpanAsyncEventSimpleInterceptor(traceContext, methodDescriptor);
        interceptor.before(asyncContextAccessor, null);
        Mockito.verify(asyncContext).continueAsyncTraceObject();
        Mockito.verify(spanEventRecorder, Mockito.never()).recordServiceType(ArgumentMatchers.any(ServiceType.class));
    }
}

