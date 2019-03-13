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
package org.apache.hadoop.hbase.master;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.JVMClusterUtil.MasterThread;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test to confirm that we will not hang when stop a backup master which is trying to become the
 * active master. See HBASE-19838
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestShutdownBackupMaster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestShutdownBackupMaster.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static volatile CountDownLatch ARRIVE;

    private static volatile CountDownLatch CONTINUE;

    public static final class MockHMaster extends HMaster {
        public MockHMaster(Configuration conf) throws IOException, KeeperException {
            super(conf);
        }

        @Override
        protected void initClusterSchemaService() throws IOException, InterruptedException {
            if ((TestShutdownBackupMaster.ARRIVE) != null) {
                TestShutdownBackupMaster.ARRIVE.countDown();
                TestShutdownBackupMaster.CONTINUE.await();
            }
            super.initClusterSchemaService();
        }
    }

    @Test
    public void testShutdownWhileBecomingActive() throws InterruptedException {
        MiniHBaseCluster cluster = TestShutdownBackupMaster.UTIL.getHBaseCluster();
        HMaster activeMaster = null;
        HMaster backupMaster = null;
        for (MasterThread t : cluster.getMasterThreads()) {
            if (t.getMaster().isActiveMaster()) {
                activeMaster = t.getMaster();
            } else {
                backupMaster = t.getMaster();
            }
        }
        Assert.assertNotNull(activeMaster);
        Assert.assertNotNull(backupMaster);
        TestShutdownBackupMaster.ARRIVE = new CountDownLatch(1);
        TestShutdownBackupMaster.CONTINUE = new CountDownLatch(1);
        activeMaster.abort("Aborting active master for test");
        // wait until we arrive the initClusterSchemaService
        TestShutdownBackupMaster.ARRIVE.await();
        // killall RSes
        cluster.getRegionServerThreads().stream().map(( t) -> t.getRegionServer()).forEachOrdered(( rs) -> rs.abort("Aborting RS for test"));
        TestShutdownBackupMaster.CONTINUE.countDown();
    }
}

