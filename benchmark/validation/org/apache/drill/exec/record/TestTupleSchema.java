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
package org.apache.drill.exec.record;


import ColumnMetadata.DEFAULT_ARRAY_SIZE;
import ColumnMetadata.StructureType.PRIMITIVE;
import ColumnMetadata.StructureType.TUPLE;
import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import MinorType.BIGINT;
import MinorType.DECIMAL18;
import MinorType.DECIMAL9;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.LIST;
import MinorType.MAP;
import MinorType.UNION;
import MinorType.VARCHAR;
import SelectionVectorMode.NONE;
import StructureType.VARIANT;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.TypeProtos.MinorType;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.MapColumnMetadata;
import org.apache.drill.exec.record.metadata.MetadataUtils;
import org.apache.drill.exec.record.metadata.PrimitiveColumnMetadata;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.record.metadata.TupleSchema;
import org.apache.drill.exec.record.metadata.VariantColumnMetadata;
import org.apache.drill.exec.record.metadata.VariantMetadata;
import org.apache.drill.test.SubOperatorTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the tuple and column metadata, including extended attributes.
 */
public class TestTupleSchema extends SubOperatorTest {
    /**
     * Test a fixed-width, primitive, required column. Includes basic
     * tests common to all data types. (Basic tests are not repeated for
     * other types.)
     */
    @Test
    public void testRequiredFixedWidthColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("c", INT, REQUIRED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        // Code may depend on the specific column class
        Assert.assertTrue((col instanceof PrimitiveColumnMetadata));
        // Generic checks
        Assert.assertEquals(PRIMITIVE, col.structureType());
        Assert.assertNull(col.mapSchema());
        Assert.assertTrue(field.isEquivalent(col.schema()));
        Assert.assertEquals(field.getName(), col.name());
        Assert.assertEquals(field.getType().getMinorType(), col.type());
        Assert.assertEquals(field.getDataMode(), col.mode());
        Assert.assertFalse(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertFalse(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertTrue(col.isEquivalent(col));
        Assert.assertFalse(col.isVariant());
        ColumnMetadata col2 = MetadataUtils.fromField(field);
        Assert.assertTrue(col.isEquivalent(col2));
        MaterializedField field3 = SchemaBuilder.columnSchema("d", INT, REQUIRED);
        ColumnMetadata col3 = MetadataUtils.fromField(field3);
        Assert.assertFalse(col.isEquivalent(col3));
        MaterializedField field4 = SchemaBuilder.columnSchema("c", BIGINT, REQUIRED);
        ColumnMetadata col4 = MetadataUtils.fromField(field4);
        Assert.assertFalse(col.isEquivalent(col4));
        MaterializedField field5 = SchemaBuilder.columnSchema("c", INT, OPTIONAL);
        ColumnMetadata col5 = MetadataUtils.fromField(field5);
        Assert.assertFalse(col.isEquivalent(col5));
        ColumnMetadata col6 = col.cloneEmpty();
        Assert.assertTrue(col.isEquivalent(col6));
        Assert.assertEquals(4, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(4, col.expectedWidth());
        Assert.assertEquals(1, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(1, col.expectedElementCount());
    }

    @Test
    public void testNullableFixedWidthColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("c", INT, OPTIONAL);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertEquals(PRIMITIVE, col.structureType());
        Assert.assertTrue(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertFalse(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(4, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(4, col.expectedWidth());
        Assert.assertEquals(1, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(1, col.expectedElementCount());
    }

    @Test
    public void testRepeatedFixedWidthColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("c", INT, REPEATED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertFalse(col.isNullable());
        Assert.assertTrue(col.isArray());
        Assert.assertFalse(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(4, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(4, col.expectedWidth());
        Assert.assertEquals(DEFAULT_ARRAY_SIZE, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(2, col.expectedElementCount());
        col.setExpectedElementCount(0);
        Assert.assertEquals(1, col.expectedElementCount());
    }

    @Test
    public void testRequiredVariableWidthColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("c", VARCHAR, REQUIRED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertEquals(PRIMITIVE, col.structureType());
        Assert.assertNull(col.mapSchema());
        Assert.assertFalse(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertTrue(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertFalse(col.isVariant());
        // A different precision is a different type.
        MaterializedField field2 = new org.apache.drill.exec.record.metadata.ColumnBuilder("c", MinorType.VARCHAR).setMode(REQUIRED).setPrecision(10).build();
        ColumnMetadata col2 = MetadataUtils.fromField(field2);
        Assert.assertFalse(col.isEquivalent(col2));
        Assert.assertEquals(50, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(10, col.expectedWidth());
        Assert.assertEquals(1, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(1, col.expectedElementCount());
        // If precision is provided, then that is the default width
        col = MetadataUtils.fromField(field2);
        Assert.assertEquals(10, col.expectedWidth());
    }

    @Test
    public void testNullableVariableWidthColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("c", VARCHAR, OPTIONAL);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertTrue(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertTrue(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(50, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(10, col.expectedWidth());
        Assert.assertEquals(1, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(1, col.expectedElementCount());
    }

    @Test
    public void testRepeatedVariableWidthColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("c", VARCHAR, REPEATED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertFalse(col.isNullable());
        Assert.assertTrue(col.isArray());
        Assert.assertTrue(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(50, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(10, col.expectedWidth());
        Assert.assertEquals(DEFAULT_ARRAY_SIZE, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(2, col.expectedElementCount());
    }

    @Test
    public void testDecimalScalePrecision() {
        MaterializedField field = MaterializedField.create("d", MajorType.newBuilder().setMinorType(DECIMAL9).setMode(REQUIRED).setPrecision(3).setScale(4).build());
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertFalse(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertFalse(col.isVariableWidth());
        Assert.assertFalse(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(3, col.precision());
        Assert.assertEquals(4, col.scale());
        Assert.assertTrue(field.isEquivalent(col.schema()));
    }

    /**
     * Tests a map column. Maps can only be required or repeated, not nullable.
     * (But, the columns in the map can be nullable.)
     */
    @Test
    public void testMapColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("m", MAP, REQUIRED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertTrue((col instanceof MapColumnMetadata));
        Assert.assertNotNull(col.mapSchema());
        Assert.assertEquals(0, col.mapSchema().size());
        Assert.assertSame(col, col.mapSchema().parent());
        MapColumnMetadata mapCol = ((MapColumnMetadata) (col));
        Assert.assertNull(mapCol.parentTuple());
        Assert.assertEquals(TUPLE, col.structureType());
        Assert.assertFalse(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertFalse(col.isVariableWidth());
        Assert.assertTrue(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(0, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(0, col.expectedWidth());
        Assert.assertEquals(1, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(1, col.expectedElementCount());
    }

    @Test
    public void testRepeatedMapColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("m", MAP, REPEATED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertTrue((col instanceof MapColumnMetadata));
        Assert.assertNotNull(col.mapSchema());
        Assert.assertEquals(0, col.mapSchema().size());
        Assert.assertFalse(col.isNullable());
        Assert.assertTrue(col.isArray());
        Assert.assertFalse(col.isVariableWidth());
        Assert.assertTrue(col.isMap());
        Assert.assertFalse(col.isVariant());
        Assert.assertEquals(0, col.expectedWidth());
        col.setExpectedWidth(10);
        Assert.assertEquals(0, col.expectedWidth());
        Assert.assertEquals(DEFAULT_ARRAY_SIZE, col.expectedElementCount());
        col.setExpectedElementCount(2);
        Assert.assertEquals(2, col.expectedElementCount());
    }

    @Test
    public void testUnionColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("u", UNION, OPTIONAL);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertFalse(col.isArray());
        doVariantTest(col);
    }

    @Test
    public void testListColumn() {
        MaterializedField field = SchemaBuilder.columnSchema("l", LIST, OPTIONAL);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertTrue(col.isArray());
        // List modeled as a repeated element. Implementation is a bit
        // more complex, but does not affect this abstract description.
        doVariantTest(col);
    }

    // Repeated list
    /**
     * Test the basics of an empty root tuple (i.e. row) schema.
     */
    @Test
    public void testEmptyRootTuple() {
        TupleMetadata root = new TupleSchema();
        Assert.assertEquals(0, root.size());
        Assert.assertTrue(root.isEmpty());
        Assert.assertEquals((-1), root.index("foo"));
        try {
            root.metadata(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        Assert.assertNull(root.metadata("foo"));
        try {
            root.column(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        Assert.assertNull(root.column("foo"));
        try {
            root.fullName(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        // The full name method does not check if the column is actually
        // in the tuple.
        MaterializedField field = SchemaBuilder.columnSchema("c", INT, REQUIRED);
        ColumnMetadata col = MetadataUtils.fromField(field);
        Assert.assertEquals("c", root.fullName(col));
        Assert.assertTrue(root.isEquivalent(root));
        Assert.assertNull(root.parent());
        Assert.assertTrue(root.toFieldList().isEmpty());
    }

    /**
     * Test the basics of a non-empty root tuple (i.e. a row) using a pair
     * of primitive columns.
     */
    @Test
    public void testNonEmptyRootTuple() {
        TupleSchema root = new TupleSchema();
        MaterializedField fieldA = SchemaBuilder.columnSchema("a", INT, REQUIRED);
        ColumnMetadata colA = root.add(fieldA);
        Assert.assertEquals(1, root.size());
        Assert.assertFalse(root.isEmpty());
        Assert.assertEquals(0, root.index("a"));
        Assert.assertEquals((-1), root.index("b"));
        Assert.assertTrue(fieldA.isEquivalent(root.column(0)));
        Assert.assertTrue(fieldA.isEquivalent(root.column("a")));
        Assert.assertTrue(fieldA.isEquivalent(root.column("A")));
        Assert.assertSame(colA, root.metadata(0));
        Assert.assertSame(colA, root.metadata("a"));
        Assert.assertEquals("a", root.fullName(0));
        Assert.assertEquals("a", root.fullName(colA));
        try {
            root.add(fieldA);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        MaterializedField fieldB = SchemaBuilder.columnSchema("b", VARCHAR, OPTIONAL);
        ColumnMetadata colB = MetadataUtils.fromField(fieldB);
        int indexB = root.addColumn(colB);
        Assert.assertEquals(1, indexB);
        Assert.assertEquals(2, root.size());
        Assert.assertFalse(root.isEmpty());
        Assert.assertEquals(indexB, root.index("b"));
        Assert.assertTrue(fieldB.isEquivalent(root.column(1)));
        Assert.assertTrue(fieldB.isEquivalent(root.column("b")));
        Assert.assertSame(colB, root.metadata(1));
        Assert.assertSame(colB, root.metadata("b"));
        Assert.assertEquals("b", root.fullName(1));
        Assert.assertEquals("b", root.fullName(colB));
        try {
            root.add(fieldB);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        List<MaterializedField> fieldList = root.toFieldList();
        Assert.assertTrue(fieldA.isEquivalent(fieldList.get(0)));
        Assert.assertTrue(fieldB.isEquivalent(fieldList.get(1)));
        TupleMetadata emptyRoot = new TupleSchema();
        Assert.assertFalse(emptyRoot.isEquivalent(root));
        // Same schema: the tuples are equivalent
        TupleMetadata root3 = new TupleSchema();
        root3.add(fieldA);
        root3.addColumn(colB);
        Assert.assertTrue(root3.isEquivalent(root));
        Assert.assertTrue(root.isEquivalent(root3));
        // Same columns, different order. The tuples are not equivalent.
        TupleMetadata root4 = new TupleSchema();
        root4.addColumn(colB);
        root4.add(fieldA);
        Assert.assertFalse(root4.isEquivalent(root));
        Assert.assertFalse(root.isEquivalent(root4));
        // A tuple is equivalent to its copy.
        Assert.assertTrue(root.isEquivalent(root.copy()));
        // And it is equivalent to the round trip to a batch schema.
        BatchSchema batchSchema = root.toBatchSchema(NONE);
        Assert.assertTrue(root.isEquivalent(MetadataUtils.fromFields(batchSchema)));
    }

    /**
     * Test a complex map schema of the form:<br>
     * a.`b.x`.`c.y`.d<br>
     * in which columns "a", "b.x" and "c.y" are maps, "b.x" and "c.y" are names
     * that contains dots, and d is primitive.
     * Here we build up the schema using the metadata schema, and generate a
     * materialized field from the metadata.
     */
    @Test
    public void testMapTupleFromMetadata() {
        TupleSchema root = new TupleSchema();
        MaterializedField fieldA = SchemaBuilder.columnSchema("a", MAP, REQUIRED);
        ColumnMetadata colA = root.add(fieldA);
        TupleMetadata mapA = colA.mapSchema();
        MaterializedField fieldB = SchemaBuilder.columnSchema("b.x", MAP, REQUIRED);
        ColumnMetadata colB = mapA.add(fieldB);
        TupleMetadata mapB = colB.mapSchema();
        MaterializedField fieldC = SchemaBuilder.columnSchema("c.y", MAP, REQUIRED);
        ColumnMetadata colC = mapB.add(fieldC);
        TupleMetadata mapC = colC.mapSchema();
        MaterializedField fieldD = SchemaBuilder.columnSchema("d", VARCHAR, REQUIRED);
        ColumnMetadata colD = mapC.add(fieldD);
        MaterializedField fieldE = SchemaBuilder.columnSchema("e", INT, REQUIRED);
        ColumnMetadata colE = mapC.add(fieldE);
        Assert.assertEquals(1, root.size());
        Assert.assertEquals(1, mapA.size());
        Assert.assertEquals(1, mapB.size());
        Assert.assertEquals(2, mapC.size());
        Assert.assertSame(colA, root.metadata("a"));
        Assert.assertSame(colB, mapA.metadata("b.x"));
        Assert.assertSame(colC, mapB.metadata("c.y"));
        Assert.assertSame(colD, mapC.metadata("d"));
        Assert.assertSame(colE, mapC.metadata("e"));
        // The full name contains quoted names if the contain dots.
        // This name is more for diagnostic than semantic purposes.
        Assert.assertEquals("a", root.fullName(0));
        Assert.assertEquals("a.`b.x`", mapA.fullName(0));
        Assert.assertEquals("a.`b.x`.`c.y`", mapB.fullName(0));
        Assert.assertEquals("a.`b.x`.`c.y`.d", mapC.fullName(0));
        Assert.assertEquals("a.`b.x`.`c.y`.e", mapC.fullName(1));
        Assert.assertEquals(1, colA.schema().getChildren().size());
        Assert.assertEquals(1, colB.schema().getChildren().size());
        Assert.assertEquals(2, colC.schema().getChildren().size());
        // Yes, it is awful that MaterializedField does not provide indexed
        // access to its children. That's one reason we have the TupleMetadata
        // classes...
        // Note that the metadata layer does not store the materialized field.
        // (Doing so causes no end of synchronization problems.) So we test
        // for equivalence, not sameness.
        Iterator<MaterializedField> iterC = colC.schema().getChildren().iterator();
        Assert.assertTrue(fieldD.isEquivalent(iterC.next()));
        Assert.assertTrue(fieldE.isEquivalent(iterC.next()));
        // Copying should be deep.
        TupleMetadata root2 = root.copy();
        Assert.assertEquals(2, root2.metadata(0).mapSchema().metadata(0).mapSchema().metadata(0).mapSchema().size());
        assert root.isEquivalent(root2);
        // Generate a materialized field and compare.
        fieldA.addChild(fieldB);
        fieldB.addChild(fieldC);
        fieldC.addChild(fieldD);
        fieldC.addChild(fieldE);
        Assert.assertTrue(colA.schema().isEquivalent(fieldA));
    }

    @Test
    public void testMapTupleFromField() {
        // Create a materialized field with the desired structure.
        MaterializedField fieldA = SchemaBuilder.columnSchema("a", MAP, REQUIRED);
        MaterializedField fieldB = SchemaBuilder.columnSchema("b.x", MAP, REQUIRED);
        fieldA.addChild(fieldB);
        MaterializedField fieldC = SchemaBuilder.columnSchema("c.y", MAP, REQUIRED);
        fieldB.addChild(fieldC);
        MaterializedField fieldD = SchemaBuilder.columnSchema("d", VARCHAR, REQUIRED);
        fieldC.addChild(fieldD);
        MaterializedField fieldE = SchemaBuilder.columnSchema("e", INT, REQUIRED);
        fieldC.addChild(fieldE);
        // Create a metadata schema from the field.
        TupleMetadata root = new TupleSchema();
        ColumnMetadata colA = root.add(fieldA);
        // Get the parts.
        TupleMetadata mapA = colA.mapSchema();
        ColumnMetadata colB = mapA.metadata("b.x");
        TupleMetadata mapB = colB.mapSchema();
        ColumnMetadata colC = mapB.metadata("c.y");
        TupleMetadata mapC = colC.mapSchema();
        ColumnMetadata colD = mapC.metadata("d");
        ColumnMetadata colE = mapC.metadata("e");
        // Validate. Should be same as earlier test that started
        // with the metadata.
        Assert.assertEquals(1, root.size());
        Assert.assertEquals(1, mapA.size());
        Assert.assertEquals(1, mapB.size());
        Assert.assertEquals(2, mapC.size());
        Assert.assertSame(colA, root.metadata("a"));
        Assert.assertSame(colB, mapA.metadata("b.x"));
        Assert.assertSame(colC, mapB.metadata("c.y"));
        Assert.assertSame(colD, mapC.metadata("d"));
        Assert.assertSame(colE, mapC.metadata("e"));
        // The full name contains quoted names if the contain dots.
        // This name is more for diagnostic than semantic purposes.
        Assert.assertEquals("a", root.fullName(0));
        Assert.assertEquals("a.`b.x`", mapA.fullName(0));
        Assert.assertEquals("a.`b.x`.`c.y`", mapB.fullName(0));
        Assert.assertEquals("a.`b.x`.`c.y`.d", mapC.fullName(0));
        Assert.assertEquals("a.`b.x`.`c.y`.e", mapC.fullName(1));
        Assert.assertEquals(1, colA.schema().getChildren().size());
        Assert.assertEquals(1, colB.schema().getChildren().size());
        Assert.assertEquals(2, colC.schema().getChildren().size());
        Assert.assertTrue(colA.schema().isEquivalent(fieldA));
    }

    @Test
    public void testUnionSchema() {
        TupleMetadata schema = new SchemaBuilder().addUnion("u").addType(BIGINT).addType(VARCHAR).resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata col = schema.metadata(0);
        Assert.assertTrue((col instanceof VariantColumnMetadata));
        Assert.assertEquals(UNION, col.type());
        Assert.assertEquals(OPTIONAL, col.mode());
        Assert.assertTrue(col.isNullable());
        Assert.assertFalse(col.isArray());
        Assert.assertTrue(col.isVariant());
        Assert.assertEquals(VARIANT, col.structureType());
        VariantMetadata union = col.variantSchema();
        Assert.assertNotNull(union);
        Assert.assertEquals(2, union.size());
        Assert.assertTrue(union.hasType(BIGINT));
        Assert.assertTrue(union.hasType(VARCHAR));
        Assert.assertFalse(union.hasType(INT));
        Collection<MinorType> types = union.types();
        Assert.assertNotNull(types);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.contains(BIGINT));
        Assert.assertTrue(types.contains(VARCHAR));
        BatchSchema batchSchema = ((TupleSchema) (schema)).toBatchSchema(NONE);
        MaterializedField field = batchSchema.getColumn(0);
        Assert.assertEquals("u", field.getName());
        MajorType majorType = field.getType();
        Assert.assertEquals(UNION, majorType.getMinorType());
        Assert.assertEquals(OPTIONAL, majorType.getMode());
        Assert.assertEquals(2, majorType.getSubTypeCount());
        List<MinorType> subtypes = majorType.getSubTypeList();
        Assert.assertEquals(2, subtypes.size());
        Assert.assertTrue(subtypes.contains(BIGINT));
        Assert.assertTrue(subtypes.contains(VARCHAR));
    }

    @Test
    public void testListSchema() {
        TupleMetadata schema = new SchemaBuilder().addList("list").addType(BIGINT).addType(VARCHAR).resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata col = schema.metadata(0);
        Assert.assertTrue((col instanceof VariantColumnMetadata));
        // Implementation shows through here: actual major
        // type is (LIST, OPTIONAL) even though the metadata
        // lies that this is a variant array.
        Assert.assertEquals(LIST, col.type());
        Assert.assertEquals(OPTIONAL, col.mode());
        Assert.assertTrue(col.isNullable());
        Assert.assertTrue(col.isArray());
        Assert.assertTrue(col.isVariant());
        Assert.assertEquals(VARIANT, col.structureType());
        VariantMetadata union = col.variantSchema();
        Assert.assertNotNull(union);
        Assert.assertEquals(2, union.size());
        Assert.assertTrue(union.hasType(BIGINT));
        Assert.assertTrue(union.hasType(VARCHAR));
        Assert.assertFalse(union.hasType(INT));
        Collection<MinorType> types = union.types();
        Assert.assertNotNull(types);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.contains(BIGINT));
        Assert.assertTrue(types.contains(VARCHAR));
        BatchSchema batchSchema = ((TupleSchema) (schema)).toBatchSchema(NONE);
        MaterializedField field = batchSchema.getColumn(0);
        Assert.assertEquals("list", field.getName());
        MajorType majorType = field.getType();
        Assert.assertEquals(LIST, majorType.getMinorType());
        Assert.assertEquals(OPTIONAL, majorType.getMode());
        Assert.assertEquals(2, majorType.getSubTypeCount());
        List<MinorType> subtypes = majorType.getSubTypeList();
        Assert.assertEquals(2, subtypes.size());
        Assert.assertTrue(subtypes.contains(BIGINT));
        Assert.assertTrue(subtypes.contains(VARCHAR));
    }

    @Test
    public void testNestedSchema() {
        TupleMetadata schema = new SchemaBuilder().addList("list").addType(BIGINT).addType(VARCHAR).addMap().add("a", INT).add("b", VARCHAR).resumeUnion().addList().addType(FLOAT8).addType(DECIMAL18).resumeUnion().resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata col = schema.metadata(0);
        Assert.assertTrue(col.isVariant());
        VariantMetadata union = col.variantSchema();
        Assert.assertNotNull(union);
        Assert.assertEquals(4, union.size());
        Assert.assertTrue(union.hasType(MAP));
        Assert.assertTrue(union.hasType(LIST));
        ColumnMetadata mapCol = union.member(MAP);
        TupleMetadata mapSchema = mapCol.mapSchema();
        Assert.assertEquals(2, mapSchema.size());
        ColumnMetadata listCol = union.member(LIST);
        VariantMetadata listSchema = listCol.variantSchema();
        Assert.assertEquals(2, listSchema.size());
        Assert.assertTrue(listSchema.hasType(FLOAT8));
        Assert.assertTrue(listSchema.hasType(DECIMAL18));
    }

    @Test
    public void testDuplicateType() {
        try {
            new SchemaBuilder().addList("list").addType(BIGINT).addType(BIGINT);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}

