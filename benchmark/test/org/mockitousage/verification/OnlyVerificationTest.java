/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitoutil.TestBase;


public class OnlyVerificationTest extends TestBase {
    @Mock
    private List<Object> mock;

    @Mock
    private List<Object> mock2;

    @Test
    public void shouldVerifyMethodWasInvokedExclusively() {
        mock.clear();
        Mockito.verify(mock, Mockito.only()).clear();
    }

    @Test
    public void shouldVerifyMethodWasInvokedExclusivelyWithMatchersUsage() {
        mock.get(0);
        Mockito.verify(mock, Mockito.only()).get(ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldFailIfMethodWasNotInvoked() {
        mock.clear();
        try {
            Mockito.verify(mock, Mockito.only()).get(0);
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }

    @Test
    public void shouldFailIfMethodWasInvokedMoreThanOnce() {
        mock.clear();
        mock.clear();
        try {
            Mockito.verify(mock, Mockito.only()).clear();
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldFailIfMethodWasInvokedButWithDifferentArguments() {
        mock.get(0);
        mock.get(2);
        try {
            Mockito.verify(mock, Mockito.only()).get(999);
            Assert.fail();
        } catch (WantedButNotInvoked e) {
        }
    }

    @Test
    public void shouldFailIfExtraMethodWithDifferentArgsFound() {
        mock.get(0);
        mock.get(2);
        try {
            Mockito.verify(mock, Mockito.only()).get(2);
            Assert.fail();
        } catch (NoInteractionsWanted e) {
        }
    }

    @Test
    public void shouldVerifyMethodWasInvokedExclusivelyWhenTwoMocksInUse() {
        mock.clear();
        mock2.get(0);
        Mockito.verify(mock, Mockito.only()).clear();
        Mockito.verify(mock2, Mockito.only()).get(0);
    }
}

