/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.test;


import ExecConstants.MAX_QUERY_MEMORY_PER_NODE_KEY;
import ExecConstants.SLICE_TARGET;
import ExecConstants.SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE;
import PlannerSettings.EXCHANGE;
import PlannerSettings.HASHAGG;
import TestTools.SAMPLE_DATA;
import TestTools.TEST_RESOURCES_ABS;
import TypeProtos.MinorType.VARCHAR;
import java.io.File;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.memory.RootAllocator;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.file.JsonFileBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Example test case using the Drill cluster fixture. Your test case
 * can be stand-alone (as here) or can inherit from DrillTest if you
 * want test-by-test messages. Don't use BaseTestQuery, it will attempt
 * to set up a Drillbit for you, which is not needed here.
 * <p>
 * There is nothing magic about running these items as tests, other than
 * that JUnit makes it very easy to run one test at a time. You can also
 * just launch the test as a Java program as shown in the <tt>main()</tt>
 * method at the end of the file.
 * <p>
 * Note also that each test sets up its own Drillbit. Of course, if you
 * have a series of test that all use the same Drilbit configuration,
 * you can create your cluster fixture in a JUnit <tt>{@literal @}Before</tt>
 * method, and shut it down in <tt>{@literal @}After</tt> method.
 * <p>
 */
// Note: Test itself is ignored because this is an example, not a
// real test.
@Ignore
public class ExampleTest {
    static final Logger logger = LoggerFactory.getLogger(ExampleTest.class);

    /**
     * This test watcher creates all the temp directories that are required for an integration test with a Drillbit. The
     * {@link ClusterFixture} and {@link BaseTestQuery} classes automatically configure their Drillbits to use the temp
     * directories created by this test watcher. Please see {@link BaseDirTestWatcher} and package-info.java. Please see
     * {@link #secondTest()} for an example.
     */
    @Rule
    public final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    /**
     * Example of the simplest possible test case: set up a default
     * cluster (with one Drillbit), a corresponding client, run a
     * query and print the results.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void firstTest() throws Exception {
        try (ClusterFixture cluster = ClusterFixture.standardCluster(dirTestWatcher);ClientFixture client = cluster.clientFixture()) {
            client.queryBuilder().sql("SELECT * FROM `cp`.`employee.json` LIMIT 10").logCsv();
        }
    }

    /**
     * <p>
     *   Example that uses the fixture builder to build a cluster fixture. Lets
     *   you set configuration (boot-time) options, session options, system options
     *   and more.
     * </p>
     * <p>
     *   You can write test files to the {@link BaseDirTestWatcher#getRootDir()} and query them in the test.
     * </p>
     * <p>
     *   Also shows how to display the plan JSON and just run a query silently,
     *   getting just the row count, batch count and run time.
     * </p>
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void secondTest() throws Exception {
        try (RootAllocator allocator = new RootAllocator(100000000)) {
            final File tableFile = dirTestWatcher.getRootDir().toPath().resolve("employee.json").toFile();
            final BatchSchema schema = new SchemaBuilder().add("id", Types.required(VARCHAR)).add("name", Types.required(VARCHAR)).build();
            final RowSet rowSet = new RowSetBuilder(allocator, schema).addRow("1", "kiwi").addRow("2", "watermelon").build();
            new JsonFileBuilder(rowSet).build(tableFile);
            rowSet.clear();
            ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).configProperty(SLICE_TARGET, 10);
            try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
                String sql = "SELECT * FROM `dfs`.`test/employee.json`";
                ExampleTest.logger.info(client.queryBuilder().sql(sql).explainJson());
                QueryBuilder.QuerySummary results = client.queryBuilder().sql(sql).run();
                ExampleTest.logger.info(String.format("Read %d rows", results.recordCount()));
                // Usually we want to test something. Here, just test that we got
                // the 2 records.
                Assert.assertEquals(2, results.recordCount());
            }
        }
    }

    /**
     * Example test using the SQL mock data source. For now, we support just two
     * column types:
     * <ul>
     * <li>Integer: _i</li>
     * <li>String (Varchar): _sn, where n is the field width.</li>
     * </ul>
     * Row count is encoded in the table name with an optional "K" or "M"
     * suffix for bigger row count numbers.
     * <p>
     * The mock data source is defined automatically by the cluster fixture.
     * <p>
     * There is another, more sophisticated, way to generate test data using
     * a mock data source defined in a JSON file. We'll add an example for
     * that later.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void thirdTest() throws Exception {
        try (ClusterFixture cluster = ClusterFixture.standardCluster(dirTestWatcher);ClientFixture client = cluster.clientFixture()) {
            String sql = "SELECT id_i, name_s10 FROM `mock`.`employees_5`";
            client.queryBuilder().sql(sql).logCsv();
        }
    }

    /**
     * Example using custom logging. Here we run a sort with trace logging enabled
     * for just the sort class, and with logging displayed to the console.
     * <p>
     * This example also shows setting up a realistic set of options prior to
     * running a query. Note that we pass in normal Java values (don't have to
     * encode the values as a string.)
     * <p>
     * Finally, also shows defining your own ad-hoc local file workspace to
     * point to a sample data file.
     * <p>
     * Unlike the other tests, don't actually run this one. It points to
     * a location on a local machine. And, the query itself takes 23 minutes
     * to run if you had the right data file...
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void fourthTest() throws Exception {
        LogFixture.LogFixtureBuilder logBuilder = // And trace messages for one class.
        // All debug messages in the xsort package
        // Log to the console for debugging convenience
        LogFixture.builder().toConsole().logger("org.apache.drill.exec.physical.impl.xsort", Level.DEBUG).logger(org.apache.drill.exec.physical.impl.xsort.managed.ExternalSortBatch.class, Level.TRACE);
        ClusterFixtureBuilder builder = // Set some session options
        // Easy way to run single threaded for easy debugging
        ClusterFixture.builder(dirTestWatcher).maxParallelization(1).sessionOption(MAX_QUERY_MEMORY_PER_NODE_KEY, (((2L * 1024) * 1024) * 1024)).sessionOption(EXCHANGE.getOptionName(), true).sessionOption(HASHAGG.getOptionName(), false);
        try (LogFixture logs = logBuilder.build();ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            setupFile();
            cluster.defineWorkspace("dfs", "data", "/tmp/drill-test", "psv");
            String sql = "select * from `dfs.data`.`example.tbl` order by columns[0]";
            QueryBuilder.QuerySummary results = client.queryBuilder().sql(sql).run();
            Assert.assertEquals(2, results.recordCount());
        }
    }

    /**
     * Example of a more realistic test that limits parallization, saves the query
     * profile, parses it, and displays the runtime timing results per operator.
     *
     * @throws Exception
     * 		if anything goes wrong
     */
    @Test
    public void fifthTest() throws Exception {
        ClusterFixtureBuilder builder = ClusterFixture.builder(dirTestWatcher).maxParallelization(1).configProperty(SYS_STORE_PROVIDER_LOCAL_ENABLE_WRITE, true);
        try (ClusterFixture cluster = builder.build();ClientFixture client = cluster.clientFixture()) {
            String sql = "SELECT id_i, name_s10 FROM `mock`.`employees_10K` ORDER BY id_i";
            QueryBuilder.QuerySummary summary = client.queryBuilder().sql(sql).run();
            ExampleTest.logger.info(String.format("Results: %,d records, %d batches, %,d ms", summary.recordCount(), summary.batchCount(), summary.runTimeMs()));
            ExampleTest.logger.info(("Query ID: " + (summary.queryIdString())));
            ProfileParser profile = client.parseProfile(summary.queryIdString());
            profile.print();
        }
    }

    /**
     * This example shows how to define a workspace that points to test files in src/main/resources.
     */
    @Test
    public void sixthTest() throws Exception {
        try (ClusterFixture cluster = ClusterFixture.standardCluster(dirTestWatcher);ClientFixture client = cluster.clientFixture()) {
            cluster.defineWorkspace("dfs", "resources", TEST_RESOURCES_ABS.toFile().getAbsolutePath(), "tsv");
            client.queryBuilder().sql("SELECT * from dfs.resources.`testframework/small_test_data.tsv`").logCsv();
        }
    }

    /**
     * This example shows how to define a workspace that points to test files in the sample-data folder.
     */
    @Test
    public void seventhTest() throws Exception {
        try (ClusterFixture cluster = ClusterFixture.standardCluster(dirTestWatcher);ClientFixture client = cluster.clientFixture()) {
            cluster.defineWorkspace("dfs", "sampledata", SAMPLE_DATA.toFile().getAbsolutePath(), "parquet");
            client.queryBuilder().sql("SELECT * from dfs.sampledata.`nation.parquet`").logCsv();
        }
    }
}

