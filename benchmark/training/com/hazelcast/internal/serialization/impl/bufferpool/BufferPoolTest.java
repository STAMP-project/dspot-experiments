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
package com.hazelcast.internal.serialization.impl.bufferpool;


import BufferPoolImpl.MAX_POOLED_ITEMS;
import Version.UNKNOWN;
import Versions.CURRENT_CLUSTER_VERSION;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static BufferPoolImpl.MAX_POOLED_ITEMS;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BufferPoolTest extends HazelcastTestSupport {
    private InternalSerializationService serializationService;

    private BufferPoolImpl bufferPool;

    // ======================= out ==========================================
    @Test
    public void takeOutputBuffer_whenPooledInstance() {
        BufferObjectDataOutput found1 = bufferPool.takeOutputBuffer();
        bufferPool.returnOutputBuffer(found1);
        BufferObjectDataOutput found2 = bufferPool.takeOutputBuffer();
        Assert.assertSame(found1, found2);
    }

    @Test
    public void takeOutputBuffer_whenNestedInstance() {
        BufferObjectDataOutput found1 = bufferPool.takeOutputBuffer();
        BufferObjectDataOutput found2 = bufferPool.takeOutputBuffer();
        Assert.assertNotSame(found1, found2);
    }

    @Test
    public void returnOutputBuffer_whenNull() {
        bufferPool.returnOutputBuffer(null);
        Assert.assertEquals(0, bufferPool.outputQueue.size());
    }

    @Test
    public void returnOutputBuffer() {
        BufferObjectDataOutput out = Mockito.mock(BufferObjectDataOutput.class);
        bufferPool.returnOutputBuffer(out);
        // lets see if the item was pushed on the queue
        Assert.assertEquals(1, bufferPool.outputQueue.size());
        // we need to make sure clear was called
        Mockito.verify(out, Mockito.times(1)).clear();
    }

    @Test
    public void returnOutputBuffer_whenOverflowing() throws IOException {
        for (int k = 0; k < (MAX_POOLED_ITEMS); k++) {
            bufferPool.returnOutputBuffer(Mockito.mock(BufferObjectDataOutput.class));
        }
        BufferObjectDataOutput out = Mockito.mock(BufferObjectDataOutput.class);
        bufferPool.returnOutputBuffer(out);
        Assert.assertEquals(MAX_POOLED_ITEMS, bufferPool.outputQueue.size());
        // we need to make sure that the out was closed since we are not going to pool it.
        Mockito.verify(out, Mockito.times(1)).close();
    }

    @Test
    public void takeOutputBuffer_whenPooledInstanceWithVersionSetIsReturned() {
        BufferObjectDataOutput found1 = bufferPool.takeOutputBuffer();
        Assert.assertEquals(UNKNOWN, found1.getVersion());
        found1.setVersion(CURRENT_CLUSTER_VERSION);
        bufferPool.returnOutputBuffer(found1);
        BufferObjectDataOutput found2 = bufferPool.takeOutputBuffer();
        Assert.assertEquals(UNKNOWN, found2.getVersion());
    }

    // ======================= in ==========================================
    @Test
    public void takeInputBuffer_whenPooledInstance() {
        Data data = new HeapData(new byte[]{  });
        BufferObjectDataInput found1 = bufferPool.takeInputBuffer(data);
        bufferPool.returnInputBuffer(found1);
        BufferObjectDataInput found2 = bufferPool.takeInputBuffer(data);
        Assert.assertSame(found1, found2);
    }

    @Test
    public void takeInputBuffer_whenNestedInstance() {
        Data data = new HeapData(new byte[]{  });
        BufferObjectDataInput found1 = bufferPool.takeInputBuffer(data);
        BufferObjectDataInput found2 = bufferPool.takeInputBuffer(data);
        Assert.assertNotSame(found1, found2);
    }

    @Test
    public void returnInputBuffer() {
        BufferObjectDataInput in = Mockito.mock(BufferObjectDataInput.class);
        bufferPool.returnInputBuffer(in);
        // lets see if the item was pushed on the queue
        Assert.assertEquals(1, bufferPool.inputQueue.size());
        // we need to make sure clear was called
        Mockito.verify(in, Mockito.times(1)).clear();
    }

    @Test
    public void returnInputBuffer_whenOverflowing() throws IOException {
        for (int k = 0; k < (MAX_POOLED_ITEMS); k++) {
            bufferPool.returnInputBuffer(Mockito.mock(BufferObjectDataInput.class));
        }
        BufferObjectDataInput in = Mockito.mock(BufferObjectDataInput.class);
        bufferPool.returnInputBuffer(in);
        Assert.assertEquals(MAX_POOLED_ITEMS, bufferPool.inputQueue.size());
        // we need to make sure that the in was closed since we are not going to pool it.
        Mockito.verify(in, Mockito.times(1)).close();
    }

    @Test
    public void returnInputBuffer_whenNull() {
        bufferPool.returnInputBuffer(null);
        Assert.assertEquals(0, bufferPool.inputQueue.size());
    }

    @Test
    public void takeInputBuffer_whenPooledInstanceWithVersionSetIsReturned() {
        Data data = new HeapData(new byte[]{  });
        BufferObjectDataInput found1 = bufferPool.takeInputBuffer(data);
        Assert.assertEquals(UNKNOWN, found1.getVersion());
        found1.setVersion(CURRENT_CLUSTER_VERSION);
        bufferPool.returnInputBuffer(found1);
        BufferObjectDataInput found2 = bufferPool.takeInputBuffer(data);
        Assert.assertEquals(UNKNOWN, found2.getVersion());
    }
}

