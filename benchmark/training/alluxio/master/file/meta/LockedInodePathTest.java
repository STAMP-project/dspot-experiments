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


import LockPattern.READ;
import LockPattern.WRITE_EDGE;
import LockPattern.WRITE_INODE;
import alluxio.AlluxioURI;
import alluxio.master.file.meta.InodeTree.LockPattern;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link LockedInodePath}.
 */
public class LockedInodePathTest extends BaseInodeLockingTest {
    private LockedInodePath mPath;

    @Test
    public void pathExistsReadLock() throws Exception {
        AlluxioURI uri = new AlluxioURI("/a/b/c");
        mPath = new LockedInodePath(uri, mInodeStore, mInodeLockManager, mRootDir, LockPattern.READ);
        Assert.assertEquals(uri, mPath.getUri());
        Assert.assertEquals(4, mPath.size());
        mPath.traverse();
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(mFileC, mPath.getInode());
        Assert.assertEquals(mFileC, mPath.getInodeOrNull());
        Assert.assertEquals(mFileC, mPath.getInodeFile());
        Assert.assertEquals(mFileC, mPath.getLastExistingInode());
        Assert.assertEquals(mDirB, mPath.getParentInodeDirectory());
        Assert.assertEquals(mDirB, mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), mPath.getInodeList());
        Assert.assertEquals(4, mPath.getExistingInodeCount());
        Assert.assertEquals(READ, mPath.getLockPattern());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void pathExistsWriteLock() throws Exception {
        mPath = create("/a/b/c", WRITE_INODE);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), mPath.getInodeList());
        Assert.assertEquals(WRITE_INODE, mPath.getLockPattern());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked(mFileC);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void pathExistsWriteEdgeLock() throws Exception {
        mPath = create("/a/b/c", WRITE_EDGE);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(mFileC, mPath.getInode());
        Assert.assertEquals(mFileC, mPath.getInodeOrNull());
        Assert.assertEquals(mFileC, mPath.getInodeFile());
        Assert.assertEquals(mFileC, mPath.getLastExistingInode());
        Assert.assertEquals(mDirB, mPath.getParentInodeDirectory());
        Assert.assertEquals(mDirB, mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), mPath.getInodeList());
        Assert.assertEquals(4, mPath.getExistingInodeCount());
        Assert.assertEquals(WRITE_EDGE, mPath.getLockPattern());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked(mFileC);
    }

    @Test
    public void missingLastReadLock() throws Exception {
        mPath = create("/a/b/missing", READ);
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getLastExistingInode());
        Assert.assertEquals(mDirB, mPath.getParentInodeDirectory());
        Assert.assertEquals(mDirB, mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), mPath.getInodeList());
        Assert.assertEquals(3, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void missingLastWriteLock() throws Exception {
        mPath = create("/a/b/missing", WRITE_INODE);
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getLastExistingInode());
        Assert.assertEquals(mDirB, mPath.getParentInodeDirectory());
        Assert.assertEquals(mDirB, mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), mPath.getInodeList());
        Assert.assertEquals(3, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void missingLastWriteEdgeLock() throws Exception {
        mPath = create("/a/b/missing", WRITE_EDGE);
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getLastExistingInode());
        Assert.assertEquals(mDirB, mPath.getParentInodeDirectory());
        Assert.assertEquals(mDirB, mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirB, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), mPath.getInodeList());
        Assert.assertEquals(3, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked();
        checkIncomingEdgeWriteLocked(mDirB.getId(), "missing");
    }

    @Test
    public void missingMultipleReadLock() throws Exception {
        mPath = create("/a/miss1/miss2", READ);
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertNull(mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirA, mPath.getLastExistingInode());
        Assert.assertEquals(mDirA, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mPath.getInodeList());
        Assert.assertEquals(2, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void missingMultipleWriteEdgeLock() throws Exception {
        mPath = create("/a/miss1/miss2", WRITE_EDGE);
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertNull(mPath.getParentInodeOrNull());
        Assert.assertEquals(mDirA, mPath.getLastExistingInode());
        Assert.assertEquals(mDirA, mPath.getAncestorInode());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA), mPath.getInodeList());
        Assert.assertEquals(2, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
        checkIncomingEdgeWriteLocked(mDirA.getId(), "miss1");
    }

    @Test
    public void readLockRoot() throws Exception {
        mPath = create("/", READ);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(mRootDir, mPath.getInodeOrNull());
        Assert.assertNull(mPath.getParentInodeOrNull());
        Assert.assertEquals(mRootDir, mPath.getLastExistingInode());
        Assert.assertEquals(Arrays.asList(mRootDir), mPath.getInodeList());
        Assert.assertEquals(1, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void writeLockRoot() throws Exception {
        mPath = create("/", WRITE_INODE);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(mRootDir, mPath.getInodeOrNull());
        Assert.assertNull(mPath.getParentInodeOrNull());
        Assert.assertEquals(mRootDir, mPath.getLastExistingInode());
        Assert.assertEquals(Arrays.asList(mRootDir), mPath.getInodeList());
        Assert.assertEquals(1, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked();
        checkOnlyNodesWriteLocked(mRootDir);
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void writeEdgeLockRoot() throws Exception {
        mPath = create("/", WRITE_EDGE);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(mRootDir, mPath.getInodeOrNull());
        Assert.assertNull(mPath.getParentInodeOrNull());
        Assert.assertEquals(mRootDir, mPath.getLastExistingInode());
        Assert.assertEquals(Arrays.asList(mRootDir), mPath.getInodeList());
        Assert.assertEquals(1, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked();
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked();
        checkOnlyIncomingEdgesWriteLocked(mRootDir);
    }

    @Test
    public void removeLastReadLockedInode() throws Exception {
        mPath = create("/a", READ);
        mPath.removeLastInode();
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertEquals(Arrays.asList(mRootDir), mPath.getInodeList());
        Assert.assertEquals(1, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void removeLastWriteEdgeLockedInode() throws Exception {
        mPath = create("/a", WRITE_EDGE);
        mPath.removeLastInode();
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertNull(mPath.getInodeOrNull());
        Assert.assertEquals(Arrays.asList(mRootDir), mPath.getInodeList());
        Assert.assertEquals(1, mPath.getExistingInodeCount());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked(mDirA);
    }

    @Test
    public void removeLastInodeImplicitlyLocked() throws Exception {
        mPath = create("/a", WRITE_EDGE);
        LockedInodePath pathC = mPath.lockDescendant(new AlluxioURI("/a/b/c"), READ);
        Assert.assertTrue(pathC.fullPathExists());
        pathC.removeLastInode();
        Assert.assertFalse(pathC.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), pathC.getInodeList());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked(mDirA);
        pathC.close();
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked(mDirA);
    }

    @Test
    public void addNextFinalInode() throws Exception {
        mPath = create("/a/missing", WRITE_EDGE);
        Assert.assertFalse(mPath.fullPathExists());
        InodeFile missingInode = inodeFile(10, mDirA.getId(), "missing");
        mInodeStore.addChild(mDirA.getId(), missingInode);
        mPath.addNextInode(missingInode);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, missingInode), mPath.getInodeList());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked(missingInode);
    }

    @Test
    public void addNextSecondToLastInode() throws Exception {
        mPath = create("/a/miss1/miss2", WRITE_EDGE);
        Assert.assertFalse(mPath.fullPathExists());
        InodeFile firstMissingInode = inodeFile(10, mDirA.getId(), "miss1");
        mInodeStore.addChild(mDirA.getId(), firstMissingInode);
        mPath.addNextInode(firstMissingInode);
        Assert.assertFalse(mPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, firstMissingInode), mPath.getInodeList());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, firstMissingInode);
        checkOnlyIncomingEdgesWriteLocked();
        // Write lock should be pushed forward when adding a non-final inode.
        checkIncomingEdgeWriteLocked(firstMissingInode.getId(), "miss2");
    }

    @Test
    public void downgradeWriteEdgeToRead() throws Exception {
        mPath = create("/a/b/c", WRITE_EDGE);
        mPath.downgradeToPattern(READ);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), mPath.getInodeList());
        Assert.assertEquals(READ, mPath.getLockPattern());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void downgradeWriteEdgeToWriteInode() throws Exception {
        mPath = create("/a/b/c", WRITE_EDGE);
        mPath.downgradeToPattern(WRITE_INODE);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), mPath.getInodeList());
        Assert.assertEquals(WRITE_INODE, mPath.getLockPattern());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked(mFileC);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void downgradeWriteInodeToReadInode() throws Exception {
        mPath = create("/a/b/c", WRITE_INODE);
        mPath.downgradeToPattern(READ);
        Assert.assertTrue(mPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), mPath.getInodeList());
        Assert.assertEquals(READ, mPath.getLockPattern());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockChildReadToWriteEdge() throws Exception {
        mPath = create("/a", READ);
        LockedInodePath childPath = mPath.lockChild(mDirB, WRITE_EDGE);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), childPath.getInodeList());
        Assert.assertEquals(WRITE_EDGE, childPath.getLockPattern());
        Assert.assertTrue(childPath.fullPathExists());
        Assert.assertEquals(mDirB, childPath.getInode());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked(mDirB);
        childPath.close();
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockChildReadToWriteInode() throws Exception {
        mPath = create("/a", READ);
        LockedInodePath childPath = mPath.lockChild(mDirB, WRITE_INODE);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), childPath.getInodeList());
        Assert.assertEquals(WRITE_INODE, childPath.getLockPattern());
        Assert.assertTrue(childPath.fullPathExists());
        Assert.assertEquals(mDirB, childPath.getInode());
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked(mDirB);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked();
        childPath.close();
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockChildWriteInodeToWriteEdge() throws Exception {
        mPath = create("/a", WRITE_INODE);
        LockedInodePath childPath = mPath.lockChild(mDirB, WRITE_EDGE);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), childPath.getInodeList());
        Assert.assertEquals(WRITE_EDGE, childPath.getLockPattern());
        Assert.assertTrue(childPath.fullPathExists());
        Assert.assertEquals(mDirB, childPath.getInode());
        // No new locks are taken since we already have a write lock
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked(mDirA);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
        childPath.close();
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked(mDirA);
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockChildReadToRead() throws Exception {
        mPath = create("/a", READ);
        LockedInodePath childPath = mPath.lockChild(mDirB, READ);
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB), childPath.getInodeList());
        Assert.assertEquals(READ, childPath.getLockPattern());
        Assert.assertTrue(childPath.fullPathExists());
        Assert.assertEquals(mDirB, childPath.getInode());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked();
        childPath.close();
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockChildMultipleReadExtensions() throws Exception {
        mPath = create("/a", READ);
        LockedInodePath childPath1 = mPath.lockChild(mDirB, READ);
        LockedInodePath childPath2 = childPath1.lockChild(mFileC, READ);
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB, mFileC);
        checkOnlyIncomingEdgesWriteLocked();
        childPath2.close();
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked();
        childPath1.close();
        checkOnlyNodesReadLocked(mRootDir, mDirA);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockDescendantReadToWriteEdge() throws Exception {
        mPath = create("/", READ);
        LockedInodePath childPath = mPath.lockDescendant(new AlluxioURI("/a/b/c"), WRITE_EDGE);
        Assert.assertTrue(childPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), childPath.getInodeList());
        checkOnlyNodesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir, mDirA, mDirB);
        checkOnlyIncomingEdgesWriteLocked(mFileC);
        childPath.close();
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockDescendantWriteEdgeToWriteEdge() throws Exception {
        mPath = create("/", WRITE_EDGE);
        LockedInodePath childPath = mPath.lockDescendant(new AlluxioURI("/a/b/c"), WRITE_EDGE);
        Assert.assertTrue(childPath.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir, mDirA, mDirB, mFileC), childPath.getInodeList());
        checkOnlyNodesReadLocked();
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked();
        checkOnlyIncomingEdgesWriteLocked(mRootDir);
        childPath.close();
        checkOnlyNodesReadLocked();
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked();
        checkOnlyIncomingEdgesWriteLocked(mRootDir);
    }

    @Test
    public void lockFinalEdgeWrite() throws Exception {
        mInodeStore.removeChild(mRootDir.getId(), "a");
        mPath = create("/a", READ);
        mPath.traverse();
        LockedInodePath writeLocked = mPath.lockFinalEdgeWrite();
        Assert.assertFalse(writeLocked.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir), writeLocked.getInodeList());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked(mDirA);
        writeLocked.close();
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked();
    }

    @Test
    public void lockFinalEdgeWriteAlreadyLocked() throws Exception {
        mInodeStore.removeChild(mRootDir.getId(), "a");
        mPath = create("/a", WRITE_EDGE);
        LockedInodePath writeLocked = mPath.lockFinalEdgeWrite();
        Assert.assertFalse(writeLocked.fullPathExists());
        Assert.assertEquals(Arrays.asList(mRootDir), writeLocked.getInodeList());
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked(mDirA);
        writeLocked.close();
        checkOnlyNodesReadLocked(mRootDir);
        checkOnlyNodesWriteLocked();
        checkOnlyIncomingEdgesReadLocked(mRootDir);
        checkOnlyIncomingEdgesWriteLocked(mDirA);
    }
}

