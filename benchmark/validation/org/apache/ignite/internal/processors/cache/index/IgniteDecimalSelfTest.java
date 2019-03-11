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


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.junit.Test;


/**
 * Test to check decimal columns.
 */
public class IgniteDecimalSelfTest extends AbstractSchemaSelfTest {
    /**
     *
     */
    private static final int PRECISION = 9;

    /**
     *
     */
    private static final int SCALE = 8;

    /**
     *
     */
    private static final String DEC_TAB_NAME = "DECIMAL_TABLE";

    /**
     *
     */
    private static final String VALUE = "VALUE";

    /**
     *
     */
    private static final String SALARY_TAB_NAME = "SALARY";

    /**
     *
     */
    private static final MathContext MATH_CTX = new MathContext(IgniteDecimalSelfTest.PRECISION);

    /**
     *
     */
    private static final BigDecimal VAL_1 = BigDecimal.valueOf(123456789);

    /**
     *
     */
    private static final BigDecimal VAL_2 = BigDecimal.valueOf(1.23456789);

    /**
     *
     */
    private static final BigDecimal VAL_3 = BigDecimal.valueOf(0.12345678);

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConfiguredFromDdl() throws Exception {
        checkPrecisionAndScale(IgniteDecimalSelfTest.DEC_TAB_NAME, IgniteDecimalSelfTest.VALUE, IgniteDecimalSelfTest.PRECISION, IgniteDecimalSelfTest.SCALE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConfiguredFromQueryEntity() throws Exception {
        checkPrecisionAndScale(IgniteDecimalSelfTest.SALARY_TAB_NAME, "amount", IgniteDecimalSelfTest.PRECISION, IgniteDecimalSelfTest.SCALE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConfiguredFromQueryEntityInDynamicallyCreatedCache() throws Exception {
        IgniteEx grid = grid(0);
        String tabName = (IgniteDecimalSelfTest.SALARY_TAB_NAME) + "2";
        CacheConfiguration<Integer, IgniteDecimalSelfTest.Salary> ccfg = cacheCfg(tabName, "SalaryCache-2");
        IgniteCache<Integer, IgniteDecimalSelfTest.Salary> cache = grid.createCache(ccfg);
        checkPrecisionAndScale(tabName, "amount", IgniteDecimalSelfTest.PRECISION, IgniteDecimalSelfTest.SCALE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testConfiguredFromAnnotations() throws Exception {
        IgniteEx grid = grid(0);
        CacheConfiguration<Integer, IgniteDecimalSelfTest.Salary> ccfg = new CacheConfiguration("SalaryCache-3");
        ccfg.setIndexedTypes(Integer.class, IgniteDecimalSelfTest.SalaryWithAnnotations.class);
        grid.createCache(ccfg);
        checkPrecisionAndScale(IgniteDecimalSelfTest.SalaryWithAnnotations.class.getSimpleName().toUpperCase(), "amount", IgniteDecimalSelfTest.PRECISION, IgniteDecimalSelfTest.SCALE);
    }

    /**
     *
     */
    @Test
    public void testSelectDecimal() throws Exception {
        IgniteEx grid = grid(0);
        List rows = execute(grid, (("SELECT id, value FROM " + (IgniteDecimalSelfTest.DEC_TAB_NAME)) + " order by id"));
        assertEquals(rows.size(), 3);
        assertEquals(Arrays.asList(1L, IgniteDecimalSelfTest.VAL_1), rows.get(0));
        assertEquals(Arrays.asList(2L, IgniteDecimalSelfTest.VAL_2), rows.get(1));
        assertEquals(Arrays.asList(3L, IgniteDecimalSelfTest.VAL_3), rows.get(2));
    }

    /**
     *
     */
    private static class Salary {
        /**
         *
         */
        private BigDecimal amount;

        /**
         *
         */
        public BigDecimal getAmount() {
            return amount;
        }

        /**
         *
         */
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     *
     */
    private static class SalaryWithAnnotations {
        /**
         *
         */
        @QuerySqlField(index = true, precision = IgniteDecimalSelfTest.PRECISION, scale = IgniteDecimalSelfTest.SCALE)
        private BigDecimal amount;

        /**
         *
         */
        public BigDecimal getAmount() {
            return amount;
        }

        /**
         *
         */
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}

