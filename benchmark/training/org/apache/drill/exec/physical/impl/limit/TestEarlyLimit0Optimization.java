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
package org.apache.drill.exec.physical.impl.limit;


import TypeProtos.DataMode.OPTIONAL;
import TypeProtos.MajorType;
import TypeProtos.MinorType.BIGINT;
import TypeProtos.MinorType.BIT;
import TypeProtos.MinorType.DATE;
import TypeProtos.MinorType.FLOAT4;
import TypeProtos.MinorType.FLOAT8;
import TypeProtos.MinorType.INT;
import TypeProtos.MinorType.TIMESTAMP;
import TypeProtos.MinorType.VARCHAR;
import Types.MAX_VARCHAR_LENGTH;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.expr.fn.impl.DateUtility;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(PlannerTest.class)
public class TestEarlyLimit0Optimization extends BaseTestQuery {
    private static final String viewName = "limitZeroEmployeeView";

    // -------------------- SIMPLE QUERIES --------------------
    @Test
    public void infoSchema() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("DESCRIBE %s", TestEarlyLimit0Optimization.viewName).unOrdered().baselineColumns("COLUMN_NAME", "DATA_TYPE", "IS_NULLABLE").baselineValues("employee_id", "INTEGER", "YES").baselineValues("full_name", "CHARACTER VARYING", "YES").baselineValues("position_id", "INTEGER", "YES").baselineValues("department_id", "BIGINT", "YES").baselineValues("birth_date", "DATE", "YES").baselineValues("hire_date", "TIMESTAMP", "YES").baselineValues("salary", "DOUBLE", "YES").baselineValues("fsalary", "FLOAT", "YES").baselineValues("single", "BOOLEAN", "NO").baselineValues("education_level", "CHARACTER VARYING", "YES").baselineValues("gender", "CHARACTER", "YES").go();
    }

    @Test
    public void simpleSelect() throws Exception {
        BaseTestQuery.testBuilder().sqlQuery("SELECT * FROM %s", TestEarlyLimit0Optimization.viewName).ordered().baselineColumns("employee_id", "full_name", "position_id", "department_id", "birth_date", "hire_date", "salary", "fsalary", "single", "education_level", "gender").baselineValues(1, "Sheri Nowmer", 1, 1L, LocalDate.parse("1961-08-26"), LocalDateTime.parse("1994-12-01 00:00:00", DateUtility.getDateTimeFormatter()), 80000.0, 80000.0F, true, "Graduate Degree", "F").go();
    }

    @Test
    public void simpleSelectLimit0() throws Exception {
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("employee_id"), Types.optional(INT)), Pair.of(SchemaPath.getSimplePath("full_name"), Types.withPrecision(VARCHAR, OPTIONAL, 25)), Pair.of(SchemaPath.getSimplePath("position_id"), Types.optional(INT)), Pair.of(SchemaPath.getSimplePath("department_id"), Types.optional(BIGINT)), Pair.of(SchemaPath.getSimplePath("birth_date"), Types.optional(DATE)), Pair.of(SchemaPath.getSimplePath("hire_date"), Types.optional(TIMESTAMP)), Pair.of(SchemaPath.getSimplePath("salary"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("fsalary"), Types.optional(FLOAT4)), Pair.of(SchemaPath.getSimplePath("single"), Types.required(BIT)), Pair.of(SchemaPath.getSimplePath("education_level"), Types.withPrecision(VARCHAR, OPTIONAL, 60)), Pair.of(SchemaPath.getSimplePath("gender"), Types.withPrecision(VARCHAR, OPTIONAL, 1)));
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(String.format("SELECT * FROM %s", TestEarlyLimit0Optimization.viewName))).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(("SELECT * FROM " + (TestEarlyLimit0Optimization.viewName)));
    }

    @Test
    public void sums() throws Exception {
        final String query = TestEarlyLimit0Optimization.getAggQuery("SUM");
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("e"), Types.optional(BIGINT)), Pair.of(SchemaPath.getSimplePath("p"), Types.optional(BIGINT)), Pair.of(SchemaPath.getSimplePath("d"), Types.optional(BIGINT)), Pair.of(SchemaPath.getSimplePath("s"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("f"), Types.optional(FLOAT8)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("e", "p", "d", "s", "f").baselineValues(1L, 1L, 1L, 80000.0, 80000.0).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void counts() throws Exception {
        final String query = TestEarlyLimit0Optimization.getAggQuery("COUNT");
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("e"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("p"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("d"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("s"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("f"), Types.required(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).baselineColumns("e", "p", "d", "s", "f").ordered().baselineValues(1L, 1L, 1L, 1L, 1L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void mins() throws Exception {
        minAndMaxTest("MIN");
    }

    @Test
    public void maxs() throws Exception {
        minAndMaxTest("MAX");
    }

    @Test
    public void avgs() throws Exception {
        final String query = TestEarlyLimit0Optimization.getAggQuery("AVG");
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("e"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("p"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("d"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("s"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("f"), Types.optional(FLOAT8)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("e", "p", "d", "s", "f").baselineValues(1.0, 1.0, 1.0, 80000.0, 80000.0).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void measures() throws Exception {
        final String query = ("SELECT " + (((("STDDEV_SAMP(employee_id) AS s, " + "STDDEV_POP(position_id) AS p, ") + "AVG(position_id) AS a, ") + "COUNT(position_id) AS c ") + "FROM ")) + (TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("s"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("p"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("a"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("c"), Types.required(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("s", "p", "a", "c").baselineValues(null, 0.0, 1.0, 1L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void nullableCount() throws Exception {
        final String query = ("SELECT " + "COUNT(CASE WHEN position_id = 1 THEN NULL ELSE position_id END) AS c FROM ") + (TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("c"), Types.required(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("c").baselineValues(0L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void nullableSumAndCount() throws Exception {
        final String query = String.format(("SELECT " + (("COUNT(position_id) AS c, " + "SUM(CAST((CASE WHEN position_id = 1 THEN NULL ELSE position_id END) AS INT)) AS p ") + "FROM %s")), TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("c"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("p"), Types.optional(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("c", "p").baselineValues(1L, null).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void castSum() throws Exception {
        final String query = "SELECT CAST(SUM(position_id) AS INT) AS s FROM cp.`employee.json`";
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("s"), Types.optional(INT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("s").baselineValues(18422).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void sumCast() throws Exception {
        final String query = "SELECT SUM(CAST(position_id AS INT)) AS s FROM cp.`employee.json`";
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("s"), Types.optional(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("s").baselineValues(18422L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void sumsAndCounts1() throws Exception {
        final String query = String.format(("SELECT " + ((((("COUNT(*) as cs, " + "COUNT(1) as c1, ") + "COUNT(employee_id) as cc, ") + "SUM(1) as s1,") + "department_id ") + " FROM %s GROUP BY department_id")), TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("cs"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("c1"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("cc"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("s1"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("department_id"), Types.optional(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("cs", "c1", "cc", "s1", "department_id").baselineValues(1L, 1L, 1L, 1L, 1L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void sumsAndCounts2() throws Exception {
        final String query = "SELECT " + ((((("SUM(1) as s1, " + "COUNT(1) as c1, ") + "COUNT(*) as cs, ") + "COUNT(CAST(n_regionkey AS INT)) as cc ") + "FROM cp.`tpch/nation.parquet` ") + "GROUP BY CAST(n_regionkey AS INT)");
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("s1"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("c1"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("cs"), Types.required(BIGINT)), Pair.of(SchemaPath.getSimplePath("cc"), Types.required(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("s1", "c1", "cs", "cc").baselineValues(5L, 5L, 5L, 5L).baselineValues(5L, 5L, 5L, 5L).baselineValues(5L, 5L, 5L, 5L).baselineValues(5L, 5L, 5L, 5L).baselineValues(5L, 5L, 5L, 5L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void rank() throws Exception {
        final String query = "SELECT RANK() OVER(PARTITION BY employee_id ORDER BY employee_id) AS r FROM " + (TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("r"), Types.required(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).ordered().baselineColumns("r").baselineValues(1L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    // -------------------- SCALAR FUNC. QUERIES --------------------
    @Test
    public void cast() throws Exception {
        final String query = String.format(("SELECT CAST(fsalary AS DOUBLE) AS d," + "CAST(employee_id AS BIGINT) AS e FROM %s"), TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("d"), Types.optional(FLOAT8)), Pair.of(SchemaPath.getSimplePath("e"), Types.optional(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).baselineColumns("d", "e").ordered().baselineValues(80000.0, 1L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void concat() throws Exception {
        concatTest(("SELECT CONCAT(full_name, education_level) AS c FROM " + (TestEarlyLimit0Optimization.viewName)), 85, false);
    }

    @Test
    public void concatOp() throws Exception {
        concatTest(("SELECT full_name || education_level AS c FROM " + (TestEarlyLimit0Optimization.viewName)), 85, true);
    }

    @Test
    public void extract() throws Exception {
        final String query = "SELECT EXTRACT(YEAR FROM hire_date) AS e FROM " + (TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("e"), Types.optional(BIGINT)));
        BaseTestQuery.testBuilder().sqlQuery(query).baselineColumns("e").ordered().baselineValues(1994L).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void binary() throws Exception {
        final String query = ("SELECT " + ((((((((((((("single AND true AS b, " + "full_name || education_level AS c, ") + "position_id / position_id AS d, ") + "position_id = position_id AS e, ") + "position_id > position_id AS g, ") + "position_id >= position_id AS ge, ") + "position_id IN (0, 1) AS i, +") + "position_id < position_id AS l, ") + "position_id <= position_id AS le, ") + "position_id - position_id AS m, ") + "position_id * position_id AS mu, ") + "position_id <> position_id AS n, ") + "single OR false AS o, ") + "position_id + position_id AS p FROM ")) + (TestEarlyLimit0Optimization.viewName);
        @SuppressWarnings("unchecked")
        final List<Pair<SchemaPath, TypeProtos.MajorType>> expectedSchema = Lists.newArrayList(Pair.of(SchemaPath.getSimplePath("b"), Types.required(BIT)), Pair.of(SchemaPath.getSimplePath("c"), Types.withPrecision(VARCHAR, OPTIONAL, 85)), Pair.of(SchemaPath.getSimplePath("d"), Types.optional(INT)), Pair.of(SchemaPath.getSimplePath("e"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("g"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("ge"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("i"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("l"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("le"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("m"), Types.optional(INT)), Pair.of(SchemaPath.getSimplePath("mu"), Types.optional(INT)), Pair.of(SchemaPath.getSimplePath("n"), Types.optional(BIT)), Pair.of(SchemaPath.getSimplePath("o"), Types.required(BIT)), Pair.of(SchemaPath.getSimplePath("p"), Types.optional(INT)));
        BaseTestQuery.testBuilder().sqlQuery(query).baselineColumns("b", "c", "d", "e", "g", "ge", "i", "l", "le", "m", "mu", "n", "o", "p").ordered().baselineValues(true, "Sheri NowmerGraduate Degree", 1, true, false, true, true, false, true, 0, 1, false, true, 2).go();
        BaseTestQuery.testBuilder().sqlQuery(TestEarlyLimit0Optimization.wrapLimit0(query)).schemaBaseLine(expectedSchema).go();
        TestEarlyLimit0Optimization.checkThatQueryPlanIsOptimized(query);
    }

    @Test
    public void substring() throws Exception {
        substringTest(("SELECT SUBSTRING(full_name, 1, 5) AS s FROM " + (TestEarlyLimit0Optimization.viewName)), MAX_VARCHAR_LENGTH);
    }

    @Test
    public void substr() throws Exception {
        substringTest(("SELECT SUBSTR(full_name, 1, 5) AS s FROM " + (TestEarlyLimit0Optimization.viewName)), MAX_VARCHAR_LENGTH);
    }
}

