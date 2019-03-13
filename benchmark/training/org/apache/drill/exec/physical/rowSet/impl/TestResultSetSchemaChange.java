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
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.ResultSetLoaderImpl.ResultSetOptions;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(RowSetTests.class)
public class TestResultSetSchemaChange extends SubOperatorTest {
    /**
     * Test the case where the schema changes in the first batch.
     * Schema changes before the first record are trivial and tested
     * elsewhere. Here we write some records, then add new columns, as a
     * JSON reader might do.
     */
    @Test
    public void testSchemaChangeFirstBatch() {
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        RowSetLoader rootWriter = rsLoader.writer();
        rootWriter.addColumn(SchemaBuilder.columnSchema("a", VARCHAR, REQUIRED));
        // Create initial rows
        rsLoader.startBatch();
        int rowCount = 0;
        for (int i = 0; i < 2; i++) {
            rootWriter.start();
            rowCount++;
            rootWriter.scalar(0).setString(("a_" + rowCount));
            rootWriter.save();
        }
        // Add a second column: nullable.
        rootWriter.addColumn(SchemaBuilder.columnSchema("b", INT, OPTIONAL));
        for (int i = 0; i < 2; i++) {
            rootWriter.start();
            rowCount++;
            rootWriter.scalar(0).setString(("a_" + rowCount));
            rootWriter.scalar(1).setInt(rowCount);
            rootWriter.save();
        }
        // Add a third column. Use variable-width so that offset
        // vectors must be back-filled.
        rootWriter.addColumn(SchemaBuilder.columnSchema("c", VARCHAR, OPTIONAL));
        for (int i = 0; i < 2; i++) {
            rootWriter.start();
            rowCount++;
            rootWriter.scalar(0).setString(("a_" + rowCount));
            rootWriter.scalar(1).setInt(rowCount);
            rootWriter.scalar(2).setString(("c_" + rowCount));
            rootWriter.save();
        }
        // Fourth: Required Varchar. Previous rows are back-filled with empty strings.
        // And a required int. Back-filled with zeros.
        // May occasionally be useful. But, does have to work to prevent
        // vector corruption if some reader decides to go this route.
        rootWriter.addColumn(SchemaBuilder.columnSchema("d", VARCHAR, REQUIRED));
        rootWriter.addColumn(SchemaBuilder.columnSchema("e", INT, REQUIRED));
        for (int i = 0; i < 2; i++) {
            rootWriter.start();
            rowCount++;
            rootWriter.scalar(0).setString(("a_" + rowCount));
            rootWriter.scalar(1).setInt(rowCount);
            rootWriter.scalar(2).setString(("c_" + rowCount));
            rootWriter.scalar(3).setString(("d_" + rowCount));
            rootWriter.scalar(4).setInt((rowCount * 10));
            rootWriter.save();
        }
        // Add an array. Now two offset vectors must be back-filled.
        rootWriter.addColumn(SchemaBuilder.columnSchema("f", VARCHAR, REPEATED));
        for (int i = 0; i < 2; i++) {
            rootWriter.start();
            rowCount++;
            rootWriter.scalar(0).setString(("a_" + rowCount));
            rootWriter.scalar(1).setInt(rowCount);
            rootWriter.scalar(2).setString(("c_" + rowCount));
            rootWriter.scalar(3).setString(("d_" + rowCount));
            rootWriter.scalar(4).setInt((rowCount * 10));
            ScalarWriter arrayWriter = rootWriter.column(5).array().scalar();
            arrayWriter.setString((("f_" + rowCount) + "-1"));
            arrayWriter.setString((("f_" + rowCount) + "-2"));
            rootWriter.save();
        }
        // Harvest the batch and verify.
        RowSet actual = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        BatchSchema expectedSchema = new SchemaBuilder().add("a", VARCHAR).addNullable("b", INT).addNullable("c", VARCHAR).add("d", VARCHAR).add("e", INT).addArray("f", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("a_1", null, null, "", 0, RowSetUtilities.strArray()).addRow("a_2", null, null, "", 0, RowSetUtilities.strArray()).addRow("a_3", 3, null, "", 0, RowSetUtilities.strArray()).addRow("a_4", 4, null, "", 0, RowSetUtilities.strArray()).addRow("a_5", 5, "c_5", "", 0, RowSetUtilities.strArray()).addRow("a_6", 6, "c_6", "", 0, RowSetUtilities.strArray()).addRow("a_7", 7, "c_7", "d_7", 70, RowSetUtilities.strArray()).addRow("a_8", 8, "c_8", "d_8", 80, RowSetUtilities.strArray()).addRow("a_9", 9, "c_9", "d_9", 90, RowSetUtilities.strArray("f_9-1", "f_9-2")).addRow("a_10", 10, "c_10", "d_10", 100, RowSetUtilities.strArray("f_10-1", "f_10-2")).build();
        RowSetUtilities.verify(expected, actual);
        rsLoader.close();
    }

    /**
     * Test a schema change on the row that overflows. If the
     * new column is added after overflow, it will appear as
     * a schema-change in the following batch. This is fine as
     * we are essentially time-shifting: pretending that the
     * overflow row was written in the next batch (which, in
     * fact, it is: that's what overflow means.)
     */
    @Test
    public void testSchemaChangeWithOverflow() {
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rootWriter.addColumn(SchemaBuilder.columnSchema("a", VARCHAR, REQUIRED));
        rsLoader.startBatch();
        byte[] value = new byte[512];
        Arrays.fill(value, ((byte) ('X')));
        int count = 0;
        while (!(rootWriter.isFull())) {
            rootWriter.start();
            rootWriter.scalar(0).setBytes(value, value.length);
            // Relies on fact that isFull becomes true right after
            // a vector overflows; don't have to wait for saveRow().
            if (rootWriter.isFull()) {
                rootWriter.addColumn(SchemaBuilder.columnSchema("b", INT, OPTIONAL));
                rootWriter.scalar(1).setInt(count);
                // Add a Varchar to ensure its offset fiddling is done properly
                rootWriter.addColumn(SchemaBuilder.columnSchema("c", VARCHAR, OPTIONAL));
                rootWriter.scalar(2).setString(("c-" + count));
                // Allow adding a required column at this point.
                // (Not intuitively obvious that this should work; we back-fill
                // with zeros.)
                rootWriter.addColumn(SchemaBuilder.columnSchema("d", INT, REQUIRED));
            }
            rootWriter.save();
            count++;
        } 
        // Result should include only the first column.
        BatchSchema expectedSchema = new SchemaBuilder().add("a", VARCHAR).build();
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertTrue(result.batchSchema().isEquivalent(expectedSchema));
        Assert.assertEquals((count - 1), result.rowCount());
        result.clear();
        Assert.assertEquals(1, rsLoader.schemaVersion());
        // Double check: still can add a required column after
        // starting the next batch. (No longer in overflow state.)
        rsLoader.startBatch();
        rootWriter.addColumn(SchemaBuilder.columnSchema("e", INT, REQUIRED));
        // Next batch should start with the overflow row, including
        // the column added at the end of the previous batch, after
        // overflow.
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(5, rsLoader.schemaVersion());
        Assert.assertEquals(1, result.rowCount());
        expectedSchema = new SchemaBuilder(expectedSchema).addNullable("b", INT).addNullable("c", VARCHAR).add("d", INT).add("e", INT).build();
        Assert.assertTrue(result.batchSchema().isEquivalent(expectedSchema));
        RowSetReader reader = result.reader();
        reader.next();
        Assert.assertEquals((count - 1), reader.scalar(1).getInt());
        Assert.assertEquals(("c-" + (count - 1)), reader.scalar(2).getString());
        Assert.assertEquals(0, scalar("d").getInt());
        Assert.assertEquals(0, scalar("e").getInt());
        result.clear();
        rsLoader.close();
    }
}

