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
package org.apache.hadoop.hbase.rest.client;


import java.util.Arrays;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test RemoteHTable retries.
 */
@Category({ RestTests.class, SmallTests.class })
public class TestRemoteHTableRetries {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRemoteHTableRetries.class);

    private static final int SLEEP_TIME = 50;

    private static final int RETRIES = 3;

    private static final long MAX_TIME = (TestRemoteHTableRetries.SLEEP_TIME) * ((TestRemoteHTableRetries.RETRIES) - 1);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROW_1 = Bytes.toBytes("testrow1");

    private static final byte[] COLUMN_1 = Bytes.toBytes("a");

    private static final byte[] QUALIFIER_1 = Bytes.toBytes("1");

    private static final byte[] VALUE_1 = Bytes.toBytes("testvalue1");

    private Client client;

    private RemoteHTable remoteTable;

    @Test
    public void testDelete() throws Exception {
        testTimedOutCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                Delete delete = new Delete(Bytes.toBytes("delete"));
                remoteTable.delete(delete);
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteHTableRetries.RETRIES)).delete(ArgumentMatchers.anyString());
    }

    @Test
    public void testGet() throws Exception {
        testTimedOutGetCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteTable.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes("Get")));
            }
        });
    }

    @Test
    public void testSingleRowPut() throws Exception {
        testTimedOutCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteTable.put(new Put(Bytes.toBytes("Row")));
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteHTableRetries.RETRIES)).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testMultiRowPut() throws Exception {
        testTimedOutCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                Put[] puts = new Put[]{ new Put(Bytes.toBytes("Row1")), new Put(Bytes.toBytes("Row2")) };
                remoteTable.put(Arrays.asList(puts));
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteHTableRetries.RETRIES)).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testGetScanner() throws Exception {
        testTimedOutCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteTable.getScanner(new Scan());
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteHTableRetries.RETRIES)).post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndPut() throws Exception {
        testTimedOutCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                Put put = new Put(TestRemoteHTableRetries.ROW_1);
                put.addColumn(TestRemoteHTableRetries.COLUMN_1, TestRemoteHTableRetries.QUALIFIER_1, TestRemoteHTableRetries.VALUE_1);
                remoteTable.checkAndMutate(TestRemoteHTableRetries.ROW_1, TestRemoteHTableRetries.COLUMN_1).qualifier(TestRemoteHTableRetries.QUALIFIER_1).ifEquals(TestRemoteHTableRetries.VALUE_1).thenPut(put);
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteHTableRetries.RETRIES)).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndDelete() throws Exception {
        testTimedOutCall(new TestRemoteHTableRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                Put put = new Put(TestRemoteHTableRetries.ROW_1);
                put.addColumn(TestRemoteHTableRetries.COLUMN_1, TestRemoteHTableRetries.QUALIFIER_1, TestRemoteHTableRetries.VALUE_1);
                Delete delete = new Delete(TestRemoteHTableRetries.ROW_1);
                // remoteTable.checkAndDelete(ROW_1, COLUMN_1, QUALIFIER_1,  VALUE_1, delete );
                remoteTable.checkAndMutate(TestRemoteHTableRetries.ROW_1, TestRemoteHTableRetries.COLUMN_1).qualifier(TestRemoteHTableRetries.QUALIFIER_1).ifEquals(TestRemoteHTableRetries.VALUE_1).thenDelete(delete);
            }
        });
    }

    private static interface CallExecutor {
        void run() throws Exception;
    }
}

