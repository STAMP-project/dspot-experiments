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
package org.apache.flink.core.fs;


import WriteMode.NO_OVERWRITE;
import WriteMode.OVERWRITE;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.core.fs.local.LocalFileSystem;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.core.testutils.OneShotLatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link LimitedConnectionsFileSystem}.
 */
public class LimitedConnectionsFileSystemTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testConstructionNumericOverflow() {
        final LimitedConnectionsFileSystem limitedFs = // unlimited total
        // limited outgoing
        // unlimited incoming
        // long timeout, close to overflow
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));// long timeout, close to overflow

        Assert.assertEquals(Integer.MAX_VALUE, limitedFs.getMaxNumOpenStreamsTotal());
        Assert.assertEquals(Integer.MAX_VALUE, limitedFs.getMaxNumOpenOutputStreams());
        Assert.assertEquals(Integer.MAX_VALUE, limitedFs.getMaxNumOpenInputStreams());
        Assert.assertTrue(((limitedFs.getStreamOpenTimeout()) > 0));
        Assert.assertTrue(((limitedFs.getStreamInactivityTimeout()) > 0));
    }

    @Test
    public void testLimitingOutputStreams() throws Exception {
        final int maxConcurrentOpen = 2;
        final int numThreads = 61;
        final LimitedConnectionsFileSystem limitedFs = // unlimited total
        // limited outgoing
        // unlimited incoming
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), Integer.MAX_VALUE, maxConcurrentOpen, Integer.MAX_VALUE, 0, 0);
        final LimitedConnectionsFileSystemTest.WriterThread[] threads = new LimitedConnectionsFileSystemTest.WriterThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            Path path = new Path(tempFolder.newFile().toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.WriterThread(limitedFs, path, maxConcurrentOpen, Integer.MAX_VALUE);
        }
        for (LimitedConnectionsFileSystemTest.WriterThread t : threads) {
            start();
        }
        for (LimitedConnectionsFileSystemTest.WriterThread t : threads) {
            sync();
        }
    }

    @Test
    public void testLimitingInputStreams() throws Exception {
        final int maxConcurrentOpen = 2;
        final int numThreads = 61;
        final LimitedConnectionsFileSystem limitedFs = // unlimited total
        // unlimited outgoing
        // limited incoming
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), Integer.MAX_VALUE, Integer.MAX_VALUE, maxConcurrentOpen, 0, 0);
        final Random rnd = new Random();
        final LimitedConnectionsFileSystemTest.ReaderThread[] threads = new LimitedConnectionsFileSystemTest.ReaderThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            File file = tempFolder.newFile();
            createRandomContents(file, rnd);
            Path path = new Path(file.toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.ReaderThread(limitedFs, path, maxConcurrentOpen, Integer.MAX_VALUE);
        }
        for (LimitedConnectionsFileSystemTest.ReaderThread t : threads) {
            start();
        }
        for (LimitedConnectionsFileSystemTest.ReaderThread t : threads) {
            sync();
        }
    }

    @Test
    public void testLimitingMixedStreams() throws Exception {
        final int maxConcurrentOpen = 2;
        final int numThreads = 61;
        final LimitedConnectionsFileSystem limitedFs = new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), maxConcurrentOpen);// limited total

        final Random rnd = new Random();
        final CheckedThread[] threads = new CheckedThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            File file = tempFolder.newFile();
            Path path = new Path(file.toURI());
            if (rnd.nextBoolean()) {
                // reader thread
                createRandomContents(file, rnd);
                threads[i] = new LimitedConnectionsFileSystemTest.ReaderThread(limitedFs, path, Integer.MAX_VALUE, maxConcurrentOpen);
            } else {
                threads[i] = new LimitedConnectionsFileSystemTest.WriterThread(limitedFs, path, Integer.MAX_VALUE, maxConcurrentOpen);
            }
        }
        for (CheckedThread t : threads) {
            t.start();
        }
        for (CheckedThread t : threads) {
            t.sync();
        }
    }

    @Test
    public void testOpenTimeoutOutputStreams() throws Exception {
        final long openTimeout = 50L;
        final int maxConcurrentOpen = 2;
        final LimitedConnectionsFileSystem limitedFs = // limited total
        // small opening timeout
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), maxConcurrentOpen, openTimeout, 0L);// infinite inactivity timeout

        // create the threads that block all streams
        final LimitedConnectionsFileSystemTest.BlockingWriterThread[] threads = new LimitedConnectionsFileSystemTest.BlockingWriterThread[maxConcurrentOpen];
        for (int i = 0; i < maxConcurrentOpen; i++) {
            Path path = new Path(tempFolder.newFile().toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.BlockingWriterThread(limitedFs, path, Integer.MAX_VALUE, maxConcurrentOpen);
            start();
        }
        // wait until all are open
        while ((limitedFs.getTotalNumberOfOpenStreams()) < maxConcurrentOpen) {
            Thread.sleep(1);
        } 
        // try to open another thread
        try {
            limitedFs.create(new Path(tempFolder.newFile().toURI()), OVERWRITE);
            Assert.fail("this should have timed out");
        } catch (IOException e) {
            // expected
        }
        // clean shutdown
        for (LimitedConnectionsFileSystemTest.BlockingWriterThread t : threads) {
            t.wakeup();
            sync();
        }
    }

    @Test
    public void testOpenTimeoutInputStreams() throws Exception {
        final long openTimeout = 50L;
        final int maxConcurrentOpen = 2;
        final LimitedConnectionsFileSystem limitedFs = // limited total
        // small opening timeout
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), maxConcurrentOpen, openTimeout, 0L);
        // infinite inactivity timeout
        // create the threads that block all streams
        final Random rnd = new Random();
        final LimitedConnectionsFileSystemTest.BlockingReaderThread[] threads = new LimitedConnectionsFileSystemTest.BlockingReaderThread[maxConcurrentOpen];
        for (int i = 0; i < maxConcurrentOpen; i++) {
            File file = tempFolder.newFile();
            createRandomContents(file, rnd);
            Path path = new Path(file.toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.BlockingReaderThread(limitedFs, path, maxConcurrentOpen, Integer.MAX_VALUE);
            start();
        }
        // wait until all are open
        while ((limitedFs.getTotalNumberOfOpenStreams()) < maxConcurrentOpen) {
            Thread.sleep(1);
        } 
        // try to open another thread
        File file = tempFolder.newFile();
        createRandomContents(file, rnd);
        try {
            limitedFs.open(new Path(file.toURI()));
            Assert.fail("this should have timed out");
        } catch (IOException e) {
            // expected
        }
        // clean shutdown
        for (LimitedConnectionsFileSystemTest.BlockingReaderThread t : threads) {
            t.wakeup();
            sync();
        }
    }

    @Test
    public void testTerminateStalledOutputStreams() throws Exception {
        final int maxConcurrentOpen = 2;
        final int numThreads = 20;
        // this testing file system has a 50 ms stream inactivity timeout
        final LimitedConnectionsFileSystem limitedFs = // no limit on total streams
        // limit on output streams
        // no limit on input streams
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), Integer.MAX_VALUE, maxConcurrentOpen, Integer.MAX_VALUE, 0, 50);
        // timeout of 50 ms
        final LimitedConnectionsFileSystemTest.WriterThread[] threads = new LimitedConnectionsFileSystemTest.WriterThread[numThreads];
        final LimitedConnectionsFileSystemTest.BlockingWriterThread[] blockers = new LimitedConnectionsFileSystemTest.BlockingWriterThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            Path path1 = new Path(tempFolder.newFile().toURI());
            Path path2 = new Path(tempFolder.newFile().toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.WriterThread(limitedFs, path1, maxConcurrentOpen, Integer.MAX_VALUE);
            blockers[i] = new LimitedConnectionsFileSystemTest.BlockingWriterThread(limitedFs, path2, maxConcurrentOpen, Integer.MAX_VALUE);
        }
        // start normal and blocker threads
        for (int i = 0; i < numThreads; i++) {
            start();
            start();
        }
        // all normal threads need to be able to finish because
        // the blockers eventually time out
        for (LimitedConnectionsFileSystemTest.WriterThread t : threads) {
            try {
                sync();
            } catch (LimitedConnectionsFileSystem e) {
                // also the regular threads may occasionally get a timeout on
                // slower test machines because we set very aggressive timeouts
                // to reduce the test time
            }
        }
        // unblock all the blocking threads
        for (LimitedConnectionsFileSystemTest.BlockingThread t : blockers) {
            t.wakeup();
        }
        for (LimitedConnectionsFileSystemTest.BlockingThread t : blockers) {
            try {
                sync();
            } catch (LimitedConnectionsFileSystem ignored) {
            }
        }
    }

    @Test
    public void testTerminateStalledInputStreams() throws Exception {
        final int maxConcurrentOpen = 2;
        final int numThreads = 20;
        // this testing file system has a 50 ms stream inactivity timeout
        final LimitedConnectionsFileSystem limitedFs = // no limit on total streams
        // limit on output streams
        // no limit on input streams
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), Integer.MAX_VALUE, Integer.MAX_VALUE, maxConcurrentOpen, 0, 50);
        // timeout of 50 ms
        final Random rnd = new Random();
        final LimitedConnectionsFileSystemTest.ReaderThread[] threads = new LimitedConnectionsFileSystemTest.ReaderThread[numThreads];
        final LimitedConnectionsFileSystemTest.BlockingReaderThread[] blockers = new LimitedConnectionsFileSystemTest.BlockingReaderThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            File file1 = tempFolder.newFile();
            File file2 = tempFolder.newFile();
            createRandomContents(file1, rnd);
            createRandomContents(file2, rnd);
            Path path1 = new Path(file1.toURI());
            Path path2 = new Path(file2.toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.ReaderThread(limitedFs, path1, maxConcurrentOpen, Integer.MAX_VALUE);
            blockers[i] = new LimitedConnectionsFileSystemTest.BlockingReaderThread(limitedFs, path2, maxConcurrentOpen, Integer.MAX_VALUE);
        }
        // start normal and blocker threads
        for (int i = 0; i < numThreads; i++) {
            start();
            start();
        }
        // all normal threads need to be able to finish because
        // the blockers eventually time out
        for (LimitedConnectionsFileSystemTest.ReaderThread t : threads) {
            try {
                sync();
            } catch (LimitedConnectionsFileSystem e) {
                // also the regular threads may occasionally get a timeout on
                // slower test machines because we set very aggressive timeouts
                // to reduce the test time
            }
        }
        // unblock all the blocking threads
        for (LimitedConnectionsFileSystemTest.BlockingThread t : blockers) {
            t.wakeup();
        }
        for (LimitedConnectionsFileSystemTest.BlockingThread t : blockers) {
            try {
                sync();
            } catch (LimitedConnectionsFileSystem ignored) {
            }
        }
    }

    @Test
    public void testTerminateStalledMixedStreams() throws Exception {
        final int maxConcurrentOpen = 2;
        final int numThreads = 20;
        final LimitedConnectionsFileSystem limitedFs = // limited total
        // no opening timeout
        new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), maxConcurrentOpen, 0L, 50L);
        // inactivity timeout of 50 ms
        final Random rnd = new Random();
        final CheckedThread[] threads = new CheckedThread[numThreads];
        final LimitedConnectionsFileSystemTest.BlockingThread[] blockers = new LimitedConnectionsFileSystemTest.BlockingThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            File file1 = tempFolder.newFile();
            File file2 = tempFolder.newFile();
            Path path1 = new Path(file1.toURI());
            Path path2 = new Path(file2.toURI());
            if (rnd.nextBoolean()) {
                createRandomContents(file1, rnd);
                createRandomContents(file2, rnd);
                threads[i] = new LimitedConnectionsFileSystemTest.ReaderThread(limitedFs, path1, maxConcurrentOpen, Integer.MAX_VALUE);
                blockers[i] = new LimitedConnectionsFileSystemTest.BlockingReaderThread(limitedFs, path2, maxConcurrentOpen, Integer.MAX_VALUE);
            } else {
                threads[i] = new LimitedConnectionsFileSystemTest.WriterThread(limitedFs, path1, maxConcurrentOpen, Integer.MAX_VALUE);
                blockers[i] = new LimitedConnectionsFileSystemTest.BlockingWriterThread(limitedFs, path2, maxConcurrentOpen, Integer.MAX_VALUE);
            }
        }
        // start normal and blocker threads
        for (int i = 0; i < numThreads; i++) {
            start();
            start();
        }
        // all normal threads need to be able to finish because
        // the blockers eventually time out
        for (CheckedThread t : threads) {
            try {
                t.sync();
            } catch (LimitedConnectionsFileSystem e) {
                // also the regular threads may occasionally get a timeout on
                // slower test machines because we set very aggressive timeouts
                // to reduce the test time
            }
        }
        // unblock all the blocking threads
        for (LimitedConnectionsFileSystemTest.BlockingThread t : blockers) {
            t.wakeup();
        }
        for (LimitedConnectionsFileSystemTest.BlockingThread t : blockers) {
            try {
                sync();
            } catch (LimitedConnectionsFileSystem ignored) {
            }
        }
    }

    @Test
    public void testFailingStreamsUnregister() throws Exception {
        final LimitedConnectionsFileSystem fs = new LimitedConnectionsFileSystem(new LimitedConnectionsFileSystemTest.FailFs(), 1);
        Assert.assertEquals(0, fs.getNumberOfOpenInputStreams());
        Assert.assertEquals(0, fs.getNumberOfOpenOutputStreams());
        Assert.assertEquals(0, fs.getTotalNumberOfOpenStreams());
        try {
            fs.open(new Path(tempFolder.newFile().toURI()));
            Assert.fail("this is expected to fail with an exception");
        } catch (IOException e) {
            // expected
        }
        try {
            fs.create(new Path(tempFolder.newFile().toURI()), NO_OVERWRITE);
            Assert.fail("this is expected to fail with an exception");
        } catch (IOException e) {
            // expected
        }
        Assert.assertEquals(0, fs.getNumberOfOpenInputStreams());
        Assert.assertEquals(0, fs.getNumberOfOpenOutputStreams());
        Assert.assertEquals(0, fs.getTotalNumberOfOpenStreams());
    }

    /**
     * Tests that a slowly written output stream is not accidentally closed too aggressively, due to
     * a wrong initialization of the timestamps or bytes written that mark when the last progress was checked.
     */
    @Test
    public void testSlowOutputStreamNotClosed() throws Exception {
        final LimitedConnectionsFileSystem fs = new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), 1, 0L, 1000L);
        // some competing threads
        final Random rnd = new Random();
        final LimitedConnectionsFileSystemTest.ReaderThread[] threads = new LimitedConnectionsFileSystemTest.ReaderThread[10];
        for (int i = 0; i < (threads.length); i++) {
            File file = tempFolder.newFile();
            createRandomContents(file, rnd);
            Path path = new Path(file.toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.ReaderThread(fs, path, 1, Integer.MAX_VALUE);
        }
        // open the stream we test
        try (FSDataOutputStream out = fs.create(new Path(tempFolder.newFile().toURI()), OVERWRITE)) {
            // start the other threads that will try to shoot this stream down
            for (LimitedConnectionsFileSystemTest.ReaderThread t : threads) {
                start();
            }
            // read the stream slowly.
            Thread.sleep(5);
            for (int bytesLeft = 50; bytesLeft > 0; bytesLeft--) {
                out.write(bytesLeft);
                Thread.sleep(5);
            }
        }
        // wait for clean shutdown
        for (LimitedConnectionsFileSystemTest.ReaderThread t : threads) {
            sync();
        }
    }

    /**
     * Tests that a slowly read stream is not accidentally closed too aggressively, due to
     * a wrong initialization of the timestamps or bytes written that mark when the last progress was checked.
     */
    @Test
    public void testSlowInputStreamNotClosed() throws Exception {
        final File file = tempFolder.newFile();
        createRandomContents(file, new Random(), 50);
        final LimitedConnectionsFileSystem fs = new LimitedConnectionsFileSystem(LocalFileSystem.getSharedInstance(), 1, 0L, 1000L);
        // some competing threads
        final LimitedConnectionsFileSystemTest.WriterThread[] threads = new LimitedConnectionsFileSystemTest.WriterThread[10];
        for (int i = 0; i < (threads.length); i++) {
            Path path = new Path(tempFolder.newFile().toURI());
            threads[i] = new LimitedConnectionsFileSystemTest.WriterThread(fs, path, 1, Integer.MAX_VALUE);
        }
        // open the stream we test
        try (FSDataInputStream in = fs.open(new Path(file.toURI()))) {
            // start the other threads that will try to shoot this stream down
            for (LimitedConnectionsFileSystemTest.WriterThread t : threads) {
                start();
            }
            // read the stream slowly.
            Thread.sleep(5);
            while ((in.read()) != (-1)) {
                Thread.sleep(5);
            } 
        }
        // wait for clean shutdown
        for (LimitedConnectionsFileSystemTest.WriterThread t : threads) {
            sync();
        }
    }

    // ------------------------------------------------------------------------
    // Testing threads
    // ------------------------------------------------------------------------
    private static final class WriterThread extends CheckedThread {
        private final LimitedConnectionsFileSystem fs;

        private final Path path;

        private final int maxConcurrentOutputStreams;

        private final int maxConcurrentStreamsTotal;

        WriterThread(LimitedConnectionsFileSystem fs, Path path, int maxConcurrentOutputStreams, int maxConcurrentStreamsTotal) {
            this.fs = fs;
            this.path = path;
            this.maxConcurrentOutputStreams = maxConcurrentOutputStreams;
            this.maxConcurrentStreamsTotal = maxConcurrentStreamsTotal;
        }

        @Override
        public void go() throws Exception {
            try (FSDataOutputStream stream = fs.create(path, OVERWRITE)) {
                Assert.assertTrue(((fs.getNumberOfOpenOutputStreams()) <= (maxConcurrentOutputStreams)));
                Assert.assertTrue(((fs.getTotalNumberOfOpenStreams()) <= (maxConcurrentStreamsTotal)));
                final Random rnd = new Random();
                final byte[] data = new byte[(rnd.nextInt(10000)) + 1];
                rnd.nextBytes(data);
                stream.write(data);
            }
        }
    }

    private static final class ReaderThread extends CheckedThread {
        private final LimitedConnectionsFileSystem fs;

        private final Path path;

        private final int maxConcurrentInputStreams;

        private final int maxConcurrentStreamsTotal;

        ReaderThread(LimitedConnectionsFileSystem fs, Path path, int maxConcurrentInputStreams, int maxConcurrentStreamsTotal) {
            this.fs = fs;
            this.path = path;
            this.maxConcurrentInputStreams = maxConcurrentInputStreams;
            this.maxConcurrentStreamsTotal = maxConcurrentStreamsTotal;
        }

        @Override
        public void go() throws Exception {
            try (FSDataInputStream stream = fs.open(path)) {
                Assert.assertTrue(((fs.getNumberOfOpenInputStreams()) <= (maxConcurrentInputStreams)));
                Assert.assertTrue(((fs.getTotalNumberOfOpenStreams()) <= (maxConcurrentStreamsTotal)));
                final byte[] readBuffer = new byte[4096];
                // noinspection StatementWithEmptyBody
                while ((stream.read(readBuffer)) != (-1)) {
                } 
            }
        }
    }

    private abstract static class BlockingThread extends CheckedThread {
        private final OneShotLatch waiter = new OneShotLatch();

        public void waitTillWokenUp() throws InterruptedException {
            waiter.await();
        }

        public void wakeup() {
            waiter.trigger();
        }
    }

    private static final class BlockingWriterThread extends LimitedConnectionsFileSystemTest.BlockingThread {
        private final LimitedConnectionsFileSystem fs;

        private final Path path;

        private final int maxConcurrentOutputStreams;

        private final int maxConcurrentStreamsTotal;

        BlockingWriterThread(LimitedConnectionsFileSystem fs, Path path, int maxConcurrentOutputStreams, int maxConcurrentStreamsTotal) {
            this.fs = fs;
            this.path = path;
            this.maxConcurrentOutputStreams = maxConcurrentOutputStreams;
            this.maxConcurrentStreamsTotal = maxConcurrentStreamsTotal;
        }

        @Override
        public void go() throws Exception {
            try (FSDataOutputStream stream = fs.create(path, OVERWRITE)) {
                Assert.assertTrue(((fs.getNumberOfOpenOutputStreams()) <= (maxConcurrentOutputStreams)));
                Assert.assertTrue(((fs.getTotalNumberOfOpenStreams()) <= (maxConcurrentStreamsTotal)));
                final Random rnd = new Random();
                final byte[] data = new byte[(rnd.nextInt(10000)) + 1];
                rnd.nextBytes(data);
                stream.write(data);
                waitTillWokenUp();
                // try to write one more thing, which might/should fail with an I/O exception
                stream.write(rnd.nextInt());
            }
        }
    }

    private static final class BlockingReaderThread extends LimitedConnectionsFileSystemTest.BlockingThread {
        private final LimitedConnectionsFileSystem fs;

        private final Path path;

        private final int maxConcurrentInputStreams;

        private final int maxConcurrentStreamsTotal;

        BlockingReaderThread(LimitedConnectionsFileSystem fs, Path path, int maxConcurrentInputStreams, int maxConcurrentStreamsTotal) {
            this.fs = fs;
            this.path = path;
            this.maxConcurrentInputStreams = maxConcurrentInputStreams;
            this.maxConcurrentStreamsTotal = maxConcurrentStreamsTotal;
        }

        @Override
        public void go() throws Exception {
            try (FSDataInputStream stream = fs.open(path)) {
                Assert.assertTrue(((fs.getNumberOfOpenInputStreams()) <= (maxConcurrentInputStreams)));
                Assert.assertTrue(((fs.getTotalNumberOfOpenStreams()) <= (maxConcurrentStreamsTotal)));
                final byte[] readBuffer = new byte[((int) (fs.getFileStatus(path).getLen())) - 1];
                Assert.assertTrue(((stream.read(readBuffer)) != (-1)));
                waitTillWokenUp();
                // try to write one more thing, which might/should fail with an I/O exception
                // noinspection ResultOfMethodCallIgnored
                stream.read();
            }
        }
    }

    // ------------------------------------------------------------------------
    // failing file system
    // ------------------------------------------------------------------------
    private static class FailFs extends LocalFileSystem {
        @Override
        public FSDataOutputStream create(Path filePath, WriteMode overwrite) throws IOException {
            throw new IOException("test exception");
        }

        @Override
        public FSDataInputStream open(Path f) throws IOException {
            throw new IOException("test exception");
        }
    }
}

