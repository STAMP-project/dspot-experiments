/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitoutil.TestBase;


public class UsingVarargsTest extends TestBase {
    private interface IVarArgs {
        void withStringVarargs(int value, String... s);

        String withStringVarargsReturningString(int value, String... s);

        void withObjectVarargs(int value, Object... o);

        boolean withBooleanVarargs(int value, boolean... b);

        int foo(Object... objects);
    }

    @Mock
    UsingVarargsTest.IVarArgs mock;

    @Test
    public void shouldStubStringVarargs() {
        Mockito.when(mock.withStringVarargsReturningString(1)).thenReturn("1");
        Mockito.when(mock.withStringVarargsReturningString(2, "1", "2", "3")).thenReturn("2");
        RuntimeException expected = new RuntimeException();
        Mockito.doThrow(expected).when(mock).withStringVarargs(3, "1", "2", "3", "4");
        Assert.assertEquals("1", mock.withStringVarargsReturningString(1));
        Assert.assertEquals(null, mock.withStringVarargsReturningString(2));
        Assert.assertEquals("2", mock.withStringVarargsReturningString(2, "1", "2", "3"));
        Assert.assertEquals(null, mock.withStringVarargsReturningString(2, "1", "2"));
        Assert.assertEquals(null, mock.withStringVarargsReturningString(2, "1", "2", "3", "4"));
        Assert.assertEquals(null, mock.withStringVarargsReturningString(2, "1", "2", "9999"));
        mock.withStringVarargs(3, "1", "2", "3", "9999");
        mock.withStringVarargs(9999, "1", "2", "3", "4");
        try {
            mock.withStringVarargs(3, "1", "2", "3", "4");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(expected, e);
        }
    }

    @Test
    public void shouldStubBooleanVarargs() {
        Mockito.when(mock.withBooleanVarargs(1)).thenReturn(true);
        Mockito.when(mock.withBooleanVarargs(1, true, false)).thenReturn(true);
        Assert.assertEquals(true, mock.withBooleanVarargs(1));
        Assert.assertEquals(false, mock.withBooleanVarargs(9999));
        Assert.assertEquals(true, mock.withBooleanVarargs(1, true, false));
        Assert.assertEquals(false, mock.withBooleanVarargs(1, true, false, true));
        Assert.assertEquals(false, mock.withBooleanVarargs(2, true, false));
        Assert.assertEquals(false, mock.withBooleanVarargs(1, true));
        Assert.assertEquals(false, mock.withBooleanVarargs(1, false, false));
    }

    @Test
    public void shouldVerifyStringVarargs() {
        mock.withStringVarargs(1);
        mock.withStringVarargs(2, "1", "2", "3");
        mock.withStringVarargs(3, "1", "2", "3", "4");
        Mockito.verify(mock).withStringVarargs(1);
        Mockito.verify(mock).withStringVarargs(2, "1", "2", "3");
        try {
            Mockito.verify(mock).withStringVarargs(2, "1", "2", "79", "4");
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
    }

    @Test
    public void shouldVerifyObjectVarargs() {
        mock.withObjectVarargs(1);
        mock.withObjectVarargs(2, "1", new ArrayList<Object>(), new Integer(1));
        mock.withObjectVarargs(3, new Integer(1));
        Mockito.verify(mock).withObjectVarargs(1);
        Mockito.verify(mock).withObjectVarargs(2, "1", new ArrayList<Object>(), new Integer(1));
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldVerifyBooleanVarargs() {
        mock.withBooleanVarargs(1);
        mock.withBooleanVarargs(2, true, false, true);
        mock.withBooleanVarargs(3, true, true, true);
        Mockito.verify(mock).withBooleanVarargs(1);
        Mockito.verify(mock).withBooleanVarargs(2, true, false, true);
        try {
            Mockito.verify(mock).withBooleanVarargs(3, true, true, true, true);
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
    }

    @Test
    public void shouldVerifyWithAnyObject() {
        UsingVarargsTest.Foo foo = Mockito.mock(UsingVarargsTest.Foo.class);
        foo.varArgs("");
        Mockito.verify(foo).varArgs(((String[]) (Mockito.anyObject())));
        Mockito.verify(foo).varArgs(((String) (Mockito.anyObject())));
    }

    @Test
    public void shouldVerifyWithNullVarArgArray() {
        UsingVarargsTest.Foo foo = Mockito.mock(UsingVarargsTest.Foo.class);
        foo.varArgs(((String[]) (null)));
        Mockito.verify(foo).varArgs(((String[]) (Mockito.anyObject())));
        Mockito.verify(foo).varArgs(((String[]) (null)));
    }

    public class Foo {
        public void varArgs(String... args) {
        }
    }

    interface MixedVarargs {
        String doSomething(String one, String... varargs);

        String doSomething(String one, String two, String... varargs);
    }

    // See bug #31
    @SuppressWarnings("all")
    @Test
    public void shouldStubCorrectlyWhenMixedVarargsUsed() {
        UsingVarargsTest.MixedVarargs mixedVarargs = Mockito.mock(UsingVarargsTest.MixedVarargs.class);
        Mockito.when(mixedVarargs.doSomething("hello", ((String[]) (null)))).thenReturn("hello");
        Mockito.when(mixedVarargs.doSomething("goodbye", ((String[]) (null)))).thenReturn("goodbye");
        String result = mixedVarargs.doSomething("hello", ((String[]) (null)));
        Assert.assertEquals("hello", result);
        Mockito.verify(mixedVarargs).doSomething("hello", ((String[]) (null)));
    }

    @SuppressWarnings("all")
    @Test
    public void shouldStubCorrectlyWhenDoubleStringAndMixedVarargsUsed() {
        UsingVarargsTest.MixedVarargs mixedVarargs = Mockito.mock(UsingVarargsTest.MixedVarargs.class);
        Mockito.when(mixedVarargs.doSomething("one", "two", ((String[]) (null)))).thenReturn("hello");
        Mockito.when(mixedVarargs.doSomething("1", "2", ((String[]) (null)))).thenReturn("goodbye");
        String result = mixedVarargs.doSomething("one", "two", ((String[]) (null)));
        Assert.assertEquals("hello", result);
    }

    // See bug #157
    @Test
    public void shouldMatchEasilyEmptyVararg() throws Exception {
        // when
        Mockito.when(mock.foo(ArgumentMatchers.anyVararg())).thenReturn((-1));
        // then
        Assert.assertEquals((-1), mock.foo());
    }
}

