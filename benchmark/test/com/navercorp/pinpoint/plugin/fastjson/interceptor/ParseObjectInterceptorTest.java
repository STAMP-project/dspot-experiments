package com.navercorp.pinpoint.plugin.fastjson.interceptor;


import FastjsonConstants.ANNOTATION_KEY_JSON_LENGTH;
import FastjsonConstants.SERVICE_TYPE;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ParseObjectInterceptorTest {
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
        ParseObjectInterceptor interceptor = new ParseObjectInterceptor(traceContext, descriptor);
        interceptor.before(null, null);
    }

    @Test
    public void after1() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ParseObjectInterceptor interceptor = new ParseObjectInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{ "{\"firstName\": \"Json\"}" }, null, null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, "{\"firstName\": \"Json\"}".length());
    }

    @Test
    public void after2() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ParseObjectInterceptor interceptor = new ParseObjectInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{ new byte[]{ 1 } }, null, null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, new byte[]{ 1 }.length);
    }

    @Test
    public void after3() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ParseObjectInterceptor interceptor = new ParseObjectInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{ new char[]{ '1' } }, null, null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, new char[]{ '1' }.length);
    }

    @Test
    public void after4() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).currentSpanEventRecorder();
        ParseObjectInterceptor interceptor = new ParseObjectInterceptor(traceContext, descriptor);
        interceptor.after(null, new Object[]{ new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public int available() throws IOException {
                return 1;
            }
        } }, null, null);
        Mockito.verify(recorder).recordServiceType(SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(ANNOTATION_KEY_JSON_LENGTH, 1);
    }
}

