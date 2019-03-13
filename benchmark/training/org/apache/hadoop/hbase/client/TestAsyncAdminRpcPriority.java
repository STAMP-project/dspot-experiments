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
package org.apache.hadoop.hbase.client;


import TableName.META_TABLE_NAME;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.AdminService.Interface;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.StopServerRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.CreateTableRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.ShutdownRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.StopMasterRequest;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Confirm that we will set the priority in {@link HBaseRpcController} for several admin operations.
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestAsyncAdminRpcPriority {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncAdminRpcPriority.class);

    private static Configuration CONF = HBaseConfiguration.create();

    private Interface masterStub;

    private Interface adminStub;

    private AsyncConnection conn;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCreateNormalTable() {
        conn.getAdmin().createTable(TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.of("cf")).build()).join();
        Mockito.verify(masterStub, Mockito.times(1)).createTable(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(CreateTableRequest.class), ArgumentMatchers.any());
    }

    // a bit strange as we can not do this in production but anyway, just a client mock to confirm
    // that we pass the correct priority
    @Test
    public void testCreateSystemTable() {
        conn.getAdmin().createTable(TableDescriptorBuilder.newBuilder(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).setColumnFamily(ColumnFamilyDescriptorBuilder.of("cf")).build()).join();
        Mockito.verify(masterStub, Mockito.times(1)).createTable(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(CreateTableRequest.class), ArgumentMatchers.any());
    }

    // a bit strange as we can not do this in production but anyway, just a client mock to confirm
    // that we pass the correct priority
    @Test
    public void testCreateMetaTable() {
        conn.getAdmin().createTable(TableDescriptorBuilder.newBuilder(META_TABLE_NAME).setColumnFamily(ColumnFamilyDescriptorBuilder.of("cf")).build()).join();
        Mockito.verify(masterStub, Mockito.times(1)).createTable(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(CreateTableRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testShutdown() {
        conn.getAdmin().shutdown().join();
        Mockito.verify(masterStub, Mockito.times(1)).shutdown(assertPriority(HConstants.HIGH_QOS), ArgumentMatchers.any(ShutdownRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testStopMaster() {
        conn.getAdmin().stopMaster().join();
        Mockito.verify(masterStub, Mockito.times(1)).stopMaster(assertPriority(HConstants.HIGH_QOS), ArgumentMatchers.any(StopMasterRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testStopRegionServer() {
        conn.getAdmin().stopRegionServer(ServerName.valueOf("rs", 16010, 12345)).join();
        Mockito.verify(adminStub, Mockito.times(1)).stopServer(assertPriority(HConstants.HIGH_QOS), ArgumentMatchers.any(StopServerRequest.class), ArgumentMatchers.any());
    }
}

