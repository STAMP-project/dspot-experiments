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
package org.apache.hadoop.hbase.io.encoding;


import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests changing data block encoding settings of a column family.
 */
@Category({ IOTests.class, LargeTests.class })
public class TestChangingEncoding {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestChangingEncoding.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestChangingEncoding.class);

    static final String CF = "EncodingTestCF";

    static final byte[] CF_BYTES = Bytes.toBytes(TestChangingEncoding.CF);

    private static final int NUM_ROWS_PER_BATCH = 100;

    private static final int NUM_COLS_PER_ROW = 20;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Configuration conf = TestChangingEncoding.TEST_UTIL.getConfiguration();

    private static final int TIMEOUT_MS = 600000;

    private HColumnDescriptor hcd;

    private TableName tableName;

    private static final List<DataBlockEncoding> ENCODINGS_TO_ITERATE = TestChangingEncoding.createEncodingsToIterate();

    /**
     * A zero-based index of the current batch of test data being written
     */
    private int numBatchesWritten;

    @Test
    public void testChangingEncoding() throws Exception {
        prepareTest("ChangingEncoding");
        for (boolean onlineChange : new boolean[]{ false, true }) {
            for (DataBlockEncoding encoding : TestChangingEncoding.ENCODINGS_TO_ITERATE) {
                setEncodingConf(encoding, onlineChange);
                writeSomeNewData();
                verifyAllData();
            }
        }
    }

    @Test
    public void testChangingEncodingWithCompaction() throws Exception {
        prepareTest("ChangingEncodingWithCompaction");
        for (boolean onlineChange : new boolean[]{ false, true }) {
            for (DataBlockEncoding encoding : TestChangingEncoding.ENCODINGS_TO_ITERATE) {
                setEncodingConf(encoding, onlineChange);
                writeSomeNewData();
                verifyAllData();
                compactAndWait();
                verifyAllData();
            }
        }
    }

    @Test
    public void testCrazyRandomChanges() throws Exception {
        prepareTest("RandomChanges");
        Random rand = new Random(2934298742974297L);
        for (int i = 0; i < 10; ++i) {
            int encodingOrdinal = rand.nextInt(DataBlockEncoding.values().length);
            DataBlockEncoding encoding = DataBlockEncoding.values()[encodingOrdinal];
            setEncodingConf(encoding, rand.nextBoolean());
            writeSomeNewData();
            verifyAllData();
        }
    }
}

