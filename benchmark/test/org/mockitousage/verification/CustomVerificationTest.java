/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CustomVerificationTest extends TestBase {
    @Mock
    IMethods mock;

    @Test
    public void custom_verification_with_old_api() {
        // given:
        mock.simpleMethod("a", 10);
        // expect:
        Mockito.verify(mock, ignoreParametersUsingOldApi()).simpleMethod();
        try {
            Mockito.verify(mock, ignoreParametersUsingOldApi()).otherMethod();
            Assert.fail();
        } catch (MockitoAssertionError e) {
        }
    }
}

