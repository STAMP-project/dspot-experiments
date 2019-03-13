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


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class CursorTest extends AbstractObjectBoxTest {
    @Test
    public void testPutAndGetEntity() {
        TestEntity entity = new TestEntity();
        entity.setSimpleInt(1977);
        Transaction transaction = store.beginTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        long key = cursor.put(entity);
        TestEntity entityRead = cursor.get(key);
        Assert.assertNotNull(entityRead);
        Assert.assertEquals(1977, entityRead.getSimpleInt());
        cursor.close();
        transaction.abort();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutEntityWithInvalidId() {
        TestEntity entity = new TestEntity();
        entity.setId(777);
        Transaction transaction = store.beginTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        try {
            cursor.put(entity);
        } finally {
            cursor.close();
            transaction.close();
        }
    }

    @Test
    public void testGetNextEntity() {
        insertTestEntities("hello", "bye", "dummy");
        Transaction transaction = store.beginReadTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        TestEntity entity1 = cursor.get(1);
        Assert.assertEquals("hello", entity1.getSimpleString());
        TestEntity entity2 = cursor.next();
        Assert.assertEquals("bye", entity2.getSimpleString());
        TestEntity entity3 = cursor.next();
        Assert.assertEquals("dummy", entity3.getSimpleString());
        Assert.assertNull(cursor.next());
        cursor.close();
        transaction.abort();
    }

    // TODO split up into several test cases
    @Test
    public void testPutGetUpdateDeleteEntity() {
        // create an entity
        TestEntity entity = new TestEntity();
        entity.setSimpleInt(1977);
        entity.setSimpleLong(54321);
        String value1 = "lulu321";
        entity.setSimpleString(value1);
        Transaction transaction = store.beginTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        long key = cursor.put(entity);
        // get it
        TestEntity entityRead = cursor.get(key);
        Assert.assertNotNull(entityRead);
        Assert.assertEquals(1977, entityRead.getSimpleInt());
        Assert.assertEquals(54321, entityRead.getSimpleLong());
        Assert.assertEquals(value1, entityRead.getSimpleString());
        // and find via index
        Assert.assertEquals(key, cursor.lookupKeyUsingIndex(9, value1));
        // assertEquals(key, cursor.find(TestEntity_.simpleString, value1).get(0).getId());
        // change entity values
        String value2 = "lala123";
        entityRead.setSimpleString(value2);
        entityRead.setSimpleLong(12345);
        // and replace the old one with the new one
        cursor.put(entityRead);
        // indexes ok?
        // assertEquals(0, cursor.find(TestEntity_.simpleString, value1).size());
        Assert.assertEquals(0, cursor.lookupKeyUsingIndex(9, value1));
        // assertEquals(key, cursor.find(TestEntity_.simpleString, value2).get(0).getId());
        // get the changed entity
        entityRead = cursor.get(key);
        Assert.assertNotNull(entityRead);
        // and check if the values changed
        Assert.assertEquals(1977, entityRead.getSimpleInt());
        Assert.assertEquals(12345, entityRead.getSimpleLong());
        Assert.assertEquals(value2, entityRead.getSimpleString());
        // and remove it
        cursor.deleteEntity(key);
        // not in any index anymore
        // assertEquals(0, cursor.find(TestEntity_.simpleString, value1).size());
        // assertEquals(0, cursor.find(TestEntity_.simpleString, value2).size());
        cursor.close();
        transaction.abort();
    }

    @Test
    public void testPutSameIndexValue() {
        TestEntity entity = new TestEntity();
        String value = "lulu321";
        entity.setSimpleString(value);
        Transaction transaction = store.beginTx();
        TestEntity read;
        try {
            Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
            long key = cursor.put(entity);
            // And again
            entity.setSimpleInt(1977);
            cursor.put(entity);
            Assert.assertEquals(key, cursor.lookupKeyUsingIndex(9, value));
            read = cursor.get(key);
            cursor.close();
        } finally {
            transaction.close();
        }
        Assert.assertEquals(1977, read.getSimpleInt());
        Assert.assertEquals(value, read.getSimpleString());
    }

    // @Test
    // public void testFindStringInEntityWithIndex() {
    // testFindStringInEntity();
    // }
    @Test
    public void testLookupKeyUsingIndex() throws IOException {
        insertTestEntities("find me", "not me");
        Transaction transaction = store.beginTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        Assert.assertEquals(2, cursor.lookupKeyUsingIndex(9, "not me"));
        Assert.assertEquals(1, cursor.lookupKeyUsingIndex(9, "find me"));
        Assert.assertEquals(0, cursor.lookupKeyUsingIndex(9, "peter pan"));
        cursor.close();
        transaction.abort();
    }

    @Test
    public void testLookupKeyUsingIndex_samePrefix() {
        insertTestEntities("aaa", "aa");
        Transaction transaction = store.beginTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        Assert.assertEquals(0, cursor.lookupKeyUsingIndex(9, "a"));
        Assert.assertEquals(2, cursor.lookupKeyUsingIndex(9, "aa"));
        Assert.assertEquals(1, cursor.lookupKeyUsingIndex(9, "aaa"));
        Assert.assertEquals(0, cursor.lookupKeyUsingIndex(9, "aaaa"));
        cursor.close();
        transaction.abort();
    }

    @Test
    public void testClose() {
        Transaction tx = store.beginReadTx();
        try {
            Cursor<TestEntity> cursor = tx.createCursor(TestEntity.class);
            Assert.assertFalse(cursor.isClosed());
            cursor.close();
            Assert.assertTrue(cursor.isClosed());
            // Double close should be fine
            cursor.close();
        } finally {
            tx.close();
        }
    }

    @Test
    public void testWriteTxBlocksOtherWriteTx() throws InterruptedException {
        long time = System.currentTimeMillis();
        Transaction tx = store.beginTx();
        long duration = (System.currentTimeMillis()) - time;// Usually 0 on desktop

        final CountDownLatch latchBeforeBeginTx = new CountDownLatch(1);
        final CountDownLatch latchAfterBeginTx = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                latchBeforeBeginTx.countDown();
                Transaction tx2 = store.beginTx();
                latchAfterBeginTx.countDown();
                tx2.close();
            }
        }.start();
        Assert.assertTrue(latchBeforeBeginTx.await(1, TimeUnit.SECONDS));
        long waitTime = 50 + (duration * 10);
        Assert.assertFalse(latchAfterBeginTx.await(waitTime, TimeUnit.MILLISECONDS));
        tx.close();
        Assert.assertTrue(latchAfterBeginTx.await(waitTime, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetPropertyId() {
        Transaction transaction = store.beginReadTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        Assert.assertEquals(1, cursor.getPropertyId("id"));
        Assert.assertEquals(2, cursor.getPropertyId("simpleBoolean"));
        Assert.assertEquals(3, cursor.getPropertyId("simpleByte"));
        Assert.assertEquals(4, cursor.getPropertyId("simpleShort"));
        cursor.close();
        transaction.abort();
    }

    @Test
    public void testRenew() throws IOException {
        insertTestEntities("orange");
        Transaction transaction = store.beginReadTx();
        Cursor<TestEntity> cursor = transaction.createCursor(TestEntity.class);
        transaction.recycle();
        transaction.renew();
        cursor.renew();
        Assert.assertEquals("orange", cursor.get(1).getSimpleString());
        cursor.close();
        transaction.close();
    }
}

