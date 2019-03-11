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
import DescribeDataSourceCommand.DATA_SOURCE_PROPERTIES_SECTION;
import DescribeDataSourceCommand.REGIONS_USING_DATA_SOURCE_SECTION;
import JndiBindingsType.JndiBinding;
import Status.ERROR;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.cache.configuration.CacheElement;
import org.apache.geode.cache.configuration.JndiBindingsType;
import org.apache.geode.cache.configuration.JndiBindingsType.JndiBinding.ConfigProperty;
import org.apache.geode.cache.configuration.RegionConfig;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.distributed.internal.InternalConfigurationPersistenceService;
import org.apache.geode.management.internal.cli.result.model.InfoResultModel;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.management.internal.cli.result.model.TabularResultModel;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DescribeDataSourceCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private DescribeDataSourceCommand command;

    private JndiBinding binding;

    private List<JndiBindingsType.JndiBinding> bindings;

    private InternalConfigurationPersistenceService clusterConfigService;

    private CacheConfig cacheConfig;

    private List<RegionConfig> regionConfigs;

    private static String COMMAND = "describe data-source";

    private static String DATA_SOURCE_NAME = "myDataSource";

    @Test
    public void missingMandatory() {
        DescribeDataSourceCommandTest.gfsh.executeAndAssertThat(command, DescribeDataSourceCommandTest.COMMAND).statusIsError().containsOutput("Invalid command: describe data-source");
    }

    @Test
    public void nameWorks() {
        DescribeDataSourceCommandTest.gfsh.executeAndAssertThat(command, (((DescribeDataSourceCommandTest.COMMAND) + " --name=") + (DescribeDataSourceCommandTest.DATA_SOURCE_NAME))).statusIsSuccess();
    }

    @Test
    public void describeDataSourceWithNoClusterConfigurationServerFails() {
        Mockito.doReturn(null).when(command).getConfigurationPersistenceService();
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.toString()).contains("Cluster configuration service must be enabled.");
    }

    @Test
    public void describeDataSourceWithNoClusterConfigFails() {
        Mockito.doReturn(null).when(clusterConfigService).getCacheConfig(ArgumentMatchers.any());
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.toString()).contains((("Data source: " + (DescribeDataSourceCommandTest.DATA_SOURCE_NAME)) + " not found"));
    }

    @Test
    public void describeDataSourceWithWrongNameFails() {
        ResultModel result = command.describeDataSource("bogusName");
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.toString()).contains("Data source: bogusName not found");
    }

    @Test
    public void describeDataSourceWithUnsupportedTypeFails() {
        binding.setType(MANAGED.getType());
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        assertThat(result.getStatus()).isEqualTo(ERROR);
        assertThat(result.toString()).contains("Unknown data source type: ManagedDataSource");
    }

    @Test
    public void describeDataSourceWithSimpleTypeReturnsPooledFalse() {
        binding.setType(SIMPLE.getType());
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(3)).isEqualTo(Arrays.asList("pooled", "false"));
    }

    @Test
    public void describeDataSourceWithPooledTypeReturnsPooledTrue() {
        binding.setType(POOLED.getType());
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(3)).isEqualTo(Arrays.asList("pooled", "true"));
    }

    @Test
    public void describeDataSourceTypeReturnsName() {
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(0)).isEqualTo(Arrays.asList("name", DescribeDataSourceCommandTest.DATA_SOURCE_NAME));
    }

    @Test
    public void describeDataSourceWithUrlReturnsUrl() {
        binding.setConnectionUrl("myUrl");
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(1)).isEqualTo(Arrays.asList("url", "myUrl"));
    }

    @Test
    public void describeDataSourceWithUsernameReturnsUsername() {
        binding.setUserName("myUserName");
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(2)).isEqualTo(Arrays.asList("username", "myUserName"));
    }

    @Test
    public void describeDataSourceWithPooledDataSourceFactoryClassShowsItInTheResult() {
        binding.setType(POOLED.getType());
        binding.setConnPooledDatasourceClass("myPooledDataSourceFactoryClass");
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(4)).isEqualTo(Arrays.asList("pooled-data-source-factory-class", "myPooledDataSourceFactoryClass"));
    }

    @Test
    public void describeDataSourceWithPasswordDoesNotShowPasswordInResult() {
        binding.setType(POOLED.getType());
        binding.setPassword("myPassword");
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        assertThat(result.toString()).doesNotContain("myPassword");
    }

    @Test
    public void describeDataSourceWithPoolPropertiesDoesNotShowsItInTheResult() {
        binding.setType(SIMPLE.getType());
        List<ConfigProperty> configProperties = binding.getConfigProperties();
        configProperties.add(new ConfigProperty("name1", "value1"));
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        assertThat(result.toString()).doesNotContain("name1");
        assertThat(result.toString()).doesNotContain("value1");
    }

    @Test
    public void describeDataSourceWithPoolPropertiesShowsItInTheResult() {
        binding.setType(POOLED.getType());
        List<ConfigProperty> configProperties = binding.getConfigProperties();
        configProperties.add(new ConfigProperty("name1", "value1"));
        configProperties.add(new ConfigProperty("name2", "value2"));
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        TabularResultModel section = result.getTableSection(DATA_SOURCE_PROPERTIES_SECTION);
        assertThat(section.getValuesInRow(5)).isEqualTo(Arrays.asList("name1", "value1"));
        assertThat(section.getValuesInRow(6)).isEqualTo(Arrays.asList("name2", "value2"));
    }

    @Test
    public void getRegionsThatUseDataSourceGivenNoRegionsReturnsEmptyList() {
        regionConfigs.clear();
        List<String> result = command.getRegionsThatUseDataSource(cacheConfig, "");
        assertThat(result).isEmpty();
    }

    @Test
    public void getRegionsThatUseDataSourceGivenRegionConfigWithNoCustomRegionElementsReturnsEmptyList() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.emptyList());
        regionConfigs.add(regionConfig);
        List<String> result = command.getRegionsThatUseDataSource(cacheConfig, "");
        assertThat(result).isEmpty();
    }

    @Test
    public void getRegionsThatUseDataSourceGivenRegionConfigWithNonRegionMappingElementReturnsEmptyList() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(Mockito.mock(CacheElement.class)));
        regionConfigs.add(regionConfig);
        List<String> result = command.getRegionsThatUseDataSource(cacheConfig, "");
        assertThat(result).isEmpty();
    }

    @Test
    public void getRegionsThatUseDataSourceGivenRegionConfigWithRegionMappingForOtherDataSourceReturnsEmptyList() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(Mockito.mock(RegionMapping.class)));
        regionConfigs.add(regionConfig);
        List<String> result = command.getRegionsThatUseDataSource(cacheConfig, "bogusDataSource");
        assertThat(result).isEmpty();
    }

    @Test
    public void getRegionsThatUseDataSourceGivenRegionConfigWithRegionMappingForDataSourceReturnsRegionName() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getName()).thenReturn("regionName");
        RegionMapping regionMapping = Mockito.mock(RegionMapping.class);
        Mockito.when(regionMapping.getDataSourceName()).thenReturn("dataSourceName");
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(regionMapping));
        regionConfigs.add(regionConfig);
        List<String> result = command.getRegionsThatUseDataSource(cacheConfig, "dataSourceName");
        assertThat(result).isEqualTo(Collections.singletonList("regionName"));
    }

    @Test
    public void getRegionsThatUseDataSourceGivenMultipleRegionConfigsReturnsAllRegionNames() {
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
        List<String> result = command.getRegionsThatUseDataSource(cacheConfig, "dataSourceName");
        assertThat(result).isEqualTo(Arrays.asList("regionName1", "regionName3"));
    }

    @Test
    public void describeDataSourceWithNoRegionsUsingItReturnsResultWithNoRegionsUsingIt() {
        RegionConfig regionConfig = Mockito.mock(RegionConfig.class);
        Mockito.when(regionConfig.getCustomRegionElements()).thenReturn(Collections.singletonList(Mockito.mock(RegionMapping.class)));
        regionConfigs.add(regionConfig);
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        InfoResultModel regionsUsingSection = ((InfoResultModel) (result.getSection(REGIONS_USING_DATA_SOURCE_SECTION)));
        assertThat(regionsUsingSection.getContent()).isEqualTo(Arrays.asList(("no regions are using " + (DescribeDataSourceCommandTest.DATA_SOURCE_NAME))));
    }

    @Test
    public void describeDataSourceWithRegionsUsingItReturnsResultWithRegionNames() {
        RegionMapping regionMapping;
        {
            RegionConfig regionConfig1 = Mockito.mock(RegionConfig.class, "regionConfig1");
            Mockito.when(regionConfig1.getName()).thenReturn("regionName1");
            regionMapping = Mockito.mock(RegionMapping.class, "regionMapping1");
            Mockito.when(regionMapping.getDataSourceName()).thenReturn(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
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
            Mockito.when(regionMapping.getDataSourceName()).thenReturn(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
            Mockito.when(regionConfig3.getCustomRegionElements()).thenReturn(Arrays.asList(regionMapping));
            regionConfigs.add(regionConfig3);
        }
        ResultModel result = command.describeDataSource(DescribeDataSourceCommandTest.DATA_SOURCE_NAME);
        InfoResultModel regionsUsingSection = ((InfoResultModel) (result.getSection(REGIONS_USING_DATA_SOURCE_SECTION)));
        assertThat(regionsUsingSection.getContent()).isEqualTo(Arrays.asList("regionName1", "regionName3"));
    }
}

