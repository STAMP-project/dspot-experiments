/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.nio.channels;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import junit.framework.TestCase;


/**
 * Tests class FileLock.
 */
public class FileLockTest extends TestCase {
    private FileChannel readWriteChannel;

    private FileLockTest.MockFileLock mockLock;

    class MockFileLock extends FileLock {
        boolean isValid = true;

        protected MockFileLock(FileChannel channel, long position, long size, boolean shared) {
            super(channel, position, size, shared);
        }

        public boolean isValid() {
            return isValid;
        }

        public void release() throws IOException {
            isValid = false;
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#FileLock(FileChannel, long, long,
    boolean)
     */
    public void test_Constructor_Ljava_nio_channels_FileChannelJJZ() {
        FileLock fileLock1 = new FileLockTest.MockFileLock(null, 0, 0, false);
        TestCase.assertNull(fileLock1.channel());
        try {
            new FileLockTest.MockFileLock(readWriteChannel, (-1), 0, false);
            TestCase.fail("should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            new FileLockTest.MockFileLock(readWriteChannel, 0, (-1), false);
            TestCase.fail("should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        // Harmony-682 regression test
        try {
            new FileLockTest.MockFileLock(readWriteChannel, Long.MAX_VALUE, 1, false);
            TestCase.fail("should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#channel()
     */
    public void test_channel() {
        TestCase.assertSame(readWriteChannel, mockLock.channel());
        FileLock lock = new FileLockTest.MockFileLock(null, 0, 10, true);
        TestCase.assertNull(lock.channel());
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#position()
     */
    public void test_position() {
        FileLock fileLock1 = new FileLockTest.MockFileLock(readWriteChannel, 20, 100, true);
        TestCase.assertEquals(20, fileLock1.position());
        final long position = ((long) (Integer.MAX_VALUE)) + 1;
        FileLock fileLock2 = new FileLockTest.MockFileLock(readWriteChannel, position, 100, true);
        TestCase.assertEquals(position, fileLock2.position());
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#size()
     */
    public void test_size() {
        FileLock fileLock1 = new FileLockTest.MockFileLock(readWriteChannel, 20, 100, true);
        TestCase.assertEquals(100, fileLock1.size());
        final long position = 1152921504606846975L;
        final long size = ((long) (Integer.MAX_VALUE)) + 1;
        FileLock fileLock2 = new FileLockTest.MockFileLock(readWriteChannel, position, size, true);
        TestCase.assertEquals(size, fileLock2.size());
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#isShared()
     */
    public void test_isShared() {
        TestCase.assertFalse(mockLock.isShared());
        FileLock lock = new FileLockTest.MockFileLock(null, 0, 10, true);
        TestCase.assertTrue(lock.isShared());
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#overlaps(long, long)
     */
    public void test_overlaps_JJ() {
        TestCase.assertTrue(mockLock.overlaps(0, 11));
        TestCase.assertFalse(mockLock.overlaps(0, 10));
        TestCase.assertTrue(mockLock.overlaps(100, 110));
        TestCase.assertTrue(mockLock.overlaps(99, 110));
        TestCase.assertFalse(mockLock.overlaps((-1), 10));
        // Harmony-671 regression test
        TestCase.assertTrue(mockLock.overlaps(1, 120));
        TestCase.assertTrue(mockLock.overlaps(20, 50));
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#isValid()
     */
    public void test_isValid() throws IOException {
        FileLock fileLock = readWriteChannel.lock();
        TestCase.assertTrue(fileLock.isValid());
        fileLock.release();
        TestCase.assertFalse(fileLock.isValid());
    }

    /**
     *
     *
     * @unknown java.nio.channels.FileLock#release()
     */
    public void test_release() throws Exception {
        File file = File.createTempFile("test", "tmp");
        file.deleteOnExit();
        FileOutputStream fout = new FileOutputStream(file);
        FileChannel fileChannel = fout.getChannel();
        FileLock fileLock = fileChannel.lock();
        fileChannel.close();
        try {
            fileLock.release();
            TestCase.fail("should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // expected
        }
        // release after release
        fout = new FileOutputStream(file);
        fileChannel = fout.getChannel();
        fileLock = fileChannel.lock();
        fileLock.release();
        fileChannel.close();
        try {
            fileLock.release();
            TestCase.fail("should throw ClosedChannelException");
        } catch (ClosedChannelException e) {
            // expected
        }
    }
}

