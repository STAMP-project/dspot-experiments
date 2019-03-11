/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.exceptions.base;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class MockitoExceptionTest extends TestBase {
    @Test
    public void shouldKeepUnfilteredStackTrace() {
        try {
            throwIt();
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertEquals("throwIt", e.getUnfilteredStackTrace()[0].getMethodName());
        }
    }
}

