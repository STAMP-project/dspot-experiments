/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.server.ft.journal.raft;


import DeployMode.EMBEDDED_HA;
import PortCoordination.EMBEDDED_JOURNAL_FAILOVER;
import PortCoordination.EMBEDDED_JOURNAL_RESTART;
import PortCoordination.EMBEDDED_JOURNAL_RESTART_STRESS;
import PropertyKey.MASTER_EMBEDDED_JOURNAL_ELECTION_TIMEOUT;
import PropertyKey.MASTER_EMBEDDED_JOURNAL_HEARTBEAT_INTERVAL;
import PropertyKey.MASTER_JOURNAL_FLUSH_TIMEOUT_MS;
import alluxio.AlluxioURI;
import alluxio.ConfigurationRule;
import alluxio.Constants;
import alluxio.client.file.FileSystem;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.FileAlreadyExistsException;
import alluxio.exception.FileDoesNotExistException;
import alluxio.multi.process.MultiProcessCluster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.util.CommonUtils;
import alluxio.util.WaitForOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for the embedded journal.
 */
public final class EmbeddedJournalIntegrationTest extends BaseIntegrationTest {
    private static final int NUM_MASTERS = 3;

    private static final int RESTART_TIMEOUT_MS = 2 * (Constants.MINUTE_MS);

    @Rule
    public ConfigurationRule mConf = new ConfigurationRule(PropertyKey.USER_METRICS_COLLECTION_ENABLED, "false", ServerConfiguration.global());

    public MultiProcessCluster mCluster;

    @Test
    public void failover() throws Exception {
        mCluster = // To make the test run faster.
        MultiProcessCluster.newBuilder(EMBEDDED_JOURNAL_FAILOVER).setClusterName("EmbeddedJournalFailover").setNumMasters(EmbeddedJournalIntegrationTest.NUM_MASTERS).setNumWorkers(0).setDeployMode(EMBEDDED_HA).addProperty(MASTER_JOURNAL_FLUSH_TIMEOUT_MS, "5min").addProperty(MASTER_EMBEDDED_JOURNAL_ELECTION_TIMEOUT, "750ms").addProperty(MASTER_EMBEDDED_JOURNAL_HEARTBEAT_INTERVAL, "250ms").build();
        mCluster.start();
        AlluxioURI testDir = new AlluxioURI("/dir");
        FileSystem fs = mCluster.getFileSystemClient();
        fs.createDirectory(testDir);
        mCluster.waitForAndKillPrimaryMaster(EmbeddedJournalIntegrationTest.RESTART_TIMEOUT_MS);
        Assert.assertTrue(fs.exists(testDir));
        mCluster.notifySuccess();
    }

    @Test
    public void restart() throws Exception {
        mCluster = // To make the test run faster.
        MultiProcessCluster.newBuilder(EMBEDDED_JOURNAL_RESTART).setClusterName("EmbeddedJournalRestart").setNumMasters(EmbeddedJournalIntegrationTest.NUM_MASTERS).setNumWorkers(0).setDeployMode(EMBEDDED_HA).addProperty(MASTER_JOURNAL_FLUSH_TIMEOUT_MS, "5min").addProperty(MASTER_EMBEDDED_JOURNAL_ELECTION_TIMEOUT, "750ms").addProperty(MASTER_EMBEDDED_JOURNAL_HEARTBEAT_INTERVAL, "250ms").build();
        mCluster.start();
        AlluxioURI testDir = new AlluxioURI("/dir");
        FileSystem fs = mCluster.getFileSystemClient();
        fs.createDirectory(testDir);
        restartMasters();
        Assert.assertTrue(fs.exists(testDir));
        restartMasters();
        Assert.assertTrue(fs.exists(testDir));
        restartMasters();
        Assert.assertTrue(fs.exists(testDir));
        mCluster.saveWorkdir();
        mCluster.notifySuccess();
    }

    @Test
    public void restartStress() throws Throwable {
        mCluster = // To make the test run faster.
        MultiProcessCluster.newBuilder(EMBEDDED_JOURNAL_RESTART_STRESS).setClusterName("EmbeddedJournalRestartStress").setNumMasters(EmbeddedJournalIntegrationTest.NUM_MASTERS).setNumWorkers(0).setDeployMode(EMBEDDED_HA).addProperty(MASTER_JOURNAL_FLUSH_TIMEOUT_MS, "5min").addProperty(MASTER_EMBEDDED_JOURNAL_ELECTION_TIMEOUT, "750ms").addProperty(MASTER_EMBEDDED_JOURNAL_HEARTBEAT_INTERVAL, "250ms").build();
        mCluster.start();
        // Run and verify operations while restarting the cluster multiple times.
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicInteger successes = new AtomicInteger(0);
        FileSystem fs = mCluster.getFileSystemClient();
        List<EmbeddedJournalIntegrationTest.OperationThread> threads = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                EmbeddedJournalIntegrationTest.OperationThread t = new EmbeddedJournalIntegrationTest.OperationThread(fs, i, failure, successes);
                t.start();
                threads.add(t);
            }
            for (int i = 0; i < 2; i++) {
                restartMasters();
                System.out.printf("---------- Iteration %s ----------\n", i);
                successes.set(0);
                CommonUtils.waitFor("11 successes", () -> (successes.get()) >= 11, WaitForOptions.defaults().setTimeoutMs(EmbeddedJournalIntegrationTest.RESTART_TIMEOUT_MS));
                if ((failure.get()) != null) {
                    throw failure.get();
                }
            }
        } finally {
            threads.forEach(( t) -> t.interrupt());
            for (Thread t : threads) {
                t.join();
            }
        }
        mCluster.notifySuccess();
    }

    private static class OperationThread extends Thread {
        private final FileSystem mFs;

        private final int mThreadNum;

        private final AtomicReference<Throwable> mFailure;

        private final AtomicInteger mSuccessCounter;

        public OperationThread(FileSystem fs, int threadNum, AtomicReference<Throwable> failure, AtomicInteger successCounter) {
            super(("operation-test-thread-" + threadNum));
            mFs = fs;
            mThreadNum = threadNum;
            mFailure = failure;
            mSuccessCounter = successCounter;
        }

        public void run() {
            try {
                runInternal();
            } catch (Exception e) {
                e.printStackTrace();
                mFailure.set(e);
            }
        }

        public void runInternal() throws Exception {
            while (!(Thread.interrupted())) {
                for (int i = 0; i < 100; i++) {
                    AlluxioURI dir = formatDirName(i);
                    try {
                        mFs.createDirectory(dir);
                    } catch (FileAlreadyExistsException e) {
                        // This could happen if the operation was retried but actually succeeded server-side on
                        // the first attempt. Alluxio does not de-duplicate retried operations.
                        continue;
                    }
                    if (!(mFs.exists(dir))) {
                        mFailure.set(new RuntimeException(String.format("Directory %s does not exist", dir)));
                        return;
                    }
                }
                for (int i = 0; i < 100; i++) {
                    AlluxioURI dir = formatDirName(i);
                    try {
                        mFs.delete(dir);
                    } catch (FileDoesNotExistException e) {
                        // This could happen if the operation was retried but actually succeeded server-side on
                        // the first attempt. Alluxio does not de-duplicate retried operations.
                        continue;
                    }
                    if (mFs.exists(dir)) {
                        mFailure.set(new RuntimeException(String.format("Directory %s still exists", dir)));
                        return;
                    }
                }
                System.out.println(("Success counter: " + (mSuccessCounter.incrementAndGet())));
            } 
        }

        private AlluxioURI formatDirName(int dirNum) {
            return new AlluxioURI(String.format("/dir-%d-%d", mThreadNum, dirNum));
        }
    }
}

