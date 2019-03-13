/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.objectbox;


import io.objectbox.exception.DbException;
import java.io.File;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;


public class BoxStoreTest extends AbstractObjectBoxTest {
    @Test
    public void testUnalignedMemoryAccess() {
        BoxStore.testUnalignedMemoryAccess();
    }

    @Test
    public void testClose() {
        Assert.assertFalse(store.isClosed());
        store.close();
        Assert.assertTrue(store.isClosed());
        // Double close should be fine
        store.close();
    }

    @Test
    public void testEmptyTransaction() {
        Transaction transaction = store.beginTx();
        transaction.commit();
    }

    @Test
    public void testSameBox() {
        Box<TestEntity> box1 = store.boxFor(TestEntity.class);
        Box<TestEntity> box2 = store.boxFor(TestEntity.class);
        Assert.assertSame(box1, box2);
    }

    @Test(expected = RuntimeException.class)
    public void testBoxForUnknownEntity() {
        store.boxFor(getClass());
    }

    @Test
    public void testRegistration() {
        Assert.assertEquals("TestEntity", store.getDbName(TestEntity.class));
        Assert.assertEquals(TestEntity.class, store.getEntityInfo(TestEntity.class).getEntityClass());
    }

    @Test
    public void testCloseThreadResources() {
        Box<TestEntity> box = store.boxFor(TestEntity.class);
        Cursor<TestEntity> reader = box.getReader();
        box.releaseReader(reader);
        Cursor<TestEntity> reader2 = box.getReader();
        box.releaseReader(reader2);
        Assert.assertSame(reader, reader2);
        store.closeThreadResources();
        Cursor<TestEntity> reader3 = box.getReader();
        box.releaseReader(reader3);
        Assert.assertNotSame(reader, reader3);
    }

    @Test(expected = DbException.class)
    public void testPreventTwoBoxStoresWithSameFileOpenend() {
        createBoxStore();
    }

    @Test
    public void testOpenSameBoxStoreAfterClose() {
        store.close();
        createBoxStore();
    }

    @Test
    public void testOpenTwoBoxStoreTwoFiles() {
        File boxStoreDir2 = new File(((boxStoreDir.getAbsolutePath()) + "-2"));
        BoxStoreBuilder builder = new BoxStoreBuilder(createTestModel(false)).directory(boxStoreDir2);
        builder.entity(new TestEntity_());
    }

    @Test
    public void testDeleteAllFiles() {
        closeStoreForTest();
    }

    @Test
    public void testDeleteAllFiles_staticDir() {
        closeStoreForTest();
        File boxStoreDir2 = new File(((boxStoreDir.getAbsolutePath()) + "-2"));
        BoxStoreBuilder builder = new BoxStoreBuilder(createTestModel(false)).directory(boxStoreDir2);
        BoxStore store2 = builder.build();
        store2.close();
        Assert.assertTrue(boxStoreDir2.exists());
        Assert.assertTrue(BoxStore.deleteAllFiles(boxStoreDir2));
        Assert.assertFalse(boxStoreDir2.exists());
    }

    @Test
    public void testDeleteAllFiles_baseDirName() {
        closeStoreForTest();
        File basedir = new File("test-base-dir");
        String name = "mydb";
        basedir.mkdir();
        Assert.assertTrue(basedir.isDirectory());
        File dbDir = new File(basedir, name);
        Assert.assertFalse(dbDir.exists());
        BoxStoreBuilder builder = new BoxStoreBuilder(createTestModel(false)).baseDirectory(basedir).name(name);
        BoxStore store2 = builder.build();
        store2.close();
        Assert.assertTrue(dbDir.exists());
        Assert.assertTrue(BoxStore.deleteAllFiles(basedir, name));
        Assert.assertFalse(dbDir.exists());
        Assert.assertTrue(basedir.delete());
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteAllFiles_openStore() {
        BoxStore.deleteAllFiles(boxStoreDir);
    }

    @Test
    public void testCallInReadTxWithRetry() {
        final int[] countHolder = new int[]{ 0 };
        String value = store.callInReadTxWithRetry(createTestCallable(countHolder), 5, 0, true);
        Assert.assertEquals("42", value);
        Assert.assertEquals(5, countHolder[0]);
    }

    @Test(expected = DbException.class)
    public void testCallInReadTxWithRetry_fail() {
        final int[] countHolder = new int[]{ 0 };
        store.callInReadTxWithRetry(createTestCallable(countHolder), 4, 0, true);
    }

    @Test
    public void testCallInReadTxWithRetry_callback() {
        closeStoreForTest();
        final int[] countHolder = new int[]{ 0 };
        final int[] countHolderCallback = new int[]{ 0 };
        BoxStoreBuilder builder = new BoxStoreBuilder(createTestModel(false)).directory(boxStoreDir).failedReadTxAttemptCallback(new TxCallback() {
            @Override
            public void txFinished(@Nullable
            Object result, @Nullable
            Throwable error) {
                Assert.assertNotNull(error);
                (countHolderCallback[0])++;
            }
        });
        store = builder.build();
        String value = store.callInReadTxWithRetry(createTestCallable(countHolder), 5, 0, true);
        Assert.assertEquals("42", value);
        Assert.assertEquals(5, countHolder[0]);
        Assert.assertEquals(4, countHolderCallback[0]);
    }
}

