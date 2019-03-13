package org.junit.runner.notification;


import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;


public class RunNotifierTest {
    private final RunNotifier fNotifier = new RunNotifier();

    @Test
    public void notifiesSecondListenerIfFirstThrowsException() {
        RunNotifierTest.FailureListener failureListener = new RunNotifierTest.FailureListener();
        fNotifier.addListener(new RunNotifierTest.CorruptListener());
        fNotifier.addListener(failureListener);
        fNotifier.fireTestFailure(new Failure(null, null));
        Assert.assertNotNull("The FailureListener registered no failure.", failureListener.failure);
    }

    @Test
    public void hasNoProblemsWithFailingListeners() {
        // see issues 209 and 395
        fNotifier.addListener(new RunNotifierTest.CorruptListener());
        fNotifier.addListener(new RunNotifierTest.FailureListener());
        fNotifier.addListener(new RunNotifierTest.CorruptListener());
        fNotifier.fireTestRunFinished(new Result());
    }

    private static class CorruptListener extends RunListener {
        @Override
        public void testRunFinished(Result result) throws Exception {
            throw new RuntimeException();
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            throw new RuntimeException();
        }
    }

    @Test
    public void addAndRemoveWithNonThreadSafeListener() {
        RunNotifierTest.CountingListener listener = new RunNotifierTest.CountingListener();
        Assert.assertThat(listener.fTestStarted.get(), Is.is(0));
        fNotifier.addListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
    }

    @Test
    public void addFirstAndRemoveWithNonThreadSafeListener() {
        RunNotifierTest.CountingListener listener = new RunNotifierTest.CountingListener();
        Assert.assertThat(listener.fTestStarted.get(), Is.is(0));
        fNotifier.addFirstListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
    }

    @Test
    public void addAndRemoveWithThreadSafeListener() {
        RunNotifierTest.ThreadSafeListener listener = new RunNotifierTest.ThreadSafeListener();
        Assert.assertThat(listener.fTestStarted.get(), Is.is(0));
        fNotifier.addListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
    }

    @Test
    public void addFirstAndRemoveWithThreadSafeListener() {
        RunNotifierTest.ThreadSafeListener listener = new RunNotifierTest.ThreadSafeListener();
        Assert.assertThat(listener.fTestStarted.get(), Is.is(0));
        fNotifier.addFirstListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        Assert.assertThat(listener.fTestStarted.get(), Is.is(1));
    }

    @Test
    public void wrapIfNotThreadSafeShouldNotWrapThreadSafeListeners() {
        RunNotifierTest.ThreadSafeListener listener = new RunNotifierTest.ThreadSafeListener();
        Assert.assertSame(listener, new RunNotifier().wrapIfNotThreadSafe(listener));
    }

    @Test
    public void wrapIfNotThreadSafeShouldWrapNonThreadSafeListeners() {
        RunNotifierTest.CountingListener listener = new RunNotifierTest.CountingListener();
        RunListener wrappedListener = new RunNotifier().wrapIfNotThreadSafe(listener);
        Assert.assertThat(wrappedListener, CoreMatchers.instanceOf(SynchronizedRunListener.class));
    }

    private static class FailureListener extends RunListener {
        private Failure failure;

        @Override
        public void testFailure(Failure failure) throws Exception {
            this.failure = failure;
        }
    }

    private static class CountingListener extends RunListener {
        final AtomicInteger fTestStarted = new AtomicInteger(0);

        @Override
        public void testStarted(Description description) throws Exception {
            fTestStarted.incrementAndGet();
        }
    }

    @RunListener.ThreadSafe
    private static class ThreadSafeListener extends RunNotifierTest.CountingListener {}
}

