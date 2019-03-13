/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.test;


import HConstants.EMPTY_BYTE_ARRAY;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Random;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.IntegrationTestBase;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.WALPlayer;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A large test which loads a lot of data that has internal references, and
 * verifies the data.
 *
 * In load step, 200 map tasks are launched, which in turn write loadmapper.num_to_write
 * (default 100K) rows to an hbase table. Rows are written in blocks, for a total of
 * 100 blocks. Each row in a block, contains loadmapper.backrefs (default 50) references
 * to random rows in the prev block.
 *
 * Verify step is scans the table, and verifies that for every referenced row, the row is
 * actually there (no data loss). Failed rows are output from reduce to be saved in the
 * job output dir in hdfs and inspected later.
 *
 * This class can be run as a unit test, as an integration test, or from the command line
 *
 * Originally taken from Apache Bigtop.
 */
@Category(IntegrationTests.class)
public class IntegrationTestLoadAndVerify extends IntegrationTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestLoadAndVerify.class);

    private static final String TEST_NAME = "IntegrationTestLoadAndVerify";

    private static final byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static final byte[] TEST_QUALIFIER = Bytes.toBytes("q1");

    private static final String NUM_TO_WRITE_KEY = "loadmapper.num_to_write";

    private static final long NUM_TO_WRITE_DEFAULT = 100 * 1000;

    private static final String TABLE_NAME_KEY = "loadmapper.table";

    private static final String TABLE_NAME_DEFAULT = "table";

    private static final String NUM_BACKREFS_KEY = "loadmapper.backrefs";

    private static final int NUM_BACKREFS_DEFAULT = 50;

    private static final String NUM_MAP_TASKS_KEY = "loadmapper.map.tasks";

    private static final String NUM_REDUCE_TASKS_KEY = "verify.reduce.tasks";

    private static final int NUM_MAP_TASKS_DEFAULT = 200;

    private static final int NUM_REDUCE_TASKS_DEFAULT = 35;

    private static final int SCANNER_CACHING = 500;

    private static final int MISSING_ROWS_TO_LOG = 10;// YARN complains when too many counters


    private String toRun = null;

    private String keysDir = null;

    private enum Counters {

        ROWS_WRITTEN,
        REFERENCES_WRITTEN,
        REFERENCES_CHECKED;}

    public static class LoadMapper extends Mapper<NullWritable, NullWritable, NullWritable, NullWritable> {
        protected long recordsToWrite;

        protected Connection connection;

        protected BufferedMutator mutator;

        protected Configuration conf;

        protected int numBackReferencesPerRow;

        protected String shortTaskId;

        protected Random rand = new Random();

        protected Counter rowsWritten;

        protected Counter refsWritten;

        @Override
        public void setup(Context context) throws IOException {
            conf = context.getConfiguration();
            recordsToWrite = conf.getLong(IntegrationTestLoadAndVerify.NUM_TO_WRITE_KEY, IntegrationTestLoadAndVerify.NUM_TO_WRITE_DEFAULT);
            String tableName = conf.get(IntegrationTestLoadAndVerify.TABLE_NAME_KEY, IntegrationTestLoadAndVerify.TABLE_NAME_DEFAULT);
            numBackReferencesPerRow = conf.getInt(IntegrationTestLoadAndVerify.NUM_BACKREFS_KEY, IntegrationTestLoadAndVerify.NUM_BACKREFS_DEFAULT);
            this.connection = ConnectionFactory.createConnection(conf);
            mutator = connection.getBufferedMutator(writeBufferSize(((4 * 1024) * 1024)));
            String taskId = conf.get("mapreduce.task.attempt.id");
            Matcher matcher = Pattern.compile(".+_m_(\\d+_\\d+)").matcher(taskId);
            if (!(matcher.matches())) {
                throw new RuntimeException(("Strange task ID: " + taskId));
            }
            shortTaskId = matcher.group(1);
            rowsWritten = context.getCounter(IntegrationTestLoadAndVerify.Counters.ROWS_WRITTEN);
            refsWritten = context.getCounter(IntegrationTestLoadAndVerify.Counters.REFERENCES_WRITTEN);
        }

        @Override
        public void cleanup(Context context) throws IOException {
            mutator.close();
            connection.close();
        }

        @Override
        protected void map(NullWritable key, NullWritable value, Context context) throws IOException, InterruptedException {
            String suffix = "/" + (shortTaskId);
            byte[] row = Bytes.add(new byte[8], Bytes.toBytes(suffix));
            int BLOCK_SIZE = ((int) ((recordsToWrite) / 100));
            for (long i = 0; i < (recordsToWrite);) {
                long blockStart = i;
                for (long idxInBlock = 0; (idxInBlock < BLOCK_SIZE) && (i < (recordsToWrite)); idxInBlock++ , i++) {
                    long byteSwapped = IntegrationTestLoadAndVerify.swapLong(i);
                    Bytes.putLong(row, 0, byteSwapped);
                    Put p = new Put(row);
                    p.addColumn(IntegrationTestLoadAndVerify.TEST_FAMILY, IntegrationTestLoadAndVerify.TEST_QUALIFIER, EMPTY_BYTE_ARRAY);
                    if (blockStart > 0) {
                        for (int j = 0; j < (numBackReferencesPerRow); j++) {
                            long referredRow = (blockStart - BLOCK_SIZE) + (rand.nextInt(BLOCK_SIZE));
                            Bytes.putLong(row, 0, IntegrationTestLoadAndVerify.swapLong(referredRow));
                            p.addColumn(IntegrationTestLoadAndVerify.TEST_FAMILY, row, EMPTY_BYTE_ARRAY);
                        }
                        refsWritten.increment(1);
                    }
                    rowsWritten.increment(1);
                    mutator.mutate(p);
                    if ((i % 100) == 0) {
                        context.setStatus((((("Written " + i) + "/") + (recordsToWrite)) + " records"));
                        context.progress();
                    }
                }
                // End of block, flush all of them before we start writing anything
                // pointing to these!
                mutator.flush();
            }
        }
    }

    public static class VerifyMapper extends TableMapper<BytesWritable, BytesWritable> {
        static final BytesWritable EMPTY = new BytesWritable(HConstants.EMPTY_BYTE_ARRAY);

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            BytesWritable bwKey = new BytesWritable(key.get());
            BytesWritable bwVal = new BytesWritable();
            for (Cell kv : value.listCells()) {
                if ((Bytes.compareTo(IntegrationTestLoadAndVerify.TEST_QUALIFIER, 0, IntegrationTestLoadAndVerify.TEST_QUALIFIER.length, kv.getQualifierArray(), kv.getQualifierOffset(), kv.getQualifierLength())) == 0) {
                    context.write(bwKey, IntegrationTestLoadAndVerify.VerifyMapper.EMPTY);
                } else {
                    bwVal.set(kv.getQualifierArray(), kv.getQualifierOffset(), kv.getQualifierLength());
                    context.write(bwVal, bwKey);
                }
            }
        }
    }

    public static class VerifyReducer extends Reducer<BytesWritable, BytesWritable, Text, Text> {
        private Counter refsChecked;

        private Counter rowsWritten;

        @Override
        public void setup(Context context) throws IOException {
            refsChecked = context.getCounter(IntegrationTestLoadAndVerify.Counters.REFERENCES_CHECKED);
            rowsWritten = context.getCounter(IntegrationTestLoadAndVerify.Counters.ROWS_WRITTEN);
        }

        @Override
        protected void reduce(BytesWritable referredRow, Iterable<BytesWritable> referrers, VerifyReducer.Context ctx) throws IOException, InterruptedException {
            boolean gotOriginalRow = false;
            int refCount = 0;
            for (BytesWritable ref : referrers) {
                if ((ref.getLength()) == 0) {
                    assert !gotOriginalRow;
                    gotOriginalRow = true;
                } else {
                    refCount++;
                }
            }
            refsChecked.increment(refCount);
            if (!gotOriginalRow) {
                String parsedRow = makeRowReadable(referredRow.getBytes(), referredRow.getLength());
                String binRow = Bytes.toStringBinary(referredRow.getBytes(), 0, referredRow.getLength());
                IntegrationTestLoadAndVerify.LOG.error(("Reference error row " + parsedRow));
                ctx.write(new Text(binRow), new Text(parsedRow));
                rowsWritten.increment(1);
            }
        }

        private String makeRowReadable(byte[] bytes, int length) {
            long rowIdx = IntegrationTestLoadAndVerify.swapLong(Bytes.toLong(bytes, 0));
            String suffix = Bytes.toString(bytes, 8, (length - 8));
            return (("Row #" + rowIdx) + " suffix ") + suffix;
        }
    }

    /**
     * Tool to search missing rows in WALs and hfiles.
     * Pass in file or dir of keys to search for. Key file must have been written by Verify step
     * (we depend on the format it writes out. We'll read them in and then search in hbase
     * WALs and oldWALs dirs (Some of this is TODO).
     */
    public static class WALSearcher extends WALPlayer {
        public WALSearcher(Configuration conf) {
            super(conf);
        }

        /**
         * The actual searcher mapper.
         */
        public static class WALMapperSearcher extends WALMapper {
            private SortedSet<byte[]> keysToFind;

            private AtomicInteger rows = new AtomicInteger(0);

            @Override
            public void setup(Mapper.Context context) throws IOException {
                super.setup(context);
                try {
                    this.keysToFind = IntegrationTestLoadAndVerify.readKeysToSearch(context.getConfiguration());
                    IntegrationTestLoadAndVerify.LOG.info(("Loaded keys to find: count=" + (this.keysToFind.size())));
                } catch (InterruptedException e) {
                    throw new InterruptedIOException(e.toString());
                }
            }

            @Override
            protected boolean filter(Context context, Cell cell) {
                // TODO: Can I do a better compare than this copying out key?
                byte[] row = new byte[cell.getRowLength()];
                System.arraycopy(cell.getRowArray(), cell.getRowOffset(), row, 0, cell.getRowLength());
                boolean b = this.keysToFind.contains(row);
                if (b) {
                    String keyStr = Bytes.toStringBinary(row);
                    try {
                        IntegrationTestLoadAndVerify.LOG.info(((("Found cell=" + cell) + " , walKey=") + (context.getCurrentKey())));
                    } catch (IOException | InterruptedException e) {
                        IntegrationTestLoadAndVerify.LOG.warn(e.toString(), e);
                    }
                    if ((rows.addAndGet(1)) < (IntegrationTestLoadAndVerify.MISSING_ROWS_TO_LOG)) {
                        context.getCounter(IntegrationTestLoadAndVerify.FOUND_GROUP_KEY, keyStr).increment(1);
                    }
                    context.getCounter(IntegrationTestLoadAndVerify.FOUND_GROUP_KEY, "CELL_WITH_MISSING_ROW").increment(1);
                }
                return b;
            }
        }

        // Put in place the above WALMapperSearcher.
        @Override
        public Job createSubmittableJob(String[] args) throws IOException {
            Job job = super.createSubmittableJob(args);
            // Call my class instead.
            job.setJarByClass(IntegrationTestLoadAndVerify.WALSearcher.WALMapperSearcher.class);
            job.setMapperClass(IntegrationTestLoadAndVerify.WALSearcher.WALMapperSearcher.class);
            job.setOutputFormatClass(NullOutputFormat.class);
            return job;
        }
    }

    static final String FOUND_GROUP_KEY = "Found";

    static final String SEARCHER_INPUTDIR_KEY = "searcher.keys.inputdir";

    @Test
    public void testLoadAndVerify() throws Exception {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(IntegrationTestLoadAndVerify.TEST_NAME));
        htd.addFamily(new HColumnDescriptor(IntegrationTestLoadAndVerify.TEST_FAMILY));
        Admin admin = getTestingUtil(getConf()).getAdmin();
        admin.createTable(htd, Bytes.toBytes(0L), Bytes.toBytes((-1L)), 40);
        doLoad(getConf(), htd);
        doVerify(getConf(), htd);
        // Only disable and drop if we succeeded to verify - otherwise it's useful
        // to leave it around for post-mortem
        getTestingUtil(getConf()).deleteTable(htd.getTableName());
    }
}

