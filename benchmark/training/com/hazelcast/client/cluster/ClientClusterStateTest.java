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
package com.hazelcast.client.cluster;


import ClientProperty.INVOCATION_TIMEOUT_SECONDS;
import ClusterState.ACTIVE;
import ClusterState.FROZEN;
import ClusterState.PASSIVE;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.exception.TargetDisconnectedException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientClusterStateTest {
    private TestHazelcastFactory factory;

    private HazelcastInstance[] instances;

    private HazelcastInstance instance;

    @Test
    public void testClient_canConnect_whenClusterState_frozen() {
        instance.getCluster().changeClusterState(FROZEN);
        factory.newHazelcastClient();
    }

    @Test
    public void testClient_canExecuteWriteOperations_whenClusterState_frozen() {
        HazelcastTestSupport.warmUpPartitions(instances);
        changeClusterStateEventually(instance, FROZEN);
        HazelcastInstance client = factory.newHazelcastClient();
        IMap<Object, Object> map = client.getMap(HazelcastTestSupport.randomMapName());
        map.put(1, 1);
    }

    @Test
    public void testClient_canExecuteReadOperations_whenClusterState_frozen() {
        HazelcastTestSupport.warmUpPartitions(instances);
        changeClusterStateEventually(instance, FROZEN);
        HazelcastInstance client = factory.newHazelcastClient();
        IMap<Object, Object> map = client.getMap(HazelcastTestSupport.randomMapName());
        map.get(1);
    }

    @Test
    public void testClient_canConnect_whenClusterState_passive() {
        instance.getCluster().changeClusterState(PASSIVE);
        factory.newHazelcastClient();
    }

    @Test(expected = OperationTimeoutException.class)
    public void testClient_canNotExecuteWriteOperations_whenClusterState_passive() {
        HazelcastTestSupport.warmUpPartitions(instances);
        ClientConfig clientConfig = new ClientConfig().setProperty(INVOCATION_TIMEOUT_SECONDS.getName(), "3");
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        IMap<Object, Object> map = client.getMap(HazelcastTestSupport.randomMapName());
        changeClusterStateEventually(instance, PASSIVE);
        map.put(1, 1);
    }

    @Test
    public void testClient_canExecuteReadOperations_whenClusterState_passive() {
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastInstance client = factory.newHazelcastClient();
        IMap<Object, Object> map = client.getMap(HazelcastTestSupport.randomMapName());
        changeClusterStateEventually(instance, PASSIVE);
        map.get(1);
    }

    @Test
    public void testClient_canConnect_whenClusterState_goesBackToActive_fromPassive() {
        instance.getCluster().changeClusterState(PASSIVE);
        instance.getCluster().changeClusterState(ACTIVE);
        factory.newHazelcastClient();
    }

    @Test
    public void testClient_canExecuteOperations_whenClusterState_goesBackToActive_fromPassive() {
        HazelcastTestSupport.warmUpPartitions(instances);
        changeClusterStateEventually(instance, PASSIVE);
        instance.getCluster().changeClusterState(ACTIVE);
        HazelcastInstance client = factory.newHazelcastClient();
        IMap<Object, Object> map = client.getMap(HazelcastTestSupport.randomMapName());
        map.put(1, 1);
    }

    @Test
    public void testClusterShutdownDuringMapPutAll() {
        HazelcastTestSupport.warmUpPartitions(instances);
        HazelcastTestSupport.waitAllForSafeState(instances);
        HazelcastInstance client = factory.newHazelcastClient();
        final IMap<Object, Object> map = client.getMap(HazelcastTestSupport.randomMapName());
        final HashMap values = new HashMap<Double, Double>();
        for (int i = 0; i < 1000; i++) {
            double value = Math.random();
            values.put(value, value);
        }
        final int numThreads = 10;
        final CountDownLatch threadsFinished = new CountDownLatch(numThreads);
        final CountDownLatch threadsStarted = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < numThreads; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ILogger logger = Logger.getLogger(getClass());
                    threadsStarted.countDown();
                    logger.info("putAll thread started");
                    while (true) {
                        try {
                            map.putAll(values);
                            Thread.sleep(100);
                        } catch (IllegalStateException e) {
                            logger.warning("Expected exception for Map putAll during cluster shutdown:", e);
                            break;
                        } catch (TargetDisconnectedException e) {
                            logger.warning("Expected exception for Map putAll during cluster shutdown:", e);
                            break;
                        } catch (InterruptedException e) {
                            // do nothing
                        }
                    } 
                    threadsFinished.countDown();
                    logger.info(("putAll thread finishing. Current finished thread count is:" + (numThreads - (threadsFinished.getCount()))));
                }
            });
        }
        try {
            Assert.assertTrue("All threads could not be started", threadsStarted.await(1, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            Assert.fail(((("All threads could not be started due to InterruptedException. Could not start " + (threadsStarted.getCount())) + " threads out of ") + numThreads));
        }
        instance.getCluster().shutdown();
        executor.shutdown();
        try {
            Assert.assertTrue("All threads could not be finished", threadsFinished.await(2, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            Assert.fail(((("All threads could not be finished due to InterruptedException. Could not finish " + (threadsFinished.getCount())) + " threads out of ") + numThreads));
        } finally {
            executor.shutdownNow();
        }
    }
}

