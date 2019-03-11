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
package org.apache.drill.test.rowSet.test;


import MinorType.INT;
import MinorType.VARCHAR;
import ObjectType.ARRAY;
import ObjectType.SCALAR;
import ObjectType.TUPLE;
import ValueType.INTEGER;
import ValueVector.MAX_ROW_COUNT;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleReader;
import org.apache.drill.exec.vector.accessor.TupleWriter;
import org.apache.drill.exec.vector.complex.MapVector;
import org.apache.drill.exec.vector.complex.RepeatedMapVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.DirectRowSet;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.apache.drill.test.rowSet.RowSetWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test row sets. Since row sets are a thin wrapper around vectors,
 * readers and writers, this is also a test of those constructs.
 * <p>
 * Tests basic protocol of the writers: <pre><code>
 * row : tuple
 * tuple : column *
 * column : scalar obj | array obj | tuple obj | variant obj
 * scalar obj : scalar
 * array obj : array
 * array : index --> column
 * element : column
 * tuple obj : tuple
 * tuple : name --> column (also index --> column)
 * variant obj : variant
 * variant : type --> column</code></pre>
 * <p>
 * A list is an array of variants. Variants are tested elsewhere.
 */
@Category(RowSetTests.class)
public class TestRowSet extends SubOperatorTest {
    private static final Logger logger = LoggerFactory.getLogger(TestRowSet.class);

    /**
     * Test the simplest constructs: a row with top-level scalar
     * columns.
     * <p>
     * The focus here is the structure of the readers and writers, along
     * with the row set loader and verifier that use those constructs.
     * That is, while this test uses the int vector, this test is not
     * focused on that vector.
     */
    @Test
    public void testScalarStructure() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        // Required Int
        // Verify the invariants of the "full" and "simple" access paths
        Assert.assertEquals(SCALAR, column("a").type());
        Assert.assertSame(column("a"), writer.column(0));
        Assert.assertSame(scalar("a"), writer.scalar(0));
        Assert.assertSame(column("a").scalar(), scalar("a"));
        Assert.assertSame(writer.column(0).scalar(), writer.scalar(0));
        Assert.assertEquals(INTEGER, writer.scalar(0).valueType());
        // Sanity checks
        try {
            writer.column(0).array();
            Assert.fail();
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
        try {
            writer.column(0).tuple();
            Assert.fail();
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
        // Test the various ways to get at the scalar writer.
        column("a").scalar().setInt(10);
        writer.save();
        scalar("a").setInt(20);
        writer.save();
        writer.column(0).scalar().setInt(30);
        writer.save();
        writer.scalar(0).setInt(40);
        writer.save();
        // Finish the row set and get a reader.
        final RowSet.SingleRowSet actual = writer.done();
        final RowSetReader reader = actual.reader();
        // Verify invariants
        Assert.assertEquals(SCALAR, reader.column(0).type());
        Assert.assertSame(column("a"), reader.column(0));
        Assert.assertSame(scalar("a"), reader.scalar(0));
        Assert.assertSame(column("a").scalar(), scalar("a"));
        Assert.assertSame(reader.column(0).scalar(), reader.scalar(0));
        Assert.assertEquals(INTEGER, reader.scalar(0).valueType());
        Assert.assertTrue(schema.metadata("a").isEquivalent(column("a").schema()));
        // Test various accessors: full and simple
        Assert.assertTrue(reader.next());
        Assert.assertFalse(column("a").scalar().isNull());
        Assert.assertEquals(10, column("a").scalar().getInt());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(scalar("a").isNull());
        Assert.assertEquals(20, scalar("a").getInt());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(reader.column(0).scalar().isNull());
        Assert.assertEquals(30, reader.column(0).scalar().getInt());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(reader.column(0).scalar().isNull());
        Assert.assertEquals(40, reader.scalar(0).getInt());
        Assert.assertFalse(reader.next());
        // Test the above again via the writer and reader
        // utility classes.
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10).addRow(20).addRow(30).addRow(40).build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * Test a record with a top level array. The focus here is on the
     * scalar array structure.
     *
     * @throws VectorOverflowException
     * 		should never occur
     */
    @Test
    public void testScalarArrayStructure() {
        final TupleMetadata schema = new SchemaBuilder().addArray("a", INT).buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        // Repeated Int
        // Verify the invariants of the "full" and "simple" access paths
        Assert.assertEquals(ARRAY, column("a").type());
        Assert.assertSame(column("a"), writer.column(0));
        Assert.assertSame(writer.array("a"), writer.array(0));
        Assert.assertSame(column("a").array(), writer.array("a"));
        Assert.assertSame(writer.column(0).array(), writer.array(0));
        Assert.assertEquals(SCALAR, column("a").array().entry().type());
        Assert.assertEquals(SCALAR, column("a").array().entryType());
        Assert.assertSame(writer.array(0).entry().scalar(), writer.array(0).scalar());
        Assert.assertEquals(INTEGER, writer.array(0).scalar().valueType());
        // Sanity checks
        try {
            writer.column(0).scalar();
            Assert.fail();
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
        try {
            writer.column(0).tuple();
            Assert.fail();
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
        // Write some data
        final ScalarWriter intWriter = writer.array("a").scalar();
        intWriter.setInt(10);
        intWriter.setInt(11);
        writer.save();
        intWriter.setInt(20);
        intWriter.setInt(21);
        intWriter.setInt(22);
        writer.save();
        intWriter.setInt(30);
        writer.save();
        intWriter.setInt(40);
        intWriter.setInt(41);
        writer.save();
        // Finish the row set and get a reader.
        final RowSet.SingleRowSet actual = writer.done();
        final RowSetReader reader = actual.reader();
        // Verify the invariants of the "full" and "simple" access paths
        Assert.assertEquals(ARRAY, column("a").type());
        Assert.assertSame(column("a"), reader.column(0));
        Assert.assertSame(reader.array("a"), reader.array(0));
        Assert.assertSame(column("a").array(), reader.array("a"));
        Assert.assertSame(reader.column(0).array(), reader.array(0));
        Assert.assertEquals(SCALAR, column("a").array().entryType());
        Assert.assertEquals(INTEGER, reader.array(0).scalar().valueType());
        // Read and verify the rows
        final ArrayReader arrayReader = reader.array(0);
        final ScalarReader intReader = arrayReader.scalar();
        Assert.assertTrue(reader.next());
        Assert.assertFalse(arrayReader.isNull());
        Assert.assertEquals(2, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(10, intReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(11, intReader.getInt());
        Assert.assertFalse(arrayReader.next());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(arrayReader.isNull());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(20, intReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(21, intReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(22, intReader.getInt());
        Assert.assertFalse(arrayReader.next());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(arrayReader.isNull());
        Assert.assertEquals(1, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(30, intReader.getInt());
        Assert.assertFalse(arrayReader.next());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(arrayReader.isNull());
        Assert.assertEquals(2, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(40, intReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(41, intReader.getInt());
        Assert.assertFalse(arrayReader.next());
        Assert.assertFalse(reader.next());
        // Test the above again via the writer and reader
        // utility classes.
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.intArray(10, 11)).addSingleCol(RowSetUtilities.intArray(20, 21, 22)).addSingleCol(RowSetUtilities.intArray(30)).addSingleCol(RowSetUtilities.intArray(40, 41)).build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * Test a simple map structure at the top level of a row.
     *
     * @throws VectorOverflowException
     * 		should never occur
     */
    @Test
    public void testMapStructure() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).addMap("m").addArray("b", INT).resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        // Map and Int
        // Test Invariants
        Assert.assertEquals(SCALAR, column("a").type());
        Assert.assertEquals(SCALAR, writer.column(0).type());
        Assert.assertEquals(TUPLE, column("m").type());
        Assert.assertEquals(TUPLE, writer.column(1).type());
        Assert.assertSame(writer.column(1).tuple(), writer.tuple(1));
        final TupleWriter mapWriter = writer.column(1).tuple();
        Assert.assertEquals(SCALAR, mapWriter.column("b").array().entry().type());
        Assert.assertEquals(SCALAR, mapWriter.column("b").array().entryType());
        final ScalarWriter aWriter = column("a").scalar();
        final ScalarWriter bWriter = column("b").array().entry().scalar();
        Assert.assertSame(bWriter, writer.tuple(1).array(0).scalar());
        Assert.assertEquals(INTEGER, bWriter.valueType());
        // Sanity checks
        try {
            writer.column(1).scalar();
            Assert.fail();
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
        try {
            writer.column(1).array();
            Assert.fail();
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
        // Write data
        aWriter.setInt(10);
        bWriter.setInt(11);
        bWriter.setInt(12);
        writer.save();
        aWriter.setInt(20);
        bWriter.setInt(21);
        bWriter.setInt(22);
        writer.save();
        aWriter.setInt(30);
        bWriter.setInt(31);
        bWriter.setInt(32);
        writer.save();
        // Finish the row set and get a reader.
        final RowSet.SingleRowSet actual = writer.done();
        final RowSetReader reader = actual.reader();
        Assert.assertEquals(SCALAR, column("a").type());
        Assert.assertEquals(SCALAR, reader.column(0).type());
        Assert.assertEquals(TUPLE, column("m").type());
        Assert.assertEquals(TUPLE, reader.column(1).type());
        Assert.assertSame(reader.column(1).tuple(), reader.tuple(1));
        final ScalarReader aReader = reader.column(0).scalar();
        final TupleReader mReader = reader.column(1).tuple();
        final ArrayReader bArray = mReader.column("b").array();
        Assert.assertEquals(SCALAR, bArray.entryType());
        final ScalarReader bReader = bArray.scalar();
        Assert.assertEquals(INTEGER, bReader.valueType());
        // Row 1: (10, {[11, 12]})
        Assert.assertTrue(reader.next());
        Assert.assertEquals(10, aReader.getInt());
        Assert.assertFalse(mReader.isNull());
        Assert.assertTrue(bArray.next());
        Assert.assertFalse(bReader.isNull());
        Assert.assertEquals(11, bReader.getInt());
        Assert.assertTrue(bArray.next());
        Assert.assertFalse(bReader.isNull());
        Assert.assertEquals(12, bReader.getInt());
        Assert.assertFalse(bArray.next());
        // Row 2: (20, {[21, 22]})
        Assert.assertTrue(reader.next());
        Assert.assertEquals(20, aReader.getInt());
        Assert.assertFalse(mReader.isNull());
        Assert.assertTrue(bArray.next());
        Assert.assertEquals(21, bReader.getInt());
        Assert.assertTrue(bArray.next());
        Assert.assertEquals(22, bReader.getInt());
        // Row 3: (30, {[31, 32]})
        Assert.assertTrue(reader.next());
        Assert.assertEquals(30, aReader.getInt());
        Assert.assertFalse(mReader.isNull());
        Assert.assertTrue(bArray.next());
        Assert.assertEquals(31, bReader.getInt());
        Assert.assertTrue(bArray.next());
        Assert.assertEquals(32, bReader.getInt());
        Assert.assertFalse(reader.next());
        // Verify that the map accessor's value count was set.
        final MapVector mapVector = ((MapVector) (actual.container().getValueVector(1).getValueVector()));
        Assert.assertEquals(actual.rowCount(), mapVector.getAccessor().getValueCount());
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.objArray(RowSetUtilities.intArray(11, 12))).addRow(20, RowSetUtilities.objArray(RowSetUtilities.intArray(21, 22))).addRow(30, RowSetUtilities.objArray(RowSetUtilities.intArray(31, 32))).build();
        RowSetUtilities.verify(expected, actual);
    }

    @Test
    public void testRepeatedMapStructure() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m").add("b", INT).add("c", INT).resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        // Map and Int
        // Pick out components and lightly test. (Assumes structure
        // tested earlier is still valid, so no need to exhaustively
        // test again.)
        Assert.assertEquals(SCALAR, column("a").type());
        Assert.assertEquals(ARRAY, column("m").type());
        final ArrayWriter maWriter = writer.column(1).array();
        Assert.assertEquals(TUPLE, maWriter.entryType());
        final TupleWriter mapWriter = maWriter.tuple();
        Assert.assertEquals(SCALAR, mapWriter.column("b").type());
        Assert.assertEquals(SCALAR, mapWriter.column("c").type());
        final ScalarWriter aWriter = column("a").scalar();
        final ScalarWriter bWriter = mapWriter.scalar("b");
        final ScalarWriter cWriter = mapWriter.scalar("c");
        Assert.assertEquals(INTEGER, aWriter.valueType());
        Assert.assertEquals(INTEGER, bWriter.valueType());
        Assert.assertEquals(INTEGER, cWriter.valueType());
        // Write data
        aWriter.setInt(10);
        bWriter.setInt(101);
        cWriter.setInt(102);
        maWriter.save();// Advance to next array position

        bWriter.setInt(111);
        cWriter.setInt(112);
        maWriter.save();
        writer.save();
        aWriter.setInt(20);
        bWriter.setInt(201);
        cWriter.setInt(202);
        maWriter.save();
        bWriter.setInt(211);
        cWriter.setInt(212);
        maWriter.save();
        writer.save();
        aWriter.setInt(30);
        bWriter.setInt(301);
        cWriter.setInt(302);
        maWriter.save();
        bWriter.setInt(311);
        cWriter.setInt(312);
        maWriter.save();
        writer.save();
        // Finish the row set and get a reader.
        final RowSet.SingleRowSet actual = writer.done();
        final RowSetReader reader = actual.reader();
        // Verify reader structure
        Assert.assertEquals(SCALAR, column("a").type());
        Assert.assertEquals(ARRAY, column("m").type());
        final ArrayReader maReader = reader.column(1).array();
        Assert.assertEquals(TUPLE, maReader.entryType());
        final TupleReader mapReader = maReader.tuple();
        Assert.assertEquals(SCALAR, mapReader.column("b").type());
        Assert.assertEquals(SCALAR, mapReader.column("c").type());
        final ScalarReader aReader = column("a").scalar();
        final ScalarReader bReader = mapReader.scalar("b");
        final ScalarReader cReader = mapReader.scalar("c");
        Assert.assertEquals(INTEGER, aReader.valueType());
        Assert.assertEquals(INTEGER, bReader.valueType());
        Assert.assertEquals(INTEGER, cReader.valueType());
        // Row 1: Use iterator-like accessors
        Assert.assertTrue(reader.next());
        Assert.assertEquals(10, aReader.getInt());
        Assert.assertFalse(maReader.isNull());// Array itself is not null

        Assert.assertTrue(maReader.next());
        Assert.assertFalse(mapReader.isNull());// Tuple 0 is not null

        Assert.assertEquals(101, mapReader.scalar(0).getInt());
        Assert.assertEquals(102, mapReader.scalar(1).getInt());
        Assert.assertTrue(maReader.next());
        Assert.assertEquals(111, mapReader.scalar(0).getInt());
        Assert.assertEquals(112, mapReader.scalar(1).getInt());
        // Row 2: use explicit positioning,
        // but access scalars through the map reader.
        Assert.assertTrue(reader.next());
        Assert.assertEquals(20, aReader.getInt());
        maReader.setPosn(0);
        Assert.assertEquals(201, mapReader.scalar(0).getInt());
        Assert.assertEquals(202, mapReader.scalar(1).getInt());
        maReader.setPosn(1);
        Assert.assertEquals(211, mapReader.scalar(0).getInt());
        Assert.assertEquals(212, mapReader.scalar(1).getInt());
        // Row 3: use scalar accessor
        Assert.assertTrue(reader.next());
        Assert.assertEquals(30, aReader.getInt());
        Assert.assertTrue(maReader.next());
        Assert.assertEquals(301, bReader.getInt());
        Assert.assertEquals(302, cReader.getInt());
        Assert.assertTrue(maReader.next());
        Assert.assertEquals(311, bReader.getInt());
        Assert.assertEquals(312, cReader.getInt());
        Assert.assertFalse(reader.next());
        // Verify that the map accessor's value count was set.
        final RepeatedMapVector mapVector = ((RepeatedMapVector) (actual.container().getValueVector(1).getValueVector()));
        Assert.assertEquals(3, mapVector.getAccessor().getValueCount());
        // Verify the readers and writers again using the testing tools.
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.objArray(RowSetUtilities.objArray(101, 102), RowSetUtilities.objArray(111, 112))).addRow(20, RowSetUtilities.objArray(RowSetUtilities.objArray(201, 202), RowSetUtilities.objArray(211, 212))).addRow(30, RowSetUtilities.objArray(RowSetUtilities.objArray(301, 302), RowSetUtilities.objArray(311, 312))).build();
        RowSetUtilities.verify(expected, actual);
    }

    /**
     * Test an array of ints (as an example fixed-width type)
     * at the top level of a schema.
     */
    @Test
    public void testTopFixedWidthArray() {
        final TupleMetadata schema = new SchemaBuilder().add("c", INT).addArray("a", INT).buildSchema();
        final RowSet.ExtendableRowSet rs1 = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rs1.writer();
        writer.scalar(0).setInt(10);
        final ScalarWriter array = writer.array(1).scalar();
        array.setInt(100);
        array.setInt(110);
        writer.save();
        writer.scalar(0).setInt(20);
        array.setInt(200);
        array.setInt(120);
        array.setInt(220);
        writer.save();
        writer.scalar(0).setInt(30);
        writer.save();
        final RowSet.SingleRowSet result = writer.done();
        final RowSetReader reader = result.reader();
        final ArrayReader arrayReader = reader.array(1);
        final ScalarReader elementReader = arrayReader.scalar();
        Assert.assertTrue(reader.next());
        Assert.assertEquals(10, reader.scalar(0).getInt());
        Assert.assertEquals(2, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(100, elementReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(110, elementReader.getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(20, reader.scalar(0).getInt());
        Assert.assertEquals(3, arrayReader.size());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(200, elementReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(120, elementReader.getInt());
        Assert.assertTrue(arrayReader.next());
        Assert.assertEquals(220, elementReader.getInt());
        Assert.assertTrue(reader.next());
        Assert.assertEquals(30, reader.scalar(0).getInt());
        Assert.assertEquals(0, arrayReader.size());
        Assert.assertFalse(reader.next());
        final RowSet.SingleRowSet rs2 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.intArray(100, 110)).addRow(20, RowSetUtilities.intArray(200, 120, 220)).addRow(30, null).build();
        RowSetUtilities.verify(rs1, rs2);
    }

    /**
     * Test filling a row set up to the maximum number of rows.
     * Values are small enough to prevent filling to the
     * maximum buffer size.
     */
    @Test
    public void testRowBounds() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).buildSchema();
        final RowSet.ExtendableRowSet rs = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rs.writer();
        int count = 0;
        while (!(writer.isFull())) {
            writer.scalar(0).setInt((count++));
            writer.save();
        } 
        writer.done();
        Assert.assertEquals(MAX_ROW_COUNT, count);
        // The writer index points past the writable area.
        // But, this is fine, the valid() method says we can't
        // write at this location.
        Assert.assertEquals(MAX_ROW_COUNT, writer.rowIndex());
        Assert.assertEquals(MAX_ROW_COUNT, rs.rowCount());
        rs.clear();
    }

    /**
     * Test filling a row set up to the maximum vector size.
     * Values in the first column are small enough to prevent filling to the
     * maximum buffer size, but values in the second column
     * will reach maximum buffer size before maximum row size.
     * The result should be the number of rows that fit, with the
     * partial last row not counting. (A complete application would
     * reload the partial row into a new row set.)
     */
    @Test
    public void testBufferBounds() {
        final TupleMetadata schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).buildSchema();
        String varCharValue;
        try {
            final byte[] rawValue = new byte[512];
            Arrays.fill(rawValue, ((byte) ('X')));
            varCharValue = new String(rawValue, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        final RowSet.ExtendableRowSet rs = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rs.writer();
        int count = 0;
        try {
            // Test overflow. This is not a typical use case: don't want to
            // hit overflow without overflow handling. In this case, we throw
            // away the last row because the row set abstraction does not
            // implement vector overflow other than throwing an exception.
            for (; ;) {
                writer.scalar(0).setInt(count);
                writer.scalar(1).setString(varCharValue);
                // Won't get here on overflow.
                writer.save();
                count++;
            }
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains("Overflow"));
        }
        writer.done();
        Assert.assertTrue((count < (ValueVector.MAX_ROW_COUNT)));
        Assert.assertEquals(count, writer.rowIndex());
        Assert.assertEquals(count, rs.rowCount());
        rs.clear();
    }

    /**
     * The code below is not a test. Rather, it is a simple example of
     * how to write a batch of data using writers, then read it using
     * readers.
     */
    @Test
    public void example() {
        // Step 1: Define a schema. In a real app, this
        // will be provided by a reader, by an incoming batch,
        // etc.
        final TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR).addArray("b", INT).addMap("c").add("c1", INT).add("c2", VARCHAR).resumeSchema().buildSchema();
        // Step 2: Create a batch. Done here because this is
        // a batch-oriented test. Done automatically in the
        // result set loader.
        final DirectRowSet drs = DirectRowSet.fromSchema(SubOperatorTest.fixture.allocator(), schema);
        // Step 3: Create the writer.
        final RowSetWriter writer = drs.writer();
        // Step 4: Populate data. Here we do it the way an app would:
        // using the individual accessors. See tests above for the many
        // ways this can be done depending on the need of the app.
        // 
        // Write two rows:
        // ("fred", [10, 11], {12, "wilma"})
        // ("barney", [20, 21], {22, "betty"})
        // 
        // This example uses Java strings for Varchar. Real code might
        // use byte arrays.
        scalar("a").setString("fred");
        final ArrayWriter bWriter = array("b");
        bWriter.scalar().setInt(10);
        bWriter.scalar().setInt(11);
        final TupleWriter cWriter = tuple("c");
        cWriter.scalar("c1").setInt(12);
        cWriter.scalar("c2").setString("wilma");
        writer.save();
        scalar("a").setString("barney");
        bWriter.scalar().setInt(20);
        bWriter.scalar().setInt(21);
        cWriter.scalar("c1").setInt(22);
        cWriter.scalar("c2").setString("betty");
        writer.save();
        // Step 5: "Harvest" the batch. Done differently in the
        // result set loader.
        final RowSet.SingleRowSet rowSet = writer.done();
        // Step 5: Create a reader.
        final RowSetReader reader = rowSet.reader();
        // Step 6: Retrieve the data. Here we just print the
        // values.
        while (reader.next()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(print(scalar("a").getString()));
            final ArrayReader bReader = reader.array("b");
            while (bReader.next()) {
                sb.append(print(bReader.scalar().getInt()));
            } 
            final TupleReader cReader = reader.tuple("c");
            sb.append(print(cReader.scalar("c1").getInt()));
            sb.append(print(cReader.scalar("c2").getString()));
            TestRowSet.logger.debug(sb.toString());
        } 
        // Step 7: Free memory.
        rowSet.clear();
    }
}

