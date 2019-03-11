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
package org.apache.ignite.internal.processors.query.h2.sql;


import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Base set of queries to compare query results from h2 database instance and mixed ignite caches (replicated and partitioned)
 * which have the same data models and data content.
 */
public class BaseH2CompareQueryTest extends AbstractH2CompareQueryTest {
    /**
     * Org count.
     */
    public static final int ORG_CNT = 30;

    /**
     * Address count.
     */
    public static final int ADDR_CNT = 10;

    /**
     * Person count.
     */
    public static final int PERS_CNT = 50;

    /**
     * Product count.
     */
    public static final int PROD_CNT = 100;

    /**
     * Purchase count.
     */
    public static final int PURCH_CNT = (BaseH2CompareQueryTest.PROD_CNT) * 5;

    /**
     *
     */
    protected static final String ORG = "org";

    /**
     *
     */
    protected static final String PERS = "pers";

    /**
     *
     */
    protected static final String PURCH = "purch";

    /**
     *
     */
    protected static final String PROD = "prod";

    /**
     *
     */
    protected static final String ADDR = "addr";

    /**
     * Cache org.
     */
    private static IgniteCache<Integer, BaseH2CompareQueryTest.Organization> cacheOrg;

    /**
     * Cache pers.
     */
    private static IgniteCache<AffinityKey, BaseH2CompareQueryTest.Person> cachePers;

    /**
     * Cache purch.
     */
    private static IgniteCache<AffinityKey, BaseH2CompareQueryTest.Purchase> cachePurch;

    /**
     * Cache prod.
     */
    private static IgniteCache<Integer, BaseH2CompareQueryTest.Product> cacheProd;

    /**
     * Cache address.
     */
    private static IgniteCache<Integer, BaseH2CompareQueryTest.Address> cacheAddr;

    /**
     *
     */
    @Test
    public void testSelectStar() {
        assertEquals(1, BaseH2CompareQueryTest.cachePers.query(new org.apache.ignite.cache.query.SqlQuery<AffinityKey<?>, BaseH2CompareQueryTest.Person>(BaseH2CompareQueryTest.Person.class, "\t\r\n  select  \n*\t from Person limit 1")).getAll().size());
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                BaseH2CompareQueryTest.cachePers.query(new org.apache.ignite.cache.query.SqlQuery(BaseH2CompareQueryTest.Person.class, "SELECT firstName from PERSON"));
                return null;
            }
        }, CacheException.class, null);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testInvalidQuery() throws Exception {
        final SqlFieldsQuery sql = new SqlFieldsQuery("SELECT firstName from Person where id <> ? and orgId <> ?");
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                BaseH2CompareQueryTest.cachePers.query(sql.setArgs(3)).getAll();
                return null;
            }
        }, CacheException.class, "Invalid number of query parameters");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testParamSubstitution() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, "select ? from \"pers\".Person", "Some arg");
    }

    /**
     *
     *
     * @throws SQLException
     * 		If failed.
     */
    @Test
    public void testAggregateOrderBy() throws SQLException {
        compareOrderedQueryRes0(BaseH2CompareQueryTest.cachePers, ("select firstName name, count(*) cnt from \"pers\".Person " + "group by name order by cnt, name desc"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNullParamSubstitution() throws Exception {
        List<List<?>> rs1 = compareQueryRes0(BaseH2CompareQueryTest.cachePers, "select ? from \"pers\".Person", null);
        // Ensure we find something.
        assertFalse(rs1.isEmpty());
    }

    /**
     *
     */
    @Test
    public void testUnion() throws SQLException {
        String base = "select _val v from \"pers\".Person";
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ((base + " union all ") + base));
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ((base + " union ") + base));
        base = "select firstName||lastName name, salary from \"pers\".Person";
        assertEquals(((BaseH2CompareQueryTest.PERS_CNT) * 2), compareOrderedQueryRes0(BaseH2CompareQueryTest.cachePers, (((base + " union all ") + base) + " order by salary desc")).size());
        assertEquals(BaseH2CompareQueryTest.PERS_CNT, compareOrderedQueryRes0(BaseH2CompareQueryTest.cachePers, (((base + " union ") + base) + " order by salary desc")).size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testEmptyResult() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, "select id from \"pers\".Person where 0 = 1");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlQueryWithAggregation() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ("select avg(salary) from \"pers\".Person, \"org\".Organization " + ("where Person.orgId = Organization.id and " + "lower(Organization.name) = lower(?)")), "Org1");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlFieldsQuery() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, "select concat(firstName, \' \', lastName) from \"pers\".Person");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSqlFieldsQueryWithJoin() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ("select concat(firstName, ' ', lastName), " + ("Organization.name from \"pers\".Person, \"org\".Organization where " + "Person.orgId = Organization.id")));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testOrdered() throws Exception {
        compareOrderedQueryRes0(BaseH2CompareQueryTest.cachePers, ("select firstName, lastName" + (" from \"pers\".Person" + " order by lastName, firstName")));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleJoin() throws Exception {
        // Have expected results.
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, String.format(("select id, firstName, lastName" + ("  from \"%s\".Person" + "  where Person.id = ?")), BaseH2CompareQueryTest.cachePers.getName()), 3);
        // Ignite cache return 0 results...
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ("select pe.firstName" + ("  from \"pers\".Person pe join \"purch\".Purchase pu on pe.id = pu.personId " + "  where pe.id = ?")), 3);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleReplicatedSelect() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cacheProd, "select id, name from \"prod\".Product");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCrossCache() throws Exception {
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ("select firstName, lastName" + ("  from \"pers\".Person, \"purch\".Purchase" + "  where Person.id = Purchase.personId")));
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ("select concat(firstName, ' ', lastName), Product.name " + (("  from \"pers\".Person, \"purch\".Purchase, \"prod\".Product " + "  where Person.id = Purchase.personId and Purchase.productId = Product.id") + "  group by Product.id")));
        compareQueryRes0(BaseH2CompareQueryTest.cachePers, ("select concat(firstName, ' ', lastName), count (Product.id) " + (("  from \"pers\".Person, \"purch\".Purchase, \"prod\".Product " + "  where Person.id = Purchase.personId and Purchase.productId = Product.id") + "  group by Product.id")));
    }

    /**
     * Person class. Stored at partitioned cache.
     */
    private static class Person implements Serializable {
        /**
         * Person ID (indexed).
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Organization ID (indexed).
         */
        @QuerySqlField(index = true)
        private int orgId;

        /**
         * First name (not-indexed).
         */
        @QuerySqlField
        private String firstName;

        /**
         * Last name (not indexed).
         */
        @QuerySqlField
        private String lastName;

        /**
         * Salary (indexed).
         */
        @QuerySqlField(index = true)
        private double salary;

        /**
         * Address Id (indexed).
         */
        @QuerySqlField(index = true)
        private int addrId;

        /**
         * Date.
         */
        @QuerySqlField(index = true)
        public Date date = new Date(System.currentTimeMillis());

        /**
         * Old.
         */
        @QuerySqlField(index = true)
        public int old = 17;

        /**
         * Constructs person record.
         *
         * @param org
         * 		Organization.
         * @param firstName
         * 		First name.
         * @param lastName
         * 		Last name.
         * @param salary
         * 		Salary.
         */
        Person(int id, BaseH2CompareQueryTest.Organization org, String firstName, String lastName, double salary, BaseH2CompareQueryTest.Address addr) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.salary = salary;
            orgId = org.id;
            addrId = addr.id;
        }

        /**
         *
         *
         * @return Custom affinity key to guarantee that person is always collocated with organization.
         */
        public AffinityKey<Integer> key() {
            return new AffinityKey(id, orgId);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof BaseH2CompareQueryTest.Person) && ((id) == (((BaseH2CompareQueryTest.Person) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((((((((((("Person [firstName=" + (firstName)) + ", lastName=") + (lastName)) + ", id=") + (id)) + ", orgId=") + (orgId)) + ", salary=") + (salary)) + ", addrId=") + (addrId)) + ']';
        }
    }

    /**
     * Organization class. Stored at partitioned cache.
     */
    private static class Organization implements Serializable {
        /**
         * Organization ID (indexed).
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Organization name (indexed).
         */
        @QuerySqlField(index = true)
        private String name;

        /**
         * Create Organization.
         *
         * @param id
         * 		Organization ID.
         * @param name
         * 		Organization name.
         */
        Organization(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof BaseH2CompareQueryTest.Organization) && ((id) == (((BaseH2CompareQueryTest.Organization) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((("Organization [id=" + (id)) + ", name=") + (name)) + ']';
        }
    }

    /**
     * Product class. Stored at replicated cache.
     */
    private static class Product implements Serializable {
        /**
         * Primary key.
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Product name.
         */
        @QuerySqlField
        private String name;

        /**
         * Product price
         */
        @QuerySqlField
        private int price;

        /**
         * Create Product.
         *
         * @param id
         * 		Product ID.
         * @param name
         * 		Product name.
         * @param price
         * 		Product price.
         */
        Product(int id, String name, int price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof BaseH2CompareQueryTest.Product) && ((id) == (((BaseH2CompareQueryTest.Product) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((((("Product [id=" + (id)) + ", name=") + (name)) + ", price=") + (price)) + ']';
        }
    }

    /**
     * Purchase class. Stored at partitioned cache.
     */
    private static class Purchase implements Serializable {
        /**
         * Primary key.
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Product ID.
         */
        @QuerySqlField
        private int productId;

        /**
         * Person ID.
         */
        @QuerySqlField
        private int personId;

        /**
         * Organization id.
         */
        @QuerySqlField
        private int organizationId;

        /**
         * Create Purchase.
         *
         * @param id
         * 		Purchase ID.
         * @param product
         * 		Purchase product.
         * @param organizationId
         * 		Organization Id.
         * @param person
         * 		Purchase person.
         */
        Purchase(int id, BaseH2CompareQueryTest.Product product, int organizationId, BaseH2CompareQueryTest.Person person) {
            this.id = id;
            productId = product.id;
            personId = person.id;
            this.organizationId = organizationId;
        }

        /**
         *
         *
         * @return Custom affinity key to guarantee that purchase is always collocated with person.
         */
        public AffinityKey<Integer> key() {
            return new AffinityKey(id, organizationId);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof BaseH2CompareQueryTest.Purchase) && ((id) == (((BaseH2CompareQueryTest.Purchase) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((((("Purchase [id=" + (id)) + ", productId=") + (productId)) + ", personId=") + (personId)) + ']';
        }
    }

    /**
     * Address class. Stored at replicated cache.
     */
    private static class Address implements Serializable {
        @QuerySqlField(index = true)
        private int id;

        @QuerySqlField(index = true)
        private String street;

        Address(int id, String street) {
            this.id = id;
            this.street = street;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof BaseH2CompareQueryTest.Address) && ((id) == (((BaseH2CompareQueryTest.Address) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((("Address [id=" + (id)) + ", street=") + (street)) + ']';
        }
    }
}

