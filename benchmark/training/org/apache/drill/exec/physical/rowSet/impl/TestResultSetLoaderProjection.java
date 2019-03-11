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


import DataMode.REQUIRED;
import MinorType.INT;
import MinorType.VARCHAR;
import ValueVector.MAX_ROW_COUNT;
import java.util.Arrays;
import java.util.List;
import org.apache.drill.categories.RowSetTests;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.exec.physical.rowSet.ResultSetLoader;
import org.apache.drill.exec.physical.rowSet.RowSetLoader;
import org.apache.drill.exec.physical.rowSet.impl.ResultSetLoaderImpl.ResultSetOptions;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.ColumnMetadata;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.record.metadata.TupleMetadata;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.test.SubOperatorTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test of the basics of the projection mechanism.
 */
@Category(RowSetTests.class)
public class TestResultSetLoaderProjection extends SubOperatorTest {
    /**
     * Test imposing a selection mask between the client and the underlying
     * vector container.
     */
    @Test
    public void testProjectionStatic() {
        List<SchemaPath> selection = RowSetTestUtils.projectList("c", "b", "e");
        TupleMetadata schema = new SchemaBuilder().add("a", INT).add("b", INT).add("c", INT).add("d", INT).buildSchema();
        ResultSetOptions options = new OptionBuilder().setProjection(selection).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        doProjectionTest(rsLoader);
    }

    @Test
    public void testProjectionDynamic() {
        List<SchemaPath> selection = RowSetTestUtils.projectList("c", "b", "e");
        ResultSetOptions options = new OptionBuilder().setProjection(selection).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        rootWriter.addColumn(SchemaBuilder.columnSchema("a", INT, REQUIRED));
        rootWriter.addColumn(SchemaBuilder.columnSchema("b", INT, REQUIRED));
        rootWriter.addColumn(SchemaBuilder.columnSchema("c", INT, REQUIRED));
        rootWriter.addColumn(SchemaBuilder.columnSchema("d", INT, REQUIRED));
        doProjectionTest(rsLoader);
    }

    @Test
    public void testMapProjection() {
        List<SchemaPath> selection = RowSetTestUtils.projectList("m1", "m2.d");
        TupleMetadata schema = new SchemaBuilder().addMap("m1").add("a", INT).add("b", INT).resumeSchema().addMap("m2").add("c", INT).add("d", INT).resumeSchema().addMap("m3").add("e", INT).add("f", INT).resumeSchema().buildSchema();
        ResultSetOptions options = new OptionBuilder().setProjection(selection).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Verify the projected columns
        TupleMetadata actualSchema = rootWriter.tupleSchema();
        ColumnMetadata m1Md = actualSchema.metadata("m1");
        Assert.assertTrue(m1Md.isMap());
        Assert.assertTrue(m1Md.isProjected());
        Assert.assertEquals(2, m1Md.mapSchema().size());
        Assert.assertTrue(m1Md.mapSchema().metadata("a").isProjected());
        Assert.assertTrue(m1Md.mapSchema().metadata("b").isProjected());
        ColumnMetadata m2Md = actualSchema.metadata("m2");
        Assert.assertTrue(m2Md.isMap());
        Assert.assertTrue(m2Md.isProjected());
        Assert.assertEquals(2, m2Md.mapSchema().size());
        Assert.assertFalse(m2Md.mapSchema().metadata("c").isProjected());
        Assert.assertTrue(m2Md.mapSchema().metadata("d").isProjected());
        ColumnMetadata m3Md = actualSchema.metadata("m3");
        Assert.assertTrue(m3Md.isMap());
        Assert.assertFalse(m3Md.isProjected());
        Assert.assertEquals(2, m3Md.mapSchema().size());
        Assert.assertFalse(m3Md.mapSchema().metadata("e").isProjected());
        Assert.assertFalse(m3Md.mapSchema().metadata("f").isProjected());
        // Write a couple of rows.
        rsLoader.startBatch();
        rootWriter.start();
        rootWriter.addRow(RowSetUtilities.mapValue(1, 2), RowSetUtilities.mapValue(3, 4), RowSetUtilities.mapValue(5, 6)).addRow(RowSetUtilities.mapValue(11, 12), RowSetUtilities.mapValue(13, 14), RowSetUtilities.mapValue(15, 16));
        // Verify. Only the projected columns appear in the result set.
        BatchSchema expectedSchema = new SchemaBuilder().addMap("m1").add("a", INT).add("b", INT).resumeSchema().addMap("m2").add("d", INT).resumeSchema().build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(RowSetUtilities.mapValue(1, 2), RowSetUtilities.mapValue(4)).addRow(RowSetUtilities.mapValue(11, 12), RowSetUtilities.mapValue(14)).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
        rsLoader.close();
    }

    @Test
    public void testMapProjectionMemberAndMap() {
        List<SchemaPath> selection = RowSetTestUtils.projectList("m1", "m1.b");
        TupleMetadata schema = new SchemaBuilder().addMap("m1").add("a", INT).add("b", INT).resumeSchema().buildSchema();
        ResultSetOptions options = new OptionBuilder().setProjection(selection).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Verify the projected columns
        TupleMetadata actualSchema = rootWriter.tupleSchema();
        ColumnMetadata m1Md = actualSchema.metadata("m1");
        Assert.assertTrue(m1Md.isMap());
        Assert.assertTrue(m1Md.isProjected());
        Assert.assertEquals(2, m1Md.mapSchema().size());
        Assert.assertTrue(m1Md.mapSchema().metadata("a").isProjected());
        Assert.assertTrue(m1Md.mapSchema().metadata("b").isProjected());
        // Write a couple of rows.
        rsLoader.startBatch();
        rootWriter.start();
        rootWriter.addSingleCol(RowSetUtilities.mapValue(1, 2)).addSingleCol(RowSetUtilities.mapValue(11, 12));
        // Verify. The whole map appears in the result set because the
        // project list included the whole map as well as a map member.
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(schema).addSingleCol(RowSetUtilities.mapValue(1, 2)).addSingleCol(RowSetUtilities.mapValue(11, 12)).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
        rsLoader.close();
    }

    /**
     * Test a map array. Use the convenience methods to set values.
     * Only the projected array members should appear in the harvested
     * results.
     */
    @Test
    public void testMapArrayProjection() {
        List<SchemaPath> selection = RowSetTestUtils.projectList("m1", "m2.d");
        TupleMetadata schema = new SchemaBuilder().addMapArray("m1").add("a", INT).add("b", INT).resumeSchema().addMapArray("m2").add("c", INT).add("d", INT).resumeSchema().addMapArray("m3").add("e", INT).add("f", INT).resumeSchema().buildSchema();
        ResultSetOptions options = new OptionBuilder().setProjection(selection).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        // Write a couple of rows.
        rsLoader.startBatch();
        rootWriter.addRow(RowSetUtilities.objArray(RowSetUtilities.objArray(10, 20), RowSetUtilities.objArray(11, 21)), RowSetUtilities.objArray(RowSetUtilities.objArray(30, 40), RowSetUtilities.objArray(31, 42)), RowSetUtilities.objArray(RowSetUtilities.objArray(50, 60), RowSetUtilities.objArray(51, 62)));
        rootWriter.addRow(RowSetUtilities.objArray(RowSetUtilities.objArray(110, 120), RowSetUtilities.objArray(111, 121)), RowSetUtilities.objArray(RowSetUtilities.objArray(130, 140), RowSetUtilities.objArray(131, 142)), RowSetUtilities.objArray(RowSetUtilities.objArray(150, 160), RowSetUtilities.objArray(151, 162)));
        // Verify. Only the projected columns appear in the result set.
        BatchSchema expectedSchema = new SchemaBuilder().addMapArray("m1").add("a", INT).add("b", INT).resumeSchema().addMapArray("m2").add("d", INT).resumeSchema().build();
        RowSet.SingleRowSet expected = SubOperatorTest.fixture.rowSetBuilder(expectedSchema).addRow(RowSetUtilities.objArray(RowSetUtilities.objArray(10, 20), RowSetUtilities.objArray(11, 21)), RowSetUtilities.objArray(RowSetUtilities.objArray(40), RowSetUtilities.objArray(42))).addRow(RowSetUtilities.objArray(RowSetUtilities.objArray(110, 120), RowSetUtilities.objArray(111, 121)), RowSetUtilities.objArray(RowSetUtilities.objArray(140), RowSetUtilities.objArray(142))).build();
        RowSetUtilities.verify(expected, SubOperatorTest.fixture.wrap(rsLoader.harvest()));
        rsLoader.close();
    }

    /**
     * Verify that the projection code plays nice with vector overflow. Overflow
     * is the most complex operation in this subsystem with many specialized
     * methods that must work together flawlessly. This test ensures that
     * non-projected columns stay in the background and don't interfere
     * with overflow logic.
     */
    @Test
    public void testProjectWithOverflow() {
        List<SchemaPath> selection = RowSetTestUtils.projectList("small", "dummy");
        TupleMetadata schema = new SchemaBuilder().add("big", VARCHAR).add("small", VARCHAR).buildSchema();
        ResultSetOptions options = new OptionBuilder().setRowCountLimit(MAX_ROW_COUNT).setProjection(selection).setSchema(schema).build();
        ResultSetLoader rsLoader = new ResultSetLoaderImpl(SubOperatorTest.fixture.allocator(), options);
        RowSetLoader rootWriter = rsLoader.writer();
        byte[] big = new byte[600];
        Arrays.fill(big, ((byte) ('X')));
        byte[] small = new byte[512];
        Arrays.fill(small, ((byte) ('X')));
        rsLoader.startBatch();
        int count = 0;
        while (!(rootWriter.isFull())) {
            rootWriter.start();
            rootWriter.scalar(0).setBytes(big, big.length);
            rootWriter.scalar(1).setBytes(small, small.length);
            rootWriter.save();
            count++;
        } 
        // Number of rows should be driven by size of the
        // projected vector ("small"), not by the larger, unprojected
        // "big" vector.
        // Our row count should include the overflow row
        int expectedCount = (ValueVector.MAX_BUFFER_SIZE) / (small.length);
        Assert.assertEquals((expectedCount + 1), count);
        // Loader's row count should include only "visible" rows
        Assert.assertEquals(expectedCount, rootWriter.rowCount());
        // Total count should include invisible and look-ahead rows.
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        // Result should exclude the overflow row
        RowSet result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(expectedCount, result.rowCount());
        result.clear();
        // Next batch should start with the overflow row
        rsLoader.startBatch();
        Assert.assertEquals(1, rootWriter.rowCount());
        Assert.assertEquals((expectedCount + 1), rsLoader.totalRowCount());
        result = SubOperatorTest.fixture.wrap(rsLoader.harvest());
        Assert.assertEquals(1, result.rowCount());
        result.clear();
        rsLoader.close();
    }
}

