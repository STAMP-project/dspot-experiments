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


import java.sql.Connection;
import java.sql.Statement;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheDistributedJoinTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static Connection conn;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoins() throws Exception {
        Ignite ignite = ignite(0);
        IgniteCache<Integer, IgniteCacheDistributedJoinTest.A> a = ignite.cache("a");
        IgniteCache<Integer, IgniteCacheDistributedJoinTest.B> b = ignite.cache("b");
        IgniteCache<Integer, IgniteCacheDistributedJoinTest.C> c = ignite.cache("c");
        Statement s = IgniteCacheDistributedJoinTest.conn.createStatement();
        checkSameResult(s, a, "select a.c, b.b, c.a from a.a, b.b, c.c where a.a = b.a and b.c = c.c order by a.c, b.b, c.a");
        checkSameResult(s, b, "select a.a, b.c, c.b from a.a, b.b, c.c where a.b = b.b and b.a = c.a order by a.a, b.c, c.b");
        checkSameResult(s, c, "select a.b, b.a, c.c from a.a, b.b, c.c where a.c = b.c and b.b = c.b order by a.b, b.a, c.c");
        for (int i = 0; i < 150; i++) {
            checkSameResult(s, a, (("select a.c, b.b, c.a from a.a, b.b, c.c where " + i) + " = a.c and a.a = b.a and b.c = c.c order by a.c, b.b, c.a"));
            checkSameResult(s, b, (("select a.a, b.c, c.b from a.a, b.b, c.c where " + i) + " = c.b and a.b = b.b and b.a = c.a order by a.a, b.c, c.b"));
            checkSameResult(s, c, (("select a.b, b.a, c.c from a.a, b.b, c.c where " + i) + " = b.c and a.c = b.c and b.b = c.b order by a.b, b.a, c.c"));
        }
    }

    /**
     *
     */
    public static class X {
        /**
         *
         */
        @QuerySqlField(index = true)
        public long a;

        /**
         *
         */
        @QuerySqlField(index = true)
        public long b;

        /**
         *
         */
        @QuerySqlField(index = true)
        public long c;

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
        public X(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        /**
         *
         */
        public long getA() {
            return a;
        }

        /**
         *
         */
        public long getB() {
            return b;
        }

        /**
         *
         */
        public long getC() {
            return c;
        }
    }

    /**
     *
     */
    public static class A extends IgniteCacheDistributedJoinTest.X {
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
        public A(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     *
     */
    public static class B extends IgniteCacheDistributedJoinTest.X {
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
        public B(int a, int b, int c) {
            super(a, b, c);
        }
    }

    /**
     *
     */
    public static class C extends IgniteCacheDistributedJoinTest.X {
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
        public C(int a, int b, int c) {
            super(a, b, c);
        }
    }
}

