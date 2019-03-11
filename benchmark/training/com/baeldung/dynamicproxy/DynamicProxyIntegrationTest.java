package com.baeldung.dynamicproxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DynamicProxyIntegrationTest {
    @Test
    public void givenDynamicProxy_thenPutWorks() {
        Map proxyInstance = ((Map) (Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{ Map.class }, new DynamicInvocationHandler())));
        proxyInstance.put("hello", "world");
    }

    @Test
    public void givenInlineDynamicProxy_thenGetWorksOtherMethodsDoNot() {
        Map proxyInstance = ((Map) (Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{ Map.class }, ( proxy, method, methodArgs) -> {
            if (method.getName().equals("get")) {
                return 42;
            } else {
                throw new UnsupportedOperationException(("Unsupported method: " + (method.getName())));
            }
        })));
        int result = ((int) (proxyInstance.get("hello")));
        Assert.assertEquals(42, result);
        try {
            proxyInstance.put("hello", "world");
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void givenTimingDynamicProxy_thenMethodInvokationsProduceTiming() {
        Map mapProxyInstance = ((Map) (Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{ Map.class }, new TimingDynamicInvocationHandler(new HashMap()))));
        mapProxyInstance.put("hello", "world");
        Assert.assertEquals("world", mapProxyInstance.get("hello"));
        CharSequence csProxyInstance = ((CharSequence) (Proxy.newProxyInstance(DynamicProxyIntegrationTest.class.getClassLoader(), new Class[]{ CharSequence.class }, new TimingDynamicInvocationHandler("Hello World"))));
        Assert.assertEquals('l', csProxyInstance.charAt(2));
        Assert.assertEquals(11, csProxyInstance.length());
    }
}

