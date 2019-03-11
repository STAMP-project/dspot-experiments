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
package org.apache.geode.cache.lucene.internal.cli.functions;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.internal.InternalLuceneService;
import org.apache.geode.cache.lucene.internal.LuceneIndexForPartitionedRegion;
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.cache.lucene.internal.cli.LuceneIndexStatus;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.PartitionedRegionDataStore;
import org.apache.geode.test.fake.Fakes;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneListIndexFunctionJUnitTest {
    @Test
    @SuppressWarnings("unchecked")
    public void executeListLuceneIndexWhenReindexingInProgress() {
        GemFireCacheImpl cache = Fakes.cache();
        final String serverName = "mockedServer";
        LuceneServiceImpl service = Mockito.mock(LuceneServiceImpl.class);
        Mockito.when(cache.getService(InternalLuceneService.class)).thenReturn(service);
        FunctionContext context = Mockito.mock(FunctionContext.class);
        ResultSender resultSender = Mockito.mock(ResultSender.class);
        Mockito.when(context.getResultSender()).thenReturn(resultSender);
        Mockito.when(context.getCache()).thenReturn(cache);
        LuceneIndexForPartitionedRegion index1 = getMockLuceneIndex("index1");
        PartitionedRegion userRegion = Mockito.mock(PartitionedRegion.class);
        Mockito.when(cache.getRegion(index1.getRegionPath())).thenReturn(userRegion);
        PartitionedRegionDataStore userRegionDataStore = Mockito.mock(PartitionedRegionDataStore.class);
        Mockito.when(userRegion.getDataStore()).thenReturn(userRegionDataStore);
        BucketRegion userBucket = Mockito.mock(BucketRegion.class);
        Mockito.when(userRegionDataStore.getLocalBucketById(1)).thenReturn(userBucket);
        Mockito.when(userBucket.isEmpty()).thenReturn(false);
        ArrayList<LuceneIndex> allIndexes = new ArrayList();
        allIndexes.add(index1);
        Mockito.when(service.getAllIndexes()).thenReturn(allIndexes);
        PartitionedRegion mockFileRegion = Mockito.mock(PartitionedRegion.class);
        Mockito.when(index1.getFileAndChunkRegion()).thenReturn(mockFileRegion);
        PartitionedRegionDataStore mockPartitionedRegionDataStore = Mockito.mock(PartitionedRegionDataStore.class);
        Mockito.when(mockFileRegion.getDataStore()).thenReturn(mockPartitionedRegionDataStore);
        Set<Integer> bucketSet = new HashSet<>();
        bucketSet.add(1);
        Mockito.when(mockPartitionedRegionDataStore.getAllLocalPrimaryBucketIds()).thenReturn(bucketSet);
        Mockito.when(index1.isIndexAvailable(1)).thenReturn(false);
        LuceneListIndexFunction function = new LuceneListIndexFunction();
        function.execute(context);
        ArgumentCaptor<Set> resultCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        Set<String> result = resultCaptor.getValue();
        TreeSet expectedResult = new TreeSet();
        expectedResult.add(new org.apache.geode.cache.lucene.internal.cli.LuceneIndexDetails(index1, serverName, LuceneIndexStatus.INDEXING_IN_PROGRESS));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(expectedResult, result);
    }
}

