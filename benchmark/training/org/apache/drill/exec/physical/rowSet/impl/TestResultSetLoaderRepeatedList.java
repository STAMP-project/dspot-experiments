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


import MinorType.INT;
import MinorType.NULL;
import MinorType.VARCHAR;
import ObjectType.ARRAY;
import ObjectType.SCALAR;
import ResultSetLoaderImpl.ResultSetOptions;
import ValueType.STRING;
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ObjectWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.writer.RepeatedListWriter;
import org.apache.drill.shaded.guava.com.google.common.base.Charsets;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests repeated list support. Repeated lists add another layer of dimensionality
 * on top of other repeated types. Since a repeated list can wrap another repeated
 * list, the repeated list allows creating 2D, 3D or higher dimensional arrays (lists,
 * actually, since the different "slices" need not have the same length...)
 * Repeated lists appear to be used only by JSON.
 */
// TODO: Test union list as inner
// TODO: Test repeated map as inner
@Category(RowSetTests.class)
public class TestResultSetLoaderRepeatedList extends SubOperatorTest {
    @Test
    public void test2DEarlySchema() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        do2DTest(schema, rsLoader);
        rsLoader.close();
    }

    @Test
    public void test2DLateSchema() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        final RowSetLoader writer = rsLoader.writer();
        // Add columns dynamically
        writer.addColumn(schema.metadata(0));
        writer.addColumn(schema.metadata(1).cloneEmpty());
        // Yes, this is ugly. The whole repeated array idea is awkward.
        // The only place it is used at present is in JSON where the
        // awkwardness is mixed in with a logs of JSON complexity.
        // Consider improving this API in the future.
        ((RepeatedListWriter) (writer.array(1))).defineElement(schema.metadata(1).childSchema());
        do2DTest(schema, rsLoader);
        rsLoader.close();
    }

    @Test
    public void test2DLateSchemaIncremental() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        final RowSetLoader writer = rsLoader.writer();
        // Add columns dynamically
        writer.addColumn(schema.metadata(0));
        // Write a row without the array.
        rsLoader.startBatch();
        writer.addRow(1);
        // Add the repeated list, but without contents.
        writer.addColumn(schema.metadata(1).cloneEmpty());
        // Sanity check of writer structure
        Assert.assertEquals(2, writer.size());
        final ObjectWriter listObj = writer.column("list2");
        Assert.assertEquals(ARRAY, listObj.type());
        final ArrayWriter listWriter = listObj.array();
        // No child defined yet. A dummy child is inserted instead.
        Assert.assertEquals(NULL, listWriter.entry().schema().type());
        Assert.assertEquals(ARRAY, listWriter.entryType());
        Assert.assertEquals(SCALAR, listWriter.array().entryType());
        Assert.assertEquals(ValueType.NULL, listWriter.array().scalar().valueType());
        // Although we don't know the type of the inner, we can still
        // create null (empty) elements in the outer array.
        writer.addRow(2, null).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.objArray(), null));
        // Define the inner type.
        final RepeatedListWriter listWriterImpl = ((RepeatedListWriter) (listWriter));
        listWriterImpl.defineElement(MaterializedField.create("list2", Types.repeated(VARCHAR)));
        // Sanity check of completed structure
        Assert.assertEquals(ARRAY, listWriter.entryType());
        final ArrayWriter innerWriter = listWriter.array();
        Assert.assertEquals(SCALAR, innerWriter.entryType());
        final ScalarWriter strWriter = innerWriter.scalar();
        Assert.assertEquals(STRING, strWriter.valueType());
        // Write values
        writer.addRow(5, RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c", "d")));
        // Verify the values.
        // (Relies on the row set level repeated list tests having passed.)
        final RowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.objArray()).addRow(2, RowSetUtilities.objArray()).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.objArray(), null)).addRow(5, RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c", "d"))).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
        rsLoader.close();
    }

    @Test
    public void test2DOverflow() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        final RowSetLoader writer = rsLoader.writer();
        // Fill the batch with enough data to cause overflow.
        // Data must be large enough to cause overflow before 64K rows
        // Make a bit bigger to overflow early.
        final int outerSize = 7;
        final int innerSize = 5;
        final int strLength = ((((ValueVector.MAX_BUFFER_SIZE) / (ValueVector.MAX_ROW_COUNT)) / outerSize) / innerSize) + 20;
        final byte[] value = new byte[strLength - 6];
        Arrays.fill(value, ((byte) ('X')));
        final String strValue = new String(value, Charsets.UTF_8);
        int rowCount = 0;
        int elementCount = 0;
        final ArrayWriter outerWriter = writer.array(1);
        final ArrayWriter innerWriter = outerWriter.array();
        final ScalarWriter elementWriter = innerWriter.scalar();
        rsLoader.startBatch();
        while (!(writer.isFull())) {
            writer.start();
            writer.scalar(0).setInt(rowCount);
            for (int j = 0; j < outerSize; j++) {
                for (int k = 0; k < innerSize; k++) {
                    elementWriter.setString(String.format("%s%06d", strValue, elementCount));
                    elementCount++;
                }
                outerWriter.save();
            }
            writer.save();
            rowCount++;
        } 
        // Number of rows should be driven by vector size.
        // Our row count should include the overflow row
        final int expectedCount = (ValueVector.MAX_BUFFER_SIZE) / ((strLength * innerSize) * outerSize);
        Assert.assertEquals((expectedCount + 1), rowCount);
        // Loader's row count should include only "visible" rows
        Assert.assertEquals(expectedCount, writer.rowCount());
        // Total count should include invisible and look-ahead rows.
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        // Result should exclude the overflow row
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(expectedCount, result.rowCount());
        // Verify the data.
        RowSetReader reader = result.reader();
        ArrayReader outerReader = array(1);
        ArrayReader innerReader = outerReader.array();
        ScalarReader strReader = innerReader.scalar();
        int readRowCount = 0;
        int readElementCount = 0;
        while (reader.next()) {
            Assert.assertEquals(readRowCount, scalar(0).getInt());
            for (int i = 0; i < outerSize; i++) {
                Assert.assertTrue(outerReader.next());
                for (int j = 0; j < innerSize; j++) {
                    Assert.assertTrue(innerReader.next());
                    Assert.assertEquals(String.format("%s%06d", strValue, readElementCount), strReader.getString());
                    readElementCount++;
                }
                Assert.assertFalse(innerReader.next());
            }
            Assert.assertFalse(outerReader.next());
            readRowCount++;
        } 
        Assert.assertEquals(readRowCount, result.rowCount());
        result.clear();
        // Write a few more rows to verify the overflow row.
        rsLoader.startBatch();
        for (int i = 0; i < 1000; i++) {
            writer.start();
            writer.scalar(0).setInt(rowCount);
            for (int j = 0; j < outerSize; j++) {
                for (int k = 0; k < innerSize; k++) {
                    elementWriter.setString(String.format("%s%06d", strValue, elementCount));
                    elementCount++;
                }
                outerWriter.save();
            }
            writer.save();
            rowCount++;
        }
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(1001, result.rowCount());
        final int startCount = readRowCount;
        reader = result.reader();
        outerReader = reader.array(1);
        innerReader = outerReader.array();
        strReader = innerReader.scalar();
        while (reader.next()) {
            Assert.assertEquals(readRowCount, scalar(0).getInt());
            for (int i = 0; i < outerSize; i++) {
                Assert.assertTrue(outerReader.next());
                for (int j = 0; j < innerSize; j++) {
                    Assert.assertTrue(innerReader.next());
                    elementWriter.setString(String.format("%s%06d", strValue, readElementCount));
                    Assert.assertEquals(String.format("%s%06d", strValue, readElementCount), strReader.getString());
                    readElementCount++;
                }
                Assert.assertFalse(innerReader.next());
            }
            Assert.assertFalse(outerReader.next());
            readRowCount++;
        } 
        Assert.assertEquals((readRowCount - startCount), result.rowCount());
        result.clear();
        rsLoader.close();
    }

    // Adapted from TestRepeatedListAccessors.testSchema3DWriterReader
    // That test exercises the low-level schema and writer mechanisms.
    // Here we simply ensure that the 3D case continues to work when
    // wrapped in the Result Set Loader
    @Test
    public void test3DEarlySchema() {
        final TupleMetadata schema = // Uses a short-hand method to avoid mucking with actual
        // nested lists.
        new SchemaBuilder().add("id", INT).addArray("cube", VARCHAR, 3).buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        rsLoader.startBatch();
        final RowSetLoader writer = rsLoader.writer();
        writer.addRow(1, RowSetUtilities.objArray(RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c")), RowSetUtilities.objArray(RowSetUtilities.strArray("d", "e", "f"), null), null, RowSetUtilities.objArray())).addRow(2, null).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.objArray())).addRow(5, RowSetUtilities.singleObjArray(RowSetUtilities.objArray(RowSetUtilities.strArray("g", "h"), RowSetUtilities.strArray("i"))));
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.objArray(RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c")), RowSetUtilities.objArray(RowSetUtilities.strArray("d", "e", "f"), RowSetUtilities.strArray()), RowSetUtilities.objArray(), RowSetUtilities.objArray())).addRow(2, RowSetUtilities.objArray()).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.objArray())).addRow(5, RowSetUtilities.singleObjArray(RowSetUtilities.objArray(RowSetUtilities.strArray("g", "h"), RowSetUtilities.strArray("i")))).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
    }
}

