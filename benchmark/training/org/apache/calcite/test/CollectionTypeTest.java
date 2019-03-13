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


import Schema.TableType;
import SqlTypeName.ANY;
import SqlTypeName.INTEGER;
import SqlTypeName.VARCHAR;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.calcite.DataContext;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for
 * <a href="https://issues.apache.org/jira/browse/CALCITE-1386">[CALCITE-1386]
 * ITEM operator seems to ignore the value type of collection and assign the value to Object</a>.
 */
public class CollectionTypeTest {
    @Test
    public void testAccessNestedMap() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        final String sql = "select \"ID\", \"MAPFIELD\"[\'c\'] AS \"MAPFIELD_C\"," + ((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"NESTEDMAPFIELD\"[\'a\'][\'b\'] = 2 AND \"ARRAYFIELD\"[2] = 200");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(1));
        // JDBC doesn't support Map / Nested Map so just relying on string representation
        String expectedRow = "ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; " + "ARRAYFIELD=[100, 200, 300]";
        Assert.assertThat(resultStrings.get(0), CoreMatchers.is(expectedRow));
    }

    @Test
    public void testAccessNonExistKeyFromMap() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        // this shouldn't throw any Exceptions on runtime, just don't return any rows.
        final String sql = "select \"ID\"," + ((" \"MAPFIELD\", \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"MAPFIELD\"[\'a\'] = 2");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(0));
    }

    @Test
    public void testAccessNonExistKeyFromNestedMap() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        // this shouldn't throw any Exceptions on runtime, just don't return any rows.
        final String sql = "select \"ID\", \"MAPFIELD\"," + ((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"NESTEDMAPFIELD\"[\'b\'][\'c\'] = 4");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(0));
    }

    @Test
    public void testInvalidAccessUseStringForIndexOnArray() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        try {
            final String sql = "select \"ID\"," + ((" \"MAPFIELD\", \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"ARRAYFIELD\"[\'a\'] = 200");
            statement.executeQuery(sql);
            Assert.fail("This query shouldn't be evaluated properly");
        } catch (SQLException e) {
            Throwable e2 = e.getCause();
            Assert.assertThat(e2, CoreMatchers.is(CoreMatchers.instanceOf(CalciteContextException.class)));
        }
    }

    @Test
    public void testNestedArrayOutOfBoundAccess() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        final String sql = "select \"ID\"," + ((" \"MAPFIELD\", \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"ARRAYFIELD\"[10] = 200");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        // this is against SQL standard definition...
        // SQL standard states that data exception should be occurred
        // when accessing array with out of bound index.
        // but PostgreSQL breaks it, and this is more convenient since it guarantees runtime safety.
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(0));
    }

    @Test
    public void testAccessNestedMapWithAnyType() throws Exception {
        Connection connection = setupConnectionWithNestedAnyTypeTable();
        final Statement statement = connection.createStatement();
        final String sql = "select \"ID\", \"MAPFIELD\"[\'c\'] AS \"MAPFIELD_C\"," + (((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where CAST(\"NESTEDMAPFIELD\"[\'a\'][\'b\'] AS INTEGER) = 2") + " AND CAST(\"ARRAYFIELD\"[2] AS INTEGER) = 200");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(1));
        // JDBC doesn't support Map / Nested Map so just relying on string representation
        String expectedRow = "ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; " + "ARRAYFIELD=[100, 200, 300]";
        Assert.assertThat(resultStrings.get(0), CoreMatchers.is(expectedRow));
    }

    @Test
    public void testAccessNestedMapWithAnyTypeWithoutCast() throws Exception {
        Connection connection = setupConnectionWithNestedAnyTypeTable();
        final Statement statement = connection.createStatement();
        // placing literal earlier than ANY type is intended: do not modify
        final String sql = "select \"ID\", \"MAPFIELD\"[\'c\'] AS \"MAPFIELD_C\"," + ((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"NESTEDMAPFIELD\"[\'a\'][\'b\'] = 2 AND 200.0 = \"ARRAYFIELD\"[2]");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(1));
        // JDBC doesn't support Map / Nested Map so just relying on string representation
        String expectedRow = "ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; " + "ARRAYFIELD=[100, 200, 300]";
        Assert.assertThat(resultStrings.get(0), CoreMatchers.is(expectedRow));
    }

    @Test
    public void testArithmeticToAnyTypeWithoutCast() throws Exception {
        Connection connection = setupConnectionWithNestedAnyTypeTable();
        final Statement statement = connection.createStatement();
        // placing literal earlier than ANY type is intended: do not modify
        final String sql = "select \"ID\", \"MAPFIELD\"[\'c\'] AS \"MAPFIELD_C\"," + ((((((((((((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where \"NESTEDMAPFIELD\"[\'a\'][\'b\'] + 1.0 = 3 ") + "AND \"NESTEDMAPFIELD\"[\'a\'][\'b\'] * 2.0 = 4 ") + "AND \"NESTEDMAPFIELD\"[\'a\'][\'b\'] > 1") + "AND \"NESTEDMAPFIELD\"[\'a\'][\'b\'] >= 2") + "AND 100.1 <> \"ARRAYFIELD\"[2] - 100.0") + "AND 100.0 = \"ARRAYFIELD\"[2] / 2") + "AND 99.9 < \"ARRAYFIELD\"[2] / 2") + "AND 100.0 <= \"ARRAYFIELD\"[2] / 2") + "AND \'200\' <> \"STRINGARRAYFIELD\"[1]") + "AND \'200\' = \"STRINGARRAYFIELD\"[2]") + "AND \'100\' < \"STRINGARRAYFIELD\"[2]");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(1));
        // JDBC doesn't support Map / Nested Map so just relying on string representation
        String expectedRow = "ID=2; MAPFIELD_C=4; NESTEDMAPFIELD={a={b=2, c=4}}; " + "ARRAYFIELD=[100, 200, 300]";
        Assert.assertThat(resultStrings.get(0), CoreMatchers.is(expectedRow));
    }

    @Test
    public void testAccessNonExistKeyFromMapWithAnyType() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        // this shouldn't throw any Exceptions on runtime, just don't return any rows.
        final String sql = "select \"ID\", \"MAPFIELD\", " + (("\"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where CAST(\"MAPFIELD\"[\'a\'] AS INTEGER) = 2");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(0));
    }

    @Test
    public void testAccessNonExistKeyFromNestedMapWithAnyType() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        // this shouldn't throw any Exceptions on runtime, just don't return any rows.
        final String sql = "select \"ID\", \"MAPFIELD\"," + ((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where CAST(\"NESTEDMAPFIELD\"[\'b\'][\'c\'] AS INTEGER) = 4");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(0));
    }

    @Test
    public void testInvalidAccessUseStringForIndexOnArrayWithAnyType() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        try {
            final String sql = "select \"ID\", \"MAPFIELD\"," + ((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where CAST(\"ARRAYFIELD\"[\'a\'] AS INTEGER) = 200");
            statement.executeQuery(sql);
            Assert.fail("This query shouldn't be evaluated properly");
        } catch (SQLException e) {
            Throwable e2 = e.getCause();
            Assert.assertThat(e2, CoreMatchers.is(CoreMatchers.instanceOf(CalciteContextException.class)));
        }
    }

    @Test
    public void testNestedArrayOutOfBoundAccessWithAnyType() throws Exception {
        Connection connection = setupConnectionWithNestedTable();
        final Statement statement = connection.createStatement();
        final String sql = "select \"ID\", \"MAPFIELD\"," + ((" \"NESTEDMAPFIELD\", \"ARRAYFIELD\" " + "from \"s\".\"nested\" ") + "where CAST(\"ARRAYFIELD\"[10] AS INTEGER) = 200");
        final ResultSet resultSet = statement.executeQuery(sql);
        final List<String> resultStrings = CalciteAssert.toList(resultSet);
        // this is against SQL standard definition...
        // SQL standard states that data exception should be occurred
        // when accessing array with out of bound index.
        // but PostgreSQL breaks it, and this is more convenient since it guarantees runtime safety.
        Assert.assertThat(resultStrings.size(), CoreMatchers.is(0));
    }

    /**
     * Table that returns columns which include complicated collection type via the ScannableTable
     * interface.
     */
    public static class NestedCollectionTable implements ScannableTable {
        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            RelDataType nullableVarcharType = typeFactory.createTypeWithNullability(typeFactory.createSqlType(VARCHAR), true);
            RelDataType nullableIntegerType = typeFactory.createTypeWithNullability(typeFactory.createSqlType(INTEGER), true);
            RelDataType nullableMapType = typeFactory.createTypeWithNullability(typeFactory.createMapType(nullableVarcharType, nullableIntegerType), true);
            return typeFactory.builder().add("ID", INTEGER).add("MAPFIELD", typeFactory.createTypeWithNullability(typeFactory.createMapType(nullableVarcharType, nullableIntegerType), true)).add("NESTEDMAPFIELD", typeFactory.createTypeWithNullability(typeFactory.createMapType(nullableVarcharType, nullableMapType), true)).add("ARRAYFIELD", typeFactory.createTypeWithNullability(typeFactory.createArrayType(nullableIntegerType, (-1L)), true)).add("STRINGARRAYFIELD", typeFactory.createTypeWithNullability(typeFactory.createArrayType(nullableVarcharType, (-1L)), true)).build();
        }

        public Statistic getStatistic() {
            return Statistics.UNKNOWN;
        }

        public TableType getJdbcTableType() {
            return TableType.TABLE;
        }

        public Enumerable<Object[]> scan(DataContext root) {
            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                public Enumerator<Object[]> enumerator() {
                    return CollectionTypeTest.nestedRecordsEnumerator();
                }
            };
        }

        @Override
        public boolean isRolledUp(String column) {
            return false;
        }

        @Override
        public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
            return false;
        }
    }

    /**
     * Table that returns columns which include complicated collection type via the ScannableTable
     * interface.
     */
    public static class NestedCollectionWithAnyTypeTable implements ScannableTable {
        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            return typeFactory.builder().add("ID", INTEGER).add("MAPFIELD", ANY).add("NESTEDMAPFIELD", ANY).add("ARRAYFIELD", ANY).add("STRINGARRAYFIELD", ANY).build();
        }

        public Statistic getStatistic() {
            return Statistics.UNKNOWN;
        }

        public TableType getJdbcTableType() {
            return TableType.TABLE;
        }

        public Enumerable<Object[]> scan(DataContext root) {
            return new org.apache.calcite.linq4j.AbstractEnumerable<Object[]>() {
                public Enumerator<Object[]> enumerator() {
                    return CollectionTypeTest.nestedRecordsEnumerator();
                }
            };
        }

        @Override
        public boolean isRolledUp(String column) {
            return false;
        }

        @Override
        public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
            return false;
        }
    }
}

/**
 * End CollectionTypeTest.java
 */
