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
package com.hazelcast.map.impl.event;


import EntryEventType.ADDED;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test EntryEventDataCache implementations
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EntryEventDataCacheTest {
    private EntryEventDataCache instance;

    private static final Address ADDRESS;

    static {
        Address address = null;
        try {
            address = new Address("127.0.0.1", 5701);
        } catch (UnknownHostException e) {
        }
        ADDRESS = address;
    }

    @Parameterized.Parameter
    public FilteringStrategy filteringStrategy;

    @Test
    public void getOrCreateEventDataIncludingValues_whenAlreadyCached() throws Exception {
        // when: creating EntryEventData including values with same arguments
        EntryEventData eed = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), true);
        EntryEventData shouldBeCached = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), true);
        // then: returned instances are the same
        Assert.assertSame(eed, shouldBeCached);
    }

    @Test
    public void getOrCreateEventDataExcludingValues_whenAlreadyCached() throws Exception {
        // when: creating EntryEventData including values with same arguments
        EntryEventData eed = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), false);
        EntryEventData shouldBeCached = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), false);
        // then: returned instances are the same
        Assert.assertSame(eed, shouldBeCached);
    }

    @Test
    public void isEmpty_whenNoEntryEventDataHaveBeenCreated() throws Exception {
        // when: no EntryEventData have been getOrCreate'd
        // then: the cache is empty
        Assert.assertTrue(instance.isEmpty());
    }

    @Test
    public void isEmpty_whenEntryEventDataHaveBeenCreated() throws Exception {
        // when: EntryEventData have been getOrCreate'd
        instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), false);
        // then: the cache is not empty
        Assert.assertFalse(instance.isEmpty());
    }

    @Test
    public void eventDataIncludingValues_whenValueIsCached() throws Exception {
        // when: EntryEventData including values have been created
        EntryEventData eed = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), true);
        // then: the cache is not empty & eventDataIncludingValues returns the cached entry
        Assert.assertFalse(instance.isEmpty());
        Assert.assertSame(eed, instance.eventDataIncludingValues().iterator().next());
    }

    @Test
    public void eventDataIncludingValues_whenNoValuesCached() throws Exception {
        // when: EntryEventData including values have been created
        // then: the cache is empty & eventDataIncludingValues returns null or empty collection
        Assert.assertTrue(instance.isEmpty());
        Assert.assertTrue((((instance.eventDataIncludingValues()) == null) || (instance.eventDataIncludingValues().isEmpty())));
    }

    @Test
    public void eventDataIncludingValues_whenDataExcludingValuesAreCached() throws Exception {
        // when: EntryEventData excluding values have been created
        EntryEventData eed = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), false);
        // then: eventDataIncludingValues returns null or empty collection
        Assert.assertTrue((((instance.eventDataIncludingValues()) == null) || (instance.eventDataIncludingValues().isEmpty())));
    }

    @Test
    public void eventDataExcludingValues_whenValueIsCached() throws Exception {
        // when: EntryEventData excluding values have been created
        EntryEventData eed = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), false);
        // then: the cache is not empty & eventDataExcludingValues returns the cached entry
        Assert.assertFalse(instance.isEmpty());
        Assert.assertSame(eed, instance.eventDataExcludingValues().iterator().next());
    }

    @Test
    public void eventDataExcludingValues_whenNoValuesCached() throws Exception {
        // when: no EntryEventData values have been created
        // then: the cache is empty & eventDataIncludingValues returns null or empty collection
        Assert.assertTrue(instance.isEmpty());
        Assert.assertTrue((((instance.eventDataIncludingValues()) == null) || (instance.eventDataIncludingValues().isEmpty())));
    }

    @Test
    public void eventDataExcludingValues_whenDataIncludingValuesAreCached() throws Exception {
        // when: no EntryEventData values have been created
        EntryEventData eed = instance.getOrCreateEventData("test", EntryEventDataCacheTest.ADDRESS, new HeapData(), new Object(), new Object(), new Object(), ADDED.getType(), true);
        // then: the cache is empty & eventDataIncludingValues returns null or empty collection
        Assert.assertTrue((((instance.eventDataExcludingValues()) == null) || (instance.eventDataExcludingValues().isEmpty())));
    }

    @Test
    public void filteringStrategy_rejects_invalidation_events() throws Exception {
        EventListenerFilter filter = EntryEventDataCacheTest.createInvalidationEventRejectingFilter();
        int matched = filteringStrategy.doFilter(filter, null, null, null, EntryEventType.INVALIDATION, "mapName");
        Assert.assertEquals(FilteringStrategy.FILTER_DOES_NOT_MATCH, matched);
    }
}

