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
package alluxio.master.file.meta;


import LockMode.READ;
import LockMode.WRITE;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link InodeLockList}.
 */
public class InodeLockListTest extends BaseInodeLockingTest {
    private InodeLockList mLockList = new InodeLockList(mInodeLockManager);

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void lockSimple() {
        mLockList.lockRootEdge(READ);
        mLockList.lockInode(mRootDir, READ);
        mLockList.lockEdge(mDirA.getName(), READ);
        mLockList.lockInode(mDirA, READ);
        Assert.assertEquals(mLockList.getLockMode(), READ);
        Assert.assertTrue(mLockList.endsInInode());
        Assert.assertEquals(2, mLockList.numLockedInodes());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mLockList.getLockedInodes());
        mLockList.lockEdge(mDirB.getName(), WRITE);
        Assert.assertEquals(mLockList.getLockMode(), WRITE);
        Assert.assertFalse(mLockList.endsInInode());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked(mDirB);
    }

    @Test
    public void pushWriteLockedEdge() {
        mLockList.lockRootEdge(WRITE);
        Assert.assertEquals(WRITE, mLockList.getLockMode());
        Assert.assertTrue(mLockList.getLockedInodes().isEmpty());
        mLockList.pushWriteLockedEdge(mRootDir, mDirA.getName());
        Assert.assertEquals(WRITE, mLockList.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir), mLockList.getLockedInodes());
        mLockList.pushWriteLockedEdge(mDirA, mDirB.getName());
        Assert.assertEquals(WRITE, mLockList.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mLockList.getLockedInodes());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked(mDirB);
    }

    @Test
    public void lockAndUnlockLast() {
        mLockList.lockRootEdge(READ);
        mLockList.lockInode(mRootDir, READ);
        mLockList.lockEdge(mDirA.getName(), READ);
        mLockList.lockInode(mDirA, WRITE);
        mLockList.unlockLastInode();
        Assert.assertEquals(Arrays.asList(mRootDir), mLockList.getLockedInodes());
        Assert.assertEquals(READ, mLockList.getLockMode());
        mLockList.unlockLastEdge();
        Assert.assertEquals(READ, mLockList.getLockMode());
        mLockList.lockEdge(mDirA.getName(), READ);
        mLockList.lockInode(mDirA, READ);
        mLockList.unlockLastInode();
        Assert.assertEquals(Arrays.asList(mRootDir), mLockList.getLockedInodes());
        Assert.assertEquals(READ, mLockList.getLockMode());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void unlockLastAll() {
        mLockList.lockRootEdge(READ);
        mLockList.lockInode(mRootDir, READ);
        for (Inode inode : Arrays.asList(mDirA, mDirB, mFileC)) {
            mLockList.lockEdge(inode.getName(), READ);
            mLockList.lockInode(inode, READ);
        }
        for (int i = 0; i < 3; i++) {
            mLockList.unlockLastInode();
            mLockList.unlockLastEdge();
        }
        mLockList.unlockLastInode();
        mLockList.unlockLastEdge();
        Assert.assertEquals(0, mLockList.numLockedInodes());
        Assert.assertEquals(READ, mLockList.getLockMode());
        mLockList.lockRootEdge(READ);
        mLockList.lockInode(mRootDir, WRITE);
        Assert.assertEquals(Arrays.asList(mRootDir), mLockList.getLockedInodes());
        checkOnlyNodesReadLocked();
        checkOnlyNodesWriteLocked(mRootDir);
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void downgradeLastInode() {
        mLockList.lockRootEdge(READ);
        mLockList.lockInode(mRootDir, READ);
        mLockList.lockEdge(mDirA.getName(), READ);
        mLockList.lockInode(mDirA, WRITE);
        mLockList.downgradeLastInode();
        Assert.assertEquals(READ, mLockList.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mLockList.getLockedInodes());
        mLockList.unlockLastInode();
        mLockList.lockInode(mDirA, WRITE);
        Assert.assertEquals(WRITE, mLockList.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mLockList.getLockedInodes());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked(mDirA);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void downgradeLastInodeRoot() {
        mLockList.lockRootEdge(READ);
        mLockList.lockInode(mRootDir, WRITE);
        mLockList.downgradeLastInode();
        Assert.assertEquals(READ, mLockList.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir), mLockList.getLockedInodes());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void downgradeLastEdge() {
        mLockList.lockRootEdge(WRITE);
        mLockList.downgradeLastEdge();
        Assert.assertEquals(READ, mLockList.getLockMode());
        mLockList.lockInode(mRootDir, READ);
        mLockList.lockEdge(mDirA.getName(), WRITE);
        mLockList.downgradeLastEdge();
        Assert.assertEquals(READ, mLockList.getLockMode());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void downgradeEdgeToInode() {
        mLockList.lockRootEdge(WRITE);
        mLockList.downgradeEdgeToInode(mRootDir, READ);
        Assert.assertEquals(Arrays.asList(mRootDir), mLockList.getLockedInodes());
        Assert.assertEquals(READ, mLockList.getLockMode());
        mLockList.lockEdge(mDirA.getName(), WRITE);
        mLockList.downgradeEdgeToInode(mDirA, WRITE);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mLockList.getLockedInodes());
        Assert.assertEquals(WRITE, mLockList.getLockMode());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked(mDirA);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void doubleWriteLock() {
        mLockList.lockRootEdge(WRITE);
        mThrown.expect(IllegalStateException.class);
        mLockList.lockInode(mRootDir, WRITE);
    }
}

