/**
 * Created on  13-09-23 09:27
 */
package com.alicp.jetcache.anno.aop;


import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.support.CacheContext;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import java.lang.reflect.Method;
import java.sql.SQLException;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class JetCacheInterceptorTest {
    private CachePointcut pc;

    private JetCacheInterceptor interceptor;

    private GlobalCacheConfig globalCacheConfig;

    interface I1 {
        @Cached
        int foo();
    }

    class C1 implements JetCacheInterceptorTest.I1 {
        public int foo() {
            return 0;
        }
    }

    @Test
    public void test1() throws Throwable {
        final Method m = JetCacheInterceptorTest.I1.class.getMethod("foo");
        final JetCacheInterceptorTest.C1 c = new JetCacheInterceptorTest.C1();
        pc.matches(m, JetCacheInterceptorTest.C1.class);
        final MethodInvocation mi = Mockito.mock(MethodInvocation.class);
        Mockito.when(mi.getMethod()).thenReturn(m);
        Mockito.when(mi.getThis()).thenReturn(c);
        Mockito.when(mi.getArguments()).thenReturn(null);
        Mockito.when(mi.proceed()).thenReturn(100);
        interceptor.invoke(mi);
        interceptor.invoke(mi);
        Mockito.verify(mi, Mockito.times(1)).proceed();
        globalCacheConfig.setEnableMethodCache(false);
        interceptor.invoke(mi);
        Mockito.verify(mi, Mockito.times(2)).proceed();
    }

    interface I2 {
        @Cached(enabled = false)
        int foo();
    }

    class C2 implements JetCacheInterceptorTest.I2 {
        public int foo() {
            return 0;
        }
    }

    @Test
    public void test2() throws Throwable {
        final Method m = JetCacheInterceptorTest.I2.class.getMethod("foo");
        final JetCacheInterceptorTest.C2 c = new JetCacheInterceptorTest.C2();
        pc.matches(m, JetCacheInterceptorTest.C2.class);
        final MethodInvocation mi = Mockito.mock(MethodInvocation.class);
        Mockito.when(mi.getMethod()).thenReturn(m);
        Mockito.when(mi.getThis()).thenReturn(c);
        Mockito.when(mi.getArguments()).thenReturn(null);
        Mockito.when(mi.proceed()).thenReturn(100);
        interceptor.invoke(mi);
        interceptor.invoke(mi);
        CacheContext.enableCache(() -> {
            try {
                interceptor.invoke(mi);
                interceptor.invoke(mi);
                return null;
            } catch ( e) {
                fail(e);
                return null;
            }
        });
        Mockito.verify(mi, Mockito.times(3)).proceed();
    }

    interface I3 {
        @Cached
        int foo() throws SQLException;
    }

    class C3 implements JetCacheInterceptorTest.I3 {
        public int foo() {
            return 0;
        }
    }

    @Test
    public void test3() throws Throwable {
        final Method m = JetCacheInterceptorTest.I3.class.getMethod("foo");
        final JetCacheInterceptorTest.C3 c = new JetCacheInterceptorTest.C3();
        pc.matches(m, JetCacheInterceptorTest.C3.class);
        final MethodInvocation mi = Mockito.mock(MethodInvocation.class);
        Mockito.when(mi.getMethod()).thenReturn(m);
        Mockito.when(mi.getThis()).thenReturn(c);
        Mockito.when(mi.getArguments()).thenReturn(null);
        Mockito.when(mi.proceed()).thenThrow(new SQLException(""));
        try {
            interceptor.invoke(mi);
            Assertions.fail("");
        } catch (SQLException e) {
        }
    }
}

