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
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.query.h2.sql.AbstractH2CompareQueryTest;
import org.apache.ignite.internal.util.tostring.GridToStringInclude;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings({ "unchecked", "PackageVisibleField", "serial" })
public class IgniteCrossCachesJoinsQueryTest extends AbstractH2CompareQueryTest {
    /**
     *
     */
    private static final String PERSON_CACHE_NAME = "person";

    /**
     *
     */
    private static final String ORG_CACHE_NAME = "org";

    /**
     *
     */
    private static final String ACC_CACHE_NAME = "acc";

    /**
     *
     */
    private static final int NODES = 5;

    /**
     *
     */
    private boolean client;

    /**
     *
     */
    private IgniteCrossCachesJoinsQueryTest.Data data;

    /**
     * Tested qry.
     */
    private String qry;

    /**
     * Tested cache.
     */
    private IgniteCache cache;

    /**
     *
     */
    private boolean distributedJoins;

    /**
     *
     */
    private static Random rnd;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDistributedJoins1() throws Exception {
        distributedJoins = true;
        checkAllCacheCombinationsSet1(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDistributedJoins2() throws Exception {
        distributedJoins = true;
        checkAllCacheCombinationsSet2(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDistributedJoins3() throws Exception {
        distributedJoins = true;
        checkAllCacheCombinationsSet3(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCollocatedJoins1() throws Exception {
        distributedJoins = false;
        checkAllCacheCombinationsSet1(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCollocatedJoins2() throws Exception {
        distributedJoins = false;
        checkAllCacheCombinationsSet2(true);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCollocatedJoins3() throws Exception {
        distributedJoins = false;
        checkAllCacheCombinationsSet3(true);
    }

    /**
     *
     */
    private enum TestCacheType {

        /**
         *
         */
        REPLICATED(CacheMode.REPLICATED, 0),
        /**
         *
         */
        PARTITIONED_b0(CacheMode.PARTITIONED, 0),
        /**
         *
         */
        PARTITIONED_b1(CacheMode.PARTITIONED, 1);
        /**
         *
         */
        final CacheMode cacheMode;

        /**
         *
         */
        final int backups;

        /**
         *
         *
         * @param mode
         * 		Cache mode.
         * @param backups
         * 		Backups.
         */
        TestCacheType(CacheMode mode, int backups) {
            cacheMode = mode;
            this.backups = backups;
        }
    }

    /**
     *
     */
    private static class TestCache {
        /**
         *
         */
        @GridToStringInclude
        final String cacheName;

        /**
         *
         */
        @GridToStringInclude
        final IgniteCrossCachesJoinsQueryTest.TestCacheType type;

        /**
         *
         *
         * @param cacheName
         * 		Cache name.
         * @param type
         * 		Cache type.
         */
        public TestCache(String cacheName, IgniteCrossCachesJoinsQueryTest.TestCacheType type) {
            this.cacheName = cacheName;
            this.type = type;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCrossCachesJoinsQueryTest.TestCache.class, this);
        }
    }

    /**
     *
     */
    private static class Data {
        /**
         *
         */
        final List<IgniteCrossCachesJoinsQueryTest.Organization> orgs;

        /**
         *
         */
        final List<IgniteCrossCachesJoinsQueryTest.Person> persons;

        /**
         *
         */
        final List<IgniteCrossCachesJoinsQueryTest.Account> accounts;

        /**
         *
         */
        final Map<Integer, Integer> personsPerOrg;

        /**
         * PersonId to count of accounts that person has.
         */
        final Map<Integer, Integer> accountsPerPerson;

        /**
         *
         */
        final Map<Integer, Integer> accountsPerOrg;

        /**
         *
         */
        final Map<Integer, Integer> maxSalaryPerOrg;

        /**
         *
         *
         * @param orgs
         * 		Organizations.
         * @param persons
         * 		Persons.
         * @param accounts
         * 		Accounts.
         * @param personsPerOrg
         * 		Count of persons per organization.
         * @param accountsPerPerson
         * 		Count of accounts per person.
         * @param accountsPerOrg
         * 		Count of accounts per organization.
         * @param maxSalaryPerOrg
         * 		Maximum salary per organization.
         */
        Data(List<IgniteCrossCachesJoinsQueryTest.Organization> orgs, List<IgniteCrossCachesJoinsQueryTest.Person> persons, List<IgniteCrossCachesJoinsQueryTest.Account> accounts, Map<Integer, Integer> personsPerOrg, Map<Integer, Integer> accountsPerPerson, Map<Integer, Integer> accountsPerOrg, Map<Integer, Integer> maxSalaryPerOrg) {
            this.orgs = orgs;
            this.persons = persons;
            this.accounts = accounts;
            this.personsPerOrg = personsPerOrg;
            this.accountsPerPerson = accountsPerPerson;
            this.accountsPerOrg = accountsPerOrg;
            this.maxSalaryPerOrg = maxSalaryPerOrg;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((((((((((((("Data [" + "orgs=") + (orgs)) + ", persons=") + (persons)) + ", accounts=") + (accounts)) + ", personsPerOrg=") + (personsPerOrg)) + ", accountsPerPerson=") + (accountsPerPerson)) + ", accountsPerOrg=") + (accountsPerOrg)) + ", maxSalaryPerOrg=") + (maxSalaryPerOrg)) + ']';
        }
    }

    /**
     *
     */
    private static class Account implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private int id;

        /**
         *
         */
        @QuerySqlField
        private int personId;

        /**
         *
         */
        @QuerySqlField
        private Date personDateId;

        /**
         *
         */
        @QuerySqlField
        private String personStrId;

        /**
         *
         */
        @QuerySqlField
        private int orgId;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param personId
         * 		Person ID.
         * @param orgId
         * 		Organization ID.
         */
        Account(int id, int personId, int orgId) {
            this.id = id;
            this.personId = personId;
            this.orgId = orgId;
            personDateId = new Date(personId);
            personStrId = "personId" + personId;
        }

        /**
         *
         *
         * @param useCollocatedData
         * 		Use colocated data.
         * @return Key.
         */
        public Object key(boolean useCollocatedData) {
            return useCollocatedData ? new org.apache.ignite.cache.affinity.AffinityKey(id, orgId) : id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((("Account [" + "id=") + (id)) + ", personId=") + (personId)) + ']';
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        @QuerySqlField
        int id;

        /**
         * Date as ID.
         */
        @QuerySqlField
        Date dateId;

        /**
         * String as ID
         */
        @QuerySqlField
        String strId;

        /**
         *
         */
        @QuerySqlField
        int orgId;

        /**
         *
         */
        @QuerySqlField
        Date orgDateId;

        /**
         *
         */
        @QuerySqlField
        String orgStrId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        @QuerySqlField
        int salary;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         * @param salary
         * 		Salary.
         */
        Person(int id, int orgId, String name, int salary) {
            this.id = id;
            dateId = new Date(id);
            strId = "personId" + id;
            this.orgId = orgId;
            orgDateId = new Date(orgId);
            orgStrId = "orgId" + orgId;
            this.name = name;
            this.salary = salary;
        }

        /**
         *
         *
         * @param useCollocatedData
         * 		Use collocated data.
         * @return Key.
         */
        public Object key(boolean useCollocatedData) {
            return useCollocatedData ? new org.apache.ignite.cache.affinity.AffinityKey(id, orgId) : id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((((((((("Person [" + "id=") + (id)) + ", orgId=") + (orgId)) + ", name='") + (name)) + '\'') + ", salary=") + (salary)) + ']';
        }
    }

    /**
     *
     */
    private static class Organization implements Serializable {
        /**
         *
         */
        @QuerySqlField
        int id;

        /**
         *
         */
        @QuerySqlField
        Date dateId;

        /**
         *
         */
        @QuerySqlField
        String strId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param name
         * 		Name.
         */
        Organization(int id, String name) {
            this.id = id;
            dateId = new Date(id);
            strId = "orgId" + id;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((((("Organization [" + "name='") + (name)) + '\'') + ", id=") + (id)) + ']';
        }
    }

    /**
     *
     */
    private static class TestConfig {
        /**
         *
         */
        private final int idx;

        /**
         *
         */
        private final IgniteCache testedCache;

        /**
         *
         */
        private final IgniteCrossCachesJoinsQueryTest.TestCache personCache;

        /**
         *
         */
        private final IgniteCrossCachesJoinsQueryTest.TestCache accCache;

        /**
         *
         */
        private final IgniteCrossCachesJoinsQueryTest.TestCache orgCache;

        /**
         *
         */
        private final String qry;

        /**
         *
         *
         * @param cfgIdx
         * 		Tested configuration index.
         * @param testedCache
         * 		Tested testedCache.
         * @param personCacheType
         * 		Person testedCache personCacheType.
         * @param accCacheType
         * 		Account testedCache personCacheType.
         * @param orgCacheType
         * 		Organization testedCache personCacheType.
         * @param testedQry
         * 		Query.
         */
        TestConfig(int cfgIdx, IgniteCache testedCache, IgniteCrossCachesJoinsQueryTest.TestCache personCacheType, IgniteCrossCachesJoinsQueryTest.TestCache accCacheType, IgniteCrossCachesJoinsQueryTest.TestCache orgCacheType, String testedQry) {
            idx = cfgIdx;
            this.testedCache = testedCache;
            this.personCache = personCacheType;
            this.accCache = accCacheType;
            this.orgCache = orgCacheType;
            qry = testedQry;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((((((((((("TestConfig [" + "idx=") + (idx)) + ", testedCache=") + (testedCache.getName())) + ", personCache=") + (personCache)) + ", accCache=") + (accCache)) + ", orgCache=") + (orgCache)) + ", qry=") + (qry)) + ']';
        }
    }
}

