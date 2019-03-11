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
package org.apache.ignite.internal.processors.cache.transaction;


import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.GridTestUtils.SystemProperty;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests DML allow/disallow operation inside transaction.
 */
public class DmlInsideTransactionTest extends GridCommonAbstractTest {
    /**
     * Person cache name.
     */
    private static final String CACHE_PERSON = "PersonCache";

    /**
     * Set of DML queries for tests.
     */
    private static final String[] DML_QUERIES = new String[]{ "MERGE INTO TEST.Person(id, name, orgId) VALUES(111,'NAME',111)", "INSERT INTO TEST.Person(id, name, orgId) VALUES(222,'NAME',111)", "UPDATE TEST.Person SET name='new name'", "DELETE TEST.Person WHERE id=1", "INSERT INTO TEST.Person(id, name, orgId) SELECT id+1000, name, orgId FROM TEST.Person" };

    /**
     * Checking correct behaviour for DML inside transaction by default.
     *
     * @throws Exception
     * 		In case failure.
     */
    @Test
    public void testDmlInTransactionByDefault() throws Exception {
        prepareIgnite();
        for (String dmlQuery : DmlInsideTransactionTest.DML_QUERIES) {
            runDmlSqlFieldsQueryInTransactionTest(dmlQuery, false, false);
            runDmlSqlFieldsQueryInTransactionTest(dmlQuery, true, false);
        }
    }

    /**
     * Checking correct behaviour for DML inside transaction when compatibility property set as disabled.
     *
     * @throws Exception
     * 		In case failure.
     */
    @Test
    public void testDmlInTransactionInDisabledCompatibilityMode() throws Exception {
        try (SystemProperty ignored = new SystemProperty(IgniteSystemProperties.IGNITE_ALLOW_DML_INSIDE_TRANSACTION, "false")) {
            prepareIgnite();
            for (String dmlQuery : DmlInsideTransactionTest.DML_QUERIES) {
                runDmlSqlFieldsQueryInTransactionTest(dmlQuery, false, false);
                runDmlSqlFieldsQueryInTransactionTest(dmlQuery, true, false);
            }
        }
    }

    /**
     * Checking correct behaviour for DML inside transaction when compatibility property set as enabled.
     *
     * @throws Exception
     * 		In case failure.
     */
    @Test
    public void testDmlInTransactionInCompatibilityMode() throws Exception {
        try (SystemProperty ignored = new SystemProperty(IgniteSystemProperties.IGNITE_ALLOW_DML_INSIDE_TRANSACTION, "true")) {
            prepareIgnite();
            for (String dmlQuery : DmlInsideTransactionTest.DML_QUERIES) {
                runDmlSqlFieldsQueryInTransactionTest(dmlQuery, false, true);
                runDmlSqlFieldsQueryInTransactionTest(dmlQuery, true, true);
            }
        }
    }

    /**
     * Checking that DML can be executed without a errors outside transaction.
     *
     * @throws Exception
     * 		In case failure.
     */
    @Test
    public void testDmlNotInTransaction() throws Exception {
        prepareIgnite();
        for (String dmlQuery : DmlInsideTransactionTest.DML_QUERIES) {
            grid(0).cache(DmlInsideTransactionTest.CACHE_PERSON).query(new SqlFieldsQuery(dmlQuery));
            grid(0).cache(DmlInsideTransactionTest.CACHE_PERSON).clear();
            grid(0).cache(DmlInsideTransactionTest.CACHE_PERSON).query(new SqlFieldsQuery(dmlQuery).setLocal(true));
        }
    }

    /**
     * Person key.
     */
    public static class PersonKey {
        /**
         * ID.
         */
        @QuerySqlField
        public long id;

        /**
         * Constructor.
         *
         * @param id
         * 		ID.
         */
        PersonKey(long id) {
            this.id = id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((int) (id));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof DmlInsideTransactionTest.PersonKey) && (F.eq(id, ((DmlInsideTransactionTest.PersonKey) (obj)).id));
        }
    }

    /**
     * Person.
     */
    public static class Person {
        /**
         * Name.
         */
        @QuerySqlField
        public String name;

        /**
         * Organization ID.
         */
        @QuerySqlField(index = true)
        public long orgId;

        /**
         * Constructor.
         *
         * @param name
         * 		Name.
         * @param orgId
         * 		Organization ID.
         */
        public Person(String name, long orgId) {
            this.name = name;
            this.orgId = orgId;
        }
    }
}

