/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore;


import MetastoreConf.ConfVars.PARTITION_MANAGEMENT_CATALOG_NAME;
import MetastoreConf.ConfVars.PARTITION_MANAGEMENT_DATABASE_PATTERN;
import MetastoreConf.ConfVars.PARTITION_MANAGEMENT_TABLE_PATTERN;
import MetastoreConf.ConfVars.PARTITION_MANAGEMENT_TABLE_TYPES;
import PartitionManagementTask.DISCOVER_PARTITIONS_TBLPROPERTY;
import PartitionManagementTask.PARTITION_RETENTION_PERIOD_TBLPROPERTY;
import TableType.EXTERNAL_TABLE;
import TableType.MANAGED_TABLE;
import TransactionalValidationListener.INSERTONLY_TRANSACTIONAL_PROPERTY;
import com.google.common.collect.Lists;
import hive_metastoreConstants.TABLE_IS_TRANSACTIONAL;
import hive_metastoreConstants.TABLE_TRANSACTIONAL_PROPERTIES;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MetastoreUnitTest.class)
public class TestPartitionManagement {
    private IMetaStoreClient client;

    private Configuration conf;

    @Test
    public void testPartitionDiscoveryDisabledByDefault() throws IOException, TException {
        String dbName = "db1";
        String tableName = "tbl1";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        fs.mkdirs(new Path(tablePath, "state=WA/dt=2018-12-01"));
        fs.mkdirs(new Path(tablePath, "state=UT/dt=2018-12-02"));
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        // partition discovery is not enabled via table property, so nothing should change on this table
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        // table property is set to false, so no change expected
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "false");
        client.alter_table(dbName, tableName, table);
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
    }

    @Test
    public void testPartitionDiscoveryEnabledBothTableTypes() throws IOException, TException {
        String dbName = "db2";
        String tableName = "tbl2";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=2018-12-02");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        // table property is set to true, we expect 5 partitions
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        client.alter_table(dbName, tableName, table);
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(5, partitions.size());
        // change table type to external, delete a partition directory and make sure partition discovery works
        table.getParameters().put("EXTERNAL", "true");
        table.setTableType(EXTERNAL_TABLE.name());
        client.alter_table(dbName, tableName, table);
        boolean deleted = fs.delete(newPart1.getParent(), true);
        Assert.assertTrue(deleted);
        Assert.assertEquals(4, fs.listStatus(tablePath).length);
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
        // remove external tables from partition discovery and expect no changes even after partition is deleted
        conf.set(PARTITION_MANAGEMENT_TABLE_TYPES.getVarname(), MANAGED_TABLE.name());
        deleted = fs.delete(newPart2.getParent(), true);
        Assert.assertTrue(deleted);
        Assert.assertEquals(3, fs.listStatus(tablePath).length);
        // this doesn't remove partition because table is still external and we have remove external table type from
        // partition discovery
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
        // no table types specified, msck will not select any tables
        conf.set(PARTITION_MANAGEMENT_TABLE_TYPES.getVarname(), "");
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
        // only EXTERNAL table type, msck should drop a partition now
        conf.set(PARTITION_MANAGEMENT_TABLE_TYPES.getVarname(), EXTERNAL_TABLE.name());
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
    }

    @Test
    public void testPartitionDiscoveryNonDefaultCatalog() throws IOException, TException {
        String catName = "cat3";
        String dbName = "db3";
        String tableName = "tbl3";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(catName, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(catName, dbName, tableName);
        List<Partition> partitions = client.listPartitions(catName, dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=2018-12-02");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        client.alter_table(catName, dbName, tableName, table);
        // default catalog in conf is 'hive' but we are using 'cat3' as catName for this test, so msck should not fix
        // anything for this one
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(catName, dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        // using the correct catalog name, we expect msck to fix partitions
        conf.set(PARTITION_MANAGEMENT_CATALOG_NAME.getVarname(), catName);
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(catName, dbName, tableName, ((short) (-1)));
        Assert.assertEquals(5, partitions.size());
    }

    @Test
    public void testPartitionDiscoveryDBPattern() throws IOException, TException {
        String dbName = "db4";
        String tableName = "tbl4";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=2018-12-02");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        client.alter_table(dbName, tableName, table);
        // no match for this db pattern, so we will see only 3 partitions
        conf.set(PARTITION_MANAGEMENT_DATABASE_PATTERN.getVarname(), "*dbfoo*");
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        // matching db pattern, we will see all 5 partitions now
        conf.set(PARTITION_MANAGEMENT_DATABASE_PATTERN.getVarname(), "*db4*");
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(5, partitions.size());
    }

    @Test
    public void testPartitionDiscoveryTablePattern() throws IOException, TException {
        String dbName = "db5";
        String tableName = "tbl5";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=2018-12-02");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        client.alter_table(dbName, tableName, table);
        // no match for this table pattern, so we will see only 3 partitions
        conf.set(PARTITION_MANAGEMENT_TABLE_PATTERN.getVarname(), "*tblfoo*");
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        // matching table pattern, we will see all 5 partitions now
        conf.set(PARTITION_MANAGEMENT_TABLE_PATTERN.getVarname(), "tbl5*");
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(5, partitions.size());
    }

    @Test
    public void testPartitionDiscoveryTransactionalTable() throws IOException, InterruptedException, ExecutionException, TException {
        String dbName = "db6";
        String tableName = "tbl6";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, true);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=2018-12-02");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        table.getParameters().put(TABLE_IS_TRANSACTIONAL, "true");
        table.getParameters().put(TABLE_TRANSACTIONAL_PROPERTIES, INSERTONLY_TRANSACTIONAL_PROPERTY);
        client.alter_table(dbName, tableName, table);
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(5, partitions.size());
        // only one partition discovery task is running, there will be no skipped attempts
        Assert.assertEquals(0, PartitionManagementTask.getSkippedAttempts());
        // delete a partition from fs, and submit 3 tasks at the same time each of them trying to acquire X lock on the
        // same table, only one of them will run other attempts will be skipped
        boolean deleted = fs.delete(newPart1.getParent(), true);
        Assert.assertTrue(deleted);
        Assert.assertEquals(4, fs.listStatus(tablePath).length);
        // 3 tasks are submitted at the same time, only one will eventually lock the table and only one get to run at a time
        // This is to simulate, skipping partition discovery task attempt when previous attempt is still incomplete
        PartitionManagementTask partitionDiscoveryTask1 = new PartitionManagementTask();
        partitionDiscoveryTask1.setConf(conf);
        PartitionManagementTask partitionDiscoveryTask2 = new PartitionManagementTask();
        partitionDiscoveryTask2.setConf(conf);
        PartitionManagementTask partitionDiscoveryTask3 = new PartitionManagementTask();
        partitionDiscoveryTask3.setConf(conf);
        List<PartitionManagementTask> tasks = Lists.newArrayList(partitionDiscoveryTask1, partitionDiscoveryTask2, partitionDiscoveryTask3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        int successBefore = PartitionManagementTask.getCompletedAttempts();
        int skippedBefore = PartitionManagementTask.getSkippedAttempts();
        List<Future<?>> futures = new ArrayList<>();
        for (PartitionManagementTask task : tasks) {
            futures.add(executorService.submit(task));
        }
        for (Future<?> future : futures) {
            future.get();
        }
        int successAfter = PartitionManagementTask.getCompletedAttempts();
        int skippedAfter = PartitionManagementTask.getSkippedAttempts();
        Assert.assertEquals(1, (successAfter - successBefore));
        Assert.assertEquals(2, (skippedAfter - skippedBefore));
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
    }

    @Test
    public void testPartitionRetention() throws IOException, InterruptedException, TException {
        String dbName = "db7";
        String tableName = "tbl7";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=2018-12-02");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        table.getParameters().put(PARTITION_RETENTION_PERIOD_TBLPROPERTY, "20000ms");
        client.alter_table(dbName, tableName, table);
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(5, partitions.size());
        // after 30s all partitions should have been gone
        Thread.sleep((30 * 1000));
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(0, partitions.size());
    }

    @Test
    public void testPartitionDiscoverySkipInvalidPath() throws IOException, InterruptedException, TException {
        String dbName = "db8";
        String tableName = "tbl8";
        Map<String, TestPartitionManagement.Column> colMap = buildAllColumns();
        List<String> partKeys = Lists.newArrayList("state", "dt");
        List<String> partKeyTypes = Lists.newArrayList("string", "date");
        List<List<String>> partVals = Lists.newArrayList(Lists.newArrayList("__HIVE_DEFAULT_PARTITION__", "1990-01-01"), Lists.newArrayList("CA", "1986-04-28"), Lists.newArrayList("MN", "2018-11-31"));
        createMetadata(Warehouse.DEFAULT_CATALOG_NAME, dbName, tableName, partKeys, partKeyTypes, partVals, colMap, false);
        Table table = client.getTable(dbName, tableName);
        List<Partition> partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        String tableLocation = table.getSd().getLocation();
        URI location = URI.create(tableLocation);
        Path tablePath = new Path(location);
        FileSystem fs = FileSystem.get(location, conf);
        Path newPart1 = new Path(tablePath, "state=WA/dt=2018-12-01");
        Path newPart2 = new Path(tablePath, "state=UT/dt=");
        fs.mkdirs(newPart1);
        fs.mkdirs(newPart2);
        Assert.assertEquals(5, fs.listStatus(tablePath).length);
        table.getParameters().put(DISCOVER_PARTITIONS_TBLPROPERTY, "true");
        // empty retention period basically means disabled
        table.getParameters().put(PARTITION_RETENTION_PERIOD_TBLPROPERTY, "");
        client.alter_table(dbName, tableName, table);
        // there is one partition with invalid path which will get skipped
        runPartitionManagementTask(conf);
        partitions = client.listPartitions(dbName, tableName, ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
    }

    private static class Column {
        private String colName;

        private String colType;

        public Column(final String colName, final String colType) {
            this.colName = colName;
            this.colType = colType;
        }
    }
}

