/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


/**
 * ignored since 'relaxed' in order verification is not implemented (too complex to bother, maybe later).
 */
public class RelaxedVerificationInOrderTest extends TestBase {
    private IMethods mockOne;

    private IMethods mockTwo;

    private IMethods mockThree;

    private InOrder inOrder;

    @Test
    public void shouldVerifyInOrderAllInvocations() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldVerifyInOrderAndBeRelaxed() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        Mockito.verifyNoMoreInteractions(mockThree);
    }

    @Test
    public void shouldAllowFirstChunkBeforeLastInvocation() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            Mockito.verifyNoMoreInteractions(mockTwo);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldAllowAllChunksBeforeLastInvocation() {
        inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldVerifyDetectFirstChunkOfInvocationThatExistInManyChunks() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        try {
            Mockito.verifyNoMoreInteractions(mockTwo);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldVerifyDetectAllChunksOfInvocationThatExistInManyChunks() {
        inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldVerifyInteractionsFromAllChunksWhenAtLeastOnceMode() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo);
        try {
            inOrder.verify(mockThree).simpleMethod(3);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldVerifyInteractionsFromFirstChunk() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        try {
            Mockito.verifyNoMoreInteractions(mockTwo);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test(expected = VerificationInOrderFailure.class)
    public void shouldFailVerificationOfNonFirstChunk() {
        inOrder.verify(mockTwo, Mockito.times(1)).simpleMethod(2);
    }

    @Test
    public void shouldPassOnCombinationOfTimesAndAtLeastOnce() {
        mockTwo.simpleMethod(2);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldPassOnEdgyCombinationOfTimesAndAtLeastOnce() {
        mockTwo.simpleMethod(2);
        mockThree.simpleMethod(3);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        Mockito.verifyNoMoreInteractions(mockThree);
    }

    @Test
    public void shouldVerifyInOrderMockTwoAndThree() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo, mockThree);
    }

    @Test
    public void shouldVerifyInOrderMockOneAndThree() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockThree);
    }

    @Test
    public void shouldVerifyInOrderOnlyTwoInvocations() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
    }

    @Test
    public void shouldVerifyInOrderOnlyMockTwo() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockTwo).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldVerifyMockTwoCalledTwice() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
    }

    @Test
    public void shouldVerifyMockTwoCalledAtLeastOnce() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
    }

    @Test(expected = WantedButNotInvoked.class)
    public void shouldFailOnWrongMethodCalledOnMockTwo() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).differentMethod();
    }

    @Test
    public void shouldAllowTimesZeroButOnlyInOrder() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        inOrder.verify(mockOne, Mockito.times(0)).simpleMethod(1);
        try {
            Mockito.verify(mockOne, Mockito.times(0)).simpleMethod(1);
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
        }
    }

    @Test
    public void shouldFailTimesZeroInOrder() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        try {
            inOrder.verify(mockThree, Mockito.times(0)).simpleMethod(3);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test(expected = VerificationInOrderFailure.class)
    public void shouldFailWhenMockTwoWantedZeroTimes() {
        inOrder.verify(mockTwo, Mockito.times(0)).simpleMethod(2);
    }

    @Test
    public void shouldVerifyLastInvocation() {
        inOrder.verify(mockOne).simpleMethod(4);
    }

    @Test
    public void shouldVerifySecondAndLastInvocation() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
    }

    @Test
    public void shouldVerifySecondAndLastInvocationWhenAtLeastOnceUsed() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
    }

    @Test
    public void shouldFailOnLastTwoInvocationsInWrongOrder() {
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnLastAndFirstInWrongOrder() {
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnWrongMethodAfterLastInvocation() {
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            inOrder.verify(mockOne).simpleMethod(999);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }
}

