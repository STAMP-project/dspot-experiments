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
package org.apache.hadoop.hbase.util;


import Compression.Algorithm;
import HConstants.HREGION_MEMSTORE_FLUSH_SIZE;
import HConstants.LOAD_BALANCER_SLOP_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A write/read/verify load test on a mini HBase cluster. Tests reading
 * and then writing.
 */
@Category({ MiscTests.class, LargeTests.class })
@RunWith(Parameterized.class)
public class TestMiniClusterLoadSequential {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMiniClusterLoadSequential.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMiniClusterLoadSequential.class);

    protected static final TableName TABLE = TableName.valueOf("load_test_tbl");

    protected static final byte[] CF = Bytes.toBytes("load_test_cf");

    protected static final int NUM_THREADS = 8;

    protected static final int NUM_RS = 2;

    protected static final int TIMEOUT_MS = 180000;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected final Configuration conf = TestMiniClusterLoadSequential.TEST_UTIL.getConfiguration();

    protected final boolean isMultiPut;

    protected final DataBlockEncoding dataBlockEncoding;

    protected MultiThreadedWriter writerThreads;

    protected MultiThreadedReader readerThreads;

    protected int numKeys;

    protected Algorithm compression = Algorithm.NONE;

    public TestMiniClusterLoadSequential(boolean isMultiPut, DataBlockEncoding dataBlockEncoding) {
        this.isMultiPut = isMultiPut;
        this.dataBlockEncoding = dataBlockEncoding;
        conf.setInt(HREGION_MEMSTORE_FLUSH_SIZE, (1024 * 1024));
        // We don't want any region reassignments by the load balancer during the test.
        conf.setFloat(LOAD_BALANCER_SLOP_KEY, 10.0F);
    }

    @Test
    public void loadTest() throws Exception {
        prepareForLoadTest();
        runLoadTestOnExistingTable();
    }
}

