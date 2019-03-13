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


import ClientProperty.MAX_CONCURRENT_INVOCATIONS;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.client.util.ClientDelegatingFuture;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMaxAllowedInvocationTest extends ClientTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_withSyncOperation() {
        int MAX_ALLOWED = 10;
        newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(MAX_CONCURRENT_INVOCATIONS.getName(), String.valueOf(MAX_ALLOWED));
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        IMap map = client.getMap(randomString());
        IExecutorService executorService = client.getExecutorService(randomString());
        for (int i = 0; i < MAX_ALLOWED; i++) {
            executorService.submit(new ClientMaxAllowedInvocationTest.SleepyProcessor(Integer.MAX_VALUE));
        }
        map.get(2);
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_withAsyncOperation() {
        int MAX_ALLOWED = 10;
        newHazelcastInstance();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setProperty(MAX_CONCURRENT_INVOCATIONS.getName(), String.valueOf(MAX_ALLOWED));
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(clientConfig);
        IMap map = client.getMap(randomString());
        IExecutorService executorService = client.getExecutorService(randomString());
        for (int i = 0; i < MAX_ALLOWED; i++) {
            executorService.submit(new ClientMaxAllowedInvocationTest.SleepyProcessor(Integer.MAX_VALUE));
        }
        map.getAsync(1);
    }

    static class SleepyProcessor implements Serializable , Callable {
        private long millis;

        SleepyProcessor(long millis) {
            this.millis = millis;
        }

        @Override
        public Object call() throws Exception {
            ILogger logger = Logger.getLogger(getClass());
            try {
                logger.info((((("SleepyProcessor(" + (this)) + ") sleeping for ") + (millis)) + " milliseconds"));
                Thread.sleep(millis);
                logger.info((("SleepyProcessor(" + (this)) + ") woke up."));
            } catch (InterruptedException e) {
                // ignored
                logger.info((("SleepyProcessor(" + (this)) + ") is interrupted."));
            }
            return null;
        }
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_andThenInternal() throws InterruptedException, ExecutionException {
        testMaxAllowed(new ClientMaxAllowedInvocationTest.RegisterCallback() {
            @Override
            public void call(ClientDelegatingFuture future, ExecutionCallback callback) {
                future.andThen(callback);
            }
        });
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_andThen() throws InterruptedException, ExecutionException {
        testMaxAllowed(new ClientMaxAllowedInvocationTest.RegisterCallback() {
            @Override
            public void call(ClientDelegatingFuture future, ExecutionCallback callback) {
                future.andThen(callback);
            }
        });
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_andThenExecutor() throws InterruptedException, ExecutionException {
        testMaxAllowed(new ClientMaxAllowedInvocationTest.RegisterCallback() {
            @Override
            public void call(ClientDelegatingFuture future, ExecutionCallback callback) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                future.andThen(callback, executor);
            }
        });
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_withWaitingCallbacks_andThenInternal() throws InterruptedException, ExecutionException {
        testMaxAllowed_withWaitingCallbacks(new ClientMaxAllowedInvocationTest.RegisterCallback() {
            @Override
            public void call(ClientDelegatingFuture future, ExecutionCallback callback) {
                future.andThen(callback);
            }
        });
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_withWaitingCallbacks_a_andThen() throws InterruptedException, ExecutionException {
        testMaxAllowed_withWaitingCallbacks(new ClientMaxAllowedInvocationTest.RegisterCallback() {
            @Override
            public void call(ClientDelegatingFuture future, ExecutionCallback callback) {
                future.andThen(callback);
            }
        });
    }

    @Test(expected = HazelcastOverloadException.class)
    public void testMaxAllowed_withWaitingCallbacks_andThenExecutor() throws InterruptedException, ExecutionException {
        testMaxAllowed_withWaitingCallbacks(new ClientMaxAllowedInvocationTest.RegisterCallback() {
            @Override
            public void call(ClientDelegatingFuture future, ExecutionCallback callback) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                future.andThen(callback, executor);
            }
        });
    }

    interface RegisterCallback {
        void call(ClientDelegatingFuture clientDelegatingFuture, ExecutionCallback countDownLatch);
    }

    static class SleepyCallback implements ExecutionCallback<ClientMessage> {
        final ILogger logger = Logger.getLogger(getClass());

        final CountDownLatch countDownLatch;

        public SleepyCallback(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onResponse(ClientMessage response) {
            try {
                logger.info("SleepyCallback onResponse entered. Will await for latch.");
                countDownLatch.await();
                logger.info("SleepyCallback onResponse latch wait finished.");
            } catch (InterruptedException e) {
                // ignored
                logger.info("SleepyCallback onResponse is interrupted.");
            }
        }

        @Override
        public void onFailure(Throwable t) {
            logger.info("SleepyCallback onFailure is entered.");
        }
    }
}

