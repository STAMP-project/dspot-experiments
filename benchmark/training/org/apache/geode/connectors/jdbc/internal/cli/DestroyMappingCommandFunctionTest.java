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
package org.apache.geode.connectors.jdbc.internal.cli;


import java.io.Serializable;
import java.util.Collections;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.asyncqueue.internal.InternalAsyncEventQueue;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.connectors.jdbc.JdbcLoader;
import org.apache.geode.connectors.jdbc.JdbcWriter;
import org.apache.geode.connectors.jdbc.internal.JdbcConnectorService;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.connectors.util.internal.MappingCommandUtils;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DestroyMappingCommandFunctionTest {
    private static final String regionName = "testRegion";

    private DestroyMappingFunction function;

    private FunctionContext<String> context;

    private ResultSender<Object> resultSender;

    private RegionMapping mapping;

    private JdbcConnectorService service;

    private InternalCache cache;

    private Region region;

    private RegionAttributes regionAttributes;

    private AttributesMutator regionMutator;

    @Test
    public void isHAReturnsFalse() {
        assertThat(function.isHA()).isFalse();
    }

    @Test
    public void getIdReturnsNameOfClass() {
        assertThat(function.getId()).isEqualTo(function.getClass().getName());
    }

    @Test
    public void serializes() {
        Serializable original = function;
        Object copy = SerializationUtils.clone(original);
        assertThat(copy).isNotSameAs(original).isInstanceOf(DestroyMappingFunction.class);
    }

    @Test
    public void executeFunctionGivenExistingMappingReturnsTrue() {
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.toString()).contains((("Destroyed JDBC mapping for region " + (DestroyMappingCommandFunctionTest.regionName)) + " on myMemberName"));
    }

    @Test
    public void executeFunctionGivenNoExistingMappingReturnsFalse() {
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.toString()).contains((("JDBC mapping for region \"" + (DestroyMappingCommandFunctionTest.regionName)) + "\" not found"));
    }

    @Test
    public void executeFunctionGivenARegionWithJdbcLoaderRemovesTheLoader() {
        Mockito.when(regionAttributes.getCacheLoader()).thenReturn(Mockito.mock(JdbcLoader.class));
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(regionMutator, Mockito.times(1)).setCacheLoader(null);
    }

    @Test
    public void executeFunctionGivenARegionWithNonJdbcLoaderDoesNotRemoveTheLoader() {
        Mockito.when(regionAttributes.getCacheLoader()).thenReturn(Mockito.mock(CacheLoader.class));
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(regionMutator, Mockito.never()).setCacheLoader(null);
    }

    @Test
    public void executeFunctionGivenARegionWithJdbcWriterRemovesTheWriter() {
        Mockito.when(regionAttributes.getCacheWriter()).thenReturn(Mockito.mock(JdbcWriter.class));
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(regionMutator, Mockito.times(1)).setCacheWriter(null);
    }

    @Test
    public void executeFunctionGivenARegionWithNonJdbcWriterDoesNotRemoveTheWriter() {
        Mockito.when(regionAttributes.getCacheWriter()).thenReturn(Mockito.mock(CacheWriter.class));
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(regionMutator, Mockito.never()).setCacheWriter(null);
    }

    @Test
    public void executeFunctionGivenARegionWithJdbcAsyncEventQueueRemovesTheQueueName() {
        String queueName = MappingCommandUtils.createAsyncEventQueueName(DestroyMappingCommandFunctionTest.regionName);
        Mockito.when(regionAttributes.getAsyncEventQueueIds()).thenReturn(Collections.singleton(queueName));
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(regionMutator, Mockito.times(1)).removeAsyncEventQueueId(queueName);
    }

    @Test
    public void executeFunctionGivenARegionWithNonJdbcAsyncEventQueueDoesNotRemoveTheQueueName() {
        Mockito.when(regionAttributes.getAsyncEventQueueIds()).thenReturn(Collections.singleton("nonJdbcQueue"));
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(regionMutator, Mockito.never()).removeAsyncEventQueueId(ArgumentMatchers.any());
    }

    @Test
    public void executeFunctionGivenAJdbcAsyncWriterQueueRemovesTheQueue() {
        String queueName = MappingCommandUtils.createAsyncEventQueueName(DestroyMappingCommandFunctionTest.regionName);
        InternalAsyncEventQueue myQueue = Mockito.mock(InternalAsyncEventQueue.class);
        Mockito.when(cache.getAsyncEventQueue(queueName)).thenReturn(myQueue);
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(myQueue, Mockito.times(1)).stop();
        Mockito.verify(myQueue, Mockito.times(1)).destroy();
    }

    @Test
    public void executeFunctionGivenExistingMappingCallsDestroyRegionMapping() {
        Mockito.when(service.getMappingForRegion(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName))).thenReturn(mapping);
        function.executeFunction(context);
        Mockito.verify(service, Mockito.times(1)).destroyRegionMapping(ArgumentMatchers.eq(DestroyMappingCommandFunctionTest.regionName));
    }

    @Test
    public void executeReportsErrorIfMappingNotFound() {
        function.execute(context);
        ArgumentCaptor<CliFunctionResult> argument = ArgumentCaptor.forClass(CliFunctionResult.class);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(argument.capture());
        assertThat(argument.getValue().getStatusMessage()).contains((("JDBC mapping for region \"" + (DestroyMappingCommandFunctionTest.regionName)) + "\" not found"));
    }
}

