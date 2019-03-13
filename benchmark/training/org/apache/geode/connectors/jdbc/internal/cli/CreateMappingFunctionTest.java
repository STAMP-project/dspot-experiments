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


import DataPolicy.PARTITION;
import DataPolicy.REPLICATE;
import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.geode.cache.AttributesMutator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.asyncqueue.AsyncEventQueueFactory;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.connectors.jdbc.internal.JdbcConnectorService;
import org.apache.geode.connectors.jdbc.internal.RegionMappingExistsException;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.connectors.util.internal.MappingCommandUtils;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class CreateMappingFunctionTest {
    private static final String REGION_NAME = "testRegion";

    private RegionMapping regionMapping;

    private FunctionContext<Object[]> context;

    private DistributedMember distributedMember;

    private ResultSender<Object> resultSender;

    private JdbcConnectorService service;

    private InternalCache cache;

    private Region region;

    private RegionAttributes attributes;

    private AsyncEventQueueFactory asyncEventQueueFactory;

    private final Object[] functionInputs = new Object[2];

    private CreateMappingFunction function;

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
        assertThat(copy).isNotSameAs(original).isInstanceOf(CreateMappingFunction.class);
    }

    @Test
    public void createRegionMappingReturnsRegionName() throws Exception {
        function.createRegionMapping(service, regionMapping);
        Mockito.verify(service, Mockito.times(1)).createRegionMapping(regionMapping);
    }

    @Test
    public void createRegionMappingThrowsIfMappingExists() throws Exception {
        Mockito.doAnswer(( m) -> {
            throw new RegionMappingExistsException();
        }).when(service).createRegionMapping(ArgumentMatchers.eq(regionMapping));
        Throwable throwable = catchThrowable(() -> function.createRegionMapping(service, regionMapping));
        assertThat(throwable).isInstanceOf(RegionMappingExistsException.class);
        Mockito.verify(service, Mockito.times(1)).createRegionMapping(regionMapping);
    }

    @Test
    public void createRegionMappingThrowsIfRegionDoesNotExist() throws Exception {
        Mockito.when(cache.getRegion(CreateMappingFunctionTest.REGION_NAME)).thenReturn(null);
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(IllegalStateException.class).hasMessage((("create jdbc-mapping requires that the region \"" + (CreateMappingFunctionTest.REGION_NAME)) + "\" exists."));
    }

    @Test
    public void executeCreatesMapping() throws Exception {
        function.executeFunction(context);
        Mockito.verify(service, Mockito.times(1)).createRegionMapping(regionMapping);
    }

    @Test
    public void executeAlterRegionLoader() throws Exception {
        function.executeFunction(context);
        Mockito.verify(service, Mockito.times(1)).createRegionMapping(regionMapping);
        AttributesMutator mutator = region.getAttributesMutator();
        Mockito.verify(mutator, Mockito.times(1)).setCacheLoader(ArgumentMatchers.any());
    }

    @Test
    public void executeWithSynchronousAltersRegionWriter() throws Exception {
        setupSynchronous();
        function.executeFunction(context);
        AttributesMutator mutator = region.getAttributesMutator();
        Mockito.verify(mutator, Mockito.times(1)).setCacheWriter(ArgumentMatchers.any());
    }

    @Test
    public void executeWithSynchronousNeverAltersRegionAsyncEventQueue() throws Exception {
        setupSynchronous();
        function.executeFunction(context);
        AttributesMutator mutator = region.getAttributesMutator();
        Mockito.verify(mutator, Mockito.never()).addAsyncEventQueueId(ArgumentMatchers.any());
    }

    @Test
    public void executeWithSynchronousNeverCreatesAsyncQueue() throws Exception {
        setupSynchronous();
        function.executeFunction(context);
        Mockito.verify(asyncEventQueueFactory, Mockito.never()).create(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void executeAlterRegionAsyncEventQueue() throws Exception {
        String queueName = MappingCommandUtils.createAsyncEventQueueName(CreateMappingFunctionTest.REGION_NAME);
        function.executeFunction(context);
        Mockito.verify(service, Mockito.times(1)).createRegionMapping(regionMapping);
        AttributesMutator mutator = region.getAttributesMutator();
        Mockito.verify(mutator, Mockito.times(1)).addAsyncEventQueueId(queueName);
    }

    @Test
    public void executeCreatesSerialAsyncQueueForNonPartitionedRegion() throws Exception {
        Mockito.when(attributes.getDataPolicy()).thenReturn(REPLICATE);
        function.executeFunction(context);
        Mockito.verify(asyncEventQueueFactory, Mockito.times(1)).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(asyncEventQueueFactory, Mockito.times(1)).setParallel(false);
    }

    @Test
    public void executeCreatesParallelAsyncQueueForPartitionedRegion() throws Exception {
        Mockito.when(attributes.getDataPolicy()).thenReturn(PARTITION);
        function.executeFunction(context);
        Mockito.verify(asyncEventQueueFactory, Mockito.times(1)).create(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(asyncEventQueueFactory, Mockito.times(1)).setParallel(true);
    }

    @Test
    public void executeReportsErrorIfRegionMappingExists() throws Exception {
        Mockito.doThrow(RegionMappingExistsException.class).when(service).createRegionMapping(ArgumentMatchers.eq(regionMapping));
        function.execute(context);
        ArgumentCaptor<CliFunctionResult> argument = ArgumentCaptor.forClass(CliFunctionResult.class);
        Mockito.verify(resultSender, Mockito.times(1)).lastResult(argument.capture());
        assertThat(argument.getValue().getStatusMessage()).contains(RegionMappingExistsException.class.getName());
    }
}

