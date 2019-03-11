/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.exceptions.base;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class MockitoAssertionErrorTest extends TestBase {
    @Test
    public void shouldKeepUnfilteredStackTrace() {
        try {
            throwIt();
            Assert.fail();
        } catch (MockitoAssertionError e) {
            Assert.assertEquals("throwIt", e.getUnfilteredStackTrace()[0].getMethodName());
        }
    }

    @Test
    public void should_prepend_message_to_original() {
        MockitoAssertionError original = new MockitoAssertionError("original message");
        MockitoAssertionError errorWithPrependedMessage = new MockitoAssertionError(original, "new message");
        Assert.assertEquals("new message\noriginal message", errorWithPrependedMessage.getMessage());
    }
}

