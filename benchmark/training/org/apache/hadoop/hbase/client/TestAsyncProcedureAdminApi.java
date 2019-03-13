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


import SnapshotManager.ONLINE_SNAPSHOT_CONTROLLER_DESCRIPTION;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.procedure.SimpleMasterProcedureManager;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Class to test asynchronous procedure admin operations.
 */
@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncProcedureAdminApi extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncProcedureAdminApi.class);

    @Test
    public void testExecProcedure() throws Exception {
        String snapshotString = "offlineTableSnapshot";
        try {
            Table table = TestAsyncAdminBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("cf"));
            for (int i = 0; i < 100; i++) {
                Put put = new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("cf"), null, Bytes.toBytes(i));
                table.put(put);
            }
            // take a snapshot of the enabled table
            Map<String, String> props = new HashMap<>();
            props.put("table", tableName.getNameAsString());
            admin.execProcedure(ONLINE_SNAPSHOT_CONTROLLER_DESCRIPTION, snapshotString, props).get();
            TestAsyncAdminBase.LOG.debug("Snapshot completed.");
        } finally {
            admin.deleteSnapshot(snapshotString).join();
            TestAsyncAdminBase.TEST_UTIL.deleteTable(tableName);
        }
    }

    @Test
    public void testExecProcedureWithRet() throws Exception {
        byte[] result = admin.execProcedureWithReturn(SimpleMasterProcedureManager.SIMPLE_SIGNATURE, "myTest2", new HashMap()).get();
        Assert.assertArrayEquals("Incorrect return data from execProcedure", Bytes.toBytes(SimpleMasterProcedureManager.SIMPLE_DATA), result);
    }

    @Test
    public void listProcedure() throws Exception {
        String procList = admin.getProcedures().get();
        Assert.assertTrue(procList.startsWith("["));
    }

    @Test
    public void isProcedureFinished() throws Exception {
        boolean failed = false;
        try {
            admin.isProcedureFinished("fake-signature", "fake-instance", new HashMap()).get();
        } catch (Exception e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void abortProcedure() throws Exception {
        Random randomGenerator = new Random();
        long procId = randomGenerator.nextLong();
        boolean abortResult = admin.abortProcedure(procId, true).get();
        Assert.assertFalse(abortResult);
    }
}

