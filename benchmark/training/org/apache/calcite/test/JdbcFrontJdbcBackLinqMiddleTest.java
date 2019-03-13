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


import org.apache.calcite.util.Bug;
import org.junit.Test;

import static org.apache.calcite.test.CalciteAssert.Config.FOODMART_CLONE;
import static org.apache.calcite.test.CalciteAssert.Config.JDBC_FOODMART;
import static org.apache.calcite.test.CalciteAssert.DatabaseInstance.MYSQL;


/**
 * Tests for a JDBC front-end and JDBC back-end where the processing is not
 * pushed down to JDBC (as in {@link JdbcFrontJdbcBackTest}) but is executed
 * in a pipeline of linq4j operators.
 */
public class JdbcFrontJdbcBackLinqMiddleTest {
    @Test
    public void testTable() {
        CalciteAssert.that().with(JDBC_FOODMART).query("select * from \"foodmart\".\"days\"").returns(("day=1; week_day=Sunday\n" + ((((("day=2; week_day=Monday\n" + "day=5; week_day=Thursday\n") + "day=4; week_day=Wednesday\n") + "day=3; week_day=Tuesday\n") + "day=6; week_day=Friday\n") + "day=7; week_day=Saturday\n")));
    }

    @Test
    public void testWhere() {
        CalciteAssert.that().with(JDBC_FOODMART).query("select * from \"foodmart\".\"days\" where \"day\" < 3").returns(("day=1; week_day=Sunday\n" + "day=2; week_day=Monday\n"));
    }

    @Test
    public void testWhere2() {
        CalciteAssert.that().with(JDBC_FOODMART).query(("select * from \"foodmart\".\"days\"\n" + "where not (lower(\"week_day\") = \'wednesday\')")).returns(("day=1; week_day=Sunday\n" + (((("day=2; week_day=Monday\n" + "day=5; week_day=Thursday\n") + "day=3; week_day=Tuesday\n") + "day=6; week_day=Friday\n") + "day=7; week_day=Saturday\n")));
    }

    @Test
    public void testCase() {
        CalciteAssert.that().with(FOODMART_CLONE).query(("select \"day\",\n" + ((((((" \"week_day\",\n" + " case when \"day\" < 3 then upper(\"week_day\")\n") + "      when \"day\" < 5 then lower(\"week_day\")\n") + "      else \"week_day\" end as d\n") + "from \"foodmart\".\"days\"\n") + "where \"day\" <> 1\n") + "order by \"day\""))).returns(("day=2; week_day=Monday; D=MONDAY\n" + (((("day=3; week_day=Tuesday; D=tuesday\n" + "day=4; week_day=Wednesday; D=wednesday\n") + "day=5; week_day=Thursday; D=Thursday\n") + "day=6; week_day=Friday; D=Friday\n") + "day=7; week_day=Saturday; D=Saturday\n")));
    }

    @Test
    public void testGroup() {
        CalciteAssert.that().with(JDBC_FOODMART).query(("select s, count(*) as c, min(\"week_day\") as mw from (\n" + ((("select \"week_day\",\n" + "  substring(\"week_day\" from 1 for 1) as s\n") + "from \"foodmart\".\"days\")\n") + "group by s"))).returnsUnordered("S=T; C=2; MW=Thursday", "S=F; C=1; MW=Friday", "S=W; C=1; MW=Wednesday", "S=S; C=2; MW=Saturday", "S=M; C=1; MW=Monday");
    }

    @Test
    public void testGroupEmpty() {
        CalciteAssert.that().with(JDBC_FOODMART).query(("select count(*) as c\n" + "from \"foodmart\".\"days\"")).returns("C=7\n");
    }

    @Test
    public void testJoinGroupByEmpty() {
        if (((CalciteAssert.DB) == (MYSQL)) && (!(Bug.CALCITE_673_FIXED))) {
            return;
        }
        CalciteAssert.that().with(JDBC_FOODMART).query(("select count(*) from (\n" + ((("  select *\n" + "  from \"foodmart\".\"sales_fact_1997\" as s\n") + "  join \"foodmart\".\"customer\" as c\n") + "  on s.\"customer_id\" = c.\"customer_id\")"))).returns("EXPR$0=86837\n");
    }

    @Test
    public void testJoinGroupByOrderBy() {
        if (((CalciteAssert.DB) == (MYSQL)) && (!(Bug.CALCITE_673_FIXED))) {
            return;
        }
        CalciteAssert.that().with(JDBC_FOODMART).query(("select count(*), c.\"state_province\",\n" + ((((("  sum(s.\"unit_sales\") as s\n" + "from \"foodmart\".\"sales_fact_1997\" as s\n") + "  join \"foodmart\".\"customer\" as c\n") + "  on s.\"customer_id\" = c.\"customer_id\"\n") + "group by c.\"state_province\"\n") + "order by c.\"state_province\""))).returns2(("EXPR$0=24442; state_province=CA; S=74748\n" + ("EXPR$0=21611; state_province=OR; S=67659\n" + "EXPR$0=40784; state_province=WA; S=124366\n")));
    }

    @Test
    public void testCompositeGroupBy() {
        CalciteAssert.that().with(JDBC_FOODMART).query(("select count(*) as c, c.\"state_province\"\n" + (("from \"foodmart\".\"customer\" as c\n" + "group by c.\"state_province\", c.\"country\"\n") + "order by c, 1"))).returns(("C=78; state_province=Sinaloa\n" + ((((((((((("C=90; state_province=Oaxaca\n" + "C=93; state_province=Veracruz\n") + "C=97; state_province=Mexico\n") + "C=99; state_province=Yucatan\n") + "C=104; state_province=Jalisco\n") + "C=106; state_province=Guerrero\n") + "C=191; state_province=Zacatecas\n") + "C=347; state_province=DF\n") + "C=1051; state_province=OR\n") + "C=1717; state_province=BC\n") + "C=2086; state_province=WA\n") + "C=4222; state_province=CA\n")));
    }

    @Test
    public void testPlan3() {
        // Plan should contain 'join'. If it doesn't, maybe int-vs-Integer
        // data type incompatibility has caused it to use a cartesian product
        // instead, and that would be wrong.
        // 
        // inventory_fact_1997 is on the LHS because it is larger than store.
        CalciteAssert.that().with(FOODMART_CLONE).query("select \"store\".\"store_country\" as \"c0\", sum(\"inventory_fact_1997\".\"supply_time\") as \"m0\" from \"store\" as \"store\", \"inventory_fact_1997\" as \"inventory_fact_1997\" where \"inventory_fact_1997\".\"store_id\" = \"store\".\"store_id\" group by \"store\".\"store_country\"").planContains(" left.join(right, new org.apache.calcite.linq4j.function.Function1() {\n");
    }
}

/**
 * End JdbcFrontJdbcBackLinqMiddleTest.java
 */
