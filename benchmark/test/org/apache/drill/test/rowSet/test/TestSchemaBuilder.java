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


import DataMode.OPTIONAL;
import DataMode.REPEATED;
import DataMode.REQUIRED;
import MinorType.BIGINT;
import MinorType.DECIMAL18;
import MinorType.FLOAT8;
import MinorType.INT;
import MinorType.LIST;
import MinorType.MAP;
import MinorType.UNION;
import MinorType.VARCHAR;
import StructureType.MULTI_ARRAY;
import StructureType.VARIANT;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.DataMode;
import org.apache.drill.common.types.TypeProtos.MinorType;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.MetadataUtils;
import org.apache.drill.exec.record.metadata.RepeatedListBuilder;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.record.metadata.VariantMetadata;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * The schema builder for tests has grown complex to handle maps, unions,
 * lists and repeated lists. This test verifies that it assembles the various
 * pieces correctly for the various nesting combinations.
 */
@Category(RowSetTests.class)
public class TestSchemaBuilder extends DrillTest {
    @Test
    public void testRowBasics() {
        TupleMetadata schema = // Convenience
        // Convenience
        // Required
        // Generic
        new SchemaBuilder().add("a", VARCHAR, OPTIONAL).add("b", INT).addNullable("c", FLOAT8).addArray("d", BIGINT).buildSchema();
        Assert.assertEquals(4, schema.size());
        ColumnMetadata a = schema.metadata(0);
        Assert.assertEquals("a", a.name());
        Assert.assertEquals(VARCHAR, a.type());
        Assert.assertEquals(OPTIONAL, a.mode());
        ColumnMetadata b = schema.metadata(1);
        Assert.assertEquals("b", b.name());
        Assert.assertEquals(INT, b.type());
        Assert.assertEquals(REQUIRED, b.mode());
        ColumnMetadata c = schema.metadata(2);
        Assert.assertEquals("c", c.name());
        Assert.assertEquals(FLOAT8, c.type());
        Assert.assertEquals(OPTIONAL, c.mode());
        ColumnMetadata d = schema.metadata(3);
        Assert.assertEquals("d", d.name());
        Assert.assertEquals(BIGINT, d.type());
        Assert.assertEquals(REPEATED, d.mode());
    }

    @Test
    public void testRowPreBuilt() {
        MaterializedField aField = MaterializedField.create("a", Types.optional(VARCHAR));
        ColumnMetadata bCol = MetadataUtils.newScalar("b", INT, REQUIRED);
        SchemaBuilder builder = new SchemaBuilder().add(aField);
        // Internal method, does not return builder itself.
        builder.addColumn(bCol);
        TupleMetadata schema = builder.buildSchema();
        Assert.assertEquals(2, schema.size());
        ColumnMetadata a = schema.metadata(0);
        Assert.assertEquals("a", a.name());
        Assert.assertEquals(VARCHAR, a.type());
        Assert.assertEquals(OPTIONAL, a.mode());
        ColumnMetadata b = schema.metadata(1);
        Assert.assertEquals("b", b.name());
        Assert.assertEquals(INT, b.type());
        Assert.assertEquals(REQUIRED, b.mode());
    }

    /**
     * Tests creating a map within a row.
     * Also the basic map add column methods.
     */
    @Test
    public void testMapInRow() {
        TupleMetadata schema = // Convenience
        // Convenience
        // Required
        // Generic
        new SchemaBuilder().addMap("m").add("a", VARCHAR, OPTIONAL).add("b", INT).addNullable("c", FLOAT8).addArray("d", BIGINT).resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata m = schema.metadata(0);
        Assert.assertEquals("m", m.name());
        Assert.assertTrue(m.isMap());
        Assert.assertEquals(REQUIRED, m.mode());
        TupleMetadata mapSchema = m.mapSchema();
        Assert.assertNotNull(mapSchema);
        Assert.assertEquals(4, mapSchema.size());
        ColumnMetadata a = mapSchema.metadata(0);
        Assert.assertEquals("a", a.name());
        Assert.assertEquals(VARCHAR, a.type());
        Assert.assertEquals(OPTIONAL, a.mode());
        ColumnMetadata b = mapSchema.metadata(1);
        Assert.assertEquals("b", b.name());
        Assert.assertEquals(INT, b.type());
        Assert.assertEquals(REQUIRED, b.mode());
        ColumnMetadata c = mapSchema.metadata(2);
        Assert.assertEquals("c", c.name());
        Assert.assertEquals(FLOAT8, c.type());
        Assert.assertEquals(OPTIONAL, c.mode());
        ColumnMetadata d = mapSchema.metadata(3);
        Assert.assertEquals("d", d.name());
        Assert.assertEquals(BIGINT, d.type());
        Assert.assertEquals(REPEATED, d.mode());
    }

    /**
     * Test building a union in the top-level schema.
     * Also tests the basic union add type methods.
     */
    @Test
    public void testUnionInRow() {
        TupleMetadata schema = new SchemaBuilder().addUnion("u").addType(VARCHAR).addType(INT).resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata u = schema.metadata(0);
        Assert.assertEquals("u", u.name());
        Assert.assertEquals(VARIANT, u.structureType());
        Assert.assertTrue(u.isVariant());
        Assert.assertEquals(UNION, u.type());
        Assert.assertEquals(OPTIONAL, u.mode());
        VariantMetadata variant = u.variantSchema();
        Assert.assertNotNull(variant);
        Assert.assertEquals(2, variant.size());
        Assert.assertTrue(variant.hasType(VARCHAR));
        ColumnMetadata vMember = variant.member(VARCHAR);
        Assert.assertNotNull(vMember);
        Assert.assertEquals(Types.typeKey(VARCHAR), vMember.name());
        Assert.assertEquals(VARCHAR, vMember.type());
        Assert.assertEquals(OPTIONAL, vMember.mode());
        Assert.assertTrue(variant.hasType(INT));
        ColumnMetadata iMember = variant.member(INT);
        Assert.assertNotNull(iMember);
        Assert.assertEquals(Types.typeKey(INT), iMember.name());
        Assert.assertEquals(INT, iMember.type());
        Assert.assertEquals(OPTIONAL, iMember.mode());
    }

    /**
     * Test building a list (of unions) in the top-level schema.
     */
    @Test
    public void testListInRow() {
        TupleMetadata schema = new SchemaBuilder().addList("list").addType(VARCHAR).addType(INT).resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata list = schema.metadata(0);
        Assert.assertEquals("list", list.name());
        Assert.assertEquals(VARIANT, list.structureType());
        Assert.assertTrue(list.isVariant());
        Assert.assertEquals(LIST, list.type());
        // Yes, strange. Though a list is, essentially, an array, an
        // optional list has one set of semantics (in ListVector, not
        // really supported), while a repeated list has entirely different
        // semantics (in the RepeatedListVector) and is supported.
        Assert.assertEquals(OPTIONAL, list.mode());
        VariantMetadata variant = list.variantSchema();
        Assert.assertNotNull(variant);
        Assert.assertEquals(2, variant.size());
        Assert.assertTrue(variant.hasType(VARCHAR));
        ColumnMetadata vMember = variant.member(VARCHAR);
        Assert.assertNotNull(vMember);
        Assert.assertEquals(Types.typeKey(VARCHAR), vMember.name());
        Assert.assertEquals(VARCHAR, vMember.type());
        Assert.assertEquals(OPTIONAL, vMember.mode());
        Assert.assertTrue(variant.hasType(INT));
        ColumnMetadata iMember = variant.member(INT);
        Assert.assertNotNull(iMember);
        Assert.assertEquals(Types.typeKey(INT), iMember.name());
        Assert.assertEquals(INT, iMember.type());
        Assert.assertEquals(OPTIONAL, iMember.mode());
    }

    /**
     * Test building a repeated list in the top-level schema.
     */
    @Test
    public void testRepeatedListInRow() {
        TupleMetadata schema = new SchemaBuilder().addRepeatedList("list").addArray(VARCHAR).resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata list = schema.metadata(0);
        Assert.assertEquals("list", list.name());
        Assert.assertFalse(list.isVariant());
        Assert.assertEquals(MULTI_ARRAY, list.structureType());
        Assert.assertEquals(LIST, list.type());
        // See note above for the (non-repeated) list.
        Assert.assertEquals(REPEATED, list.mode());
        ColumnMetadata child = list.childSchema();
        Assert.assertNotNull(child);
        Assert.assertEquals(list.name(), child.name());
        Assert.assertEquals(VARCHAR, child.type());
        Assert.assertEquals(REPEATED, child.mode());
    }

    /**
     * Test methods to provide a width (precision) for VarChar
     * columns. The schema builder does not provide shortcuts for
     * VarChar in lists, unions or repeated lists because these
     * cases are obscure and seldom (never?) used.
     */
    @Test
    public void testVarCharPrecision() {
        TupleMetadata schema = new SchemaBuilder().add("a", VARCHAR, 21).addNullable("b", VARCHAR, 22).addMap("m").add("c", VARCHAR, 23).addNullable("d", VARCHAR, 24).resumeSchema().buildSchema();
        Assert.assertEquals(3, schema.size());
        // Use name methods, just for variety
        Assert.assertEquals(21, schema.metadata("a").precision());
        Assert.assertEquals(22, schema.metadata("b").precision());
        TupleMetadata mapSchema = schema.metadata("m").mapSchema();
        Assert.assertEquals(23, mapSchema.metadata("c").precision());
        Assert.assertEquals(24, mapSchema.metadata("d").precision());
    }

    /**
     * Test the ability to specify decimal precision and scale. Decimal is
     * broken in Drill, so we don't bother about decimals in unions,
     * lists or repeated lists, though those methods could be added.
     */
    @Test
    public void testDecimal() {
        TupleMetadata schema = new SchemaBuilder().addDecimal("a", DECIMAL18, OPTIONAL, 5, 2).addDecimal("b", DECIMAL18, REQUIRED, 6, 3).addDecimal("c", DECIMAL18, REPEATED, 7, 4).addMap("m").addDecimal("d", DECIMAL18, OPTIONAL, 8, 1).resumeSchema().buildSchema();
        // Use name methods, just for variety
        ColumnMetadata a = schema.metadata("a");
        Assert.assertEquals(OPTIONAL, a.mode());
        Assert.assertEquals(5, a.precision());
        Assert.assertEquals(2, a.scale());
        ColumnMetadata b = schema.metadata("b");
        Assert.assertEquals(REQUIRED, b.mode());
        Assert.assertEquals(6, b.precision());
        Assert.assertEquals(3, b.scale());
        ColumnMetadata c = schema.metadata("c");
        Assert.assertEquals(REPEATED, c.mode());
        Assert.assertEquals(7, c.precision());
        Assert.assertEquals(4, c.scale());
        ColumnMetadata d = schema.metadata("m").mapSchema().metadata("d");
        Assert.assertEquals(OPTIONAL, d.mode());
        Assert.assertEquals(8, d.precision());
        Assert.assertEquals(1, d.scale());
    }

    /**
     * Verify that the map-in-map plumbing works.
     */
    @Test
    public void testMapInMap() {
        TupleMetadata schema = new SchemaBuilder().addMap("m1").addMap("m2").add("a", INT).resumeMap().add("b", VARCHAR).resumeSchema().buildSchema();
        TupleMetadata m1Schema = schema.metadata("m1").mapSchema();
        TupleMetadata m2Schema = m1Schema.metadata("m2").mapSchema();
        ColumnMetadata a = m2Schema.metadata(0);
        Assert.assertEquals("a", a.name());
        Assert.assertEquals(INT, a.type());
        ColumnMetadata b = m1Schema.metadata(1);
        Assert.assertEquals("b", b.name());
        Assert.assertEquals(VARCHAR, b.type());
    }

    /**
     * Verify that the union-in-map plumbing works.
     */
    @Test
    public void testUnionInMap() {
        TupleMetadata schema = new SchemaBuilder().addMap("m1").addUnion("u").addType(INT).resumeMap().add("b", VARCHAR).resumeSchema().buildSchema();
        TupleMetadata m1Schema = schema.metadata("m1").mapSchema();
        VariantMetadata uSchema = m1Schema.metadata("u").variantSchema();
        Assert.assertTrue(uSchema.hasType(INT));
        Assert.assertFalse(uSchema.hasType(VARCHAR));
        ColumnMetadata b = m1Schema.metadata(1);
        Assert.assertEquals("b", b.name());
        Assert.assertEquals(VARCHAR, b.type());
    }

    /**
     * Verify that the repeated list-in-map plumbing works.
     */
    @Test
    public void testRepeatedListInMap() {
        TupleMetadata schema = new SchemaBuilder().addMap("m1").addRepeatedList("r").addArray(INT).resumeMap().add("b", VARCHAR).resumeSchema().buildSchema();
        TupleMetadata m1Schema = schema.metadata("m1").mapSchema();
        ColumnMetadata r = m1Schema.metadata(0);
        Assert.assertEquals("r", r.name());
        Assert.assertEquals(LIST, r.type());
        Assert.assertEquals(REPEATED, r.mode());
        ColumnMetadata child = r.childSchema();
        Assert.assertEquals(r.name(), child.name());
        Assert.assertEquals(INT, child.type());
        ColumnMetadata b = m1Schema.metadata(1);
        Assert.assertEquals("b", b.name());
        Assert.assertEquals(VARCHAR, b.type());
    }

    @Test
    public void testMapInUnion() {
        TupleMetadata schema = new SchemaBuilder().addUnion("u").addMap().add("a", INT).add("b", VARCHAR).resumeUnion().addType(FLOAT8).resumeSchema().buildSchema();
        ColumnMetadata u = schema.metadata("u");
        VariantMetadata variant = u.variantSchema();
        ColumnMetadata mapType = variant.member(MAP);
        Assert.assertNotNull(mapType);
        TupleMetadata mapSchema = mapType.mapSchema();
        Assert.assertEquals(2, mapSchema.size());
        Assert.assertTrue(variant.hasType(FLOAT8));
        Assert.assertFalse(variant.hasType(VARCHAR));
    }

    @Test
    public void testRepeatedListInUnion() {
        TupleMetadata schema = new SchemaBuilder().addUnion("u").addRepeatedList().addArray(INT).resumeUnion().addType(FLOAT8).resumeSchema().buildSchema();
        ColumnMetadata u = schema.metadata("u");
        VariantMetadata variant = u.variantSchema();
        ColumnMetadata listType = variant.member(LIST);
        Assert.assertNotNull(listType);
        ColumnMetadata child = listType.childSchema();
        Assert.assertEquals(INT, child.type());
        Assert.assertTrue(variant.hasType(FLOAT8));
        Assert.assertFalse(variant.hasType(VARCHAR));
    }

    // Note: list-in-union may be supported, but this area of the code is obscure
    // and not a priority to maintain. The problem will be that both lists
    // and repeated lists key off of the same type code: LIST, so it is
    // ambiguous which is supported. The schema builder muddles through this
    // case, but the rest of the code might not.
    @Test
    public void testListInUnion() {
        TupleMetadata schema = new SchemaBuilder().addUnion("u").addList().addType(INT).resumeUnion().addType(FLOAT8).resumeSchema().buildSchema();
        ColumnMetadata u = schema.metadata("u");
        VariantMetadata variant = u.variantSchema();
        ColumnMetadata listType = variant.member(LIST);
        Assert.assertNotNull(listType);
        VariantMetadata listSchema = listType.variantSchema();
        Assert.assertTrue(listSchema.hasType(INT));
        Assert.assertTrue(variant.hasType(FLOAT8));
        Assert.assertFalse(variant.hasType(VARCHAR));
    }

    // Note: union-in-union not supported in Drill
    @Test
    public void testMapInRepeatedList() {
        TupleMetadata schema = new SchemaBuilder().addRepeatedList("x").addMapArray().add("a", INT).addNullable("b", VARCHAR).resumeList().resumeSchema().buildSchema();
        ColumnMetadata list = schema.metadata("x");
        ColumnMetadata mapCol = list.childSchema();
        Assert.assertTrue(mapCol.isMap());
        TupleMetadata mapSchema = mapCol.mapSchema();
        ColumnMetadata a = mapSchema.metadata("a");
        Assert.assertEquals(INT, a.type());
        Assert.assertEquals(REQUIRED, a.mode());
        ColumnMetadata b = mapSchema.metadata("b");
        Assert.assertEquals(VARCHAR, b.type());
        Assert.assertEquals(OPTIONAL, b.mode());
    }

    /**
     * Test that repeated lists can be nested to provide 3D or
     * higher dimensions.
     */
    @Test
    public void testRepeatedListInRepeatedList() {
        TupleMetadata schema = new SchemaBuilder().addRepeatedList("x").addDimension().addArray(VARCHAR).resumeList().resumeSchema().buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata outerList = schema.metadata(0);
        Assert.assertEquals("x", outerList.name());
        Assert.assertEquals(MULTI_ARRAY, outerList.structureType());
        Assert.assertEquals(LIST, outerList.type());
        Assert.assertEquals(REPEATED, outerList.mode());
        ColumnMetadata innerList = outerList.childSchema();
        Assert.assertNotNull(innerList);
        Assert.assertEquals(outerList.name(), innerList.name());
        Assert.assertEquals(MULTI_ARRAY, innerList.structureType());
        Assert.assertEquals(LIST, innerList.type());
        Assert.assertEquals(REPEATED, innerList.mode());
        ColumnMetadata child = innerList.childSchema();
        Assert.assertNotNull(child);
        Assert.assertEquals(outerList.name(), child.name());
        Assert.assertEquals(VARCHAR, child.type());
        Assert.assertEquals(REPEATED, child.mode());
    }

    @Test
    public void testRepeatedListShortcut() {
        TupleMetadata schema = new SchemaBuilder().addArray("x", VARCHAR, 3).buildSchema();
        Assert.assertEquals(1, schema.size());
        ColumnMetadata outerList = schema.metadata(0);
        Assert.assertEquals("x", outerList.name());
        Assert.assertEquals(MULTI_ARRAY, outerList.structureType());
        Assert.assertEquals(LIST, outerList.type());
        Assert.assertEquals(REPEATED, outerList.mode());
        ColumnMetadata innerList = outerList.childSchema();
        Assert.assertNotNull(innerList);
        Assert.assertEquals(outerList.name(), innerList.name());
        Assert.assertEquals(MULTI_ARRAY, innerList.structureType());
        Assert.assertEquals(LIST, innerList.type());
        Assert.assertEquals(REPEATED, innerList.mode());
        ColumnMetadata child = innerList.childSchema();
        Assert.assertNotNull(child);
        Assert.assertEquals(outerList.name(), child.name());
        Assert.assertEquals(VARCHAR, child.type());
        Assert.assertEquals(REPEATED, child.mode());
    }

    @Test
    public void testStandaloneMapBuilder() {
        ColumnMetadata columnMetadata = new org.apache.drill.exec.record.metadata.MapBuilder("m1", DataMode.OPTIONAL).addNullable("b", BIGINT).addMap("m2").addNullable("v", VARCHAR).resumeMap().buildColumn();
        Assert.assertTrue(columnMetadata.isMap());
        Assert.assertTrue(columnMetadata.isNullable());
        Assert.assertEquals("m1", columnMetadata.name());
        TupleMetadata schema = columnMetadata.mapSchema();
        ColumnMetadata col0 = schema.metadata(0);
        Assert.assertEquals("b", col0.name());
        Assert.assertEquals(BIGINT, col0.type());
        Assert.assertTrue(col0.isNullable());
        ColumnMetadata col1 = schema.metadata(1);
        Assert.assertEquals("m2", col1.name());
        Assert.assertTrue(col1.isMap());
        Assert.assertFalse(col1.isNullable());
        ColumnMetadata child = col1.mapSchema().metadata(0);
        Assert.assertEquals("v", child.name());
        Assert.assertEquals(VARCHAR, child.type());
        Assert.assertTrue(child.isNullable());
    }

    @Test
    public void testStandaloneRepeatedListBuilder() {
        ColumnMetadata columnMetadata = new RepeatedListBuilder("l").addMapArray().addNullable("v", VARCHAR).add("i", INT).resumeList().buildColumn();
        Assert.assertTrue(columnMetadata.isArray());
        Assert.assertEquals("l", columnMetadata.name());
        Assert.assertEquals(LIST, columnMetadata.type());
        ColumnMetadata child = columnMetadata.childSchema();
        Assert.assertEquals("l", child.name());
        Assert.assertTrue(child.isArray());
        Assert.assertTrue(child.isMap());
        TupleMetadata mapSchema = child.mapSchema();
        ColumnMetadata col0 = mapSchema.metadata(0);
        Assert.assertEquals("v", col0.name());
        Assert.assertEquals(VARCHAR, col0.type());
        Assert.assertTrue(col0.isNullable());
        ColumnMetadata col1 = mapSchema.metadata(1);
        Assert.assertEquals("i", col1.name());
        Assert.assertEquals(INT, col1.type());
        Assert.assertFalse(col1.isNullable());
    }

    @Test
    public void testStandaloneUnionBuilder() {
        ColumnMetadata columnMetadata = new org.apache.drill.exec.record.metadata.UnionBuilder("u", MinorType.VARCHAR).addType(INT).addType(VARCHAR).buildColumn();
        Assert.assertEquals("u", columnMetadata.name());
        Assert.assertTrue(columnMetadata.isVariant());
        VariantMetadata variantMetadata = columnMetadata.variantSchema();
        Assert.assertTrue(variantMetadata.hasType(INT));
        Assert.assertTrue(variantMetadata.hasType(VARCHAR));
    }
}

