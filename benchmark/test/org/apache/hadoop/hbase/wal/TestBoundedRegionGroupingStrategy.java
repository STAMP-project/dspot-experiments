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
package org.apache.hadoop.hbase.wal;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, LargeTests.class })
public class TestBoundedRegionGroupingStrategy {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBoundedRegionGroupingStrategy.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBoundedRegionGroupingStrategy.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration CONF;

    private static DistributedFileSystem FS;

    @Parameterized.Parameter
    public String walProvider;

    /**
     * Write to a log file with three concurrent threads and verifying all data is written.
     */
    @Test
    public void testConcurrentWrites() throws Exception {
        // Run the WPE tool with three threads writing 3000 edits each concurrently.
        // When done, verify that all edits were written.
        int errCode = WALPerformanceEvaluation.innerMain(new Configuration(TestBoundedRegionGroupingStrategy.CONF), new String[]{ "-threads", "3", "-verify", "-noclosefs", "-iterations", "3000" });
        Assert.assertEquals(0, errCode);
    }

    /**
     * Make sure we can successfully run with more regions then our bound.
     */
    @Test
    public void testMoreRegionsThanBound() throws Exception {
        final String parallelism = Integer.toString(((BoundedGroupingStrategy.DEFAULT_NUM_REGION_GROUPS) * 2));
        int errCode = WALPerformanceEvaluation.innerMain(new Configuration(TestBoundedRegionGroupingStrategy.CONF), new String[]{ "-threads", parallelism, "-verify", "-noclosefs", "-iterations", "3000", "-regions", parallelism });
        Assert.assertEquals(0, errCode);
    }

    @Test
    public void testBoundsGreaterThanDefault() throws Exception {
        final int temp = TestBoundedRegionGroupingStrategy.CONF.getInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, BoundedGroupingStrategy.DEFAULT_NUM_REGION_GROUPS);
        try {
            TestBoundedRegionGroupingStrategy.CONF.setInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, (temp * 4));
            final String parallelism = Integer.toString((temp * 4));
            int errCode = WALPerformanceEvaluation.innerMain(new Configuration(TestBoundedRegionGroupingStrategy.CONF), new String[]{ "-threads", parallelism, "-verify", "-noclosefs", "-iterations", "3000", "-regions", parallelism });
            Assert.assertEquals(0, errCode);
        } finally {
            TestBoundedRegionGroupingStrategy.CONF.setInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, temp);
        }
    }

    @Test
    public void testMoreRegionsThanBoundWithBoundsGreaterThanDefault() throws Exception {
        final int temp = TestBoundedRegionGroupingStrategy.CONF.getInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, BoundedGroupingStrategy.DEFAULT_NUM_REGION_GROUPS);
        try {
            TestBoundedRegionGroupingStrategy.CONF.setInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, (temp * 4));
            final String parallelism = Integer.toString(((temp * 4) * 2));
            int errCode = WALPerformanceEvaluation.innerMain(new Configuration(TestBoundedRegionGroupingStrategy.CONF), new String[]{ "-threads", parallelism, "-verify", "-noclosefs", "-iterations", "3000", "-regions", parallelism });
            Assert.assertEquals(0, errCode);
        } finally {
            TestBoundedRegionGroupingStrategy.CONF.setInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, temp);
        }
    }

    /**
     * Ensure that we can use Set.add to deduplicate WALs
     */
    @Test
    public void setMembershipDedups() throws IOException {
        final int temp = TestBoundedRegionGroupingStrategy.CONF.getInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, BoundedGroupingStrategy.DEFAULT_NUM_REGION_GROUPS);
        WALFactory wals = null;
        try {
            TestBoundedRegionGroupingStrategy.CONF.setInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, (temp * 4));
            // Set HDFS root directory for storing WAL
            FSUtils.setRootDir(TestBoundedRegionGroupingStrategy.CONF, TestBoundedRegionGroupingStrategy.TEST_UTIL.getDataTestDirOnTestFS());
            wals = new WALFactory(TestBoundedRegionGroupingStrategy.CONF, "setMembershipDedups");
            Set<WAL> seen = new HashSet<>((temp * 4));
            int count = 0;
            // we know that this should see one of the wals more than once
            for (int i = 0; i < (temp * 8); i++) {
                WAL maybeNewWAL = wals.getWAL(RegionInfoBuilder.newBuilder(TableName.valueOf(("Table-" + (ThreadLocalRandom.current().nextInt())))).build());
                TestBoundedRegionGroupingStrategy.LOG.info(((("Iteration " + i) + ", checking wal ") + maybeNewWAL));
                if (seen.add(maybeNewWAL)) {
                    count++;
                }
            }
            Assert.assertEquals(("received back a different number of WALs that are not equal() to each other " + "than the bound we placed."), (temp * 4), count);
        } finally {
            if (wals != null) {
                wals.close();
            }
            TestBoundedRegionGroupingStrategy.CONF.setInt(BoundedGroupingStrategy.NUM_REGION_GROUPS, temp);
        }
    }
}

