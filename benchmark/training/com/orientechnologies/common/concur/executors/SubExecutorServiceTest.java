/**
 * *  Copyright 2010-2017 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.common.concur.executors;


import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class SubExecutorServiceTest {
    private ExecutorService executor;

    private ExecutorService subExecutor;

    @Test
    public void testSubmitCallable() throws InterruptedException, ExecutionException {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Boolean result = subExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ran.set(true);
                return true;
            }
        }).get();
        Assert.assertTrue(result);
        Assert.assertTrue(ran.get());
    }

    @Test
    public void testSubmitRunnableWithResult() throws InterruptedException, ExecutionException {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Boolean result = subExecutor.submit(new Runnable() {
            @Override
            public void run() {
                ran.set(true);
            }
        }, true).get();
        Assert.assertTrue(result);
        Assert.assertTrue(ran.get());
    }

    @Test
    public void testSubmitRunnable() throws InterruptedException, ExecutionException {
        final AtomicBoolean ran = new AtomicBoolean(false);
        final Object result = subExecutor.submit(new Runnable() {
            @Override
            public void run() {
                ran.set(true);
            }
        }).get();
        Assert.assertNull(result);
        Assert.assertTrue(ran.get());
    }

    @Test
    public void testExecute() throws Exception {
        final AtomicBoolean ran = new AtomicBoolean(false);
        subExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ran.set(true);
            }
        });
        Assert.assertTrue(SubExecutorServiceTest.busyWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ran.get();
            }
        }));
    }

    @Test(expected = CancellationException.class)
    public void testCancelNotDone() throws Exception {
        final AtomicBoolean started = new AtomicBoolean(false);
        final AtomicBoolean stop = new AtomicBoolean(false);
        final Future<Boolean> future = subExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                started.set(true);
                Assert.assertTrue(SubExecutorServiceTest.busyWait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return stop.get();
                    }
                }));
                return true;
            }
        });
        Assert.assertTrue(SubExecutorServiceTest.busyWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return started.get();
            }
        }));
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(future.cancel(false));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        stop.set(true);
        future.get();// should throw CancellationException

    }

    @Test
    public void testShutdown() throws Exception {
        final AtomicBoolean shutdown = new AtomicBoolean(false);
        subExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Assert.assertTrue(SubExecutorServiceTest.busyWait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return shutdown.get();
                    }
                }));
                return true;
            }
        });
        subExecutor.shutdown();
        Assert.assertTrue(subExecutor.isShutdown());
        Assert.assertFalse(subExecutor.isTerminated());
        shutdown.set(true);
        Assert.assertTrue(SubExecutorServiceTest.busyWait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return subExecutor.isTerminated();
            }
        }));
        Assert.assertTrue(subExecutor.isShutdown());
        Assert.assertTrue(subExecutor.isTerminated());
        Assert.assertFalse(executor.isShutdown());
        Assert.assertFalse(executor.isTerminated());
    }

    @Test(expected = RejectedExecutionException.class)
    public void testRejected() {
        subExecutor.shutdown();
        subExecutor.submit(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Test
    public void testTaskFailure() throws InterruptedException {
        boolean thrown;
        try {
            subExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    throw new SubExecutorServiceTest.TaskFailureException();
                }
            }).get();
            thrown = false;
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof SubExecutorServiceTest.TaskFailureException));
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    private static class TaskFailureException extends RuntimeException {}
}

