/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.List;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class BasicVerificationTest extends TestBase {
    @Mock
    private List<String> mock;

    @Mock
    private List<String> mockTwo;

    @Test
    public void shouldVerify() throws Exception {
        mock.clear();
        Mockito.verify(mock).clear();
        mock.add("test");
        Mockito.verify(mock).add("test");
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test(expected = WantedButNotInvoked.class)
    public void shouldFailVerification() throws Exception {
        Mockito.verify(mock).clear();
    }

    @Test
    public void shouldFailVerificationOnMethodArgument() throws Exception {
        mock.clear();
        mock.add("foo");
        Mockito.verify(mock).clear();
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                Mockito.verify(mock).add("bar");
            }
        }).isInstanceOf(ArgumentsAreDifferent.class);
    }

    @Test
    public void shouldFailOnWrongMethod() throws Exception {
        mock.clear();
        mock.clear();
        mockTwo.add("add");
        Mockito.verify(mock, Mockito.atLeastOnce()).clear();
        Mockito.verify(mockTwo, Mockito.atLeastOnce()).add("add");
        try {
            Mockito.verify(mockTwo, Mockito.atLeastOnce()).add("foo");
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }

    @Test
    public void shouldDetectRedundantInvocation() throws Exception {
        mock.clear();
        mock.add("foo");
        mock.add("bar");
        Mockito.verify(mock).clear();
        Mockito.verify(mock).add("foo");
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldDetectWhenInvokedMoreThanOnce() throws Exception {
        mock.add("foo");
        mock.clear();
        mock.clear();
        Mockito.verify(mock).add("foo");
        try {
            Mockito.verify(mock).clear();
            Assert.fail();
        } catch (TooManyActualInvocations e) {
        }
    }

    @Test
    public void shouldVerifyStubbedMethods() throws Exception {
        Mockito.when(mock.add("test")).thenReturn(Boolean.FALSE);
        mock.add("test");
        Mockito.verify(mock).add("test");
    }

    @Test
    public void shouldDetectWhenOverloadedMethodCalled() throws Exception {
        IMethods mockThree = Mockito.mock(IMethods.class);
        mockThree.varargs(((Object[]) (new Object[]{  })));
        try {
            Mockito.verify(mockThree).varargs(((String[]) (new String[]{  })));
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }
}

