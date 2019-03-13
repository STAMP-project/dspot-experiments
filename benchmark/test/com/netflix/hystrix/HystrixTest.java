/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix;


import HystrixCommandGroupKey.Factory;
import HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;
import com.netflix.hystrix.HystrixCommand.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;


public class HystrixTest {
    @Test
    public void testNotInThread() {
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
    }

    @Test
    public void testInsideHystrixThreadViaExecute() {
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("CommandName"))) {
            @Override
            protected Boolean run() {
                Assert.assertEquals("CommandName", Hystrix.getCurrentThreadExecutingCommand().name());
                Assert.assertEquals(1, Hystrix.getCommandCount());
                return (Hystrix.getCurrentThreadExecutingCommand()) != null;
            }
        };
        Assert.assertTrue(command.execute());
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
        Assert.assertEquals(0, Hystrix.getCommandCount());
    }

    @Test
    public void testInsideHystrixThreadViaObserve() {
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("CommandName"))) {
            @Override
            protected Boolean run() {
                try {
                    // give the caller thread a chance to check that no thread locals are set on it
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    return false;
                }
                Assert.assertEquals("CommandName", Hystrix.getCurrentThreadExecutingCommand().name());
                Assert.assertEquals(1, Hystrix.getCommandCount());
                return (Hystrix.getCurrentThreadExecutingCommand()) != null;
            }
        };
        final CountDownLatch latch = new CountDownLatch(1);
        command.observe().subscribe(new rx.Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                latch.countDown();
            }

            @Override
            public void onNext(Boolean value) {
                System.out.println(("OnNext : " + value));
                Assert.assertTrue(value);
                Assert.assertEquals("CommandName", Hystrix.getCurrentThreadExecutingCommand().name());
                Assert.assertEquals(1, Hystrix.getCommandCount());
            }
        });
        try {
            Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
            Assert.assertEquals(0, Hystrix.getCommandCount());
            latch.await();
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
        Assert.assertEquals(0, Hystrix.getCommandCount());
    }

    @Test
    public void testInsideNestedHystrixThread() {
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("OuterCommand"))) {
            @Override
            protected Boolean run() {
                Assert.assertEquals("OuterCommand", Hystrix.getCurrentThreadExecutingCommand().name());
                System.out.println(("Outer Thread : " + (Thread.currentThread().getName())));
                // should be a single execution on this thread
                Assert.assertEquals(1, Hystrix.getCommandCount());
                if ((Hystrix.getCurrentThreadExecutingCommand()) == null) {
                    throw new RuntimeException("BEFORE expected it to run inside a thread");
                }
                HystrixCommand<Boolean> command2 = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("InnerCommand"))) {
                    @Override
                    protected Boolean run() {
                        Assert.assertEquals("InnerCommand", Hystrix.getCurrentThreadExecutingCommand().name());
                        System.out.println(("Inner Thread : " + (Thread.currentThread().getName())));
                        // should be a single execution on this thread, since outer/inner are on different threads
                        Assert.assertEquals(1, Hystrix.getCommandCount());
                        return (Hystrix.getCurrentThreadExecutingCommand()) != null;
                    }
                };
                if ((Hystrix.getCurrentThreadExecutingCommand()) == null) {
                    throw new RuntimeException("AFTER expected it to run inside a thread");
                }
                return command2.execute();
            }
        };
        Assert.assertTrue(command.execute());
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
    }

    @Test
    public void testInsideHystrixSemaphoreExecute() {
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("SemaphoreIsolatedCommandName")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(SEMAPHORE))) {
            @Override
            protected Boolean run() {
                Assert.assertEquals("SemaphoreIsolatedCommandName", Hystrix.getCurrentThreadExecutingCommand().name());
                System.out.println(("Semaphore Thread : " + (Thread.currentThread().getName())));
                // should be a single execution on the caller thread (since it's busy here)
                Assert.assertEquals(1, Hystrix.getCommandCount());
                return (Hystrix.getCurrentThreadExecutingCommand()) != null;
            }
        };
        // it should be true for semaphore isolation as well
        Assert.assertTrue(command.execute());
        // and then be null again once done
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
    }

    @Test
    public void testInsideHystrixSemaphoreQueue() throws Exception {
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("SemaphoreIsolatedCommandName")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(SEMAPHORE))) {
            @Override
            protected Boolean run() {
                Assert.assertEquals("SemaphoreIsolatedCommandName", Hystrix.getCurrentThreadExecutingCommand().name());
                System.out.println(("Semaphore Thread : " + (Thread.currentThread().getName())));
                // should be a single execution on the caller thread (since it's busy here)
                Assert.assertEquals(1, Hystrix.getCommandCount());
                return (Hystrix.getCurrentThreadExecutingCommand()) != null;
            }
        };
        // it should be true for semaphore isolation as well
        Assert.assertTrue(command.queue().get());
        // and then be null again once done
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
    }

    @Test
    public void testInsideHystrixSemaphoreObserve() throws Exception {
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("SemaphoreIsolatedCommandName")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(SEMAPHORE))) {
            @Override
            protected Boolean run() {
                Assert.assertEquals("SemaphoreIsolatedCommandName", Hystrix.getCurrentThreadExecutingCommand().name());
                System.out.println(("Semaphore Thread : " + (Thread.currentThread().getName())));
                // should be a single execution on the caller thread (since it's busy here)
                Assert.assertEquals(1, Hystrix.getCommandCount());
                return (Hystrix.getCurrentThreadExecutingCommand()) != null;
            }
        };
        // it should be true for semaphore isolation as well
        Assert.assertTrue(command.toObservable().toBlocking().single());
        // and then be null again once done
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
    }

    @Test
    public void testThreadNestedInsideHystrixSemaphore() {
        HystrixCommand<Boolean> command = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("OuterSemaphoreCommand")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(SEMAPHORE))) {
            @Override
            protected Boolean run() {
                Assert.assertEquals("OuterSemaphoreCommand", Hystrix.getCurrentThreadExecutingCommand().name());
                System.out.println(("Outer Semaphore Thread : " + (Thread.currentThread().getName())));
                // should be a single execution on the caller thread
                Assert.assertEquals(1, Hystrix.getCommandCount());
                if ((Hystrix.getCurrentThreadExecutingCommand()) == null) {
                    throw new RuntimeException("BEFORE expected it to run inside a semaphore");
                }
                HystrixCommand<Boolean> command2 = new HystrixCommand<Boolean>(Setter.withGroupKey(Factory.asKey("TestUtil")).andCommandKey(HystrixCommandKey.Factory.asKey("InnerCommand"))) {
                    @Override
                    protected Boolean run() {
                        Assert.assertEquals("InnerCommand", Hystrix.getCurrentThreadExecutingCommand().name());
                        System.out.println(("Inner Thread : " + (Thread.currentThread().getName())));
                        // should be a single execution on the thread isolating the second command
                        Assert.assertEquals(1, Hystrix.getCommandCount());
                        return (Hystrix.getCurrentThreadExecutingCommand()) != null;
                    }
                };
                if ((Hystrix.getCurrentThreadExecutingCommand()) == null) {
                    throw new RuntimeException("AFTER expected it to run inside a semaphore");
                }
                return command2.execute();
            }
        };
        Assert.assertTrue(command.execute());
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
    }

    @Test
    public void testSemaphoreIsolatedSynchronousHystrixObservableCommand() {
        HystrixObservableCommand<Integer> observableCmd = new HystrixTest.SynchronousObservableCommand();
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
        final CountDownLatch latch = new CountDownLatch(1);
        observableCmd.observe().subscribe(new rx.Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                latch.countDown();
            }

            @Override
            public void onNext(Integer value) {
                System.out.println((((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " SyncObservable latched Subscriber OnNext : ") + value));
            }
        });
        try {
            Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
            Assert.assertEquals(0, Hystrix.getCommandCount());
            latch.await();
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertNull(Hystrix.getCurrentThreadExecutingCommand());
        Assert.assertEquals(0, Hystrix.getCommandCount());
    }

    // @Test
    // public void testSemaphoreIsolatedAsynchronousHystrixObservableCommand() {
    // HystrixObservableCommand<Integer> observableCmd = new AsynchronousObservableCommand();
    // 
    // assertNull(Hystrix.getCurrentThreadExecutingCommand());
    // 
    // final CountDownLatch latch = new CountDownLatch(1);
    // 
    // observableCmd.observe().subscribe(new Subscriber<Integer>() {
    // @Override
    // public void onCompleted() {
    // latch.countDown();
    // }
    // 
    // @Override
    // public void onError(Throwable e) {
    // fail(e.getMessage());
    // latch.countDown();
    // }
    // 
    // @Override
    // public void onNext(Integer value) {
    // System.out.println(Thread.currentThread().getName() + " : " + System.currentTimeMillis() + " AsyncObservable latched Subscriber OnNext : " + value);
    // }
    // });
    // 
    // try {
    // assertNull(Hystrix.getCurrentThreadExecutingCommand());
    // assertEquals(0, Hystrix.getCommandCount());
    // latch.await();
    // } catch (InterruptedException ex) {
    // fail(ex.getMessage());
    // }
    // 
    // assertNull(Hystrix.getCurrentThreadExecutingCommand());
    // assertEquals(0, Hystrix.getCommandCount());
    // }
    @Test
    public void testMultipleSemaphoreObservableCommandsInFlight() throws InterruptedException {
        int NUM_COMMANDS = 50;
        List<Observable<Integer>> commands = new ArrayList<Observable<Integer>>();
        for (int i = 0; i < NUM_COMMANDS; i++) {
            commands.add(Observable.defer(new rx.functions.Func0<Observable<Integer>>() {
                @Override
                public Observable<Integer> call() {
                    return observe();
                }
            }));
        }
        final AtomicBoolean exceptionFound = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        Observable.merge(commands).subscribe(new rx.Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("OnCompleted");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("OnError : " + e));
                e.printStackTrace();
                exceptionFound.set(true);
                latch.countDown();
            }

            @Override
            public void onNext(Integer n) {
                System.out.println(((((("OnNext : " + n) + " : ") + (Thread.currentThread().getName())) + " : ") + (Hystrix.getCommandCount())));// + " : " + Hystrix.getCurrentThreadExecutingCommand().name() + " : " + Hystrix.getCommandCount());

            }
        });
        latch.await();
        Assert.assertFalse(exceptionFound.get());
    }

    // see https://github.com/Netflix/Hystrix/issues/280
    @Test
    public void testResetCommandProperties() {
        HystrixCommand<Boolean> cmd1 = new HystrixTest.ResettableCommand(100, 1, 10);
        Assert.assertEquals(100L, ((long) (cmd1.getProperties().executionTimeoutInMilliseconds().get())));
        Assert.assertEquals(1L, ((long) (cmd1.getProperties().executionIsolationSemaphoreMaxConcurrentRequests().get())));
        // assertEquals(10L, (long) cmd1.threadPool.getExecutor()..getCorePoolSize());
        Hystrix.reset();
        HystrixCommand<Boolean> cmd2 = new HystrixTest.ResettableCommand(700, 2, 40);
        Assert.assertEquals(700L, ((long) (cmd2.getProperties().executionTimeoutInMilliseconds().get())));
        Assert.assertEquals(2L, ((long) (cmd2.getProperties().executionIsolationSemaphoreMaxConcurrentRequests().get())));
        // assertEquals(40L, (long) cmd2.threadPool.getExecutor().getCorePoolSize());
    }

    private static class SynchronousObservableCommand extends HystrixObservableCommand<Integer> {
        protected SynchronousObservableCommand() {
            super(Setter.withGroupKey(Factory.asKey("GROUP")).andCommandKey(HystrixCommandKey.Factory.asKey("SyncObservable")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)));
        }

        @Override
        protected Observable<Integer> construct() {
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? extends Integer> subscriber) {
                    try {
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " SyncCommand construct()"));
                        assertEquals("SyncObservable", Hystrix.getCurrentThreadExecutingCommand().name());
                        assertEquals(1, Hystrix.getCommandCount());
                        Thread.sleep(10);
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " SyncCommand construct() -> OnNext(1)"));
                        subscriber.onNext(1);
                        Thread.sleep(10);
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " SyncCommand construct() -> OnNext(2)"));
                        subscriber.onNext(2);
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " SyncCommand construct() -> OnCompleted"));
                        subscriber.onCompleted();
                    } catch ( ex) {
                        subscriber.onError(ex);
                    }
                }
            });
        }
    }

    private static class AsynchronousObservableCommand extends HystrixObservableCommand<Integer> {
        protected AsynchronousObservableCommand() {
            super(Setter.withGroupKey(Factory.asKey("GROUP")).andCommandKey(HystrixCommandKey.Factory.asKey("AsyncObservable")).andCommandPropertiesDefaults(new HystrixCommandProperties.Setter().withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)));
        }

        @Override
        protected Observable<Integer> construct() {
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? extends Integer> subscriber) {
                    try {
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " AsyncCommand construct()"));
                        Thread.sleep(10);
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " AsyncCommand construct() -> OnNext(1)"));
                        subscriber.onNext(1);
                        Thread.sleep(10);
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " AsyncCommand construct() -> OnNext(2)"));
                        subscriber.onNext(2);
                        System.out.println(((((Thread.currentThread().getName()) + " : ") + (System.currentTimeMillis())) + " AsyncCommand construct() -> OnCompleted"));
                        subscriber.onCompleted();
                    } catch ( ex) {
                        subscriber.onError(ex);
                    }
                }
            }).subscribeOn(Schedulers.computation());
        }
    }

    private static class ResettableCommand extends HystrixCommand<Boolean> {
        ResettableCommand(int timeout, int semaphoreCount, int poolCoreSize) {
            super(Setter.withGroupKey(Factory.asKey("GROUP")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout).withExecutionIsolationSemaphoreMaxConcurrentRequests(semaphoreCount)).andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(poolCoreSize)));
        }

        @Override
        protected Boolean run() throws Exception {
            return true;
        }
    }
}

