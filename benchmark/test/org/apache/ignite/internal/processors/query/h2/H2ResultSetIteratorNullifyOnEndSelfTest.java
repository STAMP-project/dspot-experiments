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


import java.util.List;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Test for iterator data link erasure after closing or completing
 */
public class H2ResultSetIteratorNullifyOnEndSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODES_COUNT = 2;

    /**
     *
     */
    private static final int PERSON_COUNT = 20;

    /**
     *
     */
    private static final String SELECT_MAX_SAL_SQLF = "select max(salary) from Person";

    /**
     * Non local SQL Fields check nullification after close
     */
    @Test
    public void testSqlFieldsQueryClose() {
        SqlFieldsQuery qry = new SqlFieldsQuery(H2ResultSetIteratorNullifyOnEndSelfTest.SELECT_MAX_SAL_SQLF);
        QueryCursor<List<?>> qryCurs = cache().query(qry);
        qryCurs.iterator();
        qryCurs.close();
        H2ResultSetIterator h2It = extractGridIteratorInnerH2ResultSetIterator(qryCurs);
        checkIterator(h2It);
    }

    /**
     * Non local SQL Fields check nullification after complete
     */
    @Test
    public void testSqlFieldsQueryComplete() {
        SqlFieldsQuery qry = new SqlFieldsQuery(H2ResultSetIteratorNullifyOnEndSelfTest.SELECT_MAX_SAL_SQLF);
        QueryCursor<List<?>> qryCurs = cache().query(qry);
        qryCurs.getAll();
        H2ResultSetIterator h2It = extractGridIteratorInnerH2ResultSetIterator(qryCurs);
        checkIterator(h2It);
    }

    /**
     * Local SQL Fields check nullification after close
     */
    @Test
    public void testSqlFieldsQueryLocalClose() {
        SqlFieldsQuery qry = new SqlFieldsQuery(H2ResultSetIteratorNullifyOnEndSelfTest.SELECT_MAX_SAL_SQLF);
        qry.setLocal(true);
        QueryCursor<List<?>> qryCurs = cache().query(qry);
        qryCurs.iterator();
        qryCurs.close();
        H2ResultSetIterator h2It = extractGridIteratorInnerH2ResultSetIterator(qryCurs);
        checkIterator(h2It);
    }

    /**
     * Local SQL Fields check nullification after complete
     */
    @Test
    public void testSqlFieldsQueryLocalComplete() {
        SqlFieldsQuery qry = new SqlFieldsQuery(H2ResultSetIteratorNullifyOnEndSelfTest.SELECT_MAX_SAL_SQLF);
        qry.setLocal(true);
        QueryCursor<List<?>> qryCurs = cache().query(qry);
        qryCurs.getAll();
        H2ResultSetIterator h2It = extractGridIteratorInnerH2ResultSetIterator(qryCurs);
        checkIterator(h2It);
    }

    /**
     *
     */
    private static class Person {
        /**
         *
         */
        @QuerySqlField(index = true)
        private String id;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name;

        /**
         *
         */
        @QuerySqlField(index = true)
        private int salary;

        /**
         *
         */
        public String getId() {
            return id;
        }

        /**
         *
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         */
        public String getName() {
            return name;
        }

        /**
         *
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         */
        public int getSalary() {
            return salary;
        }

        /**
         *
         */
        public void setSalary(int salary) {
            this.salary = salary;
        }
    }
}

