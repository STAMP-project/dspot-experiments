/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.internal.util.MockUtil;
import org.mockitoutil.TestBase;


public class ReturnsMocksTest extends TestBase {
    private ReturnsMocks values = new ReturnsMocks();

    interface AllInterface {
        ReturnsMocksTest.FooInterface getInterface();

        ReturnsMocksTest.BarClass getNormalClass();

        ReturnsMocksTest.Baz getFinalClass();

        ReturnsGenericDeepStubsTest.WithGenerics<String> withGenerics();
    }

    interface FooInterface {}

    class BarClass {}

    final class Baz {}

    @Test
    public void should_return_mock_value_for_interface() throws Throwable {
        Object interfaceMock = values.answer(TestBase.invocationOf(ReturnsMocksTest.AllInterface.class, "getInterface"));
        Assert.assertTrue(MockUtil.isMock(interfaceMock));
    }

    @Test
    public void should_return_mock_value_for_class() throws Throwable {
        Object classMock = values.answer(TestBase.invocationOf(ReturnsMocksTest.AllInterface.class, "getNormalClass"));
        Assert.assertTrue(MockUtil.isMock(classMock));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_return_mock_value_for_generic_class() throws Throwable {
        ReturnsGenericDeepStubsTest.WithGenerics<String> classMock = ((ReturnsGenericDeepStubsTest.WithGenerics<String>) (values.answer(TestBase.invocationOf(ReturnsMocksTest.AllInterface.class, "withGenerics"))));
        Assert.assertTrue(MockUtil.isMock(classMock));
        Mockito.when(classMock.execute()).thenReturn("return");
        Assert.assertEquals("return", classMock.execute());
    }

    @Test
    public void should_return_null_for_final_class_if_unsupported() throws Throwable {
        Assume.assumeFalse(Plugins.getMockMaker().isTypeMockable(ReturnsMocksTest.Baz.class).mockable());
        Assert.assertNull(values.answer(TestBase.invocationOf(ReturnsMocksTest.AllInterface.class, "getFinalClass")));
    }

    @Test
    public void should_return_the_usual_default_values_for_primitives() throws Throwable {
        ReturnsMocks answer = new ReturnsMocks();
        Assert.assertEquals(false, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "booleanMethod")));
        Assert.assertEquals(((char) (0)), answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "charMethod")));
        Assert.assertEquals(((byte) (0)), answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "byteMethod")));
        Assert.assertEquals(((short) (0)), answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "shortMethod")));
        Assert.assertEquals(0, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "intMethod")));
        Assert.assertEquals(0L, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "longMethod")));
        Assert.assertEquals(0.0F, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "floatMethod")));
        Assert.assertEquals(0.0, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "doubleMethod")));
    }

    @SuppressWarnings("unused")
    interface StringMethods {
        String stringMethod();

        String[] stringArrayMethod();
    }

    @Test
    public void should_return_empty_array() throws Throwable {
        String[] ret = ((String[]) (values.answer(TestBase.invocationOf(ReturnsMocksTest.StringMethods.class, "stringArrayMethod"))));
        Assert.assertTrue(ret.getClass().isArray());
        Assert.assertTrue(((ret.length) == 0));
    }

    @Test
    public void should_return_empty_string() throws Throwable {
        Assert.assertEquals("", values.answer(TestBase.invocationOf(ReturnsMocksTest.StringMethods.class, "stringMethod")));
    }
}

