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
import MinorType.INT;
import MinorType.VARCHAR;
import ResultSetLoaderImpl.ResultSetOptions;
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleReader;
import org.apache.drill.exec.vector.accessor.TupleWriter;
import org.apache.drill.exec.vector.complex.RepeatedMapVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test map array support in the result set loader.
 * <p>
 * The tests here should be considered in the "extra for experts"
 * category: run and/or debug these tests only after the scalar
 * tests work. Maps, and especially repeated maps, are very complex
 * constructs not to be tackled lightly.
 */
@Category(RowSetTests.class)
public class TestResultSetLoaderMapArray extends SubOperatorTest {
    @Test
    public void testBasics() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m").add("c", INT).add("d", VARCHAR).resumeSchema().buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Verify structure and schema
        TupleMetadata actualSchema = rootWriter.tupleSchema();
        Assert.assertEquals(2, actualSchema.size());
        Assert.assertTrue(actualSchema.metadata(1).isArray());
        Assert.assertTrue(actualSchema.metadata(1).isMap());
        Assert.assertEquals(2, actualSchema.metadata("m").mapSchema().size());
        Assert.assertEquals(2, actualSchema.column("m").getChildren().size());
        TupleWriter mapWriter = rootWriter.array("m").tuple();
        Assert.assertSame(actualSchema.metadata("m").mapSchema(), mapWriter.schema().mapSchema());
        Assert.assertSame(mapWriter.tupleSchema(), mapWriter.schema().mapSchema());
        Assert.assertSame(mapWriter.tupleSchema().metadata(0), mapWriter.scalar(0).schema());
        Assert.assertSame(mapWriter.tupleSchema().metadata(1), mapWriter.scalar(1).schema());
        // Write a couple of rows with arrays.
        rsLoader.startBatch();
        rootWriter.addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, "d1.1"), RowSetUtilities.mapValue(120, "d2.2"))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, "d3.1"), RowSetUtilities.mapValue(320, "d3.2"), RowSetUtilities.mapValue(330, "d3.3")));
        // Verify the first batch
        RowSet actual = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        RepeatedMapVector mapVector = ((RepeatedMapVector) (actual.container().getValueVector(1).getValueVector()));
        MaterializedField mapField = mapVector.getField();
        Assert.assertEquals(2, mapField.getChildren().size());
        Iterator<MaterializedField> iter = mapField.getChildren().iterator();
        Assert.assertTrue(mapWriter.scalar(0).schema().schema().isEquivalent(iter.next()));
        Assert.assertTrue(mapWriter.scalar(1).schema().schema().isEquivalent(iter.next()));
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, "d1.1"), RowSetUtilities.mapValue(120, "d2.2"))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, "d3.1"), RowSetUtilities.mapValue(320, "d3.2"), RowSetUtilities.mapValue(330, "d3.3"))).build();
        RowSetUtilities.verify(expected, actual);
        // In the second, create a row, then add a map member.
        // Should be back-filled to empty for the first row.
        rsLoader.startBatch();
        rootWriter.addRow(40, RowSetUtilities.mapArray(RowSetUtilities.mapValue(410, "d4.1"), RowSetUtilities.mapValue(420, "d4.2")));
        mapWriter.addColumn(SchemaBuilder.columnSchema("e", VARCHAR, OPTIONAL));
        rootWriter.addRow(50, RowSetUtilities.mapArray(RowSetUtilities.mapValue(510, "d5.1", "e5.1"), RowSetUtilities.mapValue(520, "d5.2", null))).addRow(60, RowSetUtilities.mapArray(RowSetUtilities.mapValue(610, "d6.1", "e6.1"), RowSetUtilities.mapValue(620, "d6.2", null), RowSetUtilities.mapValue(630, "d6.3", "e6.3")));
        // Verify the second batch
        actual = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        mapVector = ((RepeatedMapVector) (actual.container().getValueVector(1).getValueVector()));
        mapField = mapVector.getField();
        Assert.assertEquals(3, mapField.getChildren().size());
        TupleMetadata expectedSchema = new SchemaBuilder().add("a", INT).addMapArray("m").add("c", INT).add("d", VARCHAR).addNullable("e", VARCHAR).resumeSchema().buildSchema();
        expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(40, RowSetUtilities.mapArray(RowSetUtilities.mapValue(410, "d4.1", null), RowSetUtilities.mapValue(420, "d4.2", null))).addRow(50, RowSetUtilities.mapArray(RowSetUtilities.mapValue(510, "d5.1", "e5.1"), RowSetUtilities.mapValue(520, "d5.2", null))).addRow(60, RowSetUtilities.mapArray(RowSetUtilities.mapValue(610, "d6.1", "e6.1"), RowSetUtilities.mapValue(620, "d6.2", null), RowSetUtilities.mapValue(630, "d6.3", "e6.3"))).build();
        RowSetUtilities.verify(expected, actual);
        rsLoader.close();
    }

    @Test
    public void testNestedArray() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m").add("c", INT).addArray("d", VARCHAR).resumeSchema().buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Write a couple of rows with arrays within arrays.
        // (And, of course, the Varchar is actually an array of
        // bytes, so that's three array levels.)
        rsLoader.startBatch();
        rootWriter.addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, RowSetUtilities.strArray("d1.1.1", "d1.1.2")), RowSetUtilities.mapValue(120, RowSetUtilities.strArray("d1.2.1", "d1.2.2")))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, RowSetUtilities.strArray("d3.1.1", "d3.2.2")), RowSetUtilities.mapValue(320, RowSetUtilities.strArray()), RowSetUtilities.mapValue(330, RowSetUtilities.strArray("d3.3.1", "d1.2.2"))));
        // Verify the batch
        RowSet actual = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(10, RowSetUtilities.mapArray(RowSetUtilities.mapValue(110, RowSetUtilities.strArray("d1.1.1", "d1.1.2")), RowSetUtilities.mapValue(120, RowSetUtilities.strArray("d1.2.1", "d1.2.2")))).addRow(20, RowSetUtilities.mapArray()).addRow(30, RowSetUtilities.mapArray(RowSetUtilities.mapValue(310, RowSetUtilities.strArray("d3.1.1", "d3.2.2")), RowSetUtilities.mapValue(320, RowSetUtilities.strArray()), RowSetUtilities.mapValue(330, RowSetUtilities.strArray("d3.3.1", "d1.2.2")))).build();
        RowSetUtilities.verify(expected, actual);
        rsLoader.close();
    }

    /**
     * Test a doubly-nested array of maps.
     */
    @Test
    public void testDoubleNestedArray() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m1").add("b", INT).addMapArray("m2").add("c", INT).addArray("d", VARCHAR).resumeMap().resumeSchema().buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        ScalarWriter aWriter = rootWriter.scalar("a");
        ArrayWriter a1Writer = rootWriter.array("m1");
        TupleWriter m1Writer = a1Writer.tuple();
        ScalarWriter bWriter = m1Writer.scalar("b");
        ArrayWriter a2Writer = m1Writer.array("m2");
        TupleWriter m2Writer = a2Writer.tuple();
        ScalarWriter cWriter = m2Writer.scalar("c");
        ScalarWriter dWriter = m2Writer.array("d").scalar();
        for (int i = 0; i < 5; i++) {
            rootWriter.start();
            aWriter.setInt(i);
            for (int j = 0; j < 4; j++) {
                int a1Key = (i + 10) + j;
                bWriter.setInt(a1Key);
                for (int k = 0; k < 3; k++) {
                    int a2Key = (a1Key * 10) + k;
                    cWriter.setInt(a2Key);
                    for (int l = 0; l < 2; l++) {
                        dWriter.setString(("d-" + ((a2Key * 10) + l)));
                    }
                    a2Writer.save();
                }
                a1Writer.save();
            }
            rootWriter.save();
        }
        RowSet results = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        RowSetReader reader = results.reader();
        ScalarReader aReader = reader.scalar("a");
        ArrayReader a1Reader = array("m1");
        TupleReader m1Reader = a1Reader.tuple();
        ScalarReader bReader = m1Reader.scalar("b");
        ArrayReader a2Reader = m1Reader.array("m2");
        TupleReader m2Reader = a2Reader.tuple();
        ScalarReader cReader = m2Reader.scalar("c");
        ArrayReader dArray = m2Reader.array("d");
        ScalarReader dReader = dArray.scalar();
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(reader.next());
            Assert.assertEquals(i, aReader.getInt());
            for (int j = 0; j < 4; j++) {
                Assert.assertTrue(a1Reader.next());
                int a1Key = (i + 10) + j;
                Assert.assertEquals(a1Key, bReader.getInt());
                for (int k = 0; k < 3; k++) {
                    Assert.assertTrue(a2Reader.next());
                    int a2Key = (a1Key * 10) + k;
                    Assert.assertEquals(a2Key, cReader.getInt());
                    for (int l = 0; l < 2; l++) {
                        Assert.assertTrue(dArray.next());
                        Assert.assertEquals(("d-" + ((a2Key * 10) + l)), dReader.getString());
                    }
                }
            }
        }
        rsLoader.close();
    }

    /**
     * Version of the {#link TestResultSetLoaderProtocol#testOverwriteRow()} test
     * that uses nested columns inside an array of maps. Here we must call
     * <tt>start()</tt> to reset the array back to the initial start position after
     * each "discard."
     */
    @Test
    public void testOverwriteRow() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("m").add("b", INT).add("c", VARCHAR).resumeSchema().buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Can't use the shortcut to populate rows when doing overwrites.
        ScalarWriter aWriter = rootWriter.scalar("a");
        ArrayWriter maWriter = rootWriter.array("m");
        TupleWriter mWriter = maWriter.tuple();
        ScalarWriter bWriter = mWriter.scalar("b");
        ScalarWriter cWriter = mWriter.scalar("c");
        // Write 100,000 rows, overwriting 99% of them. This will cause vector
        // overflow and data corruption if overwrite does not work; but will happily
        // produce the correct result if everything works as it should.
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        rsLoader.startBatch();
        while (count < 10000) {
            rootWriter.start();
            count++;
            aWriter.setInt(count);
            for (int i = 0; i < 10; i++) {
                bWriter.setInt(((count * 10) + i));
                cWriter.setBytes(value, value.length);
                maWriter.save();
            }
            if ((count % 100) == 0) {
                rootWriter.save();
            }
        } 
        // Verify using a reader.
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals((count / 100), result.rowCount());
        RowSetReader reader = result.reader();
        ArrayReader maReader = array("m");
        TupleReader mReader = maReader.tuple();
        int rowId = 1;
        while (reader.next()) {
            Assert.assertEquals((rowId * 100), reader.scalar("a").getInt());
            Assert.assertEquals(10, maReader.size());
            for (int i = 0; i < 10; i++) {
                assert maReader.next();
                Assert.assertEquals(((rowId * 1000) + i), mReader.scalar("b").getInt());
                Assert.assertTrue(Arrays.equals(value, mReader.scalar("c").getBytes()));
            }
            rowId++;
        } 
        result.clear();
        rsLoader.close();
    }

    /**
     * Check that the "fill-empties" logic descends down into
     * a repeated map.
     */
    @Test
    public void testOmittedValues() {
        TupleMetadata schema = new SchemaBuilder().add("id", INT).addMapArray("m").addNullable("a", INT).addNullable("b", VARCHAR).resumeSchema().buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        int mapSkip = 5;
        int entrySkip = 3;
        int rowCount = 1000;
        int entryCount = 10;
        rsLoader.startBatch();
        ArrayWriter maWriter = rootWriter.array("m");
        TupleWriter mWriter = maWriter.tuple();
        for (int i = 0; i < rowCount; i++) {
            rootWriter.start();
            rootWriter.scalar(0).setInt(i);
            if ((i % mapSkip) != 0) {
                for (int j = 0; j < entryCount; j++) {
                    if ((j % entrySkip) != 0) {
                        mWriter.scalar(0).setInt(((i * entryCount) + j));
                        mWriter.scalar(1).setString(((("b-" + i) + ".") + j));
                    }
                    maWriter.save();
                }
            }
            rootWriter.save();
        }
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(rowCount, result.rowCount());
        RowSetReader reader = result.reader();
        ArrayReader maReader = array("m");
        TupleReader mReader = maReader.tuple();
        for (int i = 0; i < rowCount; i++) {
            Assert.assertTrue(reader.next());
            Assert.assertEquals(i, scalar(0).getInt());
            if ((i % mapSkip) == 0) {
                Assert.assertEquals(0, maReader.size());
                continue;
            }
            Assert.assertEquals(entryCount, maReader.size());
            for (int j = 0; j < entryCount; j++) {
                Assert.assertTrue(maReader.next());
                if ((j % entrySkip) == 0) {
                    Assert.assertTrue(mReader.scalar(0).isNull());
                    Assert.assertTrue(mReader.scalar(1).isNull());
                } else {
                    Assert.assertFalse(mReader.scalar(0).isNull());
                    Assert.assertFalse(mReader.scalar(1).isNull());
                    Assert.assertEquals(((i * entryCount) + j), mReader.scalar(0).getInt());
                    Assert.assertEquals(((("b-" + i) + ".") + j), mReader.scalar(1).getString());
                }
            }
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
        TupleMetadata schema = new SchemaBuilder().addMapArray("m").add("a", INT).add("b", VARCHAR).resumeSchema().buildSchema();
        ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        ArrayWriter maWriter = rootWriter.array("m");
        TupleWriter mWriter = maWriter.tuple();
        rsLoader.startBatch();
        for (int i = 0; i < 40; i++) {
            rootWriter.start();
            for (int j = 0; j < 3; j++) {
                mWriter.scalar("a").setInt(i);
                mWriter.scalar("b").setString(("b-" + i));
                maWriter.save();
            }
            rootWriter.save();
        }
        // Don't harvest the batch. Allocator will complain if the
        // loader does not release memory.
        rsLoader.close();
    }
}

