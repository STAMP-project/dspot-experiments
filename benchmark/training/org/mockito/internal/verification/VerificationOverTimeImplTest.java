/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;


import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockito.verification.VerificationMode;


public class VerificationOverTimeImplTest {
    @Mock
    private VerificationMode delegate;

    private VerificationOverTimeImpl impl;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void should_return_on_success() {
        impl.verify(null);
        Mockito.verify(delegate).verify(null);
    }

    @Test
    public void should_throw_mockito_assertion_error() {
        MockitoAssertionError toBeThrown = new MockitoAssertionError("message");
        exception.expect(CoreMatchers.is(toBeThrown));
        Mockito.doThrow(toBeThrown).when(delegate).verify(null);
        impl.verify(null);
    }

    @Test
    public void should_deal_with_junit_assertion_error() {
        ArgumentsAreDifferent toBeThrown = new ArgumentsAreDifferent("message", "wanted", "actual");
        exception.expect(CoreMatchers.is(toBeThrown));
        exception.expectMessage("message");
        Mockito.doThrow(toBeThrown).when(delegate).verify(null);
        impl.verify(null);
    }

    @Test
    public void should_not_wrap_other_exceptions() {
        RuntimeException toBeThrown = new RuntimeException();
        exception.expect(CoreMatchers.is(toBeThrown));
        Mockito.doThrow(toBeThrown).when(delegate).verify(null);
        impl.verify(null);
    }
}

