/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class VerificationInOrderTest extends TestBase {
    private IMethods mockOne;

    private IMethods mockTwo;

    private IMethods mockThree;

    private InOrder inOrder;

    @Test
    public void shouldVerifySingleMockInOrderAndNotInOrder() {
        mockOne = Mockito.mock(IMethods.class);
        inOrder = Mockito.inOrder(mockOne);
        mockOne.simpleMethod(1);
        mockOne.simpleMethod(2);
        Mockito.verify(mockOne).simpleMethod(2);
        Mockito.verify(mockOne).simpleMethod(1);
        inOrder.verify(mockOne).simpleMethod(2);
        try {
            inOrder.verify(mockOne).simpleMethod(1);
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldMessagesPointToProperMethod() {
        mockTwo.differentMethod();
        mockOne.simpleMethod();
        try {
            inOrder.verify(mockOne, Mockito.atLeastOnce()).differentMethod();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            assertThat(e).hasMessageContaining("differentMethod()");
        }
    }

    @Test
    public void shouldVerifyInOrderWhenTwoChunksAreEqual() {
        mockOne.simpleMethod();
        mockOne.simpleMethod();
        mockTwo.differentMethod();
        mockOne.simpleMethod();
        mockOne.simpleMethod();
        inOrder.verify(mockOne, Mockito.times(2)).simpleMethod();
        inOrder.verify(mockTwo).differentMethod();
        inOrder.verify(mockOne, Mockito.times(2)).simpleMethod();
        try {
            inOrder.verify(mockOne, Mockito.atLeastOnce()).simpleMethod();
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }

    @Test
    public void shouldVerifyInOrderUsingMatcher() {
        mockOne.simpleMethod(1);
        mockOne.simpleMethod(2);
        mockTwo.differentMethod();
        mockOne.simpleMethod(3);
        mockOne.simpleMethod(4);
        Mockito.verify(mockOne, Mockito.times(4)).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockOne, Mockito.times(2)).simpleMethod(ArgumentMatchers.anyInt());
        inOrder.verify(mockTwo).differentMethod();
        inOrder.verify(mockOne, Mockito.times(2)).simpleMethod(ArgumentMatchers.anyInt());
        try {
            inOrder.verify(mockOne, Mockito.times(3)).simpleMethod(ArgumentMatchers.anyInt());
            Assert.fail();
        } catch (VerificationInOrderFailure e) {
        }
    }
}

