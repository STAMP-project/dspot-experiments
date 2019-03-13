package com.navercorp.pinpoint.plugin.cxf.interceptor;


import CxfPluginConstants.CXF_ADDRESS;
import CxfPluginConstants.CXF_CLIENT_SERVICE_TYPE;
import CxfPluginConstants.CXF_CONTENT_TYPE;
import CxfPluginConstants.CXF_HTTP_METHOD;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CxfClientHandleMessageMethodInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private ProfilerConfig profilerConfig;

    @Mock
    private Trace trace;

    @Mock
    private TraceId traceId;

    @Mock
    private TraceId nextId;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void test1() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Object target = new Object();
        Map map = new HashMap();
        map.put("org.apache.cxf.message.Message.ENDPOINT_ADDRESS", "http://foo.com/getFoo");
        map.put("org.apache.cxf.request.uri", "http://foo.com/getFoo");
        map.put("org.apache.cxf.request.method", "POST");
        map.put("Content-Type", "application/json");
        Object[] args = new Object[]{ map };
        CxfClientHandleMessageMethodInterceptor interceptor = new CxfClientHandleMessageMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(CXF_CLIENT_SERVICE_TYPE);
        Mockito.verify(recorder).recordDestinationId("http://foo.com");
        Mockito.verify(recorder).recordAttribute(CXF_ADDRESS, "http://foo.com/getFoo");
        Mockito.verify(recorder).recordAttribute(CXF_HTTP_METHOD, "POST");
        Mockito.verify(recorder).recordAttribute(CXF_CONTENT_TYPE, "application/json");
    }

    @Test
    public void test2() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Object target = new Object();
        Object[] args = new Object[]{ "" };
        CxfClientHandleMessageMethodInterceptor interceptor = new CxfClientHandleMessageMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(trace, Mockito.never()).traceBlockBegin();
    }

    @Test
    public void test3() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Object target = new Object();
        Map map = new HashMap();
        map.put("org.apache.cxf.message.Message.ENDPOINT_ADDRESS", "http://foo.com/getFoo");
        Object[] args = new Object[]{ map };
        CxfClientHandleMessageMethodInterceptor interceptor = new CxfClientHandleMessageMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(CXF_CLIENT_SERVICE_TYPE);
        Mockito.verify(recorder).recordDestinationId("http://foo.com");
        Mockito.verify(recorder).recordAttribute(CXF_ADDRESS, "unknown");
        Mockito.verify(recorder).recordAttribute(CXF_HTTP_METHOD, "unknown");
        Mockito.verify(recorder).recordAttribute(CXF_CONTENT_TYPE, "unknown");
    }
}

