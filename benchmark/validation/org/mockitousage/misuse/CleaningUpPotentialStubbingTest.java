/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.misuse;


import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CleaningUpPotentialStubbingTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldResetOngoingStubbingOnVerify() {
        // first test
        mock.booleanReturningMethod();
        Mockito.verify(mock).booleanReturningMethod();
        // second test
        assertOngoingStubbingIsReset();
    }

    @Test
    public void shouldResetOngoingStubbingOnInOrder() {
        mock.booleanReturningMethod();
        InOrder inOrder = Mockito.inOrder(mock);
        inOrder.verify(mock).booleanReturningMethod();
        assertOngoingStubbingIsReset();
    }

    @Test
    public void shouldResetOngoingStubbingOnDoReturn() {
        mock.booleanReturningMethod();
        Mockito.doReturn(false).when(mock).booleanReturningMethod();
        assertOngoingStubbingIsReset();
    }
}

