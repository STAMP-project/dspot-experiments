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


import WriteMode.OVERWRITE;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.flink.core.fs.local.LocalFileSystem;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.core.testutils.OneShotLatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * A test validating that the initialization of local output paths is properly synchronized.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LocalFileSystem.class)
public class InitOutputPathTest {
    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    /**
     * This test validates that this test case makes sense - that the error can be produced
     * in the absence of synchronization, if the threads make progress in a certain way,
     * here enforced by latches.
     */
    @Test
    public void testErrorOccursUnSynchronized() throws Exception {
        // deactivate the lock to produce the original un-synchronized state
        Field lock = FileSystem.class.getDeclaredField("OUTPUT_DIRECTORY_INIT_LOCK");
        lock.setAccessible(true);
        lock.set(null, new InitOutputPathTest.NoOpLock());
        try {
            // in the original un-synchronized state, we can force the race to occur by using
            // the proper latch order to control the process of the concurrent threads
            runTest(true);
            Assert.fail("should fail with an exception");
        } catch (FileNotFoundException e) {
            // expected
        } finally {
            // reset the proper value
            lock.set(null, new ReentrantLock(true));
        }
    }

    @Test
    public void testProperSynchronized() throws Exception {
        // in the synchronized variant, we cannot use the "await latches" because not
        // both threads can make process interleaved (due to the synchronization)
        // the test uses sleeps (rather than latches) to produce the same interleaving.
        // while that is not guaranteed to produce the pathological interleaving,
        // it helps to provoke it very often. together with validating that this order
        // is in fact pathological (see testErrorOccursUnSynchronized()), this gives
        // a rather confident guard
        runTest(false);
    }

    // ------------------------------------------------------------------------
    private static class FileCreator extends CheckedThread {
        private final FileSystem fs;

        private final Path path;

        FileCreator(FileSystem fs, Path path) {
            this.fs = fs;
            this.path = path;
        }

        @Override
        public void go() throws Exception {
            fs.initOutPathLocalFS(path.getParent(), OVERWRITE, true);
            try (FSDataOutputStream out = fs.create(path, OVERWRITE)) {
                out.write(11);
            }
        }
    }

    // ------------------------------------------------------------------------
    private static class SyncedFileSystem extends LocalFileSystem {
        private final OneShotLatch deleteTriggerLatch;

        private final OneShotLatch mkdirsTriggerLatch;

        private final OneShotLatch deleteAwaitLatch;

        private final OneShotLatch mkdirsAwaitLatch;

        SyncedFileSystem(OneShotLatch deleteTriggerLatch, OneShotLatch mkdirsTriggerLatch, OneShotLatch deleteAwaitLatch, OneShotLatch mkdirsAwaitLatch) {
            this.deleteTriggerLatch = deleteTriggerLatch;
            this.mkdirsTriggerLatch = mkdirsTriggerLatch;
            this.deleteAwaitLatch = deleteAwaitLatch;
            this.mkdirsAwaitLatch = mkdirsAwaitLatch;
        }

        @Override
        public boolean delete(Path f, boolean recursive) throws IOException {
            deleteTriggerLatch.trigger();
            try {
                deleteAwaitLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("interrupted");
            }
            return super.delete(f, recursive);
        }

        @Override
        public boolean mkdirs(Path f) throws IOException {
            mkdirsTriggerLatch.trigger();
            try {
                mkdirsAwaitLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("interrupted");
            }
            return super.mkdirs(f);
        }
    }

    // ------------------------------------------------------------------------
    @SuppressWarnings("serial")
    private static final class NoOpLock extends ReentrantLock {
        @Override
        public void lock() {
        }

        @Override
        public void lockInterruptibly() {
        }

        @Override
        public void unlock() {
        }
    }
}

