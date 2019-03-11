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


import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.internal.InternalLuceneService;
import org.apache.geode.cache.lucene.internal.LuceneIndexImpl;
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.cache.lucene.internal.cli.LuceneIndexDetails;
import org.apache.geode.cache.lucene.internal.cli.LuceneIndexInfo;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.test.fake.Fakes;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneDescribeIndexFunctionJUnitTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() throws Throwable {
        GemFireCacheImpl cache = Fakes.cache();
        final String serverName = "mockServer";
        LuceneServiceImpl service = Mockito.mock(LuceneServiceImpl.class);
        Mockito.when(cache.getService(InternalLuceneService.class)).thenReturn(service);
        FunctionContext context = Mockito.mock(FunctionContext.class);
        ResultSender resultSender = Mockito.mock(ResultSender.class);
        LuceneIndexInfo indexInfo = getMockLuceneInfo("index1");
        LuceneIndexImpl index1 = getMockLuceneIndex("index1");
        LuceneDescribeIndexFunction function = new LuceneDescribeIndexFunction();
        Mockito.doReturn(indexInfo).when(context).getArguments();
        Mockito.doReturn(resultSender).when(context).getResultSender();
        Mockito.doReturn(cache).when(context).getCache();
        Mockito.when(service.getIndex(indexInfo.getIndexName(), indexInfo.getRegionPath())).thenReturn(index1);
        function.execute(context);
        ArgumentCaptor<LuceneIndexDetails> resultCaptor = ArgumentCaptor.forClass(LuceneIndexDetails.class);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        LuceneIndexDetails result = resultCaptor.getValue();
        LuceneIndexDetails expected = new LuceneIndexDetails(index1, "mockServer");
        Assert.assertEquals(expected.getIndexName(), result.getIndexName());
        Assert.assertEquals(expected.getRegionPath(), result.getRegionPath());
        Assert.assertEquals(expected.getIndexStats(), result.getIndexStats());
        Assert.assertEquals(expected.getFieldAnalyzersString(), result.getFieldAnalyzersString());
        Assert.assertEquals(expected.getSearchableFieldNamesString(), result.getSearchableFieldNamesString());
    }
}

