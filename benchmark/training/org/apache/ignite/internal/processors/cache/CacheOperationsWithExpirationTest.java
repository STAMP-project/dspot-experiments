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
import java.util.Date;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class CacheOperationsWithExpirationTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int KEYS = 10000;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAtomicIndexEnabled() throws Exception {
        concurrentPutGetRemoveExpireAndQuery(cacheConfiguration(CacheAtomicityMode.ATOMIC, true));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAtomic() throws Exception {
        concurrentPutGetRemoveExpireAndQuery(cacheConfiguration(CacheAtomicityMode.ATOMIC, false));
    }

    /**
     *
     */
    public enum EnumType1 {

        /**
         *
         */
        TYPE1,
        /**
         *
         */
        TYPE2,
        /**
         *
         */
        TYPE3;
        /**
         *
         */
        static final CacheOperationsWithExpirationTest.EnumType1[] vals = CacheOperationsWithExpirationTest.EnumType1.values();
    }

    /**
     *
     */
    public enum EnumType2 {

        /**
         *
         */
        TYPE1,
        /**
         *
         */
        TYPE2,
        /**
         *
         */
        TYPE3,
        /**
         *
         */
        TYPE4;
        /**
         *
         */
        static final CacheOperationsWithExpirationTest.EnumType2[] vals = CacheOperationsWithExpirationTest.EnumType2.values();
    }

    /**
     *
     */
    public static class TestIndexedType implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         *
         */
        @QuerySqlField
        private final String key;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final String key1;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final String key2;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final String key3;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final int intVal;

        /**
         *
         */
        private final CacheOperationsWithExpirationTest.EnumType1 type1;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final CacheOperationsWithExpirationTest.EnumType2 type2;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final Date date1;

        /**
         *
         */
        @QuerySqlField(index = true)
        private final Date date2;

        /**
         *
         */
        private final Byte byteVal1;

        /**
         *
         */
        private final Byte byteVal2;

        /**
         *
         *
         * @param rnd
         * 		Random value.
         * @param strVal
         * 		Random string value.
         */
        public TestIndexedType(int rnd, String strVal) {
            intVal = rnd;
            key = String.valueOf(rnd);
            key1 = key;
            key2 = strVal;
            key3 = strVal;
            date1 = new Date(rnd);
            date2 = new Date(U.currentTimeMillis());
            type1 = CacheOperationsWithExpirationTest.EnumType1.vals[(rnd % (CacheOperationsWithExpirationTest.EnumType1.vals.length))];
            type2 = CacheOperationsWithExpirationTest.EnumType2.vals[(rnd % (CacheOperationsWithExpirationTest.EnumType2.vals.length))];
            byteVal1 = ((byte) (rnd));
            byteVal2 = ((byte) (rnd));
        }
    }
}

