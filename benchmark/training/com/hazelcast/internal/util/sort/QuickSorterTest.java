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
package com.hazelcast.internal.util.sort;


import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryManager;
import com.hazelcast.nio.Bits;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QuickSorterTest {
    private static final int ARRAY_LENGTH = 10000;

    private MemoryManager memMgr;

    @Test
    public void testQuickSortInt() {
        final int[] array = QuickSorterTest.intArrayWithRandomElements();
        final long baseAddr = memMgr.getAllocator().allocate(((QuickSorterTest.ARRAY_LENGTH) * (Bits.INT_SIZE_IN_BYTES)));
        final MemoryAccessor mem = memMgr.getAccessor();
        for (int i = 0; i < (QuickSorterTest.ARRAY_LENGTH); i++) {
            mem.putInt((baseAddr + ((Bits.INT_SIZE_IN_BYTES) * i)), array[i]);
        }
        Arrays.sort(array);
        sort(0, QuickSorterTest.ARRAY_LENGTH);
        for (int i = 0; i < (QuickSorterTest.ARRAY_LENGTH); i++) {
            Assert.assertEquals(("Mismatch at " + i), array[i], mem.getInt((baseAddr + ((Bits.INT_SIZE_IN_BYTES) * i))));
        }
    }

    @Test
    public void testQuickSortLong() {
        final long[] array = QuickSorterTest.longArrayWithRandomElements();
        final long baseAddr = memMgr.getAllocator().allocate(((QuickSorterTest.ARRAY_LENGTH) * (Bits.LONG_SIZE_IN_BYTES)));
        final MemoryAccessor mem = memMgr.getAccessor();
        for (int i = 0; i < (QuickSorterTest.ARRAY_LENGTH); i++) {
            mem.putLong((baseAddr + ((Bits.LONG_SIZE_IN_BYTES) * i)), array[i]);
        }
        Arrays.sort(array);
        sort(0, QuickSorterTest.ARRAY_LENGTH);
        for (int i = 0; i < (QuickSorterTest.ARRAY_LENGTH); i++) {
            Assert.assertEquals(("Mismatch at " + i), array[i], mem.getLong((baseAddr + ((Bits.LONG_SIZE_IN_BYTES) * i))));
        }
    }
}

