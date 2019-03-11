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
import org.apache.geode.cache.lucene.internal.LuceneServiceImpl;
import org.apache.geode.cache.lucene.internal.cli.LuceneDestroyIndexInfo;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.management.internal.configuration.domain.XmlEntity;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ LuceneTest.class })
public class LuceneDestroyIndexFunctionJUnitTest {
    private LuceneServiceImpl service;

    private GemFireCacheImpl cache;

    private String member;

    private FunctionContext context;

    private ResultSender resultSender;

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyIndex() throws Throwable {
        String indexName = "index1";
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(indexName, regionPath, false);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        function = Mockito.spy(function);
        function.execute(this.context);
        Mockito.verify(this.service).destroyIndex(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        Mockito.verify(function).getXmlEntity(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyDefinedIndex(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyIndexes(ArgumentMatchers.eq(regionPath));
        verifyFunctionResult(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyIndexFailure() throws Throwable {
        String indexName = "index1";
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(indexName, regionPath, false);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        Mockito.doThrow(new IllegalStateException()).when(this.service).destroyIndex(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        function.execute(this.context);
        verifyFunctionResult(false);
    }

    @Test
    public void testDestroyDefinedIndex() throws Throwable {
        String indexName = "index1";
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(indexName, regionPath, true);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        function = Mockito.spy(function);
        function.execute(this.context);
        Mockito.verify(this.service).destroyDefinedIndex(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyIndex(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyIndexes(ArgumentMatchers.eq(regionPath));
        Mockito.verify(function, Mockito.never()).getXmlEntity(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        verifyFunctionResult(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyDefinedIndexFailure() throws Throwable {
        String indexName = "index1";
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(indexName, regionPath, true);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        Mockito.doThrow(new IllegalStateException()).when(this.service).destroyDefinedIndex(ArgumentMatchers.eq(indexName), ArgumentMatchers.eq(regionPath));
        function.execute(this.context);
        verifyFunctionResult(false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyIndexes() throws Throwable {
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(null, regionPath, false);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        function = Mockito.spy(function);
        function.execute(this.context);
        Mockito.verify(this.service).destroyIndexes(ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service).destroyDefinedIndexes(ArgumentMatchers.eq(regionPath));
        Mockito.verify(function).getXmlEntity(ArgumentMatchers.eq(null), ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyIndex(ArgumentMatchers.any(), ArgumentMatchers.eq(regionPath));
        verifyFunctionResult(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyIndexesFailure() throws Throwable {
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(null, regionPath, false);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        Mockito.doThrow(new IllegalStateException()).when(this.service).destroyIndexes(ArgumentMatchers.eq(regionPath));
        Mockito.doThrow(new IllegalStateException()).when(this.service).destroyDefinedIndexes(ArgumentMatchers.eq(regionPath));
        function.execute(this.context);
        verifyFunctionResult(false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyDefinedIndexes() throws Throwable {
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(null, regionPath, true);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        function = Mockito.spy(function);
        function.execute(this.context);
        Mockito.verify(this.service).destroyDefinedIndexes(ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyIndexes(ArgumentMatchers.eq(regionPath));
        Mockito.verify(this.service, Mockito.never()).destroyIndex(ArgumentMatchers.any(), ArgumentMatchers.eq(regionPath));
        Mockito.verify(function, Mockito.never()).getXmlEntity(ArgumentMatchers.eq("index1"), ArgumentMatchers.eq(regionPath));
        verifyFunctionResult(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroyDefinedIndexesFailure() throws Throwable {
        String regionPath = "/region1";
        LuceneDestroyIndexInfo indexInfo = new LuceneDestroyIndexInfo(null, regionPath, true);
        Mockito.when(this.context.getArguments()).thenReturn(indexInfo);
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        Mockito.doThrow(new IllegalStateException()).when(this.service).destroyDefinedIndexes(ArgumentMatchers.eq(regionPath));
        function.execute(this.context);
        verifyFunctionResult(false);
    }

    @Test
    public void getXmlEntity() throws Exception {
        LuceneDestroyIndexFunction function = new LuceneDestroyIndexFunction();
        XmlEntity entity1 = function.getXmlEntity("index", "/region");
        XmlEntity entity2 = function.getXmlEntity("index", "region");
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.getSearchString()).isEqualTo(entity2.getSearchString());
    }
}

