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


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-20242
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestOpenSeqNumUnexpectedIncrease {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestOpenSeqNumUnexpectedIncrease.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static AtomicInteger FAILED_OPEN = new AtomicInteger(0);

    private static TableName TABLE_NAME = TableName.valueOf("test");

    private static byte[] CF = Bytes.toBytes("CF");

    public static final class MockHRegion extends HRegion {
        @SuppressWarnings("deprecation")
        public MockHRegion(Path tableDir, WAL wal, FileSystem fs, Configuration confParam, RegionInfo regionInfo, TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, wal, fs, confParam, regionInfo, htd, rsServices);
        }

        @Override
        protected void writeRegionOpenMarker(WAL wal, long openSeqId) throws IOException {
            if ((getRegionInfo().getTable().equals(TestOpenSeqNumUnexpectedIncrease.TABLE_NAME)) && ((TestOpenSeqNumUnexpectedIncrease.FAILED_OPEN.get()) > 0)) {
                TestOpenSeqNumUnexpectedIncrease.FAILED_OPEN.decrementAndGet();
                rsServices.abort("for testing", new Exception("Inject error for testing"));
                throw new IOException("Inject error for testing");
            }
        }
    }

    @Test
    public void test() throws IOException, InterruptedException {
        HRegion region = TestOpenSeqNumUnexpectedIncrease.UTIL.getMiniHBaseCluster().getRegions(TestOpenSeqNumUnexpectedIncrease.TABLE_NAME).get(0);
        long openSeqNum = region.getOpenSeqNum();
        HRegionServer src = TestOpenSeqNumUnexpectedIncrease.UTIL.getRSForFirstRegionInTable(TestOpenSeqNumUnexpectedIncrease.TABLE_NAME);
        HRegionServer dst = TestOpenSeqNumUnexpectedIncrease.UTIL.getOtherRegionServer(src);
        // will fail two times, and then verify that the open sequence number is still openSeqNum + 2
        TestOpenSeqNumUnexpectedIncrease.FAILED_OPEN.set(2);
        TestOpenSeqNumUnexpectedIncrease.UTIL.getAdmin().move(region.getRegionInfo().getEncodedNameAsBytes(), Bytes.toBytes(dst.getServerName().getServerName()));
        TestOpenSeqNumUnexpectedIncrease.UTIL.waitTableAvailable(TestOpenSeqNumUnexpectedIncrease.TABLE_NAME);
        HRegion region1 = TestOpenSeqNumUnexpectedIncrease.UTIL.getMiniHBaseCluster().getRegions(TestOpenSeqNumUnexpectedIncrease.TABLE_NAME).get(0);
        long openSeqNum1 = region1.getOpenSeqNum();
        Assert.assertEquals((openSeqNum + 2), openSeqNum1);
    }
}

