package okhttp3;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.RealCall.AsyncCall;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public final class DispatcherTest {
    @Rule
    public final OkHttpClientTestRule clientTestRule = new OkHttpClientTestRule();

    DispatcherTest.RecordingExecutor executor = new DispatcherTest.RecordingExecutor();

    RecordingCallback callback = new RecordingCallback();

    RecordingWebSocketListener webSocketListener = new RecordingWebSocketListener();

    Dispatcher dispatcher = new Dispatcher(executor);

    RecordingEventListener listener = new RecordingEventListener();

    OkHttpClient client = clientTestRule.client.newBuilder().dispatcher(dispatcher).eventListener(listener).build();

    @Test
    public void maxRequestsZero() throws Exception {
        try {
            dispatcher.setMaxRequests(0);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void maxPerHostZero() throws Exception {
        try {
            dispatcher.setMaxRequestsPerHost(0);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void enqueuedJobsRunImmediately() throws Exception {
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        executor.assertJobs("http://a/1");
    }

    @Test
    public void maxRequestsEnforced() throws Exception {
        dispatcher.setMaxRequests(3);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        client.newCall(newRequest("http://b/1")).enqueue(callback);
        client.newCall(newRequest("http://b/2")).enqueue(callback);
        executor.assertJobs("http://a/1", "http://a/2", "http://b/1");
    }

    @Test
    public void maxPerHostEnforced() throws Exception {
        dispatcher.setMaxRequestsPerHost(2);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        client.newCall(newRequest("http://a/3")).enqueue(callback);
        executor.assertJobs("http://a/1", "http://a/2");
    }

    @Test
    public void maxPerHostNotEnforcedForWebSockets() {
        dispatcher.setMaxRequestsPerHost(2);
        client.newWebSocket(newRequest("http://a/1"), webSocketListener);
        client.newWebSocket(newRequest("http://a/2"), webSocketListener);
        client.newWebSocket(newRequest("http://a/3"), webSocketListener);
        executor.assertJobs("http://a/1", "http://a/2", "http://a/3");
    }

    @Test
    public void increasingMaxRequestsPromotesJobsImmediately() throws Exception {
        dispatcher.setMaxRequests(2);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://b/1")).enqueue(callback);
        client.newCall(newRequest("http://c/1")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        client.newCall(newRequest("http://b/2")).enqueue(callback);
        dispatcher.setMaxRequests(4);
        executor.assertJobs("http://a/1", "http://b/1", "http://c/1", "http://a/2");
    }

    @Test
    public void increasingMaxPerHostPromotesJobsImmediately() throws Exception {
        dispatcher.setMaxRequestsPerHost(2);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        client.newCall(newRequest("http://a/3")).enqueue(callback);
        client.newCall(newRequest("http://a/4")).enqueue(callback);
        client.newCall(newRequest("http://a/5")).enqueue(callback);
        dispatcher.setMaxRequestsPerHost(4);
        executor.assertJobs("http://a/1", "http://a/2", "http://a/3", "http://a/4");
    }

    @Test
    public void oldJobFinishesNewJobCanRunDifferentHost() throws Exception {
        dispatcher.setMaxRequests(1);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://b/1")).enqueue(callback);
        executor.finishJob("http://a/1");
        executor.assertJobs("http://b/1");
    }

    @Test
    public void oldJobFinishesNewJobWithSameHostStarts() throws Exception {
        dispatcher.setMaxRequests(2);
        dispatcher.setMaxRequestsPerHost(1);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://b/1")).enqueue(callback);
        client.newCall(newRequest("http://b/2")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        executor.finishJob("http://a/1");
        executor.assertJobs("http://b/1", "http://a/2");
    }

    @Test
    public void oldJobFinishesNewJobCantRunDueToHostLimit() throws Exception {
        dispatcher.setMaxRequestsPerHost(1);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://b/1")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        executor.finishJob("http://b/1");
        executor.assertJobs("http://a/1");
    }

    @Test
    public void enqueuedCallsStillRespectMaxCallsPerHost() throws Exception {
        dispatcher.setMaxRequests(1);
        dispatcher.setMaxRequestsPerHost(1);
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://b/1")).enqueue(callback);
        client.newCall(newRequest("http://b/2")).enqueue(callback);
        client.newCall(newRequest("http://b/3")).enqueue(callback);
        dispatcher.setMaxRequests(3);
        executor.finishJob("http://a/1");
        executor.assertJobs("http://b/1");
    }

    @Test
    public void cancelingRunningJobTakesNoEffectUntilJobFinishes() throws Exception {
        dispatcher.setMaxRequests(1);
        Call c1 = client.newCall(newRequest("http://a/1", "tag1"));
        Call c2 = client.newCall(newRequest("http://a/2"));
        c1.enqueue(callback);
        c2.enqueue(callback);
        c1.cancel();
        executor.assertJobs("http://a/1");
        executor.finishJob("http://a/1");
        executor.assertJobs("http://a/2");
    }

    @Test
    public void asyncCallAccessors() throws Exception {
        dispatcher.setMaxRequests(3);
        Call a1 = client.newCall(newRequest("http://a/1"));
        Call a2 = client.newCall(newRequest("http://a/2"));
        Call a3 = client.newCall(newRequest("http://a/3"));
        Call a4 = client.newCall(newRequest("http://a/4"));
        Call a5 = client.newCall(newRequest("http://a/5"));
        a1.enqueue(callback);
        a2.enqueue(callback);
        a3.enqueue(callback);
        a4.enqueue(callback);
        a5.enqueue(callback);
        Assert.assertEquals(3, dispatcher.runningCallsCount());
        Assert.assertEquals(2, dispatcher.queuedCallsCount());
        Assert.assertEquals(set(a1, a2, a3), set(dispatcher.runningCalls()));
        Assert.assertEquals(set(a4, a5), set(dispatcher.queuedCalls()));
    }

    @Test
    public void synchronousCallAccessors() throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch waiting = new CountDownLatch(1);
        client = client.newBuilder().addInterceptor(( chain) -> {
            try {
                ready.countDown();
                waiting.await();
            } catch ( e) {
                throw new AssertionError();
            }
            throw new IOException();
        }).build();
        Call a1 = client.newCall(newRequest("http://a/1"));
        Call a2 = client.newCall(newRequest("http://a/2"));
        Call a3 = client.newCall(newRequest("http://a/3"));
        Call a4 = client.newCall(newRequest("http://a/4"));
        Thread t1 = makeSynchronousCall(a1);
        Thread t2 = makeSynchronousCall(a2);
        // We created 4 calls and started 2 of them. That's 2 running calls and 0 queued.
        ready.await();
        Assert.assertEquals(2, dispatcher.runningCallsCount());
        Assert.assertEquals(0, dispatcher.queuedCallsCount());
        Assert.assertEquals(set(a1, a2), set(dispatcher.runningCalls()));
        Assert.assertEquals(Collections.emptyList(), dispatcher.queuedCalls());
        // Cancel some calls. That doesn't impact running or queued.
        a2.cancel();
        a3.cancel();
        Assert.assertEquals(set(a1, a2), set(dispatcher.runningCalls()));
        Assert.assertEquals(Collections.emptyList(), dispatcher.queuedCalls());
        // Let the calls finish.
        waiting.countDown();
        t1.join();
        t2.join();
        // Now we should have 0 running calls and 0 queued calls.
        Assert.assertEquals(0, dispatcher.runningCallsCount());
        Assert.assertEquals(0, dispatcher.queuedCallsCount());
        Assert.assertEquals(Collections.emptyList(), dispatcher.runningCalls());
        Assert.assertEquals(Collections.emptyList(), dispatcher.queuedCalls());
        Assert.assertTrue(a1.isExecuted());
        Assert.assertFalse(a1.isCanceled());
        Assert.assertTrue(a2.isExecuted());
        Assert.assertTrue(a2.isCanceled());
        Assert.assertFalse(a3.isExecuted());
        Assert.assertTrue(a3.isCanceled());
        Assert.assertFalse(a4.isExecuted());
        Assert.assertFalse(a4.isCanceled());
    }

    @Test
    public void idleCallbackInvokedWhenIdle() throws Exception {
        AtomicBoolean idle = new AtomicBoolean();
        dispatcher.setIdleCallback(() -> idle.set(true));
        client.newCall(newRequest("http://a/1")).enqueue(callback);
        client.newCall(newRequest("http://a/2")).enqueue(callback);
        executor.finishJob("http://a/1");
        Assert.assertFalse(idle.get());
        CountDownLatch ready = new CountDownLatch(1);
        CountDownLatch proceed = new CountDownLatch(1);
        client = client.newBuilder().addInterceptor(( chain) -> {
            ready.countDown();
            try {
                proceed.await(5, SECONDS);
            } catch ( e) {
                throw new <e>RuntimeException();
            }
            return chain.proceed(chain.request());
        }).build();
        Thread t1 = makeSynchronousCall(client.newCall(newRequest("http://a/3")));
        ready.await(5, TimeUnit.SECONDS);
        executor.finishJob("http://a/2");
        Assert.assertFalse(idle.get());
        proceed.countDown();
        t1.join();
        Assert.assertTrue(idle.get());
    }

    @Test
    public void executionRejectedImmediately() throws Exception {
        Request request = newRequest("http://a/1");
        executor.shutdown();
        client.newCall(request).enqueue(callback);
        callback.await(request.url()).assertFailure(InterruptedIOException.class);
        Assert.assertEquals(Arrays.asList("CallStart", "CallFailed"), listener.recordedEventTypes());
    }

    @Test
    public void executionRejectedAfterMaxRequestsChange() throws Exception {
        Request request1 = newRequest("http://a/1");
        Request request2 = newRequest("http://a/2");
        dispatcher.setMaxRequests(1);
        client.newCall(request1).enqueue(callback);
        executor.shutdown();
        client.newCall(request2).enqueue(callback);
        dispatcher.setMaxRequests(2);// Trigger promotion.

        callback.await(request2.url()).assertFailure(InterruptedIOException.class);
        Assert.assertEquals(Arrays.asList("CallStart", "CallStart", "CallFailed"), listener.recordedEventTypes());
    }

    @Test
    public void executionRejectedAfterMaxRequestsPerHostChange() throws Exception {
        Request request1 = newRequest("http://a/1");
        Request request2 = newRequest("http://a/2");
        dispatcher.setMaxRequestsPerHost(1);
        client.newCall(request1).enqueue(callback);
        executor.shutdown();
        client.newCall(request2).enqueue(callback);
        dispatcher.setMaxRequestsPerHost(2);// Trigger promotion.

        callback.await(request2.url()).assertFailure(InterruptedIOException.class);
        Assert.assertEquals(Arrays.asList("CallStart", "CallStart", "CallFailed"), listener.recordedEventTypes());
    }

    @Test
    public void executionRejectedAfterPrecedingCallFinishes() throws Exception {
        Request request1 = newRequest("http://a/1");
        Request request2 = newRequest("http://a/2");
        dispatcher.setMaxRequests(1);
        client.newCall(request1).enqueue(callback);
        executor.shutdown();
        client.newCall(request2).enqueue(callback);
        executor.finishJob("http://a/1");// Trigger promotion.

        callback.await(request2.url()).assertFailure(InterruptedIOException.class);
        Assert.assertEquals(Arrays.asList("CallStart", "CallStart", "CallFailed"), listener.recordedEventTypes());
    }

    class RecordingExecutor extends AbstractExecutorService {
        private boolean shutdown;

        private List<AsyncCall> calls = new ArrayList<>();

        @Override
        public void execute(Runnable command) {
            if (shutdown)
                throw new RejectedExecutionException();

            calls.add(((AsyncCall) (command)));
        }

        public void assertJobs(String... expectedUrls) {
            List<String> actualUrls = new ArrayList<>();
            for (AsyncCall call : calls) {
                actualUrls.add(call.request().url().toString());
            }
            Assert.assertEquals(Arrays.asList(expectedUrls), actualUrls);
        }

        public void finishJob(String url) {
            for (Iterator<AsyncCall> i = calls.iterator(); i.hasNext();) {
                AsyncCall call = i.next();
                if (call.request().url().toString().equals(url)) {
                    i.remove();
                    dispatcher.finished(call);
                    return;
                }
            }
            throw new AssertionError(("No such job: " + url));
        }

        @Override
        public void shutdown() {
            shutdown = true;
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTerminated() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }
}

