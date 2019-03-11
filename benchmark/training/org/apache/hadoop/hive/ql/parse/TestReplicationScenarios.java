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
package org.apache.hadoop.hive.ql.parse;


import ErrorMsg.FILE_NOT_FOUND;
import ErrorMsg.REPL_EVENTS_MISSING_IN_METASTORE;
import ErrorMsg.REPL_FILE_MISSING_FROM_SRC_AND_CM_PATH;
import HiveConf.ConfVars.REPLCMDIR;
import HiveConf.ConfVars.STAGINGDIR;
import IMetaStoreClient.NotificationFilter;
import JSONMessageEncoder.FORMAT;
import MetastoreConf.ConfVars.EVENT_DB_NOTIFICATION_API_AUTH;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.InjectableBehaviourObjectStore;
import org.apache.hadoop.hive.metastore.InjectableBehaviourObjectStore.BehaviourInjection;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.ForeignKeysRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PrimaryKeysRequest;
import org.apache.hadoop.hive.metastore.api.SQLForeignKey;
import org.apache.hadoop.hive.metastore.api.SQLNotNullConstraint;
import org.apache.hadoop.hive.metastore.api.SQLPrimaryKey;
import org.apache.hadoop.hive.metastore.api.SQLUniqueConstraint;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.hadoop.hive.metastore.messaging.event.filters.DatabaseAndTableFilter;
import org.apache.hadoop.hive.metastore.messaging.event.filters.EventBoundaryFilter;
import org.apache.hadoop.hive.metastore.messaging.json.JSONMessageEncoder;
import org.apache.hadoop.hive.ql.DriverFactory;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.parse.repl.load.EventDumpDirComparator;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.ql.stats.StatsUtils;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestReplicationScenarios {
    @Rule
    public final TestName testName = new TestName();

    private static final String DBNOTIF_LISTENER_CLASSNAME = "org.apache.hive.hcatalog.listener.DbNotificationListener";

    // FIXME : replace with hive copy once that is copied
    private static final String tid = ((TestReplicationScenarios.class.getCanonicalName().toLowerCase().replace('.', '_')) + "_") + (System.currentTimeMillis());

    private static final String TEST_PATH = ((System.getProperty("test.warehouse.dir", "/tmp")) + (Path.SEPARATOR)) + (TestReplicationScenarios.tid);

    static HiveConf hconf;

    static HiveMetaStoreClient metaStoreClient;

    private static IDriver driver;

    private static String proxySettingName;

    private static HiveConf hconfMirror;

    private static IDriver driverMirror;

    private static HiveMetaStoreClient metaStoreClientMirror;

    private static boolean isMigrationTest;

    // Make sure we skip backward-compat checking for those tests that don't generate events
    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationScenarios.class);

    private ArrayList<String> lastResults;

    private final boolean VERIFY_SETUP_STEPS = false;

    private static int next = 0;

    static class Tuple {
        final String dumpLocation;

        final String lastReplId;

        Tuple(String dumpLocation, String lastReplId) {
            this.dumpLocation = dumpLocation;
            this.lastReplId = lastReplId;
        }
    }

    /**
     * Tests basic operation - creates a db, with 4 tables, 2 ptned and 2 unptned.
     * Inserts data into one of the ptned tables, and one of the unptned tables,
     * and verifies that a REPL DUMP followed by a REPL LOAD is able to load it
     * appropriately. This tests bootstrap behaviour primarily.
     */
    @Test
    public void testBasic() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned_empty(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_empty(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_empty"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_empty"), empty, TestReplicationScenarios.driver);
        String replicatedDbName = dbName + "_dupe";
        bootstrapLoadAndVerify(dbName, replicatedDbName);
        verifyRun((("SELECT * from " + replicatedDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replicatedDbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replicatedDbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replicatedDbName) + ".ptned_empty"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replicatedDbName) + ".unptned_empty"), empty, TestReplicationScenarios.driverMirror);
    }

    private abstract class checkTaskPresent {
        public boolean hasTask(Task rootTask) {
            if (rootTask == null) {
                return false;
            }
            if (validate(rootTask)) {
                return true;
            }
            List<Task<? extends Serializable>> childTasks = rootTask.getChildTasks();
            if (childTasks == null) {
                return false;
            }
            for (Task<? extends Serializable> childTask : childTasks) {
                if (hasTask(childTask)) {
                    return true;
                }
            }
            return false;
        }

        public abstract boolean validate(Task task);
    }

    @Test
    public void testTaskCreationOptimization() throws Throwable {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String dbNameReplica = dbName + "_replica";
        TestReplicationScenarios.run((("create table " + dbName) + ".t2 (place string) partitioned by (country string)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("insert into table " + dbName) + ".t2 partition(country='india') values ('bangalore')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple dump = replDumpDb(dbName, null, null, null);
        // bootstrap load should not have move task
        Task task = getReplLoadRootTask(dbNameReplica, false, dump);
        Assert.assertEquals(false, hasMoveTask(task));
        Assert.assertEquals(true, hasPartitionTask(task));
        loadAndVerify(dbNameReplica, dump.dumpLocation, dump.lastReplId);
        TestReplicationScenarios.run((("insert into table " + dbName) + ".t2 partition(country='india') values ('delhi')"), TestReplicationScenarios.driver);
        dump = replDumpDb(dbName, dump.lastReplId, null, null);
        // Partition level statistics gets updated as part of the INSERT above. So we see a partition
        // task corresponding to an ALTER_PARTITION event.
        task = getReplLoadRootTask(dbNameReplica, true, dump);
        Assert.assertEquals(true, hasMoveTask(task));
        Assert.assertEquals(true, hasPartitionTask(task));
        loadAndVerify(dbNameReplica, dump.dumpLocation, dump.lastReplId);
        TestReplicationScenarios.run((("insert into table " + dbName) + ".t2 partition(country='us') values ('sf')"), TestReplicationScenarios.driver);
        dump = replDumpDb(dbName, dump.lastReplId, null, null);
        // no move task should be added as the operation is adding a dynamic partition
        task = getReplLoadRootTask(dbNameReplica, true, dump);
        Assert.assertEquals(false, hasMoveTask(task));
        Assert.assertEquals(true, hasPartitionTask(task));
    }

    @Test
    public void testBasicWithCM() throws Exception {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned_empty(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_empty(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        String ptn_locn_2_later = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2_later")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_empty"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_empty"), empty, TestReplicationScenarios.driver);
        advanceDumpDir();
        TestReplicationScenarios.run(("REPL DUMP " + dbName), TestReplicationScenarios.driver);
        String replDumpLocn = getResult(0, 0, TestReplicationScenarios.driver);
        String replDumpId = getResult(0, 1, true, TestReplicationScenarios.driver);
        // Table dropped after "repl dump"
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        // Partition droppped after "repl dump"
        TestReplicationScenarios.run(((("ALTER TABLE " + dbName) + ".ptned ") + "DROP PARTITION(b=1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        verifyRun(("REPL STATUS " + replDbName), new String[]{ replDumpId }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_empty"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned_empty"), empty, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testBootstrapLoadOnExistingDb() throws IOException {
        String testName = "bootstrapLoadOnExistingDb";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        // Create an empty database to load
        TestReplicationScenarios.createDB((dbName + "_empty"), TestReplicationScenarios.driverMirror);
        // Load to an empty database
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, (dbName + "_empty"));
        String replDumpLocn = bootstrapDump.dumpLocation;
        verifyRun((("SELECT * from " + dbName) + "_empty.unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        String[] nullReplId = new String[]{ "NULL" };
        // Create a database with a table
        TestReplicationScenarios.createDB((dbName + "_withtable"), TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + "_withtable.unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driverMirror);
        // Load using same dump to a DB with table. It should fail as DB is not empty.
        verifyFail((((("REPL LOAD " + dbName) + "_withtable FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        // REPL STATUS should return NULL
        verifyRun((("REPL STATUS " + dbName) + "_withtable"), nullReplId, TestReplicationScenarios.driverMirror);
        // Create a database with a view
        TestReplicationScenarios.createDB((dbName + "_withview"), TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + "_withview.unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((((("CREATE VIEW " + dbName) + "_withview.view AS SELECT * FROM ") + dbName) + "_withview.unptned"), TestReplicationScenarios.driverMirror);
        // Load using same dump to a DB with view. It should fail as DB is not empty.
        verifyFail((((("REPL LOAD " + dbName) + "_withview FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        // REPL STATUS should return NULL
        verifyRun((("REPL STATUS " + dbName) + "_withview"), nullReplId, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testBootstrapWithConcurrentDropTable() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        advanceDumpDir();
        BehaviourInjection<Table, Table> ptnedTableNuller = new BehaviourInjection<Table, Table>() {
            @Nullable
            @Override
            public Table apply(@Nullable
            Table table) {
                TestReplicationScenarios.LOG.info(("Performing injection on table " + (table.getTableName())));
                if (table.getTableName().equalsIgnoreCase("ptned")) {
                    injectionPathCalled = true;
                    return null;
                } else {
                    nonInjectedPathCalled = true;
                    return table;
                }
            }
        };
        InjectableBehaviourObjectStore.setGetTableBehaviour(ptnedTableNuller);
        try {
            // The ptned table will not be dumped as getTable will return null
            TestReplicationScenarios.run(("REPL DUMP " + dbName), TestReplicationScenarios.driver);
            ptnedTableNuller.assertInjectionsPerformed(true, true);
        } finally {
            InjectableBehaviourObjectStore.resetGetTableBehaviour();// reset the behaviour

        }
        String replDumpLocn = getResult(0, 0, TestReplicationScenarios.driver);
        String replDumpId = getResult(0, 1, true, TestReplicationScenarios.driver);
        TestReplicationScenarios.LOG.info("Bootstrap-Dump: Dumped to {} with id {}", replDumpLocn, replDumpId);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        // The ptned table should miss in target as the table was marked virtually as dropped
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyFail((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), TestReplicationScenarios.driverMirror);
        verifyIfTableNotExist((replDbName + ""), "ptned", TestReplicationScenarios.metaStoreClient);
        // Verify if Drop table on a non-existing table is idempotent
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".ptned"), TestReplicationScenarios.driver);
        advanceDumpDir();
        TestReplicationScenarios.run(((("REPL DUMP " + dbName) + " FROM ") + replDumpId), TestReplicationScenarios.driver);
        String postDropReplDumpLocn = getResult(0, 0, TestReplicationScenarios.driver);
        String postDropReplDumpId = getResult(0, 1, true, TestReplicationScenarios.driver);
        TestReplicationScenarios.LOG.info("Dumped to {} with id {}->{}", postDropReplDumpLocn, replDumpId, postDropReplDumpId);
        assert TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + postDropReplDumpLocn) + "'"), true, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyIfTableNotExist(replDbName, "ptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyFail((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testBootstrapWithConcurrentDropPartition() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        advanceDumpDir();
        BehaviourInjection<List<String>, List<String>> listPartitionNamesNuller = new BehaviourInjection<List<String>, List<String>>() {
            @Nullable
            @Override
            public List<String> apply(@Nullable
            List<String> partitions) {
                injectionPathCalled = true;
                return new ArrayList<String>();
            }
        };
        InjectableBehaviourObjectStore.setListPartitionNamesBehaviour(listPartitionNamesNuller);
        try {
            // None of the partitions will be dumped as the partitions list was empty
            TestReplicationScenarios.run(("REPL DUMP " + dbName), TestReplicationScenarios.driver);
            listPartitionNamesNuller.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetListPartitionNamesBehaviour();// reset the behaviour

        }
        String replDumpLocn = getResult(0, 0, TestReplicationScenarios.driver);
        String replDumpId = getResult(0, 1, true, TestReplicationScenarios.driver);
        TestReplicationScenarios.LOG.info("Bootstrap-Dump: Dumped to {} with id {}", replDumpLocn, replDumpId);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        // All partitions should miss in target as it was marked virtually as dropped
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), empty, TestReplicationScenarios.driverMirror);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("1")), TestReplicationScenarios.metaStoreClientMirror);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("2")), TestReplicationScenarios.metaStoreClientMirror);
        // Verify if drop partition on a non-existing partition is idempotent and just a noop.
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION (b=1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION (b=2)"), TestReplicationScenarios.driver);
        advanceDumpDir();
        TestReplicationScenarios.run(((("REPL DUMP " + dbName) + " FROM ") + replDumpId), TestReplicationScenarios.driver);
        String postDropReplDumpLocn = getResult(0, 0, TestReplicationScenarios.driver);
        String postDropReplDumpId = getResult(0, 1, true, TestReplicationScenarios.driver);
        TestReplicationScenarios.LOG.info("Dumped to {} with id {}->{}", postDropReplDumpLocn, replDumpId, postDropReplDumpId);
        assert TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + postDropReplDumpLocn) + "'"), true, TestReplicationScenarios.driverMirror);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("1")), TestReplicationScenarios.metaStoreClientMirror);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("2")), TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), empty, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testBootstrapWithConcurrentRename() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] ptn_data = new String[]{ "eleven", "twelve" };
        String[] empty = new String[]{  };
        String ptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(ptn_locn, ptn_data);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        BehaviourInjection<Table, Table> ptnedTableRenamer = new BehaviourInjection<Table, Table>() {
            boolean success = false;

            @Nullable
            @Override
            public Table apply(@Nullable
            Table table) {
                if (injectionPathCalled) {
                    nonInjectedPathCalled = true;
                } else {
                    // getTable is invoked after fetching the table names
                    injectionPathCalled = true;
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TestReplicationScenarios.LOG.info("Entered new thread");
                            IDriver driver2 = DriverFactory.newDriver(TestReplicationScenarios.hconf);
                            SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(TestReplicationScenarios.hconf));
                            CommandProcessorResponse ret = driver2.run((("ALTER TABLE " + dbName) + ".ptned PARTITION (b=1) RENAME TO PARTITION (b=10)"));
                            success = (ret.getException()) == null;
                            Assert.assertFalse(success);
                            ret = driver2.run((((("ALTER TABLE " + dbName) + ".ptned RENAME TO ") + dbName) + ".ptned_renamed"));
                            success = (ret.getException()) == null;
                            Assert.assertFalse(success);
                            TestReplicationScenarios.LOG.info("Exit new thread success - {}", success);
                        }
                    });
                    t.start();
                    TestReplicationScenarios.LOG.info("Created new thread {}", t.getName());
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return table;
            }
        };
        InjectableBehaviourObjectStore.setGetTableBehaviour(ptnedTableRenamer);
        try {
            // The intermediate rename would've failed as bootstrap dump in progress
            bootstrapLoadAndVerify(dbName, replDbName);
            ptnedTableRenamer.assertInjectionsPerformed(true, true);
        } finally {
            InjectableBehaviourObjectStore.resetGetTableBehaviour();// reset the behaviour

        }
        // The ptned table should be there in both source and target as rename was not successful
        verifyRun((("SELECT a from " + dbName) + ".ptned WHERE (b=1) ORDER BY a"), ptn_data, TestReplicationScenarios.driver);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE (b=1) ORDER BY a"), ptn_data, TestReplicationScenarios.driverMirror);
        // Verify if Rename after bootstrap is successful
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned PARTITION (b=1) RENAME TO PARTITION (b=10)"), TestReplicationScenarios.driver);
        verifyIfPartitionNotExist(dbName, "ptned", new ArrayList(Arrays.asList("1")), TestReplicationScenarios.metaStoreClient);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned RENAME TO ") + dbName) + ".ptned_renamed"), TestReplicationScenarios.driver);
        verifyIfTableNotExist(dbName, "ptned", TestReplicationScenarios.metaStoreClient);
        verifyRun((("SELECT a from " + dbName) + ".ptned_renamed WHERE (b=10) ORDER BY a"), ptn_data, TestReplicationScenarios.driver);
    }

    @Test
    public void testBootstrapWithDropPartitionedTable() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] ptn_data = new String[]{ "eleven", "twelve" };
        String[] empty = new String[]{  };
        String ptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(ptn_locn, ptn_data);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        BehaviourInjection<Table, Table> ptnedTableRenamer = new BehaviourInjection<Table, Table>() {
            boolean success = false;

            @Nullable
            @Override
            public Table apply(@Nullable
            Table table) {
                if (injectionPathCalled) {
                    nonInjectedPathCalled = true;
                } else {
                    // getTable is invoked after fetching the table names
                    injectionPathCalled = true;
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TestReplicationScenarios.LOG.info("Entered new thread");
                            IDriver driver2 = DriverFactory.newDriver(TestReplicationScenarios.hconf);
                            SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(TestReplicationScenarios.hconf));
                            CommandProcessorResponse ret = driver2.run((("DROP TABLE " + dbName) + ".ptned"));
                            success = (ret.getException()) == null;
                            Assert.assertTrue(success);
                            TestReplicationScenarios.LOG.info("Exit new thread success - {}", success, ret.getException());
                        }
                    });
                    t.start();
                    TestReplicationScenarios.LOG.info("Created new thread {}", t.getName());
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return table;
            }
        };
        InjectableBehaviourObjectStore.setGetTableBehaviour(ptnedTableRenamer);
        TestReplicationScenarios.Tuple bootstrap = null;
        try {
            bootstrap = bootstrapLoadAndVerify(dbName, replDbName);
            ptnedTableRenamer.assertInjectionsPerformed(true, true);
        } finally {
            InjectableBehaviourObjectStore.resetGetTableBehaviour();// reset the behaviour

        }
        incrementalLoadAndVerify(dbName, bootstrap.lastReplId, replDbName);
        verifyIfTableNotExist(replDbName, "ptned", TestReplicationScenarios.metaStoreClientMirror);
    }

    @Test
    public void testIncrementalAdds() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned_empty(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_empty(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_empty"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned_empty"), empty, TestReplicationScenarios.driverMirror);
        // Now, we load data into the tables, and see if an incremental
        // repl drop/load can duplicate it.
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".unptned_late AS SELECT * from ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_late"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_late(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned_late PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_late WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned_late PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_late WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        // Perform REPL-DUMP/LOAD
        incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        // VERIFY tables and partitions on destination for equivalence.
        verifyRun((("SELECT * from " + replDbName) + ".unptned_empty"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_empty"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned_late"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_late WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_late WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIncrementalLoadWithVariableLengthEventId() throws IOException, TException {
        String testName = "incrementalLoadWithVariableLengthEventId";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO TABLE " + dbName) + ".unptned values('ten')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        // CREATE_TABLE - TRUNCATE - INSERT - The result is just one record.
        // Creating dummy table to control the event ID of TRUNCATE not to be 10 or 100 or 1000...
        String[] unptn_data = new String[]{ "eleven" };
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".dummy(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String incrementalDumpLocn = incrementalDump.dumpLocation;
        replDumpId = incrementalDump.lastReplId;
        // Rename the event directories such a way that the length varies.
        // We will encounter create_table, truncate followed by insert.
        // For the insert, set the event ID longer such that old comparator picks insert before truncate
        // Eg: Event IDs CREATE_TABLE - 5, TRUNCATE - 9, INSERT - 12 changed to
        // CREATE_TABLE - 5, TRUNCATE - 9, INSERT - 100
        // But if TRUNCATE have ID-10, then having INSERT-100 won't be sufficient to test the scenario.
        // So, we set any event comes after CREATE_TABLE starts with 20.
        // Eg: Event IDs CREATE_TABLE - 5, TRUNCATE - 10, INSERT - 12 changed to
        // CREATE_TABLE - 5, TRUNCATE - 20(20 <= Id < 100), INSERT - 100
        Path dumpPath = new Path(incrementalDumpLocn);
        FileSystem fs = dumpPath.getFileSystem(TestReplicationScenarios.hconf);
        FileStatus[] dirsInLoadPath = fs.listStatus(dumpPath, EximUtil.getDirectoryFilter(fs));
        Arrays.sort(dirsInLoadPath, new EventDumpDirComparator());
        long nextEventId = 0;
        for (FileStatus dir : dirsInLoadPath) {
            Path srcPath = dir.getPath();
            if (nextEventId == 0) {
                nextEventId = ((long) (Math.pow(10.0, ((double) (srcPath.getName().length()))))) * 2;
                continue;
            }
            Path destPath = new Path(srcPath.getParent(), String.valueOf(nextEventId));
            fs.rename(srcPath, destPath);
            TestReplicationScenarios.LOG.info("Renamed eventDir {} to {}", srcPath.getName(), destPath.getName());
            // Once the eventId reaches 5-20-100, then just increment it sequentially. This is to avoid longer values.
            if (((String.valueOf(nextEventId).length()) - (srcPath.getName().length())) >= 2) {
                nextEventId++;
            } else {
                nextEventId = ((long) (Math.pow(10.0, ((double) (String.valueOf(nextEventId).length())))));
            }
        }
        // Load from modified dump event directories.
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + incrementalDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIncrementalReplWithEventsMissing() throws IOException, TException {
        String testName = "incrementalReplWithEventsMissing";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        // CREATE_TABLE - INSERT - TRUNCATE - INSERT - The result is just one record.
        String[] unptn_data = new String[]{ "eleven" };
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO TABLE " + dbName) + ".unptned values('ten')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Inject a behaviour where some events missing from notification_log table.
        // This ensures the incremental dump doesn't get all events for replication.
        BehaviourInjection<NotificationEventResponse, NotificationEventResponse> eventIdSkipper = new BehaviourInjection<NotificationEventResponse, NotificationEventResponse>() {
            @Nullable
            @Override
            public NotificationEventResponse apply(@Nullable
            NotificationEventResponse eventIdList) {
                if (null != eventIdList) {
                    List<NotificationEvent> eventIds = eventIdList.getEvents();
                    List<NotificationEvent> outEventIds = new ArrayList<NotificationEvent>();
                    for (int i = 0; i < (eventIds.size()); i++) {
                        NotificationEvent event = eventIds.get(i);
                        // Skip all the INSERT events
                        if ((event.getDbName().equalsIgnoreCase(dbName)) && (event.getEventType().equalsIgnoreCase("INSERT"))) {
                            injectionPathCalled = true;
                            continue;
                        }
                        outEventIds.add(event);
                    }
                    // Return the new list
                    return new NotificationEventResponse(outEventIds);
                } else {
                    return null;
                }
            }
        };
        InjectableBehaviourObjectStore.setGetNextNotificationBehaviour(eventIdSkipper);
        try {
            advanceDumpDir();
            CommandProcessorResponse ret = TestReplicationScenarios.driver.run(((("REPL DUMP " + dbName) + " FROM ") + replDumpId));
            Assert.assertTrue(((ret.getResponseCode()) == (REPL_EVENTS_MISSING_IN_METASTORE.getErrorCode())));
            eventIdSkipper.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetGetNextNotificationBehaviour();// reset the behaviour

        }
    }

    @Test
    public void testDrops() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned2(a string) partitioned by (b string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned3(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b='1')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b='2')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b='1')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b='2')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned3 PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned3 PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        // At this point, we've set up all the tables and ptns we're going to test drops across
        // Replicate it first, and then we'll drop it on the source.
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2 WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2 WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned3 WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned3 WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // All tables good on destination, drop on source.
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION (b='2')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".ptned2"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned3 DROP PARTITION (b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='2'"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned3 WHERE b=1"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned3"), ptn_data_2, TestReplicationScenarios.driver);
        // replicate the incremental drops
        incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        // verify that drops were replicated. This can either be from tables or ptns
        // not existing, and thus, throwing a NoSuchObjectException, or returning nulls
        // or select * returning empty, depending on what we're testing.
        verifyIfTableNotExist(replDbName, "unptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='2'"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned3 WHERE b=1"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned3"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyIfTableNotExist(replDbName, "ptned2", TestReplicationScenarios.metaStoreClientMirror);
    }

    @Test
    public void testDropsWithCM() throws IOException {
        String testName = "drops_with_cm";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned2(a string) partitioned by (b string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b='1')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b='2')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b='1')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b='2')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2 WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2 WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run(((((("CREATE TABLE " + dbName) + ".unptned_copy") + " AS SELECT a FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run(((((("CREATE TABLE " + dbName) + ".ptned_copy") + " LIKE ") + dbName) + ".ptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run(((((("INSERT INTO TABLE " + dbName) + ".ptned_copy") + " PARTITION(b='1') SELECT a FROM ") + dbName) + ".ptned WHERE b='1'"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned_copy"), unptn_data, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_copy"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION (b='2')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".ptned2"), TestReplicationScenarios.driver);
        advanceDumpDir();
        TestReplicationScenarios.run(((("REPL DUMP " + dbName) + " FROM ") + replDumpId), TestReplicationScenarios.driver);
        String postDropReplDumpLocn = getResult(0, 0, TestReplicationScenarios.driver);
        String postDropReplDumpId = getResult(0, 1, true, TestReplicationScenarios.driver);
        TestReplicationScenarios.LOG.info("Dumped to {} with id {}->{}", postDropReplDumpLocn, replDumpId, postDropReplDumpId);
        // Drop table after dump
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".unptned_copy"), TestReplicationScenarios.driver);
        // Drop partition after dump
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned_copy DROP PARTITION(b='1')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + postDropReplDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        Exception e = null;
        try {
            Table tbl = TestReplicationScenarios.metaStoreClientMirror.getTable(replDbName, "unptned");
            Assert.assertNull(tbl);
        } catch (TException te) {
            e = te;
        }
        Assert.assertNotNull(e);
        Assert.assertEquals(NoSuchObjectException.class, e.getClass());
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyIfTableNotExist(replDbName, "ptned2", TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_copy"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_copy"), ptn_data_1, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testTableAlters() throws IOException {
        String testName = "TableAlters";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned2(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned2(a string) partitioned by (b string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned2"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned2"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b='1')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b='2')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b='1')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b='2')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driver);
        // base tables set up, let's replicate them over
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned2"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2 WHERE b='1'"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2 WHERE b='2'"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // tables have been replicated over, and verified to be identical. Now, we do a couple of
        // alters on the source
        // Rename unpartitioned table
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".unptned RENAME TO ") + dbName) + ".unptned_rn"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_rn"), unptn_data, TestReplicationScenarios.driver);
        // Alter unpartitioned table set table property
        String testKey = "blah";
        String testVal = "foo";
        TestReplicationScenarios.run((((((("ALTER TABLE " + dbName) + ".unptned2 SET TBLPROPERTIES ('") + testKey) + "' = '") + testVal) + "')"), TestReplicationScenarios.driver);
        if (VERIFY_SETUP_STEPS) {
            try {
                Table unptn2 = TestReplicationScenarios.metaStoreClient.getTable(dbName, "unptned2");
                Assert.assertTrue(unptn2.getParameters().containsKey(testKey));
                Assert.assertEquals(testVal, unptn2.getParameters().get(testKey));
            } catch (TException e) {
                Assert.assertNull(e);
            }
        }
        // alter partitioned table, rename partition
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned PARTITION (b='2') RENAME TO PARTITION (b='22')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=22"), ptn_data_2, TestReplicationScenarios.driver);
        // alter partitioned table set table property
        TestReplicationScenarios.run((((((("ALTER TABLE " + dbName) + ".ptned SET TBLPROPERTIES ('") + testKey) + "' = '") + testVal) + "')"), TestReplicationScenarios.driver);
        if (VERIFY_SETUP_STEPS) {
            try {
                Table ptned = TestReplicationScenarios.metaStoreClient.getTable(dbName, "ptned");
                Assert.assertTrue(ptned.getParameters().containsKey(testKey));
                Assert.assertEquals(testVal, ptned.getParameters().get(testKey));
            } catch (TException e) {
                Assert.assertNull(e);
            }
        }
        // alter partitioned table's partition set partition property
        // Note : No DDL way to alter a partition, so we use the MSC api directly.
        try {
            List<String> ptnVals1 = new ArrayList<String>();
            ptnVals1.add("1");
            Partition ptn1 = TestReplicationScenarios.metaStoreClient.getPartition(dbName, "ptned", ptnVals1);
            ptn1.getParameters().put(testKey, testVal);
            TestReplicationScenarios.metaStoreClient.alter_partition(dbName, "ptned", ptn1, null);
        } catch (TException e) {
            Assert.assertNull(e);
        }
        // rename partitioned table
        verifySetup((("SELECT a from " + dbName) + ".ptned2 WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned2 RENAME TO ") + dbName) + ".ptned2_rn"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned2_rn WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        // All alters done, now we replicate them over.
        incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        // Replication done, we now do the following verifications:
        // verify that unpartitioned table rename succeeded.
        verifyIfTableNotExist(replDbName, "unptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT * from " + replDbName) + ".unptned_rn"), unptn_data, TestReplicationScenarios.driverMirror);
        // verify that partition rename succeded.
        try {
            Table unptn2 = TestReplicationScenarios.metaStoreClientMirror.getTable(replDbName, "unptned2");
            Assert.assertTrue(unptn2.getParameters().containsKey(testKey));
            Assert.assertEquals(testVal, unptn2.getParameters().get(testKey));
        } catch (TException te) {
            Assert.assertNull(te);
        }
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=22"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // verify that ptned table rename succeded.
        verifyIfTableNotExist(replDbName, "ptned2", TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned2_rn WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // verify that ptned table property set worked
        try {
            Table ptned = TestReplicationScenarios.metaStoreClientMirror.getTable(replDbName, "ptned");
            Assert.assertTrue(ptned.getParameters().containsKey(testKey));
            Assert.assertEquals(testVal, ptned.getParameters().get(testKey));
        } catch (TException te) {
            Assert.assertNull(te);
        }
        // verify that partitioned table partition property set worked.
        try {
            List<String> ptnVals1 = new ArrayList<String>();
            ptnVals1.add("1");
            Partition ptn1 = TestReplicationScenarios.metaStoreClientMirror.getPartition(replDbName, "ptned", ptnVals1);
            Assert.assertTrue(ptn1.getParameters().containsKey(testKey));
            Assert.assertEquals(testVal, ptn1.getParameters().get(testKey));
        } catch (TException te) {
            Assert.assertNull(te);
        }
    }

    @Test
    public void testDatabaseAlters() throws IOException {
        String testName = "DatabaseAlters";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        String ownerName = "test";
        TestReplicationScenarios.run(((("ALTER DATABASE " + dbName) + " SET OWNER USER ") + ownerName), TestReplicationScenarios.driver);
        // Trigger bootstrap replication
        TestReplicationScenarios.Tuple bootstrap = bootstrapLoadAndVerify(dbName, replDbName);
        try {
            Database replDb = TestReplicationScenarios.metaStoreClientMirror.getDatabase(replDbName);
            Assert.assertEquals(ownerName, replDb.getOwnerName());
            Assert.assertEquals("USER", replDb.getOwnerType().toString());
        } catch (TException e) {
            Assert.assertNull(e);
        }
        // Alter database set DB property
        String testKey = "blah";
        String testVal = "foo";
        TestReplicationScenarios.run((((((("ALTER DATABASE " + dbName) + " SET DBPROPERTIES ('") + testKey) + "' = '") + testVal) + "')"), TestReplicationScenarios.driver);
        // All alters done, now we replicate them over.
        TestReplicationScenarios.Tuple incremental = incrementalLoadAndVerify(dbName, bootstrap.lastReplId, replDbName);
        // Replication done, we need to check if the new property is added
        try {
            Database replDb = TestReplicationScenarios.metaStoreClientMirror.getDatabase(replDbName);
            Assert.assertTrue(replDb.getParameters().containsKey(testKey));
            Assert.assertEquals(testVal, replDb.getParameters().get(testKey));
        } catch (TException e) {
            Assert.assertNull(e);
        }
        String newValue = "newFoo";
        String newOwnerName = "newTest";
        TestReplicationScenarios.run((((((("ALTER DATABASE " + dbName) + " SET DBPROPERTIES ('") + testKey) + "' = '") + newValue) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run(((("ALTER DATABASE " + dbName) + " SET OWNER ROLE ") + newOwnerName), TestReplicationScenarios.driver);
        incremental = incrementalLoadAndVerify(dbName, incremental.lastReplId, replDbName);
        // Replication done, we need to check if new value is set for existing property
        try {
            Database replDb = TestReplicationScenarios.metaStoreClientMirror.getDatabase(replDbName);
            Assert.assertTrue(replDb.getParameters().containsKey(testKey));
            Assert.assertEquals(newValue, replDb.getParameters().get(testKey));
            Assert.assertEquals(newOwnerName, replDb.getOwnerName());
            Assert.assertEquals("ROLE", replDb.getOwnerType().toString());
        } catch (TException e) {
            Assert.assertNull(e);
        }
    }

    @Test
    public void testIncrementalLoad() throws IOException {
        String testName = "incrementalLoad";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned_empty(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_empty(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        verifySetup((("SELECT a from " + dbName) + ".ptned_empty"), empty, TestReplicationScenarios.driverMirror);
        verifySetup((("SELECT * from " + dbName) + ".unptned_empty"), empty, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".unptned_late LIKE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned_late SELECT * FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_late"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT * from " + replDbName) + ".unptned_late"), unptn_data, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_late(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_late PARTITION(b=1) SELECT a FROM ") + dbName) + ".ptned WHERE b=1"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_late WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_late PARTITION(b=2) SELECT a FROM ") + dbName) + ".ptned WHERE b=2"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_late WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_late WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_late WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIncrementalInserts() throws IOException {
        String testName = "incrementalInserts";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".unptned_late LIKE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned_late SELECT * FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_late ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_late ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        String[] unptn_data_after_ins = new String[]{ "eleven", "thirteen", "twelve" };
        String[] data_after_ovwrite = new String[]{ "hundred" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned_late values('") + (unptn_data_after_ins[1])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned_late ORDER BY a"), unptn_data_after_ins, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".unptned values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned"), data_after_ovwrite, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_late ORDER BY a"), unptn_data_after_ins, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testEventTypesForDynamicAddPartitionByInsert() throws IOException {
        String name = testName.getMethodName();
        final String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrap = bootstrapLoadAndVerify(dbName, replDbName);
        String[] ptn_data = new String[]{ "ten" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Inject a behaviour where it throws exception if an INSERT event is found
        // As we dynamically add a partition through INSERT INTO cmd, it should just add ADD_PARTITION
        // event not an INSERT event
        BehaviourInjection<NotificationEventResponse, NotificationEventResponse> eventTypeValidator = new BehaviourInjection<NotificationEventResponse, NotificationEventResponse>() {
            @Nullable
            @Override
            public NotificationEventResponse apply(@Nullable
            NotificationEventResponse eventsList) {
                if (null != eventsList) {
                    List<NotificationEvent> events = eventsList.getEvents();
                    for (int i = 0; i < (events.size()); i++) {
                        NotificationEvent event = events.get(i);
                        // Skip all the events belong to other DBs/tables.
                        if (event.getDbName().equalsIgnoreCase(dbName)) {
                            if (event.getEventType().equalsIgnoreCase("INSERT")) {
                                // If an insert event is found, then return null hence no event is dumped.
                                TestReplicationScenarios.LOG.error("Encountered INSERT event when it was not expected to");
                                return null;
                            }
                        }
                    }
                    injectionPathCalled = true;
                }
                return eventsList;
            }
        };
        InjectableBehaviourObjectStore.setGetNextNotificationBehaviour(eventTypeValidator);
        try {
            incrementalLoadAndVerify(dbName, bootstrap.lastReplId, replDbName);
            eventTypeValidator.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetGetNextNotificationBehaviour();// reset the behaviour

        }
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1)"), ptn_data, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIdempotentMoveTaskForInsertFiles() throws IOException {
        String name = testName.getMethodName();
        final String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrap = bootstrapLoadAndVerify(dbName, replDbName);
        String[] unptn_data = new String[]{ "ten" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Inject a behaviour where it repeats the INSERT event twice with different event IDs
        BehaviourInjection<NotificationEventResponse, NotificationEventResponse> insertEventRepeater = new BehaviourInjection<NotificationEventResponse, NotificationEventResponse>() {
            @Nullable
            @Override
            public NotificationEventResponse apply(@Nullable
            NotificationEventResponse eventsList) {
                if (null != eventsList) {
                    List<NotificationEvent> events = eventsList.getEvents();
                    List<NotificationEvent> outEvents = new ArrayList<>();
                    long insertEventId = -1;
                    for (int i = 0; i < (events.size()); i++) {
                        NotificationEvent event = events.get(i);
                        // Skip all the events belong to other DBs/tables.
                        if (event.getDbName().equalsIgnoreCase(dbName)) {
                            if (event.getEventType().equalsIgnoreCase("INSERT")) {
                                // Add insert event twice with different event ID to allow apply of both events.
                                NotificationEvent newEvent = new NotificationEvent(event);
                                outEvents.add(newEvent);
                                insertEventId = newEvent.getEventId();
                            }
                        }
                        NotificationEvent newEvent = new NotificationEvent(event);
                        if (insertEventId != (-1)) {
                            insertEventId++;
                            newEvent.setEventId(insertEventId);
                        }
                        outEvents.add(newEvent);
                    }
                    eventsList.setEvents(outEvents);
                    injectionPathCalled = true;
                }
                return eventsList;
            }
        };
        InjectableBehaviourObjectStore.setGetNextNotificationBehaviour(insertEventRepeater);
        try {
            incrementalLoadAndVerify(dbName, bootstrap.lastReplId, replDbName);
            insertEventRepeater.assertInjectionsPerformed(true, false);
        } finally {
            InjectableBehaviourObjectStore.resetGetNextNotificationBehaviour();// reset the behaviour

        }
        if (TestReplicationScenarios.isMigrationTest) {
            // as the move is done using a different event, load will be done within a different transaction and thus
            // we will get two records.
            verifyRun((("SELECT a from " + replDbName) + ".unptned"), new String[]{ unptn_data[0], unptn_data[0] }, TestReplicationScenarios.driverMirror);
        } else {
            verifyRun((("SELECT a from " + replDbName) + ".unptned"), unptn_data[0], TestReplicationScenarios.driverMirror);
        }
    }

    @Test
    public void testIncrementalInsertToPartition() throws IOException {
        String testName = "incrementalInsertToPartition";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen", "thirteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen", "sixteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=2)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        String[] data_after_ovwrite = new String[]{ "hundred" };
        // Insert overwrite on existing partition
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".ptned partition(b=2) values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=2)"), data_after_ovwrite, TestReplicationScenarios.driver);
        // Insert overwrite on dynamic partition
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".ptned partition(b=3) values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=3)"), data_after_ovwrite, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2)"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=3)"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testInsertToMultiKeyPartition() throws IOException {
        String testName = "insertToMultiKeyPartition";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".namelist(name string) partitioned by (year int, month int, day int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run(("USE " + dbName), TestReplicationScenarios.driver);
        String[] ptn_data_1 = new String[]{ "abraham", "bob", "carter" };
        String[] ptn_year_1980 = new String[]{ "abraham", "bob" };
        String[] ptn_day_1 = new String[]{ "abraham", "carter" };
        String[] ptn_year_1984_month_4_day_1_1 = new String[]{ "carter" };
        String[] ptn_list_1 = new String[]{ "year=1980/month=4/day=1", "year=1980/month=5/day=5", "year=1984/month=4/day=1" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".namelist partition(year=1980,month=4,day=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".namelist partition(year=1980,month=5,day=5) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".namelist partition(year=1984,month=4,day=1) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (year=1980) ORDER BY name"), ptn_year_1980, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (day=1) ORDER BY name"), ptn_day_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (year=1984 and month=4 and day=1) ORDER BY name"), ptn_year_1984_month_4_day_1_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist ORDER BY name"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SHOW PARTITIONS " + dbName) + ".namelist"), ptn_list_1, TestReplicationScenarios.driver);
        verifyRunWithPatternMatch("SHOW TABLE EXTENDED LIKE namelist PARTITION (year=1980,month=4,day=1)", "location", "namelist/year=1980/month=4/day=1", TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        verifyRun((("SELECT name from " + replDbName) + ".namelist where (year=1980) ORDER BY name"), ptn_year_1980, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT name from " + replDbName) + ".namelist where (day=1) ORDER BY name"), ptn_day_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT name from " + replDbName) + ".namelist where (year=1984 and month=4 and day=1) ORDER BY name"), ptn_year_1984_month_4_day_1_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT name from " + replDbName) + ".namelist ORDER BY name"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SHOW PARTITIONS " + replDbName) + ".namelist"), ptn_list_1, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run(("USE " + replDbName), TestReplicationScenarios.driverMirror);
        verifyRunWithPatternMatch("SHOW TABLE EXTENDED LIKE namelist PARTITION (year=1980,month=4,day=1)", "location", "namelist/year=1980/month=4/day=1", TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run(("USE " + dbName), TestReplicationScenarios.driver);
        String[] ptn_data_2 = new String[]{ "abraham", "bob", "carter", "david", "eugene" };
        String[] ptn_year_1984_month_4_day_1_2 = new String[]{ "carter", "david" };
        String[] ptn_day_1_2 = new String[]{ "abraham", "carter", "david" };
        String[] ptn_list_2 = new String[]{ "year=1980/month=4/day=1", "year=1980/month=5/day=5", "year=1984/month=4/day=1", "year=1990/month=5/day=25" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".namelist partition(year=1984,month=4,day=1) values('") + (ptn_data_2[3])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".namelist partition(year=1990,month=5,day=25) values('") + (ptn_data_2[4])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (year=1980) ORDER BY name"), ptn_year_1980, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (day=1) ORDER BY name"), ptn_day_1_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (year=1984 and month=4 and day=1) ORDER BY name"), ptn_year_1984_month_4_day_1_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist ORDER BY name"), ptn_data_2, TestReplicationScenarios.driver);
        verifyRun((("SHOW PARTITIONS " + dbName) + ".namelist"), ptn_list_2, TestReplicationScenarios.driver);
        verifyRunWithPatternMatch("SHOW TABLE EXTENDED LIKE namelist PARTITION (year=1990,month=5,day=25)", "location", "namelist/year=1990/month=5/day=25", TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT name from " + replDbName) + ".namelist where (year=1980) ORDER BY name"), ptn_year_1980, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT name from " + replDbName) + ".namelist where (day=1) ORDER BY name"), ptn_day_1_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT name from " + replDbName) + ".namelist where (year=1984 and month=4 and day=1) ORDER BY name"), ptn_year_1984_month_4_day_1_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT name from " + replDbName) + ".namelist ORDER BY name"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SHOW PARTITIONS " + replDbName) + ".namelist"), ptn_list_2, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run(("USE " + replDbName), TestReplicationScenarios.driverMirror);
        verifyRunWithPatternMatch("SHOW TABLE EXTENDED LIKE namelist PARTITION (year=1990,month=5,day=25)", "location", "namelist/year=1990/month=5/day=25", TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run(("USE " + dbName), TestReplicationScenarios.driver);
        String[] ptn_data_3 = new String[]{ "abraham", "bob", "carter", "david", "fisher" };
        String[] data_after_ovwrite = new String[]{ "fisher" };
        // Insert overwrite on existing partition
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".namelist partition(year=1990,month=5,day=25) values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist where (year=1990 and month=5 and day=25)"), data_after_ovwrite, TestReplicationScenarios.driver);
        verifySetup((("SELECT name from " + dbName) + ".namelist ORDER BY name"), ptn_data_3, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifySetup((("SELECT name from " + replDbName) + ".namelist where (year=1990 and month=5 and day=25)"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
        verifySetup((("SELECT name from " + replDbName) + ".namelist ORDER BY name"), ptn_data_3, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIncrementalInsertDropUnpartitionedTable() throws IOException {
        String testName = "incrementalInsertDropUnpartitionedTable";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".unptned_tmp AS SELECT * FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned_tmp ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        // Get the last repl ID corresponding to all insert/alter/create events except DROP.
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String lastDumpIdWithoutDrop = incrementalDump.lastReplId;
        // Drop all the tables
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".unptned_tmp"), TestReplicationScenarios.driver);
        verifyFail((("SELECT * FROM " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifyFail((("SELECT * FROM " + dbName) + ".unptned_tmp"), TestReplicationScenarios.driver);
        // Dump all the events except DROP
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, lastDumpIdWithoutDrop, replDbName);
        replDumpId = incrementalDump.lastReplId;
        // Need to find the tables and data as drop is not part of this dump
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_tmp ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        // Dump the drop events and check if tables are getting dropped in target as well
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyFail((("SELECT * FROM " + replDbName) + ".unptned"), TestReplicationScenarios.driverMirror);
        verifyFail((("SELECT * FROM " + replDbName) + ".unptned_tmp"), TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIncrementalInsertDropPartitionedTable() throws IOException {
        String testName = "incrementalInsertDropPartitionedTable";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen", "thirteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen", "sixteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=20)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned RENAME PARTITION (b=20) TO PARTITION (b=2"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".ptned_tmp AS SELECT * FROM ") + dbName) + ".ptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_tmp where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_tmp where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        // Get the last repl ID corresponding to all insert/alter/create events except DROP.
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String lastDumpIdWithoutDrop = incrementalDump.lastReplId;
        // Drop all the tables
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".ptned_tmp"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".ptned"), TestReplicationScenarios.driver);
        verifyFail((("SELECT * FROM " + dbName) + ".ptned_tmp"), TestReplicationScenarios.driver);
        verifyFail((("SELECT * FROM " + dbName) + ".ptned"), TestReplicationScenarios.driver);
        // Replicate all the events except DROP
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, lastDumpIdWithoutDrop, replDbName);
        replDumpId = incrementalDump.lastReplId;
        // Need to find the tables and data as drop is not part of this dump
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_tmp where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_tmp where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // Replicate the drop events and check if tables are getting dropped in target as well
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyFail((("SELECT * FROM " + replDbName) + ".ptned_tmp"), TestReplicationScenarios.driverMirror);
        verifyFail((("SELECT * FROM " + replDbName) + ".ptned"), TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testInsertOverwriteOnUnpartitionedTableWithCM() throws IOException {
        String testName = "insertOverwriteOnUnpartitionedTableWithCM";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        // After INSERT INTO operation, get the last Repl ID
        String[] unptn_data = new String[]{ "thirteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String insertDumpId = incrementalDump.lastReplId;
        // Insert overwrite on unpartitioned table
        String[] data_after_ovwrite = new String[]{ "hundred" };
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".unptned values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        // Replicate only one INSERT INTO operation on the table.
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, insertDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        // After Load from this dump, all target tables/partitions will have initial set of data but source will have latest data.
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        // Replicate the remaining INSERT OVERWRITE operations on the table.
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        // After load, shall see the overwritten data.
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testInsertOverwriteOnPartitionedTableWithCM() throws IOException {
        String testName = "insertOverwriteOnPartitionedTableWithCM";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        // INSERT INTO 2 partitions and get the last repl ID
        String[] ptn_data_1 = new String[]{ "fourteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String insertDumpId = incrementalDump.lastReplId;
        // Insert overwrite on one partition with multiple files
        String[] data_after_ovwrite = new String[]{ "hundred" };
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".ptned partition(b=2) values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned where (b=2)"), data_after_ovwrite, TestReplicationScenarios.driver);
        // Replicate only 2 INSERT INTO operations.
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, insertDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        // After Load from this dump, all target tables/partitions will have initial set of data.
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // Replicate the remaining INSERT OVERWRITE operation on the table.
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        // After load, shall see the overwritten data.
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testDropPartitionEventWithPartitionOnTimestampColumn() throws IOException {
        String testName = "dropPartitionEventWithPartitionOnTimestampColumn";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) PARTITIONED BY (b timestamp)"), TestReplicationScenarios.driver);
        String[] ptn_data = new String[]{ "fourteen" };
        String ptnVal = "2017-10-01 01:00:10.1";
        TestReplicationScenarios.run((((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=\"") + ptnVal) + "\") values(\'") + (ptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        ptn_data = new String[]{ "fifteen" };
        ptnVal = "2017-10-24 00:00:00.0";
        TestReplicationScenarios.run((((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=\"") + ptnVal) + "\") values(\'") + (ptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Replicate insert event and verify
        TestReplicationScenarios.Tuple incrDump = incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        verifyRun((((("SELECT a from " + replDbName) + ".ptned where (b=\"") + ptnVal) + "\") ORDER BY a"), ptn_data, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION(b=\"") + ptnVal) + "\")"), TestReplicationScenarios.driver);
        // Replicate drop partition event and verify
        incrementalLoadAndVerify(dbName, incrDump.lastReplId, replDbName);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList(ptnVal)), TestReplicationScenarios.metaStoreClientMirror);
    }

    /**
     * Verify replication when string partition column value has special chars
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testWithStringPartitionSpecialChars() throws IOException {
        String testName = "testWithStringPartitionSpecialChars";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(v string) PARTITIONED BY (p string)"), TestReplicationScenarios.driver);
        String[] ptn_data = new String[]{ "fourteen", "fifteen" };
        String[] ptnVal = new String[]{ "has a space, /, and \t tab", "another set of '#@ chars" };
        TestReplicationScenarios.run((((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(p=\"") + (ptnVal[0])) + "\") values(\'") + (ptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        TestReplicationScenarios.run((((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(p=\"") + (ptnVal[1])) + "\") values(\'") + (ptn_data[1])) + "')"), TestReplicationScenarios.driver);
        // Replicate insert event and verify
        TestReplicationScenarios.Tuple incrDump = incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        verifyRun((("SELECT p from " + replDbName) + ".ptned ORDER BY p desc"), ptnVal, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION(p=\"") + (ptnVal[0])) + "\")"), TestReplicationScenarios.driver);
        // Replicate drop partition event and verify
        incrementalLoadAndVerify(dbName, incrDump.lastReplId, replDbName);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList(ptnVal[0])), TestReplicationScenarios.metaStoreClientMirror);
    }

    @Test
    public void testRenameTableWithCM() throws IOException {
        String testName = "renameTableWithCM";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] unptn_data = new String[]{ "ten", "twenty" };
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=2)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        // Get the last repl ID corresponding to all insert events except RENAME.
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String lastDumpIdWithoutRename = incrementalDump.lastReplId;
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".unptned RENAME TO ") + dbName) + ".unptned_renamed"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned RENAME TO ") + dbName) + ".ptned_renamed"), TestReplicationScenarios.driver);
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, lastDumpIdWithoutRename, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyFail((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), TestReplicationScenarios.driverMirror);
        verifyFail((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_renamed ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_renamed where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_renamed where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testRenamePartitionWithCM() throws IOException {
        String testName = "renamePartitionWithCM";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] empty = new String[]{  };
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=2)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        // Get the last repl ID corresponding to all insert events except RENAME.
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        String lastDumpIdWithoutRename = incrementalDump.lastReplId;
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned PARTITION (b=2) RENAME TO PARTITION (b=10)"), TestReplicationScenarios.driver);
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, lastDumpIdWithoutRename, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=10) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=10) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testRenameTableAcrossDatabases() throws IOException {
        String testName = "renameTableAcrossDatabases";
        TestReplicationScenarios.LOG.info(("Testing " + testName));
        String dbName1 = ((testName + "_") + (TestReplicationScenarios.tid)) + "_1";
        String dbName2 = ((testName + "_") + (TestReplicationScenarios.tid)) + "_2";
        String replDbName1 = dbName1 + "_dupe";
        String replDbName2 = dbName2 + "_dupe";
        TestReplicationScenarios.createDB(dbName1, TestReplicationScenarios.driver);
        TestReplicationScenarios.createDB(dbName2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName1) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "ten", "twenty" };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName1) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrap1 = bootstrapLoadAndVerify(dbName1, replDbName1);
        TestReplicationScenarios.Tuple bootstrap2 = bootstrapLoadAndVerify(dbName2, replDbName2);
        verifyRun((("SELECT a from " + replDbName1) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyIfTableNotExist(replDbName2, "unptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyFail((((("ALTER TABLE " + dbName1) + ".unptned RENAME TO ") + dbName2) + ".unptned_renamed"), TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName1, bootstrap1.lastReplId, replDbName1);
        incrementalLoadAndVerify(dbName2, bootstrap2.lastReplId, replDbName2);
        verifyIfTableNotExist(replDbName1, "unptned_renamed", TestReplicationScenarios.metaStoreClientMirror);
        verifyIfTableNotExist(replDbName2, "unptned_renamed", TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT a from " + replDbName1) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testRenamePartitionedTableAcrossDatabases() throws IOException {
        String testName = "renamePartitionedTableAcrossDatabases";
        TestReplicationScenarios.LOG.info(("Testing " + testName));
        String dbName1 = ((testName + "_") + (TestReplicationScenarios.tid)) + "_1";
        String dbName2 = ((testName + "_") + (TestReplicationScenarios.tid)) + "_2";
        String replDbName1 = dbName1 + "_dupe";
        String replDbName2 = dbName2 + "_dupe";
        TestReplicationScenarios.createDB(dbName1, TestReplicationScenarios.driver);
        TestReplicationScenarios.createDB(dbName2, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName1) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] ptn_data = new String[]{ "fifteen", "fourteen" };
        String ptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(ptn_locn, ptn_data);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn) + "' OVERWRITE INTO TABLE ") + dbName1) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrap1 = bootstrapLoadAndVerify(dbName1, replDbName1);
        TestReplicationScenarios.Tuple bootstrap2 = bootstrapLoadAndVerify(dbName2, replDbName2);
        verifyRun((("SELECT a from " + replDbName1) + ".ptned where (b=1) ORDER BY a"), ptn_data, TestReplicationScenarios.driverMirror);
        verifyIfTableNotExist(replDbName2, "ptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyFail((((("ALTER TABLE " + dbName1) + ".ptned RENAME TO ") + dbName2) + ".ptned_renamed"), TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName1, bootstrap1.lastReplId, replDbName1);
        incrementalLoadAndVerify(dbName2, bootstrap2.lastReplId, replDbName2);
        verifyIfTableNotExist(replDbName1, "ptned_renamed", TestReplicationScenarios.metaStoreClientMirror);
        verifyIfTableNotExist(replDbName2, "ptned_renamed", TestReplicationScenarios.metaStoreClientMirror);
        verifyRun((("SELECT a from " + replDbName1) + ".ptned where (b=1) ORDER BY a"), ptn_data, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testViewsReplication() throws IOException {
        String testName = "viewsReplication";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE VIEW " + dbName) + ".virtual_view AS SELECT * FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] ptn_data_1 = new String[]{ "thirteen", "fourteen", "fifteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "sixteen", "seventeen" };
        String[] empty = new String[]{  };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        verifySetup((("SELECT a from " + dbName) + ".ptned"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".virtual_view"), empty, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".virtual_view"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_2) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=2"), ptn_data_2, TestReplicationScenarios.driver);
        // TODO: This does not work because materialized views need the creation metadata
        // to be updated in case tables used were replicated to a different database.
        // run("CREATE MATERIALIZED VIEW " + dbName + ".mat_view AS SELECT a FROM " + dbName + ".ptned where b=1", driver);
        // verifySetup("SELECT a from " + dbName + ".mat_view", ptn_data_1, driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        // view is referring to old database, so no data
        verifyRun((("SELECT * from " + replDbName) + ".virtual_view"), empty, TestReplicationScenarios.driverMirror);
        // verifyRun("SELECT a from " + replDbName + ".mat_view", ptn_data_1, driverMirror);
        TestReplicationScenarios.run((((("CREATE VIEW " + dbName) + ".virtual_view2 AS SELECT a FROM ") + dbName) + ".ptned where b=2"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".virtual_view2"), ptn_data_2, TestReplicationScenarios.driver);
        // Create a view with name already exist. Just to verify if failure flow clears the added create_table event.
        TestReplicationScenarios.run((((("CREATE VIEW " + dbName) + ".virtual_view2 AS SELECT a FROM ") + dbName) + ".ptned where b=2"), TestReplicationScenarios.driver);
        // run("CREATE MATERIALIZED VIEW " + dbName + ".mat_view2 AS SELECT * FROM " + dbName + ".unptned", driver);
        // verifySetup("SELECT * from " + dbName + ".mat_view2", unptn_data, driver);
        // Perform REPL-DUMP/LOAD
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT * from " + replDbName) + ".unptned"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where b=1"), ptn_data_1, TestReplicationScenarios.driverMirror);
        // view is referring to old database, so no data
        verifyRun((("SELECT * from " + replDbName) + ".virtual_view"), empty, TestReplicationScenarios.driverMirror);
        // verifyRun("SELECT a from " + replDbName + ".mat_view", ptn_data_1, driverMirror);
        // view is referring to old database, so no data
        verifyRun((("SELECT * from " + replDbName) + ".virtual_view2"), empty, TestReplicationScenarios.driverMirror);
        // verifyRun("SELECT * from " + replDbName + ".mat_view2", unptn_data, driverMirror);
        // Test "alter table" with rename
        TestReplicationScenarios.run((((("ALTER VIEW " + dbName) + ".virtual_view RENAME TO ") + dbName) + ".virtual_view_rename"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".virtual_view_rename"), unptn_data, TestReplicationScenarios.driver);
        // Perform REPL-DUMP/LOAD
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT * from " + replDbName) + ".virtual_view_rename"), empty, TestReplicationScenarios.driverMirror);
        // Test "alter table" with schema change
        TestReplicationScenarios.run((((("ALTER VIEW " + dbName) + ".virtual_view_rename AS SELECT a, concat(a, '_') as a_ FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SHOW COLUMNS FROM " + dbName) + ".virtual_view_rename"), new String[]{ "a", "a_" }, TestReplicationScenarios.driver);
        // Perform REPL-DUMP/LOAD
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SHOW COLUMNS FROM " + replDbName) + ".virtual_view_rename"), new String[]{ "a", "a_" }, TestReplicationScenarios.driverMirror);
        // Test "DROP VIEW"
        TestReplicationScenarios.run((("DROP VIEW " + dbName) + ".virtual_view"), TestReplicationScenarios.driver);
        verifyIfTableNotExist(dbName, "virtual_view", TestReplicationScenarios.metaStoreClient);
        // Perform REPL-DUMP/LOAD
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyIfTableNotExist(replDbName, "virtual_view", TestReplicationScenarios.metaStoreClientMirror);
    }

    @Test
    public void testDumpLimit() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = replDumpDb(dbName, null, null, null);
        String replDumpId = bootstrapDump.lastReplId;
        String replDumpLocn = bootstrapDump.dumpLocation;
        String[] unptn_data = new String[]{ "eleven", "thirteen", "twelve" };
        String[] unptn_data_load1 = new String[]{ "eleven" };
        String[] unptn_data_load2 = new String[]{ "eleven", "thirteen" };
        // x events to insert, last repl ID: replDumpId+x
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        String firstInsertLastReplId = replDumpDb(dbName, replDumpId, null, null).lastReplId;
        Integer numOfEventsIns1 = (Integer.valueOf(firstInsertLastReplId)) - (Integer.valueOf(replDumpId));
        // x events to insert, last repl ID: replDumpId+2x
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        String secondInsertLastReplId = replDumpDb(dbName, firstInsertLastReplId, null, null).lastReplId;
        Integer numOfEventsIns2 = (Integer.valueOf(secondInsertLastReplId)) - (Integer.valueOf(firstInsertLastReplId));
        // x events to insert, last repl ID: replDumpId+3x
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[2])) + "')"), TestReplicationScenarios.driver);
        verifyRun((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, null, numOfEventsIns1.toString(), replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data_load1, TestReplicationScenarios.driverMirror);
        Integer lastReplID = Integer.valueOf(replDumpId);
        lastReplID += 1000;
        String toReplID = String.valueOf(lastReplID);
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, toReplID, numOfEventsIns2.toString(), replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data_load2, TestReplicationScenarios.driverMirror);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testExchangePartition() throws IOException {
        String testName = "exchangePartition";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_src(a string) partitioned by (b int, c int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_dest(a string) partitioned by (b int, c int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] empty = new String[]{  };
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen", "thirteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen", "sixteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=1, c=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=1, c=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=1, c=1) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned_src ADD PARTITION (b=2, c=2)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=2, c=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=2, c=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=2, c=2) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=2, c=3) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=2, c=3) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_src partition(b=2, c=3) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=1 and c=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=2 and c=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=2 and c=3) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=1 and c=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=2 and c=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=2 and c=3) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=1 and c=1)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=2 and c=2)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=2 and c=3)"), empty, TestReplicationScenarios.driverMirror);
        // Exchange single partitions using complete partition-spec (all partition columns)
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned_dest EXCHANGE PARTITION (b=1, c=1) WITH TABLE ") + dbName) + ".ptned_src"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=1 and c=1)"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=2 and c=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=2 and c=3) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_dest where (b=1 and c=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_dest where (b=2 and c=2)"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_dest where (b=2 and c=3)"), empty, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=1 and c=1)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=2 and c=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=2 and c=3) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=1 and c=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=2 and c=2)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=2 and c=3)"), empty, TestReplicationScenarios.driverMirror);
        // Exchange multiple partitions using partial partition-spec (only one partition column)
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".ptned_dest EXCHANGE PARTITION (b=2) WITH TABLE ") + dbName) + ".ptned_src"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=1 and c=1)"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=2 and c=2)"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_src where (b=2 and c=3)"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_dest where (b=1 and c=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_dest where (b=2 and c=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_dest where (b=2 and c=3) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=1 and c=1)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=2 and c=2)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_src where (b=2 and c=3)"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=1 and c=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=2 and c=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_dest where (b=2 and c=3) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testTruncateTable() throws IOException {
        String testName = "truncateTable";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] empty = new String[]{  };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned"), empty, TestReplicationScenarios.driver);
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned"), empty, TestReplicationScenarios.driverMirror);
        String[] unptn_data_after_ins = new String[]{ "thirteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data_after_ins[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data_after_ins, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data_after_ins, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testTruncatePartitionedTable() throws IOException {
        String testName = "truncatePartitionedTable";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_1(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_2(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen", "thirteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen", "sixteen" };
        String[] empty = new String[]{  };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_1 PARTITION(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_1 PARTITION(b=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_1 PARTITION(b=1) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_1 PARTITION(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_1 PARTITION(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_1 PARTITION(b=2) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_2 PARTITION(b=10) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_2 PARTITION(b=10) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_2 PARTITION(b=10) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_2 PARTITION(b=20) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_2 PARTITION(b=20) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_2 PARTITION(b=20) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_1 where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_1 where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_2 where (b=10) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_2 where (b=20) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".ptned_1 where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_1 where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_2 where (b=10) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_2 where (b=20) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".ptned_1 PARTITION(b=2)"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_1 where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_1 where (b=2)"), empty, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".ptned_2"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_2 where (b=10)"), empty, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned_2 where (b=20)"), empty, TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifySetup((("SELECT a from " + replDbName) + ".ptned_1 where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifySetup((("SELECT a from " + replDbName) + ".ptned_1 where (b=2)"), empty, TestReplicationScenarios.driverMirror);
        verifySetup((("SELECT a from " + replDbName) + ".ptned_2 where (b=10)"), empty, TestReplicationScenarios.driverMirror);
        verifySetup((("SELECT a from " + replDbName) + ".ptned_2 where (b=20)"), empty, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testTruncateWithCM() throws IOException {
        String testName = "truncateWithCM";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = replDumpDb(dbName, null, null, null);
        String replDumpId = bootstrapDump.lastReplId;
        String replDumpLocn = bootstrapDump.dumpLocation;
        String[] empty = new String[]{  };
        String[] unptn_data = new String[]{ "eleven", "thirteen" };
        String[] unptn_data_load1 = new String[]{ "eleven" };
        String[] unptn_data_load2 = new String[]{ "eleven", "thirteen" };
        // x events to insert, last repl ID: replDumpId+x
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        String firstInsertLastReplId = replDumpDb(dbName, replDumpId, null, null).lastReplId;
        Integer numOfEventsIns1 = (Integer.valueOf(firstInsertLastReplId)) - (Integer.valueOf(replDumpId));
        // x events to insert, last repl ID: replDumpId+2x
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        verifyRun((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        String secondInsertLastReplId = replDumpDb(dbName, firstInsertLastReplId, null, null).lastReplId;
        Integer numOfEventsIns2 = (Integer.valueOf(secondInsertLastReplId)) - (Integer.valueOf(firstInsertLastReplId));
        // y event to truncate, last repl ID: replDumpId+2x+y
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifyRun((("SELECT a from " + dbName) + ".unptned ORDER BY a"), empty, TestReplicationScenarios.driver);
        String thirdTruncLastReplId = replDumpDb(dbName, secondInsertLastReplId, null, null).lastReplId;
        Integer numOfEventsTrunc3 = (Integer.valueOf(thirdTruncLastReplId)) - (Integer.valueOf(secondInsertLastReplId));
        // x events to insert, last repl ID: replDumpId+3x+y
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data_load1[0])) + "')"), TestReplicationScenarios.driver);
        verifyRun((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data_load1, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        // Dump and load only first insert (1 record)
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, null, numOfEventsIns1.toString(), replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + dbName) + "_dupe.unptned ORDER BY a"), unptn_data_load1, TestReplicationScenarios.driverMirror);
        // Dump and load only second insert (2 records)
        Integer lastReplID = Integer.valueOf(replDumpId);
        lastReplID += 1000;
        String toReplID = String.valueOf(lastReplID);
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, toReplID, numOfEventsIns2.toString(), replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data_load2, TestReplicationScenarios.driverMirror);
        // Dump and load only truncate (0 records)
        incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, null, numOfEventsTrunc3.toString(), replDbName);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
        // Dump and load insert after truncate (1 record)
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data_load1, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testIncrementalRepeatEventOnExistingObject() throws IOException {
        String testName = "incrementalRepeatEventOnExistingObject";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        // List to maintain the incremental dumps for each operation
        List<TestReplicationScenarios.Tuple> incrementalDumpList = new ArrayList<TestReplicationScenarios.Tuple>();
        String[] empty = new String[]{  };
        String[] unptn_data = new String[]{ "ten" };
        String[] ptn_data_1 = new String[]{ "fifteen" };
        String[] ptn_data_2 = new String[]{ "seventeen" };
        // INSERT EVENT to unpartitioned table
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple replDump = dumpDbFromLastDump(dbName, bootstrapDump);
        incrementalDumpList.add(replDump);
        // INSERT EVENT to partitioned table with dynamic ADD_PARTITION
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // ADD_PARTITION EVENT to partitioned table
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=2)"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // INSERT EVENT to partitioned table on existing partition
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // TRUNCATE_PARTITION EVENT on partitioned table
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".ptned PARTITION (b=1)"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // TRUNCATE_TABLE EVENT on unpartitioned table
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // CREATE_TABLE EVENT with multiple partitions
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".unptned_tmp AS SELECT * FROM ") + dbName) + ".ptned"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // ADD_CONSTRAINT EVENT
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".unptned_tmp ADD CONSTRAINT uk_unptned UNIQUE(a) disable"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // Replicate all the events happened so far
        TestReplicationScenarios.Tuple incrDump = incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_tmp where (b=1) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_tmp where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        // Load each incremental dump from the list. Each dump have only one operation.
        for (TestReplicationScenarios.Tuple currDump : incrementalDumpList) {
            // Load the incremental dump and ensure it does nothing and lastReplID remains same
            loadAndVerify(replDbName, currDump.dumpLocation, incrDump.lastReplId);
            // Verify if the data are intact even after applying an applied event once again on existing objects
            verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
            verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
            verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
            verifyRun((("SELECT a from " + replDbName) + ".unptned_tmp where (b=1) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
            verifyRun((("SELECT a from " + replDbName) + ".unptned_tmp where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        }
    }

    @Test
    public void testIncrementalRepeatEventOnMissingObject() throws Exception {
        String testName = "incrementalRepeatEventOnMissingObject";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        // List to maintain the incremental dumps for each operation
        List<TestReplicationScenarios.Tuple> incrementalDumpList = new ArrayList<TestReplicationScenarios.Tuple>();
        String[] empty = new String[]{  };
        String[] unptn_data = new String[]{ "ten" };
        String[] ptn_data_1 = new String[]{ "fifteen" };
        String[] ptn_data_2 = new String[]{ "seventeen" };
        // INSERT EVENT to unpartitioned table
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple replDump = dumpDbFromLastDump(dbName, bootstrapDump);
        incrementalDumpList.add(replDump);
        // INSERT EVENT to partitioned table with dynamic ADD_PARTITION
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // ADD_PARTITION EVENT to partitioned table
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=2)"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // INSERT EVENT to partitioned table on existing partition
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned partition(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // TRUNCATE_PARTITION EVENT on partitioned table
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // TRUNCATE_TABLE EVENT on unpartitioned table
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // CREATE_TABLE EVENT on partitioned table
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned_tmp (a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // INSERT EVENT to partitioned table with dynamic ADD_PARTITION
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_tmp partition(b=10) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // INSERT EVENT to partitioned table with dynamic ADD_PARTITION
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned_tmp partition(b=20) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // DROP_PARTITION EVENT to partitioned table
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned DROP PARTITION (b=1)"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // RENAME_PARTITION EVENT to partitioned table
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned PARTITION (b=2) RENAME TO PARTITION (b=20)"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // RENAME_TABLE EVENT to unpartitioned table
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".unptned RENAME TO ") + dbName) + ".unptned_new"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // ADD_CONSTRAINT EVENT
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned_tmp ADD CONSTRAINT uk_unptned UNIQUE(a) disable"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // DROP_TABLE EVENT to partitioned table
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".ptned_tmp"), TestReplicationScenarios.driver);
        replDump = dumpDbFromLastDump(dbName, replDump);
        incrementalDumpList.add(replDump);
        // Replicate all the events happened so far
        TestReplicationScenarios.Tuple incrDump = incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        verifyIfTableNotExist(replDbName, "unptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyIfTableNotExist(replDbName, "ptned_tmp", TestReplicationScenarios.metaStoreClientMirror);
        verifyIfTableExist(replDbName, "unptned_new", TestReplicationScenarios.metaStoreClientMirror);
        verifyIfTableExist(replDbName, "ptned", TestReplicationScenarios.metaStoreClientMirror);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("1")), TestReplicationScenarios.metaStoreClientMirror);
        verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("2")), TestReplicationScenarios.metaStoreClientMirror);
        verifyIfPartitionExist(replDbName, "ptned", new ArrayList(Arrays.asList("20")), TestReplicationScenarios.metaStoreClientMirror);
        // Load each incremental dump from the list. Each dump have only one operation.
        for (TestReplicationScenarios.Tuple currDump : incrementalDumpList) {
            // Load the current incremental dump and ensure it does nothing and lastReplID remains same
            loadAndVerify(replDbName, currDump.dumpLocation, incrDump.lastReplId);
            // Verify if the data are intact even after applying an applied event once again on missing objects
            verifyIfTableNotExist(replDbName, "unptned", TestReplicationScenarios.metaStoreClientMirror);
            verifyIfTableNotExist(replDbName, "ptned_tmp", TestReplicationScenarios.metaStoreClientMirror);
            verifyIfTableExist(replDbName, "unptned_new", TestReplicationScenarios.metaStoreClientMirror);
            verifyIfTableExist(replDbName, "ptned", TestReplicationScenarios.metaStoreClientMirror);
            verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("1")), TestReplicationScenarios.metaStoreClientMirror);
            verifyIfPartitionNotExist(replDbName, "ptned", new ArrayList(Arrays.asList("2")), TestReplicationScenarios.metaStoreClientMirror);
            verifyIfPartitionExist(replDbName, "ptned", new ArrayList(Arrays.asList("20")), TestReplicationScenarios.metaStoreClientMirror);
        }
    }

    @Test
    public void testConcatenateTable() throws IOException {
        String testName = "concatenateTable";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS ORC"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        String[] empty = new String[]{  };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".unptned CONCATENATE"), TestReplicationScenarios.driver);
        verifyRun((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        // Replicate all the events happened after bootstrap
        TestReplicationScenarios.Tuple incrDump = incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        // migration test is failing as CONCATENATE is not working. Its not creating the merged file.
        if (!(TestReplicationScenarios.isMigrationTest)) {
            verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        }
    }

    @Test
    public void testConcatenatePartitionedTable() throws IOException {
        String testName = "concatenatePartitionedTable";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) PARTITIONED BY (b int) STORED AS ORC"), TestReplicationScenarios.driver);
        String[] ptn_data_1 = new String[]{ "fifteen", "fourteen", "thirteen" };
        String[] ptn_data_2 = new String[]{ "fifteen", "seventeen", "sixteen" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=2) values('") + (ptn_data_2[0])) + "')"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=1) values('") + (ptn_data_1[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=1) values('") + (ptn_data_1[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=2) values('") + (ptn_data_2[1])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=2) values('") + (ptn_data_2[2])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ALTER TABLE " + dbName) + ".ptned PARTITION(b=2) CONCATENATE"), TestReplicationScenarios.driver);
        // Replicate all the events happened so far
        TestReplicationScenarios.Tuple incrDump = incrementalLoadAndVerify(dbName, bootstrapDump.lastReplId, replDbName);
        // migration test is failing as CONCATENATE is not working. Its not creating the merged file.
        if (!(TestReplicationScenarios.isMigrationTest)) {
            verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
            verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=2) ORDER BY a"), ptn_data_2, TestReplicationScenarios.driverMirror);
        }
    }

    @Test
    public void testIncrementalLoadFailAndRetry() throws IOException {
        String testName = "incrementalLoadFailAndRetry";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a string) PARTITIONED BY (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        // Bootstrap dump/load
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        // Prefixed with incrementalLoadFailAndRetry to avoid finding entry in cmpath
        String[] ptn_data_1 = new String[]{ "incrementalLoadFailAndRetry_fifteen" };
        String[] empty = new String[]{  };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".ptned PARTITION(b=1) values('") + (ptn_data_1[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".ptned_tmp AS SELECT * FROM ") + dbName) + ".ptned"), TestReplicationScenarios.driver);
        // Move the data files of this newly created partition to a temp location
        Partition ptn = null;
        try {
            ptn = TestReplicationScenarios.metaStoreClient.getPartition(dbName, "ptned", new ArrayList(Arrays.asList("1")));
        } catch (Exception e) {
            assert false;
        }
        Path ptnLoc = new Path(ptn.getSd().getLocation());
        Path tmpLoc = new Path(((TestReplicationScenarios.TEST_PATH) + "/incrementalLoadFailAndRetry"));
        FileSystem dataFs = ptnLoc.getFileSystem(TestReplicationScenarios.hconf);
        assert dataFs.rename(ptnLoc, tmpLoc);
        // Replicate all the events happened so far. It should fail as the data files missing in
        // original path and not available in CM as well.
        TestReplicationScenarios.Tuple incrDump = replDumpDb(dbName, bootstrapDump.lastReplId, null, null);
        verifyFail((((("REPL LOAD " + replDbName) + " FROM '") + (incrDump.dumpLocation)) + "'"), TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), empty, TestReplicationScenarios.driverMirror);
        verifyFail((("SELECT a from " + replDbName) + ".ptned_tmp where (b=1) ORDER BY a"), TestReplicationScenarios.driverMirror);
        // Move the files back to original data location
        assert dataFs.rename(tmpLoc, ptnLoc);
        loadAndVerify(replDbName, incrDump.dumpLocation, incrDump.lastReplId);
        verifyRun((("SELECT a from " + replDbName) + ".ptned where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".ptned_tmp where (b=1) ORDER BY a"), ptn_data_1, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testStatus() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String lastReplDumpId = bootstrapDump.lastReplId;
        // Bootstrap done, now on to incremental. First, we test db-level REPL LOADs.
        // Both db-level and table-level repl.last.id must be updated.
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, "ptned", lastReplDumpId, (("CREATE TABLE " + dbName) + ".ptned(a string) partitioned by (b int) STORED AS TEXTFILE"), replDbName);
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, "ptned", lastReplDumpId, (("ALTER TABLE " + dbName) + ".ptned ADD PARTITION (b=1)"), replDbName);
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, "ptned", lastReplDumpId, (("ALTER TABLE " + dbName) + ".ptned PARTITION (b=1) RENAME TO PARTITION (b=11)"), replDbName);
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, "ptned", lastReplDumpId, (("ALTER TABLE " + dbName) + ".ptned SET TBLPROPERTIES ('blah'='foo')"), replDbName);
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, "ptned_rn", lastReplDumpId, (((("ALTER TABLE " + dbName) + ".ptned RENAME TO  ") + dbName) + ".ptned_rn"), replDbName);
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, "ptned_rn", lastReplDumpId, (("ALTER TABLE " + dbName) + ".ptned_rn DROP PARTITION (b=11)"), replDbName);
        lastReplDumpId = verifyAndReturnDbReplStatus(dbName, null, lastReplDumpId, (("DROP TABLE " + dbName) + ".ptned_rn"), replDbName);
        // DB-level REPL LOADs testing done, now moving on to table level repl loads.
        // In each of these cases, the table-level repl.last.id must move forward, but the
        // db-level last.repl.id must not.
        String lastTblReplDumpId = lastReplDumpId;
        lastTblReplDumpId = verifyAndReturnTblReplStatus(dbName, "ptned2", lastReplDumpId, lastTblReplDumpId, (("CREATE TABLE " + dbName) + ".ptned2(a string) partitioned by (b int) STORED AS TEXTFILE"), replDbName);
        lastTblReplDumpId = verifyAndReturnTblReplStatus(dbName, "ptned2", lastReplDumpId, lastTblReplDumpId, (("ALTER TABLE " + dbName) + ".ptned2 ADD PARTITION (b=1)"), replDbName);
        lastTblReplDumpId = verifyAndReturnTblReplStatus(dbName, "ptned2", lastReplDumpId, lastTblReplDumpId, (("ALTER TABLE " + dbName) + ".ptned2 PARTITION (b=1) RENAME TO PARTITION (b=11)"), replDbName);
        lastTblReplDumpId = verifyAndReturnTblReplStatus(dbName, "ptned2", lastReplDumpId, lastTblReplDumpId, (("ALTER TABLE " + dbName) + ".ptned2 SET TBLPROPERTIES ('blah'='foo')"), replDbName);
        // Note : Not testing table rename because table rename replication is not supported for table-level repl.
        String finalTblReplDumpId = verifyAndReturnTblReplStatus(dbName, "ptned2", lastReplDumpId, lastTblReplDumpId, (("ALTER TABLE " + dbName) + ".ptned2 DROP PARTITION (b=11)"), replDbName);
        /* Comparisons using Strings for event Ids is wrong. This should be numbers since lexical string comparison
        and numeric comparision differ. This requires a broader change where we return the dump Id as long and not string
        fixing this here for now as it was observed in one of the builds where "1001".compareTo("998") results
        in failure of the assertion below.
         */
        Assert.assertTrue(((new Long(Long.parseLong(finalTblReplDumpId)).compareTo(Long.parseLong(lastTblReplDumpId))) > 0));
        // TODO : currently not testing the following scenarios:
        // a) Multi-db wh-level REPL LOAD - need to add that
        // b) Insert into tables - quite a few cases need to be enumerated there, including dyn adds.
    }

    @Test
    public void testConstraints() throws IOException {
        String testName = "constraints";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".tbl1(a string, b string, primary key (a, b) disable novalidate rely)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".tbl2(a string, b string, foreign key (a, b) references ") + dbName) + ".tbl1(a, b) disable novalidate)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".tbl3(a string, b string not null disable, unique (a) disable)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        try {
            List<SQLPrimaryKey> pks = TestReplicationScenarios.metaStoreClientMirror.getPrimaryKeys(new PrimaryKeysRequest(replDbName, "tbl1"));
            Assert.assertEquals(pks.size(), 2);
            List<SQLUniqueConstraint> uks = TestReplicationScenarios.metaStoreClientMirror.getUniqueConstraints(new org.apache.hadoop.hive.metastore.api.UniqueConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, replDbName, "tbl3"));
            Assert.assertEquals(uks.size(), 1);
            List<SQLForeignKey> fks = TestReplicationScenarios.metaStoreClientMirror.getForeignKeys(new ForeignKeysRequest(null, null, replDbName, "tbl2"));
            Assert.assertEquals(fks.size(), 2);
            List<SQLNotNullConstraint> nns = TestReplicationScenarios.metaStoreClientMirror.getNotNullConstraints(new org.apache.hadoop.hive.metastore.api.NotNullConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, replDbName, "tbl3"));
            Assert.assertEquals(nns.size(), 1);
        } catch (TException te) {
            Assert.assertNull(te);
        }
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".tbl4(a string, b string, primary key (a, b) disable novalidate rely)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".tbl5(a string, b string, foreign key (a, b) references ") + dbName) + ".tbl4(a, b) disable novalidate)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".tbl6(a string, b string not null disable, unique (a) disable)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        replDumpId = incrementalDump.lastReplId;
        String pkName = null;
        String ukName = null;
        String fkName = null;
        String nnName = null;
        try {
            List<SQLPrimaryKey> pks = TestReplicationScenarios.metaStoreClientMirror.getPrimaryKeys(new PrimaryKeysRequest(replDbName, "tbl4"));
            Assert.assertEquals(pks.size(), 2);
            pkName = pks.get(0).getPk_name();
            List<SQLUniqueConstraint> uks = TestReplicationScenarios.metaStoreClientMirror.getUniqueConstraints(new org.apache.hadoop.hive.metastore.api.UniqueConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, replDbName, "tbl6"));
            Assert.assertEquals(uks.size(), 1);
            ukName = uks.get(0).getUk_name();
            List<SQLForeignKey> fks = TestReplicationScenarios.metaStoreClientMirror.getForeignKeys(new ForeignKeysRequest(null, null, replDbName, "tbl5"));
            Assert.assertEquals(fks.size(), 2);
            fkName = fks.get(0).getFk_name();
            List<SQLNotNullConstraint> nns = TestReplicationScenarios.metaStoreClientMirror.getNotNullConstraints(new org.apache.hadoop.hive.metastore.api.NotNullConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, replDbName, "tbl6"));
            Assert.assertEquals(nns.size(), 1);
            nnName = nns.get(0).getNn_name();
        } catch (TException te) {
            Assert.assertNull(te);
        }
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".tbl4 DROP CONSTRAINT `") + pkName) + "`"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".tbl4 DROP CONSTRAINT `") + ukName) + "`"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".tbl5 DROP CONSTRAINT `") + fkName) + "`"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".tbl6 DROP CONSTRAINT `") + nnName) + "`"), TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        try {
            List<SQLPrimaryKey> pks = TestReplicationScenarios.metaStoreClientMirror.getPrimaryKeys(new PrimaryKeysRequest(replDbName, "tbl4"));
            Assert.assertTrue(pks.isEmpty());
            List<SQLUniqueConstraint> uks = TestReplicationScenarios.metaStoreClientMirror.getUniqueConstraints(new org.apache.hadoop.hive.metastore.api.UniqueConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, replDbName, "tbl4"));
            Assert.assertTrue(uks.isEmpty());
            List<SQLForeignKey> fks = TestReplicationScenarios.metaStoreClientMirror.getForeignKeys(new ForeignKeysRequest(null, null, replDbName, "tbl5"));
            Assert.assertTrue(fks.isEmpty());
            List<SQLNotNullConstraint> nns = TestReplicationScenarios.metaStoreClientMirror.getNotNullConstraints(new org.apache.hadoop.hive.metastore.api.NotNullConstraintsRequest(Warehouse.DEFAULT_CATALOG_NAME, replDbName, "tbl6"));
            Assert.assertTrue(nns.isEmpty());
        } catch (TException te) {
            Assert.assertNull(te);
        }
    }

    @Test
    public void testRemoveStats() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        String[] unptn_data = new String[]{ "1", "2" };
        String[] ptn_data_1 = new String[]{ "5", "7", "8" };
        String[] ptn_data_2 = new String[]{ "3", "2", "9" };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (name + "_unptn")).toUri().getPath();
        String ptn_locn_1 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn1")).toUri().getPath();
        String ptn_locn_2 = new Path(TestReplicationScenarios.TEST_PATH, (name + "_ptn2")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.createTestDataFile(ptn_locn_1, ptn_data_1);
        TestReplicationScenarios.createTestDataFile(ptn_locn_2, ptn_data_2);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned(a int) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned PARTITION(b=1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".unptned COMPUTE STATISTICS FOR COLUMNS"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".unptned COMPUTE STATISTICS"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".ptned partition(b) COMPUTE STATISTICS FOR COLUMNS"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".ptned partition(b) COMPUTE STATISTICS"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".ptned WHERE b=1"), ptn_data_1, TestReplicationScenarios.driver);
        verifySetup((("SELECT count(*) from " + dbName) + ".unptned"), new String[]{ "2" }, TestReplicationScenarios.driver);
        verifySetup((("SELECT count(*) from " + dbName) + ".ptned"), new String[]{ "3" }, TestReplicationScenarios.driver);
        verifySetup((("SELECT max(a) from " + dbName) + ".unptned"), new String[]{ "2" }, TestReplicationScenarios.driver);
        verifySetup((("SELECT max(a) from " + dbName) + ".ptned where b=1"), new String[]{ "8" }, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned"), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".ptned"), new String[]{ "3" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT max(a) from " + replDbName) + ".unptned"), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT max(a) from " + replDbName) + ".ptned where b=1"), new String[]{ "8" }, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned2(a int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned2"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".ptned2(a int) partitioned by (b int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + ptn_locn_1) + "' OVERWRITE INTO TABLE ") + dbName) + ".ptned2 PARTITION(b=1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".unptned2 COMPUTE STATISTICS FOR COLUMNS"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".unptned2 COMPUTE STATISTICS"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".ptned2 partition(b) COMPUTE STATISTICS FOR COLUMNS"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("ANALYZE TABLE " + dbName) + ".ptned2 partition(b) COMPUTE STATISTICS"), TestReplicationScenarios.driver);
        incrementalLoadAndVerify(dbName, replDumpId, replDbName);
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned2"), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".ptned2"), new String[]{ "3" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT max(a) from " + replDbName) + ".unptned2"), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT max(a) from " + replDbName) + ".ptned2 where b=1"), new String[]{ "8" }, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testDeleteStagingDir() throws IOException {
        String testName = "deleteStagingDir";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        String tableName = "unptned";
        TestReplicationScenarios.run((("CREATE TABLE " + (StatsUtils.getFullyQualifiedTableName(dbName, tableName))) + "(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        String[] unptn_data = new String[]{ "one", "two" };
        String unptn_locn = new Path(TestReplicationScenarios.TEST_PATH, (testName + "_unptn")).toUri().getPath();
        TestReplicationScenarios.createTestDataFile(unptn_locn, unptn_data);
        TestReplicationScenarios.run((((("LOAD DATA LOCAL INPATH '" + unptn_locn) + "' OVERWRITE INTO TABLE ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned"), unptn_data, TestReplicationScenarios.driver);
        // Perform repl
        String replDumpLocn = replDumpDb(dbName, null, null, null).dumpLocation;
        // Reset the driver
        TestReplicationScenarios.driverMirror.close();
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        // Calling close() explicitly to clean up the staging dirs
        TestReplicationScenarios.driverMirror.close();
        // Check result
        Path warehouse = new Path(System.getProperty("test.warehouse.dir", "/tmp"));
        FileSystem fs = FileSystem.get(warehouse.toUri(), TestReplicationScenarios.hconf);
        try {
            Path path = new Path(warehouse, (((replDbName + ".db") + (Path.SEPARATOR)) + tableName));
            // First check if the table dir exists (could have been deleted for some reason in pre-commit tests)
            if (!(fs.exists(path))) {
                return;
            }
            PathFilter filter = new PathFilter() {
                @Override
                public boolean accept(Path path) {
                    return path.getName().startsWith(HiveConf.getVar(TestReplicationScenarios.hconf, STAGINGDIR));
                }
            };
            FileStatus[] statuses = fs.listStatus(path, filter);
            Assert.assertEquals(0, statuses.length);
        } catch (IOException e) {
            TestReplicationScenarios.LOG.error(("Failed to list files in: " + warehouse), e);
            assert false;
        }
    }

    @Test
    public void testCMConflict() throws IOException {
        String testName = "cmConflict";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_dupe";
        // Create table and insert two file of the same content
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO TABLE " + dbName) + ".unptned values('ten')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO TABLE " + dbName) + ".unptned values('ten')"), TestReplicationScenarios.driver);
        // Bootstrap test
        TestReplicationScenarios.Tuple bootstrapDump = replDumpDb(dbName, null, null, null);
        advanceDumpDir();
        TestReplicationScenarios.run(("REPL DUMP " + dbName), TestReplicationScenarios.driver);
        String replDumpLocn = bootstrapDump.dumpLocation;
        String replDumpId = bootstrapDump.lastReplId;
        // Drop two files so they are moved to CM
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".unptned"), TestReplicationScenarios.driver);
        TestReplicationScenarios.LOG.info("Bootstrap-Dump: Dumped to {} with id {}", replDumpLocn, replDumpId);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + replDumpLocn) + "'"), TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned"), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testEventFilters() {
        // Test testing that the filters introduced by EventUtils are working correctly.
        // The current filters we use in ReplicationSemanticAnalyzer is as follows:
        // IMetaStoreClient.NotificationFilter evFilter = EventUtils.andFilter(
        // EventUtils.getDbTblNotificationFilter(dbNameOrPattern, tblNameOrPattern),
        // EventUtils.getEventBoundaryFilter(eventFrom, eventTo),
        // EventUtils.restrictByMessageFormat(MessageFactory.getInstance().getMessageFormat()));
        // So, we test each of those three filters, and then test andFilter itself.
        String dbname = "testfilter_db";
        String tblname = "testfilter_tbl";
        // Test EventUtils.getDbTblNotificationFilter - this is supposed to restrict
        // events to those that match the dbname and tblname provided to the filter.
        // If the tblname passed in to the filter is null, then it restricts itself
        // to dbname-matching alone.
        IMetaStoreClient.NotificationFilter dbTblFilter = new DatabaseAndTableFilter(dbname, tblname);
        IMetaStoreClient.NotificationFilter dbFilter = new DatabaseAndTableFilter(dbname, null);
        Assert.assertFalse(dbTblFilter.accept(null));
        Assert.assertTrue(dbTblFilter.accept(createDummyEvent(dbname, tblname, 0)));
        Assert.assertFalse(dbTblFilter.accept(createDummyEvent(dbname, (tblname + "extra"), 0)));
        Assert.assertFalse(dbTblFilter.accept(createDummyEvent((dbname + "extra"), tblname, 0)));
        Assert.assertFalse(dbFilter.accept(null));
        Assert.assertTrue(dbFilter.accept(createDummyEvent(dbname, tblname, 0)));
        Assert.assertTrue(dbFilter.accept(createDummyEvent(dbname, (tblname + "extra"), 0)));
        Assert.assertFalse(dbFilter.accept(createDummyEvent((dbname + "extra"), tblname, 0)));
        // Test EventUtils.getEventBoundaryFilter - this is supposed to only allow events
        // within a range specified.
        long evBegin = 50;
        long evEnd = 75;
        IMetaStoreClient.NotificationFilter evRangeFilter = new EventBoundaryFilter(evBegin, evEnd);
        Assert.assertTrue((evBegin < evEnd));
        Assert.assertFalse(evRangeFilter.accept(null));
        Assert.assertFalse(evRangeFilter.accept(createDummyEvent(dbname, tblname, (evBegin - 1))));
        Assert.assertTrue(evRangeFilter.accept(createDummyEvent(dbname, tblname, evBegin)));
        Assert.assertTrue(evRangeFilter.accept(createDummyEvent(dbname, tblname, (evBegin + 1))));
        Assert.assertTrue(evRangeFilter.accept(createDummyEvent(dbname, tblname, (evEnd - 1))));
        Assert.assertTrue(evRangeFilter.accept(createDummyEvent(dbname, tblname, evEnd)));
        Assert.assertFalse(evRangeFilter.accept(createDummyEvent(dbname, tblname, (evEnd + 1))));
        // Test EventUtils.restrictByMessageFormat - this restricts events generated to those
        // that match a provided message format
        IMetaStoreClient.NotificationFilter restrictByDefaultMessageFormat = new org.apache.hadoop.hive.metastore.messaging.event.filters.MessageFormatFilter(JSONMessageEncoder.FORMAT);
        IMetaStoreClient.NotificationFilter restrictByArbitraryMessageFormat = new org.apache.hadoop.hive.metastore.messaging.event.filters.MessageFormatFilter(((JSONMessageEncoder.FORMAT) + "_bogus"));
        NotificationEvent dummyEvent = createDummyEvent(dbname, tblname, 0);
        Assert.assertEquals(FORMAT, dummyEvent.getMessageFormat());
        Assert.assertFalse(restrictByDefaultMessageFormat.accept(null));
        Assert.assertTrue(restrictByDefaultMessageFormat.accept(dummyEvent));
        Assert.assertFalse(restrictByArbitraryMessageFormat.accept(dummyEvent));
        // Test andFilter operation.
        IMetaStoreClient.NotificationFilter yes = new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent notificationEvent) {
                return true;
            }
        };
        IMetaStoreClient.NotificationFilter no = new IMetaStoreClient.NotificationFilter() {
            @Override
            public boolean accept(NotificationEvent notificationEvent) {
                return false;
            }
        };
        Assert.assertTrue(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(yes, yes).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(yes, no).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(no, yes).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(no, no).accept(dummyEvent));
        Assert.assertTrue(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(yes, yes, yes).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(yes, yes, no).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(yes, no, yes).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(yes, no, no).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(no, yes, yes).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(no, yes, no).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(no, no, yes).accept(dummyEvent));
        Assert.assertFalse(new org.apache.hadoop.hive.metastore.messaging.event.filters.AndFilter(no, no, no).accept(dummyEvent));
    }

    @Test
    public void testAuthForNotificationAPIs() throws Exception {
        // Setup
        long firstEventId = TestReplicationScenarios.metaStoreClient.getCurrentNotificationEventId().getEventId();
        String dbName = "testAuthForNotificationAPIs";
        TestReplicationScenarios.createDB(dbName, TestReplicationScenarios.driver);
        NotificationEventResponse rsp = TestReplicationScenarios.metaStoreClient.getNextNotification(firstEventId, 0, null);
        Assert.assertEquals(1, rsp.getEventsSize());
        // Test various scenarios
        // Remove the proxy privilege and the auth should fail (in reality the proxy setting should not be changed on the fly)
        TestReplicationScenarios.hconf.unset(TestReplicationScenarios.proxySettingName);
        // Need to explicitly update ProxyUsers
        ProxyUsers.refreshSuperUserGroupsConfiguration(TestReplicationScenarios.hconf);
        // Verify if the auth should fail
        Exception ex = null;
        try {
            rsp = TestReplicationScenarios.metaStoreClient.getNextNotification(firstEventId, 0, null);
        } catch (TException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
        // Disable auth so the call should succeed
        MetastoreConf.setBoolVar(TestReplicationScenarios.hconf, EVENT_DB_NOTIFICATION_API_AUTH, false);
        try {
            rsp = TestReplicationScenarios.metaStoreClient.getNextNotification(firstEventId, 0, null);
            Assert.assertEquals(1, rsp.getEventsSize());
        } finally {
            // Restore the settings
            MetastoreConf.setBoolVar(TestReplicationScenarios.hconf, EVENT_DB_NOTIFICATION_API_AUTH, true);
            TestReplicationScenarios.hconf.set(TestReplicationScenarios.proxySettingName, "*");
            ProxyUsers.refreshSuperUserGroupsConfiguration(TestReplicationScenarios.hconf);
        }
    }

    @Test
    public void testRecycleFileDropTempTable() throws IOException {
        String dbName = TestReplicationScenarios.createDB(testName.getMethodName(), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".normal(a int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".normal"), TestReplicationScenarios.driver);
        String cmDir = TestReplicationScenarios.hconf.getVar(REPLCMDIR);
        Path path = new Path(cmDir);
        FileSystem fs = path.getFileSystem(TestReplicationScenarios.hconf);
        ContentSummary cs = fs.getContentSummary(path);
        long fileCount = cs.getFileCount();
        Assert.assertTrue((fileCount != 0));
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".normal(a int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (1)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TEMPORARY TABLE " + dbName) + ".temp(a int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".temp values (2)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".temp select * from ") + dbName) + ".normal"), TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        long fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".temp values (3)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".temp"), TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".temp values (4)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".temp RENAME to ") + dbName) + ".temp1"), TestReplicationScenarios.driver);
        verifyRun((("SELECT count(*) from " + dbName) + ".temp1"), new String[]{ "1" }, TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".temp1 values (5)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".temp1"), TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
    }

    @Test
    public void testLoadCmPathMissing() throws IOException {
        String dbName = TestReplicationScenarios.createDB(testName.getMethodName(), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".normal(a int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (1)"), TestReplicationScenarios.driver);
        advanceDumpDir();
        TestReplicationScenarios.run(("repl dump " + dbName), true, TestReplicationScenarios.driver);
        String dumpLocation = getResult(0, 0, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".normal"), TestReplicationScenarios.driver);
        String cmDir = TestReplicationScenarios.hconf.getVar(REPLCMDIR);
        Path path = new Path(cmDir);
        FileSystem fs = path.getFileSystem(TestReplicationScenarios.hconf);
        ContentSummary cs = fs.getContentSummary(path);
        long fileCount = cs.getFileCount();
        Assert.assertTrue((fileCount != 0));
        fs.delete(path);
        CommandProcessorResponse ret = TestReplicationScenarios.driverMirror.run((((("REPL LOAD " + dbName) + " FROM '") + dumpLocation) + "'"));
        Assert.assertTrue(((ret.getResponseCode()) == (REPL_FILE_MISSING_FROM_SRC_AND_CM_PATH.getErrorCode())));
        TestReplicationScenarios.run(("drop database " + dbName), true, TestReplicationScenarios.driver);
        fs.create(path, false);
    }

    @Test
    public void testDumpWithTableDirMissing() throws IOException {
        String dbName = TestReplicationScenarios.createDB(testName.getMethodName(), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".normal(a int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (1)"), TestReplicationScenarios.driver);
        Path path = new Path(System.getProperty("test.warehouse.dir", ""));
        path = new Path(path, ((dbName.toLowerCase()) + ".db"));
        path = new Path(path, "normal");
        FileSystem fs = path.getFileSystem(TestReplicationScenarios.hconf);
        fs.delete(path);
        advanceDumpDir();
        CommandProcessorResponse ret = TestReplicationScenarios.driver.run(("REPL DUMP " + dbName));
        Assert.assertEquals(ret.getResponseCode(), FILE_NOT_FOUND.getErrorCode());
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".normal"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run(("drop database " + dbName), true, TestReplicationScenarios.driver);
    }

    @Test
    public void testDumpWithPartitionDirMissing() throws IOException {
        String dbName = TestReplicationScenarios.createDB(testName.getMethodName(), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".normal(a int) PARTITIONED BY (part int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal partition (part= 124) values (1)"), TestReplicationScenarios.driver);
        Path path = new Path(System.getProperty("test.warehouse.dir", ""));
        path = new Path(path, ((dbName.toLowerCase()) + ".db"));
        path = new Path(path, "normal");
        path = new Path(path, "part=124");
        FileSystem fs = path.getFileSystem(TestReplicationScenarios.hconf);
        fs.delete(path);
        advanceDumpDir();
        CommandProcessorResponse ret = TestReplicationScenarios.driver.run(("REPL DUMP " + dbName));
        Assert.assertEquals(ret.getResponseCode(), FILE_NOT_FOUND.getErrorCode());
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".normal"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run(("drop database " + dbName), true, TestReplicationScenarios.driver);
    }

    @Test
    public void testDumpNonReplDatabase() throws IOException {
        String dbName = TestReplicationScenarios.createDBNonRepl(testName.getMethodName(), TestReplicationScenarios.driver);
        verifyFail(("REPL DUMP " + dbName), TestReplicationScenarios.driver);
        verifyFail((("REPL DUMP " + dbName) + " from 1 "), TestReplicationScenarios.driver);
        Assert.assertTrue(TestReplicationScenarios.run((("REPL DUMP " + dbName) + " with ('hive.repl.dump.metadata.only' = 'true')"), true, TestReplicationScenarios.driver));
        Assert.assertTrue(TestReplicationScenarios.run((("REPL DUMP " + dbName) + " from 1  with ('hive.repl.dump.metadata.only' = 'true')"), true, TestReplicationScenarios.driver));
        TestReplicationScenarios.run((("alter database " + dbName) + " set dbproperties ('repl.source.for' = '1, 2, 3')"), TestReplicationScenarios.driver);
        Assert.assertTrue(TestReplicationScenarios.run(("REPL DUMP " + dbName), true, TestReplicationScenarios.driver));
        Assert.assertTrue(TestReplicationScenarios.run((("REPL DUMP " + dbName) + " from 1 "), true, TestReplicationScenarios.driver));
        dbName = TestReplicationScenarios.createDBNonRepl(((testName.getMethodName()) + "_case"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("alter database " + dbName) + " set dbproperties ('repl.SOURCE.for' = '1, 2, 3')"), TestReplicationScenarios.driver);
        Assert.assertTrue(TestReplicationScenarios.run(("REPL DUMP " + dbName), true, TestReplicationScenarios.driver));
        Assert.assertTrue(TestReplicationScenarios.run((("REPL DUMP " + dbName) + " from 1 "), true, TestReplicationScenarios.driver));
    }

    @Test
    public void testRecycleFileNonReplDatabase() throws IOException {
        String dbName = TestReplicationScenarios.createDBNonRepl(testName.getMethodName(), TestReplicationScenarios.driver);
        String cmDir = TestReplicationScenarios.hconf.getVar(REPLCMDIR);
        Path path = new Path(cmDir);
        FileSystem fs = path.getFileSystem(TestReplicationScenarios.hconf);
        ContentSummary cs = fs.getContentSummary(path);
        long fileCount = cs.getFileCount();
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".normal(a int)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (1)"), TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        long fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (3)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("TRUNCATE TABLE " + dbName) + ".normal"), TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal values (4)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("ALTER TABLE " + dbName) + ".normal RENAME to ") + dbName) + ".normal1"), TestReplicationScenarios.driver);
        verifyRun((("SELECT count(*) from " + dbName) + ".normal1"), new String[]{ "1" }, TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
        TestReplicationScenarios.run((("INSERT INTO " + dbName) + ".normal1 values (5)"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("DROP TABLE " + dbName) + ".normal1"), TestReplicationScenarios.driver);
        cs = fs.getContentSummary(path);
        fileCountAfter = cs.getFileCount();
        Assert.assertTrue((fileCount == fileCountAfter));
    }

    @Test
    public void testMoveOptimizationBootstrap() throws IOException {
        String name = testName.getMethodName();
        String dbName = TestReplicationScenarios.createDB(name, TestReplicationScenarios.driver);
        String tableNameNoPart = dbName + "_no_part";
        String tableNamePart = dbName + "_part";
        TestReplicationScenarios.run((" use " + dbName), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + tableNameNoPart) + " (fld int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("CREATE TABLE " + tableNamePart) + " (fld int) partitioned by (part int) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("insert into " + tableNameNoPart) + " values (1) "), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("insert into " + tableNameNoPart) + " values (2) "), TestReplicationScenarios.driver);
        verifyRun(("SELECT fld from " + tableNameNoPart), new String[]{ "1", "2" }, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("insert into " + tableNamePart) + " partition (part=10) values (1) "), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("insert into " + tableNamePart) + " partition (part=10) values (2) "), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((("insert into " + tableNamePart) + " partition (part=11) values (3) "), TestReplicationScenarios.driver);
        verifyRun(("SELECT fld from " + tableNamePart), new String[]{ "1", "2", "3" }, TestReplicationScenarios.driver);
        verifyRun((("SELECT fld from " + tableNamePart) + " where part = 10"), new String[]{ "1", "2" }, TestReplicationScenarios.driver);
        verifyRun((("SELECT fld from " + tableNamePart) + " where part = 11"), new String[]{ "3" }, TestReplicationScenarios.driver);
        String replDbName = dbName + "_replica";
        TestReplicationScenarios.Tuple dump = replDumpDb(dbName, null, null, null);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + (dump.dumpLocation)) + "' with ('hive.repl.enable.move.optimization'='true')"), TestReplicationScenarios.driverMirror);
        verifyRun(("REPL STATUS " + replDbName), dump.lastReplId, TestReplicationScenarios.driverMirror);
        TestReplicationScenarios.run((" use " + replDbName), TestReplicationScenarios.driverMirror);
        verifyRun(("SELECT fld from " + tableNamePart), new String[]{ "1", "2", "3" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT fld from " + tableNamePart) + " where part = 10"), new String[]{ "1", "2" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT fld from " + tableNamePart) + " where part = 11"), new String[]{ "3" }, TestReplicationScenarios.driverMirror);
        verifyRun(("SELECT fld from " + tableNameNoPart), new String[]{ "1", "2" }, TestReplicationScenarios.driverMirror);
        verifyRun(("SELECT count(*) from " + tableNamePart), new String[]{ "3" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + tableNamePart) + " where part = 10"), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + tableNamePart) + " where part = 11"), new String[]{ "1" }, TestReplicationScenarios.driverMirror);
        verifyRun(("SELECT count(*) from " + tableNameNoPart), new String[]{ "2" }, TestReplicationScenarios.driverMirror);
    }

    @Test
    public void testMoveOptimizationIncremental() throws IOException {
        String testName = "testMoveOptimizationIncremental";
        String dbName = TestReplicationScenarios.createDB(testName, TestReplicationScenarios.driver);
        String replDbName = dbName + "_replica";
        TestReplicationScenarios.Tuple bootstrapDump = bootstrapLoadAndVerify(dbName, replDbName);
        String replDumpId = bootstrapDump.lastReplId;
        String[] unptn_data = new String[]{ "eleven", "twelve" };
        TestReplicationScenarios.run((("CREATE TABLE " + dbName) + ".unptned(a string) STORED AS TEXTFILE"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[0])) + "')"), TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned values('") + (unptn_data[1])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("CREATE TABLE " + dbName) + ".unptned_late AS SELECT * FROM ") + dbName) + ".unptned"), TestReplicationScenarios.driver);
        verifySetup((("SELECT * from " + dbName) + ".unptned_late ORDER BY a"), unptn_data, TestReplicationScenarios.driver);
        TestReplicationScenarios.Tuple incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + (incrementalDump.dumpLocation)) + "' with ('hive.repl.enable.move.optimization'='true')"), TestReplicationScenarios.driverMirror);
        verifyRun(("REPL STATUS " + replDbName), incrementalDump.lastReplId, TestReplicationScenarios.driverMirror);
        replDumpId = incrementalDump.lastReplId;
        verifyRun((("SELECT a from " + replDbName) + ".unptned ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_late ORDER BY a"), unptn_data, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned "), "2", TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned_late"), "2", TestReplicationScenarios.driverMirror);
        String[] unptn_data_after_ins = new String[]{ "eleven", "thirteen", "twelve" };
        String[] data_after_ovwrite = new String[]{ "hundred" };
        TestReplicationScenarios.run((((("INSERT INTO TABLE " + dbName) + ".unptned_late values('") + (unptn_data_after_ins[1])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned_late ORDER BY a"), unptn_data_after_ins, TestReplicationScenarios.driver);
        TestReplicationScenarios.run((((("INSERT OVERWRITE TABLE " + dbName) + ".unptned values('") + (data_after_ovwrite[0])) + "')"), TestReplicationScenarios.driver);
        verifySetup((("SELECT a from " + dbName) + ".unptned"), data_after_ovwrite, TestReplicationScenarios.driver);
        incrementalDump = replDumpDb(dbName, replDumpId, null, null);
        TestReplicationScenarios.run((((("REPL LOAD " + replDbName) + " FROM '") + (incrementalDump.dumpLocation)) + "' with ('hive.repl.enable.move.optimization'='true')"), TestReplicationScenarios.driverMirror);
        verifyRun(("REPL STATUS " + replDbName), incrementalDump.lastReplId, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned_late ORDER BY a"), unptn_data_after_ins, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT a from " + replDbName) + ".unptned"), data_after_ovwrite, TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned"), "1", TestReplicationScenarios.driverMirror);
        verifyRun((("SELECT count(*) from " + replDbName) + ".unptned_late "), "3", TestReplicationScenarios.driverMirror);
    }
}

