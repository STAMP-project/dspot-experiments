package com.navercorp.pinpoint.plugin.fastjson.interceptor;


import FastjsonConstants.ANNOTATION_KEY_JSON_LENGTH;
import FastjsonConstants.SERVICE_TYPE;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ParseInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private Trace trace;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void before() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        ParseInterceptor interceptor = new ParseInterceptor(traceContext, descriptor);
        interceptor.before(null, null);
    }

    @Test
    public void after1() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ParseInterceptor interceptor = new ParseInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{ "{\"firstName\": \"Json\"}" }, null, null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, "{\"firstName\": \"Json\"}".length());
    }

    @Test
    public void after2() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ParseInterceptor interceptor = new ParseInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{ new byte[]{ 1 } }, null, null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, new byte[]{ 1 }.length);
    }
}

