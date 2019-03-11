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


import Result.Status.OK;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheConfig.AsyncEventQueue;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.DeclarableType;
import org.apache.geode.cache.configuration.RegionAttributesType;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.connectors.jdbc.JdbcWriter;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.connectors.util.internal.MappingCommandUtils;
import org.apache.geode.distributed.ConfigurationPersistenceService;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DestroyMappingCommandTest {
    private InternalCache cache;

    private DestroyMappingCommand destroyRegionMappingCommand;

    private String regionName;

    private Set<InternalDistributedMember> members;

    private List<CliFunctionResult> results;

    private CliFunctionResult successFunctionResult;

    private CacheConfig cacheConfig;

    RegionConfig matchingRegion;

    RegionAttributesType matchingRegionAttributes;

    @Test
    public void destroyMappingGivenARegionNameForServerGroup() throws PreconditionException {
        ConfigurationPersistenceService service = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(service).when(destroyRegionMappingCommand).checkForClusterConfiguration();
        Mockito.when(service.getCacheConfig("testGroup1")).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        RegionConfig region = Mockito.mock(RegionConfig.class);
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping mapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(mapping);
        Mockito.when(region.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(region);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        Mockito.doReturn(members).when(destroyRegionMappingCommand).findMembers(new String[]{ "testGroup1" }, null);
        results.add(successFunctionResult);
        ResultModel result = destroyRegionMappingCommand.destroyMapping(regionName, new String[]{ "testGroup1" });
        assertThat(result.getStatus()).isSameAs(OK);
        assertThat(result.getConfigObject()).isEqualTo(regionName);
    }

    @Test
    public void destroyMappingGivenARegionNameReturnsTheNameAsTheConfigObject() throws PreconditionException {
        ConfigurationPersistenceService service = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(service).when(destroyRegionMappingCommand).checkForClusterConfiguration();
        Mockito.when(service.getCacheConfig("cluster")).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        RegionConfig region = Mockito.mock(RegionConfig.class);
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping mapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(mapping);
        Mockito.when(region.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(region);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        results.add(successFunctionResult);
        ResultModel result = destroyRegionMappingCommand.destroyMapping(regionName, null);
        assertThat(result.getStatus()).isSameAs(OK);
        assertThat(result.getConfigObject()).isEqualTo(regionName);
    }

    @Test
    public void destroyMappingGivenARegionPathReturnsTheNoSlashRegionNameAsTheConfigObject() throws PreconditionException {
        ConfigurationPersistenceService service = Mockito.mock(ConfigurationPersistenceService.class);
        Mockito.doReturn(service).when(destroyRegionMappingCommand).checkForClusterConfiguration();
        Mockito.when(service.getCacheConfig("cluster")).thenReturn(cacheConfig);
        List<RegionConfig> list = new ArrayList<>();
        RegionConfig region = Mockito.mock(RegionConfig.class);
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping mapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(mapping);
        Mockito.when(region.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(region);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        results.add(successFunctionResult);
        ResultModel result = destroyRegionMappingCommand.destroyMapping(("/" + (regionName)), null);
        Mockito.verify(destroyRegionMappingCommand, Mockito.times(1)).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.eq(regionName), ArgumentMatchers.any());
        assertThat(result.getStatus()).isSameAs(OK);
        assertThat(result.getConfigObject()).isEqualTo(regionName);
    }

    @Test
    public void updateClusterConfigWithNoRegionsDoesNotThrowException() {
        Mockito.when(cacheConfig.getRegions()).thenReturn(Collections.emptyList());
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        assertThat(modified).isFalse();
    }

    @Test
    public void updateClusterConfigWithOneNonMatchingRegionDoesNotRemoveMapping() {
        List<RegionConfig> list = new ArrayList<>();
        RegionConfig nonMatchingRegion = Mockito.mock(RegionConfig.class);
        Mockito.when(nonMatchingRegion.getName()).thenReturn("nonMatchingRegion");
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping nonMatchingMapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(nonMatchingMapping);
        Mockito.when(nonMatchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(nonMatchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        assertThat(listCacheElements).isEqualTo(Arrays.asList(nonMatchingMapping));
        assertThat(modified).isFalse();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionDoesRemoveMapping() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping matchingMapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(matchingMapping);
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        assertThat(listCacheElements).isEmpty();
        assertThat(modified).isTrue();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndJdbcAsyncQueueRemovesTheQueue() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping matchingMapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(matchingMapping);
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        AsyncEventQueue queue = Mockito.mock(AsyncEventQueue.class);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        Mockito.when(queue.getId()).thenReturn(queueName);
        List<AsyncEventQueue> queueList = new ArrayList<>();
        queueList.add(queue);
        Mockito.when(cacheConfig.getAsyncEventQueues()).thenReturn(queueList);
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        assertThat(queueList).isEmpty();
        assertThat(modified).isTrue();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndJdbcWriterRemovesTheWriter() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping matchingMapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(matchingMapping);
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        DeclarableType cacheWriter = Mockito.mock(DeclarableType.class);
        Mockito.when(cacheWriter.getClassName()).thenReturn(JdbcWriter.class.getName());
        Mockito.when(matchingRegionAttributes.getCacheWriter()).thenReturn(cacheWriter);
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        Mockito.verify(matchingRegionAttributes, Mockito.times(1)).setCacheWriter(null);
        assertThat(modified).isTrue();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndJdbcAsyncQueueIdRemovesTheId() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping matchingMapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(matchingMapping);
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn(queueName);
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        Mockito.verify(matchingRegionAttributes, Mockito.times(1)).setAsyncEventQueueIds("");
        assertThat(modified).isTrue();
    }

    @Test
    public void updateClusterConfigWithOneMatchingRegionAndJdbcAsyncQueueIdsRemovesTheId() {
        List<RegionConfig> list = new ArrayList<>();
        List<CacheElement> listCacheElements = new ArrayList<>();
        RegionMapping matchingMapping = Mockito.mock(RegionMapping.class);
        listCacheElements.add(matchingMapping);
        Mockito.when(matchingRegion.getCustomRegionElements()).thenReturn(listCacheElements);
        list.add(matchingRegion);
        Mockito.when(cacheConfig.getRegions()).thenReturn(list);
        String queueName = MappingCommandUtils.createAsyncEventQueueName(regionName);
        Mockito.when(matchingRegionAttributes.getAsyncEventQueueIds()).thenReturn((((((queueName + "1,") + queueName) + ",") + queueName) + "2"));
        boolean modified = destroyRegionMappingCommand.updateConfigForGroup(null, cacheConfig, regionName);
        Mockito.verify(matchingRegionAttributes, Mockito.times(1)).setAsyncEventQueueIds((((queueName + "1,") + queueName) + "2"));
        assertThat(modified).isTrue();
    }
}

