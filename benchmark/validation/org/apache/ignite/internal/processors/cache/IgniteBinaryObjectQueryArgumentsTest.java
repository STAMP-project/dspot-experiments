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


import Cache.Entry;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteBinaryObjectQueryArgumentsTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final int NODES = 3;

    /**
     *
     */
    private static final String PRIM_CACHE = "prim-cache";

    /**
     *
     */
    private static final String STR_CACHE = "str-cache";

    /**
     *
     */
    private static final String ENUM_CACHE = "enum-cache";

    /**
     *
     */
    private static final String UUID_CACHE = "uuid-cache";

    /**
     *
     */
    private static final String DATE_CACHE = "date-cache";

    /**
     *
     */
    private static final String TIMESTAMP_CACHE = "timestamp-cache";

    /**
     *
     */
    private static final String BIG_DECIMAL_CACHE = "decimal-cache";

    /**
     *
     */
    private static final String OBJECT_CACHE = "obj-cache";

    /**
     *
     */
    private static final String FIELD_CACHE = "field-cache";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testObjectArgument() throws Exception {
        testKeyQuery(IgniteBinaryObjectQueryArgumentsTest.OBJECT_CACHE, new IgniteBinaryObjectQueryArgumentsTest.TestKey(1), new IgniteBinaryObjectQueryArgumentsTest.TestKey(2));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPrimitiveObjectArgument() throws Exception {
        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.PRIM_CACHE, 1, 2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStringObjectArgument() throws Exception {
        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.STR_CACHE, "str1", "str2");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEnumObjectArgument() throws Exception {
        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.ENUM_CACHE, IgniteBinaryObjectQueryArgumentsTest.EnumKey.KEY1, IgniteBinaryObjectQueryArgumentsTest.EnumKey.KEY2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testUuidObjectArgument() throws Exception {
        final UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        while (uuid1.equals(uuid2))
            uuid2 = UUID.randomUUID();

        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.UUID_CACHE, uuid1, uuid2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDateObjectArgument() throws Exception {
        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.DATE_CACHE, new Date(0), new Date(1));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTimestampArgument() throws Exception {
        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.TIMESTAMP_CACHE, new Timestamp(0), new Timestamp(1));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBigDecimalArgument() throws Exception {
        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final BigDecimal bd1 = new BigDecimal(rnd.nextDouble());
        BigDecimal bd2 = new BigDecimal(rnd.nextDouble());
        while (bd1.equals(bd2))
            bd2 = new BigDecimal(rnd.nextDouble());

        testKeyValQuery(IgniteBinaryObjectQueryArgumentsTest.BIG_DECIMAL_CACHE, bd1, bd2);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testFieldSearch() throws Exception {
        final IgniteCache<Integer, IgniteBinaryObjectQueryArgumentsTest.SearchValue> cache = ignite(0).cache(IgniteBinaryObjectQueryArgumentsTest.FIELD_CACHE);
        final Map<Integer, IgniteBinaryObjectQueryArgumentsTest.SearchValue> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, new IgniteBinaryObjectQueryArgumentsTest.SearchValue(UUID.randomUUID(), String.valueOf(i), new BigDecimal((i * 0.1)), i, new Date(i), new Timestamp(i), new IgniteBinaryObjectQueryArgumentsTest.Person(String.valueOf(("name-" + i))), ((i % 2) == 0 ? IgniteBinaryObjectQueryArgumentsTest.EnumKey.KEY1 : IgniteBinaryObjectQueryArgumentsTest.EnumKey.KEY2)));
        }
        cache.putAll(map);
        SqlQuery<Integer, IgniteBinaryObjectQueryArgumentsTest.SearchValue> qry = new SqlQuery(IgniteBinaryObjectQueryArgumentsTest.SearchValue.class, "where uuid=? and str=? and decimal=? and integer=? and date=? and ts=? and person=? and enumKey=?");
        final int k = ThreadLocalRandom.current().nextInt(10);
        final IgniteBinaryObjectQueryArgumentsTest.SearchValue val = map.get(k);
        qry.setLocal(isLocal());
        qry.setArgs(val.uuid, val.str, val.decimal, val.integer, val.date, val.ts, val.person, val.enumKey);
        final List<Entry<Integer, IgniteBinaryObjectQueryArgumentsTest.SearchValue>> res = cache.query(qry).getAll();
        assertEquals(1, res.size());
        assertEquals(val.integer, res.get(0).getKey());
        assertEquals(val, res.get(0).getValue());
    }

    /**
     *
     */
    private static class Person {
        /**
         *
         */
        String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public Person(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(final Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            final IgniteBinaryObjectQueryArgumentsTest.Person person = ((IgniteBinaryObjectQueryArgumentsTest.Person) (o));
            return (name) != null ? name.equals(person.name) : (person.name) == null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteBinaryObjectQueryArgumentsTest.Person.class, this);
        }
    }

    /**
     *
     */
    public static class TestKey {
        /**
         *
         */
        private int id;

        /**
         *
         *
         * @param id
         * 		Key.
         */
        TestKey(int id) {
            this.id = id;
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

            IgniteBinaryObjectQueryArgumentsTest.TestKey other = ((IgniteBinaryObjectQueryArgumentsTest.TestKey) (o));
            return (id) == (other.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     *
     */
    private enum EnumKey {

        /**
         *
         */
        KEY1,
        /**
         *
         */
        KEY2;}

    /**
     *
     */
    private static class SearchValue {
        /**
         *
         */
        @QuerySqlField
        private UUID uuid;

        /**
         *
         */
        @QuerySqlField
        private String str;

        /**
         *
         */
        @QuerySqlField
        private BigDecimal decimal;

        /**
         *
         */
        @QuerySqlField
        private Integer integer;

        /**
         *
         */
        @QuerySqlField
        private Date date;

        /**
         *
         */
        @QuerySqlField
        private Timestamp ts;

        /**
         *
         */
        @QuerySqlField
        private IgniteBinaryObjectQueryArgumentsTest.Person person;

        /**
         *
         */
        @QuerySqlField
        private IgniteBinaryObjectQueryArgumentsTest.EnumKey enumKey;

        /**
         *
         *
         * @param uuid
         * 		UUID.
         * @param str
         * 		String.
         * @param decimal
         * 		Decimal.
         * @param integer
         * 		Integer.
         * @param date
         * 		Date.
         * @param ts
         * 		Timestamp.
         * @param person
         * 		Person.
         * @param enumKey
         * 		Enum.
         */
        SearchValue(final UUID uuid, final String str, final BigDecimal decimal, final Integer integer, final Date date, final Timestamp ts, final IgniteBinaryObjectQueryArgumentsTest.Person person, final IgniteBinaryObjectQueryArgumentsTest.EnumKey enumKey) {
            this.uuid = uuid;
            this.str = str;
            this.decimal = decimal;
            this.integer = integer;
            this.date = date;
            this.ts = ts;
            this.person = person;
            this.enumKey = enumKey;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(final Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            final IgniteBinaryObjectQueryArgumentsTest.SearchValue that = ((IgniteBinaryObjectQueryArgumentsTest.SearchValue) (o));
            if ((uuid) != null ? !(uuid.equals(that.uuid)) : (that.uuid) != null)
                return false;

            if ((str) != null ? !(str.equals(that.str)) : (that.str) != null)
                return false;

            if ((decimal) != null ? !(decimal.equals(that.decimal)) : (that.decimal) != null)
                return false;

            if ((integer) != null ? !(integer.equals(that.integer)) : (that.integer) != null)
                return false;

            if ((date) != null ? !(date.equals(that.date)) : (that.date) != null)
                return false;

            if ((ts) != null ? !(ts.equals(that.ts)) : (that.ts) != null)
                return false;

            if ((person) != null ? !(person.equals(that.person)) : (that.person) != null)
                return false;

            return (enumKey) == (that.enumKey);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            int res = ((uuid) != null) ? uuid.hashCode() : 0;
            res = (31 * res) + ((str) != null ? str.hashCode() : 0);
            res = (31 * res) + ((decimal) != null ? decimal.hashCode() : 0);
            res = (31 * res) + ((integer) != null ? integer.hashCode() : 0);
            res = (31 * res) + ((date) != null ? date.hashCode() : 0);
            res = (31 * res) + ((ts) != null ? ts.hashCode() : 0);
            res = (31 * res) + ((person) != null ? person.hashCode() : 0);
            res = (31 * res) + ((enumKey) != null ? enumKey.hashCode() : 0);
            return res;
        }
    }
}

