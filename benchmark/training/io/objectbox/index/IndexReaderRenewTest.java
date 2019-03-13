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
package io.objectbox.index;


import EntityLongIndex_.indexedLong;
import io.objectbox.AbstractObjectBoxTest;
import io.objectbox.Box;
import io.objectbox.index.model.EntityLongIndex;
import io.objectbox.query.Query;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class IndexReaderRenewTest extends AbstractObjectBoxTest {
    @Test
    public void testOverwriteIndexedValue() throws InterruptedException {
        final Box<EntityLongIndex> box = store.boxFor(EntityLongIndex.class);
        final int initialValue = 1;
        final EntityLongIndex[] transformResults = new EntityLongIndex[]{ null, null, null };
        final CountDownLatch transformLatch1 = new CountDownLatch(1);
        final CountDownLatch transformLatch2 = new CountDownLatch(1);
        final AtomicInteger transformerCallCount = new AtomicInteger();
        final Query<EntityLongIndex> query = box.query().equal(indexedLong, 0).build();
        store.subscribe(EntityLongIndex.class).transform(new io.objectbox.reactive.DataTransformer<Class<EntityLongIndex>, EntityLongIndex>() {
            @Override
            public EntityLongIndex transform(Class<EntityLongIndex> clazz) throws Exception {
                int callCount = transformerCallCount.incrementAndGet();
                if (callCount == 1) {
                    query.setParameter(indexedLong, 1);
                    EntityLongIndex unique = query.findUnique();
                    transformLatch1.countDown();
                    return unique;
                } else
                    if (callCount == 2) {
                        query.setParameter(indexedLong, 1);
                        transformResults[0] = query.findUnique();
                        transformResults[1] = query.findUnique();
                        query.setParameter(indexedLong, 0);
                        transformResults[2] = query.findUnique();
                        transformLatch2.countDown();
                        return transformResults[0];
                    } else {
                        throw new RuntimeException(("Unexpected: " + callCount));
                    }

            }
        }).observer(new io.objectbox.reactive.DataObserver<EntityLongIndex>() {
            @Override
            public void onData(EntityLongIndex data) {
                // Dummy
            }
        });
        Assert.assertTrue(transformLatch1.await(5, TimeUnit.SECONDS));
        box.put(createEntityLongIndex(initialValue));
        Assert.assertTrue(transformLatch2.await(5, TimeUnit.SECONDS));
        Assert.assertEquals(2, transformerCallCount.intValue());
        Assert.assertNotNull(transformResults[0]);
        Assert.assertNotNull(transformResults[1]);
        Assert.assertNull(transformResults[2]);
        query.setParameter(indexedLong, initialValue);
        Assert.assertNotNull(query.findUnique());
        query.setParameter(indexedLong, initialValue);
        Assert.assertNotNull(query.findUnique());
        Assert.assertNotNull(query.findUnique());
    }

    @Test
    public void testOldReaderInThread() throws InterruptedException {
        final Box<EntityLongIndex> box = store.boxFor(EntityLongIndex.class);
        final int initialValue = 1;
        final EntityLongIndex[] results = new EntityLongIndex[5];
        final CountDownLatch latchRead1 = new CountDownLatch(1);
        final CountDownLatch latchPut = new CountDownLatch(1);
        final CountDownLatch latchRead2 = new CountDownLatch(1);
        final Query<EntityLongIndex> query = box.query().equal(indexedLong, 0).build();
        new Thread() {
            @Override
            public void run() {
                query.setParameter(indexedLong, initialValue);
                EntityLongIndex unique = query.findUnique();
                Assert.assertNull(unique);
                latchRead1.countDown();
                System.out.println(("BEFORE put: " + (box.getReaderDebugInfo())));
                System.out.println(("count before: " + (box.count())));
                try {
                    latchPut.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                System.out.println(("AFTER put: " + (box.getReaderDebugInfo())));
                System.out.println(("count after: " + (box.count())));
                query.setParameter(indexedLong, initialValue);
                results[0] = query.findUnique();
                results[1] = box.get(1);
                results[2] = query.findUnique();
                query.setParameter(indexedLong, 0);
                results[3] = query.findUnique();
                latchRead2.countDown();
            }
        }.start();
        Assert.assertTrue(latchRead1.await(5, TimeUnit.SECONDS));
        box.put(createEntityLongIndex(initialValue));
        latchPut.countDown();
        Assert.assertTrue(latchRead2.await(5, TimeUnit.SECONDS));
        Assert.assertNotNull(results[1]);
        Assert.assertNotNull(results[0]);
        Assert.assertNotNull(results[2]);
        Assert.assertNull(results[3]);
        query.setParameter(indexedLong, initialValue);
        Assert.assertNotNull(query.findUnique());
        query.setParameter(indexedLong, initialValue);
        Assert.assertNotNull(query.findUnique());
        Assert.assertNotNull(query.findUnique());
    }

    @Test
    public void testOldReaderWithIndex() throws InterruptedException {
        final Box<EntityLongIndex> box = store.boxFor(EntityLongIndex.class);
        final int initialValue = 1;
        final Query<EntityLongIndex> query = box.query().equal(indexedLong, 0).build();
        Assert.assertNull(query.findUnique());
        System.out.println(("BEFORE put: " + (box.getReaderDebugInfo())));
        System.out.println(("count before: " + (box.count())));
        box.put(createEntityLongIndex(initialValue));
        System.out.println(("AFTER put: " + (box.getReaderDebugInfo())));
        System.out.println(("count after: " + (box.count())));
        query.setParameter(indexedLong, initialValue);
        Assert.assertNotNull(query.findUnique());
        query.setParameter(indexedLong, 0);
        Assert.assertNull(query.findUnique());
    }
}

