package com.navercorp.pinpoint.plugin.hbase.interceptor;


import HbasePluginConstants.HBASE_ASYNC_CLIENT;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScopeInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbaseClientMainInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private Trace trace;

    @Mock
    private SpanEventRecorder recorder;

    @Mock
    private InterceptorScope scope;

    @Mock
    private AsyncContext asyncContext;

    @Mock
    private InterceptorScopeInvocation invocation;

    @Test
    public void before() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Mockito.doReturn(asyncContext).when(recorder).recordNextAsyncContext();
        Mockito.doReturn(invocation).when(scope).getCurrentInvocation();
        Object target = new Object();
        Object[] args = new Object[]{ "foo", "bar" };
        HbaseClientMainInterceptor interceptor = new HbaseClientMainInterceptor(traceContext, descriptor, scope);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(HBASE_ASYNC_CLIENT);
        Mockito.verify(recorder).recordApi(descriptor, args);
    }

    @Test
    public void after() {
        Mockito.doReturn(trace).when(traceContext).currentTraceObject();
        Object target = new Object();
        Object[] args = new Object[]{ "foo", "bar" };
        HbaseClientMainInterceptor interceptor = new HbaseClientMainInterceptor(traceContext, descriptor, scope);
        interceptor.after(target, args, null, null);
    }
}

