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
package org.apache.ignite.internal.processors.database;


import QuerySqlField.Group;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.junit.Test;


/**
 *
 */
public class IgniteDbSingleNodeWithIndexingPutGetTest extends IgniteDbSingleNodePutGetTest {
    /**
     *
     */
    @Test
    public void testGroupIndexes() {
        IgniteEx ig = grid(0);
        IgniteCache<Integer, IgniteDbSingleNodeWithIndexingPutGetTest.Abc> cache = ig.cache("abc");
        long cnt = 50000;
        for (int i = 0; i < cnt; i++)
            cache.put(i, new IgniteDbSingleNodeWithIndexingPutGetTest.Abc(i, (i % 10), (i % 100)));

        assertEquals(1, IgniteDbSingleNodeWithIndexingPutGetTest.querySize(cache, "select count(*) from Abc"));
        assertEquals(Long.valueOf(cnt), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc"));
        assertEquals(((int) (cnt)), IgniteDbSingleNodeWithIndexingPutGetTest.querySize(cache, "select count(*) from Abc group by a"));
        assertEquals(Long.valueOf(1L), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc group by a"));
        assertEquals(Long.valueOf(1L), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc where a = 1"));
        assertEquals(10, IgniteDbSingleNodeWithIndexingPutGetTest.querySize(cache, "select count(*) from Abc group by b"));
        assertEquals(Long.valueOf((cnt / 10)), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc group by b"));
        assertEquals(Long.valueOf((cnt / 10)), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc where b = 1"));
        assertEquals(100, IgniteDbSingleNodeWithIndexingPutGetTest.querySize(cache, "select count(*) from Abc group by c"));
        assertEquals(Long.valueOf((cnt / 100)), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc group by c"));
        assertEquals(Long.valueOf((cnt / 100)), IgniteDbSingleNodeWithIndexingPutGetTest.queryOne(cache, "select count(*) from Abc where c = 1"));
    }

    /**
     *
     */
    @Test
    public void testGroupIndexes2() {
        IgniteEx ig = grid(0);
        IgniteCache<Integer, IgniteDbSingleNodeWithIndexingPutGetTest.Abc> cache = ig.cache("abc");
        long cnt = 10000;
        Map<Integer, AtomicLong> as = new TreeMap<>();
        Map<Integer, AtomicLong> bs = new TreeMap<>();
        Map<Integer, AtomicLong> cs = new TreeMap<>();
        Random rnd = ThreadLocalRandom.current();
        for (int i = 0; i < cnt; i++) {
            IgniteDbSingleNodeWithIndexingPutGetTest.Abc abc = new IgniteDbSingleNodeWithIndexingPutGetTest.Abc(rnd.nextInt(2000), rnd.nextInt(100), rnd.nextInt(5));
            // X.println(">> " + i + " " + abc);
            cache.put(i, abc);
            IgniteDbSingleNodeWithIndexingPutGetTest.add(as, abc.a, true);
            IgniteDbSingleNodeWithIndexingPutGetTest.add(bs, abc.b, true);
            IgniteDbSingleNodeWithIndexingPutGetTest.add(cs, abc.c, true);
            if ((rnd.nextInt(1000)) == 0) {
                switch (rnd.nextInt(3)) {
                    case 0 :
                        IgniteDbSingleNodeWithIndexingPutGetTest.check(as, cache, "a");
                        break;
                    case 1 :
                        IgniteDbSingleNodeWithIndexingPutGetTest.check(bs, cache, "b");
                        break;
                    case 2 :
                        IgniteDbSingleNodeWithIndexingPutGetTest.check(cs, cache, "c");
                        break;
                    default :
                        fail();
                }
            }
        }
        IgniteDbSingleNodeWithIndexingPutGetTest.check(as, cache, "a");
        IgniteDbSingleNodeWithIndexingPutGetTest.check(bs, cache, "b");
        IgniteDbSingleNodeWithIndexingPutGetTest.check(cs, cache, "c");
    }

    static class Abc {
        /**
         *
         */
        @QuerySqlField(orderedGroups = // (index = true)
        { @Group(name = "abc", order = 0) })
        private int a;

        /**
         *
         */
        @QuerySqlField(index = true, orderedGroups = { @Group(name = "abc", order = 1), @Group(name = "cb", order = 1) })
        private int b;

        /**
         *
         */
        @QuerySqlField(orderedGroups = // (index = true)
        { @Group(name = "abc", order = 2), @Group(name = "cb", order = 0) })
        private int c;

        /**
         *
         *
         * @param a
         * 		A.
         * @param b
         * 		B.
         * @param c
         * 		C.
         */
        Abc(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteDbSingleNodeWithIndexingPutGetTest.Abc.class, this);
        }
    }
}

