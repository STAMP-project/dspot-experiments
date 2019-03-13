package org.apache.hadoop.hive.metastore.cache;


import HiveMetaStore.HMSHandler;
import PrincipalType.ROLE;
import SharedCache.ColumStatsWithWriteId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.Warehouse;
import org.junit.Assert;
import org.junit.Test;


public class TestCachedStoreUpdateUsingEvents {
    private RawStore rawStore;

    private SharedCache sharedCache;

    private Configuration conf;

    private HMSHandler hmsHandler;

    private String[] colType = new String[]{ "double", "string" };

    @Test
    public void testDatabaseOpsForUpdateUsingEvents() throws Exception {
        RawStore rawStore = hmsHandler.getMS();
        // Prewarm CachedStore
        CachedStore.setCachePrewarmedState(false);
        CachedStore.prewarm(rawStore);
        // Add a db via rawStore
        String dbName = "testDatabaseOps";
        String dbOwner = "user1";
        Database db = createTestDb(dbName, dbOwner);
        hmsHandler.create_database(db);
        db = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Read database via CachedStore
        Database dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        // Add another db via rawStore
        final String dbName1 = "testDatabaseOps1";
        Database db1 = createTestDb(dbName1, dbOwner);
        hmsHandler.create_database(db1);
        db1 = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        // Read database via CachedStore
        dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        Assert.assertEquals(db1, dbRead);
        // Alter the db via rawStore (can only alter owner or parameters)
        dbOwner = "user2";
        Database newdb = new Database(db);
        newdb.setOwnerName(dbOwner);
        hmsHandler.alter_database(dbName, newdb);
        newdb = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Read db via cachedStore
        dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(newdb, dbRead);
        // Add another db via rawStore
        final String dbName2 = "testDatabaseOps2";
        Database db2 = createTestDb(dbName2, dbOwner);
        hmsHandler.create_database(db2);
        db2 = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName2);
        // Alter db "testDatabaseOps" via rawStore
        dbOwner = "user1";
        newdb = new Database(db);
        newdb.setOwnerName(dbOwner);
        hmsHandler.alter_database(dbName, newdb);
        newdb = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Drop db "testDatabaseOps1" via rawStore
        Database dropDb = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        hmsHandler.drop_database(dbName1, true, true);
        // Read the newly added db via CachedStore
        dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName2);
        Assert.assertEquals(db2, dbRead);
        // Read the altered db via CachedStore (altered user from "user2" to "user1")
        dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(newdb, dbRead);
        // Try to read the dropped db after cache update
        dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName1);
        Assert.assertEquals(null, dbRead);
        // Clean up
        hmsHandler.drop_database(dbName, true, true);
        hmsHandler.drop_database(dbName2, true, true);
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }

    @Test
    public void testTableOpsForUpdateUsingEvents() throws Exception {
        long lastEventId = -1;
        RawStore rawStore = hmsHandler.getMS();
        // Prewarm CachedStore
        CachedStore.setCachePrewarmedState(false);
        CachedStore.prewarm(rawStore);
        // Add a db via rawStore
        String dbName = "test_table_ops";
        String dbOwner = "user1";
        Database db = createTestDb(dbName, dbOwner);
        hmsHandler.create_database(db);
        db = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Add a table via rawStore
        String tblName = "tbl";
        String tblOwner = "user1";
        FieldSchema col1 = new FieldSchema("col1", "int", "integer column");
        FieldSchema col2 = new FieldSchema("col2", "string", "string column");
        List<FieldSchema> cols = new ArrayList<FieldSchema>();
        cols.add(col1);
        cols.add(col2);
        List<FieldSchema> ptnCols = new ArrayList<FieldSchema>();
        Table tbl = createTestTbl(dbName, tblName, tblOwner, cols, ptnCols);
        hmsHandler.create_table(tbl);
        tbl = rawStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        // Read database, table via CachedStore
        Database dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        Assert.assertEquals(db, dbRead);
        Table tblRead = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        compareTables(tblRead, tbl);
        // Add a new table via rawStore
        String tblName2 = "tbl2";
        Table tbl2 = createTestTbl(dbName, tblName2, tblOwner, cols, ptnCols);
        hmsHandler.create_table(tbl2);
        tbl2 = rawStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName2);
        // Alter table "tbl" via rawStore
        tblOwner = "role1";
        Table newTable = new Table(tbl);
        newTable.setOwner(tblOwner);
        newTable.setOwnerType(ROLE);
        hmsHandler.alter_table(dbName, tblName, newTable);
        newTable = rawStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        Assert.assertEquals("Owner of the table did not change.", tblOwner, newTable.getOwner());
        Assert.assertEquals("Owner type of the table did not change", ROLE, newTable.getOwnerType());
        // Drop table "tbl2" via rawStore
        hmsHandler.drop_table(dbName, tblName2, true);
        // Read the altered "tbl" via CachedStore
        tblRead = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        compareTables(tblRead, newTable);
        // Try to read the dropped "tbl2" via CachedStore (should throw exception)
        tblRead = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName2);
        Assert.assertNull(tblRead);
        // Clean up
        hmsHandler.drop_database(dbName, true, true);
        tblRead = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName2);
        Assert.assertNull(tblRead);
        tblRead = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        Assert.assertNull(tblRead);
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }

    @Test
    public void testPartitionOpsForUpdateUsingEvents() throws Exception {
        long lastEventId = -1;
        RawStore rawStore = hmsHandler.getMS();
        // Prewarm CachedStore
        CachedStore.setCachePrewarmedState(false);
        CachedStore.prewarm(rawStore);
        // Add a db via rawStore
        String dbName = "test_partition_ops";
        String dbOwner = "user1";
        Database db = createTestDb(dbName, dbOwner);
        hmsHandler.create_database(db);
        db = rawStore.getDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        // Add a table via rawStore
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
        hmsHandler.create_table(tbl);
        tbl = rawStore.getTable(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName);
        final String ptnColVal1 = "aaa";
        Map<String, String> partParams = new HashMap<String, String>();
        Partition ptn1 = new Partition(Arrays.asList(ptnColVal1), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn1.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        hmsHandler.add_partition(ptn1);
        ptn1 = rawStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1));
        final String ptnColVal2 = "bbb";
        Partition ptn2 = new Partition(Arrays.asList(ptnColVal2), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn2.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        hmsHandler.add_partition(ptn2);
        ptn2 = rawStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
        // Read database, table, partition via CachedStore
        Database dbRead = sharedCache.getDatabaseFromCache(Warehouse.DEFAULT_CATALOG_NAME.toLowerCase(), dbName.toLowerCase());
        Assert.assertEquals(db, dbRead);
        Table tblRead = sharedCache.getTableFromCache(Warehouse.DEFAULT_CATALOG_NAME.toLowerCase(), dbName.toLowerCase(), tblName.toLowerCase());
        compareTables(tbl, tblRead);
        Partition ptn1Read = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME.toLowerCase(), dbName.toLowerCase(), tblName.toLowerCase(), Arrays.asList(ptnColVal1));
        comparePartitions(ptn1, ptn1Read);
        Partition ptn2Read = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME.toLowerCase(), dbName.toLowerCase(), tblName.toLowerCase(), Arrays.asList(ptnColVal2));
        comparePartitions(ptn2, ptn2Read);
        // Add a new partition via rawStore
        final String ptnColVal3 = "ccc";
        Partition ptn3 = new Partition(Arrays.asList(ptnColVal3), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn3.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        hmsHandler.add_partition(ptn3);
        ptn3 = rawStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal3));
        // Alter an existing partition ("aaa") via rawStore
        ptn1 = rawStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1));
        final String ptnColVal1Alt = "aaa";
        Partition ptn1Atl = new Partition(Arrays.asList(ptnColVal1Alt), dbName, tblName, 0, 0, tbl.getSd(), partParams);
        ptn1Atl.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        hmsHandler.alter_partitions(dbName, tblName, Arrays.asList(ptn1Atl));
        ptn1Atl = rawStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1Alt));
        // Drop an existing partition ("bbb") via rawStore
        Partition ptnDrop = rawStore.getPartition(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
        hmsHandler.drop_partition(dbName, tblName, Arrays.asList(ptnColVal2), false);
        // Read the newly added partition via CachedStore
        Partition ptnRead = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal3));
        comparePartitions(ptn3, ptnRead);
        // Read the altered partition via CachedStore
        ptnRead = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1Alt));
        Assert.assertEquals(ptn1Atl.getParameters(), ptnRead.getParameters());
        ptnRead = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal2));
        Assert.assertEquals(null, ptnRead);
        // Drop table "tbl" via rawStore, it should remove the partition also
        hmsHandler.drop_table(dbName, tblName, true);
        ptnRead = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal1Alt));
        Assert.assertEquals(null, ptnRead);
        ptnRead = sharedCache.getPartitionFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Arrays.asList(ptnColVal3));
        Assert.assertEquals(null, ptnRead);
        // Clean up
        rawStore.dropDatabase(Warehouse.DEFAULT_CATALOG_NAME, dbName);
        sharedCache.getDatabaseCache().clear();
        sharedCache.getTableCache().clear();
        sharedCache.getSdCache().clear();
    }

    @Test
    public void testTableColumnStatistics() throws Throwable {
        String dbName = "column_stats_test_db";
        String tblName = "tbl";
        testTableColStatInternal(dbName, tblName, false);
    }

    @Test
    public void testTableColumnStatisticsTxnTable() throws Throwable {
        String dbName = "column_stats_test_db_txn";
        String tblName = "tbl_txn";
        testTableColStatInternal(dbName, tblName, true);
    }

    @Test
    public void testTableColumnStatisticsTxnTableMulti() throws Throwable {
        String dbName = "column_stats_test_db_txn_multi";
        String tblName = "tbl_part";
        String[] colName = new String[]{ "income", "name" };
        double highValue = 1200000.4525;
        double avgColLen = 50.3;
        setUpBeforeTest(dbName, null, colName, true);
        createTableWithPart(dbName, tblName, colName, true);
        List<String> partitions = hmsHandler.get_partition_names(dbName, tblName, ((short) (-1)));
        String partName = partitions.get(0);
        updatePartColStats(dbName, tblName, true, colName, partName, highValue, avgColLen);
        updatePartColStats(dbName, tblName, true, colName, partName, 1200000.4521, avgColLen);
        updatePartColStats(dbName, tblName, true, colName, partName, highValue, 34.78);
    }

    @Test
    public void testTableColumnStatisticsTxnTableMultiAbort() throws Throwable {
        String dbName = "column_stats_test_db_txn_multi_abort";
        String tblName = "tbl_part";
        String[] colName = new String[]{ "income", "name" };
        double highValue = 1200000.4525;
        double avgColLen = 50.3;
        setUpBeforeTest(dbName, null, colName, true);
        createTableWithPart(dbName, tblName, colName, true);
        List<String> partitions = hmsHandler.get_partition_names(dbName, tblName, ((short) (-1)));
        String partName = partitions.get(0);
        List<Long> txnIds = allocateTxns(1);
        long writeId = allocateWriteIds(txnIds, dbName, tblName).get(0).getWriteId();
        String validWriteIds = getValidWriteIds(dbName, tblName);
        // create a new columnstatistics desc to represent partition level column stats
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
        statsDesc.setDbName(dbName);
        statsDesc.setTableName(tblName);
        statsDesc.setPartName(partName);
        statsDesc.setIsTblLevel(false);
        ColumnStatistics colStats = new ColumnStatistics();
        colStats.setStatsDesc(statsDesc);
        colStats.setStatsObj(getStatsObjects(dbName, tblName, colName, highValue, avgColLen));
        SetPartitionsStatsRequest setTblColStat = new SetPartitionsStatsRequest(Collections.singletonList(colStats));
        setTblColStat.setWriteId(writeId);
        setTblColStat.setValidWriteIdList(validWriteIds);
        // write stats objs persistently
        hmsHandler.update_partition_column_statistics_req(setTblColStat);
        // abort the txn and verify that the stats got is not compliant.
        AbortTxnRequest rqst = new AbortTxnRequest(txnIds.get(0));
        hmsHandler.abort_txn(rqst);
        allocateWriteIds(allocateTxns(1), dbName, tblName);
        validWriteIds = getValidWriteIds(dbName, tblName);
        Deadline.startTimer("getPartitionColumnStatistics");
        List<ColumnStatistics> statRawStore = rawStore.getPartitionColumnStatistics(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Collections.singletonList(partName), Collections.singletonList(colName[1]), validWriteIds);
        Deadline.stopTimer();
        verifyStat(statRawStore.get(0).getStatsObj(), colName, highValue, avgColLen);
        Assert.assertEquals(statRawStore.get(0).isIsStatsCompliant(), false);
        List<ColumnStatistics> statsListFromCache = sharedCache.getPartitionColStatsListFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Collections.singletonList(partName), Collections.singletonList(colName[1]), validWriteIds, true);
        verifyStat(statsListFromCache.get(0).getStatsObj(), colName, highValue, avgColLen);
        Assert.assertEquals(statsListFromCache.get(0).isIsStatsCompliant(), false);
        SharedCache.ColumStatsWithWriteId columStatsWithWriteId = sharedCache.getPartitionColStatsFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, CachedStore.partNameToVals(partName), colName[1], validWriteIds);
        Assert.assertEquals(columStatsWithWriteId, null);
        validatePartPara(dbName, tblName, partName);
    }

    @Test
    public void testTableColumnStatisticsTxnTableOpenTxn() throws Throwable {
        String dbName = "column_stats_test_db_txn_multi_open";
        String tblName = "tbl_part";
        String[] colName = new String[]{ "income", "name" };
        double highValue = 1200000.4121;
        double avgColLen = 23.3;
        setUpBeforeTest(dbName, null, colName, true);
        createTableWithPart(dbName, tblName, colName, true);
        List<String> partitions = hmsHandler.get_partition_names(dbName, tblName, ((short) (-1)));
        String partName = partitions.get(0);
        // update part col stats successfully.
        updatePartColStats(dbName, tblName, true, colName, partName, 1.2, 12.2);
        List<Long> txnIds = allocateTxns(1);
        long writeId = allocateWriteIds(txnIds, dbName, tblName).get(0).getWriteId();
        String validWriteIds = getValidWriteIds(dbName, tblName);
        // create a new columnstatistics desc to represent partition level column stats
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
        statsDesc.setDbName(dbName);
        statsDesc.setTableName(tblName);
        statsDesc.setPartName(partName);
        statsDesc.setIsTblLevel(false);
        ColumnStatistics colStats = new ColumnStatistics();
        colStats.setStatsDesc(statsDesc);
        colStats.setStatsObj(getStatsObjects(dbName, tblName, colName, highValue, avgColLen));
        SetPartitionsStatsRequest setTblColStat = new SetPartitionsStatsRequest(Collections.singletonList(colStats));
        setTblColStat.setWriteId(writeId);
        setTblColStat.setValidWriteIdList(validWriteIds);
        // write stats objs persistently
        hmsHandler.update_partition_column_statistics_req(setTblColStat);
        // keep the txn open and verify that the stats got is not compliant.
        allocateWriteIds(allocateTxns(1), dbName, tblName);
        validWriteIds = getValidWriteIds(dbName, tblName);
        Deadline.startTimer("getPartitionColumnStatistics");
        List<ColumnStatistics> statRawStore = rawStore.getPartitionColumnStatistics(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Collections.singletonList(partName), Collections.singletonList(colName[1]), validWriteIds);
        Deadline.stopTimer();
        verifyStat(statRawStore.get(0).getStatsObj(), colName, highValue, avgColLen);
        Assert.assertEquals(statRawStore.get(0).isIsStatsCompliant(), false);
        List<ColumnStatistics> statsListFromCache = sharedCache.getPartitionColStatsListFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, Collections.singletonList(partName), Collections.singletonList(colName[1]), validWriteIds, true);
        verifyStat(statsListFromCache.get(0).getStatsObj(), colName, highValue, avgColLen);
        Assert.assertEquals(statsListFromCache.get(0).isIsStatsCompliant(), false);
        SharedCache.ColumStatsWithWriteId columStatsWithWriteId = sharedCache.getPartitionColStatsFromCache(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, CachedStore.partNameToVals(partName), colName[1], validWriteIds);
        Assert.assertEquals(columStatsWithWriteId, null);
        validatePartPara(dbName, tblName, partName);
    }

    @Test
    public void testAggrStat() throws Throwable {
        String dbName = "aggr_stats_test";
        String tblName = "tbl_part";
        String[] colName = new String[]{ "income", "name" };
        setUpBeforeTest(dbName, null, colName, false);
        createTableWithPart(dbName, tblName, colName, false);
        List<String> partitions = hmsHandler.get_partition_names(dbName, tblName, ((short) (-1)));
        String partName = partitions.get(0);
        // update part col stats successfully.
        updatePartColStats(dbName, tblName, false, colName, partitions.get(0), 2, 12);
        updatePartColStats(dbName, tblName, false, colName, partitions.get(1), 4, 10);
        verifyAggrStat(dbName, tblName, colName, partitions, false, 4);
        updatePartColStats(dbName, tblName, false, colName, partitions.get(1), 3, 10);
        verifyAggrStat(dbName, tblName, colName, partitions, false, 3);
    }

    @Test
    public void testAggrStatTxnTable() throws Throwable {
        String dbName = "aggr_stats_test_db_txn";
        String tblName = "tbl_part";
        String[] colName = new String[]{ "income", "name" };
        setUpBeforeTest(dbName, null, colName, true);
        createTableWithPart(dbName, tblName, colName, true);
        List<String> partitions = hmsHandler.get_partition_names(dbName, tblName, ((short) (-1)));
        String partName = partitions.get(0);
        // update part col stats successfully.
        updatePartColStats(dbName, tblName, true, colName, partitions.get(0), 2, 12);
        updatePartColStats(dbName, tblName, true, colName, partitions.get(1), 4, 10);
        verifyAggrStat(dbName, tblName, colName, partitions, true, 4);
        updatePartColStats(dbName, tblName, true, colName, partitions.get(1), 3, 10);
        verifyAggrStat(dbName, tblName, colName, partitions, true, 3);
        List<Long> txnIds = allocateTxns(1);
        long writeId = allocateWriteIds(txnIds, dbName, tblName).get(0).getWriteId();
        String validWriteIds = getValidWriteIds(dbName, tblName);
        // create a new columnstatistics desc to represent partition level column stats
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
        statsDesc.setDbName(dbName);
        statsDesc.setTableName(tblName);
        statsDesc.setPartName(partName);
        statsDesc.setIsTblLevel(false);
        ColumnStatistics colStats = new ColumnStatistics();
        colStats.setStatsDesc(statsDesc);
        colStats.setStatsObj(getStatsObjects(dbName, tblName, colName, 5, 20));
        SetPartitionsStatsRequest setTblColStat = new SetPartitionsStatsRequest(Collections.singletonList(colStats));
        setTblColStat.setWriteId(writeId);
        setTblColStat.setValidWriteIdList(validWriteIds);
        hmsHandler.update_partition_column_statistics_req(setTblColStat);
        Deadline.startTimer("getPartitionSpecsByFilterAndProjection");
        AggrStats aggrStats = rawStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, partitions, Collections.singletonList(colName[0]), validWriteIds);
        Deadline.stopTimer();
        Assert.assertEquals(aggrStats, null);
        // keep the txn open and verify that the stats got is not compliant.
        PartitionsStatsRequest request = new PartitionsStatsRequest(dbName, tblName, Collections.singletonList(colName[0]), partitions);
        request.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        request.setValidWriteIdList(validWriteIds);
        AggrStats aggrStatsCached = hmsHandler.get_aggr_stats_for(request);
        Assert.assertEquals(aggrStatsCached, null);
    }

    @Test
    public void testAggrStatAbortTxn() throws Throwable {
        String dbName = "aggr_stats_test_db_txn_abort";
        String tblName = "tbl_part";
        String[] colName = new String[]{ "income", "name" };
        setUpBeforeTest(dbName, null, colName, true);
        createTableWithPart(dbName, tblName, colName, true);
        List<String> partitions = hmsHandler.get_partition_names(dbName, tblName, ((short) (-1)));
        String partName = partitions.get(0);
        // update part col stats successfully.
        updatePartColStats(dbName, tblName, true, colName, partitions.get(0), 2, 12);
        updatePartColStats(dbName, tblName, true, colName, partitions.get(1), 4, 10);
        verifyAggrStat(dbName, tblName, colName, partitions, true, 4);
        List<Long> txnIds = allocateTxns(4);
        long writeId = allocateWriteIds(txnIds, dbName, tblName).get(0).getWriteId();
        String validWriteIds = getValidWriteIds(dbName, tblName);
        // create a new columnstatistics desc to represent partition level column stats
        ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
        statsDesc.setDbName(dbName);
        statsDesc.setTableName(tblName);
        statsDesc.setPartName(partName);
        statsDesc.setIsTblLevel(false);
        ColumnStatistics colStats = new ColumnStatistics();
        colStats.setStatsDesc(statsDesc);
        colStats.setStatsObj(getStatsObjects(dbName, tblName, colName, 5, 20));
        SetPartitionsStatsRequest setTblColStat = new SetPartitionsStatsRequest(Collections.singletonList(colStats));
        setTblColStat.setWriteId(writeId);
        setTblColStat.setValidWriteIdList(validWriteIds);
        hmsHandler.update_partition_column_statistics_req(setTblColStat);
        AbortTxnRequest abortTxnRequest = new AbortTxnRequest(txnIds.get(0));
        hmsHandler.abort_txn(abortTxnRequest);
        Deadline.startTimer("getPartitionSpecsByFilterAndProjection");
        AggrStats aggrStats = rawStore.get_aggr_stats_for(Warehouse.DEFAULT_CATALOG_NAME, dbName, tblName, partitions, Collections.singletonList(colName[0]), validWriteIds);
        Deadline.stopTimer();
        Assert.assertEquals(aggrStats, null);
        // keep the txn open and verify that the stats got is not compliant.
        PartitionsStatsRequest request = new PartitionsStatsRequest(dbName, tblName, Collections.singletonList(colName[0]), partitions);
        request.setCatName(Warehouse.DEFAULT_CATALOG_NAME);
        request.setValidWriteIdList(validWriteIds);
        AggrStats aggrStatsCached = hmsHandler.get_aggr_stats_for(request);
        Assert.assertEquals(aggrStatsCached, null);
    }
}

