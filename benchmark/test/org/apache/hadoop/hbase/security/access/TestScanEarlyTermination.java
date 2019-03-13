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
package org.apache.hadoop.hbase.security.access;


import Action.READ;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.Permission.Action;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, MediumTests.class })
public class TestScanEarlyTermination extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScanEarlyTermination.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScanEarlyTermination.class);

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] TEST_FAMILY1 = Bytes.toBytes("f1");

    private static final byte[] TEST_FAMILY2 = Bytes.toBytes("f2");

    private static final byte[] TEST_ROW = Bytes.toBytes("testrow");

    private static final byte[] TEST_Q1 = Bytes.toBytes("q1");

    private static final byte[] TEST_Q2 = Bytes.toBytes("q2");

    private static final byte[] ZERO = Bytes.toBytes(0L);

    private static Configuration conf;

    private static User USER_OWNER;

    private static User USER_OTHER;

    @Test
    public void testEarlyScanTermination() throws Exception {
        // Grant USER_OTHER access to TEST_FAMILY1 only
        SecureTestUtil.grantOnTable(TestScanEarlyTermination.TEST_UTIL, TestScanEarlyTermination.USER_OTHER.getShortName(), TEST_TABLE.getTableName(), TestScanEarlyTermination.TEST_FAMILY1, null, READ);
        // Set up test data
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                // force a new RS connection
                TestScanEarlyTermination.conf.set("testkey", getRandomUUID().toString());
                Connection connection = ConnectionFactory.createConnection(TestScanEarlyTermination.conf);
                Table t = connection.getTable(TEST_TABLE.getTableName());
                try {
                    Put put = new Put(TestScanEarlyTermination.TEST_ROW).addColumn(TestScanEarlyTermination.TEST_FAMILY1, TestScanEarlyTermination.TEST_Q1, TestScanEarlyTermination.ZERO);
                    t.put(put);
                    // Set a READ cell ACL for USER_OTHER on this value in FAMILY2
                    put = new Put(TestScanEarlyTermination.TEST_ROW).addColumn(TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q1, TestScanEarlyTermination.ZERO);
                    put.setACL(TestScanEarlyTermination.USER_OTHER.getShortName(), new Permission(Action.READ));
                    t.put(put);
                    // Set an empty cell ACL for USER_OTHER on this other value in FAMILY2
                    put = new Put(TestScanEarlyTermination.TEST_ROW).addColumn(TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q2, TestScanEarlyTermination.ZERO);
                    put.setACL(TestScanEarlyTermination.USER_OTHER.getShortName(), new Permission());
                    t.put(put);
                } finally {
                    t.close();
                    connection.close();
                }
                return null;
            }
        }, TestScanEarlyTermination.USER_OWNER);
        // A scan of FAMILY1 will be allowed
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                // force a new RS connection
                TestScanEarlyTermination.conf.set("testkey", getRandomUUID().toString());
                Connection connection = ConnectionFactory.createConnection(TestScanEarlyTermination.conf);
                Table t = connection.getTable(TEST_TABLE.getTableName());
                try {
                    Scan scan = new Scan().addFamily(TestScanEarlyTermination.TEST_FAMILY1);
                    Result result = t.getScanner(scan).next();
                    if (result != null) {
                        Assert.assertTrue("Improper exclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY1, TestScanEarlyTermination.TEST_Q1));
                        Assert.assertFalse("Improper inclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q1));
                        return result.listCells();
                    }
                    return null;
                } finally {
                    t.close();
                    connection.close();
                }
            }
        }, TestScanEarlyTermination.USER_OTHER);
        // A scan of FAMILY1 and FAMILY2 will produce results for FAMILY1 without
        // throwing an exception, however no cells from FAMILY2 will be returned
        // because we early out checks at the CF level.
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                // force a new RS connection
                TestScanEarlyTermination.conf.set("testkey", getRandomUUID().toString());
                Connection connection = ConnectionFactory.createConnection(TestScanEarlyTermination.conf);
                Table t = connection.getTable(TEST_TABLE.getTableName());
                try {
                    Scan scan = new Scan();
                    Result result = t.getScanner(scan).next();
                    if (result != null) {
                        Assert.assertTrue("Improper exclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY1, TestScanEarlyTermination.TEST_Q1));
                        Assert.assertFalse("Improper inclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q1));
                        return result.listCells();
                    }
                    return null;
                } finally {
                    t.close();
                    connection.close();
                }
            }
        }, TestScanEarlyTermination.USER_OTHER);
        // A scan of FAMILY2 will throw an AccessDeniedException
        SecureTestUtil.verifyDenied(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                // force a new RS connection
                TestScanEarlyTermination.conf.set("testkey", getRandomUUID().toString());
                Connection connection = ConnectionFactory.createConnection(TestScanEarlyTermination.conf);
                Table t = connection.getTable(TEST_TABLE.getTableName());
                try {
                    Scan scan = new Scan().addFamily(TestScanEarlyTermination.TEST_FAMILY2);
                    Result result = t.getScanner(scan).next();
                    if (result != null) {
                        return result.listCells();
                    }
                    return null;
                } finally {
                    t.close();
                    connection.close();
                }
            }
        }, TestScanEarlyTermination.USER_OTHER);
        // Now grant USER_OTHER access to TEST_FAMILY2:TEST_Q2
        SecureTestUtil.grantOnTable(TestScanEarlyTermination.TEST_UTIL, TestScanEarlyTermination.USER_OTHER.getShortName(), TEST_TABLE.getTableName(), TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q2, READ);
        // A scan of FAMILY1 and FAMILY2 will produce combined results. In FAMILY2
        // we have access granted to Q2 at the CF level. Because we early out
        // checks at the CF level the cell ACL on Q1 also granting access is ignored.
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                // force a new RS connection
                TestScanEarlyTermination.conf.set("testkey", getRandomUUID().toString());
                Connection connection = ConnectionFactory.createConnection(TestScanEarlyTermination.conf);
                Table t = connection.getTable(TEST_TABLE.getTableName());
                try {
                    Scan scan = new Scan();
                    Result result = t.getScanner(scan).next();
                    if (result != null) {
                        Assert.assertTrue("Improper exclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY1, TestScanEarlyTermination.TEST_Q1));
                        Assert.assertFalse("Improper inclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q1));
                        Assert.assertTrue("Improper exclusion", result.containsColumn(TestScanEarlyTermination.TEST_FAMILY2, TestScanEarlyTermination.TEST_Q2));
                        return result.listCells();
                    }
                    return null;
                } finally {
                    t.close();
                    connection.close();
                }
            }
        }, TestScanEarlyTermination.USER_OTHER);
    }
}

