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
package com.hazelcast.client.util;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.ClientTestSupport;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.FutureUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientStateListenerTest extends ClientTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testWithUnusedClientConfig() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        Assert.assertFalse(listener.awaitConnected(1, TimeUnit.MILLISECONDS));
        Assert.assertFalse(listener.awaitDisconnected(1, TimeUnit.MILLISECONDS));
        Assert.assertFalse(listener.isConnected());
        Assert.assertFalse(listener.isShutdown());
        Assert.assertFalse(listener.isStarted());
        Assert.assertEquals(LifecycleState.STARTING, listener.getCurrentState());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientASYNCStartConnected() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        clientConfig.getConnectionStrategyConfig().setAsyncStart(true);
        newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig);
        Assert.assertTrue(listener.awaitConnected());
        Assert.assertFalse(listener.awaitDisconnected(1, TimeUnit.MILLISECONDS));
        Assert.assertTrue(listener.isConnected());
        Assert.assertFalse(listener.isShutdown());
        Assert.assertTrue(listener.isStarted());
        Assert.assertEquals(LifecycleState.CLIENT_CONNECTED, listener.getCurrentState());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientReconnectModeAsyncConnected() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        clientConfig.getConnectionStrategyConfig().setReconnectMode(ReconnectMode.ASYNC);
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        HazelcastInstance hazelcastInstance = hazelcastFactory.newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig);
        hazelcastInstance.shutdown();
        Assert.assertTrue(listener.awaitDisconnected());
        Assert.assertFalse(listener.isConnected());
        Assert.assertFalse(listener.isShutdown());
        Assert.assertTrue(listener.isStarted());
        Assert.assertEquals(LifecycleState.CLIENT_DISCONNECTED, listener.getCurrentState());
        newHazelcastInstance();
        Assert.assertTrue(listener.awaitConnected());
        Assert.assertTrue(listener.isConnected());
        Assert.assertFalse(listener.isShutdown());
        Assert.assertTrue(listener.isStarted());
        Assert.assertEquals(LifecycleState.CLIENT_CONNECTED, listener.getCurrentState());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientReconnectModeAsyncConnectedMultipleThreads() throws InterruptedException {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ClientConfig clientConfig = new ClientConfig();
        final ClientStateListener listener = new ClientStateListener(clientConfig);
        clientConfig.getConnectionStrategyConfig().setReconnectMode(ReconnectMode.ASYNC);
        clientConfig.getNetworkConfig().setConnectionAttemptLimit(Integer.MAX_VALUE);
        HazelcastInstance hazelcastInstance = hazelcastFactory.newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig);
        hazelcastInstance.shutdown();
        List<Future> futures = new ArrayList<Future>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Assert.assertTrue(listener.awaitDisconnected());
                    } catch (InterruptedException e) {
                        Assert.fail("Should not be interrupted");
                    }
                    Assert.assertFalse(listener.isConnected());
                    Assert.assertFalse(listener.isShutdown());
                    Assert.assertTrue(listener.isStarted());
                    Assert.assertEquals(LifecycleState.CLIENT_DISCONNECTED, listener.getCurrentState());
                }
            }));
        }
        FutureUtil.waitForever(futures);
        Assert.assertTrue(FutureUtil.allDone(futures));
        newHazelcastInstance();
        futures.clear();
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener.awaitConnected();
                    } catch (InterruptedException e) {
                        Assert.fail("Should not be interrupted");
                    }
                    Assert.assertTrue(listener.isConnected());
                    Assert.assertFalse(listener.isShutdown());
                    Assert.assertTrue(listener.isStarted());
                    Assert.assertEquals(LifecycleState.CLIENT_CONNECTED, listener.getCurrentState());
                }
            }));
        }
        FutureUtil.waitForever(futures);
        Assert.assertTrue(FutureUtil.allDone(futures));
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientReconnectModeOffDisconnected() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        final ClientStateListener listener = new ClientStateListener(clientConfig);
        clientConfig.getConnectionStrategyConfig().setReconnectMode(ReconnectMode.OFF);
        HazelcastInstance hazelcastInstance = hazelcastFactory.newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig);
        hazelcastInstance.shutdown();
        newHazelcastInstance();
        Assert.assertTrue(listener.awaitDisconnected());
        Assert.assertFalse(listener.isConnected());
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(LifecycleState.SHUTDOWN, listener.getCurrentState());
            }
        });
        Assert.assertFalse(listener.isStarted());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientConnectedWithTimeout() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig);
        Assert.assertTrue(listener.awaitConnected());
        Assert.assertFalse(listener.awaitDisconnected(1, TimeUnit.MILLISECONDS));
        Assert.assertTrue(listener.isConnected());
        Assert.assertFalse(listener.isShutdown());
        Assert.assertTrue(listener.isStarted());
        Assert.assertEquals(LifecycleState.CLIENT_CONNECTED, listener.getCurrentState());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientConnectedWithoutTimeout() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig);
        listener.awaitConnected();
        Assert.assertEquals(LifecycleState.CLIENT_CONNECTED, listener.getCurrentState());
        Assert.assertTrue(listener.isConnected());
        Assert.assertTrue(listener.isStarted());
        Assert.assertFalse(listener.awaitDisconnected(1, TimeUnit.MILLISECONDS));
        Assert.assertFalse(listener.isShutdown());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientDisconnectedWithTimeout() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig).shutdown();
        Assert.assertFalse(listener.awaitConnected(1, TimeUnit.MILLISECONDS));
        Assert.assertTrue(listener.awaitDisconnected(1, TimeUnit.MILLISECONDS));
        Assert.assertFalse(listener.isConnected());
        Assert.assertTrue(listener.isShutdown());
        Assert.assertFalse(listener.isStarted());
        Assert.assertEquals(LifecycleState.SHUTDOWN, listener.getCurrentState());
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void testClientDisconnectedWithoutTimeout() throws InterruptedException {
        ClientConfig clientConfig = new ClientConfig();
        ClientStateListener listener = new ClientStateListener(clientConfig);
        newHazelcastInstance();
        hazelcastFactory.newHazelcastClient(clientConfig).shutdown();
        listener.awaitDisconnected();
        Assert.assertFalse(listener.awaitConnected(1, TimeUnit.MILLISECONDS));
        Assert.assertFalse(listener.isConnected());
        Assert.assertTrue(listener.isShutdown());
        Assert.assertFalse(listener.isStarted());
        Assert.assertEquals(LifecycleState.SHUTDOWN, listener.getCurrentState());
    }
}

