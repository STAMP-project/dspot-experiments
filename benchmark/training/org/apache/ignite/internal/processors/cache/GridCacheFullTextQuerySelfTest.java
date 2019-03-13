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
import java.util.Calendar;
import java.util.Date;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * FullTest queries left test.
 */
public class GridCacheFullTextQuerySelfTest extends GridCommonAbstractTest {
    /**
     * Cache size.
     */
    private static final int MAX_ITEM_COUNT = 100;

    /**
     * Cache name
     */
    private static final String PERSON_CACHE = "Person";

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQueryWithField() throws Exception {
        checkTextQuery("name:1*", false, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testLocalTextQueryWithKeepBinary() throws Exception {
        checkTextQuery(true, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testLocalTextQuery() throws Exception {
        checkTextQuery(true, false);
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQueryWithKeepBinary() throws Exception {
        checkTextQuery(false, true);
    }

    /**
     *
     *
     * @throws Exception
     * 		In case of error.
     */
    @Test
    public void testTextQuery() throws Exception {
        checkTextQuery(false, true);
    }

    /**
     * Test model class.
     */
    public static class Person implements Serializable {
        /**
         *
         */
        @QueryTextField
        String name;

        /**
         *
         */
        @QuerySqlField(index = true)
        int age;

        /**
         *
         */
        @QuerySqlField
        final Date birthday;

        /**
         * Constructor
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age % 2000;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, (-age));
            birthday = cal.getTime();
        }
    }
}

