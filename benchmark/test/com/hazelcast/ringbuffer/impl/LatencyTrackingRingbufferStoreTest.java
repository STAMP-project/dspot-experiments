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
package com.hazelcast.ringbuffer.impl;


import com.hazelcast.core.RingbufferStore;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LatencyTrackingRingbufferStoreTest extends HazelcastTestSupport {
    private static final String NAME = "LatencyTrackingRingbufferStoreTest";

    private static final ObjectNamespace NAMESPACE = RingbufferService.getRingbufferNamespace(LatencyTrackingRingbufferStoreTest.NAME);

    private StoreLatencyPlugin plugin;

    private RingbufferStore<String> delegate;

    private LatencyTrackingRingbufferStore<String> ringbufferStore;

    @Test
    public void load() {
        long sequence = 1L;
        String value = "someValue";
        Mockito.when(delegate.load(sequence)).thenReturn(value);
        String result = ringbufferStore.load(sequence);
        Assert.assertEquals(result, value);
        assertProbeCalledOnce("load");
    }

    @Test
    public void getLargestSequence() {
        long largestSequence = 100L;
        Mockito.when(delegate.getLargestSequence()).thenReturn(largestSequence);
        long result = ringbufferStore.getLargestSequence();
        Assert.assertEquals(largestSequence, result);
        assertProbeCalledOnce("getLargestSequence");
    }

    @Test
    public void store() {
        long sequence = 1L;
        String value = "value1";
        ringbufferStore.store(sequence, value);
        Mockito.verify(delegate).store(sequence, value);
        assertProbeCalledOnce("store");
    }

    @Test
    public void storeAll() {
        String[] items = new String[]{ "1", "2" };
        ringbufferStore.storeAll(100, items);
        Mockito.verify(delegate).storeAll(100, items);
        assertProbeCalledOnce("storeAll");
    }
}

