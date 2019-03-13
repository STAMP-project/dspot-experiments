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
package org.apache.hadoop.hbase.wal;


import HConstants.RECOVERED_EDITS_DIR;
import RegionInfoBuilder.FIRST_META_REGIONINFO;
import TableName.META_TABLE_NAME;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.regionserver.wal.FaultyProtobufLogReader;
import org.apache.hadoop.hbase.regionserver.wal.InstrumentedLogWriter;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CancelableProgressable;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.WAL.Entry;
import org.apache.hadoop.hbase.wal.WALProvider.Writer;
import org.apache.hadoop.hbase.wal.WALSplitter.CorruptedLogFileException;
import org.apache.hadoop.hdfs.server.namenode.LeaseExpiredException;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hbase.thirdparty.com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.regionserver.wal.FaultyProtobufLogReader.FailureType.BEGINNING;
import static org.apache.hadoop.hbase.regionserver.wal.FaultyProtobufLogReader.FailureType.NONE;
import static org.apache.hadoop.hbase.regionserver.wal.FaultyProtobufLogReader.FailureType.values;


/**
 * Testing {@link WAL} splitting code.
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestWALSplit {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALSplit.class);

    {
        // Uncomment the following lines if more verbosity is needed for
        // debugging (see HBASE-12285 for details).
        // ((Log4JLogger)DataNode.LOG).getLogger().setLevel(Level.ALL);
        // ((Log4JLogger)LeaseManager.LOG).getLogger().setLevel(Level.ALL);
        // ((Log4JLogger)FSNamesystem.LOG).getLogger().setLevel(Level.ALL);
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestWALSplit.class);

    private static Configuration conf;

    private FileSystem fs;

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Path HBASEDIR;

    private Path HBASELOGDIR;

    private Path WALDIR;

    private Path OLDLOGDIR;

    private Path CORRUPTDIR;

    private Path TABLEDIR;

    private String TMPDIRNAME;

    private static final int NUM_WRITERS = 10;

    private static final int ENTRIES = 10;// entries per writer per region


    private static final String FILENAME_BEING_SPLIT = "testfile";

    private static final TableName TABLE_NAME = TableName.valueOf("t1");

    private static final byte[] FAMILY = Bytes.toBytes("f1");

    private static final byte[] QUALIFIER = Bytes.toBytes("q1");

    private static final byte[] VALUE = Bytes.toBytes("v1");

    private static final String WAL_FILE_PREFIX = "wal.dat.";

    private static List<String> REGIONS = new ArrayList<>();

    private static final String HBASE_SKIP_ERRORS = "hbase.hlog.split.skip.errors";

    private static String ROBBER;

    private static String ZOMBIE;

    private static String[] GROUP = new String[]{ "supergroup" };

    static enum Corruptions {

        INSERT_GARBAGE_ON_FIRST_LINE,
        INSERT_GARBAGE_IN_THE_MIDDLE,
        APPEND_GARBAGE,
        TRUNCATE,
        TRUNCATE_TRAILER;}

    @Rule
    public TestName name = new TestName();

    private WALFactory wals = null;

    /**
     * Simulates splitting a WAL out from under a regionserver that is still trying to write it.
     * Ensures we do not lose edits.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testLogCannotBeWrittenOnceParsed() throws IOException, InterruptedException {
        final AtomicLong counter = new AtomicLong(0);
        AtomicBoolean stop = new AtomicBoolean(false);
        // Region we'll write edits too and then later examine to make sure they all made it in.
        final String region = TestWALSplit.REGIONS.get(0);
        final int numWriters = 3;
        Thread zombie = new TestWALSplit.ZombieLastLogWriterRegionServer(counter, stop, region, numWriters);
        try {
            long startCount = counter.get();
            zombie.start();
            // Wait till writer starts going.
            while (startCount == (counter.get()))
                Threads.sleep(1);

            // Give it a second to write a few appends.
            Threads.sleep(1000);
            final Configuration conf2 = HBaseConfiguration.create(TestWALSplit.conf);
            final User robber = User.createUserForTesting(conf2, TestWALSplit.ROBBER, TestWALSplit.GROUP);
            int count = robber.runAs(new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws Exception {
                    StringBuilder ls = new StringBuilder("Contents of WALDIR (").append(WALDIR).append("):\n");
                    for (FileStatus status : fs.listStatus(WALDIR)) {
                        ls.append("\t").append(status.toString()).append("\n");
                    }
                    TestWALSplit.LOG.debug(Objects.toString(ls));
                    TestWALSplit.LOG.info((("Splitting WALs out from under zombie. Expecting " + numWriters) + " files."));
                    WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, conf2, wals);
                    TestWALSplit.LOG.info("Finished splitting out from under zombie.");
                    Path[] logfiles = getLogForRegion(TestWALSplit.TABLE_NAME, region);
                    Assert.assertEquals("wrong number of split files for region", numWriters, logfiles.length);
                    int count = 0;
                    for (Path logfile : logfiles) {
                        count += countWAL(logfile);
                    }
                    return count;
                }
            });
            TestWALSplit.LOG.info(((("zombie=" + (counter.get())) + ", robber=") + count));
            Assert.assertTrue((((("The log file could have at most 1 extra log entry, but can't have less. " + "Zombie could write ") + (counter.get())) + " and logfile had only ") + count), (((counter.get()) == count) || (((counter.get()) + 1) == count)));
        } finally {
            stop.set(true);
            zombie.interrupt();
            Threads.threadDumpingIsAlive(zombie);
        }
    }

    /**
     * This thread will keep writing to a 'wal' file even after the split process has started.
     * It simulates a region server that was considered dead but woke up and wrote some more to the
     * last log entry. Does its writing as an alternate user in another filesystem instance to
     * simulate better it being a regionserver.
     */
    class ZombieLastLogWriterRegionServer extends Thread {
        final AtomicLong editsCount;

        final AtomicBoolean stop;

        final int numOfWriters;

        /**
         * Region to write edits for.
         */
        final String region;

        final User user;

        public ZombieLastLogWriterRegionServer(AtomicLong counter, AtomicBoolean stop, final String region, final int writers) throws IOException, InterruptedException {
            super("ZombieLastLogWriterRegionServer");
            setDaemon(true);
            this.stop = stop;
            this.editsCount = counter;
            this.region = region;
            this.user = User.createUserForTesting(TestWALSplit.conf, TestWALSplit.ZOMBIE, TestWALSplit.GROUP);
            numOfWriters = writers;
        }

        @Override
        public void run() {
            try {
                doWriting();
            } catch (IOException e) {
                TestWALSplit.LOG.warn((((getName()) + " Writer exiting ") + e));
            } catch (InterruptedException e) {
                TestWALSplit.LOG.warn((((getName()) + " Writer exiting ") + e));
            }
        }

        private void doWriting() throws IOException, InterruptedException {
            this.user.runAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // Index of the WAL we want to keep open.  generateWALs will leave open the WAL whose
                    // index we supply here.
                    int walToKeepOpen = (numOfWriters) - 1;
                    // The below method writes numOfWriters files each with ENTRIES entries for a total of
                    // numOfWriters * ENTRIES added per column family in the region.
                    Writer writer = null;
                    try {
                        writer = generateWALs(numOfWriters, TestWALSplit.ENTRIES, walToKeepOpen);
                    } catch (IOException e1) {
                        throw new RuntimeException("Failed", e1);
                    }
                    // Update counter so has all edits written so far.
                    editsCount.addAndGet(((numOfWriters) * (TestWALSplit.ENTRIES)));
                    loop(writer);
                    // If we've been interruped, then things should have shifted out from under us.
                    // closing should error
                    try {
                        writer.close();
                        Assert.fail("Writing closing after parsing should give an error.");
                    } catch (IOException exception) {
                        TestWALSplit.LOG.debug("ignoring error when closing final writer.", exception);
                    }
                    return null;
                }
            });
        }

        private void loop(final Writer writer) {
            byte[] regionBytes = Bytes.toBytes(this.region);
            while (!(stop.get())) {
                try {
                    long seq = TestWALSplit.appendEntry(writer, TestWALSplit.TABLE_NAME, regionBytes, Bytes.toBytes(("r" + (editsCount.get()))), regionBytes, TestWALSplit.QUALIFIER, TestWALSplit.VALUE, 0);
                    long count = editsCount.incrementAndGet();
                    TestWALSplit.LOG.info((((((getName()) + " sync count=") + count) + ", seq=") + seq));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // 
                    }
                } catch (IOException ex) {
                    TestWALSplit.LOG.error((((getName()) + " ex ") + (ex.toString())));
                    if (ex instanceof RemoteException) {
                        TestWALSplit.LOG.error(((("Juliet: got RemoteException " + (ex.getMessage())) + " while writing ") + ((editsCount.get()) + 1)));
                    } else {
                        TestWALSplit.LOG.error((((getName()) + " failed to write....at ") + (editsCount.get())));
                        Assert.fail(("Failed to write " + (editsCount.get())));
                    }
                    break;
                } catch (Throwable t) {
                    TestWALSplit.LOG.error((((getName()) + " HOW? ") + t));
                    TestWALSplit.LOG.debug("exception details", t);
                    break;
                }
            } 
            TestWALSplit.LOG.info(((getName()) + " Writer exiting"));
        }
    }

    /**
     * {@see https://issues.apache.org/jira/browse/HBASE-3020}
     */
    @Test
    public void testRecoveredEditsPathForMeta() throws IOException {
        byte[] encoded = FIRST_META_REGIONINFO.getEncodedNameAsBytes();
        Path tdir = FSUtils.getTableDir(HBASEDIR, META_TABLE_NAME);
        Path regiondir = new Path(tdir, FIRST_META_REGIONINFO.getEncodedName());
        fs.mkdirs(regiondir);
        long now = System.currentTimeMillis();
        Entry entry = new Entry(new WALKeyImpl(encoded, TableName.META_TABLE_NAME, 1, now, HConstants.DEFAULT_CLUSTER_ID), new WALEdit());
        Path p = WALSplitter.getRegionSplitEditsPath(entry, TestWALSplit.FILENAME_BEING_SPLIT, TMPDIRNAME, TestWALSplit.conf);
        String parentOfParent = p.getParent().getParent().getName();
        Assert.assertEquals(parentOfParent, FIRST_META_REGIONINFO.getEncodedName());
    }

    /**
     * Test old recovered edits file doesn't break WALSplitter.
     * This is useful in upgrading old instances.
     */
    @Test
    public void testOldRecoveredEditsFileSidelined() throws IOException {
        byte[] encoded = FIRST_META_REGIONINFO.getEncodedNameAsBytes();
        Path tdir = FSUtils.getTableDir(HBASEDIR, META_TABLE_NAME);
        Path regiondir = new Path(tdir, FIRST_META_REGIONINFO.getEncodedName());
        fs.mkdirs(regiondir);
        long now = System.currentTimeMillis();
        Entry entry = new Entry(new WALKeyImpl(encoded, TableName.META_TABLE_NAME, 1, now, HConstants.DEFAULT_CLUSTER_ID), new WALEdit());
        Path parent = WALSplitter.getRegionDirRecoveredEditsDir(regiondir);
        Assert.assertEquals(RECOVERED_EDITS_DIR, parent.getName());
        fs.createNewFile(parent);// create a recovered.edits file

        Path p = WALSplitter.getRegionSplitEditsPath(entry, TestWALSplit.FILENAME_BEING_SPLIT, TMPDIRNAME, TestWALSplit.conf);
        String parentOfParent = p.getParent().getParent().getName();
        Assert.assertEquals(parentOfParent, FIRST_META_REGIONINFO.getEncodedName());
        WALFactory.createRecoveredEditsWriter(fs, p, TestWALSplit.conf).close();
    }

    @Test
    public void testSplitPreservesEdits() throws IOException {
        final String REGION = "region__1";
        TestWALSplit.REGIONS.clear();
        TestWALSplit.REGIONS.add(REGION);
        generateWALs(1, 10, (-1), 0);
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        Path originalLog = fs.listStatus(OLDLOGDIR)[0].getPath();
        Path[] splitLog = getLogForRegion(TestWALSplit.TABLE_NAME, REGION);
        Assert.assertEquals(1, splitLog.length);
        Assert.assertTrue("edits differ after split", logsAreEqual(originalLog, splitLog[0]));
    }

    @Test
    public void testSplitRemovesRegionEventsEdits() throws IOException {
        final String REGION = "region__1";
        TestWALSplit.REGIONS.clear();
        TestWALSplit.REGIONS.add(REGION);
        generateWALs(1, 10, (-1), 100);
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        Path originalLog = fs.listStatus(OLDLOGDIR)[0].getPath();
        Path[] splitLog = getLogForRegion(TestWALSplit.TABLE_NAME, REGION);
        Assert.assertEquals(1, splitLog.length);
        Assert.assertFalse("edits differ after split", logsAreEqual(originalLog, splitLog[0]));
        // split log should only have the test edits
        Assert.assertEquals(10, countWAL(splitLog[0]));
    }

    @Test
    public void testSplitLeavesCompactionEventsEdits() throws IOException {
        RegionInfo hri = RegionInfoBuilder.newBuilder(TestWALSplit.TABLE_NAME).build();
        TestWALSplit.REGIONS.clear();
        TestWALSplit.REGIONS.add(hri.getEncodedName());
        Path regionDir = new Path(FSUtils.getTableDir(HBASEDIR, TestWALSplit.TABLE_NAME), hri.getEncodedName());
        TestWALSplit.LOG.info(("Creating region directory: " + regionDir));
        Assert.assertTrue(fs.mkdirs(regionDir));
        Writer writer = generateWALs(1, 10, 0, 10);
        String[] compactInputs = new String[]{ "file1", "file2", "file3" };
        String compactOutput = "file4";
        TestWALSplit.appendCompactionEvent(writer, hri, compactInputs, compactOutput);
        writer.close();
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        Path originalLog = fs.listStatus(OLDLOGDIR)[0].getPath();
        // original log should have 10 test edits, 10 region markers, 1 compaction marker
        Assert.assertEquals(21, countWAL(originalLog));
        Path[] splitLog = getLogForRegion(TestWALSplit.TABLE_NAME, hri.getEncodedName());
        Assert.assertEquals(1, splitLog.length);
        Assert.assertFalse("edits differ after split", logsAreEqual(originalLog, splitLog[0]));
        // split log should have 10 test edits plus 1 compaction marker
        Assert.assertEquals(11, countWAL(splitLog[0]));
    }

    @Test
    public void testEmptyLogFiles() throws IOException {
        testEmptyLogFiles(true);
    }

    @Test
    public void testEmptyOpenLogFiles() throws IOException {
        testEmptyLogFiles(false);
    }

    @Test
    public void testOpenZeroLengthReportedFileButWithDataGetsSplit() throws IOException {
        // generate logs but leave wal.dat.5 open.
        generateWALs(5);
        splitAndCount(TestWALSplit.NUM_WRITERS, ((TestWALSplit.NUM_WRITERS) * (TestWALSplit.ENTRIES)));
    }

    @Test
    public void testTralingGarbageCorruptionFileSkipErrorsPasses() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, true);
        generateWALs(Integer.MAX_VALUE);
        corruptWAL(new Path(WALDIR, ((TestWALSplit.WAL_FILE_PREFIX) + "5")), TestWALSplit.Corruptions.APPEND_GARBAGE, true);
        splitAndCount(TestWALSplit.NUM_WRITERS, ((TestWALSplit.NUM_WRITERS) * (TestWALSplit.ENTRIES)));
    }

    @Test
    public void testFirstLineCorruptionLogFileSkipErrorsPasses() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, true);
        generateWALs(Integer.MAX_VALUE);
        corruptWAL(new Path(WALDIR, ((TestWALSplit.WAL_FILE_PREFIX) + "5")), TestWALSplit.Corruptions.INSERT_GARBAGE_ON_FIRST_LINE, true);
        splitAndCount(((TestWALSplit.NUM_WRITERS) - 1), (((TestWALSplit.NUM_WRITERS) - 1) * (TestWALSplit.ENTRIES)));// 1 corrupt

    }

    @Test
    public void testMiddleGarbageCorruptionSkipErrorsReadsHalfOfFile() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, true);
        generateWALs(Integer.MAX_VALUE);
        corruptWAL(new Path(WALDIR, ((TestWALSplit.WAL_FILE_PREFIX) + "5")), TestWALSplit.Corruptions.INSERT_GARBAGE_IN_THE_MIDDLE, false);
        // the entries in the original logs are alternating regions
        // considering the sequence file header, the middle corruption should
        // affect at least half of the entries
        int goodEntries = ((TestWALSplit.NUM_WRITERS) - 1) * (TestWALSplit.ENTRIES);
        int firstHalfEntries = ((int) (Math.ceil(((TestWALSplit.ENTRIES) / 2)))) - 1;
        int allRegionsCount = splitAndCount(TestWALSplit.NUM_WRITERS, (-1));
        Assert.assertTrue("The file up to the corrupted area hasn't been parsed", (((TestWALSplit.REGIONS.size()) * (goodEntries + firstHalfEntries)) <= allRegionsCount));
    }

    @Test
    public void testCorruptedFileGetsArchivedIfSkipErrors() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, true);
        List<FaultyProtobufLogReader.FailureType> failureTypes = Arrays.asList(values()).stream().filter(( x) -> x != (NONE)).collect(Collectors.toList());
        for (FaultyProtobufLogReader.FailureType failureType : failureTypes) {
            final Set<String> walDirContents = splitCorruptWALs(failureType);
            final Set<String> archivedLogs = new HashSet<>();
            final StringBuilder archived = new StringBuilder("Archived logs in CORRUPTDIR:");
            for (FileStatus log : fs.listStatus(CORRUPTDIR)) {
                archived.append("\n\t").append(log.toString());
                archivedLogs.add(log.getPath().getName());
            }
            TestWALSplit.LOG.debug(archived.toString());
            Assert.assertEquals(((failureType.name()) + ": expected to find all of our wals corrupt."), archivedLogs, walDirContents);
        }
    }

    @Test(expected = IOException.class)
    public void testTrailingGarbageCorruptionLogFileSkipErrorsFalseThrows() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, false);
        splitCorruptWALs(BEGINNING);
    }

    @Test
    public void testCorruptedLogFilesSkipErrorsFalseDoesNotTouchLogs() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, false);
        try {
            splitCorruptWALs(BEGINNING);
        } catch (IOException e) {
            TestWALSplit.LOG.debug("split with 'skip errors' set to 'false' correctly threw");
        }
        Assert.assertEquals("if skip.errors is false all files should remain in place", TestWALSplit.NUM_WRITERS, fs.listStatus(WALDIR).length);
    }

    @Test
    public void testEOFisIgnored() throws IOException {
        int entryCount = 10;
        ignoreCorruption(TestWALSplit.Corruptions.TRUNCATE, entryCount, (entryCount - 1));
    }

    @Test
    public void testCorruptWALTrailer() throws IOException {
        int entryCount = 10;
        ignoreCorruption(TestWALSplit.Corruptions.TRUNCATE_TRAILER, entryCount, entryCount);
    }

    @Test
    public void testLogsGetArchivedAfterSplit() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, false);
        generateWALs((-1));
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        FileStatus[] archivedLogs = fs.listStatus(OLDLOGDIR);
        Assert.assertEquals("wrong number of files in the archive log", TestWALSplit.NUM_WRITERS, archivedLogs.length);
    }

    @Test
    public void testSplit() throws IOException {
        generateWALs((-1));
        splitAndCount(TestWALSplit.NUM_WRITERS, ((TestWALSplit.NUM_WRITERS) * (TestWALSplit.ENTRIES)));
    }

    @Test
    public void testLogDirectoryShouldBeDeletedAfterSuccessfulSplit() throws IOException {
        generateWALs((-1));
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        FileStatus[] statuses = null;
        try {
            statuses = fs.listStatus(WALDIR);
            if (statuses != null) {
                Assert.fail(("Files left in log dir: " + (Joiner.on(",").join(FileUtil.stat2Paths(statuses)))));
            }
        } catch (FileNotFoundException e) {
            // hadoop 0.21 throws FNFE whereas hadoop 0.20 returns null
        }
    }

    @Test(expected = IOException.class)
    public void testSplitWillFailIfWritingToRegionFails() throws Exception {
        // leave 5th log open so we could append the "trap"
        Writer writer = generateWALs(4);
        useDifferentDFSClient();
        String region = "break";
        Path regiondir = new Path(TABLEDIR, region);
        fs.mkdirs(regiondir);
        InstrumentedLogWriter.activateFailure = false;
        TestWALSplit.appendEntry(writer, TestWALSplit.TABLE_NAME, Bytes.toBytes(region), Bytes.toBytes(("r" + 999)), TestWALSplit.FAMILY, TestWALSplit.QUALIFIER, TestWALSplit.VALUE, 0);
        writer.close();
        try {
            InstrumentedLogWriter.activateFailure = true;
            WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("This exception is instrumented and should only be thrown for testing"));
            throw e;
        } finally {
            InstrumentedLogWriter.activateFailure = false;
        }
    }

    @Test
    public void testSplitDeletedRegion() throws IOException {
        TestWALSplit.REGIONS.clear();
        String region = "region_that_splits";
        TestWALSplit.REGIONS.add(region);
        generateWALs(1);
        useDifferentDFSClient();
        Path regiondir = new Path(TABLEDIR, region);
        fs.delete(regiondir, true);
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        Assert.assertFalse(fs.exists(regiondir));
    }

    @Test
    public void testIOEOnOutputThread() throws Exception {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, false);
        generateWALs((-1));
        useDifferentDFSClient();
        FileStatus[] logfiles = fs.listStatus(WALDIR);
        Assert.assertTrue("There should be some log file", ((logfiles != null) && ((logfiles.length) > 0)));
        // wals with no entries (like the one we don't use in the factory)
        // won't cause a failure since nothing will ever be written.
        // pick the largest one since it's most likely to have entries.
        int largestLogFile = 0;
        long largestSize = 0;
        for (int i = 0; i < (logfiles.length); i++) {
            if ((logfiles[i].getLen()) > largestSize) {
                largestLogFile = i;
                largestSize = logfiles[i].getLen();
            }
        }
        Assert.assertTrue("There should be some log greater than size 0.", (0 < largestSize));
        // Set up a splitter that will throw an IOE on the output side
        WALSplitter logSplitter = new WALSplitter(wals, TestWALSplit.conf, HBASEDIR, fs, null, null) {
            @Override
            protected Writer createWriter(Path logfile) throws IOException {
                Writer mockWriter = Mockito.mock(Writer.class);
                Mockito.doThrow(new IOException("Injected")).when(mockWriter).append(Mockito.<Entry>any());
                return mockWriter;
            }
        };
        // Set up a background thread dumper.  Needs a thread to depend on and then we need to run
        // the thread dumping in a background thread so it does not hold up the test.
        final AtomicBoolean stop = new AtomicBoolean(false);
        final Thread someOldThread = new Thread("Some-old-thread") {
            @Override
            public void run() {
                while (!(stop.get()))
                    Threads.sleep(10);

            }
        };
        someOldThread.setDaemon(true);
        someOldThread.start();
        final Thread t = new Thread("Background-thread-dumper") {
            @Override
            public void run() {
                try {
                    Threads.threadDumpingIsAlive(someOldThread);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.setDaemon(true);
        t.start();
        try {
            logSplitter.splitLogFile(logfiles[largestLogFile], null);
            Assert.fail("Didn't throw!");
        } catch (IOException ioe) {
            Assert.assertTrue(ioe.toString().contains("Injected"));
        } finally {
            // Setting this to true will turn off the background thread dumper.
            stop.set(true);
        }
    }

    // Test for HBASE-3412
    @Test
    public void testMovedWALDuringRecovery() throws Exception {
        // This partial mock will throw LEE for every file simulating
        // files that were moved
        FileSystem spiedFs = Mockito.spy(fs);
        // The "File does not exist" part is very important,
        // that's how it comes out of HDFS
        Mockito.doThrow(new LeaseExpiredException("Injected: File does not exist")).when(spiedFs).append(Mockito.<Path>any());
        retryOverHdfsProblem(spiedFs);
    }

    @Test
    public void testRetryOpenDuringRecovery() throws Exception {
        FileSystem spiedFs = Mockito.spy(fs);
        // The "Cannot obtain block length", "Could not obtain the last block",
        // and "Blocklist for [^ ]* has changed.*" part is very important,
        // that's how it comes out of HDFS. If HDFS changes the exception
        // message, this test needs to be adjusted accordingly.
        // 
        // When DFSClient tries to open a file, HDFS needs to locate
        // the last block of the file and get its length. However, if the
        // last block is under recovery, HDFS may have problem to obtain
        // the block length, in which case, retry may help.
        Mockito.doAnswer(new Answer<FSDataInputStream>() {
            private final String[] errors = new String[]{ "Cannot obtain block length", "Could not obtain the last block", ("Blocklist for " + (OLDLOGDIR)) + " has changed" };

            private int count = 0;

            @Override
            public FSDataInputStream answer(InvocationOnMock invocation) throws Throwable {
                if ((count) < 3) {
                    throw new IOException(errors[((count)++)]);
                }
                return ((FSDataInputStream) (invocation.callRealMethod()));
            }
        }).when(spiedFs).open(Mockito.<Path>any(), Mockito.anyInt());
        retryOverHdfsProblem(spiedFs);
    }

    @Test
    public void testTerminationAskedByReporter() throws IOException, CorruptedLogFileException {
        generateWALs(1, 10, (-1));
        FileStatus logfile = fs.listStatus(WALDIR)[0];
        useDifferentDFSClient();
        final AtomicInteger count = new AtomicInteger();
        CancelableProgressable localReporter = new CancelableProgressable() {
            @Override
            public boolean progress() {
                count.getAndIncrement();
                return false;
            }
        };
        FileSystem spiedFs = Mockito.spy(fs);
        Mockito.doAnswer(new Answer<FSDataInputStream>() {
            @Override
            public FSDataInputStream answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(1500);// Sleep a while and wait report status invoked

                return ((FSDataInputStream) (invocation.callRealMethod()));
            }
        }).when(spiedFs).open(Mockito.<Path>any(), Mockito.anyInt());
        try {
            TestWALSplit.conf.setInt("hbase.splitlog.report.period", 1000);
            boolean ret = WALSplitter.splitLogFile(HBASEDIR, logfile, spiedFs, TestWALSplit.conf, localReporter, null, null, wals);
            Assert.assertFalse("Log splitting should failed", ret);
            Assert.assertTrue(((count.get()) > 0));
        } catch (IOException e) {
            Assert.fail(("There shouldn't be any exception but: " + (e.toString())));
        } finally {
            // reset it back to its default value
            TestWALSplit.conf.setInt("hbase.splitlog.report.period", 59000);
        }
    }

    /**
     * Test log split process with fake data and lots of edits to trigger threading
     * issues.
     */
    @Test
    public void testThreading() throws Exception {
        doTestThreading(20000, ((128 * 1024) * 1024), 0);
    }

    /**
     * Test blocking behavior of the log split process if writers are writing slower
     * than the reader is reading.
     */
    @Test
    public void testThreadingSlowWriterSmallBuffer() throws Exception {
        doTestThreading(200, 1024, 50);
    }

    // Does leaving the writer open in testSplitDeletedRegion matter enough for two tests?
    @Test
    public void testSplitLogFileDeletedRegionDir() throws IOException {
        TestWALSplit.LOG.info("testSplitLogFileDeletedRegionDir");
        final String REGION = "region__1";
        TestWALSplit.REGIONS.clear();
        TestWALSplit.REGIONS.add(REGION);
        generateWALs(1, 10, (-1));
        useDifferentDFSClient();
        Path regiondir = new Path(TABLEDIR, REGION);
        TestWALSplit.LOG.info(("Region directory is" + regiondir));
        fs.delete(regiondir, true);
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        Assert.assertFalse(fs.exists(regiondir));
    }

    @Test
    public void testSplitLogFileEmpty() throws IOException {
        TestWALSplit.LOG.info("testSplitLogFileEmpty");
        // we won't create the hlog dir until getWAL got called, so
        // make dir here when testing empty log file
        fs.mkdirs(WALDIR);
        injectEmptyFile(".empty", true);
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        Path tdir = FSUtils.getTableDir(HBASEDIR, TestWALSplit.TABLE_NAME);
        Assert.assertFalse(fs.exists(tdir));
        Assert.assertEquals(0, countWAL(fs.listStatus(OLDLOGDIR)[0].getPath()));
    }

    @Test
    public void testSplitLogFileMultipleRegions() throws IOException {
        TestWALSplit.LOG.info("testSplitLogFileMultipleRegions");
        generateWALs(1, 10, (-1));
        splitAndCount(1, 10);
    }

    @Test
    public void testSplitLogFileFirstLineCorruptionLog() throws IOException {
        TestWALSplit.conf.setBoolean(TestWALSplit.HBASE_SKIP_ERRORS, true);
        generateWALs(1, 10, (-1));
        FileStatus logfile = fs.listStatus(WALDIR)[0];
        corruptWAL(logfile.getPath(), TestWALSplit.Corruptions.INSERT_GARBAGE_ON_FIRST_LINE, true);
        useDifferentDFSClient();
        WALSplitter.split(HBASELOGDIR, WALDIR, OLDLOGDIR, fs, TestWALSplit.conf, wals);
        final Path corruptDir = new Path(FSUtils.getWALRootDir(TestWALSplit.conf), HConstants.CORRUPT_DIR_NAME);
        Assert.assertEquals(1, fs.listStatus(corruptDir).length);
    }

    /**
     * {@see https://issues.apache.org/jira/browse/HBASE-4862}
     */
    @Test
    public void testConcurrentSplitLogAndReplayRecoverEdit() throws IOException {
        TestWALSplit.LOG.info("testConcurrentSplitLogAndReplayRecoverEdit");
        // Generate wals for our destination region
        String regionName = "r0";
        final Path regiondir = new Path(TABLEDIR, regionName);
        TestWALSplit.REGIONS.clear();
        TestWALSplit.REGIONS.add(regionName);
        generateWALs((-1));
        wals.getWAL(null);
        FileStatus[] logfiles = fs.listStatus(WALDIR);
        Assert.assertTrue("There should be some log file", ((logfiles != null) && ((logfiles.length) > 0)));
        WALSplitter logSplitter = new WALSplitter(wals, TestWALSplit.conf, HBASEDIR, fs, null, null) {
            @Override
            protected Writer createWriter(Path logfile) throws IOException {
                Writer writer = wals.createRecoveredEditsWriter(this.walFS, logfile);
                // After creating writer, simulate region's
                // replayRecoveredEditsIfAny() which gets SplitEditFiles of this
                // region and delete them, excluding files with '.temp' suffix.
                NavigableSet<Path> files = WALSplitter.getSplitEditFilesSorted(fs, regiondir);
                if ((files != null) && (!(files.isEmpty()))) {
                    for (Path file : files) {
                        if (!(this.walFS.delete(file, false))) {
                            TestWALSplit.LOG.error(("Failed delete of " + file));
                        } else {
                            TestWALSplit.LOG.debug(("Deleted recovered.edits file=" + file));
                        }
                    }
                }
                return writer;
            }
        };
        try {
            logSplitter.splitLogFile(logfiles[0], null);
        } catch (IOException e) {
            TestWALSplit.LOG.info(e.toString(), e);
            Assert.fail(("Throws IOException when spliting " + ("log, it is most likely because writing file does not " + "exist which is caused by concurrent replayRecoveredEditsIfAny()")));
        }
        if (fs.exists(CORRUPTDIR)) {
            if ((fs.listStatus(CORRUPTDIR).length) > 0) {
                Assert.fail(("There are some corrupt logs, " + "it is most likely caused by concurrent replayRecoveredEditsIfAny()"));
            }
        }
    }
}

