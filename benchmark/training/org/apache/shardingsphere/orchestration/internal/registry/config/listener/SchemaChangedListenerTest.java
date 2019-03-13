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
package org.apache.shardingsphere.orchestration.internal.registry.config.listener;


import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.DataSourceChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.IgnoredShardingOrchestrationEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.MasterSlaveRuleChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.SchemaAddedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.SchemaDeletedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.ShardingRuleChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.listener.ShardingOrchestrationEvent;
import org.apache.shardingsphere.orchestration.reg.api.RegistryCenter;
import org.apache.shardingsphere.orchestration.reg.listener.DataChangedEvent;
import org.apache.shardingsphere.orchestration.reg.listener.DataChangedEvent.ChangedType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SchemaChangedListenerTest {
    private static final String DATA_SOURCE_YAML = "master_ds: !!org.apache.shardingsphere.orchestration.yaml.config.YamlDataSourceConfiguration\n" + (((("  dataSourceClassName: com.zaxxer.hikari.HikariDataSource\n" + "  properties:\n") + "    url: jdbc:mysql://localhost:3306/demo_ds_master\n") + "    username: root\n") + "    password: null\n");

    private static final String SHARDING_RULE_YAML = "tables:\n" + (((((("  t_order:\n" + "    logicTable: t_order\n") + "    actualDataNodes: ds_${0..1}.t_order_${0..1}\n") + "    tableStrategy:\n") + "      inline:\n") + "        algorithmExpression: t_order_${order_id % 2}\n") + "        shardingColumn: order_id");

    private static final String MASTER_SLAVE_RULE_YAML = "masterDataSourceName: master_ds\n" + ((("name: ms_ds\n" + "slaveDataSourceNames:\n") + "- slave_ds_0\n") + "- slave_ds_1\n");

    private SchemaChangedListener schemaChangedListener;

    @Mock
    private RegistryCenter regCenter;

    @Test
    public void assertCreateIgnoredEvent() {
        Assert.assertThat(schemaChangedListener.createShardingOrchestrationEvent(new DataChangedEvent("/test/config/schema/logic_db", "test", ChangedType.UPDATED)), CoreMatchers.instanceOf(IgnoredShardingOrchestrationEvent.class));
        Assert.assertThat(schemaChangedListener.createShardingOrchestrationEvent(new DataChangedEvent("/test/config/schema/logic_db/rule", "test", ChangedType.IGNORED)), CoreMatchers.instanceOf(IgnoredShardingOrchestrationEvent.class));
    }

    @Test
    public void assertCreateDataSourceChangedEventForExistedSchema() {
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/sharding_db/datasource", SchemaChangedListenerTest.DATA_SOURCE_YAML, ChangedType.UPDATED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(DataSourceChangedEvent.class));
        Assert.assertThat(getShardingSchemaName(), CoreMatchers.is("sharding_db"));
    }

    @Test
    public void assertCreateShardingRuleChangedEventForExistedSchema() {
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/sharding_db/rule", SchemaChangedListenerTest.SHARDING_RULE_YAML, ChangedType.UPDATED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(ShardingRuleChangedEvent.class));
        Assert.assertThat(getShardingSchemaName(), CoreMatchers.is("sharding_db"));
        Assert.assertThat(getShardingRuleConfiguration().getTableRuleConfigs().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertCreateMasterSlaveRuleChangedEventForExistedSchema() {
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/masterslave_db/rule", SchemaChangedListenerTest.MASTER_SLAVE_RULE_YAML, ChangedType.UPDATED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(MasterSlaveRuleChangedEvent.class));
        Assert.assertThat(getShardingSchemaName(), CoreMatchers.is("masterslave_db"));
        Assert.assertThat(getMasterSlaveRuleConfiguration().getMasterDataSourceName(), CoreMatchers.is("master_ds"));
    }

    @Test
    public void assertCreateIgnoredShardingOrchestrationEventForNewSchema() {
        Mockito.when(regCenter.get("/test/config/schema/logic_db/datasource")).thenReturn("");
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/logic_db/rule", "rule", ChangedType.UPDATED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(IgnoredShardingOrchestrationEvent.class));
    }

    @Test
    public void assertCreateShardingSchemaAddedEventForNewSchema() {
        Mockito.when(regCenter.get("/test/config/schema/logic_db/rule")).thenReturn(SchemaChangedListenerTest.SHARDING_RULE_YAML);
        Mockito.when(regCenter.get("/test/config/schema/logic_db/datasource")).thenReturn(SchemaChangedListenerTest.DATA_SOURCE_YAML);
        Mockito.when(regCenter.getDirectly("/test/config/schema/logic_db/rule")).thenReturn(SchemaChangedListenerTest.SHARDING_RULE_YAML);
        Mockito.when(regCenter.getDirectly("/test/config/schema/logic_db/datasource")).thenReturn(SchemaChangedListenerTest.DATA_SOURCE_YAML);
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/logic_db/datasource", SchemaChangedListenerTest.DATA_SOURCE_YAML, ChangedType.UPDATED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(SchemaAddedEvent.class));
        Assert.assertThat(getRuleConfiguration(), CoreMatchers.instanceOf(ShardingRuleConfiguration.class));
    }

    @Test
    public void assertCreateMasterSlaveSchemaAddedEventForNewSchema() {
        Mockito.when(regCenter.get("/test/config/schema/logic_db/rule")).thenReturn(SchemaChangedListenerTest.MASTER_SLAVE_RULE_YAML);
        Mockito.when(regCenter.get("/test/config/schema/logic_db/datasource")).thenReturn(SchemaChangedListenerTest.DATA_SOURCE_YAML);
        Mockito.when(regCenter.getDirectly("/test/config/schema/logic_db/rule")).thenReturn(SchemaChangedListenerTest.MASTER_SLAVE_RULE_YAML);
        Mockito.when(regCenter.getDirectly("/test/config/schema/logic_db/datasource")).thenReturn(SchemaChangedListenerTest.DATA_SOURCE_YAML);
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/logic_db/datasource", SchemaChangedListenerTest.DATA_SOURCE_YAML, ChangedType.UPDATED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(SchemaAddedEvent.class));
        Assert.assertThat(getRuleConfiguration(), CoreMatchers.instanceOf(MasterSlaveRuleConfiguration.class));
    }

    @Test
    public void assertCreateSchemaDeletedEventForNewSchema() {
        DataChangedEvent dataChangedEvent = new DataChangedEvent("/test/config/schema/logic_db/datasource", SchemaChangedListenerTest.DATA_SOURCE_YAML, ChangedType.DELETED);
        ShardingOrchestrationEvent actual = schemaChangedListener.createShardingOrchestrationEvent(dataChangedEvent);
        Assert.assertThat(actual, CoreMatchers.instanceOf(SchemaDeletedEvent.class));
        Assert.assertThat(getShardingSchemaName(), CoreMatchers.is("logic_db"));
    }
}

