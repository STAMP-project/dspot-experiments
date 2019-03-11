/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill;


import ExecErrorConstants.DECIMAL_DISABLE_ERR_MSG;
import PlannerSettings.ENABLE_DECIMAL_DATA_TYPE_KEY;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.work.foreman.UnsupportedDataTypeException;
import org.apache.drill.exec.work.foreman.UnsupportedFunctionException;
import org.apache.drill.exec.work.foreman.UnsupportedRelOperatorException;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(UnlikelyTest.class)
public class TestDisabledFunctionality extends BaseTestQuery {
    static final Logger logger = LoggerFactory.getLogger(TestExampleQueries.class);

    // see DRILL-2054
    @Test(expected = UserException.class)
    public void testBooleanORWhereClause() throws Exception {
        BaseTestQuery.test("select * from cp.`tpch/nation.parquet` where (true || true) ");
    }

    // see DRILL-2054
    @Test(expected = UserException.class)
    public void testBooleanAND() throws Exception {
        BaseTestQuery.test("select true && true from cp.`tpch/nation.parquet` ");
    }

    // see DRILL-1921
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testDisabledIntersect() throws Exception {
        try {
            BaseTestQuery.test("(select n_name as name from cp.`tpch/nation.parquet`) INTERSECT (select r_name as name from cp.`tpch/region.parquet`)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1921
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testDisabledIntersectALL() throws Exception {
        try {
            BaseTestQuery.test("(select n_name as name from cp.`tpch/nation.parquet`) INTERSECT ALL (select r_name as name from cp.`tpch/region.parquet`)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1921
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testDisabledExceptALL() throws Exception {
        try {
            BaseTestQuery.test("(select n_name as name from cp.`tpch/nation.parquet`) EXCEPT ALL (select r_name as name from cp.`tpch/region.parquet`)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1921
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testDisabledExcept() throws Exception {
        try {
            BaseTestQuery.test("(select n_name as name from cp.`tpch/nation.parquet`) EXCEPT (select r_name as name from cp.`tpch/region.parquet`)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1921
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testDisabledNaturalJoin() throws Exception {
        try {
            BaseTestQuery.test("select * from cp.`tpch/nation.parquet` NATURAL JOIN cp.`tpch/region.parquet`");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1959
    @Test(expected = UnsupportedDataTypeException.class)
    public void testDisabledCastTINYINT() throws Exception {
        try {
            BaseTestQuery.test("select cast(n_name as tinyint) from cp.`tpch/nation.parquet`;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1959
    @Test(expected = UnsupportedDataTypeException.class)
    public void testDisabledCastSMALLINT() throws Exception {
        try {
            BaseTestQuery.test("select cast(n_name as smallint) from cp.`tpch/nation.parquet`;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-1959
    @Test(expected = UnsupportedDataTypeException.class)
    public void testDisabledCastREAL() throws Exception {
        try {
            BaseTestQuery.test("select cast(n_name as real) from cp.`tpch/nation.parquet`;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2115
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisabledCardinality() throws Exception {
        try {
            BaseTestQuery.test("select cardinality(employee_id) from cp.`employee.json`;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // DRILL-2068
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testImplicitCartesianJoin() throws Exception {
        try {
            BaseTestQuery.test(("select a.*, b.user_port " + "from cp.`employee.json` a, sys.drillbits b;"));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2068, DRILL-1325
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testNonEqualJoin() throws Exception {
        try {
            BaseTestQuery.test(("select a.*, b.user_port " + ("from cp.`employee.json` a, sys.drillbits b " + "where a.position_id <> b.user_port;")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2068, DRILL-1325
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testMultipleJoinsWithOneNonEqualJoin() throws Exception {
        try {
            BaseTestQuery.test(("select a.last_name, b.n_name, c.r_name " + ("from cp.`employee.json` a, cp.`tpch/nation.parquet` b, cp.`tpch/region.parquet` c " + "where a.position_id > b.n_nationKey and b.n_nationKey = c.r_regionkey;")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see  DRILL-2068, DRILL-1325
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testLeftOuterJoin() throws Exception {
        try {
            BaseTestQuery.test(("select a.lastname, b.n_name " + ("from cp.`employee.json` a LEFT JOIN cp.`tpch/nation.parquet` b " + "ON a.position_id > b.n_nationKey;")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2068, DRILL-1325
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testInnerJoin() throws Exception {
        try {
            BaseTestQuery.test(("select a.lastname, b.n_name " + ("from cp.`employee.json` a INNER JOIN cp.`tpch/nation.parquet` b " + "ON a.position_id > b.n_nationKey;")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2068, DRILL-1325
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testExplainPlanForCartesianJoin() throws Exception {
        try {
            BaseTestQuery.test(("explain plan for (select a.lastname, b.n_name " + ("from cp.`employee.json` a INNER JOIN cp.`tpch/nation.parquet` b " + "ON a.position_id > b.n_nationKey);")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2441
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testExplainPlanOuterJoinWithInequality() throws Exception {
        try {
            BaseTestQuery.test(("explain plan for (select a.lastname, b.n_name " + ("from cp.`employee.json` a LEFT OUTER JOIN cp.`tpch/nation.parquet` b " + "ON (a.position_id > b.n_nationKey AND a.employee_id = b.n_regionkey));")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2441
    @Test(expected = UnsupportedRelOperatorException.class)
    public void testOuterJoinWithInequality() throws Exception {
        try {
            BaseTestQuery.test(("select a.lastname, b.n_name " + ("from cp.`employee.json` a RIGHT OUTER JOIN cp.`tpch/nation.parquet` b " + "ON (a.position_id > b.n_nationKey AND a.employee_id = b.n_regionkey);")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
        }
    }

    // see DRILL-2181
    @Test(expected = UnsupportedFunctionException.class)
    public void testFlattenWithinGroupBy() throws Exception {
        try {
            BaseTestQuery.test(("select flatten(j.topping) tt " + "from cp.`store/text/sample.json` j group by flatten(j.topping)"));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // see DRILL-2181
    @Test(expected = UnsupportedFunctionException.class)
    public void testFlattenWithinOrderBy() throws Exception {
        try {
            BaseTestQuery.test(("select flatten(j.topping) tt " + ("from cp.`store/text/sample.json` j " + "order by flatten(j.topping)")));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // see DRILL-2181
    @Test(expected = UnsupportedFunctionException.class)
    public void testFlattenWithinAggFunction() throws Exception {
        try {
            BaseTestQuery.test(("select count(flatten(j.topping)) tt " + "from cp.`store/text/sample.json` j"));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // see DRILL-2181
    @Test(expected = UnsupportedFunctionException.class)
    public void testFlattenWithinDistinct() throws Exception {
        try {
            BaseTestQuery.test(("select Distinct (flatten(j.topping)) tt " + "from cp.`store/text/sample.json` j"));
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-2848
    @Test
    public void testDisableDecimalCasts() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, false);
            final String query = "select cast('1.2' as decimal(9, 2)) from cp.`employee.json` limit 1";
            BaseTestQuery.errorMsgTestHelper(query, DECIMAL_DISABLE_ERR_MSG);
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    // DRILL-2848
    @Test
    public void testDisableDecimalFromParquet() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, false);
            final String query = "select * from cp.`parquet/decimal_dictionary.parquet`";
            BaseTestQuery.errorMsgTestHelper(query, DECIMAL_DISABLE_ERR_MSG);
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableRollup() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*) as cnt from cp.`tpch/nation.parquet` group by rollup(n_regionkey, n_name)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableCube() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*) as cnt from cp.`tpch/nation.parquet` group by cube(n_regionkey, n_name)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableGroupingSets() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*) as cnt from cp.`tpch/nation.parquet` group by grouping sets(n_regionkey, n_name)");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableGrouping() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*), GROUPING(n_regionkey) from cp.`tpch/nation.parquet` group by n_regionkey;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableGrouping_ID() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*), GROUPING_ID(n_regionkey) from cp.`tpch/nation.parquet` group by n_regionkey;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableGroup_ID() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*), GROUP_ID() from cp.`tpch/nation.parquet` group by n_regionkey;");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }

    // DRILL-3802
    @Test(expected = UnsupportedFunctionException.class)
    public void testDisableGroupingInFilter() throws Exception {
        try {
            BaseTestQuery.test("select n_regionkey, count(*) from cp.`tpch/nation.parquet` group by n_regionkey HAVING GROUPING(n_regionkey) = 1");
        } catch (UserException ex) {
            TestDisabledFunctionality.throwAsUnsupportedException(ex);
            throw ex;
        }
    }
}

