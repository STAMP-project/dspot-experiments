/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.clientside.ClientTestUtil;
import com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl;
import com.hazelcast.client.spi.ClientInvocationService;
import com.hazelcast.client.spi.impl.ClientInvocation;
import com.hazelcast.client.spi.impl.SmartClientInvocationService;
import com.hazelcast.client.spi.properties.ClientProperty;
import com.hazelcast.client.test.bounce.MultiSocketClientDriverFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.util.ThreadLocalRandomProvider;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.test.bounce.BounceMemberRule;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(SlowTest.class)
public class ClientBackpressureBouncingTest extends HazelcastTestSupport {
    private static final long TEST_DURATION_SECONDS = 240;// 4 minutes


    private static final long TEST_TIMEOUT_MILLIS = (10 * 60) * 1000;// 10 minutes


    private static final int MAX_CONCURRENT_INVOCATION_CONFIG = 100;

    private static final int WORKER_THREAD_COUNT = 5;

    @Rule
    public BounceMemberRule bounceMemberRule;

    private ClientBackpressureBouncingTest.InvocationCheckingThread checkingThread;

    private long backoff;

    public ClientBackpressureBouncingTest(int backoffTimeoutMillis) {
        this.backoff = backoffTimeoutMillis;
        this.bounceMemberRule = BounceMemberRule.with(new Config()).driverFactory(new MultiSocketClientDriverFactory(new ClientConfig().setProperty(ClientProperty.MAX_CONCURRENT_INVOCATIONS.getName(), String.valueOf(ClientBackpressureBouncingTest.MAX_CONCURRENT_INVOCATION_CONFIG)).setProperty(ClientProperty.BACKPRESSURE_BACKOFF_TIMEOUT_MILLIS.getName(), String.valueOf(backoff)))).build();
    }

    @Test(timeout = ClientBackpressureBouncingTest.TEST_TIMEOUT_MILLIS)
    public void testInFlightInvocationCountIsNotGrowing() {
        HazelcastInstance driver = bounceMemberRule.getNextTestDriver();
        IMap<Integer, Integer> map = driver.getMap(randomMapName());
        Runnable[] tasks = createTasks(map);
        checkingThread = new ClientBackpressureBouncingTest.InvocationCheckingThread(driver);
        checkingThread.start();
        bounceMemberRule.testRepeatedly(tasks, ClientBackpressureBouncingTest.TEST_DURATION_SECONDS);
        System.out.println("Finished bouncing");
        checkingThread.shutdown();
        checkingThread.assertInFlightInvocationsWereNotGrowing();
    }

    private static class InvocationCheckingThread extends Thread {
        private final long warmUpDeadline;

        private final long deadLine;

        private final ConcurrentMap<Long, ClientInvocation> invocations;

        private int maxInvocationCountObserved;

        private int maxInvocationCountObservedDuringWarmup;

        private volatile boolean running = true;

        private InvocationCheckingThread(HazelcastInstance client) {
            long durationMillis = TimeUnit.SECONDS.toMillis(ClientBackpressureBouncingTest.TEST_DURATION_SECONDS);
            long now = System.currentTimeMillis();
            this.warmUpDeadline = now + (durationMillis / 5);
            this.deadLine = now + durationMillis;
            this.invocations = extractInvocations(client);
        }

        @Override
        public void run() {
            while (((System.currentTimeMillis()) < (deadLine)) && (running)) {
                int currentSize = invocations.size();
                maxInvocationCountObserved = Math.max(currentSize, maxInvocationCountObserved);
                if ((System.currentTimeMillis()) < (warmUpDeadline)) {
                    maxInvocationCountObservedDuringWarmup = Math.max(currentSize, maxInvocationCountObservedDuringWarmup);
                }
                sleepAtLeastMillis(100);
            } 
        }

        private void shutdown() {
            running = false;
            interrupt();
            assertJoinable(this);
        }

        private void assertInFlightInvocationsWereNotGrowing() {
            Assert.assertTrue("There are no invocations to be observed!", ((maxInvocationCountObserved) > 0));
            long maximumTolerableInvocationCount = ((long) ((maxInvocationCountObservedDuringWarmup) * 2));
            Assert.assertTrue((((("Apparently number of in-flight invocations is growing." + " Max. number of in-flight invocation during first fifth of test duration: ") + (maxInvocationCountObservedDuringWarmup)) + " Max. number of in-flight invocation in total: ") + (maxInvocationCountObserved)), ((maxInvocationCountObserved) <= maximumTolerableInvocationCount));
        }

        @SuppressWarnings("unchecked")
        private ConcurrentMap<Long, ClientInvocation> extractInvocations(HazelcastInstance client) {
            try {
                HazelcastClientInstanceImpl clientImpl = ClientTestUtil.getHazelcastClientInstanceImpl(client);
                ClientInvocationService invocationService = clientImpl.getInvocationService();
                SmartClientInvocationService smartInvocationService = ((SmartClientInvocationService) (invocationService));
                Field invocationsField = SmartClientInvocationService.class.getSuperclass().getDeclaredField("invocations");
                invocationsField.setAccessible(true);
                return ((ConcurrentMap<Long, ClientInvocation>) (invocationsField.get(smartInvocationService)));
            } catch (Exception e) {
                throw rethrow(e);
            }
        }
    }

    private class MyRunnable implements Runnable {
        private final ExecutionCallback<Integer> callback = new ClientBackpressureBouncingTest.MyRunnable.CountingCallback();

        private final AtomicLong backpressureCounter = new AtomicLong();

        private final AtomicLong progressCounter = new AtomicLong();

        private final AtomicLong failureCounter = new AtomicLong();

        private final IMap<Integer, Integer> map;

        private final int workerNo;

        MyRunnable(IMap<Integer, Integer> map, int workerNo) {
            this.map = map;
            this.workerNo = workerNo;
        }

        @Override
        public void run() {
            try {
                int key = ThreadLocalRandomProvider.get().nextInt();
                map.getAsync(key).andThen(callback);
            } catch (HazelcastOverloadException e) {
                if ((backoff) != (-1)) {
                    Assert.fail(String.format("HazelcastOverloadException should not be thrown when backoff is configured (%d ms), but got: %s", backoff, e));
                }
                long current = backpressureCounter.incrementAndGet();
                if ((current % 250000) == 0) {
                    System.out.println(((("Worker no. " + (workerNo)) + " backpressured. counter: ") + current));
                }
            }
        }

        private class CountingCallback implements ExecutionCallback<Integer> {
            @Override
            public void onResponse(Integer response) {
                long position = progressCounter.incrementAndGet();
                if ((position % 50000) == 0) {
                    System.out.println(((("Worker no. " + (workerNo)) + " at ") + position));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                long position = failureCounter.incrementAndGet();
                if ((position % 100) == 0) {
                    System.out.println(((("Failure Worker no. " + (workerNo)) + " at ") + position));
                }
            }
        }
    }
}

