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
package com.hazelcast.internal.nearcache;


import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("WeakerAccess")
public abstract class AbstractNearCacheLeakTest<NK, NV> extends HazelcastTestSupport {
    /**
     * The default count to be inserted into the Near Caches.
     */
    protected static final int DEFAULT_RECORD_COUNT = 1000;

    /**
     * The default name used for the data structures which have a Near Cache.
     */
    protected static final String DEFAULT_NEAR_CACHE_NAME = "defaultNearCache";

    /**
     * The {@link NearCacheConfig} used by the Near Cache tests.
     * <p>
     * Needs to be set by the implementations of this class in their {@link org.junit.Before} methods.
     */
    protected NearCacheConfig nearCacheConfig;

    @Test
    public void testNearCacheMemoryLeak() {
        // invalidations have to be enabled, otherwise no RepairHandler is registered
        Assert.assertTrue(nearCacheConfig.isInvalidateOnChange());
        NearCacheTestContext<Integer, Integer, NK, NV> context = createContext();
        populateDataAdapter(context, AbstractNearCacheLeakTest.DEFAULT_RECORD_COUNT);
        populateNearCache(context, AbstractNearCacheLeakTest.DEFAULT_RECORD_COUNT);
        NearCacheTestUtils.assertNearCacheSize(context, AbstractNearCacheLeakTest.DEFAULT_RECORD_COUNT);
        AbstractNearCacheLeakTest.assertNearCacheManager(context, 1);
        AbstractNearCacheLeakTest.assertRepairingTask(context, 1);
        context.nearCacheAdapter.destroy();
        NearCacheTestUtils.assertNearCacheSize(context, 0);
        AbstractNearCacheLeakTest.assertNearCacheManager(context, 0);
        AbstractNearCacheLeakTest.assertRepairingTask(context, 0);
    }
}

