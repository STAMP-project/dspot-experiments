package com.navercorp.pinpoint.plugin.hbase.interceptor;


import HbasePluginConstants.HBASE_ASYNC_CLIENT;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbaseClientRunInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void doInAfterTrace() {
        Object target = new Object();
        Object[] args = new Object[]{ "foo", "bar" };
        HbaseClientRunInterceptor interceptor = new HbaseClientRunInterceptor(traceContext, descriptor);
        interceptor.doInAfterTrace(recorder, target, args, null, null);
        Mockito.verify(recorder).recordServiceType(HBASE_ASYNC_CLIENT);
        Mockito.verify(recorder).recordApi(descriptor);
        Mockito.verify(recorder).recordException(null);
    }
}

