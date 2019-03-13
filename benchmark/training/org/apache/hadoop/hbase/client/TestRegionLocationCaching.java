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
package org.apache.hadoop.hbase.client;


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestRegionLocationCaching {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionLocationCaching.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static int SLAVES = 1;

    private static TableName TABLE_NAME = TableName.valueOf("TestRegionLocationCaching");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    @Test
    public void testCachingForHTableSinglePut() throws Exception {
        byte[] row = Bytes.toBytes("htable_single_put");
        byte[] value = Bytes.toBytes("value");
        Put put = new Put(row);
        put.addColumn(TestRegionLocationCaching.FAMILY, TestRegionLocationCaching.QUALIFIER, value);
        try (Table table = TestRegionLocationCaching.TEST_UTIL.getConnection().getTable(TestRegionLocationCaching.TABLE_NAME)) {
            table.put(put);
        }
        checkRegionLocationIsCached(TestRegionLocationCaching.TABLE_NAME, TestRegionLocationCaching.TEST_UTIL.getConnection());
        TestRegionLocationCaching.checkExistence(TestRegionLocationCaching.TABLE_NAME, row, TestRegionLocationCaching.FAMILY, TestRegionLocationCaching.QUALIFIER);
    }

    @Test
    public void testCachingForHTableMultiPut() throws Exception {
        List<Put> multiput = new ArrayList<Put>();
        for (int i = 0; i < 10; i++) {
            Put put = new Put(Bytes.toBytes(("htable_multi_put" + i)));
            byte[] value = Bytes.toBytes(("value_" + i));
            put.addColumn(TestRegionLocationCaching.FAMILY, TestRegionLocationCaching.QUALIFIER, value);
            multiput.add(put);
        }
        try (Table table = TestRegionLocationCaching.TEST_UTIL.getConnection().getTable(TestRegionLocationCaching.TABLE_NAME)) {
            table.put(multiput);
        }
        checkRegionLocationIsCached(TestRegionLocationCaching.TABLE_NAME, TestRegionLocationCaching.TEST_UTIL.getConnection());
        for (int i = 0; i < 10; i++) {
            TestRegionLocationCaching.checkExistence(TestRegionLocationCaching.TABLE_NAME, Bytes.toBytes(("htable_multi_put" + i)), TestRegionLocationCaching.FAMILY, TestRegionLocationCaching.QUALIFIER);
        }
    }
}

