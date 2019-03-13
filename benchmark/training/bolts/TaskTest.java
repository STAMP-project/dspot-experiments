/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package bolts;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


// endregion
public class TaskTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCache() {
        Assert.assertSame(Task.forResult(null), Task.forResult(null));
        Task<Boolean> trueTask = Task.forResult(true);
        Assert.assertTrue(trueTask.getResult());
        Assert.assertSame(trueTask, Task.forResult(true));
        Task<Boolean> falseTask = Task.forResult(false);
        Assert.assertFalse(falseTask.getResult());
        Assert.assertSame(falseTask, Task.forResult(false));
        Assert.assertSame(Task.cancelled(), Task.cancelled());
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testPrimitives() {
        Task<Integer> complete = Task.forResult(5);
        Task<Integer> error = Task.forError(new RuntimeException());
        Task<Integer> cancelled = Task.cancelled();
        Assert.assertTrue(complete.isCompleted());
        Assert.assertEquals(5, complete.getResult().intValue());
        Assert.assertFalse(complete.isFaulted());
        Assert.assertFalse(complete.isCancelled());
        Assert.assertTrue(error.isCompleted());
        Assert.assertTrue(((error.getError()) instanceof RuntimeException));
        Assert.assertTrue(error.isFaulted());
        Assert.assertFalse(error.isCancelled());
        Assert.assertTrue(cancelled.isCompleted());
        Assert.assertFalse(cancelled.isFaulted());
        Assert.assertTrue(cancelled.isCancelled());
    }

    @Test
    public void testDelay() throws InterruptedException {
        final Task<Void> delayed = Task.delay(200);
        Thread.sleep(50);
        Assert.assertFalse(delayed.isCompleted());
        Thread.sleep(200);
        Assert.assertTrue(delayed.isCompleted());
        Assert.assertFalse(delayed.isFaulted());
        Assert.assertFalse(delayed.isCancelled());
    }

    @Test
    public void testDelayWithCancelledToken() throws InterruptedException {
        CancellationTokenSource cts = new CancellationTokenSource();
        cts.cancel();
        final Task<Void> delayed = Task.delay(200, cts.getToken());
        Assert.assertTrue(delayed.isCancelled());
    }

    @Test
    public void testDelayWithToken() throws InterruptedException {
        CancellationTokenSource cts = new CancellationTokenSource();
        final Task<Void> delayed = Task.delay(200, cts.getToken());
        Assert.assertFalse(delayed.isCancelled());
        cts.cancel();
        Assert.assertTrue(delayed.isCancelled());
    }

    @Test
    public void testSynchronousContinuation() {
        final Task<Integer> complete = Task.forResult(5);
        final Task<Integer> error = Task.forError(new RuntimeException());
        final Task<Integer> cancelled = Task.cancelled();
        complete.continueWith(new Continuation<Integer, Void>() {
            public Void then(Task<Integer> task) {
                Assert.assertEquals(complete, task);
                Assert.assertTrue(task.isCompleted());
                Assert.assertEquals(5, task.getResult().intValue());
                Assert.assertFalse(task.isFaulted());
                Assert.assertFalse(task.isCancelled());
                return null;
            }
        });
        error.continueWith(new Continuation<Integer, Void>() {
            public Void then(Task<Integer> task) {
                Assert.assertEquals(error, task);
                Assert.assertTrue(task.isCompleted());
                Assert.assertTrue(((task.getError()) instanceof RuntimeException));
                Assert.assertTrue(task.isFaulted());
                Assert.assertFalse(task.isCancelled());
                return null;
            }
        });
        cancelled.continueWith(new Continuation<Integer, Void>() {
            public Void then(Task<Integer> task) {
                Assert.assertEquals(cancelled, task);
                Assert.assertTrue(cancelled.isCompleted());
                Assert.assertFalse(cancelled.isFaulted());
                Assert.assertTrue(cancelled.isCancelled());
                return null;
            }
        });
    }

    @Test
    public void testSynchronousChaining() {
        Task<Integer> first = Task.forResult(1);
        Task<Integer> second = first.continueWith(new Continuation<Integer, Integer>() {
            public Integer then(Task<Integer> task) {
                return 2;
            }
        });
        Task<Integer> third = second.continueWithTask(new Continuation<Integer, Task<Integer>>() {
            public Task<Integer> then(Task<Integer> task) {
                return Task.forResult(3);
            }
        });
        Assert.assertTrue(first.isCompleted());
        Assert.assertTrue(second.isCompleted());
        Assert.assertTrue(third.isCompleted());
        Assert.assertEquals(1, first.getResult().intValue());
        Assert.assertEquals(2, second.getResult().intValue());
        Assert.assertEquals(3, third.getResult().intValue());
    }

    @Test
    public void testSynchronousCancellation() {
        Task<Integer> first = Task.forResult(1);
        Task<Integer> second = first.continueWith(new Continuation<Integer, Integer>() {
            public Integer then(Task<Integer> task) {
                throw new CancellationException();
            }
        });
        Assert.assertTrue(first.isCompleted());
        Assert.assertTrue(second.isCancelled());
    }

    @Test
    public void testSynchronousContinuationTokenAlreadyCancelled() {
        CancellationTokenSource cts = new CancellationTokenSource();
        final Capture<Boolean> continuationRun = new Capture(false);
        cts.cancel();
        Task<Integer> first = Task.forResult(1);
        Task<Integer> second = first.continueWith(new Continuation<Integer, Integer>() {
            public Integer then(Task<Integer> task) {
                continuationRun.set(true);
                return 2;
            }
        }, cts.getToken());
        Assert.assertTrue(first.isCompleted());
        Assert.assertTrue(second.isCancelled());
        Assert.assertFalse(continuationRun.get());
    }

    @Test
    public void testSynchronousTaskCancellation() {
        Task<Integer> first = Task.forResult(1);
        Task<Integer> second = first.continueWithTask(new Continuation<Integer, Task<Integer>>() {
            public Task<Integer> then(Task<Integer> task) {
                throw new CancellationException();
            }
        });
        Assert.assertTrue(first.isCompleted());
        Assert.assertTrue(second.isCancelled());
    }

    @Test
    public void testBackgroundCall() {
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.callInBackground(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        Thread.sleep(100);
                        return 5;
                    }
                }).continueWith(new Continuation<Integer, Void>() {
                    public Void then(Task<Integer> task) {
                        Assert.assertEquals(5, task.getResult().intValue());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testBackgroundCallTokenCancellation() {
        final CancellationTokenSource cts = new CancellationTokenSource();
        final CancellationToken ct = cts.getToken();
        final Capture<Boolean> waitingToBeCancelled = new Capture(false);
        final Object cancelLock = new Object();
        Task<Integer> task = Task.callInBackground(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                synchronized(cancelLock) {
                    waitingToBeCancelled.set(true);
                    cancelLock.wait();
                }
                ct.throwIfCancellationRequested();
                return 5;
            }
        });
        while (true) {
            synchronized(cancelLock) {
                if (waitingToBeCancelled.get()) {
                    cts.cancel();
                    cancelLock.notify();
                    break;
                }
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } 
        try {
            task.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(task.isCancelled());
    }

    @Test
    public void testBackgroundCallTokenAlreadyCancelled() {
        final CancellationTokenSource cts = new CancellationTokenSource();
        cts.cancel();
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.callInBackground(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        Thread.sleep(100);
                        return 5;
                    }
                }, cts.getToken()).continueWith(new Continuation<Integer, Void>() {
                    public Void then(Task<Integer> task) {
                        Assert.assertTrue(task.isCancelled());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testBackgroundCallWaiting() throws Exception {
        Task<Integer> task = Task.callInBackground(new Callable<Integer>() {
            public Integer call() throws Exception {
                Thread.sleep(100);
                return 5;
            }
        });
        task.waitForCompletion();
        Assert.assertTrue(task.isCompleted());
        Assert.assertEquals(5, task.getResult().intValue());
    }

    @Test
    public void testBackgroundCallWaitingWithTimeouts() throws Exception {
        final Object sync = new Object();
        Task<Integer> task = Task.callInBackground(new Callable<Integer>() {
            public Integer call() throws Exception {
                synchronized(sync) {
                    sync.wait();
                    Thread.sleep(100);
                }
                return 5;
            }
        });
        // wait -> timeout
        Assert.assertFalse(task.waitForCompletion(100, TimeUnit.MILLISECONDS));
        synchronized(sync) {
            sync.notify();
        }
        // wait -> completes
        Assert.assertTrue(task.waitForCompletion(1000, TimeUnit.MILLISECONDS));
        // wait -> already completed
        Assert.assertTrue(task.waitForCompletion(100, TimeUnit.MILLISECONDS));
        Assert.assertEquals(5, task.getResult().intValue());
    }

    @Test
    public void testBackgroundCallWaitingOnError() throws Exception {
        Task<Integer> task = Task.callInBackground(new Callable<Integer>() {
            public Integer call() throws Exception {
                Thread.sleep(100);
                throw new RuntimeException();
            }
        });
        task.waitForCompletion();
        Assert.assertTrue(task.isCompleted());
        Assert.assertTrue(task.isFaulted());
    }

    @Test
    public void testBackgroundCallWaitOnCancellation() throws Exception {
        Task<Integer> task = Task.callInBackground(new Callable<Integer>() {
            public Integer call() throws Exception {
                Thread.sleep(100);
                return 5;
            }
        }).continueWithTask(new Continuation<Integer, Task<Integer>>() {
            public Task<Integer> then(Task<Integer> task) {
                return Task.cancelled();
            }
        });
        task.waitForCompletion();
        Assert.assertTrue(task.isCompleted());
        Assert.assertTrue(task.isCancelled());
    }

    @Test
    public void testBackgroundError() {
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.callInBackground(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        throw new IllegalStateException();
                    }
                }).continueWith(new Continuation<Integer, Void>() {
                    public Void then(Task<Integer> task) {
                        Assert.assertTrue(task.isFaulted());
                        Assert.assertTrue(((task.getError()) instanceof IllegalStateException));
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testBackgroundCancellation() {
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.callInBackground(new Callable<Void>() {
                    public Void call() throws Exception {
                        throw new CancellationException();
                    }
                }).continueWith(new Continuation<Void, Void>() {
                    public Void then(Task<Void> task) {
                        Assert.assertTrue(task.isCancelled());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testUnobservedError() throws InterruptedException {
        try {
            final Object sync = new Object();
            Task.setUnobservedExceptionHandler(new Task.UnobservedExceptionHandler() {
                @Override
                public void unobservedException(Task<?> t, UnobservedTaskException e) {
                    synchronized(sync) {
                        sync.notify();
                    }
                }
            });
            synchronized(sync) {
                startFailedTask();
                System.gc();
                sync.wait();
            }
        } finally {
            Task.setUnobservedExceptionHandler(null);
        }
    }

    @Test
    public void testWhenAllNoTasks() {
        Task<Void> task = Task.whenAll(new ArrayList<Task<Void>>());
        Assert.assertTrue(task.isCompleted());
        Assert.assertFalse(task.isCancelled());
        Assert.assertFalse(task.isFaulted());
    }

    @Test
    public void testWhenAnyResultFirstSuccess() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Integer>> tasks = new ArrayList<>();
                final Task<Integer> firstToCompleteSuccess = Task.callInBackground(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        Thread.sleep(50);
                        return 10;
                    }
                });
                tasks.addAll(launchTasksWithRandomCompletions(5));
                tasks.add(firstToCompleteSuccess);
                tasks.addAll(launchTasksWithRandomCompletions(5));
                return Task.whenAnyResult(tasks).continueWith(new Continuation<Task<Integer>, Void>() {
                    @Override
                    public Void then(Task<Task<Integer>> task) throws Exception {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(firstToCompleteSuccess, task.getResult());
                        Assert.assertTrue(task.getResult().isCompleted());
                        Assert.assertFalse(task.getResult().isCancelled());
                        Assert.assertFalse(task.getResult().isFaulted());
                        Assert.assertEquals(10, ((int) (task.getResult().getResult())));
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAnyFirstSuccess() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<?>> tasks = new ArrayList<>();
                final Task<String> firstToCompleteSuccess = Task.callInBackground(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Thread.sleep(50);
                        return "SUCCESS";
                    }
                });
                tasks.addAll(launchTasksWithRandomCompletions(5));
                tasks.add(firstToCompleteSuccess);
                tasks.addAll(launchTasksWithRandomCompletions(5));
                return Task.whenAny(tasks).continueWith(new Continuation<Task<?>, Object>() {
                    @Override
                    public Object then(Task<Task<?>> task) throws Exception {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(firstToCompleteSuccess, task.getResult());
                        Assert.assertTrue(task.getResult().isCompleted());
                        Assert.assertFalse(task.getResult().isCancelled());
                        Assert.assertFalse(task.getResult().isFaulted());
                        Assert.assertEquals("SUCCESS", task.getResult().getResult());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAnyResultFirstError() {
        final Exception error = new RuntimeException("This task failed.");
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Integer>> tasks = new ArrayList<>();
                final Task<Integer> firstToCompleteError = Task.callInBackground(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        Thread.sleep(50);
                        throw error;
                    }
                });
                tasks.addAll(launchTasksWithRandomCompletions(5));
                tasks.add(firstToCompleteError);
                tasks.addAll(launchTasksWithRandomCompletions(5));
                return Task.whenAnyResult(tasks).continueWith(new Continuation<Task<Integer>, Object>() {
                    @Override
                    public Object then(Task<Task<Integer>> task) throws Exception {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(firstToCompleteError, task.getResult());
                        Assert.assertTrue(task.getResult().isCompleted());
                        Assert.assertFalse(task.getResult().isCancelled());
                        Assert.assertTrue(task.getResult().isFaulted());
                        Assert.assertEquals(error, task.getResult().getError());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAnyFirstError() {
        final Exception error = new RuntimeException("This task failed.");
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<?>> tasks = new ArrayList<>();
                final Task<String> firstToCompleteError = Task.callInBackground(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Thread.sleep(50);
                        throw error;
                    }
                });
                tasks.addAll(launchTasksWithRandomCompletions(5));
                tasks.add(firstToCompleteError);
                tasks.addAll(launchTasksWithRandomCompletions(5));
                return Task.whenAny(tasks).continueWith(new Continuation<Task<?>, Object>() {
                    @Override
                    public Object then(Task<Task<?>> task) throws Exception {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(firstToCompleteError, task.getResult());
                        Assert.assertTrue(task.getResult().isCompleted());
                        Assert.assertFalse(task.getResult().isCancelled());
                        Assert.assertTrue(task.getResult().isFaulted());
                        Assert.assertEquals(error, task.getResult().getError());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAnyResultFirstCancelled() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Integer>> tasks = new ArrayList<>();
                final Task<Integer> firstToCompleteCancelled = Task.callInBackground(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        Thread.sleep(50);
                        throw new CancellationException();
                    }
                });
                tasks.addAll(launchTasksWithRandomCompletions(5));
                tasks.add(firstToCompleteCancelled);
                tasks.addAll(launchTasksWithRandomCompletions(5));
                return Task.whenAnyResult(tasks).continueWith(new Continuation<Task<Integer>, Object>() {
                    @Override
                    public Object then(Task<Task<Integer>> task) throws Exception {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(firstToCompleteCancelled, task.getResult());
                        Assert.assertTrue(task.getResult().isCompleted());
                        Assert.assertTrue(task.getResult().isCancelled());
                        Assert.assertFalse(task.getResult().isFaulted());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAnyFirstCancelled() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<?>> tasks = new ArrayList<>();
                final Task<String> firstToCompleteCancelled = Task.callInBackground(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Thread.sleep(50);
                        throw new CancellationException();
                    }
                });
                tasks.addAll(launchTasksWithRandomCompletions(5));
                tasks.add(firstToCompleteCancelled);
                tasks.addAll(launchTasksWithRandomCompletions(5));
                return Task.whenAny(tasks).continueWith(new Continuation<Task<?>, Object>() {
                    @Override
                    public Object then(Task<Task<?>> task) throws Exception {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(firstToCompleteCancelled, task.getResult());
                        Assert.assertTrue(task.getResult().isCompleted());
                        Assert.assertTrue(task.getResult().isCancelled());
                        Assert.assertFalse(task.getResult().isFaulted());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAllSuccess() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Void>> tasks = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    Task<Void> task = Task.callInBackground(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Thread.sleep(((long) ((Math.random()) * 100)));
                            return null;
                        }
                    });
                    tasks.add(task);
                }
                return Task.whenAll(tasks).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        for (Task<Void> t : tasks) {
                            Assert.assertTrue(t.isCompleted());
                        }
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAllOneError() {
        final Exception error = new RuntimeException("This task failed.");
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Void>> tasks = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    final int number = i;
                    Task<Void> task = Task.callInBackground(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Thread.sleep(((long) ((Math.random()) * 100)));
                            if (number == 10) {
                                throw error;
                            }
                            return null;
                        }
                    });
                    tasks.add(task);
                }
                return Task.whenAll(tasks).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertTrue(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertFalse(((task.getError()) instanceof AggregateException));
                        Assert.assertEquals(error, task.getError());
                        for (Task<Void> t : tasks) {
                            Assert.assertTrue(t.isCompleted());
                        }
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAllTwoErrors() {
        final Exception error0 = new RuntimeException("This task failed (0).");
        final Exception error1 = new RuntimeException("This task failed (1).");
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Void>> tasks = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    final int number = i;
                    Task<Void> task = Task.callInBackground(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Thread.sleep(((long) (number * 10)));
                            if (number == 10) {
                                throw error0;
                            } else
                                if (number == 11) {
                                    throw error1;
                                }

                            return null;
                        }
                    });
                    tasks.add(task);
                }
                return Task.whenAll(tasks).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertTrue(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertTrue(((task.getError()) instanceof AggregateException));
                        Assert.assertEquals(2, getInnerThrowables().size());
                        Assert.assertEquals(error0, getInnerThrowables().get(0));
                        Assert.assertEquals(error1, getInnerThrowables().get(1));
                        Assert.assertEquals(error0, task.getError().getCause());
                        for (Task<Void> t : tasks) {
                            Assert.assertTrue(t.isCompleted());
                        }
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAllCancel() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final ArrayList<Task<Void>> tasks = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    final TaskCompletionSource<Void> tcs = new TaskCompletionSource();
                    final int number = i;
                    Task.callInBackground(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Thread.sleep(((long) ((Math.random()) * 100)));
                            if (number == 10) {
                                tcs.setCancelled();
                            } else {
                                tcs.setResult(null);
                            }
                            return null;
                        }
                    });
                    tasks.add(tcs.getTask());
                }
                return Task.whenAll(tasks).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertTrue(task.isCancelled());
                        for (Task<Void> t : tasks) {
                            Assert.assertTrue(t.isCompleted());
                        }
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testWhenAllResultNoTasks() {
        Task<List<Void>> task = Task.whenAllResult(new ArrayList<Task<Void>>());
        Assert.assertTrue(task.isCompleted());
        Assert.assertFalse(task.isCancelled());
        Assert.assertFalse(task.isFaulted());
        Assert.assertTrue(task.getResult().isEmpty());
    }

    @Test
    public void testWhenAllResultSuccess() {
        runTaskTest(new Callable<Task<?>>() {
            @Override
            public Task<?> call() throws Exception {
                final List<Task<Integer>> tasks = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    final int number = i + 1;
                    Task<Integer> task = Task.callInBackground(new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            Thread.sleep(((long) ((Math.random()) * 100)));
                            return number;
                        }
                    });
                    tasks.add(task);
                }
                return Task.whenAllResult(tasks).continueWith(new Continuation<List<Integer>, Void>() {
                    @Override
                    public Void then(Task<List<Integer>> task) {
                        Assert.assertTrue(task.isCompleted());
                        Assert.assertFalse(task.isFaulted());
                        Assert.assertFalse(task.isCancelled());
                        Assert.assertEquals(tasks.size(), task.getResult().size());
                        for (int i = 0; i < (tasks.size()); i++) {
                            Task<Integer> t = tasks.get(i);
                            Assert.assertTrue(t.isCompleted());
                            Assert.assertEquals(t.getResult(), task.getResult().get(i));
                        }
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testAsyncChaining() {
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                final ArrayList<Integer> sequence = new ArrayList<>();
                Task<Void> result = Task.forResult(null);
                for (int i = 0; i < 20; i++) {
                    final int taskNumber = i;
                    result = result.continueWithTask(new Continuation<Void, Task<Void>>() {
                        public Task<Void> then(Task<Void> task) {
                            return Task.callInBackground(new Callable<Void>() {
                                public Void call() throws Exception {
                                    sequence.add(taskNumber);
                                    return null;
                                }
                            });
                        }
                    });
                }
                result = result.continueWith(new Continuation<Void, Void>() {
                    public Void then(Task<Void> task) {
                        Assert.assertEquals(20, sequence.size());
                        for (int i = 0; i < 20; i++) {
                            Assert.assertEquals(i, sequence.get(i).intValue());
                        }
                        return null;
                    }
                });
                return result;
            }
        });
    }

    @Test
    public void testOnSuccess() {
        Continuation<Integer, Integer> continuation = new Continuation<Integer, Integer>() {
            public Integer then(Task<Integer> task) {
                return (task.getResult()) + 1;
            }
        };
        Task<Integer> complete = Task.forResult(5).onSuccess(continuation);
        Task<Integer> error = Task.<Integer>forError(new IllegalStateException()).onSuccess(continuation);
        Task<Integer> cancelled = Task.<Integer>cancelled().onSuccess(continuation);
        Assert.assertTrue(complete.isCompleted());
        Assert.assertEquals(6, complete.getResult().intValue());
        Assert.assertFalse(complete.isFaulted());
        Assert.assertFalse(complete.isCancelled());
        Assert.assertTrue(error.isCompleted());
        Assert.assertTrue(((error.getError()) instanceof RuntimeException));
        Assert.assertTrue(error.isFaulted());
        Assert.assertFalse(error.isCancelled());
        Assert.assertTrue(cancelled.isCompleted());
        Assert.assertFalse(cancelled.isFaulted());
        Assert.assertTrue(cancelled.isCancelled());
    }

    @Test
    public void testOnSuccessTask() {
        Continuation<Integer, Task<Integer>> continuation = new Continuation<Integer, Task<Integer>>() {
            public Task<Integer> then(Task<Integer> task) {
                return Task.forResult(((task.getResult()) + 1));
            }
        };
        Task<Integer> complete = Task.forResult(5).onSuccessTask(continuation);
        Task<Integer> error = Task.<Integer>forError(new IllegalStateException()).onSuccessTask(continuation);
        Task<Integer> cancelled = Task.<Integer>cancelled().onSuccessTask(continuation);
        Assert.assertTrue(complete.isCompleted());
        Assert.assertEquals(6, complete.getResult().intValue());
        Assert.assertFalse(complete.isFaulted());
        Assert.assertFalse(complete.isCancelled());
        Assert.assertTrue(error.isCompleted());
        Assert.assertTrue(((error.getError()) instanceof RuntimeException));
        Assert.assertTrue(error.isFaulted());
        Assert.assertFalse(error.isCancelled());
        Assert.assertTrue(cancelled.isCompleted());
        Assert.assertFalse(cancelled.isFaulted());
        Assert.assertTrue(cancelled.isCancelled());
    }

    @Test
    public void testContinueWhile() {
        final AtomicInteger count = new AtomicInteger(0);
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.forResult(null).continueWhile(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return (count.get()) < 10;
                    }
                }, new Continuation<Void, Task<Void>>() {
                    public Task<Void> then(Task<Void> task) throws Exception {
                        count.incrementAndGet();
                        return null;
                    }
                }).continueWith(new Continuation<Void, Void>() {
                    public Void then(Task<Void> task) throws Exception {
                        Assert.assertEquals(10, count.get());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testContinueWhileAsync() {
        final AtomicInteger count = new AtomicInteger(0);
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.forResult(null).continueWhile(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return (count.get()) < 10;
                    }
                }, new Continuation<Void, Task<Void>>() {
                    public Task<Void> then(Task<Void> task) throws Exception {
                        count.incrementAndGet();
                        return null;
                    }
                }, Executors.newCachedThreadPool()).continueWith(new Continuation<Void, Void>() {
                    public Void then(Task<Void> task) throws Exception {
                        Assert.assertEquals(10, count.get());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testContinueWhileAsyncCancellation() {
        final AtomicInteger count = new AtomicInteger(0);
        final CancellationTokenSource cts = new CancellationTokenSource();
        runTaskTest(new Callable<Task<?>>() {
            public Task<?> call() throws Exception {
                return Task.forResult(null).continueWhile(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        return (count.get()) < 10;
                    }
                }, new Continuation<Void, Task<Void>>() {
                    public Task<Void> then(Task<Void> task) throws Exception {
                        if ((count.incrementAndGet()) == 5) {
                            cts.cancel();
                        }
                        return null;
                    }
                }, Executors.newCachedThreadPool(), cts.getToken()).continueWith(new Continuation<Void, Void>() {
                    public Void then(Task<Void> task) throws Exception {
                        Assert.assertTrue(task.isCancelled());
                        Assert.assertEquals(5, count.get());
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void testCallWithBadExecutor() {
        final RuntimeException exception = new RuntimeException("BAD EXECUTORS");
        Task.call(new Callable<Integer>() {
            public Integer call() throws Exception {
                return 1;
            }
        }, new Executor() {
            @Override
            public void execute(Runnable command) {
                throw exception;
            }
        }).continueWith(new Continuation<Integer, Object>() {
            @Override
            public Object then(Task<Integer> task) throws Exception {
                Assert.assertTrue(task.isFaulted());
                Assert.assertTrue(((task.getError()) instanceof ExecutorException));
                Assert.assertEquals(exception, task.getError().getCause());
                return null;
            }
        });
    }

    @Test
    public void testContinueWithBadExecutor() {
        final RuntimeException exception = new RuntimeException("BAD EXECUTORS");
        Task.call(new Callable<Integer>() {
            public Integer call() throws Exception {
                return 1;
            }
        }).continueWith(new Continuation<Integer, Integer>() {
            @Override
            public Integer then(Task<Integer> task) throws Exception {
                return task.getResult();
            }
        }, new Executor() {
            @Override
            public void execute(Runnable command) {
                throw exception;
            }
        }).continueWith(new Continuation<Integer, Object>() {
            @Override
            public Object then(Task<Integer> task) throws Exception {
                Assert.assertTrue(task.isFaulted());
                Assert.assertTrue(((task.getError()) instanceof ExecutorException));
                Assert.assertEquals(exception, task.getError().getCause());
                return null;
            }
        });
    }

    @Test
    public void testContinueWithTaskAndBadExecutor() {
        final RuntimeException exception = new RuntimeException("BAD EXECUTORS");
        Task.call(new Callable<Integer>() {
            public Integer call() throws Exception {
                return 1;
            }
        }).continueWithTask(new Continuation<Integer, Task<Integer>>() {
            @Override
            public Task<Integer> then(Task<Integer> task) throws Exception {
                return task;
            }
        }, new Executor() {
            @Override
            public void execute(Runnable command) {
                throw exception;
            }
        }).continueWith(new Continuation<Integer, Object>() {
            @Override
            public Object then(Task<Integer> task) throws Exception {
                Assert.assertTrue(task.isFaulted());
                Assert.assertTrue(((task.getError()) instanceof ExecutorException));
                Assert.assertEquals(exception, task.getError().getCause());
                return null;
            }
        });
    }

    // region TaskCompletionSource
    @Test
    public void testTrySetResult() {
        TaskCompletionSource<String> tcs = new TaskCompletionSource();
        Task<String> task = tcs.getTask();
        Assert.assertFalse(task.isCompleted());
        boolean success = tcs.trySetResult("SHOW ME WHAT YOU GOT");
        Assert.assertTrue(success);
        Assert.assertTrue(task.isCompleted());
        Assert.assertEquals("SHOW ME WHAT YOU GOT", task.getResult());
    }

    @Test
    public void testTrySetError() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource();
        Task<Void> task = tcs.getTask();
        Assert.assertFalse(task.isCompleted());
        Exception exception = new RuntimeException("DISQUALIFIED");
        boolean success = tcs.trySetError(exception);
        Assert.assertTrue(success);
        Assert.assertTrue(task.isCompleted());
        Assert.assertEquals(exception, task.getError());
    }

    @Test
    public void testTrySetCanceled() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource();
        Task<Void> task = tcs.getTask();
        Assert.assertFalse(task.isCompleted());
        boolean success = tcs.trySetCancelled();
        Assert.assertTrue(success);
        Assert.assertTrue(task.isCompleted());
        Assert.assertTrue(task.isCancelled());
    }

    @Test
    public void testTrySetOnCompletedTask() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource();
        tcs.setResult(null);
        Assert.assertFalse(tcs.trySetResult(null));
        Assert.assertFalse(tcs.trySetError(new RuntimeException()));
        Assert.assertFalse(tcs.trySetCancelled());
    }

    @Test
    public void testSetResultOnCompletedTask() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource();
        tcs.setResult(null);
        thrown.expect(IllegalStateException.class);
        tcs.setResult(null);
    }

    @Test
    public void testSetErrorOnCompletedTask() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource();
        tcs.setResult(null);
        thrown.expect(IllegalStateException.class);
        tcs.setError(new RuntimeException());
    }

    @Test
    public void testSetCancelledOnCompletedTask() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource();
        tcs.setResult(null);
        thrown.expect(IllegalStateException.class);
        tcs.setCancelled();
    }

    // endregion
    // region deprecated
    @SuppressWarnings("deprecation")
    @Test
    public void testDeprecatedTaskCompletionSource() {
        Task<Void>.TaskCompletionSource tcsA = Task.create();
        tcsA.setResult(null);
        Assert.assertTrue(tcsA.getTask().isCompleted());
        TaskCompletionSource<Void> tcsB = Task.create();
        tcsB.setResult(null);
        Assert.assertTrue(tcsA.getTask().isCompleted());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeprecatedAggregateExceptionMethods() {
        final Exception error0 = new Exception("This is an exception (0).");
        final Exception error1 = new Exception("This is an exception (1).");
        final Error error2 = new Error("This is an error.");
        List<Exception> exceptions = new ArrayList<>();
        exceptions.add(error0);
        exceptions.add(error1);
        // Test old functionality
        AggregateException aggregate = new AggregateException(exceptions);
        Assert.assertEquals("There were multiple errors.", aggregate.getMessage());
        Assert.assertEquals(2, aggregate.getErrors().size());
        Assert.assertEquals(error0, aggregate.getErrors().get(0));
        Assert.assertEquals(error1, aggregate.getErrors().get(1));
        Assert.assertEquals(2, aggregate.getCauses().length);
        Assert.assertEquals(error0, aggregate.getCauses()[0]);
        Assert.assertEquals(error1, aggregate.getCauses()[1]);
        // Test deprecated getErrors method returns sane results with non-Exceptions
        aggregate = new AggregateException("message", new Throwable[]{ error0, error1, error2 });
        Assert.assertEquals("message", aggregate.getMessage());
        Assert.assertEquals(3, aggregate.getErrors().size());
        Assert.assertEquals(error0, aggregate.getErrors().get(0));
        Assert.assertEquals(error1, aggregate.getErrors().get(1));
        Assert.assertNotSame(error2, aggregate.getErrors().get(2));
        Assert.assertEquals(error2, aggregate.getErrors().get(2).getCause());
    }
}

