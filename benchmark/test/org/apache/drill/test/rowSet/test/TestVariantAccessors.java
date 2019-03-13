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


import MinorType.BIGINT;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.LIST;
import MinorType.MAP;
import MinorType.UNION;
import MinorType.VARCHAR;
import ObjectType.ARRAY;
import ObjectType.SCALAR;
import ObjectType.TUPLE;
import ObjectType.VARIANT;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.TypeProtos.MinorType;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.VectorContainer;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.NullableBigIntVector;
import org.apache.drill.exec.vector.NullableFloat8Vector;
import org.apache.drill.exec.vector.NullableIntVector;
import org.apache.drill.exec.vector.NullableVarCharVector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.accessor.ArrayReader;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ObjectReader;
import org.apache.drill.exec.vector.accessor.ObjectWriter;
import org.apache.drill.exec.vector.accessor.ScalarReader;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleReader;
import org.apache.drill.exec.vector.accessor.TupleWriter;
import org.apache.drill.exec.vector.accessor.VariantReader;
import org.apache.drill.exec.vector.accessor.VariantWriter;
import org.apache.drill.exec.vector.complex.ListVector;
import org.apache.drill.exec.vector.complex.MapVector;
import org.apache.drill.exec.vector.complex.UnionVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for readers and writers for union and list types.
 * <p>
 * Note that the union type is only partially supported in Drill.
 * The list type is unsupported. (However, the list type works in
 * the schema builder, row set writer, row set reader and the
 * result set builder. It does not, however, work in the Project
 * and other operators. Some assembly required for future use.)
 */
// TODO: Repeated list
@Category(RowSetTests.class)
public class TestVariantAccessors extends SubOperatorTest {
    @Test
    public void testBuildRowSetUnion() {
        final TupleMetadata schema = // Union with simple and complex types
        new SchemaBuilder().addUnion("u").addType(INT).addMap().addNullable("c", BIGINT).addNullable("d", VARCHAR).resumeUnion().addList().addType(VARCHAR).resumeUnion().resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final VectorContainer vc = rowSet.container();
        Assert.assertEquals(1, vc.getNumberOfColumns());
        // Single union
        final ValueVector vector = vc.getValueVector(0).getValueVector();
        Assert.assertTrue((vector instanceof UnionVector));
        final UnionVector union = ((UnionVector) (vector));
        final MapVector typeMap = union.getTypeMap();
        ValueVector member = typeMap.getChild(INT.name());
        Assert.assertTrue((member instanceof NullableIntVector));
        // Inner map
        member = typeMap.getChild(MAP.name());
        Assert.assertTrue((member instanceof MapVector));
        member = typeMap.getChild(MAP.name());
        Assert.assertTrue((member instanceof MapVector));
        final MapVector childMap = ((MapVector) (member));
        ValueVector mapMember = childMap.getChild("c");
        Assert.assertNotNull(mapMember);
        Assert.assertTrue((mapMember instanceof NullableBigIntVector));
        mapMember = childMap.getChild("d");
        Assert.assertNotNull(mapMember);
        Assert.assertTrue((mapMember instanceof NullableVarCharVector));
        // Inner list
        member = typeMap.getChild(LIST.name());
        Assert.assertTrue((member instanceof ListVector));
        final ListVector list = ((ListVector) (member));
        Assert.assertTrue(((list.getDataVector()) instanceof NullableVarCharVector));
        rowSet.clear();
    }

    /**
     * Test a variant (AKA "union vector") at the top level, using
     * just scalar values.
     */
    @Test
    public void testScalarVariant() {
        final TupleMetadata schema = new SchemaBuilder().addUnion("u").addType(INT).addType(VARCHAR).addType(FLOAT8).resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rs = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rs.writer();
        // Sanity check of writer structure
        final ObjectWriter wo = column(0);
        Assert.assertEquals(VARIANT, wo.type());
        final VariantWriter vw = wo.variant();
        Assert.assertSame(vw, writer.variant(0));
        Assert.assertSame(vw, variant("u"));
        Assert.assertTrue(vw.hasType(INT));
        Assert.assertTrue(vw.hasType(VARCHAR));
        Assert.assertTrue(vw.hasType(FLOAT8));
        // Write values of different types
        vw.scalar(INT).setInt(10);
        writer.save();
        vw.scalar(VARCHAR).setString("fred");
        writer.save();
        // The entire variant is null
        vw.setNull();
        writer.save();
        vw.scalar(FLOAT8).setDouble(123.45);
        writer.save();
        // Strange case: just the value is null, but the variant
        // is not null.
        vw.scalar(INT).setNull();
        writer.save();
        // Marker to avoid fill-empty issues (fill-empties tested elsewhere.)
        vw.scalar(INT).setInt(20);
        writer.save();
        final RowSet.SingleRowSet result = writer.done();
        Assert.assertEquals(6, result.rowCount());
        // Read the values.
        final RowSetReader reader = result.reader();
        // Sanity check of structure
        final ObjectReader ro = reader.column(0);
        Assert.assertEquals(VARIANT, ro.type());
        final VariantReader vr = ro.variant();
        Assert.assertSame(vr, reader.variant(0));
        Assert.assertSame(vr, variant("u"));
        for (final MinorType type : MinorType.values()) {
            if (((type == (MinorType.INT)) || (type == (MinorType.VARCHAR))) || (type == (MinorType.FLOAT8))) {
                Assert.assertTrue(vr.hasType(type));
            } else {
                Assert.assertFalse(vr.hasType(type));
            }
        }
        // Can get readers up front
        final ScalarReader intReader = vr.scalar(INT);
        final ScalarReader strReader = vr.scalar(VARCHAR);
        final ScalarReader floatReader = vr.scalar(FLOAT8);
        // Verify the data
        // Int 10
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), INT);
        Assert.assertSame(intReader, vr.scalar());
        Assert.assertNotNull(vr.member());
        Assert.assertSame(vr.scalar(), vr.member().scalar());
        Assert.assertFalse(intReader.isNull());
        Assert.assertEquals(10, intReader.getInt());
        Assert.assertTrue(strReader.isNull());
        Assert.assertTrue(floatReader.isNull());
        // String "fred"
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), VARCHAR);
        Assert.assertSame(strReader, vr.scalar());
        Assert.assertFalse(strReader.isNull());
        Assert.assertEquals("fred", strReader.getString());
        Assert.assertTrue(intReader.isNull());
        Assert.assertTrue(floatReader.isNull());
        // Null value
        Assert.assertTrue(reader.next());
        Assert.assertTrue(vr.isNull());
        Assert.assertNull(vr.dataType());
        Assert.assertNull(vr.scalar());
        Assert.assertTrue(intReader.isNull());
        Assert.assertTrue(strReader.isNull());
        Assert.assertTrue(floatReader.isNull());
        // Double 123.45
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), FLOAT8);
        Assert.assertSame(floatReader, vr.scalar());
        Assert.assertFalse(floatReader.isNull());
        Assert.assertEquals(123.45, vr.scalar().getDouble(), 0.001);
        Assert.assertTrue(intReader.isNull());
        Assert.assertTrue(strReader.isNull());
        // Strange case: null int (but union is not null)
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), INT);
        Assert.assertTrue(intReader.isNull());
        // Int 20
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertFalse(intReader.isNull());
        Assert.assertEquals(20, intReader.getInt());
        Assert.assertFalse(reader.next());
        result.clear();
    }

    @SuppressWarnings("resource")
    @Test
    public void testBuildRowSetScalarList() {
        final TupleMetadata schema = // Top-level single-element list
        new SchemaBuilder().addList("list2").addType(VARCHAR).resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final VectorContainer vc = rowSet.container();
        Assert.assertEquals(1, vc.getNumberOfColumns());
        // Single-type list
        final ValueVector vector = vc.getValueVector(0).getValueVector();
        Assert.assertTrue((vector instanceof ListVector));
        final ListVector list = ((ListVector) (vector));
        Assert.assertTrue(((list.getDataVector()) instanceof NullableVarCharVector));
        rowSet.clear();
    }

    @SuppressWarnings("resource")
    @Test
    public void testBuildRowSetUnionArray() {
        final TupleMetadata schema = // Nested single-element list
        // List with multiple types
        new SchemaBuilder().addList("list1").addType(BIGINT).addMap().addNullable("a", INT).addNullable("b", VARCHAR).resumeUnion().addList().addType(FLOAT8).resumeUnion().resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final VectorContainer vc = rowSet.container();
        Assert.assertEquals(1, vc.getNumberOfColumns());
        // List with complex internal structure
        final ValueVector vector = vc.getValueVector(0).getValueVector();
        Assert.assertTrue((vector instanceof ListVector));
        final ListVector list = ((ListVector) (vector));
        Assert.assertTrue(((list.getDataVector()) instanceof UnionVector));
        final UnionVector union = ((UnionVector) (list.getDataVector()));
        // Union inside the list
        final MajorType unionType = union.getField().getType();
        final List<MinorType> types = unionType.getSubTypeList();
        Assert.assertEquals(3, types.size());
        Assert.assertTrue(types.contains(BIGINT));
        Assert.assertTrue(types.contains(MAP));
        Assert.assertTrue(types.contains(LIST));
        final MapVector typeMap = union.getTypeMap();
        ValueVector member = typeMap.getChild(BIGINT.name());
        Assert.assertTrue((member instanceof NullableBigIntVector));
        // Map inside the list
        member = typeMap.getChild(MAP.name());
        Assert.assertTrue((member instanceof MapVector));
        final MapVector childMap = ((MapVector) (member));
        ValueVector mapMember = childMap.getChild("a");
        Assert.assertNotNull(mapMember);
        Assert.assertTrue((mapMember instanceof NullableIntVector));
        mapMember = childMap.getChild("b");
        Assert.assertNotNull(mapMember);
        Assert.assertTrue((mapMember instanceof NullableVarCharVector));
        // Single-type list inside the outer list
        member = typeMap.getChild(LIST.name());
        Assert.assertTrue((member instanceof ListVector));
        final ListVector childList = ((ListVector) (member));
        Assert.assertTrue(((childList.getDataVector()) instanceof NullableFloat8Vector));
        rowSet.clear();
    }

    /**
     * Test a variant (AKA "union vector") at the top level which
     * includes a map.
     */
    @Test
    public void testUnionWithMap() {
        final TupleMetadata schema = new SchemaBuilder().addUnion("u").addType(VARCHAR).addMap().addNullable("a", INT).addNullable("b", VARCHAR).resumeUnion().resumeSchema().buildSchema();
        RowSet.SingleRowSet result;
        // Write values
        {
            final RowSet.ExtendableRowSet rs = SubOperatorTest.fixture.rowSet(schema);
            final RowSetWriter writer = rs.writer();
            // Sanity check of writer structure
            final ObjectWriter wo = column(0);
            Assert.assertEquals(VARIANT, wo.type());
            final VariantWriter vw = wo.variant();
            Assert.assertTrue(vw.hasType(VARCHAR));
            final ObjectWriter strObj = vw.member(VARCHAR);
            final ScalarWriter strWriter = strObj.scalar();
            Assert.assertSame(strWriter, vw.scalar(VARCHAR));
            Assert.assertTrue(vw.hasType(MAP));
            final ObjectWriter mapObj = vw.member(MAP);
            final TupleWriter mWriter = mapObj.tuple();
            Assert.assertSame(mWriter, vw.tuple());
            final ScalarWriter aWriter = mWriter.scalar("a");
            final ScalarWriter bWriter = mWriter.scalar("b");
            // First row: string "first"
            vw.setType(VARCHAR);
            strWriter.setString("first");
            writer.save();
            // Second row: a map
            vw.setType(MAP);
            aWriter.setInt(20);
            bWriter.setString("fred");
            writer.save();
            // Third row: null
            vw.setNull();
            writer.save();
            // Fourth row: map with a null string
            vw.setType(MAP);
            aWriter.setInt(40);
            bWriter.setNull();
            writer.save();
            // Fifth row: string "last"
            vw.setType(VARCHAR);
            strWriter.setString("last");
            writer.save();
            result = writer.done();
            Assert.assertEquals(5, result.rowCount());
        }
        // Read the values.
        {
            final RowSetReader reader = result.reader();
            // Sanity check of structure
            final ObjectReader ro = reader.column(0);
            Assert.assertEquals(VARIANT, ro.type());
            final VariantReader vr = ro.variant();
            Assert.assertTrue(vr.hasType(VARCHAR));
            final ObjectReader strObj = vr.member(VARCHAR);
            final ScalarReader strReader = strObj.scalar();
            Assert.assertSame(strReader, vr.scalar(VARCHAR));
            Assert.assertTrue(vr.hasType(MAP));
            final ObjectReader mapObj = vr.member(MAP);
            final TupleReader mReader = mapObj.tuple();
            Assert.assertSame(mReader, vr.tuple());
            final ScalarReader aReader = mReader.scalar("a");
            final ScalarReader bReader = mReader.scalar("b");
            // First row: string "first"
            Assert.assertTrue(reader.next());
            Assert.assertFalse(vr.isNull());
            Assert.assertEquals(VARCHAR, vr.dataType());
            Assert.assertFalse(strReader.isNull());
            Assert.assertTrue(mReader.isNull());
            Assert.assertEquals("first", strReader.getString());
            // Second row: a map
            Assert.assertTrue(reader.next());
            Assert.assertFalse(vr.isNull());
            Assert.assertEquals(MAP, vr.dataType());
            Assert.assertTrue(strReader.isNull());
            Assert.assertFalse(mReader.isNull());
            Assert.assertFalse(aReader.isNull());
            Assert.assertEquals(20, aReader.getInt());
            Assert.assertFalse(bReader.isNull());
            Assert.assertEquals("fred", bReader.getString());
            // Third row: null
            Assert.assertTrue(reader.next());
            Assert.assertTrue(vr.isNull());
            Assert.assertTrue(strReader.isNull());
            Assert.assertTrue(mReader.isNull());
            Assert.assertTrue(aReader.isNull());
            Assert.assertTrue(bReader.isNull());
            // Fourth row: map with a null string
            Assert.assertTrue(reader.next());
            Assert.assertEquals(MAP, vr.dataType());
            Assert.assertEquals(40, aReader.getInt());
            Assert.assertTrue(bReader.isNull());
            // Fifth row: string "last"
            Assert.assertTrue(reader.next());
            Assert.assertEquals(VARCHAR, vr.dataType());
            Assert.assertEquals("last", strReader.getString());
            Assert.assertFalse(reader.next());
        }
        result.clear();
    }

    /**
     * Test a scalar list. Should act just like a repeated type, with the
     * addition of allowing the list for a row to be null. But, a list
     * writer does not do auto-increment, so we must do that explicitly
     * after each write.
     */
    @Test
    public void testScalarList() {
        final TupleMetadata schema = new SchemaBuilder().addList("list").addType(VARCHAR).resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        {
            final ObjectWriter listObj = column(0);
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayWriter listArray = listObj.array();
            // The list contains only a scalar. But, because lists can,
            // in general, contain multiple contents, the list requires
            // an explicit save after each entry.
            final ObjectWriter itemObj = listArray.entry();
            Assert.assertEquals(SCALAR, itemObj.type());
            final ScalarWriter strWriter = itemObj.scalar();
            // First row: two strings and a null
            // Unlike a repeated type, a list can mark individual elements
            // as null.
            // List will automatically detect that data was written.
            strWriter.setString("fred");
            listArray.save();
            strWriter.setNull();
            listArray.save();
            strWriter.setString("wilma");
            listArray.save();
            writer.save();
            // Second row: null
            writer.save();
            // Third row: one string
            strWriter.setString("dino");
            listArray.save();
            writer.save();
            // Fourth row: empty array. Note that there is no trigger
            // to say that the column is not null, so we have to do it
            // explicitly.
            listArray.setNull(false);
            writer.save();
            // Last row: a null string and non-null
            strWriter.setNull();
            listArray.save();
            strWriter.setString("pebbles");
            listArray.save();
            writer.save();
        }
        final RowSet.SingleRowSet result = writer.done();
        Assert.assertEquals(5, result.rowCount());
        {
            final RowSetReader reader = result.reader();
            final ObjectReader listObj = reader.column(0);
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayReader listArray = listObj.array();
            // The list is a repeated scalar
            Assert.assertEquals(SCALAR, listArray.entry().type());
            final ScalarReader strReader = listArray.scalar();
            // First row: two strings and a null
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(3, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertFalse(strReader.isNull());
            Assert.assertEquals("fred", strReader.getString());
            Assert.assertTrue(listArray.next());
            Assert.assertTrue(strReader.isNull());
            Assert.assertTrue(listArray.next());
            Assert.assertFalse(strReader.isNull());
            Assert.assertEquals("wilma", strReader.getString());
            Assert.assertFalse(listArray.next());
            // Second row: null
            Assert.assertTrue(reader.next());
            Assert.assertTrue(listArray.isNull());
            Assert.assertEquals(0, listArray.size());
            // Third row: one string
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(1, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals("dino", strReader.getString());
            Assert.assertFalse(listArray.next());
            // Fourth row: empty array.
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(0, listArray.size());
            Assert.assertFalse(listArray.next());
            // Last row: a null string and non-null
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(2, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertTrue(strReader.isNull());
            Assert.assertTrue(listArray.next());
            Assert.assertFalse(strReader.isNull());
            Assert.assertEquals("pebbles", strReader.getString());
            Assert.assertFalse(listArray.next());
            Assert.assertFalse(reader.next());
        }
        result.clear();
    }

    /**
     * List of maps. Like a repeated map, but each list entry can be
     * null.
     */
    @Test
    public void testListOfMaps() {
        final TupleMetadata schema = new SchemaBuilder().addList("list").addMap().addNullable("a", INT).addNullable("b", VARCHAR).resumeUnion().resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        {
            final ObjectWriter listObj = writer.column("list");
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayWriter listArray = listObj.array();
            final ObjectWriter itemObj = listArray.entry();
            Assert.assertEquals(TUPLE, itemObj.type());
            final TupleWriter mapWriter = itemObj.tuple();
            final ScalarWriter aWriter = mapWriter.scalar("a");
            final ScalarWriter bWriter = mapWriter.scalar("b");
            // First row:
            // {1, "fred"}, null, {3, null}
            aWriter.setInt(1);
            bWriter.setString("fred");
            listArray.save();
            // Can't mark the map as null. Instead, we simply skip
            // the map and the contained nullable members will automatically
            // back-fill each entry with a null value.
            listArray.save();
            aWriter.setInt(3);
            bWriter.setNull();
            listArray.save();
            writer.save();
            // Second row: null
            writer.save();
            // Third row: {null, "dino"}
            aWriter.setNull();
            bWriter.setString("dino");
            listArray.save();
            writer.save();
            // Fourth row: empty array. Note that there is no trigger
            // to say that the column is not null, so we have to do it
            // explicitly.
            listArray.setNull(false);
            writer.save();
            // Last row: {4, "pebbles"}
            aWriter.setInt(4);
            bWriter.setString("pebbles");
            listArray.save();
            writer.save();
        }
        final RowSet.SingleRowSet result = writer.done();
        Assert.assertEquals(5, result.rowCount());
        {
            final RowSetReader reader = result.reader();
            final ObjectReader listObj = reader.column("list");
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayReader listArray = listObj.array();
            Assert.assertEquals(TUPLE, listArray.entry().type());
            final TupleReader mapReader = listArray.tuple();
            final ScalarReader aReader = mapReader.scalar("a");
            final ScalarReader bReader = mapReader.scalar("b");
            // First row:
            // {1, "fred"}, null, {3, null}
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertFalse(mapReader.isNull());
            Assert.assertEquals(3, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertFalse(aReader.isNull());
            Assert.assertEquals(1, aReader.getInt());
            Assert.assertFalse(bReader.isNull());
            Assert.assertEquals("fred", bReader.getString());
            Assert.assertTrue(listArray.next());
            // Awkward: the map has no null state, but its
            // members do.
            Assert.assertTrue(aReader.isNull());
            Assert.assertTrue(bReader.isNull());
            Assert.assertTrue(listArray.next());
            Assert.assertFalse(aReader.isNull());
            Assert.assertEquals(3, aReader.getInt());
            Assert.assertTrue(bReader.isNull());
            Assert.assertFalse(listArray.next());
            // Second row: null
            Assert.assertTrue(reader.next());
            Assert.assertTrue(listArray.isNull());
            Assert.assertEquals(0, listArray.size());
            // Third row: {null, "dino"}
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(1, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertTrue(aReader.isNull());
            Assert.assertFalse(bReader.isNull());
            Assert.assertEquals("dino", bReader.getString());
            Assert.assertFalse(listArray.next());
            // Fourth row: empty array.
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(0, listArray.size());
            Assert.assertFalse(listArray.next());
            // Last row: {4, "pebbles"}
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(1, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(4, aReader.getInt());
            Assert.assertEquals("pebbles", bReader.getString());
            Assert.assertFalse(listArray.next());
            Assert.assertFalse(reader.next());
        }
        result.clear();
    }

    /**
     * Test a union list.
     */
    @Test
    public void testListOfUnions() {
        final TupleMetadata schema = new SchemaBuilder().addList("list").addType(INT).addType(VARCHAR).resumeSchema().buildSchema();
        final RowSet.ExtendableRowSet rowSet = SubOperatorTest.fixture.rowSet(schema);
        final RowSetWriter writer = rowSet.writer();
        {
            final ObjectWriter listObj = column(0);
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayWriter listArray = listObj.array();
            final ObjectWriter itemObj = listArray.entry();
            Assert.assertEquals(VARIANT, itemObj.type());
            final VariantWriter variant = itemObj.variant();
            final ScalarWriter intWriter = variant.scalar(INT);
            final ScalarWriter strWriter = variant.scalar(VARCHAR);
            // First row: (1, "two", 3)
            variant.setType(INT);
            intWriter.setInt(1);
            listArray.save();
            variant.setType(VARCHAR);
            strWriter.setString("two");
            listArray.save();
            variant.setType(INT);
            intWriter.setInt(3);
            listArray.save();
            writer.save();
            // Second row: null
            writer.save();
            // Third row: 4, null, "six", null int, null string
            variant.setType(INT);
            intWriter.setInt(4);
            listArray.save();
            variant.setNull();
            listArray.save();
            variant.setType(VARCHAR);
            strWriter.setString("six");
            listArray.save();
            variant.setType(INT);
            intWriter.setNull();
            listArray.save();
            variant.setType(VARCHAR);
            intWriter.setNull();
            listArray.save();
            writer.save();
            // Fourth row: empty array.
            listArray.setNull(false);
            writer.save();
            // Fifth row: 9
            variant.setType(INT);
            intWriter.setInt(9);
            listArray.save();
            writer.save();
        }
        final RowSet.SingleRowSet result = writer.done();
        Assert.assertEquals(5, result.rowCount());
        {
            final RowSetReader reader = result.reader();
            final ObjectReader listObj = reader.column(0);
            Assert.assertEquals(ARRAY, listObj.type());
            final ArrayReader listArray = listObj.array();
            Assert.assertEquals(VARIANT, listArray.entry().type());
            final VariantReader variant = listArray.variant();
            final ScalarReader intReader = variant.scalar(INT);
            final ScalarReader strReader = variant.scalar(VARCHAR);
            // First row: (1, "two", 3)
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(3, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(INT, variant.dataType());
            Assert.assertFalse(intReader.isNull());
            Assert.assertTrue(strReader.isNull());
            Assert.assertEquals(1, intReader.getInt());
            Assert.assertEquals(1, variant.scalar().getInt());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(VARCHAR, variant.dataType());
            Assert.assertTrue(intReader.isNull());
            Assert.assertFalse(strReader.isNull());
            Assert.assertEquals("two", strReader.getString());
            Assert.assertEquals("two", variant.scalar().getString());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(INT, variant.dataType());
            Assert.assertEquals(3, intReader.getInt());
            Assert.assertFalse(listArray.next());
            // Second row: null
            Assert.assertTrue(reader.next());
            Assert.assertTrue(listArray.isNull());
            Assert.assertEquals(0, listArray.size());
            // Third row: 4, null, "six", null int, null string
            Assert.assertTrue(reader.next());
            Assert.assertEquals(5, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(4, intReader.getInt());
            Assert.assertTrue(listArray.next());
            Assert.assertTrue(variant.isNull());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals("six", strReader.getString());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(INT, variant.dataType());
            Assert.assertTrue(intReader.isNull());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(VARCHAR, variant.dataType());
            Assert.assertTrue(strReader.isNull());
            Assert.assertFalse(listArray.next());
            // Fourth row: empty array.
            Assert.assertTrue(reader.next());
            Assert.assertFalse(listArray.isNull());
            Assert.assertEquals(0, listArray.size());
            Assert.assertFalse(listArray.next());
            // Fifth row: 9
            Assert.assertTrue(reader.next());
            Assert.assertEquals(1, listArray.size());
            Assert.assertTrue(listArray.next());
            Assert.assertEquals(9, intReader.getInt());
            Assert.assertFalse(listArray.next());
            Assert.assertFalse(reader.next());
        }
        result.clear();
    }

    /**
     * Test a variant (AKA "union vector") at the top level, using
     * just scalar values.
     */
    @Test
    public void testAddTypes() {
        final BatchSchema batchSchema = new SchemaBuilder().addNullable("v", UNION).build();
        final RowSet.ExtendableRowSet rs = SubOperatorTest.fixture.rowSet(batchSchema);
        final RowSetWriter writer = rs.writer();
        // Sanity check of writer structure
        final ObjectWriter wo = column(0);
        Assert.assertEquals(VARIANT, wo.type());
        final VariantWriter vw = wo.variant();
        Assert.assertSame(vw, writer.variant(0));
        Assert.assertSame(vw, variant("v"));
        for (final MinorType type : MinorType.values()) {
            Assert.assertFalse(vw.hasType(type));
        }
        // Write values of different types
        vw.scalar(INT).setInt(10);
        Assert.assertTrue(vw.hasType(INT));
        Assert.assertFalse(vw.hasType(VARCHAR));
        writer.save();
        vw.scalar(VARCHAR).setString("fred");
        Assert.assertTrue(vw.hasType(VARCHAR));
        writer.save();
        vw.setNull();
        writer.save();
        vw.scalar(FLOAT8).setDouble(123.45);
        Assert.assertTrue(vw.hasType(INT));
        Assert.assertTrue(vw.hasType(FLOAT8));
        writer.save();
        final RowSet.SingleRowSet result = writer.done();
        Assert.assertEquals(4, result.rowCount());
        // Read the values.
        final RowSetReader reader = result.reader();
        // Sanity check of structure
        final ObjectReader ro = reader.column(0);
        Assert.assertEquals(VARIANT, ro.type());
        final VariantReader vr = ro.variant();
        Assert.assertSame(vr, reader.variant(0));
        Assert.assertSame(vr, variant("v"));
        for (final MinorType type : MinorType.values()) {
            if (((type == (MinorType.INT)) || (type == (MinorType.VARCHAR))) || (type == (MinorType.FLOAT8))) {
                Assert.assertTrue(vr.hasType(type));
            } else {
                Assert.assertFalse(vr.hasType(type));
            }
        }
        // Verify the data
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), INT);
        Assert.assertSame(vr.scalar(INT), vr.scalar());
        Assert.assertNotNull(vr.member());
        Assert.assertSame(vr.scalar(), vr.member().scalar());
        Assert.assertEquals(10, vr.scalar().getInt());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), VARCHAR);
        Assert.assertSame(vr.scalar(VARCHAR), vr.scalar());
        Assert.assertEquals("fred", vr.scalar().getString());
        Assert.assertTrue(reader.next());
        Assert.assertTrue(vr.isNull());
        Assert.assertNull(vr.dataType());
        Assert.assertNull(vr.scalar());
        Assert.assertTrue(reader.next());
        Assert.assertFalse(vr.isNull());
        Assert.assertSame(vr.dataType(), FLOAT8);
        Assert.assertSame(vr.scalar(FLOAT8), vr.scalar());
        Assert.assertEquals(123.45, vr.scalar().getDouble(), 0.001);
        Assert.assertFalse(reader.next());
        result.clear();
    }

    /**
     * Test a variant (AKA "union vector") at the top level which includes
     * a list.
     */
    @Test
    public void testUnionWithList() {
        final TupleMetadata schema = new SchemaBuilder().addUnion("u").addType(INT).addList().addType(VARCHAR).resumeUnion().resumeSchema().buildSchema();
        RowSet.SingleRowSet result;
        // Write values
        {
            final RowSet.ExtendableRowSet rs = SubOperatorTest.fixture.rowSet(schema);
            final RowSetWriter writer = rs.writer();
            final VariantWriter vw = writer.variant("u");
            Assert.assertTrue(vw.hasType(INT));
            final ScalarWriter intWriter = vw.scalar(INT);
            Assert.assertTrue(vw.hasType(LIST));
            final ArrayWriter aWriter = vw.array();
            final ScalarWriter strWriter = aWriter.scalar();
            // Row 1: 1, ["fred", "barney"]
            intWriter.setInt(1);
            strWriter.setString("fred");
            aWriter.save();
            strWriter.setString("barney");
            aWriter.save();
            writer.save();
            // Row 2, 2, ["wilma", "betty"]
            intWriter.setInt(2);
            strWriter.setString("wilma");
            aWriter.save();
            strWriter.setString("betty");
            aWriter.save();
            writer.save();
            result = writer.done();
            Assert.assertEquals(2, result.rowCount());
        }
        // Read the values.
        {
            final RowSetReader reader = result.reader();
            final VariantReader vr = reader.variant("u");
            Assert.assertTrue(vr.hasType(INT));
            final ScalarReader intReader = vr.scalar(INT);
            Assert.assertTrue(vr.hasType(LIST));
            final ArrayReader aReader = vr.array();
            final ScalarReader strReader = aReader.scalar();
            Assert.assertTrue(reader.next());
            Assert.assertEquals(1, intReader.getInt());
            Assert.assertEquals(2, aReader.size());
            Assert.assertTrue(aReader.next());
            Assert.assertEquals("fred", strReader.getString());
            Assert.assertTrue(aReader.next());
            Assert.assertEquals("barney", strReader.getString());
            Assert.assertFalse(aReader.next());
            Assert.assertTrue(reader.next());
            Assert.assertEquals(2, intReader.getInt());
            Assert.assertEquals(2, aReader.size());
            Assert.assertTrue(aReader.next());
            Assert.assertEquals("wilma", strReader.getString());
            Assert.assertTrue(aReader.next());
            Assert.assertEquals("betty", strReader.getString());
            Assert.assertFalse(aReader.next());
            Assert.assertFalse(reader.next());
        }
        result.clear();
    }
}

