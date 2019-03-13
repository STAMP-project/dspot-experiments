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
package org.apache.drill.exec.physical.impl.scan;


import ColumnsArrayManager.COLUMNS_COL;
import MinorType.VARCHAR;
import SchemaPath.DYNAMIC_STAR;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.physical.impl.scan.project.ReaderSchemaOrchestrator;
import org.apache.drill.exec.physical.impl.scan.project.ScanSchemaOrchestrator;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.RowSetTestUtils;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the "columns" array mechanism integrated with the scan schema
 * orchestrator including simulating reading data.
 */
@Category(RowSetTests.class)
public class TestColumnsArray extends SubOperatorTest {
    private static class MockScanner {
        ScanSchemaOrchestrator scanner;

        ReaderSchemaOrchestrator reader;

        ResultSetLoader loader;
    }

    /**
     * Test columns array. The table must be able to support it by having a
     * matching column.
     */
    @Test
    public void testColumnsArray() {
        TestColumnsArray.MockScanner mock = buildScanner(RowSetTestUtils.projectList(ScanTestUtils.FILE_NAME_COL, COLUMNS_COL, ScanTestUtils.partitionColName(0)));
        // Verify empty batch.
        TupleMetadata expectedSchema = new SchemaBuilder().add("filename", VARCHAR).addArray("columns", VARCHAR).addNullable("dir0", VARCHAR).buildSchema();
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).build();
            Assert.assertNotNull(mock.scanner.output());
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(mock.scanner.output()));
        }
        // Create a batch of data.
        mock.reader.startBatch();
        mock.loader.writer().addRow(new Object[]{ new String[]{ "fred", "flintstone" } }).addRow(new Object[]{ new String[]{ "barney", "rubble" } });
        mock.reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("z.csv", new String[]{ "fred", "flintstone" }, "x").addRow("z.csv", new String[]{ "barney", "rubble" }, "x").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(mock.scanner.output()));
        }
        mock.scanner.close();
    }

    @Test
    public void testWildcard() {
        TestColumnsArray.MockScanner mock = buildScanner(RowSetTestUtils.projectAll());
        // Verify empty batch.
        TupleMetadata expectedSchema = new SchemaBuilder().addArray("columns", VARCHAR).buildSchema();
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).build();
            Assert.assertNotNull(mock.scanner.output());
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(mock.scanner.output()));
        }
        // Create a batch of data.
        mock.reader.startBatch();
        mock.loader.writer().addRow(new Object[]{ new String[]{ "fred", "flintstone" } }).addRow(new Object[]{ new String[]{ "barney", "rubble" } });
        mock.reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addSingleCol(new String[]{ "fred", "flintstone" }).addSingleCol(new String[]{ "barney", "rubble" }).build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(mock.scanner.output()));
        }
        mock.scanner.close();
    }

    @Test
    public void testWildcardAndFileMetadata() {
        TestColumnsArray.MockScanner mock = buildScanner(RowSetTestUtils.projectList(ScanTestUtils.FILE_NAME_COL, DYNAMIC_STAR, ScanTestUtils.partitionColName(0)));
        // Verify empty batch.
        TupleMetadata expectedSchema = new SchemaBuilder().add("filename", VARCHAR).addArray("columns", VARCHAR).addNullable("dir0", VARCHAR).buildSchema();
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).build();
            Assert.assertNotNull(mock.scanner.output());
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(mock.scanner.output()));
        }
        // Create a batch of data.
        mock.reader.startBatch();
        mock.loader.writer().addRow(new Object[]{ new String[]{ "fred", "flintstone" } }).addRow(new Object[]{ new String[]{ "barney", "rubble" } });
        mock.reader.endBatch();
        // Verify
        {
            RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("z.csv", new String[]{ "fred", "flintstone" }, "x").addRow("z.csv", new String[]{ "barney", "rubble" }, "x").build();
            RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(mock.scanner.output()));
        }
        mock.scanner.close();
    }

    /**
     * Test attempting to use the columns array with an early schema with
     * column types not compatible with a varchar array.
     */
    @Test
    public void testMissingColumnsColumn() {
        ScanSchemaOrchestrator scanner = buildScan(RowSetTestUtils.projectList(COLUMNS_COL));
        TupleMetadata tableSchema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        try {
            ReaderSchemaOrchestrator reader = scanner.startReader();
            reader.makeTableLoader(tableSchema);
            reader.defineSchema();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        scanner.close();
    }

    @Test
    public void testNotRepeated() {
        ScanSchemaOrchestrator scanner = buildScan(RowSetTestUtils.projectList(COLUMNS_COL));
        TupleMetadata tableSchema = new SchemaBuilder().add(COLUMNS_COL, VARCHAR).buildSchema();
        try {
            ReaderSchemaOrchestrator reader = scanner.startReader();
            reader.makeTableLoader(tableSchema);
            reader.defineSchema();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        scanner.close();
    }
}

