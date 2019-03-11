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


@RunWith(MockitoJUnitRunner.class)
public class PointingStackTraceToActualInvocationChunkInOrderTest extends TestBase {
    @Mock
    private IMethods mock;

    @Mock
    private IMethods mockTwo;

    private InOrder inOrder;

    @Test
    public void shouldPointStackTraceToPreviousInvocation() {
        inOrder.verify(mock, Mockito.times(2)).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mock).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("secondChunk(");
        }
    }

    @Test
    public void shouldPointToThirdInteractionBecauseAtLeastOnceUsed() {
        inOrder.verify(mock, Mockito.atLeastOnce()).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("thirdChunk(");
        }
    }

    @Test
    public void shouldPointToThirdChunkWhenTooLittleActualInvocations() {
        inOrder.verify(mock, Mockito.times(2)).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mock, Mockito.atLeastOnce()).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("thirdChunk(");
        }
    }

    @Test
    public void shouldPointToFourthChunkBecauseTooManyActualInvocations() {
        inOrder.verify(mock, Mockito.atLeastOnce()).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockTwo, Mockito.times(0)).simpleMethod(ArgumentMatchers.anyInt());
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
            assertThat(e).hasMessageContaining("fourthChunk(");
        }
    }
}

