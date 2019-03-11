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
package org.apache.calcite.materialize;


import Frameworks.ConfigBuilder;
import Lattice.DerivedColumn;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.test.CalciteAssert;
import org.apache.calcite.test.FoodMartQuerySet;
import org.apache.calcite.test.SlowTests;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.apache.calcite.util.Util;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.apache.calcite.test.CalciteAssert.SchemaSpec.JDBC_FOODMART;
import static org.apache.calcite.test.CalciteAssert.SchemaSpec.SCOTT;


/**
 * Unit tests for {@link LatticeSuggester}.
 */
@Category(SlowTests.class)
public class LatticeSuggesterTest {
    /**
     * Some basic query patterns on the Scott schema with "EMP" and "DEPT"
     * tables.
     */
    @Test
    public void testEmpDept() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester();
        final String q0 = "select dept.dname, count(*), sum(sal)\n" + (("from emp\n" + "join dept using (deptno)\n") + "group by dept.dname");
        Assert.assertThat(t.addQuery(q0), isGraphs("EMP (DEPT:DEPTNO)", "[COUNT(), SUM(EMP.SAL)]"));
        // Same as above, but using WHERE rather than JOIN
        final String q1 = "select dept.dname, count(*), sum(sal)\n" + (("from emp, dept\n" + "where emp.deptno = dept.deptno\n") + "group by dept.dname");
        Assert.assertThat(t.addQuery(q1), isGraphs("EMP (DEPT:DEPTNO)", "[COUNT(), SUM(EMP.SAL)]"));
        // With HAVING
        final String q2 = "select dept.dname\n" + ((("from emp, dept\n" + "where emp.deptno = dept.deptno\n") + "group by dept.dname\n") + "having count(*) > 10");
        Assert.assertThat(t.addQuery(q2), isGraphs("EMP (DEPT:DEPTNO)", "[COUNT()]"));
        // No joins, therefore graph has a single node and no edges
        final String q3 = "select distinct dname\n" + "from dept";
        Assert.assertThat(t.addQuery(q3), isGraphs("DEPT", "[]"));
        // Graph is empty because there are no tables
        final String q4 = "select distinct t.c\n" + ("from (values 1, 2) as t(c)" + "join (values 2, 3) as u(c) using (c)\n");
        Assert.assertThat(t.addQuery(q4), isGraphs());
        // Self-join
        final String q5 = "select *\n" + ("from emp as e\n" + "join emp as m on e.mgr = m.empno");
        Assert.assertThat(t.addQuery(q5), isGraphs("EMP (EMP:MGR)", "[]"));
        // Self-join, twice
        final String q6 = "select *\n" + ("from emp as e join emp as m on e.mgr = m.empno\n" + "join emp as m2 on m.mgr = m2.empno");
        Assert.assertThat(t.addQuery(q6), isGraphs("EMP (EMP:MGR (EMP:MGR))", "[]"));
        // No graphs, because cyclic: e -> m, m -> m2, m2 -> e
        final String q7 = "select *\n" + ((("from emp as e\n" + "join emp as m on e.mgr = m.empno\n") + "join emp as m2 on m.mgr = m2.empno\n") + "where m2.mgr = e.empno");
        Assert.assertThat(t.addQuery(q7), isGraphs());
        // The graph of all tables and hops
        final String expected = "graph(" + ((("vertices: [[scott, DEPT]," + " [scott, EMP]], ") + "edges: [Step([scott, EMP], [scott, DEPT], DEPTNO:DEPTNO),") + " Step([scott, EMP], [scott, EMP], MGR:EMPNO)])");
        Assert.assertThat(t.s.space.g.toString(), CoreMatchers.is(expected));
    }

    @Test
    public void testFoodmart() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart();
        final String q = "select \"t\".\"the_year\" as \"c0\",\n" + (((((((((((((" \"t\".\"quarter\" as \"c1\",\n" + " \"pc\".\"product_family\" as \"c2\",\n") + " sum(\"s\".\"unit_sales\") as \"m0\"\n") + "from \"time_by_day\" as \"t\",\n") + " \"sales_fact_1997\" as \"s\",\n") + " \"product_class\" as \"pc\",\n") + " \"product\" as \"p\"\n") + "where \"s\".\"time_id\" = \"t\".\"time_id\"\n") + "and \"t\".\"the_year\" = 1997\n") + "and \"s\".\"product_id\" = \"p\".\"product_id\"\n") + "and \"p\".\"product_class_id\" = \"pc\".\"product_class_id\"\n") + "group by \"t\".\"the_year\",\n") + " \"t\".\"quarter\",\n") + " \"pc\".\"product_family\"");
        final String g = "sales_fact_1997" + (" (product:product_id (product_class:product_class_id)" + " time_by_day:time_id)");
        Assert.assertThat(t.addQuery(q), isGraphs(g, "[SUM(sales_fact_1997.unit_sales)]"));
        // The graph of all tables and hops
        final String expected = "graph(" + ((((((((((("vertices: [" + "[foodmart, product], ") + "[foodmart, product_class], ") + "[foodmart, sales_fact_1997], ") + "[foodmart, time_by_day]], ") + "edges: [") + "Step([foodmart, product], [foodmart, product_class],") + " product_class_id:product_class_id), ") + "Step([foodmart, sales_fact_1997], [foodmart, product],") + " product_id:product_id), ") + "Step([foodmart, sales_fact_1997], [foodmart, time_by_day],") + " time_id:time_id)])");
        Assert.assertThat(t.s.space.g.toString(), CoreMatchers.is(expected));
    }

    @Test
    public void testAggregateExpression() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart();
        final String q = "select \"t\".\"the_year\" as \"c0\",\n" + (((((((((((((" \"pc\".\"product_family\" as \"c1\",\n" + " sum((case when \"s\".\"promotion_id\" = 0\n") + "     then 0 else \"s\".\"store_sales\"\n") + "     end)) as \"sum_m0\"\n") + "from \"time_by_day\" as \"t\",\n") + " \"sales_fact_1997\" as \"s\",\n") + " \"product_class\" as \"pc\",\n") + " \"product\" as \"p\"\n") + "where \"s\".\"time_id\" = \"t\".\"time_id\"\n") + " and \"t\".\"the_year\" = 1997\n") + " and \"s\".\"product_id\" = \"p\".\"product_id\"\n") + " and \"p\".\"product_class_id\" = \"pc\".\"product_class_id\"\n") + "group by \"t\".\"the_year\",\n") + " \"pc\".\"product_family\"\n");
        final String g = "sales_fact_1997" + (" (product:product_id (product_class:product_class_id)" + " time_by_day:time_id)");
        final String expected = "[SUM(m0)]";
        Assert.assertThat(t.addQuery(q), CoreMatchers.allOf(isGraphs(g, expected), hasMeasureNames(0, "sum_m0"), hasDerivedColumnNames(0, "m0")));
    }

    @Test
    public void testSharedSnowflake() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart();
        // foodmart query 5827 (also 5828, 5830, 5832) uses the "region" table
        // twice: once via "store" and once via "customer";
        // TODO: test what happens if FK from "store" to "region" is reversed
        final String q = "select \"s\".\"store_country\" as \"c0\",\n" + (((((((((((((((((" \"r\".\"sales_region\" as \"c1\",\n" + " \"r1\".\"sales_region\" as \"c2\",\n") + " sum(\"f\".\"unit_sales\") as \"m0\"\n") + "from \"store\" as \"s\",\n") + " \"sales_fact_1997\" as \"f\",\n") + " \"region\" as \"r\",\n") + " \"region\" as \"r1\",\n") + " \"customer\" as \"c\"\n") + "where \"f\".\"store_id\" = \"s\".\"store_id\"\n") + " and \"s\".\"store_country\" = \'USA\'\n") + " and \"s\".\"region_id\" = \"r\".\"region_id\"\n") + " and \"r\".\"sales_region\" = \'South West\'\n") + " and \"f\".\"customer_id\" = \"c\".\"customer_id\"\n") + " and \"c\".\"customer_region_id\" = \"r1\".\"region_id\"\n") + " and \"r1\".\"sales_region\" = \'South West\'\n") + "group by \"s\".\"store_country\",\n") + " \"r\".\"sales_region\",\n") + " \"r1\".\"sales_region\"\n");
        final String g = "sales_fact_1997" + (" (customer:customer_id (region:customer_region_id)" + " store:store_id (region:region_id))");
        Assert.assertThat(t.addQuery(q), isGraphs(g, "[SUM(sales_fact_1997.unit_sales)]"));
    }

    @Test
    public void testExpressionInAggregate() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().withEvolve(true).foodmart();
        final FoodMartQuerySet set = FoodMartQuerySet.instance();
        for (int id : new int[]{ 392, 393 }) {
            t.addQuery(set.queries.get(id).sql);
        }
    }

    @Test
    public void testFoodMartAll() throws Exception {
        checkFoodMartAll(false);
    }

    @Test
    public void testFoodMartAllEvolve() throws Exception {
        checkFoodMartAll(true);
    }

    @Test
    public void testContains() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart();
        final LatticeRootNode fNode = t.node(("select *\n" + "from \"sales_fact_1997\""));
        final LatticeRootNode fcNode = t.node(("select *\n" + ("from \"sales_fact_1997\"\n" + "join \"customer\" using (\"customer_id\")")));
        final LatticeRootNode fcpNode = t.node(("select *\n" + (("from \"sales_fact_1997\"\n" + "join \"customer\" using (\"customer_id\")\n") + "join \"product\" using (\"product_id\")")));
        Assert.assertThat(fNode.contains(fNode), CoreMatchers.is(true));
        Assert.assertThat(fNode.contains(fcNode), CoreMatchers.is(false));
        Assert.assertThat(fNode.contains(fcpNode), CoreMatchers.is(false));
        Assert.assertThat(fcNode.contains(fNode), CoreMatchers.is(true));
        Assert.assertThat(fcNode.contains(fcNode), CoreMatchers.is(true));
        Assert.assertThat(fcNode.contains(fcpNode), CoreMatchers.is(false));
        Assert.assertThat(fcpNode.contains(fNode), CoreMatchers.is(true));
        Assert.assertThat(fcpNode.contains(fcNode), CoreMatchers.is(true));
        Assert.assertThat(fcpNode.contains(fcpNode), CoreMatchers.is(true));
    }

    @Test
    public void testEvolve() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart().withEvolve(true);
        final String q0 = "select count(*)\n" + "from \"sales_fact_1997\"";
        final String l0 = "sales_fact_1997:[COUNT()]";
        t.addQuery(q0);
        Assert.assertThat(t.s.latticeMap.size(), CoreMatchers.is(1));
        Assert.assertThat(Iterables.getOnlyElement(t.s.latticeMap.keySet()), CoreMatchers.is(l0));
        final String q1 = "select sum(\"unit_sales\")\n" + (("from \"sales_fact_1997\"\n" + "join \"customer\" using (\"customer_id\")\n") + "group by \"customer\".\"city\"");
        final String l1 = "sales_fact_1997 (customer:customer_id)" + ":[COUNT(), SUM(sales_fact_1997.unit_sales)]";
        t.addQuery(q1);
        Assert.assertThat(t.s.latticeMap.size(), CoreMatchers.is(1));
        Assert.assertThat(Iterables.getOnlyElement(t.s.latticeMap.keySet()), CoreMatchers.is(l1));
        final String q2 = "select count(distinct \"the_day\")\n" + (("from \"sales_fact_1997\"\n" + "join \"time_by_day\" using (\"time_id\")\n") + "join \"product\" using (\"product_id\")");
        final String l2 = "sales_fact_1997" + ((" (customer:customer_id product:product_id time_by_day:time_id)" + ":[COUNT(), SUM(sales_fact_1997.unit_sales),") + " COUNT(DISTINCT time_by_day.the_day)]");
        t.addQuery(q2);
        Assert.assertThat(t.s.latticeMap.size(), CoreMatchers.is(1));
        Assert.assertThat(Iterables.getOnlyElement(t.s.latticeMap.keySet()), CoreMatchers.is(l2));
        final Lattice lattice = Iterables.getOnlyElement(t.s.latticeMap.values());
        final List<List<String>> tableNames = lattice.tables().stream().map(( table) -> table.t.getQualifiedName()).sorted(Comparator.comparing(Object::toString)).collect(Util.toImmutableList());
        Assert.assertThat(tableNames.toString(), CoreMatchers.is(("[[foodmart, customer]," + ((" [foodmart, product]," + " [foodmart, sales_fact_1997],") + " [foodmart, time_by_day]]"))));
        final String q3 = "select min(\"product\".\"product_id\")\n" + ((("from \"sales_fact_1997\"\n" + "join \"product\" using (\"product_id\")\n") + "join \"product_class\" as pc using (\"product_class_id\")\n") + "group by pc.\"product_department\"");
        final String l3 = "sales_fact_1997" + (((" (customer:customer_id product:product_id" + " (product_class:product_class_id) time_by_day:time_id)") + ":[COUNT(), SUM(sales_fact_1997.unit_sales),") + " MIN(product.product_id), COUNT(DISTINCT time_by_day.the_day)]");
        t.addQuery(q3);
        Assert.assertThat(t.s.latticeMap.size(), CoreMatchers.is(1));
        Assert.assertThat(Iterables.getOnlyElement(t.s.latticeMap.keySet()), CoreMatchers.is(l3));
    }

    @Test
    public void testExpression() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart().withEvolve(true);
        final String q0 = "select\n" + (((("  \"fname\" || \' \' || \"lname\" as \"full_name\",\n" + "  count(*) as c,\n") + "  avg(\"total_children\" - \"num_children_at_home\")\n") + "from \"customer\"\n") + "group by \"fname\", \"lname\"");
        final String l0 = "customer:[COUNT(), AVG($f2)]";
        t.addQuery(q0);
        Assert.assertThat(t.s.latticeMap.size(), CoreMatchers.is(1));
        Assert.assertThat(Iterables.getOnlyElement(t.s.latticeMap.keySet()), CoreMatchers.is(l0));
        final Lattice lattice = Iterables.getOnlyElement(t.s.latticeMap.values());
        final List<Lattice.DerivedColumn> derivedColumns = lattice.columns.stream().filter(( c) -> c instanceof Lattice.DerivedColumn).map(( c) -> ((Lattice.DerivedColumn) (c))).collect(Collectors.toList());
        Assert.assertThat(derivedColumns.size(), CoreMatchers.is(2));
        final List<String> tables = ImmutableList.of("customer");
        Assert.assertThat(derivedColumns.get(0).tables, CoreMatchers.is(tables));
        Assert.assertThat(derivedColumns.get(1).tables, CoreMatchers.is(tables));
    }

    @Test
    public void testExpressionInJoin() throws Exception {
        final LatticeSuggesterTest.Tester t = new LatticeSuggesterTest.Tester().foodmart().withEvolve(true);
        final String q0 = "select\n" + (((("  \"fname\" || \' \' || \"lname\" as \"full_name\",\n" + "  count(*) as c,\n") + "  avg(\"total_children\" - \"num_children_at_home\")\n") + "from \"customer\" join \"sales_fact_1997\" using (\"customer_id\")\n") + "group by \"fname\", \"lname\"");
        final String l0 = "sales_fact_1997 (customer:customer_id)" + ":[COUNT(), AVG($f2)]";
        t.addQuery(q0);
        Assert.assertThat(t.s.latticeMap.size(), CoreMatchers.is(1));
        Assert.assertThat(Iterables.getOnlyElement(t.s.latticeMap.keySet()), CoreMatchers.is(l0));
        final Lattice lattice = Iterables.getOnlyElement(t.s.latticeMap.values());
        final List<Lattice.DerivedColumn> derivedColumns = lattice.columns.stream().filter(( c) -> c instanceof Lattice.DerivedColumn).map(( c) -> ((Lattice.DerivedColumn) (c))).collect(Collectors.toList());
        Assert.assertThat(derivedColumns.size(), CoreMatchers.is(2));
        final List<String> tables = ImmutableList.of("customer");
        Assert.assertThat(derivedColumns.get(0).tables, CoreMatchers.is(tables));
        Assert.assertThat(derivedColumns.get(1).tables, CoreMatchers.is(tables));
    }

    /**
     * Test helper.
     */
    private static class Tester {
        final LatticeSuggester s;

        private final FrameworkConfig config;

        Tester() {
            this(Frameworks.newConfigBuilder().defaultSchema(LatticeSuggesterTest.Tester.schemaFrom(SCOTT)).build());
        }

        private Tester(FrameworkConfig config) {
            this.config = config;
            s = new LatticeSuggester(config);
        }

        LatticeSuggesterTest.Tester withConfig(FrameworkConfig config) {
            return new LatticeSuggesterTest.Tester(config);
        }

        LatticeSuggesterTest.Tester foodmart() {
            return schema(JDBC_FOODMART);
        }

        private LatticeSuggesterTest.Tester schema(CalciteAssert.SchemaSpec schemaSpec) {
            return withConfig(builder().defaultSchema(LatticeSuggesterTest.Tester.schemaFrom(schemaSpec)).build());
        }

        private ConfigBuilder builder() {
            return Frameworks.newConfigBuilder(config);
        }

        List<Lattice> addQuery(String q) throws SqlParseException, RelConversionException, ValidationException {
            final Planner planner = new org.apache.calcite.prepare.PlannerImpl(config);
            final SqlNode node = planner.parse(q);
            final SqlNode node2 = planner.validate(node);
            final RelRoot root = planner.rel(node2);
            return s.addQuery(root.project());
        }

        /**
         * Parses a query returns its graph.
         */
        LatticeRootNode node(String q) throws SqlParseException, RelConversionException, ValidationException {
            final List<Lattice> list = addQuery(q);
            Assert.assertThat(list.size(), CoreMatchers.is(1));
            return list.get(0).rootNode;
        }

        private static SchemaPlus schemaFrom(CalciteAssert.SchemaSpec spec) {
            final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
            return CalciteAssert.addSchema(rootSchema, spec);
        }

        LatticeSuggesterTest.Tester withEvolve(boolean evolve) {
            return withConfig(builder().evolveLattice(evolve).build());
        }
    }
}

/**
 * End LatticeSuggesterTest.java
 */
