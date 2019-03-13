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
package alluxio.master.metastore;


import alluxio.master.file.meta.Inode;
import alluxio.master.file.meta.InodeLockManager;
import alluxio.master.file.meta.MutableInode;
import alluxio.master.file.meta.MutableInodeDirectory;
import alluxio.master.file.meta.MutableInodeFile;
import alluxio.master.metastore.InodeStore.WriteBatch;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class InodeStoreTest {
    private static final int CACHE_SIZE = 16;

    private final MutableInodeDirectory mRoot = InodeStoreTest.inodeDir(0, (-1), "");

    private final InodeStore mStore;

    private final InodeLockManager mLockManager;

    public InodeStoreTest(Function<InodeLockManager, InodeStore> store) {
        mLockManager = new InodeLockManager();
        mStore = store.apply(mLockManager);
    }

    @Test
    public void get() {
        writeInode(mRoot);
        Assert.assertEquals(Inode.wrap(mRoot), mStore.get(0).get());
    }

    @Test
    public void getMutable() {
        writeInode(mRoot);
        Assert.assertEquals(mRoot, mStore.getMutable(0).get());
    }

    @Test
    public void getChild() {
        MutableInodeFile child = InodeStoreTest.inodeFile(1, 0, "child");
        writeInode(mRoot);
        writeInode(child);
        writeEdge(mRoot, child);
        Assert.assertEquals(Inode.wrap(child), mStore.getChild(mRoot, child.getName()).get());
    }

    @Test
    public void remove() {
        writeInode(mRoot);
        removeInode(mRoot);
    }

    @Test
    public void removeChild() {
        MutableInodeFile child = InodeStoreTest.inodeFile(1, 0, "child");
        writeInode(mRoot);
        writeInode(child);
        writeEdge(mRoot, child);
        removeParentEdge(child);
        Assert.assertFalse(mStore.getChild(mRoot, child.getName()).isPresent());
    }

    @Test
    public void updateInode() {
        writeInode(mRoot);
        MutableInodeDirectory mutableRoot = mStore.getMutable(mRoot.getId()).get().asDirectory();
        mutableRoot.setLastModificationTimeMs(163, true);
        writeInode(mutableRoot);
        Assert.assertEquals(163, mStore.get(mRoot.getId()).get().getLastModificationTimeMs());
    }

    @Test
    public void batchWrite() {
        Assume.assumeTrue(mStore.supportsBatchWrite());
        WriteBatch batch = mStore.createWriteBatch();
        MutableInodeFile child = InodeStoreTest.inodeFile(1, 0, "child");
        batch.writeInode(child);
        batch.addChild(mRoot.getId(), child.getName(), child.getId());
        batch.commit();
        Assert.assertEquals(Inode.wrap(child), mStore.get(child.getId()).get());
        Assert.assertEquals(Inode.wrap(child), mStore.getChild(mRoot.getId(), child.getName()).get());
        batch = mStore.createWriteBatch();
        batch.removeInode(child.getId());
        batch.removeChild(mRoot.getId(), child.getName());
        batch.commit();
        Assert.assertEquals(Optional.empty(), mStore.get(child.getId()));
        Assert.assertEquals(Optional.empty(), mStore.getChild(mRoot.getId(), child.getName()));
    }

    @Test
    public void addRemoveAddList() {
        writeInode(mRoot);
        for (int i = 1; i < 10; i++) {
            MutableInodeFile file = InodeStoreTest.inodeFile(i, 0, ("file" + i));
            writeInode(file);
            writeEdge(mRoot, file);
        }
        Assert.assertEquals(9, Iterables.size(mStore.getChildren(mRoot)));
        for (Inode child : mStore.getChildren(mRoot)) {
            MutableInode<?> childMut = mStore.getMutable(child.getId()).get();
            removeParentEdge(childMut);
            removeInode(childMut);
        }
        for (int i = 1; i < 10; i++) {
            MutableInodeFile file = InodeStoreTest.inodeFile(i, 0, ("file" + i));
            writeInode(file);
            writeEdge(mRoot, file);
        }
        Assert.assertEquals(9, Iterables.size(mStore.getChildren(mRoot)));
    }

    @Test
    public void repeatedAddRemoveAndList() {
        MutableInodeFile child = InodeStoreTest.inodeFile(1, 0, "child");
        writeInode(mRoot);
        writeInode(child);
        writeEdge(mRoot, child);
        for (int i = 0; i < 3; i++) {
            removeParentEdge(child);
            writeEdge(mRoot, child);
        }
        List<MutableInodeDirectory> dirs = new ArrayList<>();
        for (int i = 5; i < (5 + (InodeStoreTest.CACHE_SIZE)); i++) {
            String childName = "child" + i;
            MutableInodeDirectory dir = InodeStoreTest.inodeDir(i, 0, childName);
            dirs.add(dir);
            writeInode(dir);
            writeEdge(mRoot, dir);
            mStore.getChild(mRoot, childName);
        }
        for (MutableInodeDirectory dir : dirs) {
            removeParentEdge(dir);
        }
        Assert.assertEquals(1, Iterables.size(mStore.getChildren(mRoot)));
    }

    @Test
    public void manyOperations() {
        writeInode(mRoot);
        MutableInodeDirectory curr = mRoot;
        List<Long> fileIds = new ArrayList<>();
        long numDirs = 100;
        // Create 100 nested directories, each containing a file.
        for (int i = 1; i < numDirs; i++) {
            MutableInodeDirectory dir = InodeStoreTest.inodeDir(i, curr.getId(), ("dir" + i));
            MutableInodeFile file = InodeStoreTest.inodeFile((i + 1000), i, ("file" + i));
            fileIds.add(file.getId());
            writeInode(dir);
            writeInode(file);
            writeEdge(curr, dir);
            writeEdge(dir, file);
            curr = dir;
        }
        // Check presence and delete files.
        for (int i = 0; i < numDirs; i++) {
            Assert.assertTrue(mStore.get(i).isPresent());
        }
        for (Long i : fileIds) {
            Assert.assertTrue(mStore.get(i).isPresent());
            Inode inode = mStore.get(i).get();
            removeInode(inode);
            removeParentEdge(inode);
            Assert.assertFalse(mStore.get(i).isPresent());
            Assert.assertFalse(mStore.getChild(inode.getParentId(), inode.getName()).isPresent());
        }
        long middleDir = numDirs / 2;
        // Rename a directory
        MutableInodeDirectory dir = mStore.getMutable(middleDir).get().asDirectory();
        removeParentEdge(dir);
        writeEdge(mRoot, dir);
        dir.setParentId(mRoot.getId());
        writeInode(dir);
        Optional<Inode> renamed = mStore.getChild(mRoot, dir.getName());
        Assert.assertTrue(renamed.isPresent());
        Assert.assertTrue(mStore.getChild(renamed.get().asDirectory(), ("dir" + (middleDir + 1))).isPresent());
        Assert.assertEquals(0, Iterables.size(mStore.getChildren(mStore.get((middleDir - 1)).get().asDirectory())));
    }
}

