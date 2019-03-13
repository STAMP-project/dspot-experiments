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
import io.objectbox.exception.DbExceptionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;


public class TransactionTest extends AbstractObjectBoxTest {
    @Test
    public void testTransactionCommitAndAbort() {
        prepareOneEntryWith1230();
        Transaction transaction = store.beginTx();
        KeyValueCursor cursor = transaction.createKeyValueCursor();
        cursor.put(123, new byte[]{ 3, 2, 1, 0 });
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursor.get(123));
        cursor.close();
        transaction.abort();
        transaction = store.beginTx();
        cursor = transaction.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 0 }, cursor.get(123));
        cursor.close();
        transaction.abort();
    }

    @Test
    public void testReadTransactionWhileWriting() {
        prepareOneEntryWith1230();
        Transaction txWrite = store.beginTx();
        Transaction txRead = store.beginReadTx();
        // start writing
        KeyValueCursor cursorWrite = txWrite.createKeyValueCursor();
        cursorWrite.put(123, new byte[]{ 3, 2, 1, 0 });
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursorWrite.get(123));
        cursorWrite.close();
        // start reading the old value
        KeyValueCursor cursorRead = txRead.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 0 }, cursorRead.get(123));
        cursorRead.close();
        // commit writing
        Assert.assertEquals(true, txRead.isReadOnly());
        Assert.assertEquals(false, txWrite.isReadOnly());
        Assert.assertEquals(true, txWrite.isActive());
        txWrite.commit();
        Assert.assertEquals(false, txWrite.isActive());
        // commit reading
        Assert.assertEquals(true, txRead.isActive());
        txRead.abort();
        Assert.assertEquals(false, txRead.isActive());
        // start reading again and get the new value
        txRead = store.beginReadTx();
        cursorRead = txRead.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursorRead.get(123));
        cursorRead.close();
        txRead.abort();
        store.close();
    }

    @Test
    public void testTransactionReset() {
        prepareOneEntryWith1230();
        // write transaction
        Transaction transaction = store.beginTx();
        KeyValueCursor cursor = transaction.createKeyValueCursor();
        cursor.put(123, new byte[]{ 3, 2, 1, 0 });
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursor.get(123));
        cursor.close();
        transaction.reset();
        Assert.assertEquals(true, transaction.isActive());
        cursor = transaction.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3, 0 }, cursor.get(123));
        cursor.close();
        transaction.abort();
        transaction.reset();
        cursor = transaction.createKeyValueCursor();
        cursor.put(123, new byte[]{ 3, 2, 1, 0 });
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursor.get(123));
        cursor.close();
        transaction.commit();
        transaction.reset();
        cursor = transaction.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursor.get(123));
        cursor.close();
        transaction.commit();
        // read transaction
        transaction = store.beginReadTx();
        cursor = transaction.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursor.get(123));
        cursor.close();
        transaction.reset();
        Assert.assertEquals(true, transaction.isActive());
        cursor = transaction.createKeyValueCursor();
        Assert.assertArrayEquals(new byte[]{ 3, 2, 1, 0 }, cursor.get(123));
        cursor.close();
        transaction.abort();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateCursorAfterAbortException() {
        Transaction tx = store.beginReadTx();
        tx.abort();
        tx.createKeyValueCursor();
    }

    @Test(expected = IllegalStateException.class)
    public void testCommitAfterAbortException() {
        Transaction tx = store.beginTx();
        tx.abort();
        tx.commit();
    }

    @Test(expected = IllegalStateException.class)
    public void testCommitReadTxException() {
        Transaction tx = store.beginReadTx();
        try {
            tx.commit();
        } finally {
            tx.abort();
        }
    }

    @Test
    public void testCommitReadTxException_exceptionListener() {
        final Exception[] exs = new Exception[]{ null };
        DbExceptionListener exceptionListener = new DbExceptionListener() {
            @Override
            public void onDbException(Exception e) {
                exs[0] = e;
            }
        };
        Transaction tx = store.beginReadTx();
        store.setDbExceptionListener(exceptionListener);
        try {
            tx.commit();
            Assert.fail("Should have thrown");
        } catch (IllegalStateException e) {
            tx.abort();
            Assert.assertSame(e, exs[0]);
        }
    }

    @Test
    public void testClose() {
        Transaction tx = store.beginReadTx();
        Assert.assertFalse(tx.isClosed());
        tx.close();
        Assert.assertTrue(tx.isClosed());
        // Double close should be fine
        tx.close();
        try {
            tx.reset();
            Assert.fail("Should have thrown");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testRunInTxRecursive() {
        final Box<TestEntity> box = getTestEntityBox();
        final long[] counts = new long[]{ 0, 0, 0 };
        store.runInTx(new Runnable() {
            @Override
            public void run() {
                box.put(new TestEntity());
                counts[0] = box.count();
                try {
                    store.callInTx(new Callable<Void>() {
                        @Override
                        public Void call() {
                            store.runInTx(new Runnable() {
                                @Override
                                public void run() {
                                    box.put(new TestEntity());
                                    counts[1] = box.count();
                                }
                            });
                            box.put(new TestEntity());
                            counts[2] = box.count();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Assert.assertEquals(1, counts[0]);
        Assert.assertEquals(2, counts[1]);
        Assert.assertEquals(3, counts[2]);
        Assert.assertEquals(3, box.count());
    }

    @Test
    public void testRunInReadTx() {
        final Box<TestEntity> box = getTestEntityBox();
        final long[] counts = new long[]{ 0, 0 };
        box.put(new TestEntity());
        store.runInReadTx(new Runnable() {
            @Override
            public void run() {
                counts[0] = box.count();
                store.runInReadTx(new Runnable() {
                    @Override
                    public void run() {
                        counts[1] = box.count();
                    }
                });
            }
        });
        Assert.assertEquals(1, counts[0]);
        Assert.assertEquals(1, counts[1]);
    }

    @Test
    public void testCallInReadTx() {
        final Box<TestEntity> box = getTestEntityBox();
        box.put(new TestEntity());
        long[] counts = store.callInReadTx(new Callable<long[]>() {
            @Override
            public long[] call() throws Exception {
                long count1 = store.callInReadTx(new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return box.count();
                    }
                });
                return new long[]{ box.count(), count1 };
            }
        });
        Assert.assertEquals(1, counts[0]);
        Assert.assertEquals(1, counts[1]);
    }

    @Test
    public void testRunInReadTxAndThenPut() {
        final Box<TestEntity> box = getTestEntityBox();
        store.runInReadTx(new Runnable() {
            @Override
            public void run() {
                box.count();
            }
        });
        // Verify that box does not hang on to the read-only TX by doing a put
        box.put(new TestEntity());
        Assert.assertEquals(1, box.count());
    }

    @Test
    public void testRunInReadTx_recursiveWriteTxFails() {
        store.runInReadTx(new Runnable() {
            @Override
            public void run() {
                try {
                    store.runInTx(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    Assert.fail("Should have thrown");
                } catch (IllegalStateException e) {
                    // OK
                }
            }
        });
    }

    @Test(expected = DbException.class)
    public void testRunInReadTx_putFails() {
        store.runInReadTx(new Runnable() {
            @Override
            public void run() {
                getTestEntityBox().put(new TestEntity());
            }
        });
    }

    @Test
    public void testRunInTx_PutAfterRemoveAll() {
        final Box<TestEntity> box = getTestEntityBox();
        final long[] counts = new long[]{ 0 };
        box.put(new TestEntity());
        store.runInTx(new Runnable() {
            @Override
            public void run() {
                putTestEntities(2);
                box.removeAll();
                putTestEntity("hello", 3);
                counts[0] = box.count();
            }
        });
        Assert.assertEquals(1, counts[0]);
    }

    @Test
    public void testCallInTxAsync_multiThreaded() throws InterruptedException {
        final Box<TestEntity> box = getTestEntityBox();
        final Thread mainTestThread = Thread.currentThread();
        final AtomicInteger number = new AtomicInteger();
        final AtomicInteger errorCount = new AtomicInteger();
        final int countThreads = (runExtensiveTests) ? 500 : 10;
        final int countEntities = (runExtensiveTests) ? 1000 : 100;
        final CountDownLatch threadsDoneLatch = new CountDownLatch(countThreads);
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Long call() throws Exception {
                Assert.assertNotSame(mainTestThread, Thread.currentThread());
                for (int i = 0; i < countEntities; i++) {
                    TestEntity entity = new TestEntity();
                    final int value = number.incrementAndGet();
                    entity.setSimpleInt(value);
                    long key = box.put(entity);
                    TestEntity read = box.get(key);
                    Assert.assertEquals(value, read.getSimpleInt());
                }
                return box.count();
            }
        };
        TxCallback<Object> callback = new TxCallback<Object>() {
            @Override
            @SuppressWarnings("NullableProblems")
            public void txFinished(Object result, @Nullable
            Throwable error) {
                if (error != null) {
                    errorCount.incrementAndGet();
                    error.printStackTrace();
                }
                threadsDoneLatch.countDown();
            }
        };
        for (int i = 0; i < countThreads; i++) {
            store.callInTxAsync(callable, callback);
        }
        Assert.assertTrue(threadsDoneLatch.await((runExtensiveTests ? 120 : 5), TimeUnit.SECONDS));
        Assert.assertEquals((countThreads * countEntities), number.get());
        Assert.assertEquals((countThreads * countEntities), box.count());
        Assert.assertEquals(0, errorCount.get());
    }

    @Test
    public void testCallInTxAsync_Error() throws InterruptedException {
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Long call() throws Exception {
                TestEntity entity = new TestEntity();
                entity.setId((-1));
                getTestEntityBox().put(entity);
                return null;
            }
        };
        final LinkedBlockingQueue<Throwable> queue = new LinkedBlockingQueue<>();
        TxCallback<Object> callback = new TxCallback<Object>() {
            @Override
            @SuppressWarnings("NullableProblems")
            public void txFinished(Object result, @Nullable
            Throwable error) {
                queue.add(error);
            }
        };
        store.callInTxAsync(callable, callback);
        Throwable result = queue.poll(5, TimeUnit.SECONDS);
        Assert.assertNotNull(result);
    }
}

