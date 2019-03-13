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
package org.apache.ignite.internal.processors.query.h2.twostep;


import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.Query;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 *
 */
public class CacheQueryMemoryLeakTest extends AbstractIndexingCommonTest {
    /**
     * Check, that query results are not accumulated, when result set size is a multiple of a {@link Query#pageSize}.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testResultIsMultipleOfPage() throws Exception {
        IgniteEx srv = ((IgniteEx) (startGrid("server")));
        Ignite client = startGrid("client");
        IgniteCache<Integer, CacheQueryMemoryLeakTest.Person> cache = CacheQueryMemoryLeakTest.startPeopleCache(client);
        int pages = 3;
        int pageSize = 1024;
        for (int i = 0; i < (pages * pageSize); i++) {
            CacheQueryMemoryLeakTest.Person p = new CacheQueryMemoryLeakTest.Person(("Person #" + i), 25);
            cache.put(i, p);
        }
        for (int i = 0; i < 100; i++) {
            Query<List<?>> qry = new SqlFieldsQuery("select * from people");
            qry.setPageSize(pageSize);
            QueryCursor<List<?>> cursor = cache.query(qry);
            cursor.getAll();
            cursor.close();
        }
        assertTrue("MapNodeResults is not cleared on the map node.", isMapNodeResultsEmpty(srv));
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    public static class Person {
        /**
         *
         */
        @QuerySqlField
        private String name;

        /**
         *
         */
        @QuerySqlField
        private int age;

        /**
         *
         *
         * @param name
         * 		Name.
         * @param age
         * 		Age.
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}

