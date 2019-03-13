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
package org.apache.shardingsphere.orchestration.internal.registry;


import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.apache.shardingsphere.api.config.RuleConfiguration;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.core.rule.Authentication;
import org.apache.shardingsphere.orchestration.internal.registry.config.service.ConfigurationService;
import org.apache.shardingsphere.orchestration.internal.registry.listener.ShardingOrchestrationListenerManager;
import org.apache.shardingsphere.orchestration.internal.registry.state.service.StateService;
import org.apache.shardingsphere.orchestration.reg.api.RegistryCenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class ShardingOrchestrationFacadeTest {
    private ShardingOrchestrationFacade shardingOrchestrationFacade;

    @Mock
    private RegistryCenter regCenter;

    @Mock
    private ConfigurationService configService;

    @Mock
    private StateService stateService;

    @Mock
    private ShardingOrchestrationListenerManager listenerManager;

    @Test
    public void assertInitWithParameters() {
        Map<String, DataSourceConfiguration> dataSourceConfigurationMap = Collections.singletonMap("test_ds", Mockito.mock(DataSourceConfiguration.class));
        Map<String, RuleConfiguration> ruleConfigurationMap = Collections.singletonMap("sharding_db", Mockito.mock(RuleConfiguration.class));
        Authentication authentication = new Authentication("root", "root");
        Properties props = new Properties();
        shardingOrchestrationFacade.init(Collections.singletonMap("sharding_db", dataSourceConfigurationMap), ruleConfigurationMap, authentication, props);
        Mockito.verify(configService).persistConfiguration("sharding_db", dataSourceConfigurationMap, ruleConfigurationMap.get("sharding_db"), authentication, props, true);
        Mockito.verify(stateService).persistInstanceOnline();
        Mockito.verify(stateService).persistDataSourcesNode();
        Mockito.verify(listenerManager).initListeners();
    }

    @Test
    public void assertInitWithoutParameters() {
        shardingOrchestrationFacade.init();
        Mockito.verify(stateService).persistInstanceOnline();
        Mockito.verify(stateService).persistDataSourcesNode();
        Mockito.verify(listenerManager).initListeners();
    }

    @Test
    public void assertCloseSuccess() throws Exception {
        shardingOrchestrationFacade.close();
        Mockito.verify(regCenter).close();
    }

    @Test
    public void assertCloseFailure() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(regCenter).close();
        shardingOrchestrationFacade.close();
        Mockito.verify(regCenter).close();
    }
}

