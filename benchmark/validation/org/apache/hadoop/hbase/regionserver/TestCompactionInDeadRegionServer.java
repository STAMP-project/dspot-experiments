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
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.YouAreDeadException;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.wal.WALProvider;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hadoop.hbase.zookeeper.ZNodePaths;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This testcase is used to ensure that the compaction marker will fail a compaction if the RS is
 * already dead. It can not eliminate FNFE when scanning but it does reduce the possibility a lot.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, LargeTests.class })
public class TestCompactionInDeadRegionServer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactionInDeadRegionServer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactionInDeadRegionServer.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("test");

    private static final byte[] CF = Bytes.toBytes("cf");

    private static final byte[] CQ = Bytes.toBytes("cq");

    public static final class IgnoreYouAreDeadRS extends HRegionServer {
        public IgnoreYouAreDeadRS(Configuration conf) throws IOException, InterruptedException {
            super(conf);
        }

        @Override
        protected void tryRegionServerReport(long reportStartTime, long reportEndTime) throws IOException {
            try {
                super.tryRegionServerReport(reportStartTime, reportEndTime);
            } catch (YouAreDeadException e) {
                // ignore, do not abort
            }
        }
    }

    @Parameterized.Parameter
    public Class<? extends WALProvider> walProvider;

    @Test
    public void test() throws Exception {
        HRegionServer regionSvr = TestCompactionInDeadRegionServer.UTIL.getRSForFirstRegionInTable(TestCompactionInDeadRegionServer.TABLE_NAME);
        HRegion region = regionSvr.getRegions(TestCompactionInDeadRegionServer.TABLE_NAME).get(0);
        String regName = region.getRegionInfo().getEncodedName();
        List<HRegion> metaRegs = regionSvr.getRegions(META_TABLE_NAME);
        if ((metaRegs != null) && (!(metaRegs.isEmpty()))) {
            TestCompactionInDeadRegionServer.LOG.info(("meta is on the same server: " + regionSvr));
            // when region is on same server as hbase:meta, reassigning meta would abort the server
            // since WAL is broken.
            // so the region is moved to a different server
            HRegionServer otherRs = TestCompactionInDeadRegionServer.UTIL.getOtherRegionServer(regionSvr);
            TestCompactionInDeadRegionServer.UTIL.moveRegionAndWait(region.getRegionInfo(), otherRs.getServerName());
            TestCompactionInDeadRegionServer.LOG.info(((("Moved region: " + regName) + " to ") + (otherRs.getServerName())));
        }
        HRegionServer rsToSuspend = TestCompactionInDeadRegionServer.UTIL.getRSForFirstRegionInTable(TestCompactionInDeadRegionServer.TABLE_NAME);
        region = rsToSuspend.getRegions(TestCompactionInDeadRegionServer.TABLE_NAME).get(0);
        ZKWatcher watcher = getZooKeeperWatcher();
        watcher.getRecoverableZooKeeper().delete(ZNodePaths.joinZNode(watcher.getZNodePaths().rsZNode, rsToSuspend.getServerName().toString()), (-1));
        TestCompactionInDeadRegionServer.LOG.info(("suspending " + rsToSuspend));
        waitFor(60000, 1000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                for (RegionServerThread thread : TestCompactionInDeadRegionServer.UTIL.getHBaseCluster().getRegionServerThreads()) {
                    HRegionServer rs = thread.getRegionServer();
                    if (rs != rsToSuspend) {
                        return !(rs.getRegions(TestCompactionInDeadRegionServer.TABLE_NAME).isEmpty());
                    }
                }
                return false;
            }

            @Override
            public String explainFailure() throws Exception {
                return (("The region for " + (TestCompactionInDeadRegionServer.TABLE_NAME)) + " is still on ") + (rsToSuspend.getServerName());
            }
        });
        try {
            region.compact(true);
            Assert.fail(("Should fail as our wal file has already been closed, " + "and walDir has also been renamed"));
        } catch (Exception e) {
            TestCompactionInDeadRegionServer.LOG.debug("expected exception: ", e);
        }
        Table table = TestCompactionInDeadRegionServer.UTIL.getConnection().getTable(TestCompactionInDeadRegionServer.TABLE_NAME);
        // should not hit FNFE
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(i, Bytes.toInt(table.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(i))).getValue(TestCompactionInDeadRegionServer.CF, TestCompactionInDeadRegionServer.CQ)));
        }
    }
}

