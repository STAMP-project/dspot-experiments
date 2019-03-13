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
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.HFileTestUtil;
import org.apache.hadoop.hbase.util.LoadTestTool;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A base class for tests that do something with the cluster while running
 * {@link LoadTestTool} to write and verify some data.
 */
@Category(IntegrationTests.class)
public class IntegrationTestIngest extends IntegrationTestBase {
    public static final char HIPHEN = '-';

    private static final int SERVER_COUNT = 1;// number of slaves for the smallest cluster


    protected static final long DEFAULT_RUN_TIME = (20 * 60) * 1000;

    protected static final long JUNIT_RUN_TIME = (10 * 60) * 1000;

    /**
     * A soft limit on how long we should run
     */
    protected static final String RUN_TIME_KEY = "hbase.%s.runtime";

    protected static final String NUM_KEYS_PER_SERVER_KEY = "num_keys_per_server";

    protected static final long DEFAULT_NUM_KEYS_PER_SERVER = 2500;

    protected static final String NUM_WRITE_THREADS_KEY = "num_write_threads";

    protected static final int DEFAULT_NUM_WRITE_THREADS = 20;

    protected static final String NUM_READ_THREADS_KEY = "num_read_threads";

    protected static final int DEFAULT_NUM_READ_THREADS = 20;

    // Log is being used in IntegrationTestIngestWithEncryption, hence it is protected
    protected static final Logger LOG = LoggerFactory.getLogger(IntegrationTestIngest.class);

    protected IntegrationTestingUtility util;

    protected HBaseCluster cluster;

    protected LoadTestTool loadTool;

    protected String[] LOAD_TEST_TOOL_INIT_ARGS = new String[]{ LoadTestTool.OPT_COLUMN_FAMILIES, LoadTestTool.OPT_COMPRESSION, HFileTestUtil.OPT_DATA_BLOCK_ENCODING, LoadTestTool.OPT_INMEMORY, LoadTestTool.OPT_ENCRYPTION, LoadTestTool.OPT_NUM_REGIONS_PER_SERVER, LoadTestTool.OPT_REGION_REPLICATION };

    @Test
    public void testIngest() throws Exception {
        runIngestTest(IntegrationTestIngest.JUNIT_RUN_TIME, 2500, 10, 1024, 10, 20);
    }
}

