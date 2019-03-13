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
package com.hazelcast.cluster;


import ClusterState.ACTIVE;
import ClusterState.FROZEN;
import ClusterState.PASSIVE;
import GroupProperty.BACKPRESSURE_BACKOFF_TIMEOUT_MILLIS;
import GroupProperty.BACKPRESSURE_ENABLED;
import GroupProperty.BACKPRESSURE_MAX_CONCURRENT_INVOCATIONS_PER_PARTITION;
import GroupProperty.PARTITION_COUNT;
import NodeState.SHUT_DOWN;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.AbstractWaitNotifyKey;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterShutdownTest extends HazelcastTestSupport {
    @Test
    public void cluster_mustBeShutDown_by_singleMember_when_clusterState_ACTIVE() {
        testClusterShutdownWithSingleMember(ACTIVE);
    }

    @Test
    public void cluster_mustBeShutDown_by_singleMember_when_clusterState_FROZEN() {
        testClusterShutdownWithSingleMember(FROZEN);
    }

    @Test
    public void cluster_mustBeShutDown_by_singleMember_when_clusterState_PASSIVE() {
        testClusterShutdownWithSingleMember(PASSIVE);
    }

    @Test
    public void cluster_mustBeShutDown_by_multipleMembers_when_clusterState_PASSIVE() {
        testClusterShutdownWithMultipleMembers(6, 3);
    }

    @Test
    public void cluster_mustBeShutDown_by_allMembers_when_clusterState_PASSIVE() {
        testClusterShutdownWithMultipleMembers(6, 6);
    }

    @Test
    public void whenClusterIsAlreadyShutdown_thenLifecycleServiceShutdownShouldDoNothing() {
        HazelcastInstance instance = testClusterShutdownWithSingleMember(ACTIVE);
        instance.getLifecycleService().shutdown();
    }

    @Test
    public void clusterShutdown_shouldNotBeRejected_byBackpressure() throws Exception {
        Config config = new Config();
        config.setProperty(PARTITION_COUNT.toString(), "1");
        config.setProperty(BACKPRESSURE_ENABLED.toString(), "true");
        config.setProperty(BACKPRESSURE_BACKOFF_TIMEOUT_MILLIS.toString(), "100");
        config.setProperty(BACKPRESSURE_MAX_CONCURRENT_INVOCATIONS_PER_PARTITION.toString(), "3");
        HazelcastInstance hz = createHazelcastInstance(config);
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(hz);
        final Address address = HazelcastTestSupport.getAddress(hz);
        for (int i = 0; i < 10; i++) {
            Future<Object> future = HazelcastTestSupport.spawn(new Callable<Object>() {
                @Override
                public Object call() {
                    operationService.invokeOnTarget(null, new ClusterShutdownTest.AlwaysBlockingOperation(), address);
                    return null;
                }
            });
            try {
                future.get();
            } catch (ExecutionException e) {
                HazelcastTestSupport.assertInstanceOf(HazelcastOverloadException.class, e.getCause());
            }
        }
        Node node = HazelcastTestSupport.getNode(hz);
        hz.getCluster().shutdown();
        Assert.assertFalse(hz.getLifecycleService().isRunning());
        Assert.assertEquals(SHUT_DOWN, node.getState());
    }

    private static class AlwaysBlockingOperation extends Operation implements BlockingOperation {
        @Override
        public void run() throws Exception {
        }

        @Override
        public WaitNotifyKey getWaitKey() {
            return new AbstractWaitNotifyKey(getServiceName(), "test") {};
        }

        @Override
        public boolean shouldWait() {
            return true;
        }

        @Override
        public void onWaitExpire() {
            sendResponse(new TimeoutException());
        }

        @Override
        public String getServiceName() {
            return "AlwaysBlockingOperationService";
        }
    }
}

