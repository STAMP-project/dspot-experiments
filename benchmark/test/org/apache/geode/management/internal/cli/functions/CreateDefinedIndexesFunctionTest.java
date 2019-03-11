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
package org.apache.geode.management.internal.cli.functions;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexCreationException;
import org.apache.geode.cache.query.MultiIndexCreationException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.internal.index.CompactMapRangeIndex;
import org.apache.geode.cache.query.internal.index.HashIndex;
import org.apache.geode.cache.query.internal.index.PrimaryKeyIndex;
import org.junit.Test;
import org.mockito.Mockito;


public class CreateDefinedIndexesFunctionTest {
    private Cache cache;

    private org.apache.geode.cache.Region region1;

    private org.apache.geode.cache.Region region2;

    private FunctionContext context;

    private QueryService queryService;

    private CreateDefinedIndexesFunctionTest.TestResultSender resultSender;

    private Set<RegionConfig.Index> indexDefinitions;

    private CreateDefinedIndexesFunction function;

    @Test
    public void noIndexDefinitionsAsFunctionArgument() throws Exception {
        context = new org.apache.geode.internal.cache.execute.FunctionContextImpl(cache, CreateDefinedIndexesFunction.class.getName(), Collections.emptySet(), resultSender);
        function.execute(context);
        List<?> results = resultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        Object firstResult = results.get(0);
        assertThat(firstResult).isInstanceOf(CliFunctionResult.class);
        assertThat(isSuccessful()).isTrue();
        assertThat(getMessage()).isNotEmpty();
        assertThat(getMessage()).contains("No indexes defined");
    }

    @Test
    public void noIndexPreviouslyDefinedInQueryService() throws Exception {
        Mockito.when(queryService.createDefinedIndexes()).thenReturn(Collections.emptyList());
        context = new org.apache.geode.internal.cache.execute.FunctionContextImpl(cache, CreateDefinedIndexesFunction.class.getName(), indexDefinitions, resultSender);
        function.execute(context);
        List<?> results = resultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        Object firstResult = results.get(0);
        assertThat(firstResult).isInstanceOf(CliFunctionResult.class);
        assertThat(isSuccessful()).isTrue();
        assertThat(getMessage()).isNotEmpty();
        assertThat(getMessage()).contains("No indexes defined");
    }

    @Test
    public void multiIndexCreationExceptionThrowByQueryService() throws Exception {
        HashMap<String, Exception> exceptions = new HashMap<>();
        exceptions.put("index1", new IndexCreationException("Mock Failure."));
        exceptions.put("index3", new IndexCreationException("Another Mock Failure."));
        Mockito.when(queryService.createDefinedIndexes()).thenThrow(new MultiIndexCreationException(exceptions));
        context = new org.apache.geode.internal.cache.execute.FunctionContextImpl(cache, CreateDefinedIndexesFunction.class.getName(), indexDefinitions, resultSender);
        function.execute(context);
        List<?> results = resultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(4);
        CliFunctionResult result1 = ((CliFunctionResult) (results.get(0)));
        assertThat(result1.isSuccessful()).isTrue();
        assertThat(result1.getStatusMessage()).isEqualTo("Created index index2");
        CliFunctionResult result2 = ((CliFunctionResult) (results.get(1)));
        assertThat(result2.isSuccessful()).isFalse();
        assertThat(result2.getStatusMessage()).isEqualTo("Failed to create index index1: Mock Failure.");
        CliFunctionResult result3 = ((CliFunctionResult) (results.get(2)));
        assertThat(result3.isSuccessful()).isFalse();
        assertThat(result3.getStatusMessage()).isEqualTo("Failed to create index index3: Another Mock Failure.");
    }

    @Test
    public void unexpectedExceptionThrowByQueryService() throws Exception {
        Mockito.when(queryService.createDefinedIndexes()).thenThrow(new RuntimeException("Mock Exception"));
        context = new org.apache.geode.internal.cache.execute.FunctionContextImpl(cache, CreateDefinedIndexesFunction.class.getName(), indexDefinitions, resultSender);
        function.execute(context);
        List<?> results = resultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        CliFunctionResult firstResult = ((CliFunctionResult) (results.get(0)));
        assertThat(firstResult.isSuccessful()).isFalse();
        assertThat(firstResult.getStatusMessage()).isEqualTo("Mock Exception");
    }

    @Test
    public void creationSuccess() throws Exception {
        Index index1 = Mockito.mock(HashIndex.class);
        Mockito.when(index1.getName()).thenReturn("index1");
        Mockito.when(index1.getRegion()).thenReturn(region1);
        Index index2 = Mockito.mock(CompactMapRangeIndex.class);
        Mockito.when(index2.getName()).thenReturn("index2");
        Mockito.when(index2.getRegion()).thenReturn(region2);
        Index index3 = Mockito.mock(PrimaryKeyIndex.class);
        Mockito.when(index3.getName()).thenReturn("index3");
        Mockito.when(index3.getRegion()).thenReturn(region1);
        Mockito.when(queryService.createDefinedIndexes()).thenReturn(Arrays.asList(index1, index2, index3));
        context = new org.apache.geode.internal.cache.execute.FunctionContextImpl(cache, CreateDefinedIndexesFunction.class.getName(), indexDefinitions, resultSender);
        function.execute(context);
        List<?> results = resultSender.getResults();
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(4);
        Object firstIndex = results.get(0);
        assertThat(firstIndex).isNotNull();
        assertThat(firstIndex).isInstanceOf(CliFunctionResult.class);
        assertThat(isSuccessful());
    }

    private static class TestResultSender implements ResultSender {
        private final List<Object> results = new LinkedList<>();

        private Exception t;

        protected List<Object> getResults() throws Exception {
            if ((t) != null) {
                throw t;
            }
            return Collections.unmodifiableList(results);
        }

        @Override
        public void lastResult(final Object lastResult) {
            results.add(lastResult);
        }

        @Override
        public void sendResult(final Object oneResult) {
            results.add(oneResult);
        }

        @Override
        public void sendException(final Throwable t) {
            this.t = ((Exception) (t));
        }
    }
}

