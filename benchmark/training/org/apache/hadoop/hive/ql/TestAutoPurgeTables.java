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
package org.apache.hadoop.hive.ql;


import java.sql.Connection;
import java.sql.SQLException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("Flaky")
public class TestAutoPurgeTables {
    private static final String driverName = "org.apache.hive.jdbc.HiveDriver";

    private static final String testDbName = "auto_purge_test_db";

    // private static final String testTableName = "auto_purge_test_table";
    private static final String INSERT_OVERWRITE_COMMAND_FORMAT = ("insert overwrite table " + (TestAutoPurgeTables.testDbName)) + ".%s select 1, \"test\"";

    private static final String TRUNCATE_TABLE_COMMAND_FORMAT = ("truncate table " + (TestAutoPurgeTables.testDbName)) + ".%s";

    private static final String partitionedColumnName = "partCol";

    private static final String partitionedColumnValue1 = "20090619";

    private static final String INSERT_OVERWRITE_COMMAND_PARTITIONED_FORMAT = (((((("insert overwrite table " + (TestAutoPurgeTables.testDbName)) + ".%s PARTITION (") + (TestAutoPurgeTables.partitionedColumnName)) + "=") + (TestAutoPurgeTables.partitionedColumnValue1)) + ")") + " select 1, \"test\"";

    private static final String partitionedColumnValue2 = "20100720";

    private static HiveConf conf;

    private static Connection con;

    private static MiniHS2 miniHS2;

    private static final Logger LOG = LoggerFactory.getLogger("TestAutoPurgeTables");

    @Rule
    public TestName name = new TestName();

    /**
     * Tests if previous table data skips trash when insert overwrite table .. is run against a table
     * which has auto.purge property set
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("true", false, false, false, name.getMethodName());
    }

    /**
     * Tests when auto.purge is set to a invalid string, trash should be used for insert overwrite
     * queries
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAutoPurgeInvalid() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("invalid", false, false, false, name.getMethodName());
    }

    /**
     * Test when auto.purge property is not set. Data should be moved to trash for insert overwrite
     * queries
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAutoPurgeUnset() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(null, false, false, false, name.getMethodName());
    }

    /**
     * Tests if the auto.purge property works correctly for external tables. Old data should skip
     * trash when insert overwrite table .. is run when auto.purge is set to true
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExternalTable() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("true", true, false, false, name.getMethodName());
    }

    /**
     * Tests auto.purge when managed table is partitioned. Old data should skip trash when insert
     * overwrite table .. is run and auto.purge property is set to true
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartitionedTable() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("true", false, true, false, name.getMethodName());
    }

    /**
     * Tests auto.purge for an external, partitioned table. Old partition data should skip trash when
     * auto.purge is set to true
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExternalPartitionedTable() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("true", true, true, false, name.getMethodName());
    }

    /**
     * Tests when auto.purge is set to false, older data is moved to Trash when insert overwrite table
     * .. is run
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("false", false, false, false, name.getMethodName());
    }

    /**
     * Tests when auto.purge is set to false on a external table, older data is moved to Trash when
     * insert overwrite table .. is run
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExternalNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("false", true, false, false, name.getMethodName());
    }

    /**
     * Tests when auto.purge is set to false on a partitioned table, older data is moved to Trash when
     * insert overwrite table .. is run
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartitionedNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("false", false, true, false, name.getMethodName());
    }

    /**
     * Tests when auto.purge is set to false on a partitioned external table, older data is moved to
     * Trash when insert overwrite table .. is run
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartitionedExternalNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("false", true, true, false, name.getMethodName());
    }

    // truncate on external table is not allowed
    @Test(expected = SQLException.class)
    public void testTruncatePartitionedExternalNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(false), true, true, true, name.getMethodName());
    }

    // truncate on external table is not allowed
    @Test(expected = SQLException.class)
    public void testTruncateExternalNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(false), true, false, true, name.getMethodName());
    }

    @Test
    public void testTruncatePartitionedNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(false), false, true, true, name.getMethodName());
    }

    @Test
    public void testTruncateNoAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(false), false, false, true, name.getMethodName());
    }

    @Test
    public void testTruncateInvalidAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil("invalid", false, false, true, name.getMethodName());
    }

    @Test
    public void testTruncateUnsetAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(null, false, false, true, name.getMethodName());
    }

    // truncate on external table is not allowed
    @Test(expected = SQLException.class)
    public void testTruncatePartitionedExternalAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(true), true, true, true, name.getMethodName());
    }

    // truncate on external table is not allowed
    @Test(expected = SQLException.class)
    public void testTruncateExternalAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(true), true, false, true, name.getMethodName());
    }

    @Test
    public void testTruncatePartitionedAutoPurge() throws Exception {
        TestAutoPurgeTables.LOG.info(("Running " + (name.getMethodName())));
        testUtil(String.valueOf(true), false, true, true, name.getMethodName());
    }
}

