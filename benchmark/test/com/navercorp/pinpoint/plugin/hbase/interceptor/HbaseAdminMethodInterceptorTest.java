package com.navercorp.pinpoint.plugin.hbase.interceptor;


import HbasePluginConstants.HBASE_CLIENT_ADMIN;
import HbasePluginConstants.HBASE_CLIENT_PARAMS;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbaseAdminMethodInterceptorTest {
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
        HbaseAdminMethodInterceptor interceptor = new HbaseAdminMethodInterceptor(traceContext, descriptor, true);
        interceptor.doInBeforeTrace(recorder, target, args);
        Mockito.verify(recorder).recordServiceType(HBASE_CLIENT_ADMIN);
    }

    @Test
    public void doInAfterTrace() {
        Object target = new Object();
        Object[] args = new Object[]{ "test" };
        HbaseAdminMethodInterceptor interceptor = new HbaseAdminMethodInterceptor(traceContext, descriptor, true);
        interceptor.doInAfterTrace(recorder, target, args, null, null);
        Mockito.verify(recorder).recordAttribute(HBASE_CLIENT_PARAMS, "[test]");
        Mockito.verify(recorder).recordApi(descriptor);
        Mockito.verify(recorder).recordException(null);
    }
}

