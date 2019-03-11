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
package org.apache.drill.exec.physical.impl.xsort.managed;


import DataMode.REQUIRED;
import MinorType.INT;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.RecordBatchSizer;
import org.apache.drill.exec.record.RecordBatchSizer.ColumnSize;
import org.apache.drill.exec.record.VectorInitializer;
import org.apache.drill.exec.record.VectorInitializer.AllocationHint;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.IntVector;
import org.apache.drill.exec.vector.RepeatedIntVector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;


/**
 * DRILL-5804.
 * Code had a bug that if an array had low cardinality, the average,
 * expressed as an int, was zero. We then allocated a zero length
 * buffer, tried to double it, got another zero length buffer, and
 * looped forever. This test verifies the fixes to avoid that case.
 *
 * @throws Exception
 * 		
 */
public class TestShortArrays extends SubOperatorTest {
    @Test
    public void testSizer() {
        // Create a row set with less than one item, on
        // average, per array.
        BatchSchema schema = new SchemaBuilder().add("a", INT).addArray("b", INT).build();
        RowSetBuilder builder = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.intArray(10));
        for (int i = 2; i <= 10; i++) {
            builder.addRow(i, RowSetUtilities.intArray());
        }
        RowSet rows = builder.build();
        // Run the record batch sizer on the resulting batch.
        RecordBatchSizer sizer = new RecordBatchSizer(rows.container());
        Assert.assertEquals(2, sizer.columns().size());
        ColumnSize bCol = sizer.columns().get("b");
        Assert.assertEquals(0.1, bCol.getCardinality(), 0.01);
        Assert.assertEquals(1, bCol.getElementCount());
        // Create a vector initializer using the sizer info.
        VectorInitializer vi = sizer.buildVectorInitializer();
        AllocationHint bHint = vi.hint("b");
        Assert.assertNotNull(bHint);
        Assert.assertEquals(bHint.elementCount, bCol.getCardinality(), 0.001);
        // Create a new batch, and new vector, using the sizer and
        // initializer inferred from the previous batch.
        RowSet.SingleRowSet empty = SubOperatorTest.fixture.rowSet(schema);
        vi.allocateBatch(empty.container(), 100);
        Assert.assertEquals(2, empty.container().getNumberOfColumns());
        @SuppressWarnings("resource")
        ValueVector bVector = empty.container().getValueVector(1).getValueVector();
        Assert.assertTrue((bVector instanceof RepeatedIntVector));
        Assert.assertEquals(16, getDataVector().getValueCapacity());
        rows.clear();
        empty.clear();
    }

    /**
     * Test that a zero-length vector, on reAlloc, will default
     * to 256 bytes. (Previously the code just doubled zero
     * forever.)
     */
    @Test
    public void testReAllocZeroSize() {
        try (IntVector vector = new IntVector(SchemaBuilder.columnSchema("a", INT, REQUIRED), SubOperatorTest.fixture.allocator())) {
            vector.allocateNew(0);
            vector.reAlloc();
            Assert.assertEquals((256 / 4), vector.getValueCapacity());
        }
    }
}

