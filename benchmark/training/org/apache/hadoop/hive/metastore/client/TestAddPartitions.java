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
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
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
 * Tests for creating partitions.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestAddPartitions extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "test_partition_db";

    private static final String TABLE_NAME = "test_partition_table";

    private static final String DEFAULT_PARAM_VALUE = "partparamvalue";

    private static final String DEFAULT_PARAM_KEY = "partparamkey";

    private static final String DEFAULT_YEAR_VALUE = "2017";

    private static final String DEFAULT_COL_TYPE = "string";

    private static final String YEAR_COL_NAME = "year";

    private static final String MONTH_COL_NAME = "month";

    private static final short MAX = -1;

    public TestAddPartitions(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    // Tests for the Partition add_partition(Partition partition) method
    @Test
    public void testAddPartition() throws Exception {
        Table table = createTable();
        Partition partition = buildPartition(Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE), TestAddPartitions.getYearPartCol(), 1);
        Partition resultPart = client.add_partition(partition);
        Assert.assertNotNull(resultPart);
        verifyPartition(table, "year=2017", Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE), 1);
    }

    @Test
    public void testAddPartitionTwoValues() throws Exception {
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME);
        Table table = createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getYearAndMonthPartCols(), tableLocation);
        Partition partition = buildPartition(Lists.newArrayList("2017", "march"), TestAddPartitions.getYearAndMonthPartCols(), 1);
        client.add_partition(partition);
        verifyPartition(table, "year=2017/month=march", Lists.newArrayList("2017", "march"), 1);
    }

    @Test
    public void addPartitionOtherCatalog() throws TException {
        String catName = "add_partition_catalog";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "add_partition_database_in_other_catalog";
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String tableName = "table_in_other_catalog";
        Table table = new TableBuilder().inDb(db).setTableName(tableName).addCol("id", "int").addCol("name", "string").addPartCol("partcol", "string").create(client, metaStore.getConf());
        Partition[] parts = new Partition[5];
        for (int i = 0; i < (parts.length); i++) {
            parts[i] = new PartitionBuilder().inTable(table).addValue(("a" + i)).build(metaStore.getConf());
        }
        client.add_partition(parts[0]);
        Assert.assertEquals(2, client.add_partitions(Arrays.asList(parts[1], parts[2])));
        client.add_partitions(Arrays.asList(parts), true, false);
        for (int i = 0; i < (parts.length); i++) {
            Partition fetched = client.getPartition(catName, dbName, tableName, Collections.singletonList(("a" + i)));
            Assert.assertEquals(catName, fetched.getCatName());
            Assert.assertEquals(dbName, fetched.getDbName());
            Assert.assertEquals(tableName, fetched.getTableName());
        }
        client.dropDatabase(catName, dbName, true, true, true);
        client.dropCatalog(catName);
    }

    @Test(expected = InvalidObjectException.class)
    public void noSuchCatalog() throws TException {
        String tableName = "table_for_no_such_catalog";
        Table table = new TableBuilder().setTableName(tableName).addCol("id", "int").addCol("name", "string").addPartCol("partcol", "string").create(client, metaStore.getConf());
        Partition part = new PartitionBuilder().inTable(table).addValue("a").build(metaStore.getConf());
        // Explicitly mis-set the catalog name
        part.setCatName("nosuch");
        client.add_partition(part);
    }

    @Test
    public void testAddPartitionWithDefaultAttributes() throws Exception {
        Table table = createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addValue("2017").setCols(TestAddPartitions.getYearPartCol()).addCol("test_id", "int", "test col id").addCol("test_value", "string", "test col value").build(metaStore.getConf());
        client.add_partition(partition);
        // Check if the default values are set for all unfilled attributes
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2017");
        Assert.assertNotNull(part);
        Assert.assertEquals(TestAddPartitions.TABLE_NAME, part.getTableName());
        Assert.assertEquals(TestAddPartitions.DB_NAME, part.getDbName());
        Assert.assertEquals(Lists.newArrayList("2017"), part.getValues());
        List<FieldSchema> cols = new ArrayList<>();
        cols.addAll(TestAddPartitions.getYearPartCol());
        cols.add(new FieldSchema("test_id", "int", "test col id"));
        cols.add(new FieldSchema("test_value", "string", "test col value"));
        Assert.assertEquals(cols, part.getSd().getCols());
        verifyPartitionAttributesDefaultValues(part, table.getSd().getLocation());
    }

    @Test
    public void testAddPartitionUpperCase() throws Exception {
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME);
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getMonthPartCol(), tableLocation);
        Partition partition = buildPartition(Lists.newArrayList("APRIL"), TestAddPartitions.getMonthPartCol(), 1);
        client.add_partition(partition);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "month=APRIL");
        Assert.assertNotNull(part);
        Assert.assertEquals(TestAddPartitions.TABLE_NAME, part.getTableName());
        Assert.assertEquals(TestAddPartitions.DB_NAME, part.getDbName());
        Assert.assertEquals("APRIL", part.getValues().get(0));
        Assert.assertEquals((tableLocation + "/month=APRIL"), part.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionUpperCaseDBAndTableName() throws Exception {
        // Create table 'test_partition_db.test_add_part_table'
        String tableName = "test_add_part_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (tableName.toUpperCase());
        createTable(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.getYearPartCol(), tableLocation);
        // Create partition with table name 'TEST_ADD_PART_TABLE' and db name 'TEST_PARTITION_DB'
        Partition partition = buildPartition(TestAddPartitions.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2013", (tableLocation + "/year=2013"));
        client.add_partition(partition);
        // Validate the partition attributes
        // The db and table name should be all lower case: 'test_partition_db' and
        // 'test_add_part_table'
        // The location should be saved case-sensitive, it should be
        // warehouse dir + "TEST_ADD_PART_TABLE/year=2017"
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, tableName, "year=2013");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitions.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2013"), part.getSd().getLocation());
        Partition part1 = client.getPartition(TestAddPartitions.DB_NAME.toUpperCase(), tableName.toUpperCase(), "year=2013");
        Assert.assertEquals(part, part1);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionNonExistingDb() throws Exception {
        Partition partition = buildPartition("nonexistingdb", TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionNonExistingTable() throws Exception {
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, "nonexistingtable", TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullDb() throws Exception {
        Partition partition = buildPartition(null, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullTable() throws Exception {
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, null, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionEmptyDb() throws Exception {
        Partition partition = buildPartition("", TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionEmptyTable() throws Exception {
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, "", TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = AlreadyExistsException.class)
    public void testAddPartitionAlreadyExists() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition1);
        client.add_partition(partition2);
    }

    @Test
    public void testAddPartitionsWithSameNameCaseSensitive() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getMonthPartCol(), (((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME)));
        Partition partition1 = buildPartition(Lists.newArrayList("may"), TestAddPartitions.getMonthPartCol(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("MAY"), TestAddPartitions.getMonthPartCol(), 2);
        client.add_partition(partition1);
        client.add_partition(partition2);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "month=MAY");
        Assert.assertEquals(((TestAddPartitions.DEFAULT_PARAM_VALUE) + "2"), part.getParameters().get(((TestAddPartitions.DEFAULT_PARAM_KEY) + "2")));
        Assert.assertEquals(((((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME)) + "/month=MAY"), part.getSd().getLocation());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.setSd(null);
        client.add_partition(partition);
    }

    @Test
    public void testAddPartitionNullColsInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setCols(null);
        client.add_partition(partition);
        // TODO: Not sure that this is the correct behavior. It doesn't make sense to create the
        // partition without column info. This should be investigated later.
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(part);
        Assert.assertNull(part.getSd().getCols());
    }

    @Test
    public void testAddPartitionEmptyColsInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setCols(new ArrayList());
        client.add_partition(partition);
        // TODO: Not sure that this is the correct behavior. It doesn't make sense to create the
        // partition without column info. This should be investigated later.
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(part);
        Assert.assertTrue(part.getSd().getCols().isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullColTypeInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().getCols().get(0).setType(null);
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullColNameInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().getCols().get(0).setName(null);
        client.add_partition(partition);
    }

    @Test
    public void testAddPartitionInvalidColTypeInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().getCols().get(0).setType("xyz");
        client.add_partition(partition);
        // TODO: Not sure that this is the correct behavior. It doesn't make sense to create the
        // partition with column with invalid type. This should be investigated later.
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(part);
        Assert.assertEquals("xyz", part.getSd().getCols().get(0).getType());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionEmptySerdeInfo() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setSerdeInfo(null);
        client.add_partition(partition);
    }

    @Test
    public void testAddPartitionNullLocation() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, ((metaStore.getWarehouseRoot()) + "/addparttest2"));
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE, null);
        client.add_partition(partition);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2017");
        Assert.assertEquals(((metaStore.getWarehouseRoot()) + "/addparttest2/year=2017"), part.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionEmptyLocation() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, ((metaStore.getWarehouseRoot()) + "/addparttest3"));
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE, "");
        client.add_partition(partition);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2017");
        Assert.assertEquals(((metaStore.getWarehouseRoot()) + "/addparttest3/year=2017"), part.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionNullLocationInTableToo() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, null);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE, null);
        client.add_partition(partition);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2017");
        Assert.assertEquals(((metaStore.getWarehouseRoot()) + "/test_partition_db.db/test_partition_table/year=2017"), part.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionForView() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test
    public void testAddPartitionsForViewNullPartLocation() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setLocation(null);
        List<Partition> partitions = Lists.newArrayList(partition);
        client.add_partitions(partitions);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, tableName, "year=2017");
        Assert.assertNull(part.getSd().getLocation());
    }

    @Test
    public void testAddPartitionsForViewNullPartSd() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.setSd(null);
        List<Partition> partitions = Lists.newArrayList(partition);
        client.add_partitions(partitions);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, tableName, "year=2017");
        Assert.assertNull(part.getSd());
    }

    @Test
    public void testAddPartitionForExternalTable() throws Exception {
        String tableName = "part_add_ext_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + tableName;
        String partitionLocation = tableLocation + "/addparttest";
        createExternalTable(tableName, tableLocation);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.DEFAULT_YEAR_VALUE, partitionLocation);
        client.add_partition(partition);
        Partition resultPart = client.getPartition(TestAddPartitions.DB_NAME, tableName, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(resultPart);
        Assert.assertNotNull(resultPart.getSd());
        Assert.assertEquals(partitionLocation, resultPart.getSd().getLocation());
    }

    @Test
    public void testAddPartitionForExternalTableNullLocation() throws Exception {
        String tableName = "part_add_ext_table";
        createExternalTable(tableName, null);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.DEFAULT_YEAR_VALUE, null);
        client.add_partition(partition);
        Partition resultPart = client.getPartition(TestAddPartitions.DB_NAME, tableName, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(resultPart);
        Assert.assertNotNull(resultPart.getSd());
        String defaultTableLocation = ((((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.DB_NAME)) + ".db/") + tableName;
        String defaulPartitionLocation = defaultTableLocation + "/year=2017";
        Assert.assertEquals(defaulPartitionLocation, resultPart.getSd().getLocation());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionTooManyValues() throws Exception {
        createTable();
        Partition partition = buildPartition(Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE, "march"), TestAddPartitions.getYearAndMonthPartCols(), 1);
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNoPartColOnTable() throws Exception {
        new TableBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addCol("test_id", "int", "test col id").addCol("test_value", "string", "test col value").create(client, metaStore.getConf());
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNoColInPartition() throws Exception {
        createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addValue(TestAddPartitions.DEFAULT_YEAR_VALUE).setLocation(((metaStore.getWarehouseRoot()) + "/addparttest")).build(metaStore.getConf());
        client.add_partition(partition);
    }

    @Test
    public void testAddPartitionDifferentNamesAndTypesInColAndTableCol() throws Exception {
        createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addValue("1000").addCol("time", "int").build(metaStore.getConf());
        client.add_partition(partition);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=1000");
        Assert.assertNotNull(part);
        Assert.assertEquals(TestAddPartitions.TABLE_NAME, part.getTableName());
        Assert.assertEquals("1000", part.getValues().get(0));
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNoValueInPartition() throws Exception {
        createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addCol(TestAddPartitions.YEAR_COL_NAME, TestAddPartitions.DEFAULT_COL_TYPE).setLocation(((metaStore.getWarehouseRoot()) + "/addparttest")).build(metaStore.getConf());
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionMorePartColInTable() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getYearAndMonthPartCols(), null);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        client.add_partition(partition);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullPartition() throws Exception {
        client.add_partition(null);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionNullValues() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, null);
        partition.setValues(null);
        client.add_partition(partition);
    }

    @Test
    public void testAddPartitionEmptyValue() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "");
        client.add_partition(partition);
        List<String> partitionNames = client.listPartitionNames(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, ((short) (10)));
        Assert.assertNotNull(partitionNames);
        Assert.assertTrue(((partitionNames.size()) == 1));
        Assert.assertEquals("year=__HIVE_DEFAULT_PARTITION__", partitionNames.get(0));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSetInvalidLocation() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE, "%^#$$%#$testlocation/part1");
        client.add_partition(partition);
    }

    // Tests for int add_partitions(List<Partition> partitions) method
    @Test
    public void testAddPartitions() throws Exception {
        Table table = createTable();
        List<Partition> partitions = new ArrayList<>();
        Partition partition1 = buildPartition(Lists.newArrayList("2017"), TestAddPartitions.getYearPartCol(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("2016"), TestAddPartitions.getYearPartCol(), 2);
        Partition partition3 = buildPartition(Lists.newArrayList("2015"), TestAddPartitions.getYearPartCol(), 3);
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        int numberOfCreatedParts = client.add_partitions(partitions);
        Assert.assertEquals(3, numberOfCreatedParts);
        verifyPartition(table, "year=2017", Lists.newArrayList("2017"), 1);
        verifyPartition(table, "year=2016", Lists.newArrayList("2016"), 2);
        verifyPartition(table, "year=2015", Lists.newArrayList("2015"), 3);
    }

    @Test
    public void testAddPartitionsMultipleValues() throws Exception {
        Table table = createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getYearAndMonthPartCols(), (((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME)));
        Partition partition1 = buildPartition(Lists.newArrayList("2017", "march"), TestAddPartitions.getYearAndMonthPartCols(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("2017", "june"), TestAddPartitions.getYearAndMonthPartCols(), 2);
        Partition partition3 = buildPartition(Lists.newArrayList("2016", "march"), TestAddPartitions.getYearAndMonthPartCols(), 3);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        client.add_partitions(partitions);
        verifyPartition(table, "year=2017/month=march", Lists.newArrayList("2017", "march"), 1);
        verifyPartition(table, "year=2017/month=june", Lists.newArrayList("2017", "june"), 2);
        verifyPartition(table, "year=2016/month=march", Lists.newArrayList("2016", "march"), 3);
    }

    @Test
    public void testAddPartitionsWithDefaultAttributes() throws Exception {
        Table table = createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addValue("2017").setCols(TestAddPartitions.getYearPartCol()).addCol("test_id", "int", "test col id").addCol("test_value", "string", "test col value").build(metaStore.getConf());
        client.add_partitions(Lists.newArrayList(partition));
        // Check if the default values are set for all unfilled attributes
        List<Partition> parts = client.getPartitionsByNames(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList("year=2017"));
        Assert.assertEquals(1, parts.size());
        Partition part = parts.get(0);
        Assert.assertNotNull(part);
        Assert.assertEquals(TestAddPartitions.TABLE_NAME, part.getTableName());
        Assert.assertEquals(TestAddPartitions.DB_NAME, part.getDbName());
        Assert.assertEquals(Lists.newArrayList("2017"), part.getValues());
        List<FieldSchema> cols = new ArrayList<>();
        cols.addAll(TestAddPartitions.getYearPartCol());
        cols.add(new FieldSchema("test_id", "int", "test col id"));
        cols.add(new FieldSchema("test_value", "string", "test col value"));
        Assert.assertEquals(cols, part.getSd().getCols());
        verifyPartitionAttributesDefaultValues(part, table.getSd().getLocation());
    }

    @Test
    public void testAddPartitionsUpperCaseDBAndTableName() throws Exception {
        // Create table 'test_partition_db.test_add_part_table'
        String tableName = "test_add_part_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (tableName.toUpperCase());
        createTable(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.getYearPartCol(), tableLocation);
        // Create partitions with table name 'TEST_ADD_PART_TABLE' and db name 'TEST_PARTITION_DB'
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2017", (tableLocation + "/year=2017"));
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2018", (tableLocation + "/year=2018"));
        client.add_partitions(Lists.newArrayList(partition1, partition2));
        // Validate the partitions attributes
        // The db and table name should be all lower case: 'test_partition_db' and
        // 'test_add_part_table'
        // The location should be saved case-sensitive, it should be
        // warehouse dir + "TEST_ADD_PART_TABLE/year=2017" and
        // warehouse dir + "TEST_ADD_PART_TABLE/year=2018"
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, tableName, "year=2017");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitions.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2017"), part.getSd().getLocation());
        part = client.getPartition(TestAddPartitions.DB_NAME, tableName, "year=2018");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitions.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2018"), part.getSd().getLocation());
    }

    @Test
    public void testAddPartitionsUpperCaseDBAndTableNameInOnePart() throws Exception {
        // Create table 'test_partition_db.test_add_part_table'
        String tableName = "test_add_part_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (tableName.toUpperCase());
        createTable(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.getYearPartCol(), tableLocation);
        // Create two partitions with table name 'test_add_part_table' and db name 'test_partition_db'
        // Create one partition with table name 'TEST_ADD_PART_TABLE' and db name 'TEST_PARTITION_DB'
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, tableName, "2017", (tableLocation + "/year=2017"));
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2018", (tableLocation + "/year=2018"));
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, tableName, "2019", (tableLocation + "/year=2019"));
        try {
            client.add_partitions(Lists.newArrayList(partition1, partition2, partition3));
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<String> partitionNames = client.listPartitionNames(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.MAX);
        Assert.assertNotNull(partitionNames);
        Assert.assertTrue(partitionNames.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullList() throws Exception {
        client.add_partitions(null);
    }

    @Test
    public void testAddPartitionsEmptyList() throws Exception {
        client.add_partitions(new ArrayList());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsDifferentTable() throws Exception {
        String tableName1 = (TestAddPartitions.TABLE_NAME) + "1";
        String tableName2 = (TestAddPartitions.TABLE_NAME) + "2";
        createTable(TestAddPartitions.DB_NAME, tableName1, null);
        createTable(TestAddPartitions.DB_NAME, tableName2, null);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, tableName1, "2017");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, tableName2, "2016");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, tableName1, "2018");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        client.add_partitions(partitions);
    }

    @Test
    public void testAddPartitionsDifferentDBs() throws Exception {
        createDB("parttestdb2");
        createTable();
        createTable("parttestdb2", TestAddPartitions.TABLE_NAME, null);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        Partition partition2 = buildPartition("parttestdb2", TestAddPartitions.TABLE_NAME, "2016");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2018");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        try {
            client.add_partitions(partitions);
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        client.dropDatabase("parttestdb2", true, true, true);
    }

    @Test
    public void testAddPartitionsDuplicateInTheList() throws Exception {
        createTable();
        List<Partition> partitions = buildPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList("2014", "2015", "2017", "2017", "2018", "2019"));
        try {
            client.add_partitions(partitions);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition partition : partitions) {
            Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partition.getSd().getLocation())));
        }
    }

    @Test
    public void testAddPartitionsWithSameNameInTheListCaseSensitive() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "this");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "next");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "THIS");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        client.add_partitions(partitions);
        List<String> parts = client.listPartitionNames(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertEquals(3, parts.size());
        Assert.assertTrue(parts.contains("year=this"));
        Assert.assertTrue(parts.contains("year=next"));
        Assert.assertTrue(parts.contains("year=THIS"));
    }

    @Test
    public void testAddPartitionsAlreadyExists() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016", (tableLocation + "/year=2016a"));
        client.add_partition(partition);
        List<Partition> partitions = buildPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList("2014", "2015", "2016", "2017", "2018"));
        try {
            client.add_partitions(partitions);
            Assert.fail("AlreadyExistsException should have happened.");
        } catch (AlreadyExistsException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(partition.getValues(), parts.get(0).getValues());
        for (Partition part : partitions) {
            Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
        }
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNonExistingTable() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, "nonexistingtable", "2017");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionsNonExistingDb() throws Exception {
        createTable();
        Partition partition1 = buildPartition("nonexistingdb", TestAddPartitions.TABLE_NAME, "2017");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullDb() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        partition2.setDbName(null);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsEmptyDb() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        Partition partition2 = buildPartition("", TestAddPartitions.TABLE_NAME, "2017");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullTable() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        partition2.setTableName(null);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsEmptyTable() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, "", "2017");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
    }

    @Test
    public void testAddPartitionsOneInvalid() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016", (tableLocation + "/year=2016"));
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017", (tableLocation + "/year=2017"));
        Partition partition3 = buildPartition(Lists.newArrayList("2015", "march"), TestAddPartitions.getYearAndMonthPartCols(), 1);
        partition3.getSd().setLocation((tableLocation + "/year=2015/month=march"));
        Partition partition4 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2018", (tableLocation + "/year=2018"));
        Partition partition5 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2019", (tableLocation + "/year=2019"));
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        partitions.add(partition4);
        partitions.add(partition5);
        try {
            client.add_partitions(partitions);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition part : partitions) {
            Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
        }
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.setSd(null);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition);
        client.add_partitions(partitions);
    }

    @Test
    public void testAddPartitionsNullColsInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setCols(null);
        client.add_partitions(Lists.newArrayList(partition));
        // TODO: Not sure that this is the correct behavior. It doesn't make sense to create the
        // partition without column info. This should be investigated later.
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(part);
        Assert.assertNull(part.getSd().getCols());
    }

    @Test
    public void testAddPartitionsEmptyColsInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setCols(new ArrayList());
        client.add_partitions(Lists.newArrayList(partition));
        // TODO: Not sure that this is the correct behavior. It doesn't make sense to create the
        // partition without column info. This should be investigated later.
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(part);
        Assert.assertTrue(part.getSd().getCols().isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullColTypeInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().getCols().get(0).setType(null);
        client.add_partitions(Lists.newArrayList(partition));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullColNameInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().getCols().get(0).setName(null);
        client.add_partitions(Lists.newArrayList(partition));
    }

    @Test
    public void testAddPartitionsInvalidColTypeInSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().getCols().get(0).setType("xyz");
        client.add_partitions(Lists.newArrayList(partition));
        // TODO: Not sure that this is the correct behavior. It doesn't make sense to create the
        // partition with column with invalid type. This should be investigated later.
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, Lists.newArrayList(TestAddPartitions.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(part);
        Assert.assertEquals("xyz", part.getSd().getCols().get(0).getType());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsEmptySerdeInfo() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        partition.getSd().setSerdeInfo(null);
        client.add_partitions(Lists.newArrayList(partition));
    }

    @Test
    public void testAddPartitionNullAndEmptyLocation() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, ((metaStore.getWarehouseRoot()) + "/addparttest2"));
        List<Partition> partitions = new ArrayList<>();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017", null);
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016", "");
        partitions.add(partition1);
        partitions.add(partition2);
        client.add_partitions(partitions);
        Partition part1 = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2017");
        Assert.assertEquals(((metaStore.getWarehouseRoot()) + "/addparttest2/year=2017"), part1.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part1.getSd().getLocation())));
        Partition part2 = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2016");
        Assert.assertEquals(((metaStore.getWarehouseRoot()) + "/addparttest2/year=2016"), part2.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part2.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionsNullLocationInTableToo() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, null);
        List<Partition> partitions = new ArrayList<>();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE, null);
        partitions.add(partition);
        client.add_partitions(partitions);
        Partition part = client.getPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "year=2017");
        Assert.assertEquals(((metaStore.getWarehouseRoot()) + "/test_partition_db.db/test_partition_table/year=2017"), part.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsForView() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, tableName, TestAddPartitions.DEFAULT_YEAR_VALUE);
        List<Partition> partitions = Lists.newArrayList(partition);
        client.add_partitions(partitions);
    }

    @Test
    public void testAddPartitionsForExternalTable() throws Exception {
        String tableName = "part_add_ext_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + tableName;
        createExternalTable(tableName, tableLocation);
        String location1 = tableLocation + "/addparttest2017";
        String location2 = tableLocation + "/addparttest2018";
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, tableName, "2017", location1);
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, tableName, "2018", location2);
        List<Partition> partitions = Lists.newArrayList(partition1, partition2);
        client.add_partitions(partitions);
        List<Partition> resultParts = client.getPartitionsByNames(TestAddPartitions.DB_NAME, tableName, Lists.newArrayList("year=2017", "year=2018"));
        Assert.assertNotNull(resultParts);
        Assert.assertEquals(2, resultParts.size());
        if (resultParts.get(0).getValues().get(0).equals("2017")) {
            Assert.assertEquals(location1, resultParts.get(0).getSd().getLocation());
            Assert.assertEquals(location2, resultParts.get(1).getSd().getLocation());
        } else {
            Assert.assertEquals(location2, resultParts.get(0).getSd().getLocation());
            Assert.assertEquals(location1, resultParts.get(1).getSd().getLocation());
        }
    }

    @Test
    public void testAddPartitionsForExternalTableNullLocation() throws Exception {
        String tableName = "part_add_ext_table";
        createExternalTable(tableName, null);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, tableName, "2017", null);
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, tableName, "2018", null);
        List<Partition> partitions = Lists.newArrayList(partition1, partition2);
        client.add_partitions(partitions);
        List<Partition> resultParts = client.getPartitionsByNames(TestAddPartitions.DB_NAME, tableName, Lists.newArrayList("year=2017", "year=2018"));
        Assert.assertNotNull(resultParts);
        Assert.assertEquals(2, resultParts.size());
        String defaultTableLocation = ((((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.DB_NAME)) + ".db/") + tableName;
        String defaultPartLocation1 = defaultTableLocation + "/year=2017";
        String defaultPartLocation2 = defaultTableLocation + "/year=2018";
        if (resultParts.get(0).getValues().get(0).equals("2017")) {
            Assert.assertEquals(defaultPartLocation1, resultParts.get(0).getSd().getLocation());
            Assert.assertEquals(defaultPartLocation2, resultParts.get(1).getSd().getLocation());
        } else {
            Assert.assertEquals(defaultPartLocation2, resultParts.get(0).getSd().getLocation());
            Assert.assertEquals(defaultPartLocation1, resultParts.get(1).getSd().getLocation());
        }
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNoValueInPartition() throws Exception {
        createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitions.DB_NAME).setTableName(TestAddPartitions.TABLE_NAME).addCol(TestAddPartitions.YEAR_COL_NAME, TestAddPartitions.DEFAULT_COL_TYPE).setLocation(((metaStore.getWarehouseRoot()) + "/addparttest")).build(metaStore.getConf());
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsMorePartColInTable() throws Exception {
        createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getYearAndMonthPartCols(), null);
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.DEFAULT_YEAR_VALUE);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullPartition() throws Exception {
        List<Partition> partitions = new ArrayList<>();
        partitions.add(null);
        client.add_partitions(partitions);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionsNullValues() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, null);
        partition.setValues(null);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition);
        client.add_partitions(partitions);
    }

    @Test
    public void testAddPartitionsEmptyValue() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition);
        client.add_partitions(partitions);
        List<String> partitionNames = client.listPartitionNames(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertNotNull(partitionNames);
        Assert.assertTrue(((partitionNames.size()) == 1));
        Assert.assertEquals("year=__HIVE_DEFAULT_PARTITION__", partitionNames.get(0));
    }

    @Test
    public void testAddPartitionsInvalidLocation() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME);
        Map<String, String> valuesAndLocations = new HashMap<>();
        valuesAndLocations.put("2014", (tableLocation + "/year=2014"));
        valuesAndLocations.put("2015", (tableLocation + "/year=2015"));
        valuesAndLocations.put("2016", "invalidhost:80000/wrongfolder");
        valuesAndLocations.put("2017", (tableLocation + "/year=2017"));
        valuesAndLocations.put("2018", (tableLocation + "/year=2018"));
        List<Partition> partitions = buildPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, valuesAndLocations);
        try {
            client.add_partitions(partitions);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
        }
        List<Partition> parts = client.listPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition partition : partitions) {
            if (!("invalidhost:80000/wrongfolder".equals(partition.getSd().getLocation()))) {
                Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partition.getSd().getLocation())));
            }
        }
    }

    @Test
    public void testAddPartitionsMoreThanThreadCountsOneFails() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME);
        List<Partition> partitions = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String value = String.valueOf((2000 + i));
            String location = (tableLocation + "/year=") + value;
            if (i == 30) {
                location = "invalidhost:80000/wrongfolder";
            }
            Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, value, location);
            partitions.add(partition);
        }
        try {
            client.add_partitions(partitions);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
        }
        List<Partition> parts = client.listPartitions(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition partition : partitions) {
            if (!("invalidhost:80000/wrongfolder".equals(partition.getSd().getLocation()))) {
                Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partition.getSd().getLocation())));
            }
        }
    }

    // Tests for List<Partition> add_partitions(List<Partition> partitions,
    // boolean ifNotExists, boolean needResults) method
    @Test
    public void testAddParts() throws Exception {
        Table table = createTable();
        List<Partition> partitions = new ArrayList<>();
        Partition partition1 = buildPartition(Lists.newArrayList("2017"), TestAddPartitions.getYearPartCol(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("2016"), TestAddPartitions.getYearPartCol(), 2);
        Partition partition3 = buildPartition(Lists.newArrayList("2015"), TestAddPartitions.getYearPartCol(), 3);
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        List<Partition> addedPartitions = client.add_partitions(partitions, false, false);
        Assert.assertNull(addedPartitions);
        verifyPartition(table, "year=2017", Lists.newArrayList("2017"), 1);
        verifyPartition(table, "year=2016", Lists.newArrayList("2016"), 2);
        verifyPartition(table, "year=2015", Lists.newArrayList("2015"), 3);
    }

    @Test
    public void testAddPartsMultipleValues() throws Exception {
        Table table = createTable(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.getYearAndMonthPartCols(), (((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitions.TABLE_NAME)));
        Partition partition1 = buildPartition(Lists.newArrayList("2017", "march"), TestAddPartitions.getYearAndMonthPartCols(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("2017", "june"), TestAddPartitions.getYearAndMonthPartCols(), 2);
        Partition partition3 = buildPartition(Lists.newArrayList("2016", "march"), TestAddPartitions.getYearAndMonthPartCols(), 3);
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        List<Partition> addedPartitions = client.add_partitions(partitions, false, true);
        Assert.assertNotNull(addedPartitions);
        Assert.assertEquals(3, addedPartitions.size());
        verifyPartition(table, "year=2017/month=march", Lists.newArrayList("2017", "march"), 1);
        verifyPartition(table, "year=2017/month=june", Lists.newArrayList("2017", "june"), 2);
        verifyPartition(table, "year=2016/month=march", Lists.newArrayList("2016", "march"), 3);
    }

    @Test(expected = MetaException.class)
    public void testAddPartsNullList() throws Exception {
        client.add_partitions(null, false, false);
    }

    @Test
    public void testAddPartsEmptyList() throws Exception {
        List<Partition> addedPartitions = client.add_partitions(new ArrayList(), false, true);
        Assert.assertNotNull(addedPartitions);
        Assert.assertTrue(addedPartitions.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testAddPartsDifferentTable() throws Exception {
        String tableName1 = (TestAddPartitions.TABLE_NAME) + "1";
        String tableName2 = (TestAddPartitions.TABLE_NAME) + "2";
        createTable(TestAddPartitions.DB_NAME, tableName1, null);
        createTable(TestAddPartitions.DB_NAME, tableName2, null);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, tableName1, "2017");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, tableName2, "2016");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, tableName1, "2018");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        client.add_partitions(partitions, false, false);
    }

    @Test
    public void testAddPartsDifferentDBs() throws Exception {
        createDB("parttestdb2");
        createTable();
        createTable("parttestdb2", TestAddPartitions.TABLE_NAME, null);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        Partition partition2 = buildPartition("parttestdb2", TestAddPartitions.TABLE_NAME, "2016");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2018");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        try {
            client.add_partitions(partitions, false, false);
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        }
        client.dropDatabase("parttestdb2", true, true, true);
    }

    @Test(expected = MetaException.class)
    public void testAddPartsDuplicateInTheList() throws Exception {
        createTable();
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        client.add_partitions(partitions, true, false);
    }

    @Test(expected = AlreadyExistsException.class)
    public void testAddPartsAlreadyExists() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        client.add_partition(partition);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2015");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        client.add_partitions(partitions, false, false);
    }

    @Test
    public void testAddPartsAlreadyExistsIfExistsTrue() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        client.add_partition(partition);
        Partition partition1 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2015");
        Partition partition2 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2017");
        Partition partition3 = buildPartition(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, "2016");
        List<Partition> partitions = new ArrayList<>();
        partitions.add(partition1);
        partitions.add(partition2);
        partitions.add(partition3);
        List<Partition> addedPartitions = client.add_partitions(partitions, true, true);
        Assert.assertEquals(2, addedPartitions.size());
        List<String> partitionNames = client.listPartitionNames(TestAddPartitions.DB_NAME, TestAddPartitions.TABLE_NAME, TestAddPartitions.MAX);
        Assert.assertEquals(3, partitionNames.size());
        Assert.assertTrue(partitionNames.contains("year=2015"));
        Assert.assertTrue(partitionNames.contains("year=2016"));
        Assert.assertTrue(partitionNames.contains("year=2017"));
    }

    @Test(expected = MetaException.class)
    public void testAddPartsNullPartition() throws Exception {
        List<Partition> partitions = new ArrayList<>();
        partitions.add(null);
        client.add_partitions(partitions, false, false);
    }
}

