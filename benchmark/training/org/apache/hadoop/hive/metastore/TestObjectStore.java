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
package org.apache.hadoop.hive.metastore;


import EventMessage.EventType.CREATE_DATABASE;
import MetastoreConf.ConfVars.DBACCESS_SSL_PROPS;
import MetastoreConf.ConfVars.HIVE_CODAHALE_METRICS_REPORTER_CLASSES;
import MetastoreConf.ConfVars.METASTORE_MAX_EVENT_RESPONSE;
import MetastoreConf.ConfVars.METRICS_ENABLED;
import PrincipalType.ROLE;
import PrincipalType.USER;
import Warehouse.DEFAULT_CATALOG_COMMENT;
import com.codahale.metrics.Counter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jdo.Query;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.ObjectStore.RetryingExecutor;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.NotificationEventRequest;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.SQLForeignKey;
import org.apache.hadoop.hive.metastore.api.SQLPrimaryKey;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.hadoop.hive.metastore.metrics.Metrics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ColumnType.STRING_TYPE_NAME;
import static ObjectStore.TRUSTSTORE_PASSWORD_KEY;
import static ObjectStore.TRUSTSTORE_PATH_KEY;
import static ObjectStore.TRUSTSTORE_TYPE_KEY;


@Category(MetastoreUnitTest.class)
public class TestObjectStore {
    private ObjectStore objectStore = null;

    private Configuration conf;

    private static final String DB1 = "testobjectstoredb1";

    private static final String DB2 = "testobjectstoredb2";

    private static final String TABLE1 = "testobjectstoretable1";

    private static final String KEY1 = "testobjectstorekey1";

    private static final String KEY2 = "testobjectstorekey2";

    private static final String OWNER = "testobjectstoreowner";

    private static final String USER1 = "testobjectstoreuser1";

    private static final String ROLE1 = "testobjectstorerole1";

    private static final String ROLE2 = "testobjectstorerole2";

    private static final Logger LOG = LoggerFactory.getLogger(TestObjectStore.class.getName());

    private static final class LongSupplier implements Supplier<Long> {
        public long value = 0;

        @Override
        public Long get() {
            return value;
        }
    }

    @Test
    public void catalogs() throws MetaException, NoSuchObjectException {
        final String[] names = new String[]{ "cat1", "cat2" };
        final String[] locations = new String[]{ "loc1", "loc2" };
        final String[] descriptions = new String[]{ "description 1", "description 2" };
        for (int i = 0; i < (names.length); i++) {
            Catalog cat = new CatalogBuilder().setName(names[i]).setLocation(locations[i]).setDescription(descriptions[i]).build();
            objectStore.createCatalog(cat);
        }
        List<String> fetchedNames = objectStore.getCatalogs();
        Assert.assertEquals(3, fetchedNames.size());
        for (int i = 0; i < ((names.length) - 1); i++) {
            Assert.assertEquals(names[i], fetchedNames.get(i));
            Catalog cat = objectStore.getCatalog(fetchedNames.get(i));
            Assert.assertEquals(names[i], cat.getName());
            Assert.assertEquals(descriptions[i], cat.getDescription());
            Assert.assertEquals(locations[i], cat.getLocationUri());
        }
        Catalog cat = objectStore.getCatalog(fetchedNames.get(2));
        Assert.assertEquals(Warehouse.DEFAULT_CATALOG_NAME, cat.getName());
        Assert.assertEquals(DEFAULT_CATALOG_COMMENT, cat.getDescription());
        // Location will vary by system.
        for (int i = 0; i < (names.length); i++)
            objectStore.dropCatalog(names[i]);

        fetchedNames = objectStore.getCatalogs();
        Assert.assertEquals(1, fetchedNames.size());
    }

    @Test(expected = NoSuchObjectException.class)
    public void getNoSuchCatalog() throws MetaException, NoSuchObjectException {
        objectStore.getCatalog("no_such_catalog");
    }

    @Test(expected = NoSuchObjectException.class)
    public void dropNoSuchCatalog() throws MetaException, NoSuchObjectException {
        objectStore.dropCatalog("no_such_catalog");
    }

    // TODO test dropping non-empty catalog
    /**
     * Test database operations
     */
    @Test
    public void testDatabaseOps() throws InvalidObjectException, MetaException, NoSuchObjectException {
        String catName = "tdo1_cat";
        createTestCatalog(catName);
        Database db1 = new Database(TestObjectStore.DB1, "description", "locationurl", null);
        Database db2 = new Database(TestObjectStore.DB2, "description", "locationurl", null);
        db1.setCatalogName(catName);
        db2.setCatalogName(catName);
        objectStore.createDatabase(db1);
        objectStore.createDatabase(db2);
        List<String> databases = objectStore.getAllDatabases(catName);
        TestObjectStore.LOG.info(("databases: " + databases));
        Assert.assertEquals(2, databases.size());
        Assert.assertEquals(TestObjectStore.DB1, databases.get(0));
        Assert.assertEquals(TestObjectStore.DB2, databases.get(1));
        objectStore.dropDatabase(catName, TestObjectStore.DB1);
        databases = objectStore.getAllDatabases(catName);
        Assert.assertEquals(1, databases.size());
        Assert.assertEquals(TestObjectStore.DB2, databases.get(0));
        objectStore.dropDatabase(catName, TestObjectStore.DB2);
    }

    /**
     * Test table operations
     */
    @Test
    public void testTableOps() throws InvalidInputException, InvalidObjectException, MetaException, NoSuchObjectException {
        Database db1 = new DatabaseBuilder().setName(TestObjectStore.DB1).setDescription("description").setLocation("locationurl").build(conf);
        objectStore.createDatabase(db1);
        StorageDescriptor sd1 = new StorageDescriptor(ImmutableList.of(new FieldSchema("pk_col", "double", null)), "location", null, null, false, 0, new SerDeInfo("SerDeName", "serializationLib", null), null, null, null);
        HashMap<String, String> params = new HashMap<>();
        params.put("EXTERNAL", "false");
        Table tbl1 = new Table(TestObjectStore.TABLE1, TestObjectStore.DB1, "owner", 1, 2, 3, sd1, null, params, null, null, "MANAGED_TABLE");
        objectStore.createTable(tbl1);
        List<String> tables = objectStore.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1);
        Assert.assertEquals(1, tables.size());
        Assert.assertEquals(TestObjectStore.TABLE1, tables.get(0));
        StorageDescriptor sd2 = new StorageDescriptor(ImmutableList.of(new FieldSchema("fk_col", "double", null)), "location", null, null, false, 0, new SerDeInfo("SerDeName", "serializationLib", null), null, null, null);
        Table newTbl1 = new Table(("new" + (TestObjectStore.TABLE1)), TestObjectStore.DB1, "owner", 1, 2, 3, sd2, null, params, null, null, "MANAGED_TABLE");
        // Change different fields and verify they were altered
        newTbl1.setOwner("role1");
        newTbl1.setOwnerType(ROLE);
        objectStore.alterTable(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, newTbl1, null);
        tables = objectStore.getTables(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, "new*");
        Assert.assertEquals(1, tables.size());
        Assert.assertEquals(("new" + (TestObjectStore.TABLE1)), tables.get(0));
        // Verify fields were altered during the alterTable operation
        Table alteredTable = objectStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, ("new" + (TestObjectStore.TABLE1)));
        Assert.assertEquals("Owner of table was not altered", newTbl1.getOwner(), alteredTable.getOwner());
        Assert.assertEquals("Owner type of table was not altered", newTbl1.getOwnerType(), alteredTable.getOwnerType());
        objectStore.createTable(tbl1);
        tables = objectStore.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1);
        Assert.assertEquals(2, tables.size());
        List<SQLForeignKey> foreignKeys = objectStore.getForeignKeys(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, null, null);
        Assert.assertEquals(0, foreignKeys.size());
        SQLPrimaryKey pk = new SQLPrimaryKey(TestObjectStore.DB1, TestObjectStore.TABLE1, "pk_col", 1, "pk_const_1", false, false, false);
        pk.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.addPrimaryKeys(ImmutableList.of(pk));
        SQLForeignKey fk = new SQLForeignKey(TestObjectStore.DB1, TestObjectStore.TABLE1, "pk_col", TestObjectStore.DB1, ("new" + (TestObjectStore.TABLE1)), "fk_col", 1, 0, 0, "fk_const_1", "pk_const_1", false, false, false);
        objectStore.addForeignKeys(ImmutableList.of(fk));
        // Retrieve from PK side
        foreignKeys = objectStore.getForeignKeys(Warehouse.DEFAULT_CATALOG_NAME, null, null, TestObjectStore.DB1, ("new" + (TestObjectStore.TABLE1)));
        Assert.assertEquals(1, foreignKeys.size());
        List<SQLForeignKey> fks = objectStore.getForeignKeys(Warehouse.DEFAULT_CATALOG_NAME, null, null, TestObjectStore.DB1, ("new" + (TestObjectStore.TABLE1)));
        if (fks != null) {
            for (SQLForeignKey fkcol : fks) {
                objectStore.dropConstraint(fkcol.getCatName(), fkcol.getFktable_db(), fkcol.getFktable_name(), fkcol.getFk_name());
            }
        }
        // Retrieve from FK side
        foreignKeys = objectStore.getForeignKeys(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, null, null);
        Assert.assertEquals(0, foreignKeys.size());
        // Retrieve from PK side
        foreignKeys = objectStore.getForeignKeys(Warehouse.DEFAULT_CATALOG_NAME, null, null, TestObjectStore.DB1, ("new" + (TestObjectStore.TABLE1)));
        Assert.assertEquals(0, foreignKeys.size());
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1);
        tables = objectStore.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1);
        Assert.assertEquals(1, tables.size());
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, ("new" + (TestObjectStore.TABLE1)));
        tables = objectStore.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1);
        Assert.assertEquals(0, tables.size());
        objectStore.dropDatabase(db1.getCatalogName(), TestObjectStore.DB1);
    }

    /**
     * Tests partition operations
     */
    @Test
    public void testPartitionOps() throws InvalidInputException, InvalidObjectException, MetaException, NoSuchObjectException {
        Database db1 = new DatabaseBuilder().setName(TestObjectStore.DB1).setDescription("description").setLocation("locationurl").build(conf);
        objectStore.createDatabase(db1);
        StorageDescriptor sd = createFakeSd("location");
        HashMap<String, String> tableParams = new HashMap<>();
        tableParams.put("EXTERNAL", "false");
        FieldSchema partitionKey1 = new FieldSchema("Country", STRING_TYPE_NAME, "");
        FieldSchema partitionKey2 = new FieldSchema("State", STRING_TYPE_NAME, "");
        Table tbl1 = new Table(TestObjectStore.TABLE1, TestObjectStore.DB1, "owner", 1, 2, 3, sd, Arrays.asList(partitionKey1, partitionKey2), tableParams, null, null, "MANAGED_TABLE");
        objectStore.createTable(tbl1);
        HashMap<String, String> partitionParams = new HashMap<>();
        partitionParams.put("PARTITION_LEVEL_PRIVILEGE", "true");
        List<String> value1 = Arrays.asList("US", "CA");
        Partition part1 = new Partition(value1, TestObjectStore.DB1, TestObjectStore.TABLE1, 111, 111, sd, partitionParams);
        part1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.addPartition(part1);
        List<String> value2 = Arrays.asList("US", "MA");
        Partition part2 = new Partition(value2, TestObjectStore.DB1, TestObjectStore.TABLE1, 222, 222, sd, partitionParams);
        part2.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.addPartition(part2);
        Deadline.startTimer("getPartition");
        List<Partition> partitions = objectStore.getPartitions(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10);
        Assert.assertEquals(2, partitions.size());
        Assert.assertEquals(111, partitions.get(0).getCreateTime());
        Assert.assertEquals(222, partitions.get(1).getCreateTime());
        int numPartitions = objectStore.getNumPartitionsByFilter(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, "");
        Assert.assertEquals(partitions.size(), numPartitions);
        numPartitions = objectStore.getNumPartitionsByFilter(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, "country = \"US\"");
        Assert.assertEquals(2, numPartitions);
        objectStore.dropPartition(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, value1);
        partitions = objectStore.getPartitions(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10);
        Assert.assertEquals(1, partitions.size());
        Assert.assertEquals(222, partitions.get(0).getCreateTime());
        objectStore.dropPartition(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, value2);
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1);
        objectStore.dropDatabase(db1.getCatalogName(), TestObjectStore.DB1);
    }

    /**
     * Test the concurrent drop of same partition would leak transaction.
     * https://issues.apache.org/jira/browse/HIVE-16839
     *
     * Note: the leak happens during a race condition, this test case tries
     * to simulate the race condition on best effort, it have two threads trying
     * to drop the same set of partitions
     */
    @Test
    public void testConcurrentDropPartitions() throws InvalidObjectException, MetaException {
        Database db1 = new DatabaseBuilder().setName(TestObjectStore.DB1).setDescription("description").setLocation("locationurl").build(conf);
        objectStore.createDatabase(db1);
        StorageDescriptor sd = createFakeSd("location");
        HashMap<String, String> tableParams = new HashMap<>();
        tableParams.put("EXTERNAL", "false");
        FieldSchema partitionKey1 = new FieldSchema("Country", STRING_TYPE_NAME, "");
        FieldSchema partitionKey2 = new FieldSchema("State", STRING_TYPE_NAME, "");
        Table tbl1 = new Table(TestObjectStore.TABLE1, TestObjectStore.DB1, "owner", 1, 2, 3, sd, Arrays.asList(partitionKey1, partitionKey2), tableParams, null, null, "MANAGED_TABLE");
        objectStore.createTable(tbl1);
        HashMap<String, String> partitionParams = new HashMap<>();
        partitionParams.put("PARTITION_LEVEL_PRIVILEGE", "true");
        // Create some partitions
        List<List<String>> partNames = new LinkedList<>();
        for (char c = 'A'; c < 'Z'; c++) {
            String name = "" + c;
            partNames.add(Arrays.asList(name, name));
        }
        for (List<String> n : partNames) {
            Partition p = new Partition(n, TestObjectStore.DB1, TestObjectStore.TABLE1, 111, 111, sd, partitionParams);
            p.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
            objectStore.addPartition(p);
        }
        int numThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                for (List<String> p : partNames) {
                    try {
                        objectStore.dropPartition(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, p);
                        System.out.println(("Dropping partition: " + (p.get(0))));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Assert.assertTrue("Got interrupted.", false);
        }
        Assert.assertTrue("Expect no active transactions.", (!(objectStore.isActiveTransaction())));
    }

    /**
     * Checks if the JDO cache is able to handle directSQL partition drops in one session.
     *
     * @throws MetaException
     * 		
     * @throws InvalidObjectException
     * 		
     * @throws NoSuchObjectException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testDirectSQLDropPartitionsCacheInSession() throws InvalidInputException, InvalidObjectException, MetaException, NoSuchObjectException {
        createPartitionedTable(false, false);
        // query the partitions with JDO
        Deadline.startTimer("getPartition");
        List<Partition> partitions = objectStore.getPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10, false, true);
        Assert.assertEquals(3, partitions.size());
        // drop partitions with directSql
        objectStore.dropPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, Arrays.asList("test_part_col=a0", "test_part_col=a1"), true, false);
        // query the partitions with JDO, checking the cache is not causing any problem
        partitions = objectStore.getPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10, false, true);
        Assert.assertEquals(1, partitions.size());
    }

    /**
     * Checks if the JDO cache is able to handle directSQL partition drops cross sessions.
     *
     * @throws MetaException
     * 		
     * @throws InvalidObjectException
     * 		
     * @throws NoSuchObjectException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testDirectSQLDropPartitionsCacheCrossSession() throws InvalidInputException, InvalidObjectException, MetaException, NoSuchObjectException {
        ObjectStore objectStore2 = new ObjectStore();
        objectStore2.setConf(conf);
        createPartitionedTable(false, false);
        // query the partitions with JDO in the 1st session
        Deadline.startTimer("getPartition");
        List<Partition> partitions = objectStore.getPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10, false, true);
        Assert.assertEquals(3, partitions.size());
        // query the partitions with JDO in the 2nd session
        partitions = objectStore2.getPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10, false, true);
        Assert.assertEquals(3, partitions.size());
        // drop partitions with directSql in the 1st session
        objectStore.dropPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, Arrays.asList("test_part_col=a0", "test_part_col=a1"), true, false);
        // query the partitions with JDO in the 2nd session, checking the cache is not causing any
        // problem
        partitions = objectStore2.getPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, 10, false, true);
        Assert.assertEquals(1, partitions.size());
    }

    /**
     * Checks if the directSQL partition drop removes every connected data from the RDBMS tables.
     *
     * @throws MetaException
     * 		
     * @throws InvalidObjectException
     * 		
     * @throws NoSuchObjectException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testDirectSQLDropParitionsCleanup() throws SQLException, InvalidInputException, InvalidObjectException, MetaException, NoSuchObjectException {
        createPartitionedTable(true, true);
        // Check, that every table in the expected state before the drop
        checkBackendTableSize("PARTITIONS", 3);
        checkBackendTableSize("PART_PRIVS", 3);
        checkBackendTableSize("PART_COL_PRIVS", 3);
        checkBackendTableSize("PART_COL_STATS", 3);
        checkBackendTableSize("PARTITION_PARAMS", 3);
        checkBackendTableSize("PARTITION_KEY_VALS", 3);
        checkBackendTableSize("SD_PARAMS", 3);
        checkBackendTableSize("BUCKETING_COLS", 3);
        checkBackendTableSize("SKEWED_COL_NAMES", 3);
        checkBackendTableSize("SDS", 4);// Table has an SDS

        checkBackendTableSize("SORT_COLS", 3);
        checkBackendTableSize("SERDE_PARAMS", 3);
        checkBackendTableSize("SERDES", 4);// Table has a serde

        // drop the partitions
        Deadline.startTimer("dropPartitions");
        objectStore.dropPartitionsInternal(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1, TestObjectStore.TABLE1, Arrays.asList("test_part_col=a0", "test_part_col=a1", "test_part_col=a2"), true, false);
        // Check, if every data is dropped connected to the partitions
        checkBackendTableSize("PARTITIONS", 0);
        checkBackendTableSize("PART_PRIVS", 0);
        checkBackendTableSize("PART_COL_PRIVS", 0);
        checkBackendTableSize("PART_COL_STATS", 0);
        checkBackendTableSize("PARTITION_PARAMS", 0);
        checkBackendTableSize("PARTITION_KEY_VALS", 0);
        checkBackendTableSize("SD_PARAMS", 0);
        checkBackendTableSize("BUCKETING_COLS", 0);
        checkBackendTableSize("SKEWED_COL_NAMES", 0);
        checkBackendTableSize("SDS", 1);// Table has an SDS

        checkBackendTableSize("SORT_COLS", 0);
        checkBackendTableSize("SERDE_PARAMS", 0);
        checkBackendTableSize("SERDES", 1);// Table has a serde

    }

    /**
     * Test master keys operation
     */
    @Test
    public void testMasterKeyOps() throws MetaException, NoSuchObjectException {
        int id1 = objectStore.addMasterKey(TestObjectStore.KEY1);
        int id2 = objectStore.addMasterKey(TestObjectStore.KEY2);
        String[] keys = objectStore.getMasterKeys();
        Assert.assertEquals(2, keys.length);
        Assert.assertEquals(TestObjectStore.KEY1, keys[0]);
        Assert.assertEquals(TestObjectStore.KEY2, keys[1]);
        objectStore.updateMasterKey(id1, ("new" + (TestObjectStore.KEY1)));
        objectStore.updateMasterKey(id2, ("new" + (TestObjectStore.KEY2)));
        keys = objectStore.getMasterKeys();
        Assert.assertEquals(2, keys.length);
        Assert.assertEquals(("new" + (TestObjectStore.KEY1)), keys[0]);
        Assert.assertEquals(("new" + (TestObjectStore.KEY2)), keys[1]);
        objectStore.removeMasterKey(id1);
        keys = objectStore.getMasterKeys();
        Assert.assertEquals(1, keys.length);
        Assert.assertEquals(("new" + (TestObjectStore.KEY2)), keys[0]);
        objectStore.removeMasterKey(id2);
    }

    /**
     * Test role operation
     */
    @Test
    public void testRoleOps() throws InvalidObjectException, MetaException, NoSuchObjectException {
        objectStore.addRole(TestObjectStore.ROLE1, TestObjectStore.OWNER);
        objectStore.addRole(TestObjectStore.ROLE2, TestObjectStore.OWNER);
        List<String> roles = objectStore.listRoleNames();
        Assert.assertEquals(2, roles.size());
        Assert.assertEquals(TestObjectStore.ROLE2, roles.get(1));
        Role role1 = objectStore.getRole(TestObjectStore.ROLE1);
        Assert.assertEquals(TestObjectStore.OWNER, role1.getOwnerName());
        objectStore.grantRole(role1, TestObjectStore.USER1, USER, TestObjectStore.OWNER, ROLE, true);
        objectStore.revokeRole(role1, TestObjectStore.USER1, USER, false);
        objectStore.removeRole(TestObjectStore.ROLE1);
    }

    @Test
    public void testDirectSqlErrorMetrics() throws Exception {
        Configuration conf = MetastoreConf.newMetastoreConf();
        MetastoreConf.setBoolVar(conf, METRICS_ENABLED, true);
        Metrics.initialize(conf);
        MetastoreConf.setVar(conf, HIVE_CODAHALE_METRICS_REPORTER_CLASSES, ("org.apache.hadoop.hive.common.metrics.metrics2.JsonFileMetricsReporter, " + "org.apache.hadoop.hive.common.metrics.metrics2.JmxMetricsReporter"));
        // recall setup so that we get an object store with the metrics initalized
        setUp();
        Counter directSqlErrors = Metrics.getRegistry().getCounters().get(MetricsConstants.DIRECTSQL_ERRORS);
        objectStore.new GetDbHelper(Warehouse.DEFAULT_CATALOG_NAME, "foo", true, true) {
            @Override
            protected Database getSqlResult(ObjectStore.GetHelper<Database> ctx) throws MetaException {
                return null;
            }

            @Override
            protected Database getJdoResult(ObjectStore.GetHelper<Database> ctx) throws MetaException, NoSuchObjectException {
                return null;
            }
        }.run(false);
        Assert.assertEquals(0, directSqlErrors.getCount());
        objectStore.new GetDbHelper(Warehouse.DEFAULT_CATALOG_NAME, "foo", true, true) {
            @Override
            protected Database getSqlResult(ObjectStore.GetHelper<Database> ctx) throws MetaException {
                throw new RuntimeException();
            }

            @Override
            protected Database getJdoResult(ObjectStore.GetHelper<Database> ctx) throws MetaException, NoSuchObjectException {
                return null;
            }
        }.run(false);
        Assert.assertEquals(1, directSqlErrors.getCount());
    }

    @Test
    public void testQueryCloseOnError() throws Exception {
        ObjectStore spy = Mockito.spy(objectStore);
        spy.getAllDatabases(Warehouse.DEFAULT_CATALOG_NAME);
        spy.getAllFunctions(Warehouse.DEFAULT_CATALOG_NAME);
        spy.getAllTables(Warehouse.DEFAULT_CATALOG_NAME, TestObjectStore.DB1);
        spy.getPartitionCount();
        Mockito.verify(spy, Mockito.times(3)).rollbackAndCleanup(Mockito.anyBoolean(), Mockito.<Query>anyObject());
    }

    @Test
    public void testRetryingExecutorSleep() throws Exception {
        RetryingExecutor re = new ObjectStore.RetryingExecutor(MetastoreConf.newMetastoreConf(), null);
        Assert.assertTrue("invalid sleep value", ((re.getSleepInterval()) >= 0));
    }

    /**
     * Test notification operations
     */
    // TODO MS-SPLIT uncomment once we move EventMessage over
    @Test
    public void testNotificationOps() throws InterruptedException, MetaException {
        final int NO_EVENT_ID = 0;
        final int FIRST_EVENT_ID = 1;
        final int SECOND_EVENT_ID = 2;
        NotificationEvent event = new NotificationEvent(0, 0, CREATE_DATABASE.toString(), "");
        NotificationEventResponse eventResponse;
        CurrentNotificationEventId eventId;
        // Verify that there is no notifications available yet
        eventId = objectStore.getCurrentNotificationEventId();
        Assert.assertEquals(NO_EVENT_ID, eventId.getEventId());
        // Verify that addNotificationEvent() updates the NotificationEvent with the new event ID
        objectStore.addNotificationEvent(event);
        Assert.assertEquals(FIRST_EVENT_ID, event.getEventId());
        objectStore.addNotificationEvent(event);
        Assert.assertEquals(SECOND_EVENT_ID, event.getEventId());
        // Verify that objectStore fetches the latest notification event ID
        eventId = objectStore.getCurrentNotificationEventId();
        Assert.assertEquals(SECOND_EVENT_ID, eventId.getEventId());
        // Verify that getNextNotification() returns all events
        eventResponse = objectStore.getNextNotification(new NotificationEventRequest());
        Assert.assertEquals(2, eventResponse.getEventsSize());
        Assert.assertEquals(FIRST_EVENT_ID, eventResponse.getEvents().get(0).getEventId());
        Assert.assertEquals(SECOND_EVENT_ID, eventResponse.getEvents().get(1).getEventId());
        // Verify that getNextNotification(last) returns events after a specified event
        eventResponse = objectStore.getNextNotification(new NotificationEventRequest(FIRST_EVENT_ID));
        Assert.assertEquals(1, eventResponse.getEventsSize());
        Assert.assertEquals(SECOND_EVENT_ID, eventResponse.getEvents().get(0).getEventId());
        // Verify that getNextNotification(last) returns zero events if there are no more notifications available
        eventResponse = objectStore.getNextNotification(new NotificationEventRequest(SECOND_EVENT_ID));
        Assert.assertEquals(0, eventResponse.getEventsSize());
        // Verify that cleanNotificationEvents() cleans up all old notifications
        Thread.sleep(1);
        objectStore.cleanNotificationEvents(1);
        eventResponse = objectStore.getNextNotification(new NotificationEventRequest());
        Assert.assertEquals(0, eventResponse.getEventsSize());
    }

    /**
     * Test metastore configuration property METASTORE_MAX_EVENT_RESPONSE
     */
    @Test
    public void testMaxEventResponse() throws InterruptedException, MetaException {
        NotificationEvent event = new NotificationEvent(0, 0, CREATE_DATABASE.toString(), "");
        MetastoreConf.setLongVar(conf, METASTORE_MAX_EVENT_RESPONSE, 1);
        ObjectStore objs = new ObjectStore();
        objs.setConf(conf);
        // Verify if METASTORE_MAX_EVENT_RESPONSE will limit number of events to respond
        for (int i = 0; i < 3; i++) {
            objs.addNotificationEvent(event);
        }
        NotificationEventResponse eventResponse = objs.getNextNotification(new NotificationEventRequest());
        Assert.assertEquals(1, eventResponse.getEventsSize());
    }

    /**
     * This test calls ObjectStore.setConf methods from multiple threads. Each threads uses its
     * own instance of ObjectStore to simulate thread-local objectstore behaviour.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConcurrentPMFInitialize() throws Exception {
        final String dataSourceProp = "datanucleus.connectionPool.maxPoolSize";
        // Barrier is used to ensure that all threads start race at the same time
        final int numThreads = 10;
        final int numIteration = 50;
        final CyclicBarrier barrier = new CyclicBarrier(numThreads);
        final AtomicInteger counter = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Void>> results = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            final Random random = new Random();
            Configuration conf = MetastoreConf.newMetastoreConf();
            MetaStoreTestUtils.setConfForStandloneMode(conf);
            results.add(executor.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // each thread gets its own ObjectStore to simulate threadLocal store
                    ObjectStore objectStore = new ObjectStore();
                    barrier.await();
                    for (int j = 0; j < numIteration; j++) {
                        // set connectionPool to a random value to increase the likelihood of pmf
                        // re-initialization
                        int randomNumber = random.nextInt(100);
                        if ((randomNumber % 2) == 0) {
                            objectStore.setConf(conf);
                        } else {
                            Assert.assertNotNull(objectStore.getPersistenceManager());
                        }
                        counter.getAndIncrement();
                    }
                    return null;
                }
            }));
        }
        for (Future<Void> future : results) {
            future.get(120, TimeUnit.SECONDS);
        }
        Assert.assertEquals("Unexpected number of setConf calls", (numIteration * numThreads), counter.get());
    }

    /**
     * Test the SSL configuration parameters to ensure that they modify the Java system properties correctly.
     */
    @Test
    public void testSSLPropertiesAreSet() {
        setAndCheckSSLProperties(true, "/tmp/truststore.p12", "password", "pkcs12");
    }

    /**
     * Test the property {@link MetastoreConf.ConfVars#DBACCESS_USE_SSL} to ensure that it correctly
     * toggles whether or not the SSL configuration parameters will be set. Effectively, this is testing whether
     * SSL can be turned on/off correctly.
     */
    @Test
    public void testUseSSLProperty() {
        setAndCheckSSLProperties(false, "/tmp/truststore.jks", "password", "jks");
    }

    /**
     * Test that the deprecated property {@link MetastoreConf.ConfVars#DBACCESS_SSL_PROPS} is overwritten by the
     * MetastoreConf.ConfVars#DBACCESS_SSL_* properties if both are set.
     *
     * This is not an ideal scenario. It is highly recommend to only set the MetastoreConf#ConfVars.DBACCESS_SSL_* properties.
     */
    @Test
    public void testDeprecatedConfigIsOverwritten() {
        // Different from the values in the safe config
        MetastoreConf.setVar(conf, DBACCESS_SSL_PROPS, ((((((TRUSTSTORE_PATH_KEY) + "=/tmp/truststore.p12,") + (TRUSTSTORE_PASSWORD_KEY)) + "=pwd,") + (TRUSTSTORE_TYPE_KEY)) + "=pkcs12"));
        // Safe config
        setAndCheckSSLProperties(true, "/tmp/truststore.jks", "password", "jks");
    }

    /**
     * Test that providing an empty truststore path and truststore password will not throw an exception.
     */
    @Test
    public void testEmptyTrustStoreProps() {
        setAndCheckSSLProperties(true, "", "", "jks");
    }
}

