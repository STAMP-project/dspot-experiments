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


import AsyncProcessTask.SubmittedRows.NORMAL;
import HConstants.DEFAULT_HBASE_CLIENT_OPERATION_TIMEOUT;
import HConstants.DEFAULT_HBASE_RPC_TIMEOUT;
import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * The purpose of this test is to make sure the region exception won't corrupt the results
 * of batch. The prescription is shown below.
 * 1) honor the action result rather than region exception. If the action have both of true result
 * and region exception, the action is fine as the exception is caused by other actions
 * which are in the same region.
 * 2) honor the action exception rather than region exception. If the action have both of action
 * exception and region exception, we deal with the action exception only. If we also
 * handle the region exception for the same action, it will introduce the negative count of
 * actions in progress. The AsyncRequestFuture#waitUntilDone will block forever.
 *
 * This bug can be reproduced by real use case. see TestMalformedCellFromClient(in branch-1.4+).
 * It uses the batch of RowMutations to present the bug. Given that the batch of RowMutations is
 * only supported by branch-1.4+, perhaps the branch-1.3 and branch-1.2 won't encounter this issue.
 * We still backport the fix to branch-1.3 and branch-1.2 in case we ignore some write paths.
 */
@Category({ ClientTests.class, SmallTests.class })
public class TestAsyncProcessWithRegionException {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncProcessWithRegionException.class);

    private static final Result EMPTY_RESULT = Result.create(null, true);

    private static final IOException IOE = new IOException("YOU CAN'T PASS");

    private static final Configuration CONF = new Configuration();

    private static final TableName DUMMY_TABLE = TableName.valueOf("DUMMY_TABLE");

    private static final byte[] GOOD_ROW = Bytes.toBytes("GOOD_ROW");

    private static final byte[] BAD_ROW = Bytes.toBytes("BAD_ROW");

    private static final byte[] BAD_ROW_WITHOUT_ACTION_EXCEPTION = Bytes.toBytes("BAD_ROW_WITHOUT_ACTION_EXCEPTION");

    private static final byte[] FAMILY = Bytes.toBytes("FAMILY");

    private static final ServerName SERVER_NAME = ServerName.valueOf("s1,1,1");

    private static final RegionInfo REGION_INFO = RegionInfoBuilder.newBuilder(TestAsyncProcessWithRegionException.DUMMY_TABLE).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(1).build();

    private static final HRegionLocation REGION_LOCATION = new HRegionLocation(TestAsyncProcessWithRegionException.REGION_INFO, TestAsyncProcessWithRegionException.SERVER_NAME);

    @Test
    public void testSuccessivePut() throws Exception {
        TestAsyncProcessWithRegionException.MyAsyncProcess ap = new TestAsyncProcessWithRegionException.MyAsyncProcess(TestAsyncProcessWithRegionException.createHConnection(), TestAsyncProcessWithRegionException.CONF);
        List<Put> puts = new ArrayList<>(1);
        puts.add(new Put(TestAsyncProcessWithRegionException.GOOD_ROW).addColumn(TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY));
        final int expectedSize = puts.size();
        AsyncRequestFuture arf = ap.submit(TestAsyncProcessWithRegionException.DUMMY_TABLE, puts);
        arf.waitUntilDone();
        Object[] result = arf.getResults();
        Assert.assertEquals(expectedSize, result.length);
        for (Object r : result) {
            Assert.assertEquals(Result.class, r.getClass());
        }
        Assert.assertTrue(puts.isEmpty());
        TestAsyncProcessWithRegionException.assertActionsInProgress(arf);
    }

    @Test
    public void testFailedPut() throws Exception {
        TestAsyncProcessWithRegionException.MyAsyncProcess ap = new TestAsyncProcessWithRegionException.MyAsyncProcess(TestAsyncProcessWithRegionException.createHConnection(), TestAsyncProcessWithRegionException.CONF);
        List<Put> puts = new ArrayList<>(2);
        puts.add(new Put(TestAsyncProcessWithRegionException.GOOD_ROW).addColumn(TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY));
        // this put should fail
        puts.add(new Put(TestAsyncProcessWithRegionException.BAD_ROW).addColumn(TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY));
        final int expectedSize = puts.size();
        AsyncRequestFuture arf = ap.submit(TestAsyncProcessWithRegionException.DUMMY_TABLE, puts);
        arf.waitUntilDone();
        // There is a failed puts
        TestAsyncProcessWithRegionException.assertError(arf, 1);
        Object[] result = arf.getResults();
        Assert.assertEquals(expectedSize, result.length);
        Assert.assertEquals(Result.class, result[0].getClass());
        Assert.assertTrue(((result[1]) instanceof IOException));
        Assert.assertTrue(puts.isEmpty());
        TestAsyncProcessWithRegionException.assertActionsInProgress(arf);
    }

    @Test
    public void testFailedPutWithoutActionException() throws Exception {
        TestAsyncProcessWithRegionException.MyAsyncProcess ap = new TestAsyncProcessWithRegionException.MyAsyncProcess(TestAsyncProcessWithRegionException.createHConnection(), TestAsyncProcessWithRegionException.CONF);
        List<Put> puts = new ArrayList<>(3);
        puts.add(new Put(TestAsyncProcessWithRegionException.GOOD_ROW).addColumn(TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY));
        // this put should fail
        puts.add(new Put(TestAsyncProcessWithRegionException.BAD_ROW).addColumn(TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY));
        // this put should fail, and it won't have action exception
        puts.add(new Put(TestAsyncProcessWithRegionException.BAD_ROW_WITHOUT_ACTION_EXCEPTION).addColumn(TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY, TestAsyncProcessWithRegionException.FAMILY));
        final int expectedSize = puts.size();
        AsyncRequestFuture arf = ap.submit(TestAsyncProcessWithRegionException.DUMMY_TABLE, puts);
        arf.waitUntilDone();
        // There are two failed puts
        TestAsyncProcessWithRegionException.assertError(arf, 2);
        Object[] result = arf.getResults();
        Assert.assertEquals(expectedSize, result.length);
        Assert.assertEquals(Result.class, result[0].getClass());
        Assert.assertTrue(((result[1]) instanceof IOException));
        Assert.assertTrue(((result[2]) instanceof IOException));
        Assert.assertTrue(puts.isEmpty());
        TestAsyncProcessWithRegionException.assertActionsInProgress(arf);
    }

    private static class MyAsyncProcess extends AsyncProcess {
        private final ExecutorService service = Executors.newFixedThreadPool(5);

        MyAsyncProcess(ClusterConnection hc, Configuration conf) {
            super(hc, conf, new RpcRetryingCallerFactory(conf), new org.apache.hadoop.hbase.ipc.RpcControllerFactory(conf));
        }

        public AsyncRequestFuture submit(TableName tableName, List<? extends Row> rows) throws InterruptedIOException {
            return submit(AsyncProcessTask.newBuilder().setPool(service).setTableName(tableName).setRowAccess(rows).setSubmittedRows(NORMAL).setNeedResults(true).setRpcTimeout(DEFAULT_HBASE_RPC_TIMEOUT).setOperationTimeout(DEFAULT_HBASE_CLIENT_OPERATION_TIMEOUT).build());
        }

        @Override
        protected RpcRetryingCaller<AbstractResponse> createCaller(CancellableRegionServerCallable callable, int rpcTimeout) {
            MultiServerCallable callable1 = ((MultiServerCallable) (callable));
            MultiResponse mr = new MultiResponse();
            callable1.getMulti().actions.forEach(( regionName, actions) -> {
                actions.forEach(( action) -> {
                    if (Bytes.equals(action.getAction().getRow(), org.apache.hadoop.hbase.client.GOOD_ROW)) {
                        mr.add(regionName, action.getOriginalIndex(), org.apache.hadoop.hbase.client.EMPTY_RESULT);
                    } else
                        if (Bytes.equals(action.getAction().getRow(), org.apache.hadoop.hbase.client.BAD_ROW)) {
                            mr.add(regionName, action.getOriginalIndex(), org.apache.hadoop.hbase.client.IOE);
                        }

                });
            });
            mr.addException(TestAsyncProcessWithRegionException.REGION_INFO.getRegionName(), TestAsyncProcessWithRegionException.IOE);
            return new RpcRetryingCallerImpl<AbstractResponse>(100, 500, 0, 9) {
                @Override
                public AbstractResponse callWithoutRetries(RetryingCallable<AbstractResponse> callable, int callTimeout) {
                    try {
                        // sleep one second in order for threadpool to start another thread instead of reusing
                        // existing one.
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // pass
                    }
                    return mr;
                }
            };
        }
    }
}

