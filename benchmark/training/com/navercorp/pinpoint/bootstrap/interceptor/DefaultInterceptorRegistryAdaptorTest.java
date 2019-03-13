package com.navercorp.pinpoint.bootstrap.interceptor;


import com.navercorp.pinpoint.bootstrap.interceptor.registry.DefaultInterceptorRegistryAdaptor;
import com.navercorp.pinpoint.bootstrap.interceptor.registry.InterceptorRegistryAdaptor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultInterceptorRegistryAdaptorTest {
    @Test
    public void indexSize_0() {
        try {
            new DefaultInterceptorRegistryAdaptor((-1));
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void indexSize_1() {
        try {
            InterceptorRegistryAdaptor interceptorRegistry = new DefaultInterceptorRegistryAdaptor(0);
            StaticAroundInterceptor mock = Mockito.mock(StaticAroundInterceptor.class);
            interceptorRegistry.addInterceptor(mock);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void indexSize_2() {
        InterceptorRegistryAdaptor interceptorRegistry = new DefaultInterceptorRegistryAdaptor(1);
        interceptorRegistry.addInterceptor(Mockito.mock(StaticAroundInterceptor.class));
        try {
            interceptorRegistry.addInterceptor(Mockito.mock(StaticAroundInterceptor.class));
            Assert.fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void addStaticInterceptor() {
        StaticAroundInterceptor mock = Mockito.mock(StaticAroundInterceptor.class);
        InterceptorRegistryAdaptor registry = new DefaultInterceptorRegistryAdaptor();
        int key = registry.addInterceptor(mock);
        Interceptor find = registry.getInterceptor(key);
        Assert.assertSame(mock, find);
    }

    @Test
    public void addSimpleInterceptor() {
        AroundInterceptor mock = Mockito.mock(AroundInterceptor.class);
        InterceptorRegistryAdaptor registry = new DefaultInterceptorRegistryAdaptor();
        int key = registry.addInterceptor(mock);
        Interceptor find = registry.getInterceptor(key);
        Assert.assertSame(mock, find);
    }
}

