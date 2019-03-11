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


import CacheConfig.AsyncEventQueue;
import ConfigurationPersistenceService.CLUSTER_CONFIG;
import java.util.ArrayList;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.connectors.jdbc.internal.configuration.FieldMapping;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.connectors.util.internal.MappingCommandUtils;
import org.apache.geode.connectors.util.internal.MappingConstants;
import org.apache.geode.distributed.ConfigurationPersistenceService;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static DescribeMappingCommand.RESULT_SECTION_NAME;


public class DescribeMappingCommandTest {
    public static final String COMMAND = "describe jdbc-mapping --region=region1";

    private DescribeMappingCommand command;

    @Mock
    ConfigurationPersistenceService configurationPersistenceService;

    @Mock
    CacheConfig clusterConfig;

    @Mock
    RegionConfig regionConfig;

    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    @Test
    public void requiredParameter() {
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, "describe jdbc-mapping").statusIsError().containsOutput("Invalid command");
    }

    @Test
    public void commandFailureWhenClusterConfigServiceNotEnabled() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(command.getConfigurationPersistenceService()).thenReturn(null);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsError().containsOutput("Cluster Configuration must be enabled.");
    }

    @Test
    public void commandFailureWhenClusterConfigServiceEnabledAndCacheConfigNotFound() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(configurationPersistenceService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(null);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsError().containsOutput("Cache Configuration not found.");
    }

    @Test
    public void commandFailureWhenClusterConfigServiceEnabledAndCacheConfigNotFoundWithGroup() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(configurationPersistenceService.getCacheConfig("group1")).thenReturn(null);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, ((DescribeMappingCommandTest.COMMAND) + " --group=group1")).statusIsError().containsOutput("Cache Configuration not found for group group1.");
    }

    @Test
    public void commandFailureWhenCacheConfigFoundAndRegionConfigNotFound() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        Mockito.when(clusterConfig.getRegions()).thenReturn(new ArrayList());
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsError().containsOutput("A region named region1 must already exist.");
    }

    @Test
    public void commandFailureWhenCacheConfigFoundAndRegionConfigNotFoundWithGroup() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        Mockito.when(clusterConfig.getRegions()).thenReturn(new ArrayList());
        Mockito.when(configurationPersistenceService.getCacheConfig("group1")).thenReturn(clusterConfig);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, ((DescribeMappingCommandTest.COMMAND) + " --groups=group1")).statusIsError().containsOutput("A region named region1 must already exist for group group1.");
    }

    @Test
    public void commandSuccessWhenClusterConfigFoundAndRegionConfigFound() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(elements);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsSuccess().containsOrderedOutput(((RESULT_SECTION_NAME) + "0"), MappingConstants.REGION_NAME, MappingConstants.PDX_NAME, MappingConstants.TABLE_NAME, MappingConstants.DATA_SOURCE_NAME, MappingConstants.SYNCHRONOUS_NAME, MappingConstants.ID_NAME, MappingConstants.CATALOG_NAME, MappingConstants.SCHEMA_NAME).containsOutput(MappingConstants.REGION_NAME, "region1").containsOutput(MappingConstants.DATA_SOURCE_NAME, "name1").containsOutput(MappingConstants.TABLE_NAME, "table1").containsOutput(MappingConstants.PDX_NAME, "class1").containsOutput(MappingConstants.ID_NAME, "myId").containsOutput(MappingConstants.SCHEMA_NAME, "mySchema").containsOutput(MappingConstants.CATALOG_NAME, "myCatalog").containsOutput("true");
    }

    @Test
    public void commandSuccessWhenClusterConfigFoundAndRegionConfigFoundAsync() {
        CacheConfig.AsyncEventQueue asyncEventQueue = Mockito.mock(AsyncEventQueue.class);
        ArrayList<CacheConfig.AsyncEventQueue> queueList = new ArrayList<>();
        // Adding multiple mocked objects to the list to demonstrate the ability to distinguish the
        // correct queue later on
        queueList.add(asyncEventQueue);
        queueList.add(asyncEventQueue);
        queueList.add(asyncEventQueue);
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(elements);
        Mockito.when(clusterConfig.getAsyncEventQueues()).thenReturn(queueList);
        Mockito.when(asyncEventQueue.getId()).thenReturn(MappingCommandUtils.createAsyncEventQueueName("region2")).thenReturn(MappingCommandUtils.createAsyncEventQueueName("region1")).thenReturn(MappingCommandUtils.createAsyncEventQueueName("region3"));
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsSuccess().containsOrderedOutput(((RESULT_SECTION_NAME) + "0"), MappingConstants.REGION_NAME, MappingConstants.PDX_NAME, MappingConstants.TABLE_NAME, MappingConstants.DATA_SOURCE_NAME, MappingConstants.SYNCHRONOUS_NAME, MappingConstants.ID_NAME, MappingConstants.CATALOG_NAME, MappingConstants.SCHEMA_NAME).containsOutput(MappingConstants.REGION_NAME, "region1").containsOutput(MappingConstants.DATA_SOURCE_NAME, "name1").containsOutput(MappingConstants.TABLE_NAME, "table1").containsOutput(MappingConstants.PDX_NAME, "class1").containsOutput(MappingConstants.ID_NAME, "myId").containsOutput(MappingConstants.SCHEMA_NAME, "mySchema").containsOutput(MappingConstants.CATALOG_NAME, "myCatalog").containsOutput("synchronous", "false");
    }

    @Test
    public void commandSuccessWithFieldMappings() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        FieldMapping fieldMapping = new FieldMapping("pdxName1", "pdxType1", "jdbcName1", "jdbcType1", true);
        regionMapping.addFieldMapping(fieldMapping);
        FieldMapping fieldMapping2 = new FieldMapping("veryLongpdxName2", "pdxType2", "veryLongjdbcName2", "jdbcType2", false);
        regionMapping.addFieldMapping(fieldMapping2);
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(elements);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsSuccess().containsOrderedOutput(((RESULT_SECTION_NAME) + "0"), MappingConstants.REGION_NAME, MappingConstants.PDX_NAME, MappingConstants.TABLE_NAME, MappingConstants.DATA_SOURCE_NAME, MappingConstants.SYNCHRONOUS_NAME, MappingConstants.ID_NAME, MappingConstants.CATALOG_NAME, MappingConstants.SCHEMA_NAME).containsOutput(MappingConstants.REGION_NAME, "region1").containsOutput(MappingConstants.DATA_SOURCE_NAME, "name1").containsOutput(MappingConstants.TABLE_NAME, "table1").containsOutput(MappingConstants.PDX_NAME, "class1").containsOutput(MappingConstants.ID_NAME, "myId").containsOutput(MappingConstants.SCHEMA_NAME, "mySchema").containsOutput(MappingConstants.CATALOG_NAME, "myCatalog").containsOutput("synchronous", "false").containsOutput("pdxName1", "pdxType1", "jdbcName1", "jdbcType1", "true").containsOutput("veryLongpdxName2", "pdxType2", "veryLongjdbcName2", "jdbcType2", "false");
    }

    @Test
    public void whenMemberExistsForGroup() {
        RegionMapping regionMapping = new RegionMapping();
        regionMapping.setRegionName("region1");
        regionMapping.setPdxName("class1");
        regionMapping.setTableName("table1");
        regionMapping.setDataSourceName("name1");
        regionMapping.setIds("myId");
        regionMapping.setCatalog("myCatalog");
        regionMapping.setSchema("mySchema");
        ArrayList<CacheElement> elements = new ArrayList<>();
        elements.add(regionMapping);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(elements);
        Mockito.when(configurationPersistenceService.getCacheConfig("group1")).thenReturn(clusterConfig);
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, ((DescribeMappingCommandTest.COMMAND) + " --groups=group1")).statusIsSuccess().containsOrderedOutput(((RESULT_SECTION_NAME) + "0"), MappingConstants.REGION_NAME, MappingConstants.PDX_NAME, MappingConstants.TABLE_NAME, MappingConstants.DATA_SOURCE_NAME, MappingConstants.SYNCHRONOUS_NAME, MappingConstants.ID_NAME, MappingConstants.CATALOG_NAME, MappingConstants.SCHEMA_NAME).containsOutput(MappingConstants.REGION_NAME, "region1").containsOutput(MappingConstants.DATA_SOURCE_NAME, "name1").containsOutput(MappingConstants.TABLE_NAME, "table1").containsOutput(MappingConstants.PDX_NAME, "class1").containsOutput(MappingConstants.ID_NAME, "myId").containsOutput(MappingConstants.SCHEMA_NAME, "mySchema").containsOutput(MappingConstants.CATALOG_NAME, "myCatalog").containsOutput("true");
    }

    @Test
    public void whenNoMappingFoundOnMember() {
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(new ArrayList());
        DescribeMappingCommandTest.gfsh.executeAndAssertThat(command, DescribeMappingCommandTest.COMMAND).statusIsError().containsOutput("mapping for region 'region1' not found");
    }
}

