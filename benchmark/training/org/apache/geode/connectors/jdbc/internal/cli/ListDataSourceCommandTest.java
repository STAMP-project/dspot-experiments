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


import DATASOURCE_TYPE.MANAGED;
import DATASOURCE_TYPE.POOLED;
import DATASOURCE_TYPE.SIMPLE;
import ListDataSourceCommand.DATA_SOURCE_PROPERTIES_SECTION;
import Status.ERROR;
import Status.OK;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.JndiBindingsType;
import org.apache.geode.cache.configuration.JndiBindingsType.JndiBinding;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.management.internal.cli.result.model.TabularResultModel;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ListDataSourceCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private ListDataSourceCommand command;

    private JndiBinding binding;

    private List<JndiBindingsType.JndiBinding> bindings;

    private InternalConfigurationPersistenceService clusterConfigService;

    private CacheConfig cacheConfig;

    private List<RegionConfig> regionConfigs;

    private static String COMMAND = "list data-source";

    private static String DATA_SOURCE_NAME = "myDataSource";

    @Test
    public void noArgsReturnsSuccess() {
        ListDataSourceCommandTest.gfsh.executeAndAssertThat(command, ListDataSourceCommandTest.COMMAND).statusIsSuccess();
    }

    @Test
    public void listDataSourceWithNoClusterConfigurationServerFails() {
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        ResultModel result = command.listDataSources();
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.toString()).contains("Cluster configuration service must be enabled.");
    }

    @Test
    public void listDataSourceWithNoClusterConfigIsOkWithNoDataSources() {
        Mockito.doReturn(null).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        ResultModel result = command.listDataSources();
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.toString()).contains("No data sources found");
    }

    @Test
    public void listDataSourceWithUnsupportedTypeIgnoresIt() {
        binding.setType(MANAGED.getType());
        ResultModel result = command.listDataSources();
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.toString()).doesNotContain(ListDataSourceCommandTest.DATA_SOURCE_NAME);
    }

    @Test
    public void listDataSourceWithSimpleTypeReturnsPooledFalse() {
        binding.setType(SIMPLE.getType());
        ResultModel result = command.listDataSources();
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList(ListDataSourceCommandTest.DATA_SOURCE_NAME, "false", "false", "myURL"));
    }

    @Test
    public void listDataSourceWithPooledTypeReturnsPooledTrue() {
        binding.setType(POOLED.getType());
        ResultModel result = command.listDataSources();
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList(ListDataSourceCommandTest.DATA_SOURCE_NAME, "true", "false", "myURL"));
    }

    @Test
    public void listDataSourcesReturnsInfoOnSingleDataSource() {
        ResultModel result = command.listDataSources();
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList(ListDataSourceCommandTest.DATA_SOURCE_NAME, "true", "false", "myURL"));
    }

    @Test
    public void listDataSourcesReturnsInfoOnMultipleDataSources() {
        JndiBinding binding2 = new JndiBindingsType.JndiBinding();
        binding2.setJndiName("myDataSource2");
        binding2.setType(SIMPLE.getType());
        binding2.setConnectionUrl("myURL2");
        bindings.add(binding2);
        ResultModel result = command.listDataSources();
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList(ListDataSourceCommandTest.DATA_SOURCE_NAME, "true", "false", "myURL"));
        assertThat(section.getValuesInRow(1)).isEqualTo(Arrays.asList("myDataSource2", "false", "false", "myURL2"));
    }

    @Test
    public void isDataSourceUsedByRegionGivenNoRegionsReturnsFalse() {
        regionConfigs.clear();
        boolean result = command.isDataSourceUsedByRegion(cacheConfig, "");
        assertThat(result).isFalse();
    }

    @Test
    public void isDataSourceUsedByRegionGivenRegionConfigWithNoCustomRegionElementsReturnsFalse() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.emptyList());
        regionConfigs.add(regionConfig);
        boolean result = command.isDataSourceUsedByRegion(cacheConfig, "");
        assertThat(result).isFalse();
    }

    @Test
    public void isDataSourceUsedByRegionGivenRegionConfigWithNonRegionMappingElementReturnsFalse() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(Mockito.mock(CacheElement.class)));
        regionConfigs.add(regionConfig);
        boolean result = command.isDataSourceUsedByRegion(cacheConfig, "");
        assertThat(result).isFalse();
    }

    @Test
    public void isDataSourceUsedByRegionGivenRegionConfigWithRegionMappingForOtherDataSourceReturnsFalse() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(Mockito.mock(RegionMapping.class)));
        regionConfigs.add(regionConfig);
        boolean result = command.isDataSourceUsedByRegion(cacheConfig, "bogusDataSource");
        assertThat(result).isFalse();
    }

    @Test
    public void isDataSourceUsedByRegionGivenRegionConfigWithRegionMappingForDataSourceReturnsTrue() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getName()).thenReturn("regionName");
        RegionMapping regionMapping = Mockito.mock(RegionMapping.class);
        Mockito.when(regionMapping.getDataSourceName()).thenReturn("dataSourceName");
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(regionMapping));
        regionConfigs.add(regionConfig);
        boolean result = command.isDataSourceUsedByRegion(cacheConfig, "dataSourceName");
        assertThat(result).isTrue();
    }

    @Test
    public void isDataSourceUsedByRegionGivenMultipleRegionConfigsReturnsTrue() {
        RegionMapping regionMapping;
        {
            RegionConfig regionConfig1 = Mockito.mock(RegionConfig.class, "regionConfig1");
            Mockito.when(regionConfig1.getName()).thenReturn("regionName1");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping1");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn("dataSourceName");
            Mockito.when(regionConfig1.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig1);
        }
        {
            RegionConfig regionConfig2 = Mockito.mock(RegionConfig.class, "regionConfig2");
            Mockito.when(regionConfig2.getName()).thenReturn("regionName2");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping2");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn("otherDataSourceName");
            Mockito.when(regionConfig2.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig2);
        }
        {
            RegionConfig regionConfig3 = Mockito.mock(RegionConfig.class, "regionConfig3");
            Mockito.when(regionConfig3.getName()).thenReturn("regionName3");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping3");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn("dataSourceName");
            Mockito.when(regionConfig3.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig3);
        }
        boolean result = command.isDataSourceUsedByRegion(cacheConfig, "dataSourceName");
        assertThat(result).isTrue();
    }

    @Test
    public void listDataSourcesWithNoRegionsUsingItReturnsResultWithInUseFalse() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(Mockito.mock(RegionMapping.class)));
        regionConfigs.add(regionConfig);
        ResultModel result = command.listDataSources();
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList(ListDataSourceCommandTest.DATA_SOURCE_NAME, "true", "false", "myURL"));
    }

    @Test
    public void listDataSourcesWithRegionsUsingItReturnsResultWithInUseTrue() {
        RegionMapping regionMapping;
        {
            RegionConfig regionConfig1 = Mockito.mock(RegionConfig.class, "regionConfig1");
            Mockito.when(regionConfig1.getName()).thenReturn("regionName1");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping1");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn(ListDataSourceCommandTest.DATA_SOURCE_NAME);
            Mockito.when(regionConfig1.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig1);
        }
        {
            RegionConfig regionConfig2 = Mockito.mock(RegionConfig.class, "regionConfig2");
            Mockito.when(regionConfig2.getName()).thenReturn("regionName2");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping2");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn("otherDataSourceName");
            Mockito.when(regionConfig2.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig2);
        }
        {
            RegionConfig regionConfig3 = Mockito.mock(RegionConfig.class, "regionConfig3");
            Mockito.when(regionConfig3.getName()).thenReturn("regionName3");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping3");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn(ListDataSourceCommandTest.DATA_SOURCE_NAME);
            Mockito.when(regionConfig3.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig3);
        }
        ResultModel result = command.listDataSources();
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList(ListDataSourceCommandTest.DATA_SOURCE_NAME, "true", "true", "myURL"));
    }
}

