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
package com.hazelcast.config;


import InMemoryFormat.OBJECT;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheConfigReadOnlyTest {
    @Test(expected = UnsupportedOperationException.class)
    public void getIndexConfigsOfReadOnlyQueryCacheConfigShouldReturnUnmodifiable() {
        QueryCacheConfig config = new QueryCacheConfig().addIndexConfig(new MapIndexConfig()).addIndexConfig(new MapIndexConfig());
        List<MapIndexConfig> indexConfigs = config.getAsReadOnly().getIndexConfigs();
        indexConfigs.add(new MapIndexConfig());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getEntryListenerConfigsOfReadOnlyQueryCacheConfigShouldReturnUnmodifiable() {
        QueryCacheConfig config = new QueryCacheConfig().addEntryListenerConfig(new EntryListenerConfig()).addEntryListenerConfig(new EntryListenerConfig());
        List<EntryListenerConfig> entryListenerConfigs = config.getAsReadOnly().getEntryListenerConfigs();
        entryListenerConfigs.add(new EntryListenerConfig());
    }

    @Test
    public void getEvictionConfigShouldReturnReadOnlyConfig() {
        Assert.assertTrue(((getReadOnlyConfig().getEvictionConfig()) instanceof EvictionConfigReadOnly));
    }

    @Test
    public void getPredicateConfigShouldReturnReadOnlyConfig() {
        Assert.assertTrue(((getReadOnlyConfig().getPredicateConfig()) instanceof PredicateConfigReadOnly));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setBatchSizeOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setBatchSize(5);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setBufferSizeOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setBufferSize(3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setDelaySecondsOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setDelaySeconds(3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setEntryListenerConfigsOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setEntryListenerConfigs(Collections.singletonList(new EntryListenerConfig()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setEvictionConfigOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setEvictionConfig(new EvictionConfig());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setIncludeValueOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setIncludeValue(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setIndexConfigsOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setIndexConfigs(Collections.singletonList(new MapIndexConfig()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setInMemoryFormatOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setInMemoryFormat(OBJECT);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setNameOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setName("myQueryCache");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setPredicateConfigOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setPredicateConfig(new PredicateConfig());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setPopulateOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setPopulate(true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setCoalesceOfReadOnlyMapStoreConfigShouldFail() {
        getReadOnlyConfig().setCoalesce(true);
    }
}

