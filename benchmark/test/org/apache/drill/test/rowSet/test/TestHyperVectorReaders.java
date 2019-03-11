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
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.record.selection.SelectionVector4;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.HyperRowSetImpl;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.apache.drill.test.rowSet.RowSetWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the reader mechanism that reads rows indexed via an SV4.
 * SV4's introduce an additional level of indexing: each row may
 * come from a different batch. The readers use the SV4 to find
 * the root batch and vector, then must navigate downward from that
 * vector for maps, repeated maps, lists, unions, repeated lists,
 * nullable vectors and variable-length vectors.
 * <p>
 * This test does not cover repeated vectors; those tests should be added.
 */
@Category(RowSetTests.class)
public class TestHyperVectorReaders extends SubOperatorTest {
    /**
     * Test the simplest case: a top-level required vector. Has no contained vectors.
     * This test focuses on the SV4 indirection mechanism itself.
     */
    @Test
    public void testRequired() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).buildSchema();
        RowSet.SingleRowSet rowSet1;
        {
            RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
            RowSetWriter writer = rowSet.writer();
            for (int i = 0; i < 10; i++) {
                scalar(0).setInt((i * 10));
                writer.save();
            }
            rowSet1 = writer.done();
        }
        RowSet.SingleRowSet rowSet2;
        {
            RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
            RowSetWriter writer = rowSet.writer();
            for (int i = 10; i < 20; i++) {
                scalar(0).setInt((i * 10));
                writer.save();
            }
            rowSet2 = writer.done();
        }
        // Build the hyper batch
        // [0, 10, 20, ... 190]
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(20, hyperSet.rowCount());
        // Populate the indirection vector:
        // (1, 9), (0, 9), (1, 8), (0, 8), ... (0, 0)
        SelectionVector4 sv4 = hyperSet.getSv4();
        for (int i = 0; i < 20; i++) {
            int batch = i % 2;
            int offset = 9 - (i / 2);
            sv4.set(i, batch, offset);
        }
        // Sanity check.
        for (int i = 0; i < 20; i++) {
            int batch = i % 2;
            int offset = 9 - (i / 2);
            int encoded = sv4.get(i);
            Assert.assertEquals(batch, SelectionVector4.getBatchIndex(encoded));
            Assert.assertEquals(offset, SelectionVector4.getRecordIndex(encoded));
        }
        // Verify reader
        // Expected: [190, 90, 180, 80, ... 0]
        RowSetReader reader = hyperSet.reader();
        for (int i = 0; i < 20; i++) {
            Assert.assertTrue(reader.next());
            int batch = i % 2;
            int offset = 9 - (i / 2);
            int expected = (batch * 100) + (offset * 10);
            Assert.assertEquals(expected, scalar(0).getInt());
        }
        Assert.assertFalse(reader.next());
        // Validate using an expected result set.
        RowSetBuilder rsBuilder = SubOperatorTest.fixture.rowSetBuilder(schema);
        for (int i = 0; i < 20; i++) {
            int batch = i % 2;
            int offset = 9 - (i / 2);
            int expected = (batch * 100) + (offset * 10);
            rsBuilder.addRow(expected);
        }
        RowSetUtilities.verify(rsBuilder.build(), hyperSet);
    }

    @Test
    public void testVarWidth() {
        TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR).buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol("second").addSingleCol("fourth").build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol("first").addSingleCol("third").build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(4, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 0);
        sv4.set(1, 0, 0);
        sv4.set(2, 1, 1);
        sv4.set(3, 0, 1);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow("first").addRow("second").addRow("third").addRow("fourth").build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    /**
     * Test a nullable varchar. Requires multiple indirections:
     * <ul>
     * <li>From the SV4 to the nullable vector.</li>
     * <li>From the nullable vector to the bits vector.</li>
     * <li>From the nullable vector to the data vector.</li>
     * <li>From the data vector to the offset vector.</li>
     * <li>From the data vector to the values vector.</li>
     * </ul>
     * All are coordinated by the vector index and vector accessors.
     * This test verifies that each of the indirections does, in fact,
     * work as expected.
     */
    @Test
    public void testOptional() {
        TupleMetadata schema = new SchemaBuilder().addNullable("a", VARCHAR).buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol("sixth").addSingleCol(null).addSingleCol("fourth").build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(null).addSingleCol("first").addSingleCol("third").build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(6, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 1);
        sv4.set(1, 0, 1);
        sv4.set(2, 1, 2);
        sv4.set(3, 0, 2);
        sv4.set(4, 1, 0);
        sv4.set(5, 0, 0);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol("first").addSingleCol(null).addSingleCol("third").addSingleCol("fourth").addSingleCol(null).addSingleCol("sixth").build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    /**
     * Test an array to test the indirection from the repeated vector
     * to the array offsets vector and the array values vector. (Uses
     * varchar to add another level of indirection to the data offset
     * and data values vectors.)
     */
    @Test
    public void testRepeated() {
        TupleMetadata schema = new SchemaBuilder().addArray("a", VARCHAR).buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.strArray("sixth", "6.1", "6.2")).addSingleCol(RowSetUtilities.strArray("second", "2.1", "2.2", "2.3")).addSingleCol(RowSetUtilities.strArray("fourth", "4.1")).build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.strArray("fifth", "51", "5.2")).addSingleCol(RowSetUtilities.strArray("first", "1.1", "1.2", "1.3")).addSingleCol(RowSetUtilities.strArray("third", "3.1")).build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(6, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 1);
        sv4.set(1, 0, 1);
        sv4.set(2, 1, 2);
        sv4.set(3, 0, 2);
        sv4.set(4, 1, 0);
        sv4.set(5, 0, 0);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.strArray("first", "1.1", "1.2", "1.3")).addSingleCol(RowSetUtilities.strArray("second", "2.1", "2.2", "2.3")).addSingleCol(RowSetUtilities.strArray("third", "3.1")).addSingleCol(RowSetUtilities.strArray("fourth", "4.1")).addSingleCol(RowSetUtilities.strArray("fifth", "51", "5.2")).addSingleCol(RowSetUtilities.strArray("sixth", "6.1", "6.2")).build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    /**
     * Maps are an interesting case. The hyper-vector wrapper holds a mirror-image of the
     * map members. So, we can reach the map members either via the vector wrappers or
     * the original map vector.
     */
    @Test
    public void testMap() {
        TupleMetadata schema = new SchemaBuilder().addMap("m").add("a", INT).add("b", VARCHAR).resumeSchema().buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.mapValue(2, "second")).addSingleCol(RowSetUtilities.mapValue(4, "fourth")).build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.mapValue(2, "first")).addSingleCol(RowSetUtilities.mapValue(4, "third")).build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(4, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 0);
        sv4.set(1, 0, 0);
        sv4.set(2, 1, 1);
        sv4.set(3, 0, 1);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.mapValue(2, "first")).addSingleCol(RowSetUtilities.mapValue(2, "second")).addSingleCol(RowSetUtilities.mapValue(4, "third")).addSingleCol(RowSetUtilities.mapValue(4, "fourth")).build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    @Test
    public void testRepeatedMap() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addMapArray("ma").add("b", INT).add("c", VARCHAR).resumeSchema().buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(2, RowSetUtilities.mapArray(RowSetUtilities.mapValue(21, "second.1"), RowSetUtilities.mapValue(22, "second.2"))).addRow(4, RowSetUtilities.mapArray(RowSetUtilities.mapValue(41, "fourth.1"))).build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.mapArray(RowSetUtilities.mapValue(11, "first.1"), RowSetUtilities.mapValue(12, "first.2"))).addRow(3, RowSetUtilities.mapArray(RowSetUtilities.mapValue(31, "third.1"), RowSetUtilities.mapValue(32, "third.2"), RowSetUtilities.mapValue(33, "third.3"))).build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(4, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 0);
        sv4.set(1, 0, 0);
        sv4.set(2, 1, 1);
        sv4.set(3, 0, 1);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.mapArray(RowSetUtilities.mapValue(11, "first.1"), RowSetUtilities.mapValue(12, "first.2"))).addRow(2, RowSetUtilities.mapArray(RowSetUtilities.mapValue(21, "second.1"), RowSetUtilities.mapValue(22, "second.2"))).addRow(3, RowSetUtilities.mapArray(RowSetUtilities.mapValue(31, "third.1"), RowSetUtilities.mapValue(32, "third.2"), RowSetUtilities.mapValue(33, "third.3"))).addRow(4, RowSetUtilities.mapArray(RowSetUtilities.mapValue(41, "fourth.1"))).build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    @Test
    public void testUnion() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addUnion("u").addType(INT).addType(VARCHAR).resumeSchema().buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(2, 20).addRow(4, "fourth").build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, "first").addRow(3, 30).build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(4, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 0);
        sv4.set(1, 0, 0);
        sv4.set(2, 1, 1);
        sv4.set(3, 0, 1);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, "first").addRow(2, 20).addRow(3, 30).addRow(4, "fourth").build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    @Test
    public void testScalarList() {
        TupleMetadata schema = new SchemaBuilder().addList("a").addType(VARCHAR).resumeSchema().buildSchema();
        schema.metadata("a").variantSchema().becomeSimple();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.strArray("sixth", "6.1", "6.2")).addSingleCol(null).addSingleCol(RowSetUtilities.strArray("fourth", "4.1")).build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.strArray("fifth", "51", "5.2")).addSingleCol(RowSetUtilities.strArray("first", "1.1", "1.2", "1.3")).addSingleCol(RowSetUtilities.strArray("third", "3.1")).build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(6, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 1);
        sv4.set(1, 0, 1);
        sv4.set(2, 1, 2);
        sv4.set(3, 0, 2);
        sv4.set(4, 1, 0);
        sv4.set(5, 0, 0);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.strArray("first", "1.1", "1.2", "1.3")).addSingleCol(null).addSingleCol(RowSetUtilities.strArray("third", "3.1")).addSingleCol(RowSetUtilities.strArray("fourth", "4.1")).addSingleCol(RowSetUtilities.strArray("fifth", "51", "5.2")).addSingleCol(RowSetUtilities.strArray("sixth", "6.1", "6.2")).build();
        RowSetUtilities.verify(expected, hyperSet);
    }

    @Test
    public void testUnionList() {
        TupleMetadata schema = new SchemaBuilder().add("a", INT).addList("list").addType(INT).addType(VARCHAR).resumeSchema().buildSchema();
        RowSet.SingleRowSet rowSet1 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(6, RowSetUtilities.variantArray("sixth", 61, "6.2")).addRow(2, RowSetUtilities.variantArray("second", "2.1", 22, "2.3")).addRow(4, RowSetUtilities.variantArray("fourth", 41)).build();
        RowSet.SingleRowSet rowSet2 = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(5, RowSetUtilities.variantArray("fifth", "5.1", 52)).addRow(1, RowSetUtilities.variantArray("first", 11, "1.2", 13)).addRow(3, RowSetUtilities.variantArray("third", 31)).build();
        // Build the hyper batch
        RowSet.HyperRowSet hyperSet = HyperRowSetImpl.fromRowSets(SubOperatorTest.fixture.allocator(), rowSet1, rowSet2);
        Assert.assertEquals(6, hyperSet.rowCount());
        SelectionVector4 sv4 = hyperSet.getSv4();
        sv4.set(0, 1, 1);
        sv4.set(1, 0, 1);
        sv4.set(2, 1, 2);
        sv4.set(3, 0, 2);
        sv4.set(4, 1, 0);
        sv4.set(5, 0, 0);
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.variantArray("first", 11, "1.2", 13)).addRow(2, RowSetUtilities.variantArray("second", "2.1", 22, "2.3")).addRow(3, RowSetUtilities.variantArray("third", 31)).addRow(4, RowSetUtilities.variantArray("fourth", 41)).addRow(5, RowSetUtilities.variantArray("fifth", "5.1", 52)).addRow(6, RowSetUtilities.variantArray("sixth", 61, "6.2")).build();
        RowSetUtilities.verify(expected, hyperSet);
    }
}

