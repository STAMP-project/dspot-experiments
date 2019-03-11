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


import CalciteConnectionProperty.CONFORMANCE;
import SqlConformanceEnum.LENIENT;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.TableFunction;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.TableFunctionImpl;
import org.apache.calcite.util.Smalls;
import org.apache.calcite.util.TestUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for user-defined table functions.
 *
 * @see UdfTest
 * @see Smalls
 */
public class TableFunctionTest {
    /**
     * Tests a table function with literal arguments.
     */
    @Test
    public void testTableFunction() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:")) {
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            SchemaPlus schema = rootSchema.add("s", new AbstractSchema());
            final TableFunction table = TableFunctionImpl.create(Smalls.GENERATE_STRINGS_METHOD);
            schema.add("GenerateStrings", table);
            final String sql = "select *\n" + ("from table(\"s\".\"GenerateStrings\"(5)) as t(n, c)\n" + "where char_length(c) > 3");
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.equalTo("N=4; C=abcd\n"));
        }
    }

    /**
     * Tests a table function that implements {@link ScannableTable} and returns
     * a single column.
     */
    @Test
    public void testScannableTableFunction() throws ClassNotFoundException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        SchemaPlus schema = rootSchema.add("s", new AbstractSchema());
        final TableFunction table = TableFunctionImpl.create(Smalls.MAZE_METHOD);
        schema.add("Maze", table);
        final String sql = "select *\n" + "from table(\"s\".\"Maze\"(5, 3, 1))";
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        final String result = "S=abcde\n" + ("S=xyz\n" + "S=generate(w=5, h=3, s=1)\n");
        Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is(result));
    }

    /**
     * As {@link #testScannableTableFunction()} but with named parameters.
     */
    @Test
    public void testScannableTableFunctionWithNamedParameters() throws ClassNotFoundException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:calcite:");
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        SchemaPlus schema = rootSchema.add("s", new AbstractSchema());
        final TableFunction table = TableFunctionImpl.create(Smalls.MAZE2_METHOD);
        schema.add("Maze", table);
        final String sql = "select *\n" + "from table(\"s\".\"Maze\"(5, 3, 1))";
        final Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        final String result = "S=abcde\n" + "S=xyz\n";
        Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate2(w=5, h=3, s=1)\n")));
        final String sql2 = "select *\n" + "from table(\"s\".\"Maze\"(WIDTH => 5, HEIGHT => 3, SEED => 1))";
        resultSet = statement.executeQuery(sql2);
        Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate2(w=5, h=3, s=1)\n")));
        final String sql3 = "select *\n" + "from table(\"s\".\"Maze\"(HEIGHT => 3, WIDTH => 5))";
        resultSet = statement.executeQuery(sql3);
        Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate2(w=5, h=3, s=null)\n")));
        connection.close();
    }

    /**
     * As {@link #testScannableTableFunction()} but with named parameters.
     */
    @Test
    public void testMultipleScannableTableFunctionWithNamedParameters() throws ClassNotFoundException, SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:");Statement statement = connection.createStatement()) {
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            SchemaPlus schema = rootSchema.add("s", new AbstractSchema());
            final TableFunction table1 = TableFunctionImpl.create(Smalls.MAZE_METHOD);
            schema.add("Maze", table1);
            final TableFunction table2 = TableFunctionImpl.create(Smalls.MAZE2_METHOD);
            schema.add("Maze", table2);
            final TableFunction table3 = TableFunctionImpl.create(Smalls.MAZE3_METHOD);
            schema.add("Maze", table3);
            final String sql = "select *\n" + "from table(\"s\".\"Maze\"(5, 3, 1))";
            ResultSet resultSet = statement.executeQuery(sql);
            final String result = "S=abcde\n" + "S=xyz\n";
            Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate(w=5, h=3, s=1)\n")));
            final String sql2 = "select *\n" + "from table(\"s\".\"Maze\"(WIDTH => 5, HEIGHT => 3, SEED => 1))";
            resultSet = statement.executeQuery(sql2);
            Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate2(w=5, h=3, s=1)\n")));
            final String sql3 = "select *\n" + "from table(\"s\".\"Maze\"(HEIGHT => 3, WIDTH => 5))";
            resultSet = statement.executeQuery(sql3);
            Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate2(w=5, h=3, s=null)\n")));
            final String sql4 = "select *\n" + "from table(\"s\".\"Maze\"(FOO => \'a\'))";
            resultSet = statement.executeQuery(sql4);
            Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.is((result + "S=generate3(foo=a)\n")));
        }
    }

    /**
     * Tests a table function that returns different row type based on
     * actual call arguments.
     */
    @Test
    public void testTableFunctionDynamicStructure() throws ClassNotFoundException, SQLException {
        Connection connection = getConnectionWithMultiplyFunction();
        final PreparedStatement ps = connection.prepareStatement(("select *\n" + "from table(\"s\".\"multiplication\"(4, 3, ?))\n"));
        ps.setInt(1, 100);
        ResultSet resultSet = ps.executeQuery();
        Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.equalTo(("row_name=row 0; c1=101; c2=102; c3=103; c4=104\n" + ("row_name=row 1; c1=102; c2=104; c3=106; c4=108\n" + "row_name=row 2; c1=103; c2=106; c3=109; c4=112\n"))));
    }

    @Test
    public void testUserDefinedTableFunction() {
        final String q = "select *\n" + "from table(\"s\".\"multiplication\"(2, 3, 100))\n";
        with().query(q).returnsUnordered("row_name=row 0; c1=101; c2=102", "row_name=row 1; c1=102; c2=104", "row_name=row 2; c1=103; c2=106");
    }

    @Test
    public void testUserDefinedTableFunction2() {
        final String q = "select c1\n" + ("from table(\"s\".\"multiplication\"(2, 3, 100))\n" + "where c1 + 2 < c2");
        with().query(q).throws_("Column 'C1' not found in any table; did you mean 'c1'?");
    }

    @Test
    public void testUserDefinedTableFunction3() {
        final String q = "select \"c1\"\n" + ("from table(\"s\".\"multiplication\"(2, 3, 100))\n" + "where \"c1\" + 2 < \"c2\"");
        with().query(q).returnsUnordered("c1=103");
    }

    @Test
    public void testUserDefinedTableFunction4() {
        final String q = "select *\n" + ("from table(\"s\".\"multiplication\"(\'2\', 3, 100))\n" + "where c1 + 2 < c2");
        final String e = "No match found for function signature " + "multiplication(<CHARACTER>, <NUMERIC>, <NUMERIC>)";
        with().query(q).throws_(e);
    }

    @Test
    public void testUserDefinedTableFunction5() {
        final String q = "select *\n" + ("from table(\"s\".\"multiplication\"(3, 100))\n" + "where c1 + 2 < c2");
        final String e = "No match found for function signature " + "multiplication(<NUMERIC>, <NUMERIC>)";
        with().query(q).throws_(e);
    }

    @Test
    public void testUserDefinedTableFunction6() {
        final String q = "select *\n" + "from table(\"s\".\"fibonacci\"())";
        with().query(q).returns(( r) -> {
            try {
                final List<Long> numbers = new ArrayList<>();
                while ((r.next()) && ((numbers.size()) < 13)) {
                    numbers.add(r.getLong(1));
                } 
                Assert.assertThat(numbers.toString(), CoreMatchers.is("[1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233]"));
            } catch (SQLException e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testUserDefinedTableFunction7() {
        final String q = "select *\n" + ("from table(\"s\".\"fibonacci2\"(20))\n" + "where n > 7");
        with().query(q).returnsUnordered("N=13", "N=8");
    }

    @Test
    public void testUserDefinedTableFunction8() {
        final String q = "select count(*) as c\n" + "from table(\"s\".\"fibonacci2\"(20))";
        with().query(q).returnsUnordered("C=7");
    }

    @Test
    public void testCrossApply() {
        final String q1 = "select *\n" + ("from (values 2, 5) as t (c)\n" + "cross apply table(\"s\".\"fibonacci2\"(c))");
        final String q2 = "select *\n" + ("from (values 2, 5) as t (c)\n" + "cross apply table(\"s\".\"fibonacci2\"(t.c))");
        for (String q : new String[]{ q1, q2 }) {
            with().with(CONFORMANCE, LENIENT).query(q).returnsUnordered("C=2; N=1", "C=2; N=1", "C=2; N=2", "C=5; N=1", "C=5; N=1", "C=5; N=2", "C=5; N=3", "C=5; N=5");
        }
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/CALCITE-2382">[CALCITE-2382]
     * Sub-query lateral joined to table function</a>.
     */
    @Test
    public void testInlineViewLateralTableFunction() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:")) {
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            SchemaPlus schema = rootSchema.add("s", new AbstractSchema());
            final TableFunction table = TableFunctionImpl.create(Smalls.GENERATE_STRINGS_METHOD);
            schema.add("GenerateStrings", table);
            Table tbl = new ScannableTableTest.SimpleTable();
            schema.add("t", tbl);
            final String sql = "select *\n" + (("from (select 5 as f0 from \"s\".\"t\") \"a\",\n" + "  lateral table(\"s\".\"GenerateStrings\"(f0)) as t(n, c)\n") + "where char_length(c) > 3");
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            final String expected = "F0=5; N=4; C=abcd\n" + (("F0=5; N=4; C=abcd\n" + "F0=5; N=4; C=abcd\n") + "F0=5; N=4; C=abcd\n");
            Assert.assertThat(CalciteAssert.toString(resultSet), CoreMatchers.equalTo(expected));
        }
    }
}

/**
 * End TableFunctionTest.java
 */
