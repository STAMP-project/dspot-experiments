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


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An integration test to detect regressions in HBASE-7912. Create
 * a table with many regions, load data, perform series backup/load operations,
 * then restore and verify data
 *
 * @see <a href="https://issues.apache.org/jira/browse/HBASE-7912">HBASE-7912</a>
 * @see <a href="https://issues.apache.org/jira/browse/HBASE-14123">HBASE-14123</a>
 */
@Category(IntegrationTests.class)
public class IntegrationTestBackupRestore extends IntegrationTestBase {
    private static final String CLASS_NAME = IntegrationTestBackupRestore.class.getSimpleName();

    protected static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBackupRestore.class);

    protected static final String NUMBER_OF_TABLES_KEY = "num_tables";

    protected static final String COLUMN_NAME = "f";

    protected static final String REGION_COUNT_KEY = "regions_per_rs";

    protected static final String REGIONSERVER_COUNT_KEY = "region_servers";

    protected static final String ROWS_PER_ITERATION_KEY = "rows_in_iteration";

    protected static final String NUM_ITERATIONS_KEY = "num_iterations";

    protected static final int DEFAULT_REGION_COUNT = 10;

    protected static final int DEFAULT_REGIONSERVER_COUNT = 5;

    protected static final int DEFAULT_NUMBER_OF_TABLES = 1;

    protected static final int DEFAULT_NUM_ITERATIONS = 10;

    protected static final int DEFAULT_ROWS_IN_ITERATION = 500000;

    protected static final String SLEEP_TIME_KEY = "sleeptime";

    // short default interval because tests don't run very long.
    protected static final long SLEEP_TIME_DEFAULT = 50000L;

    protected static int rowsInIteration;

    protected static int regionsCountPerServer;

    protected static int regionServerCount;

    protected static int numIterations;

    protected static int numTables;

    protected static TableName[] tableNames;

    protected long sleepTime;

    protected static Object lock = new Object();

    private static String BACKUP_ROOT_DIR = "backupIT";

    @Test
    public void testBackupRestore() throws Exception {
        IntegrationTestBackupRestore.BACKUP_ROOT_DIR = ((getDataTestDirOnTestFS()) + (Path.SEPARATOR)) + (IntegrationTestBackupRestore.BACKUP_ROOT_DIR);
        createTables();
        runTestMulti();
    }
}

