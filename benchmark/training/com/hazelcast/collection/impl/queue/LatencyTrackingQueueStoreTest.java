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
package com.hazelcast.collection.impl.queue;


import com.google.common.primitives.Longs;
import com.hazelcast.core.QueueStore;
import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LatencyTrackingQueueStoreTest extends HazelcastTestSupport {
    private static final String NAME = "someQueue";

    private StoreLatencyPlugin plugin;

    private QueueStore<String> delegate;

    private LatencyTrackingQueueStore<String> queueStore;

    @Test
    public void load() {
        Long key = 1L;
        String value = "someValue";
        Mockito.when(delegate.load(key)).thenReturn(value);
        String result = queueStore.load(key);
        Assert.assertEquals(result, value);
        assertProbeCalledOnce("load");
    }

    @Test
    public void loadAll() {
        Collection<Long> keys = Arrays.asList(1L, 2L);
        Map<Long, String> values = new HashMap<Long, String>();
        values.put(1L, "value1");
        values.put(2L, "value2");
        Mockito.when(delegate.loadAll(keys)).thenReturn(values);
        Map<Long, String> result = queueStore.loadAll(keys);
        Assert.assertEquals(values, result);
        assertProbeCalledOnce("loadAll");
    }

    @Test
    public void loadAllKeys() {
        Set<Long> keys = TestCollectionUtils.setOf(1L, 2L);
        Mockito.when(delegate.loadAllKeys()).thenReturn(keys);
        Set<Long> result = queueStore.loadAllKeys();
        Assert.assertEquals(keys, result);
        assertProbeCalledOnce("loadAllKeys");
    }

    @Test
    public void delete() {
        Long key = 1L;
        queueStore.delete(key);
        Mockito.verify(delegate).delete(key);
        assertProbeCalledOnce("delete");
    }

    @Test
    public void deleteAll() {
        Collection<Long> keys = Longs.asList(1L, 2L);
        queueStore.deleteAll(keys);
        Mockito.verify(delegate).deleteAll(keys);
        assertProbeCalledOnce("deleteAll");
    }

    @Test
    public void store() {
        Long key = 1L;
        String value = "value1";
        queueStore.store(key, value);
        Mockito.verify(delegate).store(key, value);
        assertProbeCalledOnce("store");
    }

    @Test
    public void storeAll() {
        Map<Long, String> values = new HashMap<Long, String>();
        values.put(1L, "value1");
        values.put(2L, "value2");
        queueStore.storeAll(values);
        Mockito.verify(delegate).storeAll(values);
        assertProbeCalledOnce("storeAll");
    }
}

