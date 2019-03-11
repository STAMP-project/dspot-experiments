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
package org.apache.geode.cache.lucene.internal.distributed;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.LuceneIndexNotFoundException;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.internal.InternalLuceneService;
import org.apache.geode.cache.lucene.internal.LuceneIndexCreationProfile;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.cache.lucene.internal.LuceneIndexStats;
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.cache.lucene.internal.repository.IndexRepository;
import org.apache.geode.cache.lucene.internal.repository.IndexResultCollector;
import org.apache.geode.cache.lucene.internal.repository.RepositoryManager;
import org.apache.geode.cache.lucene.test.LuceneTestUtilities;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.execute.InternalFunctionInvocationTargetException;
import org.apache.geode.internal.cache.execute.InternalRegionFunctionContext;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.search.Query;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@Category({ LuceneTest.class })
public class LuceneQueryFunctionJUnitTest {
    private String regionPath = "/region";

    private final EntryScore<String> r1_1 = new EntryScore("key-1-1", 0.5F);

    private final EntryScore<String> r1_2 = new EntryScore("key-1-2", 0.4F);

    private final EntryScore<String> r1_3 = new EntryScore("key-1-3", 0.3F);

    private final EntryScore<String> r2_1 = new EntryScore("key-2-1", 0.45F);

    private final EntryScore<String> r2_2 = new EntryScore("key-2-2", 0.35F);

    private InternalRegionFunctionContext mockContext;

    private ResultSender mockResultSender;

    private Region<Object, Object> mockRegion;

    private RepositoryManager mockRepoManager;

    private IndexRepository mockRepository1;

    private IndexRepository mockRepository2;

    private IndexResultCollector mockCollector;

    private InternalLuceneService mockService;

    private LuceneIndexImpl mockIndex;

    private LuceneIndexStats mockStats;

    private ArrayList<IndexRepository> repos;

    private LuceneFunctionContext<IndexResultCollector> searchArgs;

    private LuceneQueryProvider queryProvider;

    private Query query;

    private InternalCache mockCache;

    @Test
    public void testRepoQueryAndMerge() throws Exception {
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        Mockito.when(mockRepoManager.getRepositories(ArgumentMatchers.eq(mockContext), ArgumentMatchers.eq(false))).thenReturn(repos);
        Mockito.doAnswer(( invocation) -> {
            IndexResultCollector collector = invocation.getArgument(2);
            collector.collect(r1_1.getKey(), r1_1.getScore());
            collector.collect(r1_2.getKey(), r1_2.getScore());
            collector.collect(r1_3.getKey(), r1_3.getScore());
            return null;
        }).when(mockRepository1).query(ArgumentMatchers.eq(query), ArgumentMatchers.eq(LuceneQueryFactory.DEFAULT_LIMIT), ArgumentMatchers.any(IndexResultCollector.class));
        Mockito.doAnswer(( invocation) -> {
            IndexResultCollector collector = invocation.getArgument(2);
            collector.collect(r2_1.getKey(), r2_1.getScore());
            collector.collect(r2_2.getKey(), r2_2.getScore());
            return null;
        }).when(mockRepository2).query(ArgumentMatchers.eq(query), ArgumentMatchers.eq(LuceneQueryFactory.DEFAULT_LIMIT), ArgumentMatchers.any(IndexResultCollector.class));
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
        ArgumentCaptor<TopEntriesCollector> resultCaptor = ArgumentCaptor.forClass(TopEntriesCollector.class);
        Mockito.verify(mockResultSender).lastResult(resultCaptor.capture());
        TopEntriesCollector result = resultCaptor.getValue();
        List<EntryScore> hits = result.getEntries().getHits();
        Assert.assertEquals(5, hits.size());
        LuceneTestUtilities.verifyResultOrder(result.getEntries().getHits(), r1_1, r2_1, r1_2, r2_2, r1_3);
    }

    @Test
    public void testResultLimitClause() throws Exception {
        searchArgs = new LuceneFunctionContext<IndexResultCollector>(queryProvider, "indexName", null, 3);
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        Mockito.when(mockRepoManager.getRepositories(ArgumentMatchers.eq(mockContext), ArgumentMatchers.eq(false))).thenReturn(repos);
        Mockito.doAnswer(( invocation) -> {
            IndexResultCollector collector = invocation.getArgument(2);
            collector.collect(r1_1.getKey(), r1_1.getScore());
            collector.collect(r1_2.getKey(), r1_2.getScore());
            collector.collect(r1_3.getKey(), r1_3.getScore());
            return null;
        }).when(mockRepository1).query(ArgumentMatchers.eq(query), ArgumentMatchers.eq(3), ArgumentMatchers.any(IndexResultCollector.class));
        Mockito.doAnswer(( invocation) -> {
            IndexResultCollector collector = invocation.getArgument(2);
            collector.collect(r2_1.getKey(), r2_1.getScore());
            collector.collect(r2_2.getKey(), r2_2.getScore());
            return null;
        }).when(mockRepository2).query(ArgumentMatchers.eq(query), ArgumentMatchers.eq(3), ArgumentMatchers.any(IndexResultCollector.class));
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
        ArgumentCaptor<TopEntriesCollector> resultCaptor = ArgumentCaptor.forClass(TopEntriesCollector.class);
        Mockito.verify(mockResultSender).lastResult(resultCaptor.capture());
        TopEntriesCollector result = resultCaptor.getValue();
        List<EntryScore> hits = result.getEntries().getHits();
        Assert.assertEquals(3, hits.size());
        LuceneTestUtilities.verifyResultOrder(result.getEntries().getHits(), r1_1, r2_1, r1_2);
    }

    @Test
    public void injectCustomCollectorManager() throws Exception {
        final CollectorManager mockManager = Mockito.mock(CollectorManager.class);
        searchArgs = new LuceneFunctionContext<IndexResultCollector>(queryProvider, "indexName", mockManager);
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        repos.remove(0);
        Mockito.when(mockRepoManager.getRepositories(ArgumentMatchers.eq(mockContext), ArgumentMatchers.eq(false))).thenReturn(repos);
        Mockito.when(mockManager.newCollector(ArgumentMatchers.eq("repo2"))).thenReturn(mockCollector);
        Mockito.when(mockManager.reduce(ArgumentMatchers.any(Collection.class))).thenAnswer(( invocation) -> {
            Collection<IndexResultCollector> collectors = invocation.getArgument(0);
            assertEquals(1, collectors.size());
            assertEquals(mockCollector, collectors.iterator().next());
            return new TopEntriesCollector(null);
        });
        Mockito.doAnswer(( invocation) -> {
            IndexResultCollector collector = invocation.getArgument(2);
            collector.collect(r2_1.getKey(), r2_1.getScore());
            return null;
        }).when(mockRepository2).query(ArgumentMatchers.eq(query), ArgumentMatchers.eq(LuceneQueryFactory.DEFAULT_LIMIT), ArgumentMatchers.any(IndexResultCollector.class));
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
        Mockito.verify(mockCollector).collect(ArgumentMatchers.eq("key-2-1"), ArgumentMatchers.eq(0.45F));
        Mockito.verify(mockResultSender).lastResult(ArgumentMatchers.any(TopEntriesCollector.class));
    }

    @Test(expected = FunctionException.class)
    public void testIndexRepoQueryFails() throws Exception {
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        Mockito.when(mockRepoManager.getRepositories(ArgumentMatchers.eq(mockContext), ArgumentMatchers.eq(false))).thenReturn(repos);
        Mockito.doThrow(IOException.class).when(mockRepository1).query(ArgumentMatchers.eq(query), ArgumentMatchers.eq(LuceneQueryFactory.DEFAULT_LIMIT), ArgumentMatchers.any(IndexResultCollector.class));
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
    }

    @Test(expected = LuceneIndexNotFoundException.class)
    public void whenServiceReturnsNullIndexDuringQueryExecutionFunctionExceptionShouldBeThrown() throws Exception {
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        LuceneQueryFunction function = new LuceneQueryFunction();
        Mockito.when(mockService.getIndex(ArgumentMatchers.eq("indexName"), ArgumentMatchers.eq(regionPath))).thenReturn(null);
        function.execute(mockContext);
    }

    @Test
    public void whenServiceReturnsNullIndexButHasDefinedLuceneIndexDuringQueryExecutionShouldBlockUntilAvailable() throws Exception {
        LuceneServiceImpl mockServiceImpl = Mockito.mock(LuceneServiceImpl.class);
        Mockito.when(mockCache.getService(ArgumentMatchers.any())).thenReturn(mockServiceImpl);
        Mockito.when(mockServiceImpl.getIndex(ArgumentMatchers.eq("indexName"), ArgumentMatchers.eq(regionPath))).thenAnswer(new Answer() {
            private boolean calledFirstTime = false;

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                if ((calledFirstTime) == false) {
                    calledFirstTime = true;
                    return null;
                } else {
                    return mockIndex;
                }
            }
        });
        Mockito.when(mockServiceImpl.getDefinedIndex(ArgumentMatchers.eq("indexName"), ArgumentMatchers.eq(regionPath))).thenAnswer(new Answer() {
            private int count = 10;

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                if (((count)--) > 0) {
                    return Mockito.mock(LuceneIndexCreationProfile.class);
                }
                return null;
            }
        });
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        CancelCriterion mockCancelCriterion = Mockito.mock(CancelCriterion.class);
        Mockito.when(mockCache.getCancelCriterion()).thenReturn(mockCancelCriterion);
        Mockito.when(mockCancelCriterion.isCancelInProgress()).thenReturn(false);
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
    }

    @Test(expected = InternalFunctionInvocationTargetException.class)
    public void whenServiceThrowsCacheClosedDuringQueryExecutionFunctionExceptionShouldBeThrown() throws Exception {
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        LuceneQueryFunction function = new LuceneQueryFunction();
        Mockito.when(mockService.getIndex(ArgumentMatchers.eq("indexName"), ArgumentMatchers.eq(regionPath))).thenThrow(new CacheClosedException());
        function.execute(mockContext);
    }

    @Test(expected = InternalFunctionInvocationTargetException.class)
    public void whenCacheIsClosedDuringLuceneQueryExecutionInternalFunctionShouldBeThrownToTriggerFunctionServiceRetry() throws Exception {
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        LuceneQueryFunction function = new LuceneQueryFunction();
        Mockito.when(mockRepoManager.getRepositories(ArgumentMatchers.eq(mockContext), ArgumentMatchers.eq(false))).thenThrow(new CacheClosedException());
        function.execute(mockContext);
    }

    @Test(expected = FunctionException.class)
    public void testReduceError() throws Exception {
        final CollectorManager mockManager = Mockito.mock(CollectorManager.class);
        searchArgs = new LuceneFunctionContext<IndexResultCollector>(queryProvider, "indexName", mockManager);
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        repos.remove(1);
        Mockito.when(mockRepoManager.getRepositories(ArgumentMatchers.eq(mockContext))).thenReturn(repos);
        Mockito.when(mockManager.newCollector(ArgumentMatchers.eq("repo1"))).thenReturn(mockCollector);
        Mockito.doAnswer(( m) -> {
            throw new IOException();
        }).when(mockManager).reduce(ArgumentMatchers.any(Collection.class));
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
    }

    @Test(expected = FunctionException.class)
    public void queryProviderErrorIsHandled() throws Exception {
        queryProvider = Mockito.mock(LuceneQueryProvider.class);
        searchArgs = new LuceneFunctionContext<IndexResultCollector>(queryProvider, "indexName");
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(searchArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        Mockito.when(queryProvider.getQuery(ArgumentMatchers.eq(mockIndex))).thenThrow(LuceneQueryException.class);
        LuceneQueryFunction function = new LuceneQueryFunction();
        function.execute(mockContext);
    }

    @Test
    public void testQueryFunctionId() {
        String id = new LuceneQueryFunction().getId();
        Assert.assertEquals(LuceneQueryFunction.class.getName(), id);
    }
}

