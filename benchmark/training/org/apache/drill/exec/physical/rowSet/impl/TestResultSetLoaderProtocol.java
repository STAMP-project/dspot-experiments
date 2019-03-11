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
package org.apache.drill.exec.physical.rowSet.impl;


import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import MinorType.INT;
import MinorType.VARCHAR;
import ResultSetLoader.DEFAULT_ROW_COUNT;
import ResultSetLoaderImpl.ResultSetOptions;
import ValueVector.MAX_BUFFER_SIZE;
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleWriter.UndefinedColumnException;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.apache.drill.test.rowSet.test.TestColumnConverter.TestConverter.factory;


/**
 * Tests of the overall result set loader protocol focusing on which operations
 * are valid in each state, basics of column lookup, basics of adding columns
 * and so on. Uses the simplest possible type: a required int.
 * <p>
 * Run this test first to do a sanity check of the result set loader after making
 * changes.
 * <p>
 * You will find that the result set loader creates a very complex tree of
 * objects that can be quite hard to understand and debug. Please read the
 * material in the various subsystems to see how the classes fit together
 * to implement Drill's rich JSON-like data model.
 * <p>
 * To aid in debugging, you can also dump the result set loader, and all its
 * child objects as follows:<pre><code>
 * ((ResultSetLoaderImpl) rsLoader).dump(new HierarchicalPrinter());
 * </code></pre>
 * Simply insert that line into these tests anywhere you want to visualize
 * the structure. The object tree will show all the components and their
 * current state.
 */
@Category(RowSetTests.class)
public class TestResultSetLoaderProtocol extends SubOperatorTest {
    @Test
    public void testBasics() {
        ResultSetLoaderImpl rsLoaderImpl = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        ResultSetLoader rsLoader = rsLoaderImpl;
        Assert.assertEquals(0, rsLoader.schemaVersion());
        Assert.assertEquals(DEFAULT_ROW_COUNT, rsLoader.targetRowCount());
        Assert.assertEquals(MAX_BUFFER_SIZE, rsLoader.targetVectorSize());
        Assert.assertEquals(0, rsLoader.writer().rowCount());
        Assert.assertEquals(0, rsLoader.batchCount());
        Assert.assertEquals(0, rsLoader.totalRowCount());
        Assert.assertTrue(rsLoader.isProjectionEmpty());
        // Failures due to wrong state (Start)
        try {
            rsLoader.harvest();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        // Can define schema before starting the first batch.
        RowSetLoader rootWriter = rsLoader.writer();
        TupleMetadata schema = rootWriter.tupleSchema();
        Assert.assertEquals(0, schema.size());
        MaterializedField fieldA = SchemaBuilder.columnSchema("a", INT, REQUIRED);
        rootWriter.addColumn(fieldA);
        Assert.assertFalse(rsLoader.isProjectionEmpty());
        Assert.assertEquals(1, schema.size());
        Assert.assertTrue(fieldA.isEquivalent(schema.column(0)));
        Assert.assertSame(schema.metadata(0), schema.metadata("a"));
        // Error to start a row before the first batch.
        try {
            rootWriter.start();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        // Error to end a row before the first batch.
        try {
            rootWriter.save();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        // Because writing is an inner loop; no checks are
        // done to ensure that writing occurs only in the proper
        // state. So, can't test setInt() in the wrong state.
        rsLoader.startBatch();
        try {
            rsLoader.startBatch();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        Assert.assertFalse(rootWriter.isFull());
        rootWriter.start();
        rootWriter.scalar(0).setInt(100);
        Assert.assertEquals(0, rootWriter.rowCount());
        Assert.assertEquals(0, rsLoader.batchCount());
        rootWriter.save();
        Assert.assertEquals(1, rootWriter.rowCount());
        Assert.assertEquals(1, rsLoader.batchCount());
        Assert.assertEquals(1, rsLoader.totalRowCount());
        // Can add a field after first row, prior rows are
        // "back-filled".
        MaterializedField fieldB = SchemaBuilder.columnSchema("b", INT, OPTIONAL);
        rootWriter.addColumn(fieldB);
        Assert.assertEquals(2, schema.size());
        Assert.assertTrue(fieldB.isEquivalent(schema.column(1)));
        Assert.assertSame(schema.metadata(1), schema.metadata("b"));
        rootWriter.start();
        rootWriter.scalar(0).setInt(200);
        rootWriter.scalar(1).setInt(210);
        rootWriter.save();
        Assert.assertEquals(2, rootWriter.rowCount());
        Assert.assertEquals(1, rsLoader.batchCount());
        Assert.assertEquals(2, rsLoader.totalRowCount());
        // Harvest the first batch. Version number is the number
        // of columns added.
        Assert.assertFalse(rootWriter.isFull());
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(2, rsLoader.schemaVersion());
        Assert.assertEquals(0, rootWriter.rowCount());
        Assert.assertEquals(1, rsLoader.batchCount());
        Assert.assertEquals(2, rsLoader.totalRowCount());
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(result.batchSchema()).addRow(100, null).addRow(200, 210).build();
        RowSetUtilities.verify(expected, result);
        // Between batches: batch-based operations fail
        try {
            rootWriter.start();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        try {
            rsLoader.harvest();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        try {
            rootWriter.save();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        // Create a second batch
        rsLoader.startBatch();
        Assert.assertEquals(0, rootWriter.rowCount());
        Assert.assertEquals(1, rsLoader.batchCount());
        Assert.assertEquals(2, rsLoader.totalRowCount());
        rootWriter.start();
        rootWriter.scalar(0).setInt(300);
        rootWriter.scalar(1).setInt(310);
        rootWriter.save();
        Assert.assertEquals(1, rootWriter.rowCount());
        Assert.assertEquals(2, rsLoader.batchCount());
        Assert.assertEquals(3, rsLoader.totalRowCount());
        rootWriter.start();
        rootWriter.scalar(0).setInt(400);
        rootWriter.scalar(1).setInt(410);
        rootWriter.save();
        // Harvest. Schema has not changed.
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(2, rsLoader.schemaVersion());
        Assert.assertEquals(0, rootWriter.rowCount());
        Assert.assertEquals(2, rsLoader.batchCount());
        Assert.assertEquals(4, rsLoader.totalRowCount());
        expected = SubOperatorTest.fixture.rowSetBuilder(result.batchSchema()).addRow(300, 310).addRow(400, 410).build();
        RowSetUtilities.verify(expected, result);
        // Next batch. Schema has changed.
        rsLoader.startBatch();
        rootWriter.start();
        rootWriter.scalar(0).setInt(500);
        rootWriter.scalar(1).setInt(510);
        rootWriter.addColumn(SchemaBuilder.columnSchema("c", INT, OPTIONAL));
        rootWriter.scalar(2).setInt(520);
        rootWriter.save();
        rootWriter.start();
        rootWriter.scalar(0).setInt(600);
        rootWriter.scalar(1).setInt(610);
        rootWriter.scalar(2).setInt(620);
        rootWriter.save();
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(3, rsLoader.schemaVersion());
        expected = SubOperatorTest.fixture.rowSetBuilder(result.batchSchema()).addRow(500, 510, 520).addRow(600, 610, 620).build();
        RowSetUtilities.verify(expected, result);
        rsLoader.close();
        // Key operations fail after close.
        try {
            rootWriter.start();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        try {
            rsLoader.writer();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        try {
            rsLoader.startBatch();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        try {
            rsLoader.harvest();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        try {
            rootWriter.save();
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected
        }
        // Benign to close twice
        rsLoader.close();
    }

    /**
     * Schemas are case insensitive by default. Verify that
     * the schema mechanism works, with emphasis on the
     * case insensitive case.
     * <p>
     * The tests here and elsewhere build columns from a
     * <tt>MaterializedField</tt>. Doing so is rather old-school;
     * better to use the newer <tt>ColumnMetadata</tt> which provides
     * additional information. The code here simply uses the <tt>MaterializedField</tt>
     * to create a <tt>ColumnMetadata</tt> implicitly.
     */
    @Test
    public void testCaseInsensitiveSchema() {
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        RowSetLoader rootWriter = rsLoader.writer();
        TupleMetadata schema = rootWriter.tupleSchema();
        Assert.assertEquals(0, rsLoader.schemaVersion());
        // No columns defined in schema
        Assert.assertNull(schema.metadata("a"));
        try {
            schema.column(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // No columns defined in writer
        try {
            rootWriter.column("a");
            Assert.fail();
        } catch (UndefinedColumnException e) {
            // Expected
        }
        try {
            rootWriter.column(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // Define a column
        Assert.assertEquals(0, rsLoader.schemaVersion());
        MaterializedField colSchema = SchemaBuilder.columnSchema("a", VARCHAR, REQUIRED);
        rootWriter.addColumn(colSchema);
        Assert.assertEquals(1, rsLoader.schemaVersion());
        // Can now be found, case insensitive
        Assert.assertTrue(colSchema.isEquivalent(schema.column(0)));
        ColumnMetadata colMetadata = schema.metadata(0);
        Assert.assertSame(colMetadata, schema.metadata("a"));
        Assert.assertSame(colMetadata, schema.metadata("A"));
        Assert.assertNotNull(rootWriter.column(0));
        Assert.assertNotNull(rootWriter.column("a"));
        Assert.assertNotNull(rootWriter.column("A"));
        Assert.assertEquals(1, schema.size());
        Assert.assertEquals(0, schema.index("a"));
        Assert.assertEquals(0, schema.index("A"));
        // Reject a duplicate name, case insensitive
        try {
            rootWriter.addColumn(colSchema);
            Assert.fail();
        } catch (UserException e) {
            // Expected
        }
        try {
            MaterializedField testCol = SchemaBuilder.columnSchema("A", VARCHAR, REQUIRED);
            rootWriter.addColumn(testCol);
            Assert.fail();
        } catch (UserException e) {
            // Expected
            Assert.assertTrue(e.getMessage().contains("Duplicate"));
        }
        // Can still add required fields while writing the first row.
        rsLoader.startBatch();
        rootWriter.start();
        rootWriter.scalar(0).setString("foo");
        MaterializedField col2 = SchemaBuilder.columnSchema("b", VARCHAR, REQUIRED);
        rootWriter.addColumn(col2);
        Assert.assertEquals(2, rsLoader.schemaVersion());
        Assert.assertTrue(col2.isEquivalent(schema.column(1)));
        ColumnMetadata col2Metadata = schema.metadata(1);
        Assert.assertSame(col2Metadata, schema.metadata("b"));
        Assert.assertSame(col2Metadata, schema.metadata("B"));
        Assert.assertEquals(2, schema.size());
        Assert.assertEquals(1, schema.index("b"));
        Assert.assertEquals(1, schema.index("B"));
        rootWriter.scalar(1).setString("second");
        // After first row, can add an optional or repeated.
        // Also allows a required field: values will be back-filled.
        rootWriter.save();
        rootWriter.start();
        rootWriter.scalar(0).setString("bar");
        rootWriter.scalar(1).setString("");
        MaterializedField col3 = SchemaBuilder.columnSchema("c", VARCHAR, REQUIRED);
        rootWriter.addColumn(col3);
        Assert.assertEquals(3, rsLoader.schemaVersion());
        Assert.assertTrue(col3.isEquivalent(schema.column(2)));
        ColumnMetadata col3Metadata = schema.metadata(2);
        Assert.assertSame(col3Metadata, schema.metadata("c"));
        Assert.assertSame(col3Metadata, schema.metadata("C"));
        Assert.assertEquals(3, schema.size());
        Assert.assertEquals(2, schema.index("c"));
        Assert.assertEquals(2, schema.index("C"));
        rootWriter.scalar("c").setString("c.2");
        MaterializedField col4 = SchemaBuilder.columnSchema("d", VARCHAR, OPTIONAL);
        rootWriter.addColumn(col4);
        Assert.assertEquals(4, rsLoader.schemaVersion());
        Assert.assertTrue(col4.isEquivalent(schema.column(3)));
        ColumnMetadata col4Metadata = schema.metadata(3);
        Assert.assertSame(col4Metadata, schema.metadata("d"));
        Assert.assertSame(col4Metadata, schema.metadata("D"));
        Assert.assertEquals(4, schema.size());
        Assert.assertEquals(3, schema.index("d"));
        Assert.assertEquals(3, schema.index("D"));
        rootWriter.scalar("d").setString("d.2");
        MaterializedField col5 = SchemaBuilder.columnSchema("e", VARCHAR, REPEATED);
        rootWriter.addColumn(col5);
        Assert.assertEquals(5, rsLoader.schemaVersion());
        Assert.assertTrue(col5.isEquivalent(schema.column(4)));
        ColumnMetadata col5Metadata = schema.metadata(4);
        Assert.assertSame(col5Metadata, schema.metadata("e"));
        Assert.assertSame(col5Metadata, schema.metadata("E"));
        Assert.assertEquals(5, schema.size());
        Assert.assertEquals(4, schema.index("e"));
        Assert.assertEquals(4, schema.index("E"));
        rootWriter.array(4).setObject(RowSetUtilities.strArray("e1", "e2", "e3"));
        rootWriter.save();
        // Verify. No reason to expect problems, but might as well check.
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(5, rsLoader.schemaVersion());
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(result.batchSchema()).addRow("foo", "second", "", null, RowSetUtilities.strArray()).addRow("bar", "", "c.2", "d.2", RowSetUtilities.strArray("e1", "e2", "e3")).build();
        RowSetUtilities.verify(expected, result);
        // Handy way to test that close works to abort an in-flight batch
        // and clean up.
        rsLoader.close();
    }

    /**
     * Provide a schema up front to the loader; schema is built before
     * the first row.
     * <p>
     * Also verifies the test-time method to set a row of values using
     * a single method.
     */
    @Test
    public void testInitialSchema() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addNullable("b", INT).add("c", VARCHAR).buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        rootWriter.addRow(10, 100, "fred").addRow(20, null, "barney").addRow(30, 300, "wilma");
        RowSet actual = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        RowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, 100, "fred").addRow(20, null, "barney").addRow(30, 300, "wilma").build();
        RowSetUtilities.verify(expected, actual);
        rsLoader.close();
    }

    /**
     * The writer protocol allows a client to write to a row any number of times
     * before invoking <tt>save()</tt>. In this case, each new value simply
     * overwrites the previous value. Here, we test the most basic case: a simple,
     * flat tuple with no arrays. We use a very large Varchar that would, if
     * overwrite were not working, cause vector overflow.
     * <p>
     * The ability to overwrite rows is seldom needed except in one future use
     * case: writing a row, then applying a filter "in-place" to discard unwanted
     * rows, without having to send the row downstream.
     * <p>
     * Because of this use case, specific rules apply when discarding row or
     * overwriting values.
     * <ul>
     * <li>Values can be written once per row. Fixed-width columns actually allow
     * multiple writes. But, because of the way variable-width columns work,
     * multiple writes will cause undefined results.</li>
     * <li>To overwrite a row, call <tt>start()</tt> without calling
     * <tt>save()</tt> on the previous row. Doing so ignores data for the
     * previous row and starts a new row in place of the old one.</li>
     * </ul>
     * Note that there is no explicit method to discard a row. Instead,
     * the rule is that a row is not saved until <tt>save()</tt> is called.
     */
    @Test
    public void testOverwriteRow() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Can't use the shortcut to populate rows when doing overwrites.
        ScalarWriter aWriter = rootWriter.scalar("a");
        ScalarWriter bWriter = rootWriter.scalar("b");
        // Write 100,000 rows, overwriting 99% of them. This will cause vector
        // overflow and data corruption if overwrite does not work; but will happily
        // produce the correct result if everything works as it should.
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        rsLoader.startBatch();
        while (count < 100000) {
            rootWriter.start();
            count++;
            aWriter.setInt(count);
            bWriter.setBytes(value, value.length);
            if ((count % 100) == 0) {
                rootWriter.save();
            }
        } 
        // Verify using a reader.
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals((count / 100), result.rowCount());
        RowSetReader reader = result.reader();
        int rowId = 1;
        while (reader.next()) {
            Assert.assertEquals((rowId * 100), scalar("a").getInt());
            Assert.assertTrue(Arrays.equals(value, scalar("b").getBytes()));
            rowId++;
        } 
        result.clear();
        rsLoader.close();
    }

    /**
     * Test that memory is released if the loader is closed with an active
     * batch (that is, before the batch is harvested.)
     */
    @Test
    public void testCloseWithoutHarvest() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        for (int i = 0; i < 100; i++) {
            rootWriter.start();
            rootWriter.scalar("a").setInt(i);
            rootWriter.scalar("b").setString(("b-" + i));
            rootWriter.save();
        }
        // Don't harvest the batch. Allocator will complain if the
        // loader does not release memory.
        rsLoader.close();
    }

    /**
     * Test the use of a column type converter in the result set loader for
     * required, nullable and repeated columns.
     */
    @Test
    public void testTypeConversion() {
        TupleMetadata schema = new SchemaBuilder().add("n1", INT).addNullable("n2", INT).addArray("n3", INT).buildSchema();
        // Add a type converter. Passed in as a factory
        // since we must create a new one for each row set writer.
        schema.metadata("n1").setTypeConverter(factory());
        schema.metadata("n2").setTypeConverter(factory());
        schema.metadata("n3").setTypeConverter(factory());
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        rsLoader.startBatch();
        // Write data as both a string as an integer
        RowSetLoader rootWriter = rsLoader.writer();
        rootWriter.addRow("123", "12", RowSetUtilities.strArray("123", "124"));
        rootWriter.addRow(234, 23, RowSetUtilities.intArray(234, 235));
        RowSet actual = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        // Build the expected vector without a type converter.
        TupleMetadata expectedSchema = new SchemaBuilder().add("n1", INT).addNullable("n2", INT).addArray("n3", INT).buildSchema();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(123, 12, RowSetUtilities.intArray(123, 124)).addRow(234, 23, RowSetUtilities.intArray(234, 235)).build();
        // Compare
        RowSetUtilities.verify(expected, actual);
    }
}

