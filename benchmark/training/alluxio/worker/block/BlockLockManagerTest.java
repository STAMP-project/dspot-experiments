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
package alluxio.worker.block;


import BlockLockType.READ;
import BlockLockType.WRITE;
import ExceptionMessage.LOCK_ID_FOR_DIFFERENT_BLOCK;
import ExceptionMessage.LOCK_ID_FOR_DIFFERENT_SESSION;
import ExceptionMessage.LOCK_RECORD_NOT_FOUND_FOR_LOCK_ID;
import alluxio.collections.ConcurrentHashSet;
import alluxio.exception.BlockDoesNotExistException;
import alluxio.exception.InvalidWorkerStateException;
import com.google.common.base.Throwables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit tests for {@link BlockLockManager}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BlockMetadataManager.class)
public final class BlockLockManagerTest {
    private static final long TEST_SESSION_ID = 2;

    private static final long TEST_BLOCK_ID = 9;

    private BlockLockManager mLockManager;

    /**
     * Rule to create a new temporary folder during each test.
     */
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * Tests the {@link BlockLockManager#lockBlock(long, long, BlockLockType)} method.
     */
    @Test
    public void lockBlock() {
        // Read-lock on can both get through
        long lockId1 = mLockManager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        long lockId2 = mLockManager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        Assert.assertNotEquals(lockId1, lockId2);
    }

    /**
     * Tests that an exception is thrown when trying to unlock a block via
     * {@link BlockLockManager#unlockBlockNoException(long)} which is not locked.
     */
    @Test
    public void unlockNonExistingLock() throws Exception {
        long badLockId = 1;
        // Unlock a non-existing lockId, expect to see IOException
        Assert.assertFalse(mLockManager.unlockBlockNoException(badLockId));
    }

    /**
     * Tests that an exception is thrown when trying to validate a lock of a block via
     * {@link BlockLockManager#validateLock(long, long, long)} which is not locked.
     */
    @Test
    public void validateLockIdWithNoRecord() throws Exception {
        long badLockId = 1;
        mThrown.expect(BlockDoesNotExistException.class);
        mThrown.expectMessage(LOCK_RECORD_NOT_FOUND_FOR_LOCK_ID.getMessage(badLockId));
        // Validate a non-existing lockId, expect to see IOException
        mLockManager.validateLock(BlockLockManagerTest.TEST_SESSION_ID, BlockLockManagerTest.TEST_BLOCK_ID, badLockId);
    }

    /**
     * Tests that an exception is thrown when trying to validate a lock of a block via
     * {@link BlockLockManager#validateLock(long, long, long)} with an incorrect session ID.
     */
    @Test
    public void validateLockIdWithWrongSessionId() throws Exception {
        long lockId = mLockManager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        long wrongSessionId = (BlockLockManagerTest.TEST_SESSION_ID) + 1;
        mThrown.expect(InvalidWorkerStateException.class);
        mThrown.expectMessage(LOCK_ID_FOR_DIFFERENT_SESSION.getMessage(lockId, BlockLockManagerTest.TEST_SESSION_ID, wrongSessionId));
        // Validate an existing lockId with wrong session id, expect to see IOException
        mLockManager.validateLock(wrongSessionId, BlockLockManagerTest.TEST_BLOCK_ID, lockId);
    }

    /**
     * Tests that an exception is thrown when trying to validate a lock of a block via
     * {@link BlockLockManager#validateLock(long, long, long)} with an incorrect block ID.
     */
    @Test
    public void validateLockIdWithWrongBlockId() throws Exception {
        long lockId = mLockManager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        long wrongBlockId = (BlockLockManagerTest.TEST_BLOCK_ID) + 1;
        mThrown.expect(InvalidWorkerStateException.class);
        mThrown.expectMessage(LOCK_ID_FOR_DIFFERENT_BLOCK.getMessage(lockId, BlockLockManagerTest.TEST_BLOCK_ID, wrongBlockId));
        // Validate an existing lockId with wrong block id, expect to see IOException
        mLockManager.validateLock(BlockLockManagerTest.TEST_SESSION_ID, wrongBlockId, lockId);
    }

    /**
     * Tests that an exception is thrown when trying to validate a lock of a block via
     * {@link BlockLockManager#validateLock(long, long, long)} after the session was cleaned up.
     */
    @Test
    public void cleanupSession() throws Exception {
        long sessionId1 = BlockLockManagerTest.TEST_SESSION_ID;
        long sessionId2 = (BlockLockManagerTest.TEST_SESSION_ID) + 1;
        long lockId1 = mLockManager.lockBlock(sessionId1, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        long lockId2 = mLockManager.lockBlock(sessionId2, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        mThrown.expect(BlockDoesNotExistException.class);
        mThrown.expectMessage(LOCK_RECORD_NOT_FOUND_FOR_LOCK_ID.getMessage(lockId2));
        mLockManager.cleanupSession(sessionId2);
        // Expect validating sessionId1 to get through
        mLockManager.validateLock(sessionId1, BlockLockManagerTest.TEST_BLOCK_ID, lockId1);
        // Because sessionId2 has been cleaned up, expect validating sessionId2 to throw IOException
        mLockManager.validateLock(sessionId2, BlockLockManagerTest.TEST_BLOCK_ID, lockId2);
    }

    /**
     * Tests that up to WORKER_TIERED_STORE_BLOCK_LOCKS block locks can be grabbed simultaneously.
     */
    @Test(timeout = 10000)
    public void grabManyLocks() throws Exception {
        int maxLocks = 100;
        setMaxLocks(maxLocks);
        BlockLockManager manager = new BlockLockManager();
        for (int i = 0; i < maxLocks; i++) {
            manager.lockBlock(i, i, WRITE);
        }
        lockExpectingHang(manager, 101);
    }

    /**
     * Tests that an exception is thrown when a session tries to acquire a write lock on a block that
     * it currently has a read lock on.
     */
    @Test
    public void lockAlreadyReadLockedBlock() {
        BlockLockManager manager = new BlockLockManager();
        manager.lockBlock(1, 1, READ);
        mThrown.expect(IllegalStateException.class);
        manager.lockBlock(1, 1, WRITE);
    }

    /**
     * Tests that an exception is thrown when a session tries to acquire a write lock on a block that
     * it currently has a write lock on.
     */
    @Test
    public void lockAlreadyWriteLockedBlock() {
        BlockLockManager manager = new BlockLockManager();
        manager.lockBlock(1, 1, WRITE);
        mThrown.expect(IllegalStateException.class);
        manager.lockBlock(1, 1, WRITE);
    }

    /**
     * Tests that two sessions can both take a read lock on the same block.
     */
    @Test(timeout = 10000)
    public void lockAcrossSessions() throws Exception {
        BlockLockManager manager = new BlockLockManager();
        manager.lockBlock(1, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        manager.lockBlock(2, BlockLockManagerTest.TEST_BLOCK_ID, READ);
    }

    /**
     * Tests that a write lock can't be taken while a read lock is held.
     */
    @Test(timeout = 10000)
    public void readBlocksWrite() throws Exception {
        BlockLockManager manager = new BlockLockManager();
        manager.lockBlock(1, BlockLockManagerTest.TEST_BLOCK_ID, READ);
        lockExpectingHang(manager, BlockLockManagerTest.TEST_BLOCK_ID);
    }

    /**
     * Tests that block locks are returned to the pool when they are no longer in use.
     */
    @Test(timeout = 10000)
    public void reuseLock() throws Exception {
        setMaxLocks(1);
        BlockLockManager manager = new BlockLockManager();
        long lockId1 = manager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, 1, WRITE);
        Assert.assertTrue(manager.unlockBlockNoException(lockId1));// Without this line the next lock would hang.

        manager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, 2, WRITE);
    }

    /**
     * Tests that block locks are not returned to the pool when they are still in use.
     */
    @Test(timeout = 10000)
    public void dontReuseLock() throws Exception {
        setMaxLocks(1);
        final BlockLockManager manager = new BlockLockManager();
        long lockId1 = manager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, 1, READ);
        manager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, 1, READ);
        Assert.assertTrue(manager.unlockBlockNoException(lockId1));
        lockExpectingHang(manager, 2);
    }

    /**
     * Tests that taking and releasing many block locks concurrently won't cause a failure.
     *
     * This is done by creating 200 threads, 100 for each of 2 different block ids. Each thread locks
     * and then unlocks its block 50 times. After this, it takes a final lock on its block before
     * returning. At the end of the test, the internal state of the lock manager is validated.
     */
    @Test(timeout = 10000)
    public void stress() throws Throwable {
        final int numBlocks = 2;
        final int threadsPerBlock = 100;
        final int lockUnlocksPerThread = 50;
        setMaxLocks(numBlocks);
        final BlockLockManager manager = new BlockLockManager();
        final List<Thread> threads = new ArrayList<>();
        final CyclicBarrier barrier = new CyclicBarrier((numBlocks * threadsPerBlock));
        // If there are exceptions, we will store them here.
        final ConcurrentHashSet<Throwable> failedThreadThrowables = new ConcurrentHashSet();
        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                failedThreadThrowables.add(ex);
            }
        };
        for (int blockId = 0; blockId < numBlocks; blockId++) {
            final int finalBlockId = blockId;
            for (int i = 0; i < threadsPerBlock; i++) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            barrier.await();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        // Lock and unlock the block lockUnlocksPerThread times.
                        for (int j = 0; j < lockUnlocksPerThread; j++) {
                            long lockId = manager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, finalBlockId, READ);
                            Assert.assertTrue(manager.unlockBlockNoException(lockId));
                        }
                        // Lock the block one last time.
                        manager.lockBlock(BlockLockManagerTest.TEST_SESSION_ID, finalBlockId, READ);
                    }
                });
                t.setUncaughtExceptionHandler(exceptionHandler);
                threads.add(t);
            }
        }
        Collections.shuffle(threads);
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        if (!(failedThreadThrowables.isEmpty())) {
            StringBuilder sb = new StringBuilder("Failed with the following errors:\n");
            for (Throwable failedThreadThrowable : failedThreadThrowables) {
                sb.append(Throwables.getStackTraceAsString(failedThreadThrowable));
            }
            Assert.fail(sb.toString());
        }
        manager.validate();
    }
}

