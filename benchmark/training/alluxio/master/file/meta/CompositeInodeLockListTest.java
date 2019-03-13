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
 * Unit tests for {@link CompositeInodeLockList}.
 *
 * There is coverage for the working on top of an empty base lock list in {@link InodeLockListTest}.
 */
public class CompositeInodeLockListTest extends BaseInodeLockingTest {
    private InodeLockList mBase = new InodeLockList(mInodeLockManager);

    private CompositeInodeLockList mComposite;

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void unlockOnlyExtension() {
        mBase.lockRootEdge(READ);
        mBase.lockInode(mRootDir, READ);
        mBase.lockEdge(mDirA.getName(), READ);
        mComposite = new CompositeInodeLockList(mBase);
        mComposite.lockInode(mDirA, READ);
        mComposite.lockEdge(mDirB.getName(), READ);
        mComposite.lockInode(mDirB, WRITE);
        mComposite.close();
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void extendFromEdge() {
        mBase.lockRootEdge(READ);
        mBase.lockInode(mRootDir, READ);
        mBase.lockEdge(mDirA.getName(), READ);
        mComposite = new CompositeInodeLockList(mBase);
        Assert.assertEquals(READ, mComposite.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir), mComposite.getLockedInodes());
        mComposite.lockInode(mDirA, READ);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mComposite.getLockedInodes());
        Assert.assertEquals(2, mComposite.numLockedInodes());
        Assert.assertFalse(mComposite.isEmpty());
        Assert.assertEquals(mRootDir, mComposite.get(0));
        Assert.assertEquals(mDirA, mComposite.get(1));
        mComposite.lockEdge(mDirB.getName(), WRITE);
        Assert.assertEquals(WRITE, mComposite.getLockMode());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked(mDirB);
    }

    @Test
    public void extendFromInode() {
        mBase.lockRootEdge(READ);
        mBase.lockInode(mRootDir, READ);
        mComposite = new CompositeInodeLockList(mBase);
        Assert.assertEquals(READ, mComposite.getLockMode());
        Assert.assertEquals(Arrays.asList(mRootDir), mComposite.getLockedInodes());
        mComposite.lockEdge(mDirA.getName(), READ);
        mComposite.lockInode(mDirA, READ);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mComposite.getLockedInodes());
        Assert.assertEquals(2, mComposite.numLockedInodes());
        Assert.assertFalse(mComposite.isEmpty());
        Assert.assertEquals(mRootDir, mComposite.get(0));
        mComposite.lockEdge(mDirB.getName(), WRITE);
        Assert.assertEquals(WRITE, mComposite.getLockMode());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked(mDirB);
    }

    @Test
    public void extendFromWriteLocked() {
        mBase.lockRootEdge(WRITE);
        mComposite = new CompositeInodeLockList(mBase);
        Assert.assertEquals(WRITE, mComposite.getLockMode());
        checkOnlyNodesReadLocked();
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked();
        checkOnlyIncomingEdgesWriteLocked(mRootDir);
    }

    @Test
    public void doubleWriteLock() {
        mBase.lockRootEdge(WRITE);
        mComposite = new CompositeInodeLockList(mBase);
        mThrown.expect(IllegalStateException.class);
        mComposite.lockInode(mRootDir, WRITE);
    }

    @Test
    public void unlockIntoBase() {
        mBase.lockRootEdge(WRITE);
        mComposite = new CompositeInodeLockList(mBase);
        mThrown.expect(IllegalStateException.class);
        mComposite.unlockLastEdge();
    }
}

