package com.navercorp.pinpoint.plugin.hbase.interceptor;


import HbasePluginConstants.HBASE_CLIENT_PARAMS;
import HbasePluginConstants.HBASE_CLIENT_TABLE;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbaseTableMethodInterceptorTest {
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
        HbaseTableMethodInterceptor interceptor = new HbaseTableMethodInterceptor(traceContext, descriptor, true);
        interceptor.doInBeforeTrace(recorder, target, args);
        Mockito.verify(recorder).recordServiceType(HBASE_CLIENT_TABLE);
    }

    @Test
    public void doInAfterTrace() {
        Object target = new Object();
        Object[] args = new Object[]{ Collections.singletonList("test") };
        HbaseTableMethodInterceptor interceptor = new HbaseTableMethodInterceptor(traceContext, descriptor, true);
        interceptor.doInAfterTrace(recorder, target, args, null, null);
        Mockito.verify(recorder).recordAttribute(HBASE_CLIENT_PARAMS, "size: 1");
        Mockito.verify(recorder).recordApi(descriptor);
        Mockito.verify(recorder).recordException(null);
    }
}

