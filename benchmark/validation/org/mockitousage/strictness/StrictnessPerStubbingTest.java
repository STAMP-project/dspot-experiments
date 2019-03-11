/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.strictness;


import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class StrictnessPerStubbingTest {
    MockitoSession mockito;

    @Mock
    IMethods mock;

    @Test
    public void potential_stubbing_problem() {
        // when
        Mockito.when(mock.simpleMethod("1")).thenReturn("1");
        Mockito.lenient().when(mock.differentMethod("2")).thenReturn("2");
        // then on lenient stubbing, we can call it with different argument:
        mock.differentMethod("200");
        // but on strict stubbing, we cannot:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ProductionCode.simpleMethod(mock, "100");
            }
        }).isInstanceOf(PotentialStubbingProblem.class);
    }

    @Test
    public void doReturn_syntax() {
        // when
        Mockito.lenient().doReturn("2").doReturn("3").when(mock).simpleMethod(1);
        // then on lenient stubbing, we can call it with different argument:
        mock.simpleMethod(200);
        // and stubbing works, too:
        Assert.assertEquals("2", mock.simpleMethod(1));
        Assert.assertEquals("3", mock.simpleMethod(1));
    }

    @Test
    public void doReturn_varargs_syntax() {
        // when
        Mockito.lenient().doReturn("2", "3").when(mock).simpleMethod(1);
        // then on lenient stubbing, we can call it with different argument with no exception:
        mock.simpleMethod(200);
        // and stubbing works, too:
        Assert.assertEquals("2", mock.simpleMethod(1));
        Assert.assertEquals("3", mock.simpleMethod(1));
    }

    @Test
    public void doThrow_syntax() {
        // when
        Mockito.lenient().doThrow(IllegalArgumentException.class).doThrow(IllegalStateException.class).when(mock).simpleMethod(1);
        // then on lenient stubbing, we can call it with different argument with no exception:
        mock.simpleMethod(200);
        // and stubbing works, too:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                mock.simpleMethod(1);
            }
        }).isInstanceOf(IllegalArgumentException.class);
        // testing consecutive call:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                mock.simpleMethod(1);
            }
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void doThrow_vararg_syntax() {
        // when
        Mockito.lenient().doThrow(IllegalArgumentException.class, IllegalStateException.class).when(mock).simpleMethod(1);
        // then on lenient stubbing, we can call it with different argument with no exception:
        mock.simpleMethod(200);
        // and stubbing works, too:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                mock.simpleMethod(1);
            }
        }).isInstanceOf(IllegalArgumentException.class);
        // testing consecutive call:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                mock.simpleMethod(1);
            }
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void doThrow_instance_vararg_syntax() {
        // when
        Mockito.lenient().doThrow(new IllegalArgumentException(), new IllegalStateException()).when(mock).simpleMethod(1);
        // then on lenient stubbing, we can call it with different argument with no exception:
        mock.simpleMethod(200);
        // and stubbing works, too:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                mock.simpleMethod(1);
            }
        }).isInstanceOf(IllegalArgumentException.class);
        // testing consecutive call:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() throws Throwable {
                mock.simpleMethod(1);
            }
        }).isInstanceOf(IllegalStateException.class);
    }

    static class Counter {
        int increment(int x) {
            return x + 1;
        }

        void scream(String message) {
            throw new RuntimeException(message);
        }
    }

    @Test
    public void doCallRealMethod_syntax() {
        // when
        StrictnessPerStubbingTest.Counter mock = Mockito.mock(StrictnessPerStubbingTest.Counter.class);
        Mockito.lenient().doCallRealMethod().when(mock).increment(1);
        // then no exception and default return value if we call it with different arg:
        Assert.assertEquals(0, mock.increment(0));
        // and real method is called when using correct arg:
        Assert.assertEquals(2, mock.increment(1));
    }

    @Test
    public void doNothing_syntax() {
        // when
        final StrictnessPerStubbingTest.Counter spy = Mockito.spy(StrictnessPerStubbingTest.Counter.class);
        Mockito.lenient().doNothing().when(spy).scream("1");
        // then no stubbing exception and real method is called if we call stubbed method with different arg:
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                spy.scream("2");
            }
        }).hasMessage("2");
        // and we do nothing when stubbing called with correct arg:
        spy.scream("1");
    }

    @Test
    public void doAnswer_syntax() {
        // when
        Mockito.lenient().doAnswer(AdditionalAnswers.returnsFirstArg()).when(mock).simpleMethod("1");
        // then on lenient stubbing, we can call it with different argument:
        mock.simpleMethod("200");
        // and stubbing works, too:
        Assert.assertEquals("1", mock.simpleMethod("1"));
    }

    @Test
    public void unnecessary_stubbing() {
        // when
        Mockito.when(mock.simpleMethod("1")).thenReturn("1");
        Mockito.lenient().when(mock.differentMethod("2")).thenReturn("2");
        // then unnecessary stubbing flags method only on the strict stubbing:
        // good enough to prove that we're flagging just one unnecessary stubbing:
        // TODO 792: this assertion is duplicated with StrictnessPerMockTest
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                mockito.finishMocking();
            }
        }).isInstanceOf(UnnecessaryStubbingException.class).hasMessageContaining("1. -> ").isNot(TestBase.hasMessageContaining("2. ->"));
    }

    @Test
    public void unnecessary_stubbing_with_doReturn() {
        // when
        Mockito.lenient().doReturn("2").when(mock).differentMethod("2");
        // then no exception is thrown:
        mockito.finishMocking();
    }

    @Test
    public void verify_no_more_invocations() {
        // when
        Mockito.when(mock.simpleMethod("1")).thenReturn("1");
        Mockito.lenient().when(mock.differentMethod("2")).thenReturn("2");
        // and:
        mock.simpleMethod("1");
        mock.differentMethod("200");// <- different arg

        // then 'verifyNoMoreInteractions' flags the lenient stubbing (called with different arg)
        // and reports it with [?] in the exception message
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                Mockito.verifyNoMoreInteractions(mock);
            }
        }).isInstanceOf(NoInteractionsWanted.class).hasMessageContaining("1. ->").hasMessageContaining("2. [?]->");
        // TODO 792: assertion duplicated with StrictnessPerMockTest
        // and we should use assertions based on content of the exception rather than the string
    }
}

