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


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.query.GridQueryProcessor;
import org.apache.ignite.internal.processors.query.IgniteSQLException;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests cross cache queries.
 */
public class GridCacheCrossCacheQuerySelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String PART_CACHE_NAME = "partitioned";

    /**
     *
     */
    private static final String REPL_PROD_CACHE_NAME = "replicated-prod";

    /**
     *
     */
    private static final String REPL_STORE_CACHE_NAME = "replicated-store";

    /**
     *
     */
    private Ignite ignite;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTwoStepGroupAndAggregates() throws Exception {
        IgniteInternalCache<Integer, GridCacheCrossCacheQuerySelfTest.FactPurchase> cache = getCache(GridCacheCrossCacheQuerySelfTest.PART_CACHE_NAME);
        GridQueryProcessor qryProc = context().query();
        Set<Integer> set1 = new HashSet<>();
        X.println("___ simple");
        SqlFieldsQuery qry = new SqlFieldsQuery(("select f.productId, p.name, f.price " + "from FactPurchase f, \"replicated-prod\".DimProduct p where p.id = f.productId "));
        for (List<?> o : qryProc.querySqlFields(cache.context(), qry, null, false, true).get(0).getAll()) {
            X.println(("___ -> " + o));
            set1.add(((Integer) (o.get(0))));
        }
        assertFalse(set1.isEmpty());
        Set<Integer> set0 = new HashSet<>();
        X.println("___ GROUP BY");
        qry = new SqlFieldsQuery("select productId from FactPurchase group by productId");
        for (List<?> o : qryProc.querySqlFields(cache.context(), qry, null, false, true).get(0).getAll()) {
            X.println(("___ -> " + o));
            assertTrue(set0.add(((Integer) (o.get(0)))));
        }
        assertEquals(set0, set1);
        X.println("___ GROUP BY AVG MIN MAX SUM COUNT(*) COUNT(x) (MAX - MIN) * 2 as");
        Set<String> names = new HashSet<>();
        qry = new SqlFieldsQuery(("select p.name, avg(f.price), min(f.price), max(f.price), sum(f.price), count(*), " + (((("count(nullif(f.price, 5)), (max(f.price) - min(f.price)) * 3 as nn " + ", CAST(max(f.price) + 7 AS VARCHAR) ") + "from FactPurchase f, \"replicated-prod\".DimProduct p ") + "where p.id = f.productId ") + "group by f.productId, p.name")));
        for (List<?> o : qryProc.querySqlFields(cache.context(), qry, null, false, true).get(0).getAll()) {
            X.println(("___ -> " + o));
            assertTrue(names.add(((String) (o.get(0)))));
            assertEquals(GridCacheCrossCacheQuerySelfTest.i(o, 4), ((GridCacheCrossCacheQuerySelfTest.i(o, 2)) + (GridCacheCrossCacheQuerySelfTest.i(o, 3))));
            assertEquals(GridCacheCrossCacheQuerySelfTest.i(o, 7), (((GridCacheCrossCacheQuerySelfTest.i(o, 3)) - (GridCacheCrossCacheQuerySelfTest.i(o, 2))) * 3));
            assertEquals(o.get(8), Integer.toString(((GridCacheCrossCacheQuerySelfTest.i(o, 3)) + 7)));
        }
        X.println("___ SUM HAVING");
        qry = new SqlFieldsQuery(("select p.name, sum(f.price) s " + ((("from FactPurchase f, \"replicated-prod\".DimProduct p " + "where p.id = f.productId ") + "group by f.productId, p.name ") + "having s >= 15")));
        for (List<?> o : qryProc.querySqlFields(cache.context(), qry, null, false, true).get(0).getAll()) {
            X.println(("___ -> " + o));
            assertTrue(((GridCacheCrossCacheQuerySelfTest.i(o, 1)) >= 15));
        }
        X.println("___ DISTINCT ORDER BY TOP");
        int top = 6;
        qry = new SqlFieldsQuery(("select top 3 distinct productId " + "from FactPurchase f order by productId desc "));
        for (List<?> o : qryProc.querySqlFields(cache.context(), qry, null, false, true).get(0).getAll()) {
            X.println(("___ -> " + o));
            assertEquals((top--), o.get(0));
        }
        X.println("___ DISTINCT ORDER BY OFFSET LIMIT");
        top = 5;
        qry = new SqlFieldsQuery(("select distinct productId " + "from FactPurchase f order by productId desc limit 2 offset 1"));
        for (List<?> o : qryProc.querySqlFields(cache.context(), qry, null, false, true).get(0).getAll()) {
            X.println(("___ -> " + o));
            assertEquals((top--), o.get(0));
        }
        assertEquals(3, top);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testApiQueries() throws Exception {
        IgniteCache<Object, Object> c = ignite.cache(GridCacheCrossCacheQuerySelfTest.PART_CACHE_NAME);
        c.query(new SqlFieldsQuery("select cast(? as varchar) from FactPurchase").setArgs("aaa")).getAll();
        List<List<?>> res = c.query(new SqlFieldsQuery(("select cast(? as varchar), id " + "from FactPurchase order by id limit ? offset ?")).setArgs("aaa", 1, 1)).getAll();
        assertEquals(1, res.size());
        assertEquals("aaa", res.get(0).get(0));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultiStatement() throws Exception {
        final IgniteInternalCache<Integer, GridCacheCrossCacheQuerySelfTest.FactPurchase> cache = getCache(GridCacheCrossCacheQuerySelfTest.PART_CACHE_NAME);
        final GridQueryProcessor qryProc = context().query();
        final SqlFieldsQuery qry = new SqlFieldsQuery(("insert into FactPurchase(_key, id, productId, storeId, price) values (555, 555, 555, 555, 555);" + "select count(*) from FactPurchase"));
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                qryProc.querySqlFields(cache.context(), qry, null, false, true);
                return null;
            }
        }, IgniteSQLException.class, "Multiple statements queries are not supported");
        List<FieldsQueryCursor<List<?>>> cursors = qryProc.querySqlFields(cache.context(), qry, null, false, false);
        assertEquals(2, cursors.size());
        for (FieldsQueryCursor<List<?>> cur : cursors)
            U.closeQuiet(cur);

        qry.setLocal(true);
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                qryProc.querySqlFields(cache.context(), qry, null, false, false);
                return null;
            }
        }, IgniteSQLException.class, "Multiple statements queries are not supported for local queries");
    }

    /**
     * Represents a product available for purchase. In our {@code snowflake} schema a {@code product} is a {@code 'dimension'} and will be cached in {@link org.apache.ignite.cache.CacheMode#REPLICATED} cache.
     */
    private static class DimProduct {
        /**
         * Primary key.
         */
        @QuerySqlField
        private int id;

        /**
         * Product name.
         */
        @QuerySqlField
        private String name;

        /**
         * Constructs a product instance.
         *
         * @param id
         * 		Product ID.
         * @param name
         * 		Product name.
         */
        DimProduct(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * Gets product ID.
         *
         * @return Product ID.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets product name.
         *
         * @return Product name.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Represents a physical store location. In our {@code snowflake} schema a {@code store} is a {@code 'dimension'}
     * and will be cached in {@link org.apache.ignite.cache.CacheMode#REPLICATED} cache.
     */
    private static class DimStore {
        /**
         * Primary key.
         */
        @QuerySqlField
        private int id;

        /**
         * Store name.
         */
        @QuerySqlField
        private String name;

        /**
         * Constructs a store instance.
         *
         * @param id
         * 		Store ID.
         * @param name
         * 		Store name.
         */
        DimStore(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * Gets store ID.
         *
         * @return Store ID.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets store name.
         *
         * @return Store name.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Represents a purchase record. In our {@code snowflake} schema purchase is a {@code 'fact'} and will be cached in
     * larger {@link org.apache.ignite.cache.CacheMode#PARTITIONED} cache.
     */
    private static class FactPurchase {
        /**
         * Primary key.
         */
        @QuerySqlField
        private int id;

        /**
         * Foreign key to store at which purchase occurred.
         */
        @QuerySqlField
        private int storeId;

        /**
         * Foreign key to purchased product.
         */
        @QuerySqlField
        private int productId;

        @QuerySqlField
        private int price;

        /**
         * Constructs a purchase record.
         *
         * @param id
         * 		Purchase ID.
         * @param productId
         * 		Purchased product ID.
         * @param storeId
         * 		Store ID.
         */
        FactPurchase(int id, int productId, int storeId, int price) {
            this.id = id;
            this.productId = productId;
            this.storeId = storeId;
            this.price = price;
        }

        /**
         * Gets purchase ID.
         *
         * @return Purchase ID.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets purchased product ID.
         *
         * @return Product ID.
         */
        public int getProductId() {
            return productId;
        }

        /**
         * Gets ID of store at which purchase was made.
         *
         * @return Store ID.
         */
        public int getStoreId() {
            return storeId;
        }
    }
}

