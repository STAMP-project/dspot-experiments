/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;


public class VerificationWithDescriptionTest {
    @Mock
    private List<?> mock;

    @Test
    public void assertion_error_message_should_start_with_the_custom_specified_message() {
        String failureMessage = "Verification failed!";
        try {
            Mockito.verify(mock, Mockito.description(failureMessage)).clear();
            fail("Should not have made it this far");
        } catch (MockitoAssertionError e) {
            Assert.assertTrue(e.getMessage().startsWith(failureMessage));
        }
    }
}

