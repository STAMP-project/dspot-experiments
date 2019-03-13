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


import AbstractCommand.TryableSemaphore;
import ExecutionIsolationStrategy.SEMAPHORE;
import HystrixProperty.Factory;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.strategy.HystrixPlugins;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import rx.Scheduler;
import rx.functions.Func0;

import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.BAD_REQUEST;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.FAILURE;
import static com.netflix.hystrix.AbstractTestHystrixCommand.ExecutionResult.SUCCESS;
import static com.netflix.hystrix.AbstractTestHystrixCommand.FallbackResult.UNIMPLEMENTED;


/**
 * Place to share code and tests between {@link HystrixCommandTest} and {@link HystrixObservableCommandTest}.
 *
 * @param <C>
 * 		
 */
public abstract class CommonHystrixCommandTests<C extends AbstractTestHystrixCommand<Integer>> {
    /**
     * Threadpool with 1 thread, queue of size 1
     */
    protected static class SingleThreadedPoolWithQueue implements HystrixThreadPool {
        final LinkedBlockingQueue<Runnable> queue;

        final ThreadPoolExecutor pool;

        private final int rejectionQueueSizeThreshold;

        public SingleThreadedPoolWithQueue(int queueSize) {
            this(queueSize, 100);
        }

        public SingleThreadedPoolWithQueue(int queueSize, int rejectionQueueSizeThreshold) {
            queue = new LinkedBlockingQueue<Runnable>(queueSize);
            pool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, queue);
            this.rejectionQueueSizeThreshold = rejectionQueueSizeThreshold;
        }

        @Override
        public ThreadPoolExecutor getExecutor() {
            return pool;
        }

        @Override
        public Scheduler getScheduler() {
            return new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(HystrixPlugins.getInstance().getConcurrencyStrategy(), this);
        }

        @Override
        public Scheduler getScheduler(Func0<Boolean> shouldInterruptThread) {
            return new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(HystrixPlugins.getInstance().getConcurrencyStrategy(), this, shouldInterruptThread);
        }

        @Override
        public void markThreadExecution() {
            // not used for this test
        }

        @Override
        public void markThreadCompletion() {
            // not used for this test
        }

        @Override
        public void markThreadRejection() {
            // not used for this test
        }

        @Override
        public boolean isQueueSpaceAvailable() {
            return (queue.size()) < (rejectionQueueSizeThreshold);
        }
    }

    /**
     * Threadpool with 1 thread, queue of size 1
     */
    protected static class SingleThreadedPoolWithNoQueue implements HystrixThreadPool {
        final SynchronousQueue<Runnable> queue;

        final ThreadPoolExecutor pool;

        public SingleThreadedPoolWithNoQueue() {
            queue = new SynchronousQueue<Runnable>();
            pool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, queue);
        }

        @Override
        public ThreadPoolExecutor getExecutor() {
            return pool;
        }

        @Override
        public Scheduler getScheduler() {
            return new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(HystrixPlugins.getInstance().getConcurrencyStrategy(), this);
        }

        @Override
        public Scheduler getScheduler(Func0<Boolean> shouldInterruptThread) {
            return new com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler(HystrixPlugins.getInstance().getConcurrencyStrategy(), this, shouldInterruptThread);
        }

        @Override
        public void markThreadExecution() {
            // not used for this test
        }

        @Override
        public void markThreadCompletion() {
            // not used for this test
        }

        @Override
        public void markThreadRejection() {
            // not used for this test
        }

        @Override
        public boolean isQueueSpaceAvailable() {
            return true;// let the thread pool reject

        }
    }

    /**
     * ******************** SEMAPHORE-ISOLATED Execution Hook Tests ***********************************
     */
    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : NO
     * Execution Result: SUCCESS
     */
    @Test
    public void testExecutionHookSemaphoreSuccess() {
        assertHooksOnSuccess(new Func0<C>() {
            @Override
            public C call() {
                return getCommand(SEMAPHORE, SUCCESS, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(1, 0, 1));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals("onStart - !onRunStart - onExecutionStart - onExecutionEmit - !onRunSuccess - !onComplete - onEmit - onExecutionSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : NO
     * Execution Result: synchronous HystrixBadRequestException
     */
    @Test
    public void testExecutionHookSemaphoreBadRequestException() {
        assertHooksOnFailure(new Func0<C>() {
            @Override
            public C call() {
                return getCommand(SEMAPHORE, BAD_REQUEST, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(HystrixBadRequestException.class, hook.getCommandException().getClass());
                Assert.assertEquals(HystrixBadRequestException.class, hook.getExecutionException().getClass());
                Assert.assertEquals("onStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : NO
     * Execution Result: synchronous HystrixRuntimeException
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookSemaphoreExceptionNoFallback() {
        assertHooksOnFailure(new Func0<C>() {
            @Override
            public C call() {
                return getCommand(SEMAPHORE, FAILURE, UNIMPLEMENTED);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : NO
     * Execution Result: synchronous HystrixRuntimeException
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookSemaphoreExceptionSuccessfulFallback() {
        assertHooksOnSuccess(new Func0<C>() {
            @Override
            public C call() {
                return getCommand(SEMAPHORE, FAILURE, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals("onStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : NO
     * Execution Result: synchronous HystrixRuntimeException
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookSemaphoreExceptionUnsuccessfulFallback() {
        assertHooksOnFailure(new Func0<C>() {
            @Override
            public C call() {
                return getCommand(SEMAPHORE, FAILURE, AbstractTestHystrixCommand.FallbackResult.FAILURE);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 1, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getExecutionException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - !onRunStart - onExecutionStart - onExecutionError - !onRunError - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : YES
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookSemaphoreRejectedNoFallback() {
        assertHooksOnFailFast(new Func0<C>() {
            @Override
            public C call() {
                AbstractCommand.TryableSemaphore semaphore = new AbstractCommand.TryableSemaphoreActual(Factory.asProperty(2));
                final C cmd1 = getLatentCommand(SEMAPHORE, SUCCESS, 500, UNIMPLEMENTED, semaphore);
                final C cmd2 = getLatentCommand(SEMAPHORE, SUCCESS, 500, UNIMPLEMENTED, semaphore);
                // saturate the semaphore
                new Thread() {
                    @Override
                    public void run() {
                        observe();
                    }
                }.start();
                new Thread() {
                    @Override
                    public void run() {
                        observe();
                    }
                }.start();
                try {
                    // give the saturating threads a chance to run before we run the command we want to get rejected
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                return getLatentCommand(SEMAPHORE, SUCCESS, 500, UNIMPLEMENTED, semaphore);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : YES
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookSemaphoreRejectedSuccessfulFallback() {
        assertHooksOnSuccess(new Func0<C>() {
            @Override
            public C call() {
                AbstractCommand.TryableSemaphore semaphore = new AbstractCommand.TryableSemaphoreActual(Factory.asProperty(2));
                final C cmd1 = getLatentCommand(SEMAPHORE, SUCCESS, 1500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, semaphore);
                final C cmd2 = getLatentCommand(SEMAPHORE, SUCCESS, 1500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, semaphore);
                // saturate the semaphore
                new Thread() {
                    @Override
                    public void run() {
                        observe();
                    }
                }.start();
                new Thread() {
                    @Override
                    public void run() {
                        observe();
                    }
                }.start();
                try {
                    // give the saturating threads a chance to run before we run the command we want to get rejected
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                return getLatentCommand(SEMAPHORE, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.SUCCESS, semaphore);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals("onStart - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : NO
     * Thread/semaphore: SEMAPHORE
     * Semaphore Permit reached? : YES
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookSemaphoreRejectedUnsuccessfulFallback() {
        assertHooksOnFailFast(new Func0<C>() {
            @Override
            public C call() {
                AbstractCommand.TryableSemaphore semaphore = new AbstractCommand.TryableSemaphoreActual(Factory.asProperty(2));
                final C cmd1 = getLatentCommand(SEMAPHORE, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, semaphore);
                final C cmd2 = getLatentCommand(SEMAPHORE, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, semaphore);
                // saturate the semaphore
                new Thread() {
                    @Override
                    public void run() {
                        observe();
                    }
                }.start();
                new Thread() {
                    @Override
                    public void run() {
                        observe();
                    }
                }.start();
                try {
                    // give the saturating threads a chance to run before we run the command we want to get rejected
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                return getLatentCommand(SEMAPHORE, SUCCESS, 500, AbstractTestHystrixCommand.FallbackResult.FAILURE, semaphore);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : YES
     * Thread/semaphore: SEMAPHORE
     * Fallback: UnsupportedOperationException
     */
    @Test
    public void testExecutionHookSemaphoreShortCircuitNoFallback() {
        assertHooksOnFailFast(new Func0<C>() {
            @Override
            public C call() {
                return getCircuitOpenCommand(SEMAPHORE, UNIMPLEMENTED);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 0, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertNull(hook.getFallbackException());
                Assert.assertEquals("onStart - onError - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : YES
     * Thread/semaphore: SEMAPHORE
     * Fallback: SUCCESS
     */
    @Test
    public void testExecutionHookSemaphoreShortCircuitSuccessfulFallback() {
        assertHooksOnSuccess(new Func0<C>() {
            @Override
            public C call() {
                return getCircuitOpenCommand(SEMAPHORE, AbstractTestHystrixCommand.FallbackResult.SUCCESS);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(1, 0, 1));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(1, 0, 1));
                Assert.assertEquals("onStart - onFallbackStart - onFallbackEmit - !onFallbackSuccess - !onComplete - onEmit - onFallbackSuccess - onSuccess - ", hook.executionSequence.toString());
            }
        });
    }

    /**
     * Short-circuit? : YES
     * Thread/semaphore: SEMAPHORE
     * Fallback: synchronous HystrixRuntimeException
     */
    @Test
    public void testExecutionHookSemaphoreShortCircuitUnsuccessfulFallback() {
        assertHooksOnFailFast(new Func0<C>() {
            @Override
            public C call() {
                return getCircuitOpenCommand(SEMAPHORE, AbstractTestHystrixCommand.FallbackResult.FAILURE);
            }
        }, new rx.functions.Action1<C>() {
            @Override
            public void call(C command) {
                TestableExecutionHook hook = command.getBuilder().executionHook;
                Assert.assertTrue(hook.commandEmissionsMatch(0, 1, 0));
                Assert.assertTrue(hook.executionEventsMatch(0, 0, 0));
                Assert.assertTrue(hook.fallbackEventsMatch(0, 1, 0));
                Assert.assertEquals(RuntimeException.class, hook.getCommandException().getClass());
                Assert.assertEquals(RuntimeException.class, hook.getFallbackException().getClass());
                Assert.assertEquals("onStart - onFallbackStart - onFallbackError - onError - ", hook.executionSequence.toString());
            }
        });
    }
}

