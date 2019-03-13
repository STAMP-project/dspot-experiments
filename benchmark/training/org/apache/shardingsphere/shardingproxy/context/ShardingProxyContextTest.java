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
package org.apache.shardingsphere.shardingproxy.context;


import ShardingPropertiesConstant.PROXY_TRANSACTION_TYPE;
import ShardingPropertiesConstant.SQL_SHOW;
import java.util.Properties;
import org.apache.shardingsphere.core.rule.Authentication;
import org.apache.shardingsphere.orchestration.internal.eventbus.ShardingOrchestrationEventBus;
import org.apache.shardingsphere.orchestration.internal.registry.config.event.PropertiesChangedEvent;
import org.apache.shardingsphere.orchestration.internal.registry.state.event.CircuitStateChangedEvent;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingProxyContextTest {
    @Test
    public void assertInit() {
        Authentication authentication = new Authentication("root", "root");
        Properties props = new Properties();
        props.setProperty(SQL_SHOW.getKey(), Boolean.TRUE.toString());
        props.setProperty(PROXY_TRANSACTION_TYPE.getKey(), "BASE");
        ShardingProxyContext.getInstance().init(authentication, props);
        Assert.assertThat(ShardingProxyContext.getInstance().getAuthentication(), CoreMatchers.is(authentication));
        Assert.assertTrue(ShardingProxyContext.getInstance().getShardingProperties().<Boolean>getValue(SQL_SHOW));
        Assert.assertThat(ShardingProxyContext.getInstance().getShardingProperties().<String>getValue(PROXY_TRANSACTION_TYPE), CoreMatchers.is("BASE"));
    }

    @Test
    public void assertRenewShardingProperties() {
        ShardingProxyContext.getInstance().init(new Authentication("root", "root"), new Properties());
        Assert.assertTrue(ShardingProxyContext.getInstance().getShardingProperties().getProps().isEmpty());
        Properties props = new Properties();
        props.setProperty(SQL_SHOW.getKey(), Boolean.TRUE.toString());
        ShardingOrchestrationEventBus.getInstance().post(new PropertiesChangedEvent(props));
        Assert.assertFalse(ShardingProxyContext.getInstance().getShardingProperties().getProps().isEmpty());
    }

    @Test
    public void assertRenewAuthentication() {
        ShardingProxyContext.getInstance().init(new Authentication("root", "root"), new Properties());
        ShardingOrchestrationEventBus.getInstance().post(new org.apache.shardingsphere.orchestration.internal.registry.config.event.AuthenticationChangedEvent(new Authentication("user", "pwd")));
        Assert.assertThat(ShardingProxyContext.getInstance().getAuthentication().getUsername(), CoreMatchers.is("user"));
        Assert.assertThat(ShardingProxyContext.getInstance().getAuthentication().getPassword(), CoreMatchers.is("pwd"));
    }

    @Test
    public void assertRenewCircuitState() {
        Assert.assertFalse(ShardingProxyContext.getInstance().isCircuitBreak());
        ShardingOrchestrationEventBus.getInstance().post(new CircuitStateChangedEvent(true));
        Assert.assertTrue(ShardingProxyContext.getInstance().isCircuitBreak());
        ShardingOrchestrationEventBus.getInstance().post(new CircuitStateChangedEvent(false));
    }
}

