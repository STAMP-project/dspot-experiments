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
package org.apache.hadoop.hbase.master.procedure;


import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestSafemodeBringsDownMaster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSafemodeBringsDownMaster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSafemodeBringsDownMaster.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testSafemodeBringsDownMaster() throws Exception {
        final TableName tableName = TableName.valueOf("testSafemodeBringsDownMaster");
        final byte[][] splitKeys = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("c") };
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(getMasterProcedureExecutor(), tableName, splitKeys, "f1", "f2");
        MiniDFSCluster dfsCluster = TestSafemodeBringsDownMaster.UTIL.getDFSCluster();
        DistributedFileSystem dfs = ((DistributedFileSystem) (dfsCluster.getFileSystem()));
        dfs.setSafeMode(SAFEMODE_ENTER);
        final long timeOut = 180000;
        long startTime = System.currentTimeMillis();
        int index = -1;
        do {
            index = TestSafemodeBringsDownMaster.UTIL.getMiniHBaseCluster().getServerWithMeta();
        } while ((index == (-1)) && ((startTime + timeOut) < (System.currentTimeMillis())) );
        if (index != (-1)) {
            TestSafemodeBringsDownMaster.UTIL.getMiniHBaseCluster().abortRegionServer(index);
            TestSafemodeBringsDownMaster.UTIL.getMiniHBaseCluster().waitOnRegionServer(index);
        }
        TestSafemodeBringsDownMaster.UTIL.waitFor(timeOut, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                List<JVMClusterUtil.MasterThread> threads = TestSafemodeBringsDownMaster.UTIL.getMiniHBaseCluster().getLiveMasterThreads();
                return (threads == null) || (threads.isEmpty());
            }
        });
        dfs.setSafeMode(SAFEMODE_LEAVE);
    }
}

