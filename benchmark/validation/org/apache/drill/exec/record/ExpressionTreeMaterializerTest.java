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


import DataMode.REQUIRED;
import IfExpression.IfCondition;
import MinorType.BIGINT;
import MinorType.BIT;
import ValueExpressions.BooleanExpression;
import ValueExpressions.LongExpression;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.expression.ErrorCollector;
import org.apache.drill.common.expression.ErrorCollectorImpl;
import org.apache.drill.common.expression.ExpressionPosition;
import org.apache.drill.common.expression.IfExpression;
import org.apache.drill.common.expression.LogicalExpression;
import org.apache.drill.common.expression.SchemaPath;
import org.apache.drill.common.expression.TypedNullConstant;
import org.apache.drill.common.expression.ValueExpressions;
import org.apache.drill.common.types.TypeProtos.MajorType;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.exception.SchemaChangeException;
import org.apache.drill.exec.expr.ExpressionTreeMaterializer;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.apache.drill.shaded.guava.com.google.common.collect.Range;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(VectorTest.class)
public class ExpressionTreeMaterializerTest extends ExecTest {
    final MajorType bigIntType = MajorType.newBuilder().setMode(REQUIRED).setMinorType(BIGINT).build();

    DrillConfig c = DrillConfig.create();

    FunctionImplementationRegistry registry = new FunctionImplementationRegistry(c);

    @Test
    public void testMaterializingConstantTree() throws SchemaChangeException {
        final RecordBatch batch = Mockito.mock(RecordBatch.class);
        ErrorCollector ec = new ErrorCollectorImpl();
        LogicalExpression expr = ExpressionTreeMaterializer.materialize(new ValueExpressions.LongExpression(1L, ExpressionPosition.UNKNOWN), batch, ec, registry);
        Assert.assertTrue((expr instanceof ValueExpressions.LongExpression));
        Assert.assertEquals(1L, LongExpression.class.cast(expr).getLong());
        Assert.assertFalse(ec.hasErrors());
    }

    @Test
    public void testMaterializingLateboundField() throws SchemaChangeException {
        final RecordBatch batch = Mockito.mock(RecordBatch.class);
        Mockito.when(batch.getValueVectorId(new SchemaPath("test", ExpressionPosition.UNKNOWN))).thenReturn(new TypedFieldId(Types.required(BIGINT), (-5)));
        final SchemaBuilder builder = BatchSchema.newBuilder();
        builder.addField(getField("test", bigIntType));
        final BatchSchema schema = builder.build();
        ErrorCollector ec = new ErrorCollectorImpl();
        LogicalExpression expr = ExpressionTreeMaterializer.materialize(new org.apache.drill.common.expression.FieldReference("test", ExpressionPosition.UNKNOWN), batch, ec, registry);
        Assert.assertEquals(bigIntType, expr.getMajorType());
        Assert.assertFalse(ec.hasErrors());
    }

    @Test
    public void testMaterializingLateboundTree() throws SchemaChangeException {
        final RecordBatch batch = Mockito.mock(RecordBatch.class);
        Mockito.when(batch.getValueVectorId(SchemaPath.getSimplePath("test"))).thenReturn(new TypedFieldId(Types.required(BIT), (-4)));
        Mockito.when(batch.getValueVectorId(SchemaPath.getSimplePath("test1"))).thenReturn(new TypedFieldId(Types.required(BIGINT), (-5)));
        ErrorCollector ec = new ErrorCollectorImpl();
        LogicalExpression elseExpression = new IfExpression.Builder().setElse(new ValueExpressions.LongExpression(1L, ExpressionPosition.UNKNOWN)).setIfCondition(new IfExpression.IfCondition(new ValueExpressions.BooleanExpression("true", ExpressionPosition.UNKNOWN), new org.apache.drill.common.expression.FieldReference("test1", ExpressionPosition.UNKNOWN))).build();
        LogicalExpression expr = new IfExpression.Builder().setIfCondition(new IfExpression.IfCondition(new org.apache.drill.common.expression.FieldReference("test", ExpressionPosition.UNKNOWN), new ValueExpressions.LongExpression(2L, ExpressionPosition.UNKNOWN))).setElse(elseExpression).build();
        LogicalExpression newExpr = ExpressionTreeMaterializer.materialize(expr, batch, ec, registry);
        Assert.assertTrue((newExpr instanceof IfExpression));
        IfExpression newIfExpr = ((IfExpression) (newExpr));
        IfExpression.IfCondition ifCondition = newIfExpr.ifCondition;
        Assert.assertTrue(((newIfExpr.elseExpression) instanceof IfExpression));
        Assert.assertEquals(bigIntType, ifCondition.expression.getMajorType());
        Assert.assertEquals(true, ((ValueExpressions.BooleanExpression) (((IfExpression) (newIfExpr.elseExpression)).ifCondition.condition)).value);
        Assert.assertFalse(ec.hasErrors());
    }

    @Test
    public void testMaterializingLateboundTreeValidated() throws SchemaChangeException {
        final RecordBatch batch = Mockito.mock(RecordBatch.class);
        Mockito.when(batch.getValueVectorId(new SchemaPath("test", ExpressionPosition.UNKNOWN))).thenReturn(new TypedFieldId(Types.required(BIGINT), (-5)));
        ErrorCollector ec = new ErrorCollector() {
            int errorCount = 0;

            @Override
            public void addGeneralError(ExpressionPosition expr, String s) {
                (errorCount)++;
            }

            @Override
            public void addUnexpectedArgumentType(ExpressionPosition expr, String name, MajorType actual, MajorType[] expected, int argumentIndex) {
                (errorCount)++;
            }

            @Override
            public void addUnexpectedArgumentCount(ExpressionPosition expr, int actual, Range<Integer> expected) {
                (errorCount)++;
            }

            @Override
            public void addUnexpectedArgumentCount(ExpressionPosition expr, int actual, int expected) {
                (errorCount)++;
            }

            @Override
            public void addNonNumericType(ExpressionPosition expr, MajorType actual) {
                (errorCount)++;
            }

            @Override
            public void addUnexpectedType(ExpressionPosition expr, int index, MajorType actual) {
                (errorCount)++;
            }

            @Override
            public void addExpectedConstantValue(ExpressionPosition expr, int actual, String s) {
                (errorCount)++;
            }

            @Override
            public boolean hasErrors() {
                return (errorCount) > 0;
            }

            @Override
            public String toErrorString() {
                return String.format("Found %s errors.", errorCount);
            }

            @Override
            public int getErrorCount() {
                return errorCount;
            }
        };
        LogicalExpression functionCallExpr = new org.apache.drill.common.expression.FunctionCall("testFunc", ImmutableList.of(((LogicalExpression) (new org.apache.drill.common.expression.FieldReference("test", ExpressionPosition.UNKNOWN)))), ExpressionPosition.UNKNOWN);
        LogicalExpression newExpr = ExpressionTreeMaterializer.materialize(functionCallExpr, batch, ec, registry);
        Assert.assertTrue((newExpr instanceof TypedNullConstant));
        Assert.assertEquals(1, ec.getErrorCount());
    }
}

