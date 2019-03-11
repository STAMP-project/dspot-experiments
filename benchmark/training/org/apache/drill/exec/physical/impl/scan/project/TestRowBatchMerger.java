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
package org.apache.drill.exec.physical.impl.scan.project;


import MinorType.INT;
import MinorType.VARCHAR;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.physical.impl.scan.project.ResolvedTuple.ResolvedRow;
import org.apache.drill.exec.physical.rowSet.ResultVectorCache;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the row batch merger by merging two batches. Tests both the
 * "direct" and "exchange" cases. Direct means that the output container
 * contains the source vector directly: they are the same vectors.
 * Exchange means we have two vectors, but we swap the underlying
 * Drillbufs to effectively shift data from source to destination
 * vector.
 */
@Category(RowSetTests.class)
public class TestRowBatchMerger extends SubOperatorTest {
    public static class RowSetSource implements VectorSource {
        private RowSet.SingleRowSet rowSet;

        public RowSetSource(RowSet.SingleRowSet rowSet) {
            this.rowSet = rowSet;
        }

        public RowSet rowSet() {
            return rowSet;
        }

        public void clear() {
            rowSet.clear();
        }

        @Override
        public ValueVector vector(int index) {
            return rowSet.container().getValueVector(index).getValueVector();
        }
    }

    public static final int SLAB_SIZE = (16 * 1024) * 1024;

    public static class TestProjection extends ResolvedColumn {
        public TestProjection(VectorSource source, int sourceIndex) {
            super(source, sourceIndex);
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public int nodeType() {
            return -1;
        }

        @Override
        public MaterializedField schema() {
            return null;
        }
    }

    @Test
    public void testSimpleFlat() {
        // Create the first batch
        TestRowBatchMerger.RowSetSource first = makeFirst();
        // Create the second batch
        TestRowBatchMerger.RowSetSource second = makeSecond();
        ResolvedRow resolvedTuple = new ResolvedRow(null);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(first, 1));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(second, 0));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(second, 1));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(first, 0));
        // Do the merge
        VectorContainer output = new VectorContainer(SubOperatorTest.fixture.allocator());
        resolvedTuple.project(null, output);
        output.setRecordCount(first.rowSet().rowCount());
        RowSet result = SubOperatorTest.fixture.wrap(output);
        // Verify
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).add("b", INT).add("c", VARCHAR).add("d", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(10, 1, "foo.csv", "barney").addRow(20, 2, "foo.csv", "wilma").build();
        new RowSetComparison(expected).verifyAndClearAll(result);
    }

    @Test
    public void testImplicitFlat() {
        // Create the first batch
        TestRowBatchMerger.RowSetSource first = makeFirst();
        // Create the second batch
        TestRowBatchMerger.RowSetSource second = makeSecond();
        ResolvedRow resolvedTuple = new ResolvedRow(null);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 1));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(second, 0));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(second, 1));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 0));
        // Do the merge
        VectorContainer output = new VectorContainer(SubOperatorTest.fixture.allocator());
        resolvedTuple.project(first.rowSet().container(), output);
        output.setRecordCount(first.rowSet().rowCount());
        RowSet result = SubOperatorTest.fixture.wrap(output);
        // Verify
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).add("b", INT).add("c", VARCHAR).add("d", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(10, 1, "foo.csv", "barney").addRow(20, 2, "foo.csv", "wilma").build();
        new RowSetComparison(expected).verifyAndClearAll(result);
    }

    @Test
    public void testFlatWithNulls() {
        // Create the first batch
        TestRowBatchMerger.RowSetSource first = makeFirst();
        // Create null columns
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow resolvedTuple = new ResolvedRow(builder);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 1));
        resolvedTuple.add(resolvedTuple.nullBuilder().add("null1"));
        resolvedTuple.add(resolvedTuple.nullBuilder().add("null2", Types.optional(VARCHAR)));
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 0));
        // Build the null values
        ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        builder.build(cache);
        builder.load(first.rowSet().rowCount());
        // Do the merge
        VectorContainer output = new VectorContainer(SubOperatorTest.fixture.allocator());
        resolvedTuple.project(first.rowSet().container(), output);
        output.setRecordCount(first.rowSet().rowCount());
        RowSet result = SubOperatorTest.fixture.wrap(output);
        // Verify
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).addNullable("null1", INT).addNullable("null2", VARCHAR).add("d", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(10, null, null, "barney").addRow(20, null, null, "wilma").build();
        new RowSetComparison(expected).verifyAndClearAll(result);
        builder.close();
    }

    /**
     * Test the ability to create maps from whole cloth if requested in
     * the projection list, and the map is not available from the data
     * source.
     */
    @Test
    public void testNullMaps() {
        // Create the first batch
        TestRowBatchMerger.RowSetSource first = makeFirst();
        // Create null columns
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow resolvedTuple = new ResolvedRow(builder);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 1));
        ResolvedMapColumn nullMapCol = new ResolvedMapColumn(resolvedTuple, "map1");
        ResolvedTuple nullMap = nullMapCol.members();
        nullMap.add(nullMap.nullBuilder().add("null1"));
        nullMap.add(nullMap.nullBuilder().add("null2", Types.optional(VARCHAR)));
        ResolvedMapColumn nullMapCol2 = new ResolvedMapColumn(nullMap, "map2");
        ResolvedTuple nullMap2 = nullMapCol2.members();
        nullMap2.add(nullMap2.nullBuilder().add("null3"));
        nullMap.add(nullMapCol2);
        resolvedTuple.add(nullMapCol);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 0));
        // Build the null values
        ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        resolvedTuple.buildNulls(cache);
        // LoadNulls
        resolvedTuple.loadNulls(first.rowSet().rowCount());
        // Do the merge
        VectorContainer output = new VectorContainer(SubOperatorTest.fixture.allocator());
        resolvedTuple.project(first.rowSet().container(), output);
        resolvedTuple.setRowCount(first.rowSet().rowCount());
        RowSet result = SubOperatorTest.fixture.wrap(output);
        // Verify
        BatchSchema expectedSchema = new SchemaBuilder().add("a", INT).addMap("map1").addNullable("null1", INT).addNullable("null2", VARCHAR).addMap("map2").addNullable("null3", INT).resumeMap().resumeSchema().add("d", VARCHAR).build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(10, RowSetUtilities.mapValue(null, null, RowSetUtilities.singleMap(null)), "barney").addRow(20, RowSetUtilities.mapValue(null, null, RowSetUtilities.singleMap(null)), "wilma").build();
        new RowSetComparison(expected).verifyAndClearAll(result);
        resolvedTuple.close();
    }

    /**
     * Test that the merger mechanism can rewrite a map to include
     * projected null columns.
     */
    @Test
    public void testMapRevision() {
        // Create the first batch
        BatchSchema inputSchema = new SchemaBuilder().add("b", VARCHAR).addMap("a").add("c", INT).resumeSchema().build();
        TestRowBatchMerger.RowSetSource input = new TestRowBatchMerger.RowSetSource(SubOperatorTest.fixture.rowSetBuilder(inputSchema).addRow("barney", RowSetUtilities.singleMap(10)).addRow("wilma", RowSetUtilities.singleMap(20)).build());
        // Create mappings
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow resolvedTuple = new ResolvedRow(builder);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 0));
        ResolvedMapColumn mapCol = new ResolvedMapColumn(resolvedTuple, inputSchema.getColumn(1), 1);
        resolvedTuple.add(mapCol);
        ResolvedTuple map = mapCol.members();
        map.add(new TestRowBatchMerger.TestProjection(map, 0));
        map.add(map.nullBuilder().add("null1"));
        // Build the null values
        ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        resolvedTuple.buildNulls(cache);
        // LoadNulls
        resolvedTuple.loadNulls(input.rowSet().rowCount());
        // Do the merge
        VectorContainer output = new VectorContainer(SubOperatorTest.fixture.allocator());
        resolvedTuple.project(input.rowSet().container(), output);
        output.setRecordCount(input.rowSet().rowCount());
        RowSet result = SubOperatorTest.fixture.wrap(output);
        // Verify
        BatchSchema expectedSchema = new SchemaBuilder().add("b", VARCHAR).addMap("a").add("c", INT).addNullable("null1", INT).resumeSchema().build();
        RowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("barney", RowSetUtilities.mapValue(10, null)).addRow("wilma", RowSetUtilities.mapValue(20, null)).build();
        new RowSetComparison(expected).verifyAndClearAll(result);
    }

    /**
     * Test that the merger mechanism can rewrite a map array to include
     * projected null columns.
     */
    @Test
    public void testMapArrayRevision() {
        // Create the first batch
        BatchSchema inputSchema = new SchemaBuilder().add("b", VARCHAR).addMapArray("a").add("c", INT).resumeSchema().build();
        TestRowBatchMerger.RowSetSource input = new TestRowBatchMerger.RowSetSource(SubOperatorTest.fixture.rowSetBuilder(inputSchema).addRow("barney", RowSetUtilities.mapArray(RowSetUtilities.singleMap(10), RowSetUtilities.singleMap(11), RowSetUtilities.singleMap(12))).addRow("wilma", RowSetUtilities.mapArray(RowSetUtilities.singleMap(20), RowSetUtilities.singleMap(21))).build());
        // Create mappings
        NullColumnBuilder builder = new NullColumnBuilder(null, false);
        ResolvedRow resolvedTuple = new ResolvedRow(builder);
        resolvedTuple.add(new TestRowBatchMerger.TestProjection(resolvedTuple, 0));
        ResolvedMapColumn mapCol = new ResolvedMapColumn(resolvedTuple, inputSchema.getColumn(1), 1);
        resolvedTuple.add(mapCol);
        ResolvedTuple map = mapCol.members();
        map.add(new TestRowBatchMerger.TestProjection(map, 0));
        map.add(map.nullBuilder().add("null1"));
        // Build the null values
        ResultVectorCache cache = new org.apache.drill.exec.physical.rowSet.impl.NullResultVectorCacheImpl(SubOperatorTest.fixture.allocator());
        resolvedTuple.buildNulls(cache);
        // LoadNulls
        resolvedTuple.loadNulls(input.rowSet().rowCount());
        // Do the merge
        VectorContainer output = new VectorContainer(SubOperatorTest.fixture.allocator());
        resolvedTuple.project(input.rowSet().container(), output);
        output.setRecordCount(input.rowSet().rowCount());
        RowSet result = SubOperatorTest.fixture.wrap(output);
        // Verify
        BatchSchema expectedSchema = new SchemaBuilder().add("b", VARCHAR).addMapArray("a").add("c", INT).addNullable("null1", INT).resumeSchema().build();
        RowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow("barney", RowSetUtilities.mapArray(RowSetUtilities.mapValue(10, null), RowSetUtilities.mapValue(11, null), RowSetUtilities.mapValue(12, null))).addRow("wilma", RowSetUtilities.mapArray(RowSetUtilities.mapValue(20, null), RowSetUtilities.mapValue(21, null))).build();
        new RowSetComparison(expected).verifyAndClearAll(result);
    }
}

