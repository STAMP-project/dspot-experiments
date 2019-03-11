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
package org.eclipse.jetty.util.thread;


import Invocable.InvocationType;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.util.thread.strategy.EatWhatYouKill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EatWhatYouKillTest {
    private EatWhatYouKill ewyk;

    @Test
    public void testExceptionThrownByTask() throws Exception {
        try (StacklessLogging stackLess = new StacklessLogging(EatWhatYouKill.class)) {
            AtomicReference<Throwable> detector = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(2);
            BlockingQueue<EatWhatYouKillTest.Task> tasks = new LinkedBlockingQueue<>();
            startEWYK(() -> {
                boolean proceed = detector.compareAndSet(null, new Throwable());
                if (proceed) {
                    try {
                        latch.countDown();
                        return tasks.poll(1, TimeUnit.SECONDS);
                    } catch ( x) {
                        x.printStackTrace();
                        return null;
                    } finally {
                        detector.set(null);
                    }
                } else {
                    return null;
                }
            });
            // Start production in another thread.
            ewyk.dispatch();
            tasks.offer(new EatWhatYouKillTest.Task(() -> {
                try {
                    // While thread1 runs this task, simulate
                    // that thread2 starts producing.
                    ewyk.dispatch();
                    // Wait for thread2 to block in produce().
                    latch.await();
                    // Throw to verify that exceptions are handled correctly.
                    throw new RuntimeException();
                } catch ( x) {
                    x.printStackTrace();
                }
            }, InvocationType.BLOCKING));
            // Wait until EWYK is idle.
            while (!(ewyk.isIdle()))
                Thread.sleep(10);

            Assertions.assertNull(detector.get());
        }
    }

    private static class Task implements Runnable , Invocable {
        private final Runnable task;

        private final InvocationType invocationType;

        private Task(Runnable task, InvocationType invocationType) {
            this.task = task;
            this.invocationType = invocationType;
        }

        @Override
        public void run() {
            task.run();
        }

        @Override
        public InvocationType getInvocationType() {
            return invocationType;
        }
    }
}

