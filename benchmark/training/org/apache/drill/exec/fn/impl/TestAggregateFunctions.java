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
package org.apache.drill.exec.fn.impl;


import ExecConstants.EARLY_LIMIT0_OPT_KEY;
import PlannerSettings.ENABLE_DECIMAL_DATA_TYPE_KEY;
import TypeProtos.DataMode.OPTIONAL;
import TypeProtos.DataMode.REQUIRED;
import TypeProtos.MajorType;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.VARDECIMAL;
import UserBitShared.SerializedField;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.drill.PlanTestBase;
import org.apache.drill.categories.OperatorTest;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.categories.SqlFunctionTest;
import org.apache.drill.categories.UnlikelyTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.proto.UserBitShared;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.util.Text;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.shaded.guava.com.google.common.collect.Maps;
import org.apache.drill.test.BaseTestQuery;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category({ SqlFunctionTest.class, OperatorTest.class, PlannerTest.class })
public class TestAggregateFunctions extends BaseTestQuery {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /* Test checks the count of a nullable column within a map
    and verifies count is equal only to the number of times the
    column appears and doesn't include the null count
     */
    @Test
    public void testCountOnNullableColumn() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(t.x.y)  as cnt1, count(`integer`) as cnt2 from cp.`jsoninput/input2.json` t").ordered().baselineColumns("cnt1", "cnt2").baselineValues(3L, 4L).go();
    }

    @Test
    public void testCountDistinctOnBoolColumn() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(distinct `bool_val`) as cnt from `sys`.`options_old`").ordered().baselineColumns("cnt").baselineValues(2L).go();
    }

    @Test
    public void testMaxWithZeroInput() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, false);
            BaseTestQuery.testBuilder().sqlQuery("select max(employee_id * 0.0) as max_val from cp.`employee.json`").unOrdered().baselineColumns("max_val").baselineValues(0.0).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    // DRILL-2170: Subquery has group-by, order-by on aggregate function and limit
    @Test
    @Category(UnlikelyTest.class)
    public void testDrill2170() throws Exception {
        String query = "select count(*) as cnt from " + (((("cp.`tpch/orders.parquet` o inner join\n" + "(select l_orderkey, sum(l_quantity), sum(l_extendedprice) \n") + "from cp.`tpch/lineitem.parquet` \n") + "group by l_orderkey order by 3 limit 100) sq \n") + "on sq.l_orderkey = o.o_orderkey");
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().optionSettingQueriesForTestQuery("alter system set `planner.slice_target` = 1000").baselineColumns("cnt").baselineValues(100L).go();
    }

    // DRILL-2168
    @Test
    @Category(UnlikelyTest.class)
    public void testGBExprWithDrillFunc() throws Exception {
        BaseTestQuery.testBuilder().ordered().sqlQuery(("select concat(n_name, cast(n_nationkey as varchar(10))) as name, count(*) as cnt " + ((("from cp.`tpch/nation.parquet` " + "group by concat(n_name, cast(n_nationkey as varchar(10))) ") + "having concat(n_name, cast(n_nationkey as varchar(10))) > 'UNITED'") + "order by concat(n_name, cast(n_nationkey as varchar(10)))"))).baselineColumns("name", "cnt").baselineValues("UNITED KINGDOM23", 1L).baselineValues("UNITED STATES24", 1L).baselineValues("VIETNAM21", 1L).build().run();
    }

    // DRILL-2242
    @Test
    @Category(UnlikelyTest.class)
    public void testDRILLNestedGBWithSubsetKeys() throws Exception {
        String sql = " select count(*) as cnt from (select l_partkey from\n" + (("   (select l_partkey, l_suppkey from cp.`tpch/lineitem.parquet`\n" + "      group by l_partkey, l_suppkey) \n") + "   group by l_partkey )");
        BaseTestQuery.test("alter session set `planner.slice_target` = 1; alter session set `planner.enable_multiphase_agg` = false ;");
        BaseTestQuery.testBuilder().ordered().sqlQuery(sql).baselineColumns("cnt").baselineValues(2000L).build().run();
        BaseTestQuery.test("alter session set `planner.slice_target` = 1; alter session set `planner.enable_multiphase_agg` = true ;");
        BaseTestQuery.testBuilder().ordered().sqlQuery(sql).baselineColumns("cnt").baselineValues(2000L).build().run();
        BaseTestQuery.test("alter session set `planner.slice_target` = 100000");
    }

    @Test
    public void testAvgWithNullableScalarFunction() throws Exception {
        String query = " select avg(length(b1)) as col from cp.`jsoninput/nullable1.json`";
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col").baselineValues(3.0).go();
    }

    @Test
    public void testCountWithAvg() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(a) col1, avg(b) col2 from cp.`jsoninput/nullable3.json`").unOrdered().baselineColumns("col1", "col2").baselineValues(2L, 3.0).go();
        BaseTestQuery.testBuilder().sqlQuery("select count(a) col1, avg(a) col2 from cp.`jsoninput/nullable3.json`").unOrdered().baselineColumns("col1", "col2").baselineValues(2L, 1.0).go();
    }

    @Test
    public void testAvgOnKnownType() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select avg(cast(employee_id as bigint)) as col from cp.`employee.json`").unOrdered().baselineColumns("col").baselineValues(578.9982683982684).go();
    }

    @Test
    public void testStddevOnKnownType() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select stddev_samp(cast(employee_id as int)) as col from cp.`employee.json`").unOrdered().baselineColumns("col").baselineValues(333.56708470261117).go();
    }

    @Test
    public void testVarSampDecimal() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(("select var_samp(cast(employee_id as decimal(28, 20))) as dec20,\n" + (("var_samp(cast(employee_id as decimal(28, 0))) as dec6,\n" + "var_samp(cast(employee_id as integer)) as d\n") + "from cp.`employee.json`"))).unOrdered().baselineColumns("dec20", "dec6", "d").baselineValues(new BigDecimal("111266.99999699895713760532"), new BigDecimal("111266.999997"), 111266.99999699896).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    @Test
    public void testVarPopDecimal() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(("select var_pop(cast(employee_id as decimal(28, 20))) as dec20,\n" + (("var_pop(cast(employee_id as decimal(28, 0))) as dec6,\n" + "var_pop(cast(employee_id as integer)) as d\n") + "from cp.`employee.json`"))).unOrdered().baselineColumns("dec20", "dec6", "d").baselineValues(new BigDecimal("111170.66493206649050804895"), new BigDecimal("111170.664932"), 111170.66493206649).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    @Test
    public void testStddevSampDecimal() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            // last number differs because of double precision.
            // Was taken sqrt of 111266.99999699895713760531784795216338 and decimal result is correct
            BaseTestQuery.testBuilder().sqlQuery(("select stddev_samp(cast(employee_id as decimal(28, 20))) as dec20,\n" + (("stddev_samp(cast(employee_id as decimal(28, 0))) as dec6,\n" + "stddev_samp(cast(employee_id as integer)) as d\n") + "from cp.`employee.json`"))).unOrdered().baselineColumns("dec20", "dec6", "d").baselineValues(new BigDecimal("333.56708470261114349632"), new BigDecimal("333.567085"), 333.56708470261117).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    @Test
    public void testStddevPopDecimal() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(("select stddev_pop(cast(employee_id as decimal(28, 20))) as dec20,\n" + (("stddev_pop(cast(employee_id as decimal(28, 0))) as dec6,\n" + "stddev_pop(cast(employee_id as integer)) as d\n") + "from cp.`employee.json`"))).unOrdered().baselineColumns("dec20", "dec6", "d").baselineValues(new BigDecimal("333.42265209800381903633"), new BigDecimal("333.422652"), 333.4226520980038).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    @Test
    public void testSumDecimal() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(("select sum(cast(employee_id as decimal(9, 0))) as colDecS0,\n" + (("sum(cast(employee_id as decimal(12, 3))) as colDecS3,\n" + "sum(cast(employee_id as integer)) as colInt\n") + "from cp.`employee.json`"))).unOrdered().baselineColumns("colDecS0", "colDecS3", "colInt").baselineValues(BigDecimal.valueOf(668743), new BigDecimal("668743.000"), 668743L).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    @Test
    public void testAvgDecimal() throws Exception {
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(("select avg(cast(employee_id as decimal(28, 20))) as colDec20,\n" + (("avg(cast(employee_id as decimal(28, 0))) as colDec6,\n" + "avg(cast(employee_id as integer)) as colInt\n") + "from cp.`employee.json`"))).unOrdered().baselineColumns("colDec20", "colDec6", "colInt").baselineValues(new BigDecimal("578.99826839826839826840"), new BigDecimal("578.998268"), 578.9982683982684).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    @Test
    public void testSumAvgDecimalLimit0() throws Exception {
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = ImmutableList.of(Pair.of(SchemaPath.getSimplePath("sum_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 3, 38)), Pair.of(SchemaPath.getSimplePath("avg_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 6, 38)), Pair.of(SchemaPath.getSimplePath("stddev_pop_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 6, 38)), Pair.of(SchemaPath.getSimplePath("stddev_samp_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 6, 38)), Pair.of(SchemaPath.getSimplePath("var_pop_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 6, 38)), Pair.of(SchemaPath.getSimplePath("var_samp_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 6, 38)), Pair.of(SchemaPath.getSimplePath("max_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 3, 9)), Pair.of(SchemaPath.getSimplePath("min_col"), Types.withScaleAndPrecision(VARDECIMAL, OPTIONAL, 3, 9)));
        String query = "select\n" + (((((((("sum(cast(employee_id as decimal(9, 3))) sum_col,\n" + "avg(cast(employee_id as decimal(9, 3))) avg_col,\n") + "stddev_pop(cast(employee_id as decimal(9, 3))) stddev_pop_col,\n") + "stddev_samp(cast(employee_id as decimal(9, 3))) stddev_samp_col,\n") + "var_pop(cast(employee_id as decimal(9, 3))) var_pop_col,\n") + "var_samp(cast(employee_id as decimal(9, 3))) var_samp_col,\n") + "max(cast(employee_id as decimal(9, 3))) max_col,\n") + "min(cast(employee_id as decimal(9, 3))) min_col\n") + "from cp.`employee.json` limit 0");
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.alterSession(EARLY_LIMIT0_OPT_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).go();
            BaseTestQuery.alterSession(EARLY_LIMIT0_OPT_KEY, false);
            BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
            BaseTestQuery.resetSessionOption(EARLY_LIMIT0_OPT_KEY);
        }
    }

    // DRILL-6221
    @Test
    public void testAggGroupByWithNullDecimal() throws Exception {
        String fileName = "table.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), fileName)))) {
            writer.write("{\"a\": 1, \"b\": 0}");
            writer.write("{\"b\": 2}");
        }
        try {
            BaseTestQuery.alterSession(ENABLE_DECIMAL_DATA_TYPE_KEY, true);
            BaseTestQuery.testBuilder().sqlQuery(("select sum(cast(a as decimal(9,0))) as s,\n" + (((((("avg(cast(a as decimal(9,0))) as av,\n" + "var_samp(cast(a as decimal(9,0))) as varSamp,\n") + "var_pop(cast(a as decimal(9,0))) as varPop,\n") + "stddev_pop(cast(a as decimal(9,0))) as stddevPop,\n") + "stddev_samp(cast(a as decimal(9,0))) as stddevSamp,") + "max(cast(a as decimal(9,0))) as mx,") + "min(cast(a as decimal(9,0))) as mn from dfs.`%s` t group by a")), fileName).unOrdered().baselineColumns("s", "av", "varSamp", "varPop", "stddevPop", "stddevSamp", "mx", "mn").baselineValues(BigDecimal.valueOf(1), new BigDecimal("1.000000"), new BigDecimal("0.000000"), new BigDecimal("0.000000"), new BigDecimal("0.000000"), new BigDecimal("0.000000"), BigDecimal.valueOf(1), BigDecimal.valueOf(1)).baselineValues(null, null, null, null, null, null, null, null).go();
        } finally {
            BaseTestQuery.resetSessionOption(ENABLE_DECIMAL_DATA_TYPE_KEY);
        }
    }

    // test aggregates when input is empty and data type is optional
    @Test
    public void countEmptyNullableInput() throws Exception {
        String query = "select " + ("count(employee_id) col1, avg(employee_id) col2, sum(employee_id) col3 " + "from cp.`employee.json` where 1 = 0");
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col1", "col2", "col3").baselineValues(0L, null, null).go();
    }

    @Test
    public void stddevEmptyNonexistentNullableInput() throws Exception {
        // test stddev function
        final String query = "select " + (("stddev_pop(int_col) col1, stddev_pop(bigint_col) col2, stddev_pop(float4_col) col3, " + "stddev_pop(float8_col) col4, stddev_pop(interval_year_col) col5 ") + "from cp.`employee.json` where 1 = 0");
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col1", "col2", "col3", "col4", "col5").baselineValues(null, null, null, null, null).go();
    }

    @Test
    public void minMaxEmptyNonNullableInput() throws Exception {
        // test min and max functions on required type
        final QueryDataBatch result = BaseTestQuery.testSqlWithResults("select * from cp.`parquet/alltypes_required.parquet` limit 0").get(0);
        final Map<String, StringBuilder> functions = Maps.newHashMap();
        functions.put("min", new StringBuilder());
        functions.put("max", new StringBuilder());
        final Map<String, Object> resultingValues = Maps.newHashMap();
        for (UserBitShared.SerializedField field : result.getHeader().getDef().getFieldList()) {
            final String fieldName = field.getNamePart().getName();
            // Only COUNT aggregate function supported for Boolean type
            if (fieldName.equals("col_bln")) {
                continue;
            }
            resultingValues.put(String.format("`%s`", fieldName), null);
            for (Map.Entry<String, StringBuilder> function : functions.entrySet()) {
                function.getValue().append(function.getKey()).append("(").append(fieldName).append(") ").append(fieldName).append(",");
            }
        }
        result.release();
        final String query = "select %s from cp.`parquet/alltypes_required.parquet` where 1 = 0";
        final List<Map<String, Object>> baselineRecords = Lists.newArrayList();
        baselineRecords.add(resultingValues);
        for (StringBuilder selectBody : functions.values()) {
            selectBody.setLength(((selectBody.length()) - 1));
            BaseTestQuery.testBuilder().sqlQuery(query, selectBody.toString()).unOrdered().baselineRecords(baselineRecords).go();
        }
    }

    @Test
    public void testSingleValueFunction() throws Exception {
        List<String> tableNames = ImmutableList.of("cp.`parquet/alltypes_required.parquet`", "cp.`parquet/alltypes_optional.parquet`");
        for (String tableName : tableNames) {
            final QueryDataBatch result = BaseTestQuery.testSqlWithResults(String.format("select * from %s limit 1", tableName)).get(0);
            final Map<String, StringBuilder> functions = new HashMap<>();
            functions.put("single_value", new StringBuilder());
            final Map<String, Object> resultingValues = new HashMap<>();
            final List<String> columns = new ArrayList<>();
            final RecordBatchLoader loader = new RecordBatchLoader(BaseTestQuery.getAllocator());
            loader.load(result.getHeader().getDef(), result.getData());
            for (VectorWrapper<?> vectorWrapper : loader.getContainer()) {
                final String fieldName = vectorWrapper.getField().getName();
                Object object = vectorWrapper.getValueVector().getAccessor().getObject(0);
                // VarCharVector returns Text instance, but baseline values should contain String value
                if (object instanceof Text) {
                    object = object.toString();
                }
                resultingValues.put(String.format("`%s`", fieldName), object);
                for (Map.Entry<String, StringBuilder> function : functions.entrySet()) {
                    function.getValue().append(function.getKey()).append("(").append(fieldName).append(") ").append(fieldName).append(",");
                }
                columns.add(fieldName);
            }
            loader.clear();
            result.release();
            String columnsList = columns.stream().collect(Collectors.joining(", "));
            final List<Map<String, Object>> baselineRecords = new ArrayList<>();
            baselineRecords.add(resultingValues);
            for (StringBuilder selectBody : functions.values()) {
                selectBody.setLength(((selectBody.length()) - 1));
                BaseTestQuery.testBuilder().sqlQuery("select %s from (select %s from %s limit 1)", selectBody.toString(), columnsList, tableName).unOrdered().baselineRecords(baselineRecords).go();
            }
        }
    }

    @Test
    public void testSingleValueWithMultipleValuesInput() throws Exception {
        thrown.expect(UserRemoteException.class);
        thrown.expectMessage(CoreMatchers.containsString("FUNCTION ERROR"));
        thrown.expectMessage(CoreMatchers.containsString("Input for single_value function has more than one row"));
        BaseTestQuery.test("select single_value(n_name) from cp.`tpch/nation.parquet`");
    }

    /* Streaming agg on top of a filter produces wrong results if the first two batches are filtered out.
    In the below test we have three files in the input directory and since the ordering of reading
    of these files may not be deterministic, we have three tests to make sure we test the case where
    streaming agg gets two empty batches.
     */
    @Test
    public void drill3069() throws Exception {
        final String query = "select max(foo) col1 from dfs.`agg/bugs/drill3069` where foo = %d";
        BaseTestQuery.testBuilder().sqlQuery(query, 2).unOrdered().baselineColumns("col1").baselineValues(2L).go();
        BaseTestQuery.testBuilder().sqlQuery(query, 4).unOrdered().baselineColumns("col1").baselineValues(4L).go();
        BaseTestQuery.testBuilder().sqlQuery(query, 6).unOrdered().baselineColumns("col1").baselineValues(6L).go();
    }

    // DRILL-2748
    @Test
    @Category(UnlikelyTest.class)
    public void testPushFilterPastAgg() throws Exception {
        final String query = " select cnt " + (" from (select n_regionkey, count(*) cnt from cp.`tpch/nation.parquet` group by n_regionkey) " + " where n_regionkey = 2 ");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "(?s)(StreamAgg|HashAgg).*Filter" };
        final String[] excludedPatterns = new String[]{ "(?s)Filter.*(StreamAgg|HashAgg)" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt").baselineValues(5L).build().run();
        // having clause
        final String query2 = " select count(*) cnt from cp.`tpch/nation.parquet` group by n_regionkey " + " having n_regionkey = 2 ";
        PlanTestBase.testPlanMatchingPatterns(query2, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt").baselineValues(5L).build().run();
    }

    @Test
    public void testPushFilterInExprPastAgg() throws Exception {
        final String query = " select cnt " + (" from (select n_regionkey, count(*) cnt from cp.`tpch/nation.parquet` group by n_regionkey) " + " where n_regionkey + 100 - 100 = 2 ");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "(?s)(StreamAgg|HashAgg).*Filter" };
        final String[] excludedPatterns = new String[]{ "(?s)Filter.*(StreamAgg|HashAgg)" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("cnt").baselineValues(5L).build().run();
    }

    @Test
    public void testNegPushFilterInExprPastAgg() throws Exception {
        // negative case: should not push filter, since it involves the aggregate result
        final String query = " select cnt " + (" from (select n_regionkey, count(*) cnt from cp.`tpch/nation.parquet` group by n_regionkey) " + " where cnt + 100 - 100 = 5 ");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "(?s)Filter(?!StreamAgg|!HashAgg)" };
        final String[] excludedPatterns = new String[]{ "(?s)(StreamAgg|HashAgg).*Filter" };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
        // negative case: should not push filter, since it is expression of group key + agg result.
        final String query2 = " select cnt " + (" from (select n_regionkey, count(*) cnt from cp.`tpch/nation.parquet` group by n_regionkey) " + " where cnt + n_regionkey = 5 ");
        PlanTestBase.testPlanMatchingPatterns(query2, expectedPlan, excludedPatterns);
    }

    // DRILL-3781
    // GROUP BY System functions in schema table.
    @Test
    @Category(UnlikelyTest.class)
    public void testGroupBySystemFuncSchemaTable() throws Exception {
        final String query = "select count(*) as cnt from sys.version group by CURRENT_DATE";
        final String[] expectedPlan = new String[]{ "(?s)(StreamAgg|HashAgg)" };
        final String[] excludedPatterns = new String[]{  };
        PlanTestBase.testPlanMatchingPatterns(query, expectedPlan, excludedPatterns);
    }

    // DRILL-3781
    // GROUP BY System functions in csv, parquet, json table.
    @Test
    @Category(UnlikelyTest.class)
    public void testGroupBySystemFuncFileSystemTable() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("select count(*) as cnt from cp.`nation/nation.tbl` group by CURRENT_DATE").unOrdered().baselineColumns("cnt").baselineValues(25L).build().run();
        BaseTestQuery.testBuilder().sqlQuery("select count(*) as cnt from cp.`tpch/nation.parquet` group by CURRENT_DATE").unOrdered().baselineColumns("cnt").baselineValues(25L).build().run();
        BaseTestQuery.testBuilder().sqlQuery("select count(*) as cnt from cp.`employee.json` group by CURRENT_DATE").unOrdered().baselineColumns("cnt").baselineValues(1155L).build().run();
    }

    @Test
    public void test4443() throws Exception {
        BaseTestQuery.test("SELECT MIN(columns[1]) FROM cp.`agg/4443.csv` GROUP BY columns[0]");
    }

    @Test
    public void testCountStarRequired() throws Exception {
        final String query = "select count(*) as col from cp.`tpch/region.parquet`";
        List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList();
        TypeProtos.MajorType majorType = MajorType.newBuilder().setMinorType(BIGINT).setMode(REQUIRED).build();
        expectedSchema.add(Pair.of(SchemaPath.getSimplePath("col"), majorType));
        BaseTestQuery.testBuilder().sqlQuery(query).schemaBaseLine(expectedSchema).build().run();
        BaseTestQuery.testBuilder().sqlQuery(query).unOrdered().baselineColumns("col").baselineValues(5L).build().run();
    }

    // DRILL-4531
    @Test
    @Category(UnlikelyTest.class)
    public void testPushFilterDown() throws Exception {
        final String sql = "SELECT  cust.custAddress, \n" + (((((((((((((((((((("       lineitem.provider \n" + "FROM ( \n") + "      SELECT cast(c_custkey AS bigint) AS custkey, \n") + "             c_address                 AS custAddress \n") + "      FROM   cp.`tpch/customer.parquet` ) cust \n") + "LEFT JOIN \n") + "  ( \n") + "    SELECT DISTINCT l_linenumber, \n") + "           CASE \n") + "             WHEN l_partkey IN (1, 2) THEN \'Store1\'\n") + "             WHEN l_partkey IN (5, 6) THEN \'Store2\'\n") + "           END AS provider \n") + "    FROM  cp.`tpch/lineitem.parquet` \n") + "    WHERE ( l_orderkey >=20160101 AND l_partkey <=20160301) \n") + "      AND   l_partkey IN (1,2, 5, 6) ) lineitem\n") + "ON        cust.custkey = lineitem.l_linenumber \n") + "WHERE     provider IS NOT NULL \n") + "GROUP BY  cust.custAddress, \n") + "          lineitem.provider \n") + "ORDER BY  cust.custAddress, \n") + "          lineitem.provider");
        // Validate the plan
        final String[] expectedPlan = new String[]{ "(?s)(Join).*inner" };// With filter pushdown, left join will be converted into inner join

        final String[] excludedPatterns = new String[]{ "(?s)(Join).*(left)" };
        PlanTestBase.testPlanMatchingPatterns(sql, expectedPlan, excludedPatterns);
    }

    // DRILL-2385: count on complex objects failed with missing function implementation
    @Test
    @Category(UnlikelyTest.class)
    public void testCountComplexObjects() throws Exception {
        final String query = "select count(t.%s) %s from cp.`complex/json/complex.json` t";
        Map<String, String> objectsMap = Maps.newHashMap();
        objectsMap.put("COUNT_BIG_INT_REPEATED", "sia");
        objectsMap.put("COUNT_FLOAT_REPEATED", "sfa");
        objectsMap.put("COUNT_MAP_REPEATED", "soa");
        objectsMap.put("COUNT_MAP_REQUIRED", "oooi");
        objectsMap.put("COUNT_LIST_REPEATED", "odd");
        objectsMap.put("COUNT_LIST_OPTIONAL", "sia");
        for (String object : objectsMap.keySet()) {
            String optionSetting = "";
            if (object.equals("COUNT_LIST_OPTIONAL")) {
                // if `exec.enable_union_type` parameter is true then BIGINT<REPEATED> object is converted to LIST<OPTIONAL> one
                optionSetting = "alter session set `exec.enable_union_type`=true";
            }
            try {
                BaseTestQuery.testBuilder().sqlQuery(query, objectsMap.get(object), object).optionSettingQueriesForTestQuery(optionSetting).unOrdered().baselineColumns(object).baselineValues(3L).go();
            } finally {
                BaseTestQuery.test("ALTER SESSION RESET `exec.enable_union_type`");
            }
        }
    }

    // DRILL-4264
    @Test
    @Category(UnlikelyTest.class)
    public void testCountOnFieldWithDots() throws Exception {
        String fileName = "table.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ExecTest.dirTestWatcher.getRootDir(), fileName)))) {
            writer.write("{\"rk.q\": \"a\", \"m\": {\"a.b\":\"1\", \"a\":{\"b\":\"2\"}, \"c\":\"3\"}}");
        }
        BaseTestQuery.testBuilder().sqlQuery(("select count(t.m.`a.b`) as a,\n" + (((("count(t.m.a.b) as b,\n" + "count(t.m[\'a.b\']) as c,\n") + "count(t.rk.q) as d,\n") + "count(t.`rk.q`) as e\n") + "from dfs.`%s` t")), fileName).unOrdered().baselineColumns("a", "b", "c", "d", "e").baselineValues(1L, 1L, 1L, 0L, 1L).go();
    }

    // DRILL-5768
    @Test
    public void testGroupByWithoutAggregate() throws Exception {
        try {
            BaseTestQuery.test("select * from cp.`tpch/nation.parquet` group by n_regionkey");
            Assert.fail("Exception was not thrown");
        } catch (UserRemoteException e) {
            Assert.assertTrue("No expected current \"Expression \'tpch/nation.parquet.**\' is not being grouped\"", e.getMessage().matches(".*Expression \'tpch/nation\\.parquet\\.\\*\\*\' is not being grouped(.*\\n)*"));
        }
    }
}

