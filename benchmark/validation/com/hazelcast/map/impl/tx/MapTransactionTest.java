/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.tx;


import GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS;
import LockService.SERVICE_NAME;
import TransactionOptions.TransactionType.ONE_PHASE;
import TransactionOptions.TransactionType.TWO_PHASE;
import com.hazelcast.concurrent.lock.LockResource;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.MapStoreAdapter;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.map.impl.operation.DefaultMapOperationProvider;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTaskContext;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapTransactionTest extends HazelcastTestSupport {
    private final TransactionOptions options = new TransactionOptions().setTransactionType(TWO_PHASE);

    @Test
    public void testTransactionAtomicity_withMapAndQueue() throws InterruptedException, ExecutionException {
        final HazelcastInstance instance = createHazelcastInstance();
        Future<Object> future = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                IQueue<Object> queue = instance.getQueue("queue");
                IMap<Object, Object> map = instance.getMap("map");
                Object item = queue.take();
                return map.get(item);
            }
        });
        TransactionOptions options = new TransactionOptions().setTransactionType(ONE_PHASE);
        TransactionContext context = instance.newTransactionContext(options);
        context.beginTransaction();
        TransactionalQueue<Object> queue = context.getQueue("queue");
        TransactionalMap<Object, Object> map = context.getMap("map");
        queue.offer("item-99");
        for (int i = 0; i < 100; i++) {
            map.put(("item-" + i), "value");
        }
        context.commitTransaction();
        Assert.assertEquals("value", future.get());
    }

    @Test
    public void testNotToBlockReads() throws InterruptedException, ExecutionException {
        final HazelcastInstance instance = createHazelcastInstance();
        TransactionContext context = instance.newTransactionContext();
        context.beginTransaction();
        TransactionalMap<Object, Object> map = context.getMap("map");
        map.put("key", "value");
        Future<Object> future = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                IMap<Object, Object> map = instance.getMap("map");
                return map.get("key");
            }
        });
        Assert.assertNull(future.get());
        context.commitTransaction();
    }

    @Test
    public void testGetForUpdate_releasesBackupLock() {
        Config config = getConfig();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final String keyOwnedByInstance2 = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        instance1.executeTransaction(new com.hazelcast.transaction.TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> map = context.getMap(HazelcastTestSupport.randomString());
                map.getForUpdate(keyOwnedByInstance2);
                return null;
            }
        });
        NodeEngine nodeEngine = HazelcastTestSupport.getNodeEngineImpl(instance1);
        Data keyData = nodeEngine.toData(keyOwnedByInstance2);
        LockService lockService = nodeEngine.getService(SERVICE_NAME);
        for (LockResource lockResource : lockService.getAllLocks()) {
            if (keyData.equals(lockResource.getKey())) {
                Assert.assertEquals(0, lockResource.getLockCount());
            }
        }
    }

    @Test
    public void testCommitOrder() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final HazelcastInstance h3 = factory.newHazelcastInstance(config);
        final HazelcastInstance h4 = factory.newHazelcastInstance(config);
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.put("1", "value1");
                Assert.assertEquals("value1", txMap.put("1", "value2"));
                Assert.assertEquals("value2", txMap.put("1", "value3"));
                Assert.assertEquals("value3", txMap.put("1", "value4"));
                Assert.assertEquals("value4", txMap.put("1", "value5"));
                Assert.assertEquals("value5", txMap.put("1", "value6"));
                Assert.assertEquals(1, txMap.size());
                return true;
            }
        });
        Assert.assertEquals("value6", h4.getMap("default").get("1"));
    }

    @Test
    public void testTxnCommit() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final String map1 = "map1";
        final String map2 = "map2";
        final String key = "1";
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap1 = context.getMap(map1);
                final TransactionalMap<Object, Object> txMap2 = context.getMap(map2);
                txMap1.put(key, "value");
                Assert.assertEquals("value", txMap1.put(key, "value1"));
                Assert.assertEquals("value1", txMap1.get(key));
                txMap2.put(key, "value");
                Assert.assertEquals("value", txMap2.put(key, "value2"));
                Assert.assertEquals("value2", txMap2.get(key));
                Assert.assertTrue(txMap1.containsKey(key));
                Assert.assertTrue(txMap2.containsKey(key));
                Assert.assertNull(h1.getMap(map1).get(key));
                Assert.assertNull(h1.getMap(map2).get(key));
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertEquals("value1", h1.getMap(map1).get(key));
        Assert.assertEquals("value1", h2.getMap(map1).get(key));
        Assert.assertEquals("value2", h1.getMap(map2).get(key));
        Assert.assertEquals("value2", h2.getMap(map2).get(key));
        Assert.assertFalse(h1.getMap(map1).isLocked(key));
        Assert.assertFalse(h1.getMap(map2).isLocked(key));
    }

    @Test
    public void testTxnBackupDies() throws TransactionException, InterruptedException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final String mapName = HazelcastTestSupport.randomMapName();
        final IMap map = h1.getMap(mapName);
        final int size = 100;
        final CountDownLatch transactionCompletedLatch = new CountDownLatch((size + 1));
        final CountDownLatch thresholdReached = new CountDownLatch(1);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    final int oneThird = size / 3;
                    final int threshold = (new Random().nextInt(oneThird)) + oneThird;
                    h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
                        public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                            final TransactionalMap<Object, Object> txMap = context.getMap(mapName);
                            for (int i = 0; i < size; i++) {
                                if (i == threshold) {
                                    thresholdReached.countDown();
                                }
                                txMap.put(i, i);
                                HazelcastTestSupport.sleepMillis(100);
                                transactionCompletedLatch.countDown();
                            }
                            return true;
                        }
                    });
                    Assert.fail();
                } catch (Exception ignored) {
                }
                transactionCompletedLatch.countDown();
            }
        };
        new Thread(runnable).start();
        HazelcastTestSupport.assertOpenEventually(thresholdReached);
        TestUtil.terminateInstance(h2);
        HazelcastTestSupport.assertOpenEventually(transactionCompletedLatch);
        for (int i = 0; i < size; i++) {
            Assert.assertNull(map.get(i));
        }
    }

    @Test
    @Category(NightlyTest.class)
    public void testTxnOwnerDies() throws TransactionException, InterruptedException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final HazelcastInstance h3 = factory.newHazelcastInstance(config);
        final int size = 50;
        final AtomicBoolean result = new AtomicBoolean(false);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
                        public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                            final TransactionalMap<Object, Object> txMap = context.getMap("default");
                            for (int i = 0; i < size; i++) {
                                txMap.put(i, i);
                                HazelcastTestSupport.sleepSeconds(1);
                            }
                            return true;
                        }
                    });
                    result.set(b);
                } catch (HazelcastInstanceNotActiveException ignored) {
                } catch (TransactionException ignored) {
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        HazelcastTestSupport.sleepSeconds(1);
        h1.shutdown();
        // wait till thread finishes.
        thread.join((30 * 1000));
        Assert.assertFalse(result.get());
        final IMap map2 = h2.getMap("default");
        for (int i = 0; i < size; i++) {
            Assert.assertNull(map2.get(i));
        }
    }

    // =================== set ===============================
    @Test
    public void testSet() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.set("1", "value");
                txMap.set("1", "value2");
                Assert.assertEquals("value2", txMap.get("1"));
                Assert.assertNull(map1.get("1"));
                Assert.assertNull(map1.get("2"));
                Assert.assertEquals(1, txMap.size());
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertEquals("value2", map1.get("1"));
        Assert.assertEquals("value2", map2.get("1"));
    }

    @Test(expected = NullPointerException.class)
    public void testSet_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.set(null, "value");
                return true;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testSet_whenNullValue() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.set("key", null);
                return true;
            }
        });
    }

    // =================== putTTL ===============================
    @Test
    public void testPutTTL() throws TransactionException {
        final String mapName = "putWithTTL";
        final Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map2 = h2.getMap(mapName);
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap(mapName);
                txMap.put("1", "value", 5, TimeUnit.SECONDS);
                Assert.assertEquals("value", txMap.get("1"));
                Assert.assertEquals(1, txMap.size());
                return true;
            }
        });
        Assert.assertTrue(b);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNull(map2.get("1"));
            }
        });
    }

    // =================== getForUpdate ===============================
    @Test(expected = NullPointerException.class)
    public void testGetForUpdate_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.getForUpdate(null);
                return true;
            }
        });
    }

    @Test
    public void testGetForUpdate() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h2.getMap("default");
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final AtomicBoolean pass = new AtomicBoolean(true);
        map.put("var", "value0");
        Runnable updater = new Runnable() {
            public void run() {
                try {
                    latch1.await(100, TimeUnit.SECONDS);
                    pass.set((!(map.tryPut("var", "value1", 0, TimeUnit.SECONDS))));
                    latch2.countDown();
                } catch (Exception e) {
                }
            }
        };
        new Thread(updater).start();
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                try {
                    final TransactionalMap<String, String> txMap = context.getMap("default");
                    Assert.assertEquals("value0", txMap.getForUpdate("var"));
                    latch1.countDown();
                    latch2.await(100, TimeUnit.SECONDS);
                } catch (Exception e) {
                }
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertTrue(pass.get());
        Assert.assertTrue(map.tryPut("var", "value2", 0, TimeUnit.SECONDS));
    }

    @Test
    public void testGetForUpdate_whenTimeout() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h2.getMap("default");
        map.put("var", "value0");
        map.lock("var");
        TransactionOptions opts = new TransactionOptions();
        opts.setTimeout(1, TimeUnit.SECONDS);
        try {
            boolean b = h1.executeTransaction(opts, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
                public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                    final TransactionalMap<String, String> txMap = context.getMap("default");
                    txMap.getForUpdate("var");
                    Assert.fail();
                    return true;
                }
            });
        } catch (TransactionException e) {
        }
        Assert.assertTrue(map.isLocked("var"));
    }

    @Test
    public void testGetForUpdate_whenUpdateTxnFails() throws TransactionException {
        Config config = getConfig();
        config.setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), "5000");
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h2.getMap("default");
        map.put("var", "value");
        map.lock("varLocked");
        TransactionOptions opts = new TransactionOptions();
        opts.setTimeout(1, TimeUnit.SECONDS);
        try {
            boolean b = h1.executeTransaction(opts, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
                public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                    final TransactionalMap<String, String> txMap = context.getMap("default");
                    txMap.getForUpdate("var");
                    throw new TransactionException();
                }
            });
        } catch (TransactionException e) {
        }
        Assert.assertFalse(map.isLocked("var"));
        Assert.assertTrue(map.isLocked("varLocked"));
    }

    @Test
    public void testGetForUpdate_whenMultipleTimes() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h2.getMap("default");
        map.put("var", "value0");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                try {
                    final TransactionalMap<String, String> txMap = context.getMap("default");
                    Assert.assertEquals("value0", txMap.getForUpdate("var"));
                    Assert.assertEquals("value0", txMap.getForUpdate("var"));
                    Assert.assertEquals("value0", txMap.getForUpdate("var"));
                } catch (Exception e) {
                }
                return true;
            }
        });
    }

    @Test
    public void testUpdate_thenGetForUpdate() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h2.getMap("default");
        map.put("var", "value0");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                try {
                    final TransactionalMap<String, String> txMap = context.getMap("default");
                    Assert.assertEquals("value0", txMap.put("var", "value1"));
                    Assert.assertEquals("value1", txMap.getForUpdate("var"));
                } catch (Exception e) {
                }
                return true;
            }
        });
    }

    @Test
    public void testGetForUpdate_ThenUpdate() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h2.getMap("default");
        map.put("var", "value0");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                try {
                    final TransactionalMap<String, String> txMap = context.getMap("default");
                    Assert.assertEquals("value0", txMap.getForUpdate("var"));
                    Assert.assertEquals("value0", txMap.put("var", "value1"));
                    Assert.assertEquals("value1", txMap.getForUpdate("var"));
                    Assert.assertEquals("value1", txMap.get("var"));
                } catch (Exception e) {
                }
                return true;
            }
        });
    }

    // ========================= remove =====================
    @Test
    public void testRemoveIfSame() throws InterruptedException, ExecutionException {
        HazelcastInstance instance = createHazelcastInstance();
        TransactionContext context = instance.newTransactionContext(options);
        context.beginTransaction();
        TransactionalMap<String, String> map = context.getMap("map");
        map.put("key-0", "value");
        Assert.assertTrue(map.remove("key-0", "value"));
        context.commitTransaction();
    }

    @Test(expected = NullPointerException.class)
    public void testRemove_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.remove(null);
                return true;
            }
        });
    }

    @Test
    public void testRemove() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        map2.put("1", "1");
        map2.put("2", "2");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.put("3", "3");
                map2.put("4", "4");
                Assert.assertEquals("1", txMap.remove("1"));
                Assert.assertEquals("2", map1.remove("2"));
                Assert.assertEquals("1", map1.get("1"));
                Assert.assertNull(txMap.get("1"));
                Assert.assertNull(txMap.remove("2"));
                Assert.assertEquals(2, txMap.size());
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertNull(map1.get("1"));
        Assert.assertNull(map2.get("1"));
        Assert.assertNull(map1.get("2"));
        Assert.assertNull(map2.get("2"));
        Assert.assertEquals("3", map1.get("3"));
        Assert.assertEquals("3", map2.get("3"));
        Assert.assertEquals("4", map1.get("4"));
        Assert.assertEquals("4", map2.get("4"));
    }

    @Test
    public void testRemove_whenSame() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        map2.put("1", "1");
        map2.put("2", "2");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.put("3", "3");
                map2.put("4", "4");
                Assert.assertTrue(txMap.remove("1", "1"));
                Assert.assertFalse(txMap.remove("2", "1"));
                Assert.assertEquals("1", map1.get("1"));
                Assert.assertNull(txMap.get("1"));
                Assert.assertTrue(txMap.remove("2", "2"));
                Assert.assertFalse(txMap.remove("5", "2"));
                Assert.assertEquals(2, txMap.size());
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertNull(map1.get("1"));
        Assert.assertNull(map2.get("1"));
        Assert.assertNull(map1.get("2"));
        Assert.assertNull(map2.get("2"));
        Assert.assertEquals("3", map1.get("3"));
        Assert.assertEquals("3", map2.get("3"));
        Assert.assertEquals("4", map1.get("4"));
        Assert.assertEquals("4", map2.get("4"));
    }

    // ========================= delete =====================
    @Test(expected = NullPointerException.class)
    public void testDelete_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.delete(null);
                return true;
            }
        });
    }

    @Test
    public void testDelete() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        map2.put("1", "1");
        map2.put("2", "2");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.put("3", "3");
                map2.put("4", "4");
                txMap.delete("1");
                map2.delete("2");
                Assert.assertEquals("1", map1.get("1"));
                Assert.assertNull(txMap.get("1"));
                txMap.delete("2");
                Assert.assertEquals(2, txMap.size());
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertNull(map1.get("1"));
        Assert.assertNull(map2.get("1"));
        Assert.assertNull(map1.get("2"));
        Assert.assertNull(map2.get("2"));
        Assert.assertEquals("3", map1.get("3"));
        Assert.assertEquals("3", map2.get("3"));
        Assert.assertEquals("4", map1.get("4"));
        Assert.assertEquals("4", map2.get("4"));
    }

    // ========================= putIfAbsent =====================
    @Test(expected = NullPointerException.class)
    public void tesPutIfAbsent_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.putIfAbsent(null, "value");
                return true;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void tesPutIfAbsent_whenNullValue() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.putIfAbsent("key", null);
                return true;
            }
        });
    }

    @Test
    public void testTxnPutIfAbsent() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.putIfAbsent("1", "value");
                Assert.assertEquals("value", txMap.putIfAbsent("1", "value2"));
                Assert.assertEquals("value", txMap.get("1"));
                Assert.assertNull(map1.get("1"));
                Assert.assertNull(map2.get("2"));
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertEquals("value", map1.get("1"));
        Assert.assertEquals("value", map2.get("1"));
    }

    // ========================= replace =====================
    @Test(expected = NullPointerException.class)
    public void testReplace_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.replace(null, "oldvalue", "newvalue");
                return true;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testReplace_whenNullOldValue() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.replace("key", null, "newvalue");
                return true;
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testReplace_whenNullNewValue() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.replace("key", "oldvalue", null);
                return true;
            }
        });
    }

    @Test
    public void testReplace() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                Assert.assertNull(txMap.replace("1", "value"));
                txMap.put("1", "value2");
                Assert.assertEquals("value2", txMap.replace("1", "value3"));
                Assert.assertEquals("value3", txMap.get("1"));
                Assert.assertNull(map1.get("1"));
                Assert.assertNull(map2.get("2"));
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertEquals("value3", map1.get("1"));
        Assert.assertEquals("value3", map2.get("1"));
    }

    @Test
    public void testReplace_whenSame() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map1 = h1.getMap("default");
        final IMap map2 = h2.getMap("default");
        map2.put("1", "1");
        map2.put("2", "2");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                Assert.assertTrue(txMap.replace("1", "1", "11"));
                Assert.assertFalse(txMap.replace("5", "5", "55"));
                Assert.assertFalse(txMap.replace("2", "1", "22"));
                Assert.assertEquals("1", map1.get("1"));
                Assert.assertEquals("11", txMap.get("1"));
                Assert.assertEquals("2", map1.get("2"));
                Assert.assertEquals("2", txMap.get("2"));
                return true;
            }
        });
        Assert.assertTrue(b);
        Assert.assertEquals("11", map1.get("1"));
        Assert.assertEquals("11", map2.get("1"));
        Assert.assertEquals("2", map1.get("2"));
        Assert.assertEquals("2", map2.get("2"));
    }

    @Test
    public void testTxnReplace2() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap map2 = h2.getMap("default");
        map2.put("1", "value2");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                Assert.assertEquals("value2", txMap.replace("1", "value3"));
                Assert.assertEquals("value3", txMap.get("1"));
                Assert.assertNull(map2.get("2"));
                return true;
            }
        });
        Assert.assertTrue(b);
        IMap map1 = h1.getMap("default");
        Assert.assertEquals("value3", map1.get("1"));
        Assert.assertEquals("value3", map2.get("1"));
    }

    // ========================= containsKey =====================
    @Test(expected = NullPointerException.class)
    public void testContainsKey_whenNullKey() throws TransactionException {
        final HazelcastInstance hz = createHazelcastInstance();
        hz.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.containsKey(null);
                return true;
            }
        });
    }

    @Test
    public void testContainsKey() throws TransactionException {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final IMap<String, String> map = h1.getMap("default");
        map.put("1", "1");
        boolean b = h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap("default");
                txMap.delete("1");
                Assert.assertFalse(txMap.containsKey("1"));
                Assert.assertTrue(map.containsKey("1"));
                return true;
            }
        });
        Assert.assertTrue(b);
    }

    // TODO: @mm - Review following case...
    @Test
    public void testFailingMapStore() throws TransactionException {
        final String map = "map";
        final String anotherMap = "anotherMap";
        Config config = getConfig();
        config.getMapConfig(map).setMapStoreConfig(new MapStoreConfig().setEnabled(true).setImplementation(new MapStoreAdapter() {
            public void store(Object key, Object value) {
                throw new ExpectedRuntimeException("Map store intentionally failed :) ");
            }
        }));
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        try {
            h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
                public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                    Assert.assertNull(context.getMap(map).put("1", "value1"));
                    Assert.assertNull(context.getMap(anotherMap).put("1", "value1"));
                    return true;
                }
            });
            Assert.fail();
        } catch (ExpectedRuntimeException expected) {
        }
        Assert.assertNull(h2.getMap(map).get("1"));
        Assert.assertEquals("value1", h2.getMap(anotherMap).get("1"));
    }

    @Test
    public void testRollbackMap() throws Throwable {
        Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final TransactionContext transactionContext = h1.newTransactionContext();
        transactionContext.beginTransaction();
        TransactionalMap<Integer, String> m = transactionContext.getMap("testRollbackMap");
        Integer key1 = 1;
        String value1 = "value1";
        Integer key2 = 2;
        String value2 = "value2";
        m.put(key1, value1);
        m.put(key2, value2);
        transactionContext.rollbackTransaction();
        Assert.assertNull(h1.getMap("testRollbackMap").get(key1));
        Assert.assertNull(h1.getMap("testRollbackMap").get(key2));
    }

    @Test(expected = TransactionNotActiveException.class)
    public void testTxnMapOuterTransaction() throws Throwable {
        Config config = getConfig();
        final HazelcastInstance h1 = createHazelcastInstance(config);
        final TransactionContext transactionContext = h1.newTransactionContext();
        transactionContext.beginTransaction();
        TransactionalMap<Integer, Integer> m = transactionContext.getMap("testTxnMapOuterTransaction");
        m.put(1, 1);
        transactionContext.commitTransaction();
        m.put(1, 1);
    }

    @Test
    public void testKeySet_whenPortableKeysetAndValuesWithPredicates() throws Exception {
        final String mapName = HazelcastTestSupport.randomString();
        final Config config = getConfig();
        config.getSerializationConfig().addPortableFactory(666, new PortableFactory() {
            public Portable create(int classId) {
                return new SampleTestObjects.PortableEmployee();
            }
        });
        final HazelcastInstance instance = createHazelcastInstance(config);
        IMap map = instance.getMap(mapName);
        final SampleTestObjects.PortableEmployee emp1 = new SampleTestObjects.PortableEmployee(34, "abc-123-xvz");
        final SampleTestObjects.PortableEmployee emp2 = new SampleTestObjects.PortableEmployee(20, "abc-123-xvz");
        map.put(emp1, emp1);
        final TransactionContext context = instance.newTransactionContext();
        context.beginTransaction();
        final TransactionalMap txMap = context.getMap(mapName);
        Assert.assertNull(txMap.put(emp2, emp2));
        Assert.assertEquals(2, txMap.size());
        Assert.assertEquals(2, txMap.keySet().size());
        Assert.assertEquals(0, txMap.keySet(new SqlPredicate("a = 10")).size());
        Assert.assertEquals(0, txMap.values(new SqlPredicate("a = 10")).size());
        Assert.assertEquals(2, txMap.keySet(new SqlPredicate("a >= 10")).size());
        Assert.assertEquals(2, txMap.values(new SqlPredicate("a >= 10")).size());
        context.commitTransaction();
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(2, map.values().size());
    }

    @Test
    public void testValues_WithPredicates_notContains_oldValues() throws TransactionException {
        Config config = getConfig();
        final String mapName = "testValuesWithPredicate_notContains_oldValues";
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<Integer, SampleTestObjects.Employee> map = h1.getMap(mapName);
        final SampleTestObjects.Employee employeeAtAge22 = new SampleTestObjects.Employee("emin", 22, true, 10.0);
        final SampleTestObjects.Employee employeeAtAge23 = new SampleTestObjects.Employee("emin", 23, true, 10.0);
        map.put(1, employeeAtAge22);
        h1.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> txMap = context.getMap(mapName);
                Assert.assertEquals(1, txMap.values(new SqlPredicate("age > 21")).size());
                txMap.put(1, employeeAtAge23);
                Collection coll = txMap.values(new SqlPredicate("age > 21"));
                Assert.assertEquals(1, coll.size());
                return true;
            }
        });
        h1.shutdown();
        h2.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValues_withPagingPredicate() throws TransactionException {
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName("testValuesWithPagingPredicate");
        final Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        final HazelcastInstance node = factory.newHazelcastInstance(config);
        final IMap<Integer, SampleTestObjects.Employee> map = node.getMap(mapName);
        final SampleTestObjects.Employee emp = new SampleTestObjects.Employee("name", 77, true, 10.0);
        map.put(1, emp);
        node.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Integer, SampleTestObjects.Employee> txMap = context.getMap(mapName);
                PagingPredicate<Integer, SampleTestObjects.Employee> predicate = new PagingPredicate<Integer, SampleTestObjects.Employee>(5);
                txMap.values(predicate);
                return true;
            }
        });
    }

    @Test
    public void testValuesWithPredicate_removingExistentEntry() throws TransactionException {
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName("_testValuesWithPredicate_removingExistentEntry_");
        final Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        final HazelcastInstance node = factory.newHazelcastInstance(config);
        final IMap map = node.getMap(mapName);
        final SampleTestObjects.Employee emp = new SampleTestObjects.Employee("name", 77, true, 10.0);
        map.put(1, emp);
        node.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Object, Object> txMap = context.getMap(mapName);
                txMap.remove(1);
                Collection<Object> coll = txMap.values(new SqlPredicate("age > 70 "));
                Assert.assertEquals(0, coll.size());
                return true;
            }
        });
        node.shutdown();
    }

    @Test
    public void testValues_shouldNotDeduplicateEntriesWhenGettingByPredicate() throws TransactionException {
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        final HazelcastInstance node = factory.newHazelcastInstance(config);
        final IMap map = node.getMap(mapName);
        final SampleTestObjects.Employee emp = new SampleTestObjects.Employee("name", 77, true, 10.0);
        map.put(1, emp);
        node.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Integer, SampleTestObjects.Employee> txMap = context.getMap(mapName);
                txMap.put(2, emp);
                Collection<SampleTestObjects.Employee> coll = txMap.values(new SqlPredicate("age = 77"));
                Assert.assertEquals(2, coll.size());
                return true;
            }
        });
        node.shutdown();
    }

    @Test
    public void testValues_resultSetContainsUpdatedEntry() throws TransactionException {
        final int nodeCount = 1;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = getConfig();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        final HazelcastInstance node = factory.newHazelcastInstance(config);
        final IMap map = node.getMap(mapName);
        final SampleTestObjects.Employee emp = new SampleTestObjects.Employee("name", 77, true, 10.0);
        map.put(1, emp);
        node.executeTransaction(options, new com.hazelcast.transaction.TransactionalTask<Boolean>() {
            public Boolean execute(TransactionalTaskContext context) throws TransactionException {
                final TransactionalMap<Integer, SampleTestObjects.Employee> txMap = context.getMap(mapName);
                emp.setAge(30);
                txMap.put(1, emp);
                Collection<SampleTestObjects.Employee> coll = txMap.values();
                Assert.assertEquals(1, coll.size());
                SampleTestObjects.Employee employee = coll.iterator().next();
                Assert.assertEquals(30, employee.getAge());
                return true;
            }
        });
        node.shutdown();
    }

    @Test
    public void testUpdatesInTxnFiringUpdateEvent() {
        Config config = getConfig();
        final String mapName = HazelcastTestSupport.randomMapName("testUpdatesInTxnFiringUpdateEvent");
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<String, String> map = node.getMap(mapName);
        final CountDownLatch expectedUpdateEventCount = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.core.EntryAdapter<String, String>() {
            @Override
            public void entryUpdated(EntryEvent<String, String> event) {
                expectedUpdateEventCount.countDown();
            }
        }, true);
        map.put("foo", "one");
        TransactionContext context = node.newTransactionContext();
        context.beginTransaction();
        TransactionalMap<String, String> transactionalMap = context.getMap(mapName);
        transactionalMap.put("foo", "three");
        context.commitTransaction();
        HazelcastTestSupport.assertOpenEventually("Not reached expected update event count", expectedUpdateEventCount);
    }

    @Test
    public void testUpdatesInTxnFiresUpdateEventWithNonNullOldValue() throws Exception {
        Config config = getConfig();
        final String mapName = HazelcastTestSupport.randomMapName();
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<String, String> map = node.getMap(mapName);
        final CountDownLatch expectedUpdateEventCount = new CountDownLatch(1);
        map.put("foo", "one");
        map.addEntryListener(new com.hazelcast.core.EntryAdapter<String, String>() {
            @Override
            public void entryUpdated(EntryEvent<String, String> event) {
                Assert.assertEquals("two", event.getValue());
                Assert.assertEquals("one", event.getOldValue());
                expectedUpdateEventCount.countDown();
            }
        }, true);
        TransactionContext context = node.newTransactionContext();
        context.beginTransaction();
        TransactionalMap<String, String> transactionalMap = context.getMap(mapName);
        transactionalMap.put("foo", "two");
        context.commitTransaction();
        HazelcastTestSupport.assertOpenEventually("Not reached expected update event count", expectedUpdateEventCount);
    }

    @Test
    public void transactionalMap_shouldNotHaveNegativeSize() throws Exception {
        Config config = getConfig();
        HazelcastInstance instance = createHazelcastInstance(config);
        instance.executeTransaction(new com.hazelcast.transaction.TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                String mapName = HazelcastTestSupport.randomString();
                String key = HazelcastTestSupport.randomString();
                String val = HazelcastTestSupport.randomString();
                TransactionalMap<String, String> map = context.getMap(mapName);
                map.put(key, val);
                map.remove(key);
                Assert.assertEquals(0, map.size());
                return null;
            }
        });
    }

    @Test
    public void testGetForUpdate_LoadsKeyFromMapLoader_whenKeyDoesNotExistsInDb() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final MapStoreAdapter mock = Mockito.mock(MapStoreAdapter.class);
        Mockito.when(mock.load(ArgumentMatchers.anyObject())).thenReturn(null);
        Config config = new Config();
        MapStoreConfig storeConfig = new MapStoreConfig();
        storeConfig.setEnabled(true).setImplementation(mock);
        config.getMapConfig(mapName).setMapStoreConfig(storeConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        instance.executeTransaction(new com.hazelcast.transaction.TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> map = context.getMap(mapName);
                Object value = map.getForUpdate(1);
                Assert.assertNull("value should be null", value);
                Mockito.verify(mock, Mockito.times(1)).load(ArgumentMatchers.anyObject());
                return null;
            }
        });
    }

    @Test
    public void testGetForUpdate_LoadsKeyFromMapLoader_whenKeyExistsInDb() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final String valueFromDB = HazelcastTestSupport.randomString();
        final MapStoreAdapter mock = Mockito.mock(MapStoreAdapter.class);
        Mockito.when(mock.load(ArgumentMatchers.anyObject())).thenReturn(valueFromDB);
        Config config = new Config();
        MapStoreConfig storeConfig = new MapStoreConfig();
        storeConfig.setEnabled(true).setImplementation(mock);
        config.getMapConfig(mapName).setMapStoreConfig(storeConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        instance.executeTransaction(new com.hazelcast.transaction.TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> map = context.getMap(mapName);
                Object value = map.getForUpdate(1);
                Assert.assertEquals(valueFromDB, value);
                Mockito.verify(mock, Mockito.times(1)).load(ArgumentMatchers.anyObject());
                return null;
            }
        });
    }

    @Test
    public void testGet_LoadsKeyFromMapLoader_whenKeyExistsInDb() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final String valueFromDB = HazelcastTestSupport.randomString();
        final MapStoreAdapter mock = Mockito.mock(MapStoreAdapter.class);
        Mockito.when(mock.load(ArgumentMatchers.anyObject())).thenReturn(valueFromDB);
        Config config = getConfig();
        MapStoreConfig storeConfig = new MapStoreConfig();
        storeConfig.setEnabled(true).setImplementation(mock);
        config.getMapConfig(mapName).setMapStoreConfig(storeConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        instance.executeTransaction(new com.hazelcast.transaction.TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> map = context.getMap(mapName);
                Object value = map.get(1);
                Assert.assertEquals(valueFromDB, value);
                Mockito.verify(mock, Mockito.times(1)).load(ArgumentMatchers.anyObject());
                return null;
            }
        });
    }

    @Test
    public void testGet_LoadsKeyFromMapLoader_whenKeyDoesNotExistsInDb() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final MapStoreAdapter mock = Mockito.mock(MapStoreAdapter.class);
        Mockito.when(mock.load(ArgumentMatchers.anyObject())).thenReturn(null);
        Config config = getConfig();
        MapStoreConfig storeConfig = new MapStoreConfig();
        storeConfig.setEnabled(true).setImplementation(mock);
        config.getMapConfig(mapName).setMapStoreConfig(storeConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        instance.executeTransaction(new com.hazelcast.transaction.TransactionalTask<Object>() {
            @Override
            public Object execute(TransactionalTaskContext context) throws TransactionException {
                TransactionalMap<Object, Object> map = context.getMap(mapName);
                Object value = map.get(1);
                Assert.assertNull("value should be null", value);
                Mockito.verify(mock, Mockito.times(1)).load(ArgumentMatchers.anyObject());
                return null;
            }
        });
    }

    private static class WaitTimeoutSetterMapOperationProvider extends DefaultMapOperationProvider {
        private final MapOperationProvider operationProvider;

        public WaitTimeoutSetterMapOperationProvider(MapOperationProvider operationProvider) {
            this.operationProvider = operationProvider;
        }

        @Override
        public MapOperation createContainsKeyOperation(String name, Data dataKey) {
            MapOperation containsKeyOperation = operationProvider.createContainsKeyOperation(name, dataKey);
            containsKeyOperation.setWaitTimeout(TimeUnit.SECONDS.toMillis(3));
            return containsKeyOperation;
        }
    }
}

