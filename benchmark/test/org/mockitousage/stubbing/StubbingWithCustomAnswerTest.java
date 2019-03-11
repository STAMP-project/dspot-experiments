/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.lang.reflect.Method;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class StubbingWithCustomAnswerTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldAnswer() throws Exception {
        Mockito.when(mock.simpleMethod(ArgumentMatchers.anyString())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                String arg = invocation.getArgument(0);
                return ((invocation.getMethod().getName()) + "-") + arg;
            }
        });
        Assert.assertEquals("simpleMethod-test", mock.simpleMethod("test"));
    }

    @Test
    public void shouldAnswerWithThenAnswerAlias() throws Exception {
        StubbingWithCustomAnswerTest.RecordCall recordCall = new StubbingWithCustomAnswerTest.RecordCall();
        Set<?> mockedSet = ((Set<?>) (Mockito.when(Mockito.mock(Set.class).isEmpty()).then(recordCall).getMock()));
        mockedSet.isEmpty();
        Assert.assertTrue(recordCall.isCalled());
    }

    @Test
    public void shouldAnswerConsecutively() throws Exception {
        Mockito.when(mock.simpleMethod()).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getMethod().getName();
            }
        }).thenReturn("Hello").thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (invocation.getMethod().getName()) + "-1";
            }
        });
        Assert.assertEquals("simpleMethod", mock.simpleMethod());
        Assert.assertEquals("Hello", mock.simpleMethod());
        Assert.assertEquals("simpleMethod-1", mock.simpleMethod());
        Assert.assertEquals("simpleMethod-1", mock.simpleMethod());
    }

    @Test
    public void shouldAnswerVoidMethod() throws Exception {
        StubbingWithCustomAnswerTest.RecordCall recordCall = new StubbingWithCustomAnswerTest.RecordCall();
        Mockito.doAnswer(recordCall).when(mock).voidMethod();
        mock.voidMethod();
        Assert.assertTrue(recordCall.isCalled());
    }

    @Test
    public void shouldAnswerVoidMethodConsecutively() throws Exception {
        StubbingWithCustomAnswerTest.RecordCall call1 = new StubbingWithCustomAnswerTest.RecordCall();
        StubbingWithCustomAnswerTest.RecordCall call2 = new StubbingWithCustomAnswerTest.RecordCall();
        Mockito.doAnswer(call1).doThrow(new UnsupportedOperationException()).doAnswer(call2).when(mock).voidMethod();
        mock.voidMethod();
        Assert.assertTrue(call1.isCalled());
        Assert.assertFalse(call2.isCalled());
        try {
            mock.voidMethod();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        mock.voidMethod();
        Assert.assertTrue(call2.isCalled());
    }

    @Test
    public void shouldMakeSureTheInterfaceDoesNotChange() throws Exception {
        Mockito.when(mock.simpleMethod(ArgumentMatchers.anyString())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertTrue(invocation.getArguments().getClass().isArray());
                Assert.assertEquals(Method.class, invocation.getMethod().getClass());
                return "assertions passed";
            }
        });
        Assert.assertEquals("assertions passed", mock.simpleMethod("test"));
    }

    private static class RecordCall implements Answer<Object> {
        private boolean called = false;

        public boolean isCalled() {
            return called;
        }

        public Object answer(InvocationOnMock invocation) throws Throwable {
            called = true;
            return null;
        }
    }
}

