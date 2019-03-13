/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.util.Timer;
import org.mockito.internal.verification.VerificationDataImpl;
import org.mockitoutil.TestBase;


public class TimeoutTest extends TestBase {
    @Mock
    VerificationMode mode;

    @Mock
    VerificationDataImpl data;

    @Mock
    Timer timer;

    private final MockitoAssertionError error = new MockitoAssertionError("");

    @Test
    public void should_pass_when_verification_passes() {
        Timeout t = new Timeout(1, mode, timer);
        Mockito.when(timer.isCounting()).thenReturn(true);
        Mockito.doNothing().when(mode).verify(data);
        t.verify(data);
        InOrder inOrder = Mockito.inOrder(timer);
        inOrder.verify(timer).start();
        inOrder.verify(timer).isCounting();
    }

    @Test
    public void should_fail_because_verification_fails() {
        Timeout t = new Timeout(1, mode, timer);
        Mockito.when(timer.isCounting()).thenReturn(true, true, true, false);
        Mockito.doThrow(error).doThrow(error).doThrow(error).when(mode).verify(data);
        try {
            t.verify(data);
            Assert.fail();
        } catch (MockitoAssertionError e) {
        }
        Mockito.verify(timer, Mockito.times(4)).isCounting();
    }

    @Test
    public void should_pass_even_if_first_verification_fails() {
        Timeout t = new Timeout(1, mode, timer);
        Mockito.when(timer.isCounting()).thenReturn(true, true, true, false);
        Mockito.doThrow(error).doThrow(error).doNothing().when(mode).verify(data);
        t.verify(data);
        Mockito.verify(timer, Mockito.times(3)).isCounting();
    }

    @Test
    public void should_try_to_verify_correct_number_of_times() {
        Timeout t = new Timeout(10, mode, timer);
        Mockito.doThrow(error).when(mode).verify(data);
        Mockito.when(timer.isCounting()).thenReturn(true, true, true, true, true, false);
        try {
            t.verify(data);
            Assert.fail();
        } catch (MockitoAssertionError e) {
        }
        Mockito.verify(mode, Mockito.times(5)).verify(data);
    }
}

