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
package org.apache.ignite.internal.processors.cache.index;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.junit.Test;


/**
 *
 */
public class LongIndexNameTest extends AbstractIndexingCommonTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testLongIndexNames() throws Exception {
        try {
            Ignite ignite = startGrid(0);
            IgniteCache cache = insertSomeData(ignite);
            QueryCursor cursor1 = cache.query(new SqlFieldsQuery("SELECT * FROM Person where name like '%Name 0'"));
            QueryCursor cursor1Idx = cache.query(new SqlFieldsQuery("SELECT * FROM Person where name = 'Name 0'"));
            QueryCursor cursor2 = cache.query(new SqlFieldsQuery("SELECT * FROM Person where age like '%0'"));
            QueryCursor cursor2Idx = cache.query(new SqlFieldsQuery("SELECT * FROM Person where age = 0"));
            assertEquals(cursor1.getAll().size(), cursor1Idx.getAll().size());
            assertEquals(cursor2.getAll().size(), cursor2Idx.getAll().size());
            ignite.close();
            Thread.sleep(2000);
            ignite = startGrid(0);
            cache = insertSomeData(ignite);
            cursor1 = cache.query(new SqlFieldsQuery("SELECT * FROM Person where name like '%Name 0'"));
            cursor1Idx = cache.query(new SqlFieldsQuery("SELECT * FROM Person where name = 'Name 0'"));
            cursor2 = cache.query(new SqlFieldsQuery("SELECT * FROM Person where age like '%0'"));
            cursor2Idx = cache.query(new SqlFieldsQuery("SELECT * FROM Person where age = 0"));
            assertEquals(cursor1.getAll().size(), cursor1Idx.getAll().size());
            assertEquals(cursor2.getAll().size(), cursor2Idx.getAll().size());
        } finally {
            stopAllGrids();
        }
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
        public Person() {
            // No-op.
        }

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

        /**
         *
         *
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         *
         * @return Age.
         */
        public int getAge() {
            return age;
        }

        /**
         *
         *
         * @param age
         * 		Age.
         */
        public void setAge(int age) {
            this.age = age;
        }
    }
}

