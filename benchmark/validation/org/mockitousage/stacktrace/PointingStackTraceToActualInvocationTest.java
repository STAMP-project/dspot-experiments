/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stacktrace;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// This is required to make sure stack trace is well filtered when runner is ON
@RunWith(MockitoJUnitRunner.class)
public class PointingStackTraceToActualInvocationTest extends TestBase {
    @Mock
    private IMethods mock;

    @Mock
    private IMethods mockTwo;

    @Test
    public void shouldPointToTooManyInvocationsChunkOnError() {
        try {
            Mockito.verify(mock, Mockito.times(0)).simpleMethod(1);
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
            assertThat(e).hasMessageContaining("first(");
        }
    }

    @Test
    public void shouldNotPointStackTracesToRunnersCode() {
        try {
            Mockito.verify(mock, Mockito.times(0)).simpleMethod(1);
            Assert.fail();
        } catch (NeverWantedButInvoked e) {
            assertThat(e.getMessage()).doesNotContain(".runners.");
        }
    }
}

