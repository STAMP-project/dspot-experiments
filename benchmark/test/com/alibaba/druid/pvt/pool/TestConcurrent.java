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
package com.alibaba.druid.pvt.pool;


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestConcurrent extends TestCase {
    private MockDriver driver;

    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        // ???????
        {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(1, dataSource.getActiveCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());
            conn.close();
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
        }
        Assert.assertEquals(true, dataSource.isEnable());
        // ??????????
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(1, dataSource.getActiveCount());
            conn.close();
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());
        }
        // ????????????10???
        for (int i = 0; i < (1000 * 1); ++i) {
            final int COUNT = 10;
            Connection[] connections = new Connection[COUNT];
            for (int j = 0; j < (connections.length); ++j) {
                connections[j] = dataSource.getConnection();
                Assert.assertEquals((j + 1), dataSource.getActiveCount());
            }
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(COUNT, dataSource.getActiveCount());
            Assert.assertEquals(COUNT, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            for (int j = 0; j < (connections.length); ++j) {
                connections[j].close();
                Assert.assertEquals((j + 1), dataSource.getPoolingCount());
            }
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(COUNT, dataSource.getPoolingCount());
        }
        // 2???
        for (int i = 0; i < 3; ++i) {
            concurrent(2);
        }
        // 5???
        for (int i = 0; i < 3; ++i) {
            concurrent(5);
        }
        // 10??
        for (int i = 0; i < 3; ++i) {
            concurrent(10);
        }
        // 20??
        for (int i = 0; i < 3; ++i) {
            concurrent(20);
        }
        // 50??
        for (int i = 0; i < 3; ++i) {
            concurrent(50);
        }
        // 100??
        for (int i = 0; i < 3; ++i) {
            concurrent(100);
        }
    }
}

