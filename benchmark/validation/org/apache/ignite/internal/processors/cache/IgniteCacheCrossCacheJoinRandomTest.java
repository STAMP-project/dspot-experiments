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
import java.util.Map;
import java.util.Random;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.internal.processors.query.h2.sql.AbstractH2CompareQueryTest;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheCrossCacheJoinRandomTest extends AbstractH2CompareQueryTest {
    /**
     *
     */
    private boolean client;

    /**
     *
     */
    private static final int OBJECTS = 200;

    /**
     *
     */
    private static final int MAX_CACHES = 5;

    /**
     *
     */
    private static Random rnd;

    /**
     *
     */
    private static List<Map<Integer, Integer>> cachesData;

    /**
     *
     */
    private static final List<T2<CacheMode, Integer>> MODES_1 = // new T2<>(REPLICATED, 0),
    F.asList(new T2(CacheMode.PARTITIONED, 0), new T2(CacheMode.PARTITIONED, 1), new T2(CacheMode.PARTITIONED, 2));

    /**
     *
     */
    private static final List<T2<CacheMode, Integer>> MODES_2 = // new T2<>(REPLICATED, 0),
    F.asList(new T2(CacheMode.PARTITIONED, 0), new T2(CacheMode.PARTITIONED, 1));

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin2Caches() throws Exception {
        testJoin(2, IgniteCacheCrossCacheJoinRandomTest.MODES_1);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin3Caches() throws Exception {
        testJoin(3, IgniteCacheCrossCacheJoinRandomTest.MODES_1);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin4Caches() throws Exception {
        testJoin(4, IgniteCacheCrossCacheJoinRandomTest.MODES_2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin5Caches() throws Exception {
        testJoin(5, IgniteCacheCrossCacheJoinRandomTest.MODES_2);
    }

    /**
     *
     */
    static class TestObject implements Serializable {
        /**
         *
         */
        int parentId;

        /**
         *
         *
         * @param parentId
         * 		Parent object ID.
         */
        public TestObject(int parentId) {
            this.parentId = parentId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheCrossCacheJoinRandomTest.TestObject.class, this);
        }
    }
}

