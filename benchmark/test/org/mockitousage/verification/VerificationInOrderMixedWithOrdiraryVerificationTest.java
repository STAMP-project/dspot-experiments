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
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class VerificationInOrderMixedWithOrdiraryVerificationTest extends TestBase {
    private IMethods mockOne;

    private IMethods mockTwo;

    private IMethods mockThree;

    private InOrder inOrder;

    @Test
    public void shouldMixVerificationInOrderAndOrdinaryVerification() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockThree).simpleMethod(4);
        Mockito.verify(mockTwo).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldAllowOrdinarilyVerifyingMockPassedToInOrderObject() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        Mockito.verify(mockThree).simpleMethod(3);
        Mockito.verify(mockThree).simpleMethod(4);
        Mockito.verify(mockTwo).simpleMethod(2);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldAllowRedundantVerifications() {
        Mockito.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        Mockito.verify(mockTwo).simpleMethod(2);
        Mockito.verify(mockThree).simpleMethod(3);
        Mockito.verify(mockThree).simpleMethod(4);
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockThree).simpleMethod(4);
        Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
    }

    @Test
    public void shouldFailOnNoMoreInteractions() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockThree).simpleMethod(3);
        inOrder.verify(mockThree).simpleMethod(4);
        try {
            Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldFailOnNoMoreInteractionsOnMockVerifiedInOrder() {
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockThree).simpleMethod(3);
        Mockito.verify(mockTwo).simpleMethod(2);
        try {
            Mockito.verifyNoMoreInteractions(mockOne, mockTwo, mockThree);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldAllowOneMethodVerifiedInOrder() {
        Mockito.verify(mockTwo).simpleMethod(2);
        Mockito.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
        inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
    }

    @Test
    public void shouldFailOnLastInvocationTooEarly() {
        inOrder.verify(mockThree).simpleMethod(4);
        Mockito.verify(mockThree).simpleMethod(4);
        Mockito.verify(mockTwo).simpleMethod(2);
        try {
            inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test(expected = MockitoException.class)
    public void shouldScreamWhenUnfamiliarMockPassedToInOrderObject() {
        inOrder.verify(mockTwo, Mockito.atLeastOnce()).simpleMethod(1);
    }

    @Test
    public void shouldUseEqualsToVerifyMethodArguments() {
        mockOne = Mockito.mock(IMethods.class);
        String textOne = "test";
        String textTwo = new String(textOne);
        Assert.assertEquals(textOne, textTwo);
        Assert.assertNotSame(textOne, textTwo);
        mockOne.simpleMethod(textOne);
        mockOne.simpleMethod(textTwo);
        Mockito.verify(mockOne, Mockito.times(2)).simpleMethod(textOne);
        inOrder = Mockito.inOrder(mockOne);
        inOrder.verify(mockOne, Mockito.times(2)).simpleMethod(textOne);
    }

    @Test
    public void shouldUseEqualsToVerifyMethodVarargs() {
        mockOne = Mockito.mock(IMethods.class);
        String textOne = "test";
        String textTwo = new String(textOne);
        Assert.assertEquals(textOne, textTwo);
        Assert.assertNotSame(textOne, textTwo);
        mockOne.varargsObject(1, textOne, textOne);
        mockOne.varargsObject(1, textTwo, textTwo);
        Mockito.verify(mockOne, Mockito.times(2)).varargsObject(1, textOne, textOne);
        inOrder = Mockito.inOrder(mockOne);
        inOrder.verify(mockOne, Mockito.times(2)).varargsObject(1, textOne, textOne);
    }
}

