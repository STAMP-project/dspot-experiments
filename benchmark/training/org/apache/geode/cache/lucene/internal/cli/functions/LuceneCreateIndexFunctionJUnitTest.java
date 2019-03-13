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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.lucene.internal.InternalLuceneService;
import org.apache.geode.cache.lucene.internal.LuceneIndexFactoryImpl;
import org.apache.geode.cache.lucene.internal.cli.LuceneIndexInfo;
import org.apache.geode.cache.lucene.internal.repository.serializer.PrimitiveSerializer;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneCreateIndexFunctionJUnitTest {
    private InternalLuceneService service;

    private GemFireCacheImpl cache;

    String member;

    FunctionContext context;

    ResultSender resultSender;

    CliFunctionResult expectedResult;

    private LuceneIndexFactoryImpl factory;

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithAnalyzer() throws Throwable {
        List<String> analyzerNames = new ArrayList<>();
        analyzerNames.add(StandardAnalyzer.class.getCanonicalName());
        analyzerNames.add(KeywordAnalyzer.class.getCanonicalName());
        analyzerNames.add(StandardAnalyzer.class.getCanonicalName());
        String[] analyzers = new String[3];
        analyzerNames.toArray(analyzers);
        LuceneIndexInfo indexInfo = new LuceneIndexInfo("index1", "/region1", new String[]{ "field1", "field2", "field3" }, analyzers, null);
        Mockito.when(context.getArguments()).thenReturn(indexInfo);
        LuceneCreateIndexFunction function = new LuceneCreateIndexFunction();
        function.execute(context);
        ArgumentCaptor<Map> analyzersCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(service).createIndexFactory();
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field1"), ArgumentMatchers.isA(StandardAnalyzer.class));
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field2"), ArgumentMatchers.isA(KeywordAnalyzer.class));
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field3"), ArgumentMatchers.isA(StandardAnalyzer.class));
        Mockito.verify(factory).create(ArgumentMatchers.eq("index1"), ArgumentMatchers.eq("/region1"), ArgumentMatchers.eq(false));
        ArgumentCaptor<Set> resultCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = ((CliFunctionResult) (resultCaptor.getValue()));
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithoutAnalyzer() throws Throwable {
        String[] fields = new String[]{ "field1", "field2", "field3" };
        LuceneIndexInfo indexInfo = new LuceneIndexInfo("index1", "/region1", fields, null, null);
        Mockito.when(context.getArguments()).thenReturn(indexInfo);
        LuceneCreateIndexFunction function = new LuceneCreateIndexFunction();
        function.execute(context);
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field1"));
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field2"));
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field3"));
        Mockito.verify(factory).create(ArgumentMatchers.eq("index1"), ArgumentMatchers.eq("/region1"), ArgumentMatchers.eq(false));
        ArgumentCaptor<Set> resultCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = ((CliFunctionResult) (resultCaptor.getValue()));
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithSerializer() throws Throwable {
        String[] fields = new String[]{ "field1", "field2", "field3" };
        LuceneIndexInfo indexInfo = new LuceneIndexInfo("index1", "/region1", fields, null, PrimitiveSerializer.class.getCanonicalName());
        Mockito.when(context.getArguments()).thenReturn(indexInfo);
        LuceneCreateIndexFunction function = new LuceneCreateIndexFunction();
        function.execute(context);
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field1"));
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field2"));
        Mockito.verify(factory).addField(ArgumentMatchers.eq("field3"));
        Mockito.verify(factory).setLuceneSerializer(ArgumentMatchers.isA(PrimitiveSerializer.class));
        Mockito.verify(factory).create(ArgumentMatchers.eq("index1"), ArgumentMatchers.eq("/region1"), ArgumentMatchers.eq(false));
        ArgumentCaptor<Set> resultCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(resultSender).lastResult(resultCaptor.capture());
        CliFunctionResult result = ((CliFunctionResult) (resultCaptor.getValue()));
        Assert.assertEquals(expectedResult, result);
    }
}

