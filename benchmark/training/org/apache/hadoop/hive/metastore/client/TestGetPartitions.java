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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
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
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * API tests for HMS client's getPartitions methods.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestGetPartitions extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "testpartdb";

    private static final String TABLE_NAME = "testparttable";

    public TestGetPartitions(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    /**
     * Testing getPartition(String,String,String) ->
     *         get_partition_by_name(String,String,String).
     */
    @Test
    public void testGetPartition() throws Exception {
        createTable3PartCols1Part(client);
        Partition partition = client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=16");
        TestCase.assertNotNull(partition);
        Assert.assertEquals(Lists.newArrayList("1997", "05", "16"), partition.getValues());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionCaseSensitive() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "YyYy=1997/mM=05/dD=16");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionIncompletePartName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05");
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionEmptyPartName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionNonexistingPart() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=99");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionNoDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition("", TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionNoTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, "", "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionNoTable() throws Exception {
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionNoDb() throws Exception {
        client.dropDatabase(TestGetPartitions.DB_NAME);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionNullDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(null, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionNullTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, null, "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionNullPartName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, ((String) (null)));
    }

    /**
     * Testing getPartition(String,String,List(String)) ->
     *         get_partition(String,String,List(String)).
     */
    @Test
    public void testGetPartitionByValues() throws Exception {
        createTable3PartCols1Part(client);
        List<String> parts = Lists.newArrayList("1997", "05", "16");
        Partition partition = client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, parts);
        TestCase.assertNotNull(partition);
        Assert.assertEquals(parts, partition.getValues());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionByValuesWrongPart() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "66"));
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionByValuesWrongNumOfPartVals() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05"));
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionByValuesEmptyPartVals() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionByValuesNoDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition("", TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionByValuesNoTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, "", Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionByValuesNoTable() throws Exception {
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionByValuesNoDb() throws Exception {
        client.dropDatabase(TestGetPartitions.DB_NAME);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionByValuesNullDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(null, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionByValuesNullTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, null, Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionByValuesNullValues() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartition(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, ((List<String>) (null)));
    }

    /**
     * Testing getPartitionsByNames(String,String,List(String)) ->
     *         get_partitions_by_names(String,String,List(String)).
     */
    @Test
    public void testGetPartitionsByNames() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        // TODO: partition names in getPartitionsByNames are not case insensitive
        List<Partition> partitions = client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("yYYy=2017/MM=11/DD=27", "yYyY=1999/mM=01/dD=02"));
        Assert.assertEquals(0, partitions.size());
        partitions = client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("yyyy=2017/mm=11/dd=27", "yyyy=1999/mm=01/dd=02"));
        Assert.assertEquals(2, partitions.size());
        Assert.assertEquals(testValues.get(0), partitions.get(0).getValues());
        Assert.assertEquals(testValues.get(3), partitions.get(1).getValues());
        partitions = client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("yyyy=2017", "yyyy=1999/mm=01/dd=02"));
        Assert.assertEquals(testValues.get(0), partitions.get(0).getValues());
    }

    @Test
    public void testGetPartitionsByNamesEmptyParts() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        List<Partition> partitions = client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("", ""));
        Assert.assertEquals(0, partitions.size());
        partitions = client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList());
        Assert.assertEquals(0, partitions.size());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionsByNamesNoDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartitionsByNames("", TestGetPartitions.TABLE_NAME, Lists.newArrayList("yyyy=2000/mm=01/dd=02"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionsByNamesNoTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartitionsByNames(TestGetPartitions.DB_NAME, "", Lists.newArrayList("yyyy=2000/mm=01/dd=02"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionsByNamesNoTable() throws Exception {
        client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("yyyy=2000/mm=01/dd=02"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionsByNamesNoDb() throws Exception {
        client.dropDatabase(TestGetPartitions.DB_NAME);
        client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("yyyy=2000/mm=01/dd=02"));
    }

    @Test
    public void testGetPartitionsByNamesNullDbName() throws Exception {
        try {
            createTable3PartCols1Part(client);
            client.getPartitionsByNames(null, TestGetPartitions.TABLE_NAME, Lists.newArrayList("yyyy=2000/mm=01/dd=02"));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testGetPartitionsByNamesNullTblName() throws Exception {
        try {
            createTable3PartCols1Part(client);
            client.getPartitionsByNames(TestGetPartitions.DB_NAME, null, Lists.newArrayList("yyyy=2000/mm=01/dd=02"));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionsByNamesNullNames() throws Exception {
        createTable3PartCols1Part(client);
        client.getPartitionsByNames(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, ((List<String>) (null)));
    }

    /**
     * Testing getPartitionWithAuthInfo(String,String,List(String),String,List(String)) ->
     *         get_partition_with_auth(String,String,List(String),String,List(String)).
     */
    @Test
    public void testGetPartitionWithAuthInfoNoPrivilagesSet() throws Exception {
        createTable3PartCols1Part(client);
        Partition partition = client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "", Lists.newArrayList());
        TestCase.assertNotNull(partition);
        TestCase.assertNull(partition.getPrivileges());
    }

    @Test
    public void testGetPartitionWithAuthInfo() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        Partition partition = client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "user0", Lists.newArrayList("group0"));
        TestCase.assertNotNull(partition);
        TestGetPartitions.assertAuthInfoReturned("user0", "group0", partition);
    }

    @Test
    public void testGetPartitionWithAuthInfoEmptyUserGroup() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        Partition partition = client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "", Lists.newArrayList(""));
        TestCase.assertNotNull(partition);
        TestGetPartitions.assertAuthInfoReturned("", "", partition);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionWithAuthInfoNoDbName() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo("", TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "user0", Lists.newArrayList("group0"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionWithAuthInfoNoTblName() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, "", Lists.newArrayList("1997", "05", "16"), "user0", Lists.newArrayList("group0"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionWithAuthInfoNoSuchPart() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "66"), "user0", Lists.newArrayList("group0"));
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionWithAuthInfoWrongNumOfPartVals() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05"), "user0", Lists.newArrayList("group0"));
    }

    @Test
    public void testGetPartitionWithAuthInfoNullDbName() throws Exception {
        try {
            createTable3PartCols1PartAuthOn(client);
            client.getPartitionWithAuthInfo(null, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "user0", Lists.newArrayList("group0"));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testGetPartitionWithAuthInfoNullTblName() throws Exception {
        try {
            createTable3PartCols1PartAuthOn(client);
            client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, null, Lists.newArrayList("1997", "05", "16"), "user0", Lists.newArrayList("group0"));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test(expected = MetaException.class)
    public void testGetPartitionWithAuthInfoNullValues() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, null, "user0", Lists.newArrayList("group0"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetPartitionWithAuthInfoNullUser() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, "", Lists.newArrayList("1997", "05", "16"), null, Lists.newArrayList("group0"));
    }

    @Test
    public void testGetPartitionWithAuthInfoNullGroups() throws Exception {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo(TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "user0", null);
    }

    @Test
    public void otherCatalog() throws TException {
        String catName = "get_partition_catalog";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "get_partition_database_in_other_catalog";
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String tableName = "table_in_other_catalog";
        Table table = new TableBuilder().inDb(db).setTableName(tableName).addCol("id", "int").addCol("name", "string").addPartCol("partcol", "string").addTableParam("PARTITION_LEVEL_PRIVILEGE", "true").create(client, metaStore.getConf());
        Partition[] parts = new Partition[5];
        for (int i = 0; i < (parts.length); i++) {
            parts[i] = new PartitionBuilder().inTable(table).addValue(("a" + i)).build(metaStore.getConf());
        }
        client.add_partitions(Arrays.asList(parts));
        Partition fetched = client.getPartition(catName, dbName, tableName, Collections.singletonList("a0"));
        Assert.assertEquals(catName, fetched.getCatName());
        Assert.assertEquals("a0", fetched.getValues().get(0));
        fetched = client.getPartition(catName, dbName, tableName, "partcol=a0");
        Assert.assertEquals(catName, fetched.getCatName());
        Assert.assertEquals("a0", fetched.getValues().get(0));
        List<Partition> fetchedParts = client.getPartitionsByNames(catName, dbName, tableName, Arrays.asList("partcol=a0", "partcol=a1"));
        Assert.assertEquals(2, fetchedParts.size());
        Set<String> vals = new java.util.HashSet(fetchedParts.size());
        for (Partition part : fetchedParts)
            vals.add(part.getValues().get(0));

        Assert.assertTrue(vals.contains("a0"));
        Assert.assertTrue(vals.contains("a1"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void getPartitionBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.getPartition("bogus", TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void getPartitionByNameBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.getPartition("bogus", TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, "yyyy=1997/mm=05/dd=16");
    }

    @Test(expected = NoSuchObjectException.class)
    public void getPartitionWithAuthBogusCatalog() throws TException {
        createTable3PartCols1PartAuthOn(client);
        client.getPartitionWithAuthInfo("bogus", TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Lists.newArrayList("1997", "05", "16"), "user0", Lists.newArrayList("group0"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void getPartitionsByNamesBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.getPartitionsByNames("bogus", TestGetPartitions.DB_NAME, TestGetPartitions.TABLE_NAME, Collections.singletonList("yyyy=1997/mm=05/dd=16"));
    }
}

