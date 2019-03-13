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
public class ToJavaObjectInterceptorTest {
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
        ToJavaObjectInterceptor interceptor = new ToJavaObjectInterceptor(traceContext, descriptor);
        interceptor.before(null, null);
    }

    @Test
    public void after() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ToJavaObjectInterceptor interceptor = new ToJavaObjectInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{  }, "{\"firstName\": \"Json\"}", null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, "{\"firstName\": \"Json\"}".hashCode());
    }
}

