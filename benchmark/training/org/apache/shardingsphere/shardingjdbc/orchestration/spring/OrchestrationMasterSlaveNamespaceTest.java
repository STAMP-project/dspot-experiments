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
package org.apache.shardingsphere.shardingjdbc.orchestration.spring;


import ShardingPropertiesConstant.SQL_SHOW;
import org.apache.shardingsphere.core.rule.MasterSlaveRule;
import org.apache.shardingsphere.core.strategy.masterslave.RandomMasterSlaveLoadBalanceAlgorithm;
import org.apache.shardingsphere.core.strategy.masterslave.RoundRobinMasterSlaveLoadBalanceAlgorithm;
import org.apache.shardingsphere.shardingjdbc.orchestration.spring.datasource.OrchestrationSpringMasterSlaveDataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = "classpath:META-INF/rdb/masterSlaveOrchestration.xml")
public class OrchestrationMasterSlaveNamespaceTest extends AbstractJUnit4SpringContextTests {
    @Test
    public void assertMasterSlaveDataSourceType() {
        Assert.assertTrue((null != (applicationContext.getBean("defaultMasterSlaveDataSourceOrchestration", OrchestrationSpringMasterSlaveDataSource.class))));
    }

    @Test
    public void assertDefaultMaserSlaveDataSource() {
        MasterSlaveRule masterSlaveRule = getMasterSlaveRule("defaultMasterSlaveDataSourceOrchestration");
        Assert.assertThat(masterSlaveRule.getMasterDataSourceName(), CoreMatchers.is("dbtbl_0_master"));
        Assert.assertTrue(masterSlaveRule.getSlaveDataSourceNames().contains("dbtbl_0_slave_0"));
        Assert.assertTrue(masterSlaveRule.getSlaveDataSourceNames().contains("dbtbl_0_slave_1"));
    }

    @Test
    public void assertTypeMasterSlaveDataSource() {
        MasterSlaveRule randomSlaveRule = getMasterSlaveRule("randomMasterSlaveDataSourceOrchestration");
        MasterSlaveRule roundRobinSlaveRule = getMasterSlaveRule("roundRobinMasterSlaveDataSourceOrchestration");
        Assert.assertTrue(((randomSlaveRule.getLoadBalanceAlgorithm()) instanceof RandomMasterSlaveLoadBalanceAlgorithm));
        Assert.assertTrue(((roundRobinSlaveRule.getLoadBalanceAlgorithm()) instanceof RoundRobinMasterSlaveLoadBalanceAlgorithm));
    }

    @Test
    public void assertProperties() {
        boolean showSQL = getShardingProperties("defaultMasterSlaveDataSourceOrchestration").getValue(SQL_SHOW);
        Assert.assertTrue(showSQL);
    }
}

