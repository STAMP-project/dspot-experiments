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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ SlowTest.class, ParallelTest.class })
public class OperationServiceImpl_timeoutSlowTest extends HazelcastTestSupport {
    @Test
    public void testOperationTimeoutForLongRunningRemoteOperation() throws Exception {
        // this timeout makes the test slow. however we cannot shorten it too much
        // as otherwise it becomes to sensitive on environmental hiccups
        int callTimeoutMillis = 15000;
        Config config = new Config().setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), ("" + callTimeoutMillis));
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        // invoke on the "remote" member
        Address remoteAddress = HazelcastTestSupport.getNode(hz2).getThisAddress();
        OperationService operationService = HazelcastTestSupport.getNode(hz1).getNodeEngine().getOperationService();
        ICompletableFuture<Boolean> future = operationService.invokeOnTarget(null, new OperationServiceImpl_timeoutTest.SleepingOperation((callTimeoutMillis * 5)), remoteAddress);
        // wait more than operation timeout
        HazelcastTestSupport.sleepAtLeastMillis((callTimeoutMillis * 3));
        Assert.assertTrue(future.get());
    }
}

