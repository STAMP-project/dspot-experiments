package com.taobao.arthas.core.util;


import java.io.Serializable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TypeRenderUtilsTest {
    public class TestClass implements Serializable {
        private int testField;

        public char anotherTestField;

        public int testMethod(int i, boolean b) {
            return 0;
        }

        public void anotherTestMethod() throws NullPointerException {
        }
    }

    @Test
    public void testDrawInterface() {
        Assert.assertThat(TypeRenderUtils.drawInterface(String.class), CoreMatchers.is(CoreMatchers.equalTo("java.io.Serializable,java.lang.Comparable,java.lang.CharSequence")));
        Assert.assertThat(TypeRenderUtils.drawInterface(TypeRenderUtilsTest.TestClass.class), CoreMatchers.is(CoreMatchers.equalTo("java.io.Serializable")));
        Assert.assertThat(TypeRenderUtils.drawInterface(Serializable.class), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test
    public void testDrawParametersForMethod() throws NoSuchMethodException {
        Class[] classesOfParameters = new Class[2];
        classesOfParameters[0] = int.class;
        classesOfParameters[1] = boolean.class;
        Assert.assertThat(TypeRenderUtils.drawParameters(TypeRenderUtilsTest.TestClass.class.getMethod("testMethod", classesOfParameters)), CoreMatchers.is(CoreMatchers.equalTo("int\nboolean")));
        Assert.assertThat(TypeRenderUtils.drawParameters(TypeRenderUtilsTest.TestClass.class.getMethod("anotherTestMethod")), CoreMatchers.is(CoreMatchers.equalTo("")));
        Assert.assertThat(TypeRenderUtils.drawParameters(String.class.getMethod("charAt", int.class)), CoreMatchers.is(CoreMatchers.equalTo("int")));
        Assert.assertThat(TypeRenderUtils.drawParameters(String.class.getMethod("isEmpty")), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testDrawParametersForMethodThrowsException() throws NoSuchMethodException {
        Assert.assertThat(TypeRenderUtils.drawParameters(TypeRenderUtilsTest.TestClass.class.getMethod("method")), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test
    public void testDrawParametersForConstructor() throws NoSuchMethodException {
        Class[] classesOfParameters = new Class[3];
        classesOfParameters[0] = char[].class;
        classesOfParameters[1] = int.class;
        classesOfParameters[2] = int.class;
        Assert.assertThat(TypeRenderUtils.drawParameters(String.class.getConstructor(classesOfParameters)), CoreMatchers.is(CoreMatchers.equalTo("[]\nint\nint")));
        Assert.assertThat(TypeRenderUtils.drawParameters(String.class.getConstructor()), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testDrawParametersForConstructorThrowsException() throws NoSuchMethodException {
        Assert.assertThat(TypeRenderUtils.drawParameters(TypeRenderUtilsTest.TestClass.class.getConstructor()), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test
    public void testDrawReturn() throws NoSuchMethodException {
        Class[] classesOfParameters = new Class[2];
        classesOfParameters[0] = int.class;
        classesOfParameters[1] = boolean.class;
        Assert.assertThat(TypeRenderUtils.drawReturn(TypeRenderUtilsTest.TestClass.class.getMethod("testMethod", classesOfParameters)), CoreMatchers.is(CoreMatchers.equalTo("int")));
        Assert.assertThat(TypeRenderUtils.drawReturn(TypeRenderUtilsTest.TestClass.class.getMethod("anotherTestMethod")), CoreMatchers.is(CoreMatchers.equalTo("void")));
        Assert.assertThat(TypeRenderUtils.drawReturn(String.class.getMethod("isEmpty")), CoreMatchers.is(CoreMatchers.equalTo("boolean")));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testDrawReturnThrowsException() throws NoSuchMethodException {
        Assert.assertThat(TypeRenderUtils.drawReturn(TypeRenderUtilsTest.TestClass.class.getMethod("method")), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test
    public void testDrawExceptionsForMethod() throws NoSuchMethodException {
        Class[] classesOfParameters = new Class[2];
        classesOfParameters[0] = int.class;
        classesOfParameters[1] = boolean.class;
        Assert.assertThat(TypeRenderUtils.drawExceptions(TypeRenderUtilsTest.TestClass.class.getMethod("testMethod", classesOfParameters)), CoreMatchers.is(CoreMatchers.equalTo("")));
        Assert.assertThat(TypeRenderUtils.drawExceptions(TypeRenderUtilsTest.TestClass.class.getMethod("anotherTestMethod")), CoreMatchers.is(CoreMatchers.equalTo("java.lang.NullPointerException")));
        Assert.assertThat(TypeRenderUtils.drawExceptions(String.class.getMethod("getBytes", String.class)), CoreMatchers.is(CoreMatchers.equalTo("java.io.UnsupportedEncodingException")));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testDrawExceptionsForMethodThrowsException() throws NoSuchMethodException {
        Assert.assertThat(TypeRenderUtils.drawExceptions(TypeRenderUtilsTest.TestClass.class.getMethod("method")), CoreMatchers.is(CoreMatchers.equalTo("")));
    }

    @Test
    public void testDrawExceptionsForConstructor() throws NoSuchMethodException {
        Class[] classesOfConstructorParameters = new Class[2];
        classesOfConstructorParameters[0] = byte[].class;
        classesOfConstructorParameters[1] = String.class;
        Assert.assertThat(TypeRenderUtils.drawExceptions(String.class.getConstructor()), CoreMatchers.is(CoreMatchers.equalTo("")));
        Assert.assertThat(TypeRenderUtils.drawExceptions(String.class.getConstructor(classesOfConstructorParameters)), CoreMatchers.is(CoreMatchers.equalTo("java.io.UnsupportedEncodingException")));
    }

    @Test(expected = NoSuchMethodException.class)
    public void testDrawExceptionsForConstructorThrowsException() throws NoSuchMethodException {
        Assert.assertThat(TypeRenderUtils.drawExceptions(TypeRenderUtilsTest.TestClass.class.getConstructor()), CoreMatchers.is(CoreMatchers.equalTo("")));
    }
}

