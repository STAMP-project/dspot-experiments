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
package org.apache.shardingsphere.shardingproxy.util;


import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import org.apache.shardingsphere.core.config.DataSourceConfiguration;
import org.apache.shardingsphere.shardingproxy.config.yaml.YamlDataSourceParameter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DataSourceConverterTest {
    @Test
    public void assertGetDataSourceParameterMap() {
        Map<String, DataSourceConfiguration> dataSourceConfigurationMap = new HashMap<>(2, 1);
        DataSourceConfiguration dataSourceConfiguration0 = new DataSourceConfiguration(HikariDataSource.class.getName());
        dataSourceConfiguration0.getProperties().put("url", "jdbc:mysql://localhost:3306/demo_ds_0");
        dataSourceConfigurationMap.put("ds_0", dataSourceConfiguration0);
        DataSourceConfiguration dataSourceConfiguration1 = new DataSourceConfiguration(HikariDataSource.class.getName());
        dataSourceConfiguration1.getProperties().put("url", "jdbc:mysql://localhost:3306/demo_ds_1");
        dataSourceConfigurationMap.put("ds_1", dataSourceConfiguration1);
        Map<String, YamlDataSourceParameter> actual = DataSourceConverter.getDataSourceParameterMap(dataSourceConfigurationMap);
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        Assert.assertThat(actual.get("ds_0").getUrl(), CoreMatchers.is("jdbc:mysql://localhost:3306/demo_ds_0"));
        Assert.assertThat(actual.get("ds_1").getUrl(), CoreMatchers.is("jdbc:mysql://localhost:3306/demo_ds_1"));
    }

    @Test
    public void assertGetDataSourceConfigurationMap() {
        Map<String, YamlDataSourceParameter> dataSourceParameterMap = new HashMap<>(2, 1);
        dataSourceParameterMap.put("ds_0", crateDataSourceParameter());
        dataSourceParameterMap.put("ds_1", crateDataSourceParameter());
        Map<String, DataSourceConfiguration> actual = DataSourceConverter.getDataSourceConfigurationMap(dataSourceParameterMap);
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        assertParameter(actual.get("ds_0"));
        assertParameter(actual.get("ds_1"));
    }
}

