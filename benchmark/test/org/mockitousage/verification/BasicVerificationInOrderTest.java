/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class BasicVerificationInOrderTest extends TestBase {
    private IMethods mockOne;

    private IMethods mockTwo;

    private IMethods mockThree;

    private InOrder inOrder;

    @Test
    public void shouldVerifyInOrder() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldVerifyInOrderUsingAtLeastOnce() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldVerifyInOrderWhenExpectingSomeInvocationsToBeCalledZeroTimes() {
        inOrder.verify(mockOne, Mockito.times(0)).oneArg(false);
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockTwo, Mockito.times(0)).simpleMethod(22);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        inOrder.verify(mockThree, Mockito.times(0)).oneArg(false);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldFailWhenFirstMockCalledTwice() {
        inOrder.verify(mockOne).simpleMethod(1);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailWhenLastMockCalledTwice() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            inOrder.verify(mockOne).simpleMethod(4);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test(expected = VerificationInOrderFailure.class)
    public void shouldFailOnFirstMethodBecauseOneInvocationWanted() {
        inOrder.verify(mockOne, Mockito.times(0)).simpleMethod(1);
    }

    @Test(expected = VerificationInOrderFailure.class)
    public void shouldFailOnFirstMethodBecauseOneInvocationWantedAgain() {
        inOrder.verify(mockOne, Mockito.times(2)).simpleMethod(1);
    }

    @Test
    public void shouldFailOnSecondMethodBecauseFourInvocationsWanted() {
        inOrder.verify(mockOne, Mockito.times(1)).simpleMethod(1);
        try {
            inOrder.verify(mockTwo, Mockito.times(4)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnSecondMethodBecauseTwoInvocationsWantedAgain() {
        inOrder.verify(mockOne, Mockito.times(1)).simpleMethod(1);
        try {
            inOrder.verify(mockTwo, Mockito.times(0)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnLastMethodBecauseOneInvocationWanted() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree, Mockito.atLeastOnce()).simpleMethod(3);
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        try {
            inOrder.verify(mockOne, Mockito.times(0)).simpleMethod(4);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnLastMethodBecauseOneInvocationWantedAgain() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree, Mockito.atLeastOnce()).simpleMethod(3);
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        try {
            inOrder.verify(mockOne, Mockito.times(2)).simpleMethod(4);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    /* ------------- */
    @Test(expected = ArgumentsAreDifferent.class)
    public void shouldFailOnFirstMethodBecauseDifferentArgsWanted() {
        inOrder.verify(mockOne).simpleMethod(100);
    }

    @Test(expected = WantedButNotInvoked.class)
    public void shouldFailOnFirstMethodBecauseDifferentMethodWanted() {
        inOrder.verify(mockOne).oneArg(true);
    }

    @Test
    public void shouldFailOnSecondMethodBecauseDifferentArgsWanted() {
        inOrder.verify(mockOne).simpleMethod(1);
        try {
            inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod((-999));
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnSecondMethodBecauseDifferentMethodWanted() {
        inOrder.verify(mockOne, Mockito.times(1)).simpleMethod(1);
        try {
            inOrder.verify(mockTwo, Mockito.times(2)).oneArg(true);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnLastMethodBecauseDifferentArgsWanted() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        try {
            inOrder.verify(mockOne).simpleMethod((-666));
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnLastMethodBecauseDifferentMethodWanted() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        try {
            inOrder.verify(mockOne).oneArg(false);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    /* -------------- */
    @Test
    public void shouldFailWhenLastMethodVerifiedFirst() {
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailWhenMiddleMethodVerifiedFirst() {
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailWhenMiddleMethodVerifiedFirstInAtLeastOnceMode() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailOnVerifyNoMoreInteractions() {
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        try {
            Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test(expected = NoInteractionsWanted.class)
    public void shouldFailOnVerifyZeroInteractions() {
        Mockito.verifyZeroInteractions(mockOne);
    }

    @SuppressWarnings({ "all", "CheckReturnValue", "MockitoUsage" })
    @Test(expected = MockitoException.class)
    public void shouldScreamWhenNullPassed() {
        Mockito.inOrder(((Object[]) (null)));
    }
}

