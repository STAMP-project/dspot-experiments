/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.core.memory;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for the {@link HeapMemorySegment} in off-heap mode.
 */
@RunWith(Parameterized.class)
public class HeapMemorySegmentTest extends MemorySegmentTestBase {
    public HeapMemorySegmentTest(int pageSize) {
        super(pageSize);
    }

    @Test
    public void testHeapSegmentSpecifics() {
        final byte[] buffer = new byte[411];
        HeapMemorySegment seg = new HeapMemorySegment(buffer);
        Assert.assertFalse(seg.isFreed());
        Assert.assertFalse(seg.isOffHeap());
        Assert.assertEquals(buffer.length, seg.size());
        Assert.assertTrue((buffer == (seg.getArray())));
        ByteBuffer buf1 = seg.wrap(1, 2);
        ByteBuffer buf2 = seg.wrap(3, 4);
        Assert.assertTrue((buf1 != buf2));
        Assert.assertEquals(1, buf1.position());
        Assert.assertEquals(3, buf1.limit());
        Assert.assertEquals(3, buf2.position());
        Assert.assertEquals(7, buf2.limit());
    }
}

