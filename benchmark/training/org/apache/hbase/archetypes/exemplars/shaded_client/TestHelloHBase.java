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
package org.apache.hbase.archetypes.exemplars.shaded_client;


import HelloHBase.MY_COLUMN_FAMILY_NAME;
import HelloHBase.MY_FIRST_COLUMN_QUALIFIER;
import HelloHBase.MY_NAMESPACE_NAME;
import HelloHBase.MY_TABLE_NAME;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static HelloHBase.MY_ROW_ID;


/**
 * Unit testing for HelloHBase.
 */
@Category(MediumTests.class)
public class TestHelloHBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHelloHBase.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testNamespaceExists() throws Exception {
        final String NONEXISTENT_NAMESPACE = "xyzpdq_nonexistent";
        final String EXISTING_NAMESPACE = "pdqxyz_myExistingNamespace";
        boolean exists;
        Admin admin = TestHelloHBase.TEST_UTIL.getAdmin();
        exists = HelloHBase.namespaceExists(admin, NONEXISTENT_NAMESPACE);
        Assert.assertEquals("#namespaceExists failed: found nonexistent namespace.", false, exists);
        admin.createNamespace(NamespaceDescriptor.create(EXISTING_NAMESPACE).build());
        exists = HelloHBase.namespaceExists(admin, EXISTING_NAMESPACE);
        Assert.assertEquals("#namespaceExists failed: did NOT find existing namespace.", true, exists);
        admin.deleteNamespace(EXISTING_NAMESPACE);
    }

    @Test
    public void testCreateNamespaceAndTable() throws Exception {
        Admin admin = TestHelloHBase.TEST_UTIL.getAdmin();
        HelloHBase.createNamespaceAndTable(admin);
        boolean namespaceExists = HelloHBase.namespaceExists(admin, MY_NAMESPACE_NAME);
        Assert.assertEquals("#createNamespaceAndTable failed to create namespace.", true, namespaceExists);
        boolean tableExists = admin.tableExists(MY_TABLE_NAME);
        Assert.assertEquals("#createNamespaceAndTable failed to create table.", true, tableExists);
        admin.disableTable(MY_TABLE_NAME);
        admin.deleteTable(MY_TABLE_NAME);
        admin.deleteNamespace(MY_NAMESPACE_NAME);
    }

    @Test
    public void testPutRowToTable() throws IOException {
        Admin admin = TestHelloHBase.TEST_UTIL.getAdmin();
        admin.createNamespace(NamespaceDescriptor.create(MY_NAMESPACE_NAME).build());
        Table table = TestHelloHBase.TEST_UTIL.createTable(MY_TABLE_NAME, MY_COLUMN_FAMILY_NAME);
        HelloHBase.putRowToTable(table);
        Result row = table.get(new org.apache.hadoop.hbase.client.Get(MY_ROW_ID));
        Assert.assertEquals("#putRowToTable failed to store row.", false, row.isEmpty());
        TestHelloHBase.TEST_UTIL.deleteTable(MY_TABLE_NAME);
        admin.deleteNamespace(MY_NAMESPACE_NAME);
    }

    @Test
    public void testDeleteRow() throws IOException {
        Admin admin = TestHelloHBase.TEST_UTIL.getAdmin();
        admin.createNamespace(NamespaceDescriptor.create(MY_NAMESPACE_NAME).build());
        Table table = TestHelloHBase.TEST_UTIL.createTable(MY_TABLE_NAME, MY_COLUMN_FAMILY_NAME);
        table.put(new org.apache.hadoop.hbase.client.Put(MY_ROW_ID).addColumn(MY_COLUMN_FAMILY_NAME, MY_FIRST_COLUMN_QUALIFIER, Bytes.toBytes("xyz")));
        HelloHBase.deleteRow(table);
        Result row = table.get(new org.apache.hadoop.hbase.client.Get(MY_ROW_ID));
        Assert.assertEquals("#deleteRow failed to delete row.", true, row.isEmpty());
        TestHelloHBase.TEST_UTIL.deleteTable(MY_TABLE_NAME);
        admin.deleteNamespace(MY_NAMESPACE_NAME);
    }
}

