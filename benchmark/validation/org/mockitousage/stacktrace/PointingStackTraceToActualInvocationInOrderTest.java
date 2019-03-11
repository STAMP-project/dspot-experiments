/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stacktrace;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// This is required to make sure stack trace is well filtered when runner is ON
@RunWith(MockitoJUnitRunner.class)
public class PointingStackTraceToActualInvocationInOrderTest extends TestBase {
    @Mock
    private IMethods mock;

    @Mock
    private IMethods mockTwo;

    private InOrder inOrder;

    @Test
    public void shouldPointStackTraceToPreviousVerified() {
        inOrder.verify(mock, Mockito.atLeastOnce()).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockTwo).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mock).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("fourth(");
        }
    }

    @Test
    public void shouldPointToThirdMethod() {
        inOrder.verify(mock, Mockito.atLeastOnce()).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("third(");
        }
    }

    @Test
    public void shouldPointToSecondMethod() {
        inOrder.verify(mock).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockTwo).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("second(");
        }
    }

    @Test
    public void shouldPointToFirstMethodBecauseOfTooManyActualInvocations() {
        try {
            inOrder.verify(mock, Mockito.times(0)).simpleMethod(ArgumentMatchers.anyInt());
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("first(");
        }
    }

    @Test
    public void shouldPointToSecondMethodBecauseOfTooManyActualInvocations() {
        inOrder.verify(mock).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo, Mockito.times(0)).simpleMethod(ArgumentMatchers.anyInt());
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("second(");
        }
    }

    @Test
    public void shouldPointToFourthMethodBecauseOfTooLittleActualInvocations() {
        inOrder.verify(mock).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockTwo).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mock).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(ArgumentMatchers.anyInt());
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("fourth(");
        }
    }
}

