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
package org.apache.ignite.internal.processors.query.h2;


import java.math.BigInteger;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Ensures that BigInteger can be used as key
 */
public class IgniteSqlBigIntegerKeyTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final String CACHE_NAME = "Mycache";

    /**
     *
     */
    private static final String SELECT_BY_KEY_SQL = "select _val from STUDENT where _key=?";

    /**
     *
     */
    private static final String SELECT_BY_ID_SQL = "select _val from STUDENT where id=?";

    /**
     *
     */
    private static final IgniteSqlBigIntegerKeyTest.Student VALUE_OBJ = new IgniteSqlBigIntegerKeyTest.Student(BigInteger.valueOf(42), "John Doe");

    /**
     *
     */
    @Test
    public void testBigIntegerKeyGet() {
        IgniteCache<Object, Object> cache = getCache();
        Object val = cache.get(BigInteger.valueOf(42));
        assertNotNull(val);
        assertTrue((val instanceof IgniteSqlBigIntegerKeyTest.Student));
        assertEquals(IgniteSqlBigIntegerKeyTest.VALUE_OBJ, val);
        val = cache.get(new BigInteger("42"));
        assertNotNull(val);
        assertTrue((val instanceof IgniteSqlBigIntegerKeyTest.Student));
        assertEquals(IgniteSqlBigIntegerKeyTest.VALUE_OBJ, val);
    }

    /**
     *
     */
    @Test
    public void testBigIntegerKeyQuery() {
        IgniteCache<Object, Object> cache = getCache();
        checkQuery(cache, IgniteSqlBigIntegerKeyTest.SELECT_BY_KEY_SQL, BigInteger.valueOf(42));
        checkQuery(cache, IgniteSqlBigIntegerKeyTest.SELECT_BY_KEY_SQL, new BigInteger("42"));
    }

    /**
     *
     */
    @Test
    public void testBigIntegerFieldQuery() {
        IgniteCache<Object, Object> cache = getCache();
        checkQuery(cache, IgniteSqlBigIntegerKeyTest.SELECT_BY_ID_SQL, BigInteger.valueOf(42));
        checkQuery(cache, IgniteSqlBigIntegerKeyTest.SELECT_BY_ID_SQL, new BigInteger("42"));
    }

    /**
     *
     */
    public static class Student {
        /**
         *
         */
        @QuerySqlField(index = true)
        BigInteger id;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         * Constructor.
         */
        Student(BigInteger id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgniteSqlBigIntegerKeyTest.Student student = ((IgniteSqlBigIntegerKeyTest.Student) (o));
            if (!(id.equals(student.id)))
                return false;

            return name.equals(student.name);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = (31 * result) + (name.hashCode());
            return result;
        }
    }
}

