/**
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import Context.CancellableContext;
import Context.CancellationListener;
import Context.ROOT;
import Context.Storage;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Context.CONTEXT_DEPTH_WARN_THRESH;


/**
 * Tests for {@link Context}.
 */
// false-positive in test for current ver errorprone plugin
@RunWith(JUnit4.class)
@SuppressWarnings("CheckReturnValue")
public class ContextTest {
    private static final Context.Key<String> PET = Context.key("pet");

    private static final Context.Key<String> FOOD = Context.keyWithDefault("food", "lasagna");

    private static final Context.Key<String> COLOR = Context.key("color");

    private static final Context.Key<Object> FAVORITE = Context.key("favorite");

    private static final Context.Key<Integer> LUCKY = Context.key("lucky");

    private Context listenerNotifedContext;

    private CountDownLatch deadlineLatch = new CountDownLatch(1);

    private CancellationListener cancellationListener = new Context.CancellationListener() {
        @Override
        public void cancelled(Context context) {
            listenerNotifedContext = context;
            deadlineLatch.countDown();
        }
    };

    private Context observed;

    private Runnable runner = new Runnable() {
        @Override
        public void run() {
            observed = Context.current();
        }
    };

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void defaultContext() throws Exception {
        final SettableFuture<Context> contextOfNewThread = SettableFuture.create();
        Context contextOfThisThread = ROOT.withValue(io.grpc.PET, "dog");
        Context toRestore = contextOfThisThread.attach();
        new Thread(new Runnable() {
            @Override
            public void run() {
                contextOfNewThread.set(Context.current());
            }
        }).start();
        Assert.assertNotNull(contextOfNewThread.get(5, TimeUnit.SECONDS));
        Assert.assertNotSame(contextOfThisThread, contextOfNewThread.get());
        Assert.assertSame(contextOfThisThread, Context.current());
        contextOfThisThread.detach(toRestore);
    }

    @Test
    public void rootCanBeAttached() {
        Context fork = ROOT.fork();
        Context toRestore1 = fork.attach();
        Context toRestore2 = ROOT.attach();
        Assert.assertTrue(ROOT.isCurrent());
        Context toRestore3 = fork.attach();
        Assert.assertTrue(fork.isCurrent());
        fork.detach(toRestore3);
        ROOT.detach(toRestore2);
        fork.detach(toRestore1);
    }

    @Test
    public void rootCanNeverHaveAListener() {
        Context root = Context.current();
        root.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertEquals(0, root.listenerCount());
    }

    @Test
    public void rootIsNotCancelled() {
        Assert.assertFalse(ROOT.isCancelled());
        Assert.assertNull(ROOT.cancellationCause());
    }

    @Test
    public void attachedCancellableContextCannotBeCastFromCurrent() {
        Context initial = Context.current();
        Context.CancellableContext base = initial.withCancellation();
        base.attach();
        Assert.assertFalse(((Context.current()) instanceof Context.CancellableContext));
        Assert.assertNotSame(base, Context.current());
        Assert.assertNotSame(initial, Context.current());
        base.detachAndCancel(initial, null);
        Assert.assertSame(initial, Context.current());
    }

    @Test
    public void attachingNonCurrentReturnsCurrent() {
        Context initial = Context.current();
        Context base = initial.withValue(io.grpc.PET, "dog");
        Assert.assertSame(initial, base.attach());
        Assert.assertSame(base, initial.attach());
    }

    @Test
    public void detachingNonCurrentLogsSevereMessage() {
        final AtomicReference<LogRecord> logRef = new AtomicReference<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRef.set(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        Logger logger = Logger.getLogger(Context.storage().getClass().getName());
        try {
            logger.addHandler(handler);
            Context initial = Context.current();
            Context base = initial.withValue(io.grpc.PET, "dog");
            // Base is not attached
            base.detach(initial);
            Assert.assertSame(initial, Context.current());
            Assert.assertNotNull(logRef.get());
            Assert.assertEquals(Level.SEVERE, logRef.get().getLevel());
        } finally {
            logger.removeHandler(handler);
        }
    }

    @Test
    public void valuesAndOverrides() {
        Context base = Context.current().withValue(io.grpc.PET, "dog");
        Context child = base.withValues(io.grpc.PET, null, io.grpc.FOOD, "cheese");
        base.attach();
        Assert.assertEquals("dog", io.grpc.PET.get());
        Assert.assertEquals("lasagna", io.grpc.FOOD.get());
        Assert.assertNull(io.grpc.COLOR.get());
        child.attach();
        Assert.assertNull(io.grpc.PET.get());
        Assert.assertEquals("cheese", io.grpc.FOOD.get());
        Assert.assertNull(io.grpc.COLOR.get());
        child.detach(base);
        // Should have values from base
        Assert.assertEquals("dog", io.grpc.PET.get());
        Assert.assertEquals("lasagna", io.grpc.FOOD.get());
        Assert.assertNull(io.grpc.COLOR.get());
        base.detach(ROOT);
        Assert.assertNull(io.grpc.PET.get());
        Assert.assertEquals("lasagna", io.grpc.FOOD.get());
        Assert.assertNull(io.grpc.COLOR.get());
    }

    @Test
    public void withValuesThree() {
        Object fav = new Object();
        Context base = Context.current().withValues(io.grpc.PET, "dog", io.grpc.COLOR, "blue");
        Context child = base.withValues(io.grpc.PET, "cat", io.grpc.FOOD, "cheese", io.grpc.FAVORITE, fav);
        Context toRestore = child.attach();
        Assert.assertEquals("cat", io.grpc.PET.get());
        Assert.assertEquals("cheese", io.grpc.FOOD.get());
        Assert.assertEquals("blue", io.grpc.COLOR.get());
        Assert.assertEquals(fav, io.grpc.FAVORITE.get());
        child.detach(toRestore);
    }

    @Test
    public void withValuesFour() {
        Object fav = new Object();
        Context base = Context.current().withValues(io.grpc.PET, "dog", io.grpc.COLOR, "blue");
        Context child = base.withValues(io.grpc.PET, "cat", io.grpc.FOOD, "cheese", io.grpc.FAVORITE, fav, io.grpc.LUCKY, 7);
        Context toRestore = child.attach();
        Assert.assertEquals("cat", io.grpc.PET.get());
        Assert.assertEquals("cheese", io.grpc.FOOD.get());
        Assert.assertEquals("blue", io.grpc.COLOR.get());
        Assert.assertEquals(fav, io.grpc.FAVORITE.get());
        Assert.assertEquals(7, ((int) (io.grpc.LUCKY.get())));
        child.detach(toRestore);
    }

    @Test
    public void cancelReturnsFalseIfAlreadyCancelled() {
        Context.CancellableContext base = Context.current().withCancellation();
        Assert.assertTrue(base.cancel(null));
        Assert.assertTrue(base.isCancelled());
        Assert.assertFalse(base.cancel(null));
    }

    @Test
    public void notifyListenersOnCancel() {
        class SetContextCancellationListener implements Context.CancellationListener {
            private final AtomicReference<Context> observed;

            public SetContextCancellationListener(AtomicReference<Context> observed) {
                this.observed = observed;
            }

            @Override
            public void cancelled(Context context) {
                observed.set(context);
            }
        }
        Context.CancellableContext base = Context.current().withCancellation();
        final AtomicReference<Context> observed1 = new AtomicReference<>();
        base.addListener(new SetContextCancellationListener(observed1), MoreExecutors.directExecutor());
        final AtomicReference<Context> observed2 = new AtomicReference<>();
        base.addListener(new SetContextCancellationListener(observed2), MoreExecutors.directExecutor());
        Assert.assertNull(observed1.get());
        Assert.assertNull(observed2.get());
        base.cancel(null);
        Assert.assertSame(base, observed1.get());
        Assert.assertSame(base, observed2.get());
        final AtomicReference<Context> observed3 = new AtomicReference<>();
        base.addListener(new SetContextCancellationListener(observed3), MoreExecutors.directExecutor());
        Assert.assertSame(base, observed3.get());
    }

    @Test
    public void exceptionOfExecutorDoesntThrow() {
        final AtomicReference<Throwable> loggedThrowable = new AtomicReference<>();
        Handler logHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                Throwable thrown = record.getThrown();
                if (thrown != null) {
                    if ((loggedThrowable.get()) == null) {
                        loggedThrowable.set(thrown);
                    } else {
                        loggedThrowable.set(new RuntimeException("Too many exceptions", thrown));
                    }
                }
            }

            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }
        };
        Logger logger = Logger.getLogger(Context.class.getName());
        logger.addHandler(logHandler);
        try {
            Context.CancellableContext base = Context.current().withCancellation();
            final AtomicReference<Runnable> observed1 = new AtomicReference<>();
            final Error err = new Error();
            base.addListener(cancellationListener, new Executor() {
                @Override
                public void execute(Runnable runnable) {
                    observed1.set(runnable);
                    throw err;
                }
            });
            Assert.assertNull(observed1.get());
            Assert.assertNull(loggedThrowable.get());
            base.cancel(null);
            Assert.assertNotNull(observed1.get());
            Assert.assertSame(err, loggedThrowable.get());
            final Error err2 = new Error();
            loggedThrowable.set(null);
            final AtomicReference<Runnable> observed2 = new AtomicReference<>();
            base.addListener(cancellationListener, new Executor() {
                @Override
                public void execute(Runnable runnable) {
                    observed2.set(runnable);
                    throw err2;
                }
            });
            Assert.assertNotNull(observed2.get());
            Assert.assertSame(err2, loggedThrowable.get());
        } finally {
            logger.removeHandler(logHandler);
        }
    }

    @Test
    public void cascadingCancellationNotifiesChild() {
        // Root is not cancellable so we can't cascade from it
        Context.CancellableContext base = Context.current().withCancellation();
        Assert.assertEquals(0, base.listenerCount());
        Context child = base.withValue(io.grpc.FOOD, "lasagna");
        Assert.assertEquals(0, child.listenerCount());
        child.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertEquals(1, child.listenerCount());
        Assert.assertEquals(1, base.listenerCount());// child is now listening to base

        Assert.assertFalse(base.isCancelled());
        Assert.assertFalse(child.isCancelled());
        IllegalStateException cause = new IllegalStateException();
        base.cancel(cause);
        Assert.assertTrue(base.isCancelled());
        Assert.assertSame(cause, base.cancellationCause());
        Assert.assertSame(child, listenerNotifedContext);
        Assert.assertTrue(child.isCancelled());
        Assert.assertSame(cause, child.cancellationCause());
        Assert.assertEquals(0, base.listenerCount());
        Assert.assertEquals(0, child.listenerCount());
    }

    @Test
    public void cascadingCancellationWithoutListener() {
        Context.CancellableContext base = Context.current().withCancellation();
        Context child = base.withCancellation();
        Throwable t = new Throwable();
        base.cancel(t);
        Assert.assertTrue(child.isCancelled());
        Assert.assertSame(t, child.cancellationCause());
    }

    // Context#isCurrent() and Context.CancellableContext#isCurrent() are intended
    // to be visible only for testing. The deprecation is meant for users.
    @SuppressWarnings("deprecation")
    @Test
    public void cancellableContextIsAttached() {
        Context.CancellableContext base = Context.current().withValue(io.grpc.FOOD, "fish").withCancellation();
        Assert.assertFalse(base.isCurrent());
        Context toRestore = base.attach();
        Context attached = Context.current();
        Assert.assertSame("fish", io.grpc.FOOD.get());
        Assert.assertFalse(attached.isCancelled());
        Assert.assertNull(attached.cancellationCause());
        Assert.assertTrue(attached.canBeCancelled());
        Assert.assertTrue(attached.isCurrent());
        Assert.assertTrue(base.isCurrent());
        attached.addListener(cancellationListener, MoreExecutors.directExecutor());
        Throwable t = new Throwable();
        base.cancel(t);
        Assert.assertTrue(attached.isCancelled());
        Assert.assertSame(t, attached.cancellationCause());
        Assert.assertSame(attached, listenerNotifedContext);
        base.detach(toRestore);
    }

    @Test
    public void cancellableContextCascadesFromCancellableParent() {
        // Root is not cancellable so we can't cascade from it
        Context.CancellableContext base = Context.current().withCancellation();
        Context child = base.withCancellation();
        child.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertFalse(base.isCancelled());
        Assert.assertFalse(child.isCancelled());
        IllegalStateException cause = new IllegalStateException();
        base.cancel(cause);
        Assert.assertTrue(base.isCancelled());
        Assert.assertSame(cause, base.cancellationCause());
        Assert.assertSame(child, listenerNotifedContext);
        Assert.assertTrue(child.isCancelled());
        Assert.assertSame(cause, child.cancellationCause());
        Assert.assertEquals(0, base.listenerCount());
        Assert.assertEquals(0, child.listenerCount());
    }

    @Test
    public void nonCascadingCancellationDoesNotNotifyForked() {
        Context.CancellableContext base = Context.current().withCancellation();
        Context fork = base.fork();
        fork.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertEquals(0, base.listenerCount());
        Assert.assertEquals(0, fork.listenerCount());
        Assert.assertTrue(base.cancel(new Throwable()));
        Assert.assertNull(listenerNotifedContext);
        Assert.assertFalse(fork.isCancelled());
        Assert.assertNull(fork.cancellationCause());
    }

    @Test
    public void testWrapRunnable() throws Exception {
        Context base = Context.current().withValue(io.grpc.PET, "cat");
        Context current = Context.current().withValue(io.grpc.PET, "fish");
        current.attach();
        base.wrap(runner).run();
        Assert.assertSame(base, observed);
        Assert.assertSame(current, Context.current());
        current.wrap(runner).run();
        Assert.assertSame(current, observed);
        Assert.assertSame(current, Context.current());
        final ContextTest.TestError err = new ContextTest.TestError();
        try {
            base.wrap(new Runnable() {
                @Override
                public void run() {
                    throw err;
                }
            }).run();
            Assert.fail("Expected exception");
        } catch (ContextTest.TestError ex) {
            Assert.assertSame(err, ex);
        }
        Assert.assertSame(current, Context.current());
        current.detach(ROOT);
    }

    @Test
    public void testWrapCallable() throws Exception {
        Context base = Context.current().withValue(io.grpc.PET, "cat");
        Context current = Context.current().withValue(io.grpc.PET, "fish");
        current.attach();
        final Object ret = new Object();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() {
                runner.run();
                return ret;
            }
        };
        Assert.assertSame(ret, base.wrap(callable).call());
        Assert.assertSame(base, observed);
        Assert.assertSame(current, Context.current());
        Assert.assertSame(ret, current.wrap(callable).call());
        Assert.assertSame(current, observed);
        Assert.assertSame(current, Context.current());
        final ContextTest.TestError err = new ContextTest.TestError();
        try {
            base.wrap(new Callable<Object>() {
                @Override
                public Object call() {
                    throw err;
                }
            }).call();
            Assert.fail("Excepted exception");
        } catch (ContextTest.TestError ex) {
            Assert.assertSame(err, ex);
        }
        Assert.assertSame(current, Context.current());
        current.detach(ROOT);
    }

    @Test
    public void currentContextExecutor() throws Exception {
        ContextTest.QueuedExecutor queuedExecutor = new ContextTest.QueuedExecutor();
        Executor executor = Context.currentContextExecutor(queuedExecutor);
        Context base = Context.current().withValue(io.grpc.PET, "cat");
        Context previous = base.attach();
        try {
            executor.execute(runner);
        } finally {
            base.detach(previous);
        }
        Assert.assertEquals(1, queuedExecutor.runnables.size());
        queuedExecutor.runnables.remove().run();
        Assert.assertSame(base, observed);
    }

    @Test
    public void fixedContextExecutor() throws Exception {
        Context base = Context.current().withValue(io.grpc.PET, "cat");
        ContextTest.QueuedExecutor queuedExecutor = new ContextTest.QueuedExecutor();
        base.fixedContextExecutor(queuedExecutor).execute(runner);
        Assert.assertEquals(1, queuedExecutor.runnables.size());
        queuedExecutor.runnables.remove().run();
        Assert.assertSame(base, observed);
    }

    @Test
    public void typicalTryFinallyHandling() throws Exception {
        Context base = Context.current().withValue(io.grpc.COLOR, "blue");
        Context previous = base.attach();
        try {
            Assert.assertTrue(base.isCurrent());
            // Do something
        } finally {
            base.detach(previous);
        }
        Assert.assertFalse(base.isCurrent());
    }

    @Test
    public void typicalCancellableTryCatchFinallyHandling() throws Exception {
        Context.CancellableContext base = Context.current().withCancellation();
        Context previous = base.attach();
        try {
            // Do something
            throw new IllegalStateException("Argh");
        } catch (IllegalStateException ise) {
            base.cancel(ise);
        } finally {
            base.detachAndCancel(previous, null);
        }
        Assert.assertTrue(base.isCancelled());
        Assert.assertNotNull(base.cancellationCause());
    }

    @Test
    public void rootHasNoDeadline() {
        Assert.assertNull(ROOT.getDeadline());
    }

    @Test
    public void contextWithDeadlineHasDeadline() {
        Context.CancellableContext cancellableContext = ROOT.withDeadlineAfter(1, TimeUnit.SECONDS, scheduler);
        Assert.assertNotNull(cancellableContext.getDeadline());
    }

    @Test
    public void earlierParentDeadlineTakesPrecedenceOverLaterChildDeadline() throws Exception {
        final Deadline sooner = Deadline.after(100, TimeUnit.MILLISECONDS);
        final Deadline later = Deadline.after(1, TimeUnit.MINUTES);
        Context.CancellableContext parent = ROOT.withDeadline(sooner, scheduler);
        Context.CancellableContext child = parent.withDeadline(later, scheduler);
        Assert.assertSame(parent.getDeadline(), sooner);
        Assert.assertSame(child.getDeadline(), sooner);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Exception> error = new AtomicReference<>();
        child.addListener(new Context.CancellationListener() {
            @Override
            public void cancelled(Context context) {
                try {
                    Assert.assertTrue(sooner.isExpired());
                    Assert.assertFalse(later.isExpired());
                } catch (Exception e) {
                    error.set(e);
                }
                latch.countDown();
            }
        }, MoreExecutors.directExecutor());
        latch.await(3, TimeUnit.SECONDS);
        if ((error.get()) != null) {
            throw error.get();
        }
    }

    @Test
    public void earlierChldDeadlineTakesPrecedenceOverLaterParentDeadline() {
        Deadline sooner = Deadline.after(1, TimeUnit.HOURS);
        Deadline later = Deadline.after(1, TimeUnit.DAYS);
        Context.CancellableContext parent = ROOT.withDeadline(later, scheduler);
        Context.CancellableContext child = parent.withDeadline(sooner, scheduler);
        Assert.assertSame(parent.getDeadline(), later);
        Assert.assertSame(child.getDeadline(), sooner);
    }

    @Test
    public void forkingContextDoesNotCarryDeadline() {
        Deadline deadline = Deadline.after(1, TimeUnit.HOURS);
        Context.CancellableContext parent = ROOT.withDeadline(deadline, scheduler);
        Context fork = parent.fork();
        Assert.assertNull(fork.getDeadline());
    }

    @Test
    public void cancellationDoesNotExpireDeadline() {
        Deadline deadline = Deadline.after(1, TimeUnit.HOURS);
        Context.CancellableContext parent = ROOT.withDeadline(deadline, scheduler);
        parent.cancel(null);
        Assert.assertFalse(deadline.isExpired());
    }

    @Test
    public void absoluteDeadlineTriggersAndPropagates() throws Exception {
        Context base = Context.current().withDeadline(Deadline.after(1, TimeUnit.SECONDS), scheduler);
        Context child = base.withValue(io.grpc.FOOD, "lasagna");
        child.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertFalse(base.isCancelled());
        Assert.assertFalse(child.isCancelled());
        Assert.assertTrue(deadlineLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(base.isCancelled());
        Assert.assertTrue(((base.cancellationCause()) instanceof TimeoutException));
        Assert.assertSame(child, listenerNotifedContext);
        Assert.assertTrue(child.isCancelled());
        Assert.assertSame(base.cancellationCause(), child.cancellationCause());
    }

    @Test
    public void relativeDeadlineTriggersAndPropagates() throws Exception {
        Context base = Context.current().withDeadline(Deadline.after(1, TimeUnit.SECONDS), scheduler);
        Context child = base.withValue(io.grpc.FOOD, "lasagna");
        child.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertFalse(base.isCancelled());
        Assert.assertFalse(child.isCancelled());
        Assert.assertTrue(deadlineLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(base.isCancelled());
        Assert.assertTrue(((base.cancellationCause()) instanceof TimeoutException));
        Assert.assertSame(child, listenerNotifedContext);
        Assert.assertTrue(child.isCancelled());
        Assert.assertSame(base.cancellationCause(), child.cancellationCause());
    }

    @Test
    public void innerDeadlineCompletesBeforeOuter() throws Exception {
        Context base = Context.current().withDeadline(Deadline.after(2, TimeUnit.SECONDS), scheduler);
        Context child = base.withDeadline(Deadline.after(1, TimeUnit.SECONDS), scheduler);
        child.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertFalse(base.isCancelled());
        Assert.assertFalse(child.isCancelled());
        Assert.assertTrue(deadlineLatch.await(2, TimeUnit.SECONDS));
        Assert.assertFalse(base.isCancelled());
        Assert.assertSame(child, listenerNotifedContext);
        Assert.assertTrue(child.isCancelled());
        Assert.assertTrue(((child.cancellationCause()) instanceof TimeoutException));
        deadlineLatch = new CountDownLatch(1);
        base.addListener(cancellationListener, MoreExecutors.directExecutor());
        Assert.assertTrue(deadlineLatch.await(2, TimeUnit.SECONDS));
        Assert.assertTrue(base.isCancelled());
        Assert.assertTrue(((base.cancellationCause()) instanceof TimeoutException));
        Assert.assertNotSame(base.cancellationCause(), child.cancellationCause());
    }

    @Test
    public void cancellationCancelsScheduledTask() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        try {
            Assert.assertEquals(0, executor.getQueue().size());
            Context.CancellableContext base = Context.current().withDeadline(Deadline.after(1, TimeUnit.DAYS), executor);
            Assert.assertEquals(1, executor.getQueue().size());
            base.cancel(null);
            executor.purge();
            Assert.assertEquals(0, executor.getQueue().size());
        } finally {
            executor.shutdown();
        }
    }

    private static class QueuedExecutor implements Executor {
        private final Queue<Runnable> runnables = new ArrayDeque<>();

        @Override
        public void execute(Runnable r) {
            runnables.add(r);
        }
    }

    @Test
    public void childContextListenerNotifiedAfterParentListener() {
        Context.CancellableContext parent = Context.current().withCancellation();
        Context child = parent.withValue(io.grpc.COLOR, "red");
        final AtomicBoolean childAfterParent = new AtomicBoolean();
        final AtomicBoolean parentCalled = new AtomicBoolean();
        child.addListener(new Context.CancellationListener() {
            @Override
            public void cancelled(Context context) {
                if (parentCalled.get()) {
                    childAfterParent.set(true);
                }
            }
        }, MoreExecutors.directExecutor());
        parent.addListener(new Context.CancellationListener() {
            @Override
            public void cancelled(Context context) {
                parentCalled.set(true);
            }
        }, MoreExecutors.directExecutor());
        parent.cancel(null);
        Assert.assertTrue(parentCalled.get());
        Assert.assertTrue(childAfterParent.get());
    }

    @Test
    public void expiredDeadlineShouldCancelContextImmediately() {
        Context parent = Context.current();
        Assert.assertFalse(parent.isCancelled());
        Context.CancellableContext context = parent.withDeadlineAfter(0, TimeUnit.SECONDS, scheduler);
        Assert.assertTrue(context.isCancelled());
        Assert.assertThat(context.cancellationCause(), IsInstanceOf.instanceOf(TimeoutException.class));
        Assert.assertFalse(parent.isCancelled());
        Deadline deadline = Deadline.after((-10), TimeUnit.SECONDS);
        Assert.assertTrue(deadline.isExpired());
        context = parent.withDeadline(deadline, scheduler);
        Assert.assertTrue(context.isCancelled());
        Assert.assertThat(context.cancellationCause(), IsInstanceOf.instanceOf(TimeoutException.class));
    }

    /**
     * Tests initializing the {@link Context} class with a custom logger which uses Context's storage
     * when logging.
     */
    @Test
    public void initContextWithCustomClassLoaderWithCustomLogger() throws Exception {
        StaticTestingClassLoader classLoader = new StaticTestingClassLoader(getClass().getClassLoader(), Pattern.compile("io\\.grpc\\.[^.]+"));
        Class<?> runnable = classLoader.loadClass(ContextTest.LoadMeWithStaticTestingClassLoader.class.getName());
        ((Runnable) (runnable.getDeclaredConstructor().newInstance())).run();
    }

    /**
     * Ensure that newly created threads can attach/detach a context.
     * The current test thread already has a context manually attached in {@link #setUp()}.
     */
    @Test
    public void newThreadAttachContext() throws Exception {
        Context parent = Context.current().withValue(io.grpc.COLOR, "blue");
        parent.call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Assert.assertEquals("blue", io.grpc.COLOR.get());
                final Context child = Context.current().withValue(io.grpc.COLOR, "red");
                Future<String> workerThreadVal = scheduler.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        Context initial = Context.current();
                        Assert.assertNotNull(initial);
                        Context toRestore = child.attach();
                        try {
                            Assert.assertNotNull(toRestore);
                            return io.grpc.COLOR.get();
                        } finally {
                            child.detach(toRestore);
                            Assert.assertEquals(initial, Context.current());
                        }
                    }
                });
                Assert.assertEquals("red", workerThreadVal.get());
                Assert.assertEquals("blue", io.grpc.COLOR.get());
                return null;
            }
        });
    }

    /**
     * Similar to {@link #newThreadAttachContext()} but without giving the new thread a specific ctx.
     */
    @Test
    public void newThreadWithoutContext() throws Exception {
        Context parent = Context.current().withValue(io.grpc.COLOR, "blue");
        parent.call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Assert.assertEquals("blue", io.grpc.COLOR.get());
                Future<String> workerThreadVal = scheduler.submit(new Callable<String>() {
                    @Override
                    public String call() {
                        Assert.assertNotNull(Context.current());
                        return io.grpc.COLOR.get();
                    }
                });
                Assert.assertEquals(null, workerThreadVal.get());
                Assert.assertEquals("blue", io.grpc.COLOR.get());
                return null;
            }
        });
    }

    @Test
    public void storageReturnsNullTest() throws Exception {
        Field storage = Context.class.getDeclaredField("storage");
        Assert.assertTrue(Modifier.isFinal(storage.getModifiers()));
        // use reflection to forcibly change the storage object to a test object
        storage.setAccessible(true);
        Object o = storage.get(null);
        @SuppressWarnings("unchecked")
        AtomicReference<Context.Storage> storageRef = ((AtomicReference<Context.Storage>) (o));
        Context.Storage originalStorage = storageRef.get();
        try {
            storageRef.set(new Context.Storage() {
                @Override
                public Context doAttach(Context toAttach) {
                    return null;
                }

                @Override
                public void detach(Context toDetach, Context toRestore) {
                    // noop
                }

                @Override
                public Context current() {
                    return null;
                }
            });
            // current() returning null gets transformed into ROOT
            Assert.assertEquals(ROOT, Context.current());
            // doAttach() returning null gets transformed into ROOT
            Context blueContext = Context.current().withValue(io.grpc.COLOR, "blue");
            Context toRestore = blueContext.attach();
            Assert.assertEquals(ROOT, toRestore);
            // final sanity check
            blueContext.detach(toRestore);
            Assert.assertEquals(ROOT, Context.current());
        } finally {
            // undo the changes
            storageRef.set(originalStorage);
            storage.setAccessible(false);
        }
    }

    @Test
    public void cancellableAncestorTest() {
        Assert.assertEquals(null, Context.cancellableAncestor(null));
        Context c = Context.current();
        Assert.assertFalse(c.canBeCancelled());
        Assert.assertEquals(null, Context.cancellableAncestor(c));
        Context.CancellableContext withCancellation = c.withCancellation();
        Assert.assertEquals(withCancellation, Context.cancellableAncestor(withCancellation));
        Context child = withCancellation.withValue(io.grpc.COLOR, "blue");
        Assert.assertFalse((child instanceof Context.CancellableContext));
        Assert.assertEquals(withCancellation, Context.cancellableAncestor(child));
        Context grandChild = child.withValue(io.grpc.COLOR, "red");
        Assert.assertFalse((grandChild instanceof Context.CancellableContext));
        Assert.assertEquals(withCancellation, Context.cancellableAncestor(grandChild));
    }

    @Test
    public void cancellableAncestorIntegrationTest() {
        Context base = Context.current();
        Context blue = base.withValue(io.grpc.COLOR, "blue");
        Assert.assertNull(blue.cancellableAncestor);
        Context.CancellableContext cancellable = blue.withCancellation();
        Assert.assertNull(cancellable.cancellableAncestor);
        Context childOfCancel = cancellable.withValue(io.grpc.PET, "cat");
        Assert.assertSame(cancellable, childOfCancel.cancellableAncestor);
        Context grandChildOfCancel = childOfCancel.withValue(io.grpc.FOOD, "lasagna");
        Assert.assertSame(cancellable, grandChildOfCancel.cancellableAncestor);
        Context.CancellableContext cancellable2 = childOfCancel.withCancellation();
        Assert.assertSame(cancellable, cancellable2.cancellableAncestor);
        Context childOfCancellable2 = cancellable2.withValue(io.grpc.PET, "dog");
        Assert.assertSame(cancellable2, childOfCancellable2.cancellableAncestor);
    }

    @Test
    public void cancellableAncestorFork() {
        Context.CancellableContext cancellable = Context.current().withCancellation();
        Context fork = cancellable.fork();
        Assert.assertNull(fork.cancellableAncestor);
    }

    @Test
    public void cancellableContext_closeCancelsWithNullCause() throws Exception {
        Context.CancellableContext cancellable = Context.current().withCancellation();
        cancellable.close();
        Assert.assertTrue(cancellable.isCancelled());
        Assert.assertNull(cancellable.cancellationCause());
    }

    @Test
    public void errorWhenAncestryLengthLong() {
        final AtomicReference<LogRecord> logRef = new AtomicReference<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRef.set(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        Logger logger = Logger.getLogger(Context.class.getName());
        try {
            logger.addHandler(handler);
            Context ctx = Context.current();
            for (int i = 0; i < (CONTEXT_DEPTH_WARN_THRESH); i++) {
                Assert.assertNull(logRef.get());
                ctx = ctx.fork();
            }
            ctx = ctx.fork();
            Assert.assertNotNull(logRef.get());
            Assert.assertNotNull(logRef.get().getThrown());
            Assert.assertEquals(Level.SEVERE, logRef.get().getLevel());
        } finally {
            logger.removeHandler(handler);
        }
    }

    // UsedReflectively
    public static final class LoadMeWithStaticTestingClassLoader implements Runnable {
        @Override
        public void run() {
            Logger logger = Logger.getLogger(Context.class.getName());
            logger.setLevel(Level.ALL);
            Handler handler = new Handler() {
                @Override
                public void publish(LogRecord record) {
                    Context ctx = Context.current();
                    Context previous = ctx.attach();
                    ctx.detach(previous);
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            };
            logger.addHandler(handler);
            try {
                Assert.assertNotNull(ROOT);
            } finally {
                logger.removeHandler(handler);
            }
        }
    }

    /**
     * Allows more precise catch blocks than plain Error to avoid catching AssertionError.
     */
    private static final class TestError extends Error {}
}

