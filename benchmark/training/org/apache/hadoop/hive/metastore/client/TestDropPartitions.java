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
package org.apache.hadoop.hive.metastore.client;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
import org.apache.hadoop.hive.metastore.PartitionDropOptions;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.PartitionBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for dropping partitions.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestDropPartitions extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "test_drop_part_db";

    private static final String TABLE_NAME = "test_drop_part_table";

    private static final String DEFAULT_COL_TYPE = "string";

    private static final String YEAR_COL_NAME = "year";

    private static final String MONTH_COL_NAME = "month";

    private static final short MAX = -1;

    private static final Partition[] PARTITIONS = new Partition[3];

    public TestDropPartitions(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    // Tests for dropPartition(String db_name, String tbl_name, List<String> part_vals,
    // boolean deleteData) method
    @Test
    public void testDropPartition() throws Exception {
        boolean dropSuccessful = client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), false);
        Assert.assertTrue(dropSuccessful);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, false, false);
    }

    @Test
    public void testDropPartitionDeleteData() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), true);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, true, false);
    }

    @Test
    public void testDropPartitionDeleteParentDir() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), true);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[1].getValues(), true);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0], TestDropPartitions.PARTITIONS[1]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, true, false);
        Path parentPath = getParent();
        Assert.assertFalse((("The parent path '" + (parentPath.toString())) + "' should not exist."), metaStore.isPathExists(parentPath));
    }

    @Test
    public void testDropPartitionDeleteDataPurge() throws Exception {
        String tableName = "purge_test";
        Map<String, String> tableParams = new HashMap<>();
        tableParams.put("auto.purge", "true");
        createTable(tableName, TestDropPartitions.getYearPartCol(), tableParams);
        Partition partition1 = createPartition(tableName, null, Lists.newArrayList("2017"), TestDropPartitions.getYearPartCol(), null);
        Partition partition2 = createPartition(tableName, null, Lists.newArrayList("2018"), TestDropPartitions.getYearPartCol(), null);
        client.dropPartition(TestDropPartitions.DB_NAME, tableName, partition1.getValues(), true);
        List<Partition> droppedPartitions = Lists.newArrayList(partition1);
        List<Partition> remainingPartitions = Lists.newArrayList(partition2);
        checkPartitionsAfterDelete(tableName, droppedPartitions, remainingPartitions, true, true);
    }

    @Test
    public void testDropPartitionArchivedPartition() throws Exception {
        String originalLocation = (((metaStore.getWarehouseRoot()) + "/") + (TestDropPartitions.TABLE_NAME)) + "/2016_may";
        String location = (((metaStore.getWarehouseRoot()) + "/") + (TestDropPartitions.TABLE_NAME)) + "/year=2016/month=may";
        metaStore.createFile(new Path(originalLocation), "test");
        Map<String, String> partParams = new HashMap<>();
        partParams.put("is_archived", "true");
        partParams.put("original_location", originalLocation);
        Partition partition = createPartition(TestDropPartitions.TABLE_NAME, location, Lists.newArrayList("2016", "may"), TestDropPartitions.getYearAndMonthPartCols(), partParams);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, Lists.newArrayList("2016", "may"), true);
        List<Partition> partitionsAfterDelete = client.listPartitions(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.MAX);
        Assert.assertFalse(partitionsAfterDelete.contains(partition));
        Assert.assertTrue((("The location '" + location) + "' should exist."), metaStore.isPathExists(new Path(location)));
        Assert.assertFalse((("The original location '" + originalLocation) + "' should not exist."), metaStore.isPathExists(new Path(originalLocation)));
    }

    @Test
    public void testDropPartitionExternalTable() throws Exception {
        String tableName = "external_table";
        Map<String, String> tableParams = new HashMap<>();
        tableParams.put("EXTERNAL", "true");
        tableParams.put("auto.purge", "true");
        createTable(tableName, TestDropPartitions.getYearPartCol(), tableParams);
        String location = (metaStore.getWarehouseRoot()) + "/externalTable/year=2017";
        Partition partition = createPartition(tableName, location, Lists.newArrayList("2017"), TestDropPartitions.getYearPartCol(), null);
        client.dropPartition(TestDropPartitions.DB_NAME, tableName, partition.getValues(), true);
        List<Partition> partitionsAfterDelete = client.listPartitions(TestDropPartitions.DB_NAME, tableName, TestDropPartitions.MAX);
        Assert.assertTrue(partitionsAfterDelete.isEmpty());
        Assert.assertTrue((("The location '" + location) + "' should exist."), metaStore.isPathExists(new Path(location)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionNonExistingDB() throws Exception {
        client.dropPartition("nonexistingdb", TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017"), false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionNonExistingTable() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, "nonexistingtable", Lists.newArrayList("2017"), false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionNullDB() throws Exception {
        client.dropPartition(null, TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017"), false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionNullTable() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, null, Lists.newArrayList("2017"), false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionEmptyDB() throws Exception {
        client.dropPartition("", TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017"), false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionEmptyTable() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, "", Lists.newArrayList("2017"), false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionNullPartVals() throws Exception {
        List<String> partVals = null;
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, partVals, false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionEmptyPartVals() throws Exception {
        List<String> partVals = new ArrayList<>();
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, partVals, false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionNonExistingPartVals() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017", "may"), false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionNullVal() throws Exception {
        List<String> partVals = new ArrayList<>();
        partVals.add(null);
        partVals.add(null);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, partVals, false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionEmptyVal() throws Exception {
        List<String> partVals = new ArrayList<>();
        partVals.add("");
        partVals.add("");
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, partVals, false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionMoreValsInList() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017", "march", "12:00"), false);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionLessValsInList() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017"), false);
    }

    // Tests for dropPartition(String db_name, String tbl_name, List<String> part_vals,
    // PartitionDropOptions options) method
    @Test
    public void testDropPartitionNotDeleteData() throws Exception {
        PartitionDropOptions partDropOptions = PartitionDropOptions.instance();
        partDropOptions.deleteData(false);
        partDropOptions.purgeData(false);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), partDropOptions);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, false, false);
    }

    @Test
    public void testDropPartitionDeleteDataNoPurge() throws Exception {
        PartitionDropOptions partDropOptions = PartitionDropOptions.instance();
        partDropOptions.deleteData(true);
        partDropOptions.purgeData(false);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), partDropOptions);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, true, false);
    }

    @Test
    public void testDropPartitionDeleteDataAndPurge() throws Exception {
        PartitionDropOptions partDropOptions = PartitionDropOptions.instance();
        partDropOptions.deleteData(true);
        partDropOptions.purgeData(true);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), partDropOptions);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, true, true);
    }

    @Test
    public void testDropPartitionDeleteDataAndPurgeExternalTable() throws Exception {
        String tableName = "external_table";
        Map<String, String> tableParams = new HashMap<>();
        tableParams.put("EXTERNAL", "true");
        createTable(tableName, TestDropPartitions.getYearPartCol(), tableParams);
        String location = (metaStore.getWarehouseRoot()) + "/externalTable/year=2017";
        Partition partition = createPartition(tableName, location, Lists.newArrayList("2017"), TestDropPartitions.getYearPartCol(), null);
        PartitionDropOptions partDropOptions = PartitionDropOptions.instance();
        partDropOptions.deleteData(true);
        partDropOptions.purgeData(true);
        client.dropPartition(TestDropPartitions.DB_NAME, tableName, partition.getValues(), partDropOptions);
        List<Partition> partitionsAfterDrop = client.listPartitions(TestDropPartitions.DB_NAME, tableName, TestDropPartitions.MAX);
        Assert.assertTrue(partitionsAfterDrop.isEmpty());
        Assert.assertTrue((("The location '" + location) + "' should exist."), metaStore.isPathExists(new Path(location)));
    }

    @Test
    public void testDropPartitionNotDeleteDataPurge() throws Exception {
        PartitionDropOptions partDropOptions = PartitionDropOptions.instance();
        partDropOptions.deleteData(false);
        partDropOptions.purgeData(true);
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), partDropOptions);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, false, false);
    }

    @Test
    public void testDropPartitionPurgeSetInTable() throws Exception {
        PartitionDropOptions partDropOptions = PartitionDropOptions.instance();
        partDropOptions.deleteData(true);
        partDropOptions.purgeData(false);
        String tableName = "purge_test";
        Map<String, String> tableParams = new HashMap<>();
        tableParams.put("auto.purge", "true");
        createTable(tableName, TestDropPartitions.getYearPartCol(), tableParams);
        Partition partition1 = createPartition(tableName, null, Lists.newArrayList("2017"), TestDropPartitions.getYearPartCol(), null);
        Partition partition2 = createPartition(tableName, null, Lists.newArrayList("2018"), TestDropPartitions.getYearPartCol(), null);
        client.dropPartition(TestDropPartitions.DB_NAME, tableName, partition1.getValues(), true);
        List<Partition> droppedPartitions = Lists.newArrayList(partition1);
        List<Partition> remainingPartitions = Lists.newArrayList(partition2);
        checkPartitionsAfterDelete(tableName, droppedPartitions, remainingPartitions, true, true);
    }

    @Test
    public void testDropPartitionNullPartDropOptions() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, TestDropPartitions.PARTITIONS[0].getValues(), null);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, true, false);
    }

    // Tests for dropPartition(String db_name, String tbl_name, String name,
    // boolean deleteData) method
    @Test
    public void testDropPartitionByName() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "year=2017/month=march", false);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, false, false);
    }

    @Test
    public void testDropPartitionByNameLessValue() throws Exception {
        try {
            client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "year=2017", true);
            Assert.fail("NoSuchObjectException should be thrown.");
        } catch (NoSuchObjectException e) {
            // Expected exception
        }
        List<Partition> droppedPartitions = new ArrayList<>();
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0], TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, false, false);
    }

    @Test
    public void testDropPartitionByNameMoreValue() throws Exception {
        // The extra non existing values will be ignored.
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "year=2017/month=march/day=10", true);
        List<Partition> droppedPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[0]);
        List<Partition> remainingPartitions = Lists.newArrayList(TestDropPartitions.PARTITIONS[1], TestDropPartitions.PARTITIONS[2]);
        checkPartitionsAfterDelete(TestDropPartitions.TABLE_NAME, droppedPartitions, remainingPartitions, true, false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameNonExistingPart() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "year=2017/month=may", true);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameNonExistingTable() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, "nonexistingtable", "year=2017/month=may", true);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameNonExistingDB() throws Exception {
        client.dropPartition("nonexistingdb", TestDropPartitions.TABLE_NAME, "year=2017/month=may", true);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameInvalidName() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "ev=2017/honap=march", true);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameInvalidNameFormat() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "invalidnameformat", true);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameInvalidNameNoValues() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "year=/month=", true);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionByNameNullName() throws Exception {
        String name = null;
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, name, true);
    }

    @Test(expected = MetaException.class)
    public void testDropPartitionByNameEmptyName() throws Exception {
        client.dropPartition(TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "", true);
    }

    @Test
    public void otherCatalog() throws TException {
        String catName = "drop_partition_catalog";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "drop_partition_database_in_other_catalog";
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String tableName = "table_in_other_catalog";
        Table table = new TableBuilder().inDb(db).setTableName(tableName).addCol("id", "int").addCol("name", "string").addPartCol("partcol", "string").create(client, metaStore.getConf());
        Partition[] parts = new Partition[2];
        for (int i = 0; i < (parts.length); i++) {
            parts[i] = new PartitionBuilder().inTable(table).addValue(("a" + i)).build(metaStore.getConf());
        }
        client.add_partitions(Arrays.asList(parts));
        List<Partition> fetched = client.listPartitions(catName, dbName, tableName, ((short) (-1)));
        Assert.assertEquals(parts.length, fetched.size());
        Assert.assertTrue(client.dropPartition(catName, dbName, tableName, Collections.singletonList("a0"), PartitionDropOptions.instance().ifExists(false)));
        try {
            client.getPartition(catName, dbName, tableName, Collections.singletonList("a0"));
            Assert.fail();
        } catch (NoSuchObjectException e) {
            // NOP
        }
        Assert.assertTrue(client.dropPartition(catName, dbName, tableName, "partcol=a1", true));
        try {
            client.getPartition(catName, dbName, tableName, Collections.singletonList("a1"));
            Assert.fail();
        } catch (NoSuchObjectException e) {
            // NOP
        }
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionBogusCatalog() throws Exception {
        client.dropPartition("nosuch", TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, Lists.newArrayList("2017"), false);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testDropPartitionByNameBogusCatalog() throws Exception {
        client.dropPartition("nosuch", TestDropPartitions.DB_NAME, TestDropPartitions.TABLE_NAME, "year=2017", false);
    }
}

