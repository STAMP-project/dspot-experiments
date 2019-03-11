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


import IteratingCallback.Action.SCHEDULED;
import IteratingCallback.Action.SUCCEEDED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.thread.Scheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static Action.IDLE;
import static Action.SCHEDULED;
import static Action.SUCCEEDED;


public class IteratingCallbackTest {
    private Scheduler scheduler;

    @Test
    public void testNonWaitingProcess() throws Exception {
        IteratingCallbackTest.TestCB cb = new IteratingCallbackTest.TestCB() {
            int i = 10;

            @Override
            protected Action process() throws Exception {
                (processed)++;
                if (((i)--) > 1) {
                    succeeded();// fake a completed IO operation

                    return SCHEDULED;
                }
                return SUCCEEDED;
            }
        };
        iterate();
        Assertions.assertTrue(cb.waitForComplete());
        Assertions.assertEquals(10, cb.processed);
    }

    @Test
    public void testWaitingProcess() throws Exception {
        IteratingCallbackTest.TestCB cb = new IteratingCallbackTest.TestCB() {
            int i = 4;

            @Override
            protected Action process() throws Exception {
                (processed)++;
                if (((i)--) > 1) {
                    scheduler.schedule(successTask, 50, TimeUnit.MILLISECONDS);
                    return Action.SCHEDULED;
                }
                return Action.SUCCEEDED;
            }
        };
        iterate();
        Assertions.assertTrue(cb.waitForComplete());
        Assertions.assertEquals(4, cb.processed);
    }

    @Test
    public void testWaitingProcessSpuriousIterate() throws Exception {
        final IteratingCallbackTest.TestCB cb = new IteratingCallbackTest.TestCB() {
            int i = 4;

            @Override
            protected Action process() throws Exception {
                (processed)++;
                if (((i)--) > 1) {
                    scheduler.schedule(successTask, 50, TimeUnit.MILLISECONDS);
                    return Action.SCHEDULED;
                }
                return Action.SUCCEEDED;
            }
        };
        iterate();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                iterate();
                if (!(isSucceeded()))
                    scheduler.schedule(this, 50, TimeUnit.MILLISECONDS);

            }
        }, 49, TimeUnit.MILLISECONDS);
        Assertions.assertTrue(cb.waitForComplete());
        Assertions.assertEquals(4, cb.processed);
    }

    @Test
    public void testNonWaitingProcessFailure() throws Exception {
        IteratingCallbackTest.TestCB cb = new IteratingCallbackTest.TestCB() {
            int i = 10;

            @Override
            protected Action process() throws Exception {
                (processed)++;
                if (((i)--) > 1) {
                    if ((i) > 5)
                        succeeded();
                    else// fake a completed IO operation

                        failed(new Exception("testing"));

                    return Action.SCHEDULED;
                }
                return Action.SUCCEEDED;
            }
        };
        iterate();
        Assertions.assertFalse(cb.waitForComplete());
        Assertions.assertEquals(5, cb.processed);
    }

    @Test
    public void testWaitingProcessFailure() throws Exception {
        IteratingCallbackTest.TestCB cb = new IteratingCallbackTest.TestCB() {
            int i = 4;

            @Override
            protected Action process() throws Exception {
                (processed)++;
                if (((i)--) > 1) {
                    scheduler.schedule(((i) > 2 ? successTask : failTask), 50, TimeUnit.MILLISECONDS);
                    return Action.SCHEDULED;
                }
                return Action.SUCCEEDED;
            }
        };
        iterate();
        Assertions.assertFalse(cb.waitForComplete());
        Assertions.assertEquals(2, cb.processed);
    }

    @Test
    public void testIdleWaiting() throws Exception {
        final CountDownLatch idle = new CountDownLatch(1);
        IteratingCallbackTest.TestCB cb = new IteratingCallbackTest.TestCB() {
            int i = 5;

            @Override
            protected Action process() {
                (processed)++;
                switch ((i)--) {
                    case 5 :
                        succeeded();
                        return Action.SCHEDULED;
                    case 4 :
                        scheduler.schedule(successTask, 5, TimeUnit.MILLISECONDS);
                        return Action.SCHEDULED;
                    case 3 :
                        scheduler.schedule(new Runnable() {
                            @Override
                            public void run() {
                                idle.countDown();
                            }
                        }, 5, TimeUnit.MILLISECONDS);
                        return IDLE;
                    case 2 :
                        succeeded();
                        return Action.SCHEDULED;
                    case 1 :
                        scheduler.schedule(successTask, 5, TimeUnit.MILLISECONDS);
                        return Action.SCHEDULED;
                    case 0 :
                        return Action.SUCCEEDED;
                    default :
                        throw new IllegalStateException();
                }
            }
        };
        iterate();
        idle.await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(isIdle());
        iterate();
        Assertions.assertTrue(cb.waitForComplete());
        Assertions.assertEquals(6, cb.processed);
    }

    @Test
    public void testCloseDuringProcessingReturningScheduled() throws Exception {
        testCloseDuringProcessing(SCHEDULED);
    }

    @Test
    public void testCloseDuringProcessingReturningSucceeded() throws Exception {
        testCloseDuringProcessing(SUCCEEDED);
    }

    private abstract static class TestCB extends IteratingCallback {
        protected Runnable successTask = new Runnable() {
            @Override
            public void run() {
                succeeded();
            }
        };

        protected Runnable failTask = new Runnable() {
            @Override
            public void run() {
                failed(new Exception("testing failure"));
            }
        };

        protected CountDownLatch completed = new CountDownLatch(1);

        protected int processed = 0;

        @Override
        protected void onCompleteSuccess() {
            completed.countDown();
        }

        @Override
        public void onCompleteFailure(Throwable x) {
            completed.countDown();
        }

        boolean waitForComplete() throws InterruptedException {
            completed.await(10, TimeUnit.SECONDS);
            return isSucceeded();
        }
    }
}

