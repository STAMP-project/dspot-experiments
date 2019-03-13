/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
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


import DataPolicy.PARTITION;
import EvictionAlgorithm.NONE;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.geode.Statistics;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.asyncqueue.internal.AsyncEventQueueFactoryImpl;
import org.apache.geode.cache.lucene.LuceneIndexFactory;
import org.apache.geode.cache.lucene.LuceneSerializer;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.PartitionedRegionDataStore;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneServiceImplJUnitTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    PartitionedRegion region;

    GemFireCacheImpl cache;

    LuceneServiceImpl service = new LuceneServiceImpl();

    @Test
    public void shouldPassSerializer() {
        service = Mockito.spy(service);
        LuceneIndexFactory factory = service.createIndexFactory();
        LuceneSerializer serializer = Mockito.mock(LuceneSerializer.class);
        factory.setLuceneSerializer(serializer);
        factory.setFields("field1", "field2");
        factory.create("index", "region");
        Mockito.verify(service).createIndex(ArgumentMatchers.eq("index"), ArgumentMatchers.eq("region"), ArgumentMatchers.any(), ArgumentMatchers.eq(serializer), ArgumentMatchers.eq(false));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfFieldsAreMissing() {
        thrown.expect(IllegalArgumentException.class);
        service.createIndexFactory().create("index", "region");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfFieldsMapIsMissing() {
        thrown.expect(IllegalArgumentException.class);
        service.createIndex("index", "region", Collections.emptyMap(), null, false);
    }

    @Test
    public void shouldReturnFalseIfRegionNotFoundInWaitUntilFlush() throws InterruptedException {
        boolean result = service.waitUntilFlushed("dummyIndex", "dummyRegion", 60000, TimeUnit.MILLISECONDS);
        Assert.assertFalse(result);
    }

    @Test
    public void userRegionShouldNotBeSetBeforeIndexInitialized() throws Exception {
        LuceneServiceImplJUnitTest.TestLuceneServiceImpl testService = new LuceneServiceImplJUnitTest.TestLuceneServiceImpl();
        Field f = LuceneServiceImpl.class.getDeclaredField("cache");
        f.setAccessible(true);
        f.set(testService, cache);
        AsyncEventQueueFactoryImpl aeqFactory = Mockito.mock(AsyncEventQueueFactoryImpl.class);
        Mockito.when(cache.createAsyncEventQueueFactory()).thenReturn(aeqFactory);
        DistributedSystem ds = Mockito.mock(DistributedSystem.class);
        Statistics luceneIndexStats = Mockito.mock(Statistics.class);
        Mockito.when(cache.getDistributedSystem()).thenReturn(ds);
        Mockito.when(createAtomicStatistics(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(luceneIndexStats);
        Mockito.when(cache.getRegion(ArgumentMatchers.anyString())).thenReturn(region);
        Mockito.when(cache.getDistributionManager()).thenReturn(Mockito.mock(DistributionManager.class));
        Mockito.when(cache.getDistributionManager().getWaitingThreadPool()).thenReturn(Executors.newSingleThreadExecutor());
        RegionAttributes ratts = Mockito.mock(RegionAttributes.class);
        Mockito.when(region.getAttributes()).thenReturn(ratts);
        Mockito.when(ratts.getDataPolicy()).thenReturn(PARTITION);
        EvictionAttributes evictionAttrs = Mockito.mock(EvictionAttributes.class);
        Mockito.when(ratts.getEvictionAttributes()).thenReturn(evictionAttrs);
        Mockito.when(evictionAttrs.getAlgorithm()).thenReturn(NONE);
        Map<String, Analyzer> fieldMap = new HashMap<String, Analyzer>();
        fieldMap.put("field1", null);
        fieldMap.put("field2", null);
        testService.createIndex("index", "region", fieldMap, null, true);
    }

    @Test
    public void createLuceneIndexOnExistingRegionShouldNotThrowNPEIfBucketMovedDuringReindexing() {
        LuceneIndexImpl index = Mockito.mock(LuceneIndexImpl.class);
        PartitionedRegionDataStore dataStore = Mockito.mock(PartitionedRegionDataStore.class);
        Mockito.when(region.getDataStore()).thenReturn(dataStore);
        Integer[] bucketIds = new Integer[]{ 1, 2, 3, 4, 5 };
        Set<Integer> primaryBucketIds = new HashSet(Arrays.asList(bucketIds));
        Mockito.when(dataStore.getAllLocalPrimaryBucketIds()).thenReturn(primaryBucketIds);
        Mockito.when(dataStore.getLocalBucketById(3)).thenReturn(null);
        boolean result = service.createLuceneIndexOnDataRegion(region, index);
        Assert.assertTrue(result);
    }

    private class TestLuceneServiceImpl extends LuceneServiceImpl {
        @Override
        public void afterDataRegionCreated(InternalLuceneIndex index) {
            PartitionedRegion userRegion = ((PartitionedRegion) (index.getCache().getRegion(index.getRegionPath())));
            Mockito.verify(userRegion, Mockito.never()).addAsyncEventQueueId(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        }

        @Override
        protected void validateLuceneIndexProfile(PartitionedRegion region) {
        }

        @Override
        protected void validateAllMembersAreTheSameVersion(PartitionedRegion region) {
        }
    }
}

