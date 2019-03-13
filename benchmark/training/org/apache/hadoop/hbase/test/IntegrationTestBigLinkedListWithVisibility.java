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


import HBaseMarkers.FATAL;
import Import.Importer;
import Permission.Action;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.Import;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.AccessControlClient;
import org.apache.hadoop.hbase.security.access.Permission;
import org.apache.hadoop.hbase.security.visibility.Authorizations;
import org.apache.hadoop.hbase.security.visibility.CellVisibility;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.AbstractHBaseTool;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * IT test used to verify the deletes with visibility labels.
 * The test creates three tables tablename_0, tablename_1 and tablename_2 and each table
 * is associated with a unique pair of labels.
 * Another common table with the name 'commontable' is created and it has the data combined
 * from all these 3 tables such that there are 3 versions of every row but the visibility label
 * in every row corresponds to the table from which the row originated.
 * Then deletes are issued to the common table by selecting the visibility label
 * associated with each of the smaller tables.
 * After the delete is issued with one set of visibility labels we try to scan the common table
 * with each of the visibility pairs defined for the 3 tables.
 * So after the first delete is issued, a scan with the first set of visibility labels would
 * return zero result whereas the scan issued with the other two sets of visibility labels
 * should return all the rows corresponding to that set of visibility labels.  The above
 * process of delete and scan is repeated until after the last set of visibility labels are
 * used for the deletes the common table should not return any row.
 *
 * To use this
 * ./hbase org.apache.hadoop.hbase.test.IntegrationTestBigLinkedListWithVisibility Loop 1 1 20000 /tmp 1 10000
 * or
 * ./hbase org.apache.hadoop.hbase.IntegrationTestsDriver -r .*IntegrationTestBigLinkedListWithVisibility.*
 */
@Category(IntegrationTests.class)
public class IntegrationTestBigLinkedListWithVisibility extends IntegrationTestBigLinkedList {
    private static final String CONFIDENTIAL = "confidential";

    private static final String TOPSECRET = "topsecret";

    private static final String SECRET = "secret";

    private static final String PUBLIC = "public";

    private static final String PRIVATE = "private";

    private static final String EVERYONE = "everyone";

    private static final String RESTRICTED = "restricted";

    private static final String GROUP = "group";

    private static final String PREVILIGED = "previliged";

    private static final String OPEN = "open";

    public static String labels = ((((((((((((((((((IntegrationTestBigLinkedListWithVisibility.CONFIDENTIAL) + ",") + (IntegrationTestBigLinkedListWithVisibility.TOPSECRET)) + ",") + (IntegrationTestBigLinkedListWithVisibility.SECRET)) + ",") + (IntegrationTestBigLinkedListWithVisibility.RESTRICTED)) + ",") + (IntegrationTestBigLinkedListWithVisibility.PRIVATE)) + ",") + (IntegrationTestBigLinkedListWithVisibility.PREVILIGED)) + ",") + (IntegrationTestBigLinkedListWithVisibility.GROUP)) + ",") + (IntegrationTestBigLinkedListWithVisibility.OPEN)) + ",") + (IntegrationTestBigLinkedListWithVisibility.PUBLIC)) + ",") + (IntegrationTestBigLinkedListWithVisibility.EVERYONE);

    private static final String COMMA = ",";

    private static final String UNDER_SCORE = "_";

    public static int DEFAULT_TABLES_COUNT = 3;

    public static String tableName = "tableName";

    public static final String COMMON_TABLE_NAME = "commontable";

    public static final String LABELS_KEY = "LABELS";

    public static final String INDEX_KEY = "INDEX";

    private static User USER;

    private static final String OR = "|";

    private static String USER_OPT = "user";

    private static String userName = "user1";

    static class VisibilityGenerator extends IntegrationTestBigLinkedList.Generator {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator.class);

        @Override
        protected void createSchema() throws IOException {
            IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator.LOG.info("Creating tables");
            // Create three tables
            boolean acl = AccessControlClient.isAccessControllerRunning(ConnectionFactory.createConnection(getConf()));
            if (!acl) {
                IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator.LOG.info("No ACL available.");
            }
            try (Connection conn = ConnectionFactory.createConnection(getConf());Admin admin = conn.getAdmin()) {
                for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                    TableName tableName = IntegrationTestBigLinkedListWithVisibility.getTableName(i);
                    createTable(admin, tableName, false, acl);
                }
                TableName tableName = TableName.valueOf(IntegrationTestBigLinkedListWithVisibility.COMMON_TABLE_NAME);
                createTable(admin, tableName, true, acl);
            }
        }

        private void createTable(Admin admin, TableName tableName, boolean setVersion, boolean acl) throws IOException {
            if (!(admin.tableExists(tableName))) {
                HTableDescriptor htd = new HTableDescriptor(tableName);
                HColumnDescriptor family = new HColumnDescriptor(IntegrationTestBigLinkedList.FAMILY_NAME);
                if (setVersion) {
                    family.setMaxVersions(IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT);
                }
                htd.addFamily(family);
                admin.createTable(htd);
                if (acl) {
                    IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator.LOG.info(("Granting permissions for user " + (IntegrationTestBigLinkedListWithVisibility.USER.getShortName())));
                    Permission[] actions = new Action[]{ Action.READ };
                    try {
                        AccessControlClient.grant(ConnectionFactory.createConnection(getConf()), tableName, IntegrationTestBigLinkedListWithVisibility.USER.getShortName(), null, null, actions);
                    } catch (Throwable e) {
                        IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator.LOG.error(FATAL, ("Error in granting permission for the user " + (IntegrationTestBigLinkedListWithVisibility.USER.getShortName())), e);
                        throw new IOException(e);
                    }
                }
            }
        }

        @Override
        protected void setMapperForGenerator(Job job) {
            job.setMapperClass(IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator.VisibilityGeneratorMapper.class);
        }

        static class VisibilityGeneratorMapper extends IntegrationTestBigLinkedList.Generator.GeneratorMapper {
            BufferedMutator[] tables = new BufferedMutator[IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT];

            @Override
            protected void setup(Context context) throws IOException, InterruptedException {
                super.setup(context);
            }

            @Override
            protected void instantiateHTable() throws IOException {
                for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                    BufferedMutatorParams params = new BufferedMutatorParams(IntegrationTestBigLinkedListWithVisibility.getTableName(i));
                    params.writeBufferSize(((4 * 1024) * 1024));
                    BufferedMutator table = connection.getBufferedMutator(params);
                    this.tables[i] = table;
                }
            }

            @Override
            protected void cleanup(Context context) throws IOException, InterruptedException {
                for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                    if ((tables[i]) != null) {
                        tables[i].close();
                    }
                }
            }

            @Override
            protected void persist(Context output, long count, byte[][] prev, byte[][] current, byte[] id) throws IOException {
                String visibilityExps = "";
                String[] split = IntegrationTestBigLinkedListWithVisibility.labels.split(IntegrationTestBigLinkedListWithVisibility.COMMA);
                for (int i = 0; i < (current.length); i++) {
                    for (int j = 0; j < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); j++) {
                        Put put = new Put(current[i]);
                        byte[] value = (prev == null) ? IntegrationTestBigLinkedList.NO_KEY : prev[i];
                        put.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV, value);
                        if (count >= 0) {
                            put.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_COUNT, Bytes.toBytes((count + i)));
                        }
                        if (id != null) {
                            put.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_CLIENT, id);
                        }
                        visibilityExps = ((split[(j * 2)]) + (IntegrationTestBigLinkedListWithVisibility.OR)) + (split[((j * 2) + 1)]);
                        put.setCellVisibility(new CellVisibility(visibilityExps));
                        tables[j].mutate(put);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new IOException();
                        }
                    }
                    if ((i % 1000) == 0) {
                        // Tickle progress every so often else maprunner will think us hung
                        output.progress();
                    }
                }
            }
        }
    }

    static class Copier extends Configured implements Tool {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedListWithVisibility.Copier.class);

        private TableName tableName;

        private int labelIndex;

        private boolean delete;

        public Copier(TableName tableName, int index, boolean delete) {
            this.tableName = tableName;
            this.labelIndex = index;
            this.delete = delete;
        }

        public int runCopier(String outputDir) throws Exception {
            Job job = null;
            Scan scan = null;
            job = new Job(getConf());
            job.setJobName("Data copier");
            job.getConfiguration().setInt("INDEX", labelIndex);
            job.getConfiguration().set("LABELS", IntegrationTestBigLinkedListWithVisibility.labels);
            job.setJarByClass(getClass());
            scan = new Scan();
            scan.setCacheBlocks(false);
            scan.setRaw(true);
            String[] split = IntegrationTestBigLinkedListWithVisibility.labels.split(IntegrationTestBigLinkedListWithVisibility.COMMA);
            scan.setAuthorizations(new Authorizations(split[((this.labelIndex) * 2)], split[(((this.labelIndex) * 2) + 1)]));
            if (delete) {
                IntegrationTestBigLinkedListWithVisibility.Copier.LOG.info("Running deletes");
            } else {
                IntegrationTestBigLinkedListWithVisibility.Copier.LOG.info("Running copiers");
            }
            if (delete) {
                TableMapReduceUtil.initTableMapperJob(tableName.getNameAsString(), scan, IntegrationTestBigLinkedListWithVisibility.VisibilityDeleteImport.class, null, null, job);
            } else {
                TableMapReduceUtil.initTableMapperJob(tableName.getNameAsString(), scan, IntegrationTestBigLinkedListWithVisibility.VisibilityImport.class, null, null, job);
            }
            job.getConfiguration().setBoolean("mapreduce.map.speculative", false);
            job.getConfiguration().setBoolean("mapreduce.reduce.speculative", false);
            TableMapReduceUtil.initTableReducerJob(IntegrationTestBigLinkedListWithVisibility.COMMON_TABLE_NAME, null, job, null, null, null, null);
            TableMapReduceUtil.addDependencyJars(job);
            TableMapReduceUtil.addDependencyJars(job.getConfiguration(), AbstractHBaseTool.class);
            TableMapReduceUtil.initCredentials(job);
            job.setNumReduceTasks(0);
            boolean success = job.waitForCompletion(true);
            return success ? 0 : 1;
        }

        @Override
        public int run(String[] arg0) throws Exception {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    static class VisibilityImport extends Import.Importer {
        private int index;

        private String labels;

        private String[] split;

        @Override
        public void setup(Context context) {
            index = context.getConfiguration().getInt(IntegrationTestBigLinkedListWithVisibility.INDEX_KEY, (-1));
            labels = context.getConfiguration().get(IntegrationTestBigLinkedListWithVisibility.LABELS_KEY);
            split = labels.split(IntegrationTestBigLinkedListWithVisibility.COMMA);
            super.setup(context);
        }

        @Override
        protected void addPutToKv(Put put, Cell kv) throws IOException {
            String visibilityExps = ((split[((index) * 2)]) + (IntegrationTestBigLinkedListWithVisibility.OR)) + (split[(((index) * 2) + 1)]);
            put.setCellVisibility(new CellVisibility(visibilityExps));
            super.addPutToKv(put, kv);
        }
    }

    static class VisibilityDeleteImport extends Import.Importer {
        private int index;

        private String labels;

        private String[] split;

        @Override
        public void setup(Context context) {
            index = context.getConfiguration().getInt(IntegrationTestBigLinkedListWithVisibility.INDEX_KEY, (-1));
            labels = context.getConfiguration().get(IntegrationTestBigLinkedListWithVisibility.LABELS_KEY);
            split = labels.split(IntegrationTestBigLinkedListWithVisibility.COMMA);
            super.setup(context);
        }

        // Creating delete here
        @Override
        protected void processKV(ImmutableBytesWritable key, Result result, Context context, Put put, Delete delete) throws IOException, InterruptedException {
            String visibilityExps = ((split[((index) * 2)]) + (IntegrationTestBigLinkedListWithVisibility.OR)) + (split[(((index) * 2) + 1)]);
            for (Cell kv : result.rawCells()) {
                // skip if we filter it out
                if (kv == null)
                    continue;

                // Create deletes here
                if (delete == null) {
                    delete = new Delete(key.get());
                }
                delete.setCellVisibility(new CellVisibility(visibilityExps));
                delete.addFamily(CellUtil.cloneFamily(kv));
            }
            if (delete != null) {
                context.write(key, delete);
            }
        }
    }

    static class VisibilityVerify extends IntegrationTestBigLinkedList.Verify {
        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedListWithVisibility.VisibilityVerify.class);

        private TableName tableName;

        private int labelIndex;

        public VisibilityVerify(String tableName, int index) {
            this.tableName = TableName.valueOf(tableName);
            this.labelIndex = index;
        }

        @Override
        public int run(final Path outputDir, final int numReducers) throws Exception {
            IntegrationTestBigLinkedListWithVisibility.VisibilityVerify.LOG.info(((("Running Verify with outputDir=" + outputDir) + ", numReducers=") + numReducers));
            PrivilegedExceptionAction<Integer> scanAction = new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws Exception {
                    return doVerify(outputDir, numReducers);
                }
            };
            return IntegrationTestBigLinkedListWithVisibility.USER.runAs(scanAction);
        }

        private int doVerify(Path outputDir, int numReducers) throws IOException, ClassNotFoundException, InterruptedException {
            job = new Job(getConf());
            job.setJobName("Link Verifier");
            job.setNumReduceTasks(numReducers);
            job.setJarByClass(getClass());
            IntegrationTestBigLinkedList.setJobScannerConf(job);
            Scan scan = new Scan();
            scan.addColumn(IntegrationTestBigLinkedList.FAMILY_NAME, IntegrationTestBigLinkedList.COLUMN_PREV);
            scan.setCaching(10000);
            scan.setCacheBlocks(false);
            String[] split = IntegrationTestBigLinkedListWithVisibility.labels.split(IntegrationTestBigLinkedListWithVisibility.COMMA);
            scan.setAuthorizations(new Authorizations(split[((this.labelIndex) * 2)], split[(((this.labelIndex) * 2) + 1)]));
            TableMapReduceUtil.initTableMapperJob(tableName.getName(), scan, IntegrationTestBigLinkedList.Verify.VerifyMapper.class, BytesWritable.class, BytesWritable.class, job);
            TableMapReduceUtil.addDependencyJars(job.getConfiguration(), AbstractHBaseTool.class);
            job.getConfiguration().setBoolean("mapreduce.map.speculative", false);
            job.setReducerClass(IntegrationTestBigLinkedList.Verify.VerifyReducer.class);
            job.setOutputFormatClass(TextOutputFormat.class);
            TextOutputFormat.setOutputPath(job, outputDir);
            boolean success = job.waitForCompletion(true);
            return success ? 0 : 1;
        }

        @Override
        protected void handleFailure(Counters counters) throws IOException {
            try (Connection conn = ConnectionFactory.createConnection(job.getConfiguration())) {
                TableName tableName = TableName.valueOf(IntegrationTestBigLinkedListWithVisibility.COMMON_TABLE_NAME);
                CounterGroup g = counters.getGroup("undef");
                Iterator<Counter> it = g.iterator();
                while (it.hasNext()) {
                    String keyString = it.next().getName();
                    byte[] key = Bytes.toBytes(keyString);
                    HRegionLocation loc = conn.getRegionLocator(tableName).getRegionLocation(key, true);
                    IntegrationTestBigLinkedListWithVisibility.VisibilityVerify.LOG.error(((("undefined row " + keyString) + ", ") + loc));
                } 
                g = counters.getGroup("unref");
                it = g.iterator();
                while (it.hasNext()) {
                    String keyString = it.next().getName();
                    byte[] key = Bytes.toBytes(keyString);
                    HRegionLocation loc = conn.getRegionLocator(tableName).getRegionLocation(key, true);
                    IntegrationTestBigLinkedListWithVisibility.VisibilityVerify.LOG.error(((("unreferred row " + keyString) + ", ") + loc));
                } 
            }
        }
    }

    static class VisibilityLoop extends IntegrationTestBigLinkedList.Loop {
        private static final int SLEEP_IN_MS = 5000;

        private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.class);

        IntegrationTestBigLinkedListWithVisibility it;

        @Override
        protected void runGenerator(int numMappers, long numNodes, String outputDir, Integer width, Integer wrapMultiplier, Integer numWalkers) throws Exception {
            Path outputPath = new Path(outputDir);
            UUID uuid = UUID.randomUUID();// create a random UUID.

            Path generatorOutput = new Path(outputPath, uuid.toString());
            IntegrationTestBigLinkedList.Generator generator = new IntegrationTestBigLinkedListWithVisibility.VisibilityGenerator();
            generator.setConf(getConf());
            int retCode = generator.run(numMappers, numNodes, generatorOutput, width, wrapMultiplier, numWalkers);
            if (retCode > 0) {
                throw new RuntimeException(("Generator failed with return code: " + retCode));
            }
        }

        protected void runDelete(int numMappers, long numNodes, String outputDir, Integer width, Integer wrapMultiplier, int tableIndex) throws Exception {
            IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Running copier on table " + (IntegrationTestBigLinkedListWithVisibility.getTableName(tableIndex))));
            IntegrationTestBigLinkedListWithVisibility.Copier copier = new IntegrationTestBigLinkedListWithVisibility.Copier(IntegrationTestBigLinkedListWithVisibility.getTableName(tableIndex), tableIndex, true);
            copier.setConf(getConf());
            copier.runCopier(outputDir);
            Thread.sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
        }

        protected void runVerify(String outputDir, int numReducers, long expectedNumNodes, boolean allTables) throws Exception {
            Path outputPath = new Path(outputDir);
            if (allTables) {
                for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                    IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Verifying table " + i));
                    sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                    UUID uuid = UUID.randomUUID();// create a random UUID.

                    Path iterationOutput = new Path(outputPath, uuid.toString());
                    IntegrationTestBigLinkedList.Verify verify = new IntegrationTestBigLinkedListWithVisibility.VisibilityVerify(IntegrationTestBigLinkedListWithVisibility.getTableName(i).getNameAsString(), i);
                    verify(numReducers, expectedNumNodes, iterationOutput, verify);
                }
            }
            for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                runVerifyCommonTable(outputDir, numReducers, expectedNumNodes, i);
            }
        }

        private void runVerify(String outputDir, int numReducers, long expectedNodes, int tableIndex) throws Exception {
            long temp = expectedNodes;
            for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                if (i <= tableIndex) {
                    expectedNodes = 0;
                } else {
                    expectedNodes = temp;
                }
                IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(((("Verifying data in the table with index " + i) + " and expected nodes is ") + expectedNodes));
                runVerifyCommonTable(outputDir, numReducers, expectedNodes, i);
            }
        }

        private void sleep(long ms) throws InterruptedException {
            Thread.sleep(ms);
        }

        protected void runVerifyCommonTable(String outputDir, int numReducers, long expectedNumNodes, int index) throws Exception {
            IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Verifying common table with index " + index));
            sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
            Path outputPath = new Path(outputDir);
            UUID uuid = UUID.randomUUID();// create a random UUID.

            Path iterationOutput = new Path(outputPath, uuid.toString());
            IntegrationTestBigLinkedList.Verify verify = new IntegrationTestBigLinkedListWithVisibility.VisibilityVerify(TableName.valueOf(IntegrationTestBigLinkedListWithVisibility.COMMON_TABLE_NAME).getNameAsString(), index);
            verify(numReducers, expectedNumNodes, iterationOutput, verify);
        }

        protected void runCopier(String outputDir) throws Exception {
            for (int i = 0; i < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); i++) {
                IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Running copier " + (IntegrationTestBigLinkedListWithVisibility.getTableName(i))));
                sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                IntegrationTestBigLinkedListWithVisibility.Copier copier = new IntegrationTestBigLinkedListWithVisibility.Copier(IntegrationTestBigLinkedListWithVisibility.getTableName(i), i, false);
                copier.setConf(getConf());
                copier.runCopier(outputDir);
            }
        }

        private void verify(int numReducers, long expectedNumNodes, Path iterationOutput, IntegrationTestBigLinkedList.Verify verify) throws Exception {
            verify.setConf(getConf());
            int retCode = verify.run(iterationOutput, numReducers);
            if (retCode > 0) {
                throw new RuntimeException(("Verify.run failed with return code: " + retCode));
            }
            if (!(verify.verify(expectedNumNodes))) {
                throw new RuntimeException("Verify.verify failed");
            }
            IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Verify finished with succees. Total nodes=" + expectedNumNodes));
        }

        @Override
        public int run(String[] args) throws Exception {
            if ((args.length) < 5) {
                System.err.println(("Usage: Loop <num iterations> " + ("<num mappers> <num nodes per mapper> <output dir> " + "<num reducers> [<width> <wrap multiplier>]")));
                return 1;
            }
            IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Running Loop with args:" + (Arrays.deepToString(args))));
            int numIterations = Integer.parseInt(args[0]);
            int numMappers = Integer.parseInt(args[1]);
            long numNodes = Long.parseLong(args[2]);
            String outputDir = args[3];
            int numReducers = Integer.parseInt(args[4]);
            Integer width = ((args.length) < 6) ? null : Integer.parseInt(args[5]);
            Integer wrapMultiplier = ((args.length) < 7) ? null : Integer.parseInt(args[6]);
            long expectedNumNodes = 0;
            if (numIterations < 0) {
                numIterations = Integer.MAX_VALUE;// run indefinitely (kind of)

            }
            for (int i = 0; i < numIterations; i++) {
                IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Starting iteration = " + i));
                IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info("Generating data");
                // By default run no concurrent walkers for test with visibility
                runGenerator(numMappers, numNodes, outputDir, width, wrapMultiplier, 0);
                expectedNumNodes += numMappers * numNodes;
                // Copying wont work because expressions are not returned back to the
                // client
                IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info("Running copier");
                sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                runCopier(outputDir);
                IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info("Verifying copied data");
                sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                runVerify(outputDir, numReducers, expectedNumNodes, true);
                sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                for (int j = 0; j < (IntegrationTestBigLinkedListWithVisibility.DEFAULT_TABLES_COUNT); j++) {
                    IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info(("Deleting data on table with index: " + j));
                    runDelete(numMappers, numNodes, outputDir, width, wrapMultiplier, j);
                    sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                    IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.LOG.info("Verifying common table after deleting");
                    runVerify(outputDir, numReducers, expectedNumNodes, j);
                    sleep(IntegrationTestBigLinkedListWithVisibility.VisibilityLoop.SLEEP_IN_MS);
                }
            }
            return 0;
        }
    }

    @Override
    @Test
    public void testContinuousIngest() throws IOException, Exception {
        // Loop <num iterations> <num mappers> <num nodes per mapper> <output dir>
        // <num reducers>
        int ret = ToolRunner.run(getTestingUtil(getConf()).getConfiguration(), new IntegrationTestBigLinkedListWithVisibility.VisibilityLoop(), new String[]{ "1", "1", "20000", getDataTestDirOnTestFS("IntegrationTestBigLinkedListWithVisibility").toString(), "1", "10000" });
        Assert.assertEquals(0, ret);
    }
}

