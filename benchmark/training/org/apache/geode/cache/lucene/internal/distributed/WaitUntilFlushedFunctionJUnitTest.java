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


import java.util.concurrent.TimeUnit;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.internal.AsyncEventQueueImpl;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.internal.InternalLuceneService;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.execute.InternalRegionFunctionContext;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class WaitUntilFlushedFunctionJUnitTest {
    String regionPath = "/region";

    String indexName = "index";

    final EntryScore<String> r1_1 = new EntryScore<String>("key-1-1", 0.5F);

    final EntryScore<String> r1_2 = new EntryScore<String>("key-1-2", 0.4F);

    final EntryScore<String> r1_3 = new EntryScore<String>("key-1-3", 0.3F);

    final EntryScore<String> r2_1 = new EntryScore<String>("key-2-1", 0.45F);

    final EntryScore<String> r2_2 = new EntryScore<String>("key-2-2", 0.35F);

    InternalRegionFunctionContext mockContext;

    ResultSender mockResultSender;

    Region<Object, Object> mockRegion;

    AsyncEventQueueImpl mockAEQ;

    InternalLuceneService mockService;

    LuceneIndexImpl mockIndex;

    WaitUntilFlushedFunctionContext waitArgs;

    private InternalCache mockCache;

    @Test
    public void testExecution() throws Exception {
        Mockito.when(mockContext.getDataSet()).thenReturn(mockRegion);
        Mockito.when(mockContext.getArguments()).thenReturn(waitArgs);
        Mockito.when(mockContext.getResultSender()).thenReturn(mockResultSender);
        Mockito.when(mockCache.getAsyncEventQueue(ArgumentMatchers.any())).thenReturn(mockAEQ);
        Mockito.when(mockAEQ.waitUntilFlushed(10000, TimeUnit.MILLISECONDS)).thenReturn(true);
        WaitUntilFlushedFunction function = new WaitUntilFlushedFunction();
        function.execute(mockContext);
        ArgumentCaptor<Boolean> resultCaptor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(mockResultSender).lastResult(resultCaptor.capture());
        Boolean result = resultCaptor.getValue();
        Assert.assertTrue(result);
    }

    @Test
    public void testQueryFunctionId() {
        String id = new WaitUntilFlushedFunction().getId();
        Assert.assertEquals(WaitUntilFlushedFunction.class.getName(), id);
    }
}

