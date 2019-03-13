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
package alluxio.collections;


import LockMode.READ;
import alluxio.resource.LockResource;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the {@link LockCache} class.
 */
public class LockCacheTest {
    private LockCache<Integer> mCache;

    private static final int MAX_SIZE = 16;

    @Test(timeout = 1000)
    public void insertValueTest() {
        int highWaterMark = mCache.getSoftLimit();
        for (int i = 0; i < highWaterMark; i++) {
            Assert.assertEquals(i, mCache.size());
            try (LockResource resource = mCache.get(i, READ)) {
                Assert.assertTrue(mCache.containsKey(i));
                Assert.assertTrue(((mCache.size()) < (LockCacheTest.MAX_SIZE)));
            }
        }
        // it should be full now
        for (int i = highWaterMark; i < (2 * (LockCacheTest.MAX_SIZE)); i++) {
            try (LockResource resource = mCache.get(i, READ)) {
                Assert.assertTrue(mCache.containsKey(i));
                Assert.assertTrue(((mCache.size()) <= (LockCacheTest.MAX_SIZE)));
            }
        }
    }

    @Test(timeout = 1000)
    public void parallelInsertTest() throws InterruptedException {
        Thread t1 = getKeys(0, ((LockCacheTest.MAX_SIZE) * 2), 4);
        Thread t2 = getKeys(0, ((LockCacheTest.MAX_SIZE) * 2), 4);
        Thread t3 = getKeys(((LockCacheTest.MAX_SIZE) * 2), ((LockCacheTest.MAX_SIZE) * 4), 4);
        Thread t4 = getKeys(((LockCacheTest.MAX_SIZE) * 2), ((LockCacheTest.MAX_SIZE) * 4), 4);
        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }

    @Test(timeout = 1000)
    public void referencedLockTest() throws InterruptedException {
        LockResource lock0 = mCache.get(0, READ);
        LockResource lock1 = mCache.get(50, READ);
        LockResource lock2 = mCache.get(100, READ);
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 100; i++) {
                mCache.get(i, READ).close();
            }
        }
        Assert.assertTrue(lock0.hasSameLock(mCache.get(0, READ)));
        Assert.assertTrue(lock1.hasSameLock(mCache.get(50, READ)));
        Assert.assertTrue(lock2.hasSameLock(mCache.get(100, READ)));
    }
}

