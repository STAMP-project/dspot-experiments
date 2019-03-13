package com.navercorp.pinpoint.plugin.hbase.interceptor;


import HbasePluginConstants.HBASE_CLIENT;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import java.net.InetSocketAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbaseClientMethodInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void doInBeforeTrace() {
        Object target = new Object();
        Object[] args = new Object[]{  };
        HbaseClientMethodInterceptor interceptor = new HbaseClientMethodInterceptor(traceContext, descriptor);
        interceptor.doInBeforeTrace(recorder, target, args);
        Mockito.verify(recorder).recordServiceType(HBASE_CLIENT);
    }

    @Test
    public void doInAfterTrace() {
        Object target = new Object();
        Object[] args = new Object[]{ null, null, null, null, null, InetSocketAddress.createUnresolved("localhost", 1234), null };
        HbaseClientMethodInterceptor interceptor = new HbaseClientMethodInterceptor(traceContext, descriptor);
        interceptor.doInAfterTrace(recorder, target, args, null, null);
        Mockito.verify(recorder).recordEndPoint("localhost");
        Mockito.verify(recorder).recordDestinationId("HBASE");
        Mockito.verify(recorder).recordApi(descriptor);
        Mockito.verify(recorder).recordException(null);
    }
}

