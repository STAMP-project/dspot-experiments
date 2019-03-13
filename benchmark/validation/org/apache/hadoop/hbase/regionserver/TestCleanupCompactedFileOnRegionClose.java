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


import java.util.Collection;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class })
public class TestCleanupCompactedFileOnRegionClose {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCleanupCompactedFileOnRegionClose.class);

    private static HBaseTestingUtility util;

    @Test
    public void testCleanupOnClose() throws Exception {
        TableName tableName = TableName.valueOf("testCleanupOnClose");
        String familyName = "f";
        byte[] familyNameBytes = Bytes.toBytes(familyName);
        TestCleanupCompactedFileOnRegionClose.util.createTable(tableName, familyName);
        HBaseAdmin hBaseAdmin = TestCleanupCompactedFileOnRegionClose.util.getHBaseAdmin();
        Table table = TestCleanupCompactedFileOnRegionClose.util.getConnection().getTable(tableName);
        HRegionServer rs = TestCleanupCompactedFileOnRegionClose.util.getRSForFirstRegionInTable(tableName);
        Region region = rs.getRegions(tableName).get(0);
        int refSFCount = 4;
        for (int i = 0; i < refSFCount; i++) {
            for (int j = 0; j < refSFCount; j++) {
                Put put = new Put(Bytes.toBytes(j));
                put.addColumn(familyNameBytes, Bytes.toBytes(i), Bytes.toBytes(j));
                table.put(put);
            }
            TestCleanupCompactedFileOnRegionClose.util.flush(tableName);
        }
        Assert.assertEquals(refSFCount, region.getStoreFileList(new byte[][]{ familyNameBytes }).size());
        // add a delete, to test wether we end up with an inconsistency post region close
        Delete delete = new Delete(Bytes.toBytes((refSFCount - 1)));
        table.delete(delete);
        TestCleanupCompactedFileOnRegionClose.util.flush(tableName);
        Assert.assertFalse(table.exists(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes((refSFCount - 1)))));
        // Create a scanner and keep it open to add references to StoreFileReaders
        Scan scan = new Scan();
        scan.setStopRow(Bytes.toBytes((refSFCount - 2)));
        scan.setCaching(1);
        ResultScanner scanner = table.getScanner(scan);
        Result res = scanner.next();
        Assert.assertNotNull(res);
        Assert.assertEquals(refSFCount, res.getFamilyMap(familyNameBytes).size());
        // Verify the references
        int count = 0;
        for (HStoreFile sf : ((Collection<HStoreFile>) (region.getStore(familyNameBytes).getStorefiles()))) {
            synchronized(sf) {
                if (count < refSFCount) {
                    Assert.assertTrue(sf.isReferencedInReads());
                } else {
                    Assert.assertFalse(sf.isReferencedInReads());
                }
            }
            count++;
        }
        // Major compact to produce compacted storefiles that need to be cleaned up
        TestCleanupCompactedFileOnRegionClose.util.compact(tableName, true);
        Assert.assertEquals(1, region.getStoreFileList(new byte[][]{ familyNameBytes }).size());
        Assert.assertEquals((refSFCount + 1), getStoreEngine().getStoreFileManager().getCompactedfiles().size());
        // close then open the region to determine wether compacted storefiles get cleaned up on close
        hBaseAdmin.unassign(region.getRegionInfo().getRegionName(), false);
        hBaseAdmin.assign(region.getRegionInfo().getRegionName());
        TestCleanupCompactedFileOnRegionClose.util.waitUntilNoRegionsInTransition(10000);
        Assert.assertFalse("Deleted row should not exist", table.exists(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes((refSFCount - 1)))));
        rs = TestCleanupCompactedFileOnRegionClose.util.getRSForFirstRegionInTable(tableName);
        region = rs.getRegions(tableName).get(0);
        Assert.assertEquals(1, region.getStoreFileList(new byte[][]{ familyNameBytes }).size());
        Assert.assertEquals(0, getStoreEngine().getStoreFileManager().getCompactedfiles().size());
    }
}

