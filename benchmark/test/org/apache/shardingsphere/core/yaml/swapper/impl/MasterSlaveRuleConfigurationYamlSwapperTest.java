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
package org.apache.shardingsphere.core.yaml.swapper.impl;


import java.util.Collection;
import java.util.Collections;
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.core.yaml.config.masterslave.YamlMasterSlaveRuleConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MasterSlaveRuleConfigurationYamlSwapperTest {
    @Test
    public void assertSwapToYamlWithLoadBalanceAlgorithm() {
        YamlMasterSlaveRuleConfiguration actual = new MasterSlaveRuleConfigurationYamlSwapper().swap(new MasterSlaveRuleConfiguration("ds", "master", Collections.singletonList("slave"), new LoadBalanceStrategyConfiguration("ROUND_ROBIN")));
        Assert.assertThat(actual.getName(), CoreMatchers.is("ds"));
        Assert.assertThat(actual.getMasterDataSourceName(), CoreMatchers.is("master"));
        Assert.assertThat(actual.getSlaveDataSourceNames(), CoreMatchers.<Collection<String>>is(Collections.singletonList("slave")));
        Assert.assertThat(actual.getLoadBalanceAlgorithmType(), CoreMatchers.is("ROUND_ROBIN"));
    }

    @Test
    public void assertSwapToYamlWithoutLoadBalanceAlgorithm() {
        YamlMasterSlaveRuleConfiguration actual = new MasterSlaveRuleConfigurationYamlSwapper().swap(new MasterSlaveRuleConfiguration("ds", "master", Collections.singletonList("slave")));
        Assert.assertThat(actual.getName(), CoreMatchers.is("ds"));
        Assert.assertThat(actual.getMasterDataSourceName(), CoreMatchers.is("master"));
        Assert.assertThat(actual.getSlaveDataSourceNames(), CoreMatchers.<Collection<String>>is(Collections.singletonList("slave")));
        Assert.assertNull(actual.getLoadBalanceAlgorithmType());
    }

    @Test
    public void assertSwapToObjectWithLoadBalanceAlgorithmType() {
        YamlMasterSlaveRuleConfiguration yamlConfiguration = createYamlMasterSlaveRuleConfiguration();
        yamlConfiguration.setLoadBalanceAlgorithmType("RANDOM");
        MasterSlaveRuleConfiguration actual = new MasterSlaveRuleConfigurationYamlSwapper().swap(yamlConfiguration);
        assertMasterSlaveRuleConfiguration(actual);
        Assert.assertThat(actual.getLoadBalanceStrategyConfiguration().getType(), CoreMatchers.is("RANDOM"));
    }

    @Test
    public void assertSwapToObjectWithoutLoadBalanceAlgorithm() {
        YamlMasterSlaveRuleConfiguration yamlConfiguration = createYamlMasterSlaveRuleConfiguration();
        MasterSlaveRuleConfiguration actual = new MasterSlaveRuleConfigurationYamlSwapper().swap(yamlConfiguration);
        assertMasterSlaveRuleConfiguration(actual);
        Assert.assertNull(actual.getLoadBalanceStrategyConfiguration());
    }
}

