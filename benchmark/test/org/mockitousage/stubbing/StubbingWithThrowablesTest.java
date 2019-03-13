/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class StubbingWithThrowablesTest extends TestBase {
    private LinkedList mock;

    private Map mockTwo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void throws_same_exception_consecutively() {
        Mockito.when(mock.add("")).thenThrow(new StubbingWithThrowablesTest.ExceptionOne());
        // 1st invocation
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() {
                mock.add("");
            }
        }).isInstanceOf(StubbingWithThrowablesTest.ExceptionOne.class);
        mock.add("1");
        // 2nd invocation
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() {
                mock.add("");
            }
        }).isInstanceOf(StubbingWithThrowablesTest.ExceptionOne.class);
    }

    @Test
    public void throws_same_exception_consecutively_with_doThrow() {
        Mockito.doThrow(new StubbingWithThrowablesTest.ExceptionOne()).when(mock).clear();
        // 1st invocation
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() {
                mock.clear();
            }
        }).isInstanceOf(StubbingWithThrowablesTest.ExceptionOne.class);
        mock.add("1");
        // 2nd invocation
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            public void call() {
                mock.clear();
            }
        }).isInstanceOf(StubbingWithThrowablesTest.ExceptionOne.class);
    }

    @Test
    public void shouldStubWithThrowable() throws Exception {
        IllegalArgumentException expected = new IllegalArgumentException("thrown by mock");
        Mockito.when(mock.add("throw")).thenThrow(expected);
        exception.expect(CoreMatchers.sameInstance(expected));
        mock.add("throw");
    }

    @Test
    public void shouldSetThrowableToVoidMethod() throws Exception {
        IllegalArgumentException expected = new IllegalArgumentException("thrown by mock");
        Mockito.doThrow(expected).when(mock).clear();
        exception.expect(CoreMatchers.sameInstance(expected));
        mock.clear();
    }

    @Test
    public void shouldLastStubbingVoidBeImportant() throws Exception {
        Mockito.doThrow(new StubbingWithThrowablesTest.ExceptionOne()).when(mock).clear();
        Mockito.doThrow(new StubbingWithThrowablesTest.ExceptionTwo()).when(mock).clear();
        exception.expect(StubbingWithThrowablesTest.ExceptionTwo.class);
        mock.clear();
    }

    @Test
    public void shouldFailStubbingThrowableOnTheSameInvocationDueToAcceptableLimitation() throws Exception {
        Mockito.when(mock.size()).thenThrow(new StubbingWithThrowablesTest.ExceptionOne());
        exception.expect(StubbingWithThrowablesTest.ExceptionOne.class);
        Mockito.when(mock.size()).thenThrow(new StubbingWithThrowablesTest.ExceptionTwo());
    }

    @Test
    public void shouldAllowSettingCheckedException() throws Exception {
        Reader reader = Mockito.mock(Reader.class);
        IOException ioException = new IOException();
        Mockito.when(reader.read()).thenThrow(ioException);
        exception.expect(CoreMatchers.sameInstance(ioException));
        reader.read();
    }

    @Test
    public void shouldAllowSettingError() throws Exception {
        Error error = new Error();
        Mockito.when(mock.add("quake")).thenThrow(error);
        exception.expect(Error.class);
        mock.add("quake");
    }

    @Test
    public void shouldNotAllowNullExceptionType() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot stub with null throwable");
        Mockito.when(mock.add(null)).thenThrow(((Exception) (null)));
    }

    @Test
    public void shouldInstantiateExceptionClassOnInteraction() {
        Mockito.when(mock.add(null)).thenThrow(StubbingWithThrowablesTest.NaughtyException.class);
        exception.expect(StubbingWithThrowablesTest.NaughtyException.class);
        mock.add(null);
    }

    @Test
    public void shouldInstantiateExceptionClassWithOngoingStubbingOnInteraction() {
        Mockito.doThrow(StubbingWithThrowablesTest.NaughtyException.class).when(mock).add(null);
        exception.expect(StubbingWithThrowablesTest.NaughtyException.class);
        mock.add(null);
    }

    @Test
    public void shouldNotAllowSettingInvalidCheckedException() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Checked exception is invalid for this method");
        Mockito.when(mock.add("monkey island")).thenThrow(new Exception());
    }

    @Test
    public void shouldNotAllowSettingNullThrowable() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot stub with null throwable");
        Mockito.when(mock.add("monkey island")).thenThrow(((Throwable) (null)));
    }

    @Test
    public void shouldNotAllowSettingNullThrowableArray() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot stub with null throwable");
        Mockito.when(mock.add("monkey island")).thenThrow(((Throwable[]) (null)));
    }

    @Test
    public void shouldNotAllowSettingNullThrowableClass() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.when(mock.isEmpty()).thenThrow(((Class) (null)));
    }

    @Test
    public void shouldNotAllowSettingNullThrowableClasses() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.when(mock.isEmpty()).thenThrow(RuntimeException.class, ((Class[]) (null)));
    }

    @Test
    public void shouldNotAllowSettingNullVarArgThrowableClass() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.when(mock.isEmpty()).thenThrow(RuntimeException.class, ((Class) (null)));
    }

    @Test
    public void doThrowShouldNotAllowSettingNullThrowableClass() {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.doThrow(((Class) (null))).when(mock).isEmpty();
    }

    @Test
    public void doThrowShouldNotAllowSettingNullThrowableClasses() throws Exception {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.doThrow(RuntimeException.class, ((Class) (null))).when(mock).isEmpty();
    }

    @Test
    public void doThrowShouldNotAllowSettingNullVarArgThrowableClasses() throws Exception {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.doThrow(RuntimeException.class, ((Class[]) (null))).when(mock).isEmpty();
    }

    @Test
    public void shouldNotAllowSettingNullVarArgsThrowableClasses() throws Exception {
        exception.expect(MockitoException.class);
        exception.expectMessage("Exception type cannot be null");
        Mockito.when(mock.isEmpty()).thenThrow(RuntimeException.class, ((Class<RuntimeException>[]) (null)));
    }

    @Test
    public void shouldNotAllowDifferntCheckedException() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class);
        exception.expect(MockitoException.class);
        exception.expectMessage("Checked exception is invalid for this method");
        Mockito.when(mock.throwsIOException(0)).thenThrow(StubbingWithThrowablesTest.CheckedException.class);
    }

    @Test
    public void shouldNotAllowCheckedExceptionWhenErrorIsDeclared() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class);
        exception.expect(MockitoException.class);
        exception.expectMessage("Checked exception is invalid for this method");
        Mockito.when(mock.throwsError(0)).thenThrow(StubbingWithThrowablesTest.CheckedException.class);
    }

    @Test
    public void shouldNotAllowCheckedExceptionWhenNothingIsDeclared() throws Exception {
        IMethods mock = Mockito.mock(IMethods.class);
        exception.expect(MockitoException.class);
        exception.expectMessage("Checked exception is invalid for this method");
        Mockito.when(mock.throwsNothing(true)).thenThrow(StubbingWithThrowablesTest.CheckedException.class);
    }

    @Test
    public void shouldMixThrowablesAndReturnsOnDifferentMocks() throws Exception {
        Mockito.when(mock.add("ExceptionOne")).thenThrow(new StubbingWithThrowablesTest.ExceptionOne());
        Mockito.when(mock.getLast()).thenReturn("last");
        Mockito.doThrow(new StubbingWithThrowablesTest.ExceptionTwo()).when(mock).clear();
        Mockito.doThrow(new StubbingWithThrowablesTest.ExceptionThree()).when(mockTwo).clear();
        Mockito.when(mockTwo.containsValue("ExceptionFour")).thenThrow(new StubbingWithThrowablesTest.ExceptionFour());
        Mockito.when(mockTwo.get("Are you there?")).thenReturn("Yes!");
        Assert.assertNull(mockTwo.get("foo"));
        Assert.assertTrue(mockTwo.keySet().isEmpty());
        Assert.assertEquals("Yes!", mockTwo.get("Are you there?"));
        try {
            mockTwo.clear();
            Assert.fail();
        } catch (StubbingWithThrowablesTest.ExceptionThree e) {
        }
        try {
            mockTwo.containsValue("ExceptionFour");
            Assert.fail();
        } catch (StubbingWithThrowablesTest.ExceptionFour e) {
        }
        Assert.assertNull(mock.getFirst());
        Assert.assertEquals("last", mock.getLast());
        try {
            mock.add("ExceptionOne");
            Assert.fail();
        } catch (StubbingWithThrowablesTest.ExceptionOne e) {
        }
        try {
            mock.clear();
            Assert.fail();
        } catch (StubbingWithThrowablesTest.ExceptionTwo e) {
        }
    }

    @Test
    public void shouldStubbingWithThrowableBeVerifiable() {
        Mockito.when(mock.size()).thenThrow(new RuntimeException());
        Mockito.doThrow(new RuntimeException()).when(mock).clone();
        try {
            mock.size();
            Assert.fail();
        } catch (RuntimeException e) {
        }
        try {
            mock.clone();
            Assert.fail();
        } catch (RuntimeException e) {
        }
        Mockito.verify(mock).size();
        Mockito.verify(mock).clone();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void shouldStubbingWithThrowableFailVerification() {
        Mockito.when(mock.size()).thenThrow(new RuntimeException());
        Mockito.doThrow(new RuntimeException()).when(mock).clone();
        Mockito.verifyZeroInteractions(mock);
        mock.add("test");
        try {
            Mockito.verify(mock).size();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
        try {
            Mockito.verify(mock).clone();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    private class ExceptionOne extends RuntimeException {}

    private class ExceptionTwo extends RuntimeException {}

    private class ExceptionThree extends RuntimeException {}

    private class ExceptionFour extends RuntimeException {}

    private class CheckedException extends Exception {}

    public class NaughtyException extends RuntimeException {
        public NaughtyException() {
            throw new RuntimeException("boo!");
        }
    }
}

