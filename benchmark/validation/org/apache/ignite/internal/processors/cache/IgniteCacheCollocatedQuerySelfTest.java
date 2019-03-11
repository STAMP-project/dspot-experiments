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
package org.apache.ignite.internal.processors.cache;


import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityUuid;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.GridRandom;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheCollocatedQuerySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String QRY = "select productId, sum(price) s, count(1) c " + ((("from Purchase " + "group by productId ") + "having c > ? ") + "order by s desc, productId limit ? ");

    /**
     *
     */
    private static final int PURCHASES = 1000;

    /**
     *
     */
    private static final int PRODUCTS = 10;

    /**
     *
     */
    private static final int MAX_PRICE = 5;

    /**
     *
     */
    private static final long SEED = ThreadLocalRandom.current().nextLong();

    /**
     * Correct affinity.
     */
    @Test
    public void testColocatedQueryRight() {
        IgniteCache<AffinityUuid, IgniteCacheCollocatedQuerySelfTest.Purchase> c = ignite(0).cache(DEFAULT_CACHE_NAME);
        Random rnd = new GridRandom(IgniteCacheCollocatedQuerySelfTest.SEED);
        for (int i = 0; i < (IgniteCacheCollocatedQuerySelfTest.PURCHASES); i++) {
            IgniteCacheCollocatedQuerySelfTest.Purchase p = new IgniteCacheCollocatedQuerySelfTest.Purchase();
            p.productId = rnd.nextInt(IgniteCacheCollocatedQuerySelfTest.PRODUCTS);
            p.price = rnd.nextInt(IgniteCacheCollocatedQuerySelfTest.MAX_PRICE);
            c.put(new AffinityUuid(p.productId), p);// Correct affinity.

        }
        List<List<?>> res1 = IgniteCacheCollocatedQuerySelfTest.query(c, false);
        List<List<?>> res2 = IgniteCacheCollocatedQuerySelfTest.query(c, true);
        X.println(("res1: " + res1));
        X.println(("res2: " + res2));
        assertFalse(res1.isEmpty());
        assertEquals(res1.toString(), res2.toString());// TODO fix type conversion issue

    }

    /**
     * Correct affinity.
     */
    @Test
    public void testColocatedQueryWrong() {
        IgniteCache<AffinityUuid, IgniteCacheCollocatedQuerySelfTest.Purchase> c = ignite(0).cache(DEFAULT_CACHE_NAME);
        Random rnd = new GridRandom(IgniteCacheCollocatedQuerySelfTest.SEED);
        for (int i = 0; i < (IgniteCacheCollocatedQuerySelfTest.PURCHASES); i++) {
            IgniteCacheCollocatedQuerySelfTest.Purchase p = new IgniteCacheCollocatedQuerySelfTest.Purchase();
            p.productId = rnd.nextInt(IgniteCacheCollocatedQuerySelfTest.PRODUCTS);
            p.price = rnd.nextInt(IgniteCacheCollocatedQuerySelfTest.MAX_PRICE);
            c.put(new AffinityUuid(rnd.nextInt(IgniteCacheCollocatedQuerySelfTest.PRODUCTS)), p);// Random affinity.

        }
        List<List<?>> res1 = IgniteCacheCollocatedQuerySelfTest.query(c, false);
        List<List<?>> res2 = IgniteCacheCollocatedQuerySelfTest.query(c, true);
        X.println(("res1: " + res1));
        X.println(("res2: " + res2));
        assertFalse(res1.isEmpty());
        assertFalse(res1.equals(res2));
    }

    /**
     *
     */
    private static class Purchase implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        @QuerySqlField
        int productId;

        /**
         *
         */
        @QuerySqlField
        int price;

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgniteCacheCollocatedQuerySelfTest.Purchase purchase = ((IgniteCacheCollocatedQuerySelfTest.Purchase) (o));
            return ((productId) == (purchase.productId)) && ((price) == (purchase.price));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            int result = productId;
            result = (31 * result) + (price);
            return result;
        }
    }
}

