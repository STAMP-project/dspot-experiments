/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.exec.spark;


import SmallTableCache.SmallTableLocalCache;
import java.util.StringJoiner;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.exec.persistence.MapJoinTableContainer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test if the small table cache is evicted, when a new query is executed.
 */
public class TestSmallTableCacheEviction {
    private String smallTableName1;

    private String smallTableName2;

    private String largeTableName;

    private SmallTableLocalCache<String, MapJoinTableContainer> innerCache;

    private HiveConf conf;

    @Test
    public void testSmallTableEvictionIfNewQueryIsExecuted() throws Exception {
        for (int i = 0; i < 2; i++) {
            Driver driver = null;
            try {
                driver = createDriver();
                String simpleJoinQuery = ((((("select large.col, s1.col, s2.col from " + (largeTableName)) + " large join ") + (smallTableName1)) + " s1 on s1.col = large.col join ") + (smallTableName2)) + " s2 on s2.col = large.col";
                Assert.assertEquals(0, driver.run(simpleJoinQuery).getResponseCode());
                Assert.assertEquals(2, innerCache.size());
            } finally {
                if (driver != null) {
                    driver.destroy();
                }
            }
        }
    }

    /**
     * Helper class to create a simple table with n number of rows in it.
     */
    private static final class MockDataBuilder {
        private final String tableName;

        private int numberOfRows;

        private MockDataBuilder(String tableName) {
            this.tableName = tableName;
        }

        public TestSmallTableCacheEviction.MockDataBuilder numberOfRows(int numberOfRows) {
            this.numberOfRows = numberOfRows;
            return this;
        }

        public void create(Driver driver) {
            Assert.assertEquals(0, driver.run((("create table " + (tableName)) + " (col int)")).getResponseCode());
            if ((numberOfRows) > 0) {
                StringJoiner query = new StringJoiner(",", (("insert into " + (tableName)) + " values "), "");
                for (int i = 0; i < (numberOfRows); i++) {
                    query.add((("(" + (Integer.toString((i + 1)))) + ")"));
                }
                Assert.assertEquals(0, driver.run(query.toString()).getResponseCode());
            }
        }

        public static TestSmallTableCacheEviction.MockDataBuilder builder(String tableName) {
            return new TestSmallTableCacheEviction.MockDataBuilder(tableName);
        }
    }
}

