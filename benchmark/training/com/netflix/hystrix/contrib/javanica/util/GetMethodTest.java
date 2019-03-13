package com.netflix.hystrix.contrib.javanica.util;


import com.google.common.base.Optional;
import com.netflix.hystrix.contrib.javanica.utils.MethodProvider;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by dmgcodevil.
 */
@RunWith(Parameterized.class)
public class GetMethodTest {
    private String methodName;

    private Class<?>[] parametersTypes;

    public GetMethodTest(String methodName, Class<?>[] parametersTypes) {
        this.methodName = methodName;
        this.parametersTypes = parametersTypes;
    }

    @Test
    public void testGetMethodFoo() {
        Optional<Method> method = MethodProvider.getInstance().getMethod(GetMethodTest.C.class, methodName, parametersTypes);
        Assert.assertTrue(method.isPresent());
        Assert.assertEquals(methodName, method.get().getName());
    }

    public static class A {
        void foo(String in) {
        }
    }

    public static class B extends GetMethodTest.A {
        void bar(Integer in) {
        }
    }

    public static class C extends GetMethodTest.B {}
}

