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
package alluxio.master.metastore.caching;


import alluxio.master.file.contexts.CreateDirectoryContext;
import alluxio.master.file.contexts.CreateFileContext;
import alluxio.master.file.meta.Inode;
import alluxio.master.file.meta.MutableInodeDirectory;
import alluxio.master.file.meta.MutableInodeFile;
import alluxio.master.metastore.InodeStore;
import alluxio.util.CommonUtils;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CachingInodeStoreTest {
    private static final long CACHE_SIZE = 20;

    private static final long TEST_INODE_ID = 5;

    private static final MutableInodeDirectory TEST_INODE_DIR = MutableInodeDirectory.create(CachingInodeStoreTest.TEST_INODE_ID, 0, "name", CreateDirectoryContext.defaults());

    private InodeStore mBackingStore;

    private CachingInodeStore mStore;

    @Test
    public void cacheGetMutable() {
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(CachingInodeStoreTest.TEST_INODE_DIR, mStore.getMutable(CachingInodeStoreTest.TEST_INODE_ID).get());
        }
        verifyNoBackingStoreReads();
    }

    @Test
    public void cacheGet() {
        Inode testInode = Inode.wrap(CachingInodeStoreTest.TEST_INODE_DIR);
        for (int i = 0; i < ((CachingInodeStoreTest.CACHE_SIZE) / 2); i++) {
            Assert.assertEquals(testInode, mStore.get(CachingInodeStoreTest.TEST_INODE_ID).get());
        }
        verifyNoBackingStoreReads();
    }

    @Test
    public void cacheGetMany() {
        for (long inodeId = 1; inodeId < ((CachingInodeStoreTest.CACHE_SIZE) * 2); inodeId++) {
            createInodeDir(inodeId, 0);
        }
        for (int i = 0; i < 30; i++) {
            for (long inodeId = 1; inodeId < ((CachingInodeStoreTest.CACHE_SIZE) * 2); inodeId++) {
                Assert.assertTrue(mStore.get(inodeId).isPresent());
            }
        }
        // The workload is read-only, so we shouldn't need to write each inode to the backing store more
        // than once.
        Mockito.verify(mBackingStore, Mockito.atMost(((((int) (CachingInodeStoreTest.CACHE_SIZE)) * 2) + 1))).writeInode(ArgumentMatchers.any());
    }

    @Test
    public void removeInodeStaysRemoved() {
        mStore.remove(CachingInodeStoreTest.TEST_INODE_DIR);
        Assert.assertEquals(Optional.empty(), mStore.get(CachingInodeStoreTest.TEST_INODE_DIR.getId()));
    }

    @Test
    public void reflectWrite() {
        MutableInodeDirectory updated = MutableInodeDirectory.create(CachingInodeStoreTest.TEST_INODE_ID, 10, "newName", CreateDirectoryContext.defaults());
        mStore.writeInode(updated);
        Assert.assertEquals("newName", mStore.get(CachingInodeStoreTest.TEST_INODE_ID).get().getName());
    }

    @Test
    public void cacheGetChild() {
        MutableInodeFile child = MutableInodeFile.create(0, CachingInodeStoreTest.TEST_INODE_ID, "child", 0, CreateFileContext.defaults());
        mStore.writeInode(child);
        mStore.addChild(CachingInodeStoreTest.TEST_INODE_ID, child);
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(mStore.getChild(CachingInodeStoreTest.TEST_INODE_DIR, "child").isPresent());
        }
        verifyNoBackingStoreReads();
    }

    @Test
    public void listChildrenOverCacheSize() {
        for (long inodeId = 10; inodeId < (10 + ((CachingInodeStoreTest.CACHE_SIZE) * 2)); inodeId++) {
            MutableInodeDirectory dir = createInodeDir(inodeId, 0);
            mStore.addChild(0, dir);
        }
        Assert.assertEquals(((CachingInodeStoreTest.CACHE_SIZE) * 2), Iterables.size(mStore.getChildren(0L)));
    }

    @Test
    public void cacheGetChildMany() {
        for (long inodeId = 1; inodeId < ((CachingInodeStoreTest.CACHE_SIZE) * 2); inodeId++) {
            MutableInodeFile child = MutableInodeFile.create(0, 0, ("child" + inodeId), 0, CreateFileContext.defaults());
            mStore.writeInode(child);
            mStore.addChild(0, child);
        }
        for (int i = 0; i < 1000; i++) {
            for (long inodeId = 1; inodeId < ((CachingInodeStoreTest.CACHE_SIZE) * 2); inodeId++) {
                Assert.assertTrue(mStore.getChild(0L, ("child" + inodeId)).isPresent());
            }
        }
        // The workload is read-only, so we shouldn't need to write each edge to the backing store more
        // than once.
        Mockito.verify(mBackingStore, Mockito.atMost((((int) (CachingInodeStoreTest.CACHE_SIZE)) * 2))).addChild(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    public void cacheGetChildrenInodeLookups() {
        List<Inode> children = new ArrayList<>();
        for (int id = 100; id < 110; id++) {
            MutableInodeFile child = MutableInodeFile.create(id, CachingInodeStoreTest.TEST_INODE_ID, ("child" + id), 0, CreateFileContext.defaults());
            children.add(Inode.wrap(child));
            mStore.writeNewInode(child);
            mStore.addChild(CachingInodeStoreTest.TEST_INODE_ID, child);
        }
        Assert.assertEquals(10, Iterables.size(mStore.getChildren(CachingInodeStoreTest.TEST_INODE_DIR)));
        verifyNoBackingStoreReads();
    }

    @Test
    public void eviction() {
        for (int id = 100; id < (100 + ((CachingInodeStoreTest.CACHE_SIZE) * 2)); id++) {
            MutableInodeFile child = MutableInodeFile.create(id, CachingInodeStoreTest.TEST_INODE_ID, ("child" + id), 0, CreateFileContext.defaults());
            mStore.writeNewInode(child);
            mStore.addChild(CachingInodeStoreTest.TEST_INODE_ID, child);
        }
        for (int id = 100; id < (100 + ((CachingInodeStoreTest.CACHE_SIZE) * 2)); id++) {
            Assert.assertTrue(mStore.getChild(CachingInodeStoreTest.TEST_INODE_DIR, ("child" + id)).isPresent());
        }
        Mockito.verify(mBackingStore, Mockito.atLeastOnce()).getMutable(ArgumentMatchers.anyLong());
    }

    @Test
    public void edgeIndexTest() throws Exception {
        // Run many concurrent operations, then check that the edge cache's indices are accurate.
        long endTimeMs = (System.currentTimeMillis()) + 200;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<MutableInodeDirectory> dirs = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            MutableInodeDirectory dir = createInodeDir(i, 0);
            dirs.add(dir);
            mStore.addChild(CachingInodeStoreTest.TEST_INODE_ID, dir);
        }
        AtomicInteger operations = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int numThreads = 10;
        executor.invokeAll(Collections.nCopies(numThreads, () -> {
            while (((operations.get()) < 10000) || ((System.currentTimeMillis()) < endTimeMs)) {
                // Sometimes add, sometimes delete.
                if (random.nextBoolean()) {
                    MutableInodeDirectory dir = createInodeDir(random.nextLong(10, 15), random.nextLong(1, 5));
                    mStore.addChild(dir.getParentId(), dir);
                } else {
                    mStore.removeChild(dirs.get(random.nextInt(dirs.size())).getId(), Long.toString(random.nextLong(10, 15)));
                }
                operations.incrementAndGet();
                Assert.assertTrue(((mStore.mEdgeCache.mMap.size()) <= ((CachingInodeStoreTest.CACHE_SIZE) + numThreads)));
            } 
            return null;
        }));
        CommonUtils.waitFor("eviction thread to finish", () -> mStore.mEdgeCache.mEvictionThread.mIsSleeping);
        mStore.mEdgeCache.verifyIndices();
    }

    @Test
    public void listingCacheManyDirsEviction() throws Exception {
        for (int i = 1; i < ((CachingInodeStoreTest.CACHE_SIZE) * 3); i++) {
            createInodeDir(i, CachingInodeStoreTest.TEST_INODE_ID);
        }
        Assert.assertFalse(mStore.mListingCache.getCachedChildIds(CachingInodeStoreTest.TEST_INODE_ID).isPresent());
    }

    @Test
    public void listingCacheBigDirEviction() throws Exception {
        MutableInodeDirectory bigDir = createInodeDir(1, 0);
        long dirSize = CachingInodeStoreTest.CACHE_SIZE;
        for (int i = 10; i < (10 + dirSize); i++) {
            mStore.addChild(bigDir.getId(), createInodeDir(i, bigDir.getId()));
        }
        // Cache the large directory
        Assert.assertEquals(dirSize, Iterables.size(mStore.getChildIds(bigDir.getId())));
        // Perform another operation to trigger eviction
        mStore.addChild(bigDir.getId(), createInodeDir(10000, bigDir.getId()));
        Assert.assertFalse(mStore.mListingCache.getCachedChildIds(CachingInodeStoreTest.TEST_INODE_ID).isPresent());
    }

    @Test(timeout = 10000)
    public void listingCacheAddRemoveEdges() throws Exception {
        // Perform operations including adding and removing many files within a directory. This test has
        // rooted out some bugs related to cache weight tracking.
        MutableInodeDirectory bigDir = createInodeDir(1, 0);
        mStore.writeNewInode(bigDir);
        for (int i = 1000; i < (1000 + (CachingInodeStoreTest.CACHE_SIZE)); i++) {
            MutableInodeDirectory subDir = createInodeDir(i, bigDir.getId());
            mStore.addChild(bigDir.getId(), subDir);
            mStore.removeChild(bigDir.getId(), subDir.getName());
        }
        List<MutableInodeDirectory> inodes = new ArrayList<>();
        for (int i = 10; i < (10 + ((CachingInodeStoreTest.CACHE_SIZE) / 2)); i++) {
            MutableInodeDirectory otherDir = createInodeDir(i, 0);
            inodes.add(otherDir);
            mStore.writeNewInode(otherDir);
        }
        for (MutableInodeDirectory inode : inodes) {
            for (int i = 0; i < 10; i++) {
                Assert.assertEquals(0, Iterables.size(mStore.getChildIds(inode.getId())));
            }
            Mockito.verify(mBackingStore, Mockito.times(0)).getChildIds(inode.getId());
        }
    }

    @Test
    public void flushToBackingStore() throws Exception {
        for (long inodeId = 10; inodeId < (10 + ((CachingInodeStoreTest.CACHE_SIZE) / 2)); inodeId++) {
            MutableInodeDirectory dir = createInodeDir(inodeId, 0);
            mStore.addChild(0, dir);
        }
        Assert.assertEquals(0, Iterables.size(mBackingStore.getChildren(0L)));
        mStore.mEdgeCache.flush();
        mStore.mInodeCache.flush();
        Assert.assertEquals(((CachingInodeStoreTest.CACHE_SIZE) / 2), Iterables.size(mBackingStore.getChildren(0L)));
    }
}

