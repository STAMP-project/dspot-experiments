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
package org.apache.hadoop.hbase.replication.regionserver;


import AdminProtos.WALEntry.Builder;
import Batch.Call;
import Cell.Type.Put;
import CellBuilderType.DEEP_COPY;
import WALEntrySinkFilter.WAL_ENTRY_FILTER_KEY;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellBuilder;
import org.apache.hadoop.hbase.CellBuilderFactory;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple test of sink-side wal entry filter facility.
 */
@Category({ ReplicationTests.class, SmallTests.class })
public class TestWALEntrySinkFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALEntrySinkFilter.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationSink.class);

    @Rule
    public TestName name = new TestName();

    static final int BOUNDARY = 5;

    static final AtomicInteger UNFILTERED = new AtomicInteger();

    static final AtomicInteger FILTERED = new AtomicInteger();

    /**
     * Implemetentation of Stoppable to pass into ReplicationSink.
     */
    private static Stoppable STOPPABLE = new Stoppable() {
        private final AtomicBoolean stop = new AtomicBoolean(false);

        @Override
        public boolean isStopped() {
            return this.stop.get();
        }

        @Override
        public void stop(String why) {
            TestWALEntrySinkFilter.LOG.info(("STOPPING BECAUSE: " + why));
            this.stop.set(true);
        }
    };

    /**
     * Test filter.
     * Filter will filter out any write time that is <= 5 (BOUNDARY). We count how many items we
     * filter out and we count how many cells make it through for distribution way down below in the
     * Table#batch implementation. Puts in place a custom DevNullConnection so we can insert our
     * counting Table.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testWALEntryFilter() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        // Make it so our filter is instantiated on construction of ReplicationSink.
        conf.setClass(WAL_ENTRY_FILTER_KEY, TestWALEntrySinkFilter.IfTimeIsGreaterThanBOUNDARYWALEntrySinkFilterImpl.class, WALEntrySinkFilter.class);
        conf.setClass("hbase.client.connection.impl", TestWALEntrySinkFilter.DevNullConnection.class, Connection.class);
        ReplicationSink sink = new ReplicationSink(conf, TestWALEntrySinkFilter.STOPPABLE);
        // Create some dumb walentries.
        List<AdminProtos.WALEntry> entries = new ArrayList<>();
        AdminProtos.WALEntry.Builder entryBuilder = AdminProtos.WALEntry.newBuilder();
        // Need a tablename.
        ByteString tableName = ByteString.copyFromUtf8(TableName.valueOf(this.name.getMethodName()).toString());
        // Add WALEdit Cells to Cells List. The way edits arrive at the sink is with protos
        // describing the edit with all Cells from all edits aggregated in a single CellScanner.
        final List<Cell> cells = new ArrayList<>();
        int count = (TestWALEntrySinkFilter.BOUNDARY) * 2;
        for (int i = 0; i < count; i++) {
            byte[] bytes = Bytes.toBytes(i);
            // Create a wal entry. Everything is set to the current index as bytes or int/long.
            entryBuilder.clear();
            entryBuilder.setKey(entryBuilder.getKeyBuilder().setLogSequenceNumber(i).setEncodedRegionName(ByteString.copyFrom(bytes)).setWriteTime(i).setTableName(tableName).build());
            // Lets have one Cell associated with each WALEdit.
            entryBuilder.setAssociatedCellCount(1);
            entries.add(entryBuilder.build());
            // We need to add a Cell per WALEdit to the cells array.
            CellBuilder cellBuilder = CellBuilderFactory.create(DEEP_COPY);
            // Make cells whose row, family, cell, value, and ts are == 'i'.
            Cell cell = cellBuilder.setRow(bytes).setFamily(bytes).setQualifier(bytes).setType(Put).setTimestamp(i).setValue(bytes).build();
            cells.add(cell);
        }
        // Now wrap our cells array in a CellScanner that we can pass in to replicateEntries. It has
        // all Cells from all the WALEntries made above.
        CellScanner cellScanner = new CellScanner() {
            // Set to -1 because advance gets called before current.
            int index = -1;

            @Override
            public Cell current() {
                return cells.get(index);
            }

            @Override
            public boolean advance() throws IOException {
                (index)++;
                return (index) < (cells.size());
            }
        };
        // Call our sink.
        sink.replicateEntries(entries, cellScanner, null, null, null);
        // Check what made it through and what was filtered.
        Assert.assertTrue(((TestWALEntrySinkFilter.FILTERED.get()) > 0));
        Assert.assertTrue(((TestWALEntrySinkFilter.UNFILTERED.get()) > 0));
        Assert.assertEquals(count, ((TestWALEntrySinkFilter.FILTERED.get()) + (TestWALEntrySinkFilter.UNFILTERED.get())));
    }

    /**
     * Simple filter that will filter out any entry wholse writeTime is <= 5.
     */
    public static class IfTimeIsGreaterThanBOUNDARYWALEntrySinkFilterImpl implements WALEntrySinkFilter {
        public IfTimeIsGreaterThanBOUNDARYWALEntrySinkFilterImpl() {
        }

        @Override
        public void init(Connection connection) {
            // Do nothing.
        }

        @Override
        public boolean filter(TableName table, long writeTime) {
            boolean b = writeTime <= (TestWALEntrySinkFilter.BOUNDARY);
            if (b) {
                TestWALEntrySinkFilter.FILTERED.incrementAndGet();
            }
            return b;
        }
    }

    /**
     * A DevNull Connection whose only purpose is checking what edits made it through. See down in
     * {@link Table#batch(List, Object[])}.
     */
    public static class DevNullConnection implements Connection {
        private final Configuration configuration;

        DevNullConnection(Configuration configuration, ExecutorService es, User user) {
            this.configuration = configuration;
        }

        @Override
        public void abort(String why, Throwable e) {
        }

        @Override
        public boolean isAborted() {
            return false;
        }

        @Override
        public Configuration getConfiguration() {
            return this.configuration;
        }

        @Override
        public BufferedMutator getBufferedMutator(TableName tableName) throws IOException {
            return null;
        }

        @Override
        public BufferedMutator getBufferedMutator(BufferedMutatorParams params) throws IOException {
            return null;
        }

        @Override
        public RegionLocator getRegionLocator(TableName tableName) throws IOException {
            return null;
        }

        @Override
        public Admin getAdmin() throws IOException {
            return null;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public TableBuilder getTableBuilder(final TableName tableName, ExecutorService pool) {
            return new TableBuilder() {
                @Override
                public TableBuilder setOperationTimeout(int timeout) {
                    return this;
                }

                @Override
                public TableBuilder setRpcTimeout(int timeout) {
                    return this;
                }

                @Override
                public TableBuilder setReadRpcTimeout(int timeout) {
                    return this;
                }

                @Override
                public TableBuilder setWriteRpcTimeout(int timeout) {
                    return this;
                }

                @Override
                public Table build() {
                    return new Table() {
                        @Override
                        public TableName getName() {
                            return tableName;
                        }

                        @Override
                        public Configuration getConfiguration() {
                            return configuration;
                        }

                        @Override
                        public HTableDescriptor getTableDescriptor() throws IOException {
                            return null;
                        }

                        @Override
                        public TableDescriptor getDescriptor() throws IOException {
                            return null;
                        }

                        @Override
                        public boolean exists(Get get) throws IOException {
                            return false;
                        }

                        @Override
                        public boolean[] exists(List<Get> gets) throws IOException {
                            return new boolean[0];
                        }

                        @Override
                        public void batch(List<? extends Row> actions, Object[] results) throws IOException, InterruptedException {
                            for (Row action : actions) {
                                // Row is the index of the loop above where we make WALEntry and Cells.
                                int row = Bytes.toInt(action.getRow());
                                Assert.assertTrue(("" + row), (row > (TestWALEntrySinkFilter.BOUNDARY)));
                                TestWALEntrySinkFilter.UNFILTERED.incrementAndGet();
                            }
                        }

                        @Override
                        public <R> void batchCallback(List<? extends Row> actions, Object[] results, Batch.Callback<R> callback) throws IOException, InterruptedException {
                        }

                        @Override
                        public Result get(Get get) throws IOException {
                            return null;
                        }

                        @Override
                        public Result[] get(List<Get> gets) throws IOException {
                            return new Result[0];
                        }

                        @Override
                        public ResultScanner getScanner(Scan scan) throws IOException {
                            return null;
                        }

                        @Override
                        public ResultScanner getScanner(byte[] family) throws IOException {
                            return null;
                        }

                        @Override
                        public ResultScanner getScanner(byte[] family, byte[] qualifier) throws IOException {
                            return null;
                        }

                        @Override
                        public void put(org.apache.hadoop.hbase.client.Put put) throws IOException {
                        }

                        @Override
                        public void put(List<org.apache.hadoop.hbase.client.Put> puts) throws IOException {
                        }

                        @Override
                        public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, byte[] value, org.apache.hadoop.hbase.client.Put put) throws IOException {
                            return false;
                        }

                        @Override
                        public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value, org.apache.hadoop.hbase.client.Put put) throws IOException {
                            return false;
                        }

                        @Override
                        public boolean checkAndPut(byte[] row, byte[] family, byte[] qualifier, CompareOperator op, byte[] value, org.apache.hadoop.hbase.client.Put put) throws IOException {
                            return false;
                        }

                        @Override
                        public void delete(Delete delete) throws IOException {
                        }

                        @Override
                        public void delete(List<Delete> deletes) throws IOException {
                        }

                        @Override
                        public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, byte[] value, Delete delete) throws IOException {
                            return false;
                        }

                        @Override
                        public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value, Delete delete) throws IOException {
                            return false;
                        }

                        @Override
                        public boolean checkAndDelete(byte[] row, byte[] family, byte[] qualifier, CompareOperator op, byte[] value, Delete delete) throws IOException {
                            return false;
                        }

                        @Override
                        public CheckAndMutateBuilder checkAndMutate(byte[] row, byte[] family) {
                            return null;
                        }

                        @Override
                        public void mutateRow(RowMutations rm) throws IOException {
                        }

                        @Override
                        public Result append(Append append) throws IOException {
                            return null;
                        }

                        @Override
                        public Result increment(Increment increment) throws IOException {
                            return null;
                        }

                        @Override
                        public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount) throws IOException {
                            return 0;
                        }

                        @Override
                        public long incrementColumnValue(byte[] row, byte[] family, byte[] qualifier, long amount, Durability durability) throws IOException {
                            return 0;
                        }

                        @Override
                        public void close() throws IOException {
                        }

                        @Override
                        public CoprocessorRpcChannel coprocessorService(byte[] row) {
                            return null;
                        }

                        @Override
                        public <T extends Service, R> Map<byte[], R> coprocessorService(Class<T> service, byte[] startKey, byte[] endKey, Call<T, R> callable) throws ServiceException, Throwable {
                            return null;
                        }

                        @Override
                        public <T extends Service, R> void coprocessorService(Class<T> service, byte[] startKey, byte[] endKey, Call<T, R> callable, Batch.Callback<R> callback) throws ServiceException, Throwable {
                        }

                        @Override
                        public <R extends Message> Map<byte[], R> batchCoprocessorService(MethodDescriptor methodDescriptor, Message request, byte[] startKey, byte[] endKey, R responsePrototype) throws ServiceException, Throwable {
                            return null;
                        }

                        @Override
                        public <R extends Message> void batchCoprocessorService(MethodDescriptor methodDescriptor, Message request, byte[] startKey, byte[] endKey, R responsePrototype, Batch.Callback<R> callback) throws ServiceException, Throwable {
                        }

                        @Override
                        public boolean checkAndMutate(byte[] row, byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value, RowMutations mutation) throws IOException {
                            return false;
                        }

                        @Override
                        public boolean checkAndMutate(byte[] row, byte[] family, byte[] qualifier, CompareOperator op, byte[] value, RowMutations mutation) throws IOException {
                            return false;
                        }

                        @Override
                        public long getRpcTimeout(TimeUnit unit) {
                            return 0;
                        }

                        @Override
                        public int getRpcTimeout() {
                            return 0;
                        }

                        @Override
                        public void setRpcTimeout(int rpcTimeout) {
                        }

                        @Override
                        public long getReadRpcTimeout(TimeUnit unit) {
                            return 0;
                        }

                        @Override
                        public int getReadRpcTimeout() {
                            return 0;
                        }

                        @Override
                        public void setReadRpcTimeout(int readRpcTimeout) {
                        }

                        @Override
                        public long getWriteRpcTimeout(TimeUnit unit) {
                            return 0;
                        }

                        @Override
                        public int getWriteRpcTimeout() {
                            return 0;
                        }

                        @Override
                        public void setWriteRpcTimeout(int writeRpcTimeout) {
                        }

                        @Override
                        public long getOperationTimeout(TimeUnit unit) {
                            return 0;
                        }

                        @Override
                        public int getOperationTimeout() {
                            return 0;
                        }

                        @Override
                        public void setOperationTimeout(int operationTimeout) {
                        }
                    };
                }
            };
        }

        @Override
        public void clearRegionLocationCache() {
        }
    }
}

