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


import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.YouAreDeadException;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * See HBASE-19929 for more details.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, MediumTests.class })
public class TestShutdownWhileWALBroken {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestShutdownWhileWALBroken.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestShutdownWhileWALBroken.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("TestShutdownWhileWALBroken");

    private static byte[] CF = Bytes.toBytes("CF");

    @Parameterized.Parameter
    public String walType;

    public static final class MyRegionServer extends HRegionServer {
        private final CountDownLatch latch = new CountDownLatch(1);

        public MyRegionServer(Configuration conf) throws IOException {
            super(conf);
        }

        @Override
        protected void tryRegionServerReport(long reportStartTime, long reportEndTime) throws IOException {
            try {
                super.tryRegionServerReport(reportStartTime, reportEndTime);
            } catch (YouAreDeadException e) {
                TestShutdownWhileWALBroken.LOG.info("Caught YouAreDeadException, ignore", e);
            }
        }

        @Override
        public void abort(String reason, Throwable cause) {
            if (cause instanceof SessionExpiredException) {
                // called from ZKWatcher, let's wait a bit to make sure that we call stop before calling
                // abort.
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
            } else {
                // abort from other classes, usually LogRoller, now we can make progress on abort.
                latch.countDown();
            }
            super.abort(reason, cause);
        }
    }

    @Test
    public void test() throws Exception {
        TestShutdownWhileWALBroken.UTIL.createMultiRegionTable(TestShutdownWhileWALBroken.TABLE_NAME, TestShutdownWhileWALBroken.CF);
        try (Table table = TestShutdownWhileWALBroken.UTIL.getConnection().getTable(TestShutdownWhileWALBroken.TABLE_NAME)) {
            TestShutdownWhileWALBroken.UTIL.loadTable(table, TestShutdownWhileWALBroken.CF);
        }
        int numRegions = TestShutdownWhileWALBroken.UTIL.getMiniHBaseCluster().getRegions(TestShutdownWhileWALBroken.TABLE_NAME).size();
        RegionServerThread rst0 = TestShutdownWhileWALBroken.UTIL.getMiniHBaseCluster().getRegionServerThreads().get(0);
        RegionServerThread rst1 = TestShutdownWhileWALBroken.UTIL.getMiniHBaseCluster().getRegionServerThreads().get(1);
        HRegionServer liveRS;
        RegionServerThread toKillRSThread;
        if (rst1.getRegionServer().getRegions(META_TABLE_NAME).isEmpty()) {
            liveRS = rst0.getRegionServer();
            toKillRSThread = rst1;
        } else {
            liveRS = rst1.getRegionServer();
            toKillRSThread = rst0;
        }
        Assert.assertTrue(((liveRS.getRegions(TestShutdownWhileWALBroken.TABLE_NAME).size()) < numRegions));
        TestShutdownWhileWALBroken.UTIL.expireSession(toKillRSThread.getRegionServer().getZooKeeper(), false);
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (liveRS.getRegions(TestShutdownWhileWALBroken.TABLE_NAME).size()) == numRegions;
            }

            @Override
            public String explainFailure() throws Exception {
                return "Failover is not finished yet";
            }
        });
        toKillRSThread.getRegionServer().stop("Stop for test");
        // make sure that we can successfully quit
        toKillRSThread.join();
    }
}

