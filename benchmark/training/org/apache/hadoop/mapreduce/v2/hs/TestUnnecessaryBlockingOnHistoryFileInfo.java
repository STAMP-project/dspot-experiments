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
package org.apache.hadoop.mapreduce.v2.hs;


import HistoryFileManager.HistoryFileInfo;
import JHAdminConfig.MR_HISTORY_INTERMEDIATE_DONE_DIR;
import JHAdminConfig.MR_HISTORY_MAX_AGE_MS;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobIndexInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static java.lang.Thread.State.BLOCKED;


/**
 * The test in this class is created specifically to address the issue in
 * MAPREDUCE-6684. In cases where there are two threads trying to load different
 * jobs through job history file manager, one thread could be blocked by the
 * other that is loading a huge job file, which is undesirable.
 */
public class TestUnnecessaryBlockingOnHistoryFileInfo {
    /**
     * The intermediate done directory that JHS scans for completed jobs.
     */
    private static final File INTERMEDIATE_DIR = new File("target", ((TestUnnecessaryBlockingOnHistoryFileInfo.class.getName()) + "/intermediate"));

    /**
     * A test user directory under intermediate done directory.
     */
    private static final File USER_DIR = new File(TestUnnecessaryBlockingOnHistoryFileInfo.INTERMEDIATE_DIR, "test");

    /**
     * This create a test case in which two threads are trying to load two
     * different jobs of the same user under the intermediate directory.
     * One thread should not be blocked by the other thread that is loading
     * a huge job files (This is simulated by hanging up parsing the job files
     * forever). The test will fail by triggering the timeout if one thread is
     * blocked by the other while the other thread is holding the lock on its
     * associated job files and hanging up parsing the files.
     */
    @Test(timeout = 600000)
    public void testTwoThreadsQueryingDifferentJobOfSameUser() throws IOException, InterruptedException {
        final Configuration config = new Configuration();
        config.set(MR_HISTORY_INTERMEDIATE_DONE_DIR, TestUnnecessaryBlockingOnHistoryFileInfo.INTERMEDIATE_DIR.getPath());
        config.setLong(MR_HISTORY_MAX_AGE_MS, Long.MAX_VALUE);
        final JobId job1 = TestUnnecessaryBlockingOnHistoryFileInfo.createJobId(0);
        final JobId job2 = TestUnnecessaryBlockingOnHistoryFileInfo.createJobId(1);
        final TestUnnecessaryBlockingOnHistoryFileInfo.HistoryFileManagerUnderContention historyFileManager = createHistoryFileManager(config, job1, job2);
        Thread webRequest1 = null;
        Thread webRequest2 = null;
        try {
            /**
             * create a dummy .jhist file for job1, and try to load/parse the job
             * files in one child thread.
             */
            TestUnnecessaryBlockingOnHistoryFileInfo.createJhistFile(job1);
            webRequest1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HistoryFileManager.HistoryFileInfo historyFileInfo = historyFileManager.getFileInfo(job1);
                        historyFileInfo.loadJob();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            webRequest1.start();
            historyFileManager.waitUntilIntermediateDirIsScanned(job1);
            /**
             * At this point, thread webRequest1 has finished scanning the
             * intermediate directory and is hanging up parsing the job files while
             * it's holding the lock on the associated HistoryFileInfo object.
             */
            /**
             * create a dummy .jhist file for job2 and try to load/parse the job files
             * in the other child thread. Because job files are not moved from the
             * intermediate directory to the done directory, thread webRequest2
             * will also see the job history files for job1.
             */
            TestUnnecessaryBlockingOnHistoryFileInfo.createJhistFile(job2);
            webRequest2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HistoryFileManager.HistoryFileInfo historyFileInfo = historyFileManager.getFileInfo(job2);
                        historyFileInfo.loadJob();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            webRequest2.start();
            historyFileManager.waitUntilIntermediateDirIsScanned(job2);
            /**
             * If execution had gotten to this point, then thread webRequest2 would
             * not have tried to acquire the lock of the HistoryFileInfo object
             * associated job1, which is permanently held by thread webRequest1 that
             * is hanging up parsing the job history files, so it was able to proceed
             * with parsing job history files of job2.
             */
            Assert.assertTrue(("Thread 2 is blocked while it is trying to " + "load job2 by Thread 1 which is loading job1."), ((webRequest2.getState()) != (BLOCKED)));
        } finally {
            if (webRequest1 != null) {
                webRequest1.interrupt();
            }
            if (webRequest2 != null) {
                webRequest2.interrupt();
            }
        }
    }

    /**
     * A test implementation of HistoryFileManager that does not move files
     * from intermediate directory to done directory and hangs up parsing
     * job history files.
     */
    class HistoryFileManagerUnderContention extends HistoryFileManager {
        /**
         * A map of job to a signal that indicates whether the intermediate
         * directory is done being scanned before the job files are parsed.
         */
        private Map<JobId, CountDownLatch> scanningDoneSignals = new HashMap<>();

        /**
         * A HistoryFileManager that expects to load given jobs and hangs up
         * parsing the job files. It perform no moving of files from the
         * intermediate directory to done directory.
         *
         * @param jobId
         * 		the set of jobs expected to load and parse
         */
        public HistoryFileManagerUnderContention(JobId... jobId) {
            for (JobId job : jobId) {
                scanningDoneSignals.put(job, new CountDownLatch(1));
            }
        }

        /**
         * Wait until scanning of the intermediate directory finishes and load
         * of the given job is started.
         */
        public void waitUntilIntermediateDirIsScanned(JobId jobId) throws InterruptedException {
            if (scanningDoneSignals.containsKey(jobId)) {
                scanningDoneSignals.get(jobId).await();
            }
        }

        /**
         * Create a HistoryFileInfo instance that hangs on parsing job files.
         */
        @Override
        protected HistoryFileManager.HistoryFileInfo createHistoryFileInfo(Path historyFile, Path confFile, Path summaryFile, JobIndexInfo jobIndexInfo, boolean isInDone) {
            return new TestUnnecessaryBlockingOnHistoryFileInfo.HistoryFileManagerUnderContention.HistoryFileInfo(historyFile, confFile, summaryFile, jobIndexInfo, isInDone, scanningDoneSignals.get(jobIndexInfo.getJobId()));
        }

        /**
         * Create a dummy ThreadPoolExecutor that does not execute submitted tasks.
         */
        @Override
        protected ThreadPoolExecutor createMoveToDoneThreadPool(int numMoveThreads) {
            return Mockito.mock(ThreadPoolExecutor.class);
        }

        /**
         * A HistoryFileInfo implementation that takes forever to parse the
         * associated job files. This mimics the behavior of parsing huge job files.
         */
        class HistoryFileInfo extends HistoryFileManager.HistoryFileInfo {
            /**
             * A signal that indicates scanning of the intermediate directory is done
             * as HistoryFileManager is in the process of loading the HistoryFileInfo
             * instance.
             */
            private final CountDownLatch scanningDoneSignal;

            HistoryFileInfo(Path historyFile, Path confFile, Path summaryFile, JobIndexInfo jobIndexInfo, boolean isInDone, CountDownLatch scanningDoneSignal) {
                super(historyFile, confFile, summaryFile, jobIndexInfo, isInDone);
                this.scanningDoneSignal = scanningDoneSignal;
            }

            /**
             * An test implementation that takes forever to load a job in order to
             * mimic what happens when job files of large size are parsed in JHS.
             * Before loading, we signal that scanning of the intermediate directory
             * is finished.
             */
            @Override
            public synchronized Job loadJob() throws IOException {
                if ((scanningDoneSignal) != null) {
                    scanningDoneSignal.countDown();
                }
                while (!(Thread.currentThread().isInterrupted())) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                } 
                return null;
            }
        }
    }
}

