/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool;


import com.alibaba.druid.mock.MockDriver;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestIdle3_Concurrent_MaxActive extends TestCase {
    private MockDriver driver;

    private DruidDataSource dataSource;

    public void test_idle2() throws Exception {
        // ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource,
        // new ObjectName("com.alibaba:type=DataSource"));
        // ???????
        {
            Assert.assertEquals(0, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getActiveCount());
            conn.close();
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(1, driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }
        for (int i = 0; i < 1000; ++i) {
            concurrent(200);
            Assert.assertEquals(("" + i), true, ((dataSource.getPoolingCount()) <= (dataSource.getMaxActive())));
            dataSource.shrink();
        }
        // ??????????
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(1, dataSource.getActiveCount());
            conn.close();
        }
        // Assert.assertEquals(2, dataSource.getPoolingCount());
        dataSource.close();
    }
}

