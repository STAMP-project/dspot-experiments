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


import java.util.Arrays;
import java.util.List;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.internal.processors.query.IgniteSQLException;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Base class for testing work of combinations of DML and DDL operations.
 */
public abstract class H2DynamicIndexingComplexAbstractTest extends DynamicIndexAbstractSelfTest {
    /**
     * Cache mode to test with.
     */
    private final CacheMode cacheMode;

    /**
     * Cache atomicity mode to test with.
     */
    private final CacheAtomicityMode atomicityMode;

    /**
     * Node index to initiate operations from.
     */
    private final int nodeIdx;

    /**
     * Backups to configure
     */
    private final int backups;

    /**
     * Names of companies to use.
     */
    private static final List<String> COMPANIES = Arrays.asList("ASF", "GNU", "BSD");

    /**
     * Cities to use.
     */
    private static final List<String> CITIES = Arrays.asList("St. Petersburg", "Boston", "Berkeley", "London");

    /**
     * Index of server node.
     */
    protected static final int SRV_IDX = 0;

    /**
     * Index of client node.
     */
    protected static final int CLIENT_IDX = 1;

    /**
     * Constructor.
     *
     * @param cacheMode
     * 		Cache mode.
     * @param atomicityMode
     * 		Cache atomicity mode.
     * @param backups
     * 		Number of backups.
     * @param nodeIdx
     * 		Node index.
     */
    H2DynamicIndexingComplexAbstractTest(CacheMode cacheMode, CacheAtomicityMode atomicityMode, int backups, int nodeIdx) {
        this.cacheMode = cacheMode;
        this.atomicityMode = atomicityMode;
        this.backups = backups;
        this.nodeIdx = nodeIdx;
    }

    /**
     * Do test.
     */
    @Test
    public void testOperations() {
        executeSql(((((((("CREATE TABLE person (id int, name varchar, age int, company varchar, city varchar, " + "primary key (id, name, city)) WITH \"template=") + (cacheMode.name())) + ",atomicity=") + (atomicityMode.name())) + ",backups=") + (backups)) + ",affinity_key=city\""));
        executeSql("CREATE INDEX idx on person (city asc, name asc)");
        executeSql(((((((("CREATE TABLE city (name varchar, population int, primary key (name)) WITH " + "\"template=") + (cacheMode.name())) + ",atomicity=") + (atomicityMode.name())) + ",backups=") + (backups)) + ",affinity_key=name\""));
        executeSql("INSERT INTO city (name, population) values(?, ?), (?, ?), (?, ?)", "St. Petersburg", 6000000, "Boston", 2000000, "London", 8000000);
        final long PERSON_COUNT = 100;
        for (int i = 0; i < PERSON_COUNT; i++)
            executeSql("INSERT INTO person (id, name, age, company, city) values (?, ?, ?, ?, ?)", i, ("Person " + i), (20 + (i % 10)), H2DynamicIndexingComplexAbstractTest.COMPANIES.get((i % (H2DynamicIndexingComplexAbstractTest.COMPANIES.size()))), H2DynamicIndexingComplexAbstractTest.CITIES.get((i % (H2DynamicIndexingComplexAbstractTest.CITIES.size()))));

        assertAllPersons(new org.apache.ignite.lang.IgniteInClosure<List<?>>() {
            @Override
            public void apply(List<?> person) {
                assertInitPerson(person);
            }
        });
        long r = ((Long) (executeSqlSingle("SELECT COUNT(*) from Person")));
        assertEquals(PERSON_COUNT, r);
        r = ((Long) (executeSqlSingle("SELECT COUNT(*) from Person p inner join City c on p.city = c.name")));
        // Berkeley is not present in City table, although 25 people have it specified as their city.
        assertEquals(75L, r);
        executeSqlSingle(("UPDATE Person SET company = 'GNU', age = CASE WHEN MOD(id, 2) <> 0 THEN age + 5 ELSE " + "age + 1 END WHERE company = 'ASF'"));
        assertAllPersons(new org.apache.ignite.lang.IgniteInClosure<List<?>>() {
            @Override
            public void apply(List<?> person) {
                int id = ((Integer) (person.get(0)));
                if ((id % (H2DynamicIndexingComplexAbstractTest.COMPANIES.size())) == 0) {
                    int initAge = 20 + (id % 10);
                    int expAge = ((initAge % 2) != 0) ? initAge + 5 : initAge + 1;
                    assertPerson(id, ("Person " + id), expAge, "GNU", H2DynamicIndexingComplexAbstractTest.CITIES.get((id % (H2DynamicIndexingComplexAbstractTest.CITIES.size()))), person);
                } else
                    assertInitPerson(person);

            }
        });
        executeSql("DROP INDEX idx");
        // Index drop should not affect data.
        assertAllPersons(new org.apache.ignite.lang.IgniteInClosure<List<?>>() {
            @Override
            public void apply(List<?> person) {
                int id = ((Integer) (person.get(0)));
                if ((id % (H2DynamicIndexingComplexAbstractTest.COMPANIES.size())) == 0) {
                    int initAge = 20 + (id % 10);
                    int expAge = ((initAge % 2) != 0) ? initAge + 5 : initAge + 1;
                    assertPerson(id, ("Person " + id), expAge, "GNU", H2DynamicIndexingComplexAbstractTest.CITIES.get((id % (H2DynamicIndexingComplexAbstractTest.CITIES.size()))), person);
                } else
                    assertInitPerson(person);

            }
        });
        // Let's drop all BSD folks living in Berkeley and Boston - this compares ASCII codes of 1st symbols.
        executeSql("DELETE FROM person WHERE ASCII(company) = ASCII(city)");
        assertAllPersons(new org.apache.ignite.lang.IgniteInClosure<List<?>>() {
            @Override
            public void apply(List<?> person) {
                String city = H2DynamicIndexingComplexAbstractTest.city(person);
                String company = H2DynamicIndexingComplexAbstractTest.company(person);
                assertFalse(((city.charAt(0)) == (company.charAt(0))));
            }
        });
        assertNotNull(node().cache("SQL_PUBLIC_PERSON"));
        executeSql("DROP TABLE person");
        assertNull(node().cache("SQL_PUBLIC_PERSON"));
        GridTestUtils.assertThrows(null, new org.apache.ignite.lang.IgniteCallable<Object>() {
            @Override
            public Object call() throws Exception {
                return executeSql("SELECT * from Person");
            }
        }, IgniteSQLException.class, "Table \"PERSON\" not found");
    }
}

