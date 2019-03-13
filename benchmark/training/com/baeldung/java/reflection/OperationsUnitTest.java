package com.baeldung.java.reflection;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OperationsUnitTest {
    public OperationsUnitTest() {
    }

    @Test(expected = IllegalAccessException.class)
    public void givenObject_whenInvokePrivateMethod_thenFail() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
        Method andPrivateMethod = Operations.class.getDeclaredMethod("privateAnd", boolean.class, boolean.class);
        Operations operationsInstance = new Operations();
        Boolean result = ((Boolean) (andPrivateMethod.invoke(operationsInstance, true, false)));
        Assert.assertFalse(result);
    }

    @Test
    public void givenObject_whenInvokePrivateMethod_thenCorrect() throws Exception {
        Method andPrivatedMethod = Operations.class.getDeclaredMethod("privateAnd", boolean.class, boolean.class);
        andPrivatedMethod.setAccessible(true);
        Operations operationsInstance = new Operations();
        Boolean result = ((Boolean) (andPrivatedMethod.invoke(operationsInstance, true, false)));
        Assert.assertFalse(result);
    }

    @Test
    public void givenObject_whenInvokePublicMethod_thenCorrect() throws Exception {
        Method sumInstanceMethod = Operations.class.getMethod("publicSum", int.class, double.class);
        Operations operationsInstance = new Operations();
        Double result = ((Double) (sumInstanceMethod.invoke(operationsInstance, 1, 3)));
        Assert.assertThat(result, CoreMatchers.equalTo(4.0));
    }

    @Test
    public void givenObject_whenInvokeStaticMethod_thenCorrect() throws Exception {
        Method multiplyStaticMethod = Operations.class.getDeclaredMethod("publicStaticMultiply", float.class, long.class);
        Double result = ((Double) (multiplyStaticMethod.invoke(null, 3.5F, 2)));
        Assert.assertThat(result, CoreMatchers.equalTo(7.0));
    }
}

