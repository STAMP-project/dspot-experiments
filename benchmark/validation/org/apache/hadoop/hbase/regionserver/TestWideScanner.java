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


import HRegion.RegionScannerImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestCase;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestWideScanner extends HBaseTestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWideScanner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWideScanner.class);

    static final byte[] A = Bytes.toBytes("A");

    static final byte[] B = Bytes.toBytes("B");

    static final byte[] C = Bytes.toBytes("C");

    static byte[][] COLUMNS = new byte[][]{ TestWideScanner.A, TestWideScanner.B, TestWideScanner.C };

    static final Random rng = new Random();

    static final HTableDescriptor TESTTABLEDESC = new HTableDescriptor(TableName.valueOf("testwidescan"));

    static {
        for (byte[] cfName : new byte[][]{ TestWideScanner.A, TestWideScanner.B, TestWideScanner.C }) {
            TestWideScanner.TESTTABLEDESC.addFamily(// Keep versions to help debugging.
            new HColumnDescriptor(cfName).setMaxVersions(100).setBlocksize((8 * 1024)));
        }
    }

    /**
     * HRegionInfo for root region
     */
    HRegion r;

    @Test
    public void testWideScanBatching() throws IOException {
        final int batch = 256;
        try {
            this.r = createNewHRegion(TestWideScanner.TESTTABLEDESC, null, null);
            int inserted = addWideContent(this.r);
            List<Cell> results = new ArrayList<>();
            Scan scan = new Scan();
            scan.addFamily(TestWideScanner.A);
            scan.addFamily(TestWideScanner.B);
            scan.addFamily(TestWideScanner.C);
            scan.setMaxVersions(100);
            scan.setBatch(batch);
            InternalScanner s = r.getScanner(scan);
            int total = 0;
            int i = 0;
            boolean more;
            do {
                more = s.next(results);
                i++;
                TestWideScanner.LOG.info(((("iteration #" + i) + ", results.size=") + (results.size())));
                // assert that the result set is no larger
                TestCase.assertTrue(((results.size()) <= batch));
                total += results.size();
                if ((results.size()) > 0) {
                    // assert that all results are from the same row
                    byte[] row = CellUtil.cloneRow(results.get(0));
                    for (Cell kv : results) {
                        TestCase.assertTrue(Bytes.equals(row, CellUtil.cloneRow(kv)));
                    }
                }
                results.clear();
                // trigger ChangedReadersObservers
                Iterator<KeyValueScanner> scanners = ((HRegion.RegionScannerImpl) (s)).storeHeap.getHeap().iterator();
                while (scanners.hasNext()) {
                    StoreScanner ss = ((StoreScanner) (scanners.next()));
                    ss.updateReaders(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
                } 
            } while (more );
            // assert that the scanner returned all values
            TestWideScanner.LOG.info(((("inserted " + inserted) + ", scanned ") + total));
            TestCase.assertEquals(total, inserted);
            s.close();
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.r);
        }
    }
}

