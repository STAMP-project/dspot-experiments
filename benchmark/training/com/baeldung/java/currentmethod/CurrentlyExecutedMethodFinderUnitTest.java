package com.baeldung.java.currentmethod;


import org.junit.Assert;
import org.junit.Test;


/**
 * The class presents various ways of finding the name of currently executed method.
 */
public class CurrentlyExecutedMethodFinderUnitTest {
    @Test
    public void givenCurrentThread_whenGetStackTrace_thenFindMethod() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Assert.assertEquals("givenCurrentThread_whenGetStackTrace_thenFindMethod", stackTrace[1].getMethodName());
    }

    @Test
    public void givenException_whenGetStackTrace_thenFindMethod() {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        Assert.assertEquals("givenException_whenGetStackTrace_thenFindMethod", methodName);
    }

    @Test
    public void givenThrowable_whenGetStacktrace_thenFindMethod() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Assert.assertEquals("givenThrowable_whenGetStacktrace_thenFindMethod", stackTrace[0].getMethodName());
    }

    @Test
    public void givenObject_whenGetEnclosingMethod_thenFindMethod() {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        Assert.assertEquals("givenObject_whenGetEnclosingMethod_thenFindMethod", methodName);
    }

    @Test
    public void givenLocal_whenGetEnclosingMethod_thenFindMethod() {
        class Local {}
        String methodName = Local.class.getEnclosingMethod().getName();
        Assert.assertEquals("givenLocal_whenGetEnclosingMethod_thenFindMethod", methodName);
    }
}

