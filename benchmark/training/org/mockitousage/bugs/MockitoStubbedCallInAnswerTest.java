/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitoutil.TestBase;


/**
 *
 *
 * @see <a href="https://github.com/mockito/mockito/issues/1279">Issue #1279</a>
 */
public class MockitoStubbedCallInAnswerTest extends TestBase {
    @Mock
    MockitoStubbedCallInAnswerTest.Foo foo;

    @Mock
    MockitoStubbedCallInAnswerTest.Bar bar;

    @Test
    public void stubbing_the_right_mock() throws Exception {
        // stubbing on different mock should not be altered
        Mockito.when(bar.doInt()).thenReturn(0);
        Mockito.when(foo.doInt()).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return bar.doInt();
            }
        });
        Assert.assertEquals(0, foo.doInt());
        Assert.assertEquals(0, bar.doInt());
        // when we override the stubbing
        Mockito.when(foo.doInt()).thenReturn(1);
        // we expect it to be reflected:
        Assert.assertEquals(1, foo.doInt());
        // but the stubbing on a different mock should not be altered:
        Assert.assertEquals(0, bar.doInt());
    }

    @Test
    public void return_type_validation() throws Exception {
        Mockito.when(foo.doString()).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                // invoking a method on a different mock, with different return type
                return String.valueOf(bar.doInt());
            }
        });
        Assert.assertEquals("0", foo.doString());
        // we can override stubbing without misleading return type validation errors:
        Mockito.when(foo.doString()).thenReturn("");
        Assert.assertEquals("", foo.doString());
    }

    @Test
    public void prevents_stack_overflow() throws Exception {
        Mockito.when(foo.doInt()).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return bar.doInt();
            }
        });
        Assert.assertEquals(0, foo.doInt());
        Mockito.when(foo.doInt()).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return (bar.doInt()) + 1;
            }
        });
        // calling below used to cause SO error
        Assert.assertEquals(1, foo.doInt());
    }

    @Test
    public void overriding_stubbing() throws Exception {
        Mockito.when(bar.doInt()).thenReturn(10);
        Mockito.when(foo.doInt()).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return (bar.doInt()) + 1;
            }
        });
        Assert.assertEquals(11, foo.doInt());
        // when we override the stubbing with a different one
        Mockito.when(foo.doInt()).thenReturn(100);
        // we expect it to be reflected:
        Assert.assertEquals(100, foo.doInt());
    }

    interface Foo {
        String doString();

        int doInt();
    }

    interface Bar {
        int doInt();
    }
}

