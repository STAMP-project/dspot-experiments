/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class SelectedMocksInOrderVerificationTest extends TestBase {
    private IMethods mockOne;

    private IMethods mockTwo;

    private IMethods mockThree;

    @Test
    public void shouldVerifyAllInvocationsInOrder() {
        InOrder inOrder = Mockito.inOrder(mockOne, mockTwo, mockThree);
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldVerifyInOrderMockTwoAndThree() {
        InOrder inOrder = Mockito.inOrder(mockTwo, mockThree);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo, mockThree);
    }

    @Test
    public void shouldVerifyInOrderMockOneAndThree() {
        InOrder inOrder = Mockito.inOrder(mockOne, mockThree);
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockThree);
    }

    @Test
    public void shouldVerifyMockOneInOrder() {
        InOrder inOrder = Mockito.inOrder(mockOne);
        inOrder.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockOne).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne);
    }

    @Test
    public void shouldFailVerificationForMockOne() {
        InOrder inOrder = Mockito.inOrder(mockOne);
        inOrder.verify(mockOne).simpleMethod(1);
        try {
            inOrder.verify(mockOne).differentMethod();
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldFailVerificationForMockOneBecauseOfWrongOrder() {
        InOrder inOrder = Mockito.inOrder(mockOne);
        inOrder.verify(mockOne).simpleMethod(4);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldVerifyMockTwoWhenThreeTimesUsed() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldVerifyMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldFailVerificationForMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        try {
            inOrder.verify(mockTwo).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldThrowNoMoreInvocationsForMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        try {
            inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldThrowTooLittleInvocationsForMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        try {
            inOrder.verify(mockTwo, Mockito.times(4)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldThrowTooManyInvocationsForMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        try {
            inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldAllowThreeTimesOnMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo);
        inOrder.verify(mockTwo, Mockito.times(3)).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo);
    }

    @Test
    public void shouldVerifyMockTwoCompletely() {
        InOrder inOrder = Mockito.inOrder(mockTwo, mockThree);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockTwo).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockTwo, mockThree);
    }

    @Test
    public void shouldAllowTwoTimesOnMockTwo() {
        InOrder inOrder = Mockito.inOrder(mockTwo, mockThree);
        inOrder.verify(mockTwo, Mockito.times(2)).simpleMethod(2);
        try {
            Mockito.verifyNoMoreInteractions(mockTwo);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }
}

