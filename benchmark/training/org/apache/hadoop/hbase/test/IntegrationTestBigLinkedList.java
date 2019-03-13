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
package org.apache.hadoop.hbase.test;


import Bytes.SIZEOF_SHORT;
import ConnectionConfiguration.MAX_KEYVALUE_SIZE_DEFAULT;
import ConnectionConfiguration.MAX_KEYVALUE_SIZE_KEY;
import FlushPolicyFactory.HBASE_FLUSH_POLICY_KEY;
import HBaseTestingUtility.DEFAULT_REGIONS_PER_SERVER;
import HBaseTestingUtility.PRESPLIT_TEST_TABLE;
import HBaseTestingUtility.PRESPLIT_TEST_TABLE_KEY;
import HBaseTestingUtility.REGIONS_PER_SERVER_KEY;
import HConstants.HBASE_DIR;
import Option.LIVE_SERVERS;
import SequenceFileAsBinaryInputFormat.SequenceFileAsBinaryRecordReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.IntegrationTestBase;
import org.apache.hadoop.hbase.IntegrationTestingUtility;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.fs.HFileSystem;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.WALPlayer;
import org.apache.hadoop.hbase.regionserver.FlushAllLargeStoresPolicy;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.AbstractHBaseTool;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CommonFSUtils;
import org.apache.hadoop.hbase.util.Random64;
import org.apache.hadoop.hbase.util.RegionSplitter;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsBinaryInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileAsBinaryOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hbase.thirdparty.com.google.common.base.Preconditions;
import org.apache.hbase.thirdparty.org.apache.commons.cli.CommandLine;
import org.apache.hbase.thirdparty.org.apache.commons.cli.GnuParser;
import org.apache.hbase.thirdparty.org.apache.commons.cli.HelpFormatter;
import org.apache.hbase.thirdparty.org.apache.commons.cli.Options;
import org.apache.hbase.thirdparty.org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is an integration test borrowed from goraci, written by Keith Turner,
 * which is in turn inspired by the Accumulo test called continous ingest (ci).
 * The original source code can be found here:
 * https://github.com/keith-turner/goraci
 * https://github.com/enis/goraci/
 *
 * Apache Accumulo [0] has a simple test suite that verifies that data is not
 * lost at scale. This test suite is called continuous ingest. This test runs
 * many ingest clients that continually create linked lists containing 25
 * million nodes. At some point the clients are stopped and a map reduce job is
 * run to ensure no linked list has a hole. A hole indicates data was lost.??
 *
 * The nodes in the linked list are random. This causes each linked list to
 * spread across the table. Therefore if one part of a table loses data, then it
 * will be detected by references in another part of the table.
 *
 * THE ANATOMY OF THE TEST
 *
 * Below is rough sketch of how data is written. For specific details look at
 * the Generator code.
 *
 * 1 Write out 1 million nodes? 2 Flush the client? 3 Write out 1 million that
 * reference previous million? 4 If this is the 25th set of 1 million nodes,
 * then update 1st set of million to point to last? 5 goto 1
 *
 * The key is that nodes only reference flushed nodes. Therefore a node should
 * never reference a missing node, even if the ingest client is killed at any
 * point in time.
 *
 * When running this test suite w/ Accumulo there is a script running in
 * parallel called the Aggitator that randomly and continuously kills server
 * processes.?? The outcome was that many data loss bugs were found in Accumulo
 * by doing this.? This test suite can also help find bugs that impact uptime
 * and stability when? run for days or weeks.??
 *
 * This test suite consists the following? - a few Java programs? - a little
 * helper script to run the java programs - a maven script to build it.??
 *
 * When generating data, its best to have each map task generate a multiple of
 * 25 million. The reason for this is that circular linked list are generated
 * every 25M. Not generating a multiple in 25M will result in some nodes in the
 * linked list not having references. The loss of an unreferenced node can not
 * be detected.
 *
 *
 * Below is a description of the Java programs
 *
 * Generator - A map only job that generates data. As stated previously,?its best to generate data
 * in multiples of 25M. An option is also available to allow concurrent walkers to select and walk
 * random flushed loops during this phase.
 *
 * Verify - A map reduce job that looks for holes. Look at the counts after running. REFERENCED and
 * UNREFERENCED are? ok, any UNDEFINED counts are bad. Do not run at the? same
 * time as the Generator.
 *
 * Walker - A standalone program that start following a linked list? and emits timing info.??
 *
 * Print - A standalone program that prints nodes in the linked list
 *
 * Delete - A standalone program that deletes a single node
 *
 * This class can be run as a unit test, as an integration test, or from the command line
 *
 * ex:
 * ./hbase org.apache.hadoop.hbase.test.IntegrationTestBigLinkedList
 *    loop 2 1 100000 /temp 1 1000 50 1 0
 */
@Category(IntegrationTests.class)
public class IntegrationTestBigLinkedList extends IntegrationTestBase {
    protected static final byte[] NO_KEY = new byte[1];

    protected static String TABLE_NAME_KEY = "IntegrationTestBigLinkedList.table";

    protected static String DEFAULT_TABLE_NAME = "IntegrationTestBigLinkedList";

    protected static byte[] FAMILY_NAME = Bytes.toBytes("meta");

    private static byte[] BIG_FAMILY_NAME = Bytes.toBytes("big");

    private static byte[] TINY_FAMILY_NAME = Bytes.toBytes("tiny");

    // link to the id of the prev node in the linked list
    protected static final byte[] COLUMN_PREV = Bytes.toBytes("prev");

    // identifier of the mapred task that generated this row
    protected static final byte[] COLUMN_CLIENT = Bytes.toBytes("client");

    // the id of the row within the same client.
    protected static final byte[] COLUMN_COUNT = Bytes.toBytes("count");

    /**
     * How many rows to write per map task. This has to be a multiple of 25M
     */
    private static final String GENERATOR_NUM_ROWS_PER_MAP_KEY = "IntegrationTestBigLinkedList.generator.num_rows";

    private static final String GENERATOR_NUM_MAPPERS_KEY = "IntegrationTestBigLinkedList.generator.map.tasks";

    private static final String GENERATOR_WIDTH_KEY = "IntegrationTestBigLinkedList.generator.width";

    private static final String GENERATOR_WRAP_KEY = "IntegrationTestBigLinkedList.generator.wrap";

    private static final String CONCURRENT_WALKER_KEY = "IntegrationTestBigLinkedList.generator.concurrentwalkers";

    protected int NUM_SLAVES_BASE = 3;// number of slaves for the cluster


    private static final int MISSING_ROWS_TO_LOG = 10;// YARN complains when too many counters


    private static final int WIDTH_DEFAULT = 1000000;

    private static final int WRAP_DEFAULT = 25;

    private static final int ROWKEY_LENGTH = 16;

    private static final int CONCURRENT_WALKER_DEFAULT = 0;

    protected String toRun;

    protected String[] otherArgs;

    static class CINode {
        byte[] key;

        byte[] prev;

        String client;

        long count;
    }

    /**
     * A Map only job that generates random linked list and stores them.
     */
    static class Generator extends Configured implements Tool {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedList.Generator.class);

        /**
         * Set this configuration if you want to test single-column family flush works. If set, we will
         * add a big column family and a small column family on either side of the usual ITBLL 'meta'
         * column family. When we write out the ITBLL, we will also add to the big column family a value
         * bigger than that for ITBLL and for small, something way smaller. The idea is that when
         * flush-by-column family rather than by region is enabled, we can see if ITBLL is broke in any
         * way. Here is how you would pass it:
         * <p>
         * $ ./bin/hbase org.apache.hadoop.hbase.test.IntegrationTestBigLinkedList
         * -Dgenerator.multiple.columnfamilies=true generator 1 10 g
         */
        public static final String MULTIPLE_UNEVEN_COLUMNFAMILIES_KEY = "generator.multiple.columnfamilies";

        /**
         * Set this configuration if you want to scale up the size of test data quickly.
         * <p>
         * $ ./bin/hbase org.apache.hadoop.hbase.test.IntegrationTestBigLinkedList
         * -Dgenerator.big.family.value.size=1024 generator 1 10 output
         */
        public static final String BIG_FAMILY_VALUE_SIZE_KEY = "generator.big.family.value.size";

        public static enum Counts {

            SUCCESS,
            TERMINATING,
            UNDEFINED,
            IOEXCEPTION;}

        public static final String USAGE = (((("Usage : " + (IntegrationTestBigLinkedList.Generator.class.getSimpleName())) + " <num mappers> <num nodes per map> <tmp output dir> [<width> <wrap multiplier>") + " <num walker threads>] \n") + "where <num nodes per map> should be a multiple of width*wrap multiplier, 25M by default \n") + "walkers will verify random flushed loop during Generation.";

        public Job job;

        static class GeneratorInputFormat extends InputFormat<BytesWritable, NullWritable> {
            static class GeneratorInputSplit extends InputSplit implements Writable {
                @Override
                public long getLength() throws IOException, InterruptedException {
                    return 1;
                }

                @Override
                public String[] getLocations() throws IOException, InterruptedException {
                    return new String[0];
                }

                @Override
                public void readFields(DataInput arg0) throws IOException {
                }

                @Override
                public void write(DataOutput arg0) throws IOException {
                }
            }

            static class GeneratorRecordReader extends RecordReader<BytesWritable, NullWritable> {
                private long count;

                private long numNodes;

                private Random64 rand;

                @Override
                public void close() throws IOException {
                }

                @Override
                public BytesWritable getCurrentKey() throws IOException, InterruptedException {
                    byte[] bytes = new byte[IntegrationTestBigLinkedList.ROWKEY_LENGTH];
                    rand.nextBytes(bytes);
                    return new BytesWritable(bytes);
                }

                @Override
                public NullWritable getCurrentValue() throws IOException, InterruptedException {
                    return NullWritable.get();
                }

                @Override
                public float getProgress() throws IOException, InterruptedException {
                    return ((float) ((count) / ((double) (numNodes))));
                }

                @Override
                public void initialize(InputSplit arg0, TaskAttemptContext context) throws IOException, InterruptedException {
                    numNodes = context.getConfiguration().getLong(IntegrationTestBigLinkedList.GENERATOR_NUM_ROWS_PER_MAP_KEY, 25000000);
                    // Use Random64 to avoid issue described in HBASE-21256.
                    rand = new Random64();
                }

                @Override
                public boolean nextKeyValue() throws IOException, InterruptedException {
                    return ((count)++) < (numNodes);
                }
            }

            @Override
            public RecordReader<BytesWritable, NullWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
                IntegrationTestBigLinkedList.Generator.GeneratorInputFormat.GeneratorRecordReader rr = new IntegrationTestBigLinkedList.Generator.GeneratorInputFormat.GeneratorRecordReader();
                rr.initialize(split, context);
                return rr;
            }

            @Override
            public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
                int numMappers = job.getConfiguration().getInt(IntegrationTestBigLinkedList.GENERATOR_NUM_MAPPERS_KEY, 1);
                ArrayList<InputSplit> splits = new ArrayList<>(numMappers);
                for (int i = 0; i < numMappers; i++) {
                    splits.add(new IntegrationTestBigLinkedList.Generator.GeneratorInputFormat.GeneratorInputSplit());
                }
                return splits;
            }
        }

        /**
         * Ensure output files from prev-job go to map inputs for current job
         */
        static class OneFilePerMapperSFIF<K, V> extends SequenceFileInputFormat<K, V> {
            @Override
            protected boolean isSplitable(JobContext context, Path filename) {
                return false;
            }
        }

        /**
         * Some ASCII art time:
         * <p>
         * [ . . . ] represents one batch of random longs of length WIDTH
         * <pre>
         *                _________________________
         *               |                  ______ |
         *               |                 |      ||
         *             .-+-----------------+-----.||
         *             | |                 |     |||
         * first   = [ . . . . . . . . . . . ]   |||
         *             ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^     |||
         *             | | | | | | | | | | |     |||
         * prev    = [ . . . . . . . . . . . ]   |||
         *             ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^     |||
         *             | | | | | | | | | | |     |||
         * current = [ . . . . . . . . . . . ]   |||
         *                                       |||
         * ...                                   |||
         *                                       |||
         * last    = [ . . . . . . . . . . . ]   |||
         *             ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^_____|||
         *             |                 |________||
         *             |___________________________|
         * </pre>
         */
        static class GeneratorMapper extends Mapper<BytesWritable, NullWritable, NullWritable, NullWritable> {
            byte[][] first = null;

            byte[][] prev = null;

            byte[][] current = null;

            byte[] id;

            long count = 0;

            int i;

            BufferedMutator mutator;

            Connection connection;

            long numNodes;

            long wrap;

            int width;

            boolean multipleUnevenColumnFamilies;

            byte[] tinyValue = new byte[]{ 't' };

            byte[] bigValue = null;

            Configuration conf;

            volatile boolean walkersStop;

            int numWalkers;

            volatile List<Long> flushedLoops = new ArrayList<>();

            List<Thread> walkers = new ArrayList<>();

            @Override
            protected void setup(Context context) throws IOException, InterruptedException {
                id = Bytes.toBytes(((("Job: " + (context.getJobID())) + " Task: ") + (context.getTaskAttemptID())));
                this.connection = ConnectionFactory.createConnection(context.getConfiguration());
                instantiateHTable();
                this.width = context.getConfiguration().getInt(IntegrationTestBigLinkedList.GENERATOR_WIDTH_KEY, IntegrationTestBigLinkedList.WIDTH_DEFAULT);
                current = new byte[this.width][];
                int wrapMultiplier = context.getConfiguration().getInt(IntegrationTestBigLinkedList.GENERATOR_WRAP_KEY, IntegrationTestBigLinkedList.WRAP_DEFAULT);
                this.wrap = ((long) (wrapMultiplier)) * (width);
                this.numNodes = context.getConfiguration().getLong(IntegrationTestBigLinkedList.GENERATOR_NUM_ROWS_PER_MAP_KEY, (((long) (IntegrationTestBigLinkedList.WIDTH_DEFAULT)) * (IntegrationTestBigLinkedList.WRAP_DEFAULT)));
                if ((this.numNodes) < (this.wrap)) {
                    this.wrap = this.numNodes;
                }
                this.multipleUnevenColumnFamilies = IntegrationTestBigLinkedList.isMultiUnevenColumnFamilies(context.getConfiguration());
                this.numWalkers = context.getConfiguration().getInt(IntegrationTestBigLinkedList.CONCURRENT_WALKER_KEY, IntegrationTestBigLinkedList.CONCURRENT_WALKER_DEFAULT);
                this.walkersStop = false;
                this.conf = context.getConfiguration();
                if (multipleUnevenColumnFamilies) {
                    int n = context.getConfiguration().getInt(IntegrationTestBigLinkedList.Generator.BIG_FAMILY_VALUE_SIZE_KEY, 256);
                    int limit = context.getConfiguration().getInt(MAX_KEYVALUE_SIZE_KEY, MAX_KEYVALUE_SIZE_DEFAULT);
                    Preconditions.checkArgument((n <= limit), "%s(%s) > %s(%s)", IntegrationTestBigLinkedList.Generator.BIG_FAMILY_VALUE_SIZE_KEY, n, MAX_KEYVALUE_SIZE_KEY, limit);
                    bigValue = new byte[n];
                    ThreadLocalRandom.current().nextBytes(bigValue);
                    IntegrationTestBigLinkedList.Generator.LOG.info((("Create a bigValue with " + n) + " bytes."));
                }
                Preconditions.checkArgument(((numNodes) > 0), "numNodes(%s) <= 0", numNodes);
                Preconditions.checkArgument((((numNodes) % (width)) == 0), "numNodes(%s) mod width(%s) != 0", numNodes, width);
                Preconditions.checkArgument((((numNodes) % (wrap)) == 0), "numNodes(%s) mod wrap(%s) != 0", numNodes, wrap);
            }

            protected void instantiateHTable() throws IOException {
                mutator = connection.getBufferedMutator(writeBufferSize(((4 * 1024) * 1024)));
            }

            @Override
            protected void cleanup(Context context) throws IOException, InterruptedException {
                joinWalkers();
                mutator.close();
                connection.close();
            }

            @Override
            protected void map(BytesWritable key, NullWritable value, Context output) throws IOException {
                current[i] = new byte[key.getLength()];
                System.arraycopy(key.getBytes(), 0, current[i], 0, key.getLength());
                if ((++(i)) == (current.length)) {
                    IntegrationTestBigLinkedList.Generator.LOG.debug("Persisting current.length={}, count={}, id={}, current={}, i=", current.length, count, Bytes.toStringBinary(id), Bytes.toStringBinary(current[0]), i);
                    persist(output, count, prev, current, id);
                    i = 0;
                    if ((first) == null) {
                        first = current;
                    }
                    prev = current;
                    current = new byte[this.width][];
                    count += current.length;
                    output.setStatus(("Count " + (count)));
                    if (((count) % (wrap)) == 0) {
                        // this block of code turns the 1 million linked list of length 25 into one giant
                        // circular linked list of 25 million
                        IntegrationTestBigLinkedList.Generator.GeneratorMapper.circularLeftShift(first);
                        persist(output, (-1), prev, first, null);
                        // At this point the entire loop has been flushed so we can add one of its nodes to the
                        // concurrent walker
                        if ((numWalkers) > 0) {
                            addFlushed(key.getBytes());
                            if (walkers.isEmpty()) {
                                startWalkers(numWalkers, conf, output);
                            }
                        }
                        first = null;
                        prev = null;
                    }
                }
            }

            private static <T> void circularLeftShift(T[] first) {
                T ez = first[0];
                System.arraycopy(first, 1, first, 0, ((first.length) - 1));
                first[((first.length) - 1)] = ez;
            }

            private void addFlushed(byte[] rowKey) {
                synchronized(flushedLoops) {
                    flushedLoops.add(Bytes.toLong(rowKey));
                    flushedLoops.notifyAll();
                }
            }

            protected void persist(Context output, long count, byte[][] prev, byte[][] current, byte[] id) throws IOException {
                for (int i = 0; i < (current.length); i++) {
                    if ((i % 100) == 0) {
                        // Tickle progress every so often else maprunner will think us hung
                        output.progress();
                    }
                    Put put = new Put(current[i]);
                    put.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV, (prev == null ? IntegrationTestBigLinkedList.NO_KEY : prev[i]));
                    if (count >= 0) {
                        put.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_COUNT, Bytes.toBytes((count + i)));
                    }
                    if (id != null) {
                        put.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_CLIENT, id);
                    }
                    // See if we are to write multiple columns.
                    if (this.multipleUnevenColumnFamilies) {
                        // Use any column name.
                        put.addColumn(IntegrationTestBigLinkedList.TINY_FAMILY_NAME, IntegrationTestBigLinkedList.TINY_FAMILY_NAME, this.tinyValue);
                        // Use any column name.
                        put.addColumn(IntegrationTestBigLinkedList.BIG_FAMILY_NAME, IntegrationTestBigLinkedList.BIG_FAMILY_NAME, this.bigValue);
                    }
                    mutator.mutate(put);
                }
                mutator.flush();
            }

            private void startWalkers(int numWalkers, Configuration conf, Context context) {
                IntegrationTestBigLinkedList.Generator.LOG.info((("Starting " + numWalkers) + " concurrent walkers"));
                for (int i = 0; i < numWalkers; i++) {
                    Thread walker = new Thread(new IntegrationTestBigLinkedList.Generator.GeneratorMapper.ContinuousConcurrentWalker(conf, context));
                    walker.start();
                    walkers.add(walker);
                }
            }

            private void joinWalkers() {
                walkersStop = true;
                synchronized(flushedLoops) {
                    flushedLoops.notifyAll();
                }
                for (Thread walker : walkers) {
                    try {
                        walker.join();
                    } catch (InterruptedException e) {
                        // no-op
                    }
                }
            }

            /**
             * Randomly selects and walks a random flushed loop concurrently with the Generator Mapper by
             * spawning ConcurrentWalker's with specified StartNodes. These ConcurrentWalker's are
             * configured to only log erroneous nodes.
             */
            public class ContinuousConcurrentWalker implements Runnable {
                IntegrationTestBigLinkedList.Generator.GeneratorMapper.ConcurrentWalker walker;

                Configuration conf;

                Context context;

                Random rand;

                public ContinuousConcurrentWalker(Configuration conf, Context context) {
                    this.conf = conf;
                    this.context = context;
                    rand = new Random();
                }

                @Override
                public void run() {
                    while (!(walkersStop)) {
                        try {
                            long node = selectLoop();
                            try {
                                walkLoop(node);
                            } catch (IOException e) {
                                context.getCounter(IntegrationTestBigLinkedList.Generator.Counts.IOEXCEPTION).increment(1L);
                                return;
                            }
                        } catch (InterruptedException e) {
                            return;
                        }
                    } 
                }

                private void walkLoop(long node) throws IOException {
                    walker = new IntegrationTestBigLinkedList.Generator.GeneratorMapper.ConcurrentWalker(context);
                    walker.setConf(conf);
                    walker.run(node, wrap);
                }

                private long selectLoop() throws InterruptedException {
                    synchronized(flushedLoops) {
                        while ((flushedLoops.isEmpty()) && (!(walkersStop))) {
                            flushedLoops.wait();
                        } 
                        if (walkersStop) {
                            throw new InterruptedException();
                        }
                        return flushedLoops.get(rand.nextInt(flushedLoops.size()));
                    }
                }
            }

            public static class ConcurrentWalker extends IntegrationTestBigLinkedList.WalkerBase {
                Context context;

                public ConcurrentWalker(Context context) {
                    this.context = context;
                }

                public void run(long startKeyIn, long maxQueriesIn) throws IOException {
                    long maxQueries = (maxQueriesIn > 0) ? maxQueriesIn : Long.MAX_VALUE;
                    byte[] startKey = Bytes.toBytes(startKeyIn);
                    Connection connection = ConnectionFactory.createConnection(getConf());
                    Table table = connection.getTable(IntegrationTestBigLinkedList.getTableName(getConf()));
                    long numQueries = 0;
                    // If isSpecificStart is set, only walk one list from that particular node.
                    // Note that in case of circular (or P-shaped) list it will walk forever, as is
                    // the case in normal run without startKey.
                    IntegrationTestBigLinkedList.CINode node = IntegrationTestBigLinkedList.WalkerBase.findStartNode(table, startKey);
                    if (node == null) {
                        IntegrationTestBigLinkedList.Generator.LOG.error(("Start node not found: " + (Bytes.toStringBinary(startKey))));
                        throw new IOException(("Start node not found: " + startKeyIn));
                    }
                    while (numQueries < maxQueries) {
                        numQueries++;
                        byte[] prev = node.prev;
                        long t1 = System.currentTimeMillis();
                        node = getNode(prev, table, node);
                        long t2 = System.currentTimeMillis();
                        if (node == null) {
                            IntegrationTestBigLinkedList.Generator.LOG.error(("ConcurrentWalker found UNDEFINED NODE: " + (Bytes.toStringBinary(prev))));
                            context.getCounter(IntegrationTestBigLinkedList.Generator.Counts.UNDEFINED).increment(1L);
                        } else
                            if ((node.prev.length) == (IntegrationTestBigLinkedList.NO_KEY.length)) {
                                IntegrationTestBigLinkedList.Generator.LOG.error(("ConcurrentWalker found TERMINATING NODE: " + (Bytes.toStringBinary(node.key))));
                                context.getCounter(IntegrationTestBigLinkedList.Generator.Counts.TERMINATING).increment(1L);
                            } else {
                                // Increment for successful walk
                                context.getCounter(IntegrationTestBigLinkedList.Generator.Counts.SUCCESS).increment(1L);
                            }

                    } 
                    table.close();
                    connection.close();
                }
            }
        }

        @Override
        public int run(String[] args) throws Exception {
            if ((args.length) < 3) {
                System.err.println(IntegrationTestBigLinkedList.Generator.USAGE);
                return 1;
            }
            try {
                int numMappers = Integer.parseInt(args[0]);
                long numNodes = Long.parseLong(args[1]);
                Path tmpOutput = new Path(args[2]);
                Integer width = ((args.length) < 4) ? null : Integer.parseInt(args[3]);
                Integer wrapMultiplier = ((args.length) < 5) ? null : Integer.parseInt(args[4]);
                Integer numWalkers = ((args.length) < 6) ? null : Integer.parseInt(args[5]);
                return run(numMappers, numNodes, tmpOutput, width, wrapMultiplier, numWalkers);
            } catch (NumberFormatException e) {
                System.err.println(("Parsing generator arguments failed: " + (e.getMessage())));
                System.err.println(IntegrationTestBigLinkedList.Generator.USAGE);
                return 1;
            }
        }

        protected void createSchema() throws IOException {
            Configuration conf = getConf();
            TableName tableName = IntegrationTestBigLinkedList.getTableName(conf);
            try (Connection conn = ConnectionFactory.createConnection(conf);Admin admin = conn.getAdmin()) {
                if (!(admin.tableExists(tableName))) {
                    HTableDescriptor htd = new HTableDescriptor(IntegrationTestBigLinkedList.getTableName(getConf()));
                    htd.addFamily(new HColumnDescriptor(IntegrationTestBigLinkedList.FAMILY_NAME));
                    // Always add these families. Just skip writing to them when we do not test per CF flush.
                    htd.addFamily(new HColumnDescriptor(IntegrationTestBigLinkedList.BIG_FAMILY_NAME));
                    htd.addFamily(new HColumnDescriptor(IntegrationTestBigLinkedList.TINY_FAMILY_NAME));
                    // if -DuseMob=true force all data through mob path.
                    if (conf.getBoolean("useMob", false)) {
                        for (HColumnDescriptor hcd : htd.getColumnFamilies()) {
                            hcd.setMobEnabled(true);
                            hcd.setMobThreshold(4);
                        }
                    }
                    // If we want to pre-split compute how many splits.
                    if (conf.getBoolean(PRESPLIT_TEST_TABLE_KEY, PRESPLIT_TEST_TABLE)) {
                        int numberOfServers = admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().size();
                        if (numberOfServers == 0) {
                            throw new IllegalStateException("No live regionservers");
                        }
                        int regionsPerServer = conf.getInt(REGIONS_PER_SERVER_KEY, DEFAULT_REGIONS_PER_SERVER);
                        int totalNumberOfRegions = numberOfServers * regionsPerServer;
                        IntegrationTestBigLinkedList.Generator.LOG.info((((((((("Number of live regionservers: " + numberOfServers) + ", ") + "pre-splitting table into ") + totalNumberOfRegions) + " regions ") + "(default regions per server: ") + regionsPerServer) + ")"));
                        byte[][] splits = new RegionSplitter.UniformSplit().split(totalNumberOfRegions);
                        admin.createTable(htd, splits);
                    } else {
                        // Looks like we're just letting things play out.
                        // Create a table with on region by default.
                        // This will make the splitting work hard.
                        admin.createTable(htd);
                    }
                }
            } catch (MasterNotRunningException e) {
                IntegrationTestBigLinkedList.Generator.LOG.error("Master not running", e);
                throw new IOException(e);
            }
        }

        public int runRandomInputGenerator(int numMappers, long numNodes, Path tmpOutput, Integer width, Integer wrapMultiplier, Integer numWalkers) throws Exception {
            IntegrationTestBigLinkedList.Generator.LOG.info(((("Running RandomInputGenerator with numMappers=" + numMappers) + ", numNodes=") + numNodes));
            Job job = Job.getInstance(getConf());
            job.setJobName("Random Input Generator");
            job.setNumReduceTasks(0);
            job.setJarByClass(getClass());
            job.setInputFormatClass(IntegrationTestBigLinkedList.Generator.GeneratorInputFormat.class);
            job.setOutputKeyClass(BytesWritable.class);
            job.setOutputValueClass(NullWritable.class);
            IntegrationTestBigLinkedList.setJobConf(job, numMappers, numNodes, width, wrapMultiplier, numWalkers);
            job.setMapperClass(Mapper.class);// identity mapper

            FileOutputFormat.setOutputPath(job, tmpOutput);
            job.setOutputFormatClass(SequenceFileOutputFormat.class);
            TableMapReduceUtil.addDependencyJarsForClasses(job.getConfiguration(), Random64.class);
            boolean success = jobCompletion(job);
            return success ? 0 : 1;
        }

        public int runGenerator(int numMappers, long numNodes, Path tmpOutput, Integer width, Integer wrapMultiplier, Integer numWalkers) throws Exception {
            IntegrationTestBigLinkedList.Generator.LOG.info(((("Running Generator with numMappers=" + numMappers) + ", numNodes=") + numNodes));
            createSchema();
            job = Job.getInstance(getConf());
            job.setJobName("Link Generator");
            job.setNumReduceTasks(0);
            job.setJarByClass(getClass());
            FileInputFormat.setInputPaths(job, tmpOutput);
            job.setInputFormatClass(IntegrationTestBigLinkedList.Generator.OneFilePerMapperSFIF.class);
            job.setOutputKeyClass(NullWritable.class);
            job.setOutputValueClass(NullWritable.class);
            IntegrationTestBigLinkedList.setJobConf(job, numMappers, numNodes, width, wrapMultiplier, numWalkers);
            setMapperForGenerator(job);
            job.setOutputFormatClass(NullOutputFormat.class);
            job.getConfiguration().setBoolean("mapreduce.map.speculative", false);
            TableMapReduceUtil.addDependencyJars(job);
            TableMapReduceUtil.addDependencyJarsForClasses(job.getConfiguration(), AbstractHBaseTool.class);
            TableMapReduceUtil.initCredentials(job);
            boolean success = jobCompletion(job);
            return success ? 0 : 1;
        }

        protected boolean jobCompletion(Job job) throws IOException, ClassNotFoundException, InterruptedException {
            boolean success = job.waitForCompletion(true);
            return success;
        }

        protected void setMapperForGenerator(Job job) {
            job.setMapperClass(IntegrationTestBigLinkedList.Generator.GeneratorMapper.class);
        }

        public int run(int numMappers, long numNodes, Path tmpOutput, Integer width, Integer wrapMultiplier, Integer numWalkers) throws Exception {
            int ret = runRandomInputGenerator(numMappers, numNodes, tmpOutput, width, wrapMultiplier, numWalkers);
            if (ret > 0) {
                return ret;
            }
            return runGenerator(numMappers, numNodes, tmpOutput, width, wrapMultiplier, numWalkers);
        }

        public boolean verify() {
            try {
                Counters counters = job.getCounters();
                if (counters == null) {
                    IntegrationTestBigLinkedList.Generator.LOG.info(("Counters object was null, Generator verification cannot be performed." + " This is commonly a result of insufficient YARN configuration."));
                    return false;
                }
                if ((((counters.findCounter(IntegrationTestBigLinkedList.Generator.Counts.TERMINATING).getValue()) > 0) || ((counters.findCounter(IntegrationTestBigLinkedList.Generator.Counts.UNDEFINED).getValue()) > 0)) || ((counters.findCounter(IntegrationTestBigLinkedList.Generator.Counts.IOEXCEPTION).getValue()) > 0)) {
                    IntegrationTestBigLinkedList.Generator.LOG.error("Concurrent walker failed to verify during Generation phase");
                    IntegrationTestBigLinkedList.Generator.LOG.error(("TERMINATING nodes: " + (counters.findCounter(IntegrationTestBigLinkedList.Generator.Counts.TERMINATING).getValue())));
                    IntegrationTestBigLinkedList.Generator.LOG.error(("UNDEFINED nodes: " + (counters.findCounter(IntegrationTestBigLinkedList.Generator.Counts.UNDEFINED).getValue())));
                    IntegrationTestBigLinkedList.Generator.LOG.error(("IOEXCEPTION nodes: " + (counters.findCounter(IntegrationTestBigLinkedList.Generator.Counts.IOEXCEPTION).getValue())));
                    return false;
                }
            } catch (IOException e) {
                IntegrationTestBigLinkedList.Generator.LOG.info("Generator verification could not find counter");
                return false;
            }
            return true;
        }
    }

    /**
     * Tool to search missing rows in WALs and hfiles.
     * Pass in file or dir of keys to search for. Key file must have been written by Verify step
     * (we depend on the format it writes out. We'll read them in and then search in hbase
     * WALs and oldWALs dirs (Some of this is TODO).
     */
    static class Search extends Configured implements Tool {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedList.Search.class);

        protected Job job;

        private static void printUsage(final String error) {
            if ((error != null) && ((error.length()) > 0))
                System.out.println(("ERROR: " + error));

            System.err.println("Usage: search <KEYS_DIR> [<MAPPERS_COUNT>]");
        }

        @Override
        public int run(String[] args) throws Exception {
            if (((args.length) < 1) || ((args.length) > 2)) {
                IntegrationTestBigLinkedList.Search.printUsage(null);
                return 1;
            }
            Path inputDir = new Path(args[0]);
            int numMappers = 1;
            if ((args.length) > 1) {
                numMappers = Integer.parseInt(args[1]);
            }
            return run(inputDir, numMappers);
        }

        /**
         * WALPlayer override that searches for keys loaded in the setup.
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
                        this.keysToFind = IntegrationTestBigLinkedList.Search.readKeysToSearch(context.getConfiguration());
                        IntegrationTestBigLinkedList.Search.LOG.info(("Loaded keys to find: count=" + (this.keysToFind.size())));
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
                            IntegrationTestBigLinkedList.Search.LOG.info(((("Found cell=" + cell) + " , walKey=") + (context.getCurrentKey())));
                        } catch (IOException | InterruptedException e) {
                            IntegrationTestBigLinkedList.Search.LOG.warn(e.toString(), e);
                        }
                        if ((rows.addAndGet(1)) < (IntegrationTestBigLinkedList.MISSING_ROWS_TO_LOG)) {
                            context.getCounter(IntegrationTestBigLinkedList.Search.FOUND_GROUP_KEY, keyStr).increment(1);
                        }
                        context.getCounter(IntegrationTestBigLinkedList.Search.FOUND_GROUP_KEY, "CELL_WITH_MISSING_ROW").increment(1);
                    }
                    return b;
                }
            }

            // Put in place the above WALMapperSearcher.
            @Override
            public Job createSubmittableJob(String[] args) throws IOException {
                Job job = super.createSubmittableJob(args);
                // Call my class instead.
                job.setJarByClass(IntegrationTestBigLinkedList.Search.WALSearcher.WALMapperSearcher.class);
                job.setMapperClass(IntegrationTestBigLinkedList.Search.WALSearcher.WALMapperSearcher.class);
                job.setOutputFormatClass(NullOutputFormat.class);
                return job;
            }
        }

        static final String FOUND_GROUP_KEY = "Found";

        static final String SEARCHER_INPUTDIR_KEY = "searcher.keys.inputdir";

        public int run(Path inputDir, int numMappers) throws Exception {
            getConf().set(IntegrationTestBigLinkedList.Search.SEARCHER_INPUTDIR_KEY, inputDir.toString());
            SortedSet<byte[]> keys = IntegrationTestBigLinkedList.Search.readKeysToSearch(getConf());
            if (keys.isEmpty())
                throw new RuntimeException("No keys to find");

            IntegrationTestBigLinkedList.Search.LOG.info(("Count of keys to find: " + (keys.size())));
            for (byte[] key : keys)
                IntegrationTestBigLinkedList.Search.LOG.info(("Key: " + (Bytes.toStringBinary(key))));

            // Now read all WALs. In two dirs. Presumes certain layout.
            Path walsDir = new Path(CommonFSUtils.getWALRootDir(getConf()), HConstants.HREGION_LOGDIR_NAME);
            Path oldWalsDir = new Path(CommonFSUtils.getWALRootDir(getConf()), HConstants.HREGION_OLDLOGDIR_NAME);
            IntegrationTestBigLinkedList.Search.LOG.info(((((("Running Search with keys inputDir=" + inputDir) + ", numMappers=") + numMappers) + " against ") + (getConf().get(HBASE_DIR))));
            int ret = ToolRunner.run(getConf(), new IntegrationTestBigLinkedList.Search.WALSearcher(getConf()), new String[]{ walsDir.toString(), "" });
            if (ret != 0) {
                return ret;
            }
            return ToolRunner.run(getConf(), new IntegrationTestBigLinkedList.Search.WALSearcher(getConf()), new String[]{ oldWalsDir.toString(), "" });
        }

        static SortedSet<byte[]> readKeysToSearch(final Configuration conf) throws IOException, InterruptedException {
            Path keysInputDir = new Path(conf.get(IntegrationTestBigLinkedList.Search.SEARCHER_INPUTDIR_KEY));
            FileSystem fs = FileSystem.get(conf);
            SortedSet<byte[]> result = new java.util.TreeSet(Bytes.BYTES_COMPARATOR);
            if (!(fs.exists(keysInputDir))) {
                throw new FileNotFoundException(keysInputDir.toString());
            }
            if (!(fs.isDirectory(keysInputDir))) {
                throw new UnsupportedOperationException("TODO");
            } else {
                RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(keysInputDir, false);
                while (iterator.hasNext()) {
                    LocatedFileStatus keyFileStatus = iterator.next();
                    // Skip "_SUCCESS" file.
                    if (keyFileStatus.getPath().getName().startsWith("_"))
                        continue;

                    result.addAll(IntegrationTestBigLinkedList.Search.readFileToSearch(conf, fs, keyFileStatus));
                } 
            }
            return result;
        }

        private static SortedSet<byte[]> readFileToSearch(final Configuration conf, final FileSystem fs, final LocatedFileStatus keyFileStatus) throws IOException, InterruptedException {
            SortedSet<byte[]> result = new java.util.TreeSet(Bytes.BYTES_COMPARATOR);
            // Return entries that are flagged Counts.UNDEFINED in the value. Return the row. This is
            // what is missing.
            TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, new TaskAttemptID());
            try (SequenceFileAsBinaryInputFormat.SequenceFileAsBinaryRecordReader rr = new SequenceFileAsBinaryInputFormat.SequenceFileAsBinaryRecordReader()) {
                InputSplit is = new org.apache.hadoop.mapreduce.lib.input.FileSplit(keyFileStatus.getPath(), 0, keyFileStatus.getLen(), new String[]{  });
                rr.initialize(is, context);
                while (rr.nextKeyValue()) {
                    rr.getCurrentKey();
                    BytesWritable bw = rr.getCurrentValue();
                    if ((IntegrationTestBigLinkedList.Verify.VerifyReducer.whichType(bw.getBytes())) == (IntegrationTestBigLinkedList.Verify.Counts.UNDEFINED)) {
                        byte[] key = new byte[rr.getCurrentKey().getLength()];
                        System.arraycopy(rr.getCurrentKey().getBytes(), 0, key, 0, rr.getCurrentKey().getLength());
                        result.add(key);
                    }
                } 
            }
            return result;
        }
    }

    /**
     * A Map Reduce job that verifies that the linked lists generated by
     * {@link Generator} do not have any holes.
     */
    static class Verify extends Configured implements Tool {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedList.Verify.class);

        protected static final BytesWritable DEF = new BytesWritable(new byte[]{ 0 });

        protected static final BytesWritable DEF_LOST_FAMILIES = new BytesWritable(new byte[]{ 1 });

        protected Job job;

        public static class VerifyMapper extends TableMapper<BytesWritable, BytesWritable> {
            private BytesWritable row = new BytesWritable();

            private BytesWritable ref = new BytesWritable();

            private boolean multipleUnevenColumnFamilies;

            @Override
            protected void setup(Mapper.Context context) throws IOException, InterruptedException {
                this.multipleUnevenColumnFamilies = IntegrationTestBigLinkedList.isMultiUnevenColumnFamilies(context.getConfiguration());
            }

            @Override
            protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
                byte[] rowKey = key.get();
                row.set(rowKey, 0, rowKey.length);
                if ((multipleUnevenColumnFamilies) && ((!(value.containsColumn(IntegrationTestBigLinkedList.BIG_FAMILY_NAME, IntegrationTestBigLinkedList.BIG_FAMILY_NAME))) || (!(value.containsColumn(IntegrationTestBigLinkedList.TINY_FAMILY_NAME, IntegrationTestBigLinkedList.TINY_FAMILY_NAME))))) {
                    context.write(row, IntegrationTestBigLinkedList.Verify.DEF_LOST_FAMILIES);
                } else {
                    context.write(row, IntegrationTestBigLinkedList.Verify.DEF);
                }
                byte[] prev = value.getValue(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV);
                if ((prev != null) && ((prev.length) > 0)) {
                    ref.set(prev, 0, prev.length);
                    context.write(ref, row);
                } else {
                    IntegrationTestBigLinkedList.Verify.LOG.warn(String.format("Prev is not set for: %s", Bytes.toStringBinary(rowKey)));
                }
            }
        }

        /**
         * Don't change the order of these enums. Their ordinals are used as type flag when we emit
         * problems found from the reducer.
         */
        public static enum Counts {

            UNREFERENCED,
            UNDEFINED,
            REFERENCED,
            CORRUPT,
            EXTRAREFERENCES,
            EXTRA_UNDEF_REFERENCES,
            LOST_FAMILIES;}

        /**
         * Per reducer, we output problem rows as byte arrasy so can be used as input for
         * subsequent investigative mapreduce jobs. Each emitted value is prefaced by a one byte flag
         * saying what sort of emission it is. Flag is the Count enum ordinal as a short.
         */
        public static class VerifyReducer extends Reducer<BytesWritable, BytesWritable, BytesWritable, BytesWritable> {
            private ArrayList<byte[]> refs = new ArrayList<>();

            private final BytesWritable UNREF = new BytesWritable(IntegrationTestBigLinkedList.Verify.VerifyReducer.addPrefixFlag(IntegrationTestBigLinkedList.Verify.Counts.UNREFERENCED.ordinal(), new byte[]{  }));

            private final BytesWritable LOSTFAM = new BytesWritable(IntegrationTestBigLinkedList.Verify.VerifyReducer.addPrefixFlag(IntegrationTestBigLinkedList.Verify.Counts.LOST_FAMILIES.ordinal(), new byte[]{  }));

            private AtomicInteger rows = new AtomicInteger(0);

            private Connection connection;

            @Override
            protected void setup(Reducer.Context context) throws IOException, InterruptedException {
                super.setup(context);
                this.connection = ConnectionFactory.createConnection(context.getConfiguration());
            }

            @Override
            protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {
                if ((this.connection) != null) {
                    this.connection.close();
                }
                super.cleanup(context);
            }

            /**
             *
             *
             * @param ordinal
             * 		
             * @param r
             * 		
             * @return Return new byte array that has <code>ordinal</code> as prefix on front taking up
            Bytes.SIZEOF_SHORT bytes followed by <code>r</code>
             */
            public static byte[] addPrefixFlag(final int ordinal, final byte[] r) {
                byte[] prefix = Bytes.toBytes(((short) (ordinal)));
                if ((prefix.length) != (Bytes.SIZEOF_SHORT)) {
                    throw new RuntimeException(("Unexpected size: " + (prefix.length)));
                }
                byte[] result = new byte[(prefix.length) + (r.length)];
                System.arraycopy(prefix, 0, result, 0, prefix.length);
                System.arraycopy(r, 0, result, prefix.length, r.length);
                return result;
            }

            /**
             *
             *
             * @param bs
             * 		
             * @return Type from the Counts enum of this row. Reads prefix added by
            {@link #addPrefixFlag(int, byte[])}
             */
            public static IntegrationTestBigLinkedList.Verify.Counts whichType(final byte[] bs) {
                int ordinal = Bytes.toShort(bs, 0, SIZEOF_SHORT);
                return IntegrationTestBigLinkedList.Verify.Counts.values()[ordinal];
            }

            /**
             *
             *
             * @param bw
             * 		
             * @return Row bytes minus the type flag.
             */
            public static byte[] getRowOnly(BytesWritable bw) {
                byte[] bytes = new byte[(bw.getLength()) - (Bytes.SIZEOF_SHORT)];
                System.arraycopy(bw.getBytes(), SIZEOF_SHORT, bytes, 0, bytes.length);
                return bytes;
            }

            @Override
            public void reduce(BytesWritable key, Iterable<BytesWritable> values, Context context) throws IOException, InterruptedException {
                int defCount = 0;
                boolean lostFamilies = false;
                refs.clear();
                for (BytesWritable type : values) {
                    if ((type.getLength()) == (IntegrationTestBigLinkedList.Verify.DEF.getLength())) {
                        defCount++;
                        if ((type.getBytes()[0]) == 1) {
                            lostFamilies = true;
                        }
                    } else {
                        byte[] bytes = new byte[type.getLength()];
                        System.arraycopy(type.getBytes(), 0, bytes, 0, type.getLength());
                        refs.add(bytes);
                    }
                }
                // TODO check for more than one def, should not happen
                StringBuilder refsSb = null;
                if ((defCount == 0) || ((refs.size()) != 1)) {
                    String keyString = Bytes.toStringBinary(key.getBytes(), 0, key.getLength());
                    refsSb = dumpExtraInfoOnRefs(key, context, refs);
                    IntegrationTestBigLinkedList.Verify.LOG.error(((("LinkedListError: key=" + keyString) + ", reference(s)=") + (refsSb != null ? refsSb.toString() : "")));
                }
                if (lostFamilies) {
                    String keyString = Bytes.toStringBinary(key.getBytes(), 0, key.getLength());
                    IntegrationTestBigLinkedList.Verify.LOG.error((("LinkedListError: key=" + keyString) + ", lost big or tiny families"));
                    context.getCounter(IntegrationTestBigLinkedList.Verify.Counts.LOST_FAMILIES).increment(1);
                    context.write(key, LOSTFAM);
                }
                if ((defCount == 0) && ((refs.size()) > 0)) {
                    // This is bad, found a node that is referenced but not defined. It must have been
                    // lost, emit some info about this node for debugging purposes.
                    // Write out a line per reference. If more than one, flag it.;
                    for (int i = 0; i < (refs.size()); i++) {
                        byte[] bs = refs.get(i);
                        int ordinal;
                        if (i <= 0) {
                            ordinal = IntegrationTestBigLinkedList.Verify.Counts.UNDEFINED.ordinal();
                            context.write(key, new BytesWritable(IntegrationTestBigLinkedList.Verify.VerifyReducer.addPrefixFlag(ordinal, bs)));
                            context.getCounter(IntegrationTestBigLinkedList.Verify.Counts.UNDEFINED).increment(1);
                        } else {
                            ordinal = IntegrationTestBigLinkedList.Verify.Counts.EXTRA_UNDEF_REFERENCES.ordinal();
                            context.write(key, new BytesWritable(IntegrationTestBigLinkedList.Verify.VerifyReducer.addPrefixFlag(ordinal, bs)));
                        }
                    }
                    if ((rows.addAndGet(1)) < (IntegrationTestBigLinkedList.MISSING_ROWS_TO_LOG)) {
                        // Print out missing row; doing get on reference gives info on when the referencer
                        // was added which can help a little debugging. This info is only available in mapper
                        // output -- the 'Linked List error Key...' log message above. What we emit here is
                        // useless for debugging.
                        String keyString = Bytes.toStringBinary(key.getBytes(), 0, key.getLength());
                        context.getCounter("undef", keyString).increment(1);
                    }
                } else
                    if ((defCount > 0) && (refs.isEmpty())) {
                        // node is defined but not referenced
                        context.write(key, UNREF);
                        context.getCounter(IntegrationTestBigLinkedList.Verify.Counts.UNREFERENCED).increment(1);
                        if ((rows.addAndGet(1)) < (IntegrationTestBigLinkedList.MISSING_ROWS_TO_LOG)) {
                            String keyString = Bytes.toStringBinary(key.getBytes(), 0, key.getLength());
                            context.getCounter("unref", keyString).increment(1);
                        }
                    } else {
                        if ((refs.size()) > 1) {
                            // Skip first reference.
                            for (int i = 1; i < (refs.size()); i++) {
                                context.write(key, new BytesWritable(IntegrationTestBigLinkedList.Verify.VerifyReducer.addPrefixFlag(IntegrationTestBigLinkedList.Verify.Counts.EXTRAREFERENCES.ordinal(), refs.get(i))));
                            }
                            context.getCounter(IntegrationTestBigLinkedList.Verify.Counts.EXTRAREFERENCES).increment(((refs.size()) - 1));
                        }
                        // node is defined and referenced
                        context.getCounter(IntegrationTestBigLinkedList.Verify.Counts.REFERENCED).increment(1);
                    }

            }

            /**
             * Dump out extra info around references if there are any. Helps debugging.
             *
             * @return StringBuilder filled with references if any.
             * @throws IOException
             * 		
             */
            private StringBuilder dumpExtraInfoOnRefs(final BytesWritable key, final Context context, final List<byte[]> refs) throws IOException {
                StringBuilder refsSb = null;
                if (refs.isEmpty())
                    return refsSb;

                refsSb = new StringBuilder();
                String comma = "";
                // If a row is a reference but has no define, print the content of the row that has
                // this row as a 'prev'; it will help debug.  The missing row was written just before
                // the row we are dumping out here.
                TableName tn = IntegrationTestBigLinkedList.getTableName(context.getConfiguration());
                try (Table t = this.connection.getTable(tn)) {
                    for (byte[] ref : refs) {
                        Result r = t.get(new Get(ref));
                        List<Cell> cells = r.listCells();
                        String ts = ((cells != null) && (!(cells.isEmpty()))) ? new Date(cells.get(0).getTimestamp()).toString() : "";
                        byte[] b = r.getValue(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_CLIENT);
                        String jobStr = ((b != null) && ((b.length) > 0)) ? Bytes.toString(b) : "";
                        b = r.getValue(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_COUNT);
                        long count = ((b != null) && ((b.length) > 0)) ? Bytes.toLong(b) : -1;
                        b = r.getValue(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV);
                        String refRegionLocation = "";
                        String keyRegionLocation = "";
                        if ((b != null) && ((b.length) > 0)) {
                            try (RegionLocator rl = this.connection.getRegionLocator(tn)) {
                                HRegionLocation hrl = rl.getRegionLocation(b);
                                if (hrl != null)
                                    refRegionLocation = hrl.toString();

                                // Key here probably has trailing zeros on it.
                                hrl = rl.getRegionLocation(key.getBytes());
                                if (hrl != null)
                                    keyRegionLocation = hrl.toString();

                            }
                        }
                        IntegrationTestBigLinkedList.Verify.LOG.error(((((((((((((((("Extras on ref without a def, ref=" + (Bytes.toStringBinary(ref))) + ", refPrevEqualsKey=") + ((Bytes.compareTo(key.getBytes(), 0, key.getLength(), b, 0, b.length)) == 0)) + ", key=") + (Bytes.toStringBinary(key.getBytes(), 0, key.getLength()))) + ", ref row date=") + ts) + ", jobStr=") + jobStr) + ", ref row count=") + count) + ", ref row regionLocation=") + refRegionLocation) + ", key row regionLocation=") + keyRegionLocation));
                        refsSb.append(comma);
                        comma = ",";
                        refsSb.append(Bytes.toStringBinary(ref));
                    }
                }
                return refsSb;
            }
        }

        @Override
        public int run(String[] args) throws Exception {
            if ((args.length) != 2) {
                System.out.println((("Usage : " + (IntegrationTestBigLinkedList.Verify.class.getSimpleName())) + " <output dir> <num reducers>"));
                return 0;
            }
            String outputDir = args[0];
            int numReducers = Integer.parseInt(args[1]);
            return run(outputDir, numReducers);
        }

        public int run(String outputDir, int numReducers) throws Exception {
            return run(new Path(outputDir), numReducers);
        }

        public int run(Path outputDir, int numReducers) throws Exception {
            IntegrationTestBigLinkedList.Verify.LOG.info(((("Running Verify with outputDir=" + outputDir) + ", numReducers=") + numReducers));
            job = Job.getInstance(getConf());
            job.setJobName("Link Verifier");
            job.setNumReduceTasks(numReducers);
            job.setJarByClass(getClass());
            IntegrationTestBigLinkedList.setJobScannerConf(job);
            Scan scan = new Scan();
            scan.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV);
            scan.setCaching(10000);
            scan.setCacheBlocks(false);
            if (IntegrationTestBigLinkedList.isMultiUnevenColumnFamilies(getConf())) {
                scan.addColumn(IntegrationTestBigLinkedList.BIG_FAMILY_NAME, IntegrationTestBigLinkedList.BIG_FAMILY_NAME);
                scan.addColumn(IntegrationTestBigLinkedList.TINY_FAMILY_NAME, IntegrationTestBigLinkedList.TINY_FAMILY_NAME);
            }
            TableMapReduceUtil.initTableMapperJob(IntegrationTestBigLinkedList.getTableName(getConf()).getName(), scan, IntegrationTestBigLinkedList.Verify.VerifyMapper.class, BytesWritable.class, BytesWritable.class, job);
            TableMapReduceUtil.addDependencyJarsForClasses(job.getConfiguration(), AbstractHBaseTool.class);
            job.getConfiguration().setBoolean("mapreduce.map.speculative", false);
            job.setReducerClass(IntegrationTestBigLinkedList.Verify.VerifyReducer.class);
            job.setOutputFormatClass(SequenceFileAsBinaryOutputFormat.class);
            job.setOutputKeyClass(BytesWritable.class);
            job.setOutputValueClass(BytesWritable.class);
            TextOutputFormat.setOutputPath(job, outputDir);
            boolean success = job.waitForCompletion(true);
            if (success) {
                Counters counters = job.getCounters();
                if (null == counters) {
                    IntegrationTestBigLinkedList.Verify.LOG.warn(("Counters were null, cannot verify Job completion." + " This is commonly a result of insufficient YARN configuration."));
                    // We don't have access to the counters to know if we have "bad" counts
                    return 0;
                }
                // If we find no unexpected values, the job didn't outright fail
                if (verifyUnexpectedValues(counters)) {
                    // We didn't check referenced+unreferenced counts, leave that to visual inspection
                    return 0;
                }
            }
            // We failed
            return 1;
        }

        public boolean verify(long expectedReferenced) throws Exception {
            if ((job) == null) {
                throw new IllegalStateException("You should call run() first");
            }
            Counters counters = job.getCounters();
            if (counters == null) {
                IntegrationTestBigLinkedList.Verify.LOG.info(("Counters object was null, write verification cannot be performed." + " This is commonly a result of insufficient YARN configuration."));
                return false;
            }
            // Run through each check, even if we fail one early
            boolean success = verifyExpectedValues(expectedReferenced, counters);
            if (!(verifyUnexpectedValues(counters))) {
                // We found counter objects which imply failure
                success = false;
            }
            if (!success) {
                handleFailure(counters);
            }
            return success;
        }

        /**
         * Verify the values in the Counters against the expected number of entries written.
         *
         * @param expectedReferenced
         * 		Expected number of referenced entrires
         * @param counters
         * 		The Job's Counters object
         * @return True if the values match what's expected, false otherwise
         */
        protected boolean verifyExpectedValues(long expectedReferenced, Counters counters) {
            final Counter referenced = counters.findCounter(IntegrationTestBigLinkedList.Verify.Counts.REFERENCED);
            final Counter unreferenced = counters.findCounter(IntegrationTestBigLinkedList.Verify.Counts.UNREFERENCED);
            boolean success = true;
            if (expectedReferenced != (referenced.getValue())) {
                IntegrationTestBigLinkedList.Verify.LOG.error((((("Expected referenced count does not match with actual referenced count. " + "expected referenced=") + expectedReferenced) + " ,actual=") + (referenced.getValue())));
                success = false;
            }
            if ((unreferenced.getValue()) > 0) {
                final Counter multiref = counters.findCounter(IntegrationTestBigLinkedList.Verify.Counts.EXTRAREFERENCES);
                boolean couldBeMultiRef = (multiref.getValue()) == (unreferenced.getValue());
                IntegrationTestBigLinkedList.Verify.LOG.error((("Unreferenced nodes were not expected. Unreferenced count=" + (unreferenced.getValue())) + (couldBeMultiRef ? "; could be due to duplicate random numbers" : "")));
                success = false;
            }
            return success;
        }

        /**
         * Verify that the Counters don't contain values which indicate an outright failure from the Reducers.
         *
         * @param counters
         * 		The Job's counters
         * @return True if the "bad" counter objects are 0, false otherwise
         */
        protected boolean verifyUnexpectedValues(Counters counters) {
            final Counter undefined = counters.findCounter(IntegrationTestBigLinkedList.Verify.Counts.UNDEFINED);
            final Counter lostfamilies = counters.findCounter(IntegrationTestBigLinkedList.Verify.Counts.LOST_FAMILIES);
            boolean success = true;
            if ((undefined.getValue()) > 0) {
                IntegrationTestBigLinkedList.Verify.LOG.error(("Found an undefined node. Undefined count=" + (undefined.getValue())));
                success = false;
            }
            if ((lostfamilies.getValue()) > 0) {
                IntegrationTestBigLinkedList.Verify.LOG.error(("Found nodes which lost big or tiny families, count=" + (lostfamilies.getValue())));
                success = false;
            }
            return success;
        }

        protected void handleFailure(Counters counters) throws IOException {
            Configuration conf = job.getConfiguration();
            TableName tableName = IntegrationTestBigLinkedList.getTableName(conf);
            try (Connection conn = ConnectionFactory.createConnection(conf)) {
                try (RegionLocator rl = conn.getRegionLocator(tableName)) {
                    CounterGroup g = counters.getGroup("undef");
                    Iterator<Counter> it = g.iterator();
                    while (it.hasNext()) {
                        String keyString = it.next().getName();
                        byte[] key = Bytes.toBytes(keyString);
                        HRegionLocation loc = rl.getRegionLocation(key, true);
                        IntegrationTestBigLinkedList.Verify.LOG.error(((("undefined row " + keyString) + ", ") + loc));
                    } 
                    g = counters.getGroup("unref");
                    it = g.iterator();
                    while (it.hasNext()) {
                        String keyString = it.next().getName();
                        byte[] key = Bytes.toBytes(keyString);
                        HRegionLocation loc = rl.getRegionLocation(key, true);
                        IntegrationTestBigLinkedList.Verify.LOG.error(((("unreferred row " + keyString) + ", ") + loc));
                    } 
                }
            }
        }
    }

    /**
     * Executes Generate and Verify in a loop. Data is not cleaned between runs, so each iteration
     * adds more data.
     */
    static class Loop extends Configured implements Tool {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedList.Loop.class);

        private static final String USAGE = "Usage: Loop <num iterations> <num mappers> " + ((("<num nodes per mapper> <output dir> <num reducers> [<width> <wrap multiplier>" + " <num walker threads>] \n") + "where <num nodes per map> should be a multiple of width*wrap multiplier, 25M by default \n") + "walkers will select and verify random flushed loop during Generation.");

        IntegrationTestBigLinkedList it;

        protected void runGenerator(int numMappers, long numNodes, String outputDir, Integer width, Integer wrapMultiplier, Integer numWalkers) throws Exception {
            Path outputPath = new Path(outputDir);
            UUID uuid = UUID.randomUUID();// create a random UUID.

            Path generatorOutput = new Path(outputPath, uuid.toString());
            IntegrationTestBigLinkedList.Generator generator = new IntegrationTestBigLinkedList.Generator();
            generator.setConf(getConf());
            int retCode = generator.run(numMappers, numNodes, generatorOutput, width, wrapMultiplier, numWalkers);
            if (retCode > 0) {
                throw new RuntimeException(("Generator failed with return code: " + retCode));
            }
            if (numWalkers > 0) {
                if (!(generator.verify())) {
                    throw new RuntimeException("Generator.verify failed");
                }
            }
        }

        protected void runVerify(String outputDir, int numReducers, long expectedNumNodes) throws Exception {
            Path outputPath = new Path(outputDir);
            UUID uuid = UUID.randomUUID();// create a random UUID.

            Path iterationOutput = new Path(outputPath, uuid.toString());
            IntegrationTestBigLinkedList.Verify verify = new IntegrationTestBigLinkedList.Verify();
            verify.setConf(getConf());
            int retCode = verify.run(iterationOutput, numReducers);
            if (retCode > 0) {
                throw new RuntimeException(("Verify.run failed with return code: " + retCode));
            }
            if (!(verify.verify(expectedNumNodes))) {
                throw new RuntimeException("Verify.verify failed");
            }
            IntegrationTestBigLinkedList.Loop.LOG.info(("Verify finished with success. Total nodes=" + expectedNumNodes));
        }

        @Override
        public int run(String[] args) throws Exception {
            if ((args.length) < 5) {
                System.err.println(IntegrationTestBigLinkedList.Loop.USAGE);
                return 1;
            }
            try {
                int numIterations = Integer.parseInt(args[0]);
                int numMappers = Integer.parseInt(args[1]);
                long numNodes = Long.parseLong(args[2]);
                String outputDir = args[3];
                int numReducers = Integer.parseInt(args[4]);
                Integer width = ((args.length) < 6) ? null : Integer.parseInt(args[5]);
                Integer wrapMultiplier = ((args.length) < 7) ? null : Integer.parseInt(args[6]);
                Integer numWalkers = ((args.length) < 8) ? 0 : Integer.parseInt(args[7]);
                long expectedNumNodes = 0;
                if (numIterations < 0) {
                    numIterations = Integer.MAX_VALUE;// run indefinitely (kind of)

                }
                IntegrationTestBigLinkedList.Loop.LOG.info(("Running Loop with args:" + (Arrays.deepToString(args))));
                for (int i = 0; i < numIterations; i++) {
                    IntegrationTestBigLinkedList.Loop.LOG.info(("Starting iteration = " + i));
                    runGenerator(numMappers, numNodes, outputDir, width, wrapMultiplier, numWalkers);
                    expectedNumNodes += numMappers * numNodes;
                    runVerify(outputDir, numReducers, expectedNumNodes);
                }
                return 0;
            } catch (NumberFormatException e) {
                System.err.println(("Parsing loop arguments failed: " + (e.getMessage())));
                System.err.println(IntegrationTestBigLinkedList.Loop.USAGE);
                return 1;
            }
        }
    }

    /**
     * A stand alone program that prints out portions of a list created by {@link Generator}
     */
    private static class Print extends Configured implements Tool {
        @Override
        public int run(String[] args) throws Exception {
            Options options = new Options();
            options.addOption("s", "start", true, "start key");
            options.addOption("e", "end", true, "end key");
            options.addOption("l", "limit", true, "number to print");
            GnuParser parser = new GnuParser();
            CommandLine cmd = null;
            try {
                cmd = parser.parse(options, args);
                if ((cmd.getArgs().length) != 0) {
                    throw new ParseException("Command takes no arguments");
                }
            } catch (ParseException e) {
                System.err.println(("Failed to parse command line " + (e.getMessage())));
                System.err.println();
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(getClass().getSimpleName(), options);
                System.exit((-1));
            }
            Connection connection = ConnectionFactory.createConnection(getConf());
            Table table = connection.getTable(IntegrationTestBigLinkedList.getTableName(getConf()));
            Scan scan = new Scan();
            scan.setBatch(10000);
            if (cmd.hasOption("s"))
                scan.setStartRow(Bytes.toBytesBinary(cmd.getOptionValue("s")));

            if (cmd.hasOption("e"))
                scan.setStopRow(Bytes.toBytesBinary(cmd.getOptionValue("e")));

            int limit = 0;
            if (cmd.hasOption("l"))
                limit = Integer.parseInt(cmd.getOptionValue("l"));
            else
                limit = 100;

            ResultScanner scanner = table.getScanner(scan);
            IntegrationTestBigLinkedList.CINode node = new IntegrationTestBigLinkedList.CINode();
            Result result = scanner.next();
            int count = 0;
            while ((result != null) && ((count++) < limit)) {
                node = IntegrationTestBigLinkedList.getCINode(result, node);
                System.out.printf("%s:%s:%012d:%s\n", Bytes.toStringBinary(node.key), Bytes.toStringBinary(node.prev), node.count, node.client);
                result = scanner.next();
            } 
            scanner.close();
            table.close();
            connection.close();
            return 0;
        }
    }

    /**
     * A stand alone program that deletes a single node.
     */
    private static class Delete extends Configured implements Tool {
        @Override
        public int run(String[] args) throws Exception {
            if ((args.length) != 1) {
                System.out.println((("Usage : " + (IntegrationTestBigLinkedList.Delete.class.getSimpleName())) + " <node to delete>"));
                return 0;
            }
            byte[] val = Bytes.toBytesBinary(args[0]);
            org.apache.hadoop.hbase.client.Delete delete = new org.apache.hadoop.hbase.client.Delete(val);
            try (Connection connection = ConnectionFactory.createConnection(getConf());Table table = connection.getTable(IntegrationTestBigLinkedList.getTableName(getConf()))) {
                table.delete(delete);
            }
            System.out.println("Delete successful");
            return 0;
        }
    }

    abstract static class WalkerBase extends Configured {
        protected static IntegrationTestBigLinkedList.CINode findStartNode(Table table, byte[] startKey) throws IOException {
            Scan scan = new Scan();
            scan.setStartRow(startKey);
            scan.setBatch(1);
            scan.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV);
            long t1 = System.currentTimeMillis();
            ResultScanner scanner = table.getScanner(scan);
            Result result = scanner.next();
            long t2 = System.currentTimeMillis();
            scanner.close();
            if (result != null) {
                IntegrationTestBigLinkedList.CINode node = IntegrationTestBigLinkedList.getCINode(result, new IntegrationTestBigLinkedList.CINode());
                System.out.printf("FSR %d %s\n", (t2 - t1), Bytes.toStringBinary(node.key));
                return node;
            }
            System.out.println(("FSR " + (t2 - t1)));
            return null;
        }

        protected IntegrationTestBigLinkedList.CINode getNode(byte[] row, Table table, IntegrationTestBigLinkedList.CINode node) throws IOException {
            Get get = new Get(row);
            get.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV);
            Result result = table.get(get);
            return IntegrationTestBigLinkedList.getCINode(result, node);
        }
    }

    /**
     * A stand alone program that follows a linked list created by {@link Generator} and prints
     * timing info.
     */
    private static class Walker extends IntegrationTestBigLinkedList.WalkerBase implements Tool {
        public Walker() {
        }

        @Override
        public int run(String[] args) throws IOException {
            Options options = new Options();
            options.addOption("n", "num", true, "number of queries");
            options.addOption("s", "start", true, "key to start at, binary string");
            options.addOption("l", "logevery", true, "log every N queries");
            GnuParser parser = new GnuParser();
            CommandLine cmd = null;
            try {
                cmd = parser.parse(options, args);
                if ((cmd.getArgs().length) != 0) {
                    throw new ParseException("Command takes no arguments");
                }
            } catch (ParseException e) {
                System.err.println(("Failed to parse command line " + (e.getMessage())));
                System.err.println();
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(getClass().getSimpleName(), options);
                System.exit((-1));
            }
            long maxQueries = Long.MAX_VALUE;
            if (cmd.hasOption('n')) {
                maxQueries = Long.parseLong(cmd.getOptionValue("n"));
            }
            Random rand = new SecureRandom();
            boolean isSpecificStart = cmd.hasOption('s');
            byte[] startKey = (isSpecificStart) ? Bytes.toBytesBinary(cmd.getOptionValue('s')) : null;
            int logEvery = (cmd.hasOption('l')) ? Integer.parseInt(cmd.getOptionValue('l')) : 1;
            Connection connection = ConnectionFactory.createConnection(getConf());
            Table table = connection.getTable(IntegrationTestBigLinkedList.getTableName(getConf()));
            long numQueries = 0;
            // If isSpecificStart is set, only walk one list from that particular node.
            // Note that in case of circular (or P-shaped) list it will walk forever, as is
            // the case in normal run without startKey.
            while ((numQueries < maxQueries) && ((numQueries == 0) || (!isSpecificStart))) {
                if (!isSpecificStart) {
                    startKey = new byte[IntegrationTestBigLinkedList.ROWKEY_LENGTH];
                    rand.nextBytes(startKey);
                }
                IntegrationTestBigLinkedList.CINode node = IntegrationTestBigLinkedList.WalkerBase.findStartNode(table, startKey);
                if ((node == null) && isSpecificStart) {
                    System.err.printf("Start node not found: %s \n", Bytes.toStringBinary(startKey));
                }
                numQueries++;
                while (((node != null) && ((node.prev.length) != (IntegrationTestBigLinkedList.NO_KEY.length))) && (numQueries < maxQueries)) {
                    byte[] prev = node.prev;
                    long t1 = System.currentTimeMillis();
                    node = getNode(prev, table, node);
                    long t2 = System.currentTimeMillis();
                    if ((logEvery > 0) && ((numQueries % logEvery) == 0)) {
                        System.out.printf("CQ %d: %d %s \n", numQueries, (t2 - t1), Bytes.toStringBinary(prev));
                    }
                    numQueries++;
                    if (node == null) {
                        System.err.printf("UNDEFINED NODE %s \n", Bytes.toStringBinary(prev));
                    } else
                        if ((node.prev.length) == (IntegrationTestBigLinkedList.NO_KEY.length)) {
                            System.err.printf("TERMINATING NODE %s \n", Bytes.toStringBinary(node.key));
                        }

                } 
            } 
            table.close();
            connection.close();
            return 0;
        }
    }

    private static class Clean extends Configured implements Tool {
        @Override
        public int run(String[] args) throws Exception {
            if ((args.length) < 1) {
                System.err.println("Usage: Clean <output dir>");
                return -1;
            }
            Path p = new Path(args[0]);
            Configuration conf = getConf();
            TableName tableName = IntegrationTestBigLinkedList.getTableName(conf);
            try (FileSystem fs = HFileSystem.get(conf);Connection conn = ConnectionFactory.createConnection(conf);Admin admin = conn.getAdmin()) {
                if (admin.tableExists(tableName)) {
                    admin.disableTable(tableName);
                    admin.deleteTable(tableName);
                }
                if (fs.exists(p)) {
                    fs.delete(p, true);
                }
            }
            return 0;
        }
    }

    protected IntegrationTestingUtility util;

    @Test
    public void testContinuousIngest() throws IOException, Exception {
        // Loop <num iterations> <num mappers> <num nodes per mapper> <output dir> <num reducers>
        Configuration conf = getTestingUtil(getConf()).getConfiguration();
        if (IntegrationTestBigLinkedList.isMultiUnevenColumnFamilies(getConf())) {
            // make sure per CF flush is on
            conf.set(HBASE_FLUSH_POLICY_KEY, FlushAllLargeStoresPolicy.class.getName());
        }
        int ret = ToolRunner.run(conf, new IntegrationTestBigLinkedList.Loop(), new String[]{ "1", "1", "2000000", getDataTestDirOnTestFS("IntegrationTestBigLinkedList").toString(), "1" });
        Assert.assertEquals(0, ret);
    }
}

