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


import HConstants.HREGION_MEMSTORE_BLOCK_MULTIPLIER;
import HConstants.HREGION_MEMSTORE_FLUSH_SIZE;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test minor compactions
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestMinorCompaction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMinorCompaction.class);

    @Rule
    public TestName name = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestMinorCompaction.class.getName());

    private static final HBaseTestingUtility UTIL = HBaseTestingUtility.createLocalHTU();

    protected Configuration conf = TestMinorCompaction.UTIL.getConfiguration();

    private HRegion r = null;

    private HTableDescriptor htd = null;

    private int compactionThreshold;

    private byte[] firstRowBytes;

    private byte[] secondRowBytes;

    private byte[] thirdRowBytes;

    private final byte[] col1;

    private final byte[] col2;

    /**
     * constructor
     */
    public TestMinorCompaction() {
        super();
        // Set cache flush size to 1MB
        conf.setInt(HREGION_MEMSTORE_FLUSH_SIZE, (1024 * 1024));
        conf.setInt(HREGION_MEMSTORE_BLOCK_MULTIPLIER, 100);
        compactionThreshold = conf.getInt("hbase.hstore.compactionThreshold", 3);
        firstRowBytes = HBaseTestingUtility.START_KEY_BYTES;
        secondRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        // Increment the least significant character so we get to next row.
        (secondRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)])++;
        thirdRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)] = ((byte) ((thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)]) + 2));
        col1 = Bytes.toBytes("column1");
        col2 = Bytes.toBytes("column2");
    }

    @Test
    public void testMinorCompactionWithDeleteRow() throws Exception {
        Delete deleteRow = new Delete(secondRowBytes);
        testMinorCompactionWithDelete(deleteRow);
    }

    @Test
    public void testMinorCompactionWithDeleteColumn1() throws Exception {
        Delete dc = new Delete(secondRowBytes);
        /* delete all timestamps in the column */
        dc.addColumns(HBaseTestingUtility.fam2, col2);
        testMinorCompactionWithDelete(dc);
    }

    @Test
    public void testMinorCompactionWithDeleteColumn2() throws Exception {
        Delete dc = new Delete(secondRowBytes);
        dc.addColumn(HBaseTestingUtility.fam2, col2);
        /* compactionThreshold is 3. The table has 4 versions: 0, 1, 2, and 3.
        we only delete the latest version. One might expect to see only
        versions 1 and 2. HBase differs, and gives us 0, 1 and 2.
        This is okay as well. Since there was no compaction done before the
        delete, version 0 seems to stay on.
         */
        testMinorCompactionWithDelete(dc, 3);
    }

    @Test
    public void testMinorCompactionWithDeleteColumnFamily() throws Exception {
        Delete deleteCF = new Delete(secondRowBytes);
        deleteCF.addFamily(HBaseTestingUtility.fam2);
        testMinorCompactionWithDelete(deleteCF);
    }

    @Test
    public void testMinorCompactionWithDeleteVersion1() throws Exception {
        Delete deleteVersion = new Delete(secondRowBytes);
        deleteVersion.addColumns(HBaseTestingUtility.fam2, col2, 2);
        /* compactionThreshold is 3. The table has 4 versions: 0, 1, 2, and 3.
        We delete versions 0 ... 2. So, we still have one remaining.
         */
        testMinorCompactionWithDelete(deleteVersion, 1);
    }

    @Test
    public void testMinorCompactionWithDeleteVersion2() throws Exception {
        Delete deleteVersion = new Delete(secondRowBytes);
        deleteVersion.addColumn(HBaseTestingUtility.fam2, col2, 1);
        /* the table has 4 versions: 0, 1, 2, and 3.
        We delete 1.
        Should have 3 remaining.
         */
        testMinorCompactionWithDelete(deleteVersion, 3);
    }
}

