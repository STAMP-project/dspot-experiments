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
package com.hazelcast.internal.util.hashslot.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static CapacityUtil.MAX_INT_CAPACITY;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CapacityUtilTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() throws Exception {
        HazelcastTestSupport.assertUtilityConstructor(CapacityUtil.class);
    }

    @Test
    public void testRoundCapacity() {
        int capacity = 2342;
        int roundedCapacity = CapacityUtil.roundCapacity(capacity);
        Assert.assertEquals(4096, roundedCapacity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRoundCapacity_shouldThrowIfMaximumCapacityIsExceeded() {
        CapacityUtil.roundCapacity(((MAX_INT_CAPACITY) + 1));
    }

    @Test
    public void testNextCapacity_withInt() {
        int capacity = 16;
        int nextCapacity = CapacityUtil.nextCapacity(capacity);
        Assert.assertEquals(32, nextCapacity);
    }

    @Test
    public void testNextCapacity_withInt_shouldIncreaseToHalfOfMinCapacity() {
        int capacity = 1;
        int nextCapacity = CapacityUtil.nextCapacity(capacity);
        Assert.assertEquals(4, nextCapacity);
    }

    @Test(expected = RuntimeException.class)
    public void testNextCapacity_withInt_shouldThrowIfMaxCapacityReached() {
        int capacity = Integer.highestOneBit(((Integer.MAX_VALUE) - 1));
        CapacityUtil.nextCapacity(capacity);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testNextCapacity_withInt_shouldThrowIfCapacityNoPowerOfTwo() {
        int capacity = 23;
        CapacityUtil.nextCapacity(capacity);
    }

    @Test
    public void testNextCapacity_withLong() {
        long capacity = 16;
        long nextCapacity = CapacityUtil.nextCapacity(capacity);
        Assert.assertEquals(32, nextCapacity);
    }

    @Test
    public void testNextCapacity_withLong_shouldIncreaseToHalfOfMinCapacity() {
        long capacity = 1;
        long nextCapacity = CapacityUtil.nextCapacity(capacity);
        Assert.assertEquals(4, nextCapacity);
    }

    @Test(expected = RuntimeException.class)
    public void testNextCapacity_withLong_shouldThrowIfMaxCapacityReached() {
        long capacity = Long.highestOneBit(((Long.MAX_VALUE) - 1));
        CapacityUtil.nextCapacity(capacity);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testNextCapacity_withLong_shouldThrowIfCapacityNoPowerOfTwo() {
        long capacity = 23;
        CapacityUtil.nextCapacity(capacity);
    }
}

