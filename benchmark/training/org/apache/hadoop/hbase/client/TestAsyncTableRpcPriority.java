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


import ClientProtos.MultiRequest;
import ClientService.Interface;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.GetRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MutateRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanRequest;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Confirm that we will set the priority in {@link HBaseRpcController} for several table operations.
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestAsyncTableRpcPriority {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableRpcPriority.class);

    private static Configuration CONF = HBaseConfiguration.create();

    private Interface stub;

    private AsyncConnection conn;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testGet() {
        conn.getTable(TableName.valueOf(name.getMethodName())).get(setPriority(11)).join();
        Mockito.verify(stub, Mockito.times(1)).get(assertPriority(11), ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testGetNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).get(new Get(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).get(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testGetSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).get(new Get(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).get(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testGetMetaTable() {
        conn.getTable(META_TABLE_NAME).get(new Get(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).get(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(GetRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testPut() {
        conn.getTable(TableName.valueOf(name.getMethodName())).put(setPriority(12).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(12), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testPutNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).put(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testPutSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).put(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testPutMetaTable() {
        conn.getTable(META_TABLE_NAME).put(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testDelete() {
        conn.getTable(TableName.valueOf(name.getMethodName())).delete(setPriority(13)).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(13), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testDeleteNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).delete(new Delete(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testDeleteSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).delete(new Delete(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testDeleteMetaTable() {
        conn.getTable(META_TABLE_NAME).delete(new Delete(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testAppend() {
        conn.getTable(TableName.valueOf(name.getMethodName())).append(setPriority(14).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(14), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testAppendNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).append(new Append(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testAppendSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).append(new Append(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testAppendMetaTable() {
        conn.getTable(META_TABLE_NAME).append(new Append(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testIncrement() {
        conn.getTable(TableName.valueOf(name.getMethodName())).increment(setPriority(15)).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(15), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testIncrementNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).incrementColumnValue(Bytes.toBytes(0), Bytes.toBytes("cf"), Bytes.toBytes("cq"), 1).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testIncrementSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).incrementColumnValue(Bytes.toBytes(0), Bytes.toBytes("cf"), Bytes.toBytes("cq"), 1).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testIncrementMetaTable() {
        conn.getTable(META_TABLE_NAME).incrementColumnValue(Bytes.toBytes(0), Bytes.toBytes("cf"), Bytes.toBytes("cq"), 1).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndPut() {
        conn.getTable(TableName.valueOf(name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifNotExists().thenPut(setPriority(16)).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(16), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndPutNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifNotExists().thenPut(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndPutSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifNotExists().thenPut(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndPutMetaTable() {
        conn.getTable(META_TABLE_NAME).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifNotExists().thenPut(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndDelete() {
        conn.getTable(TableName.valueOf(name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenDelete(setPriority(17)).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(17), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndDeleteNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenDelete(new Delete(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndDeleteSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenDelete(new Delete(Bytes.toBytes(0))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndDeleteMetaTable() {
        conn.getTable(META_TABLE_NAME).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifNotExists().thenPut(new Put(Bytes.toBytes(0)).addColumn(Bytes.toBytes("cf"), Bytes.toBytes("cq"), Bytes.toBytes("v"))).join();
        Mockito.verify(stub, Mockito.times(1)).mutate(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MutateRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndMutate() throws IOException {
        conn.getTable(TableName.valueOf(name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenMutate(add(((Mutation) (setPriority(18))))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(18), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndMutateNormalTable() throws IOException {
        conn.getTable(TableName.valueOf(name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenMutate(add(((Mutation) (new Delete(Bytes.toBytes(0)))))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndMutateSystemTable() throws IOException {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenMutate(add(((Mutation) (new Delete(Bytes.toBytes(0)))))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testCheckAndMutateMetaTable() throws IOException {
        conn.getTable(META_TABLE_NAME).checkAndMutate(Bytes.toBytes(0), Bytes.toBytes("cf")).qualifier(Bytes.toBytes("cq")).ifEquals(Bytes.toBytes("v")).thenMutate(add(((Mutation) (new Delete(Bytes.toBytes(0)))))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testScan() throws IOException, InterruptedException {
        try (ResultScanner scanner = conn.getTable(TableName.valueOf(name.getMethodName())).getScanner(setPriority(19))) {
            Assert.assertNotNull(scanner.next());
            Thread.sleep(1000);
        }
        Thread.sleep(1000);
        // open, next, several renew lease, and then close
        Mockito.verify(stub, Mockito.atLeast(4)).scan(assertPriority(19), ArgumentMatchers.any(ScanRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testScanNormalTable() throws IOException, InterruptedException {
        try (ResultScanner scanner = conn.getTable(TableName.valueOf(name.getMethodName())).getScanner(new Scan().setCaching(1).setMaxResultSize(1))) {
            Assert.assertNotNull(scanner.next());
            Thread.sleep(1000);
        }
        Thread.sleep(1000);
        // open, next, several renew lease, and then close
        Mockito.verify(stub, Mockito.atLeast(4)).scan(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(ScanRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testScanSystemTable() throws IOException, InterruptedException {
        try (ResultScanner scanner = conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).getScanner(new Scan().setCaching(1).setMaxResultSize(1))) {
            Assert.assertNotNull(scanner.next());
            Thread.sleep(1000);
        }
        Thread.sleep(1000);
        // open, next, several renew lease, and then close
        Mockito.verify(stub, Mockito.atLeast(4)).scan(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(ScanRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testScanMetaTable() throws IOException, InterruptedException {
        try (ResultScanner scanner = conn.getTable(META_TABLE_NAME).getScanner(new Scan().setCaching(1).setMaxResultSize(1))) {
            Assert.assertNotNull(scanner.next());
            Thread.sleep(1000);
        }
        Thread.sleep(1000);
        // open, next, several renew lease, and then close
        Mockito.verify(stub, Mockito.atLeast(4)).scan(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(ScanRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testBatchNormalTable() {
        conn.getTable(TableName.valueOf(name.getMethodName())).batchAll(Arrays.asList(new Delete(Bytes.toBytes(0)))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(HConstants.NORMAL_QOS), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testBatchSystemTable() {
        conn.getTable(TableName.valueOf(NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR, name.getMethodName())).batchAll(Arrays.asList(new Delete(Bytes.toBytes(0)))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }

    @Test
    public void testBatchMetaTable() {
        conn.getTable(META_TABLE_NAME).batchAll(Arrays.asList(new Delete(Bytes.toBytes(0)))).join();
        Mockito.verify(stub, Mockito.times(1)).multi(assertPriority(HConstants.SYSTEMTABLE_QOS), ArgumentMatchers.any(MultiRequest.class), ArgumentMatchers.any());
    }
}

