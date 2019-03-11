/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.concurrentmockito;


import org.junit.Test;
import org.mockito.Mock;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// this test exposes the problem most of the time
public class ThreadVerifiesContinuoslyInteractingMockTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void shouldAllowVerifyingInThreads() throws Exception {
        for (int i = 0; i < 100; i++) {
            performTest();
        }
    }
}

