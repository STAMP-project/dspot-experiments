/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.concurrentmockito;


import org.junit.Test;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ThreadsShareAMockTest extends TestBase {
    private IMethods mock;

    @Test
    public void shouldAllowVerifyingInThreads() throws Exception {
        for (int i = 0; i < 100; i++) {
            performTest();
        }
    }
}

