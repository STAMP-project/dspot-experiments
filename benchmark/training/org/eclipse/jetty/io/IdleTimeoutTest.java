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
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.util.thread.TimerScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class IdleTimeoutTest {
    volatile boolean _open;

    volatile TimeoutException _expired;

    TimerScheduler _timer;

    IdleTimeout _timeout;

    @Test
    public void testNotIdle() throws Exception {
        for (int i = 0; i < 20; i++) {
            Thread.sleep(100);
            _timeout.notIdle();
        }
        Assertions.assertNull(_expired);
    }

    @Test
    public void testIdle() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            _timeout.notIdle();
        }
        Thread.sleep(1500);
        Assertions.assertNotNull(_expired);
    }

    @Test
    public void testClose() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            _timeout.notIdle();
        }
        _timeout.onClose();
        Thread.sleep(1500);
        Assertions.assertNull(_expired);
    }

    @Test
    public void testClosed() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            _timeout.notIdle();
        }
        _open = false;
        Thread.sleep(1500);
        Assertions.assertNull(_expired);
    }

    @Test
    public void testShorten() throws Exception {
        _timeout.setIdleTimeout(2000);
        for (int i = 0; i < 30; i++) {
            Thread.sleep(100);
            _timeout.notIdle();
        }
        Assertions.assertNull(_expired);
        _timeout.setIdleTimeout(100);
        long start = System.nanoTime();
        while (((_expired) == null) && ((TimeUnit.NANOSECONDS.toSeconds(((System.nanoTime()) - start))) < 5))
            Thread.sleep(200);

        Assertions.assertNotNull(_expired);
    }

    @Test
    public void testLengthen() throws Exception {
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            _timeout.notIdle();
        }
        _timeout.setIdleTimeout(10000);
        Thread.sleep(1500);
        Assertions.assertNull(_expired);
    }

    @Test
    public void testMultiple() throws Exception {
        Thread.sleep(1500);
        Assertions.assertNotNull(_expired);
        _expired = null;
        Thread.sleep(1000);
        Assertions.assertNotNull(_expired);
    }
}

