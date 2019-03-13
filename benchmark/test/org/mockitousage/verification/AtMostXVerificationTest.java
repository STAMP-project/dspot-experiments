/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.MoreThanAllowedActualInvocations;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockitoutil.TestBase;


public class AtMostXVerificationTest extends TestBase {
    @Mock
    private List<String> mock;

    @Test
    public void shouldVerifyAtMostXTimes() throws Exception {
        mock.clear();
        mock.clear();
        Mockito.verify(mock, Mockito.atMost(2)).clear();
        Mockito.verify(mock, Mockito.atMost(3)).clear();
        try {
            Mockito.verify(mock, Mockito.atMost(1)).clear();
            Assert.fail();
        } catch (MoreThanAllowedActualInvocations e) {
        }
    }

    @Test
    public void shouldWorkWithArgumentMatchers() throws Exception {
        mock.add("one");
        Mockito.verify(mock, Mockito.atMost(5)).add(ArgumentMatchers.anyString());
        try {
            Mockito.verify(mock, Mockito.atMost(0)).add(ArgumentMatchers.anyString());
            Assert.fail();
        } catch (MoreThanAllowedActualInvocations e) {
        }
    }

    @Test
    public void shouldNotAllowNegativeNumber() throws Exception {
        try {
            Mockito.verify(mock, Mockito.atMost((-1))).clear();
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertEquals("Negative value is not allowed here", e.getMessage());
        }
    }

    @Test
    public void shouldPrintDecentMessage() throws Exception {
        mock.clear();
        mock.clear();
        try {
            Mockito.verify(mock, Mockito.atMost(1)).clear();
            Assert.fail();
        } catch (MoreThanAllowedActualInvocations e) {
            Assert.assertEquals("\nWanted at most 1 time but was 2", e.getMessage());
        }
    }

    @Test
    public void shouldNotAllowInOrderMode() throws Exception {
        mock.clear();
        InOrder inOrder = Mockito.inOrder(mock);
        try {
            inOrder.verify(mock, Mockito.atMost(1)).clear();
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertEquals("AtMost is not implemented to work with InOrder", e.getMessage());
        }
    }

    @Test
    public void shouldMarkInteractionsAsVerified() throws Exception {
        mock.clear();
        mock.clear();
        Mockito.verify(mock, Mockito.atMost(3)).clear();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void shouldDetectUnverifiedInMarkInteractionsAsVerified() throws Exception {
        mock.clear();
        mock.clear();
        undesiredInteraction();
        Mockito.verify(mock, Mockito.atMost(3)).clear();
        try {
            Mockito.verifyNoMoreInteractions(mock);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
            assertThat(e).hasMessageContaining("undesiredInteraction(");
        }
    }
}

