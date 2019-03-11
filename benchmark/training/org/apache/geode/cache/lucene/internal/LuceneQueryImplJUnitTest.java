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


import LuceneQueryFunction.ID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;
import org.apache.geode.cache.lucene.internal.distributed.LuceneFunctionContext;
import org.apache.geode.cache.lucene.internal.distributed.TopEntries;
import org.apache.geode.cache.lucene.internal.distributed.TopEntriesCollector;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneQueryImplJUnitTest {
    private static int LIMIT = 123;

    private LuceneQueryImpl<Object, Object> query;

    private Execution execution;

    private LuceneQueryProvider provider;

    private ResultCollector<TopEntriesCollector, TopEntries> collector;

    private Region region;

    private PageableLuceneQueryResults<Object, Object> results;

    private Cache cache;

    private CacheTransactionManager cacheTransactionManager;

    @Test
    public void shouldReturnKeysFromFindKeys() throws LuceneQueryException {
        addValueToResults();
        Collection<Object> results = query.findKeys();
        Assert.assertEquals(Collections.singletonList("hi"), results);
    }

    @Test
    public void shouldReturnEmptyListFromFindKeysWithNoResults() throws LuceneQueryException {
        TopEntries entries = new TopEntries();
        Mockito.when(collector.getResult()).thenReturn(entries);
        Collection<Object> results = query.findKeys();
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void shouldReturnValuesFromFindValues() throws LuceneQueryException {
        addValueToResults();
        Collection<Object> results = query.findValues();
        Assert.assertEquals(Collections.singletonList("value"), results);
    }

    @Test
    public void shouldReturnEmptyListFromFindValuesWithNoResults() throws LuceneQueryException {
        TopEntries entries = new TopEntries();
        Mockito.when(collector.getResult()).thenReturn(entries);
        Collection<Object> results = query.findValues();
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void shouldReturnLuceneResultStructFromFindResults() throws LuceneQueryException {
        addValueToResults();
        List<LuceneResultStruct<String, String>> result = new ArrayList<>();
        result.add(new LuceneResultStructImpl("hi", "value", 5));
        Assert.assertEquals(result, query.findResults());
    }

    @Test
    public void shouldInvokeLuceneFunctionWithCorrectArguments() throws Exception {
        addValueToResults();
        PageableLuceneQueryResults<Object, Object> results = query.findPages();
        Mockito.verify(execution).execute(ArgumentMatchers.eq(ID));
        ArgumentCaptor<LuceneFunctionContext> captor = ArgumentCaptor.forClass(LuceneFunctionContext.class);
        Mockito.verify(execution).setArguments(captor.capture());
        LuceneFunctionContext context = captor.getValue();
        Assert.assertEquals(LuceneQueryImplJUnitTest.LIMIT, context.getLimit());
        Assert.assertEquals(provider, context.getQueryProvider());
        Assert.assertEquals("index", context.getIndexName());
        Assert.assertEquals(5, results.getMaxScore(), 0.01);
        Assert.assertEquals(1, results.size());
        final List<LuceneResultStruct<Object, Object>> page = results.next();
        Assert.assertEquals(1, page.size());
        LuceneResultStruct element = page.iterator().next();
        Assert.assertEquals("hi", element.getKey());
        Assert.assertEquals("value", element.getValue());
        Assert.assertEquals(5, element.getScore(), 0.01);
    }
}

