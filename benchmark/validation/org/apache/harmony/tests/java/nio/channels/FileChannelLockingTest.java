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


import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import junit.framework.TestCase;


/**
 * API tests for the NIO FileChannel locking APIs
 */
public class FileChannelLockingTest extends TestCase {
    private FileChannel readOnlyChannel;

    private FileChannel writeOnlyChannel;

    private FileChannel readWriteChannel;

    private final String CONTENT = "The best things in life are nearest: Breath in your nostrils, light in your eyes, " + (("flowers at your feet, duties at your hand, the path of right just before you. Then do not grasp at the stars, " + "but do life's plain, common work as it comes, certain that daily duties and daily bread are the sweetest ") + " things in life.--Robert Louis Stevenson");

    public void test_illegalLocks() throws IOException {
        // Cannot acquire an exclusive lock on a read-only file channel
        try {
            readOnlyChannel.lock();
            TestCase.fail("Acquiring a full exclusive lock on a read only channel should fail.");
        } catch (NonWritableChannelException ex) {
            // Expected.
        }
        // Cannot get a shared lock on a write-only file channel.
        try {
            writeOnlyChannel.lock(1, 10, true);
            TestCase.fail("Acquiring a shared lock on a write-only channel should fail.");
        } catch (NonReadableChannelException ex) {
            // expected
        }
    }

    public void test_lockReadWrite() throws IOException {
        // Acquire an exclusive lock across the entire file.
        FileLock flock = readWriteChannel.lock();
        if (flock != null) {
            flock.release();
        }
    }

    public void test_illegalLockParameters() throws IOException {
        // Cannot lock negative positions
        try {
            readOnlyChannel.lock((-1), 10, true);
            TestCase.fail("Passing illegal args to lock should fail.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            writeOnlyChannel.lock((-1), 10, false);
            TestCase.fail("Passing illegal args to lock should fail.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            readWriteChannel.lock((-1), 10, false);
            TestCase.fail("Passing illegal args to lock should fail.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        // Lock a range at the front, shared.
        FileLock flock1 = readWriteChannel.lock(22, 110, true);
        // Try to acquire an overlapping lock.
        try {
            readWriteChannel.lock(75, 210, true);
        } catch (OverlappingFileLockException exception) {
            // expected
            flock1.release();
        }
    }

    public void test_lockLLZ() throws IOException {
        // Lock a range at the front, non-shared.
        FileLock flock1 = readWriteChannel.lock(0, 10, false);
        // Lock a shared range further in the same file.
        FileLock flock2 = readWriteChannel.lock(22, 100, true);
        // The spec allows the impl to refuse shared locks
        flock1.release();
        flock2.release();
    }

    public void test_tryLock() throws IOException {
        try {
            readOnlyChannel.tryLock();
            TestCase.fail("Acquiring a full exclusive lock on a read channel should have thrown an exception.");
        } catch (NonWritableChannelException ex) {
            // Expected.
        }
    }

    public void test_tryLockLLZ() throws IOException {
        // It is illegal to request an exclusive lock on a read-only channel
        try {
            readOnlyChannel.tryLock(0, 99, false);
            TestCase.fail("Acquiring exclusive lock on read-only channel should fail");
        } catch (NonWritableChannelException ex) {
            // Expected
        }
        // It is invalid to request a lock starting before the file start
        try {
            readOnlyChannel.tryLock((-99), 0, true);
            TestCase.fail("Acquiring an illegal lock value should fail.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        // Acquire a valid lock
        FileLock tmpLock = readOnlyChannel.tryLock(0, 10, true);
        TestCase.assertTrue(tmpLock.isValid());
        tmpLock.release();
        // Acquire another valid lock -- and don't release it yet
        FileLock lock = readOnlyChannel.tryLock(10, 788, true);
        TestCase.assertTrue(lock.isValid());
        // Overlapping locks are illegal
        try {
            readOnlyChannel.tryLock(1, 23, true);
            TestCase.fail("Acquiring an overlapping lock should fail.");
        } catch (OverlappingFileLockException ex) {
            // Expected
        }
        // Adjacent locks are legal
        FileLock adjacentLock = readOnlyChannel.tryLock(1, 3, true);
        TestCase.assertTrue(adjacentLock.isValid());
        adjacentLock.release();
        // Release longer lived lock
        lock.release();
    }
}

