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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.regionserver.wal.FSHLog;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestLogRoller {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLogRoller.class);

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int logRollPeriod = 20 * 1000;

    @Test
    public void testRemoveClosedWAL() throws Exception {
        HRegionServer rs = TestLogRoller.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        Configuration conf = rs.getConfiguration();
        LogRoller logRoller = TestLogRoller.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0).getWalRoller();
        int originalSize = logRoller.getWalNeedsRoll().size();
        FSHLog wal1 = new FSHLog(rs.getWALFileSystem(), rs.getWALRootDir(), AbstractFSWALProvider.getWALDirectoryName(rs.getServerName().getServerName()), conf);
        logRoller.addWAL(wal1);
        FSHLog wal2 = new FSHLog(rs.getWALFileSystem(), rs.getWALRootDir(), AbstractFSWALProvider.getWALDirectoryName(rs.getServerName().getServerName()), conf);
        logRoller.addWAL(wal2);
        FSHLog wal3 = new FSHLog(rs.getWALFileSystem(), rs.getWALRootDir(), AbstractFSWALProvider.getWALDirectoryName(rs.getServerName().getServerName()), conf);
        logRoller.addWAL(wal3);
        Assert.assertEquals((originalSize + 3), logRoller.getWalNeedsRoll().size());
        Assert.assertTrue(logRoller.getWalNeedsRoll().containsKey(wal1));
        wal1.close();
        Thread.sleep((2 * (TestLogRoller.logRollPeriod)));
        Assert.assertEquals((originalSize + 2), logRoller.getWalNeedsRoll().size());
        Assert.assertFalse(logRoller.getWalNeedsRoll().containsKey(wal1));
        wal2.close();
        wal3.close();
        Thread.sleep((2 * (TestLogRoller.logRollPeriod)));
        Assert.assertEquals(originalSize, logRoller.getWalNeedsRoll().size());
    }
}

