/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.fibers;


import co.paralleluniverse.common.test.TestUtil;
import co.paralleluniverse.strands.SuspendableRunnable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class FiberAsyncTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    private FiberScheduler scheduler;

    public FiberAsyncTest() {
        scheduler = new FiberForkJoinScheduler("test", 4, null, false);
    }

    interface MyCallback {
        void call(String str);

        void fail(RuntimeException e);
    }

    interface Service {
        void registerCallback(FiberAsyncTest.MyCallback callback);
    }

    final FiberAsyncTest.Service syncService = new FiberAsyncTest.Service() {
        @Override
        public void registerCallback(FiberAsyncTest.MyCallback callback) {
            callback.call("sync result!");
        }
    };

    final FiberAsyncTest.Service badSyncService = new FiberAsyncTest.Service() {
        @Override
        public void registerCallback(FiberAsyncTest.MyCallback callback) {
            callback.fail(new RuntimeException("sync exception!"));
        }
    };

    final ExecutorService executor = Executors.newFixedThreadPool(1);

    final FiberAsyncTest.Service asyncService = new FiberAsyncTest.Service() {
        @Override
        public void registerCallback(final FiberAsyncTest.MyCallback callback) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20);
                        callback.call("async result!");
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    };

    final FiberAsyncTest.Service longAsyncService = new FiberAsyncTest.Service() {
        @Override
        public void registerCallback(final FiberAsyncTest.MyCallback callback) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        callback.call("async result!");
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    };

    final FiberAsyncTest.Service badAsyncService = new FiberAsyncTest.Service() {
        @Override
        public void registerCallback(final FiberAsyncTest.MyCallback callback) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20);
                        callback.fail(new RuntimeException("async exception!"));
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    };

    abstract static class MyFiberAsync extends FiberAsync<String, RuntimeException> implements FiberAsyncTest.MyCallback {
        private final Fiber fiber;

        public MyFiberAsync() {
            this.fiber = Fiber.currentFiber();
        }

        @Override
        public void call(String str) {
            super.asyncCompleted(str);
        }

        @Override
        public void fail(RuntimeException e) {
            super.asyncFailed(e);
        }
    }

    @Test
    public void testSyncCallback() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testSyncCallbackException() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testAsyncCallback() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testAsyncCallbackException() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testAsyncCallbackExceptionInRequestAsync() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testTimedAsyncCallbackNoTimeout() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testTimedAsyncCallbackWithTimeout() throws Exception {
        final Fiber fiber = start();
        fiber.join();
    }

    @Test
    public void testInterrupt1() throws Exception {
        final Fiber fiber = start();
        fiber.interrupt();
        fiber.join();
    }

    @Test
    public void testInterrupt2() throws Exception {
        final Fiber fiber = start();
        Thread.sleep(100);
        fiber.interrupt();
        fiber.join();
    }

    @Test
    public void whenCancelRunBlockingInterruptExecutingThread() throws Exception {
        final AtomicBoolean started = new AtomicBoolean();
        final AtomicBoolean interrupted = new AtomicBoolean();
        Fiber fiber = new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                FiberAsync.runBlocking(Executors.newSingleThreadExecutor(), new co.paralleluniverse.common.util.CheckedCallable<Void, RuntimeException>() {
                    @Override
                    public Void call() throws RuntimeException {
                        started.set(true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            interrupted.set(true);
                        }
                        return null;
                    }
                });
            }
        });
        fiber.start();
        Thread.sleep(100);
        fiber.cancel(true);
        try {
            fiber.join(5, TimeUnit.MILLISECONDS);
            Assert.fail("InterruptedException not thrown");
        } catch (ExecutionException e) {
            if (!((e.getCause()) instanceof InterruptedException))
                Assert.fail("InterruptedException not thrown");

        }
        Thread.sleep(100);
        Assert.assertThat(started.get(), CoreMatchers.is(true));
        Assert.assertThat(interrupted.get(), CoreMatchers.is(true));
    }

    @Test
    public void testRunBlocking() throws Exception {
        final Fiber fiber = new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                String res = FiberAsync.runBlocking(Executors.newCachedThreadPool(), new co.paralleluniverse.common.util.CheckedCallable<String, InterruptedException>() {
                    public String call() throws InterruptedException {
                        Thread.sleep(300);
                        return "ok";
                    }
                });
                Assert.assertThat(res, CoreMatchers.equalTo("ok"));
            }
        }).start();
        fiber.join();
    }

    @Test
    public void testRunBlockingWithTimeout1() throws Exception {
        final Fiber fiber = new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                try {
                    String res = FiberAsync.runBlocking(Executors.newCachedThreadPool(), 400, TimeUnit.MILLISECONDS, new co.paralleluniverse.common.util.CheckedCallable<String, InterruptedException>() {
                        public String call() throws InterruptedException {
                            Thread.sleep(300);
                            return "ok";
                        }
                    });
                    Assert.assertThat(res, CoreMatchers.equalTo("ok"));
                } catch (TimeoutException e) {
                    Assert.fail();
                }
            }
        }).start();
        fiber.join();
    }

    @Test
    public void testRunBlockingWithTimeout2() throws Exception {
        final Fiber fiber = new Fiber(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                try {
                    String res = FiberAsync.runBlocking(Executors.newCachedThreadPool(), 100, TimeUnit.MILLISECONDS, new co.paralleluniverse.common.util.CheckedCallable<String, InterruptedException>() {
                        public String call() throws InterruptedException {
                            Thread.sleep(300);
                            return "ok";
                        }
                    });
                    Assert.fail();
                } catch (TimeoutException e) {
                }
            }
        }).start();
        fiber.join();
    }
}

