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
package org.apache.drill.exec.physical.impl.validate;


import DataMode.REPEATED;
import MinorType.INT;
import MinorType.VARCHAR;
import java.util.List;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.VectorAccessible;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.vector.RepeatedVarCharVector;
import org.apache.drill.exec.vector.UInt4Vector;
import org.apache.drill.exec.vector.ValueVector;
import org.apache.drill.exec.vector.VarCharVector;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.LogFixture;
import org.apache.drill.test.OperatorFixture;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/* TODO: extends SubOperatorTest */
public class TestBatchValidator {
    protected static OperatorFixture fixture;

    protected static LogFixture logFixture;

    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void testValidFixed() {
        BatchSchema schema = new SchemaBuilder().add("a", INT).addNullable("b", INT).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow(10, 100).addRow(20, 120).addRow(30, null).addRow(40, 140).build();
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        Assert.assertTrue(validator.errors().isEmpty());
        batch.clear();
    }

    @Test
    public void testValidVariable() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR).addNullable("b", VARCHAR).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow("col1.1", "col1.2").addRow("col2.1", "col2.2").addRow("col3.1", null).addRow("col4.1", "col4.2").build();
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        Assert.assertTrue(validator.errors().isEmpty());
        batch.clear();
    }

    @Test
    public void testValidRepeated() {
        BatchSchema schema = new SchemaBuilder().add("a", INT, REPEATED).add("b", VARCHAR, REPEATED).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow(RowSetUtilities.intArray(), RowSetUtilities.strArray()).addRow(RowSetUtilities.intArray(1, 2, 3), RowSetUtilities.strArray("fred", "barney", "wilma")).addRow(RowSetUtilities.intArray(4), RowSetUtilities.strArray("dino")).build();
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        Assert.assertTrue(validator.errors().isEmpty());
        batch.clear();
    }

    @Test
    public void testVariableMissingLast() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow("x").addRow("y").addRow("z").build();
        // Here we are evil: stomp on the last offset to simulate corruption.
        // Don't do this in real code!
        VectorAccessible va = batch.vectorAccessible();
        ValueVector v = va.iterator().next().getValueVector();
        VarCharVector vc = ((VarCharVector) (v));
        UInt4Vector ov = vc.getOffsetVector();
        Assert.assertTrue(((ov.getAccessor().get(3)) > 0));
        ov.getMutator().set(3, 0);
        // Validator should catch the error.
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Decreasing offsets"));
        batch.clear();
    }

    @Test
    public void testVariableCorruptFirst() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow("x").addRow("y").addRow("z").build();
        zapOffset(batch, 0, 1);
        // Validator should catch the error.
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Offset (0) must be 0"));
        batch.clear();
    }

    @Test
    public void testVariableCorruptMiddleLow() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow("xx").addRow("yy").addRow("zz").build();
        zapOffset(batch, 2, 1);
        // Validator should catch the error.
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Decreasing offsets"));
        batch.clear();
    }

    @Test
    public void testVariableCorruptMiddleHigh() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow("xx").addRow("yy").addRow("zz").build();
        zapOffset(batch, 1, 10);
        // Validator should catch the error.
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Decreasing offsets"));
        batch.clear();
    }

    @Test
    public void testVariableCorruptLastOutOfRange() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow("xx").addRow("yy").addRow("zz").build();
        zapOffset(batch, 3, 100000);
        // Validator should catch the error.
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Invalid offset"));
        batch.clear();
    }

    @Test
    public void testRepeatedBadArrayOffset() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR, REPEATED).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow(((Object) (RowSetUtilities.strArray()))).addRow(((Object) (RowSetUtilities.strArray("fred", "barney", "wilma")))).addRow(((Object) (RowSetUtilities.strArray("dino")))).build();
        VectorAccessible va = batch.vectorAccessible();
        ValueVector v = va.iterator().next().getValueVector();
        RepeatedVarCharVector vc = ((RepeatedVarCharVector) (v));
        UInt4Vector ov = vc.getOffsetVector();
        ov.getMutator().set(3, 1);
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Decreasing offsets"));
        batch.clear();
    }

    @Test
    public void testRepeatedBadValueOffset() {
        BatchSchema schema = new SchemaBuilder().add("a", VARCHAR, REPEATED).build();
        RowSet.SingleRowSet batch = TestBatchValidator.fixture.rowSetBuilder(schema).addRow(((Object) (RowSetUtilities.strArray()))).addRow(((Object) (RowSetUtilities.strArray("fred", "barney", "wilma")))).addRow(((Object) (RowSetUtilities.strArray("dino")))).build();
        VectorAccessible va = batch.vectorAccessible();
        ValueVector v = va.iterator().next().getValueVector();
        RepeatedVarCharVector rvc = ((RepeatedVarCharVector) (v));
        VarCharVector vc = rvc.getDataVector();
        UInt4Vector ov = vc.getOffsetVector();
        ov.getMutator().set(4, 100000);
        BatchValidator validator = new BatchValidator(batch.vectorAccessible(), true);
        validator.validate();
        List<String> errors = validator.errors();
        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.get(0).contains("Invalid offset"));
        batch.clear();
    }
}

