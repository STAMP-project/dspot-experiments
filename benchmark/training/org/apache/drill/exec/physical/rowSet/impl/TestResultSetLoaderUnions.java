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
import MinorType.LIST;
import MinorType.MAP;
import MinorType.UNION;
import MinorType.VARCHAR;
import ObjectType.SCALAR;
import ObjectType.VARIANT;
import ResultSetLoaderImpl.ResultSetOptions;
import ValueType.STRING;
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.MetadataUtils;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.NullableIntVector;
import org.apache.drill.exec.vector.NullableVarCharVector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.accessor.ArrayWriter;
import org.apache.drill.exec.vector.accessor.ObjectWriter;
import org.apache.drill.exec.vector.accessor.ScalarWriter;
import org.apache.drill.exec.vector.accessor.TupleWriter;
import org.apache.drill.exec.vector.accessor.VariantWriter;
import org.apache.drill.exec.vector.accessor.writer.EmptyListShim;
import org.apache.drill.exec.vector.accessor.writer.SimpleListShim;
import org.apache.drill.exec.vector.accessor.writer.UnionVectorShim;
import org.apache.drill.exec.vector.complex.ListVector;
import org.apache.drill.exec.vector.complex.UnionVector;
import org.apache.drill.shaded.guava.com.google.common.base.Charsets;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetBuilder;
import org.apache.drill.test.rowSet.RowSetReader;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests the result set loader support for union vectors. Union vectors
 * are only lightly supported in Apache Drill and not supported at all
 * in commercial versions. They have may problems: both in code and in theory.
 * Most operators do not support them. But, JSON uses them, so they must
 * be made to work in the result set loader layer.
 */
// TODO: Simple list of map
// TODO: Regular list of map
// TODO: Regular list of union
@Category(RowSetTests.class)
public class TestResultSetLoaderUnions extends SubOperatorTest {
    @Test
    public void testUnionBasics() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addUnion("u").addType(VARCHAR).addMap().addNullable("a", INT).addNullable("b", VARCHAR).resumeUnion().resumeSchema().buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        final RowSetLoader writer = rsLoader.writer();
        // Sanity check of writer structure
        final ObjectWriter wo = writer.column(1);
        Assert.assertEquals(VARIANT, wo.type());
        final VariantWriter vw = wo.variant();
        Assert.assertTrue(vw.hasType(VARCHAR));
        Assert.assertNotNull(vw.memberWriter(VARCHAR));
        Assert.assertTrue(vw.hasType(MAP));
        Assert.assertNotNull(vw.memberWriter(MAP));
        // Write values
        rsLoader.startBatch();
        writer.addRow(1, "first").addRow(2, RowSetUtilities.mapValue(20, "fred")).addRow(3, null).addRow(4, RowSetUtilities.mapValue(40, null)).addRow(5, "last");
        // Verify the values.
        // (Relies on the row set level union tests having passed.)
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, "first").addRow(2, RowSetUtilities.mapValue(20, "fred")).addRow(3, null).addRow(4, RowSetUtilities.mapValue(40, null)).addRow(5, "last").build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
    }

    @Test
    public void testUnionAddTypes() {
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        final RowSetLoader writer = rsLoader.writer();
        rsLoader.startBatch();
        // First row, (1, "first"), create types as we go.
        writer.start();
        writer.addColumn(SchemaBuilder.columnSchema("id", INT, REQUIRED));
        writer.scalar("id").setInt(1);
        writer.addColumn(SchemaBuilder.columnSchema("u", UNION, OPTIONAL));
        final VariantWriter variant = writer.column("u").variant();
        variant.member(VARCHAR).scalar().setString("first");
        writer.save();
        // Second row, (2, {20, "fred"}), create types as we go.
        writer.start();
        writer.scalar("id").setInt(2);
        final TupleWriter innerMap = variant.member(MAP).tuple();
        innerMap.addColumn(SchemaBuilder.columnSchema("a", INT, OPTIONAL));
        innerMap.scalar("a").setInt(20);
        innerMap.addColumn(SchemaBuilder.columnSchema("b", VARCHAR, OPTIONAL));
        innerMap.scalar("b").setString("fred");
        writer.save();
        // Write remaining rows using convenient methods, using
        // schema defined above.
        writer.addRow(3, null).addRow(4, RowSetUtilities.mapValue(40, null)).addRow(5, "last");
        // Verify the values.
        // (Relies on the row set level union tests having passed.)
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addUnion("u").addType(VARCHAR).addMap().addNullable("a", INT).addNullable("b", VARCHAR).resumeUnion().resumeSchema().buildSchema();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, "first").addRow(2, RowSetUtilities.mapValue(20, "fred")).addRow(3, null).addRow(4, RowSetUtilities.mapValue(40, null)).addRow(5, "last").build();
        final RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        RowSetUtilities.verify(expected, result);
    }

    @Test
    public void testUnionOverflow() {
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addUnion("u").addType(INT).addType(VARCHAR).resumeSchema().buildSchema();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setSchema(schema).build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        final RowSetLoader writer = rsLoader.writer();
        // Fill the batch with enough data to cause overflow.
        // Fill even rows with a Varchar, odd rows with an int.
        // Data must be large enough to cause overflow before 32K rows
        // (the half that get strings.
        // 16 MB / 32 K = 512 bytes
        // Make a bit bigger to overflow early.
        final int strLength = 600;
        final byte[] value = new byte[strLength - 6];
        Arrays.fill(value, ((byte) ('X')));
        final String strValue = new String(value, Charsets.UTF_8);
        int count = 0;
        rsLoader.startBatch();
        while (!(writer.isFull())) {
            if ((count % 2) == 0) {
                writer.addRow(count, String.format("%s%06d", strValue, count));
            } else {
                writer.addRow(count, (count * 10));
            }
            count++;
        } 
        // Number of rows should be driven by vector size.
        // Our row count should include the overflow row
        final int expectedCount = ((ValueVector.MAX_BUFFER_SIZE) / strLength) * 2;
        Assert.assertEquals((expectedCount + 1), count);
        // Loader's row count should include only "visible" rows
        Assert.assertEquals(expectedCount, writer.rowCount());
        // Total count should include invisible and look-ahead rows.
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        // Result should exclude the overflow row
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(expectedCount, result.rowCount());
        // Verify the data.
        RowSetReader reader = result.reader();
        int readCount = 0;
        while (reader.next()) {
            Assert.assertEquals(readCount, scalar(0).getInt());
            if ((readCount % 2) == 0) {
                Assert.assertEquals(String.format("%s%06d", strValue, readCount), variant(1).scalar().getString());
            } else {
                Assert.assertEquals((readCount * 10), variant(1).scalar().getInt());
            }
            readCount++;
        } 
        Assert.assertEquals(readCount, result.rowCount());
        result.clear();
        // Write a few more rows to verify the overflow row.
        rsLoader.startBatch();
        for (int i = 0; i < 1000; i++) {
            if ((count % 2) == 0) {
                writer.addRow(count, String.format("%s%06d", strValue, count));
            } else {
                writer.addRow(count, (count * 10));
            }
            count++;
        }
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(1001, result.rowCount());
        final int startCount = readCount;
        reader = result.reader();
        while (reader.next()) {
            Assert.assertEquals(readCount, scalar(0).getInt());
            if ((readCount % 2) == 0) {
                Assert.assertEquals(String.format("%s%06d", strValue, readCount), variant(1).scalar().getString());
            } else {
                Assert.assertEquals((readCount * 10), variant(1).scalar().getInt());
            }
            readCount++;
        } 
        Assert.assertEquals((readCount - startCount), result.rowCount());
        result.clear();
        rsLoader.close();
    }

    /**
     * Test for the case of a list defined to contain exactly one type.
     * Relies on the row set tests to verify that the single type model
     * works for lists. Here we test that the ResultSetLoader put the
     * pieces together correctly.
     */
    @Test
    public void testSimpleList() {
        // Schema with a list declared with one type, not expandable
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addList("list").addType(VARCHAR).resumeSchema().buildSchema();
        schema.metadata("list").variantSchema().becomeSimple();
        final ResultSetLoaderImpl.ResultSetOptions options = new OptionBuilder().setSchema(schema).build();
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        final RowSetLoader writer = rsLoader.writer();
        // Sanity check: should be an array of Varchar because we said the
        // types within the list is not expandable.
        final ArrayWriter arrWriter = writer.array("list");
        Assert.assertEquals(SCALAR, arrWriter.entryType());
        final ScalarWriter strWriter = arrWriter.scalar();
        Assert.assertEquals(STRING, strWriter.valueType());
        // Can write a batch as if this was a repeated Varchar, except
        // that any value can also be null.
        rsLoader.startBatch();
        writer.addRow(1, RowSetUtilities.strArray("fred", "barney")).addRow(2, null).addRow(3, RowSetUtilities.strArray("wilma", "betty", "pebbles"));
        // Verify
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.strArray("fred", "barney")).addRow(2, null).addRow(3, RowSetUtilities.strArray("wilma", "betty", "pebbles")).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
    }

    /**
     * Test a simple list created dynamically at load time.
     * The list must include a single type member.
     */
    @Test
    public void testSimpleListDynamic() {
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        final RowSetLoader writer = rsLoader.writer();
        // Can write a batch as if this was a repeated Varchar, except
        // that any value can also be null.
        rsLoader.startBatch();
        writer.addColumn(MaterializedField.create("id", Types.required(INT)));
        final ColumnMetadata colSchema = MetadataUtils.newVariant("list", REPEATED);
        colSchema.variantSchema().addType(VARCHAR);
        colSchema.variantSchema().becomeSimple();
        writer.addColumn(colSchema);
        // Sanity check: should be an array of Varchar because we said the
        // types within the list is not expandable.
        final ArrayWriter arrWriter = writer.array("list");
        Assert.assertEquals(SCALAR, arrWriter.entryType());
        final ScalarWriter strWriter = arrWriter.scalar();
        Assert.assertEquals(STRING, strWriter.valueType());
        writer.addRow(1, RowSetUtilities.strArray("fred", "barney")).addRow(2, null).addRow(3, RowSetUtilities.strArray("wilma", "betty", "pebbles"));
        // Verify
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addList("list").addType(VARCHAR).resumeSchema().buildSchema();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, RowSetUtilities.strArray("fred", "barney")).addRow(2, null).addRow(3, RowSetUtilities.strArray("wilma", "betty", "pebbles")).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
    }

    /**
     * Try to create a simple (non-expandable) list without
     * giving a member type. Expected to fail.
     */
    @Test
    public void testSimpleListNoTypes() {
        // Schema with a list declared with one type, not expandable
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addList("list").resumeSchema().buildSchema();
        try {
            schema.metadata("list").variantSchema().becomeSimple();
            Assert.fail();
        } catch (final IllegalStateException e) {
            // expected
        }
    }

    /**
     * Try to create a simple (non-expandable) list while specifying
     * two types. Expected to fail.
     */
    @Test
    public void testSimpleListMultiTypes() {
        // Schema with a list declared with one type, not expandable
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addList("list").addType(VARCHAR).addType(INT).resumeSchema().buildSchema();
        try {
            schema.metadata("list").variantSchema().becomeSimple();
            Assert.fail();
        } catch (final IllegalStateException e) {
            // expected
        }
    }

    /**
     * Test a variant list created dynamically at load time.
     * The list starts with no type, at which time it can hold
     * only null values. Then we add a Varchar, and finally an
     * Int.
     * <p>
     * This test is superficial. There are many odd cases to consider.
     * <ul>
     * <li>Write nulls to a list with no type. (This test ensures that
     * adding a (nullable) scalar "does the right thing.")</li>
     * <li>Add a map to the list. Maps carry no "bits" vector, so null
     * list entries to that point are lost. (For maps, we could go straight
     * to a union, with just a map, to preserve the null states. This whole
     * area is a huge mess...)</li>
     * <li>Do the type transitions when writing to a row. (The tests here
     * do the transition between rows.</li>
     * </ul>
     *
     * The reason for the sparse coverage is that Drill barely supports lists
     * and unions; most code is just plain broken. Our goal here is not to fix
     * all those problems, just to leave things no more broken than before.
     */
    @Test
    public void testVariantListDynamic() {
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        final RowSetLoader writer = rsLoader.writer();
        // Can write a batch as if this was a repeated Varchar, except
        // that any value can also be null.
        rsLoader.startBatch();
        writer.addColumn(MaterializedField.create("id", Types.required(INT)));
        writer.addColumn(MaterializedField.create("list", Types.optional(LIST)));
        // Sanity check: should be an array of variants because we said the
        // types within the list are expandable (which is the default.)
        final ArrayWriter arrWriter = writer.array("list");
        Assert.assertEquals(VARIANT, arrWriter.entryType());
        final VariantWriter variant = arrWriter.variant();
        // We need to verify that the internal state is what we expect, so
        // the next assertion peeks inside the private bits of the union
        // writer. No client code should ever need to do this, of course.
        Assert.assertTrue(((shim()) instanceof EmptyListShim));
        // No types, so all we can do is add a null list, or a list of nulls.
        writer.addRow(1, null).addRow(2, RowSetUtilities.variantArray()).addRow(3, RowSetUtilities.variantArray(null, null));
        // Add a String. Now we can create a list of strings and/or nulls.
        variant.addMember(VARCHAR);
        Assert.assertTrue(variant.hasType(VARCHAR));
        // Sanity check: sniff inside to ensure that the list contains a single
        // type.
        Assert.assertTrue(((shim()) instanceof SimpleListShim));
        Assert.assertTrue(((vector().getDataVector()) instanceof NullableVarCharVector));
        writer.addRow(4, RowSetUtilities.variantArray("fred", null, "barney"));
        // Add an integer. The list vector should be promoted to union.
        // Now we can add both types.
        variant.addMember(INT);
        // Sanity check: sniff inside to ensure promotion to union occurred
        Assert.assertTrue(((shim()) instanceof UnionVectorShim));
        Assert.assertTrue(((vector().getDataVector()) instanceof UnionVector));
        writer.addRow(5, RowSetUtilities.variantArray("wilma", null, 30));
        // Verify
        final RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        // result.print();
        final TupleMetadata schema = new SchemaBuilder().add("id", INT).addList("list").addType(VARCHAR).addType(INT).resumeSchema().buildSchema();
        final RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addRow(1, null).addRow(2, RowSetUtilities.variantArray()).addRow(3, RowSetUtilities.variantArray(null, null)).addRow(4, RowSetUtilities.variantArray("fred", null, "barney")).addRow(5, RowSetUtilities.variantArray("wilma", null, 30)).build();
        RowSetUtilities.verify(expected, result);
    }

    /**
     * The semantics of the ListVector are such that it allows
     * multi-dimensional lists. In this way, it is like a (slightly
     * more normalized) version of the repeated list vector. This form
     * allows arrays to be null.
     * <p>
     * This test verifies that the (non-repeated) list vector can
     * be used to create multi-dimensional arrays in the result set
     * loader layer. However, the rest of Drill does not support this
     * functionality at present, so this test is more of a proof-of-
     * concept than a necessity.
     */
    @Test
    public void testListofListofScalar() {
        // JSON equivalent: {a: [[1, 2], [3, 4]]}
        final ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator());
        final RowSetLoader writer = rsLoader.writer();
        // Can write a batch as if this was a repeated Varchar, except
        // that any value can also be null.
        rsLoader.startBatch();
        writer.addColumn(MaterializedField.create("a", Types.optional(LIST)));
        final ArrayWriter outerArray = writer.array("a");
        final VariantWriter outerVariant = outerArray.variant();
        outerVariant.addMember(LIST);
        final ArrayWriter innerArray = outerVariant.array();
        final VariantWriter innerVariant = innerArray.variant();
        innerVariant.addMember(INT);
        writer.addSingleCol(RowSetUtilities.listValue(RowSetUtilities.listValue(1, 2), RowSetUtilities.listValue(3, 4)));
        final RowSet results = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        // Verify metadata
        final ListVector outer = ((ListVector) (results.container().getValueVector(0).getValueVector()));
        final MajorType outerType = outer.getField().getType();
        Assert.assertEquals(1, outerType.getSubTypeCount());
        Assert.assertEquals(LIST, outerType.getSubType(0));
        Assert.assertEquals(1, outer.getField().getChildren().size());
        final ListVector inner = ((ListVector) (outer.getDataVector()));
        Assert.assertSame(inner.getField(), outer.getField().getChildren().iterator().next());
        final MajorType innerType = inner.getField().getType();
        Assert.assertEquals(1, innerType.getSubTypeCount());
        Assert.assertEquals(INT, innerType.getSubType(0));
        Assert.assertEquals(1, inner.getField().getChildren().size());
        final ValueVector data = inner.getDataVector();
        Assert.assertSame(data.getField(), inner.getField().getChildren().iterator().next());
        Assert.assertEquals(INT, data.getField().getType().getMinorType());
        Assert.assertEquals(OPTIONAL, data.getField().getType().getMode());
        Assert.assertTrue((data instanceof NullableIntVector));
        // Note use of TupleMetadata: BatchSchema can't hold the
        // structure of a list.
        final TupleMetadata expectedSchema = new SchemaBuilder().addList("a").addList().addType(INT).resumeUnion().resumeSchema().buildSchema();
        final RowSet expected = new RowSetBuilder(SubOperatorTest.fixture.allocator(), expectedSchema).addSingleCol(RowSetUtilities.listValue(RowSetUtilities.listValue(1, 2), RowSetUtilities.listValue(3, 4))).build();
        RowSetUtilities.verify(expected, results);
    }
}

