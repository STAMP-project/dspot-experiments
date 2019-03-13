/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stacktrace;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.Conditions;
import org.mockitoutil.TestBase;


public class StackTraceFilteringTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldFilterStackTraceOnVerify() {
        try {
            Mockito.verify(mock).simpleMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            Assertions.assertThat(e).has(Conditions.firstMethodInStackTrace("shouldFilterStackTraceOnVerify"));
        }
    }

    @Test
    public void shouldFilterStackTraceOnVerifyNoMoreInteractions() {
        mock.oneArg(true);
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            Assertions.assertThat(e).has(Conditions.firstMethodInStackTrace("shouldFilterStackTraceOnVerifyNoMoreInteractions"));
        }
    }

    @Test
    public void shouldFilterStackTraceOnVerifyZeroInteractions() {
        mock.oneArg(true);
        try {
            Mockito.verifyZeroInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            Assertions.assertThat(e).has(Conditions.firstMethodInStackTrace("shouldFilterStackTraceOnVerifyZeroInteractions"));
        }
    }

    @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
    @Test
    public void shouldFilterStacktraceOnMockitoException() {
        Mockito.verify(mock);
        try {
            Mockito.verify(mock).oneArg(true);
            Assert.fail();
        } catch (MockitoException expected) {
            Assertions.assertThat(expected).has(Conditions.firstMethodInStackTrace("shouldFilterStacktraceOnMockitoException"));
        }
    }

    @Test
    public void shouldFilterStacktraceWhenVerifyingInOrder() {
        InOrder inOrder = Mockito.inOrder(mock);
        mock.oneArg(true);
        mock.oneArg(false);
        inOrder.verify(mock).oneArg(false);
        try {
            inOrder.verify(mock).oneArg(true);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            Assertions.assertThat(e).has(Conditions.firstMethodInStackTrace("shouldFilterStacktraceWhenVerifyingInOrder"));
        }
    }

    @Test
    public void shouldFilterStacktraceWhenInOrderThrowsMockitoException() {
        try {
            Mockito.inOrder();
            Assert.fail();
        } catch (MockitoException expected) {
            Assertions.assertThat(expected).has(Conditions.firstMethodInStackTrace("shouldFilterStacktraceWhenInOrderThrowsMockitoException"));
        }
    }

    @Test
    public void shouldFilterStacktraceWhenInOrderVerifies() {
        try {
            InOrder inOrder = Mockito.inOrder(mock);
            inOrder.verify(null);
            Assert.fail();
        } catch (MockitoException expected) {
            Assertions.assertThat(expected).has(Conditions.firstMethodInStackTrace("shouldFilterStacktraceWhenInOrderVerifies"));
        }
    }

    @Test
    public void shouldFilterStackTraceWhenThrowingExceptionFromMockHandler() {
        try {
            Mockito.when(mock.oneArg(true)).thenThrow(new Exception());
            Assert.fail();
        } catch (MockitoException expected) {
            Assertions.assertThat(expected).has(Conditions.firstMethodInStackTrace("shouldFilterStackTraceWhenThrowingExceptionFromMockHandler"));
        }
    }

    @Test
    public void shouldShowProperExceptionStackTrace() throws Exception {
        Mockito.when(mock.simpleMethod()).thenThrow(new RuntimeException());
        try {
            mock.simpleMethod();
            Assert.fail();
        } catch (RuntimeException e) {
            Assertions.assertThat(e).has(Conditions.firstMethodInStackTrace("shouldShowProperExceptionStackTrace"));
        }
    }
}

