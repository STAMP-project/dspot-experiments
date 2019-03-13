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


import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.orchestration.yaml.config.YamlDataSourceConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DataSourceConfigurationYamlSwapperTest {
    private final DataSourceConfigurationYamlSwapper dataSourceConfigurationYamlSwapper = new DataSourceConfigurationYamlSwapper();

    @Test
    public void assertSwapToYaml() {
        DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration("xxx.jdbc.driver");
        dataSourceConfiguration.getProperties().put("url", "xx:xxx");
        dataSourceConfiguration.getProperties().put("username", "root");
        YamlDataSourceConfiguration actual = dataSourceConfigurationYamlSwapper.swap(dataSourceConfiguration);
        Assert.assertThat(actual.getDataSourceClassName(), CoreMatchers.is("xxx.jdbc.driver"));
        Assert.assertThat(actual.getProperties().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getProperties().get("url").toString(), CoreMatchers.is("xx:xxx"));
        Assert.assertThat(actual.getProperties().get("username").toString(), CoreMatchers.is("root"));
    }

    @Test
    public void assertSwapToConfiguration() {
        YamlDataSourceConfiguration yamlConfiguration = new YamlDataSourceConfiguration();
        yamlConfiguration.setDataSourceClassName("xxx.jdbc.driver");
        yamlConfiguration.getProperties().put("url", "xx:xxx");
        yamlConfiguration.getProperties().put("username", "root");
        DataSourceConfiguration actual = dataSourceConfigurationYamlSwapper.swap(yamlConfiguration);
        Assert.assertThat(actual.getDataSourceClassName(), CoreMatchers.is("xxx.jdbc.driver"));
        Assert.assertThat(actual.getProperties().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getProperties().get("url").toString(), CoreMatchers.is("xx:xxx"));
        Assert.assertThat(actual.getProperties().get("username").toString(), CoreMatchers.is("root"));
    }
}

