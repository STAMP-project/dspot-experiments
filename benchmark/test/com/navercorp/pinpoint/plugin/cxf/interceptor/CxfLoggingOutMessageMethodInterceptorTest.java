package com.navercorp.pinpoint.plugin.cxf.interceptor;


import CxfPluginConstants.CXF_CONTENT_TYPE;
import CxfPluginConstants.CXF_ENCODING;
import CxfPluginConstants.CXF_HEADERS;
import CxfPluginConstants.CXF_LOGGING_IN_SERVICE_TYPE;
import CxfPluginConstants.CXF_LOGGING_OUT_SERVICE_TYPE;
import CxfPluginConstants.CXF_RESPONSE_CODE;
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
public class CxfLoggingOutMessageMethodInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void doInBeforeTrace() {
        LoggingMessage message = new LoggingMessage("", "1");
        message.getEncoding().append("UTF-8");
        message.getContentType().append("application/json");
        message.getResponseCode().append("200");
        message.getHeader().append("test");
        Object target = new Object();
        Object[] args = new Object[]{ message };
        CxfLoggingOutMessageMethodInterceptor outMessageMethodInterceptor = new CxfLoggingOutMessageMethodInterceptor(traceContext, descriptor);
        outMessageMethodInterceptor.doInBeforeTrace(recorder, target, args);
        Mockito.verify(recorder).recordServiceType(CXF_LOGGING_OUT_SERVICE_TYPE);
        Mockito.verify(recorder).recordAttribute(CXF_ENCODING, "UTF-8");
        Mockito.verify(recorder).recordAttribute(CXF_RESPONSE_CODE, "200");
        Mockito.verify(recorder).recordAttribute(CXF_CONTENT_TYPE, "application/json");
        Mockito.verify(recorder).recordAttribute(CXF_HEADERS, "test");
    }

    @Test
    public void doInAfterTrace() {
        LoggingMessage message = new LoggingMessage("", "1");
        message.getEncoding().append("UTF-8");
        message.getContentType().append("application/json");
        message.getResponseCode().append("200");
        message.getHeader().append("test");
        Object target = new Object();
        Object[] args = new Object[]{ message };
        CxfLoggingOutMessageMethodInterceptor outMessageMethodInterceptor = new CxfLoggingOutMessageMethodInterceptor(traceContext, descriptor);
        outMessageMethodInterceptor.doInAfterTrace(recorder, target, args, null, null);
        Mockito.verify(recorder, Mockito.never()).recordServiceType(CXF_LOGGING_IN_SERVICE_TYPE);
        Mockito.verify(recorder).recordApi(descriptor);
    }
}

