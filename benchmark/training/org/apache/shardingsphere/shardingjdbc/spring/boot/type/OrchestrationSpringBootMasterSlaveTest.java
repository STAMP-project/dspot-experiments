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
package org.apache.shardingsphere.shardingjdbc.spring.boot.type;


import java.lang.reflect.Field;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.datasource.OrchestrationMasterSlaveDataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrchestrationSpringBootMasterSlaveTest.class)
@SpringBootApplication
@ActiveProfiles("masterslave")
public class OrchestrationSpringBootMasterSlaveTest {
    @Resource
    private DataSource dataSource;

    @Test
    @SneakyThrows
    public void assertWithMasterSlaveDataSource() {
        Assert.assertTrue(((dataSource) instanceof OrchestrationMasterSlaveDataSource));
        Field field = OrchestrationMasterSlaveDataSource.class.getDeclaredField("dataSource");
        field.setAccessible(true);
        MasterSlaveDataSource masterSlaveDataSource = ((MasterSlaveDataSource) (field.get(dataSource)));
        for (DataSource each : masterSlaveDataSource.getDataSourceMap().values()) {
            Assert.assertThat(getMaxTotal(), CoreMatchers.is(16));
            Assert.assertThat(getUsername(), CoreMatchers.is("root"));
        }
    }
}

