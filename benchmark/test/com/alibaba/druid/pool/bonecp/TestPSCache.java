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
package com.alibaba.druid.pool.bonecp;


import com.jolbox.bonecp.BoneCPDataSource;
import junit.framework.TestCase;


public class TestPSCache extends TestCase {
    public void test_boneCP() throws Exception {
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setJdbcUrl("jdbc:mock:test");
        ds.setPartitionCount(1);
        ds.setMaxConnectionsPerPartition(10);
        ds.setMinConnectionsPerPartition(0);
        ds.setPreparedStatementsCacheSize(10);
        for (int i = 0; i < 10; ++i) {
            TestPSCache.f(ds, 5);
            System.out.println("--------------------------------------------");
        }
    }
}

