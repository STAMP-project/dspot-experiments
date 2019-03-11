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
package org.apache.drill.exec.record.vector;


import MinorType.INT;
import MinorType.VARCHAR;
import io.netty.buffer.DrillBuf;
import java.util.List;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.exception.SchemaChangeException;
import org.apache.drill.exec.memory.BufferAllocator;
import org.apache.drill.exec.memory.RootAllocatorFactory;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.RecordBatchLoader;
import org.apache.drill.exec.record.WritableBatch;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.ValueVector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(VectorTest.class)
public class TestLoad extends ExecTest {
    private final DrillConfig drillConfig = DrillConfig.create();

    @SuppressWarnings("resource")
    @Test
    public void testLoadValueVector() throws Exception {
        final BufferAllocator allocator = RootAllocatorFactory.newRoot(drillConfig);
        BatchSchema schema = new SchemaBuilder().add("ints", INT).add("chars", VARCHAR).addNullable("chars2", VARCHAR).build();
        // Create vectors
        final List<ValueVector> vectors = TestLoad.createVectors(allocator, schema, 100);
        // Writeable batch now owns vector buffers
        final WritableBatch writableBatch = WritableBatch.getBatchNoHV(100, vectors, false);
        // Serialize the vectors
        final DrillBuf byteBuf = TestLoad.serializeBatch(allocator, writableBatch);
        // Batch loader does NOT take ownership of the serialized buffer
        final RecordBatchLoader batchLoader = new RecordBatchLoader(allocator);
        batchLoader.load(writableBatch.getDef(), byteBuf);
        // Release the serialized buffer.
        byteBuf.release();
        // TODO: Do actual validation
        Assert.assertEquals(100, batchLoader.getRecordCount());
        // Free the original vectors
        writableBatch.clear();
        // Free the deserialized vectors
        batchLoader.clear();
        // The allocator will verify that the frees were done correctly.
        allocator.close();
    }

    @Test
    public void testSchemaChange() throws SchemaChangeException {
        final BufferAllocator allocator = RootAllocatorFactory.newRoot(drillConfig);
        final RecordBatchLoader batchLoader = new RecordBatchLoader(allocator);
        // Initial schema: a: INT, b: VARCHAR
        // Schema change: N/A
        BatchSchema schema1 = new SchemaBuilder().add("a", INT).add("b", VARCHAR).build();
        {
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema1));
            Assert.assertTrue(schema1.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Same schema
        // Schema change: No
        {
            Assert.assertFalse(loadBatch(allocator, batchLoader, schema1));
            Assert.assertTrue(schema1.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Reverse columns: b: VARCHAR, a: INT
        // Schema change: No
        {
            BatchSchema schema = new SchemaBuilder().add("b", VARCHAR).add("a", INT).build();
            Assert.assertFalse(loadBatch(allocator, batchLoader, schema));
            // Potential bug: see DRILL-5828
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Drop a column: a: INT
        // Schema change: Yes
        {
            BatchSchema schema = new SchemaBuilder().add("a", INT).build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Add a column: a: INT, b: VARCHAR, c: INT
        // Schema change: Yes
        {
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema1));
            Assert.assertTrue(schema1.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
            BatchSchema schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).add("c", INT).build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Change a column type: a: INT, b: VARCHAR, c: VARCHAR
        // Schema change: Yes
        {
            BatchSchema schema = new SchemaBuilder().add("a", INT).add("b", VARCHAR).add("c", VARCHAR).build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Empty schema
        // Schema change: Yes
        {
            BatchSchema schema = new SchemaBuilder().build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        batchLoader.clear();
        allocator.close();
    }

    @Test
    public void testMapSchemaChange() throws SchemaChangeException {
        final BufferAllocator allocator = RootAllocatorFactory.newRoot(drillConfig);
        final RecordBatchLoader batchLoader = new RecordBatchLoader(allocator);
        // Initial schema: a: INT, m: MAP{}
        BatchSchema schema1 = new SchemaBuilder().add("a", INT).addMap("m").resumeSchema().build();
        {
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema1));
            Assert.assertTrue(schema1.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Same schema
        // Schema change: No
        {
            Assert.assertFalse(loadBatch(allocator, batchLoader, schema1));
            Assert.assertTrue(schema1.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Add column to map: a: INT, m: MAP{b: VARCHAR}
        // Schema change: Yes
        BatchSchema schema2 = new SchemaBuilder().add("a", INT).addMap("m").add("b", VARCHAR).resumeSchema().build();
        {
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema2));
            Assert.assertTrue(schema2.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Same schema
        // Schema change: No
        {
            Assert.assertFalse(loadBatch(allocator, batchLoader, schema2));
            Assert.assertTrue(schema2.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Add column:  a: INT, m: MAP{b: VARCHAR, c: INT}
        // Schema change: Yes
        {
            BatchSchema schema = new SchemaBuilder().add("a", INT).addMap("m").add("b", VARCHAR).add("c", INT).resumeSchema().build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Drop a column:  a: INT, m: MAP{b: VARCHAR}
        // Schema change: Yes
        {
            BatchSchema schema = new SchemaBuilder().add("a", INT).addMap("m").add("b", VARCHAR).resumeSchema().build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Change type:  a: INT, m: MAP{b: INT}
        // Schema change: Yes
        {
            BatchSchema schema = new SchemaBuilder().add("a", INT).addMap("m").add("b", INT).resumeSchema().build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Empty map: a: INT, m: MAP{}
        {
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema1));
            Assert.assertTrue(schema1.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        // Drop map: a: INT
        {
            BatchSchema schema = new SchemaBuilder().add("a", INT).build();
            Assert.assertTrue(loadBatch(allocator, batchLoader, schema));
            Assert.assertTrue(schema.isEquivalent(batchLoader.getSchema()));
            batchLoader.getContainer().zeroVectors();
        }
        batchLoader.clear();
        allocator.close();
    }
}

