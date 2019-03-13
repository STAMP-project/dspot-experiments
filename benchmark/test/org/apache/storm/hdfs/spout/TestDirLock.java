/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.hdfs.spout;


import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.storm.hdfs.testing.MiniDFSClusterRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static DirLock.DIR_LOCK_FILE;


public class TestDirLock {
    private static final int LOCK_EXPIRY_SEC = 1;

    private final Path locksDir = new Path("/tmp/lockdir");

    @Rule
    public MiniDFSClusterRule DFS_CLUSTER_RULE = new MiniDFSClusterRule();

    private FileSystem fs;

    private HdfsConfiguration conf = new HdfsConfiguration();

    @Test
    public void testBasicLocking() throws Exception {
        // 1 grab lock
        DirLock lock = DirLock.tryLock(fs, locksDir);
        Assert.assertTrue(fs.exists(lock.getLockFile()));
        // 2 try to grab another lock while dir is locked
        DirLock lock2 = DirLock.tryLock(fs, locksDir);// should fail

        Assert.assertNull(lock2);
        // 3 let go first lock
        lock.release();
        Assert.assertFalse(fs.exists(lock.getLockFile()));
        // 4 try locking again
        lock2 = DirLock.tryLock(fs, locksDir);
        Assert.assertTrue(fs.exists(lock2.getLockFile()));
        lock2.release();
        Assert.assertFalse(fs.exists(lock.getLockFile()));
        lock2.release();// should be throw

    }

    @Test
    public void testConcurrentLocking() throws Exception {
        TestDirLock.DirLockingThread[] threads = null;
        try {
            threads = startThreads(100, locksDir);
            for (TestDirLock.DirLockingThread thd : threads) {
                thd.join(30000);
                Assert.assertTrue(((thd.getName()) + " did not exit cleanly"), thd.cleanExit);
            }
            Path lockFile = new Path((((locksDir) + (Path.SEPARATOR)) + (DIR_LOCK_FILE)));
            Assert.assertFalse(fs.exists(lockFile));
        } finally {
            if (threads != null) {
                for (TestDirLock.DirLockingThread thread : threads) {
                    thread.interrupt();
                    thread.join(30000);
                    if (thread.isAlive()) {
                        throw new RuntimeException("Failed to stop threads within 30 seconds, threads may leak into other tests");
                    }
                }
            }
        }
    }

    @Test
    public void testLockRecovery() throws Exception {
        DirLock lock1 = DirLock.tryLock(fs, locksDir);// should pass

        Assert.assertNotNull(lock1);
        DirLock lock2 = DirLock.takeOwnershipIfStale(fs, locksDir, TestDirLock.LOCK_EXPIRY_SEC);// should fail

        Assert.assertNull(lock2);
        Thread.sleep((((TestDirLock.LOCK_EXPIRY_SEC) * 1000) + 500));// wait for lock to expire

        Assert.assertTrue(fs.exists(lock1.getLockFile()));
        DirLock lock3 = DirLock.takeOwnershipIfStale(fs, locksDir, TestDirLock.LOCK_EXPIRY_SEC);// should pass now

        Assert.assertNotNull(lock3);
        Assert.assertTrue(fs.exists(lock3.getLockFile()));
        lock3.release();
        Assert.assertFalse(fs.exists(lock3.getLockFile()));
        lock1.release();// should not throw

    }

    class DirLockingThread extends Thread {
        private final FileSystem fs;

        private final Path dir;

        public boolean cleanExit = false;

        private int thdNum;

        public DirLockingThread(int thdNum, FileSystem fs, Path dir) throws IOException {
            this.thdNum = thdNum;
            this.fs = fs;
            this.dir = dir;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(("DirLockingThread-" + (thdNum)));
            DirLock lock = null;
            try {
                do {
                    System.err.println(("Trying lock " + (getName())));
                    lock = DirLock.tryLock(fs, dir);
                    System.err.println(("Acquired lock " + (getName())));
                    if (lock == null) {
                        System.out.println(("Retrying lock - " + (getName())));
                    }
                } while ((lock == null) && (!(Thread.currentThread().isInterrupted())) );
                cleanExit = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (lock != null) {
                        lock.release();
                        System.err.println(("Released lock " + (getName())));
                    }
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
            System.err.println(("Thread exiting " + (getName())));
        }// run()

    }
}

