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
package org.apache.hadoop.hbase.thrift2;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RestTests.class, MediumTests.class })
public class TestThriftConnection {
    private static final Logger LOG = LoggerFactory.getLogger(TestThriftConnection.class);

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftConnection.class);

    private static final byte[] FAMILYA = Bytes.toBytes("fa");

    private static final byte[] FAMILYB = Bytes.toBytes("fb");

    private static final byte[] FAMILYC = Bytes.toBytes("fc");

    private static final byte[] FAMILYD = Bytes.toBytes("fd");

    private static final byte[] ROW_1 = Bytes.toBytes("testrow1");

    private static final byte[] ROW_2 = Bytes.toBytes("testrow2");

    private static final byte[] ROW_3 = Bytes.toBytes("testrow3");

    private static final byte[] ROW_4 = Bytes.toBytes("testrow4");

    private static final byte[] QUALIFIER_1 = Bytes.toBytes("1");

    private static final byte[] QUALIFIER_2 = Bytes.toBytes("2");

    private static final byte[] VALUE_1 = Bytes.toBytes("testvalue1");

    private static final byte[] VALUE_2 = Bytes.toBytes("testvalue2");

    private static final long ONE_HOUR = (60 * 60) * 1000;

    private static final long TS_2 = System.currentTimeMillis();

    private static final long TS_1 = (TestThriftConnection.TS_2) - (TestThriftConnection.ONE_HOUR);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static ThriftServer thriftServer;

    protected static ThriftServer thriftHttpServer;

    protected static int thriftPort;

    protected static int httpPort;

    protected static Connection thriftConnection;

    protected static Connection thriftHttpConnection;

    private static Admin thriftAdmin;

    @Test
    public void testThrfitAdmin() throws Exception {
        testThriftAdmin(TestThriftConnection.thriftConnection, "testThrfitAdminNamesapce", "testThrfitAdminTable");
        testThriftAdmin(TestThriftConnection.thriftHttpConnection, "testThrfitHttpAdminNamesapce", "testThrfitHttpAdminTable");
    }

    @Test
    public void testGet() throws Exception {
        testGet(TestThriftConnection.thriftConnection, "testGetTable");
        testGet(TestThriftConnection.thriftHttpConnection, "testGetHttpTable");
    }

    @Test
    public void testHBASE22011() throws Exception {
        testHBASE22011(TestThriftConnection.thriftConnection, "testHBASE22011Table");
        testHBASE22011(TestThriftConnection.thriftHttpConnection, "testHBASE22011HttpTable");
    }

    @Test
    public void testMultiGet() throws Exception {
        testMultiGet(TestThriftConnection.thriftConnection, "testMultiGetTable");
        testMultiGet(TestThriftConnection.thriftHttpConnection, "testMultiGetHttpTable");
    }

    @Test
    public void testPut() throws Exception {
        testPut(TestThriftConnection.thriftConnection, "testPutTable");
        testPut(TestThriftConnection.thriftHttpConnection, "testPutHttpTable");
    }

    @Test
    public void testDelete() throws Exception {
        testDelete(TestThriftConnection.thriftConnection, "testDeleteTable");
        testDelete(TestThriftConnection.thriftHttpConnection, "testDeleteHttpTable");
    }

    @Test
    public void testScanner() throws Exception {
        testScanner(TestThriftConnection.thriftConnection, "testScannerTable");
        testScanner(TestThriftConnection.thriftHttpConnection, "testScannerHttpTable");
    }

    @Test
    public void testCheckAndDelete() throws Exception {
        testCheckAndDelete(TestThriftConnection.thriftConnection, "testCheckAndDeleteTable");
        testCheckAndDelete(TestThriftConnection.thriftHttpConnection, "testCheckAndDeleteHttpTable");
    }

    @Test
    public void testIteratorScaner() throws Exception {
        testIteratorScanner(TestThriftConnection.thriftConnection, "testIteratorScanerTable");
        testIteratorScanner(TestThriftConnection.thriftHttpConnection, "testIteratorScanerHttpTable");
    }

    @Test
    public void testReverseScan() throws Exception {
        testReverseScan(TestThriftConnection.thriftConnection, "testReverseScanTable");
        testReverseScan(TestThriftConnection.thriftHttpConnection, "testReverseScanHttpTable");
    }

    @Test
    public void testScanWithFilters() throws Exception {
        testScanWithFilters(TestThriftConnection.thriftConnection, "testScanWithFiltersTable");
        testScanWithFilters(TestThriftConnection.thriftHttpConnection, "testScanWithFiltersHttpTable");
    }
}

