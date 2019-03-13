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


import MinorType.VARCHAR;
import java.io.IOException;
import java.util.Iterator;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.rowSet.DirectRowSet;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// CSV reader now hosted on the row set framework
@Category(RowSetTests.class)
public class TestCsvWithoutHeaders extends BaseCsvTest {
    private static final String TEST_FILE_NAME = "simple.csv";

    private static String[] sampleData = new String[]{ "10,foo,bar", "20,fred,wilma" };

    private static String[] raggedRows = new String[]{ "10,dino", "20,foo,bar", "30" };

    private static String[] secondSet = new String[]{ "30,barney,betty" };

    @Test
    public void testWildcard() throws IOException {
        try {
            enableV3(false);
            doTestWildcard();
            enableV3(true);
            doTestWildcard();
        } finally {
            resetV3();
        }
    }

    @Test
    public void testColumns() throws IOException {
        try {
            enableV3(false);
            doTestColumns();
            enableV3(true);
            doTestColumns();
        } finally {
            resetV3();
        }
    }

    @Test
    public void doTestWildcardAndMetadataV2() throws IOException {
        try {
            enableV3(false);
            String sql = "SELECT *, filename FROM `dfs.data`.`%s`";
            RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsvWithoutHeaders.TEST_FILE_NAME).rowSet();
            TupleMetadata expectedSchema = new SchemaBuilder().addArray("columns", VARCHAR).addNullable("filename", VARCHAR).buildSchema();
            RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(RowSetUtilities.strArray("10", "foo", "bar"), TestCsvWithoutHeaders.TEST_FILE_NAME).addRow(RowSetUtilities.strArray("20", "fred", "wilma"), TestCsvWithoutHeaders.TEST_FILE_NAME).build();
            RowSetUtilities.verify(expected, actual);
        } finally {
            resetV3();
        }
    }

    @Test
    public void doTestWildcardAndMetadataV3() throws IOException {
        try {
            enableV3(true);
            String sql = "SELECT *, filename FROM `dfs.data`.`%s`";
            RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsvWithoutHeaders.TEST_FILE_NAME).rowSet();
            TupleMetadata expectedSchema = new SchemaBuilder().addArray("columns", VARCHAR).add("filename", VARCHAR).buildSchema();
            RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(RowSetUtilities.strArray("10", "foo", "bar"), TestCsvWithoutHeaders.TEST_FILE_NAME).addRow(RowSetUtilities.strArray("20", "fred", "wilma"), TestCsvWithoutHeaders.TEST_FILE_NAME).build();
            RowSetUtilities.verify(expected, actual);
        } finally {
            resetV3();
        }
    }

    @Test
    public void testColumnsAndMetadataV2() throws IOException {
        try {
            enableV3(false);
            String sql = "SELECT columns, filename FROM `dfs.data`.`%s`";
            RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsvWithoutHeaders.TEST_FILE_NAME).rowSet();
            TupleMetadata expectedSchema = new SchemaBuilder().addArray("columns", VARCHAR).addNullable("filename", VARCHAR).buildSchema();
            RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(RowSetUtilities.strArray("10", "foo", "bar"), TestCsvWithoutHeaders.TEST_FILE_NAME).addRow(RowSetUtilities.strArray("20", "fred", "wilma"), TestCsvWithoutHeaders.TEST_FILE_NAME).build();
            RowSetUtilities.verify(expected, actual);
        } finally {
            resetV3();
        }
    }

    @Test
    public void testColumnsAndMetadataV3() throws IOException {
        try {
            enableV3(true);
            String sql = "SELECT columns, filename FROM `dfs.data`.`%s`";
            RowSet actual = ClusterTest.client.queryBuilder().sql(sql, TestCsvWithoutHeaders.TEST_FILE_NAME).rowSet();
            TupleMetadata expectedSchema = new SchemaBuilder().addArray("columns", VARCHAR).add("filename", VARCHAR).buildSchema();
            RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(RowSetUtilities.strArray("10", "foo", "bar"), TestCsvWithoutHeaders.TEST_FILE_NAME).addRow(RowSetUtilities.strArray("20", "fred", "wilma"), TestCsvWithoutHeaders.TEST_FILE_NAME).build();
            RowSetUtilities.verify(expected, actual);
        } finally {
            resetV3();
        }
    }

    @Test
    public void testSpecificColumns() throws IOException {
        try {
            enableV3(false);
            doTestSpecificColumns();
            enableV3(true);
            doTestSpecificColumns();
        } finally {
            resetV3();
        }
    }

    @Test
    public void testRaggedRows() throws IOException {
        try {
            enableV3(false);
            doTestRaggedRows();
            enableV3(true);
            doTestRaggedRows();
        } finally {
            resetV3();
        }
    }

    /**
     * Test partition expansion. Because the two files are read in the
     * same scan operator, the schema is consistent.
     * <p>
     * V2, since Drill 1.12, puts partition columns ahead of data columns.
     */
    @Test
    public void testPartitionExpansionV2() throws IOException {
        try {
            enableV3(false);
            String sql = "SELECT * FROM `dfs.data`.`%s`";
            Iterator<DirectRowSet> iter = ClusterTest.client.queryBuilder().sql(sql, BaseCsvTest.PART_DIR).rowSetIterator();
            TupleMetadata expectedSchema = new SchemaBuilder().addNullable("dir0", VARCHAR).addArray("columns", VARCHAR).buildSchema();
            // Read the two batches.
            for (int i = 0; i < 2; i++) {
                Assert.assertTrue(iter.hasNext());
                RowSet rowSet = iter.next();
                // Figure out which record this is and test accordingly.
                RowSetReader reader = rowSet.reader();
                Assert.assertTrue(reader.next());
                ArrayReader ar = array(1);
                Assert.assertTrue(ar.next());
                String col1 = ar.scalar().getString();
                if (col1.equals("10")) {
                    RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(null, RowSetUtilities.strArray("10", "foo", "bar")).addRow(null, RowSetUtilities.strArray("20", "fred", "wilma")).build();
                    RowSetUtilities.verify(expected, rowSet);
                } else {
                    RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(BaseCsvTest.NESTED_DIR, RowSetUtilities.strArray("30", "barney", "betty")).build();
                    RowSetUtilities.verify(expected, rowSet);
                }
            }
            Assert.assertFalse(iter.hasNext());
        } finally {
            resetV3();
        }
    }

    /**
     * Test partition expansion in V3.
     * <p>
     * V3, as in V2 before Drill 1.12, puts partition columns after
     * data columns (so that data columns don't shift positions if
     * files are nested to another level.)
     */
    @Test
    public void testPartitionExpansionV3() throws IOException {
        try {
            enableV3(true);
            String sql = "SELECT * FROM `dfs.data`.`%s`";
            Iterator<DirectRowSet> iter = ClusterTest.client.queryBuilder().sql(sql, BaseCsvTest.PART_DIR).rowSetIterator();
            TupleMetadata expectedSchema = new SchemaBuilder().addArray("columns", VARCHAR).addNullable("dir0", VARCHAR).buildSchema();
            // First batch is empty; just carries the schema.
            Assert.assertTrue(iter.hasNext());
            RowSet rowSet = iter.next();
            Assert.assertEquals(0, rowSet.rowCount());
            rowSet.clear();
            // Read the other two batches.
            for (int i = 0; i < 2; i++) {
                Assert.assertTrue(iter.hasNext());
                rowSet = iter.next();
                // Figure out which record this is and test accordingly.
                RowSetReader reader = rowSet.reader();
                Assert.assertTrue(reader.next());
                ArrayReader ar = array(0);
                Assert.assertTrue(ar.next());
                String col1 = ar.scalar().getString();
                if (col1.equals("10")) {
                    RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(RowSetUtilities.strArray("10", "foo", "bar"), null).addRow(RowSetUtilities.strArray("20", "fred", "wilma"), null).build();
                    RowSetUtilities.verify(expected, rowSet);
                } else {
                    RowSet expected = new RowSetBuilder(ClusterTest.client.allocator(), expectedSchema).addRow(RowSetUtilities.strArray("30", "barney", "betty"), BaseCsvTest.NESTED_DIR).build();
                    RowSetUtilities.verify(expected, rowSet);
                }
            }
            Assert.assertFalse(iter.hasNext());
        } finally {
            resetV3();
        }
    }
}

