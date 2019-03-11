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
package org.apache.hadoop.mapred.gridmix;


import CommonConfigurationKeys.FS_DEFAULT_NAME_KEY;
import GridmixJobSubmissionPolicy.JOB_SUBMISSION_POLICY;
import GridmixJobSubmissionPolicy.REPLAY;
import JTConfig.JT_IPC_ADDRESS;
import Summarizer.NA;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.gridmix.GenerateData.DataStatistics;
import org.apache.hadoop.mapred.gridmix.Statistics.ClusterStats;
import org.apache.hadoop.mapred.gridmix.Statistics.JobStats;
import org.apache.hadoop.tools.rumen.JobStory;
import org.apache.hadoop.tools.rumen.JobStoryProducer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link ExecutionSummarizer} and {@link ClusterSummarizer}.
 */
public class TestGridmixSummary {
    /**
     * Test {@link DataStatistics}.
     */
    @Test
    public void testDataStatistics() throws Exception {
        // test data-statistics getters with compression enabled
        DataStatistics stats = new DataStatistics(10, 2, true);
        Assert.assertEquals("Data size mismatch", 10, stats.getDataSize());
        Assert.assertEquals("Num files mismatch", 2, stats.getNumFiles());
        Assert.assertTrue("Compression configuration mismatch", stats.isDataCompressed());
        // test data-statistics getters with compression disabled
        stats = new DataStatistics(100, 5, false);
        Assert.assertEquals("Data size mismatch", 100, stats.getDataSize());
        Assert.assertEquals("Num files mismatch", 5, stats.getNumFiles());
        Assert.assertFalse("Compression configuration mismatch", stats.isDataCompressed());
        // test publish data stats
        Configuration conf = new Configuration();
        Path rootTempDir = new Path(System.getProperty("test.build.data", "/tmp"));
        Path testDir = new Path(rootTempDir, "testDataStatistics");
        FileSystem fs = testDir.getFileSystem(conf);
        fs.delete(testDir, true);
        Path testInputDir = new Path(testDir, "test");
        fs.mkdirs(testInputDir);
        // test empty folder (compression = true)
        CompressionEmulationUtil.setCompressionEmulationEnabled(conf, true);
        Boolean failed = null;
        try {
            GenerateData.publishDataStatistics(testInputDir, 1024L, conf);
            failed = false;
        } catch (RuntimeException e) {
            failed = true;
        }
        Assert.assertNotNull("Expected failure!", failed);
        Assert.assertTrue("Compression data publishing error", failed);
        // test with empty folder (compression = off)
        CompressionEmulationUtil.setCompressionEmulationEnabled(conf, false);
        stats = GenerateData.publishDataStatistics(testInputDir, 1024L, conf);
        Assert.assertEquals("Data size mismatch", 0, stats.getDataSize());
        Assert.assertEquals("Num files mismatch", 0, stats.getNumFiles());
        Assert.assertFalse("Compression configuration mismatch", stats.isDataCompressed());
        // test with some plain input data (compression = off)
        CompressionEmulationUtil.setCompressionEmulationEnabled(conf, false);
        Path inputDataFile = new Path(testInputDir, "test");
        long size = org.apache.hadoop.mapred.UtilsForTests.createTmpFileDFS(fs, inputDataFile, org.apache.hadoop.fs.permission.FsPermission.createImmutable(((short) (777))), "hi hello bye").size();
        stats = GenerateData.publishDataStatistics(testInputDir, (-1), conf);
        Assert.assertEquals("Data size mismatch", size, stats.getDataSize());
        Assert.assertEquals("Num files mismatch", 1, stats.getNumFiles());
        Assert.assertFalse("Compression configuration mismatch", stats.isDataCompressed());
        // test with some plain input data (compression = on)
        CompressionEmulationUtil.setCompressionEmulationEnabled(conf, true);
        failed = null;
        try {
            GenerateData.publishDataStatistics(testInputDir, 1234L, conf);
            failed = false;
        } catch (RuntimeException e) {
            failed = true;
        }
        Assert.assertNotNull("Expected failure!", failed);
        Assert.assertTrue("Compression data publishing error", failed);
        // test with some compressed input data (compression = off)
        CompressionEmulationUtil.setCompressionEmulationEnabled(conf, false);
        fs.delete(inputDataFile, false);
        inputDataFile = new Path(testInputDir, "test.gz");
        size = org.apache.hadoop.mapred.UtilsForTests.createTmpFileDFS(fs, inputDataFile, org.apache.hadoop.fs.permission.FsPermission.createImmutable(((short) (777))), "hi hello").size();
        stats = GenerateData.publishDataStatistics(testInputDir, 1234L, conf);
        Assert.assertEquals("Data size mismatch", size, stats.getDataSize());
        Assert.assertEquals("Num files mismatch", 1, stats.getNumFiles());
        Assert.assertFalse("Compression configuration mismatch", stats.isDataCompressed());
        // test with some compressed input data (compression = on)
        CompressionEmulationUtil.setCompressionEmulationEnabled(conf, true);
        stats = GenerateData.publishDataStatistics(testInputDir, 1234L, conf);
        Assert.assertEquals("Data size mismatch", size, stats.getDataSize());
        Assert.assertEquals("Num files mismatch", 1, stats.getNumFiles());
        Assert.assertTrue("Compression configuration mismatch", stats.isDataCompressed());
    }

    /**
     * A fake {@link JobFactory}.
     */
    @SuppressWarnings("rawtypes")
    private static class FakeJobFactory extends JobFactory {
        /**
         * A fake {@link JobStoryProducer} for {@link FakeJobFactory}.
         */
        private static class FakeJobStoryProducer implements JobStoryProducer {
            @Override
            public void close() throws IOException {
            }

            @Override
            public JobStory getNextJob() throws IOException {
                return null;
            }
        }

        FakeJobFactory(Configuration conf) {
            super(null, new TestGridmixSummary.FakeJobFactory.FakeJobStoryProducer(), null, conf, null, null);
        }

        @Override
        public void update(Object item) {
        }

        @Override
        protected Thread createReaderThread() {
            return new Thread();
        }
    }

    /**
     * Test {@link ExecutionSummarizer}.
     */
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testExecutionSummarizer() throws IOException {
        Configuration conf = new Configuration();
        ExecutionSummarizer es = new ExecutionSummarizer();
        Assert.assertEquals("ExecutionSummarizer init failed", NA, es.getCommandLineArgsString());
        long startTime = System.currentTimeMillis();
        // test configuration parameters
        String[] initArgs = new String[]{ "-Xmx20m", "-Dtest.args='test'" };
        es = new ExecutionSummarizer(initArgs);
        Assert.assertEquals("ExecutionSummarizer init failed", "-Xmx20m -Dtest.args='test'", es.getCommandLineArgsString());
        // test start time
        Assert.assertTrue("Start time mismatch", ((es.getStartTime()) >= startTime));
        Assert.assertTrue("Start time mismatch", ((es.getStartTime()) <= (System.currentTimeMillis())));
        // test start() of ExecutionSummarizer
        es.update(null);
        Assert.assertEquals("ExecutionSummarizer init failed", 0, es.getSimulationStartTime());
        TestGridmixSummary.testExecutionSummarizer(0, 0, 0, 0, 0, 0, 0, es);
        long simStartTime = System.currentTimeMillis();
        es.start(null);
        Assert.assertTrue("Simulation start time mismatch", ((es.getSimulationStartTime()) >= simStartTime));
        Assert.assertTrue("Simulation start time mismatch", ((es.getSimulationStartTime()) <= (System.currentTimeMillis())));
        // test with job stats
        JobStats stats = TestGridmixSummary.generateFakeJobStats(1, 10, true, false);
        es.update(stats);
        TestGridmixSummary.testExecutionSummarizer(1, 10, 0, 1, 1, 0, 0, es);
        // test with failed job
        stats = TestGridmixSummary.generateFakeJobStats(5, 1, false, false);
        es.update(stats);
        TestGridmixSummary.testExecutionSummarizer(6, 11, 0, 2, 1, 1, 0, es);
        // test with successful but lost job
        stats = TestGridmixSummary.generateFakeJobStats(1, 1, true, true);
        es.update(stats);
        TestGridmixSummary.testExecutionSummarizer(7, 12, 0, 3, 1, 1, 1, es);
        // test with failed but lost job
        stats = TestGridmixSummary.generateFakeJobStats(2, 2, false, true);
        es.update(stats);
        TestGridmixSummary.testExecutionSummarizer(9, 14, 0, 4, 1, 1, 2, es);
        // test finalize
        // define a fake job factory
        JobFactory factory = new TestGridmixSummary.FakeJobFactory(conf);
        // fake the num jobs in trace
        factory.numJobsInTrace = 3;
        Path rootTempDir = new Path(System.getProperty("test.build.data", "/tmp"));
        Path testDir = new Path(rootTempDir, "testGridmixSummary");
        Path testTraceFile = new Path(testDir, "test-trace.json");
        FileSystem fs = FileSystem.getLocal(conf);
        fs.create(testTraceFile).close();
        // finalize the summarizer
        UserResolver resolver = new RoundRobinUserResolver();
        DataStatistics dataStats = new DataStatistics(100, 2, true);
        String policy = REPLAY.name();
        conf.set(JOB_SUBMISSION_POLICY, policy);
        es.finalize(factory, testTraceFile.toString(), 1024L, resolver, dataStats, conf);
        // test num jobs in trace
        Assert.assertEquals("Mismtach in num jobs in trace", 3, es.getNumJobsInTrace());
        // test trace signature
        String tid = ExecutionSummarizer.getTraceSignature(testTraceFile.toString());
        Assert.assertEquals("Mismatch in trace signature", tid, es.getInputTraceSignature());
        // test trace location
        Path qPath = fs.makeQualified(testTraceFile);
        Assert.assertEquals("Mismatch in trace filename", qPath.toString(), es.getInputTraceLocation());
        // test expected data size
        Assert.assertEquals("Mismatch in expected data size", "1 K", es.getExpectedDataSize());
        // test input data statistics
        Assert.assertEquals("Mismatch in input data statistics", ExecutionSummarizer.stringifyDataStatistics(dataStats), es.getInputDataStatistics());
        // test user resolver
        Assert.assertEquals("Mismatch in user resolver", resolver.getClass().getName(), es.getUserResolver());
        // test policy
        Assert.assertEquals("Mismatch in policy", policy, es.getJobSubmissionPolicy());
        // test data stringification using large data
        es.finalize(factory, testTraceFile.toString(), (((1024 * 1024) * 1024) * 10L), resolver, dataStats, conf);
        Assert.assertEquals("Mismatch in expected data size", "10 G", es.getExpectedDataSize());
        // test trace signature uniqueness
        // touch the trace file
        fs.delete(testTraceFile, false);
        // sleep for 1 sec
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }
        fs.create(testTraceFile).close();
        es.finalize(factory, testTraceFile.toString(), 0L, resolver, dataStats, conf);
        // test missing expected data size
        Assert.assertEquals("Mismatch in trace data size", NA, es.getExpectedDataSize());
        Assert.assertFalse("Mismatch in trace signature", tid.equals(es.getInputTraceSignature()));
        // get the new identifier
        tid = ExecutionSummarizer.getTraceSignature(testTraceFile.toString());
        Assert.assertEquals("Mismatch in trace signature", tid, es.getInputTraceSignature());
        testTraceFile = new Path(testDir, "test-trace2.json");
        fs.create(testTraceFile).close();
        es.finalize(factory, testTraceFile.toString(), 0L, resolver, dataStats, conf);
        Assert.assertFalse("Mismatch in trace signature", tid.equals(es.getInputTraceSignature()));
        // get the new identifier
        tid = ExecutionSummarizer.getTraceSignature(testTraceFile.toString());
        Assert.assertEquals("Mismatch in trace signature", tid, es.getInputTraceSignature());
        // finalize trace identifier '-' input
        es.finalize(factory, "-", 0L, resolver, dataStats, conf);
        Assert.assertEquals("Mismatch in trace signature", NA, es.getInputTraceSignature());
        Assert.assertEquals("Mismatch in trace file location", NA, es.getInputTraceLocation());
    }

    /**
     * Test {@link ClusterSummarizer}.
     */
    @Test
    public void testClusterSummarizer() throws IOException {
        ClusterSummarizer cs = new ClusterSummarizer();
        Configuration conf = new Configuration();
        String jt = "test-jt:1234";
        String nn = "test-nn:5678";
        conf.set(JT_IPC_ADDRESS, jt);
        conf.set(FS_DEFAULT_NAME_KEY, nn);
        cs.start(conf);
        Assert.assertEquals("JT name mismatch", jt, cs.getJobTrackerInfo());
        Assert.assertEquals("NN name mismatch", nn, cs.getNamenodeInfo());
        ClusterStats cStats = ClusterStats.getClusterStats();
        conf.set(JT_IPC_ADDRESS, "local");
        conf.set(FS_DEFAULT_NAME_KEY, "local");
        JobClient jc = new JobClient(conf);
        cStats.setClusterMetric(jc.getClusterStatus());
        cs.update(cStats);
        // test
        Assert.assertEquals("Cluster summary test failed!", 1, cs.getMaxMapTasks());
        Assert.assertEquals("Cluster summary test failed!", 1, cs.getMaxReduceTasks());
        Assert.assertEquals("Cluster summary test failed!", 1, cs.getNumActiveTrackers());
        Assert.assertEquals("Cluster summary test failed!", 0, cs.getNumBlacklistedTrackers());
    }
}

