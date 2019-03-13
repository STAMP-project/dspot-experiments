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
package org.apache.hadoop.hbase.master;


import TableDescriptorBuilder.NAMESPACE_COL_DESC_BYTES;
import TableDescriptorBuilder.NAMESPACE_FAMILY_INFO_BYTES;
import TableDescriptorBuilder.NAMESPACE_TABLEDESC;
import TableName.NAMESPACE_TABLE_NAME;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.procedure.AbstractStateMachineNamespaceProcedure;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.MasterThread;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-21154.
 */
@Category({ MasterTests.class, LargeTests.class })
public class TestMigrateNamespaceTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMigrateNamespaceTable.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testMigrate() throws IOException, InterruptedException {
        TestMigrateNamespaceTable.UTIL.getAdmin().createTable(NAMESPACE_TABLEDESC);
        try (Table table = TestMigrateNamespaceTable.UTIL.getConnection().getTable(NAMESPACE_TABLE_NAME)) {
            for (int i = 0; i < 5; i++) {
                NamespaceDescriptor nd = NamespaceDescriptor.create(("Test-NS-" + i)).addConfiguration(("key-" + i), ("value-" + i)).build();
                table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(nd.getName())).addColumn(NAMESPACE_FAMILY_INFO_BYTES, NAMESPACE_COL_DESC_BYTES, ProtobufUtil.toProtoNamespaceDescriptor(nd).toByteArray()));
                AbstractStateMachineNamespaceProcedure.createDirectory(TestMigrateNamespaceTable.UTIL.getMiniHBaseCluster().getMaster().getMasterFileSystem(), nd);
            }
        }
        MasterThread masterThread = TestMigrateNamespaceTable.UTIL.getMiniHBaseCluster().getMasterThread();
        masterThread.getMaster().stop("For testing");
        masterThread.join();
        TestMigrateNamespaceTable.UTIL.getMiniHBaseCluster().startMaster();
        // 5 + default and system('hbase')
        Assert.assertEquals(7, TestMigrateNamespaceTable.UTIL.getAdmin().listNamespaceDescriptors().length);
        for (int i = 0; i < 5; i++) {
            NamespaceDescriptor nd = TestMigrateNamespaceTable.UTIL.getAdmin().getNamespaceDescriptor(("Test-NS-" + i));
            Assert.assertEquals(("Test-NS-" + i), nd.getName());
            Assert.assertEquals(1, nd.getConfiguration().size());
            Assert.assertEquals(("value-" + i), nd.getConfigurationValue(("key-" + i)));
        }
        waitFor(30000, () -> UTIL.getAdmin().isTableDisabled(TableName.NAMESPACE_TABLE_NAME));
        masterThread = TestMigrateNamespaceTable.UTIL.getMiniHBaseCluster().getMasterThread();
        masterThread.getMaster().stop("For testing");
        masterThread.join();
        TestMigrateNamespaceTable.UTIL.getMiniHBaseCluster().startMaster();
        // make sure that we could still restart the cluster after disabling the namespace table.
        Assert.assertEquals(7, TestMigrateNamespaceTable.UTIL.getAdmin().listNamespaceDescriptors().length);
        // let's delete the namespace table
        TestMigrateNamespaceTable.UTIL.getAdmin().deleteTable(NAMESPACE_TABLE_NAME);
        Assert.assertFalse(TestMigrateNamespaceTable.UTIL.getAdmin().tableExists(NAMESPACE_TABLE_NAME));
    }
}

