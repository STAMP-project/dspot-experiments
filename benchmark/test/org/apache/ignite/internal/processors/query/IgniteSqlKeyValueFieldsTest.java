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
package org.apache.ignite.internal.processors.query;


import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Test hidden _key, _val, _ver columns
 */
public class IgniteSqlKeyValueFieldsTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static String NODE_BAD_CONF_MISS_KEY_FIELD = "badConf1";

    /**
     *
     */
    private static String NODE_BAD_CONF_MISS_VAL_FIELD = "badConf2";

    /**
     *
     */
    private static String NODE_CLIENT = "client";

    /**
     *
     */
    private static String CACHE_PERSON_NO_KV = "PersonNoKV";

    /**
     *
     */
    private static String CACHE_INT_NO_KV_TYPE = "IntNoKVType";

    /**
     *
     */
    private static String CACHE_PERSON = "Person";

    /**
     *
     */
    private static String CACHE_JOB = "Job";

    /**
     * Test for setIndexedTypes() primitive types
     */
    @Test
    public void testSetIndexTypesPrimitive() throws Exception {
        IgniteCache<Integer, Integer> cache = grid(IgniteSqlKeyValueFieldsTest.NODE_CLIENT).cache(IgniteSqlKeyValueFieldsTest.CACHE_JOB);
        checkInsert(cache, "insert into Integer (_key, _val) values (?,?)", 1, 100);
        checkSelect(cache, "select * from Integer", 1, 100);
        checkSelect(cache, "select _key, _val from Integer", 1, 100);
    }

    /**
     * Test configuration error : keyFieldName is missing from fields
     */
    @Test
    public void testErrorKeyFieldMissingFromFields() throws Exception {
        checkCacheStartupError(IgniteSqlKeyValueFieldsTest.NODE_BAD_CONF_MISS_KEY_FIELD);
    }

    /**
     * Test configuration error : valueFieldName is missing from fields
     */
    @Test
    public void testErrorValueFieldMissingFromFields() throws Exception {
        checkCacheStartupError(IgniteSqlKeyValueFieldsTest.NODE_BAD_CONF_MISS_VAL_FIELD);
    }

    /**
     * Check that it is allowed to leave QE.keyType and QE.valueType unset
     * in case keyFieldName and valueFieldName are set and present in fields
     */
    @Test
    public void testQueryEntityAutoKeyValTypes() throws Exception {
        IgniteCache<Integer, Integer> cache = grid(IgniteSqlKeyValueFieldsTest.NODE_CLIENT).cache(IgniteSqlKeyValueFieldsTest.CACHE_INT_NO_KV_TYPE);
        checkInsert(cache, "insert into Integer (_key, _val) values (?,?)", 1, 100);
        checkSelect(cache, "select * from Integer where id = 1", 1, 100);
        checkSelect(cache, "select * from Integer", 1, 100);
        checkSelect(cache, "select _key, _val from Integer", 1, 100);
        checkSelect(cache, "select id, v from Integer", 1, 100);
    }

    /**
     * Check that it is possible to not have keyFieldName and valueFieldName
     */
    @Test
    public void testNoKeyValueAliases() throws Exception {
        IgniteCache<Integer, IgniteSqlKeyValueFieldsTest.Person> cache = grid(IgniteSqlKeyValueFieldsTest.NODE_CLIENT).cache(IgniteSqlKeyValueFieldsTest.CACHE_PERSON_NO_KV);
        IgniteSqlKeyValueFieldsTest.Person alice = new IgniteSqlKeyValueFieldsTest.Person("Alice", 1);
        checkInsert(cache, "insert into Person (_key, _val) values (?,?)", 1, alice);
        checkSelect(cache, "select * from Person", alice.name, alice.age);
        checkSelect(cache, "select _key, _val from Person", 1, alice);
    }

    /**
     * Check keyFieldName and valueFieldName columns access
     */
    @Test
    public void testKeyValueAlias() throws Exception {
        // _key, _val, _ver | name, age, id, v
        IgniteSqlKeyValueFieldsTest.Person alice = new IgniteSqlKeyValueFieldsTest.Person("Alice", 1);
        IgniteSqlKeyValueFieldsTest.Person bob = new IgniteSqlKeyValueFieldsTest.Person("Bob", 2);
        IgniteCache<Integer, IgniteSqlKeyValueFieldsTest.Person> cache = grid(IgniteSqlKeyValueFieldsTest.NODE_CLIENT).cache(IgniteSqlKeyValueFieldsTest.CACHE_PERSON);
        checkInsert(cache, "insert into Person (_key, _val) values (?,?)", 1, alice);
        checkInsert(cache, "insert into Person (id, v) values (?,?)", 2, bob);
        checkSelect(cache, "select * from Person where _key=1", alice.name, alice.age, 1, alice);
        checkSelect(cache, "select _key, _val from Person where id=1", 1, alice);
        checkSelect(cache, "select * from Person where _key=2", bob.name, bob.age, 2, bob);
        checkSelect(cache, "select _key, _val from Person where id=2", 2, bob);
        checkInsert(cache, "update Person set age = ? where id = ?", 3, 1);
        checkSelect(cache, "select _key, age from Person where id=1", 1, 3);
        checkInsert(cache, "update Person set v = ? where id = ?", alice, 1);
        checkSelect(cache, "select _key, _val from Person where id=1", 1, alice);
    }

    /**
     * Check that joins are working on keyFieldName, valueFieldName columns
     */
    @Test
    public void testJoinKeyValFields() throws Exception {
        IgniteEx client = grid(IgniteSqlKeyValueFieldsTest.NODE_CLIENT);
        IgniteCache<Integer, IgniteSqlKeyValueFieldsTest.Person> cache = client.cache(IgniteSqlKeyValueFieldsTest.CACHE_PERSON);
        IgniteCache<Integer, Integer> cache2 = client.cache(IgniteSqlKeyValueFieldsTest.CACHE_JOB);
        checkInsert(cache, "insert into Person (id, v) values (?, ?)", 1, new IgniteSqlKeyValueFieldsTest.Person("Bob", 30));
        checkInsert(cache, "insert into Person (id, v) values (?, ?)", 2, new IgniteSqlKeyValueFieldsTest.Person("David", 35));
        checkInsert(cache2, "insert into Integer (_key, _val) values (?, ?)", 100, 1);
        checkInsert(cache2, "insert into Integer (_key, _val) values (?, ?)", 200, 2);
        QueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery((("select p.id, j._key from Person p, \"" + (IgniteSqlKeyValueFieldsTest.CACHE_JOB)) + "\".Integer j where p.id = j._val")));
        List<List<?>> results = cursor.getAll();
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).get(0));
        assertEquals(100, results.get(0).get(1));
        assertEquals(2, results.get(1).get(0));
        assertEquals(200, results.get(1).get(1));
    }

    /**
     * Check automatic addition of index for keyFieldName column
     */
    @Test
    public void testAutoKeyFieldIndex() throws Exception {
        IgniteEx client = grid(IgniteSqlKeyValueFieldsTest.NODE_CLIENT);
        IgniteCache<Integer, IgniteSqlKeyValueFieldsTest.Person> cache = client.cache(IgniteSqlKeyValueFieldsTest.CACHE_PERSON);
        QueryCursor<List<?>> cursor = cache.query(new SqlFieldsQuery("explain select * from Person where id = 1"));
        List<List<?>> results = cursor.getAll();
        assertEquals(1, results.size());
        assertTrue(((String) (results.get(0).get(0))).contains("\"_key_PK_proxy\""));
        cursor = cache.query(new SqlFieldsQuery("explain select * from Person where _key = 1"));
        results = cursor.getAll();
        assertEquals(1, results.size());
        assertTrue(((String) (results.get(0).get(0))).contains("\"_key_PK\""));
    }

    /**
     *
     */
    private static class Person {
        /**
         *
         */
        private String name;

        /**
         *
         */
        private int age;

        /**
         *
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         *
         */
        @Override
        public int hashCode() {
            return (name.hashCode()) ^ (age);
        }

        /**
         *
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof IgniteSqlKeyValueFieldsTest.Person))
                return false;

            IgniteSqlKeyValueFieldsTest.Person other = ((IgniteSqlKeyValueFieldsTest.Person) (o));
            return (name.equals(other.name)) && ((age) == (other.age));
        }
    }
}

