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
package org.apache.hadoop.hdfs;


import NameNode.stateChangeLog;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.server.namenode.TestFileTruncate;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test randomly mixing append, snapshot and truncate operations.
 * Use local file system to simulate the each operation and verify
 * the correctness.
 */
public class TestAppendSnapshotTruncate {
    static {
        GenericTestUtils.setLogLevel(stateChangeLog, Level.ALL);
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestAppendSnapshotTruncate.class);

    private static final int BLOCK_SIZE = 1024;

    private static final int DATANODE_NUM = 4;

    private static final short REPLICATION = 3;

    private static final int FILE_WORKER_NUM = 10;

    private static final long TEST_TIME_SECOND = 20;

    private static final long TEST_TIMEOUT_SECOND = (TestAppendSnapshotTruncate.TEST_TIME_SECOND) + 60;

    static final int SHORT_HEARTBEAT = 1;

    static final String[] EMPTY_STRINGS = new String[]{  };

    static Configuration conf;

    static MiniDFSCluster cluster;

    static DistributedFileSystem dfs;

    /**
     * Test randomly mixing append, snapshot and truncate operations.
     */
    @Test(timeout = (TestAppendSnapshotTruncate.TEST_TIMEOUT_SECOND) * 1000)
    public void testAST() throws Exception {
        final String dirPathString = "/dir";
        final Path dir = new Path(dirPathString);
        TestAppendSnapshotTruncate.dfs.mkdirs(dir);
        TestAppendSnapshotTruncate.dfs.allowSnapshot(dir);
        final File localDir = GenericTestUtils.getTestDir(dirPathString);
        if (localDir.exists()) {
            FileUtil.fullyDelete(localDir);
        }
        localDir.mkdirs();
        final TestAppendSnapshotTruncate.DirWorker w = new TestAppendSnapshotTruncate.DirWorker(dir, localDir, TestAppendSnapshotTruncate.FILE_WORKER_NUM);
        w.startAllFiles();
        w.start();
        TestAppendSnapshotTruncate.Worker.sleep(((TestAppendSnapshotTruncate.TEST_TIME_SECOND) * 1000));
        w.stop();
        w.stopAllFiles();
        w.checkEverything();
    }

    static final FileFilter FILE_ONLY = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isFile();
        }
    };

    static class DirWorker extends TestAppendSnapshotTruncate.Worker {
        final Path dir;

        final File localDir;

        final TestAppendSnapshotTruncate.FileWorker[] files;

        private Map<String, Path> snapshotPaths = new HashMap<String, Path>();

        private AtomicInteger snapshotCount = new AtomicInteger();

        DirWorker(Path dir, File localDir, int nFiles) throws IOException {
            super(dir.getName());
            this.dir = dir;
            this.localDir = localDir;
            this.files = new TestAppendSnapshotTruncate.FileWorker[nFiles];
            for (int i = 0; i < (files.length); i++) {
                files[i] = new TestAppendSnapshotTruncate.FileWorker(dir, localDir, String.format("file%02d", i));
            }
        }

        static String getSnapshotName(int n) {
            return String.format("s%02d", n);
        }

        String createSnapshot(String snapshot) throws IOException {
            final StringBuilder b = new StringBuilder("createSnapshot: ").append(snapshot).append(" for ").append(dir);
            {
                // copy all local files to a sub dir to simulate snapshot.
                final File subDir = new File(localDir, snapshot);
                Assert.assertFalse(subDir.exists());
                subDir.mkdir();
                for (File f : localDir.listFiles(TestAppendSnapshotTruncate.FILE_ONLY)) {
                    FileUtils.copyFile(f, new File(subDir, f.getName()));
                }
            }
            final Path p = TestAppendSnapshotTruncate.dfs.createSnapshot(dir, snapshot);
            snapshotPaths.put(snapshot, p);
            return b.toString();
        }

        String checkSnapshot(String snapshot) throws IOException {
            final StringBuilder b = new StringBuilder("checkSnapshot: ").append(snapshot);
            final File subDir = new File(localDir, snapshot);
            Assert.assertTrue(subDir.exists());
            final File[] localFiles = subDir.listFiles(TestAppendSnapshotTruncate.FILE_ONLY);
            final Path p = snapshotPaths.get(snapshot);
            final FileStatus[] statuses = TestAppendSnapshotTruncate.dfs.listStatus(p);
            Assert.assertEquals(localFiles.length, statuses.length);
            b.append(p).append(" vs ").append(subDir).append(", ").append(statuses.length).append(" entries");
            Arrays.sort(localFiles);
            Arrays.sort(statuses);
            for (int i = 0; i < (statuses.length); i++) {
                TestAppendSnapshotTruncate.FileWorker.checkFullFile(statuses[i].getPath(), localFiles[i]);
            }
            return b.toString();
        }

        String deleteSnapshot(String snapshot) throws IOException {
            final StringBuilder b = new StringBuilder("deleteSnapshot: ").append(snapshot).append(" from ").append(dir);
            FileUtil.fullyDelete(new File(localDir, snapshot));
            TestAppendSnapshotTruncate.dfs.deleteSnapshot(dir, snapshot);
            snapshotPaths.remove(snapshot);
            return b.toString();
        }

        @Override
        public String call() throws Exception {
            final int op = ThreadLocalRandom.current().nextInt(6);
            if (op <= 1) {
                pauseAllFiles();
                try {
                    final String snapshot = TestAppendSnapshotTruncate.DirWorker.getSnapshotName(snapshotCount.getAndIncrement());
                    return createSnapshot(snapshot);
                } finally {
                    startAllFiles();
                }
            } else
                if (op <= 3) {
                    final String[] keys = snapshotPaths.keySet().toArray(TestAppendSnapshotTruncate.EMPTY_STRINGS);
                    if ((keys.length) == 0) {
                        return "NO-OP";
                    }
                    final String snapshot = keys[ThreadLocalRandom.current().nextInt(keys.length)];
                    final String s = checkSnapshot(snapshot);
                    if (op == 2) {
                        return deleteSnapshot(snapshot);
                    }
                    return s;
                } else {
                    return "NO-OP";
                }

        }

        void pauseAllFiles() {
            for (TestAppendSnapshotTruncate.FileWorker f : files) {
                f.pause();
            }
            for (int i = 0; i < (files.length);) {
                TestAppendSnapshotTruncate.Worker.sleep(100);
                for (; (i < (files.length)) && (files[i].isPaused()); i++);
            }
        }

        void startAllFiles() {
            for (TestAppendSnapshotTruncate.FileWorker f : files) {
                f.start();
            }
        }

        void stopAllFiles() throws InterruptedException {
            for (TestAppendSnapshotTruncate.FileWorker f : files) {
                f.stop();
            }
        }

        void checkEverything() throws IOException {
            TestAppendSnapshotTruncate.LOG.info("checkEverything");
            for (TestAppendSnapshotTruncate.FileWorker f : files) {
                f.checkFullFile();
                f.checkErrorState();
            }
            for (String snapshot : snapshotPaths.keySet()) {
                checkSnapshot(snapshot);
            }
            checkErrorState();
        }
    }

    static class FileWorker extends TestAppendSnapshotTruncate.Worker {
        final Path file;

        final File localFile;

        FileWorker(Path dir, File localDir, String filename) throws IOException {
            super(filename);
            this.file = new Path(dir, filename);
            this.localFile = new File(localDir, filename);
            localFile.createNewFile();
            TestAppendSnapshotTruncate.dfs.create(file, false, 4096, TestAppendSnapshotTruncate.REPLICATION, TestAppendSnapshotTruncate.BLOCK_SIZE).close();
        }

        @Override
        public String call() throws IOException {
            final int op = ThreadLocalRandom.current().nextInt(9);
            if (op == 0) {
                return checkFullFile();
            } else {
                final int nBlocks = (ThreadLocalRandom.current().nextInt(4)) + 1;
                final int lastBlockSize = (ThreadLocalRandom.current().nextInt(TestAppendSnapshotTruncate.BLOCK_SIZE)) + 1;
                final int nBytes = (nBlocks * (TestAppendSnapshotTruncate.BLOCK_SIZE)) + lastBlockSize;
                if (op <= 4) {
                    return append(nBytes);
                } else
                    if (op <= 6) {
                        return truncateArbitrarily(nBytes);
                    } else {
                        return truncateToBlockBoundary(nBlocks);
                    }

            }
        }

        String append(int n) throws IOException {
            final StringBuilder b = new StringBuilder("append ").append(n).append(" bytes to ").append(file.getName());
            final byte[] bytes = new byte[n];
            ThreadLocalRandom.current().nextBytes(bytes);
            {
                // write to local file
                final FileOutputStream out = new FileOutputStream(localFile, true);
                out.write(bytes, 0, bytes.length);
                out.close();
            }
            {
                final FSDataOutputStream out = TestAppendSnapshotTruncate.dfs.append(file);
                out.write(bytes, 0, bytes.length);
                out.close();
            }
            return b.toString();
        }

        String truncateArbitrarily(int nBytes) throws IOException {
            Preconditions.checkArgument((nBytes > 0));
            final int length = checkLength();
            final StringBuilder b = new StringBuilder("truncateArbitrarily: ").append(nBytes).append(" bytes from ").append(file.getName()).append((", length=" + length));
            truncate((length > nBytes ? length - nBytes : 0), b);
            return b.toString();
        }

        String truncateToBlockBoundary(int nBlocks) throws IOException {
            Preconditions.checkArgument((nBlocks > 0));
            final int length = checkLength();
            final StringBuilder b = new StringBuilder("truncateToBlockBoundary: ").append(nBlocks).append(" blocks from ").append(file.getName()).append((", length=" + length));
            final int n = ((nBlocks - 1) * (TestAppendSnapshotTruncate.BLOCK_SIZE)) + (length % (TestAppendSnapshotTruncate.BLOCK_SIZE));
            Preconditions.checkState(truncate((length > n ? length - n : 0), b), b);
            return b.toString();
        }

        private boolean truncate(long newLength, StringBuilder b) throws IOException {
            final RandomAccessFile raf = new RandomAccessFile(localFile, "rw");
            raf.setLength(newLength);
            raf.close();
            final boolean isReady = TestAppendSnapshotTruncate.dfs.truncate(file, newLength);
            b.append(", newLength=").append(newLength).append(", isReady=").append(isReady);
            if (!isReady) {
                TestFileTruncate.checkBlockRecovery(file, TestAppendSnapshotTruncate.dfs, 100, 300L);
            }
            return isReady;
        }

        int checkLength() throws IOException {
            return TestAppendSnapshotTruncate.FileWorker.checkLength(file, localFile);
        }

        static int checkLength(Path file, File localFile) throws IOException {
            final long length = TestAppendSnapshotTruncate.dfs.getFileStatus(file).getLen();
            Assert.assertEquals(localFile.length(), length);
            Assert.assertTrue((length <= (Integer.MAX_VALUE)));
            return ((int) (length));
        }

        String checkFullFile() throws IOException {
            return TestAppendSnapshotTruncate.FileWorker.checkFullFile(file, localFile);
        }

        static String checkFullFile(Path file, File localFile) throws IOException {
            final StringBuilder b = new StringBuilder("checkFullFile: ").append(file.getName()).append(" vs ").append(localFile);
            final byte[] bytes = new byte[TestAppendSnapshotTruncate.FileWorker.checkLength(file, localFile)];
            b.append(", length=").append(bytes.length);
            final FileInputStream in = new FileInputStream(localFile);
            for (int n = 0; n < (bytes.length);) {
                n += in.read(bytes, n, ((bytes.length) - n));
            }
            in.close();
            AppendTestUtil.checkFullFile(TestAppendSnapshotTruncate.dfs, file, bytes.length, bytes, ("File content mismatch: " + b), false);
            return b.toString();
        }
    }

    abstract static class Worker implements Callable<String> {
        enum State {

            IDLE(false),
            RUNNING(false),
            STOPPED(true),
            ERROR(true);
            final boolean isTerminated;

            State(boolean isTerminated) {
                this.isTerminated = isTerminated;
            }
        }

        final String name;

        final AtomicReference<TestAppendSnapshotTruncate.Worker.State> state = new AtomicReference<TestAppendSnapshotTruncate.Worker.State>(TestAppendSnapshotTruncate.Worker.State.IDLE);

        final AtomicBoolean isCalling = new AtomicBoolean();

        final AtomicReference<Thread> thread = new AtomicReference<Thread>();

        private Throwable thrown = null;

        Worker(String name) {
            this.name = name;
        }

        TestAppendSnapshotTruncate.Worker.State checkErrorState() {
            final TestAppendSnapshotTruncate.Worker.State s = state.get();
            if (s == (TestAppendSnapshotTruncate.Worker.State.ERROR)) {
                throw new IllegalStateException((((name) + " has ") + s), thrown);
            }
            return s;
        }

        void setErrorState(Throwable t) {
            checkErrorState();
            TestAppendSnapshotTruncate.LOG.error((("Worker " + (name)) + " failed."), t);
            state.set(TestAppendSnapshotTruncate.Worker.State.ERROR);
            thrown = t;
        }

        void start() {
            Preconditions.checkState(state.compareAndSet(TestAppendSnapshotTruncate.Worker.State.IDLE, TestAppendSnapshotTruncate.Worker.State.RUNNING));
            if ((thread.get()) == null) {
                final Thread t = new Thread(null, new Runnable() {
                    @Override
                    public void run() {
                        for (TestAppendSnapshotTruncate.Worker.State s; !((s = checkErrorState()).isTerminated);) {
                            if (s == (TestAppendSnapshotTruncate.Worker.State.RUNNING)) {
                                isCalling.set(true);
                                try {
                                    TestAppendSnapshotTruncate.LOG.info(call());
                                } catch (Throwable t) {
                                    setErrorState(t);
                                    return;
                                }
                                isCalling.set(false);
                            }
                            TestAppendSnapshotTruncate.Worker.sleep(((ThreadLocalRandom.current().nextInt(100)) + 50));
                        }
                    }
                }, name);
                Preconditions.checkState(thread.compareAndSet(null, t));
                t.start();
            }
        }

        boolean isPaused() {
            final TestAppendSnapshotTruncate.Worker.State s = checkErrorState();
            if (s == (TestAppendSnapshotTruncate.Worker.State.STOPPED)) {
                throw new IllegalStateException((((name) + " is ") + s));
            }
            return (s == (TestAppendSnapshotTruncate.Worker.State.IDLE)) && (!(isCalling.get()));
        }

        void pause() {
            checkErrorState();
            Preconditions.checkState(state.compareAndSet(TestAppendSnapshotTruncate.Worker.State.RUNNING, TestAppendSnapshotTruncate.Worker.State.IDLE), "%s: state=%s != %s", name, state.get(), TestAppendSnapshotTruncate.Worker.State.RUNNING);
        }

        void stop() throws InterruptedException {
            checkErrorState();
            state.set(TestAppendSnapshotTruncate.Worker.State.STOPPED);
            thread.get().join();
        }

        static void sleep(final long sleepTimeMs) {
            try {
                Thread.sleep(sleepTimeMs);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

