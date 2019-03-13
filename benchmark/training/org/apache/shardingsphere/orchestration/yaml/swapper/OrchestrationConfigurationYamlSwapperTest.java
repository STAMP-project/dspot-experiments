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
package org.apache.shardingsphere.orchestration.yaml.swapper;


import org.apache.shardingsphere.orchestration.config.OrchestrationConfiguration;
import org.apache.shardingsphere.orchestration.reg.api.RegistryCenterConfiguration;
import org.apache.shardingsphere.orchestration.yaml.config.YamlOrchestrationConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class OrchestrationConfigurationYamlSwapperTest {
    private final OrchestrationConfigurationYamlSwapper orchestrationConfigurationYamlSwapper = new OrchestrationConfigurationYamlSwapper();

    @Test
    public void assertSwapToYaml() {
        RegistryCenterConfiguration registryCenterConfiguration = Mockito.mock(RegistryCenterConfiguration.class);
        YamlOrchestrationConfiguration actual = orchestrationConfigurationYamlSwapper.swap(new OrchestrationConfiguration("orche_ds", registryCenterConfiguration, true));
        Assert.assertThat(actual.getName(), CoreMatchers.is("orche_ds"));
        Assert.assertThat(actual.getRegistry(), CoreMatchers.is(registryCenterConfiguration));
        Assert.assertTrue(actual.isOverwrite());
    }

    @Test
    public void assertSwapToConfiguration() {
        YamlOrchestrationConfiguration yamlConfiguration = new YamlOrchestrationConfiguration();
        yamlConfiguration.setName("orche_ds");
        RegistryCenterConfiguration registryCenterConfiguration = Mockito.mock(RegistryCenterConfiguration.class);
        yamlConfiguration.setRegistry(registryCenterConfiguration);
        yamlConfiguration.setOverwrite(true);
        OrchestrationConfiguration actual = orchestrationConfigurationYamlSwapper.swap(yamlConfiguration);
        Assert.assertThat(actual.getName(), CoreMatchers.is("orche_ds"));
        Assert.assertThat(actual.getRegCenterConfig(), CoreMatchers.is(registryCenterConfiguration));
        Assert.assertTrue(actual.isOverwrite());
    }
}

