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
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.X;
import org.junit.Test;

import static org.apache.ignite.internal.processors.query.h2.sql.AbstractH2CompareQueryTest.Ordering.RANDOM;


/**
 * Executes one big query (and subqueries of the big query) to compare query results from h2 database instance and
 * mixed ignite caches (replicated and partitioned) which have the same data models and data content.
 *
 *
 * <pre>
 *
 *  -------------------------------------> rootOrderId (virtual) <--------------------------
 *  |                                                                |                     |
 *  |                                                                |                     |
 *  |                                ---------------------           |                     |
 *  |  ------------------            --part.ReplaceOrder--           |                     |
 *  |  --part.CustOrder--            ---------------------           |                     |
 *  |  ------------------            -  id  PK           -           |                     |
 *  |  -  orderId  PK   - <--  <---> -  orderId          -           |                     |
 *  -- -  rootOrderId   -   |        -  rootOrderId      - -----------                     |
 *     -  origOrderId   -   |        -  refOrderId       -   // = origOrderId              |
 *     -  date          -   |        -  date             -                                 |
 *     -  alias         -   |        -  alias            -                                 |
 *     -  archSeq       -   |        -  archSeq          -          -------------------    |
 *     ------------------   |        ---------------------          ----part.Exec------    |
 *                          |                                       -------------------    |
 *     -----------------    |                                       -  rootOrderId PK - ----
 *     ---part.Cancel---    |                                       -  date           -
 *     -----------------    |        ---------------------          -  execShares int -
 *     -  id       PK  -    |        --part.OrderParams---          -  price      int -
 *     -  refOrderId   - ---|        ---------------------          -  lastMkt    int -
 *     -  date         -    |        -  id  PK           -          -------------------
 *     -----------------    -------  - orderId           -
 *                                   - date              -
 *                                   - parentAlgo        -
 *                                   ---------------------
 *  </pre>
 */
public class H2CompareBigQueryTest extends AbstractH2CompareQueryTest {
    /**
     * Root order count.
     */
    private static final int ROOT_ORDER_CNT = 1000;

    /**
     * Dates count.
     */
    private static final int DATES_CNT = 5;

    /**
     * Full the big query.
     */
    private String bigQry = getBigQry();

    /**
     * Cache cust ord.
     */
    private static IgniteCache<Integer, H2CompareBigQueryTest.CustOrder> cacheCustOrd;

    /**
     * Cache repl ord.
     */
    private static IgniteCache<Object, H2CompareBigQueryTest.ReplaceOrder> cacheReplOrd;

    /**
     * Cache ord parameter.
     */
    private static IgniteCache<Object, H2CompareBigQueryTest.OrderParams> cacheOrdParam;

    /**
     * Cache cancel.
     */
    private static IgniteCache<Object, H2CompareBigQueryTest.Cancel> cacheCancel;

    /**
     * Cache execute.
     */
    private static IgniteCache<Object, H2CompareBigQueryTest.Exec> cacheExec;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBigQuery() throws Exception {
        X.println();
        X.println(bigQry);
        X.println();
        X.println(("   Plan: \n" + (H2CompareBigQueryTest.cacheCustOrd.query(new SqlFieldsQuery(("EXPLAIN " + (bigQry))).setDistributedJoins(distributedJoins())).getAll())));
        List<List<?>> res = AbstractH2CompareQueryTest.compareQueryRes0(H2CompareBigQueryTest.cacheCustOrd, bigQry, distributedJoins(), new Object[0], RANDOM);
        X.println(("   Result size: " + (res.size())));
        assertTrue((!(res.isEmpty())));// Ensure we set good testing data at database.

    }

    /**
     * Custom Order.
     */
    static class CustOrder implements Serializable {
        /**
         * Primary key.
         */
        @QuerySqlField(index = true)
        private int orderId;

        /**
         * Root order ID
         */
        @QuerySqlField
        private int rootOrderId;

        /**
         * Orig order ID
         */
        @QuerySqlField
        private int origOrderId;

        /**
         * Date
         */
        @QuerySqlField
        private Date date;

        /**
         *
         */
        @QuerySqlField
        private String alias = "CUSTOM";

        /**
         *
         */
        @QuerySqlField
        private int archSeq = 11;// TODO: use it.


        /**
         *
         *
         * @param orderId
         * 		Order id.
         * @param rootOrderId
         * 		Root order id.
         * @param date
         * 		Date.
         * @param alias
         * 		Alias.
         * @param origOrderId
         * 		Orig order id.
         */
        CustOrder(int orderId, int rootOrderId, Date date, String alias, int origOrderId) {
            this.orderId = orderId;
            this.rootOrderId = rootOrderId;
            this.origOrderId = origOrderId;
            this.date = date;
            this.alias = alias;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof H2CompareBigQueryTest.CustOrder) && ((orderId) == (((H2CompareBigQueryTest.CustOrder) (o)).orderId)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return orderId;
        }
    }

    /**
     * Replace Order.
     */
    static class ReplaceOrder implements Serializable {
        /**
         * Primary key.
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Order id.
         */
        @QuerySqlField(index = true)
        private int orderId;

        /**
         * Root order ID
         */
        @QuerySqlField
        private int rootOrderId;

        /**
         * Ref order ID
         */
        @QuerySqlField
        private int refOrderId;

        /**
         * Date
         */
        @QuerySqlField
        private Date date;

        /**
         *
         */
        @QuerySqlField
        private String alias = "CUSTOM";

        /**
         *
         */
        @QuerySqlField
        private int archSeq = 111;// TODO: use it.


        /**
         *
         *
         * @param id
         * 		Id.
         * @param orderId
         * 		Order id.
         * @param rootOrderId
         * 		Root order id.
         * @param alias
         * 		Alias.
         * @param date
         * 		Date.
         * @param refOrderId
         * 		Reference order id.
         */
        ReplaceOrder(int id, int orderId, int rootOrderId, String alias, Date date, int refOrderId) {
            this.id = id;
            this.orderId = orderId;
            this.rootOrderId = rootOrderId;
            this.refOrderId = refOrderId;
            this.date = date;
            this.alias = alias;
        }

        /**
         *
         *
         * @param useColocatedData
         * 		Use colocated data.
         * @return Key.
         */
        public Object key(boolean useColocatedData) {
            return useColocatedData ? new org.apache.ignite.cache.affinity.AffinityKey(id, orderId) : id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof H2CompareBigQueryTest.ReplaceOrder) && ((id) == (((H2CompareBigQueryTest.ReplaceOrder) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * Order params.
     */
    static class OrderParams implements Serializable {
        /**
         * Primary key.
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Order id.
         */
        @QuerySqlField(index = true)
        private int orderId;

        /**
         * Date
         */
        @QuerySqlField
        private Date date;

        /**
         *
         */
        @QuerySqlField
        private String parentAlgo = "CUSTOM_ALGO";

        /**
         *
         *
         * @param id
         * 		Id.
         * @param orderId
         * 		Order id.
         * @param date
         * 		Date.
         * @param parentAlgo
         * 		Parent algo.
         */
        OrderParams(int id, int orderId, Date date, String parentAlgo) {
            this.id = id;
            this.orderId = orderId;
            this.date = date;
            this.parentAlgo = parentAlgo;
        }

        /**
         *
         *
         * @param useColocatedData
         * 		Use colocated data.*
         * @return Key.
         */
        public Object key(boolean useColocatedData) {
            return useColocatedData ? new org.apache.ignite.cache.affinity.AffinityKey(id, orderId) : id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof H2CompareBigQueryTest.OrderParams) && ((id) == (((H2CompareBigQueryTest.OrderParams) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * Cancel Order.
     */
    static class Cancel implements Serializable {
        /**
         * Primary key.
         */
        @QuerySqlField(index = true)
        private int id;

        /**
         * Order id.
         */
        @QuerySqlField(index = true)
        private int refOrderId;

        /**
         * Date
         */
        @QuerySqlField
        private Date date;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param refOrderId
         * 		Reference order id.
         * @param date
         * 		Date.
         */
        Cancel(int id, int refOrderId, Date date) {
            this.id = id;
            this.refOrderId = refOrderId;
            this.date = date;
        }

        /**
         *
         *
         * @param useColocatedData
         * 		Use colocated data.
         * @return Key.
         */
        public Object key(boolean useColocatedData) {
            return useColocatedData ? new org.apache.ignite.cache.affinity.AffinityKey(id, refOrderId) : id;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof H2CompareBigQueryTest.Cancel) && ((id) == (((H2CompareBigQueryTest.Cancel) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * Execute information about root query.
     */
    static class Exec implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private int id;

        /**
         *
         */
        @QuerySqlField(index = true)
        private int rootOrderId;

        /**
         * Date
         */
        @QuerySqlField
        private Date date;

        /**
         *
         */
        @QuerySqlField
        private int execShares;

        /**
         *
         */
        @QuerySqlField
        private int price;

        /**
         *
         */
        @QuerySqlField
        private int lastMkt;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param rootOrderId
         * 		Root order id.
         * @param date
         * 		Date.
         * @param execShares
         * 		Execute shares.
         * @param price
         * 		Price.
         * @param lastMkt
         * 		Last mkt.
         */
        Exec(int id, int rootOrderId, Date date, int execShares, int price, int lastMkt) {
            this.id = id;
            this.rootOrderId = rootOrderId;
            this.date = date;
            this.execShares = execShares;
            this.price = price;
            this.lastMkt = lastMkt;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            return ((this) == o) || ((o instanceof H2CompareBigQueryTest.Exec) && ((id) == (((H2CompareBigQueryTest.Exec) (o)).id)));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }

        public Object key(boolean useColocatedData) {
            return useColocatedData ? new org.apache.ignite.cache.affinity.AffinityKey(id, rootOrderId) : id;
        }
    }
}

