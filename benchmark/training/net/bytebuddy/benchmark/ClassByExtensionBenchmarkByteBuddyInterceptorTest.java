package net.bytebuddy.benchmark;


import ClassByExtensionBenchmark.ByteBuddyAccessInterceptor;
import ClassByExtensionBenchmark.ByteBuddyProxyInterceptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;


public class ClassByExtensionBenchmarkByteBuddyInterceptorTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testProxyInterceptor() throws Exception {
        Constructor<?> constructor = ByteBuddyProxyInterceptor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((UnsupportedOperationException) (exception.getCause()));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAccessorInterceptor() throws Exception {
        Constructor<?> constructor = ByteBuddyAccessInterceptor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((UnsupportedOperationException) (exception.getCause()));
        }
    }
}

