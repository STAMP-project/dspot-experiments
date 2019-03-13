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


import SqlTypeName.INTEGER;
import SqlTypeName.VARCHAR;
import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.calcite.DataContext;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;
import org.apache.calcite.schema.ProjectableFilterableTable;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.AbstractTable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.schema.ScannableTable}.
 */
public class ScannableTableTest {
    @Test
    public void testTens() throws SQLException {
        final Enumerator<Object[]> cursor = ScannableTableTest.tens();
        Assert.assertTrue(cursor.moveNext());
        Assert.assertThat(cursor.current()[0], CoreMatchers.equalTo(((Object) (0))));
        Assert.assertThat(cursor.current().length, CoreMatchers.equalTo(1));
        Assert.assertTrue(cursor.moveNext());
        Assert.assertThat(cursor.current()[0], CoreMatchers.equalTo(((Object) (10))));
        Assert.assertTrue(cursor.moveNext());
        Assert.assertThat(cursor.current()[0], CoreMatchers.equalTo(((Object) (20))));
        Assert.assertTrue(cursor.moveNext());
        Assert.assertThat(cursor.current()[0], CoreMatchers.equalTo(((Object) (30))));
        Assert.assertFalse(cursor.moveNext());
    }

    /**
     * A table with one column.
     */
    @Test
    public void testSimple() throws Exception {
        CalciteAssert.that().with(newSchema("s", "simple", new ScannableTableTest.SimpleTable())).query("select * from \"s\".\"simple\"").returnsUnordered("i=0", "i=10", "i=20", "i=30");
    }

    /**
     * A table with two columns.
     */
    @Test
    public void testSimple2() throws Exception {
        CalciteAssert.that().with(newSchema("s", "beatles", new ScannableTableTest.BeatlesTable())).query("select * from \"s\".\"beatles\"").returnsUnordered("i=4; j=John", "i=4; j=Paul", "i=6; j=George", "i=5; j=Ringo");
    }

    /**
     * A filter on a {@link FilterableTable} with two columns (cooperative).
     */
    @Test
    public void testFilterableTableCooperative() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesFilterableTable(buf, true);
        final String explain = "PLAN=" + ("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles]], filters=[[=($0, 4)]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query("select * from \"s\".\"beatles\" where \"i\" = 4").explainContains(explain).returnsUnordered("i=4; j=John; k=1940", "i=4; j=Paul; k=1942");
        // Only 2 rows came out of the table. If the value is 4, it means that the
        // planner did not pass the filter down.
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=2, filter=4"));
    }

    /**
     * A filter on a {@link FilterableTable} with two columns (noncooperative).
     */
    @Test
    public void testFilterableTableNonCooperative() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesFilterableTable(buf, false);
        final String explain = "PLAN=" + ("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles2]], filters=[[=($0, 4)]])");
        CalciteAssert.that().with(newSchema("s", "beatles2", table)).query("select * from \"s\".\"beatles2\" where \"i\" = 4").explainContains(explain).returnsUnordered("i=4; j=John; k=1940", "i=4; j=Paul; k=1942");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=4"));
    }

    /**
     * A filter on a {@link org.apache.calcite.schema.ProjectableFilterableTable}
     * with two columns (cooperative).
     */
    @Test
    public void testProjectableFilterableCooperative() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, true);
        final String explain = "PLAN=" + ("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles]], filters=[[=($0, 4)]], projects=[[1]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query("select \"j\" from \"s\".\"beatles\" where \"i\" = 4").explainContains(explain).returnsUnordered("j=John", "j=Paul");
        // Only 2 rows came out of the table. If the value is 4, it means that the
        // planner did not pass the filter down.
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=2, filter=4, projects=[1]"));
    }

    @Test
    public void testProjectableFilterableNonCooperative() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=" + ("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles2]], filters=[[=($0, 4)]], projects=[[1]]");
        CalciteAssert.that().with(newSchema("s", "beatles2", table)).query("select \"j\" from \"s\".\"beatles2\" where \"i\" = 4").explainContains(explain).returnsUnordered("j=John", "j=Paul");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=4, projects=[1, 0]"));
    }

    /**
     * A filter on a {@link org.apache.calcite.schema.ProjectableFilterableTable}
     * with two columns, and a project in the query. (Cooperative)
     */
    @Test
    public void testProjectableFilterableWithProjectAndFilter() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, true);
        final String explain = "PLAN=" + ("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles]], filters=[[=($0, 4)]], projects=[[2, 1]]");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query("select \"k\",\"j\" from \"s\".\"beatles\" where \"i\" = 4").explainContains(explain).returnsUnordered("k=1940; j=John", "k=1942; j=Paul");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=2, filter=4, projects=[2, 1]"));
    }

    /**
     * A filter on a {@link org.apache.calcite.schema.ProjectableFilterableTable}
     * with two columns, and a project in the query (NonCooperative).
     */
    @Test
    public void testProjectableFilterableWithProjectFilterNonCooperative() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=" + (("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles]], filters=[[>($2, 1941)]], ") + "projects=[[0, 2]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query("select \"i\",\"k\" from \"s\".\"beatles\" where \"k\" > 1941").explainContains(explain).returnsUnordered("i=4; k=1942", "i=6; k=1943");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=4, projects=[0, 2]"));
    }

    /**
     * A filter and project on a
     * {@link org.apache.calcite.schema.ProjectableFilterableTable}. The table
     * refuses to execute the filter, so Calcite should add a pull up and
     * transform the filter (projecting the column needed by the filter).
     */
    @Test
    public void testPFTableRefusesFilterCooperative() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles2]], filters=[[=($0, 4)]], projects=[[2]])";
        CalciteAssert.that().with(newSchema("s", "beatles2", table)).query("select \"k\" from \"s\".\"beatles2\" where \"i\" = 4").explainContains(explain).returnsUnordered("k=1940", "k=1942");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=4, projects=[2, 0]"));
    }

    @Test
    public void testPFPushDownProjectFilterInAggregateNoGroup() {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=EnumerableAggregate(group=[{}], M=[MAX($0)])\n" + ("  EnumerableInterpreter\n" + "    BindableTableScan(table=[[s, beatles]], filters=[[>($0, 1)]], projects=[[2]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query("select max(\"k\") as m from \"s\".\"beatles\" where \"i\" > 1").explainContains(explain).returnsUnordered("M=1943");
    }

    @Test
    public void testPFPushDownProjectFilterAggregateGroup() {
        final String sql = "select \"i\", count(*) as c\n" + (("from \"s\".\"beatles\"\n" + "where \"k\" > 1900\n") + "group by \"i\"");
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=" + ((("EnumerableAggregate(group=[{0}], C=[COUNT()])\n" + "  EnumerableInterpreter\n") + "    BindableTableScan(table=[[s, beatles]], filters=[[>($2, 1900)]], ") + "projects=[[0]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query(sql).explainContains(explain).returnsUnordered("i=4; C=2", "i=5; C=1", "i=6; C=1");
    }

    @Test
    public void testPFPushDownProjectFilterAggregateNested() {
        final StringBuilder buf = new StringBuilder();
        final String sql = "select \"k\", count(*) as c\n" + ((("from (\n" + "  select \"k\", \"i\" from \"s\".\"beatles\" group by \"k\", \"i\") t\n") + "where \"k\" = 1940\n") + "group by \"k\"");
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=" + (((("EnumerableAggregate(group=[{0}], C=[COUNT()])\n" + "  EnumerableAggregate(group=[{0, 1}])\n") + "    EnumerableInterpreter\n") + "      BindableTableScan(table=[[s, beatles]], ") + "filters=[[=($2, 1940)]], projects=[[2, 0]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query(sql).explainContains(explain).returnsUnordered("k=1940; C=2");
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-458">[CALCITE-458]
     * ArrayIndexOutOfBoundsException when using just a single column in
     * interpreter</a>.
     */
    @Test
    public void testPFTableRefusesFilterSingleColumn() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, false);
        final String explain = "PLAN=" + ("EnumerableInterpreter\n" + "  BindableTableScan(table=[[s, beatles2]], filters=[[>($2, 1941)]], projects=[[2]])");
        CalciteAssert.that().with(newSchema("s", "beatles2", table)).query("select \"k\" from \"s\".\"beatles2\" where \"k\" > 1941").explainContains(explain).returnsUnordered("k=1942", "k=1943");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=4, projects=[2]"));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2039">[CALCITE-2039]
     * AssertionError when pushing project to ProjectableFilterableTable</a>.
     * Cannot push down a project if it is not a permutation of columns; in this
     * case, it contains a literal.
     */
    @Test
    public void testCannotPushProject() throws Exception {
        final StringBuilder buf = new StringBuilder();
        final Table table = new ScannableTableTest.BeatlesProjectableFilterableTable(buf, true);
        final String explain = "PLAN=" + ((("EnumerableCalc(expr#0..2=[{inputs}], expr#3=[3], k=[$t2], j=[$t1], " + "i=[$t0], EXPR$3=[$t3])\n") + "  EnumerableInterpreter\n") + "    BindableTableScan(table=[[s, beatles]])");
        CalciteAssert.that().with(newSchema("s", "beatles", table)).query("select \"k\",\"j\",\"i\",3 from \"s\".\"beatles\"").explainContains(explain).returnsUnordered("k=1940; j=John; i=4; EXPR$3=3", "k=1940; j=Ringo; i=5; EXPR$3=3", "k=1942; j=Paul; i=4; EXPR$3=3", "k=1943; j=George; i=6; EXPR$3=3");
        Assert.assertThat(buf.toString(), CoreMatchers.is("returnCount=4"));
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-1031">[CALCITE-1031]
     * In prepared statement, CsvScannableTable.scan is called twice</a>.
     */
    @Test
    public void testPrepared2() throws SQLException {
        final Properties properties = new Properties();
        properties.setProperty("caseSensitive", "true");
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", properties)) {
            final CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            final AtomicInteger scanCount = new AtomicInteger();
            final AtomicInteger enumerateCount = new AtomicInteger();
            final Schema schema = new AbstractSchema() {
                @Override
                protected Map<String, Table> getTableMap() {
                    return ImmutableMap.of("TENS", new ScannableTableTest.SimpleTable() {
                        private Enumerable<Object[]> superScan(DataContext root) {
                            return super.scan(root);
                        }

                        @Override
                        public Enumerable<Object[]> scan(final DataContext root) {
                            scanCount.incrementAndGet();
                            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                                public Enumerator<Object[]> enumerator() {
                                    enumerateCount.incrementAndGet();
                                    return superScan(root).enumerator();
                                }
                            };
                        }
                    });
                }
            };
            calciteConnection.getRootSchema().add("TEST", schema);
            final String sql = "select * from \"TEST\".\"TENS\" where \"i\" < ?";
            final PreparedStatement statement = calciteConnection.prepareStatement(sql);
            Assert.assertThat(scanCount.get(), CoreMatchers.is(0));
            Assert.assertThat(enumerateCount.get(), CoreMatchers.is(0));
            // First execute
            statement.setInt(1, 20);
            Assert.assertThat(scanCount.get(), CoreMatchers.is(0));
            ResultSet resultSet = statement.executeQuery();
            Assert.assertThat(scanCount.get(), CoreMatchers.is(1));
            Assert.assertThat(enumerateCount.get(), CoreMatchers.is(1));
            Assert.assertThat(resultSet, Matchers.returnsUnordered("i=0", "i=10"));
            Assert.assertThat(scanCount.get(), CoreMatchers.is(1));
            Assert.assertThat(enumerateCount.get(), CoreMatchers.is(1));
            // Second execute
            resultSet = statement.executeQuery();
            Assert.assertThat(scanCount.get(), CoreMatchers.is(2));
            Assert.assertThat(resultSet, Matchers.returnsUnordered("i=0", "i=10"));
            Assert.assertThat(scanCount.get(), CoreMatchers.is(2));
            // Third execute
            statement.setInt(1, 30);
            resultSet = statement.executeQuery();
            Assert.assertThat(scanCount.get(), CoreMatchers.is(3));
            Assert.assertThat(resultSet, Matchers.returnsUnordered("i=0", "i=10", "i=20"));
            Assert.assertThat(scanCount.get(), CoreMatchers.is(3));
        }
    }

    /**
     * Table that returns one column via the {@link ScannableTable} interface.
     */
    public static class SimpleTable extends AbstractTable implements ScannableTable {
        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder().add("i", INTEGER).build();
        }

        public Enumerable<Object[]> scan(DataContext root) {
            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                public Enumerator<Object[]> enumerator() {
                    return ScannableTableTest.tens();
                }
            };
        }
    }

    /**
     * Table that returns two columns via the ScannableTable interface.
     */
    public static class BeatlesTable extends AbstractTable implements ScannableTable {
        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder().add("i", INTEGER).add("j", VARCHAR).build();
        }

        public Enumerable<Object[]> scan(DataContext root) {
            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                public Enumerator<Object[]> enumerator() {
                    return ScannableTableTest.beatles(new StringBuilder(), null, null);
                }
            };
        }
    }

    /**
     * Table that returns two columns via the {@link FilterableTable}
     * interface.
     */
    public static class BeatlesFilterableTable extends AbstractTable implements FilterableTable {
        private final StringBuilder buf;

        private final boolean cooperative;

        public BeatlesFilterableTable(StringBuilder buf, boolean cooperative) {
            this.buf = buf;
            this.cooperative = cooperative;
        }

        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder().add("i", INTEGER).add("j", VARCHAR).add("k", INTEGER).build();
        }

        public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters) {
            final Integer filter = ScannableTableTest.getFilter(cooperative, filters);
            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                public Enumerator<Object[]> enumerator() {
                    return ScannableTableTest.beatles(buf, filter, null);
                }
            };
        }
    }

    /**
     * Table that returns two columns via the {@link FilterableTable}
     * interface.
     */
    public static class BeatlesProjectableFilterableTable extends AbstractTable implements ProjectableFilterableTable {
        private final StringBuilder buf;

        private final boolean cooperative;

        public BeatlesProjectableFilterableTable(StringBuilder buf, boolean cooperative) {
            this.buf = buf;
            this.cooperative = cooperative;
        }

        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder().add("i", INTEGER).add("j", VARCHAR).add("k", INTEGER).build();
        }

        public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters, final int[] projects) {
            final Integer filter = ScannableTableTest.getFilter(cooperative, filters);
            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                public Enumerator<Object[]> enumerator() {
                    return ScannableTableTest.beatles(buf, filter, projects);
                }
            };
        }
    }

    private static final Object[][] BEATLES = new Object[][]{ new Object[]{ 4, "John", 1940 }, new Object[]{ 4, "Paul", 1942 }, new Object[]{ 6, "George", 1943 }, new Object[]{ 5, "Ringo", 1940 } };
}

/**
 * End ScannableTableTest.java
 */
