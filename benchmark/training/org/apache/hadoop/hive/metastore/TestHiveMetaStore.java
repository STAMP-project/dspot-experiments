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


import ColumnType.DATE_TYPE_NAME;
import ColumnType.INT_TYPE_NAME;
import ColumnType.SERIALIZATION_FORMAT;
import ColumnType.STRING_TYPE_NAME;
import ConfVars.CLIENT_SOCKET_LIFETIME;
import ConfVars.STATS_AUTO_GATHER;
import ConfVars.WAREHOUSE;
import HiveMetaStore.HMSHandler;
import HiveMetaStore.PUBLIC;
import PrincipalType.ROLE;
import PrincipalType.USER;
import StatsSetupConst.DO_NOT_UPDATE_STATS;
import TableType.VIRTUAL_VIEW;
import Warehouse.DEFAULT_CATALOG_NAME;
import Warehouse.DEFAULT_DATABASE_NAME;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsData;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.ConfigValSecurityException;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.DoubleColumnStatsData;
import org.apache.hadoop.hive.metastore.api.EnvironmentContext;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.FunctionType;
import org.apache.hadoop.hive.metastore.api.GetAllFunctionsResponse;
import org.apache.hadoop.hive.metastore.api.GetPartitionsFilterSpec;
import org.apache.hadoop.hive.metastore.api.GetPartitionsProjectionSpec;
import org.apache.hadoop.hive.metastore.api.GetPartitionsRequest;
import org.apache.hadoop.hive.metastore.api.GetPartitionsResponse;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionSpecWithSharedSD;
import org.apache.hadoop.hive.metastore.api.PartitionWithoutSD;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.ResourceType;
import org.apache.hadoop.hive.metastore.api.ResourceUri;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.StringColumnStatsData;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.api.hive_metastoreConstants;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.hadoop.hive.metastore.utils.SecurityUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ColumnType.INT_TYPE_NAME;
import static ColumnType.STRING_TYPE_NAME;


public abstract class TestHiveMetaStore {
    private static final Logger LOG = LoggerFactory.getLogger(TestHiveMetaStore.class);

    protected static HiveMetaStoreClient client;

    protected static Configuration conf = MetastoreConf.newMetastoreConf();

    protected static Warehouse warehouse;

    protected static boolean isThriftClient = false;

    private static final String TEST_DB1_NAME = "testdb1";

    private static final String TEST_DB2_NAME = "testdb2";

    private static final int DEFAULT_LIMIT_PARTITION_REQUEST = 100;

    @Test
    public void testNameMethods() {
        Map<String, String> spec = new LinkedHashMap<>();
        spec.put("ds", "2008-07-01 14:13:12");
        spec.put("hr", "14");
        List<String> vals = new ArrayList<>();
        vals.addAll(spec.values());
        String partName = "ds=2008-07-01 14%3A13%3A12/hr=14";
        try {
            List<String> testVals = TestHiveMetaStore.client.partitionNameToVals(partName);
            Assert.assertTrue("Values from name are incorrect", vals.equals(testVals));
            Map<String, String> testSpec = TestHiveMetaStore.client.partitionNameToSpec(partName);
            Assert.assertTrue("Spec from name is incorrect", spec.equals(testSpec));
            List<String> emptyVals = TestHiveMetaStore.client.partitionNameToVals("");
            Assert.assertTrue("Values should be empty", ((emptyVals.size()) == 0));
            Map<String, String> emptySpec = TestHiveMetaStore.client.partitionNameToSpec("");
            Assert.assertTrue("Spec should be empty", ((emptySpec.size()) == 0));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * tests create table and partition and tries to drop the table without
     * droppping the partition
     */
    @Test
    public void testPartition() throws Exception {
        TestHiveMetaStore.partitionTester(TestHiveMetaStore.client, TestHiveMetaStore.conf);
    }

    @Test
    public void testListPartitions() throws Throwable {
        // create a table with multiple partitions
        String dbName = "compdb";
        String tblName = "comptbl";
        String typeName = "Person";
        cleanUp(dbName, tblName, typeName);
        List<List<String>> values = new ArrayList<>();
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "14"));
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-02 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-03 14:13:12", "151"));
        createMultiPartitionTableSchema(dbName, tblName, typeName, values);
        List<Partition> partitions = TestHiveMetaStore.client.listPartitions(dbName, tblName, ((short) (-1)));
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(((" should have returned " + (values.size())) + " partitions"), values.size(), partitions.size());
        partitions = TestHiveMetaStore.client.listPartitions(dbName, tblName, ((short) ((values.size()) / 2)));
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(((" should have returned " + ((values.size()) / 2)) + " partitions"), ((values.size()) / 2), partitions.size());
        partitions = TestHiveMetaStore.client.listPartitions(dbName, tblName, ((short) ((values.size()) * 2)));
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(((" should have returned " + (values.size())) + " partitions"), values.size(), partitions.size());
        cleanUp(dbName, tblName, typeName);
    }

    @Test
    public void testListPartitionsWihtLimitEnabled() throws Throwable {
        // create a table with multiple partitions
        String dbName = "compdb";
        String tblName = "comptbl";
        String typeName = "Person";
        cleanUp(dbName, tblName, typeName);
        // Create too many partitions, just enough to validate over limit requests
        List<List<String>> values = new ArrayList<>();
        for (int i = 0; i < ((TestHiveMetaStore.DEFAULT_LIMIT_PARTITION_REQUEST) + 1); i++) {
            values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", Integer.toString(i)));
        }
        createMultiPartitionTableSchema(dbName, tblName, typeName, values);
        List<Partition> partitions;
        short maxParts;
        // Requesting more partitions than allowed should throw an exception
        try {
            maxParts = -1;
            partitions = TestHiveMetaStore.client.listPartitions(dbName, tblName, maxParts);
            Assert.fail("should have thrown MetaException about partition limit");
        } catch (MetaException e) {
            Assert.assertTrue(true);
        }
        // Requesting more partitions than allowed should throw an exception
        try {
            maxParts = (TestHiveMetaStore.DEFAULT_LIMIT_PARTITION_REQUEST) + 1;
            partitions = TestHiveMetaStore.client.listPartitions(dbName, tblName, maxParts);
            Assert.fail("should have thrown MetaException about partition limit");
        } catch (MetaException e) {
            Assert.assertTrue(true);
        }
        // Requesting less partitions than allowed should work
        maxParts = (TestHiveMetaStore.DEFAULT_LIMIT_PARTITION_REQUEST) / 2;
        partitions = TestHiveMetaStore.client.listPartitions(dbName, tblName, maxParts);
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(" should have returned 50 partitions", maxParts, partitions.size());
    }

    @Test
    public void testAlterTableCascade() throws Throwable {
        // create a table with multiple partitions
        String dbName = "compdb";
        String tblName = "comptbl";
        String typeName = "Person";
        cleanUp(dbName, tblName, typeName);
        List<List<String>> values = new ArrayList<>();
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "14"));
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-02 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-03 14:13:12", "151"));
        createMultiPartitionTableSchema(dbName, tblName, typeName, values);
        Table tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
        List<FieldSchema> cols = tbl.getSd().getCols();
        cols.add(new FieldSchema("new_col", STRING_TYPE_NAME, ""));
        tbl.getSd().setCols(cols);
        // add new column with cascade option
        TestHiveMetaStore.client.alter_table(dbName, tblName, tbl, true);
        // 
        Table tbl2 = TestHiveMetaStore.client.getTable(dbName, tblName);
        Assert.assertEquals("Unexpected number of cols", 3, tbl2.getSd().getCols().size());
        Assert.assertEquals("Unexpected column name", "new_col", getName());
        // get a partition
        List<String> pvalues = new ArrayList<>(2);
        pvalues.add("2008-07-01 14:13:12");
        pvalues.add("14");
        Partition partition = TestHiveMetaStore.client.getPartition(dbName, tblName, pvalues);
        Assert.assertEquals("Unexpected number of cols", 3, partition.getSd().getCols().size());
        Assert.assertEquals("Unexpected column name", "new_col", getName());
        // add another column
        cols = tbl.getSd().getCols();
        cols.add(new FieldSchema("new_col2", STRING_TYPE_NAME, ""));
        tbl.getSd().setCols(cols);
        // add new column with no cascade option
        TestHiveMetaStore.client.alter_table(dbName, tblName, tbl, false);
        tbl2 = TestHiveMetaStore.client.getTable(dbName, tblName);
        Assert.assertEquals("Unexpected number of cols", 4, tbl2.getSd().getCols().size());
        Assert.assertEquals("Unexpected column name", "new_col2", getName());
        // get partition, this partition should not have the newly added column since cascade option
        // was false
        partition = TestHiveMetaStore.client.getPartition(dbName, tblName, pvalues);
        Assert.assertEquals("Unexpected number of cols", 3, partition.getSd().getCols().size());
    }

    @Test
    public void testListPartitionNames() throws Throwable {
        // create a table with multiple partitions
        String dbName = "compdb";
        String tblName = "comptbl";
        String typeName = "Person";
        cleanUp(dbName, tblName, typeName);
        List<List<String>> values = new ArrayList<>();
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "14"));
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-02 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-03 14:13:12", "151"));
        createMultiPartitionTableSchema(dbName, tblName, typeName, values);
        List<String> partitions = TestHiveMetaStore.client.listPartitionNames(dbName, tblName, ((short) (-1)));
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(((" should have returned " + (values.size())) + " partitions"), values.size(), partitions.size());
        partitions = TestHiveMetaStore.client.listPartitionNames(dbName, tblName, ((short) ((values.size()) / 2)));
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(((" should have returned " + ((values.size()) / 2)) + " partitions"), ((values.size()) / 2), partitions.size());
        partitions = TestHiveMetaStore.client.listPartitionNames(dbName, tblName, ((short) ((values.size()) * 2)));
        Assert.assertNotNull("should have returned partitions", partitions);
        Assert.assertEquals(((" should have returned " + (values.size())) + " partitions"), values.size(), partitions.size());
        cleanUp(dbName, tblName, typeName);
    }

    @Test
    public void testGetPartitionsWithSpec() throws Throwable {
        // create a table with multiple partitions
        List<Partition> createdPartitions = setupProjectionTestTable();
        Table tbl = TestHiveMetaStore.client.getTable("compdb", "comptbl");
        GetPartitionsRequest request = new GetPartitionsRequest();
        GetPartitionsProjectionSpec projectSpec = new GetPartitionsProjectionSpec();
        projectSpec.setFieldList(Arrays.asList("dbName", "tableName", "catName", "parameters", "lastAccessTime", "sd.location", "values", "createTime", "sd.serdeInfo.serializationLib", "sd.cols"));
        projectSpec.setExcludeParamKeyPattern("exclude%");
        GetPartitionsFilterSpec filter = new GetPartitionsFilterSpec();
        request.setDbName("compdb");
        request.setTblName("comptbl");
        request.setFilterSpec(filter);
        request.setProjectionSpec(projectSpec);
        GetPartitionsResponse response;
        try {
            response = TestHiveMetaStore.client.getPartitionsWithSpecs(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            TestHiveMetaStore.LOG.error("Exception while retriveing partitions", ex);
            throw ex;
        }
        Assert.assertEquals(1, response.getPartitionSpecSize());
        PartitionSpecWithSharedSD partitionSpecWithSharedSD = response.getPartitionSpec().get(0).getSharedSDPartitionSpec();
        Assert.assertNotNull(partitionSpecWithSharedSD.getSd());
        StorageDescriptor sharedSD = partitionSpecWithSharedSD.getSd();
        Assert.assertEquals("Root location should be set to table location", tbl.getSd().getLocation(), sharedSD.getLocation());
        Assert.assertFalse("Fields which are not requested should not be set", sharedSD.isSetParameters());
        Assert.assertNotNull("serializationLib class was requested but was not found in the returned partition", sharedSD.getSerdeInfo().getSerializationLib());
        Assert.assertNotNull("db name was requested but was not found in the returned partition", response.getPartitionSpec().get(0).getDbName());
        Assert.assertNotNull("Table name was requested but was not found in the returned partition", response.getPartitionSpec().get(0).getTableName());
        Assert.assertTrue("sd.cols was requested but was not found in the returned response", partitionSpecWithSharedSD.getSd().isSetCols());
        List<FieldSchema> origSdCols = createdPartitions.get(0).getSd().getCols();
        Assert.assertEquals("Size of the requested sd.cols should be same", origSdCols.size(), partitionSpecWithSharedSD.getSd().getCols().size());
        for (int i = 0; i < (origSdCols.size()); i++) {
            FieldSchema origFs = origSdCols.get(i);
            FieldSchema returnedFs = partitionSpecWithSharedSD.getSd().getCols().get(i);
            Assert.assertEquals("Field schemas returned different than expected", origFs, returnedFs);
        }
        /* Assert
        .assertNotNull("Catalog name was requested but was not found in the returned partition",
        response.getPartitionSpec().get(0).getCatName());
         */
        List<PartitionWithoutSD> partitionWithoutSDS = partitionSpecWithSharedSD.getPartitions();
        Assert.assertEquals(createdPartitions.size(), partitionWithoutSDS.size());
        for (int i = 0; i < (createdPartitions.size()); i++) {
            Partition origPartition = createdPartitions.get(i);
            PartitionWithoutSD returnedPartitionWithoutSD = partitionWithoutSDS.get(i);
            Assert.assertEquals(String.format("Location returned for Partition %d is not correct", i), origPartition.getSd().getLocation(), ((sharedSD.getLocation()) + (returnedPartitionWithoutSD.getRelativePath())));
            Assert.assertTrue("createTime was request but is not set", returnedPartitionWithoutSD.isSetCreateTime());
            Assert.assertTrue("Partition parameters were requested but are not set", returnedPartitionWithoutSD.isSetParameters());
            // first partition has parameters set
            if (i == 0) {
                Assert.assertTrue("partition parameters not set", returnedPartitionWithoutSD.getParameters().containsKey("key1"));
                Assert.assertEquals("partition parameters does not contain included keys", "val1", returnedPartitionWithoutSD.getParameters().get("key1"));
                // excluded parameter should not be returned
                Assert.assertFalse("Excluded parameter key returned", returnedPartitionWithoutSD.getParameters().containsKey("excludeKey1"));
                Assert.assertFalse("Excluded parameter key returned", returnedPartitionWithoutSD.getParameters().containsKey("excludeKey2"));
            }
            List<String> returnedVals = returnedPartitionWithoutSD.getValues();
            List<String> actualVals = origPartition.getValues();
            for (int j = 0; j < (actualVals.size()); j++) {
                Assert.assertEquals(actualVals.get(j), returnedVals.get(j));
            }
        }
    }

    @Test
    public void testDropTable() throws Throwable {
        // create a table with multiple partitions
        String dbName = "compdb";
        String tblName = "comptbl";
        String typeName = "Person";
        cleanUp(dbName, tblName, typeName);
        List<List<String>> values = new ArrayList<>();
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "14"));
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-02 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-03 14:13:12", "151"));
        createMultiPartitionTableSchema(dbName, tblName, typeName, values);
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.client.dropType(typeName);
        boolean exceptionThrown = false;
        try {
            TestHiveMetaStore.client.getTable(dbName, tblName);
        } catch (Exception e) {
            Assert.assertEquals("table should not have existed", NoSuchObjectException.class, e.getClass());
            exceptionThrown = true;
        }
        Assert.assertTrue((("Table " + tblName) + " should have been dropped "), exceptionThrown);
    }

    @Test
    public void testAlterViewParititon() throws Throwable {
        String dbName = "compdb";
        String tblName = "comptbl";
        String viewName = "compView";
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).setDescription("Alter Partition Test database").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("name", STRING_TYPE_NAME).addCol("income", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        if (TestHiveMetaStore.isThriftClient) {
            // the createTable() above does not update the location in the 'tbl'
            // object when the client is a thrift client and the code below relies
            // on the location being present in the 'tbl' object - so get the table
            // from the metastore
            tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
        }
        ArrayList<FieldSchema> viewCols = new ArrayList<>(1);
        viewCols.add(new FieldSchema("income", INT_TYPE_NAME, ""));
        ArrayList<FieldSchema> viewPartitionCols = new ArrayList<>(1);
        viewPartitionCols.add(new FieldSchema("name", STRING_TYPE_NAME, ""));
        Table view = new Table();
        view.setDbName(dbName);
        view.setTableName(viewName);
        view.setTableType(VIRTUAL_VIEW.name());
        view.setPartitionKeys(viewPartitionCols);
        view.setViewOriginalText(("SELECT income, name FROM " + tblName));
        view.setViewExpandedText((((((((("SELECT `" + tblName) + "`.`income`, `") + tblName) + "`.`name` FROM `") + dbName) + "`.`") + tblName) + "`"));
        view.setRewriteEnabled(false);
        StorageDescriptor viewSd = new StorageDescriptor();
        view.setSd(viewSd);
        viewSd.setCols(viewCols);
        viewSd.setCompressed(false);
        viewSd.setParameters(new HashMap());
        viewSd.setSerdeInfo(new SerDeInfo());
        viewSd.getSerdeInfo().setParameters(new HashMap());
        TestHiveMetaStore.client.createTable(view);
        if (TestHiveMetaStore.isThriftClient) {
            // the createTable() above does not update the location in the 'tbl'
            // object when the client is a thrift client and the code below relies
            // on the location being present in the 'tbl' object - so get the table
            // from the metastore
            view = TestHiveMetaStore.client.getTable(dbName, viewName);
        }
        List<String> vals = new ArrayList<>(1);
        vals.add("abc");
        Partition part = new Partition();
        part.setDbName(dbName);
        part.setTableName(viewName);
        part.setValues(vals);
        part.setParameters(new HashMap());
        TestHiveMetaStore.client.add_partition(part);
        Partition part2 = TestHiveMetaStore.client.getPartition(dbName, viewName, part.getValues());
        part2.getParameters().put("a", "b");
        TestHiveMetaStore.client.alter_partition(dbName, viewName, part2, null);
        Partition part3 = TestHiveMetaStore.client.getPartition(dbName, viewName, part.getValues());
        Assert.assertEquals("couldn't view alter partition", part3.getParameters().get("a"), "b");
        TestHiveMetaStore.client.dropTable(dbName, viewName);
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.client.dropDatabase(dbName);
    }

    @Test
    public void testAlterPartition() throws Throwable {
        try {
            String dbName = "compdb";
            String tblName = "comptbl";
            List<String> vals = new ArrayList<>(2);
            vals.add("2008-07-01");
            vals.add("14");
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).setDescription("Alter Partition Test database").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("name", STRING_TYPE_NAME).addCol("income", INT_TYPE_NAME).addTableParam("test_param_1", "Use this for comments etc").addBucketCol("name").addSerdeParam(SERIALIZATION_FORMAT, "1").addPartCol("ds", STRING_TYPE_NAME).addPartCol("hr", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            if (TestHiveMetaStore.isThriftClient) {
                // the createTable() above does not update the location in the 'tbl'
                // object when the client is a thrift client and the code below relies
                // on the location being present in the 'tbl' object - so get the table
                // from the metastore
                tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
            }
            Partition part = new Partition();
            part.setDbName(dbName);
            part.setTableName(tblName);
            part.setValues(vals);
            part.setParameters(new HashMap());
            part.setSd(tbl.getSd());
            part.getSd().setSerdeInfo(tbl.getSd().getSerdeInfo());
            part.getSd().setLocation(((tbl.getSd().getLocation()) + "/part1"));
            TestHiveMetaStore.client.add_partition(part);
            Partition part2 = TestHiveMetaStore.client.getPartition(dbName, tblName, part.getValues());
            part2.getParameters().put("retention", "10");
            part2.getSd().setNumBuckets(12);
            part2.getSd().getSerdeInfo().getParameters().put("abc", "1");
            TestHiveMetaStore.client.alter_partition(dbName, tblName, part2, null);
            Partition part3 = TestHiveMetaStore.client.getPartition(dbName, tblName, part.getValues());
            Assert.assertEquals("couldn't alter partition", part3.getParameters().get("retention"), "10");
            Assert.assertEquals("couldn't alter partition", part3.getSd().getSerdeInfo().getParameters().get("abc"), "1");
            Assert.assertEquals("couldn't alter partition", part3.getSd().getNumBuckets(), 12);
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.client.dropDatabase(dbName);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testPartition() failed.");
            throw e;
        }
    }

    @Test
    public void testRenamePartition() throws Throwable {
        try {
            String dbName = "compdb1";
            String tblName = "comptbl1";
            List<String> vals = new ArrayList<>(2);
            vals.add("2011-07-11");
            vals.add("8");
            String part_path = "/ds=2011-07-11/hr=8";
            List<String> tmp_vals = new ArrayList<>(2);
            tmp_vals.add("tmp_2011-07-11");
            tmp_vals.add("-8");
            String part2_path = "/ds=tmp_2011-07-11/hr=-8";
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).setDescription("Rename Partition Test database").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("name", STRING_TYPE_NAME).addCol("income", INT_TYPE_NAME).addPartCol("ds", STRING_TYPE_NAME).addPartCol("hr", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            if (TestHiveMetaStore.isThriftClient) {
                // the createTable() above does not update the location in the 'tbl'
                // object when the client is a thrift client and the code below relies
                // on the location being present in the 'tbl' object - so get the table
                // from the metastore
                tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
            }
            Partition part = new Partition();
            part.setDbName(dbName);
            part.setTableName(tblName);
            part.setValues(vals);
            part.setParameters(new HashMap());
            part.setSd(tbl.getSd().deepCopy());
            part.getSd().setSerdeInfo(tbl.getSd().getSerdeInfo());
            part.getSd().setLocation(((tbl.getSd().getLocation()) + "/part1"));
            part.getParameters().put("retention", "10");
            part.getSd().setNumBuckets(12);
            part.getSd().getSerdeInfo().getParameters().put("abc", "1");
            TestHiveMetaStore.client.add_partition(part);
            part.setValues(tmp_vals);
            TestHiveMetaStore.client.renamePartition(dbName, tblName, vals, part);
            boolean exceptionThrown = false;
            try {
                Partition p = TestHiveMetaStore.client.getPartition(dbName, tblName, vals);
            } catch (Exception e) {
                Assert.assertEquals("partition should not have existed", NoSuchObjectException.class, e.getClass());
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected NoSuchObjectException", exceptionThrown);
            Partition part3 = TestHiveMetaStore.client.getPartition(dbName, tblName, tmp_vals);
            Assert.assertEquals("couldn't rename partition", part3.getParameters().get("retention"), "10");
            Assert.assertEquals("couldn't rename partition", part3.getSd().getSerdeInfo().getParameters().get("abc"), "1");
            Assert.assertEquals("couldn't rename partition", part3.getSd().getNumBuckets(), 12);
            Assert.assertEquals("new partition sd matches", part3.getSd().getLocation(), ((tbl.getSd().getLocation()) + part2_path));
            part.setValues(vals);
            TestHiveMetaStore.client.renamePartition(dbName, tblName, tmp_vals, part);
            exceptionThrown = false;
            try {
                Partition p = TestHiveMetaStore.client.getPartition(dbName, tblName, tmp_vals);
            } catch (Exception e) {
                Assert.assertEquals("partition should not have existed", NoSuchObjectException.class, e.getClass());
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected NoSuchObjectException", exceptionThrown);
            part3 = TestHiveMetaStore.client.getPartition(dbName, tblName, vals);
            Assert.assertEquals("couldn't rename partition", part3.getParameters().get("retention"), "10");
            Assert.assertEquals("couldn't rename partition", part3.getSd().getSerdeInfo().getParameters().get("abc"), "1");
            Assert.assertEquals("couldn't rename partition", part3.getSd().getNumBuckets(), 12);
            Assert.assertEquals("new partition sd matches", part3.getSd().getLocation(), ((tbl.getSd().getLocation()) + part_path));
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.client.dropDatabase(dbName);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testRenamePartition() failed.");
            throw e;
        }
    }

    @Test
    public void testDatabase() throws Throwable {
        try {
            // clear up any existing databases
            TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB2_NAME);
            Database db = new DatabaseBuilder().setName(TestHiveMetaStore.TEST_DB1_NAME).setOwnerName(SecurityUtils.getUser()).build(TestHiveMetaStore.conf);
            Assert.assertEquals(SecurityUtils.getUser(), db.getOwnerName());
            TestHiveMetaStore.client.createDatabase(db);
            db = TestHiveMetaStore.client.getDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            Assert.assertEquals("name of returned db is different from that of inserted db", TestHiveMetaStore.TEST_DB1_NAME, db.getName());
            Assert.assertEquals("location of the returned db is different from that of inserted db", TestHiveMetaStore.warehouse.getDatabasePath(db).toString(), db.getLocationUri());
            Assert.assertEquals(db.getOwnerName(), SecurityUtils.getUser());
            Assert.assertEquals(db.getOwnerType(), USER);
            Assert.assertEquals(DEFAULT_CATALOG_NAME, db.getCatalogName());
            Database db2 = new DatabaseBuilder().setName(TestHiveMetaStore.TEST_DB2_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            db2 = TestHiveMetaStore.client.getDatabase(TestHiveMetaStore.TEST_DB2_NAME);
            Assert.assertEquals("name of returned db is different from that of inserted db", TestHiveMetaStore.TEST_DB2_NAME, db2.getName());
            Assert.assertEquals("location of the returned db is different from that of inserted db", TestHiveMetaStore.warehouse.getDatabasePath(db2).toString(), db2.getLocationUri());
            List<String> dbs = TestHiveMetaStore.client.getDatabases(".*");
            Assert.assertTrue(("first database is not " + (TestHiveMetaStore.TEST_DB1_NAME)), dbs.contains(TestHiveMetaStore.TEST_DB1_NAME));
            Assert.assertTrue(("second database is not " + (TestHiveMetaStore.TEST_DB2_NAME)), dbs.contains(TestHiveMetaStore.TEST_DB2_NAME));
            TestHiveMetaStore.client.dropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            TestHiveMetaStore.client.dropDatabase(TestHiveMetaStore.TEST_DB2_NAME);
            TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB2_NAME);
        } catch (Throwable e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testDatabase() failed.");
            throw e;
        }
    }

    @Test
    public void testDatabaseLocationWithPermissionProblems() throws Exception {
        // Note: The following test will fail if you are running this test as root. Setting
        // permission to '0' on the database folder will not preclude root from being able
        // to create the necessary files.
        if (System.getProperty("user.name").equals("root")) {
            System.err.println("Skipping test because you are running as root!");
            return;
        }
        TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
        String dbLocation = (MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/test/_testDB_create_";
        FileSystem fs = FileSystem.get(new Path(dbLocation).toUri(), TestHiveMetaStore.conf);
        fs.mkdirs(new Path(((MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/test")), new FsPermission(((short) (0))));
        Database db = new DatabaseBuilder().setName(TestHiveMetaStore.TEST_DB1_NAME).setLocation(dbLocation).build(TestHiveMetaStore.conf);
        boolean createFailed = false;
        try {
            TestHiveMetaStore.client.createDatabase(db);
        } catch (MetaException cantCreateDB) {
            createFailed = true;
        } finally {
            // Cleanup
            if (!createFailed) {
                try {
                    TestHiveMetaStore.client.dropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
                } catch (Exception e) {
                    System.err.println(("Failed to remove database in cleanup: " + (e.getMessage())));
                }
            }
            fs.setPermission(new Path(((MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/test")), new FsPermission(((short) (755))));
            fs.delete(new Path(((MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/test")), true);
        }
        Assert.assertTrue("Database creation succeeded even with permission problem", createFailed);
    }

    @Test
    public void testDatabaseLocation() throws Throwable {
        try {
            // clear up any existing databases
            TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            String dbLocation = (MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/_testDB_create_";
            new DatabaseBuilder().setName(TestHiveMetaStore.TEST_DB1_NAME).setLocation(dbLocation).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Database db = TestHiveMetaStore.client.getDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            Assert.assertEquals("name of returned db is different from that of inserted db", TestHiveMetaStore.TEST_DB1_NAME, db.getName());
            Assert.assertEquals("location of the returned db is different from that of inserted db", TestHiveMetaStore.warehouse.getDnsPath(new Path(dbLocation)).toString(), db.getLocationUri());
            TestHiveMetaStore.client.dropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            TestHiveMetaStore.silentDropDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            boolean objectNotExist = false;
            try {
                TestHiveMetaStore.client.getDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            } catch (NoSuchObjectException e) {
                objectNotExist = true;
            }
            Assert.assertTrue((("Database " + (TestHiveMetaStore.TEST_DB1_NAME)) + " exists "), objectNotExist);
            dbLocation = (MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/_testDB_file_";
            FileSystem fs = FileSystem.get(new Path(dbLocation).toUri(), TestHiveMetaStore.conf);
            fs.createNewFile(new Path(dbLocation));
            fs.deleteOnExit(new Path(dbLocation));
            db = new DatabaseBuilder().setName(TestHiveMetaStore.TEST_DB1_NAME).setLocation(dbLocation).build(TestHiveMetaStore.conf);
            boolean createFailed = false;
            try {
                TestHiveMetaStore.client.createDatabase(db);
            } catch (MetaException cantCreateDB) {
                System.err.println(cantCreateDB.getMessage());
                createFailed = true;
            }
            Assert.assertTrue("Database creation succeeded even location exists and is a file", createFailed);
            objectNotExist = false;
            try {
                TestHiveMetaStore.client.getDatabase(TestHiveMetaStore.TEST_DB1_NAME);
            } catch (NoSuchObjectException e) {
                objectNotExist = true;
            }
            Assert.assertTrue((("Database " + (TestHiveMetaStore.TEST_DB1_NAME)) + " exists when location is specified and is a file"), objectNotExist);
        } catch (Throwable e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testDatabaseLocation() failed.");
            throw e;
        }
    }

    @Test
    public void testSimpleTypeApi() throws Exception {
        try {
            TestHiveMetaStore.client.dropType(INT_TYPE_NAME);
            Type typ1 = new Type();
            typ1.setName(INT_TYPE_NAME);
            boolean ret = TestHiveMetaStore.client.createType(typ1);
            Assert.assertTrue("Unable to create type", ret);
            Type typ1_2 = TestHiveMetaStore.client.getType(INT_TYPE_NAME);
            Assert.assertNotNull(typ1_2);
            Assert.assertEquals(getName(), getName());
            ret = TestHiveMetaStore.client.dropType(INT_TYPE_NAME);
            Assert.assertTrue("unable to drop type integer", ret);
            boolean exceptionThrown = false;
            try {
                TestHiveMetaStore.client.getType(INT_TYPE_NAME);
            } catch (NoSuchObjectException e) {
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected NoSuchObjectException", exceptionThrown);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testSimpleTypeApi() failed.");
            throw e;
        }
    }

    // TODO:pc need to enhance this with complex fields and getType_all function
    @Test
    public void testComplexTypeApi() throws Exception {
        try {
            TestHiveMetaStore.client.dropType("Person");
            Type typ1 = new Type();
            setName("Person");
            setFields(new ArrayList(2));
            getFields().add(new FieldSchema("name", STRING_TYPE_NAME, ""));
            getFields().add(new FieldSchema("income", INT_TYPE_NAME, ""));
            boolean ret = TestHiveMetaStore.client.createType(typ1);
            Assert.assertTrue("Unable to create type", ret);
            Type typ1_2 = TestHiveMetaStore.client.getType("Person");
            Assert.assertNotNull("type Person not found", typ1_2);
            Assert.assertEquals(getName(), getName());
            Assert.assertEquals(getFields().size(), getFields().size());
            Assert.assertEquals(getFields().get(0), getFields().get(0));
            Assert.assertEquals(getFields().get(1), getFields().get(1));
            TestHiveMetaStore.client.dropType("Family");
            Type fam = new Type();
            setName("Family");
            setFields(new ArrayList(2));
            getFields().add(new FieldSchema("name", STRING_TYPE_NAME, ""));
            getFields().add(new FieldSchema("members", ColumnType.getListType(getName()), ""));
            ret = TestHiveMetaStore.client.createType(fam);
            Assert.assertTrue(("Unable to create type " + (getName())), ret);
            Type fam2 = TestHiveMetaStore.client.getType("Family");
            Assert.assertNotNull("type Person not found", fam2);
            Assert.assertEquals(getName(), getName());
            Assert.assertEquals(getFields().size(), getFields().size());
            Assert.assertEquals(getFields().get(0), getFields().get(0));
            Assert.assertEquals(getFields().get(1), getFields().get(1));
            ret = TestHiveMetaStore.client.dropType("Family");
            Assert.assertTrue("unable to drop type Family", ret);
            ret = TestHiveMetaStore.client.dropType("Person");
            Assert.assertTrue("unable to drop type Person", ret);
            boolean exceptionThrown = false;
            try {
                TestHiveMetaStore.client.getType("Person");
            } catch (NoSuchObjectException e) {
                exceptionThrown = true;
            }
            Assert.assertTrue("Expected NoSuchObjectException", exceptionThrown);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testComplexTypeApi() failed.");
            throw e;
        }
    }

    @Test
    public void testSimpleTable() throws Exception {
        try {
            String dbName = "simpdb";
            String tblName = "simptbl";
            String tblName2 = "simptbl2";
            String typeName = "Person";
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            TestHiveMetaStore.client.dropType(typeName);
            Type typ1 = new Type();
            setName(typeName);
            setFields(new ArrayList(2));
            getFields().add(new FieldSchema("name", STRING_TYPE_NAME, ""));
            getFields().add(new FieldSchema("income", INT_TYPE_NAME, ""));
            TestHiveMetaStore.client.createType(typ1);
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).setCols(getFields()).setNumBuckets(1).addBucketCol("name").addStorageDescriptorParam("test_param_1", "Use this for comments etc").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            if (TestHiveMetaStore.isThriftClient) {
                // the createTable() above does not update the location in the 'tbl'
                // object when the client is a thrift client and the code below relies
                // on the location being present in the 'tbl' object - so get the table
                // from the metastore
                tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
            }
            Table tbl2 = TestHiveMetaStore.client.getTable(dbName, tblName);
            Assert.assertNotNull(tbl2);
            Assert.assertTrue(tbl2.isSetId());
            Assert.assertEquals(tbl2.getDbName(), dbName);
            Assert.assertEquals(tbl2.getTableName(), tblName);
            Assert.assertEquals(tbl2.getSd().getCols().size(), getFields().size());
            Assert.assertEquals(tbl2.getSd().isCompressed(), false);
            Assert.assertEquals(tbl2.getSd().getNumBuckets(), 1);
            Assert.assertEquals(tbl2.getSd().getLocation(), tbl.getSd().getLocation());
            Assert.assertNotNull(tbl2.getSd().getSerdeInfo());
            tbl.getSd().getSerdeInfo().setParameters(new HashMap());
            tbl.getSd().getSerdeInfo().getParameters().put(SERIALIZATION_FORMAT, "1");
            tbl2.setTableName(tblName2);
            tbl2.setParameters(new HashMap());
            tbl2.getParameters().put("EXTERNAL", "TRUE");
            tbl2.getSd().setLocation(((tbl.getSd().getLocation()) + "-2"));
            List<FieldSchema> fieldSchemas = TestHiveMetaStore.client.getFields(dbName, tblName);
            Assert.assertNotNull(fieldSchemas);
            Assert.assertEquals(fieldSchemas.size(), tbl.getSd().getCols().size());
            for (FieldSchema fs : tbl.getSd().getCols()) {
                Assert.assertTrue(fieldSchemas.contains(fs));
            }
            List<FieldSchema> fieldSchemasFull = TestHiveMetaStore.client.getSchema(dbName, tblName);
            Assert.assertNotNull(fieldSchemasFull);
            Assert.assertEquals(fieldSchemasFull.size(), ((tbl.getSd().getCols().size()) + (tbl.getPartitionKeys().size())));
            for (FieldSchema fs : tbl.getSd().getCols()) {
                Assert.assertTrue(fieldSchemasFull.contains(fs));
            }
            for (FieldSchema fs : tbl.getPartitionKeys()) {
                Assert.assertTrue(fieldSchemasFull.contains(fs));
            }
            tbl2.unsetId();
            TestHiveMetaStore.client.createTable(tbl2);
            if (TestHiveMetaStore.isThriftClient) {
                tbl2 = TestHiveMetaStore.client.getTable(tbl2.getDbName(), tbl2.getTableName());
            }
            Table tbl3 = TestHiveMetaStore.client.getTable(dbName, tblName2);
            Assert.assertNotNull(tbl3);
            Assert.assertEquals(tbl3.getDbName(), dbName);
            Assert.assertEquals(tbl3.getTableName(), tblName2);
            Assert.assertEquals(tbl3.getSd().getCols().size(), getFields().size());
            Assert.assertEquals(tbl3.getSd().isCompressed(), false);
            Assert.assertEquals(tbl3.getSd().getNumBuckets(), 1);
            Assert.assertEquals(tbl3.getSd().getLocation(), tbl2.getSd().getLocation());
            Assert.assertEquals(tbl3.getParameters(), tbl2.getParameters());
            fieldSchemas = TestHiveMetaStore.client.getFields(dbName, tblName2);
            Assert.assertNotNull(fieldSchemas);
            Assert.assertEquals(fieldSchemas.size(), tbl2.getSd().getCols().size());
            for (FieldSchema fs : tbl2.getSd().getCols()) {
                Assert.assertTrue(fieldSchemas.contains(fs));
            }
            fieldSchemasFull = TestHiveMetaStore.client.getSchema(dbName, tblName2);
            Assert.assertNotNull(fieldSchemasFull);
            Assert.assertEquals(fieldSchemasFull.size(), ((tbl2.getSd().getCols().size()) + (tbl2.getPartitionKeys().size())));
            for (FieldSchema fs : tbl2.getSd().getCols()) {
                Assert.assertTrue(fieldSchemasFull.contains(fs));
            }
            for (FieldSchema fs : tbl2.getPartitionKeys()) {
                Assert.assertTrue(fieldSchemasFull.contains(fs));
            }
            Assert.assertEquals("Use this for comments etc", tbl2.getSd().getParameters().get("test_param_1"));
            Assert.assertEquals("name", tbl2.getSd().getBucketCols().get(0));
            Assert.assertTrue("Partition key list is not empty", (((tbl2.getPartitionKeys()) == null) || ((tbl2.getPartitionKeys().size()) == 0)));
            // test get_table_objects_by_name functionality
            ArrayList<String> tableNames = new ArrayList<>();
            tableNames.add(tblName2);
            tableNames.add(tblName);
            tableNames.add(tblName2);
            List<Table> foundTables = TestHiveMetaStore.client.getTableObjectsByName(dbName, tableNames);
            Assert.assertEquals(2, foundTables.size());
            for (Table t : foundTables) {
                if (t.getTableName().equals(tblName2)) {
                    Assert.assertEquals(t.getSd().getLocation(), tbl2.getSd().getLocation());
                } else {
                    Assert.assertEquals(t.getTableName(), tblName);
                    Assert.assertEquals(t.getSd().getLocation(), tbl.getSd().getLocation());
                }
                Assert.assertEquals(t.getSd().getCols().size(), getFields().size());
                Assert.assertEquals(t.getSd().isCompressed(), false);
                Assert.assertEquals(foundTables.get(0).getSd().getNumBuckets(), 1);
                Assert.assertNotNull(t.getSd().getSerdeInfo());
                Assert.assertEquals(t.getDbName(), dbName);
            }
            tableNames.add(1, "table_that_doesnt_exist");
            foundTables = TestHiveMetaStore.client.getTableObjectsByName(dbName, tableNames);
            Assert.assertEquals(foundTables.size(), 2);
            InvalidOperationException ioe = null;
            try {
                foundTables = TestHiveMetaStore.client.getTableObjectsByName(dbName, null);
            } catch (InvalidOperationException e) {
                ioe = e;
            }
            Assert.assertNotNull(ioe);
            Assert.assertTrue("Table not found", ioe.getMessage().contains("null tables"));
            UnknownDBException udbe = null;
            try {
                foundTables = TestHiveMetaStore.client.getTableObjectsByName("db_that_doesnt_exist", tableNames);
            } catch (UnknownDBException e) {
                udbe = e;
            }
            Assert.assertNotNull(udbe);
            Assert.assertTrue("DB not found", udbe.getMessage().contains("not find database hive.db_that_doesnt_exist"));
            udbe = null;
            try {
                foundTables = TestHiveMetaStore.client.getTableObjectsByName("", tableNames);
            } catch (UnknownDBException e) {
                udbe = e;
            }
            Assert.assertNotNull(udbe);
            Assert.assertTrue("DB not found", udbe.getMessage().contains("is null or empty"));
            FileSystem fs = FileSystem.get(toUri(), TestHiveMetaStore.conf);
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            Assert.assertFalse(fs.exists(new Path(tbl.getSd().getLocation())));
            TestHiveMetaStore.client.dropTable(dbName, tblName2);
            Assert.assertTrue(fs.exists(new Path(tbl2.getSd().getLocation())));
            TestHiveMetaStore.client.dropType(typeName);
            TestHiveMetaStore.client.dropDatabase(dbName);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testSimpleTable() failed.");
            throw e;
        }
    }

    // Tests that in the absence of stats for partitions, and/or absence of columns
    // to get stats for, the metastore does not break. See HIVE-12083 for motivation.
    @Test
    public void testStatsFastTrivial() throws Throwable {
        String dbName = "tstatsfast";
        String tblName = "t1";
        String tblOwner = "statstester";
        String typeName = "Person";
        int lastAccessed = 12083;
        cleanUp(dbName, tblName, typeName);
        List<List<String>> values = new ArrayList<>();
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "14"));
        values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-02 14:13:12", "15"));
        values.add(TestHiveMetaStore.makeVals("2008-07-03 14:13:12", "151"));
        createMultiPartitionTableSchema(dbName, tblName, typeName, values);
        List<String> emptyColNames = new ArrayList<>();
        List<String> emptyPartNames = new ArrayList<>();
        List<String> colNames = new ArrayList<>();
        colNames.add("name");
        colNames.add("income");
        List<String> partNames = TestHiveMetaStore.client.listPartitionNames(dbName, tblName, ((short) (-1)));
        Assert.assertEquals(0, emptyColNames.size());
        Assert.assertEquals(0, emptyPartNames.size());
        Assert.assertEquals(2, colNames.size());
        Assert.assertEquals(4, partNames.size());
        // Test for both colNames and partNames being empty:
        AggrStats aggrStatsEmpty = TestHiveMetaStore.client.getAggrColStatsFor(dbName, tblName, emptyColNames, emptyPartNames);
        Assert.assertNotNull(aggrStatsEmpty);// short-circuited on client-side, verifying that it's an empty object, not null

        Assert.assertEquals(0, aggrStatsEmpty.getPartsFound());
        Assert.assertNotNull(aggrStatsEmpty.getColStats());
        assert aggrStatsEmpty.getColStats().isEmpty();
        // Test for only colNames being empty
        AggrStats aggrStatsOnlyParts = TestHiveMetaStore.client.getAggrColStatsFor(dbName, tblName, emptyColNames, partNames);
        Assert.assertNotNull(aggrStatsOnlyParts);// short-circuited on client-side, verifying that it's an empty object, not null

        Assert.assertEquals(0, aggrStatsOnlyParts.getPartsFound());
        Assert.assertNotNull(aggrStatsOnlyParts.getColStats());
        assert aggrStatsOnlyParts.getColStats().isEmpty();
        // Test for only partNames being empty
        AggrStats aggrStatsOnlyCols = TestHiveMetaStore.client.getAggrColStatsFor(dbName, tblName, colNames, emptyPartNames);
        Assert.assertNotNull(aggrStatsOnlyCols);// short-circuited on client-side, verifying that it's an empty object, not null

        Assert.assertEquals(0, aggrStatsOnlyCols.getPartsFound());
        Assert.assertNotNull(aggrStatsOnlyCols.getColStats());
        assert aggrStatsOnlyCols.getColStats().isEmpty();
        // Test for valid values for both.
        AggrStats aggrStatsFull = TestHiveMetaStore.client.getAggrColStatsFor(dbName, tblName, colNames, partNames);
        Assert.assertNotNull(aggrStatsFull);
        Assert.assertEquals(0, aggrStatsFull.getPartsFound());// would still be empty, because no stats are actually populated.

        Assert.assertNotNull(aggrStatsFull.getColStats());
        assert aggrStatsFull.getColStats().isEmpty();
    }

    @Test
    public void testColumnStatistics() throws Throwable {
        String dbName = "columnstatstestdb";
        String tblName = "tbl";
        String typeName = "Person";
        String tblOwner = "testowner";
        int lastAccessed = 6796;
        try {
            cleanUp(dbName, tblName, typeName);
            new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            createTableForTestFilter(dbName, tblName, tblOwner, lastAccessed, true);
            // Create a ColumnStatistics Obj
            String[] colName = new String[]{ "income", "name" };
            double lowValue = 50000.21;
            double highValue = 1200000.4525;
            long numNulls = 3;
            long numDVs = 22;
            double avgColLen = 50.3;
            long maxColLen = 102;
            String[] colType = new String[]{ "double", "string" };
            boolean isTblLevel = true;
            String partName = null;
            List<ColumnStatisticsObj> statsObjs = new ArrayList<>();
            ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
            statsDesc.setDbName(dbName);
            statsDesc.setTableName(tblName);
            statsDesc.setIsTblLevel(isTblLevel);
            statsDesc.setPartName(partName);
            ColumnStatisticsObj statsObj = new ColumnStatisticsObj();
            statsObj.setColName(colName[0]);
            statsObj.setColType(colType[0]);
            ColumnStatisticsData statsData = new ColumnStatisticsData();
            DoubleColumnStatsData numericStats = new DoubleColumnStatsData();
            statsData.setDoubleStats(numericStats);
            statsData.getDoubleStats().setHighValue(highValue);
            statsData.getDoubleStats().setLowValue(lowValue);
            statsData.getDoubleStats().setNumDVs(numDVs);
            statsData.getDoubleStats().setNumNulls(numNulls);
            statsObj.setStatsData(statsData);
            statsObjs.add(statsObj);
            statsObj = new ColumnStatisticsObj();
            statsObj.setColName(colName[1]);
            statsObj.setColType(colType[1]);
            statsData = new ColumnStatisticsData();
            StringColumnStatsData stringStats = new StringColumnStatsData();
            statsData.setStringStats(stringStats);
            statsData.getStringStats().setAvgColLen(avgColLen);
            statsData.getStringStats().setMaxColLen(maxColLen);
            statsData.getStringStats().setNumDVs(numDVs);
            statsData.getStringStats().setNumNulls(numNulls);
            statsObj.setStatsData(statsData);
            statsObjs.add(statsObj);
            ColumnStatistics colStats = new ColumnStatistics();
            colStats.setStatsDesc(statsDesc);
            colStats.setStatsObj(statsObjs);
            // write stats objs persistently
            TestHiveMetaStore.client.updateTableColumnStatistics(colStats);
            // retrieve the stats obj that was just written
            ColumnStatisticsObj colStats2 = TestHiveMetaStore.client.getTableColumnStatistics(dbName, tblName, Lists.newArrayList(colName[0])).get(0);
            // compare stats obj to ensure what we get is what we wrote
            Assert.assertNotNull(colStats2);
            Assert.assertEquals(colStats2.getColName(), colName[0]);
            Assert.assertEquals(colStats2.getStatsData().getDoubleStats().getLowValue(), lowValue, 0.01);
            Assert.assertEquals(colStats2.getStatsData().getDoubleStats().getHighValue(), highValue, 0.01);
            Assert.assertEquals(colStats2.getStatsData().getDoubleStats().getNumNulls(), numNulls);
            Assert.assertEquals(colStats2.getStatsData().getDoubleStats().getNumDVs(), numDVs);
            // test delete column stats; if no col name is passed all column stats associated with the
            // table is deleted
            boolean status = TestHiveMetaStore.client.deleteTableColumnStatistics(dbName, tblName, null);
            Assert.assertTrue(status);
            // try to query stats for a column for which stats doesn't exist
            Assert.assertTrue(TestHiveMetaStore.client.getTableColumnStatistics(dbName, tblName, Lists.newArrayList(colName[1])).isEmpty());
            colStats.setStatsDesc(statsDesc);
            colStats.setStatsObj(statsObjs);
            // update table level column stats
            TestHiveMetaStore.client.updateTableColumnStatistics(colStats);
            // query column stats for column whose stats were updated in the previous call
            colStats2 = TestHiveMetaStore.client.getTableColumnStatistics(dbName, tblName, Lists.newArrayList(colName[0])).get(0);
            // partition level column statistics test
            // create a table with multiple partitions
            cleanUp(dbName, tblName, typeName);
            List<List<String>> values = new ArrayList<>();
            values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "14"));
            values.add(TestHiveMetaStore.makeVals("2008-07-01 14:13:12", "15"));
            values.add(TestHiveMetaStore.makeVals("2008-07-02 14:13:12", "15"));
            values.add(TestHiveMetaStore.makeVals("2008-07-03 14:13:12", "151"));
            createMultiPartitionTableSchema(dbName, tblName, typeName, values);
            List<String> partitions = TestHiveMetaStore.client.listPartitionNames(dbName, tblName, ((short) (-1)));
            partName = partitions.get(0);
            isTblLevel = false;
            // create a new columnstatistics desc to represent partition level column stats
            statsDesc = new ColumnStatisticsDesc();
            statsDesc.setDbName(dbName);
            statsDesc.setTableName(tblName);
            statsDesc.setPartName(partName);
            statsDesc.setIsTblLevel(isTblLevel);
            colStats = new ColumnStatistics();
            colStats.setStatsDesc(statsDesc);
            colStats.setStatsObj(statsObjs);
            TestHiveMetaStore.client.updatePartitionColumnStatistics(colStats);
            colStats2 = TestHiveMetaStore.client.getPartitionColumnStatistics(dbName, tblName, Lists.newArrayList(partName), Lists.newArrayList(colName[1])).get(partName).get(0);
            // compare stats obj to ensure what we get is what we wrote
            Assert.assertNotNull(colStats2);
            Assert.assertEquals(colStats.getStatsDesc().getPartName(), partName);
            Assert.assertEquals(colStats2.getColName(), colName[1]);
            Assert.assertEquals(colStats2.getStatsData().getStringStats().getMaxColLen(), maxColLen);
            Assert.assertEquals(colStats2.getStatsData().getStringStats().getAvgColLen(), avgColLen, 0.01);
            Assert.assertEquals(colStats2.getStatsData().getStringStats().getNumNulls(), numNulls);
            Assert.assertEquals(colStats2.getStatsData().getStringStats().getNumDVs(), numDVs);
            // test stats deletion at partition level
            TestHiveMetaStore.client.deletePartitionColumnStatistics(dbName, tblName, partName, colName[1]);
            colStats2 = TestHiveMetaStore.client.getPartitionColumnStatistics(dbName, tblName, Lists.newArrayList(partName), Lists.newArrayList(colName[0])).get(partName).get(0);
            // test get stats on a column for which stats doesn't exist
            Assert.assertTrue(TestHiveMetaStore.client.getPartitionColumnStatistics(dbName, tblName, Lists.newArrayList(partName), Lists.newArrayList(colName[1])).isEmpty());
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testColumnStatistics() failed.");
            throw e;
        } finally {
            cleanUp(dbName, tblName, typeName);
        }
    }

    @Test(expected = MetaException.class)
    public void testGetSchemaWithNoClassDefFoundError() throws TException {
        String dbName = "testDb";
        String tblName = "testTable";
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("name", STRING_TYPE_NAME, "").setSerdeLib("no.such.class").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        TestHiveMetaStore.client.getSchema(dbName, tblName);
    }

    @Test
    public void testCreateAndGetTableWithDriver() throws Exception {
        String dbName = "createDb";
        String tblName = "createTbl";
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        createTable(dbName, tblName);
        Table tblRead = TestHiveMetaStore.client.getTable(dbName, tblName);
        Assert.assertTrue(tblRead.isSetId());
        long firstTableId = tblRead.getId();
        createTable(dbName, (tblName + "_2"));
        Table tblRead2 = TestHiveMetaStore.client.getTable(dbName, (tblName + "_2"));
        Assert.assertTrue(tblRead2.isSetId());
        Assert.assertNotEquals(firstTableId, tblRead2.getId());
    }

    @Test
    public void testCreateTableSettingId() throws Exception {
        String dbName = "createDb";
        String tblName = "createTbl";
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        Table table = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("foo", "string").addCol("bar", "string").build(TestHiveMetaStore.conf);
        table.setId(1);
        try {
            TestHiveMetaStore.client.createTable(table);
            Assert.fail(("An error should happen when setting the id" + " to create a table"));
        } catch (InvalidObjectException e) {
            Assert.assertTrue(e.getMessage().contains("Id shouldn't be set"));
            Assert.assertTrue(e.getMessage().contains(tblName));
        }
    }

    @Test
    public void testAlterTable() throws Exception {
        String dbName = "alterdb";
        String invTblName = "alter-tbl";
        String tblName = "altertbl";
        try {
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            ArrayList<FieldSchema> invCols = new ArrayList<>(2);
            invCols.add(new FieldSchema("n-ame", STRING_TYPE_NAME, ""));
            invCols.add(new FieldSchema("in.come", INT_TYPE_NAME, ""));
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(invTblName).setCols(invCols).build(TestHiveMetaStore.conf);
            boolean failed = false;
            try {
                TestHiveMetaStore.client.createTable(tbl);
            } catch (InvalidObjectException ex) {
                failed = true;
            }
            if (!failed) {
                Assert.assertTrue(("Able to create table with invalid name: " + invTblName), false);
            }
            // create an invalid table which has wrong column type
            ArrayList<FieldSchema> invColsInvType = new ArrayList<>(2);
            invColsInvType.add(new FieldSchema("name", STRING_TYPE_NAME, ""));
            invColsInvType.add(new FieldSchema("income", "xyz", ""));
            tbl.setTableName(tblName);
            tbl.getSd().setCols(invColsInvType);
            boolean failChecker = false;
            try {
                TestHiveMetaStore.client.createTable(tbl);
            } catch (InvalidObjectException ex) {
                failChecker = true;
            }
            if (!failChecker) {
                Assert.assertTrue(("Able to create table with invalid column type: " + invTblName), false);
            }
            ArrayList<FieldSchema> cols = new ArrayList<>(2);
            cols.add(new FieldSchema("name", STRING_TYPE_NAME, ""));
            cols.add(new FieldSchema("income", INT_TYPE_NAME, ""));
            // create a valid table
            tbl.setTableName(tblName);
            tbl.getSd().setCols(cols);
            TestHiveMetaStore.client.createTable(tbl);
            if (TestHiveMetaStore.isThriftClient) {
                tbl = TestHiveMetaStore.client.getTable(tbl.getDbName(), tbl.getTableName());
            }
            // now try to invalid alter table
            Table tbl2 = TestHiveMetaStore.client.getTable(dbName, tblName);
            failed = false;
            try {
                tbl2.setTableName(invTblName);
                tbl2.getSd().setCols(invCols);
                TestHiveMetaStore.client.alter_table(dbName, tblName, tbl2);
            } catch (InvalidOperationException ex) {
                failed = true;
            }
            if (!failed) {
                Assert.assertTrue(("Able to rename table with invalid name: " + invTblName), false);
            }
            // try an invalid alter table with partition key name
            Table tbl_pk = TestHiveMetaStore.client.getTable(tbl.getDbName(), tbl.getTableName());
            List<FieldSchema> partitionKeys = tbl_pk.getPartitionKeys();
            for (FieldSchema fs : partitionKeys) {
                fs.setName("invalid_to_change_name");
                fs.setComment("can_change_comment");
            }
            tbl_pk.setPartitionKeys(partitionKeys);
            try {
                TestHiveMetaStore.client.alter_table(dbName, tblName, tbl_pk);
            } catch (InvalidOperationException ex) {
                failed = true;
            }
            Assert.assertTrue("Should not have succeeded in altering partition key name", failed);
            // try a valid alter table partition key comment
            failed = false;
            tbl_pk = TestHiveMetaStore.client.getTable(tbl.getDbName(), tbl.getTableName());
            partitionKeys = tbl_pk.getPartitionKeys();
            for (FieldSchema fs : partitionKeys) {
                fs.setComment("can_change_comment");
            }
            tbl_pk.setPartitionKeys(partitionKeys);
            try {
                TestHiveMetaStore.client.alter_table(dbName, tblName, tbl_pk);
            } catch (InvalidOperationException ex) {
                failed = true;
            }
            Assert.assertFalse("Should not have failed alter table partition comment", failed);
            Table newT = TestHiveMetaStore.client.getTable(tbl.getDbName(), tbl.getTableName());
            Assert.assertEquals(partitionKeys, newT.getPartitionKeys());
            // try a valid alter table
            tbl2.setTableName((tblName + "_renamed"));
            tbl2.getSd().setCols(cols);
            tbl2.getSd().setNumBuckets(32);
            TestHiveMetaStore.client.alter_table(dbName, tblName, tbl2);
            Table tbl3 = TestHiveMetaStore.client.getTable(dbName, tbl2.getTableName());
            Assert.assertEquals("Alter table didn't succeed. Num buckets is different ", tbl2.getSd().getNumBuckets(), tbl3.getSd().getNumBuckets());
            // check that data has moved
            FileSystem fs = FileSystem.get(toUri(), TestHiveMetaStore.conf);
            Assert.assertFalse("old table location still exists", fs.exists(new Path(tbl.getSd().getLocation())));
            Assert.assertTrue("data did not move to new location", fs.exists(new Path(tbl3.getSd().getLocation())));
            if (!(TestHiveMetaStore.isThriftClient)) {
                Assert.assertEquals("alter table didn't move data correct location", tbl3.getSd().getLocation(), tbl2.getSd().getLocation());
            }
            // alter table with invalid column type
            tbl_pk.getSd().setCols(invColsInvType);
            failed = false;
            try {
                TestHiveMetaStore.client.alter_table(dbName, tbl2.getTableName(), tbl_pk);
            } catch (InvalidOperationException ex) {
                failed = true;
            }
            Assert.assertTrue("Should not have succeeded in altering column", failed);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testSimpleTable() failed.");
            throw e;
        } finally {
            TestHiveMetaStore.silentDropDatabase(dbName);
        }
    }

    @Test
    public void testComplexTable() throws Exception {
        String dbName = "compdb";
        String tblName = "comptbl";
        String typeName = "Person";
        try {
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            TestHiveMetaStore.client.dropType(typeName);
            Type typ1 = new Type();
            setName(typeName);
            setFields(new ArrayList(2));
            getFields().add(new FieldSchema("name", STRING_TYPE_NAME, ""));
            getFields().add(new FieldSchema("income", INT_TYPE_NAME, ""));
            TestHiveMetaStore.client.createType(typ1);
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).setCols(getFields()).addPartCol("ds", DATE_TYPE_NAME).addPartCol("hr", INT_TYPE_NAME).setNumBuckets(1).addBucketCol("name").addStorageDescriptorParam("test_param_1", "Use this for comments etc").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Table tbl2 = TestHiveMetaStore.client.getTable(dbName, tblName);
            Assert.assertEquals(tbl2.getDbName(), dbName);
            Assert.assertEquals(tbl2.getTableName(), tblName);
            Assert.assertEquals(tbl2.getSd().getCols().size(), getFields().size());
            Assert.assertFalse(tbl2.getSd().isCompressed());
            Assert.assertFalse(tbl2.getSd().isStoredAsSubDirectories());
            Assert.assertEquals(tbl2.getSd().getNumBuckets(), 1);
            Assert.assertEquals("Use this for comments etc", tbl2.getSd().getParameters().get("test_param_1"));
            Assert.assertEquals("name", tbl2.getSd().getBucketCols().get(0));
            Assert.assertNotNull(tbl2.getPartitionKeys());
            Assert.assertEquals(2, tbl2.getPartitionKeys().size());
            Assert.assertEquals(DATE_TYPE_NAME, tbl2.getPartitionKeys().get(0).getType());
            Assert.assertEquals(INT_TYPE_NAME, tbl2.getPartitionKeys().get(1).getType());
            Assert.assertEquals("ds", getName());
            Assert.assertEquals("hr", getName());
            List<FieldSchema> fieldSchemas = TestHiveMetaStore.client.getFields(dbName, tblName);
            Assert.assertNotNull(fieldSchemas);
            Assert.assertEquals(fieldSchemas.size(), tbl.getSd().getCols().size());
            for (FieldSchema fs : tbl.getSd().getCols()) {
                Assert.assertTrue(fieldSchemas.contains(fs));
            }
            List<FieldSchema> fieldSchemasFull = TestHiveMetaStore.client.getSchema(dbName, tblName);
            Assert.assertNotNull(fieldSchemasFull);
            Assert.assertEquals(fieldSchemasFull.size(), ((tbl.getSd().getCols().size()) + (tbl.getPartitionKeys().size())));
            for (FieldSchema fs : tbl.getSd().getCols()) {
                Assert.assertTrue(fieldSchemasFull.contains(fs));
            }
            for (FieldSchema fs : tbl.getPartitionKeys()) {
                Assert.assertTrue(fieldSchemasFull.contains(fs));
            }
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testComplexTable() failed.");
            throw e;
        } finally {
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            boolean ret = TestHiveMetaStore.client.dropType(typeName);
            Assert.assertTrue(("Unable to drop type " + typeName), ret);
            TestHiveMetaStore.client.dropDatabase(dbName);
        }
    }

    @Test
    public void testTableDatabase() throws Exception {
        String dbName = "testDb";
        String tblName_1 = "testTbl_1";
        String tblName_2 = "testTbl_2";
        try {
            TestHiveMetaStore.silentDropDatabase(dbName);
            String dbLocation = (MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "_testDB_table_create_";
            new DatabaseBuilder().setName(dbName).setLocation(dbLocation).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Database db = TestHiveMetaStore.client.getDatabase(dbName);
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName_1).addCol("name", STRING_TYPE_NAME).addCol("income", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            tbl = TestHiveMetaStore.client.getTable(dbName, tblName_1);
            Path path = new Path(tbl.getSd().getLocation());
            System.err.println(((("Table's location " + path) + ", Database's location ") + (db.getLocationUri())));
            Assert.assertEquals("Table location is not a subset of the database location", path.getParent().toString(), db.getLocationUri());
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testTableDatabase() failed.");
            throw e;
        } finally {
            TestHiveMetaStore.silentDropDatabase(dbName);
        }
    }

    @Test
    public void testGetConfigValue() {
        String val = "value";
        if (!(TestHiveMetaStore.isThriftClient)) {
            try {
                Assert.assertEquals(TestHiveMetaStore.client.getConfigValue("hive.key1", val), "value1");
                Assert.assertEquals(TestHiveMetaStore.client.getConfigValue("hive.key2", val), "http://www.example.com");
                Assert.assertEquals(TestHiveMetaStore.client.getConfigValue("hive.key3", val), "");
                Assert.assertEquals(TestHiveMetaStore.client.getConfigValue("hive.key4", val), "0");
                Assert.assertEquals(TestHiveMetaStore.client.getConfigValue("hive.key5", val), val);
                Assert.assertEquals(TestHiveMetaStore.client.getConfigValue(null, val), val);
            } catch (TException e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
        boolean threwException = false;
        try {
            // Attempting to get the password should throw an exception
            TestHiveMetaStore.client.getConfigValue("javax.jdo.option.ConnectionPassword", "password");
        } catch (ConfigValSecurityException e) {
            threwException = true;
        } catch (TException e) {
            e.printStackTrace();
            Assert.fail();
        }
        assert threwException;
    }

    /**
     * Tests for list partition by filter functionality.
     */
    @Test
    public void testPartitionFilter() throws Exception {
        String dbName = "filterdb";
        String tblName = "filtertbl";
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("c1", STRING_TYPE_NAME).addCol("c2", INT_TYPE_NAME).addPartCol("p1", STRING_TYPE_NAME).addPartCol("p2", STRING_TYPE_NAME).addPartCol("p3", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
        add_partition(TestHiveMetaStore.client, tbl, Lists.newArrayList("p11", "p21", "31"), "part1");
        add_partition(TestHiveMetaStore.client, tbl, Lists.newArrayList("p11", "p22", "32"), "part2");
        add_partition(TestHiveMetaStore.client, tbl, Lists.newArrayList("p12", "p21", "31"), "part3");
        add_partition(TestHiveMetaStore.client, tbl, Lists.newArrayList("p12", "p23", "32"), "part4");
        add_partition(TestHiveMetaStore.client, tbl, Lists.newArrayList("p13", "p24", "31"), "part5");
        add_partition(TestHiveMetaStore.client, tbl, Lists.newArrayList("p13", "p25", "-33"), "part6");
        // Test equals operator for strings and integers.
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 = \"p21\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 = \"p23\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 = 31", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 = 33", 0);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 = -33", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" and p2=\"p22\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" or p2=\"p23\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" or p1=\"p12\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" or p1=\"p12\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" or p1=\"p12\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" and p3 = 31", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 = -33 or p1 = \"p12\"", 3);
        // Test not-equals operator for strings and integers.
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p11\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 != \"p23\"", 5);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 != \"p33\"", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 != 32", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 != 8589934592", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p11\" and p1 != \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p11\" and p2 != \"p22\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p11\" or p2 != \"p22\"", 5);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p12\" and p2 != \"p25\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p12\" or p2 != \"p25\"", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 != -33 or p1 != \"p13\"", 5);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 != \"p11\" and p3 = 31", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 != 31 and p1 = \"p12\"", 1);
        // Test reverse order.
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "31 != p3 and p1 = \"p12\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "\"p23\" = p2", 1);
        // Test and/or more...
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p11\" or (p1=\"p12\" and p2=\"p21\")", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, ("p1 = \"p11\" or (p1=\"p12\" and p2=\"p21\") Or " + "(p1=\"p13\" aNd p2=\"p24\")"), 4);
        // test for and or precedence
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1=\"p12\" and (p2=\"p27\" Or p2=\"p21\")", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1=\"p12\" and p2=\"p27\" Or p2=\"p21\"", 2);
        // Test gt/lt/lte/gte/like for strings.
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 > \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 >= \"p12\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 < \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 <= \"p12\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 like \"p1.*\"", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 like \"p.*3\"", 1);
        // Test gt/lt/lte/gte for numbers.
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 < 0", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 >= -33", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 > -33", 5);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 > 31 and p3 < 32", 0);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 > 31 or p3 < 31", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 > 30 or p3 < 30", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 >= 31 or p3 < -32", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 >= 32", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 > 32", 0);
        // Test between
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 between \"p11\" and \"p12\"", 4);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 not between \"p11\" and \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 not between 0 and 2", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 between 31 and 32", 5);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 between 32 and 31", 0);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 between -32 and 34 and p3 not between 31 and 32", 0);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 between 1 and 3 or p3 not between 1 and 3", 6);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p3 between 31 and 32 and p1 between \"p12\" and \"p14\"", 3);
        // Test for setting the maximum partition count
        List<Partition> partitions = TestHiveMetaStore.client.listPartitionsByFilter(dbName, tblName, "p1 >= \"p12\"", ((short) (2)));
        Assert.assertEquals("User specified row limit for partitions", 2, partitions.size());
        // Negative tests
        Exception me = null;
        try {
            TestHiveMetaStore.client.listPartitionsByFilter(dbName, tblName, "p3 >= \"p12\"", ((short) (-1)));
        } catch (MetaException e) {
            me = e;
        }
        Assert.assertNotNull(me);
        Assert.assertTrue("Filter on int partition key", me.getMessage().contains("Filtering is supported only on partition keys of type string"));
        me = null;
        try {
            TestHiveMetaStore.client.listPartitionsByFilter(dbName, tblName, "c1 >= \"p12\"", ((short) (-1)));
        } catch (MetaException e) {
            me = e;
        }
        Assert.assertNotNull(me);
        Assert.assertTrue("Filter on invalid key", me.getMessage().contains("<c1> is not a partitioning key for the table"));
        me = null;
        try {
            TestHiveMetaStore.client.listPartitionsByFilter(dbName, tblName, "c1 >= ", ((short) (-1)));
        } catch (MetaException e) {
            me = e;
        }
        Assert.assertNotNull(me);
        Assert.assertTrue("Invalid filter string", me.getMessage().contains("Error parsing partition filter"));
        me = null;
        try {
            TestHiveMetaStore.client.listPartitionsByFilter("invDBName", "invTableName", "p1 = \"p11\"", ((short) (-1)));
        } catch (NoSuchObjectException e) {
            me = e;
        }
        Assert.assertNotNull(me);
        Assert.assertTrue("NoSuchObject exception", me.getMessage().contains("invDBName.invTableName table not found"));
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.client.dropDatabase(dbName);
    }

    /**
     * Test filtering on table with single partition
     */
    @Test
    public void testFilterSinglePartition() throws Exception {
        String dbName = "filterdb";
        String tblName = "filtertbl";
        List<String> vals = new ArrayList<>(1);
        vals.add("p11");
        List<String> vals2 = new ArrayList<>(1);
        vals2.add("p12");
        List<String> vals3 = new ArrayList<>(1);
        vals3.add("p13");
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("c1", STRING_TYPE_NAME).addCol("c2", INT_TYPE_NAME).addPartCol("p1", STRING_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
        add_partition(TestHiveMetaStore.client, tbl, vals, "part1");
        add_partition(TestHiveMetaStore.client, tbl, vals2, "part2");
        add_partition(TestHiveMetaStore.client, tbl, vals3, "part3");
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 = \"p12\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 < \"p12\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 > \"p12\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 >= \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 <= \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 <> \"p12\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 like \"p1.*\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p1 like \"p.*2\"", 1);
        TestHiveMetaStore.client.dropTable(dbName, tblName);
        TestHiveMetaStore.client.dropDatabase(dbName);
    }

    /**
     * Test filtering based on the value of the last partition
     */
    @Test
    public void testFilterLastPartition() throws Exception {
        String dbName = "filterdb";
        String tblName = "filtertbl";
        List<String> vals = new ArrayList<>(2);
        vals.add("p11");
        vals.add("p21");
        List<String> vals2 = new ArrayList<>(2);
        vals2.add("p11");
        vals2.add("p22");
        List<String> vals3 = new ArrayList<>(2);
        vals3.add("p12");
        vals3.add("p21");
        cleanUp(dbName, tblName, null);
        createDb(dbName);
        Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("c1", STRING_TYPE_NAME).addCol("c2", INT_TYPE_NAME).addPartCol("p1", STRING_TYPE_NAME).addPartCol("p2", STRING_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
        add_partition(TestHiveMetaStore.client, tbl, vals, "part1");
        add_partition(TestHiveMetaStore.client, tbl, vals2, "part2");
        add_partition(TestHiveMetaStore.client, tbl, vals3, "part3");
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 = \"p21\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 < \"p23\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 > \"p21\"", 1);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 >= \"p21\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 <= \"p21\"", 2);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 <> \"p12\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 != \"p12\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 like \"p2.*\"", 3);
        checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 like \"p.*2\"", 1);
        try {
            checkFilter(TestHiveMetaStore.client, dbName, tblName, "p2 !< 'dd'", 0);
            Assert.fail("Invalid operator not detected");
        } catch (MetaException e) {
            // expected exception due to lexer error
        }
        cleanUp(dbName, tblName, null);
    }

    /**
     * Tests {@link HiveMetaStoreClient#newSynchronizedClient}.  Does not
     * actually test multithreading, but does verify that the proxy
     * at least works correctly.
     */
    @Test
    public void testSynchronized() throws Exception {
        int currentNumberOfDbs = TestHiveMetaStore.client.getAllDatabases().size();
        IMetaStoreClient synchronizedClient = HiveMetaStoreClient.newSynchronizedClient(TestHiveMetaStore.client);
        List<String> databases = synchronizedClient.getAllDatabases();
        Assert.assertEquals(currentNumberOfDbs, databases.size());
    }

    @Test
    public void testTableFilter() throws Exception {
        try {
            String dbName = "testTableFilter";
            String owner1 = "testOwner1";
            String owner2 = "testOwner2";
            int lastAccessTime1 = 90;
            int lastAccessTime2 = 30;
            String tableName1 = "table1";
            String tableName2 = "table2";
            String tableName3 = "table3";
            TestHiveMetaStore.client.dropTable(dbName, tableName1);
            TestHiveMetaStore.client.dropTable(dbName, tableName2);
            TestHiveMetaStore.client.dropTable(dbName, tableName3);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).setDescription("Alter Partition Test database").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Table table1 = createTableForTestFilter(dbName, tableName1, owner1, lastAccessTime1, true);
            Table table2 = createTableForTestFilter(dbName, tableName2, owner2, lastAccessTime2, true);
            Table table3 = createTableForTestFilter(dbName, tableName3, owner1, lastAccessTime2, false);
            List<String> tableNames;
            String filter;
            // test owner
            // owner like ".*Owner.*" and owner like "test.*"
            filter = (((hive_metastoreConstants.HIVE_FILTER_FIELD_OWNER) + " like \".*Owner.*\" and ") + (hive_metastoreConstants.HIVE_FILTER_FIELD_OWNER)) + " like  \"test.*\"";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(tableNames.size(), 3);
            assert tableNames.contains(table1.getTableName());
            assert tableNames.contains(table2.getTableName());
            assert tableNames.contains(table3.getTableName());
            // owner = "testOwner1"
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_OWNER) + " = \"testOwner1\"";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(2, tableNames.size());
            assert tableNames.contains(table1.getTableName());
            assert tableNames.contains(table3.getTableName());
            // lastAccessTime < 90
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_LAST_ACCESS) + " < 90";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(2, tableNames.size());
            assert tableNames.contains(table2.getTableName());
            assert tableNames.contains(table3.getTableName());
            // lastAccessTime > 90
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_LAST_ACCESS) + " > 90";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(0, tableNames.size());
            // test params
            // test_param_2 = "50"
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_PARAMS) + "test_param_2 LIKE \"50\"";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(2, tableNames.size());
            assert tableNames.contains(table1.getTableName());
            assert tableNames.contains(table2.getTableName());
            // test_param_2 = "75"
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_PARAMS) + "test_param_2 LIKE \"75\"";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(0, tableNames.size());
            // key_dne = "50"
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_PARAMS) + "key_dne LIKE \"50\"";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(0, tableNames.size());
            // test_param_1 != "yellow"
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_PARAMS) + "test_param_1 NOT LIKE \"yellow\"";
            // Commenting as part of HIVE-12274 != and <> are not supported for CLOBs
            // tableNames = client.listTableNamesByFilter(dbName, filter, (short) 2);
            // assertEquals(2, tableNames.size());
            filter = (hive_metastoreConstants.HIVE_FILTER_FIELD_PARAMS) + "test_param_1 NOT LIKE \"yellow\"";
            // tableNames = client.listTableNamesByFilter(dbName, filter, (short) 2);
            // assertEquals(2, tableNames.size());
            // owner = "testOwner1" and (lastAccessTime = 30 or test_param_1 = "hi")
            filter = (((((hive_metastoreConstants.HIVE_FILTER_FIELD_OWNER) + " = \"testOwner1\" and (") + (hive_metastoreConstants.HIVE_FILTER_FIELD_LAST_ACCESS)) + " = 30 or ") + (hive_metastoreConstants.HIVE_FILTER_FIELD_PARAMS)) + "test_param_1 LIKE \"hi\")";
            tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            Assert.assertEquals(2, tableNames.size());
            assert tableNames.contains(table1.getTableName());
            assert tableNames.contains(table3.getTableName());
            // Negative tests
            Exception me = null;
            try {
                filter = "badKey = \"testOwner1\"";
                tableNames = TestHiveMetaStore.client.listTableNamesByFilter(dbName, filter, ((short) (-1)));
            } catch (MetaException e) {
                me = e;
            }
            Assert.assertNotNull(me);
            Assert.assertTrue("Bad filter key test", me.getMessage().contains("Invalid key name in filter"));
            TestHiveMetaStore.client.dropTable(dbName, tableName1);
            TestHiveMetaStore.client.dropTable(dbName, tableName2);
            TestHiveMetaStore.client.dropTable(dbName, tableName3);
            TestHiveMetaStore.client.dropDatabase(dbName);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testTableFilter() failed.");
            throw e;
        }
    }

    /**
     * Verify that if another  client, either a metastore Thrift server or  a Hive CLI instance
     * renames a table recently created by this instance, and hence potentially in its cache, the
     * current instance still sees the change.
     */
    @Test
    public void testConcurrentMetastores() throws Exception {
        String dbName = "concurrentdb";
        String tblName = "concurrenttbl";
        String renameTblName = "rename_concurrenttbl";
        try {
            cleanUp(dbName, tblName, null);
            createDb(dbName);
            Table tbl1 = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("c1", STRING_TYPE_NAME).addCol("c2", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            // get the table from the client, verify the name is correct
            Table tbl2 = TestHiveMetaStore.client.getTable(dbName, tblName);
            Assert.assertEquals("Client returned table with different name.", tbl2.getTableName(), tblName);
            // Simulate renaming via another metastore Thrift server or another Hive CLI instance
            updateTableNameInDB(tblName, renameTblName);
            // get the table from the client again, verify the name has been updated
            Table tbl3 = TestHiveMetaStore.client.getTable(dbName, renameTblName);
            Assert.assertEquals("Client returned table with different name after rename.", tbl3.getTableName(), renameTblName);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testConcurrentMetastores() failed.");
            throw e;
        } finally {
            TestHiveMetaStore.silentDropDatabase(dbName);
        }
    }

    @Test
    public void testSimpleFunction() throws Exception {
        String dbName = "test_db";
        String funcName = "test_func";
        String className = "org.apache.hadoop.hive.ql.udf.generic.GenericUDFUpper";
        String owner = "test_owner";
        final int N_FUNCTIONS = 5;
        PrincipalType ownerType = PrincipalType.USER;
        int createTime = ((int) ((System.currentTimeMillis()) / 1000));
        FunctionType funcType = FunctionType.JAVA;
        try {
            cleanUp(dbName, null, null);
            for (Function f : TestHiveMetaStore.client.getAllFunctions().getFunctions()) {
                TestHiveMetaStore.client.dropFunction(f.getDbName(), f.getFunctionName());
            }
            createDb(dbName);
            for (int i = 0; i < N_FUNCTIONS; i++) {
                createFunction(dbName, ((funcName + "_") + i), className, owner, ownerType, createTime, funcType, null);
            }
            // Try the different getters
            // getFunction()
            Function func = TestHiveMetaStore.client.getFunction(dbName, (funcName + "_0"));
            Assert.assertEquals("function db name", dbName, func.getDbName());
            Assert.assertEquals("function name", (funcName + "_0"), func.getFunctionName());
            Assert.assertEquals("function class name", className, func.getClassName());
            Assert.assertEquals("function owner name", owner, func.getOwnerName());
            Assert.assertEquals("function owner type", USER, func.getOwnerType());
            Assert.assertEquals("function type", funcType, func.getFunctionType());
            List<ResourceUri> resources = func.getResourceUris();
            Assert.assertTrue("function resources", ((resources == null) || ((resources.size()) == 0)));
            boolean gotException = false;
            try {
                func = TestHiveMetaStore.client.getFunction(dbName, "nonexistent_func");
            } catch (NoSuchObjectException e) {
                // expected failure
                gotException = true;
            }
            Assert.assertEquals(true, gotException);
            // getAllFunctions()
            GetAllFunctionsResponse response = TestHiveMetaStore.client.getAllFunctions();
            List<Function> allFunctions = response.getFunctions();
            Assert.assertEquals(N_FUNCTIONS, allFunctions.size());
            Assert.assertEquals((funcName + "_3"), allFunctions.get(3).getFunctionName());
            // getFunctions()
            List<String> funcs = TestHiveMetaStore.client.getFunctions(dbName, "*_func_*");
            Assert.assertEquals(N_FUNCTIONS, funcs.size());
            Assert.assertEquals((funcName + "_0"), funcs.get(0));
            funcs = TestHiveMetaStore.client.getFunctions(dbName, "nonexistent_func");
            Assert.assertEquals(0, funcs.size());
            // dropFunction()
            for (int i = 0; i < N_FUNCTIONS; i++) {
                TestHiveMetaStore.client.dropFunction(dbName, ((funcName + "_") + i));
            }
            // Confirm that the function is now gone
            funcs = TestHiveMetaStore.client.getFunctions(dbName, funcName);
            Assert.assertEquals(0, funcs.size());
            response = TestHiveMetaStore.client.getAllFunctions();
            allFunctions = response.getFunctions();
            Assert.assertEquals(0, allFunctions.size());
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testConcurrentMetastores() failed.");
            throw e;
        } finally {
            TestHiveMetaStore.silentDropDatabase(dbName);
        }
    }

    @Test
    public void testFunctionWithResources() throws Exception {
        String dbName = "test_db2";
        String funcName = "test_func";
        String className = "org.apache.hadoop.hive.ql.udf.generic.GenericUDFUpper";
        String owner = "test_owner";
        PrincipalType ownerType = PrincipalType.USER;
        int createTime = ((int) ((System.currentTimeMillis()) / 1000));
        FunctionType funcType = FunctionType.JAVA;
        List<ResourceUri> resList = new ArrayList<>();
        resList.add(new ResourceUri(ResourceType.JAR, "hdfs:///tmp/jar1.jar"));
        resList.add(new ResourceUri(ResourceType.FILE, "hdfs:///tmp/file1.txt"));
        resList.add(new ResourceUri(ResourceType.ARCHIVE, "hdfs:///tmp/archive1.tgz"));
        try {
            cleanUp(dbName, null, null);
            createDb(dbName);
            createFunction(dbName, funcName, className, owner, ownerType, createTime, funcType, resList);
            // Try the different getters
            // getFunction()
            Function func = TestHiveMetaStore.client.getFunction(dbName, funcName);
            Assert.assertEquals("function db name", dbName, func.getDbName());
            Assert.assertEquals("function name", funcName, func.getFunctionName());
            Assert.assertEquals("function class name", className, func.getClassName());
            Assert.assertEquals("function owner name", owner, func.getOwnerName());
            Assert.assertEquals("function owner type", USER, func.getOwnerType());
            Assert.assertEquals("function type", funcType, func.getFunctionType());
            List<ResourceUri> resources = func.getResourceUris();
            Assert.assertEquals("Resource list size", resList.size(), resources.size());
            for (ResourceUri res : resources) {
                Assert.assertTrue(((("Matching resource " + (res.getResourceType())) + " ") + (res.getUri())), ((resList.indexOf(res)) >= 0));
            }
            TestHiveMetaStore.client.dropFunction(dbName, funcName);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testConcurrentMetastores() failed.");
            throw e;
        } finally {
            TestHiveMetaStore.silentDropDatabase(dbName);
        }
    }

    @Test
    public void testDBOwner() throws TException {
        Database db = TestHiveMetaStore.client.getDatabase(DEFAULT_DATABASE_NAME);
        Assert.assertEquals(db.getOwnerName(), PUBLIC);
        Assert.assertEquals(db.getOwnerType(), ROLE);
    }

    /**
     * Test changing owner and owner type of a database
     */
    @Test
    public void testDBOwnerChange() throws TException {
        final String dbName = "alterDbOwner";
        final String user1 = "user1";
        final String user2 = "user2";
        final String role1 = "role1";
        TestHiveMetaStore.silentDropDatabase(dbName);
        Database db = new DatabaseBuilder().setName(dbName).setOwnerName(user1).setOwnerType(USER).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        checkDbOwnerType(dbName, user1, USER);
        db.setOwnerName(user2);
        TestHiveMetaStore.client.alterDatabase(dbName, db);
        checkDbOwnerType(dbName, user2, USER);
        db.setOwnerName(role1);
        db.setOwnerType(ROLE);
        TestHiveMetaStore.client.alterDatabase(dbName, db);
        checkDbOwnerType(dbName, role1, ROLE);
    }

    /**
     * Test table objects can be retrieved in batches
     */
    @Test
    public void testGetTableObjects() throws Exception {
        String dbName = "db";
        List<String> tableNames = Arrays.asList("table1", "table2", "table3", "table4", "table5");
        // Setup
        TestHiveMetaStore.silentDropDatabase(dbName);
        new DatabaseBuilder().setName(dbName).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        for (String tableName : tableNames) {
            createTable(dbName, tableName);
        }
        createMaterializedView(dbName, "mv1", Sets.newHashSet("db.table1", "db.table2"));
        // Test
        List<Table> tableObjs = TestHiveMetaStore.client.getTableObjectsByName(dbName, tableNames);
        // Verify
        Assert.assertEquals(tableNames.size(), tableObjs.size());
        for (Table table : tableObjs) {
            Assert.assertTrue(tableNames.contains(table.getTableName().toLowerCase()));
        }
        // Cleanup
        TestHiveMetaStore.client.dropDatabase(dbName, true, true, true);
    }

    @Test
    public void testDropDatabaseCascadeMVMultiDB() throws Exception {
        String dbName1 = "db1";
        String tableName1 = "table1";
        String dbName2 = "db2";
        String tableName2 = "table2";
        String mvName = "mv1";
        // Setup
        TestHiveMetaStore.silentDropDatabase(dbName1);
        TestHiveMetaStore.silentDropDatabase(dbName2);
        Database db1 = new Database();
        db1.setName(dbName1);
        TestHiveMetaStore.client.createDatabase(db1);
        createTable(dbName1, tableName1);
        Database db2 = new Database();
        db2.setName(dbName2);
        TestHiveMetaStore.client.createDatabase(db2);
        createTable(dbName2, tableName2);
        createMaterializedView(dbName2, mvName, Sets.newHashSet("db1.table1", "db2.table2"));
        boolean exceptionFound = false;
        try {
            // Cannot drop db1 because mv1 uses one of its tables
            // TODO: Error message coming from metastore is currently not very concise
            // (foreign key violation), we should make it easily understandable
            TestHiveMetaStore.client.dropDatabase(dbName1, true, true, true);
        } catch (Exception e) {
            exceptionFound = true;
        }
        Assert.assertTrue(exceptionFound);
        TestHiveMetaStore.client.dropDatabase(dbName2, true, true, true);
        TestHiveMetaStore.client.dropDatabase(dbName1, true, true, true);
    }

    @Test
    public void testDBLocationChange() throws IOException, TException {
        final String dbName = "alterDbLocation";
        String defaultUri = (MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/default_location.db";
        String newUri = (MetastoreConf.getVar(TestHiveMetaStore.conf, WAREHOUSE)) + "/new_location.db";
        new DatabaseBuilder().setName(dbName).setLocation(defaultUri).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
        Database db = TestHiveMetaStore.client.getDatabase(dbName);
        Assert.assertEquals("Incorrect default location of the database", TestHiveMetaStore.warehouse.getDnsPath(new Path(defaultUri)).toString(), db.getLocationUri());
        db.setLocationUri(newUri);
        TestHiveMetaStore.client.alterDatabase(dbName, db);
        db = TestHiveMetaStore.client.getDatabase(dbName);
        Assert.assertEquals("Incorrect new location of the database", TestHiveMetaStore.warehouse.getDnsPath(new Path(newUri)).toString(), db.getLocationUri());
        TestHiveMetaStore.client.dropDatabase(dbName);
        TestHiveMetaStore.silentDropDatabase(dbName);
    }

    @Test
    public void testRetriableClientWithConnLifetime() throws Exception {
        Configuration newConf = MetastoreConf.newMetastoreConf(new Configuration(this.conf));
        MetastoreConf.setTimeVar(newConf, CLIENT_SOCKET_LIFETIME, 4, TimeUnit.SECONDS);
        MetaStoreTestUtils.setConfForStandloneMode(newConf);
        long timeout = 5 * 1000;// Lets use a timeout more than the socket lifetime to simulate a reconnect

        // Test a normal retriable client
        IMetaStoreClient client = RetryingMetaStoreClient.getProxy(newConf, getHookLoader(), HiveMetaStoreClient.class.getName());
        client.getAllDatabases();
        client.close();
        // Connect after the lifetime, there should not be any failures
        client = RetryingMetaStoreClient.getProxy(newConf, getHookLoader(), HiveMetaStoreClient.class.getName());
        Thread.sleep(timeout);
        client.getAllDatabases();
        client.close();
    }

    @Test
    public void testJDOPersistanceManagerCleanup() throws Exception {
        if ((TestHiveMetaStore.isThriftClient) == false) {
            return;
        }
        int numObjectsBeforeClose = TestHiveMetaStore.getJDOPersistanceManagerCacheSize();
        HiveMetaStoreClient closingClient = new HiveMetaStoreClient(TestHiveMetaStore.conf);
        closingClient.getAllDatabases();
        closingClient.close();
        Thread.sleep((5 * 1000));// give HMS time to handle close request

        int numObjectsAfterClose = TestHiveMetaStore.getJDOPersistanceManagerCacheSize();
        Assert.assertTrue((numObjectsBeforeClose == numObjectsAfterClose));
        HiveMetaStoreClient nonClosingClient = new HiveMetaStoreClient(TestHiveMetaStore.conf);
        nonClosingClient.getAllDatabases();
        // Drop connection without calling close. HMS thread deleteContext
        // will trigger cleanup
        nonClosingClient.getTTransport().close();
        Thread.sleep((5 * 1000));
        int numObjectsAfterDroppedConnection = TestHiveMetaStore.getJDOPersistanceManagerCacheSize();
        Assert.assertTrue((numObjectsAfterClose == numObjectsAfterDroppedConnection));
    }

    @Test
    public void testValidateTableCols() throws Throwable {
        try {
            String dbName = "compdb";
            String tblName = "comptbl";
            TestHiveMetaStore.client.dropTable(dbName, tblName);
            TestHiveMetaStore.silentDropDatabase(dbName);
            new DatabaseBuilder().setName(dbName).setDescription("Validate Table Columns test").create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            Table tbl = new TableBuilder().setDbName(dbName).setTableName(tblName).addCol("name", STRING_TYPE_NAME).addCol("income", INT_TYPE_NAME).create(TestHiveMetaStore.client, TestHiveMetaStore.conf);
            if (TestHiveMetaStore.isThriftClient) {
                tbl = TestHiveMetaStore.client.getTable(dbName, tblName);
            }
            List<String> expectedCols = Lists.newArrayList();
            expectedCols.add("name");
            ObjectStore objStore = new ObjectStore();
            try {
                objStore.validateTableCols(tbl, expectedCols);
            } catch (MetaException ex) {
                throw new RuntimeException(ex);
            }
            expectedCols.add("doesntExist");
            boolean exceptionFound = false;
            try {
                objStore.validateTableCols(tbl, expectedCols);
            } catch (MetaException ex) {
                Assert.assertEquals(ex.getMessage(), "Column doesntExist doesn't exist in table comptbl in database compdb");
                exceptionFound = true;
            }
            Assert.assertTrue(exceptionFound);
        } catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            System.err.println("testValidateTableCols() failed.");
            throw e;
        }
    }

    @Test
    public void testGetMetastoreUuid() throws Throwable {
        String uuid = TestHiveMetaStore.client.getMetastoreDbUuid();
        Assert.assertNotNull(uuid);
    }

    @Test
    public void testGetUUIDInParallel() throws Exception {
        int numThreads = 5;
        int parallelCalls = 10;
        int numAPICallsPerThread = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<List<String>>> futures = new ArrayList<>();
        for (int n = 0; n < parallelCalls; n++) {
            futures.add(executorService.submit(new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    HiveMetaStoreClient testClient = new HiveMetaStoreClient(TestHiveMetaStore.conf);
                    List<String> uuids = new ArrayList<>(10);
                    for (int i = 0; i < numAPICallsPerThread; i++) {
                        String uuid = testClient.getMetastoreDbUuid();
                        uuids.add(uuid);
                    }
                    return uuids;
                }
            }));
        }
        String firstUUID = null;
        List<String> allUuids = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            for (String uuid : future.get()) {
                if (firstUUID == null) {
                    firstUUID = uuid;
                } else {
                    Assert.assertEquals(firstUUID.toLowerCase(), uuid.toLowerCase());
                }
                allUuids.add(uuid);
            }
        }
        int size = allUuids.size();
        Assert.assertEquals((numAPICallsPerThread * parallelCalls), size);
    }

    /**
     * While altering partition(s), verify DO NOT calculate partition statistics if
     * <ol>
     *   <li>table property DO_NOT_UPDATE_STATS is true</li>
     *   <li>STATS_AUTO_GATHER is false</li>
     *   <li>Is View</li>
     * </ol>
     */
    @Test
    public void testUpdatePartitionStat_doesNotUpdateStats() throws Exception {
        final String DB_NAME = "db1";
        final String TABLE_NAME = "tbl1";
        Table tbl = new TableBuilder().setDbName(DB_NAME).setTableName(TABLE_NAME).addCol("id", "int").addTableParam(DO_NOT_UPDATE_STATS, "true").build(null);
        List<String> vals = new ArrayList<>(2);
        vals.add("col1");
        vals.add("col2");
        Partition part = new Partition();
        part.setDbName(DB_NAME);
        part.setTableName(TABLE_NAME);
        part.setValues(vals);
        part.setParameters(new HashMap());
        part.setSd(tbl.getSd().deepCopy());
        part.getSd().setSerdeInfo(tbl.getSd().getSerdeInfo());
        part.getSd().setLocation(((tbl.getSd().getLocation()) + "/partCol=1"));
        Warehouse wh = Mockito.mock(Warehouse.class);
        // Execute initializeAddedPartition() and it should not trigger updatePartitionStatsFast() as DO_NOT_UPDATE_STATS is true
        HiveMetaStore.HMSHandler hms = new HiveMetaStore.HMSHandler("", TestHiveMetaStore.conf, false);
        Method m = hms.getClass().getDeclaredMethod("initializeAddedPartition", Table.class, Partition.class, boolean.class, EnvironmentContext.class);
        m.setAccessible(true);
        // Invoke initializeAddedPartition();
        m.invoke(hms, tbl, part, false, null);
        Mockito.verify(wh, Mockito.never()).getFileStatusesForLocation(part.getSd().getLocation());
        // Remove tbl's DO_NOT_UPDATE_STATS & set STATS_AUTO_GATHER = false
        tbl.unsetParameters();
        MetastoreConf.setBoolVar(TestHiveMetaStore.conf, STATS_AUTO_GATHER, false);
        m.invoke(hms, tbl, part, false, null);
        Mockito.verify(wh, Mockito.never()).getFileStatusesForLocation(part.getSd().getLocation());
        // Set STATS_AUTO_GATHER = true and set tbl as a VIRTUAL_VIEW
        MetastoreConf.setBoolVar(TestHiveMetaStore.conf, STATS_AUTO_GATHER, true);
        tbl.setTableType("VIRTUAL_VIEW");
        m.invoke(hms, tbl, part, false, null);
        Mockito.verify(wh, Mockito.never()).getFileStatusesForLocation(part.getSd().getLocation());
    }
}

