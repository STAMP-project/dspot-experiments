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
package com.hazelcast.cache.impl;


import com.hazelcast.internal.diagnostics.StoreLatencyPlugin;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.cache.integration.CacheLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LatencyTrackingCacheLoaderTest extends HazelcastTestSupport {
    private static final String NAME = "someCache";

    private StoreLatencyPlugin plugin;

    private CacheLoader<String, String> delegate;

    private LatencyTrackingCacheLoader<String, String> cacheLoader;

    @Test
    public void load() {
        String key = "key";
        String value = "value";
        Mockito.when(delegate.load(key)).thenReturn(value);
        String result = cacheLoader.load(key);
        Assert.assertSame(value, result);
        assertProbeCalledOnce("load");
    }

    @Test
    public void loadAll() {
        Collection<String> keys = Arrays.asList("key1", "key2");
        Map<String, String> values = new HashMap<String, String>();
        values.put("key1", "value1");
        values.put("key2", "value2");
        Mockito.when(delegate.loadAll(keys)).thenReturn(values);
        Map<String, String> result = cacheLoader.loadAll(keys);
        Assert.assertSame(values, result);
        assertProbeCalledOnce("loadAll");
    }
}

