/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CustomMatcherDoesYieldCCETest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldNotThrowCCE() {
        mock.simpleMethod(new Object());
        try {
            // calling overloaded method so that matcher will be called with
            // different type
            Mockito.verify(mock).simpleMethod(ArgumentMatchers.argThat(isStringWithTextFoo()));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
        }
    }
}

