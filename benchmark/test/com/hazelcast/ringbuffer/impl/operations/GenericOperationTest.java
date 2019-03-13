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
package com.hazelcast.ringbuffer.impl.operations;


import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class GenericOperationTest extends HazelcastTestSupport {
    private static final int CAPACITY = 10;

    private NodeEngineImpl nodeEngine;

    private Ringbuffer<Object> ringbuffer;

    private RingbufferContainer ringbufferContainer;

    private SerializationService serializationService;

    private RingbufferService ringbufferService;

    @Test
    public void size() throws Exception {
        ringbuffer.add("a");
        ringbuffer.add("b");
        GenericOperation op = getGenericOperation(GenericOperation.OPERATION_SIZE);
        op.run();
        Long result = op.getResponse();
        Assert.assertEquals(new Long(ringbufferContainer.size()), result);
    }

    @Test
    public void capacity() throws Exception {
        ringbuffer.add("a");
        ringbuffer.add("b");
        GenericOperation op = getGenericOperation(GenericOperation.OPERATION_CAPACITY);
        op.run();
        Long result = op.getResponse();
        Assert.assertEquals(new Long(GenericOperationTest.CAPACITY), result);
    }

    @Test
    public void remainingCapacity() throws Exception {
        ringbuffer.add("a");
        ringbuffer.add("b");
        GenericOperation op = getGenericOperation(GenericOperation.OPERATION_REMAINING_CAPACITY);
        op.run();
        Long result = op.getResponse();
        Assert.assertEquals(new Long(((GenericOperationTest.CAPACITY) - 2)), result);
    }

    @Test
    public void tail() throws Exception {
        ringbuffer.add("a");
        ringbuffer.add("b");
        GenericOperation op = getGenericOperation(GenericOperation.OPERATION_TAIL);
        op.run();
        Long result = op.getResponse();
        Assert.assertEquals(new Long(ringbufferContainer.tailSequence()), result);
    }

    @Test
    public void head() throws Exception {
        for (int k = 0; k < ((GenericOperationTest.CAPACITY) * 2); k++) {
            ringbuffer.add("a");
        }
        GenericOperation op = getGenericOperation(GenericOperation.OPERATION_HEAD);
        op.run();
        Long result = op.getResponse();
        Assert.assertEquals(new Long(ringbufferContainer.headSequence()), result);
    }
}

