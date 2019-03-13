/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import org.apache.calcite.util.TestUtil;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.calcite.test.CalciteAssert.Config.JDBC_FOODMART;


/**
 * Tests for a JDBC front-end and JDBC back-end.
 *
 * <p>The idea is that as much as possible of the query is pushed down
 * to the JDBC data source, in the form of a large (and hopefully efficient)
 * SQL statement.</p>
 *
 * @see JdbcFrontJdbcBackLinqMiddleTest
 */
public class JdbcFrontJdbcBackTest {
    @Test
    public void testWhere2() {
        CalciteAssert.that().with(JDBC_FOODMART).query("select * from \"foodmart\".\"days\" where \"day\" < 3").returns(("day=1; week_day=Sunday\n" + "day=2; week_day=Monday\n"));
    }

    @Test
    public void testTablesByType() throws Exception {
        // check with the form recommended by JDBC
        checkTablesByType("SYSTEM TABLE", Is.is("COLUMNS;TABLES;"));
        // the form we used until 1.14 no longer generates results
        checkTablesByType("SYSTEM_TABLE", Is.is(""));
    }

    @Test
    public void testColumns() throws Exception {
        CalciteAssert.that().with(JDBC_FOODMART).doWithConnection(( connection) -> {
            try {
                ResultSet rset = connection.getMetaData().getColumns(null, null, "sales_fact_1997", null);
                StringBuilder buf = new StringBuilder();
                while (rset.next()) {
                    buf.append(rset.getString(4)).append(';');
                } 
                Assert.assertEquals("product_id;time_id;customer_id;promotion_id;store_id;store_sales;store_cost;unit_sales;", buf.toString());
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    /**
     * Tests a JDBC method known to be not implemented (as it happens,
     * {@link java.sql.DatabaseMetaData#getPrimaryKeys}) that therefore uses
     * empty result set.
     */
    @Test
    public void testEmpty() throws Exception {
        CalciteAssert.that().with(JDBC_FOODMART).doWithConnection(( connection) -> {
            try {
                ResultSet rset = connection.getMetaData().getPrimaryKeys(null, null, "sales_fact_1997");
                Assert.assertFalse(rset.next());
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testCase() {
        CalciteAssert.that().with(JDBC_FOODMART).query(("select\n" + (((("  case when \"sales_fact_1997\".\"promotion_id\" = 1 then 0\n" + "  else \"sales_fact_1997\".\"store_sales\" end as \"c0\"\n") + "from \"sales_fact_1997\" as \"sales_fact_1997\"") + "where \"product_id\" = 1\n") + "and \"time_id\" < 400"))).returns2(("c0=11.4\n" + "c0=8.55\n"));
    }
}

/**
 * End JdbcFrontJdbcBackTest.java
 */
