/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stacktrace;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ClickableStackTracesTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldShowActualAndExpectedWhenArgumentsAreDifferent() {
        callMethodOnMock("foo");
        try {
            verifyTheMock(1, "not foo");
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            assertThat(e).hasMessageContaining("callMethodOnMock(").hasMessageContaining("verifyTheMock(");
        }
    }
}

