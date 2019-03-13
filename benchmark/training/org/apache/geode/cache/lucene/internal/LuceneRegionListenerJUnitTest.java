/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.lucene.internal;


import EvictionAlgorithm.NONE;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.lucene.LuceneSerializer;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.InternalRegionArguments;
import org.apache.geode.test.fake.Fakes;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneRegionListenerJUnitTest {
    @Test
    public void beforeDataRegionCreatedShouldHaveSerializer() {
        String name = "indexName";
        String regionPath = "regionName";
        String[] fields = new String[]{ "field1", "field2" };
        String aeqId = LuceneServiceImpl.getUniqueIndexName(name, regionPath);
        InternalCache cache = Fakes.cache();
        final Region region = Fakes.region(regionPath, cache);
        RegionAttributes attributes = region.getAttributes();
        DataPolicy policy = attributes.getDataPolicy();
        Mockito.when(policy.withPartitioning()).thenReturn(true);
        EvictionAttributes evictionAttributes = Mockito.mock(EvictionAttributes.class);
        Mockito.when(attributes.getEvictionAttributes()).thenReturn(evictionAttributes);
        CopyOnWriteArraySet set = new CopyOnWriteArraySet();
        set.add(aeqId);
        Mockito.when(attributes.getAsyncEventQueueIds()).thenReturn(set);
        Mockito.when(evictionAttributes.getAlgorithm()).thenReturn(NONE);
        LuceneServiceImpl service = Mockito.mock(LuceneServiceImpl.class);
        Analyzer analyzer = Mockito.mock(Analyzer.class);
        LuceneSerializer serializer = Mockito.mock(LuceneSerializer.class);
        InternalRegionArguments internalRegionArgs = Mockito.mock(InternalRegionArguments.class);
        Mockito.when(internalRegionArgs.addCacheServiceProfile(ArgumentMatchers.any())).thenReturn(internalRegionArgs);
        LuceneRegionListener listener = new LuceneRegionListener(service, cache, name, ("/" + regionPath), fields, analyzer, null, serializer);
        listener.beforeCreate(null, regionPath, attributes, internalRegionArgs);
        Mockito.verify(service).beforeDataRegionCreated(ArgumentMatchers.eq(name), ArgumentMatchers.eq(("/" + regionPath)), ArgumentMatchers.eq(attributes), ArgumentMatchers.eq(analyzer), ArgumentMatchers.any(), ArgumentMatchers.eq(aeqId), ArgumentMatchers.eq(serializer), ArgumentMatchers.any());
    }
}

