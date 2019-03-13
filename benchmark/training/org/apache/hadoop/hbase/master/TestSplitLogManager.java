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


import CreateMode.PERSISTENT;
import HConstants.HBASE_DIR;
import Ids.OPEN_ACL_UNSAFE;
import ZKSplitLogManagerCoordination.DEFAULT_MAX_RESUBMIT;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.SplitLogCounters;
import org.apache.hadoop.hbase.SplitLogTask;
import org.apache.hadoop.hbase.coordination.ZkCoordinatedStateManager;
import org.apache.hadoop.hbase.master.SplitLogManager.Task;
import org.apache.hadoop.hbase.master.SplitLogManager.TaskBatch;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.zookeeper.ZKSplitLog;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestSplitLogManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitLogManager.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSplitLogManager.class);

    private final ServerManager sm = Mockito.mock(ServerManager.class);

    private ZKWatcher zkw;

    private TestSplitLogManager.DummyMasterServices master;

    private SplitLogManager slm;

    private Configuration conf;

    private int to;

    private static HBaseTestingUtility TEST_UTIL;

    class DummyMasterServices extends MockNoopMasterServices {
        private ZKWatcher zkw;

        private CoordinatedStateManager cm;

        public DummyMasterServices(ZKWatcher zkw, Configuration conf) {
            super(conf);
            this.zkw = zkw;
            cm = new ZkCoordinatedStateManager(this);
        }

        @Override
        public ZKWatcher getZooKeeper() {
            return zkw;
        }

        @Override
        public CoordinatedStateManager getCoordinatedStateManager() {
            return cm;
        }

        @Override
        public ServerManager getServerManager() {
            return sm;
        }
    }

    private interface Expr {
        long eval();
    }

    /**
     * Test whether the splitlog correctly creates a task in zookeeper
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTaskCreation() throws Exception {
        TestSplitLogManager.LOG.info("TestTaskCreation - test the creation of a task in zk");
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        byte[] data = ZKUtil.getData(zkw, tasknode);
        SplitLogTask slt = SplitLogTask.parseFrom(data);
        TestSplitLogManager.LOG.info(("Task node created " + (slt.toString())));
        Assert.assertTrue(slt.isUnassigned(master.getServerName()));
    }

    @Test
    public void testOrphanTaskAcquisition() throws Exception {
        TestSplitLogManager.LOG.info("TestOrphanTaskAcquisition");
        String tasknode = ZKSplitLog.getEncodedNodeName(zkw, "orphan/test/slash");
        SplitLogTask slt = new SplitLogTask.Owned(master.getServerName());
        zkw.getRecoverableZooKeeper().create(tasknode, slt.toByteArray(), OPEN_ACL_UNSAFE, PERSISTENT);
        slm = new SplitLogManager(master, conf);
        waitForCounter(SplitLogCounters.tot_mgr_orphan_task_acquired, 0, 1, ((to) / 2));
        Task task = findOrCreateOrphanTask(tasknode);
        Assert.assertTrue(task.isOrphan());
        waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 0, 1, ((to) / 2));
        Assert.assertFalse(task.isUnassigned());
        long curt = System.currentTimeMillis();
        Assert.assertTrue((((task.last_update) <= curt) && ((task.last_update) > (curt - 1000))));
        TestSplitLogManager.LOG.info("waiting for manager to resubmit the orphan task");
        waitForCounter(SplitLogCounters.tot_mgr_resubmit, 0, 1, ((to) + ((to) / 2)));
        Assert.assertTrue(task.isUnassigned());
        waitForCounter(SplitLogCounters.tot_mgr_rescan, 0, 1, ((to) + ((to) / 2)));
    }

    @Test
    public void testUnassignedOrphan() throws Exception {
        TestSplitLogManager.LOG.info(("TestUnassignedOrphan - an unassigned task is resubmitted at" + " startup"));
        String tasknode = ZKSplitLog.getEncodedNodeName(zkw, "orphan/test/slash");
        // create an unassigned orphan task
        SplitLogTask slt = new SplitLogTask.Unassigned(master.getServerName());
        zkw.getRecoverableZooKeeper().create(tasknode, slt.toByteArray(), OPEN_ACL_UNSAFE, PERSISTENT);
        int version = ZKUtil.checkExists(zkw, tasknode);
        slm = new SplitLogManager(master, conf);
        waitForCounter(SplitLogCounters.tot_mgr_orphan_task_acquired, 0, 1, ((to) / 2));
        Task task = findOrCreateOrphanTask(tasknode);
        Assert.assertTrue(task.isOrphan());
        Assert.assertTrue(task.isUnassigned());
        // wait for RESCAN node to be created
        waitForCounter(SplitLogCounters.tot_mgr_rescan, 0, 1, ((to) / 2));
        Task task2 = findOrCreateOrphanTask(tasknode);
        Assert.assertTrue((task == task2));
        TestSplitLogManager.LOG.debug(("task = " + task));
        Assert.assertEquals(1L, SplitLogCounters.tot_mgr_resubmit.sum());
        Assert.assertEquals(1, task.incarnation.get());
        Assert.assertEquals(0, task.unforcedResubmits.get());
        Assert.assertTrue(task.isOrphan());
        Assert.assertTrue(task.isUnassigned());
        Assert.assertTrue(((ZKUtil.checkExists(zkw, tasknode)) > version));
    }

    @Test
    public void testMultipleResubmits() throws Exception {
        TestSplitLogManager.LOG.info("TestMultipleResbmits - no indefinite resubmissions");
        conf.setInt("hbase.splitlog.max.resubmit", 2);
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        int version = ZKUtil.checkExists(zkw, tasknode);
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        final ServerName worker2 = ServerName.valueOf("worker2,1,1");
        final ServerName worker3 = ServerName.valueOf("worker3,1,1");
        SplitLogTask slt = new SplitLogTask.Owned(worker1);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 0, 1, ((to) / 2));
        waitForCounter(SplitLogCounters.tot_mgr_resubmit, 0, 1, ((to) + ((to) / 2)));
        int version1 = ZKUtil.checkExists(zkw, tasknode);
        Assert.assertTrue((version1 > version));
        slt = new SplitLogTask.Owned(worker2);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 1, 2, ((to) / 2));
        waitForCounter(SplitLogCounters.tot_mgr_resubmit, 1, 2, ((to) + ((to) / 2)));
        int version2 = ZKUtil.checkExists(zkw, tasknode);
        Assert.assertTrue((version2 > version1));
        slt = new SplitLogTask.Owned(worker3);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 2, 3, ((to) / 2));
        waitForCounter(SplitLogCounters.tot_mgr_resubmit_threshold_reached, 0, 1, ((to) + ((to) / 2)));
        Thread.sleep(((to) + ((to) / 2)));
        Assert.assertEquals(2L, ((SplitLogCounters.tot_mgr_resubmit.sum()) - (SplitLogCounters.tot_mgr_resubmit_force.sum())));
    }

    @Test
    public void testRescanCleanup() throws Exception {
        TestSplitLogManager.LOG.info("TestRescanCleanup - ensure RESCAN nodes are cleaned up");
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        int version = ZKUtil.checkExists(zkw, tasknode);
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        SplitLogTask slt = new SplitLogTask.Owned(worker1);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 0, 1, ((to) / 2));
        waitForCounter(new TestSplitLogManager.Expr() {
            @Override
            public long eval() {
                return (SplitLogCounters.tot_mgr_resubmit.sum()) + (SplitLogCounters.tot_mgr_resubmit_failed.sum());
            }
        }, 0, 1, (5 * 60000));// wait long enough

        Assert.assertEquals("Could not run test. Lost ZK connection?", 0, SplitLogCounters.tot_mgr_resubmit_failed.sum());
        int version1 = ZKUtil.checkExists(zkw, tasknode);
        Assert.assertTrue((version1 > version));
        byte[] taskstate = ZKUtil.getData(zkw, tasknode);
        slt = SplitLogTask.parseFrom(taskstate);
        Assert.assertTrue(slt.isUnassigned(master.getServerName()));
        waitForCounter(SplitLogCounters.tot_mgr_rescan_deleted, 0, 1, ((to) / 2));
    }

    @Test
    public void testTaskDone() throws Exception {
        TestSplitLogManager.LOG.info("TestTaskDone - cleanup task node once in DONE state");
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        SplitLogTask slt = new SplitLogTask.Done(worker1);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        synchronized(batch) {
            while ((batch.installed) != (batch.done)) {
                batch.wait();
            } 
        }
        waitForCounter(SplitLogCounters.tot_mgr_task_deleted, 0, 1, ((to) / 2));
        Assert.assertTrue(((ZKUtil.checkExists(zkw, tasknode)) == (-1)));
    }

    @Test
    public void testTaskErr() throws Exception {
        TestSplitLogManager.LOG.info("TestTaskErr - cleanup task node once in ERR state");
        conf.setInt("hbase.splitlog.max.resubmit", 0);
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        SplitLogTask slt = new SplitLogTask.Err(worker1);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        synchronized(batch) {
            while ((batch.installed) != (batch.error)) {
                batch.wait();
            } 
        }
        waitForCounter(SplitLogCounters.tot_mgr_task_deleted, 0, 1, ((to) / 2));
        Assert.assertTrue(((ZKUtil.checkExists(zkw, tasknode)) == (-1)));
        conf.setInt("hbase.splitlog.max.resubmit", DEFAULT_MAX_RESUBMIT);
    }

    @Test
    public void testTaskResigned() throws Exception {
        TestSplitLogManager.LOG.info("TestTaskResigned - resubmit task node once in RESIGNED state");
        Assert.assertEquals(0, SplitLogCounters.tot_mgr_resubmit.sum());
        slm = new SplitLogManager(master, conf);
        Assert.assertEquals(0, SplitLogCounters.tot_mgr_resubmit.sum());
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        Assert.assertEquals(0, SplitLogCounters.tot_mgr_resubmit.sum());
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        Assert.assertEquals(0, SplitLogCounters.tot_mgr_resubmit.sum());
        SplitLogTask slt = new SplitLogTask.Resigned(worker1);
        Assert.assertEquals(0, SplitLogCounters.tot_mgr_resubmit.sum());
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        ZKUtil.checkExists(zkw, tasknode);
        // Could be small race here.
        if ((SplitLogCounters.tot_mgr_resubmit.sum()) == 0) {
            waitForCounter(SplitLogCounters.tot_mgr_resubmit, 0, 1, ((to) / 2));
        }
        Assert.assertEquals(1, SplitLogCounters.tot_mgr_resubmit.sum());
        byte[] taskstate = ZKUtil.getData(zkw, tasknode);
        slt = SplitLogTask.parseFrom(taskstate);
        Assert.assertTrue(slt.isUnassigned(master.getServerName()));
    }

    @Test
    public void testUnassignedTimeout() throws Exception {
        TestSplitLogManager.LOG.info(("TestUnassignedTimeout - iff all tasks are unassigned then" + " resubmit"));
        // create an orphan task in OWNED state
        String tasknode1 = ZKSplitLog.getEncodedNodeName(zkw, "orphan/1");
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        SplitLogTask slt = new SplitLogTask.Owned(worker1);
        zkw.getRecoverableZooKeeper().create(tasknode1, slt.toByteArray(), OPEN_ACL_UNSAFE, PERSISTENT);
        slm = new SplitLogManager(master, conf);
        waitForCounter(SplitLogCounters.tot_mgr_orphan_task_acquired, 0, 1, ((to) / 2));
        // submit another task which will stay in unassigned mode
        TaskBatch batch = new TaskBatch();
        submitTaskAndWait(batch, "foo/1");
        // keep updating the orphan owned node every to/2 seconds
        for (int i = 0; i < ((3 * (to)) / 100); i++) {
            Thread.sleep(100);
            final ServerName worker2 = ServerName.valueOf("worker1,1,1");
            slt = new SplitLogTask.Owned(worker2);
            ZKUtil.setData(zkw, tasknode1, slt.toByteArray());
        }
        // since we have stopped heartbeating the owned node therefore it should
        // get resubmitted
        TestSplitLogManager.LOG.info("waiting for manager to resubmit the orphan task");
        waitForCounter(SplitLogCounters.tot_mgr_resubmit, 0, 1, ((to) + ((to) / 2)));
        // now all the nodes are unassigned. manager should post another rescan
        waitForCounter(SplitLogCounters.tot_mgr_resubmit_unassigned, 0, 1, ((2 * (to)) + ((to) / 2)));
    }

    @Test
    public void testDeadWorker() throws Exception {
        TestSplitLogManager.LOG.info("testDeadWorker");
        conf.setLong("hbase.splitlog.max.resubmit", 0);
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        int version = ZKUtil.checkExists(zkw, tasknode);
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        SplitLogTask slt = new SplitLogTask.Owned(worker1);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        if ((SplitLogCounters.tot_mgr_heartbeat.sum()) == 0)
            waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 0, 1, ((to) / 2));

        slm.handleDeadWorker(worker1);
        if ((SplitLogCounters.tot_mgr_resubmit.sum()) == 0)
            waitForCounter(SplitLogCounters.tot_mgr_resubmit, 0, 1, ((to) + ((to) / 2)));

        if ((SplitLogCounters.tot_mgr_resubmit_dead_server_task.sum()) == 0) {
            waitForCounter(SplitLogCounters.tot_mgr_resubmit_dead_server_task, 0, 1, ((to) + ((to) / 2)));
        }
        int version1 = ZKUtil.checkExists(zkw, tasknode);
        Assert.assertTrue((version1 > version));
        byte[] taskstate = ZKUtil.getData(zkw, tasknode);
        slt = SplitLogTask.parseFrom(taskstate);
        Assert.assertTrue(slt.isUnassigned(master.getServerName()));
        return;
    }

    @Test
    public void testWorkerCrash() throws Exception {
        slm = new SplitLogManager(master, conf);
        TaskBatch batch = new TaskBatch();
        String tasknode = submitTaskAndWait(batch, "foo/1");
        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
        SplitLogTask slt = new SplitLogTask.Owned(worker1);
        ZKUtil.setData(zkw, tasknode, slt.toByteArray());
        if ((SplitLogCounters.tot_mgr_heartbeat.sum()) == 0)
            waitForCounter(SplitLogCounters.tot_mgr_heartbeat, 0, 1, ((to) / 2));

        // Not yet resubmitted.
        Assert.assertEquals(0, SplitLogCounters.tot_mgr_resubmit.sum());
        // This server becomes dead
        Mockito.when(sm.isServerOnline(worker1)).thenReturn(false);
        Thread.sleep(1300);// The timeout checker is done every 1000 ms (hardcoded).

        // It has been resubmitted
        Assert.assertEquals(1, SplitLogCounters.tot_mgr_resubmit.sum());
    }

    @Test
    public void testEmptyLogDir() throws Exception {
        TestSplitLogManager.LOG.info("testEmptyLogDir");
        slm = new SplitLogManager(master, conf);
        FileSystem fs = TestSplitLogManager.TEST_UTIL.getTestFileSystem();
        Path emptyLogDirPath = new Path(new Path(fs.getWorkingDirectory(), HConstants.HREGION_LOGDIR_NAME), ServerName.valueOf("emptyLogDir", 1, 1).toString());
        fs.mkdirs(emptyLogDirPath);
        slm.splitLogDistributed(emptyLogDirPath);
        Assert.assertFalse(fs.exists(emptyLogDirPath));
    }

    @Test
    public void testLogFilesAreArchived() throws Exception {
        TestSplitLogManager.LOG.info("testLogFilesAreArchived");
        slm = new SplitLogManager(master, conf);
        FileSystem fs = TestSplitLogManager.TEST_UTIL.getTestFileSystem();
        Path dir = TestSplitLogManager.TEST_UTIL.getDataTestDirOnTestFS("testLogFilesAreArchived");
        conf.set(HBASE_DIR, dir.toString());
        String serverName = ServerName.valueOf("foo", 1, 1).toString();
        Path logDirPath = new Path(new Path(dir, HConstants.HREGION_LOGDIR_NAME), serverName);
        fs.mkdirs(logDirPath);
        // create an empty log file
        String logFile = new Path(logDirPath, getRandomUUID().toString()).toString();
        fs.create(new Path(logDirPath, logFile)).close();
        // spin up a thread mocking split done.
        new Thread() {
            @Override
            public void run() {
                boolean done = false;
                while (!done) {
                    for (Map.Entry<String, Task> entry : slm.getTasks().entrySet()) {
                        final ServerName worker1 = ServerName.valueOf("worker1,1,1");
                        SplitLogTask slt = new SplitLogTask.Done(worker1);
                        boolean encounteredZKException = false;
                        try {
                            ZKUtil.setData(zkw, entry.getKey(), slt.toByteArray());
                        } catch (KeeperException e) {
                            TestSplitLogManager.LOG.warn(e.toString(), e);
                            encounteredZKException = true;
                        }
                        if (!encounteredZKException) {
                            done = true;
                        }
                    }
                } 
            }
        }.start();
        slm.splitLogDistributed(logDirPath);
        Assert.assertFalse(fs.exists(logDirPath));
    }
}

