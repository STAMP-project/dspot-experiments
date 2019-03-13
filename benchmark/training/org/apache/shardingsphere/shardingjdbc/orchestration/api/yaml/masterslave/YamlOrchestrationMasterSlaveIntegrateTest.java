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
package org.apache.shardingsphere.shardingjdbc.orchestration.api.yaml.masterslave;


import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.shardingjdbc.orchestration.api.yaml.AbstractYamlDataSourceTest;
import org.apache.shardingsphere.shardingjdbc.orchestration.api.yaml.YamlOrchestrationMasterSlaveDataSourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class YamlOrchestrationMasterSlaveIntegrateTest extends AbstractYamlDataSourceTest {
    private final String filePath;

    private final boolean hasDataSource;

    @Test
    public void assertWithDataSource() throws Exception {
        File yamlFile = new File(YamlOrchestrationMasterSlaveIntegrateTest.class.getResource(filePath).toURI());
        DataSource dataSource;
        if (hasDataSource) {
            dataSource = YamlOrchestrationMasterSlaveDataSourceFactory.createDataSource(yamlFile);
        } else {
            dataSource = YamlOrchestrationMasterSlaveDataSourceFactory.createDataSource(Maps.asMap(Sets.newHashSet("db_master", "db_slave_0", "db_slave_1"), new Function<String, DataSource>() {
                @Override
                public DataSource apply(final String key) {
                    return AbstractYamlDataSourceTest.createDataSource(key);
                }
            }), yamlFile);
        }
        try (Connection conn = dataSource.getConnection();Statement stm = conn.createStatement()) {
            stm.executeQuery("SELECT * FROM t_order");
            stm.executeQuery("SELECT * FROM t_order_item");
            stm.executeQuery("SELECT * FROM t_config");
        }
        close();
    }
}

