package com.navercorp.pinpoint.plugin.cxf.interceptor;


import CxfPluginConstants.CXF_ADDRESS;
import CxfPluginConstants.CXF_CONTENT_TYPE;
import CxfPluginConstants.CXF_HEADERS;
import CxfPluginConstants.CXF_HTTP_METHOD;
import CxfPluginConstants.CXF_LOGGING_IN_SERVICE_TYPE;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import org.apache.cxf.interceptor.LoggingMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CxfLoggingInMessageMethodInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void doInBeforeTrace() {
        LoggingMessage message = new LoggingMessage("", "1");
        message.getAddress().append("http://foo.com/getFoo");
        message.getContentType().append("application/json");
        message.getHttpMethod().append("POST");
        message.getHeader().append("test");
        Object target = new Object();
        Object[] args = new Object[]{ message };
        CxfLoggingInMessageMethodInterceptor inMessageMethodInterceptor = new CxfLoggingInMessageMethodInterceptor(traceContext, descriptor);
        inMessageMethodInterceptor.doInBeforeTrace(recorder, target, args);
        Mockito.verify(recorder).recordServiceType(CXF_LOGGING_IN_SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(CXF_ADDRESS, "http://foo.com/getFoo");
        Mockito.verify(recorder).recordAttribute(CXF_HTTP_METHOD, "POST");
        Mockito.verify(recorder).recordAttribute(CXF_CONTENT_TYPE, "application/json");
        Mockito.verify(recorder).recordAttribute(CXF_HEADERS, "test");
    }

    @Test
    public void doInAfterTrace() {
        LoggingMessage message = new LoggingMessage("", "1");
        message.getAddress().append("http://foo.com/getFoo");
        message.getContentType().append("application/json");
        message.getHttpMethod().append("POST");
        message.getHeader().append("test");
        Object target = new Object();
        Object[] args = new Object[]{ message };
        CxfLoggingInMessageMethodInterceptor inMessageMethodInterceptor = new CxfLoggingInMessageMethodInterceptor(traceContext, descriptor);
        inMessageMethodInterceptor.doInAfterTrace(recorder, target, args, null, null);
        Mockito.verify(recorder, Mockito.never()).recordServiceType(CXF_LOGGING_IN_SERVICE_TYPE);
        Mockito.verify(recorder).recordApi(descriptor);
    }
}

