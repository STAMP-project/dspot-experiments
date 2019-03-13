/**
 * Copyright 2016 Pinpoint contributors and NAVER Corp.
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
package com.navercorp.pinpoint.plugin.jboss;


import Header.HTTP_FLAGS;
import Header.HTTP_PARENT_SPAN_ID;
import Header.HTTP_SAMPLED;
import Header.HTTP_SPAN_ID;
import Header.HTTP_TRACE_ID;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.plugin.jboss.interceptor.StandardHostValveInvokeInterceptor;
import com.navercorp.pinpoint.profiler.context.DefaultMethodDescriptor;
import com.navercorp.pinpoint.profiler.context.id.DefaultTraceId;
import com.navercorp.pinpoint.profiler.context.module.DefaultApplicationContext;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * The Class InvokeMethodInterceptorTest.
 *
 * @author emeroad
 */
public class InvokeMethodInterceptorTest {
    /**
     * The request.
     */
    @Mock
    public HttpServletRequest request;

    /**
     * The response.
     */
    @Mock
    public HttpServletResponse response;

    /**
     * The descriptor.
     */
    private final MethodDescriptor descriptor = new DefaultMethodDescriptor("org.apache.catalina.core.StandardHostValve", "invoke", new String[]{ "org.apache.catalina.connector.Request", "org.apache.catalina.connector.Response" }, new String[]{ "request", "response" });

    private DefaultApplicationContext applicationContext;

    /**
     * Test header not exists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHeaderNOTExists() {
        Mockito.when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        Mockito.when(request.getHeader(HTTP_TRACE_ID.toString())).thenReturn(null);
        Mockito.when(request.getHeader(HTTP_PARENT_SPAN_ID.toString())).thenReturn(null);
        Mockito.when(request.getHeader(HTTP_SPAN_ID.toString())).thenReturn(null);
        Mockito.when(request.getHeader(HTTP_SAMPLED.toString())).thenReturn(null);
        Mockito.when(request.getHeader(HTTP_FLAGS.toString())).thenReturn(null);
        final Enumeration<?> enumeration = Mockito.mock(Enumeration.class);
        Mockito.when(request.getParameterNames()).thenReturn(((Enumeration<String>) (enumeration)));
        TraceContext traceContext = spyTraceContext();
        final StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, descriptor);
        interceptor.before("target", new Object[]{ request, response });
        interceptor.after("target", new Object[]{ request, response }, new Object(), null);
        Mockito.verify(traceContext, Mockito.times(1)).newAsyncTraceObject();
        interceptor.before("target", new Object[]{ request, response });
        interceptor.after("target", new Object[]{ request, response }, new Object(), null);
        Mockito.verify(traceContext, Mockito.times(2)).newAsyncTraceObject();
    }

    /**
     * Test invalid header exists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidHeaderExists() {
        Mockito.when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        Mockito.when(request.getHeader(HTTP_TRACE_ID.toString())).thenReturn("TRACEID");
        Mockito.when(request.getHeader(HTTP_PARENT_SPAN_ID.toString())).thenReturn("PARENTSPANID");
        Mockito.when(request.getHeader(HTTP_SPAN_ID.toString())).thenReturn("SPANID");
        Mockito.when(request.getHeader(HTTP_SAMPLED.toString())).thenReturn("false");
        Mockito.when(request.getHeader(HTTP_FLAGS.toString())).thenReturn("0");
        final Enumeration<?> enumeration = Mockito.mock(Enumeration.class);
        Mockito.when(request.getParameterNames()).thenReturn(((Enumeration<String>) (enumeration)));
        TraceContext traceContext = spyTraceContext();
        final StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, descriptor);
        interceptor.before("target", new Object[]{ request, response });
        interceptor.after("target", new Object[]{ request, response }, new Object(), null);
        Mockito.verify(traceContext, Mockito.never()).newTraceObject();
        Mockito.verify(traceContext, Mockito.never()).disableSampling();
        Mockito.verify(traceContext, Mockito.never()).continueTraceObject(ArgumentMatchers.any(TraceId.class));
        interceptor.before("target", new Object[]{ request, response });
        interceptor.after("target", new Object[]{ request, response }, new Object(), null);
        Mockito.verify(traceContext, Mockito.never()).newTraceObject();
        Mockito.verify(traceContext, Mockito.never()).disableSampling();
        Mockito.verify(traceContext, Mockito.never()).continueTraceObject(ArgumentMatchers.any(TraceId.class));
    }

    /**
     * Test valid header exists.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testValidHeaderExists() {
        Mockito.when(request.getRequestURI()).thenReturn("/hellotest.nhn");
        Mockito.when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        TraceId traceId = new DefaultTraceId("agentTest", System.currentTimeMillis(), 1);
        Mockito.when(request.getHeader(HTTP_TRACE_ID.toString())).thenReturn(traceId.getTransactionId());
        Mockito.when(request.getHeader(HTTP_PARENT_SPAN_ID.toString())).thenReturn("PARENTSPANID");
        Mockito.when(request.getHeader(HTTP_SPAN_ID.toString())).thenReturn("SPANID");
        Mockito.when(request.getHeader(HTTP_SAMPLED.toString())).thenReturn("false");
        Mockito.when(request.getHeader(HTTP_FLAGS.toString())).thenReturn("0");
        final Enumeration<?> enumeration = Mockito.mock(Enumeration.class);
        Mockito.when(request.getParameterNames()).thenReturn(((Enumeration<String>) (enumeration)));
        TraceContext traceContext = spyTraceContext();
        final StandardHostValveInvokeInterceptor interceptor = new StandardHostValveInvokeInterceptor(traceContext, descriptor);
        interceptor.before("target", new Object[]{ request, response });
        interceptor.after("target", new Object[]{ request, response }, new Object(), null);
        Mockito.verify(traceContext, Mockito.times(1)).continueAsyncTraceObject(ArgumentMatchers.any(TraceId.class));
        interceptor.before("target", new Object[]{ request, response });
        interceptor.after("target", new Object[]{ request, response }, new Object(), null);
        Mockito.verify(traceContext, Mockito.times(2)).continueAsyncTraceObject(ArgumentMatchers.any(TraceId.class));
    }
}

