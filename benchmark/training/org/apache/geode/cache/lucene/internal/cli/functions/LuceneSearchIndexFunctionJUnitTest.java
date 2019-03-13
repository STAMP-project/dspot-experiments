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


import java.util.Set;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.internal.InternalLuceneService;
import org.apache.geode.cache.lucene.internal.cli.LuceneQueryInfo;
import org.apache.geode.cache.lucene.internal.cli.LuceneSearchResults;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.test.fake.Fakes;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneSearchIndexFunctionJUnitTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() throws LuceneQueryException {
        FunctionContext context = Mockito.mock(FunctionContext.class);
        ResultSender resultSender = Mockito.mock(ResultSender.class);
        GemFireCacheImpl cache = Fakes.cache();
        LuceneQueryInfo queryInfo = createMockQueryInfo("index", "region", "field1:region1", "field1", 1);
        InternalLuceneService service = getMockLuceneService("A", "Value", "1.2");
        Region mockRegion = Mockito.mock(Region.class);
        LuceneSearchIndexFunction function = new LuceneSearchIndexFunction();
        Mockito.doReturn(queryInfo).when(context).getArguments();
        Mockito.doReturn(resultSender).when(context).getResultSender();
        Mockito.doReturn(cache).when(context).getCache();
        Mockito.when(cache.getService(ArgumentMatchers.eq(InternalLuceneService.class))).thenReturn(service);
        Mockito.when(cache.getRegion(queryInfo.getRegionPath())).thenReturn(mockRegion);
        function.execute(context);
        ArgumentCaptor<Set> resultCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        Set<LuceneSearchResults> result = resultCaptor.getValue();
        Assert.assertEquals(1, result.size());
        for (LuceneSearchResults searchResult : result) {
            Assert.assertEquals("A", searchResult.getKey());
            Assert.assertEquals("Value", searchResult.getValue());
            Assert.assertEquals(1.2, searchResult.getScore(), 0.1);
        }
    }
}

