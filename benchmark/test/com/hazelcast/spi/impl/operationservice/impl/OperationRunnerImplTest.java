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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OperationRunnerImplTest extends HazelcastTestSupport {
    private HazelcastInstance local;

    private HazelcastInstance remote;

    private OperationRunnerImpl operationRunner;

    private OperationServiceImpl operationService;

    private ClusterService clusterService;

    private OperationResponseHandler responseHandler;

    @Test
    public void runTask() {
        final AtomicLong counter = new AtomicLong();
        operationRunner.run(new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        });
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void runOperation() {
        final AtomicLong counter = new AtomicLong();
        final Object response = "someresponse";
        Operation op = new Operation() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }

            @Override
            public Object getResponse() {
                return response;
            }
        };
        op.setPartitionId(operationRunner.getPartitionId());
        op.setOperationResponseHandler(responseHandler);
        operationRunner.run(op);
        Assert.assertEquals(1, counter.get());
        Mockito.verify(responseHandler).sendResponse(op, response);
    }

    @Test
    public void runOperation_whenGeneric() {
        final AtomicLong counter = new AtomicLong();
        final Object response = "someresponse";
        Operation op = new Operation() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }

            @Override
            public Object getResponse() {
                return response;
            }
        };
        op.setPartitionId((-1));
        op.setOperationResponseHandler(responseHandler);
        operationRunner.run(op);
        Assert.assertEquals(1, counter.get());
        Mockito.verify(responseHandler).sendResponse(op, response);
    }

    @Test
    public void runOperation_whenWrongPartition_thenTaskNotExecuted() {
        final AtomicLong counter = new AtomicLong();
        Operation op = new Operation() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        };
        op.setPartitionId(((operationRunner.getPartitionId()) + 1));
        op.setOperationResponseHandler(responseHandler);
        operationRunner.run(op);
        Assert.assertEquals(0, counter.get());
        Mockito.verify(responseHandler).sendResponse(ArgumentMatchers.same(op), ArgumentMatchers.any(IllegalStateException.class));
    }

    @Test
    public void runOperation_whenRunThrowsException() {
        Operation op = new Operation() {
            @Override
            public void run() throws Exception {
                throw new ExpectedRuntimeException();
            }
        };
        op.setOperationResponseHandler(responseHandler);
        op.setPartitionId(operationRunner.getPartitionId());
        operationRunner.run(op);
        Mockito.verify(responseHandler).sendResponse(ArgumentMatchers.same(op), ArgumentMatchers.any(ExpectedRuntimeException.class));
    }

    @Test
    public void runOperation_whenWaitingNeeded() {
        final AtomicLong counter = new AtomicLong();
        OperationRunnerImplTest.DummyWaitingOperation op = new OperationRunnerImplTest.DummyWaitingOperation() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        };
        op.setPartitionId(operationRunner.getPartitionId());
        operationRunner.run(op);
        Assert.assertEquals(0, counter.get());
        // verify that the response handler was not called
        Mockito.verify(responseHandler, Mockito.never()).sendResponse(ArgumentMatchers.same(op), ArgumentMatchers.any());
    }

    @Test
    public void runOperation_whenTimeout_thenOperationNotExecuted() {
        final AtomicLong counter = new AtomicLong();
        Operation op = new Operation() {
            @Override
            public void run() throws Exception {
                counter.incrementAndGet();
            }
        };
        OperationAccessor.setCallId(op, 10);
        OperationAccessor.setCallTimeout(op, ((clusterService.getClusterClock().getClusterTime()) - 1));
        op.setPartitionId(operationRunner.getPartitionId());
        op.setOperationResponseHandler(responseHandler);
        operationRunner.run(op);
        Assert.assertEquals(0, counter.get());
        Mockito.verify(responseHandler).sendResponse(ArgumentMatchers.same(op), ArgumentMatchers.any(CallTimeoutResponse.class));
    }

    @Test
    public void runPacket() throws Exception {
        Operation op = new DummyOperation();
        OperationAccessor.setCallId(op, (1000 * 1000));
        Packet packet = HazelcastTestSupport.toPacket(local, remote, op);
        operationRunner.run(packet);
    }

    @Test(expected = HazelcastSerializationException.class)
    public void runPacket_whenBroken() throws Exception {
        Operation op = new DummyOperation();
        OperationAccessor.setCallId(op, (1000 * 1000));
        Packet packet = HazelcastTestSupport.toPacket(local, remote, op);
        byte[] bytes = packet.toByteArray();
        for (int k = 0; k < (bytes.length); k++) {
            (bytes[k])++;
        }
        operationRunner.run(packet);
    }

    public abstract class DummyWaitingOperation extends Operation implements BlockingOperation {
        WaitNotifyKey waitNotifyKey = new WaitNotifyKey() {
            @Override
            public String getServiceName() {
                return "someservice";
            }

            @Override
            public String getObjectName() {
                return "someobject";
            }
        };

        @Override
        public WaitNotifyKey getWaitKey() {
            return waitNotifyKey;
        }

        @Override
        public boolean shouldWait() {
            return true;
        }

        @Override
        public void onWaitExpire() {
        }
    }
}

