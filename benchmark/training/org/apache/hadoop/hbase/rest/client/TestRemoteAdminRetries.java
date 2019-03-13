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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link RemoteAdmin} retries.
 */
@Category({ RestTests.class, SmallTests.class })
public class TestRemoteAdminRetries {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRemoteAdminRetries.class);

    private static final int SLEEP_TIME = 50;

    private static final int RETRIES = 3;

    private static final long MAX_TIME = (TestRemoteAdminRetries.SLEEP_TIME) * ((TestRemoteAdminRetries.RETRIES) - 1);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private RemoteAdmin remoteAdmin;

    private Client client;

    @Test
    public void testFailingGetRestVersion() throws Exception {
        testTimedOutGetCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.getRestVersion();
            }
        });
    }

    @Test
    public void testFailingGetClusterStatus() throws Exception {
        testTimedOutGetCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.getClusterStatus();
            }
        });
    }

    @Test
    public void testFailingGetClusterVersion() throws Exception {
        testTimedOutGetCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.getClusterVersion();
            }
        });
    }

    @Test
    public void testFailingGetTableAvailable() throws Exception {
        testTimedOutCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.isTableAvailable(Bytes.toBytes("TestTable"));
            }
        });
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testFailingCreateTable() throws Exception {
        testTimedOutCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.createTable(new org.apache.hadoop.hbase.HTableDescriptor(TableName.valueOf("TestTable")));
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteAdminRetries.RETRIES)).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testFailingDeleteTable() throws Exception {
        testTimedOutCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.deleteTable("TestTable");
            }
        });
        Mockito.verify(client, Mockito.times(TestRemoteAdminRetries.RETRIES)).delete(ArgumentMatchers.anyString());
    }

    @Test
    public void testFailingGetTableList() throws Exception {
        testTimedOutGetCall(new TestRemoteAdminRetries.CallExecutor() {
            @Override
            public void run() throws Exception {
                remoteAdmin.getTableList();
            }
        });
    }

    private static interface CallExecutor {
        void run() throws Exception;
    }
}

