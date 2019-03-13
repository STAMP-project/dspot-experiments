/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.concurrentmockito;


import org.junit.Test;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// this test always passes but please keep looking sys err
// this test should be run 10 times, manually
public class ThreadsShareGenerouslyStubbedMockTest extends TestBase {
    private IMethods mock;

    @Test
    public void shouldAllowVerifyingInThreads() throws Exception {
        for (int i = 0; i < 50; i++) {
            performTest();
        }
    }
}

