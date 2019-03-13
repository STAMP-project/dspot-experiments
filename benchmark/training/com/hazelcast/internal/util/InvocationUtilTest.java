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
package com.hazelcast.internal.util;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvocationUtilTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(InvocationUtil.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeLocallyWithRetryFailsWhenOperationHandlerIsSet() {
        final Operation op = new Operation() {};
        op.setOperationResponseHandler(new OperationResponseHandler() {
            @Override
            public void sendResponse(Operation op, Object response) {
            }
        });
        InvocationUtil.executeLocallyWithRetry(null, op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeLocallyWithRetryFailsWhenOperationDoesNotReturnResponse() {
        final Operation op = new Operation() {
            @Override
            public boolean returnsResponse() {
                return false;
            }
        };
        InvocationUtil.executeLocallyWithRetry(null, op);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeLocallyWithRetryFailsWhenOperationValidatesTarget() {
        final Operation op = new Operation() {
            @Override
            public boolean validatesTarget() {
                return true;
            }
        };
        InvocationUtil.executeLocallyWithRetry(null, op);
    }

    @Test
    public void executeLocallyRetriesWhenPartitionIsMigrating() throws InterruptedException {
        final HazelcastInstance instance = createHazelcastInstance(HazelcastTestSupport.smallInstanceConfig());
        final NodeEngineImpl nodeEngineImpl = HazelcastTestSupport.getNodeEngineImpl(instance);
        final InternalPartitionService partitionService = nodeEngineImpl.getPartitionService();
        final int randomPartitionId = ((int) ((Math.random()) * (partitionService.getPartitionCount())));
        final InternalPartitionImpl partition = ((InternalPartitionImpl) (partitionService.getPartition(randomPartitionId)));
        partition.setMigrating(true);
        final String operationResponse = "operationResponse";
        final Operation operation = setPartitionId(randomPartitionId);
        final LocalRetryableExecution execution = InvocationUtil.executeLocallyWithRetry(nodeEngineImpl, operation);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                }
                partition.setMigrating(false);
            }
        });
        Assert.assertTrue(execution.awaitCompletion(1, TimeUnit.MINUTES));
        Assert.assertEquals(operationResponse, execution.getResponse());
    }

    public class LocalOperation extends AbstractLocalOperation {
        private final Object operationResponse;

        private Object response;

        public LocalOperation(Object operationResponse) {
            this.operationResponse = operationResponse;
        }

        @Override
        public void run() throws Exception {
            response = operationResponse;
        }

        @Override
        public Object getResponse() {
            return response;
        }

        @Override
        public boolean validatesTarget() {
            return false;
        }
    }
}

