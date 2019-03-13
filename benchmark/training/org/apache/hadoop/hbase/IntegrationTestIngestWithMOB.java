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


/**
 * Integration Test for MOB ingest.
 */
@Category(IntegrationTests.class)
public class IntegrationTestIngestWithMOB extends IntegrationTestIngest {
    private static final char COLON = ':';

    private byte[] mobColumnFamily = HFileTestUtil.DEFAULT_COLUMN_FAMILY;

    public static final String THRESHOLD = "threshold";

    public static final String MIN_MOB_DATA_SIZE = "minMobDataSize";

    public static final String MAX_MOB_DATA_SIZE = "maxMobDataSize";

    private int threshold = 1024;// 1KB


    private int minMobDataSize = 512;// 512B


    private int maxMobDataSize = (threshold) * 5;// 5KB


    private static final long JUNIT_RUN_TIME = (2 * 60) * 1000;// 2 minutes


    // similar to LOAD_TEST_TOOL_INIT_ARGS except OPT_IN_MEMORY is removed
    protected String[] LOAD_TEST_TOOL_MOB_INIT_ARGS = new String[]{ LoadTestTool.OPT_COMPRESSION, HFileTestUtil.OPT_DATA_BLOCK_ENCODING, LoadTestTool.OPT_ENCRYPTION, LoadTestTool.OPT_NUM_REGIONS_PER_SERVER, LoadTestTool.OPT_REGION_REPLICATION };

    @Test
    public void testIngest() throws Exception {
        runIngestTest(IntegrationTestIngestWithMOB.JUNIT_RUN_TIME, 100, 10, 1024, 10, 20);
    }
}

