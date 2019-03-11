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


import DataPolicy.PERSISTENT_PARTITION;
import IndexRepositoryFactory.APACHE_GEODE_INDEX_COMPLETE;
import RegionShortcut.PARTITION;
import RegionShortcut.PARTITION_PERSISTENT;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.asyncqueue.internal.AsyncEventQueueFactoryImpl;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.lucene.LuceneSerializer;
import org.apache.geode.cache.lucene.internal.directory.DumpDirectoryFiles;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.test.fake.Fakes;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneIndexForPartitionedRegionTest {
    @Rule
    public ExpectedException expectedExceptions = ExpectedException.none();

    @Test
    public void getIndexNameReturnsCorrectName() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        Assert.assertEquals(name, index.getName());
    }

    @Test
    public void getRegionPathReturnsPath() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        Assert.assertEquals(regionPath, index.getRegionPath());
    }

    @Test
    public void fileRegionExistsWhenFileRegionExistsShouldReturnTrue() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        PartitionedRegion region = Mockito.mock(PartitionedRegion.class);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        String fileRegionName = index.createFileRegionName();
        Mockito.when(cache.getRegion(fileRegionName)).thenReturn(region);
        Assert.assertTrue(index.fileRegionExists(fileRegionName));
    }

    @Test
    public void indexIsAvailableReturnsTrueIfCompleteFileIsPresent() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        PartitionedRegion region = Mockito.mock(PartitionedRegion.class);
        PartitionedRegion mockFileRegion = Mockito.mock(PartitionedRegion.class);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        String fileRegionName = index.createFileRegionName();
        Mockito.when(cache.getRegion(fileRegionName)).thenReturn(region);
        LuceneIndexForPartitionedRegion spy = Mockito.spy(index);
        Mockito.when(spy.getFileAndChunkRegion()).thenReturn(mockFileRegion);
        Mockito.when(mockFileRegion.get(APACHE_GEODE_INDEX_COMPLETE, 1)).thenReturn("SOMETHING IS PRESENT");
        Assert.assertTrue(spy.isIndexAvailable(1));
    }

    @Test
    public void fileRegionExistsWhenFileRegionDoesNotExistShouldReturnFalse() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        String fileRegionName = index.createFileRegionName();
        Mockito.when(cache.getRegion(fileRegionName)).thenReturn(null);
        Assert.assertFalse(index.fileRegionExists(fileRegionName));
    }

    @Test
    public void createAEQWithPersistenceCallsCreateOnAEQFactory() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        final Region region = Fakes.region(regionPath, cache);
        RegionAttributes attributes = region.getAttributes();
        Mockito.when(attributes.getDataPolicy()).thenReturn(PERSISTENT_PARTITION);
        AsyncEventQueueFactoryImpl aeqFactory = Mockito.mock(AsyncEventQueueFactoryImpl.class);
        Mockito.when(cache.createAsyncEventQueueFactory()).thenReturn(aeqFactory);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        index.createAEQ(region);
        Mockito.verify(aeqFactory).setPersistent(ArgumentMatchers.eq(true));
        Mockito.verify(aeqFactory).create(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void createRepositoryManagerWithNotNullSerializer() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        LuceneSerializer serializer = Mockito.mock(LuceneSerializer.class);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        index = Mockito.spy(index);
        index.setupRepositoryManager(serializer);
        Mockito.verify(index).createRepositoryManager(ArgumentMatchers.eq(serializer));
    }

    @Test
    public void createRepositoryManagerWithNullSerializer() {
        String name = "indexName";
        String regionPath = "regionName";
        String[] fields = new String[]{ "field1", "field2" };
        InternalCache cache = Fakes.cache();
        ArgumentCaptor<LuceneSerializer> serializerCaptor = ArgumentCaptor.forClass(LuceneSerializer.class);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        index = Mockito.spy(index);
        Mockito.when(index.getFieldNames()).thenReturn(fields);
        index.setupRepositoryManager(null);
        Mockito.verify(index).createRepositoryManager(serializerCaptor.capture());
        LuceneSerializer serializer = serializerCaptor.getValue();
        Assert.assertNull(serializer);
    }

    @Test
    public void createAEQCallsCreateOnAEQFactory() {
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        final Region region = Fakes.region(regionPath, cache);
        AsyncEventQueueFactoryImpl aeqFactory = Mockito.mock(AsyncEventQueueFactoryImpl.class);
        Mockito.when(cache.createAsyncEventQueueFactory()).thenReturn(aeqFactory);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        index.createAEQ(region);
        Mockito.verify(aeqFactory, Mockito.never()).setPersistent(ArgumentMatchers.eq(true));
        Mockito.verify(aeqFactory).create(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void initializeWithNoLocalMemoryShouldSucceed() {
        boolean withPersistence = false;
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        Region region = initializeScenario(withPersistence, regionPath, cache, 0);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        LuceneIndexForPartitionedRegion spy = setupSpy(region, index, "aeq");
        spy.initialize();
    }

    @Test
    public void initializeWithoutPersistenceShouldCreateAEQ() {
        boolean withPersistence = false;
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        Region region = initializeScenario(withPersistence, regionPath, cache);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        LuceneIndexForPartitionedRegion spy = setupSpy(region, index, "aeq");
        Mockito.verify(spy).createAEQ(ArgumentMatchers.eq(region.getAttributes()), ArgumentMatchers.eq("aeq"));
    }

    @Test
    public void initializeShouldCreatePartitionFileRegion() {
        boolean withPersistence = false;
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        Region region = initializeScenario(withPersistence, regionPath, cache);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        LuceneIndexForPartitionedRegion spy = setupSpy(region, index, "aeq");
        Mockito.verify(spy).createRegion(ArgumentMatchers.eq(index.createFileRegionName()), ArgumentMatchers.eq(PARTITION), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void createFileRegionWithPartitionShortcutCreatesRegionUsingCreateVMRegion() throws Exception {
        String name = "indexName";
        String regionPath = "regionName";
        GemFireCacheImpl cache = Fakes.cache();
        RegionAttributes regionAttributes = Mockito.mock(RegionAttributes.class);
        Mockito.when(regionAttributes.getDataPolicy()).thenReturn(DataPolicy.PARTITION);
        PartitionAttributes partitionAttributes = initializeAttributes(cache);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        LuceneIndexForPartitionedRegion indexSpy = Mockito.spy(index);
        indexSpy.createRegion(index.createFileRegionName(), PARTITION, regionPath, partitionAttributes, regionAttributes, null);
        String fileRegionName = index.createFileRegionName();
        Mockito.verify(indexSpy).createRegion(fileRegionName, PARTITION, regionPath, partitionAttributes, regionAttributes, null);
        Mockito.verify(cache).createVMRegion(ArgumentMatchers.eq(fileRegionName), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void initializeShouldCreatePartitionPersistentFileRegion() {
        boolean withPersistence = true;
        String name = "indexName";
        String regionPath = "regionName";
        InternalCache cache = Fakes.cache();
        initializeScenario(withPersistence, regionPath, cache);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        index.setSearchableFields(new String[]{ "field" });
        LuceneIndexForPartitionedRegion spy = Mockito.spy(index);
        Mockito.doReturn(null).when(spy).createRegion(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(null).when(spy).createAEQ(((RegionAttributes) (ArgumentMatchers.any())), ArgumentMatchers.any());
        spy.setupRepositoryManager(null);
        spy.createAEQ(ArgumentMatchers.any(), ArgumentMatchers.any());
        spy.initialize();
        Mockito.verify(spy).createRegion(ArgumentMatchers.eq(index.createFileRegionName()), ArgumentMatchers.eq(PARTITION_PERSISTENT), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void dumpFilesShouldInvokeDumpFunction() {
        boolean withPersistence = false;
        String name = "indexName";
        String regionPath = "regionName";
        String[] fields = new String[]{ "field1", "field2" };
        InternalCache cache = Fakes.cache();
        initializeScenario(withPersistence, regionPath, cache);
        AsyncEventQueue aeq = Mockito.mock(AsyncEventQueue.class);
        DumpDirectoryFiles function = new DumpDirectoryFiles();
        FunctionService.registerFunction(function);
        LuceneIndexForPartitionedRegion index = new LuceneIndexForPartitionedRegion(name, regionPath, cache);
        index = Mockito.spy(index);
        Mockito.when(index.getFieldNames()).thenReturn(fields);
        Mockito.doReturn(aeq).when(index).createAEQ(ArgumentMatchers.any(), ArgumentMatchers.any());
        index.setupRepositoryManager(null);
        index.createAEQ(cache.getRegionAttributes(regionPath), aeq.getId());
        index.initialize();
        PartitionedRegion region = ((PartitionedRegion) (cache.getRegion(regionPath)));
        ResultCollector collector = Mockito.mock(ResultCollector.class);
        Mockito.when(region.executeFunction(ArgumentMatchers.eq(function), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(collector);
        index.dumpFiles("directory");
        Mockito.verify(region).executeFunction(ArgumentMatchers.eq(function), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }
}

