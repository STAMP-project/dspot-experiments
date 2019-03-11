/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LeakDetectorTest {
    private LeakDetector<Object> leakDetector;

    @Test
    public void testResourceAcquiredAndReleased() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        prepare(new LeakDetector<Object>() {
            @Override
            protected void leaked(LeakInfo leakInfo) {
                latch.countDown();
            }
        });
        // Block to make sure "resource" goes out of scope
        {
            Object resource = new Object();
            leakDetector.acquired(resource);
            leakDetector.released(resource);
        }
        gc();
        Assertions.assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testResourceAcquiredAndNotReleased() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        prepare(new LeakDetector<Object>() {
            @Override
            protected void leaked(LeakInfo leakInfo) {
                latch.countDown();
            }
        });
        leakDetector.acquired(new Object());
        gc();
        Assertions.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }
}

