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
package org.apache.hadoop.hbase.regionserver;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ LargeTests.class })
public class TestCleanupCompactedFileAfterFailover {
    private static final Logger LOG = LoggerFactory.getLogger(TestCleanupCompactedFileAfterFailover.class);

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCleanupCompactedFileAfterFailover.class);

    private static HBaseTestingUtility TEST_UTIL;

    private static Admin admin;

    private static Table table;

    private static TableName TABLE_NAME = TableName.valueOf("TestCleanupCompactedFileAfterFailover");

    private static byte[] ROW = Bytes.toBytes("row");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUALIFIER = Bytes.toBytes("cq");

    private static byte[] VALUE = Bytes.toBytes("value");

    private static final int RS_NUMBER = 5;

    @Test
    public void testCleanupAfterFailoverWithCompactOnce() throws Exception {
        testCleanupAfterFailover(1);
    }

    @Test
    public void testCleanupAfterFailoverWithCompactTwice() throws Exception {
        testCleanupAfterFailover(2);
    }

    @Test
    public void testCleanupAfterFailoverWithCompactThreeTimes() throws Exception {
        testCleanupAfterFailover(3);
    }
}

