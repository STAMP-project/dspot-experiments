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


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.RegionSplitter;
import org.apache.hadoop.hbase.util.RegionSplitter.SplitAlgorithm;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An integration test to detect regressions in HBASE-7220. Create
 * a table with many regions and verify it completes within a
 * reasonable amount of time.
 *
 * @see <a href="https://issues.apache.org/jira/browse/HBASE-7220">HBASE-7220</a>
 */
@Category(IntegrationTests.class)
public class IntegrationTestManyRegions {
    private static final String CLASS_NAME = IntegrationTestManyRegions.class.getSimpleName();

    protected static final Logger LOG = LoggerFactory.getLogger(IntegrationTestManyRegions.class);

    protected static final TableName TABLE_NAME = TableName.valueOf(IntegrationTestManyRegions.CLASS_NAME);

    protected static final String REGION_COUNT_KEY = String.format("hbase.%s.regions", IntegrationTestManyRegions.CLASS_NAME);

    protected static final String REGIONSERVER_COUNT_KEY = String.format("hbase.%s.regionServers", IntegrationTestManyRegions.CLASS_NAME);

    protected static final String TIMEOUT_MINUTES_KEY = String.format("hbase.%s.timeoutMinutes", IntegrationTestManyRegions.CLASS_NAME);

    protected static final IntegrationTestingUtility UTIL = new IntegrationTestingUtility();

    protected static final int DEFAULT_REGION_COUNT = 1000;

    protected static final int REGION_COUNT = getConfiguration().getInt(IntegrationTestManyRegions.REGION_COUNT_KEY, IntegrationTestManyRegions.DEFAULT_REGION_COUNT);

    protected static final int DEFAULT_REGIONSERVER_COUNT = 1;

    protected static final int REGION_SERVER_COUNT = getConfiguration().getInt(IntegrationTestManyRegions.REGIONSERVER_COUNT_KEY, IntegrationTestManyRegions.DEFAULT_REGIONSERVER_COUNT);

    // running on laptop, consistently takes about 2.5 minutes.
    // A timeout of 5 minutes should be reasonably safe.
    protected static final int DEFAULT_TIMEOUT_MINUTES = 5;

    protected static final int TIMEOUT_MINUTES = getConfiguration().getInt(IntegrationTestManyRegions.TIMEOUT_MINUTES_KEY, IntegrationTestManyRegions.DEFAULT_TIMEOUT_MINUTES);

    // This timeout is suitable since there is only single testcase in this test.
    @ClassRule
    public static final TestRule timeout = Timeout.builder().withTimeout(IntegrationTestManyRegions.TIMEOUT_MINUTES, TimeUnit.MINUTES).withLookingForStuckThread(true).build();

    private Admin admin;

    @Test
    public void testCreateTableWithRegions() throws Exception {
        HTableDescriptor desc = new HTableDescriptor(IntegrationTestManyRegions.TABLE_NAME);
        desc.addFamily(new HColumnDescriptor("cf"));
        SplitAlgorithm algo = new RegionSplitter.HexStringSplit();
        byte[][] splits = algo.split(IntegrationTestManyRegions.REGION_COUNT);
        IntegrationTestManyRegions.LOG.info(String.format("Creating table %s with %d splits.", IntegrationTestManyRegions.TABLE_NAME, IntegrationTestManyRegions.REGION_COUNT));
        long startTime = System.currentTimeMillis();
        try {
            admin.createTable(desc, splits);
            IntegrationTestManyRegions.LOG.info(String.format("Pre-split table created successfully in %dms.", ((System.currentTimeMillis()) - startTime)));
        } catch (IOException e) {
            IntegrationTestManyRegions.LOG.error("Failed to create table", e);
        }
    }
}

