package com.navercorp.pinpoint.plugin.hbase.interceptor;


import com.navercorp.pinpoint.bootstrap.async.AsyncContextAccessor;
import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScopeInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbaseClientConstructorInterceptorTest {
    @Mock
    private AsyncContext context;

    @Mock
    private InterceptorScope scope;

    @Mock
    private AsyncContextAccessor target;

    @Mock
    private InterceptorScopeInvocation invocation;

    @Test
    public void after() {
        Mockito.doReturn(invocation).when(scope).getCurrentInvocation();
        Mockito.doReturn(context).when(invocation).getAttachment();
        HbaseClientConstructorInterceptor interceptor = new HbaseClientConstructorInterceptor(scope);
        interceptor.after(target, null, null, null);
    }
}

