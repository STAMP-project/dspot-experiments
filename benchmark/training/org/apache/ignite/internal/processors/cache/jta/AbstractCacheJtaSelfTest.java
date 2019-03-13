/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache.jta;


import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractSelfTest;
import org.apache.ignite.testframework.GridTestSafeThreadFactory;
import org.apache.ignite.transactions.Transaction;
import org.junit.Test;
import org.objectweb.jotm.Jotm;

import static java.lang.Thread.State.WAITING;


/**
 * Abstract class for cache tests.
 */
public abstract class AbstractCacheJtaSelfTest extends GridCacheAbstractSelfTest {
    /**
     *
     */
    private static final int GRID_CNT = 1;

    /**
     * Java Open Transaction Manager facade.
     */
    protected static Jotm jotm;

    /**
     * JUnit.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJta() throws Exception {
        UserTransaction jtaTx = AbstractCacheJtaSelfTest.jotm.getUserTransaction();
        IgniteCache<String, Integer> cache = jcache();
        assert (ignite(0).transactions().tx()) == null;
        jtaTx.begin();
        try {
            assert (ignite(0).transactions().tx()) == null;
            assert (cache.getAndPut("key", 1)) == null;
            Transaction tx = ignite(0).transactions().tx();
            assert tx != null;
            assert (tx.state()) == (ACTIVE);
            Integer one = 1;
            assertEquals(one, cache.get("key"));
            tx = ignite(0).transactions().tx();
            assert tx != null;
            assert (tx.state()) == (ACTIVE);
            jtaTx.commit();
            assert (ignite(0).transactions().tx()) == null;
        } finally {
            if ((jtaTx.getStatus()) == (Status.STATUS_ACTIVE))
                jtaTx.rollback();

        }
        assertEquals(((Integer) (1)), cache.get("key"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJtaTwoCaches() throws Exception {
        UserTransaction jtaTx = AbstractCacheJtaSelfTest.jotm.getUserTransaction();
        IgniteEx ignite = grid(0);
        IgniteCache<String, Integer> cache1 = jcache();
        IgniteCache<Object, Object> cache2 = ignite.cache("cache-2");
        assertNull(ignite.transactions().tx());
        jtaTx.begin();
        try {
            cache1.put("key", 0);
            cache2.put("key", 0);
            cache1.put("key1", 1);
            cache2.put("key2", 2);
            assertEquals(0, ((int) (cache1.get("key"))));
            assertEquals(0, ((int) (cache2.get("key"))));
            assertEquals(1, ((int) (cache1.get("key1"))));
            assertEquals(2, ((int) (cache2.get("key2"))));
            assertEquals(ignite.transactions().tx().state(), ACTIVE);
            jtaTx.commit();
            assertNull(ignite.transactions().tx());
            assertEquals(0, ((int) (cache1.get("key"))));
            assertEquals(0, ((int) (cache2.get("key"))));
            assertEquals(1, ((int) (cache1.get("key1"))));
            assertEquals(2, ((int) (cache2.get("key2"))));
        } finally {
            if ((jtaTx.getStatus()) == (Status.STATUS_ACTIVE))
                jtaTx.rollback();

        }
        assertEquals(0, ((int) (cache1.get("key"))));
        assertEquals(0, ((int) (cache2.get("key"))));
        assertEquals(1, ((int) (cache1.get("key1"))));
        assertEquals(2, ((int) (cache2.get("key2"))));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAsyncOpAwait() throws Exception {
        final IgniteCache<String, Integer> cache = jcache();
        GridTestSafeThreadFactory factory = new GridTestSafeThreadFactory("JtaThread");
        final CountDownLatch latch = new CountDownLatch(1);
        Callable<Object> c = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                assertNull(grid(0).transactions().tx());
                UserTransaction jtaTx = AbstractCacheJtaSelfTest.jotm.getUserTransaction();
                jtaTx.begin();
                try {
                    cache.put("key1", 1);
                    cache.putAsync("key", 1);
                    assertEquals(grid(0).transactions().tx().state(), ACTIVE);
                    latch.countDown();
                    info("Before JTA commit.");
                } finally {
                    jtaTx.commit();
                }
                info("After JTA commit.");
                assertEquals(((Integer) (1)), cache.get("key"));
                return null;
            }
        };
        Thread task = factory.newThread(c);
        try (Transaction tx = ignite(0).transactions().txStart(PESSIMISTIC, READ_COMMITTED)) {
            cache.put("key", 0);
            task.start();
            latch.await();
            while ((task.getState()) != (WAITING))
                factory.checkError();

            info("Before cache TX commit.");
            tx.commit();
        }
    }
}

