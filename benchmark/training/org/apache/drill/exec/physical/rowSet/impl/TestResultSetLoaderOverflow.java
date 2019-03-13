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


import DataMode.REPEATED;
import MinorType.INT;
import MinorType.VARCHAR;
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.ResultSetLoaderImpl.ResultSetOptions;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.shaded.guava.com.google.common.base.Charsets;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Exercise the vector overflow functionality for the result set loader.
 */
@Category(RowSetTests.class)
public class TestResultSetLoaderOverflow extends SubOperatorTest {
    /**
     * Test that the writer detects a vector overflow. The offending column
     * value should be moved to the next batch.
     */
    @Test
    public void testVectorSizeLimit() {
        TupleMetadata schema = new SchemaBuilder().add("s", VARCHAR).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        while (!(rootWriter.isFull())) {
            rootWriter.start();
            rootWriter.scalar(0).setBytes(value, value.length);
            rootWriter.save();
            count++;
        } 
        // Number of rows should be driven by vector size.
        // Our row count should include the overflow row
        int expectedCount = (ValueVector.MAX_BUFFER_SIZE) / (value.length);
        Assert.assertEquals((expectedCount + 1), count);
        // Loader's row count should include only "visible" rows
        Assert.assertEquals(expectedCount, rootWriter.rowCount());
        // Total count should include invisible and look-ahead rows.
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        // Result should exclude the overflow row
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(expectedCount, result.rowCount());
        result.clear();
        // Next batch should start with the overflow row
        rsLoader.startBatch();
        Assert.assertEquals(1, rootWriter.rowCount());
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(1, result.rowCount());
        result.clear();
        rsLoader.close();
    }

    /**
     * Test that the writer detects a vector overflow. The offending column
     * value should be moved to the next batch.
     */
    @Test
    public void testBatchSizeLimit() {
        TupleMetadata schema = new SchemaBuilder().add("s", VARCHAR).buildSchema();
        ResultSetOptions options = // Offsets, doubled because of +1
        new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).setBatchSizeLimit((((8 * 1024) * 1024)// Data
         + ((2 * (ValueVector.MAX_ROW_COUNT)) * 4))).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        while (!(rootWriter.isFull())) {
            rootWriter.start();
            rootWriter.scalar(0).setBytes(value, value.length);
            rootWriter.save();
            count++;
        } 
        // Our row count should include the overflow row
        int expectedCount = ((8 * 1024) * 1024) / (value.length);
        Assert.assertEquals((expectedCount + 1), count);
        // Loader's row count should include only "visible" rows
        Assert.assertEquals(expectedCount, rootWriter.rowCount());
        // Total count should include invisible and look-ahead rows.
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        // Result should exclude the overflow row
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(expectedCount, result.rowCount());
        result.clear();
        // Next batch should start with the overflow row
        rsLoader.startBatch();
        Assert.assertEquals(1, rootWriter.rowCount());
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(1, result.rowCount());
        result.clear();
        rsLoader.close();
    }

    /**
     * Load a batch to overflow. Then, close the loader with the overflow
     * batch unharvested. The Loader should release the memory allocated
     * to the unused overflow vectors.
     */
    @Test
    public void testCloseWithOverflow() {
        TupleMetadata schema = new SchemaBuilder().add("s", VARCHAR).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        while (!(rootWriter.isFull())) {
            rootWriter.start();
            rootWriter.scalar(0).setBytes(value, value.length);
            rootWriter.save();
            count++;
        } 
        Assert.assertTrue((count < (ValueVector.MAX_ROW_COUNT)));
        // Harvest the full batch
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        result.clear();
        // Close without harvesting the overflow batch.
        rsLoader.close();
    }

    /**
     * Case where a single array fills up the vector to the maximum size
     * limit. Overflow won't work here; the attempt will fail with a user
     * exception.
     */
    @Test
    public void testOversizeArray() {
        TupleMetadata schema = new SchemaBuilder().addArray("s", VARCHAR).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Create a single array as the column value in the first row. When
        // this overflows, an exception is thrown since overflow is not possible.
        rsLoader.startBatch();
        byte[] value = new byte[473];
        Arrays.fill(value, ((byte) ('X')));
        rootWriter.start();
        ScalarWriter array = rootWriter.array(0).scalar();
        try {
            for (int i = 0; i < (ValueVector.MAX_ROW_COUNT); i++) {
                array.setBytes(value, value.length);
            }
            Assert.fail();
        } catch (UserException e) {
            Assert.assertTrue(e.getMessage().contains("column value is larger than the maximum"));
        }
        rsLoader.close();
    }

    /**
     * Test a row with a single array column which overflows. Verifies
     * that all the fiddly bits about offset vectors and so on works
     * correctly. Run this test (the simplest case) if you change anything
     * about the array handling code.
     */
    @Test
    public void testSizeLimitOnArray() {
        TupleMetadata schema = new SchemaBuilder().addArray("s", VARCHAR).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Fill batch with rows of with a single array, three values each. Tack on
        // a suffix to each so we can be sure the proper data is written and moved
        // to the overflow batch.
        rsLoader.startBatch();
        byte[] value = new byte[473];
        Arrays.fill(value, ((byte) ('X')));
        String strValue = new String(value, Charsets.UTF_8);
        int count = 0;
        int rowSize = 0;
        int totalSize = 0;
        int valuesPerArray = 13;
        while (rootWriter.start()) {
            totalSize += rowSize;
            rowSize = 0;
            ScalarWriter array = rootWriter.array(0).scalar();
            for (int i = 0; i < valuesPerArray; i++) {
                String cellValue = ((strValue + (count + 1)) + ".") + i;
                array.setString(cellValue);
                rowSize += cellValue.length();
            }
            rootWriter.save();
            count++;
        } 
        // Row count should include the overflow row.
        int expectedCount = count - 1;
        // Size without overflow row should fit in the vector, size
        // with overflow should not.
        Assert.assertTrue((totalSize <= (ValueVector.MAX_BUFFER_SIZE)));
        Assert.assertTrue(((totalSize + rowSize) > (ValueVector.MAX_BUFFER_SIZE)));
        // Result should exclude the overflow row. Last row
        // should hold the last full array.
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(expectedCount, result.rowCount());
        RowSetReader reader = result.reader();
        reader.setPosn((expectedCount - 1));
        ArrayReader arrayReader = reader.array(0);
        ScalarReader strReader = arrayReader.scalar();
        Assert.assertEquals(valuesPerArray, arrayReader.size());
        for (int i = 0; i < valuesPerArray; i++) {
            Assert.assertTrue(arrayReader.next());
            String cellValue = ((strValue + (count - 1)) + ".") + i;
            Assert.assertEquals(cellValue, strReader.getString());
        }
        result.clear();
        // Next batch should start with the overflow row.
        // The only row in this next batch should be the whole
        // array being written at the time of overflow.
        rsLoader.startBatch();
        // VectorPrinter.printStrings((VarCharVector) ((VarCharColumnWriter) rootWriter.array(0).scalar()).vector(), 0, 5);
        // ((ResultSetLoaderImpl) rsLoader).dump(new HierarchicalPrinter());
        Assert.assertEquals(1, rootWriter.rowCount());
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        // VectorPrinter.printStrings((VarCharVector) ((VarCharColumnWriter) rootWriter.array(0).scalar()).vector(), 0, 5);
        Assert.assertEquals(1, result.rowCount());
        reader = result.reader();
        reader.next();
        arrayReader = reader.array(0);
        strReader = arrayReader.scalar();
        Assert.assertEquals(valuesPerArray, arrayReader.size());
        for (int i = 0; i < valuesPerArray; i++) {
            Assert.assertTrue(arrayReader.next());
            String cellValue = ((strValue + count) + ".") + i;
            Assert.assertEquals(cellValue, strReader.getString());
        }
        result.clear();
        rsLoader.close();
    }

    /**
     * Test the complete set of array overflow cases:
     * <ul>
     * <li>Array a is written before the column that has overflow,
     * and must be copied, in its entirety, to the overflow row.</li>
     * <li>Column b causes the overflow.</li>
     * <li>Column c is written after the overflow, and should go
     * to the look-ahead row.</li>
     * <li>Column d is written for a while, then has empties before
     * the overflow row, but is written in the overflow row.<li>
     * <li>Column e is like d, but is not written in the overflow
     * row.</li>
     */
    @Test
    public void testArrayOverflowWithOtherArrays() {
        TupleMetadata schema = new SchemaBuilder().addArray("a", INT).addArray("b", VARCHAR).addArray("c", INT).addArray("d", INT).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Fill batch with rows of with a single array, three values each. Tack on
        // a suffix to each so we can be sure the proper data is written and moved
        // to the overflow batch.
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        String strValue = new String(value, Charsets.UTF_8);
        int aCount = 3;
        int bCount = 11;
        int cCount = 5;
        int dCount = 7;
        int cCutoff = (((ValueVector.MAX_BUFFER_SIZE) / (value.length)) / bCount) / 2;
        ScalarWriter aWriter = rootWriter.array("a").scalar();
        ScalarWriter bWriter = rootWriter.array("b").scalar();
        ScalarWriter cWriter = rootWriter.array("c").scalar();
        ScalarWriter dWriter = rootWriter.array("d").scalar();
        int count = 0;
        rsLoader.startBatch();
        while (rootWriter.start()) {
            if ((rootWriter.rowCount()) == 2952) {
                count = count + 0;
            }
            for (int i = 0; i < aCount; i++) {
                aWriter.setInt(((count * aCount) + i));
            }
            for (int i = 0; i < bCount; i++) {
                String cellValue = strValue + ((count * bCount) + i);
                bWriter.setString(cellValue);
            }
            if (count < cCutoff) {
                for (int i = 0; i < cCount; i++) {
                    cWriter.setInt(((count * cCount) + i));
                }
            }
            // Relies on fact that isFull becomes true right after
            // a vector overflows; don't have to wait for saveRow().
            if ((count < cCutoff) || (rootWriter.isFull())) {
                for (int i = 0; i < dCount; i++) {
                    dWriter.setInt(((count * dCount) + i));
                }
            }
            rootWriter.save();
            count++;
        } 
        // Verify
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals((count - 1), result.rowCount());
        {
            RowSetReader reader = result.reader();
            ArrayReader aArray = array("a");
            ScalarReader aReader = aArray.scalar();
            ArrayReader bArray = array("b");
            ScalarReader bReader = bArray.scalar();
            ArrayReader cArray = array("c");
            ScalarReader cReader = cArray.scalar();
            ArrayReader dArray = array("d");
            ScalarReader dReader = dArray.scalar();
            while (reader.next()) {
                int rowId = reader.offset();
                Assert.assertEquals(aCount, aArray.size());
                for (int i = 0; i < aCount; i++) {
                    Assert.assertTrue(aArray.next());
                    Assert.assertEquals(((rowId * aCount) + i), aReader.getInt());
                }
                Assert.assertEquals(bCount, bArray.size());
                for (int i = 0; i < bCount; i++) {
                    Assert.assertTrue(bArray.next());
                    String cellValue = strValue + ((rowId * bCount) + i);
                    Assert.assertEquals(cellValue, bReader.getString());
                }
                if (rowId < cCutoff) {
                    Assert.assertEquals(cCount, cArray.size());
                    for (int i = 0; i < cCount; i++) {
                        Assert.assertTrue(cArray.next());
                        Assert.assertEquals(((rowId * cCount) + i), cReader.getInt());
                    }
                    Assert.assertEquals(dCount, dArray.size());
                    for (int i = 0; i < dCount; i++) {
                        Assert.assertTrue(dArray.next());
                        Assert.assertEquals(((rowId * dCount) + i), dReader.getInt());
                    }
                } else {
                    Assert.assertEquals(0, cArray.size());
                    Assert.assertEquals(0, dArray.size());
                }
            } 
            result.clear();
        }
        int firstCount = count - 1;
        // One row is in the batch. Write more, skipping over the
        // initial few values for columns c and d. Column d has a
        // roll-over value, c has an empty roll-over.
        rsLoader.startBatch();
        for (int j = 0; j < 5; j++) {
            rootWriter.start();
            for (int i = 0; i < aCount; i++) {
                aWriter.setInt(((count * aCount) + i));
            }
            for (int i = 0; i < bCount; i++) {
                String cellValue = strValue + ((count * bCount) + i);
                bWriter.setString(cellValue);
            }
            if (j > 3) {
                for (int i = 0; i < cCount; i++) {
                    cWriter.setInt(((count * cCount) + i));
                }
                for (int i = 0; i < dCount; i++) {
                    dWriter.setInt(((count * dCount) + i));
                }
            }
            rootWriter.save();
            count++;
        }
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(6, result.rowCount());
        {
            RowSetReader reader = result.reader();
            ArrayReader aArray = array("a");
            ScalarReader aReader = aArray.scalar();
            ArrayReader bArray = array("b");
            ScalarReader bReader = bArray.scalar();
            ArrayReader cArray = array("c");
            ScalarReader cReader = cArray.scalar();
            ArrayReader dArray = array("d");
            ScalarReader dReader = dArray.scalar();
            int j = 0;
            while (reader.next()) {
                int rowId = firstCount + (reader.offset());
                Assert.assertEquals(aCount, aArray.size());
                for (int i = 0; i < aCount; i++) {
                    Assert.assertTrue(aArray.next());
                    Assert.assertEquals(("Index " + i), ((rowId * aCount) + i), aReader.getInt());
                }
                Assert.assertEquals(bCount, bArray.size());
                for (int i = 0; i < bCount; i++) {
                    Assert.assertTrue(bArray.next());
                    String cellValue = strValue + ((rowId * bCount) + i);
                    Assert.assertEquals(cellValue, bReader.getString());
                }
                if (j > 4) {
                    Assert.assertEquals(cCount, cArray.size());
                    for (int i = 0; i < cCount; i++) {
                        Assert.assertTrue(cArray.next());
                        Assert.assertEquals(((rowId * cCount) + i), cReader.getInt());
                    }
                } else {
                    Assert.assertEquals(0, cArray.size());
                }
                if ((j == 0) || (j > 4)) {
                    Assert.assertEquals(dCount, dArray.size());
                    for (int i = 0; i < dCount; i++) {
                        Assert.assertTrue(dArray.next());
                        Assert.assertEquals(((rowId * dCount) + i), dReader.getInt());
                    }
                } else {
                    Assert.assertEquals(0, dArray.size());
                }
                j++;
            } 
        }
        result.clear();
        rsLoader.close();
    }

    /**
     * Create an array that contains more than 64K values. Drill has no numeric
     * limit on array lengths. (Well, it does, but the limit is about 2 billion
     * which, even for bytes, is too large to fit into a vector...)
     */
    @Test
    public void testLargeArray() {
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        RowSetLoader rootWriter = rsLoader.writer();
        MaterializedField field = SchemaBuilder.columnSchema("a", INT, REPEATED);
        rootWriter.addColumn(field);
        // Create a single array as the column value in the first row. When
        // this overflows, an exception is thrown since overflow is not possible.
        rsLoader.startBatch();
        rootWriter.start();
        ScalarWriter array = rootWriter.array(0).scalar();
        try {
            for (int i = 0; i < (Integer.MAX_VALUE); i++) {
                array.setInt((i + 1));
            }
            Assert.fail();
        } catch (UserException e) {
            // Expected
        }
        rsLoader.close();
    }

    /**
     * Test the case that an array has "missing values" before the overflow.
     */
    @Test
    public void testMissingArrayValues() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).addArray("c", INT).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int blankAfter = (((ValueVector.MAX_BUFFER_SIZE) / 512) * 2) / 3;
        ScalarWriter cWriter = rootWriter.array("c").scalar();
        rsLoader.startBatch();
        int rowId = 0;
        while (rootWriter.start()) {
            rootWriter.scalar("a").setInt(rowId);
            rootWriter.scalar("b").setBytes(value, value.length);
            if (rowId < blankAfter) {
                for (int i = 0; i < 3; i++) {
                    cWriter.setInt(((rowId * 3) + i));
                }
            }
            rootWriter.save();
            rowId++;
        } 
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals((rowId - 1), result.rowCount());
        RowSetReader reader = result.reader();
        ArrayReader cArray = array("c");
        ScalarReader cReader = cArray.scalar();
        while (reader.next()) {
            Assert.assertEquals(reader.offset(), reader.scalar("a").getInt());
            Assert.assertTrue(Arrays.equals(value, reader.scalar("b").getBytes()));
            if ((reader.offset()) < blankAfter) {
                Assert.assertEquals(3, cArray.size());
                for (int i = 0; i < 3; i++) {
                    Assert.assertTrue(cArray.next());
                    Assert.assertEquals((((reader.offset()) * 3) + i), cReader.getInt());
                }
            } else {
                Assert.assertEquals(0, cArray.size());
            }
        } 
        result.clear();
        rsLoader.close();
    }

    @Test
    public void testOverflowWithNullables() {
        TupleMetadata schema = new SchemaBuilder().add("n", INT).addNullable("a", VARCHAR).addNullable("b", VARCHAR).addNullable("c", VARCHAR).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rsLoader.startBatch();
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        while (!(rootWriter.isFull())) {
            rootWriter.start();
            rootWriter.scalar(0).setInt(count);
            rootWriter.scalar(1).setNull();
            rootWriter.scalar(2).setBytes(value, value.length);
            rootWriter.scalar(3).setNull();
            rootWriter.save();
            count++;
        } 
        // Result should exclude the overflow row
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals((count - 1), result.rowCount());
        RowSetReader reader = result.reader();
        while (reader.next()) {
            Assert.assertEquals(reader.offset(), scalar(0).getInt());
            Assert.assertTrue(scalar(1).isNull());
            Assert.assertTrue(Arrays.equals(value, scalar(2).getBytes()));
            Assert.assertTrue(scalar(3).isNull());
        } 
        result.clear();
        // Next batch should start with the overflow row
        rsLoader.startBatch();
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        reader = result.reader();
        Assert.assertEquals(1, result.rowCount());
        Assert.assertTrue(reader.next());
        Assert.assertEquals((count - 1), scalar(0).getInt());
        Assert.assertTrue(scalar(1).isNull());
        Assert.assertTrue(Arrays.equals(value, scalar(2).getBytes()));
        Assert.assertTrue(scalar(3).isNull());
        result.clear();
        rsLoader.close();
    }
}

