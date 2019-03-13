/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.orchestration.internal.registry.config.service;


import ShardingPropertiesConstant.SQL_SHOW;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.core.rule.Authentication;
import org.apache.shardingsphere.orchestration.reg.api.RegistryCenter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class ConfigurationServiceTest {
    private static final String DATA_SOURCE_YAML = "ds_0: !!org.apache.shardingsphere.orchestration.yaml.config.YamlDataSourceConfiguration\n" + (((((((((((("  dataSourceClassName: org.apache.commons.dbcp2.BasicDataSource\n" + "  properties:\n") + "    driverClassName: com.mysql.jdbc.Driver\n") + "    url: jdbc:mysql://localhost:3306/ds_0\n") + "    username: root\n") + "    password: root\n") + "ds_1: !!org.apache.shardingsphere.orchestration.yaml.config.YamlDataSourceConfiguration\n") + "  dataSourceClassName: org.apache.commons.dbcp2.BasicDataSource\n") + "  properties:\n") + "    driverClassName: com.mysql.jdbc.Driver\n") + "    url: jdbc:mysql://localhost:3306/ds_1\n") + "    username: root\n") + "    password: root\n");

    private static final String DATA_SOURCE_PARAMETER_YAML = "ds_0: !!org.apache.shardingsphere.core.rule.DataSourceParameter\n" + (((((("  url: jdbc:mysql://localhost:3306/ds_0\n" + "  username: root\n") + "  password: root\n") + "ds_1: !!org.apache.shardingsphere.core.rule.DataSourceParameter\n") + "  url: jdbc:mysql://localhost:3306/ds_1\n") + "  username: root\n") + "  password: root\n");

    private static final String SHARDING_RULE_YAML = "tables:\n" + (((((("  t_order:\n" + "    actualDataNodes: ds_${0..1}.t_order_${0..1}\n") + "    logicTable: t_order\n") + "    tableStrategy:\n") + "      inline:\n") + "        algorithmExpression: t_order_${order_id % 2}\n") + "        shardingColumn: order_id\n");

    private static final String MASTER_SLAVE_RULE_YAML = "masterDataSourceName: master_ds\n" + ((("name: ms_ds\n" + "slaveDataSourceNames:\n") + "- slave_ds_0\n") + "- slave_ds_1\n");

    private static final String AUTHENTICATION_YAML = "password: root\n" + "username: root\n";

    private static final String PROPS_YAML = "sql.show: false\n";

    @Mock
    private RegistryCenter regCenter;

    @Test
    public void assertPersistConfigurationForShardingRuleWithoutAuthenticationAndIsNotOverwriteAndConfigurationIsExisted() {
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/datasource")).thenReturn(ConfigurationServiceTest.DATA_SOURCE_YAML);
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.when(regCenter.get("/test/config/props")).thenReturn(ConfigurationServiceTest.PROPS_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createShardingRuleConfiguration(), null, createProperties(), false);
        Mockito.verify(regCenter, Mockito.times(0)).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForShardingRuleWithoutAuthenticationAndIsNotOverwriteAndConfigurationIsNotExisted() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createShardingRuleConfiguration(), null, createProperties(), false);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForShardingRuleWithoutAuthenticationAndIsOverwrite() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createShardingRuleConfiguration(), null, createProperties(), true);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForMasterSlaveRuleWithoutAuthenticationAndIsNotOverwriteAndConfigurationIsExisted() {
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/datasource")).thenReturn(ConfigurationServiceTest.DATA_SOURCE_YAML);
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.when(regCenter.get("/test/config/props")).thenReturn(ConfigurationServiceTest.PROPS_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createMasterSlaveRuleConfiguration(), null, createProperties(), false);
        Mockito.verify(regCenter, Mockito.times(0)).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForMasterSlaveRuleWithoutAuthenticationAndIsNotOverwriteAndConfigurationIsNotExisted() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createMasterSlaveRuleConfiguration(), null, createProperties(), false);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForMasterSlaveRuleWithoutAuthenticationAndIsOverwrite() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createMasterSlaveRuleConfiguration(), null, createProperties(), true);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForShardingRuleWithAuthenticationAndIsNotOverwriteAndConfigurationIsExisted() {
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/datasource")).thenReturn(ConfigurationServiceTest.DATA_SOURCE_PARAMETER_YAML);
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.when(regCenter.get("/test/config/authentication")).thenReturn(ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.when(regCenter.get("/test/config/props")).thenReturn(ConfigurationServiceTest.PROPS_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createShardingRuleConfiguration(), new Authentication("root", "root"), createProperties(), false);
        Mockito.verify(regCenter, Mockito.times(0)).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/authentication", ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForShardingRuleWithAuthenticationAndIsNotOverwriteAndConfigurationIsNotExisted() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createShardingRuleConfiguration(), new Authentication("root", "root"), createProperties(), false);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/authentication", ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForShardingRuleWithAuthenticationAndIsOverwrite() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createShardingRuleConfiguration(), new Authentication("root", "root"), createProperties(), true);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.SHARDING_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/authentication", ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForMasterSlaveRuleWithAuthenticationAndIsNotOverwriteAndConfigurationIsExisted() {
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/datasource")).thenReturn(ConfigurationServiceTest.DATA_SOURCE_PARAMETER_YAML);
        Mockito.when(regCenter.get("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.when(regCenter.get("/test/config/authentication")).thenReturn(ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.when(regCenter.get("/test/config/props")).thenReturn(ConfigurationServiceTest.PROPS_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createMasterSlaveRuleConfiguration(), new Authentication("root", "root"), createProperties(), false);
        Mockito.verify(regCenter, Mockito.times(0)).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/authentication", ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.verify(regCenter, Mockito.times(0)).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForMasterSlaveRuleWithAuthenticationAndIsNotOverwriteAndConfigurationIsNotExisted() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createMasterSlaveRuleConfiguration(), new Authentication("root", "root"), createProperties(), false);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/authentication", ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertPersistConfigurationForMasterSlaveRuleWithAuthenticationAndIsOverwrite() {
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        configurationService.persistConfiguration("sharding_db", createDataSourceConfigurations(), createMasterSlaveRuleConfiguration(), new Authentication("root", "root"), createProperties(), true);
        Mockito.verify(regCenter).persist(ArgumentMatchers.eq("/test/config/schema/sharding_db/datasource"), ArgumentMatchers.<String>any());
        Mockito.verify(regCenter).persist("/test/config/schema/sharding_db/rule", ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        Mockito.verify(regCenter).persist("/test/config/authentication", ConfigurationServiceTest.AUTHENTICATION_YAML);
        Mockito.verify(regCenter).persist("/test/config/props", ConfigurationServiceTest.PROPS_YAML);
    }

    @Test
    public void assertLoadDataSourceConfigurations() {
        Mockito.when(regCenter.getDirectly("/test/config/schema/sharding_db/datasource")).thenReturn(ConfigurationServiceTest.DATA_SOURCE_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        Map<String, DataSourceConfiguration> actual = configurationService.loadDataSourceConfigurations("sharding_db");
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        assertDataSourceConfiguration(actual.get("ds_0"), createDataSourceConfiguration(createDataSource("ds_0")));
        assertDataSourceConfiguration(actual.get("ds_1"), createDataSourceConfiguration(createDataSource("ds_1")));
    }

    @Test
    public void assertIsShardingRule() {
        Mockito.when(regCenter.getDirectly("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.SHARDING_RULE_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        Assert.assertTrue(configurationService.isShardingRule("sharding_db"));
    }

    @Test
    public void assertIsNotShardingRule() {
        Mockito.when(regCenter.getDirectly("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        Assert.assertFalse(configurationService.isShardingRule("sharding_db"));
    }

    @Test
    public void assertLoadShardingRuleConfiguration() {
        Mockito.when(regCenter.getDirectly("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.SHARDING_RULE_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        ShardingRuleConfiguration actual = configurationService.loadShardingRuleConfiguration("sharding_db");
        Assert.assertThat(actual.getTableRuleConfigs().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getTableRuleConfigs().iterator().next().getLogicTable(), CoreMatchers.is("t_order"));
    }

    @Test
    public void assertLoadMasterSlaveRuleConfiguration() {
        Mockito.when(regCenter.getDirectly("/test/config/schema/sharding_db/rule")).thenReturn(ConfigurationServiceTest.MASTER_SLAVE_RULE_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        MasterSlaveRuleConfiguration actual = configurationService.loadMasterSlaveRuleConfiguration("sharding_db");
        Assert.assertThat(actual.getName(), CoreMatchers.is("ms_ds"));
    }

    @Test
    public void assertLoadAuthentication() {
        Mockito.when(regCenter.getDirectly("/test/config/authentication")).thenReturn(ConfigurationServiceTest.AUTHENTICATION_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        Authentication actual = configurationService.loadAuthentication();
        Assert.assertThat(actual.getUsername(), CoreMatchers.is("root"));
        Assert.assertThat(actual.getPassword(), CoreMatchers.is("root"));
    }

    @Test
    public void assertLoadProperties() {
        Mockito.when(regCenter.getDirectly("/test/config/props")).thenReturn(ConfigurationServiceTest.PROPS_YAML);
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        Properties actual = configurationService.loadProperties();
        Assert.assertThat(actual.get(SQL_SHOW.getKey()), CoreMatchers.<Object>is(Boolean.FALSE));
    }

    @Test
    public void assertGetAllShardingSchemaNames() {
        Mockito.when(regCenter.getChildrenKeys("/test/config/schema")).thenReturn(Arrays.asList("sharding_db", "masterslave_db"));
        ConfigurationService configurationService = new ConfigurationService("test", regCenter);
        Collection<String> actual = configurationService.getAllShardingSchemaNames();
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        Assert.assertThat(actual, CoreMatchers.hasItems("sharding_db"));
        Assert.assertThat(actual, CoreMatchers.hasItems("masterslave_db"));
    }
}

