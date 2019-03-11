/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rpc.akka;


import akka.actor.ActorSystem;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link AkkaRpcService}.
 */
public class AkkaRpcServiceTest extends TestLogger {
    private static final Time TIMEOUT = Time.milliseconds(10000L);

    // ------------------------------------------------------------------------
    // shared test members
    // ------------------------------------------------------------------------
    private static ActorSystem actorSystem;

    private static AkkaRpcService akkaRpcService;

    // ------------------------------------------------------------------------
    // tests
    // ------------------------------------------------------------------------
    @Test
    public void testScheduleRunnable() throws Exception {
        final OneShotLatch latch = new OneShotLatch();
        final long delay = 100L;
        final long start = System.nanoTime();
        ScheduledFuture<?> scheduledFuture = AkkaRpcServiceTest.akkaRpcService.scheduleRunnable(latch::trigger, delay, TimeUnit.MILLISECONDS);
        scheduledFuture.get();
        Assert.assertTrue(latch.isTriggered());
        final long stop = System.nanoTime();
        Assert.assertTrue("call was not properly delayed", (((stop - start) / 1000000) >= delay));
    }

    /**
     * Tests that the {@link AkkaRpcService} can execute runnables.
     */
    @Test
    public void testExecuteRunnable() throws Exception {
        final OneShotLatch latch = new OneShotLatch();
        AkkaRpcServiceTest.akkaRpcService.execute(latch::trigger);
        latch.await(30L, TimeUnit.SECONDS);
    }

    /**
     * Tests that the {@link AkkaRpcService} can execute callables and returns their result as
     * a {@link CompletableFuture}.
     */
    @Test
    public void testExecuteCallable() throws Exception {
        final OneShotLatch latch = new OneShotLatch();
        final int expected = 42;
        CompletableFuture<Integer> result = AkkaRpcServiceTest.akkaRpcService.execute(() -> {
            latch.trigger();
            return expected;
        });
        int actual = result.get(30L, TimeUnit.SECONDS);
        Assert.assertEquals(expected, actual);
        Assert.assertTrue(latch.isTriggered());
    }

    @Test
    public void testGetAddress() {
        Assert.assertEquals(AkkaUtils.getAddress(AkkaRpcServiceTest.actorSystem).host().get(), AkkaRpcServiceTest.akkaRpcService.getAddress());
    }

    @Test
    public void testGetPort() {
        Assert.assertEquals(AkkaUtils.getAddress(AkkaRpcServiceTest.actorSystem).port().get(), AkkaRpcServiceTest.akkaRpcService.getPort());
    }

    /**
     * Tests that we can wait for the termination of the rpc service.
     */
    @Test(timeout = 60000)
    public void testTerminationFuture() throws Exception {
        final AkkaRpcService rpcService = startAkkaRpcService();
        CompletableFuture<Void> terminationFuture = rpcService.getTerminationFuture();
        Assert.assertFalse(terminationFuture.isDone());
        rpcService.stopService();
        terminationFuture.get();
    }

    /**
     * Tests a simple scheduled runnable being executed by the RPC services scheduled executor
     * service.
     */
    @Test(timeout = 60000)
    public void testScheduledExecutorServiceSimpleSchedule() throws Exception {
        ScheduledExecutor scheduledExecutor = AkkaRpcServiceTest.akkaRpcService.getScheduledExecutor();
        final OneShotLatch latch = new OneShotLatch();
        ScheduledFuture<?> future = scheduledExecutor.schedule(latch::trigger, 10L, TimeUnit.MILLISECONDS);
        future.get();
        // once the future is completed, then the latch should have been triggered
        Assert.assertTrue(latch.isTriggered());
    }

    /**
     * Tests that the RPC service's scheduled executor service can execute runnables at a fixed
     * rate.
     */
    @Test(timeout = 60000)
    public void testScheduledExecutorServicePeriodicSchedule() throws Exception {
        ScheduledExecutor scheduledExecutor = AkkaRpcServiceTest.akkaRpcService.getScheduledExecutor();
        final int tries = 4;
        final long delay = 10L;
        final CountDownLatch countDownLatch = new CountDownLatch(tries);
        long currentTime = System.nanoTime();
        ScheduledFuture<?> future = scheduledExecutor.scheduleAtFixedRate(countDownLatch::countDown, delay, delay, TimeUnit.MILLISECONDS);
        Assert.assertTrue((!(future.isDone())));
        countDownLatch.await();
        // the future should not complete since we have a periodic task
        Assert.assertTrue((!(future.isDone())));
        long finalTime = (System.nanoTime()) - currentTime;
        // the processing should have taken at least delay times the number of count downs.
        Assert.assertTrue((finalTime >= (tries * delay)));
        future.cancel(true);
    }

    /**
     * Tests that the RPC service's scheduled executor service can execute runnable with a fixed
     * delay.
     */
    @Test(timeout = 60000)
    public void testScheduledExecutorServiceWithFixedDelaySchedule() throws Exception {
        ScheduledExecutor scheduledExecutor = AkkaRpcServiceTest.akkaRpcService.getScheduledExecutor();
        final int tries = 4;
        final long delay = 10L;
        final CountDownLatch countDownLatch = new CountDownLatch(tries);
        long currentTime = System.nanoTime();
        ScheduledFuture<?> future = scheduledExecutor.scheduleWithFixedDelay(countDownLatch::countDown, delay, delay, TimeUnit.MILLISECONDS);
        Assert.assertTrue((!(future.isDone())));
        countDownLatch.await();
        // the future should not complete since we have a periodic task
        Assert.assertTrue((!(future.isDone())));
        long finalTime = (System.nanoTime()) - currentTime;
        // the processing should have taken at least delay times the number of count downs.
        Assert.assertTrue((finalTime >= (tries * delay)));
        future.cancel(true);
    }

    /**
     * Tests that canceling the returned future will stop the execution of the scheduled runnable.
     */
    @Test
    public void testScheduledExecutorServiceCancelWithFixedDelay() throws InterruptedException {
        ScheduledExecutor scheduledExecutor = AkkaRpcServiceTest.akkaRpcService.getScheduledExecutor();
        long delay = 10L;
        final OneShotLatch futureTask = new OneShotLatch();
        final OneShotLatch latch = new OneShotLatch();
        final OneShotLatch shouldNotBeTriggeredLatch = new OneShotLatch();
        ScheduledFuture<?> future = scheduledExecutor.scheduleWithFixedDelay(() -> {
            try {
                if (futureTask.isTriggered()) {
                    shouldNotBeTriggeredLatch.trigger();
                } else {
                    // first run
                    futureTask.trigger();
                    latch.await();
                }
            } catch ( ignored) {
                // ignore
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
        // wait until we're in the runnable
        futureTask.await();
        // cancel the scheduled future
        future.cancel(false);
        latch.trigger();
        try {
            shouldNotBeTriggeredLatch.await((5 * delay), TimeUnit.MILLISECONDS);
            Assert.fail("The shouldNotBeTriggeredLatch should never be triggered.");
        } catch (TimeoutException e) {
            // expected
        }
    }

    /**
     * Tests that the {@link AkkaRpcService} terminates all its RpcEndpoints when shutting down.
     */
    @Test
    public void testAkkaRpcServiceShutDownWithRpcEndpoints() throws Exception {
        final AkkaRpcService akkaRpcService = startAkkaRpcService();
        try {
            final int numberActors = 5;
            CompletableFuture<Void> terminationFuture = akkaRpcService.getTerminationFuture();
            final Collection<CompletableFuture<Void>> onStopFutures = startStopNCountingAsynchronousOnStopEndpoints(akkaRpcService, numberActors);
            for (CompletableFuture<Void> onStopFuture : onStopFutures) {
                onStopFuture.complete(null);
            }
            terminationFuture.get();
            Assert.assertThat(akkaRpcService.getActorSystem().isTerminated(), Matchers.is(true));
        } finally {
            RpcUtils.terminateRpcService(akkaRpcService, AkkaRpcServiceTest.TIMEOUT);
        }
    }

    /**
     * Tests that {@link AkkaRpcService} terminates all its RpcEndpoints and also stops
     * the underlying {@link ActorSystem} if one of the RpcEndpoints fails while stopping.
     */
    @Test
    public void testAkkaRpcServiceShutDownWithFailingRpcEndpoints() throws Exception {
        final AkkaRpcService akkaRpcService = startAkkaRpcService();
        final int numberActors = 5;
        CompletableFuture<Void> terminationFuture = akkaRpcService.getTerminationFuture();
        final Collection<CompletableFuture<Void>> onStopFutures = startStopNCountingAsynchronousOnStopEndpoints(akkaRpcService, numberActors);
        Iterator<CompletableFuture<Void>> iterator = onStopFutures.iterator();
        for (int i = 0; i < (numberActors - 1); i++) {
            iterator.next().complete(null);
        }
        iterator.next().completeExceptionally(new AkkaRpcServiceTest.OnStopException("onStop exception occurred."));
        for (CompletableFuture<Void> onStopFuture : onStopFutures) {
            onStopFuture.complete(null);
        }
        try {
            terminationFuture.get();
            Assert.fail("Expected the termination future to complete exceptionally.");
        } catch (ExecutionException e) {
            Assert.assertThat(ExceptionUtils.findThrowable(e, AkkaRpcServiceTest.OnStopException.class).isPresent(), Matchers.is(true));
        }
        Assert.assertThat(akkaRpcService.getActorSystem().isTerminated(), Matchers.is(true));
    }

    private static class CountingAsynchronousOnStopEndpoint extends AkkaRpcActorTest.AsynchronousOnStopEndpoint {
        private final CountDownLatch countDownLatch;

        protected CountingAsynchronousOnStopEndpoint(RpcService rpcService, CompletableFuture<Void> onStopFuture, CountDownLatch countDownLatch) {
            super(rpcService, onStopFuture);
            this.countDownLatch = countDownLatch;
        }

        @Override
        public CompletableFuture<Void> onStop() {
            countDownLatch.countDown();
            return super.onStop();
        }
    }

    private static class OnStopException extends FlinkException {
        private static final long serialVersionUID = 7136609202083168954L;

        public OnStopException(String message) {
            super(message);
        }
    }
}

