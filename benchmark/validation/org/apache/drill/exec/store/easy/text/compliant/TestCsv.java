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
package org.apache.drill.exec.store.easy.text.compliant;


import MinorType.INT;
import MinorType.VARCHAR;
import java.io.File;
import java.io.IOException;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;


/**
 * SQL-level tests for CSV headers. See
 * {@link TestHeaderBuilder} for detailed unit tests.
 * This test does not attempt to duplicate all the cases
 * from the unit tests; instead it just does a sanity check.
 */
public class TestCsv extends ClusterTest {
    private static final String CASE2_FILE_NAME = "case2.csv";

    private static File testDir;

    private static String[] emptyHeaders = new String[]{ "", "10,foo,bar" };

    @Test
    public void testEmptyCsvHeaders() throws IOException {
        String fileName = "case1.csv";
        TestCsv.buildFile(fileName, TestCsv.emptyHeaders);
        try {
            ClusterTest.client.queryBuilder().sql(makeStatement(fileName)).run();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("must define at least one header"));
        }
    }

    private static String[] validHeaders = new String[]{ "a,b,c", "10,foo,bar" };

    @Test
    public void testValidCsvHeaders() throws IOException {
        RowSet actual = ClusterTest.client.queryBuilder().sql(makeStatement(TestCsv.CASE2_FILE_NAME)).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", VARCHAR).add("b", VARCHAR).add("c", VARCHAR).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", "foo", "bar").build();
        RowSetUtilities.verify(expected, actual);
    }

    private static String[] invalidHeaders = new String[]{ "$,,9b,c,c,c_2", "10,foo,bar,fourth,fifth,sixth" };

    @Test
    public void testInvalidCsvHeaders() throws IOException {
        String fileName = "case3.csv";
        TestCsv.buildFile(fileName, TestCsv.invalidHeaders);
        RowSet actual = ClusterTest.client.queryBuilder().sql(makeStatement(fileName)).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("column_1", VARCHAR).add("column_2", VARCHAR).add("col_9b", VARCHAR).add("c", VARCHAR).add("c_2", VARCHAR).add("c_2_2", VARCHAR).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", "foo", "bar", "fourth", "fifth", "sixth").build();
        RowSetUtilities.verify(expected, actual);
    }

    // Test fix for DRILL-5590
    @Test
    public void testCsvHeadersCaseInsensitive() throws IOException {
        String sql = "SELECT A, b, C FROM `dfs.data`.`%s`";
        RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsv.CASE2_FILE_NAME).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("A", VARCHAR).add("b", VARCHAR).add("C", VARCHAR).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", "foo", "bar").build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * Verify that the wildcard expands columns to the header names, including
     * case
     */
    @Test
    public void testWildcard() throws IOException {
        String sql = "SELECT * FROM `dfs.data`.`%s`";
        RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsv.CASE2_FILE_NAME).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", VARCHAR).add("b", VARCHAR).add("c", VARCHAR).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", "foo", "bar").build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * Verify that implicit columns are recognized and populated. Sanity test
     * of just one implicit column.
     */
    @Test
    public void testImplicitColsExplicitSelect() throws IOException {
        String sql = "SELECT A, filename FROM `dfs.data`.`%s`";
        RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsv.CASE2_FILE_NAME).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("A", VARCHAR).addNullable("filename", VARCHAR).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", TestCsv.CASE2_FILE_NAME).build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * Verify that implicit columns are recognized and populated. Sanity test
     * of just one implicit column.
     */
    @Test
    public void testImplicitColsWildcard() throws IOException {
        String sql = "SELECT *, filename FROM `dfs.data`.`%s`";
        RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsv.CASE2_FILE_NAME).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", VARCHAR).add("b", VARCHAR).add("c", VARCHAR).addNullable("filename", VARCHAR).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", "foo", "bar", TestCsv.CASE2_FILE_NAME).build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * CSV does not allow explicit use of dir0, dir1, etc. columns. Treated
     * as undefined nullable int columns.
     * <p>
     * Note that the class path storage plugin does not support directories
     * (partitions). It is unclear if that should show up here as the
     * partition column names being undefined (hence Nullable INT) or should
     * they still be defined, but set to a null Nullable VARCHAR?
     */
    @Test
    public void testPartitionColsWildcard() throws IOException {
        String sql = "SELECT *, dir0, dir5 FROM `dfs.data`.`%s`";
        RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsv.CASE2_FILE_NAME).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", VARCHAR).add("b", VARCHAR).add("c", VARCHAR).addNullable("dir0", INT).addNullable("dir5", INT).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", "foo", "bar", null, null).build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * CSV does not allow explicit use of dir0, dir1, etc. columns. Treated
     * as undefined nullable int columns.
     */
    @Test
    public void testPartitionColsExplicit() throws IOException {
        String sql = "SELECT a, dir0, dir5 FROM `dfs.data`.`%s`";
        RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsv.CASE2_FILE_NAME).rowSet();
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", VARCHAR).addNullable("dir0", INT).addNullable("dir5", INT).buildSchema();
        RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow("10", null, null).build();
        RowSetUtilities.verify(expected, actual);
    }
}

