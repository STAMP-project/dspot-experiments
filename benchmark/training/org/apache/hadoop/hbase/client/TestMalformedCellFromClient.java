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


import ClientProtos.Action;
import ClientProtos.MultiRequest;
import ClientProtos.MultiResponse;
import ClientProtos.MutationProto.MutationType.PUT;
import ClientProtos.RegionAction;
import HBaseProtos.RegionSpecifier.RegionSpecifierType.REGION_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ipc.HBaseRpcController;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.RequestConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The purpose of this test is to ensure whether rs deals with the malformed cells correctly.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestMalformedCellFromClient {
    private static final Logger LOG = LoggerFactory.getLogger(TestMalformedCellFromClient.class);

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMalformedCellFromClient.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] FAMILY = Bytes.toBytes("testFamily");

    private static final int CELL_SIZE = 100;

    private static final TableName TABLE_NAME = TableName.valueOf("TestMalformedCellFromClient");

    /**
     * The purpose of this ut is to check the consistency between the exception and results.
     * If the RetriesExhaustedWithDetailsException contains the whole batch,
     * each result should be of IOE. Otherwise, the row operation which is not in the exception
     * should have a true result.
     */
    @Test
    public void testRegionException() throws IOException, InterruptedException {
        List<Row> batches = new ArrayList<>();
        batches.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[10]));
        // the rm is used to prompt the region exception.
        // see RSRpcServices#multi
        RowMutations rm = new RowMutations(Bytes.toBytes("fail"));
        rm.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        batches.add(rm);
        Object[] results = new Object[batches.size()];
        try (Table table = TestMalformedCellFromClient.TEST_UTIL.getConnection().getTable(TestMalformedCellFromClient.TABLE_NAME)) {
            Throwable exceptionByCaught = null;
            try {
                table.batch(batches, results);
                Assert.fail("Where is the exception? We put the malformed cells!!!");
            } catch (RetriesExhaustedWithDetailsException e) {
                for (Throwable throwable : e.getCauses()) {
                    Assert.assertNotNull(throwable);
                }
                Assert.assertEquals(1, e.getNumExceptions());
                exceptionByCaught = e.getCause(0);
            }
            for (Object obj : results) {
                Assert.assertNotNull(obj);
            }
            Assert.assertEquals(Result.class, results[0].getClass());
            Assert.assertEquals(exceptionByCaught.getClass(), results[1].getClass());
            Result result = table.get(new Get(Bytes.toBytes("good")));
            Assert.assertEquals(1, result.size());
            Cell cell = result.getColumnLatestCell(TestMalformedCellFromClient.FAMILY, null);
            Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(cell), new byte[10]));
        }
    }

    /**
     * This test verifies region exception doesn't corrupt the results of batch. The prescription is
     * shown below. 1) honor the action result rather than region exception. If the action have both
     * of true result and region exception, the action is fine as the exception is caused by other
     * actions which are in the same region. 2) honor the action exception rather than region
     * exception. If the action have both of action exception and region exception, we deal with the
     * action exception only. If we also handle the region exception for the same action, it will
     * introduce the negative count of actions in progress. The AsyncRequestFuture#waitUntilDone will
     * block forever. If the RetriesExhaustedWithDetailsException contains the whole batch, each
     * result should be of IOE. Otherwise, the row operation which is not in the exception should have
     * a true result. The no-cluster test is in TestAsyncProcessWithRegionException.
     */
    @Test
    public void testRegionExceptionByAsync() throws Exception {
        List<Row> batches = new ArrayList<>();
        batches.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[10]));
        // the rm is used to prompt the region exception.
        // see RSRpcServices#multi
        RowMutations rm = new RowMutations(Bytes.toBytes("fail"));
        rm.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        batches.add(rm);
        try (AsyncConnection asyncConnection = ConnectionFactory.createAsyncConnection(TestMalformedCellFromClient.TEST_UTIL.getConfiguration()).get()) {
            AsyncTable<AdvancedScanResultConsumer> table = asyncConnection.getTable(TestMalformedCellFromClient.TABLE_NAME);
            List<CompletableFuture<AdvancedScanResultConsumer>> results = table.batch(batches);
            Assert.assertEquals(2, results.size());
            try {
                results.get(1).get();
                Assert.fail("Where is the exception? We put the malformed cells!!!");
            } catch (ExecutionException e) {
                // pass
            }
            Result result = table.get(new Get(Bytes.toBytes("good"))).get();
            Assert.assertEquals(1, result.size());
            Cell cell = result.getColumnLatestCell(TestMalformedCellFromClient.FAMILY, null);
            Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(cell), new byte[10]));
        }
    }

    /**
     * The invalid cells is in rm. The rm should fail but the subsequent mutations should succeed.
     * Currently, we have no client api to submit the request consisting of condition-rm and mutation.
     * Hence, this test build the request manually.
     */
    @Test
    public void testAtomicOperations() throws Exception {
        RowMutations rm = new RowMutations(Bytes.toBytes("fail"));
        rm.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        rm.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[10]));
        Put put = new Put(Bytes.toBytes("good")).addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[10]);
        // build the request
        HRegion r = TestMalformedCellFromClient.TEST_UTIL.getMiniHBaseCluster().getRegions(TestMalformedCellFromClient.TABLE_NAME).get(0);
        ClientProtos.MultiRequest request = MultiRequest.newBuilder(TestMalformedCellFromClient.createRequest(rm, r.getRegionInfo().getRegionName())).addRegionAction(RegionAction.newBuilder().setRegion(RequestConverter.buildRegionSpecifier(REGION_NAME, r.getRegionInfo().getRegionName())).addAction(Action.newBuilder().setMutation(ProtobufUtil.toMutationNoData(PUT, put)))).build();
        List<Cell> cells = new ArrayList<>();
        for (Mutation m : rm.getMutations()) {
            cells.addAll(m.getCellList(TestMalformedCellFromClient.FAMILY));
        }
        cells.addAll(put.getCellList(TestMalformedCellFromClient.FAMILY));
        Assert.assertEquals(3, cells.size());
        HBaseRpcController controller = Mockito.mock(HBaseRpcController.class);
        Mockito.when(controller.cellScanner()).thenReturn(CellUtil.createCellScanner(cells));
        HRegionServer rs = TestMalformedCellFromClient.TEST_UTIL.getMiniHBaseCluster().getRegionServer(TestMalformedCellFromClient.TEST_UTIL.getMiniHBaseCluster().getServerHoldingRegion(TestMalformedCellFromClient.TABLE_NAME, r.getRegionInfo().getRegionName()));
        ClientProtos.MultiResponse response = rs.getRSRpcServices().multi(controller, request);
        Assert.assertEquals(2, response.getRegionActionResultCount());
        Assert.assertTrue(response.getRegionActionResultList().get(0).hasException());
        Assert.assertFalse(response.getRegionActionResultList().get(1).hasException());
        Assert.assertEquals(1, response.getRegionActionResultList().get(1).getResultOrExceptionCount());
        Assert.assertTrue(response.getRegionActionResultList().get(1).getResultOrExceptionList().get(0).hasResult());
        try (Table table = TestMalformedCellFromClient.TEST_UTIL.getConnection().getTable(TestMalformedCellFromClient.TABLE_NAME)) {
            Result result = table.get(new Get(Bytes.toBytes("good")));
            Assert.assertEquals(1, result.size());
            Cell cell = result.getColumnLatestCell(TestMalformedCellFromClient.FAMILY, null);
            Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(cell), new byte[10]));
        }
    }

    /**
     * This test depends on how regionserver process the batch ops.
     * 1) group the put/delete until meeting the increment
     * 2) process the batch of put/delete
     * 3) process the increment
     * see RSRpcServices#doNonAtomicRegionMutation
     */
    @Test
    public void testNonAtomicOperations() throws IOException, InterruptedException {
        Increment inc = new Increment(Bytes.toBytes("good")).addColumn(TestMalformedCellFromClient.FAMILY, null, 100);
        List<Row> batches = new ArrayList<>();
        // the first and second puts will be group by regionserver
        batches.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        batches.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        // this Increment should succeed
        batches.add(inc);
        // this put should succeed
        batches.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[1]));
        Object[] objs = new Object[batches.size()];
        try (Table table = TestMalformedCellFromClient.TEST_UTIL.getConnection().getTable(TestMalformedCellFromClient.TABLE_NAME)) {
            table.batch(batches, objs);
            Assert.fail("Where is the exception? We put the malformed cells!!!");
        } catch (RetriesExhaustedWithDetailsException e) {
            Assert.assertEquals(2, e.getNumExceptions());
            for (int i = 0; i != (e.getNumExceptions()); ++i) {
                Assert.assertNotNull(e.getCause(i));
                Assert.assertEquals(DoNotRetryIOException.class, e.getCause(i).getClass());
                Assert.assertEquals("fail", Bytes.toString(e.getRow(i).getRow()));
            }
        } finally {
            TestMalformedCellFromClient.assertObjects(objs, batches.size());
            Assert.assertTrue(((objs[0]) instanceof IOException));
            Assert.assertTrue(((objs[1]) instanceof IOException));
            Assert.assertEquals(Result.class, objs[2].getClass());
            Assert.assertEquals(Result.class, objs[3].getClass());
        }
    }

    @Test
    public void testRowMutations() throws IOException, InterruptedException {
        Put put = new Put(Bytes.toBytes("good")).addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[1]);
        List<Row> batches = new ArrayList<>();
        RowMutations mutations = new RowMutations(Bytes.toBytes("fail"));
        // the first and second puts will be group by regionserver
        mutations.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        mutations.add(addColumn(TestMalformedCellFromClient.FAMILY, null, new byte[TestMalformedCellFromClient.CELL_SIZE]));
        batches.add(mutations);
        // this bm should succeed
        mutations = new RowMutations(Bytes.toBytes("good"));
        mutations.add(put);
        mutations.add(put);
        batches.add(mutations);
        Object[] objs = new Object[batches.size()];
        try (Table table = TestMalformedCellFromClient.TEST_UTIL.getConnection().getTable(TestMalformedCellFromClient.TABLE_NAME)) {
            table.batch(batches, objs);
            Assert.fail("Where is the exception? We put the malformed cells!!!");
        } catch (RetriesExhaustedWithDetailsException e) {
            Assert.assertEquals(1, e.getNumExceptions());
            for (int i = 0; i != (e.getNumExceptions()); ++i) {
                Assert.assertNotNull(e.getCause(i));
                Assert.assertTrue(((e.getCause(i)) instanceof IOException));
                Assert.assertEquals("fail", Bytes.toString(e.getRow(i).getRow()));
            }
        } finally {
            TestMalformedCellFromClient.assertObjects(objs, batches.size());
            Assert.assertTrue(((objs[0]) instanceof IOException));
            Assert.assertEquals(Result.class, objs[1].getClass());
        }
    }
}

