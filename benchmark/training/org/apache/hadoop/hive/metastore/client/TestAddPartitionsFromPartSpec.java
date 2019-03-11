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


import PartitionSpecProxy.Factory;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionListComposingSpec;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.PartitionSpecWithSharedSD;
import org.apache.hadoop.hive.metastore.api.PartitionWithoutSD;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.PartitionBuilder;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for creating partitions from partition spec.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestAddPartitionsFromPartSpec extends MetaStoreClientTest {
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

    private static final int DEFAULT_CREATE_TIME = 123456;

    private static final short MAX = -1;

    public TestAddPartitionsFromPartSpec(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    // Tests for int add_partitions_pspec(PartitionSpecProxy partitionSpec) method
    @Test
    public void testAddPartitionSpec() throws Exception {
        Table table = createTable();
        Partition partition1 = buildPartition(Lists.newArrayList("2013"), TestAddPartitionsFromPartSpec.getYearPartCol(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("2014"), TestAddPartitionsFromPartSpec.getYearPartCol(), 2);
        Partition partition3 = buildPartition(Lists.newArrayList("2012"), TestAddPartitionsFromPartSpec.getYearPartCol(), 3);
        List<Partition> partitions = Lists.newArrayList(partition1, partition2, partition3);
        String rootPath = (table.getSd().getLocation()) + "/addpartspectest/";
        PartitionSpecProxy partitionSpec = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, rootPath, partitions);
        client.add_partitions_pspec(partitionSpec);
        verifyPartition(table, "year=2013", Lists.newArrayList("2013"), 1);
        verifyPartition(table, "year=2014", Lists.newArrayList("2014"), 2);
        verifyPartition(table, "year=2012", Lists.newArrayList("2012"), 3);
    }

    @Test
    public void testAddPartitionSpecWithSharedSD() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition1 = buildPartitionWithoutSD(Lists.newArrayList("2013"), 1);
        PartitionWithoutSD partition2 = buildPartitionWithoutSD(Lists.newArrayList("2014"), 2);
        PartitionWithoutSD partition3 = buildPartitionWithoutSD(Lists.newArrayList("2012"), 3);
        List<PartitionWithoutSD> partitions = Lists.newArrayList(partition1, partition2, partition3);
        String location = (table.getSd().getLocation()) + "/sharedSDTest/";
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(partitions, buildSD(location));
        client.add_partitions_pspec(partitionSpecProxy);
        verifyPartitionSharedSD(table, "year=2013", Lists.newArrayList("2013"), 1);
        verifyPartitionSharedSD(table, "year=2014", Lists.newArrayList("2014"), 2);
        verifyPartitionSharedSD(table, "year=2012", Lists.newArrayList("2012"), 3);
    }

    @Test
    public void testAddPartitionSpecWithSharedSDUpperCaseDBAndTableName() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2013"), 1);
        List<PartitionWithoutSD> partitions = Lists.newArrayList(partition);
        String location = (table.getSd().getLocation()) + "/sharedSDTest/";
        PartitionSpec partitionSpec = new PartitionSpec();
        partitionSpec.setDbName(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase());
        partitionSpec.setTableName(TestAddPartitionsFromPartSpec.TABLE_NAME.toUpperCase());
        PartitionSpecWithSharedSD partitionList = new PartitionSpecWithSharedSD();
        partitionList.setPartitions(partitions);
        partitionList.setSd(buildSD(location));
        partitionSpec.setSharedSDPartitionSpec(partitionList);
        PartitionSpecProxy partitionSpecProxy = Factory.get(partitionSpec);
        client.add_partitions_pspec(partitionSpecProxy);
        Partition part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "year=2013");
        Assert.assertNotNull(part);
        Assert.assertEquals(TestAddPartitionsFromPartSpec.DB_NAME, part.getDbName());
        Assert.assertEquals(TestAddPartitionsFromPartSpec.TABLE_NAME, part.getTableName());
        Assert.assertEquals(((((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME)) + "/sharedSDTest/partwithoutsd1"), part.getSd().getLocation());
    }

    @Test
    public void testAddPartitionSpecsMultipleValues() throws Exception {
        Table table = createTable(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.getYearAndMonthPartCols(), (((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME)));
        Partition partition1 = buildPartition(Lists.newArrayList("2002", "march"), TestAddPartitionsFromPartSpec.getYearAndMonthPartCols(), 1);
        Partition partition2 = buildPartition(Lists.newArrayList("2003", "april"), TestAddPartitionsFromPartSpec.getYearAndMonthPartCols(), 2);
        PartitionWithoutSD partition3 = buildPartitionWithoutSD(Lists.newArrayList("2004", "june"), 3);
        PartitionWithoutSD partition4 = buildPartitionWithoutSD(Lists.newArrayList("2005", "may"), 4);
        List<Partition> partitions = Lists.newArrayList(partition1, partition2);
        List<PartitionWithoutSD> partitionsWithoutSD = Lists.newArrayList(partition3, partition4);
        PartitionSpecProxy partitionSpec = buildPartitionSpec(partitions, partitionsWithoutSD);
        client.add_partitions_pspec(partitionSpec);
        verifyPartition(table, "year=2002/month=march", Lists.newArrayList("2002", "march"), 1);
        verifyPartition(table, "year=2003/month=april", Lists.newArrayList("2003", "april"), 2);
        verifyPartitionSharedSD(table, "year=2004/month=june", Lists.newArrayList("2004", "june"), 3);
        verifyPartitionSharedSD(table, "year=2005/month=may", Lists.newArrayList("2005", "may"), 4);
    }

    @Test
    public void testAddPartitionSpecUpperCaseDBAndTableName() throws Exception {
        // Create table 'test_partition_db.test_add_part_table'
        String tableName = "test_add_part_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (tableName.toUpperCase());
        createTable(TestAddPartitionsFromPartSpec.DB_NAME, tableName, TestAddPartitionsFromPartSpec.getYearPartCol(), tableLocation);
        // Create partitions with table name 'TEST_ADD_PART_TABLE' and db name 'TEST_PARTITION_DB'
        Partition partition1 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2013", (tableLocation + "/year=2013"));
        Partition partition2 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2014", (tableLocation + "/year=2014"));
        List<Partition> partitions = Lists.newArrayList(partition1, partition2);
        String rootPath = tableLocation + "/addpartspectest/";
        PartitionSpecProxy partitionSpec = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase(), tableName.toUpperCase(), rootPath, partitions);
        client.add_partitions_pspec(partitionSpec);
        // Validate the partition attributes
        // The db and table name should be all lower case: 'test_partition_db' and
        // 'test_add_part_table'
        // The location should be saved case-sensitive, it should be
        // warehouse dir + "TEST_ADD_PART_TABLE/year=2017"
        Partition part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "year=2013");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitionsFromPartSpec.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2013"), part.getSd().getLocation());
        Partition part1 = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase(), tableName.toUpperCase(), "year=2013");
        Assert.assertEquals(part, part1);
        part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "year=2014");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitionsFromPartSpec.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2014"), part.getSd().getLocation());
        part1 = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase(), tableName.toUpperCase(), "year=2014");
        Assert.assertEquals(part, part1);
    }

    @Test
    public void testAddPartitionSpecUpperCaseDBAndTableNameInOnePart() throws Exception {
        String tableName = "test_add_part_table";
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (tableName.toUpperCase());
        createTable(TestAddPartitionsFromPartSpec.DB_NAME, tableName, TestAddPartitionsFromPartSpec.getYearPartCol(), tableLocation);
        Partition partition1 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "2013", (tableLocation + "/year=2013"));
        Partition partition2 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME.toUpperCase(), tableName.toUpperCase(), "2014", (tableLocation + "/year=2014"));
        List<Partition> partitions = Lists.newArrayList(partition1, partition2);
        String rootPath = tableLocation + "/addpartspectest/";
        PartitionSpecProxy partitionSpec = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, tableName, rootPath, partitions);
        client.add_partitions_pspec(partitionSpec);
        Partition part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "year=2013");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitionsFromPartSpec.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2013"), part.getSd().getLocation());
        part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "year=2014");
        Assert.assertNotNull(part);
        Assert.assertEquals(tableName, part.getTableName());
        Assert.assertEquals(TestAddPartitionsFromPartSpec.DB_NAME, part.getDbName());
        Assert.assertEquals((tableLocation + "/year=2014"), part.getSd().getLocation());
    }

    // TODO add tests for partitions in other catalogs
    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNullSpec() throws Exception {
        client.add_partitions_pspec(null);
    }

    @Test
    public void testAddPartitionSpecEmptyPartList() throws Exception {
        createTable();
        List<Partition> partitions = new ArrayList<>();
        PartitionSpecProxy partitionSpec = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        client.add_partitions_pspec(partitionSpec);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNullPartList() throws Exception {
        createTable();
        List<Partition> partitions = null;
        PartitionSpecProxy partitionSpec = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        client.add_partitions_pspec(partitionSpec);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNoDB() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(null, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNoTable() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, null, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNoDBAndTableInPartition() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        partition.setDbName(null);
        partition.setTableName(null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecDBAndTableSetFromSpecProxy() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        partition.setDbName(null);
        partition.setTableName(null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(null, null, null, Lists.newArrayList(partition));
        partitionSpecProxy.setDbName(TestAddPartitionsFromPartSpec.DB_NAME);
        partitionSpecProxy.setTableName(TestAddPartitionsFromPartSpec.TABLE_NAME);
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList(TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(resultPart);
    }

    @Test
    public void testAddPartitionSpecWithSharedSDDBAndTableSetFromSpecProxy() throws Exception {
        createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList(TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE), 1);
        String location = (((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME)) + "/sharedSDTest/";
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(location));
        partitionSpecProxy.setDbName(TestAddPartitionsFromPartSpec.DB_NAME);
        partitionSpecProxy.setTableName(TestAddPartitionsFromPartSpec.TABLE_NAME);
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList(TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE));
        Assert.assertNotNull(resultPart);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionSpecEmptyDB() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec("", TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionSpecEmptyTable() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, "", null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionSpecNonExistingDB() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec("nonexistingdb", TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = InvalidObjectException.class)
    public void testAddPartitionSpecNonExistingTable() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, "nonexistingtable", null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecDiffDBName() throws Exception {
        createDB("NewPartDB");
        createTable();
        createTable("NewPartDB", "NewPartTable", TestAddPartitionsFromPartSpec.getYearPartCol(), null);
        List<Partition> partitions = new ArrayList<>();
        Partition partition1 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        Partition partition2 = buildPartition("NewPartDB", "NewPartTable", TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        partitions.add(partition1);
        partitions.add(partition2);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        try {
            client.add_partitions_pspec(partitionSpecProxy);
            Assert.fail("MetaException should have been thrown.");
        } catch (MetaException e) {
            // Expected exception
        } finally {
            client.dropDatabase("NewPartDB", true, true, true);
        }
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNullPart() throws Exception {
        createTable();
        List<Partition> partitions = new ArrayList<>();
        Partition partition1 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        Partition partition2 = null;
        partitions.add(partition1);
        partitions.add(partition2);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecUnsupportedPartSpecType() throws Exception {
        createTable();
        PartitionSpec partitionSpec = new PartitionSpec();
        partitionSpec.setDbName(TestAddPartitionsFromPartSpec.DB_NAME);
        partitionSpec.setTableName(TestAddPartitionsFromPartSpec.TABLE_NAME);
        partitionSpec.setPartitionList(null);
        partitionSpec.setSharedSDPartitionSpec(null);
        try {
            PartitionSpecProxy bubu = Factory.get(partitionSpec);
            client.add_partitions_pspec(bubu);
            Assert.fail("AssertionError should have been thrown.");
        } catch (AssertionError e) {
            // Expected error
        }
    }

    @Test
    public void testAddPartitionSpecBothTypeSet() throws Exception {
        Table table = createTable();
        Partition partition = buildPartition(Lists.newArrayList("2013"), TestAddPartitionsFromPartSpec.getYearPartCol(), 1);
        PartitionWithoutSD partitionWithoutSD = buildPartitionWithoutSD(Lists.newArrayList("2014"), 0);
        PartitionSpec partitionSpec = new PartitionSpec();
        partitionSpec.setDbName(TestAddPartitionsFromPartSpec.DB_NAME);
        partitionSpec.setTableName(TestAddPartitionsFromPartSpec.TABLE_NAME);
        PartitionListComposingSpec partitionListComposingSpec = new PartitionListComposingSpec();
        partitionListComposingSpec.setPartitions(Lists.newArrayList(partition));
        partitionSpec.setPartitionList(partitionListComposingSpec);
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = new PartitionSpecWithSharedSD();
        partitionSpecWithSharedSD.setPartitions(Lists.newArrayList(partitionWithoutSD));
        partitionSpecWithSharedSD.setSd(buildSD(((table.getSd().getLocation()) + "/sharedSDTest/")));
        partitionSpec.setSharedSDPartitionSpec(partitionSpecWithSharedSD);
        PartitionSpecProxy partitionSpecProxy = Factory.get(partitionSpec);
        client.add_partitions_pspec(partitionSpecProxy);
        List<String> partitionNames = client.listPartitionNames(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(partitionNames);
        Assert.assertTrue(((partitionNames.size()) == 1));
        Assert.assertEquals("year=2013", partitionNames.get(0));
    }

    @Test
    public void testAddPartitionSpecSetRootPath() throws Exception {
        Table table = createTable();
        String rootPath = (table.getSd().getLocation()) + "/addPartSpecRootPath/";
        String rootPath1 = (table.getSd().getLocation()) + "/someotherpath/";
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2007", (rootPath + "part2007/"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, rootPath1, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2007"));
        Assert.assertEquals((rootPath + "part2007"), resultPart.getSd().getLocation());
    }

    @Test
    public void testAddPartitionSpecChangeRootPath() throws Exception {
        Table table = createTable();
        String rootPath = (table.getSd().getLocation()) + "/addPartSpecRootPath/";
        String rootPath1 = (table.getSd().getLocation()) + "/someotherpath/";
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2007", (rootPath + "part2007/"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, rootPath, Lists.newArrayList(partition));
        partitionSpecProxy.setRootLocation(rootPath1);
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2007"));
        Assert.assertEquals((rootPath1 + "part2007"), resultPart.getSd().getLocation());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecChangeRootPathFromNull() throws Exception {
        Table table = createTable();
        String rootPath = (table.getSd().getLocation()) + "/addPartSpecRootPath/";
        String rootPath1 = (table.getSd().getLocation()) + "/someotherpath/";
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2007", (rootPath + "part2007/"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        partitionSpecProxy.setRootLocation(rootPath1);
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecChangeRootPathToNull() throws Exception {
        Table table = createTable();
        String rootPath = (table.getSd().getLocation()) + "/addPartSpecRootPath/";
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2007", (rootPath + "part2007/"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, rootPath, Lists.newArrayList(partition));
        partitionSpecProxy.setRootLocation(null);
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecChangeRootPathDiffInSd() throws Exception {
        Table table = createTable();
        String rootPath = (table.getSd().getLocation()) + "/addPartSpecRootPath/";
        String rootPath1 = (table.getSd().getLocation()) + "/addPartSdPath/";
        String rootPath2 = (table.getSd().getLocation()) + "/someotherpath/";
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2007", (rootPath1 + "part2007/"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, rootPath, Lists.newArrayList(partition));
        partitionSpecProxy.setRootLocation(rootPath2);
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecWithSharedSDChangeRootPath() throws Exception {
        Table table = createTable();
        String rootPath = (table.getSd().getLocation()) + "/addPartSpecRootPath/";
        String rootPath1 = (table.getSd().getLocation()) + "/someotherpath/";
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2014"), 0);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(rootPath));
        partitionSpecProxy.setRootLocation(rootPath1);
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2014"));
        Assert.assertEquals((rootPath1 + "partwithoutsd0"), resultPart.getSd().getLocation());
    }

    @Test
    public void testAddPartitionSpecWithSharedSDWithoutRelativePath() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2014"), 0);
        partition.setRelativePath(null);
        String location = (table.getSd().getLocation()) + "/sharedSDTest/";
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(location));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "year=2014");
        Assert.assertNotNull(part);
        Assert.assertEquals(((table.getSd().getLocation()) + "/sharedSDTest/null"), part.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionSpecPartAlreadyExists() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME);
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2016", (tableLocation + "/year=2016a"));
        client.add_partition(partition);
        List<Partition> partitions = buildPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2014", "2015", "2016", "2017", "2018"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        try {
            client.add_partitions_pspec(partitionSpecProxy);
            Assert.fail("AlreadyExistsException should have happened.");
        } catch (AlreadyExistsException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(parts);
        Assert.assertEquals(1, parts.size());
        Assert.assertEquals(partition.getValues(), parts.get(0).getValues());
        for (Partition part : partitions) {
            Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
        }
    }

    @Test
    public void testAddPartitionSpecPartDuplicateInSpec() throws Exception {
        createTable();
        List<Partition> partitions = buildPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2014", "2015", "2017", "2017", "2018", "2019"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        try {
            client.add_partitions_pspec(partitionSpecProxy);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition partition : partitions) {
            Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partition.getSd().getLocation())));
        }
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecPartDuplicateInSpecs() throws Exception {
        createTable(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.getYearPartCol(), (((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME)));
        Partition partition = buildPartition(Lists.newArrayList("2002"), TestAddPartitionsFromPartSpec.getYearPartCol(), 1);
        PartitionWithoutSD partitionWithoutSD = buildPartitionWithoutSD(Lists.newArrayList("2002"), 0);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(Lists.newArrayList(partition), Lists.newArrayList(partitionWithoutSD));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNullSd() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        partition.setSd(null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecWithSharedSDNullSd() throws Exception {
        createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2002"), 0);
        StorageDescriptor sd = null;
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), sd);
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecWithSharedSDNullLocation() throws Exception {
        createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2002"), 0);
        partition.setRelativePath("year2002");
        String location = null;
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(location));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecWithSharedSDEmptyLocation() throws Exception {
        createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2002"), 0);
        partition.setRelativePath("year2002");
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(""));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecWithSharedSDInvalidSD() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition = buildPartitionWithoutSD(Lists.newArrayList("2002"), 0);
        partition.setRelativePath("year2002");
        StorageDescriptor sd = new StorageDescriptor();
        sd.setLocation(((table.getSd().getLocation()) + "/nullLocationTest/"));
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), sd);
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecNullLocation() throws Exception {
        Table table = createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE, null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList(TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE));
        Assert.assertEquals(((table.getSd().getLocation()) + "/year=2017"), resultPart.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(resultPart.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionSpecEmptyLocation() throws Exception {
        Table table = createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE, "");
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList(TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE));
        Assert.assertEquals(((table.getSd().getLocation()) + "/year=2017"), resultPart.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(resultPart.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionSpecEmptyLocationInTableToo() throws Exception {
        Table table = createTable(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.getYearPartCol(), null);
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE, "");
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList(TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE));
        Assert.assertEquals(((table.getSd().getLocation()) + "/year=2017"), resultPart.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(resultPart.getSd().getLocation())));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecForView() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, tableName, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecForViewNullPartLocation() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        partition.getSd().setLocation(null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, tableName, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "year=2017");
        Assert.assertNull(part.getSd().getLocation());
    }

    @Test
    public void testAddPartitionsForViewNullPartSd() throws Exception {
        String tableName = "test_add_partition_view";
        createView(tableName);
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, TestAddPartitionsFromPartSpec.DEFAULT_YEAR_VALUE);
        partition.setSd(null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, tableName, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition part = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, tableName, "year=2017");
        Assert.assertNull(part.getSd());
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecWithSharedSDNoValue() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition = new PartitionWithoutSD();
        partition.setRelativePath("addpartspectest");
        String location = (table.getSd().getLocation()) + "/nullValueTest/";
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(location));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNoValue() throws Exception {
        createTable();
        Partition partition = new PartitionBuilder().setDbName(TestAddPartitionsFromPartSpec.DB_NAME).setTableName(TestAddPartitionsFromPartSpec.TABLE_NAME).addCol(TestAddPartitionsFromPartSpec.YEAR_COL_NAME, TestAddPartitionsFromPartSpec.DEFAULT_COL_TYPE).setLocation(((metaStore.getWarehouseRoot()) + "/addpartspectest")).build(metaStore.getConf());
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecNullValues() throws Exception {
        createTable();
        Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null);
        partition.setValues(null);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecWithSharedSDEmptyValue() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition = new PartitionWithoutSD();
        partition.setRelativePath("addpartspectest");
        partition.setValues(Lists.newArrayList(""));
        String location = (table.getSd().getLocation()) + "/nullValueTest/";
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition), buildSD(location));
        client.add_partitions_pspec(partitionSpecProxy);
        List<String> partitionNames = client.listPartitionNames(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(partitionNames);
        Assert.assertTrue(((partitionNames.size()) == 1));
        Assert.assertEquals("year=__HIVE_DEFAULT_PARTITION__", partitionNames.get(0));
    }

    @Test(expected = MetaException.class)
    public void testAddPartitionSpecMoreValues() throws Exception {
        createTable();
        Partition partition = buildPartition(Lists.newArrayList("2017", "march"), TestAddPartitionsFromPartSpec.getYearAndMonthPartCols(), 1);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, Lists.newArrayList(partition));
        client.add_partitions_pspec(partitionSpecProxy);
    }

    @Test
    public void testAddPartitionSpecWithSharedSDNoRelativePath() throws Exception {
        Table table = createTable();
        PartitionWithoutSD partition1 = buildPartitionWithoutSD(Lists.newArrayList("2007"), 0);
        PartitionWithoutSD partition2 = buildPartitionWithoutSD(Lists.newArrayList("2008"), 0);
        partition1.setRelativePath(null);
        partition2.setRelativePath(null);
        String location = (table.getSd().getLocation()) + "/noRelativePath/";
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpecWithSharedSD(Lists.newArrayList(partition1, partition2), buildSD(location));
        client.add_partitions_pspec(partitionSpecProxy);
        Partition resultPart1 = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2007"));
        Assert.assertEquals((location + "null"), resultPart1.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(resultPart1.getSd().getLocation())));
        Partition resultPart2 = client.getPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, Lists.newArrayList("2008"));
        Assert.assertEquals((location + "null"), resultPart2.getSd().getLocation());
        Assert.assertTrue(metaStore.isPathExists(new org.apache.hadoop.fs.Path(resultPart2.getSd().getLocation())));
    }

    @Test
    public void testAddPartitionSpecOneInvalid() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME);
        Partition partition1 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2016", (tableLocation + "/year=2016"));
        Partition partition2 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2017", (tableLocation + "/year=2017"));
        Partition partition3 = buildPartition(Lists.newArrayList("2015", "march"), TestAddPartitionsFromPartSpec.getYearAndMonthPartCols(), 1);
        partition3.getSd().setLocation((tableLocation + "/year=2015/month=march"));
        Partition partition4 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2018", (tableLocation + "/year=2018"));
        Partition partition5 = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, "2019", (tableLocation + "/year=2019"));
        List<Partition> partitions = Lists.newArrayList(partition1, partition2, partition3, partition4, partition5);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        try {
            client.add_partitions_pspec(partitionSpecProxy);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition part : partitions) {
            Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(part.getSd().getLocation())));
        }
    }

    @Test
    public void testAddPartitionSpecInvalidLocation() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME);
        Map<String, String> valuesAndLocations = new HashMap<>();
        valuesAndLocations.put("2014", (tableLocation + "/year=2014"));
        valuesAndLocations.put("2015", (tableLocation + "/year=2015"));
        valuesAndLocations.put("2016", "invalidhost:80000/wrongfolder");
        valuesAndLocations.put("2017", (tableLocation + "/year=2017"));
        valuesAndLocations.put("2018", (tableLocation + "/year=2018"));
        List<Partition> partitions = buildPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, valuesAndLocations);
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        try {
            client.add_partitions_pspec(partitionSpecProxy);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition partition : partitions) {
            if (!("invalidhost:80000/wrongfolder".equals(partition.getSd().getLocation()))) {
                Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partition.getSd().getLocation())));
            }
        }
    }

    @Test
    public void testAddPartitionSpecMoreThanThreadCountsOneFails() throws Exception {
        createTable();
        String tableLocation = ((metaStore.getWarehouseRoot()) + "/") + (TestAddPartitionsFromPartSpec.TABLE_NAME);
        List<Partition> partitions = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String value = String.valueOf((2000 + i));
            String location = (tableLocation + "/year=") + value;
            if (i == 30) {
                location = "invalidhost:80000/wrongfolder";
            }
            Partition partition = buildPartition(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, value, location);
            partitions.add(partition);
        }
        PartitionSpecProxy partitionSpecProxy = buildPartitionSpec(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, null, partitions);
        try {
            client.add_partitions_pspec(partitionSpecProxy);
            Assert.fail("MetaException should have happened.");
        } catch (MetaException e) {
            // Expected exception
        }
        List<Partition> parts = client.listPartitions(TestAddPartitionsFromPartSpec.DB_NAME, TestAddPartitionsFromPartSpec.TABLE_NAME, TestAddPartitionsFromPartSpec.MAX);
        Assert.assertNotNull(parts);
        Assert.assertTrue(parts.isEmpty());
        for (Partition partition : partitions) {
            if (!("invalidhost:80000/wrongfolder".equals(partition.getSd().getLocation()))) {
                Assert.assertFalse(metaStore.isPathExists(new org.apache.hadoop.fs.Path(partition.getSd().getLocation())));
            }
        }
    }
}

