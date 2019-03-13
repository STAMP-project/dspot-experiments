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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReadOneOperationTest extends HazelcastTestSupport {
    private HazelcastInstance hz;

    private NodeEngineImpl nodeEngine;

    private SerializationService serializationService;

    private Ringbuffer<Object> ringbuffer;

    private RingbufferContainer ringbufferContainer;

    private RingbufferService ringbufferService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenAtTail() throws Exception {
        ringbuffer.add("tail");
        ReadOneOperation op = getReadOneOperation(ringbuffer.tailSequence());
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertFalse(shouldWait);
        op.run();
        Assert.assertEquals(toData("tail"), op.getResponse());
    }

    @Test
    public void whenOneAfterTail() throws Exception {
        ringbuffer.add("tail");
        ReadOneOperation op = getReadOneOperation(((ringbuffer.tailSequence()) + 1));
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertTrue(shouldWait);
    }

    @Test
    public void whenTooFarAfterTail() throws Exception {
        ringbuffer.add("tail");
        ReadOneOperation op = getReadOneOperation(((ringbuffer.tailSequence()) + 2));
        // since there is an item, we don't need to wait
        op.shouldWait();
        expectedException.expect(IllegalArgumentException.class);
        op.beforeRun();
    }

    @Test
    public void whenOneAfterTailAndBufferEmpty() throws Exception {
        ReadOneOperation op = getReadOneOperation(((ringbuffer.tailSequence()) + 1));
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertTrue(shouldWait);
    }

    @Test
    public void whenOnTailAndBufferEmpty() throws Exception {
        ReadOneOperation op = getReadOneOperation(ringbuffer.tailSequence());
        // since there is an item, we don't need to wait
        op.shouldWait();
        expectedException.expect(StaleSequenceException.class);
        op.beforeRun();
    }

    @Test
    public void whenBeforeTail() throws Exception {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ReadOneOperation op = getReadOneOperation(((ringbuffer.tailSequence()) - 1));
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertFalse(shouldWait);
        op.run();
        Assert.assertEquals(toData("item2"), op.getResponse());
    }

    @Test
    public void whenAtHead() throws Exception {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ReadOneOperation op = getReadOneOperation(ringbuffer.headSequence());
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertFalse(shouldWait);
        op.run();
        Assert.assertEquals(toData("item1"), op.getResponse());
    }

    @Test
    public void whenBeforeHead() throws Exception {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        long oldhead = ringbuffer.headSequence();
        ringbufferContainer.setHeadSequence(ringbufferContainer.tailSequence());
        ReadOneOperation op = getReadOneOperation(oldhead);
        op.shouldWait();
        expectedException.expect(StaleSequenceException.class);
        op.beforeRun();
    }
}

