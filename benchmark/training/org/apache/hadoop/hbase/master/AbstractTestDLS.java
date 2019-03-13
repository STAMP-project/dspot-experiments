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
package org.apache.hadoop.hbase.master;


import NamespaceDescriptor.SYSTEM_NAMESPACE_NAME_STR;
import ServerManager.WAIT_ON_REGIONSERVERS_MINTOSTART;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.SplitLogCounters;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coordination.ZKSplitLogManagerCoordination;
import org.apache.hadoop.hbase.master.SplitLogManager.TaskBatch;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.apache.hadoop.hbase.wal.WALSplitter;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base class for testing distributed log splitting.
 */
public abstract class AbstractTestDLS {
    private static final Logger LOG = LoggerFactory.getLogger(TestSplitLogManager.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    // Start a cluster with 2 masters and 5 regionservers
    private static final int NUM_MASTERS = 2;

    private static final int NUM_RS = 5;

    private static byte[] COLUMN_FAMILY = Bytes.toBytes("family");

    @Rule
    public TestName testName = new TestName();

    private TableName tableName;

    private MiniHBaseCluster cluster;

    private HMaster master;

    private Configuration conf;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRecoveredEdits() throws Exception {
        conf.setLong("hbase.regionserver.hlog.blocksize", (30 * 1024));// create more than one wal

        startCluster(AbstractTestDLS.NUM_RS);
        int numLogLines = 10000;
        SplitLogManager slm = master.getMasterWalManager().getSplitLogManager();
        // turn off load balancing to prevent regions from moving around otherwise
        // they will consume recovered.edits
        master.balanceSwitch(false);
        FileSystem fs = master.getMasterFileSystem().getFileSystem();
        List<RegionServerThread> rsts = cluster.getLiveRegionServerThreads();
        Path rootdir = FSUtils.getRootDir(conf);
        int numRegions = 50;
        try (ZKWatcher zkw = new ZKWatcher(conf, "table-creation", null);Table t = installTable(zkw, numRegions)) {
            TableName table = t.getName();
            List<RegionInfo> regions = null;
            HRegionServer hrs = null;
            for (int i = 0; i < (AbstractTestDLS.NUM_RS); i++) {
                hrs = rsts.get(i).getRegionServer();
                regions = ProtobufUtil.getOnlineRegions(hrs.getRSRpcServices());
                // At least one RS will have >= to average number of regions.
                if ((regions.size()) >= (numRegions / (AbstractTestDLS.NUM_RS))) {
                    break;
                }
            }
            Path logDir = new Path(rootdir, AbstractFSWALProvider.getWALDirectoryName(hrs.getServerName().toString()));
            AbstractTestDLS.LOG.info(("#regions = " + (regions.size())));
            Iterator<RegionInfo> it = regions.iterator();
            while (it.hasNext()) {
                RegionInfo region = it.next();
                if (region.getTable().getNamespaceAsString().equals(SYSTEM_NAMESPACE_NAME_STR)) {
                    it.remove();
                }
            } 
            makeWAL(hrs, regions, numLogLines, 100);
            slm.splitLogDistributed(logDir);
            int count = 0;
            for (RegionInfo hri : regions) {
                Path tdir = FSUtils.getWALTableDir(conf, table);
                @SuppressWarnings("deprecation")
                Path editsdir = WALSplitter.getRegionDirRecoveredEditsDir(FSUtils.getWALRegionDir(conf, tableName, hri.getEncodedName()));
                AbstractTestDLS.LOG.debug(("checking edits dir " + editsdir));
                FileStatus[] files = fs.listStatus(editsdir, new PathFilter() {
                    @Override
                    public boolean accept(Path p) {
                        if (WALSplitter.isSequenceIdFile(p)) {
                            return false;
                        }
                        return true;
                    }
                });
                Assert.assertTrue(("edits dir should have more than a single file in it. instead has " + (files.length)), ((files.length) > 1));
                for (int i = 0; i < (files.length); i++) {
                    int c = countWAL(files[i].getPath(), fs, conf);
                    count += c;
                }
                AbstractTestDLS.LOG.info((((count + " edits in ") + (files.length)) + " recovered edits files."));
            }
            // check that the log file is moved
            Assert.assertFalse(fs.exists(logDir));
            Assert.assertEquals(numLogLines, count);
        }
    }

    @Test
    public void testMasterStartsUpWithLogSplittingWork() throws Exception {
        conf.setInt(WAIT_ON_REGIONSERVERS_MINTOSTART, ((AbstractTestDLS.NUM_RS) - 1));
        startCluster(AbstractTestDLS.NUM_RS);
        int numRegionsToCreate = 40;
        int numLogLines = 1000;
        // turn off load balancing to prevent regions from moving around otherwise
        // they will consume recovered.edits
        master.balanceSwitch(false);
        try (ZKWatcher zkw = new ZKWatcher(conf, "table-creation", null);Table ht = installTable(zkw, numRegionsToCreate)) {
            HRegionServer hrs = findRSToKill(false);
            List<RegionInfo> regions = ProtobufUtil.getOnlineRegions(hrs.getRSRpcServices());
            makeWAL(hrs, regions, numLogLines, 100);
            // abort master
            abortMaster(cluster);
            // abort RS
            AbstractTestDLS.LOG.info(("Aborting region server: " + (hrs.getServerName())));
            hrs.abort("testing");
            // wait for abort completes
            AbstractTestDLS.TEST_UTIL.waitFor(120000, 200, new Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (cluster.getLiveRegionServerThreads().size()) <= ((AbstractTestDLS.NUM_RS) - 1);
                }
            });
            Thread.sleep(2000);
            AbstractTestDLS.LOG.info(("Current Open Regions:" + (HBaseTestingUtility.getAllOnlineRegions(cluster).size())));
            // wait for abort completes
            AbstractTestDLS.TEST_UTIL.waitFor(120000, 200, new Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (HBaseTestingUtility.getAllOnlineRegions(cluster).size()) >= (numRegionsToCreate + 1);
                }
            });
            AbstractTestDLS.LOG.info(("Current Open Regions After Master Node Starts Up:" + (HBaseTestingUtility.getAllOnlineRegions(cluster).size())));
            Assert.assertEquals(numLogLines, AbstractTestDLS.TEST_UTIL.countRows(ht));
        }
    }

    /**
     * The original intention of this test was to force an abort of a region server and to make sure
     * that the failure path in the region servers is properly evaluated. But it is difficult to
     * ensure that the region server doesn't finish the log splitting before it aborts. Also now,
     * there is this code path where the master will preempt the region server when master detects
     * that the region server has aborted.
     *
     * @throws Exception
     * 		
     */
    // Was marked flaky before Distributed Log Replay cleanup.
    @Test
    public void testWorkerAbort() throws Exception {
        AbstractTestDLS.LOG.info("testWorkerAbort");
        startCluster(3);
        int numLogLines = 10000;
        SplitLogManager slm = master.getMasterWalManager().getSplitLogManager();
        FileSystem fs = master.getMasterFileSystem().getFileSystem();
        List<RegionServerThread> rsts = cluster.getLiveRegionServerThreads();
        HRegionServer hrs = findRSToKill(false);
        Path rootdir = FSUtils.getRootDir(conf);
        final Path logDir = new Path(rootdir, AbstractFSWALProvider.getWALDirectoryName(hrs.getServerName().toString()));
        try (ZKWatcher zkw = new ZKWatcher(conf, "table-creation", null);Table t = installTable(zkw, 40)) {
            makeWAL(hrs, ProtobufUtil.getOnlineRegions(hrs.getRSRpcServices()), numLogLines, 100);
            new Thread() {
                @Override
                public void run() {
                    try {
                        waitForCounter(SplitLogCounters.tot_wkr_task_acquired, 0, 1, 1000);
                    } catch (InterruptedException e) {
                    }
                    for (RegionServerThread rst : rsts) {
                        rst.getRegionServer().abort("testing");
                        break;
                    }
                }
            }.start();
            FileStatus[] logfiles = fs.listStatus(logDir);
            TaskBatch batch = new TaskBatch();
            slm.enqueueSplitTask(logfiles[0].getPath().toString(), batch);
            // waitForCounter but for one of the 2 counters
            long curt = System.currentTimeMillis();
            long waitTime = 80000;
            long endt = curt + waitTime;
            while (curt < endt) {
                if ((((((SplitLogCounters.tot_wkr_task_resigned.sum()) + (SplitLogCounters.tot_wkr_task_err.sum())) + (SplitLogCounters.tot_wkr_final_transition_failed.sum())) + (SplitLogCounters.tot_wkr_task_done.sum())) + (SplitLogCounters.tot_wkr_preempt_task.sum())) == 0) {
                    Thread.sleep(100);
                    curt = System.currentTimeMillis();
                } else {
                    Assert.assertTrue((1 <= (((((SplitLogCounters.tot_wkr_task_resigned.sum()) + (SplitLogCounters.tot_wkr_task_err.sum())) + (SplitLogCounters.tot_wkr_final_transition_failed.sum())) + (SplitLogCounters.tot_wkr_task_done.sum())) + (SplitLogCounters.tot_wkr_preempt_task.sum()))));
                    return;
                }
            } 
            Assert.fail(((((("none of the following counters went up in " + waitTime) + " milliseconds - ") + "tot_wkr_task_resigned, tot_wkr_task_err, ") + "tot_wkr_final_transition_failed, tot_wkr_task_done, ") + "tot_wkr_preempt_task"));
        }
    }

    @Test
    public void testThreeRSAbort() throws Exception {
        AbstractTestDLS.LOG.info("testThreeRSAbort");
        int numRegionsToCreate = 40;
        int numRowsPerRegion = 100;
        startCluster(AbstractTestDLS.NUM_RS);// NUM_RS=6.

        try (ZKWatcher zkw = new ZKWatcher(conf, "distributed log splitting test", null);Table table = installTable(zkw, numRegionsToCreate)) {
            populateDataInTable(numRowsPerRegion);
            List<RegionServerThread> rsts = cluster.getLiveRegionServerThreads();
            Assert.assertEquals(AbstractTestDLS.NUM_RS, rsts.size());
            cluster.killRegionServer(rsts.get(0).getRegionServer().getServerName());
            cluster.killRegionServer(rsts.get(1).getRegionServer().getServerName());
            cluster.killRegionServer(rsts.get(2).getRegionServer().getServerName());
            AbstractTestDLS.TEST_UTIL.waitFor(60000, new Waiter.ExplainingPredicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (cluster.getLiveRegionServerThreads().size()) <= ((AbstractTestDLS.NUM_RS) - 3);
                }

                @Override
                public String explainFailure() throws Exception {
                    return "Timed out waiting for server aborts.";
                }
            });
            AbstractTestDLS.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
            int rows;
            try {
                rows = AbstractTestDLS.TEST_UTIL.countRows(table);
            } catch (Exception e) {
                Threads.printThreadInfo(System.out, "Thread dump before fail");
                throw e;
            }
            Assert.assertEquals((numRegionsToCreate * numRowsPerRegion), rows);
        }
    }

    @Test
    public void testDelayedDeleteOnFailure() throws Exception {
        AbstractTestDLS.LOG.info("testDelayedDeleteOnFailure");
        startCluster(1);
        final SplitLogManager slm = master.getMasterWalManager().getSplitLogManager();
        final FileSystem fs = master.getMasterFileSystem().getFileSystem();
        final Path rootLogDir = new Path(FSUtils.getWALRootDir(conf), HConstants.HREGION_LOGDIR_NAME);
        final Path logDir = new Path(rootLogDir, ServerName.valueOf("x", 1, 1).toString());
        fs.mkdirs(logDir);
        ExecutorService executor = null;
        try {
            final Path corruptedLogFile = new Path(logDir, "x");
            FSDataOutputStream out;
            out = fs.create(corruptedLogFile);
            out.write(0);
            out.write(Bytes.toBytes("corrupted bytes"));
            out.close();
            ZKSplitLogManagerCoordination coordination = ((ZKSplitLogManagerCoordination) (master.getCoordinatedStateManager().getSplitLogManagerCoordination()));
            coordination.setIgnoreDeleteForTesting(true);
            executor = Executors.newSingleThreadExecutor();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // since the logDir is a fake, corrupted one, so the split log worker
                        // will finish it quickly with error, and this call will fail and throw
                        // an IOException.
                        slm.splitLogDistributed(logDir);
                    } catch (IOException ioe) {
                        try {
                            Assert.assertTrue(fs.exists(corruptedLogFile));
                            // this call will block waiting for the task to be removed from the
                            // tasks map which is not going to happen since ignoreZKDeleteForTesting
                            // is set to true, until it is interrupted.
                            slm.splitLogDistributed(logDir);
                        } catch (IOException e) {
                            Assert.assertTrue(Thread.currentThread().isInterrupted());
                            return;
                        }
                        Assert.fail("did not get the expected IOException from the 2nd call");
                    }
                    Assert.fail("did not get the expected IOException from the 1st call");
                }
            };
            Future<?> result = executor.submit(runnable);
            try {
                result.get(2000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException te) {
                // it is ok, expected.
            }
            waitForCounter(SplitLogCounters.tot_mgr_wait_for_zk_delete, 0, 1, 10000);
            executor.shutdownNow();
            executor = null;
            // make sure the runnable is finished with no exception thrown.
            result.get();
        } finally {
            if (executor != null) {
                // interrupt the thread in case the test fails in the middle.
                // it has no effect if the thread is already terminated.
                executor.shutdownNow();
            }
            fs.delete(logDir, true);
        }
    }
}

