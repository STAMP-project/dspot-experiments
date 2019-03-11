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
package org.apache.hadoop.hive.metastore.cache;


import PrincipalType.ROLE;
import TableType.MANAGED_TABLE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.common.ndv.hll.HyperLogLog;
import org.apache.hadoop.hive.metastore.ObjectStore;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsData;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.columnstats.cache.LongColumnStatsDataInspector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MetastoreCheckinTest.class)
public class TestCachedStore {
    private ObjectStore objectStore;

    private CachedStore cachedStore;

    private SharedCache sharedCache;

    private Configuration conf;

    /**
     * ********************************************************************************************
     * Methods that test CachedStore
     * *******************************************************************************************
     */
    @Test
    public void testDatabaseOps() throws Exception {
        // Add a db via ObjectStore
        String dbName = "testDatabaseOps";
        String dbOwner = "user1";
        Database db = createTestDb(dbName, dbOwner);
        objectStore.createDatabase(db);
        db = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Prewarm CachedStore
        CachedStore.setCachePrewarmedState(false);
        CachedStore.prewarm(objectStore);
        // Read database via CachedStore
        Database dbRead = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        // Add another db via CachedStore
        final String dbName1 = "testDatabaseOps1";
        Database db1 = createTestDb(dbName1, dbOwner);
        cachedStore.createDatabase(db1);
        db1 = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        // Read db via ObjectStore
        dbRead = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        Assert.assertEquals(db1, dbRead);
        // Alter the db via CachedStore (can only alter owner or parameters)
        dbOwner = "user2";
        db = new Database(db);
        db.setOwnerName(dbOwner);
        cachedStore.alterDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName, db);
        db = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Read db via ObjectStore
        dbRead = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        // Add another db via ObjectStore
        final String dbName2 = "testDatabaseOps2";
        Database db2 = createTestDb(dbName2, dbOwner);
        objectStore.createDatabase(db2);
        db2 = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName2);
        // Alter db "testDatabaseOps" via ObjectStore
        dbOwner = "user1";
        db = new Database(db);
        db.setOwnerName(dbOwner);
        objectStore.alterDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName, db);
        db = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Drop db "testDatabaseOps1" via ObjectStore
        objectStore.dropDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        // We update twice to accurately detect if cache is dirty or not
        updateCache(cachedStore);
        updateCache(cachedStore);
        // Read the newly added db via CachedStore
        dbRead = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName2);
        Assert.assertEquals(db2, dbRead);
        // Read the altered db via CachedStore (altered user from "user2" to "user1")
        dbRead = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        // Try to read the dropped db after cache update
        try {
            dbRead = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
            Assert.fail((("The database: " + dbName1) + " should have been removed from the cache after running the update service"));
        } catch (NoSuchObjectException e) {
            // Expected
        }
        // Clean up
        objectStore.dropDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        objectStore.dropDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName2);
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }

    @Test
    public void testTableOps() throws Exception {
        // Add a db via ObjectStore
        String dbName = "testTableOps";
        String dbOwner = "user1";
        Database db = createTestDb(dbName, dbOwner);
        objectStore.createDatabase(db);
        db = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Add a table via ObjectStore
        String tblName = "tbl";
        String tblOwner = "user1";
        FieldSchema col1 = new FieldSchema("col1", "int", "integer column");
        FieldSchema col2 = new FieldSchema("col2", "string", "string column");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        cols.add(col2);
        List<FieldSchema> ptnCols = new ArrayList<FieldSchema>();
        Table tbl = createTestTbl(dbName, tblName, tblOwner, cols, ptnCols);
        objectStore.createTable(tbl);
        tbl = objectStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        // Prewarm CachedStore
        CachedStore.setCachePrewarmedState(false);
        CachedStore.prewarm(objectStore);
        // Read database, table via CachedStore
        Database dbRead = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        Table tblRead = cachedStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        Assert.assertEquals(tbl, tblRead);
        // Add a new table via CachedStore
        String tblName1 = "tbl1";
        Table tbl1 = new Table(tbl);
        tbl1.setTableName(tblName1);
        cachedStore.createTable(tbl1);
        tbl1 = cachedStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName1);
        // Read via object store
        tblRead = objectStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName1);
        Assert.assertEquals(tbl1, tblRead);
        // Add a new table via ObjectStore
        String tblName2 = "tbl2";
        Table tbl2 = new Table(tbl);
        tbl2.setTableName(tblName2);
        objectStore.createTable(tbl2);
        tbl2 = objectStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName2);
        // Alter table "tbl" via ObjectStore
        tblOwner = "role1";
        tbl.setOwner(tblOwner);
        tbl.setOwnerType(ROLE);
        objectStore.alterTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, tbl, null);
        tbl = objectStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        Assert.assertEquals("Owner of the table did not change.", tblOwner, tbl.getOwner());
        Assert.assertEquals("Owner type of the table did not change", ROLE, tbl.getOwnerType());
        // Drop table "tbl1" via ObjectStore
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName1);
        // We update twice to accurately detect if cache is dirty or not
        updateCache(cachedStore);
        updateCache(cachedStore);
        // Read "tbl2" via CachedStore
        tblRead = cachedStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName2);
        Assert.assertEquals(tbl2, tblRead);
        // Read the altered "tbl" via CachedStore
        tblRead = cachedStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        Assert.assertEquals(tbl, tblRead);
        // Try to read the dropped "tbl1" via CachedStore (should throw exception)
        tblRead = cachedStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName1);
        Assert.assertNull(tblRead);
        // Should return "tbl" and "tbl2"
        List<String> tblNames = cachedStore.getTables(Warehouse.DEFAULT_CATALOG_NAME, dbName, "*");
        Assert.assertTrue(tblNames.contains(tblName));
        Assert.assertTrue((!(tblNames.contains(tblName1))));
        Assert.assertTrue(tblNames.contains(tblName2));
        // Clean up
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName2);
        objectStore.dropDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }

    @Test
    public void testPartitionOps() throws Exception {
        // Add a db via ObjectStore
        String dbName = "testPartitionOps";
        String dbOwner = "user1";
        Database db = createTestDb(dbName, dbOwner);
        objectStore.createDatabase(db);
        db = objectStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Add a table via ObjectStore
        String tblName = "tbl";
        String tblOwner = "user1";
        FieldSchema col1 = new FieldSchema("col1", "int", "integer column");
        FieldSchema col2 = new FieldSchema("col2", "string", "string column");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        cols.add(col2);
        FieldSchema ptnCol1 = new FieldSchema("part1", "string", "string partition column");
        List<FieldSchema> ptnCols = new ArrayList<FieldSchema>();
        ptnCols.add(ptnCol1);
        Table tbl = createTestTbl(dbName, tblName, tblOwner, cols, ptnCols);
        objectStore.createTable(tbl);
        tbl = objectStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        final String ptnColVal1 = "aaa";
        Map<String, String> partParams = new HashMap<String, String>();
        Partition ptn1 = new Partition(Arrays.asList(ptnColVal1), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.addPartition(ptn1);
        ptn1 = objectStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1));
        ptn1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        final String ptnColVal2 = "bbb";
        Partition ptn2 = new Partition(Arrays.asList(ptnColVal2), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn2.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.addPartition(ptn2);
        ptn2 = objectStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
        // Prewarm CachedStore
        CachedStore.setCachePrewarmedState(false);
        CachedStore.prewarm(objectStore);
        // Read database, table, partition via CachedStore
        Database dbRead = cachedStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        Table tblRead = cachedStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        Assert.assertEquals(tbl, tblRead);
        Partition ptn1Read = cachedStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1));
        Assert.assertEquals(ptn1, ptn1Read);
        Partition ptn2Read = cachedStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
        Assert.assertEquals(ptn2, ptn2Read);
        // Add a new partition via ObjectStore
        final String ptnColVal3 = "ccc";
        Partition ptn3 = new Partition(Arrays.asList(ptnColVal3), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn3.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.addPartition(ptn3);
        ptn3 = objectStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal3));
        // Alter an existing partition ("aaa") via ObjectStore
        final String ptnColVal1Alt = "aaaAlt";
        Partition ptn1Atl = new Partition(Arrays.asList(ptnColVal1Alt), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn1Atl.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        objectStore.alterPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1), ptn1Atl, null);
        ptn1Atl = objectStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1Alt));
        // Drop an existing partition ("bbb") via ObjectStore
        objectStore.dropPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
        // We update twice to accurately detect if cache is dirty or not
        updateCache(cachedStore);
        updateCache(cachedStore);
        // Read the newly added partition via CachedStore
        Partition ptnRead = cachedStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal3));
        Assert.assertEquals(ptn3, ptnRead);
        // Read the altered partition via CachedStore
        ptnRead = cachedStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1Alt));
        Assert.assertEquals(ptn1Atl, ptnRead);
        // Try to read the dropped partition via CachedStore
        try {
            ptnRead = cachedStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
            Assert.fail((("The partition: " + ptnColVal2) + " should have been removed from the cache after running the update service"));
        } catch (NoSuchObjectException e) {
            // Expected
        }
        // Clean up
        objectStore.dropPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1Alt));
        objectStore.dropPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal3));
        objectStore.dropTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        objectStore.dropDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }

    /**
     * ********************************************************************************************
     * Methods that test SharedCache
     * *******************************************************************************************
     */
    @Test
    public void testSharedStoreDb() {
        Database db1 = createTestDb("db1", "user1");
        Database db2 = createTestDb("db2", "user1");
        Database db3 = createTestDb("db3", "user1");
        Database newDb1 = createTestDb("newdb1", "user1");
        sharedCache.addDatabaseToCache(db1);
        sharedCache.addDatabaseToCache(db2);
        sharedCache.addDatabaseToCache(db3);
        Assert.assertEquals(sharedCache.getCachedDatabaseCount(), 3);
        sharedCache.alterDatabaseInCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", newDb1);
        Assert.assertEquals(sharedCache.getCachedDatabaseCount(), 3);
        sharedCache.removeDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, "db2");
        Assert.assertEquals(sharedCache.getCachedDatabaseCount(), 2);
        List<String> dbs = sharedCache.listCachedDatabases(Warehouse.DEFAULT_CATALOG_NAME);
        Assert.assertEquals(dbs.size(), 2);
        Assert.assertTrue(dbs.contains("newdb1"));
        Assert.assertTrue(dbs.contains("db3"));
    }

    @Test
    public void testSharedStoreTable() {
        Table tbl1 = new Table();
        StorageDescriptor sd1 = new StorageDescriptor();
        List<FieldSchema> cols1 = new ArrayList<>();
        cols1.add(new FieldSchema("col1", "int", ""));
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        sd1.setCols(cols1);
        sd1.setParameters(params1);
        sd1.setLocation("loc1");
        tbl1.setSd(sd1);
        tbl1.setPartitionKeys(new ArrayList());
        Table tbl2 = new Table();
        StorageDescriptor sd2 = new StorageDescriptor();
        List<FieldSchema> cols2 = new ArrayList<>();
        cols2.add(new FieldSchema("col1", "int", ""));
        Map<String, String> params2 = new HashMap<>();
        params2.put("key", "value");
        sd2.setCols(cols2);
        sd2.setParameters(params2);
        sd2.setLocation("loc2");
        tbl2.setSd(sd2);
        tbl2.setPartitionKeys(new ArrayList());
        Table tbl3 = new Table();
        StorageDescriptor sd3 = new StorageDescriptor();
        List<FieldSchema> cols3 = new ArrayList<>();
        cols3.add(new FieldSchema("col3", "int", ""));
        Map<String, String> params3 = new HashMap<>();
        params3.put("key2", "value2");
        sd3.setCols(cols3);
        sd3.setParameters(params3);
        sd3.setLocation("loc3");
        tbl3.setSd(sd3);
        tbl3.setPartitionKeys(new ArrayList());
        Table newTbl1 = new Table();
        newTbl1.setDbName("db2");
        newTbl1.setTableName("tbl1");
        StorageDescriptor newSd1 = new StorageDescriptor();
        List<FieldSchema> newCols1 = new ArrayList<>();
        newCols1.add(new FieldSchema("newcol1", "int", ""));
        Map<String, String> newParams1 = new HashMap<>();
        newParams1.put("key", "value");
        newSd1.setCols(newCols1);
        newSd1.setParameters(params1);
        newSd1.setLocation("loc1");
        newTbl1.setSd(newSd1);
        newTbl1.setPartitionKeys(new ArrayList());
        sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", "tbl1", tbl1);
        sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", "tbl2", tbl2);
        sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", "tbl3", tbl3);
        sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, "db2", "tbl1", tbl1);
        Assert.assertEquals(sharedCache.getCachedTableCount(), 4);
        Assert.assertEquals(sharedCache.getSdCache().size(), 2);
        Table t = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", "tbl1");
        Assert.assertEquals(t.getSd().getLocation(), "loc1");
        sharedCache.removeTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", "tbl1");
        Assert.assertEquals(sharedCache.getCachedTableCount(), 3);
        Assert.assertEquals(sharedCache.getSdCache().size(), 2);
        sharedCache.alterTableInCache(Warehouse.DEFAULT_CATALOG_NAME, "db2", "tbl1", newTbl1);
        Assert.assertEquals(sharedCache.getCachedTableCount(), 3);
        Assert.assertEquals(sharedCache.getSdCache().size(), 3);
        sharedCache.removeTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, "db1", "tbl2");
        Assert.assertEquals(sharedCache.getCachedTableCount(), 2);
        Assert.assertEquals(sharedCache.getSdCache().size(), 2);
    }

    @Test
    public void testSharedStorePartition() {
        String dbName = "db1";
        String tbl1Name = "tbl1";
        String tbl2Name = "tbl2";
        String owner = "user1";
        Database db = createTestDb(dbName, owner);
        sharedCache.addDatabaseToCache(db);
        FieldSchema col1 = new FieldSchema("col1", "int", "integer column");
        FieldSchema col2 = new FieldSchema("col2", "string", "string column");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        cols.add(col2);
        List<FieldSchema> ptnCols = new ArrayList<FieldSchema>();
        Table tbl1 = createTestTbl(dbName, tbl1Name, owner, cols, ptnCols);
        sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, tbl1);
        Table tbl2 = createTestTbl(dbName, tbl2Name, owner, cols, ptnCols);
        sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl2Name, tbl2);
        Partition part1 = new Partition();
        StorageDescriptor sd1 = new StorageDescriptor();
        List<FieldSchema> cols1 = new ArrayList<>();
        cols1.add(new FieldSchema("col1", "int", ""));
        Map<String, String> params1 = new HashMap<>();
        params1.put("key", "value");
        sd1.setCols(cols1);
        sd1.setParameters(params1);
        sd1.setLocation("loc1");
        part1.setSd(sd1);
        part1.setValues(Arrays.asList("201701"));
        Partition part2 = new Partition();
        StorageDescriptor sd2 = new StorageDescriptor();
        List<FieldSchema> cols2 = new ArrayList<>();
        cols2.add(new FieldSchema("col1", "int", ""));
        Map<String, String> params2 = new HashMap<>();
        params2.put("key", "value");
        sd2.setCols(cols2);
        sd2.setParameters(params2);
        sd2.setLocation("loc2");
        part2.setSd(sd2);
        part2.setValues(Arrays.asList("201702"));
        Partition part3 = new Partition();
        StorageDescriptor sd3 = new StorageDescriptor();
        List<FieldSchema> cols3 = new ArrayList<>();
        cols3.add(new FieldSchema("col3", "int", ""));
        Map<String, String> params3 = new HashMap<>();
        params3.put("key2", "value2");
        sd3.setCols(cols3);
        sd3.setParameters(params3);
        sd3.setLocation("loc3");
        part3.setSd(sd3);
        part3.setValues(Arrays.asList("201703"));
        Partition newPart1 = new Partition();
        newPart1.setDbName(dbName);
        newPart1.setTableName(tbl1Name);
        StorageDescriptor newSd1 = new StorageDescriptor();
        List<FieldSchema> newCols1 = new ArrayList<>();
        newCols1.add(new FieldSchema("newcol1", "int", ""));
        Map<String, String> newParams1 = new HashMap<>();
        newParams1.put("key", "value");
        newSd1.setCols(newCols1);
        newSd1.setParameters(params1);
        newSd1.setLocation("loc1new");
        newPart1.setSd(newSd1);
        newPart1.setValues(Arrays.asList("201701"));
        sharedCache.addPartitionToCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, part1);
        sharedCache.addPartitionToCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, part2);
        sharedCache.addPartitionToCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, part3);
        sharedCache.addPartitionToCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl2Name, part1);
        Partition t = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, Arrays.asList("201701"));
        Assert.assertEquals(t.getSd().getLocation(), "loc1");
        sharedCache.removePartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl2Name, Arrays.asList("201701"));
        t = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl2Name, Arrays.asList("201701"));
        Assert.assertNull(t);
        sharedCache.alterPartitionInCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, Arrays.asList("201701"), newPart1);
        t = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tbl1Name, Arrays.asList("201701"));
        Assert.assertEquals(t.getSd().getLocation(), "loc1new");
    }

    @Test
    public void testAggrStatsRepeatedRead() throws Exception {
        String dbName = "testTableColStatsOps";
        String tblName = "tbl";
        String colName = "f1";
        Database db = new DatabaseBuilder().setName(dbName).setLocation("some_location").build(conf);
        cachedStore.createDatabase(db);
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(colName, "int", null));
        List<FieldSchema> partCols = new ArrayList<>();
        partCols.add(new FieldSchema("col", "int", null));
        StorageDescriptor sd = new StorageDescriptor(cols, null, "input", "output", false, 0, new SerDeInfo("serde", "seriallib", new HashMap()), null, null, null);
        Table tbl = new Table(tblName, dbName, null, 0, 0, 0, sd, partCols, new HashMap(), null, null, MANAGED_TABLE.toString());
        tbl.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.createTable(tbl);
        List<String> partVals1 = new ArrayList<>();
        partVals1.add("1");
        List<String> partVals2 = new ArrayList<>();
        partVals2.add("2");
        Partition ptn1 = new Partition(partVals1, dbName, tblName, 0, 0, sd, new HashMap());
        ptn1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.addPartition(ptn1);
        Partition ptn2 = new Partition(partVals2, dbName, tblName, 0, 0, sd, new HashMap());
        ptn2.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.addPartition(ptn2);
        ColumnStatistics stats = new ColumnStatistics();
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc(true, dbName, tblName);
        statsDesc.setPartName("col");
        List<ColumnStatisticsObj> colStatObjs = new ArrayList<>();
        ColumnStatisticsData data = new ColumnStatisticsData();
        ColumnStatisticsObj colStats = new ColumnStatisticsObj(colName, "int", data);
        LongColumnStatsDataInspector longStats = new LongColumnStatsDataInspector();
        longStats.setLowValue(0);
        longStats.setHighValue(100);
        longStats.setNumNulls(50);
        longStats.setNumDVs(30);
        data.setLongStats(longStats);
        colStatObjs.add(colStats);
        stats.setStatsDesc(statsDesc);
        stats.setStatsObj(colStatObjs);
        cachedStore.updatePartitionColumnStatistics(stats.deepCopy(), partVals1, null, (-1));
        cachedStore.updatePartitionColumnStatistics(stats.deepCopy(), partVals2, null, (-1));
        List<String> colNames = new ArrayList<>();
        colNames.add(colName);
        List<String> aggrPartVals = new ArrayList<>();
        aggrPartVals.add("1");
        aggrPartVals.add("2");
        AggrStats aggrStats = cachedStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, aggrPartVals, colNames);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumNulls(), 100);
        aggrStats = cachedStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, aggrPartVals, colNames);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumNulls(), 100);
    }

    @Test
    public void testPartitionAggrStats() throws Exception {
        String dbName = "testTableColStatsOps1";
        String tblName = "tbl1";
        String colName = "f1";
        Database db = new Database(dbName, null, "some_location", null);
        db.setCatalogName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.createDatabase(db);
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(colName, "int", null));
        List<FieldSchema> partCols = new ArrayList<>();
        partCols.add(new FieldSchema("col", "int", null));
        StorageDescriptor sd = new StorageDescriptor(cols, null, "input", "output", false, 0, new SerDeInfo("serde", "seriallib", new HashMap()), null, null, null);
        Table tbl = new Table(tblName, dbName, null, 0, 0, 0, sd, partCols, new HashMap(), null, null, MANAGED_TABLE.toString());
        tbl.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.createTable(tbl);
        List<String> partVals1 = new ArrayList<>();
        partVals1.add("1");
        List<String> partVals2 = new ArrayList<>();
        partVals2.add("2");
        Partition ptn1 = new Partition(partVals1, dbName, tblName, 0, 0, sd, new HashMap());
        ptn1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.addPartition(ptn1);
        Partition ptn2 = new Partition(partVals2, dbName, tblName, 0, 0, sd, new HashMap());
        ptn2.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.addPartition(ptn2);
        ColumnStatistics stats = new ColumnStatistics();
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc(true, dbName, tblName);
        statsDesc.setPartName("col");
        List<ColumnStatisticsObj> colStatObjs = new ArrayList<>();
        ColumnStatisticsData data = new ColumnStatisticsData();
        ColumnStatisticsObj colStats = new ColumnStatisticsObj(colName, "int", data);
        LongColumnStatsDataInspector longStats = new LongColumnStatsDataInspector();
        longStats.setLowValue(0);
        longStats.setHighValue(100);
        longStats.setNumNulls(50);
        longStats.setNumDVs(30);
        data.setLongStats(longStats);
        colStatObjs.add(colStats);
        stats.setStatsDesc(statsDesc);
        stats.setStatsObj(colStatObjs);
        cachedStore.updatePartitionColumnStatistics(stats.deepCopy(), partVals1, null, (-1));
        longStats.setNumDVs(40);
        cachedStore.updatePartitionColumnStatistics(stats.deepCopy(), partVals2, null, (-1));
        List<String> colNames = new ArrayList<>();
        colNames.add(colName);
        List<String> aggrPartVals = new ArrayList<>();
        aggrPartVals.add("1");
        aggrPartVals.add("2");
        AggrStats aggrStats = cachedStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, aggrPartVals, colNames);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumNulls(), 100);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumDVs(), 40);
        aggrStats = cachedStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, aggrPartVals, colNames);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumNulls(), 100);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumDVs(), 40);
    }

    @Test
    public void testPartitionAggrStatsBitVector() throws Exception {
        String dbName = "testTableColStatsOps2";
        String tblName = "tbl2";
        String colName = "f1";
        Database db = new Database(dbName, null, "some_location", null);
        db.setCatalogName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.createDatabase(db);
        List<FieldSchema> cols = new ArrayList<>();
        cols.add(new FieldSchema(colName, "int", null));
        List<FieldSchema> partCols = new ArrayList<>();
        partCols.add(new FieldSchema("col", "int", null));
        StorageDescriptor sd = new StorageDescriptor(cols, null, "input", "output", false, 0, new SerDeInfo("serde", "seriallib", new HashMap()), null, null, null);
        Table tbl = new Table(tblName, dbName, null, 0, 0, 0, sd, partCols, new HashMap(), null, null, MANAGED_TABLE.toString());
        tbl.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.createTable(tbl);
        List<String> partVals1 = new ArrayList<>();
        partVals1.add("1");
        List<String> partVals2 = new ArrayList<>();
        partVals2.add("2");
        Partition ptn1 = new Partition(partVals1, dbName, tblName, 0, 0, sd, new HashMap());
        ptn1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.addPartition(ptn1);
        Partition ptn2 = new Partition(partVals2, dbName, tblName, 0, 0, sd, new HashMap());
        ptn2.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        cachedStore.addPartition(ptn2);
        ColumnStatistics stats = new ColumnStatistics();
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc(true, dbName, tblName);
        statsDesc.setPartName("col");
        List<ColumnStatisticsObj> colStatObjs = new ArrayList<>();
        ColumnStatisticsData data = new ColumnStatisticsData();
        ColumnStatisticsObj colStats = new ColumnStatisticsObj(colName, "int", data);
        LongColumnStatsDataInspector longStats = new LongColumnStatsDataInspector();
        longStats.setLowValue(0);
        longStats.setHighValue(100);
        longStats.setNumNulls(50);
        longStats.setNumDVs(30);
        HyperLogLog hll = HyperLogLog.builder().build();
        hll.addLong(1);
        hll.addLong(2);
        hll.addLong(3);
        longStats.setBitVectors(hll.serialize());
        data.setLongStats(longStats);
        colStatObjs.add(colStats);
        stats.setStatsDesc(statsDesc);
        stats.setStatsObj(colStatObjs);
        cachedStore.updatePartitionColumnStatistics(stats.deepCopy(), partVals1, null, (-1));
        longStats.setNumDVs(40);
        hll = HyperLogLog.builder().build();
        hll.addLong(2);
        hll.addLong(3);
        hll.addLong(4);
        hll.addLong(5);
        longStats.setBitVectors(hll.serialize());
        cachedStore.updatePartitionColumnStatistics(stats.deepCopy(), partVals2, null, (-1));
        List<String> colNames = new ArrayList<>();
        colNames.add(colName);
        List<String> aggrPartVals = new ArrayList<>();
        aggrPartVals.add("1");
        aggrPartVals.add("2");
        AggrStats aggrStats = cachedStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, aggrPartVals, colNames);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumNulls(), 100);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumDVs(), 5);
        aggrStats = cachedStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, aggrPartVals, colNames);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumNulls(), 100);
        Assert.assertEquals(aggrStats.getColStats().get(0).getStatsData().getLongStats().getNumDVs(), 5);
    }

    @Test
    public void testMultiThreadedSharedCacheOps() throws Exception {
        List<String> dbNames = new ArrayList<String>(Arrays.asList("db1", "db2", "db3", "db4", "db5"));
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        ExecutorService executor = Executors.newFixedThreadPool(50, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });
        // Create 5 dbs
        for (String dbName : dbNames) {
            Callable<Object> c = new Callable<Object>() {
                public Object call() {
                    Database db = createTestDb(dbName, "user1");
                    sharedCache.addDatabaseToCache(db);
                    return null;
                }
            };
            tasks.add(c);
        }
        executor.invokeAll(tasks);
        for (String dbName : dbNames) {
            Database db = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName);
            Assert.assertNotNull(db);
            Assert.assertEquals(dbName, db.getName());
        }
        // Created 5 tables under "db1"
        List<String> tblNames = new ArrayList<String>(Arrays.asList("tbl1", "tbl2", "tbl3", "tbl4", "tbl5"));
        tasks.clear();
        for (String tblName : tblNames) {
            FieldSchema col1 = new FieldSchema("col1", "int", "integer column");
            FieldSchema col2 = new FieldSchema("col2", "string", "string column");
            List<FieldSchema> cols = new ArrayList<FieldSchema>();
            cols.add(col1);
            cols.add(col2);
            FieldSchema ptnCol1 = new FieldSchema("part1", "string", "string partition column");
            List<FieldSchema> ptnCols = new ArrayList<FieldSchema>();
            ptnCols.add(ptnCol1);
            Callable<Object> c = new Callable<Object>() {
                public Object call() {
                    Table tbl = createTestTbl(dbNames.get(0), tblName, "user1", cols, ptnCols);
                    sharedCache.addTableToCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, tbl);
                    return null;
                }
            };
            tasks.add(c);
        }
        executor.invokeAll(tasks);
        for (String tblName : tblNames) {
            Table tbl = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName);
            Assert.assertNotNull(tbl);
            Assert.assertEquals(tblName, tbl.getTableName());
        }
        // Add 5 partitions to all tables
        List<String> ptnVals = new ArrayList<String>(Arrays.asList("aaa", "bbb", "ccc", "ddd", "eee"));
        tasks.clear();
        for (String tblName : tblNames) {
            Table tbl = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName);
            for (String ptnVal : ptnVals) {
                Map<String, String> partParams = new HashMap<String, String>();
                Callable<Object> c = new Callable<Object>() {
                    public Object call() {
                        Partition ptn = new Partition(Arrays.asList(ptnVal), dbNames.get(0), tblName, 0, 0, tbl.getSd(), partParams);
                        sharedCache.addPartitionToCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, ptn);
                        return null;
                    }
                };
                tasks.add(c);
            }
        }
        executor.invokeAll(tasks);
        for (String tblName : tblNames) {
            for (String ptnVal : ptnVals) {
                Partition ptn = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, Arrays.asList(ptnVal));
                Assert.assertNotNull(ptn);
                Assert.assertEquals(tblName, ptn.getTableName());
                Assert.assertEquals(tblName, ptn.getTableName());
                Assert.assertEquals(Arrays.asList(ptnVal), ptn.getValues());
            }
        }
        // Drop all partitions from "tbl1", "tbl2", "tbl3" and add 2 new partitions to "tbl4" and "tbl5"
        List<String> newPtnVals = new ArrayList<String>(Arrays.asList("fff", "ggg"));
        List<String> dropPtnTblNames = new ArrayList<String>(Arrays.asList("tbl1", "tbl2", "tbl3"));
        List<String> addPtnTblNames = new ArrayList<String>(Arrays.asList("tbl4", "tbl5"));
        tasks.clear();
        for (String tblName : dropPtnTblNames) {
            for (String ptnVal : ptnVals) {
                Callable<Object> c = new Callable<Object>() {
                    public Object call() {
                        sharedCache.removePartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, Arrays.asList(ptnVal));
                        return null;
                    }
                };
                tasks.add(c);
            }
        }
        for (String tblName : addPtnTblNames) {
            Table tbl = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName);
            for (String ptnVal : newPtnVals) {
                Map<String, String> partParams = new HashMap<String, String>();
                Callable<Object> c = new Callable<Object>() {
                    public Object call() {
                        Partition ptn = new Partition(Arrays.asList(ptnVal), dbNames.get(0), tblName, 0, 0, tbl.getSd(), partParams);
                        sharedCache.addPartitionToCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, ptn);
                        return null;
                    }
                };
                tasks.add(c);
            }
        }
        executor.invokeAll(tasks);
        for (String tblName : addPtnTblNames) {
            for (String ptnVal : newPtnVals) {
                Partition ptn = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, Arrays.asList(ptnVal));
                Assert.assertNotNull(ptn);
                Assert.assertEquals(tblName, ptn.getTableName());
                Assert.assertEquals(tblName, ptn.getTableName());
                Assert.assertEquals(Arrays.asList(ptnVal), ptn.getValues());
            }
        }
        for (String tblName : dropPtnTblNames) {
            List<Partition> ptns = sharedCache.listCachedPartitions(Warehouse.DEFAULT_CATALOG_NAME, dbNames.get(0), tblName, 100);
            Assert.assertEquals(0, ptns.size());
        }
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }
}

