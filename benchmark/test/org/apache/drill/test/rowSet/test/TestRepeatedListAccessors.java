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


import BaseRepeatedValueVector.DEFAULT_DATA_VECTOR;
import DataMode.REPEATED;
import MinorType.INT;
import MinorType.LIST;
import MinorType.VARCHAR;
import ObjectType.ARRAY;
import ObjectType.SCALAR;
import StructureType.MULTI_ARRAY;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.RepeatedVarCharVector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ObjectReader;
import org.apache.drill.exec.vector.accessor.ObjectWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.complex.RepeatedListVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.DirectRowSet;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetComparison;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.apache.drill.test.rowSet.RowSetWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test the basics of repeated list support in the schema builder,
 * column writers and column readers. These tests work with a
 * single row set (batch). These tests should pass before moving
 * on to the result set loader tests.
 */
@Category(RowSetTests.class)
public class TestRepeatedListAccessors extends SubOperatorTest {
    /**
     * Test the intermediate case in which a repeated list
     * does not yet have child type.
     */
    @Test
    public void testSchemaIncompleteBatch() {
        final BatchSchema schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").resumeSchema().build();
        Assert.assertEquals(2, schema.getFieldCount());
        final MaterializedField list = schema.getColumn(1);
        Assert.assertEquals("list2", list.getName());
        Assert.assertEquals(LIST, list.getType().getMinorType());
        Assert.assertEquals(REPEATED, list.getType().getMode());
        Assert.assertTrue(list.getChildren().isEmpty());
    }

    @Test
    public void testSchemaIncompleteMetadata() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").resumeSchema().buildSchema();
        Assert.assertEquals(2, schema.size());
        final ColumnMetadata list = schema.metadata(1);
        Assert.assertEquals("list2", list.name());
        Assert.assertEquals(LIST, list.type());
        Assert.assertEquals(REPEATED, list.mode());
        Assert.assertNull(list.childSchema());
    }

    /**
     * Test the case of a simple 2D array. Drill represents
     * this as two levels of materialized fields.
     */
    @Test
    public void testSchema2DBatch() {
        final BatchSchema schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().build();
        Assert.assertEquals(2, schema.getFieldCount());
        final MaterializedField list = schema.getColumn(1);
        Assert.assertEquals("list2", list.getName());
        Assert.assertEquals(LIST, list.getType().getMinorType());
        Assert.assertEquals(REPEATED, list.getType().getMode());
        Assert.assertEquals(1, size());
        final MaterializedField inner = list.getChildren().iterator().next();
        Assert.assertEquals("list2", inner.getName());
        Assert.assertEquals(VARCHAR, inner.getType().getMinorType());
        Assert.assertEquals(REPEATED, inner.getType().getMode());
    }

    /**
     * Test a 2D array using metadata. The metadata also uses
     * a column per dimension as that provides the easiest mapping
     * to the nested fields. A better design might be a single level
     * (as in repeated fields), but with a single attribute that
     * describes the number of dimensions. The <tt>dimensions()</tt>
     * method is a compromise.
     */
    @Test
    public void testSchema2DMetadata() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        Assert.assertEquals(2, schema.size());
        final ColumnMetadata list = schema.metadata(1);
        Assert.assertEquals("list2", list.name());
        Assert.assertEquals(LIST, list.type());
        Assert.assertEquals(REPEATED, list.mode());
        Assert.assertEquals(MULTI_ARRAY, list.structureType());
        Assert.assertTrue(list.isArray());
        Assert.assertEquals(2, list.dimensions());
        Assert.assertNotNull(list.childSchema());
        final ColumnMetadata child = list.childSchema();
        Assert.assertEquals("list2", child.name());
        Assert.assertEquals(VARCHAR, child.type());
        Assert.assertEquals(REPEATED, child.mode());
        Assert.assertTrue(child.isArray());
        Assert.assertEquals(1, child.dimensions());
        Assert.assertNull(child.childSchema());
    }

    @Test
    public void testSchema3DBatch() {
        final BatchSchema schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addDimension().addArray(VARCHAR).resumeList().resumeSchema().build();
        Assert.assertEquals(2, schema.getFieldCount());
        final MaterializedField list = schema.getColumn(1);
        Assert.assertEquals("list2", list.getName());
        Assert.assertEquals(LIST, list.getType().getMinorType());
        Assert.assertEquals(REPEATED, list.getType().getMode());
        Assert.assertEquals(1, size());
        final MaterializedField child1 = list.getChildren().iterator().next();
        Assert.assertEquals("list2", child1.getName());
        Assert.assertEquals(LIST, child1.getType().getMinorType());
        Assert.assertEquals(REPEATED, child1.getType().getMode());
        Assert.assertEquals(1, size());
        final MaterializedField child2 = child1.getChildren().iterator().next();
        Assert.assertEquals("list2", child2.getName());
        Assert.assertEquals(VARCHAR, child2.getType().getMinorType());
        Assert.assertEquals(REPEATED, child2.getType().getMode());
        Assert.assertEquals(0, size());
    }

    @Test
    public void testSchema3DMetadata() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addDimension().addArray(VARCHAR).resumeList().resumeSchema().buildSchema();
        Assert.assertEquals(2, schema.size());
        final ColumnMetadata list = schema.metadata(1);
        Assert.assertEquals("list2", list.name());
        Assert.assertEquals(LIST, list.type());
        Assert.assertEquals(REPEATED, list.mode());
        Assert.assertEquals(MULTI_ARRAY, list.structureType());
        Assert.assertTrue(list.isArray());
        Assert.assertEquals(3, list.dimensions());
        Assert.assertNotNull(list.childSchema());
        final ColumnMetadata child1 = list.childSchema();
        Assert.assertEquals("list2", child1.name());
        Assert.assertEquals(LIST, child1.type());
        Assert.assertEquals(REPEATED, child1.mode());
        Assert.assertEquals(MULTI_ARRAY, child1.structureType());
        Assert.assertTrue(child1.isArray());
        Assert.assertEquals(2, child1.dimensions());
        Assert.assertNotNull(child1.childSchema());
        final ColumnMetadata child2 = child1.childSchema();
        Assert.assertEquals("list2", child2.name());
        Assert.assertEquals(VARCHAR, child2.type());
        Assert.assertEquals(REPEATED, child2.mode());
        Assert.assertTrue(child2.isArray());
        Assert.assertEquals(1, child2.dimensions());
        Assert.assertNull(child2.childSchema());
    }

    @Test
    public void testIncompleteVectors() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").resumeSchema().buildSchema();
        final DirectRowSet rowSet = DirectRowSet.fromSchema(SubOperatorTest.fixture.allocator(), schema);
        final VectorContainer container = rowSet.container();
        Assert.assertEquals(2, container.getNumberOfColumns());
        Assert.assertTrue(((container.getValueVector(1).getValueVector()) instanceof RepeatedListVector));
        final RepeatedListVector list = ((RepeatedListVector) (container.getValueVector(1).getValueVector()));
        Assert.assertSame(DEFAULT_DATA_VECTOR, list.getDataVector());
        Assert.assertTrue(list.getField().getChildren().isEmpty());
        rowSet.clear();
    }

    @Test
    public void testSchema2DVector() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        final DirectRowSet rowSet = DirectRowSet.fromSchema(SubOperatorTest.fixture.allocator(), schema);
        final VectorContainer container = rowSet.container();
        Assert.assertEquals(2, container.getNumberOfColumns());
        Assert.assertTrue(((container.getValueVector(1).getValueVector()) instanceof RepeatedListVector));
        final RepeatedListVector list = ((RepeatedListVector) (container.getValueVector(1).getValueVector()));
        Assert.assertEquals(1, size());
        final ValueVector child = list.getDataVector();
        Assert.assertTrue((child instanceof RepeatedVarCharVector));
        Assert.assertSame(list.getField().getChildren().iterator().next(), child.getField());
        rowSet.clear();
    }

    @Test
    public void testSchema3DVector() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addDimension().addArray(VARCHAR).resumeList().resumeSchema().buildSchema();
        final DirectRowSet rowSet = DirectRowSet.fromSchema(SubOperatorTest.fixture.allocator(), schema);
        final VectorContainer container = rowSet.container();
        Assert.assertEquals(2, container.getNumberOfColumns());
        Assert.assertTrue(((container.getValueVector(1).getValueVector()) instanceof RepeatedListVector));
        final RepeatedListVector list = ((RepeatedListVector) (container.getValueVector(1).getValueVector()));
        Assert.assertEquals(1, size());
        Assert.assertTrue(((list.getDataVector()) instanceof RepeatedListVector));
        final RepeatedListVector child1 = ((RepeatedListVector) (list.getDataVector()));
        Assert.assertEquals(1, size());
        Assert.assertSame(list.getField().getChildren().iterator().next(), child1.getField());
        final ValueVector child2 = child1.getDataVector();
        Assert.assertTrue((child2 instanceof RepeatedVarCharVector));
        Assert.assertSame(child1.getField().getChildren().iterator().next(), child2.getField());
        rowSet.clear();
    }

    @Test
    public void testSchema2DWriterReader() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addRepeatedList("list2").addArray(VARCHAR).resumeSchema().buildSchema();
        final DirectRowSet rowSet = DirectRowSet.fromSchema(SubOperatorTest.fixture.allocator(), schema);
        RowSet.SingleRowSet result;
        {
            final RowSetWriter writer = rowSet.writer();
            Assert.assertEquals(2, size());
            final ObjectWriter listObj = column("list2");
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayWriter listWriter = listObj.array();
            Assert.assertEquals(ARRAY, listWriter.entryType());
            final ArrayWriter innerWriter = listWriter.array();
            Assert.assertEquals(SCALAR, innerWriter.entryType());
            final ScalarWriter strWriter = innerWriter.scalar();
            // Write one row using writers explicitly.
            // 
            // (1, [["a, "b"], ["c", "d"]])
            // 
            // Note auto increment of inner list on write.
            scalar("id").setInt(1);
            strWriter.setString("a");
            strWriter.setString("b");
            listWriter.save();
            strWriter.setString("c");
            strWriter.setString("d");
            listWriter.save();
            writer.save();
            // Write more rows using the convenience methods.
            // 
            // (2, [["e"], [], ["f", "g", "h"]])
            // (3, [])
            // (4, [[], ["i"], []])
            writer.addRow(2, RowSetUtilities.objArray(RowSetUtilities.strArray("e"), RowSetUtilities.strArray(), RowSetUtilities.strArray("f", "g", "h"))).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.strArray(), RowSetUtilities.strArray("i"), RowSetUtilities.strArray()));
            result = writer.done();
        }
        // Verify one row using the individual readers.
        {
            final RowSetReader reader = result.reader();
            Assert.assertEquals(2, columnCount());
            final ObjectReader listObj = reader.column("list2");
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayReader listReader = listObj.array();
            Assert.assertEquals(ARRAY, listReader.entryType());
            final ArrayReader innerReader = listReader.array();
            Assert.assertEquals(SCALAR, innerReader.entryType());
            final ScalarReader strReader = innerReader.scalar();
            // Write one row using writers explicitly.
            // 
            // (1, [["a, "b"], ["c", "d"]])
            Assert.assertTrue(reader.next());
            Assert.assertEquals(2, listReader.size());
            Assert.assertTrue(listReader.next());
            Assert.assertEquals(2, innerReader.size());
            Assert.assertTrue(innerReader.next());
            Assert.assertEquals("a", strReader.getString());
            Assert.assertTrue(innerReader.next());
            Assert.assertEquals("b", strReader.getString());
            Assert.assertFalse(innerReader.next());
            Assert.assertTrue(listReader.next());
            Assert.assertEquals(2, innerReader.size());
            Assert.assertTrue(innerReader.next());
            Assert.assertEquals("c", strReader.getString());
            Assert.assertTrue(innerReader.next());
            Assert.assertEquals("d", strReader.getString());
            Assert.assertFalse(innerReader.next());
            Assert.assertFalse(listReader.next());
        }
        // Verify both rows by building another row set and comparing.
        final RowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c", "d"))).addRow(2, RowSetUtilities.objArray(RowSetUtilities.strArray("e"), RowSetUtilities.strArray(), RowSetUtilities.strArray("f", "g", "h"))).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.strArray(), RowSetUtilities.strArray("i"), RowSetUtilities.strArray())).build();
        new RowSetComparison(expected).verify(result);
        // Test that the row set rebuilds its internal structure from
        // a vector container.
        RowSet wrapped = SubOperatorTest.fixture.wrap(result.container());
        RowSetUtilities.verify(expected, wrapped);
    }

    @Test
    public void testSchema3DWriterReader() {
        final TupleMetadata schema = // Uses a short-hand method to avoid mucking with actual
        // nested lists.
        new SchemaBuilder().add("id", INT).addArray("cube", VARCHAR, 3).buildSchema();
        final RowSet.SingleRowSet actual = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.objArray(RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c")), RowSetUtilities.objArray(RowSetUtilities.strArray("d", "e", "f"), null), null, RowSetUtilities.objArray())).addRow(2, null).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.objArray())).addRow(5, RowSetUtilities.singleObjArray(RowSetUtilities.objArray(RowSetUtilities.strArray("g", "h"), RowSetUtilities.strArray("i")))).build();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.objArray(RowSetUtilities.objArray(RowSetUtilities.strArray("a", "b"), RowSetUtilities.strArray("c")), RowSetUtilities.objArray(RowSetUtilities.strArray("d", "e", "f"), RowSetUtilities.strArray()), RowSetUtilities.objArray(), RowSetUtilities.objArray())).addRow(2, RowSetUtilities.objArray()).addRow(3, RowSetUtilities.objArray()).addRow(4, RowSetUtilities.objArray(RowSetUtilities.objArray())).addRow(5, RowSetUtilities.singleObjArray(RowSetUtilities.objArray(RowSetUtilities.strArray("g", "h"), RowSetUtilities.strArray("i")))).build();
        RowSetUtilities.verify(expected, actual);
    }
}

