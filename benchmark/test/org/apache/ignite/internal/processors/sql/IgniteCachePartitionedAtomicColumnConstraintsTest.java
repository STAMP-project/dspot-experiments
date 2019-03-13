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
package org.apache.ignite.internal.processors.sql;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.Consumer;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.util.typedef.T2;
import org.junit.Test;


/**
 *
 */
public class IgniteCachePartitionedAtomicColumnConstraintsTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final long FUT_TIMEOUT = 10000L;

    /**
     *
     */
    private static final String STR_CACHE_NAME = "STR_STR";

    /**
     *
     */
    private static final String STR_ORG_CACHE_NAME = "STR_ORG";

    /**
     *
     */
    private static final String STR_ORG_WITH_FIELDS_CACHE_NAME = "STR_ORG_WITH_FIELDS";

    /**
     *
     */
    private static final String OBJ_CACHE_NAME = "ORG_ADDRESS";

    /**
     *
     */
    private static final String DEC_CACHE_NAME_FOR_SCALE = "DEC_DEC_FOR_SCALE";

    /**
     *
     */
    private static final String OBJ_CACHE_NAME_FOR_SCALE = "ORG_EMPLOYEE_FOR_SCALE";

    /**
     *
     */
    private static final String DEC_EMPL_CACHE_NAME_FOR_SCALE = "DEC_EMPLOYEE_FOR_SCALE";

    /**
     *
     */
    private static final String DEC_CACHE_NAME_FOR_PREC = "DEC_DEC_FOR_PREC";

    /**
     *
     */
    private static final String OBJ_CACHE_NAME_FOR_PREC = "ORG_EMPLOYEE_FOR_PREC";

    /**
     *
     */
    private static final String DEC_EMPL_CACHE_NAME_FOR_PREC = "DEC_EMPLOYEE_FOR_PREC";

    /**
     *
     */
    private Consumer<Runnable> shouldFail = ( op) -> assertThrowsWithCause(op, IgniteException.class);

    /**
     *
     */
    private Consumer<Runnable> shouldSucceed = Runnable::run;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongStringValueFail() throws Exception {
        IgniteCache<String, String> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.STR_CACHE_NAME);
        T2<String, String> val = new T2("3", "123456");
        checkPutAll(shouldFail, cache, new T2("1", "1"), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, "1");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongStringKeyFail() throws Exception {
        IgniteCache<String, String> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.STR_CACHE_NAME);
        T2<String, String> val = new T2("123456", "2");
        checkPutAll(shouldFail, cache, new T2("1", "1"), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongStringValueFieldFail() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("3"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("123456"));
        checkPutAll(shouldFail, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("1"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("1")), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("1"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongStringKeyFieldFail() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("123456"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("2"));
        checkPutAll(shouldFail, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("1"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("1")), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongStringKeyFail2() throws Exception {
        doCheckPutTooLongStringKeyFail2(IgniteCachePartitionedAtomicColumnConstraintsTest.STR_ORG_CACHE_NAME);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongStringKeyFail3() throws Exception {
        doCheckPutTooLongStringKeyFail2(IgniteCachePartitionedAtomicColumnConstraintsTest.STR_ORG_WITH_FIELDS_CACHE_NAME);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutLongStringValue() throws Exception {
        IgniteCache<String, String> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.STR_CACHE_NAME);
        T2<String, String> val = new T2("3", "12345");
        checkPutAll(shouldSucceed, cache, new T2("1", "1"), val);
        checkPutOps(shouldSucceed, cache, val);
        checkReplaceOps(shouldSucceed, cache, val, "1");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutLongStringKey() throws Exception {
        IgniteCache<String, String> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.STR_CACHE_NAME);
        T2<String, String> val = new T2("12345", "2");
        checkPutAll(shouldSucceed, cache, new T2("1", "1"), val);
        checkPutOps(shouldSucceed, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutLongStringValueField() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("3"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("12345"));
        checkPutAll(shouldSucceed, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("1"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("1")), val);
        checkPutOps(shouldSucceed, cache, val);
        checkReplaceOps(shouldSucceed, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("1"));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutLongStringKeyField() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.Organization, IgniteCachePartitionedAtomicColumnConstraintsTest.Address> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("12345"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("2"));
        checkPutAll(shouldSucceed, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.Organization("1"), new IgniteCachePartitionedAtomicColumnConstraintsTest.Address("1")), val);
        checkPutOps(shouldSucceed, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutLongStringKey2() throws Exception {
        doCheckPutLongStringKey2(IgniteCachePartitionedAtomicColumnConstraintsTest.STR_ORG_CACHE_NAME);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutLongStringKey3() throws Exception {
        doCheckPutLongStringKey2(IgniteCachePartitionedAtomicColumnConstraintsTest.STR_ORG_WITH_FIELDS_CACHE_NAME);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalValueFail() throws Exception {
        IgniteCache<BigDecimal, BigDecimal> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_CACHE_NAME_FOR_PREC);
        T2<BigDecimal, BigDecimal> val = new T2(d(12.36), d(123.45));
        checkPutAll(shouldFail, cache, new T2(d(12.34), d(12.34)), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, d(12.34));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalKeyFail() throws Exception {
        IgniteCache<BigDecimal, BigDecimal> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_CACHE_NAME_FOR_PREC);
        T2<BigDecimal, BigDecimal> val = new T2(d(123.45), d(12.34));
        checkPutAll(shouldFail, cache, new T2(d(12.35), d(12.34)), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalKeyFail2() throws Exception {
        IgniteCache<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_EMPL_CACHE_NAME_FOR_PREC);
        T2<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(d(123.45), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
        checkPutAll(shouldFail, cache, new T2(d(12.35), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalValueFieldFail() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME_FOR_PREC);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.36)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(123.45)));
        checkPutAll(shouldFail, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.34)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalValueFieldFail2() throws Exception {
        IgniteCache<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_EMPL_CACHE_NAME_FOR_PREC);
        T2<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(d(12.36), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(123.45)));
        checkPutAll(shouldFail, cache, new T2(d(12.34), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalKeyFieldFail() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME_FOR_PREC);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(123.45)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
        checkPutAll(shouldFail, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.35)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalValueScaleFail() throws Exception {
        IgniteCache<BigDecimal, BigDecimal> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_CACHE_NAME_FOR_SCALE);
        T2<BigDecimal, BigDecimal> val = new T2(d(12.36), d(3.456));
        checkPutAll(shouldFail, cache, new T2(d(12.34), d(12.34)), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, d(12.34));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalKeyScaleFail() throws Exception {
        IgniteCache<BigDecimal, BigDecimal> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_CACHE_NAME_FOR_SCALE);
        T2<BigDecimal, BigDecimal> val = new T2(d(3.456), d(12.34));
        checkPutAll(shouldFail, cache, new T2(d(12.35), d(12.34)), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalKeyScaleFail2() throws Exception {
        IgniteCache<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_EMPL_CACHE_NAME_FOR_SCALE);
        T2<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(d(3.456), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
        checkPutAll(shouldFail, cache, new T2(d(12.35), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalValueFieldScaleFail() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME_FOR_SCALE);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.36)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(3.456)));
        checkPutAll(shouldFail, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.34)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalValueFieldScaleFail2() throws Exception {
        IgniteCache<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_EMPL_CACHE_NAME_FOR_SCALE);
        T2<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(d(12.36), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(3.456)));
        checkPutAll(shouldFail, cache, new T2(d(12.34), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
        checkReplaceOps(shouldFail, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutTooLongDecimalKeyFieldScaleFail() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME_FOR_SCALE);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(3.456)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
        checkPutAll(shouldFail, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.35)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldFail, cache, val);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutValidDecimalKeyAndValue() throws Exception {
        IgniteCache<BigDecimal, BigDecimal> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_CACHE_NAME_FOR_SCALE);
        T2<BigDecimal, BigDecimal> val = new T2(d(12.37), d(12.34));
        checkPutAll(shouldSucceed, cache, new T2(d(12.36), d(12.34)), val);
        checkPutOps(shouldSucceed, cache, val);
        checkReplaceOps(shouldSucceed, cache, val, d(12.34));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutValidDecimalKeyAndValueField() throws Exception {
        IgniteCache<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.OBJ_CACHE_NAME_FOR_SCALE);
        T2<IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.37)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
        checkPutAll(shouldSucceed, cache, new T2(new IgniteCachePartitionedAtomicColumnConstraintsTest.DecOrganization(d(12.36)), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldSucceed, cache, val);
        checkReplaceOps(shouldSucceed, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPutValidDecimalKeyAndValueField2() throws Exception {
        IgniteCache<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> cache = jcache(0, IgniteCachePartitionedAtomicColumnConstraintsTest.DEC_EMPL_CACHE_NAME_FOR_SCALE);
        T2<BigDecimal, IgniteCachePartitionedAtomicColumnConstraintsTest.Employee> val = new T2(d(12.37), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
        checkPutAll(shouldSucceed, cache, new T2(d(12.36), new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34))), val);
        checkPutOps(shouldSucceed, cache, val);
        checkReplaceOps(shouldSucceed, cache, val, new IgniteCachePartitionedAtomicColumnConstraintsTest.Employee(d(12.34)));
    }

    /**
     *
     */
    private static class Organization implements Serializable {
        /**
         * Name.
         */
        private final String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        private Organization(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    private static class Address implements Serializable {
        /**
         * Name.
         */
        private final String address;

        /**
         *
         *
         * @param address
         * 		Address.
         */
        private Address(String address) {
            this.address = address;
        }
    }

    /**
     *
     */
    @SuppressWarnings("UnusedDeclaration")
    private static class DecOrganization implements Serializable {
        /**
         * Id.
         */
        private final BigDecimal id;

        /**
         *
         *
         * @param id
         * 		Id.
         */
        private DecOrganization(BigDecimal id) {
            this.id = id;
        }
    }

    /**
     *
     */
    @SuppressWarnings("UnusedDeclaration")
    private static class Employee implements Serializable {
        /**
         * Salary.
         */
        private final BigDecimal salary;

        /**
         *
         *
         * @param salary
         * 		Salary.
         */
        private Employee(BigDecimal salary) {
            this.salary = salary;
        }
    }
}

