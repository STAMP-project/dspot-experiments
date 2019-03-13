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
package org.eclipse.jetty.io;


import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CyclicTimeoutTest {
    private volatile boolean _expired;

    private ScheduledExecutorScheduler _timer = new ScheduledExecutorScheduler();

    private CyclicTimeout _timeout;

    @Test
    public void testReschedule() throws Exception {
        for (int i = 0; i < 20; i++) {
            Thread.sleep(100);
            Assertions.assertTrue(_timeout.schedule(1000, TimeUnit.MILLISECONDS));
        }
        Assertions.assertFalse(_expired);
    }

    @Test
    public void testExpire() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            Assertions.assertTrue(_timeout.schedule(1000, TimeUnit.MILLISECONDS));
        }
        Thread.sleep(1500);
        Assertions.assertTrue(_expired);
    }

    @Test
    public void testCancel() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            Assertions.assertTrue(_timeout.schedule(1000, TimeUnit.MILLISECONDS));
        }
        _timeout.cancel();
        Thread.sleep(1500);
        Assertions.assertFalse(_expired);
    }

    @Test
    public void testShorten() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            Assertions.assertTrue(_timeout.schedule(1000, TimeUnit.MILLISECONDS));
        }
        Assertions.assertTrue(_timeout.schedule(100, TimeUnit.MILLISECONDS));
        Thread.sleep(400);
        Assertions.assertTrue(_expired);
    }

    @Test
    public void testLengthen() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            Assertions.assertTrue(_timeout.schedule(1000, TimeUnit.MILLISECONDS));
        }
        Assertions.assertTrue(_timeout.schedule(10000, TimeUnit.MILLISECONDS));
        Thread.sleep(1500);
        Assertions.assertFalse(_expired);
    }

    @Test
    public void testMultiple() throws Exception {
        Thread.sleep(1500);
        Assertions.assertTrue(_expired);
        _expired = false;
        Assertions.assertFalse(_timeout.schedule(500, TimeUnit.MILLISECONDS));
        Thread.sleep(1000);
        Assertions.assertTrue(_expired);
        _expired = false;
        _timeout.schedule(500, TimeUnit.MILLISECONDS);
        Thread.sleep(1000);
        Assertions.assertTrue(_expired);
    }
}

