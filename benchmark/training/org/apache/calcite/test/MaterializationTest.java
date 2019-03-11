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


import Hook.SUB;
import Prepare.THREAD_TRIM;
import ReflectiveSchema.Factory;
import SqlStdOperatorTable.AND;
import SqlStdOperatorTable.EQUALS;
import SqlStdOperatorTable.NOT;
import SqlStdOperatorTable.OR;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.materialize.MaterializationService;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.SubstitutionVisitor;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelReferentialConstraint;
import org.apache.calcite.rel.RelReferentialConstraintImpl;
import org.apache.calcite.rel.RelVisitor;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexSimplify;
import org.apache.calcite.schema.QueryableTable;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.tools.RuleSets;
import org.apache.calcite.util.JsonBuilder;
import org.apache.calcite.util.Smalls;
import org.apache.calcite.util.TryThreadLocal;
import org.apache.calcite.util.mapping.IntPair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit test for the materialized view rewrite mechanism. Each test has a
 * query and one or more materializations (what Oracle calls materialized views)
 * and checks that the materialization is used.
 */
@Category(SlowTests.class)
public class MaterializationTest {
    private static final Consumer<ResultSet> CONTAINS_M0 = CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, m0]])");

    private static final Consumer<ResultSet> CONTAINS_LOCATIONS = CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, locations]])");

    private static final Ordering<Iterable<String>> CASE_INSENSITIVE_LIST_COMPARATOR = Ordering.from(String.CASE_INSENSITIVE_ORDER).lexicographical();

    private static final Ordering<Iterable<List<String>>> CASE_INSENSITIVE_LIST_LIST_COMPARATOR = MaterializationTest.CASE_INSENSITIVE_LIST_COMPARATOR.lexicographical();

    private static final String HR_FKUK_SCHEMA = (((((((("{\n" + (("       type: \'custom\',\n" + "       name: \'hr\',\n") + "       factory: '")) + (Factory.class.getName())) + "\',\n") + "       operand: {\n") + "         class: '") + (MaterializationTest.HrFKUKSchema.class.getName())) + "\'\n") + "       }\n") + "     }\n";

    private static final String HR_FKUK_MODEL = ((("{\n" + (("  version: \'1.0\',\n" + "  defaultSchema: \'hr\',\n") + "   schemas: [\n")) + (MaterializationTest.HR_FKUK_SCHEMA)) + "   ]\n") + "}";

    final JavaTypeFactoryImpl typeFactory = new JavaTypeFactoryImpl(RelDataTypeSystem.DEFAULT);

    private final RexBuilder rexBuilder = new RexBuilder(typeFactory);

    private final RexSimplify simplify = withParanoid(true);

    @Test
    public void testScan() {
        CalciteAssert.that().withMaterializations(((((((((((((((("{\n" + ((((((("  version: \'1.0\',\n" + "  defaultSchema: \'SCOTT_CLONE\',\n") + "  schemas: [ {\n") + "    name: \'SCOTT_CLONE\',\n") + "    type: \'custom\',\n") + "    factory: \'org.apache.calcite.adapter.clone.CloneSchema$Factory\',\n") + "    operand: {\n") + "      jdbcDriver: '")) + (JdbcTest.SCOTT.driver)) + "\',\n") + "      jdbcUser: '") + (JdbcTest.SCOTT.username)) + "\',\n") + "      jdbcPassword: '") + (JdbcTest.SCOTT.password)) + "\',\n") + "      jdbcUrl: '") + (JdbcTest.SCOTT.url)) + "\',\n") + "      jdbcSchema: \'SCOTT\'\n") + "   } } ]\n") + "}"), "m0", "select empno, deptno from emp order by deptno").query("select empno, deptno from emp").enableMaterializations(true).explainContains("EnumerableTableScan(table=[[SCOTT_CLONE, m0]])").sameResultWithMaterializationsDisabled();
    }

    @Test
    public void testFilter() {
        CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", "select * from \"emps\" where \"deptno\" = 10").query("select \"empid\" + 1 from \"emps\" where \"deptno\" = 10").enableMaterializations(true).explainContains("EnumerableTableScan(table=[[hr, m0]])").sameResultWithMaterializationsDisabled();
    }

    @Test
    public void testFilterQueryOnProjectView() {
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", "select \"deptno\", \"empid\" from \"emps\"").query("select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10").enableMaterializations(true).explainContains("EnumerableTableScan(table=[[hr, m0]])").sameResultWithMaterializationsDisabled();
        }
    }

    /**
     * Runs the same test as {@link #testFilterQueryOnProjectView()} but more
     * concisely.
     */
    @Test
    public void testFilterQueryOnProjectView0() {
        checkMaterialize("select \"deptno\", \"empid\" from \"emps\"", "select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10");
    }

    /**
     * As {@link #testFilterQueryOnProjectView()} but with extra column in
     * materialized view.
     */
    @Test
    public void testFilterQueryOnProjectView1() {
        checkMaterialize("select \"deptno\", \"empid\", \"name\" from \"emps\"", "select \"empid\" + 1 as x from \"emps\" where \"deptno\" = 10");
    }

    /**
     * As {@link #testFilterQueryOnProjectView()} but with extra column in both
     * materialized view and query.
     */
    @Test
    public void testFilterQueryOnProjectView2() {
        checkMaterialize("select \"deptno\", \"empid\", \"name\" from \"emps\"", "select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10");
    }

    @Test
    public void testFilterQueryOnProjectView3() {
        checkMaterialize("select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\"", "select \"name\" from \"emps\" where \"deptno\" - 10 = 0");
    }

    /**
     * As {@link #testFilterQueryOnProjectView3()} but materialized view cannot
     * be used because it does not contain required expression.
     */
    @Test
    public void testFilterQueryOnProjectView4() {
        checkNoMaterialize("select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\"", "select \"name\" from \"emps\" where \"deptno\" + 10 = 20", MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnProjectView3()} but also contains an
     * expression column.
     */
    @Test
    public void testFilterQueryOnProjectView5() {
        checkMaterialize(("select \"deptno\" - 10 as \"x\", \"empid\" + 1 as ee, \"name\"\n" + "from \"emps\""), ("select \"name\", \"empid\" + 1 as e\n" + "from \"emps\" where \"deptno\" - 10 = 2"), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..2=[{inputs}], expr#3=[2], " + ("expr#4=[=($t0, $t3)], name=[$t2], EE=[$t1], $condition=[$t4])\n" + "  EnumerableTableScan(table=[[hr, m0]]"))));
    }

    /**
     * Cannot materialize because "name" is not projected in the MV.
     */
    @Test
    public void testFilterQueryOnProjectView6() {
        checkNoMaterialize("select \"deptno\" - 10 as \"x\", \"empid\"  from \"emps\"", "select \"name\" from \"emps\" where \"deptno\" - 10 = 0", MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnProjectView3()} but also contains an
     * expression column.
     */
    @Test
    public void testFilterQueryOnProjectView7() {
        checkNoMaterialize("select \"deptno\" - 10 as \"x\", \"empid\" + 1, \"name\" from \"emps\"", "select \"name\", \"empid\" + 2 from \"emps\" where \"deptno\" - 10 = 0", MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-988">[CALCITE-988]
     * FilterToProjectUnifyRule.invert(MutableRel, MutableRel, MutableProject)
     * works incorrectly</a>.
     */
    @Test
    public void testFilterQueryOnProjectView8() {
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            final String m = "select \"salary\", \"commission\",\n" + "\"deptno\", \"empid\", \"name\" from \"emps\"";
            final String v = "select * from \"emps\" where \"name\" is null";
            final String q = "select * from V where \"commission\" is null";
            final JsonBuilder builder = new JsonBuilder();
            final String model = ((((((((((((((((((((("{\n" + (((((((("  version: \'1.0\',\n" + "  defaultSchema: \'hr\',\n") + "  schemas: [\n") + "    {\n") + "      materializations: [\n") + "        {\n") + "          table: \'m0\',\n") + "          view: \'m0v\',\n") + "          sql: ")) + (builder.toJsonString(m))) + "        }\n") + "      ],\n") + "      tables: [\n") + "        {\n") + "          name: \'V\',\n") + "          type: \'view\',\n") + "          sql: ") + (builder.toJsonString(v))) + "\n") + "        }\n") + "      ],\n") + "      type: \'custom\',\n") + "      name: \'hr\',\n") + "      factory: \'org.apache.calcite.adapter.java.ReflectiveSchema$Factory\',\n") + "      operand: {\n") + "        class: \'org.apache.calcite.test.JdbcTest$HrSchema\'\n") + "      }\n") + "    }\n") + "  ]\n") + "}\n";
            CalciteAssert.that().withModel(model).query(q).enableMaterializations(true).explainMatches("", MaterializationTest.CONTAINS_M0).sameResultWithMaterializationsDisabled();
        }
    }

    @Test
    public void testFilterQueryOnFilterView() {
        checkMaterialize("select \"deptno\", \"empid\", \"name\" from \"emps\" where \"deptno\" = 10", "select \"empid\" + 1 as x, \"name\" from \"emps\" where \"deptno\" = 10");
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is stronger in
     * query.
     */
    @Test
    public void testFilterQueryOnFilterView4() {
        checkMaterialize("select * from \"emps\" where \"deptno\" > 10", "select \"name\" from \"emps\" where \"deptno\" > 30");
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is stronger in
     * query and columns selected are subset of columns in materialized view.
     */
    @Test
    public void testFilterQueryOnFilterView5() {
        checkMaterialize("select \"name\", \"deptno\" from \"emps\" where \"deptno\" > 10", "select \"name\" from \"emps\" where \"deptno\" > 30");
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is stronger in
     * query and columns selected are subset of columns in materialized view.
     */
    @Test
    public void testFilterQueryOnFilterView6() {
        checkMaterialize(("select \"name\", \"deptno\", \"salary\" from \"emps\" " + "where \"salary\" > 2000.5"), "select \"name\" from \"emps\" where \"deptno\" > 30 and \"salary\" > 3000");
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is stronger in
     * query and columns selected are subset of columns in materialized view.
     * Condition here is complex.
     */
    @Test
    public void testFilterQueryOnFilterView7() {
        checkMaterialize(("select * from \"emps\" where " + (("((\"salary\" < 1111.9 and \"deptno\" > 10)" + "or (\"empid\" > 400 and \"salary\" > 5000) ") + "or \"salary\" > 500)")), ("select \"name\" from \"emps\" where (\"salary\" > 1000 " + "or (\"deptno\" >= 30 and \"salary\" <= 500))"));
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is stronger in
     * query. However, columns selected are not present in columns of materialized
     * view, Hence should not use materialized view.
     */
    @Test
    public void testFilterQueryOnFilterView8() {
        checkNoMaterialize("select \"name\", \"deptno\" from \"emps\" where \"deptno\" > 10", "select \"name\", \"empid\" from \"emps\" where \"deptno\" > 30", MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is weaker in
     * query.
     */
    @Test
    public void testFilterQueryOnFilterView9() {
        checkNoMaterialize("select \"name\", \"deptno\" from \"emps\" where \"deptno\" > 10", ("select \"name\", \"empid\" from \"emps\" " + "where \"deptno\" > 30 or \"empid\" > 10"), MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition currently
     * has unsupported type being checked on query.
     */
    @Test
    public void testFilterQueryOnFilterView10() {
        checkNoMaterialize(("select \"name\", \"deptno\" from \"emps\" where \"deptno\" > 10 " + "and \"name\" = \'calcite\'"), ("select \"name\", \"empid\" from \"emps\" where \"deptno\" > 30 " + "or \"empid\" > 10"), MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is weaker in
     * query and columns selected are subset of columns in materialized view.
     * Condition here is complex.
     */
    @Test
    public void testFilterQueryOnFilterView11() {
        checkNoMaterialize(("select \"name\", \"deptno\" from \"emps\" where " + ("(\"salary\" < 1111.9 and \"deptno\" > 10)" + "or (\"empid\" > 400 and \"salary\" > 5000)")), "select \"name\" from \"emps\" where \"deptno\" > 30 and \"salary\" > 3000", MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition of
     * query is stronger but is on the column not present in MV (salary).
     */
    @Test
    public void testFilterQueryOnFilterView12() {
        checkNoMaterialize("select \"name\", \"deptno\" from \"emps\" where \"salary\" > 2000.5", "select \"name\" from \"emps\" where \"deptno\" > 30 and \"salary\" > 3000", MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnFilterView()} but condition is weaker in
     * query and columns selected are subset of columns in materialized view.
     * Condition here is complex.
     */
    @Test
    public void testFilterQueryOnFilterView13() {
        checkNoMaterialize(("select * from \"emps\" where " + ("(\"salary\" < 1111.9 and \"deptno\" > 10)" + "or (\"empid\" > 400 and \"salary\" > 5000)")), ("select \"name\" from \"emps\" where \"salary\" > 1000 " + "or (\"deptno\" > 30 and \"salary\" > 3000)"), MaterializationTest.HR_FKUK_MODEL);
    }

    /**
     * As {@link #testFilterQueryOnFilterView7()} but columns in materialized
     * view are a permutation of columns in the query.
     */
    @Test
    public void testFilterQueryOnFilterView14() {
        String q = "select * from \"emps\" where (\"salary\" > 1000 " + "or (\"deptno\" >= 30 and \"salary\" <= 500))";
        String m = "select \"deptno\", \"empid\", \"name\", \"salary\", \"commission\" " + ((("from \"emps\" as em where " + "((\"salary\" < 1111.9 and \"deptno\" > 10)") + "or (\"empid\" > 400 and \"salary\" > 5000) ") + "or \"salary\" > 500)");
        checkMaterialize(m, q);
    }

    /**
     * As {@link #testFilterQueryOnFilterView13()} but using alias
     * and condition of query is stronger.
     */
    @Test
    public void testAlias() {
        checkMaterialize(("select * from \"emps\" as em where " + ("(em.\"salary\" < 1111.9 and em.\"deptno\" > 10)" + "or (em.\"empid\" > 400 and em.\"salary\" > 5000)")), ("select \"name\" as n from \"emps\" as e where " + "(e.\"empid\" > 500 and e.\"salary\" > 6000)"));
    }

    /**
     * Aggregation query at same level of aggregation as aggregation
     * materialization.
     */
    @Test
    public void testAggregate() {
        checkMaterialize("select \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" group by \"deptno\"", "select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\"");
    }

    /**
     * Aggregation query at coarser level of aggregation than aggregation
     * materialization. Requires an additional aggregate to roll up. Note that
     * COUNT is rolled up using SUM.
     */
    @Test
    public void testAggregateRollUp() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" " + "group by \"empid\", \"deptno\""), "select count(*) + 1 as c, \"deptno\" from \"emps\" group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], " + (("expr#3=[+($t1, $t2)], C=[$t3], deptno=[$t0])\n" + "  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($2)])\n") + "    EnumerableTableScan(table=[[hr, m0]])"))));
    }

    /**
     * Unit test for logic functions
     * {@link org.apache.calcite.plan.SubstitutionVisitor#mayBeSatisfiable} and
     * {@link RexUtil#simplify}.
     */
    @Test
    public void testSatisfiable() {
        // TRUE may be satisfiable
        checkSatisfiable(rexBuilder.makeLiteral(true), "true");
        // FALSE is not satisfiable
        checkNotSatisfiable(rexBuilder.makeLiteral(false));
        // The expression "$0 = 1".
        final RexNode i0_eq_0 = rexBuilder.makeCall(EQUALS, rexBuilder.makeInputRef(typeFactory.createType(int.class), 0), rexBuilder.makeExactLiteral(BigDecimal.ZERO));
        // "$0 = 1" may be satisfiable
        checkSatisfiable(i0_eq_0, "=($0, 0)");
        // "$0 = 1 AND TRUE" may be satisfiable
        final RexNode e0 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeLiteral(true));
        checkSatisfiable(e0, "=($0, 0)");
        // "$0 = 1 AND FALSE" is not satisfiable
        final RexNode e1 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeLiteral(false));
        checkNotSatisfiable(e1);
        // "$0 = 0 AND NOT $0 = 0" is not satisfiable
        final RexNode e2 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeCall(NOT, i0_eq_0));
        checkNotSatisfiable(e2);
        // "TRUE AND NOT $0 = 0" may be satisfiable. Can simplify.
        final RexNode e3 = rexBuilder.makeCall(AND, rexBuilder.makeLiteral(true), rexBuilder.makeCall(NOT, i0_eq_0));
        checkSatisfiable(e3, "<>($0, 0)");
        // The expression "$1 = 1".
        final RexNode i1_eq_1 = rexBuilder.makeCall(EQUALS, rexBuilder.makeInputRef(typeFactory.createType(int.class), 1), rexBuilder.makeExactLiteral(BigDecimal.ONE));
        // "$0 = 0 AND $1 = 1 AND NOT $0 = 0" is not satisfiable
        final RexNode e4 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeCall(AND, i1_eq_1, rexBuilder.makeCall(NOT, i0_eq_0)));
        checkNotSatisfiable(e4);
        // "$0 = 0 AND NOT $1 = 1" may be satisfiable. Can't simplify.
        final RexNode e5 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeCall(NOT, i1_eq_1));
        checkSatisfiable(e5, "AND(=($0, 0), <>($1, 1))");
        // "$0 = 0 AND NOT ($0 = 0 AND $1 = 1)" may be satisfiable. Can simplify.
        final RexNode e6 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeCall(NOT, rexBuilder.makeCall(AND, i0_eq_0, i1_eq_1)));
        checkSatisfiable(e6, "AND(=($0, 0), OR(<>($0, 0), <>($1, 1)))");
        // "$0 = 0 AND ($1 = 1 AND NOT ($0 = 0))" is not satisfiable.
        final RexNode e7 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeCall(AND, i1_eq_1, rexBuilder.makeCall(NOT, i0_eq_0)));
        checkNotSatisfiable(e7);
        // The expression "$2".
        final RexInputRef i2 = rexBuilder.makeInputRef(typeFactory.createType(boolean.class), 2);
        // The expression "$3".
        final RexInputRef i3 = rexBuilder.makeInputRef(typeFactory.createType(boolean.class), 3);
        // The expression "$4".
        final RexInputRef i4 = rexBuilder.makeInputRef(typeFactory.createType(boolean.class), 4);
        // "$0 = 0 AND $2 AND $3 AND NOT ($2 AND $3 AND $4) AND NOT ($2 AND $4)" may
        // be satisfiable. Can't simplify.
        final RexNode e8 = rexBuilder.makeCall(AND, i0_eq_0, rexBuilder.makeCall(AND, i2, rexBuilder.makeCall(AND, i3, rexBuilder.makeCall(NOT, rexBuilder.makeCall(AND, i2, i3, i4)), rexBuilder.makeCall(NOT, i4))));
        checkSatisfiable(e8, "AND(=($0, 0), $2, $3, OR(NOT($2), NOT($3), NOT($4)), NOT($4))");
    }

    @Test
    public void testSplitFilter() {
        final RexLiteral i1 = rexBuilder.makeExactLiteral(BigDecimal.ONE);
        final RexLiteral i2 = rexBuilder.makeExactLiteral(BigDecimal.valueOf(2));
        final RexLiteral i3 = rexBuilder.makeExactLiteral(BigDecimal.valueOf(3));
        final RelDataType intType = typeFactory.createType(int.class);
        final RexInputRef x = rexBuilder.makeInputRef(intType, 0);// $0

        final RexInputRef y = rexBuilder.makeInputRef(intType, 1);// $1

        final RexInputRef z = rexBuilder.makeInputRef(intType, 2);// $2

        final RexNode x_eq_1 = rexBuilder.makeCall(EQUALS, x, i1);// $0 = 1

        final RexNode x_eq_1_b = rexBuilder.makeCall(EQUALS, i1, x);// 1 = $0

        final RexNode x_eq_2 = rexBuilder.makeCall(EQUALS, x, i2);// $0 = 2

        final RexNode y_eq_2 = rexBuilder.makeCall(EQUALS, y, i2);// $1 = 2

        final RexNode z_eq_3 = rexBuilder.makeCall(EQUALS, z, i3);// $2 = 3

        RexNode newFilter;
        // Example 1.
        // condition: x = 1 or y = 2
        // target:    y = 2 or 1 = x
        // yields
        // residue:   true
        newFilter = SubstitutionVisitor.splitFilter(simplify, rexBuilder.makeCall(OR, x_eq_1, y_eq_2), rexBuilder.makeCall(OR, y_eq_2, x_eq_1_b));
        Assert.assertThat(newFilter.isAlwaysTrue(), CoreMatchers.equalTo(true));
        // Example 2.
        // condition: x = 1,
        // target:    x = 1 or z = 3
        // yields
        // residue:   x = 1
        newFilter = SubstitutionVisitor.splitFilter(simplify, x_eq_1, rexBuilder.makeCall(OR, x_eq_1, z_eq_3));
        Assert.assertThat(newFilter.toString(), CoreMatchers.equalTo("=($0, 1)"));
        // 2b.
        // condition: x = 1 or y = 2
        // target:    x = 1 or y = 2 or z = 3
        // yields
        // residue:   x = 1 or y = 2
        newFilter = SubstitutionVisitor.splitFilter(simplify, rexBuilder.makeCall(OR, x_eq_1, y_eq_2), rexBuilder.makeCall(OR, x_eq_1, y_eq_2, z_eq_3));
        Assert.assertThat(newFilter.toString(), CoreMatchers.equalTo("OR(=($0, 1), =($1, 2))"));
        // 2c.
        // condition: x = 1
        // target:    x = 1 or y = 2 or z = 3
        // yields
        // residue:   x = 1
        newFilter = SubstitutionVisitor.splitFilter(simplify, x_eq_1, rexBuilder.makeCall(OR, x_eq_1, y_eq_2, z_eq_3));
        Assert.assertThat(newFilter.toString(), CoreMatchers.equalTo("=($0, 1)"));
        // 2d.
        // condition: x = 1 or y = 2
        // target:    y = 2 or x = 1
        // yields
        // residue:   true
        newFilter = SubstitutionVisitor.splitFilter(simplify, rexBuilder.makeCall(OR, x_eq_1, y_eq_2), rexBuilder.makeCall(OR, y_eq_2, x_eq_1));
        Assert.assertThat(newFilter.isAlwaysTrue(), CoreMatchers.equalTo(true));
        // 2e.
        // condition: x = 1
        // target:    x = 1 (different object)
        // yields
        // residue:   true
        newFilter = SubstitutionVisitor.splitFilter(simplify, x_eq_1, x_eq_1_b);
        Assert.assertThat(newFilter.isAlwaysTrue(), CoreMatchers.equalTo(true));
        // 2f.
        // condition: x = 1 or y = 2
        // target:    x = 1
        // yields
        // residue:   null
        newFilter = SubstitutionVisitor.splitFilter(simplify, rexBuilder.makeCall(OR, x_eq_1, y_eq_2), x_eq_1);
        Assert.assertNull(newFilter);
        // Example 3.
        // Condition [x = 1 and y = 2],
        // target [y = 2 and x = 1] yields
        // residue [true].
        newFilter = SubstitutionVisitor.splitFilter(simplify, rexBuilder.makeCall(AND, x_eq_1, y_eq_2), rexBuilder.makeCall(AND, y_eq_2, x_eq_1));
        Assert.assertThat(newFilter.isAlwaysTrue(), CoreMatchers.equalTo(true));
        // Example 4.
        // condition: x = 1 and y = 2
        // target:    y = 2
        // yields
        // residue:   x = 1
        newFilter = SubstitutionVisitor.splitFilter(simplify, rexBuilder.makeCall(AND, x_eq_1, y_eq_2), y_eq_2);
        Assert.assertThat(newFilter.toString(), CoreMatchers.equalTo("=($0, 1)"));
        // Example 5.
        // condition: x = 1
        // target:    x = 1 and y = 2
        // yields
        // residue:   null
        newFilter = SubstitutionVisitor.splitFilter(simplify, x_eq_1, rexBuilder.makeCall(AND, x_eq_1, y_eq_2));
        Assert.assertNull(newFilter);
        // Example 6.
        // condition: x = 1
        // target:    y = 2
        // yields
        // residue:   null
        newFilter = SubstitutionVisitor.splitFilter(simplify, x_eq_1, y_eq_2);
        Assert.assertNull(newFilter);
        // Example 7.
        // condition: x = 1
        // target:    x = 2
        // yields
        // residue:   null
        newFilter = SubstitutionVisitor.splitFilter(simplify, x_eq_1, x_eq_2);
        Assert.assertNull(newFilter);
    }

    @Test
    public void testJoinMaterialization() {
        String q = "select *\n" + ("from (select * from \"emps\" where \"empid\" < 300)\n" + "join \"depts\" using (\"deptno\")");
        checkMaterialize("select * from \"emps\" where \"empid\" < 500", q);
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-891">[CALCITE-891]
     * TableScan without Project cannot be substituted by any projected
     * materialization</a>.
     */
    @Test
    public void testJoinMaterialization2() {
        String q = "select *\n" + ("from \"emps\"\n" + "join \"depts\" using (\"deptno\")");
        final String m = "select \"deptno\", \"empid\", \"name\",\n" + "\"salary\", \"commission\" from \"emps\"";
        checkMaterialize(m, q);
    }

    @Test
    public void testJoinMaterialization3() {
        String q = "select \"empid\" \"deptno\" from \"emps\"\n" + "join \"depts\" using (\"deptno\") where \"empid\" = 1";
        final String m = "select \"empid\" \"deptno\" from \"emps\"\n" + "join \"depts\" using (\"deptno\")";
        checkMaterialize(m, q);
    }

    @Test
    public void testUnionAll() {
        String q = "select * from \"emps\" where \"empid\" > 300\n" + "union all select * from \"emps\" where \"empid\" < 200";
        String m = "select * from \"emps\" where \"empid\" < 500";
        checkMaterialize(m, q, MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, m0]])", 1));
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs1() {
        checkMaterialize("select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\"", "select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, m0]])"));
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs2() {
        checkMaterialize("select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\"", "select \"deptno\" from \"emps\" group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs3() {
        checkNoMaterialize("select \"deptno\" from \"emps\" group by \"deptno\"", "select \"empid\", \"deptno\" from \"emps\" group by \"empid\", \"deptno\"", MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs4() {
        checkMaterialize("select \"empid\", \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"empid\", \"deptno\"", "select \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs5() {
        checkNoMaterialize("select \"empid\", \"deptno\" from \"emps\" where \"deptno\" = 5 group by \"empid\", \"deptno\"", "select \"deptno\" from \"emps\" where \"deptno\" = 10 group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs6() {
        checkMaterialize("select \"empid\", \"deptno\" from \"emps\" where \"deptno\" > 5 group by \"empid\", \"deptno\"", "select \"deptno\" from \"emps\" where \"deptno\" > 10 group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}])\n" + (("  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[10], expr#3=[<($t2, $t1)], " + "proj#0..1=[{exprs}], $condition=[$t3])\n") + "    EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs7() {
        checkNoMaterialize("select \"empid\", \"deptno\" from \"emps\" where \"deptno\" > 5 group by \"empid\", \"deptno\"", "select \"deptno\" from \"emps\" where \"deptno\" < 10 group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs8() {
        checkNoMaterialize("select \"empid\" from \"emps\" group by \"empid\", \"deptno\"", "select \"deptno\" from \"emps\" group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testAggregateMaterializationNoAggregateFuncs9() {
        checkNoMaterialize(("select \"empid\", \"deptno\" from \"emps\"\n" + "where \"salary\" > 1000 group by \"name\", \"empid\", \"deptno\""), ("select \"empid\" from \"emps\"\n" + "where \"salary\" > 2000 group by \"name\", \"empid\""), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs1() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", \"deptno\""), "select \"deptno\" from \"emps\" group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs2() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", \"deptno\""), ("select \"deptno\", count(*) as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"deptno\""), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}], C=[$SUM0($2)], S=[$SUM0($3)])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs3() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", \"deptno\""), ("select \"deptno\", \"empid\", sum(\"empid\") as s, count(*) as c\n" + "from \"emps\" group by \"empid\", \"deptno\""), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t1], empid=[$t0], " + ("S=[$t3], C=[$t2])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs4() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n" + "from \"emps\" where \"deptno\" >= 10 group by \"empid\", \"deptno\""), ("select \"deptno\", sum(\"empid\") as s\n" + "from \"emps\" where \"deptno\" > 10 group by \"deptno\""), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}], S=[$SUM0($3)])\n" + (("  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], " + "proj#0..3=[{exprs}], $condition=[$t5])\n") + "    EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs5() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" where \"deptno\" >= 10 group by \"empid\", \"deptno\""), ("select \"deptno\", sum(\"empid\") + 1 as s\n" + "from \"emps\" where \"deptno\" > 10 group by \"deptno\""), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)]," + ((((" deptno=[$t0], $f1=[$t3])\n" + "  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($3)])\n") + "    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], ") + "proj#0..3=[{exprs}], $condition=[$t5])\n") + "      EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs6() {
        checkNoMaterialize(("select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") + 2 as s\n" + "from \"emps\" where \"deptno\" >= 10 group by \"empid\", \"deptno\""), ("select \"deptno\", sum(\"empid\") + 1 as s\n" + "from \"emps\" where \"deptno\" > 10 group by \"deptno\""), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs7() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" where \"deptno\" >= 10 group by \"empid\", \"deptno\""), ("select \"deptno\" + 1, sum(\"empid\") + 1 as s\n" + "from \"emps\" where \"deptno\" > 10 group by \"deptno\""), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t0, $t2)], " + (((("expr#4=[+($t1, $t2)], $f0=[$t3], $f1=[$t4])\n" + "  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($3)])\n") + "    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], ") + "proj#0..3=[{exprs}], $condition=[$t5])\n") + "      EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs9() {
        checkMaterialize(("select \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month), count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month)"), ("select floor(cast(\'1997-01-20 12:34:56\' as timestamp) to year), sum(\"empid\") as s\n" + "from \"emps\" group by floor(cast(\'1997-01-20 12:34:56\' as timestamp) to year)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs10() {
        checkMaterialize(("select \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month), count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month)"), ("select floor(cast(\'1997-01-20 12:34:56\' as timestamp) to year), sum(\"empid\") + 1 as s\n" + "from \"emps\" group by floor(cast(\'1997-01-20 12:34:56\' as timestamp) to year)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs11() {
        checkMaterialize(("select \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to second), count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to second)"), ("select floor(cast(\'1997-01-20 12:34:56\' as timestamp) to minute), sum(\"empid\") as s\n" + "from \"emps\" group by floor(cast(\'1997-01-20 12:34:56\' as timestamp) to minute)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs12() {
        checkMaterialize(("select \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to second), count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to second)"), ("select floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month), sum(\"empid\") as s\n" + "from \"emps\" group by floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs13() {
        checkMaterialize(("select \"empid\", cast(\'1997-01-20 12:34:56\' as timestamp), count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", cast(\'1997-01-20 12:34:56\' as timestamp)"), ("select floor(cast(\'1997-01-20 12:34:56\' as timestamp) to year), sum(\"empid\") as s\n" + "from \"emps\" group by floor(cast(\'1997-01-20 12:34:56\' as timestamp) to year)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs14() {
        checkMaterialize(("select \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month), count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", floor(cast(\'1997-01-20 12:34:56\' as timestamp) to month)"), ("select floor(cast(\'1997-01-20 12:34:56\' as timestamp) to hour), sum(\"empid\") as s\n" + "from \"emps\" group by floor(cast(\'1997-01-20 12:34:56\' as timestamp) to hour)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs15() {
        checkMaterialize(("select \"eventid\", floor(cast(\"ts\" as timestamp) to second), count(*) + 1 as c, sum(\"eventid\") as s\n" + "from \"events\" group by \"eventid\", floor(cast(\"ts\" as timestamp) to second)"), ("select floor(cast(\"ts\" as timestamp) to minute), sum(\"eventid\") as s\n" + "from \"events\" group by floor(cast(\"ts\" as timestamp) to minute)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs16() {
        checkMaterialize(("select \"eventid\", cast(\"ts\" as timestamp), count(*) + 1 as c, sum(\"eventid\") as s\n" + "from \"events\" group by \"eventid\", cast(\"ts\" as timestamp)"), ("select floor(cast(\"ts\" as timestamp) to year), sum(\"eventid\") as s\n" + "from \"events\" group by floor(cast(\"ts\" as timestamp) to year)"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs17() {
        checkMaterialize(("select \"eventid\", floor(cast(\"ts\" as timestamp) to month), count(*) + 1 as c, sum(\"eventid\") as s\n" + "from \"events\" group by \"eventid\", floor(cast(\"ts\" as timestamp) to month)"), ("select floor(cast(\"ts\" as timestamp) to hour), sum(\"eventid\") as s\n" + "from \"events\" group by floor(cast(\"ts\" as timestamp) to hour)"), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, events]])"));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs18() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", \"deptno\""), ("select \"empid\"*\"deptno\", sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\"*\"deptno\""));
    }

    @Test
    public void testAggregateMaterializationAggregateFuncs19() {
        checkMaterialize(("select \"empid\", \"deptno\", count(*) as c, sum(\"empid\") as s\n" + "from \"emps\" group by \"empid\", \"deptno\""), ("select \"empid\" + 10, count(*) + 1 as c\n" + "from \"emps\" group by \"empid\" + 10"));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs1() {
        checkMaterialize(("select \"empid\", \"depts\".\"deptno\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"depts\".\"deptno\" > 10\n" + "group by \"empid\", \"depts\".\"deptno\"")), ("select \"empid\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"depts\".\"deptno\" > 20\n" + "group by \"empid\", \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t1)], " + ("empid=[$t0], $condition=[$t3])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs2() {
        checkMaterialize(("select \"depts\".\"deptno\", \"empid\" from \"depts\"\n" + ("join \"emps\" using (\"deptno\") where \"depts\".\"deptno\" > 10\n" + "group by \"empid\", \"depts\".\"deptno\"")), ("select \"empid\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"depts\".\"deptno\" > 20\n" + "group by \"empid\", \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t0)], " + ("empid=[$t1], $condition=[$t3])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs3() {
        // It does not match, Project on top of query
        checkNoMaterialize(("select \"empid\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"depts\".\"deptno\" > 10\n" + "group by \"empid\", \"depts\".\"deptno\"")), ("select \"empid\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"depts\".\"deptno\" > 20\n" + "group by \"empid\", \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs4() {
        checkMaterialize(("select \"empid\", \"depts\".\"deptno\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"emps\".\"deptno\" > 10\n" + "group by \"empid\", \"depts\".\"deptno\"")), ("select \"empid\" from \"emps\"\n" + ("join \"depts\" using (\"deptno\") where \"depts\".\"deptno\" > 20\n" + "group by \"empid\", \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[20], expr#3=[<($t2, $t1)], " + ("empid=[$t0], $condition=[$t3])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs5() {
        checkMaterialize(("select \"depts\".\"deptno\", \"emps\".\"empid\" from \"depts\"\n" + ("join \"emps\" using (\"deptno\") where \"emps\".\"empid\" > 10\n" + "group by \"depts\".\"deptno\", \"emps\".\"empid\"")), ("select \"depts\".\"deptno\" from \"depts\"\n" + ("join \"emps\" using (\"deptno\") where \"emps\".\"empid\" > 15\n" + "group by \"depts\".\"deptno\", \"emps\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[15], expr#3=[<($t2, $t1)], " + ("deptno=[$t0], $condition=[$t3])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs6() {
        checkMaterialize(("select \"depts\".\"deptno\", \"emps\".\"empid\" from \"depts\"\n" + ("join \"emps\" using (\"deptno\") where \"emps\".\"empid\" > 10\n" + "group by \"depts\".\"deptno\", \"emps\".\"empid\"")), ("select \"depts\".\"deptno\" from \"depts\"\n" + ("join \"emps\" using (\"deptno\") where \"emps\".\"empid\" > 15\n" + "group by \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{0}])\n" + (("  EnumerableCalc(expr#0..1=[{inputs}], expr#2=[15], expr#3=[<($t2, $t1)], " + "proj#0..1=[{exprs}], $condition=[$t3])\n") + "    EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs7() {
        checkMaterialize(("select \"depts\".\"deptno\", \"dependents\".\"empid\"\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 11\n") + "group by \"depts\".\"deptno\", \"dependents\".\"empid\"")), ("select \"dependents\".\"empid\"\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableAggregate(group=[{0}])", "EnumerableUnion(all=[true])", "EnumerableAggregate(group=[{2}])", "EnumerableTableScan(table=[[hr, m0]])", "expr#5=[10], expr#6=[>($t0, $t5)], expr#7=[11], expr#8=[>=($t7, $t0)]"));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs8() {
        checkNoMaterialize(("select \"depts\".\"deptno\", \"dependents\".\"empid\"\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 20\n") + "group by \"depts\".\"deptno\", \"dependents\".\"empid\"")), ("select \"dependents\".\"empid\"\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10 and \"depts\".\"deptno\" < 20\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs9() {
        checkMaterialize(("select \"depts\".\"deptno\", \"dependents\".\"empid\"\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 11 and \"depts\".\"deptno\" < 19\n") + "group by \"depts\".\"deptno\", \"dependents\".\"empid\"")), ("select \"dependents\".\"empid\"\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10 and \"depts\".\"deptno\" < 20\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableAggregate(group=[{0}])", "EnumerableUnion(all=[true])", "EnumerableAggregate(group=[{2}])", "EnumerableTableScan(table=[[hr, m0]])", "expr#13=[OR($t10, $t12)], expr#14=[AND($t6, $t8, $t13)]"));
    }

    @Test
    public void testJoinAggregateMaterializationNoAggregateFuncs10() {
        checkMaterialize(("select \"depts\".\"name\", \"dependents\".\"name\" as \"name2\", " + (((((("\"emps\".\"deptno\", \"depts\".\"deptno\" as \"deptno2\", " + "\"dependents\".\"empid\"\n") + "from \"depts\", \"dependents\", \"emps\"\n") + "where \"depts\".\"deptno\" > 10\n") + "group by \"depts\".\"name\", \"dependents\".\"name\", ") + "\"emps\".\"deptno\", \"depts\".\"deptno\", ") + "\"dependents\".\"empid\"")), ("select \"dependents\".\"empid\"\n" + (((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{4}])\n" + (((("  EnumerableCalc(expr#0..4=[{inputs}], expr#5=[=($t2, $t3)], " + "expr#6=[CAST($t1):VARCHAR], ") + "expr#7=[CAST($t0):VARCHAR], ") + "expr#8=[=($t6, $t7)], expr#9=[AND($t5, $t8)], proj#0..4=[{exprs}], $condition=[$t9])\n") + "    EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs1() {
        // This test relies on FK-UK relationship
        checkMaterialize(("select \"empid\", \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "group by \"empid\", \"depts\".\"deptno\"")), "select \"deptno\" from \"emps\" group by \"deptno\"", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs2() {
        checkMaterialize(("select \"empid\", \"emps\".\"deptno\", count(*) as c, sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "group by \"empid\", \"emps\".\"deptno\"")), ("select \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "group by \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}], C=[$SUM0($2)], S=[$SUM0($3)])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs3() {
        // This test relies on FK-UK relationship
        checkMaterialize(("select \"empid\", \"depts\".\"deptno\", count(*) as c, sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "group by \"empid\", \"depts\".\"deptno\"")), ("select \"deptno\", \"empid\", sum(\"empid\") as s, count(*) as c\n" + "from \"emps\" group by \"empid\", \"deptno\""), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..3=[{inputs}], deptno=[$t1], empid=[$t0], " + ("S=[$t3], C=[$t2])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs4() {
        checkMaterialize(("select \"empid\", \"emps\".\"deptno\", count(*) as c, sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "where \"emps\".\"deptno\" >= 10 group by \"empid\", \"emps\".\"deptno\"")), ("select \"depts\".\"deptno\", sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "where \"emps\".\"deptno\" > 10 group by \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{1}], S=[$SUM0($3)])\n" + (("  EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], " + "proj#0..3=[{exprs}], $condition=[$t5])\n") + "    EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs5() {
        checkMaterialize(("select \"empid\", \"depts\".\"deptno\", count(*) + 1 as c, sum(\"empid\") as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "where \"depts\".\"deptno\" >= 10 group by \"empid\", \"depts\".\"deptno\"")), ("select \"depts\".\"deptno\", sum(\"empid\") + 1 as s\n" + ("from \"emps\" join \"depts\" using (\"deptno\")\n" + "where \"depts\".\"deptno\" > 10 group by \"depts\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], " + (((("deptno=[$t0], S=[$t3])\n" + "  EnumerableAggregate(group=[{1}], agg#0=[$SUM0($3)])\n") + "    EnumerableCalc(expr#0..3=[{inputs}], expr#4=[10], expr#5=[<($t4, $t1)], ") + "proj#0..3=[{exprs}], $condition=[$t5])\n") + "      EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs7() {
        checkMaterialize(("select \"dependents\".\"empid\", \"emps\".\"deptno\", sum(\"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), ("select \"dependents\".\"empid\", sum(\"salary\") as s\n" + ((("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{0}], S=[$SUM0($2)])\n" + (("  EnumerableJoin(condition=[=($1, $3)], joinType=[inner])\n" + "    EnumerableTableScan(table=[[hr, m0]])\n") + "    EnumerableTableScan(table=[[hr, depts]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs8() {
        checkMaterialize(("select \"dependents\".\"empid\", \"emps\".\"deptno\", sum(\"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), ("select \"depts\".\"name\", sum(\"salary\") as s\n" + ((("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"depts\".\"name\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{4}], S=[$SUM0($2)])\n" + (("  EnumerableJoin(condition=[=($1, $3)], joinType=[inner])\n" + "    EnumerableTableScan(table=[[hr, m0]])\n") + "    EnumerableTableScan(table=[[hr, depts]])"))));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs9() {
        checkMaterialize(("select \"dependents\".\"empid\", \"emps\".\"deptno\", count(distinct \"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), ("select \"emps\".\"deptno\", count(distinct \"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..2=[{inputs}], deptno=[$t1], S=[$t2])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs10() {
        checkNoMaterialize(("select \"dependents\".\"empid\", \"emps\".\"deptno\", count(distinct \"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), ("select \"emps\".\"deptno\", count(distinct \"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"emps\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs11() {
        checkMaterialize(("select \"depts\".\"deptno\", \"dependents\".\"empid\", count(\"emps\".\"salary\") as s\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 11 and \"depts\".\"deptno\" < 19\n") + "group by \"depts\".\"deptno\", \"dependents\".\"empid\"")), ("select \"dependents\".\"empid\", count(\"emps\".\"salary\") + 1\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10 and \"depts\".\"deptno\" < 20\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("PLAN=EnumerableCalc(expr#0..1=[{inputs}], expr#2=[1], expr#3=[+($t1, $t2)], " + ("empid=[$t0], EXPR$1=[$t3])\n" + "  EnumerableAggregate(group=[{0}], agg#0=[$SUM0($1)])")), "EnumerableUnion(all=[true])", "EnumerableAggregate(group=[{2}], agg#0=[COUNT()])", "EnumerableAggregate(group=[{1}], agg#0=[$SUM0($2)])", "EnumerableTableScan(table=[[hr, m0]])", "expr#13=[OR($t10, $t12)], expr#14=[AND($t6, $t8, $t13)]"));
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs12() {
        checkNoMaterialize(("select \"depts\".\"deptno\", \"dependents\".\"empid\", count(distinct \"emps\".\"salary\") as s\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 11 and \"depts\".\"deptno\" < 19\n") + "group by \"depts\".\"deptno\", \"dependents\".\"empid\"")), ("select \"dependents\".\"empid\", count(distinct \"emps\".\"salary\") + 1\n" + ((((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10 and \"depts\".\"deptno\" < 20\n") + "group by \"dependents\".\"empid\"")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinAggregateMaterializationAggregateFuncs13() {
        checkNoMaterialize(("select \"dependents\".\"empid\", \"emps\".\"deptno\", count(distinct \"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), ("select \"emps\".\"deptno\", count(\"salary\") as s\n" + (("from \"emps\"\n" + "join \"dependents\" on (\"emps\".\"empid\" = \"dependents\".\"empid\")\n") + "group by \"dependents\".\"empid\", \"emps\".\"deptno\"")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinMaterialization4() {
        checkMaterialize(("select \"empid\" \"deptno\" from \"emps\"\n" + "join \"depts\" using (\"deptno\")"), ("select \"empid\" \"deptno\" from \"emps\"\n" + "join \"depts\" using (\"deptno\") where \"empid\" = 1"), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):INTEGER NOT NULL], expr#2=[1], " + ("expr#3=[=($t1, $t2)], deptno=[$t0], $condition=[$t3])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinMaterialization5() {
        checkMaterialize(("select cast(\"empid\" as BIGINT) from \"emps\"\n" + "join \"depts\" using (\"deptno\")"), ("select \"empid\" \"deptno\" from \"emps\"\n" + "join \"depts\" using (\"deptno\") where \"empid\" > 1"), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):JavaType(int) NOT NULL], " + ("expr#2=[1], expr#3=[>($t1, $t2)], EXPR$0=[$t1], $condition=[$t3])\n" + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinMaterialization6() {
        checkMaterialize(("select cast(\"empid\" as BIGINT) from \"emps\"\n" + "join \"depts\" using (\"deptno\")"), ("select \"empid\" \"deptno\" from \"emps\"\n" + "join \"depts\" using (\"deptno\") where \"empid\" = 1"), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):JavaType(int) NOT NULL], " + (("expr#2=[CAST($t1):INTEGER NOT NULL], expr#3=[1], expr#4=[=($t2, $t3)], " + "EXPR$0=[$t1], $condition=[$t4])\n") + "  EnumerableTableScan(table=[[hr, m0]])"))));
    }

    @Test
    public void testJoinMaterialization7() {
        checkMaterialize(("select \"depts\".\"name\"\n" + ("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), ("select \"dependents\".\"empid\"\n" + (("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..2=[{inputs}], empid=[$t1])\n" + (((("  EnumerableJoin(condition=[=($0, $2)], joinType=[inner])\n" + "    EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):VARCHAR], name=[$t1])\n") + "      EnumerableTableScan(table=[[hr, m0]])\n") + "    EnumerableCalc(expr#0..1=[{inputs}], expr#2=[CAST($t1):VARCHAR], empid=[$t0], name0=[$t2])\n") + "      EnumerableTableScan(table=[[hr, dependents]])"))));
    }

    @Test
    public void testJoinMaterialization8() {
        checkMaterialize(("select \"depts\".\"name\"\n" + ("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), ("select \"dependents\".\"empid\"\n" + (("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..4=[{inputs}], empid=[$t2])\n" + (((("  EnumerableJoin(condition=[=($1, $4)], joinType=[inner])\n" + "    EnumerableCalc(expr#0=[{inputs}], expr#1=[CAST($t0):VARCHAR], proj#0..1=[{exprs}])\n") + "      EnumerableTableScan(table=[[hr, m0]])\n") + "    EnumerableCalc(expr#0..1=[{inputs}], expr#2=[CAST($t1):VARCHAR], proj#0..2=[{exprs}])\n") + "      EnumerableTableScan(table=[[hr, dependents]])"))));
    }

    @Test
    public void testJoinMaterialization9() {
        checkMaterialize(("select \"depts\".\"name\"\n" + ("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), ("select \"dependents\".\"empid\"\n" + ((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"locations\" on (\"locations\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), MaterializationTest.HR_FKUK_MODEL, MaterializationTest.CONTAINS_M0);
    }

    @Test
    public void testJoinMaterialization10() {
        checkMaterialize(("select \"depts\".\"deptno\", \"dependents\".\"empid\"\n" + ((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 30")), ("select \"dependents\".\"empid\"\n" + ((("from \"depts\"\n" + "join \"dependents\" on (\"depts\".\"name\" = \"dependents\".\"name\")\n") + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")\n") + "where \"depts\".\"deptno\" > 10")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableUnion(all=[true])", "EnumerableTableScan(table=[[hr, m0]])", "expr#5=[10], expr#6=[>($t0, $t5)], expr#7=[30], expr#8=[>=($t7, $t0)]"));
    }

    @Test
    public void testJoinMaterialization11() {
        checkNoMaterialize(("select \"empid\" from \"emps\"\n" + "join \"depts\" using (\"deptno\")"), ("select \"empid\" from \"emps\"\n" + "where \"deptno\" in (select \"deptno\" from \"depts\")"), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinMaterializationUKFK1() {
        checkMaterialize(("select \"a\".\"empid\" \"deptno\" from\n" + (("(select * from \"emps\" where \"empid\" = 1) \"a\"\n" + "join \"depts\" using (\"deptno\")\n") + "join \"dependents\" using (\"empid\")")), ("select \"a\".\"empid\" from \n" + ("(select * from \"emps\" where \"empid\" = 1) \"a\"\n" + "join \"dependents\" using (\"empid\")\n")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("PLAN=EnumerableTableScan(table=[[hr, m0]])"));
    }

    @Test
    public void testJoinMaterializationUKFK2() {
        checkMaterialize(("select \"a\".\"empid\", \"a\".\"deptno\" from\n" + (("(select * from \"emps\" where \"empid\" = 1) \"a\"\n" + "join \"depts\" using (\"deptno\")\n") + "join \"dependents\" using (\"empid\")")), ("select \"a\".\"empid\" from \n" + ("(select * from \"emps\" where \"empid\" = 1) \"a\"\n" + "join \"dependents\" using (\"empid\")\n")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], empid=[$t0])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testJoinMaterializationUKFK3() {
        checkNoMaterialize(("select \"a\".\"empid\", \"a\".\"deptno\" from\n" + (("(select * from \"emps\" where \"empid\" = 1) \"a\"\n" + "join \"depts\" using (\"deptno\")\n") + "join \"dependents\" using (\"empid\")")), ("select \"a\".\"name\" from \n" + ("(select * from \"emps\" where \"empid\" = 1) \"a\"\n" + "join \"dependents\" using (\"empid\")\n")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinMaterializationUKFK4() {
        checkMaterialize(("select \"empid\" \"deptno\" from\n" + ("(select * from \"emps\" where \"empid\" = 1)\n" + "join \"depts\" using (\"deptno\")")), "select \"empid\" from \"emps\" where \"empid\" = 1\n", MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("PLAN=EnumerableTableScan(table=[[hr, m0]])"));
    }

    @Test
    public void testJoinMaterializationUKFK5() {
        checkMaterialize(("select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n" + (("join \"depts\" using (\"deptno\")\n" + "join \"dependents\" using (\"empid\")") + "where \"emps\".\"empid\" = 1")), ("select \"emps\".\"empid\" from \"emps\"\n" + ("join \"dependents\" using (\"empid\")\n" + "where \"emps\".\"empid\" = 1")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], empid0=[$t0])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testJoinMaterializationUKFK6() {
        checkMaterialize(("select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n" + ((("join \"depts\" \"a\" on (\"emps\".\"deptno\"=\"a\".\"deptno\")\n" + "join \"depts\" \"b\" on (\"emps\".\"deptno\"=\"b\".\"deptno\")\n") + "join \"dependents\" using (\"empid\")") + "where \"emps\".\"empid\" = 1")), ("select \"emps\".\"empid\" from \"emps\"\n" + ("join \"dependents\" using (\"empid\")\n" + "where \"emps\".\"empid\" = 1")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableCalc(expr#0..1=[{inputs}], empid0=[$t0])\n" + "  EnumerableTableScan(table=[[hr, m0]])")));
    }

    @Test
    public void testJoinMaterializationUKFK7() {
        checkNoMaterialize(("select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n" + ((("join \"depts\" \"a\" on (\"emps\".\"name\"=\"a\".\"name\")\n" + "join \"depts\" \"b\" on (\"emps\".\"name\"=\"b\".\"name\")\n") + "join \"dependents\" using (\"empid\")") + "where \"emps\".\"empid\" = 1")), ("select \"emps\".\"empid\" from \"emps\"\n" + ("join \"dependents\" using (\"empid\")\n" + "where \"emps\".\"empid\" = 1")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinMaterializationUKFK8() {
        checkNoMaterialize(("select \"emps\".\"empid\", \"emps\".\"deptno\" from \"emps\"\n" + ((("join \"depts\" \"a\" on (\"emps\".\"deptno\"=\"a\".\"deptno\")\n" + "join \"depts\" \"b\" on (\"emps\".\"name\"=\"b\".\"name\")\n") + "join \"dependents\" using (\"empid\")") + "where \"emps\".\"empid\" = 1")), ("select \"emps\".\"empid\" from \"emps\"\n" + ("join \"dependents\" using (\"empid\")\n" + "where \"emps\".\"empid\" = 1")), MaterializationTest.HR_FKUK_MODEL);
    }

    @Test
    public void testJoinMaterializationUKFK9() {
        checkMaterialize(("select * from \"emps\"\n" + "join \"dependents\" using (\"empid\")"), ("select \"emps\".\"empid\", \"dependents\".\"empid\", \"emps\".\"deptno\"\n" + ((("from \"emps\"\n" + "join \"dependents\" using (\"empid\")") + "join \"depts\" \"a\" on (\"emps\".\"deptno\"=\"a\".\"deptno\")\n") + "where \"emps\".\"name\" = \'Bill\'")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, m0]])"));
    }

    @Test
    public void testViewMaterialization() {
        checkThatMaterialize(("select \"depts\".\"name\"\n" + ("from \"emps\"\n" + "join \"depts\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), ("select \"depts\".\"name\"\n" + ("from \"depts\"\n" + "join \"emps\" on (\"emps\".\"deptno\" = \"depts\".\"deptno\")")), "matview", true, MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableValues(tuples=[[{ 'noname' }]])"), RuleSets.ofList(ImmutableList.of())).returnsValue("noname");
    }

    @Test
    public void testSubQuery() {
        String q = "select \"empid\", \"deptno\", \"salary\" from \"emps\" e1\n" + (("where \"empid\" = (\n" + "  select max(\"empid\") from \"emps\"\n") + "  where \"deptno\" = e1.\"deptno\")");
        final String m = "select \"empid\", \"deptno\" from \"emps\"\n";
        checkMaterialize(m, q, MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, m0]])", 1));
    }

    @Test
    public void testTableModify() {
        final String m = "select \"deptno\", \"empid\", \"name\"" + "from \"emps\" where \"deptno\" = 10";
        final String q = "upsert into \"dependents\"" + ("select \"empid\" + 1 as x, \"name\"" + "from \"emps\" where \"deptno\" = 10");
        final List<List<List<String>>> substitutedNames = new ArrayList<>();
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", m).query(q).withHook(SUB, ((Consumer<RelNode>) (( r) -> substitutedNames.add(new MaterializationTest.TableNameVisitor().run(r))))).enableMaterializations(true).explainContains("hr, m0");
        } catch (Exception e) {
            // Table "dependents" not modifiable.
        }
        Assert.assertThat(substitutedNames, CoreMatchers.is(MaterializationTest.list3(new String[][][]{ new String[][]{ new String[]{ "hr", "m0" } } })));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-761">[CALCITE-761]
     * Pre-populated materializations</a>.
     */
    @Test
    public void testPrePopulated() {
        String q = "select \"deptno\" from \"emps\"";
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, ( builder) -> {
                final Map<String, Object> map = builder.map();
                map.put("table", "locations");
                String sql = "select `deptno` as `empid`, \'\' as `name`\n" + "from `emps`";
                final String sql2 = sql.replaceAll("`", "\"");
                map.put("sql", sql2);
                return ImmutableList.of(map);
            }).query(q).enableMaterializations(true).explainMatches("", MaterializationTest.CONTAINS_LOCATIONS).sameResultWithMaterializationsDisabled();
        }
    }

    @Test
    public void testViewSchemaPath() {
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            final String m = "select empno, deptno from emp";
            final String q = "select deptno from scott.emp";
            final List<String> path = ImmutableList.of("SCOTT");
            final JsonBuilder builder = new JsonBuilder();
            final String model = ((((((((((((((((((((((("{\n" + (("  version: \'1.0\',\n" + "  defaultSchema: \'hr\',\n") + "  schemas: [\n")) + (JdbcTest.SCOTT_SCHEMA)) + "  ,\n") + "    {\n") + "      materializations: [\n") + "        {\n") + "          table: \'m0\',\n") + "          view: \'m0v\',\n") + "          sql: ") + (builder.toJsonString(m))) + ",\n") + "          viewSchemaPath: ") + (builder.toJsonString(path))) + "        }\n") + "      ],\n") + "      type: \'custom\',\n") + "      name: \'hr\',\n") + "      factory: \'org.apache.calcite.adapter.java.ReflectiveSchema$Factory\',\n") + "      operand: {\n") + "        class: \'org.apache.calcite.test.JdbcTest$HrSchema\'\n") + "      }\n") + "    }\n") + "  ]\n") + "}\n";
            CalciteAssert.that().withModel(model).query(q).enableMaterializations(true).explainMatches("", MaterializationTest.CONTAINS_M0).sameResultWithMaterializationsDisabled();
        }
    }

    @Test
    public void testSingleMaterializationMultiUsage() {
        String q = "select *\n" + ("from (select * from \"emps\" where \"empid\" < 300)\n" + "join (select * from \"emps\" where \"empid\" < 200) using (\"empid\")");
        String m = "select * from \"emps\" where \"empid\" < 500";
        checkMaterialize(m, q, MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains("EnumerableTableScan(table=[[hr, m0]])", 2));
    }

    @Test
    public void testMultiMaterializationMultiUsage() {
        String q = "select *\n" + ("from (select * from \"emps\" where \"empid\" < 300)\n" + "join (select \"deptno\", count(*) as c from \"emps\" group by \"deptno\") using (\"deptno\")");
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", "select \"deptno\", count(*) as c, sum(\"empid\") as s from \"emps\" group by \"deptno\"", "m1", "select * from \"emps\" where \"empid\" < 500").query(q).enableMaterializations(true).explainContains("EnumerableTableScan(table=[[hr, m0]])").explainContains("EnumerableTableScan(table=[[hr, m1]])").sameResultWithMaterializationsDisabled();
        }
    }

    @Test
    public void testMaterializationOnJoinQuery() {
        final String q = "select *\n" + ("from \"emps\"\n" + "join \"depts\" using (\"deptno\") where \"empid\" < 300 ");
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", "select * from \"emps\" where \"empid\" < 500").query(q).enableMaterializations(true).explainContains("EnumerableTableScan(table=[[hr, m0]])").sameResultWithMaterializationsDisabled();
        }
    }

    @Test
    public void testAggregateMaterializationOnCountDistinctQuery1() {
        // The column empid is already unique, thus DISTINCT is not
        // in the COUNT of the resulting rewriting
        checkMaterialize(("select \"deptno\", \"empid\", \"salary\"\n" + ("from \"emps\"\n" + "group by \"deptno\", \"empid\", \"salary\"")), ("select \"deptno\", count(distinct \"empid\") as c from (\n" + ((("select \"deptno\", \"empid\"\n" + "from \"emps\"\n") + "group by \"deptno\", \"empid\")\n") + "group by \"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{0}], C=[COUNT($1)])\n" + "  EnumerableTableScan(table=[[hr, m0]]")));
    }

    @Test
    public void testAggregateMaterializationOnCountDistinctQuery2() {
        // The column empid is already unique, thus DISTINCT is not
        // in the COUNT of the resulting rewriting
        checkMaterialize(("select \"deptno\", \"salary\", \"empid\"\n" + ("from \"emps\"\n" + "group by \"deptno\", \"salary\", \"empid\"")), ("select \"deptno\", count(distinct \"empid\") as c from (\n" + ((("select \"deptno\", \"empid\"\n" + "from \"emps\"\n") + "group by \"deptno\", \"empid\")\n") + "group by \"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{0}], C=[COUNT($2)])\n" + "  EnumerableTableScan(table=[[hr, m0]]")));
    }

    @Test
    public void testAggregateMaterializationOnCountDistinctQuery3() {
        // The column salary is not unique, thus we end up with
        // a different rewriting
        checkMaterialize(("select \"deptno\", \"empid\", \"salary\"\n" + ("from \"emps\"\n" + "group by \"deptno\", \"empid\", \"salary\"")), ("select \"deptno\", count(distinct \"salary\") from (\n" + ((("select \"deptno\", \"salary\"\n" + "from \"emps\"\n") + "group by \"deptno\", \"salary\")\n") + "group by \"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{0}], EXPR$1=[COUNT($1)])\n" + ("  EnumerableAggregate(group=[{0, 2}])\n" + "    EnumerableTableScan(table=[[hr, m0]]"))));
    }

    @Test
    public void testAggregateMaterializationOnCountDistinctQuery4() {
        // Although there is no DISTINCT in the COUNT, this is
        // equivalent to previous query
        checkMaterialize(("select \"deptno\", \"salary\", \"empid\"\n" + ("from \"emps\"\n" + "group by \"deptno\", \"salary\", \"empid\"")), ("select \"deptno\", count(\"salary\") from (\n" + ((("select \"deptno\", \"salary\"\n" + "from \"emps\"\n") + "group by \"deptno\", \"salary\")\n") + "group by \"deptno\"")), MaterializationTest.HR_FKUK_MODEL, CalciteAssert.checkResultContains(("EnumerableAggregate(group=[{0}], EXPR$1=[COUNT()])\n" + ("  EnumerableAggregate(group=[{0, 1}])\n" + "    EnumerableTableScan(table=[[hr, m0]]"))));
    }

    @Test
    public void testMaterializationSubstitution() {
        String q = "select *\n" + ("from (select * from \"emps\" where \"empid\" < 300)\n" + "join (select * from \"emps\" where \"empid\" < 200) using (\"empid\")");
        final String[][][] expectedNames = new String[][][]{ new String[][]{ new String[]{ "hr", "emps" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "emps" }, new String[]{ "hr", "m1" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "emps" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "m1" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "emps" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "m1" } } };
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            final List<List<List<String>>> substitutedNames = new ArrayList<>();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", "select * from \"emps\" where \"empid\" < 300", "m1", "select * from \"emps\" where \"empid\" < 600").query(q).withHook(SUB, ((Consumer<RelNode>) (( r) -> substitutedNames.add(new MaterializationTest.TableNameVisitor().run(r))))).enableMaterializations(true).sameResultWithMaterializationsDisabled();
            substitutedNames.sort(MaterializationTest.CASE_INSENSITIVE_LIST_LIST_COMPARATOR);
            Assert.assertThat(substitutedNames, CoreMatchers.is(MaterializationTest.list3(expectedNames)));
        }
    }

    @Test
    public void testMaterializationSubstitution2() {
        String q = "select *\n" + ("from (select * from \"emps\" where \"empid\" < 300)\n" + "join (select * from \"emps\" where \"empid\" < 200) using (\"empid\")");
        final String[][][] expectedNames = new String[][][]{ new String[][]{ new String[]{ "hr", "emps" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "emps" }, new String[]{ "hr", "m1" } }, new String[][]{ new String[]{ "hr", "emps" }, new String[]{ "hr", "m2" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "emps" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "m1" } }, new String[][]{ new String[]{ "hr", "m0" }, new String[]{ "hr", "m2" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "emps" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "m1" } }, new String[][]{ new String[]{ "hr", "m1" }, new String[]{ "hr", "m2" } }, new String[][]{ new String[]{ "hr", "m2" }, new String[]{ "hr", "emps" } }, new String[][]{ new String[]{ "hr", "m2" }, new String[]{ "hr", "m0" } }, new String[][]{ new String[]{ "hr", "m2" }, new String[]{ "hr", "m1" } }, new String[][]{ new String[]{ "hr", "m2" }, new String[]{ "hr", "m2" } } };
        try (TryThreadLocal.Memo ignored = THREAD_TRIM.push(true)) {
            MaterializationService.setThreadLocal();
            final List<List<List<String>>> substitutedNames = new ArrayList<>();
            CalciteAssert.that().withMaterializations(MaterializationTest.HR_FKUK_MODEL, "m0", "select * from \"emps\" where \"empid\" < 300", "m1", "select * from \"emps\" where \"empid\" < 600", "m2", "select * from \"m1\"").query(q).withHook(SUB, ((Consumer<RelNode>) (( r) -> substitutedNames.add(new MaterializationTest.TableNameVisitor().run(r))))).enableMaterializations(true).sameResultWithMaterializationsDisabled();
            substitutedNames.sort(MaterializationTest.CASE_INSENSITIVE_LIST_LIST_COMPARATOR);
            Assert.assertThat(substitutedNames, CoreMatchers.is(MaterializationTest.list3(expectedNames)));
        }
    }

    /**
     * Implementation of RelVisitor to extract substituted table names.
     */
    private static class TableNameVisitor extends RelVisitor {
        private List<List<String>> names = new ArrayList<>();

        List<List<String>> run(RelNode input) {
            go(input);
            return names;
        }

        @Override
        public void visit(RelNode node, int ordinal, RelNode parent) {
            if (node instanceof TableScan) {
                RelOptTable table = node.getTable();
                List<String> qName = table.getQualifiedName();
                names.add(qName);
            }
            super.visit(node, ordinal, parent);
        }
    }

    /**
     * Hr schema with FK-UK relationship.
     */
    public static class HrFKUKSchema {
        @Override
        public String toString() {
            return "HrFKUKSchema";
        }

        public final JdbcTest.Employee[] emps = new JdbcTest.Employee[]{ new JdbcTest.Employee(100, 10, "Bill", 10000, 1000), new JdbcTest.Employee(200, 20, "Eric", 8000, 500), new JdbcTest.Employee(150, 10, "Sebastian", 7000, null), new JdbcTest.Employee(110, 10, "Theodore", 10000, 250) };

        public final JdbcTest.Department[] depts = new JdbcTest.Department[]{ new JdbcTest.Department(10, "Sales", Arrays.asList(emps[0], emps[2], emps[3]), new JdbcTest.Location((-122), 38)), new JdbcTest.Department(30, "Marketing", ImmutableList.of(), new JdbcTest.Location(0, 52)), new JdbcTest.Department(20, "HR", Collections.singletonList(emps[1]), null) };

        public final JdbcTest.Dependent[] dependents = new JdbcTest.Dependent[]{ new JdbcTest.Dependent(10, "Michael"), new JdbcTest.Dependent(10, "Jane") };

        public final JdbcTest.Dependent[] locations = new JdbcTest.Dependent[]{ new JdbcTest.Dependent(10, "San Francisco"), new JdbcTest.Dependent(20, "San Diego") };

        public final JdbcTest.Event[] events = new JdbcTest.Event[]{ new JdbcTest.Event(100, new Timestamp(0)), new JdbcTest.Event(200, new Timestamp(0)), new JdbcTest.Event(150, new Timestamp(0)), new JdbcTest.Event(110, null) };

        public final RelReferentialConstraint rcs0 = RelReferentialConstraintImpl.of(ImmutableList.of("hr", "emps"), ImmutableList.of("hr", "depts"), ImmutableList.of(IntPair.of(1, 0)));

        public QueryableTable foo(int count) {
            return Smalls.generateStrings(count);
        }

        public TranslatableTable view(String s) {
            return Smalls.view(s);
        }

        public TranslatableTable matview() {
            return Smalls.strView("noname");
        }
    }
}

/**
 * End MaterializationTest.java
 */
