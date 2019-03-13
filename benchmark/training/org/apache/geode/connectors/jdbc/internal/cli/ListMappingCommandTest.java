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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.distributed.ConfigurationPersistenceService;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ListMappingCommandTest {
    final String TEST_GROUP1 = "testGroup1";

    public final String COMMAND = "list jdbc-mappings";

    public final String COMMAND_FOR_GROUP = ((COMMAND) + " --groups=") + (TEST_GROUP1);

    private ListMappingCommand command;

    private ConfigurationPersistenceService configService = Mockito.mock(ConfigurationPersistenceService.class);

    private CacheConfig cacheConfig = Mockito.mock(CacheConfig.class);

    RegionConfig region1Config = Mockito.mock(RegionConfig.class);

    RegionConfig region2Config = Mockito.mock(RegionConfig.class);

    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    @Test
    public void whenNoCacheConfig() {
        Mockito.when(configService.getCacheConfig(CLUSTER_CONFIG)).thenReturn(null);
        ListMappingCommandTest.gfsh.executeAndAssertThat(command, COMMAND).statusIsError().containsOutput("Cache Configuration not found.");
    }

    @Test
    public void whenClusterConfigDisabled() throws PreconditionException {
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        ListMappingCommandTest.gfsh.executeAndAssertThat(command, COMMAND).statusIsError().containsOutput("Cluster Configuration must be enabled.");
    }

    @Test
    public void whenNoMappingExists() {
        Mockito.when(configService.getCacheConfig(ArgumentMatchers.eq(CLUSTER_CONFIG))).thenReturn(cacheConfig);
        ListMappingCommandTest.gfsh.executeAndAssertThat(command, COMMAND).statusIsSuccess().containsOutput(ListMappingCommand.NO_MAPPINGS_FOUND);
    }

    @Test
    public void whenMappingExists() {
        Mockito.when(configService.getCacheConfig(ArgumentMatchers.eq(CLUSTER_CONFIG))).thenReturn(cacheConfig);
        List<RegionConfig> regions = new ArrayList<RegionConfig>();
        regions.add(region1Config);
        regions.add(region2Config);
        Mockito.when(region1Config.getName()).thenReturn("region1");
        Mockito.when(region2Config.getName()).thenReturn("region2");
        Mockito.when(cacheConfig.getRegions()).thenReturn(regions);
        RegionMapping mapping1 = new RegionMapping("region1", "class1", "table1", "name1", null, null, null);
        RegionMapping mapping2 = new RegionMapping("region2", "class2", "table2", "name2", null, null, null);
        List<CacheElement> mappingList1 = new ArrayList<CacheElement>();
        mappingList1.add(mapping1);
        List<CacheElement> mappingList2 = new ArrayList<CacheElement>();
        mappingList2.add(mapping2);
        Mockito.when(region1Config.getCustomRegionElements()).thenReturn(mappingList1);
        Mockito.when(region2Config.getCustomRegionElements()).thenReturn(mappingList2);
        ResultCollector rc = Mockito.mock(ResultCollector.class);
        Mockito.doReturn(rc).when(command).executeFunction(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Set.class));
        Mockito.when(rc.getResult()).thenReturn(Collections.singletonList(new org.apache.geode.management.internal.cli.functions.CliFunctionResult("server-1", Stream.of(mapping1, mapping2).collect(Collectors.toSet()), "success")));
        ListMappingCommandTest.gfsh.executeAndAssertThat(command, COMMAND).statusIsSuccess().containsOutput("region1", "region2");
    }

    @Test
    public void whenMappingExistsForServerGroup() {
        Mockito.when(configService.getCacheConfig(TEST_GROUP1)).thenReturn(cacheConfig);
        List<RegionConfig> regions = new ArrayList<RegionConfig>();
        regions.add(region1Config);
        regions.add(region2Config);
        Mockito.when(region1Config.getName()).thenReturn("region1");
        Mockito.when(region2Config.getName()).thenReturn("region2");
        Mockito.when(cacheConfig.getRegions()).thenReturn(regions);
        RegionMapping mapping1 = new RegionMapping("region1", "class1", "table1", "name1", null, null, null);
        RegionMapping mapping2 = new RegionMapping("region2", "class2", "table2", "name2", null, null, null);
        List<CacheElement> mappingList1 = new ArrayList<CacheElement>();
        mappingList1.add(mapping1);
        List<CacheElement> mappingList2 = new ArrayList<CacheElement>();
        mappingList2.add(mapping2);
        Mockito.when(region1Config.getCustomRegionElements()).thenReturn(mappingList1);
        Mockito.when(region2Config.getCustomRegionElements()).thenReturn(mappingList2);
        ResultCollector rc = Mockito.mock(ResultCollector.class);
        Mockito.doReturn(rc).when(command).executeFunction(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(Set.class));
        Mockito.when(rc.getResult()).thenReturn(Collections.singletonList(new org.apache.geode.management.internal.cli.functions.CliFunctionResult("server-1", Stream.of(mapping1, mapping2).collect(Collectors.toSet()), "success")));
        ListMappingCommandTest.gfsh.executeAndAssertThat(command, COMMAND_FOR_GROUP).statusIsSuccess().containsOutput("region1", "region2");
    }
}

