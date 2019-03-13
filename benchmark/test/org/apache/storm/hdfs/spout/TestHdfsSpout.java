/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.hdfs.spout;


import Config.TOPOLOGY_ACKER_EXECUTORS;
import Configs.SEQ;
import Configs.TEXT;
import TextFileReader.defaultFields;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.storm.hdfs.common.HdfsUtils;
import org.apache.storm.hdfs.common.HdfsUtils.Pair;
import org.apache.storm.hdfs.testing.MiniDFSClusterRule;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestHdfsSpout {
    private static final Configuration conf = new Configuration();

    @ClassRule
    public static MiniDFSClusterRule DFS_CLUSTER_RULE = new MiniDFSClusterRule();

    private static DistributedFileSystem fs;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public File baseFolder;

    private Path source;

    private Path archive;

    private Path badfiles;

    @Test
    public void testSimpleText_noACK() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 5);
        Path file2 = new Path(((source.toString()) + "/file2.txt"));
        createTextFile(file2, 5);
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            spout.setCommitFrequencySec(1);
            Map<String, Object> conf = getCommonConfigs();
            openSpout(spout, 0, conf);
            runSpout(spout, "r11");
            Path arc1 = new Path(((archive.toString()) + "/file1.txt"));
            Path arc2 = new Path(((archive.toString()) + "/file2.txt"));
            checkCollectorOutput_txt(((TestHdfsSpout.MockCollector) (spout.getCollector())), arc1, arc2);
        }
    }

    @Test
    public void testSimpleText_ACK() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 5);
        Path file2 = new Path(((source.toString()) + "/file2.txt"));
        createTextFile(file2, 5);
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            spout.setCommitFrequencySec(1);
            Map<String, Object> conf = getCommonConfigs();
            conf.put(TOPOLOGY_ACKER_EXECUTORS, "1");// enable ACKing

            openSpout(spout, 0, conf);
            // consume file 1
            runSpout(spout, "r6", "a0", "a1", "a2", "a3", "a4");
            Path arc1 = new Path(((archive.toString()) + "/file1.txt"));
            checkCollectorOutput_txt(((TestHdfsSpout.MockCollector) (spout.getCollector())), arc1);
            // consume file 2
            runSpout(spout, "r6", "a5", "a6", "a7", "a8", "a9");
            Path arc2 = new Path(((archive.toString()) + "/file2.txt"));
            checkCollectorOutput_txt(((TestHdfsSpout.MockCollector) (spout.getCollector())), arc1, arc2);
        }
    }

    @Test
    public void testEmptySimpleText_ACK() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file_empty.txt"));
        createTextFile(file1, 0);
        Path file2 = new Path(((source.toString()) + "/file.txt"));
        createTextFile(file2, 5);
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            Map<String, Object> conf = getCommonConfigs();
            conf.put(TOPOLOGY_ACKER_EXECUTORS, "1");// enable ACKing

            openSpout(spout, 0, conf);
            // consume empty file
            runSpout(spout, "r1");
            Path arc1 = new Path(((archive.toString()) + "/file_empty.txt"));
            checkCollectorOutput_txt(((TestHdfsSpout.MockCollector) (spout.getCollector())), arc1);
            // consume file 2
            runSpout(spout, "r5", "a0", "a1", "a2", "a3", "a4");
            Path arc2 = new Path(((archive.toString()) + "/file.txt"));
            checkCollectorOutput_txt(((TestHdfsSpout.MockCollector) (spout.getCollector())), arc1, arc2);
        }
    }

    @Test
    public void testResumeAbandoned_Text_NoAck() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 6);
        final Integer lockExpirySec = 1;
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            spout.setCommitFrequencySec(1000);// effectively disable commits based on time

            spout.setLockTimeoutSec(lockExpirySec);
            try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout2 = makeSpout(TEXT, defaultFields)) {
                HdfsSpout spout2 = closeableSpout2.spout;
                spout2.setCommitFrequencyCount(1);
                spout2.setCommitFrequencySec(1000);// effectively disable commits based on time

                spout2.setLockTimeoutSec(lockExpirySec);
                Map<String, Object> conf = getCommonConfigs();
                openSpout(spout, 0, conf);
                openSpout(spout2, 1, conf);
                // consume file 1 partially
                List<String> res = runSpout(spout, "r2");
                Assert.assertEquals(2, res.size());
                // abandon file
                FileLock lock = TestHdfsSpout.getField(spout, "lock");
                TestFileLock.closeUnderlyingLockFile(lock);
                Thread.sleep(((lockExpirySec * 2) * 1000));
                // check lock file presence
                Assert.assertTrue(TestHdfsSpout.fs.exists(lock.getLockFile()));
                // create another spout to take over processing and read a few lines
                List<String> res2 = runSpout(spout2, "r3");
                Assert.assertEquals(3, res2.size());
                // check lock file presence
                Assert.assertTrue(TestHdfsSpout.fs.exists(lock.getLockFile()));
                // check lock file contents
                List<String> contents = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lock.getLockFile().toString());
                Assert.assertFalse(contents.isEmpty());
                // finish up reading the file
                res2 = runSpout(spout2, "r2");
                Assert.assertEquals(4, res2.size());
                // check lock file is gone
                Assert.assertFalse(TestHdfsSpout.fs.exists(lock.getLockFile()));
                FileReader rdr = TestHdfsSpout.getField(spout2, "reader");
                Assert.assertNull(rdr);
                Assert.assertTrue(TestHdfsSpout.getBoolField(spout2, "fileReadCompletely"));
            }
        }
    }

    @Test
    public void testResumeAbandoned_Seq_NoAck() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.seq"));
        TestHdfsSpout.createSeqFile(TestHdfsSpout.fs, file1, 6);
        final Integer lockExpirySec = 1;
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(SEQ, SequenceFileReader.defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            spout.setCommitFrequencySec(1000);// effectively disable commits based on time

            spout.setLockTimeoutSec(lockExpirySec);
            try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout2 = makeSpout(SEQ, SequenceFileReader.defaultFields)) {
                HdfsSpout spout2 = closeableSpout2.spout;
                spout2.setCommitFrequencyCount(1);
                spout2.setCommitFrequencySec(1000);// effectively disable commits based on time

                spout2.setLockTimeoutSec(lockExpirySec);
                Map<String, Object> conf = getCommonConfigs();
                openSpout(spout, 0, conf);
                openSpout(spout2, 1, conf);
                // consume file 1 partially
                List<String> res = runSpout(spout, "r2");
                Assert.assertEquals(2, res.size());
                // abandon file
                FileLock lock = TestHdfsSpout.getField(spout, "lock");
                TestFileLock.closeUnderlyingLockFile(lock);
                Thread.sleep(((lockExpirySec * 2) * 1000));
                // check lock file presence
                Assert.assertTrue(TestHdfsSpout.fs.exists(lock.getLockFile()));
                // create another spout to take over processing and read a few lines
                List<String> res2 = runSpout(spout2, "r3");
                Assert.assertEquals(3, res2.size());
                // check lock file presence
                Assert.assertTrue(TestHdfsSpout.fs.exists(lock.getLockFile()));
                // check lock file contents
                List<String> contents = getTextFileContents(TestHdfsSpout.fs, lock.getLockFile());
                Assert.assertFalse(contents.isEmpty());
                // finish up reading the file
                res2 = runSpout(spout2, "r3");
                Assert.assertEquals(4, res2.size());
                // check lock file is gone
                Assert.assertFalse(TestHdfsSpout.fs.exists(lock.getLockFile()));
                FileReader rdr = TestHdfsSpout.getField(spout2, "reader");
                Assert.assertNull(rdr);
                Assert.assertTrue(TestHdfsSpout.getBoolField(spout2, "fileReadCompletely"));
            }
        }
    }

    @Test
    public void testMultipleFileConsumption_Ack() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 5);
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            spout.setCommitFrequencySec(1);
            Map<String, Object> conf = getCommonConfigs();
            conf.put(TOPOLOGY_ACKER_EXECUTORS, "1");// enable ACKing

            openSpout(spout, 0, conf);
            // read few lines from file1 dont ack
            runSpout(spout, "r3");
            FileReader reader = TestHdfsSpout.getField(spout, "reader");
            Assert.assertNotNull(reader);
            Assert.assertEquals(false, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
            // read remaining lines
            runSpout(spout, "r3");
            reader = TestHdfsSpout.getField(spout, "reader");
            Assert.assertNotNull(reader);
            Assert.assertEquals(true, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
            // ack few
            runSpout(spout, "a0", "a1", "a2");
            reader = TestHdfsSpout.getField(spout, "reader");
            Assert.assertNotNull(reader);
            Assert.assertEquals(true, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
            // ack rest
            runSpout(spout, "a3", "a4");
            reader = TestHdfsSpout.getField(spout, "reader");
            Assert.assertNull(reader);
            Assert.assertEquals(true, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
            // go to next file
            Path file2 = new Path(((source.toString()) + "/file2.txt"));
            createTextFile(file2, 5);
            // Read 1 line
            runSpout(spout, "r1");
            Assert.assertNotNull(TestHdfsSpout.getField(spout, "reader"));
            Assert.assertEquals(false, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
            // ack 1 tuple
            runSpout(spout, "a5");
            Assert.assertNotNull(TestHdfsSpout.getField(spout, "reader"));
            Assert.assertEquals(false, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
            // read and ack remaining lines
            runSpout(spout, "r5", "a6", "a7", "a8", "a9");
            Assert.assertNull(TestHdfsSpout.getField(spout, "reader"));
            Assert.assertEquals(true, TestHdfsSpout.getBoolField(spout, "fileReadCompletely"));
        }
    }

    @Test
    public void testSimpleSequenceFile() throws Exception {
        // 1) create a couple files to consume
        source = new Path("/tmp/hdfsspout/source");
        TestHdfsSpout.fs.mkdirs(source);
        archive = new Path("/tmp/hdfsspout/archive");
        TestHdfsSpout.fs.mkdirs(archive);
        Path file1 = new Path(((source) + "/file1.seq"));
        TestHdfsSpout.createSeqFile(TestHdfsSpout.fs, file1, 5);
        Path file2 = new Path(((source) + "/file2.seq"));
        TestHdfsSpout.createSeqFile(TestHdfsSpout.fs, file2, 5);
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(SEQ, SequenceFileReader.defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            Map<String, Object> conf = getCommonConfigs();
            openSpout(spout, 0, conf);
            // consume both files
            List<String> res = runSpout(spout, "r11");
            Assert.assertEquals(10, res.size());
            Assert.assertEquals(2, listDir(archive).size());
            Path f1 = new Path(((archive) + "/file1.seq"));
            Path f2 = new Path(((archive) + "/file2.seq"));
            checkCollectorOutput_seq(((TestHdfsSpout.MockCollector) (spout.getCollector())), f1, f2);
        }
    }

    @Test
    public void testReadFailures() throws Exception {
        // 1) create couple of input files to read
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        Path file2 = new Path(((source.toString()) + "/file2.txt"));
        createTextFile(file1, 6);
        createTextFile(file2, 7);
        Assert.assertEquals(2, listDir(source).size());
        // 2) run spout
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TestHdfsSpout.MockTextFailingReader.class.getName(), TestHdfsSpout.MockTextFailingReader.defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            Map<String, Object> conf = getCommonConfigs();
            openSpout(spout, 0, conf);
            List<String> res = runSpout(spout, "r11");
            String[] expected = new String[]{ "[line 0]", "[line 1]", "[line 2]", "[line 0]", "[line 1]", "[line 2]" };
            Assert.assertArrayEquals(expected, res.toArray());
            // 3) make sure 6 lines (3 from each file) were read in all
            Assert.assertEquals(((TestHdfsSpout.MockCollector) (spout.getCollector())).lines.size(), 6);
            ArrayList<Path> badFiles = HdfsUtils.listFilesByModificationTime(TestHdfsSpout.fs, badfiles, 0);
            Assert.assertEquals(badFiles.size(), 2);
        }
    }

    // check lock creation/deletion and contents
    @Test
    public void testLocking() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 10);
        // 0) config spout to log progress in lock file for each tuple
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(1);
            spout.setCommitFrequencySec(1000);// effectively disable commits based on time

            Map<String, Object> conf = getCommonConfigs();
            openSpout(spout, 0, conf);
            // 1) read initial lines in file, then check if lock exists
            List<String> res = runSpout(spout, "r5");
            Assert.assertEquals(5, res.size());
            List<String> lockFiles = listDir(spout.getLockDirPath());
            Assert.assertEquals(1, lockFiles.size());
            // 2) check log file content line count == tuples emitted + 1
            List<String> lines = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lockFiles.get(0));
            Assert.assertEquals(lines.size(), ((res.size()) + 1));
            // 3) read remaining lines in file, then ensure lock is gone
            runSpout(spout, "r6");
            lockFiles = listDir(spout.getLockDirPath());
            Assert.assertEquals(0, lockFiles.size());
            // 4)  --- Create another input file and reverify same behavior ---
            Path file2 = new Path(((source.toString()) + "/file2.txt"));
            createTextFile(file2, 10);
            // 5) read initial lines in file, then check if lock exists
            res = runSpout(spout, "r5");
            Assert.assertEquals(15, res.size());
            lockFiles = listDir(spout.getLockDirPath());
            Assert.assertEquals(1, lockFiles.size());
            // 6) check log file content line count == tuples emitted + 1
            lines = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lockFiles.get(0));
            Assert.assertEquals(6, lines.size());
            // 7) read remaining lines in file, then ensure lock is gone
            runSpout(spout, "r6");
            lockFiles = listDir(spout.getLockDirPath());
            Assert.assertEquals(0, lockFiles.size());
        }
    }

    @Test
    public void testLockLoggingFreqCount() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 10);
        // 0) config spout to log progress in lock file for each tuple
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(2);// 1 lock log entry every 2 tuples

            spout.setCommitFrequencySec(1000);// Effectively disable commits based on time

            Map<String, Object> conf = getCommonConfigs();
            openSpout(spout, 0, conf);
            // 1) read 5 lines in file,
            runSpout(spout, "r5");
            // 2) check log file contents
            String lockFile = listDir(spout.getLockDirPath()).get(0);
            List<String> lines = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lockFile);
            Assert.assertEquals(lines.size(), 3);
            // 3) read 6th line and see if another log entry was made
            runSpout(spout, "r1");
            lines = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lockFile);
            Assert.assertEquals(lines.size(), 4);
        }
    }

    @Test
    public void testLockLoggingFreqSec() throws Exception {
        Path file1 = new Path(((source.toString()) + "/file1.txt"));
        createTextFile(file1, 10);
        // 0) config spout to log progress in lock file for each tuple
        try (TestHdfsSpout.AutoCloseableHdfsSpout closeableSpout = makeSpout(TEXT, defaultFields)) {
            HdfsSpout spout = closeableSpout.spout;
            spout.setCommitFrequencyCount(0);// disable it

            spout.setCommitFrequencySec(2);// log every 2 sec

            Map<String, Object> conf = getCommonConfigs();
            openSpout(spout, 0, conf);
            // 1) read 5 lines in file
            runSpout(spout, "r5");
            // 2) check log file contents
            String lockFile = listDir(spout.getLockDirPath()).get(0);
            List<String> lines = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lockFile);
            Assert.assertEquals(lines.size(), 1);
            Thread.sleep(3000);// allow freq_sec to expire

            // 3) read another line and see if another log entry was made
            runSpout(spout, "r1");
            lines = TestHdfsSpout.readTextFile(TestHdfsSpout.fs, lockFile);
            Assert.assertEquals(2, lines.size());
        }
    }

    private static class AutoCloseableHdfsSpout implements AutoCloseable {
        private final HdfsSpout spout;

        public AutoCloseableHdfsSpout(HdfsSpout spout) {
            this.spout = spout;
        }

        @Override
        public void close() throws Exception {
            spout.close();
        }
    }

    static class MockCollector extends SpoutOutputCollector {
        // comma separated offsets
        public ArrayList<String> lines;

        public ArrayList<Pair<HdfsSpout.MessageId, List<Object>>> items;

        public MockCollector() {
            super(null);
            lines = new ArrayList<>();
            items = new ArrayList();
        }

        @Override
        public List<Integer> emit(List<Object> tuple, Object messageId) {
            lines.add(tuple.toString());
            items.add(HdfsUtils.Pair.of(messageId, tuple));
            return null;
        }

        @Override
        public List<Integer> emit(String streamId, List<Object> tuple, Object messageId) {
            return emit(tuple, messageId);
        }

        @Override
        public void emitDirect(int arg0, String arg1, List<Object> arg2, Object arg3) {
            throw new UnsupportedOperationException("NOT Implemented");
        }

        @Override
        public void reportError(Throwable arg0) {
            throw new UnsupportedOperationException("NOT Implemented");
        }

        @Override
        public long getPendingCount() {
            return 0;
        }
    }

    // Throws IOExceptions for 3rd & 4th call to next(), succeeds on 5th, thereafter
    // throws ParseException. Effectively produces 3 lines (1,2 & 3) from each file read
    static class MockTextFailingReader extends TextFileReader {
        public static final String[] defaultFields = new String[]{ "line" };

        int readAttempts = 0;

        public MockTextFailingReader(FileSystem fs, Path file, Map<String, Object> conf) throws IOException {
            super(fs, file, conf);
        }

        @Override
        public List<Object> next() throws IOException, ParseException {
            (readAttempts)++;
            if (((readAttempts) == 3) || ((readAttempts) == 4)) {
                throw new IOException("mock test exception");
            } else
                if ((readAttempts) > 5) {
                    throw new ParseException("mock test exception", null);
                }

            return super.next();
        }
    }

    static class MockTopologyContext extends TopologyContext {
        private final int componentId;

        public MockTopologyContext(int componentId, Map<String, Object> topoConf) {
            // StormTopology topology, Map<String, Object> topoConf, Map<Integer, String> taskToComponent, Map<String, List<Integer>>
            // componentToSortedTasks, Map<String, Map<String, Fields>> componentToStreamToFields, String stormId, String codeDir, String
            // pidDir, Integer taskId, Integer workerPort, List<Integer> workerTasks, Map<String, Object> defaultResources, Map<String,
            // Object> userResources, Map<String, Object> executorData, Map<Integer, Map<Integer, Map<String, IMetric>>>
            // registeredMetrics, Atom openOrPrepareWasCalled
            super(null, topoConf, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
            this.componentId = componentId;
        }

        public String getThisComponentId() {
            return Integer.toString(componentId);
        }
    }
}

