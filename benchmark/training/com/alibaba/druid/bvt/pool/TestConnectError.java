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
package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestConnectError extends TestCase {
    private DruidDataSource dataSource;

    private MockDriver driver;

    public void test_connect_error() throws Exception {
        Assert.assertEquals(0, dataSource.getCreateErrorCount());
        int count = 10;
        Connection[] connections = new Connection[count];
        for (int i = 0; i < count; ++i) {
            connections[i] = dataSource.getConnection();
        }
        for (int i = 0; i < count; ++i) {
            connections[i].close();
        }
        Assert.assertEquals(10, dataSource.getCreateErrorCount());
    }
}

