/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.SmartNullPointerException;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class SmartNullsStubbingTest extends TestBase {
    private IMethods mock;

    @Test
    public void shouldSmartNPEPointToUnstubbedCall() throws Exception {
        IMethods methods = unstubbedMethodInvokedHere(mock);
        try {
            methods.simpleMethod();
            Assert.fail();
        } catch (SmartNullPointerException e) {
            assertThat(e).hasMessageContaining("unstubbedMethodInvokedHere(");
        }
    }

    @Test
    public void should_not_throw_NPE_when_verifying_with_returns_smart_nulls() {
        SmartNullsStubbingTest.Foo mock = Mockito.mock(SmartNullsStubbingTest.Foo.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(mock.returnsFromArg(null)).thenReturn("Does not fail.");
        assertThat(((Object) (mock.returnsFromArg(null)))).isEqualTo("Does not fail.");
    }

    interface Bar {
        void boo();
    }

    class Foo {
        SmartNullsStubbingTest.Foo getSomeClass() {
            return null;
        }

        SmartNullsStubbingTest.Bar getSomeInterface() {
            return null;
        }

        SmartNullsStubbingTest.Bar getBarWithParams(int x, String y) {
            return null;
        }

        <T> T returnsFromArg(T arg) {
            return arg;
        }

        void boo() {
        }
    }

    @Test
    public void shouldThrowSmartNPEWhenMethodReturnsClass() throws Exception {
        SmartNullsStubbingTest.Foo mock = Mockito.mock(SmartNullsStubbingTest.Foo.class, Mockito.RETURNS_SMART_NULLS);
        SmartNullsStubbingTest.Foo foo = mock.getSomeClass();
        try {
            foo.boo();
            Assert.fail();
        } catch (SmartNullPointerException e) {
        }
    }

    @Test
    public void shouldThrowSmartNPEWhenMethodReturnsInterface() throws Exception {
        SmartNullsStubbingTest.Foo mock = Mockito.mock(SmartNullsStubbingTest.Foo.class, Mockito.RETURNS_SMART_NULLS);
        SmartNullsStubbingTest.Bar bar = mock.getSomeInterface();
        try {
            bar.boo();
            Assert.fail();
        } catch (SmartNullPointerException e) {
        }
    }

    @Test
    public void shouldReturnOrdinaryEmptyValuesForOrdinaryTypes() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.RETURNS_SMART_NULLS);
        Assert.assertEquals("", mock.stringReturningMethod());
        Assert.assertEquals(0, mock.intReturningMethod());
        Assert.assertEquals(true, mock.listReturningMethod().isEmpty());
        Assert.assertEquals(0, mock.arrayReturningMethod().length);
    }

    @Test
    public void shouldNotThrowSmartNullPointerOnToString() {
        Object smartNull = mock.objectReturningMethod();
        try {
            Mockito.verify(mock).simpleMethod(smartNull);
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }

    @Test
    public void shouldNotThrowSmartNullPointerOnObjectMethods() {
        Object smartNull = mock.objectReturningMethod();
        smartNull.toString();
    }

    @Test
    public void shouldShowParameters() {
        SmartNullsStubbingTest.Foo foo = Mockito.mock(SmartNullsStubbingTest.Foo.class, Mockito.RETURNS_SMART_NULLS);
        SmartNullsStubbingTest.Bar smartNull = foo.getBarWithParams(10, "yes sir");
        try {
            smartNull.boo();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("yes sir");
        }
    }

    @Test
    public void shouldShowParametersWhenParamsAreHuge() {
        SmartNullsStubbingTest.Foo foo = Mockito.mock(SmartNullsStubbingTest.Foo.class, Mockito.RETURNS_SMART_NULLS);
        String longStr = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        SmartNullsStubbingTest.Bar smartNull = foo.getBarWithParams(10, longStr);
        try {
            smartNull.boo();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Lorem Ipsum");
        }
    }
}

