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
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.partition.NoDataMemberInClusterException;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationServiceImpl_invokeOnPartitionLiteMemberTest extends HazelcastTestSupport {
    private Config liteMemberConfig = new Config().setLiteMember(true);

    private Operation operation;

    @Test
    public void test_invokeOnPartition_onLiteMember() throws InterruptedException {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        final InternalCompletableFuture<Object> future = operationService.invokeOnPartition(null, operation, 0);
        try {
            future.get();
            Assert.fail("partition operation should not run on lite member!");
        } catch (ExecutionException e) {
            TestCase.assertTrue(((e.getCause()) instanceof NoDataMemberInClusterException));
        }
    }

    @Test
    public void test_invokeOnPartition_withDataMember() throws InterruptedException, ExecutionException {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance lite = factory.newHazelcastInstance(liteMemberConfig);
        factory.newHazelcastInstance();
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(lite);
        final InternalCompletableFuture<Object> future = operationService.invokeOnPartition(null, operation, 0);
        Assert.assertEquals("foobar", future.get());
    }

    @Test
    public void test_asyncInvokeOnPartition_onLiteMember() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        final OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyExecutionCallback callback = new OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyExecutionCallback();
        operationService.asyncInvokeOnPartition(null, operation, 0, callback);
        HazelcastTestSupport.assertOpenEventually(callback.responseLatch);
        TestCase.assertTrue(((callback.response) instanceof NoDataMemberInClusterException));
    }

    @Test
    public void test_asyncInvokeOnPartition_withDataMember() {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        factory.newHazelcastInstance();
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        final OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyExecutionCallback callback = new OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyExecutionCallback();
        operationService.asyncInvokeOnPartition(null, operation, 0, callback);
        HazelcastTestSupport.assertOpenEventually(callback.responseLatch);
        Assert.assertEquals("foobar", callback.response);
    }

    @Test(expected = NoDataMemberInClusterException.class)
    public void test_invokeOnPartitions_onLiteMember() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        operationService.invokeOnPartitions(InternalPartitionService.SERVICE_NAME, new OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyOperationFactory(), Collections.singletonList(0));
    }

    @Test
    public void test_invokeOnPartitions_withDataMember() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        factory.newHazelcastInstance();
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        final Map<Integer, Object> resultMap = operationService.invokeOnPartitions(InternalPartitionService.SERVICE_NAME, new OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyOperationFactory(), Collections.singletonList(0));
        Assert.assertEquals(1, resultMap.size());
        Assert.assertEquals("foobar", resultMap.get(0));
    }

    @Test(expected = NoDataMemberInClusterException.class)
    public void test_invokeOnAllPartitions_onLiteMember() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        operationService.invokeOnAllPartitions(InternalPartitionService.SERVICE_NAME, new OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyOperationFactory());
    }

    @Test
    public void test_invokeOnAllPartitions_withDataMember() throws Exception {
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance = factory.newHazelcastInstance(liteMemberConfig);
        factory.newHazelcastInstance();
        final InternalOperationService operationService = HazelcastTestSupport.getOperationService(instance);
        final Map<Integer, Object> resultMap = operationService.invokeOnAllPartitions(InternalPartitionService.SERVICE_NAME, new OperationServiceImpl_invokeOnPartitionLiteMemberTest.DummyOperationFactory());
        assertFalse(resultMap.isEmpty());
    }

    private static class DummyOperationFactory implements OperationFactory {
        @Override
        public Operation createOperation() {
            return new DummyOperation("foobar");
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
        }

        @Override
        public int getFactoryId() {
            return 0;
        }

        @Override
        public int getId() {
            return 0;
        }
    }

    static class DummyExecutionCallback implements ExecutionCallback<String> {
        private final CountDownLatch responseLatch = new CountDownLatch(1);

        private volatile Object response;

        @Override
        public void onResponse(String response) {
            setResponse(response);
        }

        @Override
        public void onFailure(Throwable t) {
            setResponse(t);
        }

        private void setResponse(Object response) {
            this.response = response;
            responseLatch.countDown();
        }

        public Object getResponse() {
            return response;
        }
    }
}

