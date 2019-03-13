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


import MetastoreConf.ConfVars.TRY_DIRECT_SQL;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.MetaStoreTestUtils;
import org.apache.hadoop.hive.metastore.annotation.MetastoreCheckinTest;
import org.apache.hadoop.hive.metastore.api.Catalog;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionValuesRequest;
import org.apache.hadoop.hive.metastore.api.PartitionValuesResponse;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.client.builder.CatalogBuilder;
import org.apache.hadoop.hive.metastore.client.builder.DatabaseBuilder;
import org.apache.hadoop.hive.metastore.client.builder.PartitionBuilder;
import org.apache.hadoop.hive.metastore.client.builder.TableBuilder;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.hadoop.hive.metastore.minihms.AbstractMetaStoreService;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * API tests for HMS client's listPartitions methods.
 */
@RunWith(Parameterized.class)
@Category(MetastoreCheckinTest.class)
public class TestListPartitions extends MetaStoreClientTest {
    private AbstractMetaStoreService metaStore;

    private IMetaStoreClient client;

    private static final String DB_NAME = "testpartdb";

    private static final String TABLE_NAME = "testparttable";

    public TestListPartitions(String name, AbstractMetaStoreService metaStore) {
        this.metaStore = metaStore;
    }

    /**
     * Testing listPartitions(String,String,short) ->
     *         get_partitions(String,String,short).
     */
    @Test
    public void testListPartitionsAll() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        List<Partition> partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues);
        partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(0, 1));
        partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (0)));
        TestCase.assertTrue(partitions.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsAllHighMaxParts() throws Exception {
        createTable3PartCols1Part(client);
        List<Partition> partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (101)));
        TestCase.assertTrue(partitions.isEmpty());
    }

    @Test
    public void testListPartitionsAllNoParts() throws Exception {
        createTestTable(client, TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("yyyy", "mm", "dd"));
        List<Partition> partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
        TestCase.assertTrue(partitions.isEmpty());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsAllNoTable() throws Exception {
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsAllNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsAllNoDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions("", TestListPartitions.TABLE_NAME, ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsAllNoTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(TestListPartitions.DB_NAME, "", ((short) (-1)));
    }

    @Test
    public void testListPartitionsAllNullTblName() throws Exception {
        try {
            createTable3PartCols1Part(client);
            client.listPartitions(TestListPartitions.DB_NAME, ((String) (null)), ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionsAllNullDbName() throws Exception {
        try {
            createTable3PartCols1Part(client);
            client.listPartitions(null, TestListPartitions.TABLE_NAME, ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    /**
     * Testing listPartitions(String,String,List(String),short) ->
     *         get_partitions(String,String,List(String),short).
     */
    @Test
    public void testListPartitionsByValues() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        List<Partition> partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)));
        Assert.assertEquals(2, partitions.size());
        Assert.assertEquals(testValues.get(2), partitions.get(0).getValues());
        Assert.assertEquals(testValues.get(3), partitions.get(1).getValues());
        partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11"), ((short) (-1)));
        Assert.assertEquals(1, partitions.size());
        Assert.assertEquals(testValues.get(3), partitions.get(0).getValues());
        partitions = client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("20177", "11"), ((short) (-1)));
        Assert.assertEquals(0, partitions.size());
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByValuesNoVals() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList(), ((short) (-1)));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByValuesTooManyVals() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("0", "1", "2", "3"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByValuesNoDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions("", TestListPartitions.TABLE_NAME, Lists.newArrayList("1999"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByValuesNoTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(TestListPartitions.DB_NAME, "", Lists.newArrayList("1999"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByValuesNoTable() throws Exception {
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("1999"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByValuesNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("1999"), ((short) (-1)));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByValuesNullDbName() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(null, TestListPartitions.TABLE_NAME, Lists.newArrayList("1999"), ((short) (-1)));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByValuesNullTblName() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(TestListPartitions.DB_NAME, null, Lists.newArrayList("1999"), ((short) (-1)));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByValuesNullValues() throws Exception {
        createTable3PartCols1Part(client);
        client.listPartitions(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((List<String>) (null)), ((short) (-1)));
    }

    /**
     * Testing listPartitionSpecs(String,String,int) ->
     *         get_partitions_pspec(String,String,int).
     */
    @Test
    public void testListPartitionSpecs() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        PartitionSpecProxy partSpecProxy = client.listPartitionSpecs(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, (-1));
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, testValues);
        partSpecProxy = client.listPartitionSpecs(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, 2);
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, testValues.subList(0, 2));
        partSpecProxy = client.listPartitionSpecs(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, 0);
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, testValues.subList(0, 0));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsNoTable() throws Exception {
        client.listPartitionSpecs(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionSpecs(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, (-1));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionSpecsHighMaxParts() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecs(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, 101);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecs("", TestListPartitions.TABLE_NAME, (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecs(TestListPartitions.DB_NAME, "", (-1));
    }

    @Test
    public void testListPartitionSpecsNullDbName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionSpecs(null, TestListPartitions.TABLE_NAME, (-1));
            Assert.fail("Should have thrown exception");
        } catch (MetaException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionSpecsNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionSpecs(TestListPartitions.DB_NAME, null, (-1));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    /**
     * Testing listPartitionsWithAuthInfo(String,String,short,String,List(String)) ->
     *         get_partitions_with_auth(String,String,short,String,List(String)).
     */
    @Test
    public void testListPartitionsWithAuth() throws Exception {
        List<List<String>> partValues = createTable4PartColsPartsAuthOn(client);
        String user = "user0";
        List<String> groups = Lists.newArrayList("group0");
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)), user, groups);
        Assert.assertEquals(4, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues);
        partitions.forEach(( partition) -> assertAuthInfoReturned(user, groups.get(0), partition));
        partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (2)), user, groups);
        Assert.assertEquals(2, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(0, 2));
        partitions.forEach(( partition) -> assertAuthInfoReturned(user, groups.get(0), partition));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsWithAuthHighMaxParts() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (101)), "", Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthNoPrivilegesSet() throws Exception {
        List<List<String>> partValues = createTable4PartColsParts(client);
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)), "", Lists.newArrayList());
        Assert.assertEquals(4, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues);
        partitions.forEach(( partition) -> assertNull(partition.getPrivileges()));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo("", TestListPartitions.TABLE_NAME, ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, "", ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthNoTable() throws Exception {
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsWithAuthNullDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo(null, TestListPartitions.TABLE_NAME, ((short) (-1)), "", Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, ((String) (null)), ((short) (-1)), "", Lists.newArrayList());
            Assert.fail("Should have thrown exception");
        } catch (MetaException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionsWithAuthNullUser() throws Exception {
        createTable4PartColsPartsAuthOn(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)), null, Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthNullGroup() throws Exception {
        createTable4PartColsPartsAuthOn(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)), "user0", null);
    }

    /**
     * Testing listPartitionsWithAuthInfo(String,String,List(String),short,String,List(String)) ->
     *         get_partitions_ps_with_auth(String,String,List(String),short,String,List(String)).
     */
    @Test
    public void testListPartitionsWithAuthByValues() throws Exception {
        List<List<String>> partValues = createTable4PartColsPartsAuthOn(client);
        String user = "user0";
        List<String> groups = Lists.newArrayList("group0");
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), user, groups);
        Assert.assertEquals(1, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(3, 4));
        partitions.forEach(( partition) -> assertAuthInfoReturned(user, groups.get(0), partition));
        partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)), user, groups);
        Assert.assertEquals(2, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(2, 4));
        partitions.forEach(( partition) -> assertAuthInfoReturned(user, groups.get(0), partition));
        partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (1)), user, groups);
        Assert.assertEquals(1, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(2, 3));
        partitions.forEach(( partition) -> assertAuthInfoReturned(user, groups.get(0), partition));
        partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2013"), ((short) (-1)), user, groups);
        TestCase.assertTrue(partitions.isEmpty());
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsWithAuthByValuesNoVals() throws Exception {
        createTable4PartColsPartsAuthOn(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList(), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsWithAuthByValuesTooManyVals() throws Exception {
        createTable4PartColsPartsAuthOn(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("0", "1", "2", "3"), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthByValuesHighMaxParts() throws Exception {
        List<List<String>> partValues = createTable4PartColsParts(client);
        // This doesn't throw MetaException when setting to high max part count
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (101)), "", Lists.newArrayList());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(2, 4));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsWithAuthByValuesTooManyValsHighMaxParts() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("0", "1", "2", "3"), ((short) (101)), "", Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthByValuesNoPrivilegesSet() throws Exception {
        List<List<String>> partValues = createTable4PartColsPartsAuthOn(client);
        String user = "user0";
        List<String> groups = Lists.newArrayList("group0");
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), user, groups);
        Assert.assertEquals(1, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(3, 4));
        partitions.forEach(( partition) -> assertAuthInfoReturned(user, groups.get(0), partition));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthByValuesNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo("", TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthByValuesNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, "", Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthByValuesNoTable() throws Exception {
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsWithAuthByValuesNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthByValuesNullDbName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionsWithAuthInfo(null, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", Lists.newArrayList());
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionsWithAuthByValuesNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, null, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", Lists.newArrayList());
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsWithAuthByValuesNullValues() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((List<String>) (null)), ((short) (-1)), "", Lists.newArrayList());
    }

    @Test
    public void testListPartitionsWithAuthByValuesNullUser() throws Exception {
        List<List<String>> partValues = createTable4PartColsPartsAuthOn(client);
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), null, Lists.newArrayList());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(3, 4));
    }

    @Test
    public void testListPartitionsWithAuthByValuesNullGroup() throws Exception {
        List<List<String>> partValues = createTable4PartColsPartsAuthOn(client);
        List<Partition> partitions = client.listPartitionsWithAuthInfo(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "11", "27"), ((short) (-1)), "", null);
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(3, 4));
    }

    /**
     * Testing listPartitionsByFilter(String,String,String,short) ->
     *         get_partitions_by_filter(String,String,String,short).
     */
    @Test
    public void testListPartitionsByFilter() throws Exception {
        List<List<String>> partValues = createTable4PartColsParts(client);
        List<Partition> partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""), ((short) (-1)));
        Assert.assertEquals(3, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(1, 4));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""), ((short) (2)));
        Assert.assertEquals(2, partitions.size());
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, partValues.subList(1, 3));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""), ((short) (0)));
        TestCase.assertTrue(partitions.isEmpty());
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\" AND mm=\"99\"", ((short) (-1)));
        TestCase.assertTrue(partitions.isEmpty());
    }

    @Test
    public void testListPartitionsByFilterCaseInsensitive() throws Exception {
        String tableName = (TestListPartitions.TABLE_NAME) + "_caseinsensitive";
        Table t = createTestTable(client, TestListPartitions.DB_NAME, tableName, Lists.newArrayList("yyyy", "month", "day"), false);
        List<List<String>> testValues = Lists.newArrayList(Lists.newArrayList("2017", "march", "11"), Lists.newArrayList("2017", "march", "15"), Lists.newArrayList("2017", "may", "15"), Lists.newArrayList("2018", "march", "11"), Lists.newArrayList("2018", "september", "7"));
        for (List<String> vals : testValues) {
            addPartition(client, t, vals);
        }
        List<Partition> partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "yYyY=\"2017\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(0, 3));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "yYyY=\"2017\" AND mOnTh=\"may\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(2, 3));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "yYyY!=\"2017\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(3, 5));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "mOnTh=\"september\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(4, 5));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "mOnTh like \"m.*\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(0, 4));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "yYyY=\"2018\" AND mOnTh like \"m.*\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(3, 4));
        client.dropTable(TestListPartitions.DB_NAME, tableName);
    }

    @Test
    public void testListPartitionsByFilterCaseSensitive() throws Exception {
        String tableName = (TestListPartitions.TABLE_NAME) + "_casesensitive";
        Table t = createTestTable(client, TestListPartitions.DB_NAME, tableName, Lists.newArrayList("yyyy", "month", "day"), false);
        List<List<String>> testValues = Lists.newArrayList(Lists.newArrayList("2017", "march", "11"), Lists.newArrayList("2017", "march", "15"), Lists.newArrayList("2017", "may", "15"), Lists.newArrayList("2018", "march", "11"), Lists.newArrayList("2018", "april", "7"));
        for (List<String> vals : testValues) {
            addPartition(client, t, vals);
        }
        List<Partition> partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "month=\"mArCh\"", ((short) (-1)));
        Assert.assertTrue(partitions.isEmpty());
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "yyyy=\"2017\" AND month=\"May\"", ((short) (-1)));
        Assert.assertTrue(partitions.isEmpty());
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "yyyy=\"2017\" AND month!=\"mArCh\"", ((short) (-1)));
        TestListPartitions.assertPartitionsHaveCorrectValues(partitions, testValues.subList(0, 3));
        partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, tableName, "month like \"M.*\"", ((short) (-1)));
        Assert.assertTrue(partitions.isEmpty());
        client.dropTable(TestListPartitions.DB_NAME, tableName);
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByFilterInvalidFilter() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyy=\"2017\"", ((short) (101)));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionsByFilterHighMaxParts() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", ((short) (101)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByFilterNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsByFilter(TestListPartitions.DB_NAME, "", "yyyy=\"2017\"", ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByFilterNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionsByFilter("", TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByFilterNoTable() throws Exception {
        client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionsByFilterNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", ((short) (-1)));
    }

    @Test
    public void testListPartitionsByFilterNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionsByFilter(TestListPartitions.DB_NAME, null, "yyyy=\"2017\"", ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionsByFilterNullDbName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionsByFilter(null, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionsByFilterNullFilter() throws Exception {
        createTable4PartColsParts(client);
        List<Partition> partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, null, ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
    }

    @Test
    public void testListPartitionsByFilterEmptyFilter() throws Exception {
        createTable4PartColsParts(client);
        List<Partition> partitions = client.listPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "", ((short) (-1)));
        Assert.assertEquals(4, partitions.size());
    }

    /**
     * Testing listPartitionSpecsByFilter(String,String,String,int) ->
     *         get_part_specs_by_filter(String,String,String,int).
     */
    @Test
    public void testListPartitionsSpecsByFilter() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        PartitionSpecProxy partSpecProxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""), (-1));
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, testValues.subList(1, 4));
        partSpecProxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""), 2);
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, testValues.subList(1, 3));
        partSpecProxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""), 0);
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, Lists.newArrayList());
        partSpecProxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"20177\"", (-1));
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, Lists.newArrayList());
        // HIVE-18977
        if (MetastoreConf.getBoolVar(metaStore.getConf(), TRY_DIRECT_SQL)) {
            partSpecProxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yYyY=\"2017\"", (-1));
            TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, testValues.subList(2, 4));
        }
        partSpecProxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\" AND mm=\"99\"", (-1));
        TestListPartitions.assertPartitionsSpecProxy(partSpecProxy, Lists.newArrayList());
    }

    @Test(expected = MetaException.class)
    public void testListPartitionSpecsByFilterInvalidFilter() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyy=\"2017\"", 101);
    }

    @Test(expected = MetaException.class)
    public void testListPartitionSpecsByFilterHighMaxParts() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", 101);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsByFilterNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, "", "yyyy=\"2017\"", (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsByFilterNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecsByFilter("", TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsByFilterNoTable() throws Exception {
        client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionSpecsByFilterNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", (-1));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionSpecsByFilterNullTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, null, "yyyy=\"2017\"", (-1));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionSpecsByFilterNullDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionSpecsByFilter(null, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"", (-1));
    }

    @Test
    public void testListPartitionSpecsByFilterNullFilter() throws Exception {
        List<List<String>> values = createTable4PartColsParts(client);
        PartitionSpecProxy pproxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, null, (-1));
        TestListPartitions.assertPartitionsSpecProxy(pproxy, values);
    }

    @Test
    public void testListPartitionSpecsByFilterEmptyFilter() throws Exception {
        List<List<String>> values = createTable4PartColsParts(client);
        PartitionSpecProxy pproxy = client.listPartitionSpecsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "", (-1));
        TestListPartitions.assertPartitionsSpecProxy(pproxy, values);
    }

    /**
     * Testing getNumPartitionsByFilter(String,String,String) ->
     *         get_num_partitions_by_filter(String,String,String).
     */
    @Test
    public void testGetNumPartitionsByFilter() throws Exception {
        createTable4PartColsParts(client);
        int n = client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ("yyyy=\"2017\" OR " + "mm=\"02\""));
        Assert.assertEquals(3, n);
        n = client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "");
        Assert.assertEquals(4, n);
        n = client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"20177\"");
        Assert.assertEquals(0, n);
        // HIVE-18977
        if (MetastoreConf.getBoolVar(metaStore.getConf(), TRY_DIRECT_SQL)) {
            n = client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yYyY=\"2017\"");
            Assert.assertEquals(2, n);
        }
        n = client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\" AND mm=\"99\"");
        Assert.assertEquals(0, n);
    }

    @Test(expected = MetaException.class)
    public void testGetNumPartitionsByFilterInvalidFilter() throws Exception {
        createTable4PartColsParts(client);
        client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyy=\"2017\"");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetNumPartitionsByFilterNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, "", "yyyy=\"2017\"");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetNumPartitionsByFilterNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.getNumPartitionsByFilter("", TestListPartitions.TABLE_NAME, "yyyy=\"2017\"");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetNumPartitionsByFilterNoTable() throws Exception {
        client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"");
    }

    @Test(expected = NoSuchObjectException.class)
    public void testGetNumPartitionsByFilterNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"");
    }

    @Test(expected = MetaException.class)
    public void testGetNumPartitionsByFilterNullTblName() throws Exception {
        createTable4PartColsParts(client);
        client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, null, "yyyy=\"2017\"");
    }

    @Test(expected = MetaException.class)
    public void testGetNumPartitionsByFilterNullDbName() throws Exception {
        createTable4PartColsParts(client);
        client.getNumPartitionsByFilter(null, TestListPartitions.TABLE_NAME, "yyyy=\"2017\"");
    }

    @Test
    public void testGetNumPartitionsByFilterNullFilter() throws Exception {
        createTable4PartColsParts(client);
        int n = client.getNumPartitionsByFilter(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, null);
        Assert.assertEquals(4, n);
    }

    /**
     * Testing listPartitionNames(String,String,short) ->
     *         get_partition_names(String,String,short).
     */
    @Test
    public void testListPartitionNames() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        List<String> partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues, Lists.newArrayList("yyyy", "mm", "dd"));
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (2)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues.subList(0, 2), Lists.newArrayList("yyyy", "mm", "dd"));
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (0)));
        TestCase.assertTrue(partitionNames.isEmpty());
        // This method does not depend on MetastoreConf.LIMIT_PARTITION_REQUEST setting:
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (101)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues, Lists.newArrayList("yyyy", "mm", "dd"));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames("", TestListPartitions.TABLE_NAME, ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames(TestListPartitions.DB_NAME, "", ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesNoTable() throws Exception {
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((short) (-1)));
    }

    @Test
    public void testListPartitionNamesNullDbName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionNames(null, TestListPartitions.TABLE_NAME, ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionNamesNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionNames(TestListPartitions.DB_NAME, ((String) (null)), ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    /**
     * Testing listPartitionNames(String,String,List(String),short) ->
     *         get_partition_names_ps(String,String,List(String),short).
     */
    @Test
    public void testListPartitionNamesByValues() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        List<String> partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues.subList(2, 4), Lists.newArrayList("yyyy", "mm", "dd"));
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (101)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues.subList(2, 4), Lists.newArrayList("yyyy", "mm", "dd"));
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (1)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues.subList(2, 3), Lists.newArrayList("yyyy", "mm", "dd"));
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (0)));
        TestCase.assertTrue(partitionNames.isEmpty());
        partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017", "10"), ((short) (-1)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues.subList(2, 3), Lists.newArrayList("yyyy", "mm", "dd"));
    }

    @Test
    public void testListPartitionNamesByValuesMaxPartCountUnlimited() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        // TODO: due to value 101 this probably should throw an exception
        List<String> partitionNames = client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (101)));
        TestListPartitions.assertCorrectPartitionNames(partitionNames, testValues.subList(2, 4), Lists.newArrayList("yyyy", "mm", "dd"));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionNamesByValuesNoPartVals() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList(), ((short) (-1)));
    }

    @Test(expected = MetaException.class)
    public void testListPartitionNamesByValuesTooManyVals() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("1", "2", "3", "4"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesByValuesNoDbName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames("", TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesByValuesNoTblName() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames(TestListPartitions.DB_NAME, "", Lists.newArrayList("2017"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesByValuesNoTable() throws Exception {
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)));
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionNamesByValuesNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)));
    }

    @Test
    public void testListPartitionNamesByValuesNullDbName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionNames(null, TestListPartitions.TABLE_NAME, Lists.newArrayList("2017"), ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionNamesByValuesNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionNames(TestListPartitions.DB_NAME, null, Lists.newArrayList("2017"), ((short) (-1)));
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test(expected = MetaException.class)
    public void testListPartitionNamesByValuesNullValues() throws Exception {
        createTable4PartColsParts(client);
        client.listPartitionNames(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, ((List<String>) (null)), ((short) (-1)));
    }

    /**
     * Testing listPartitionValues(PartitionValuesRequest) ->
     *         get_partition_values(PartitionValuesRequest).
     */
    @Test
    public void testListPartitionValues() throws Exception {
        List<List<String>> testValues = createTable4PartColsParts(client);
        List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
        PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, partitionSchema);
        PartitionValuesResponse response = client.listPartitionValues(request);
        TestListPartitions.assertCorrectPartitionValuesResponse(testValues, response);
    }

    @Test
    public void testListPartitionValuesEmptySchema() throws Exception {
        try {
            List<List<String>> testValues = createTable4PartColsParts(client);
            List<FieldSchema> partitionSchema = Lists.newArrayList();
            PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, partitionSchema);
            client.listPartitionValues(request);
            Assert.fail("Should have thrown exception");
        } catch (IndexOutOfBoundsException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionValuesNoDbName() throws Exception {
        createTable4PartColsParts(client);
        List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
        PartitionValuesRequest request = new PartitionValuesRequest("", TestListPartitions.TABLE_NAME, partitionSchema);
        client.listPartitionValues(request);
    }

    @Test(expected = NoSuchObjectException.class)
    public void testListPartitionValuesNoTblName() throws Exception {
        createTable4PartColsParts(client);
        List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
        PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, "", partitionSchema);
        client.listPartitionValues(request);
    }

    @Test(expected = MetaException.class)
    public void testListPartitionValuesNoTable() throws Exception {
        List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
        PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, partitionSchema);
        client.listPartitionValues(request);
    }

    @Test(expected = MetaException.class)
    public void testListPartitionValuesNoDb() throws Exception {
        client.dropDatabase(TestListPartitions.DB_NAME);
        List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
        PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, partitionSchema);
        client.listPartitionValues(request);
    }

    @Test
    public void testListPartitionValuesNullDbName() throws Exception {
        try {
            createTable4PartColsParts(client);
            List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
            PartitionValuesRequest request = new PartitionValuesRequest(null, TestListPartitions.TABLE_NAME, partitionSchema);
            client.listPartitionValues(request);
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TProtocolException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionValuesNullTblName() throws Exception {
        try {
            createTable4PartColsParts(client);
            List<FieldSchema> partitionSchema = Lists.newArrayList(new FieldSchema("yyyy", "string", ""), new FieldSchema("mm", "string", ""));
            PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, null, partitionSchema);
            client.listPartitionValues(request);
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TProtocolException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionValuesNullSchema() throws Exception {
        try {
            createTable4PartColsParts(client);
            PartitionValuesRequest request = new PartitionValuesRequest(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, null);
            client.listPartitionValues(request);
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TProtocolException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void testListPartitionValuesNullRequest() throws Exception {
        try {
            createTable4PartColsParts(client);
            client.listPartitionValues(null);
            Assert.fail("Should have thrown exception");
        } catch (NullPointerException | TTransportException e) {
            // TODO: should not throw different exceptions for different HMS deployment types
        }
    }

    @Test
    public void otherCatalog() throws TException {
        String catName = "list_partition_catalog";
        Catalog cat = new CatalogBuilder().setName(catName).setLocation(MetaStoreTestUtils.getTestWarehouseDir(catName)).build();
        client.createCatalog(cat);
        String dbName = "list_partition_database_in_other_catalog";
        Database db = new DatabaseBuilder().setName(dbName).setCatalogName(catName).create(client, metaStore.getConf());
        String tableName = "table_in_other_catalog";
        Table table = new TableBuilder().inDb(db).setTableName(tableName).addCol("id", "int").addCol("name", "string").addPartCol("partcol", "string").create(client, metaStore.getConf());
        Partition[] parts = new Partition[5];
        for (int i = 0; i < (parts.length); i++) {
            parts[i] = new PartitionBuilder().inTable(table).addValue(("a" + i)).build(metaStore.getConf());
        }
        client.add_partitions(Arrays.asList(parts));
        List<Partition> fetched = client.listPartitions(catName, dbName, tableName, (-1));
        Assert.assertEquals(parts.length, fetched.size());
        Assert.assertEquals(catName, fetched.get(0).getCatName());
        fetched = client.listPartitions(catName, dbName, tableName, Collections.singletonList("a0"), (-1));
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(catName, fetched.get(0).getCatName());
        PartitionSpecProxy proxy = client.listPartitionSpecs(catName, dbName, tableName, (-1));
        Assert.assertEquals(parts.length, proxy.size());
        Assert.assertEquals(catName, proxy.getCatName());
        fetched = client.listPartitionsByFilter(catName, dbName, tableName, "partcol=\"a0\"", (-1));
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals(catName, fetched.get(0).getCatName());
        proxy = client.listPartitionSpecsByFilter(catName, dbName, tableName, "partcol=\"a0\"", (-1));
        Assert.assertEquals(1, proxy.size());
        Assert.assertEquals(catName, proxy.getCatName());
        Assert.assertEquals(1, client.getNumPartitionsByFilter(catName, dbName, tableName, "partcol=\"a0\""));
        List<String> names = client.listPartitionNames(catName, dbName, tableName, 57);
        Assert.assertEquals(parts.length, names.size());
        names = client.listPartitionNames(catName, dbName, tableName, Collections.singletonList("a0"), ((Short.MAX_VALUE) + 1));
        Assert.assertEquals(1, names.size());
        PartitionValuesRequest rqst = new PartitionValuesRequest(dbName, tableName, Lists.newArrayList(new FieldSchema("partcol", "string", "")));
        rqst.setCatName(catName);
        PartitionValuesResponse rsp = client.listPartitionValues(rqst);
        Assert.assertEquals(5, rsp.getPartitionValuesSize());
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionsBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitions("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionsWithPartialValuesBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitions("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Collections.singletonList("a0"), (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionsSpecsBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitionSpecs("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionsByFilterBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitionsByFilter("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "partcol=\"a0\"", (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionSpecsByFilterBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitionSpecsByFilter("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "partcol=\"a0\"", (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void getNumPartitionsByFilterBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.getNumPartitionsByFilter("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, "partcol=\"a0\"");
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionNamesBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitionNames("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, (-1));
    }

    @Test(expected = NoSuchObjectException.class)
    public void listPartitionNamesPartialValsBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        client.listPartitionNames("bogus", TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Collections.singletonList("a0"), (-1));
    }

    @Test(expected = MetaException.class)
    public void listPartitionValuesBogusCatalog() throws TException {
        createTable3PartCols1Part(client);
        PartitionValuesRequest rqst = new PartitionValuesRequest(TestListPartitions.DB_NAME, TestListPartitions.TABLE_NAME, Lists.newArrayList(new FieldSchema("partcol", "string", "")));
        rqst.setCatName("bogus");
        client.listPartitionValues(rqst);
    }
}

