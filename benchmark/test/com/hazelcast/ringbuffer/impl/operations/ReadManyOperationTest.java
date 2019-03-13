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
import com.hazelcast.core.IFunction;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReadManyOperationTest extends HazelcastTestSupport {
    private HazelcastInstance hz;

    private NodeEngineImpl nodeEngine;

    private Ringbuffer<Object> ringbuffer;

    private RingbufferContainer ringbufferContainer;

    private SerializationService serializationService;

    private RingbufferService ringbufferService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void whenAtTail() throws Exception {
        ringbuffer.add("tail");
        ReadManyOperation<String> op = getReadManyOperation(ringbuffer.tailSequence(), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertFalse(shouldWait);
        op.run();
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals(Arrays.asList("tail"), response);
        Assert.assertEquals(1, response.readCount());
    }

    @Test
    public void whenOneAfterTail() throws Exception {
        ringbuffer.add("tail");
        ReadManyOperation op = getReadManyOperation(((ringbuffer.tailSequence()) + 1), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertTrue(shouldWait);
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals(0, response.readCount());
    }

    @Test
    public void whenTooFarAfterTail() throws Exception {
        ringbuffer.add("tail");
        ReadManyOperation op = getReadManyOperation(((ringbuffer.tailSequence()) + 2), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        Assert.assertFalse(op.shouldWait());
        expectedException.expect(IllegalArgumentException.class);
        op.beforeRun();
    }

    @Test
    public void whenOneAfterTailAndBufferEmpty() throws Exception {
        ReadManyOperation op = getReadManyOperation(((ringbuffer.tailSequence()) + 1), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertTrue(shouldWait);
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals(0, response.readCount());
        Assert.assertEquals(0, response.size());
    }

    @Test
    public void whenOnTailAndBufferEmpty() throws Exception {
        ReadManyOperation op = getReadManyOperation(ringbuffer.tailSequence(), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        Assert.assertFalse(op.shouldWait());
        expectedException.expect(StaleSequenceException.class);
        op.beforeRun();
    }

    @Test
    public void whenBeforeTail() throws Exception {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ReadManyOperation op = getReadManyOperation(((ringbuffer.tailSequence()) - 1), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertFalse(shouldWait);
        op.run();
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals(Arrays.asList("item2"), response);
        Assert.assertEquals(1, response.readCount());
        Assert.assertEquals(1, response.size());
    }

    @Test
    public void whenAtHead() throws Exception {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ReadManyOperation op = getReadManyOperation(ringbuffer.headSequence(), 1, 1, null);
        op.setNodeEngine(nodeEngine);
        // since there is an item, we don't need to wait
        boolean shouldWait = op.shouldWait();
        Assert.assertFalse(shouldWait);
        op.run();
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals(Arrays.asList("item1"), response);
        Assert.assertEquals(1, response.readCount());
        Assert.assertEquals(1, response.size());
    }

    @Test
    public void whenBeforeHead() throws Exception {
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        long oldhead = ringbuffer.headSequence();
        ringbufferContainer.setHeadSequence(ringbufferContainer.tailSequence());
        ReadManyOperation op = getReadManyOperation(oldhead, 1, 1, null);
        op.setNodeEngine(nodeEngine);
        Assert.assertFalse(op.shouldWait());
        expectedException.expect(StaleSequenceException.class);
        op.beforeRun();
    }

    @Test
    public void whenMinimumNumberOfItemsNotAvailable() throws Exception {
        long startSequence = (ringbuffer.tailSequence()) + 1;
        ReadManyOperation op = getReadManyOperation(startSequence, 3, 3, null);
        op.setNodeEngine(nodeEngine);
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals(startSequence, op.sequence);
        Assert.assertTrue(getReadResultSet(op).isEmpty());
        ringbuffer.add("item1");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 1), op.sequence);
        Assert.assertEquals(Arrays.asList("item1"), op.getResponse());
        ringbuffer.add("item2");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 2), op.sequence);
        Assert.assertEquals(Arrays.asList("item1", "item2"), op.getResponse());
        ringbuffer.add("item3");
        Assert.assertFalse(op.shouldWait());
        Assert.assertEquals((startSequence + 3), op.sequence);
        Assert.assertEquals(Arrays.asList("item1", "item2", "item3"), op.getResponse());
    }

    @Test
    public void whenBelowMinimumAvailable() throws Exception {
        long startSequence = (ringbuffer.tailSequence()) + 1;
        ReadManyOperation op = getReadManyOperation(startSequence, 3, 3, null);
        op.setNodeEngine(nodeEngine);
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 2), op.sequence);
        Assert.assertEquals(Arrays.asList("item1", "item2"), op.getResponse());
        ringbuffer.add("item3");
        Assert.assertFalse(op.shouldWait());
        Assert.assertEquals((startSequence + 3), op.sequence);
        Assert.assertEquals(Arrays.asList("item1", "item2", "item3"), op.getResponse());
    }

    @Test
    public void whenMinimumNumberOfItemsAvailable() throws Exception {
        long startSequence = (ringbuffer.tailSequence()) + 1;
        ReadManyOperation op = getReadManyOperation(startSequence, 3, 3, null);
        op.setNodeEngine(nodeEngine);
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        Assert.assertFalse(op.shouldWait());
        Assert.assertEquals((startSequence + 3), op.sequence);
        Assert.assertEquals(Arrays.asList("item1", "item2", "item3"), op.getResponse());
    }

    @Test
    public void whenEnoughItemsAvailable() throws Exception {
        long startSequence = (ringbuffer.tailSequence()) + 1;
        ReadManyOperation op = getReadManyOperation(startSequence, 1, 3, null);
        op.setNodeEngine(nodeEngine);
        ringbuffer.add("item1");
        ringbuffer.add("item2");
        ringbuffer.add("item3");
        ringbuffer.add("item4");
        ringbuffer.add("item5");
        Assert.assertFalse(op.shouldWait());
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals((startSequence + 3), op.sequence);
        Assert.assertEquals(Arrays.asList("item1", "item2", "item3"), response);
        Assert.assertEquals(3, response.readCount());
    }

    @Test
    public void whenFilterProvidedAndNoItemsAvailable() throws Exception {
        long startSequence = (ringbuffer.tailSequence()) + 1;
        IFunction<String, Boolean> filter = new IFunction<String, Boolean>() {
            @Override
            public Boolean apply(String input) {
                return input.startsWith("good");
            }
        };
        ReadManyOperation op = getReadManyOperation(startSequence, 3, 3, filter);
        op.setNodeEngine(nodeEngine);
        Assert.assertTrue(op.shouldWait());
        ReadResultSetImpl response = getReadResultSet(op);
        Assert.assertEquals(startSequence, op.sequence);
        Assert.assertTrue(getReadResultSet(op).isEmpty());
        ringbuffer.add("bad1");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 1), op.sequence);
        Assert.assertEquals(1, response.readCount());
        Assert.assertEquals(0, response.size());
        ringbuffer.add("good1");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 2), op.sequence);
        Assert.assertEquals(Arrays.asList("good1"), response);
        Assert.assertEquals(2, response.readCount());
        ringbuffer.add("bad2");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 3), op.sequence);
        Assert.assertEquals(Arrays.asList("good1"), response);
        Assert.assertEquals(3, response.readCount());
        ringbuffer.add("good2");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 4), op.sequence);
        Assert.assertEquals(Arrays.asList("good1", "good2"), response);
        Assert.assertEquals(4, response.readCount());
        ringbuffer.add("bad3");
        Assert.assertTrue(op.shouldWait());
        Assert.assertEquals((startSequence + 5), op.sequence);
        Assert.assertEquals(Arrays.asList("good1", "good2"), response);
        Assert.assertEquals(5, response.readCount());
        ringbuffer.add("good3");
        Assert.assertFalse(op.shouldWait());
        Assert.assertEquals((startSequence + 6), op.sequence);
        Assert.assertEquals(Arrays.asList("good1", "good2", "good3"), response);
        Assert.assertEquals(6, response.readCount());
    }

    @Test
    public void whenFilterProvidedAndAllItemsAvailable() throws Exception {
        long startSequence = (ringbuffer.tailSequence()) + 1;
        IFunction<String, Boolean> filter = new IFunction<String, Boolean>() {
            @Override
            public Boolean apply(String input) {
                return input.startsWith("good");
            }
        };
        ReadManyOperation op = getReadManyOperation(startSequence, 3, 3, filter);
        op.setNodeEngine(nodeEngine);
        ringbuffer.add("bad1");
        ringbuffer.add("good1");
        ringbuffer.add("bad2");
        ringbuffer.add("good2");
        ringbuffer.add("bad3");
        ringbuffer.add("good3");
        Assert.assertFalse(op.shouldWait());
        Assert.assertEquals((startSequence + 6), op.sequence);
        Assert.assertEquals(Arrays.asList("good1", "good2", "good3"), op.getResponse());
    }
}

