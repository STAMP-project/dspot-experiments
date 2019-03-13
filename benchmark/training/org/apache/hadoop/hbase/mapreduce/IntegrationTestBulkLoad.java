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
package org.apache.hadoop.hbase.mapreduce;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.IntegrationTestBase;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hbase.thirdparty.com.google.common.base.Joiner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Bulk Load and MR on a distributed cluster.
 * It starts an MR job that creates linked chains
 *
 * The format of rows is like this:
 * Row Key -> Long
 *
 * L:<< Chain Id >> -> Row Key of the next link in the chain
 * S:<< Chain Id >> -> The step in the chain that his link is.
 * D:<< Chain Id >> -> Random Data.
 *
 * All chains start on row 0.
 * All rk's are > 0.
 *
 * After creating the linked lists they are walked over using a TableMapper based Mapreduce Job.
 *
 * There are a few options exposed:
 *
 * hbase.IntegrationTestBulkLoad.chainLength
 * The number of rows that will be part of each and every chain.
 *
 * hbase.IntegrationTestBulkLoad.numMaps
 * The number of mappers that will be run.  Each mapper creates on linked list chain.
 *
 * hbase.IntegrationTestBulkLoad.numImportRounds
 * How many jobs will be run to create linked lists.
 *
 * hbase.IntegrationTestBulkLoad.tableName
 * The name of the table.
 *
 * hbase.IntegrationTestBulkLoad.replicaCount
 * How many region replicas to configure for the table under test.
 */
@Category(IntegrationTests.class)
public class IntegrationTestBulkLoad extends IntegrationTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBulkLoad.class);

    private static final byte[] CHAIN_FAM = Bytes.toBytes("L");

    private static final byte[] SORT_FAM = Bytes.toBytes("S");

    private static final byte[] DATA_FAM = Bytes.toBytes("D");

    private static String CHAIN_LENGTH_KEY = "hbase.IntegrationTestBulkLoad.chainLength";

    private static int CHAIN_LENGTH = 500000;

    private static String NUM_MAPS_KEY = "hbase.IntegrationTestBulkLoad.numMaps";

    private static int NUM_MAPS = 1;

    private static String NUM_IMPORT_ROUNDS_KEY = "hbase.IntegrationTestBulkLoad.numImportRounds";

    private static int NUM_IMPORT_ROUNDS = 1;

    private static String ROUND_NUM_KEY = "hbase.IntegrationTestBulkLoad.roundNum";

    private static String TABLE_NAME_KEY = "hbase.IntegrationTestBulkLoad.tableName";

    private static String TABLE_NAME = "IntegrationTestBulkLoad";

    private static String NUM_REPLICA_COUNT_KEY = "hbase.IntegrationTestBulkLoad.replicaCount";

    private static int NUM_REPLICA_COUNT_DEFAULT = 1;

    private static final String OPT_LOAD = "load";

    private static final String OPT_CHECK = "check";

    private boolean load = false;

    private boolean check = false;

    public static class SlowMeCoproScanOperations implements RegionCoprocessor , RegionObserver {
        static final AtomicLong sleepTime = new AtomicLong(2000);

        Random r = new Random();

        AtomicLong countOfNext = new AtomicLong(0);

        AtomicLong countOfOpen = new AtomicLong(0);

        public SlowMeCoproScanOperations() {
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preScannerOpen(final ObserverContext<RegionCoprocessorEnvironment> e, final Scan scan) throws IOException {
            if ((countOfOpen.incrementAndGet()) == 2) {
                // slowdown openScanner randomly
                slowdownCode(e);
            }
        }

        @Override
        public boolean preScannerNext(final ObserverContext<RegionCoprocessorEnvironment> e, final InternalScanner s, final List<Result> results, final int limit, final boolean hasMore) throws IOException {
            // this will slow down a certain next operation if the conditions are met. The slowness
            // will allow the call to go to a replica
            countOfNext.incrementAndGet();
            if (((countOfNext.get()) == 0) || ((countOfNext.get()) == 4)) {
                slowdownCode(e);
            }
            return true;
        }

        protected void slowdownCode(final ObserverContext<RegionCoprocessorEnvironment> e) {
            if ((e.getEnvironment().getRegion().getRegionInfo().getReplicaId()) == 0) {
                try {
                    if ((IntegrationTestBulkLoad.SlowMeCoproScanOperations.sleepTime.get()) > 0) {
                        IntegrationTestBulkLoad.LOG.info((("Sleeping for " + (IntegrationTestBulkLoad.SlowMeCoproScanOperations.sleepTime.get())) + " ms"));
                        Thread.sleep(IntegrationTestBulkLoad.SlowMeCoproScanOperations.sleepTime.get());
                    }
                } catch (InterruptedException e1) {
                    IntegrationTestBulkLoad.LOG.error(e1.toString(), e1);
                }
            }
        }
    }

    @Test
    public void testBulkLoad() throws Exception {
        runLoad();
        installSlowingCoproc();
        runCheckWithRetry();
    }

    public static class EmptySplit extends InputSplit implements Writable {
        @Override
        public void write(DataOutput out) throws IOException {
        }

        @Override
        public void readFields(DataInput in) throws IOException {
        }

        @Override
        public long getLength() {
            return 0L;
        }

        @Override
        public String[] getLocations() {
            return new String[0];
        }
    }

    public static class FixedRecordReader<K, V> extends RecordReader<K, V> {
        private int index = -1;

        private K[] keys;

        private V[] values;

        public FixedRecordReader(K[] keys, V[] values) {
            this.keys = keys;
            this.values = values;
        }

        @Override
        public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            return (++(index)) < (keys.length);
        }

        @Override
        public K getCurrentKey() throws IOException, InterruptedException {
            return keys[index];
        }

        @Override
        public V getCurrentValue() throws IOException, InterruptedException {
            return values[index];
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return ((float) (index)) / (keys.length);
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static class ITBulkLoadInputFormat extends InputFormat<LongWritable, LongWritable> {
        @Override
        public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
            int numSplits = context.getConfiguration().getInt(IntegrationTestBulkLoad.NUM_MAPS_KEY, IntegrationTestBulkLoad.NUM_MAPS);
            ArrayList<InputSplit> ret = new ArrayList<>(numSplits);
            for (int i = 0; i < numSplits; ++i) {
                ret.add(new IntegrationTestBulkLoad.EmptySplit());
            }
            return ret;
        }

        @Override
        public RecordReader<LongWritable, LongWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
            int taskId = context.getTaskAttemptID().getTaskID().getId();
            int numMapTasks = context.getConfiguration().getInt(IntegrationTestBulkLoad.NUM_MAPS_KEY, IntegrationTestBulkLoad.NUM_MAPS);
            int numIterations = context.getConfiguration().getInt(IntegrationTestBulkLoad.NUM_IMPORT_ROUNDS_KEY, IntegrationTestBulkLoad.NUM_IMPORT_ROUNDS);
            int iteration = context.getConfiguration().getInt(IntegrationTestBulkLoad.ROUND_NUM_KEY, 0);
            taskId = taskId + (iteration * numMapTasks);
            numMapTasks = numMapTasks * numIterations;
            long chainId = Math.abs(new Random().nextLong());
            chainId = (chainId - (chainId % numMapTasks)) + taskId;// ensure that chainId is unique per task and across iterations

            LongWritable[] keys = new LongWritable[]{ new LongWritable(chainId) };
            return new IntegrationTestBulkLoad.FixedRecordReader(keys, keys);
        }
    }

    /**
     * Mapper that creates a linked list of KeyValues.
     *
     * Each map task generates one linked list.
     * All lists start on row key 0L.
     * All lists should be CHAIN_LENGTH long.
     */
    public static class LinkedListCreationMapper extends Mapper<LongWritable, LongWritable, ImmutableBytesWritable, KeyValue> {
        private Random rand = new Random();

        @Override
        protected void map(LongWritable key, LongWritable value, Context context) throws IOException, InterruptedException {
            long chainId = value.get();
            IntegrationTestBulkLoad.LOG.info(("Starting mapper with chainId:" + chainId));
            byte[] chainIdArray = Bytes.toBytes(chainId);
            long currentRow = 0;
            long chainLength = context.getConfiguration().getLong(IntegrationTestBulkLoad.CHAIN_LENGTH_KEY, IntegrationTestBulkLoad.CHAIN_LENGTH);
            long nextRow = getNextRow(0, chainLength);
            for (long i = 0; i < chainLength; i++) {
                byte[] rk = Bytes.toBytes(currentRow);
                // Next link in the chain.
                KeyValue linkKv = new KeyValue(rk, IntegrationTestBulkLoad.CHAIN_FAM, chainIdArray, Bytes.toBytes(nextRow));
                // What link in the chain this is.
                KeyValue sortKv = new KeyValue(rk, IntegrationTestBulkLoad.SORT_FAM, chainIdArray, Bytes.toBytes(i));
                // Added data so that large stores are created.
                KeyValue dataKv = new KeyValue(rk, IntegrationTestBulkLoad.DATA_FAM, chainIdArray, Bytes.toBytes(RandomStringUtils.randomAlphabetic(50)));
                // Emit the key values.
                context.write(new ImmutableBytesWritable(rk), linkKv);
                context.write(new ImmutableBytesWritable(rk), sortKv);
                context.write(new ImmutableBytesWritable(rk), dataKv);
                // Move to the next row.
                currentRow = nextRow;
                nextRow = getNextRow((i + 1), chainLength);
            }
        }

        /**
         * Returns a unique row id within this chain for this index
         */
        private long getNextRow(long index, long chainLength) {
            long nextRow = Math.abs(rand.nextLong());
            // use significant bits from the random number, but pad with index to ensure it is unique
            // this also ensures that we do not reuse row = 0
            // row collisions from multiple mappers are fine, since we guarantee unique chainIds
            nextRow = (nextRow - (nextRow % chainLength)) + index;
            return nextRow;
        }
    }

    /**
     * Writable class used as the key to group links in the linked list.
     *
     * Used as the key emited from a pass over the table.
     */
    public static class LinkKey implements WritableComparable<IntegrationTestBulkLoad.LinkKey> {
        private Long chainId;

        public Long getOrder() {
            return order;
        }

        public Long getChainId() {
            return chainId;
        }

        private Long order;

        public LinkKey() {
        }

        public LinkKey(long chainId, long order) {
            this.chainId = chainId;
            this.order = order;
        }

        @Override
        public int compareTo(IntegrationTestBulkLoad.LinkKey linkKey) {
            int res = getChainId().compareTo(linkKey.getChainId());
            if (res == 0) {
                res = getOrder().compareTo(linkKey.getOrder());
            }
            return res;
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
            WritableUtils.writeVLong(dataOutput, chainId);
            WritableUtils.writeVLong(dataOutput, order);
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
            chainId = WritableUtils.readVLong(dataInput);
            order = WritableUtils.readVLong(dataInput);
        }
    }

    /**
     * Writable used as the value emitted from a pass over the hbase table.
     */
    public static class LinkChain implements WritableComparable<IntegrationTestBulkLoad.LinkChain> {
        public Long getNext() {
            return next;
        }

        public Long getRk() {
            return rk;
        }

        public LinkChain() {
        }

        public LinkChain(Long rk, Long next) {
            this.rk = rk;
            this.next = next;
        }

        private Long rk;

        private Long next;

        @Override
        public int compareTo(IntegrationTestBulkLoad.LinkChain linkChain) {
            int res = getRk().compareTo(linkChain.getRk());
            if (res == 0) {
                res = getNext().compareTo(linkChain.getNext());
            }
            return res;
        }

        @Override
        public void write(DataOutput dataOutput) throws IOException {
            WritableUtils.writeVLong(dataOutput, rk);
            WritableUtils.writeVLong(dataOutput, next);
        }

        @Override
        public void readFields(DataInput dataInput) throws IOException {
            rk = WritableUtils.readVLong(dataInput);
            next = WritableUtils.readVLong(dataInput);
        }
    }

    /**
     * Class to figure out what partition to send a link in the chain to.  This is based upon
     * the linkKey's ChainId.
     */
    public static class NaturalKeyPartitioner extends Partitioner<IntegrationTestBulkLoad.LinkKey, IntegrationTestBulkLoad.LinkChain> {
        @Override
        public int getPartition(IntegrationTestBulkLoad.LinkKey linkKey, IntegrationTestBulkLoad.LinkChain linkChain, int numPartitions) {
            int hash = linkKey.getChainId().hashCode();
            return Math.abs((hash % numPartitions));
        }
    }

    /**
     * Comparator used to figure out if a linkKey should be grouped together.  This is based upon the
     * linkKey's ChainId.
     */
    public static class NaturalKeyGroupingComparator extends WritableComparator {
        protected NaturalKeyGroupingComparator() {
            super(IntegrationTestBulkLoad.LinkKey.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntegrationTestBulkLoad.LinkKey k1 = ((IntegrationTestBulkLoad.LinkKey) (w1));
            IntegrationTestBulkLoad.LinkKey k2 = ((IntegrationTestBulkLoad.LinkKey) (w2));
            return k1.getChainId().compareTo(k2.getChainId());
        }
    }

    /**
     * Comparator used to order linkKeys so that they are passed to a reducer in order.  This is based
     * upon linkKey ChainId and Order.
     */
    public static class CompositeKeyComparator extends WritableComparator {
        protected CompositeKeyComparator() {
            super(IntegrationTestBulkLoad.LinkKey.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntegrationTestBulkLoad.LinkKey k1 = ((IntegrationTestBulkLoad.LinkKey) (w1));
            IntegrationTestBulkLoad.LinkKey k2 = ((IntegrationTestBulkLoad.LinkKey) (w2));
            return k1.compareTo(k2);
        }
    }

    /**
     * Mapper to pass over the table.
     *
     * For every row there could be multiple chains that landed on this row. So emit a linkKey
     * and value for each.
     */
    public static class LinkedListCheckingMapper extends TableMapper<IntegrationTestBulkLoad.LinkKey, IntegrationTestBulkLoad.LinkChain> {
        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            long longRk = Bytes.toLong(value.getRow());
            for (Map.Entry<byte[], byte[]> entry : value.getFamilyMap(IntegrationTestBulkLoad.CHAIN_FAM).entrySet()) {
                long chainId = Bytes.toLong(entry.getKey());
                long next = Bytes.toLong(entry.getValue());
                Cell c = value.getColumnCells(IntegrationTestBulkLoad.SORT_FAM, entry.getKey()).get(0);
                long order = Bytes.toLong(CellUtil.cloneValue(c));
                context.write(new IntegrationTestBulkLoad.LinkKey(chainId, order), new IntegrationTestBulkLoad.LinkChain(longRk, next));
            }
        }
    }

    /**
     * Class that does the actual checking of the links.
     *
     * All links in the chain should be grouped and sorted when sent to this class.  Then the chain
     * will be traversed making sure that no link is missing and that the chain is the correct length.
     *
     * This will throw an exception if anything is not correct.  That causes the job to fail if any
     * data is corrupt.
     */
    public static class LinkedListCheckingReducer extends Reducer<IntegrationTestBulkLoad.LinkKey, IntegrationTestBulkLoad.LinkChain, NullWritable, NullWritable> {
        @Override
        protected void reduce(IntegrationTestBulkLoad.LinkKey key, Iterable<IntegrationTestBulkLoad.LinkChain> values, Context context) throws IOException, InterruptedException {
            long next = -1L;
            long prev = -1L;
            long count = 0L;
            for (IntegrationTestBulkLoad.LinkChain lc : values) {
                if (next == (-1)) {
                    if ((lc.getRk()) != 0L) {
                        String msg = (((("Chains should all start at rk 0, but read rk " + (lc.getRk())) + ". Chain:") + (key.chainId)) + ", order:") + (key.order);
                        IntegrationTestBulkLoad.LinkedListCheckingReducer.logError(msg, context);
                        throw new RuntimeException(msg);
                    }
                    next = lc.getNext();
                } else {
                    if (next != (lc.getRk())) {
                        String msg = (((((((("Missing a link in the chain. Prev rk " + prev) + " was, expecting ") + next) + " but got ") + (lc.getRk())) + ". Chain:") + (key.chainId)) + ", order:") + (key.order);
                        IntegrationTestBulkLoad.LinkedListCheckingReducer.logError(msg, context);
                        throw new RuntimeException(msg);
                    }
                    prev = lc.getRk();
                    next = lc.getNext();
                }
                count++;
            }
            int expectedChainLen = context.getConfiguration().getInt(IntegrationTestBulkLoad.CHAIN_LENGTH_KEY, IntegrationTestBulkLoad.CHAIN_LENGTH);
            if (count != expectedChainLen) {
                String msg = (((((("Chain wasn't the correct length.  Expected " + expectedChainLen) + " got ") + count) + ". Chain:") + (key.chainId)) + ", order:") + (key.order);
                IntegrationTestBulkLoad.LinkedListCheckingReducer.logError(msg, context);
                throw new RuntimeException(msg);
            }
        }

        private static void logError(String msg, Context context) throws IOException {
            TableName table = IntegrationTestBulkLoad.getTableName(context.getConfiguration());
            IntegrationTestBulkLoad.LOG.error(("Failure in chain verification: " + msg));
            try (Connection connection = ConnectionFactory.createConnection(context.getConfiguration());Admin admin = connection.getAdmin()) {
                IntegrationTestBulkLoad.LOG.error(("cluster status:\n" + (admin.getClusterStatus())));
                IntegrationTestBulkLoad.LOG.error(("table regions:\n" + (Joiner.on("\n").join(admin.getTableRegions(table)))));
            }
        }
    }
}

