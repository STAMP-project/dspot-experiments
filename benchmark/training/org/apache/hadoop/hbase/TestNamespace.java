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
package org.apache.hadoop.hbase;


import NamespaceDescriptor.DEFAULT_NAMESPACE;
import NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR;
import NamespaceDescriptor.SYSTEM_NAMESPACE;
import NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HConstants.BASE_NAMESPACE_DIR;


@Category({ MiscTests.class, MediumTests.class })
public class TestNamespace {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNamespace.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestNamespace.class);

    private static HMaster master;

    protected static final int NUM_SLAVES_BASE = 4;

    private static HBaseTestingUtility TEST_UTIL;

    protected static Admin admin;

    protected static HBaseCluster cluster;

    private String prefix = "TestNamespace";

    @Rule
    public TestName name = new TestName();

    @Test
    public void verifyReservedNS() throws IOException {
        // verify existence of reserved namespaces
        NamespaceDescriptor ns = TestNamespace.admin.getNamespaceDescriptor(DEFAULT_NAMESPACE.getName());
        Assert.assertNotNull(ns);
        Assert.assertEquals(ns.getName(), DEFAULT_NAMESPACE.getName());
        ns = TestNamespace.admin.getNamespaceDescriptor(SYSTEM_NAMESPACE.getName());
        Assert.assertNotNull(ns);
        Assert.assertEquals(ns.getName(), SYSTEM_NAMESPACE.getName());
        Assert.assertEquals(2, TestNamespace.admin.listNamespaceDescriptors().length);
        // verify existence of system tables
        Set<TableName> systemTables = Sets.newHashSet(META_TABLE_NAME);
        HTableDescriptor[] descs = TestNamespace.admin.listTableDescriptorsByNamespace(SYSTEM_NAMESPACE.getName());
        Assert.assertEquals(systemTables.size(), descs.length);
        for (HTableDescriptor desc : descs) {
            Assert.assertTrue(systemTables.contains(desc.getTableName()));
        }
        // verify system tables aren't listed
        Assert.assertEquals(0, TestNamespace.admin.listTables().length);
        // Try creating default and system namespaces.
        boolean exceptionCaught = false;
        try {
            TestNamespace.admin.createNamespace(DEFAULT_NAMESPACE);
        } catch (IOException exp) {
            TestNamespace.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
        exceptionCaught = false;
        try {
            TestNamespace.admin.createNamespace(SYSTEM_NAMESPACE);
        } catch (IOException exp) {
            TestNamespace.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testDeleteReservedNS() throws Exception {
        boolean exceptionCaught = false;
        try {
            TestNamespace.admin.deleteNamespace(DEFAULT_NAMESPACE_NAME_STR);
        } catch (IOException exp) {
            TestNamespace.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
        try {
            TestNamespace.admin.deleteNamespace(SYSTEM_NAMESPACE_NAME_STR);
        } catch (IOException exp) {
            TestNamespace.LOG.warn(exp.toString(), exp);
            exceptionCaught = true;
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void createRemoveTest() throws Exception {
        String nsName = ((prefix) + "_") + (name.getMethodName());
        TestNamespace.LOG.info(name.getMethodName());
        // create namespace and verify
        TestNamespace.admin.createNamespace(NamespaceDescriptor.create(nsName).build());
        Assert.assertEquals(3, TestNamespace.admin.listNamespaceDescriptors().length);
        // remove namespace and verify
        TestNamespace.admin.deleteNamespace(nsName);
        Assert.assertEquals(2, TestNamespace.admin.listNamespaceDescriptors().length);
    }

    @Test
    public void createDoubleTest() throws IOException, InterruptedException {
        String nsName = ((prefix) + "_") + (name.getMethodName());
        TestNamespace.LOG.info(name.getMethodName());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final TableName tableNameFoo = TableName.valueOf(((nsName + ":") + (name.getMethodName())));
        // create namespace and verify
        TestNamespace.admin.createNamespace(NamespaceDescriptor.create(nsName).build());
        TestNamespace.TEST_UTIL.createTable(tableName, Bytes.toBytes(nsName));
        TestNamespace.TEST_UTIL.createTable(tableNameFoo, Bytes.toBytes(nsName));
        Assert.assertEquals(2, TestNamespace.admin.listTables().length);
        Assert.assertNotNull(TestNamespace.admin.getTableDescriptor(tableName));
        Assert.assertNotNull(TestNamespace.admin.getTableDescriptor(tableNameFoo));
        // remove namespace and verify
        TestNamespace.admin.disableTable(tableName);
        TestNamespace.admin.deleteTable(tableName);
        Assert.assertEquals(1, TestNamespace.admin.listTables().length);
    }

    @Test
    public void createTableTest() throws IOException, InterruptedException {
        String nsName = ((prefix) + "_") + (name.getMethodName());
        TestNamespace.LOG.info(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(((nsName + ":") + (name.getMethodName()))));
        HColumnDescriptor colDesc = new HColumnDescriptor("my_cf");
        desc.addFamily(colDesc);
        try {
            TestNamespace.admin.createTable(desc);
            Assert.fail("Expected no namespace exists exception");
        } catch (NamespaceNotFoundException ex) {
        }
        // create table and in new namespace
        TestNamespace.admin.createNamespace(NamespaceDescriptor.create(nsName).build());
        TestNamespace.admin.createTable(desc);
        TestNamespace.TEST_UTIL.waitTableAvailable(desc.getTableName().getName(), 10000);
        FileSystem fs = FileSystem.get(TestNamespace.TEST_UTIL.getConfiguration());
        Assert.assertTrue(fs.exists(new org.apache.hadoop.fs.Path(TestNamespace.master.getMasterFileSystem().getRootDir(), new org.apache.hadoop.fs.Path(BASE_NAMESPACE_DIR, new org.apache.hadoop.fs.Path(nsName, desc.getTableName().getQualifierAsString())))));
        Assert.assertEquals(1, TestNamespace.admin.listTables().length);
        // verify non-empty namespace can't be removed
        try {
            TestNamespace.admin.deleteNamespace(nsName);
            Assert.fail("Expected non-empty namespace constraint exception");
        } catch (Exception ex) {
            TestNamespace.LOG.info(("Caught expected exception: " + ex));
        }
        // sanity check try to write and read from table
        Table table = TestNamespace.TEST_UTIL.getConnection().getTable(desc.getTableName());
        Put p = new Put(Bytes.toBytes("row1"));
        p.addColumn(Bytes.toBytes("my_cf"), Bytes.toBytes("my_col"), Bytes.toBytes("value1"));
        table.put(p);
        // flush and read from disk to make sure directory changes are working
        TestNamespace.admin.flush(desc.getTableName());
        Get g = new Get(Bytes.toBytes("row1"));
        Assert.assertTrue(table.exists(g));
        // normal case of removing namespace
        TestNamespace.TEST_UTIL.deleteTable(desc.getTableName());
        TestNamespace.admin.deleteNamespace(nsName);
    }

    @Test
    public void createTableInDefaultNamespace() throws Exception {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        HColumnDescriptor colDesc = new HColumnDescriptor("cf1");
        desc.addFamily(colDesc);
        TestNamespace.admin.createTable(desc);
        Assert.assertTrue(((TestNamespace.admin.listTables().length) == 1));
        TestNamespace.admin.disableTable(desc.getTableName());
        TestNamespace.admin.deleteTable(desc.getTableName());
    }

    @Test
    public void createTableInSystemNamespace() throws Exception {
        final TableName tableName = TableName.valueOf(("hbase:" + (name.getMethodName())));
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor colDesc = new HColumnDescriptor("cf1");
        desc.addFamily(colDesc);
        TestNamespace.admin.createTable(desc);
        Assert.assertEquals(0, TestNamespace.admin.listTables().length);
        Assert.assertTrue(TestNamespace.admin.tableExists(tableName));
        TestNamespace.admin.disableTable(desc.getTableName());
        TestNamespace.admin.deleteTable(desc.getTableName());
    }

    @Test
    public void testNamespaceOperations() throws IOException {
        TestNamespace.admin.createNamespace(NamespaceDescriptor.create(((prefix) + "ns1")).build());
        TestNamespace.admin.createNamespace(NamespaceDescriptor.create(((prefix) + "ns2")).build());
        // create namespace that already exists
        TestNamespace.runWithExpectedException(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                TestNamespace.admin.createNamespace(NamespaceDescriptor.create(((prefix) + "ns1")).build());
                return null;
            }
        }, NamespaceExistException.class);
        // create a table in non-existing namespace
        TestNamespace.runWithExpectedException(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("non_existing_namespace", name.getMethodName()));
                htd.addFamily(new HColumnDescriptor("family1"));
                TestNamespace.admin.createTable(htd);
                return null;
            }
        }, NamespaceNotFoundException.class);
        // get descriptor for existing namespace
        TestNamespace.admin.getNamespaceDescriptor(((prefix) + "ns1"));
        // get descriptor for non-existing namespace
        TestNamespace.runWithExpectedException(new Callable<NamespaceDescriptor>() {
            @Override
            public NamespaceDescriptor call() throws Exception {
                return TestNamespace.admin.getNamespaceDescriptor("non_existing_namespace");
            }
        }, NamespaceNotFoundException.class);
        // delete descriptor for existing namespace
        TestNamespace.admin.deleteNamespace(((prefix) + "ns2"));
        // delete descriptor for non-existing namespace
        TestNamespace.runWithExpectedException(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                TestNamespace.admin.deleteNamespace("non_existing_namespace");
                return null;
            }
        }, NamespaceNotFoundException.class);
        // modify namespace descriptor for existing namespace
        NamespaceDescriptor ns1 = TestNamespace.admin.getNamespaceDescriptor(((prefix) + "ns1"));
        ns1.setConfiguration("foo", "bar");
        TestNamespace.admin.modifyNamespace(ns1);
        // modify namespace descriptor for non-existing namespace
        TestNamespace.runWithExpectedException(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                TestNamespace.admin.modifyNamespace(NamespaceDescriptor.create("non_existing_namespace").build());
                return null;
            }
        }, NamespaceNotFoundException.class);
        // get table descriptors for existing namespace
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(((prefix) + "ns1"), name.getMethodName()));
        htd.addFamily(new HColumnDescriptor("family1"));
        TestNamespace.admin.createTable(htd);
        HTableDescriptor[] htds = TestNamespace.admin.listTableDescriptorsByNamespace(((prefix) + "ns1"));
        Assert.assertNotNull("Should have not returned null", htds);
        Assert.assertEquals("Should have returned non-empty array", 1, htds.length);
        // get table descriptors for non-existing namespace
        TestNamespace.runWithExpectedException(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                TestNamespace.admin.listTableDescriptorsByNamespace("non_existant_namespace");
                return null;
            }
        }, NamespaceNotFoundException.class);
        // get table names for existing namespace
        TableName[] tableNames = TestNamespace.admin.listTableNamesByNamespace(((prefix) + "ns1"));
        Assert.assertNotNull("Should have not returned null", tableNames);
        Assert.assertEquals("Should have returned non-empty array", 1, tableNames.length);
        // get table names for non-existing namespace
        TestNamespace.runWithExpectedException(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                TestNamespace.admin.listTableNamesByNamespace("non_existing_namespace");
                return null;
            }
        }, NamespaceNotFoundException.class);
    }
}

