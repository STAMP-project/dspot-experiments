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


import ConfigurationPersistenceService.CLUSTER_CONFIG;
import RegionAttributesDataPolicy.PARTITION;
import Result.Status.ERROR;
import Result.Status.OK;
import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheConfig.AsyncEventQueue;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.DeclarableType;
import org.apache.geode.cache.configuration.RegionAttributesType;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.connectors.jdbc.JdbcAsyncWriter;
import org.apache.geode.connectors.jdbc.internal.configuration.FieldMapping;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.connectors.util.internal.MappingCommandUtils;
import org.apache.geode.distributed.ConfigurationPersistenceService;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateMappingCommandTest {
    private InternalCache cache;

    private CreateMappingCommand createRegionMappingCommand;

    private String regionName;

    private String dataSourceName;

    private String tableName;

    private String pdxClass;

    private String pdxClassFile;

    private String group1Name;

    private String group2Name;

    private Set<InternalDistributedMember> members;

    private CliFunctionResult preconditionCheckResults;

    private ArrayList<FieldMapping> fieldMappings = new ArrayList<>();

    private Object[] preconditionOutput = new Object[]{ null, fieldMappings, null };

    private List<CliFunctionResult> results;

    private CliFunctionResult successFunctionResult;

    private RegionMapping mapping;

    private final Object[] arguments = new Object[2];

    private CacheConfig cacheConfig;

    RegionConfig matchingRegion;

    RegionAttributesType matchingRegionAttributes;

    @Test
    public void createsMappingReturnsStatusOKWhenFunctionResultSuccess() throws IOException {
        setupRequiredPreconditions();
        results.add(successFunctionResult);
        String ids = "ids";
        String catalog = "catalog";
        String schema = "schema";
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, null);
        assertThat(result.getStatus()).isSameAs(OK);
        Object[] results = ((Object[]) (result.getConfigObject()));
        RegionMapping regionMapping = ((RegionMapping) (results[0]));
        boolean synchronous = ((boolean) (results[1]));
        assertThat(regionMapping).isNotNull();
        assertThat(regionMapping.getRegionName()).isEqualTo(regionName);
        assertThat(regionMapping.getDataSourceName()).isEqualTo(dataSourceName);
        assertThat(regionMapping.getTableName()).isEqualTo(tableName);
        assertThat(regionMapping.getPdxName()).isEqualTo(pdxClass);
        assertThat(regionMapping.getIds()).isEqualTo(ids);
        assertThat(regionMapping.getCatalog()).isEqualTo(catalog);
        assertThat(regionMapping.getSchema()).isEqualTo(schema);
        assertThat(synchronous).isFalse();
    }

    @Test
    public void createsMappingReturnsStatusOKWhenFunctionResultSuccessWithGroups() throws IOException {
        setupRequiredPreconditionsForGroup();
        results.add(successFunctionResult);
        String ids = "ids";
        String catalog = "catalog";
        String schema = "schema";
        String[] groups = new String[]{ group1Name, group2Name };
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, groups);
        assertThat(result.getStatus()).isSameAs(OK);
        Object[] results = ((Object[]) (result.getConfigObject()));
        RegionMapping regionMapping = ((RegionMapping) (results[0]));
        boolean synchronous = ((boolean) (results[1]));
        assertThat(regionMapping).isNotNull();
        assertThat(regionMapping.getRegionName()).isEqualTo(regionName);
        assertThat(regionMapping.getDataSourceName()).isEqualTo(dataSourceName);
        assertThat(regionMapping.getTableName()).isEqualTo(tableName);
        assertThat(regionMapping.getPdxName()).isEqualTo(pdxClass);
        assertThat(regionMapping.getIds()).isEqualTo(ids);
        assertThat(regionMapping.getCatalog()).isEqualTo(catalog);
        assertThat(regionMapping.getSchema()).isEqualTo(schema);
        assertThat(regionMapping.getFieldMappings()).isEmpty();
        assertThat(synchronous).isFalse();
    }

    @Test
    public void createsMappingReturnsCorrectFieldMappings() throws IOException {
        setupRequiredPreconditions();
        results.add(successFunctionResult);
        String ids = "ids";
        String catalog = "catalog";
        String schema = "schema";
        this.fieldMappings.add(new FieldMapping("pdx1", "pdx1type", "jdbc1", "jdbc1type", false));
        this.fieldMappings.add(new FieldMapping("pdx2", "pdx2type", "jdbc2", "jdbc2type", false));
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, null);
        assertThat(result.getStatus()).isSameAs(OK);
        Object[] results = ((Object[]) (result.getConfigObject()));
        RegionMapping regionMapping = ((RegionMapping) (results[0]));
        assertThat(regionMapping.getFieldMappings()).isEqualTo(this.fieldMappings);
    }

    @Test
    public void createsMappingWithPdxClassFileReturnsCorrectFieldMappings() throws IOException {
        RemoteInputStream remoteInputStream = setupPdxClassFile();
        setupRequiredPreconditions();
        results.add(successFunctionResult);
        String ids = "ids";
        String catalog = "catalog";
        String schema = "schema";
        this.fieldMappings.add(new FieldMapping("pdx1", "pdx1type", "jdbc1", "jdbc1type", false));
        this.fieldMappings.add(new FieldMapping("pdx2", "pdx2type", "jdbc2", "jdbc2type", false));
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, null);
        assertThat(result.getStatus()).isSameAs(OK);
        Object[] results = ((Object[]) (result.getConfigObject()));
        RegionMapping regionMapping = ((RegionMapping) (results[0]));
        assertThat(regionMapping.getFieldMappings()).isEqualTo(this.fieldMappings);
        ArgumentCaptor<Object[]> argumentCaptor = ArgumentCaptor.forClass(Object[].class);
        Mockito.verify(createRegionMappingCommand).executeFunctionAndGetFunctionResult(ArgumentMatchers.any(), argumentCaptor.capture(), ArgumentMatchers.any());
        Object[] args = argumentCaptor.getValue();
        assertThat(args).hasSize(3);
        assertThat(args[0]).isEqualTo(regionMapping);
        assertThat(args[1]).isEqualTo("myPdxClassFilePath");
        assertThat(args[2]).isSameAs(remoteInputStream);
    }

    @Test
    public void createsMappingWithPdxClassFileAndFilePathFromShellIsEmptyListThrowsIllegalStateException() throws IOException {
        setupPdxClassFile();
        Mockito.doReturn(Collections.emptyList()).when(createRegionMappingCommand).getFilePathFromShell();
        setupRequiredPreconditions();
        String ids = "ids";
        String catalog = "catalog";
        String schema = "schema";
        assertThatThrownBy(() -> createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, null)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void createsMappingReturnsRegionMappingWithComputedIds() throws IOException {
        setupRequiredPreconditions();
        results.add(successFunctionResult);
        String ids = "does not matter";
        String catalog = "catalog";
        String schema = "schema";
        String computedIds = "id1,id2";
        preconditionOutput[0] = computedIds;
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, null);
        assertThat(result.getStatus()).isSameAs(OK);
        Object[] results = ((Object[]) (result.getConfigObject()));
        RegionMapping regionMapping = ((RegionMapping) (results[0]));
        assertThat(regionMapping.getIds()).isEqualTo(computedIds);
    }

    @Test
    public void createsMappingReturnsErrorIfPreconditionCheckErrors() throws IOException {
        setupRequiredPreconditions();
        results.add(successFunctionResult);
        String ids = "ids";
        String catalog = "catalog";
        String schema = "schema";
        Mockito.when(preconditionCheckResults.isSuccessful()).thenReturn(false);
        Mockito.when(preconditionCheckResults.getStatusMessage()).thenReturn("precondition check failed");
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, ids, catalog, schema, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains("precondition check failed");
    }

    @Test
    public void createsMappingWithRegionPathCreatesMappingWithSlashRemoved() throws IOException {
        setupRequiredPreconditions();
        results.add(successFunctionResult);
        ResultModel result = createRegionMappingCommand.createMapping(("/" + (regionName)), dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(OK);
        Object[] results = ((Object[]) (result.getConfigObject()));
        RegionMapping regionMapping = ((RegionMapping) (results[0]));
        assertThat(regionMapping).isNotNull();
        assertThat(regionMapping.getRegionName()).isEqualTo(regionName);
    }

    @Test
    public void createsMappingReturnsStatusERRORWhenFunctionResultIsEmpty() throws IOException {
        setupRequiredPreconditions();
        results.clear();
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
    }

    @Test
    public void createsMappingReturnsStatusERRORWhenClusterConfigIsDisabled() throws IOException {
        results.add(successFunctionResult);
        Mockito.doReturn(null).when(createRegionMappingCommand).getConfigurationPersistenceService();
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains("Cluster Configuration must be enabled.");
    }

    @Test
    public void createsMappingReturnsStatusERRORWhenClusterConfigDoesNotContainRegion() throws IOException {
        results.add(successFunctionResult);
        ConfigurationPersistenceService configurationPersistenceService = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(configurationPersistenceService).when(createRegionMappingCommand).getConfigurationPersistenceService();
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(cacheConfig);
        Mockito.when(cacheConfig.getRegions()).thenReturn(Collections.emptyList());
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains((("A region named " + (regionName)) + " must already exist."));
    }

    @Test
    public void createsMappingReturnsStatusERRORWhenRegionMappingExists() throws IOException {
        results.add(successFunctionResult);
        ConfigurationPersistenceService configurationPersistenceService = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(configurationPersistenceService).when(createRegionMappingCommand).getConfigurationPersistenceService();
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        RegionAttributesType loaderAttribute = Mockito.mock(RegionAttributesType.class);
        DeclarableType loaderDeclarable = Mockito.mock(DeclarableType.class);
        Mockito.when(loaderDeclarable.getClassName()).thenReturn(null);
        Mockito.when(loaderAttribute.getCacheLoader()).thenReturn(loaderDeclarable);
        Mockito.when(matchingRegion.getRegionAttributes()).thenReturn(loaderAttribute);
        List<CacheElement> customList = new ArrayList<>();
        RegionMapping existingMapping = Mockito.mock(RegionMapping.class);
        customList.add(existingMapping);
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(customList);
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains((("A JDBC mapping for " + (regionName)) + " already exists."));
    }

    @Test
    public void createsMappingReturnsStatusERRORWhenClusterConfigRegionHasLoader() throws IOException {
        results.add(successFunctionResult);
        ConfigurationPersistenceService configurationPersistenceService = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(configurationPersistenceService).when(createRegionMappingCommand).getConfigurationPersistenceService();
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        RegionAttributesType loaderAttribute = Mockito.mock(RegionAttributesType.class);
        DeclarableType loaderDeclarable = Mockito.mock(DeclarableType.class);
        Mockito.when(loaderDeclarable.getClassName()).thenReturn("MyCacheLoaderClass");
        Mockito.when(loaderAttribute.getCacheLoader()).thenReturn(loaderDeclarable);
        Mockito.when(matchingRegion.getRegionAttributes()).thenReturn(loaderAttribute);
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains((("The existing region " + (regionName)) + " must not already have a cache-loader, but it has MyCacheLoaderClass"));
    }

    @Test
    public void createMappingWithSynchronousReturnsStatusERRORWhenClusterConfigRegionHasWriter() throws IOException {
        results.add(successFunctionResult);
        ConfigurationPersistenceService configurationPersistenceService = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(configurationPersistenceService).when(createRegionMappingCommand).getConfigurationPersistenceService();
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        RegionAttributesType writerAttribute = Mockito.mock(RegionAttributesType.class);
        DeclarableType writerDeclarable = Mockito.mock(DeclarableType.class);
        Mockito.when(writerDeclarable.getClassName()).thenReturn("MyCacheWriterClass");
        Mockito.when(writerAttribute.getCacheWriter()).thenReturn(writerDeclarable);
        Mockito.when(matchingRegion.getRegionAttributes()).thenReturn(writerAttribute);
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, true, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains((("The existing region " + (regionName)) + " must not already have a cache-writer, but it has MyCacheWriterClass"));
    }

    @Test
    public void createMappingWithSynchronousReturnsStatusOKWhenAsycnEventQueueAlreadyExists() throws IOException {
        results.add(successFunctionResult);
        ConfigurationPersistenceService configurationPersistenceService = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(configurationPersistenceService).when(createRegionMappingCommand).getConfigurationPersistenceService();
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        RegionAttributesType loaderAttribute = Mockito.mock(RegionAttributesType.class);
        Mockito.when(loaderAttribute.getCacheLoader()).thenReturn(null);
        Mockito.when(matchingRegion.getRegionAttributes()).thenReturn(loaderAttribute);
        List<AsyncEventQueue> asyncEventQueues = new ArrayList<>();
        AsyncEventQueue matchingQueue = Mockito.mock(AsyncEventQueue.class);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        Mockito.when(matchingQueue.getId()).thenReturn(queueName);
        asyncEventQueues.add(matchingQueue);
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(asyncEventQueues);
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, true, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(OK);
    }

    @Test
    public void createsMappingReturnsStatusERRORWhenAsycnEventQueueAlreadyExists() throws IOException {
        results.add(successFunctionResult);
        ConfigurationPersistenceService configurationPersistenceService = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(configurationPersistenceService).when(createRegionMappingCommand).getConfigurationPersistenceService();
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        RegionAttributesType loaderAttribute = Mockito.mock(RegionAttributesType.class);
        Mockito.when(loaderAttribute.getCacheLoader()).thenReturn(null);
        Mockito.when(matchingRegion.getRegionAttributes()).thenReturn(loaderAttribute);
        List<AsyncEventQueue> asyncEventQueues = new ArrayList<>();
        AsyncEventQueue matchingQueue = Mockito.mock(AsyncEventQueue.class);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        Mockito.when(matchingQueue.getId()).thenReturn(queueName);
        asyncEventQueues.add(matchingQueue);
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(asyncEventQueues);
        ResultModel result = createRegionMappingCommand.createMapping(regionName, dataSourceName, tableName, pdxClass, pdxClassFile, false, null, null, null, null);
        assertThat(result.getStatus()).isSameAs(ERROR);
        assertThat(result.toString()).contains((("An async-event-queue named " + queueName) + " must not already exist."));
    }

    @Test
    public void updateClusterConfigWithNoRegionsDoesNotThrowException() {
        Mockito.when(cacheConfig.getRegions()).thenReturn(Collections.emptyList());
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAddsMappingToRegion() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        assertThat(listCacheElements.size()).isEqualTo(1);
        assertThat(listCacheElements).contains(mapping);
    }

    @Test
    public void updateClusterConfigWithOneNonMatchingRegionDoesNotAddMapping() {
        List<RegionConfig> list = new ArrayList<>();
        RegionConfig nonMatchingRegion = Mockito.mock(RegionConfig.class);
        Mockito.when(nonMatchingRegion.getName()).thenReturn("nonMatchingRegion");
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(nonMatchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(nonMatchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        assertThat(listCacheElements).isEmpty();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionCreatesAsyncEventQueue() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        assertThat(queueList.size()).isEqualTo(1);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        AsyncEventQueue createdQueue = queueList.get(0);
        assertThat(createdQueue.getId()).isEqualTo(queueName);
        assertThat(createdQueue.isParallel()).isFalse();
        assertThat(createdQueue.getAsyncEventListener().getClassName()).isEqualTo(JdbcAsyncWriter.class.getName());
    }

    @Test
    public void updateClusterConfigWithOneMatchingPartitionedRegionCreatesParallelAsyncEventQueue() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        Mockito.when(matchingRegionAttributes.getDataPolicy()).thenReturn(PARTITION);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        assertThat(queueList.get(0).isParallel()).isTrue();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionCallsSetCacheLoader() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        Mockito.verify(matchingRegionAttributes).setCacheLoader(ArgumentMatchers.any());
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndNullQueuesAddsAsyncEventQueueIdToRegion() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn(null);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(matchingRegionAttributes).setAsyncEventQueueIds(argument.capture());
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        assertThat(argument.getValue()).isEqualTo(queueName);
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndEmptyQueuesAddsAsyncEventQueueIdToRegion() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn("");
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(matchingRegionAttributes).setAsyncEventQueueIds(argument.capture());
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        assertThat(argument.getValue()).isEqualTo(queueName);
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndExistingQueuesAddsAsyncEventQueueIdToRegion() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn("q1,q2");
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        Mockito.verify(matchingRegionAttributes).setAsyncEventQueueIds(argument.capture());
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        assertThat(argument.getValue()).isEqualTo(("q1,q2," + queueName));
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndQueuesContainingDuplicateDoesNotModifyAsyncEventQueueIdOnRegion() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        String existingQueues = ("q1," + queueName) + ",q2";
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn(existingQueues);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        Mockito.verify(matchingRegionAttributes, Mockito.never()).setAsyncEventQueueIds(ArgumentMatchers.any());
    }

    @Test
    public void updateClusterConfigWithSynchronousSetsTheCacheWriterOnTheMatchingRegion() {
        arguments[1] = true;
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        Mockito.verify(matchingRegionAttributes).setCacheWriter(ArgumentMatchers.any());
    }

    @Test
    public void updateClusterConfigWithSynchronousAndOneMatchingRegionAndExistingQueuesDoesNotAddsAsyncEventQueueIdToRegion() {
        arguments[1] = true;
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn("q1,q2");
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        Mockito.verify(matchingRegionAttributes, Mockito.never()).setAsyncEventQueueIds(ArgumentMatchers.any());
    }

    @Test
    public void updateClusterConfigWithSynchronousAndOneMatchingRegionDoesNotCreateAsyncEventQueue() {
        arguments[1] = true;
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        List<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        createRegionMappingCommand.updateConfigForGroup(null, cacheConfig, arguments);
        assertThat(queueList).isEmpty();
    }

    @Test
    public void createAsyncEventQueueNameWithRegionPathReturnsQueueNameThatIsTheSameAsRegionWithNoSlash() {
        String queueName1 = MappingCommandUtils.createAsyncEventQueueName("regionName");
        String queueName2 = MappingCommandUtils.createAsyncEventQueueName("/regionName");
        assertThat(queueName1).isEqualTo(queueName2);
    }

    @Test
    public void createAsyncEventQueueNameWithEmptyStringReturnsQueueName() {
        String queueName = MappingCommandUtils.createAsyncEventQueueName("");
        assertThat(queueName).isEqualTo("JDBC#");
    }

    @Test
    public void createAsyncEventQueueNameWithSubregionNameReturnsQueueNameWithNoSlashes() {
        String queueName = MappingCommandUtils.createAsyncEventQueueName("/parent/child/grandchild");
        assertThat(queueName).isEqualTo("JDBC#parent_child_grandchild");
    }
}

