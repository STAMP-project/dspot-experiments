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
package org.apache.drill.exec.expr;


import MinorType.BIGINT;
import MinorType.INT;
import TypeProtos.MajorType;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.exceptions.ExpressionParsingException;
import org.apache.drill.common.expression.ExpressionPosition;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.expr.fn.FunctionImplementationRegistry;
import org.apache.drill.exec.memory.RootAllocatorFactory;
import org.apache.drill.exec.record.MaterializedField;
import org.apache.drill.exec.record.RecordBatch;
import org.apache.drill.exec.record.TypedFieldId;
import org.apache.drill.exec.record.VectorWrapper;
import org.apache.drill.exec.vector.IntVector;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExpressionTest extends ExecTest {
    private static final Logger logger = LoggerFactory.getLogger(ExpressionTest.class);

    private final DrillConfig c = DrillConfig.create();

    private final FunctionImplementationRegistry registry = new FunctionImplementationRegistry(c);

    @Test
    public void testBasicExpression() throws Exception {
        getExpressionCode("if(true) then 1 else 0 end");
    }

    @Test
    public void testExprParseUpperExponent() throws Exception {
        getExpressionCode("multiply(`$f0`, 1.0E-4)");
    }

    @Test
    public void testExprParseLowerExponent() throws Exception {
        getExpressionCode("multiply(`$f0`, 1.0e-4)");
    }

    @Test
    public void testSpecial() throws Exception {
        final RecordBatch batch = Mockito.mock(RecordBatch.class);
        final VectorWrapper wrapper = Mockito.mock(VectorWrapper.class);
        final TypeProtos.MajorType type = Types.optional(INT);
        final TypedFieldId tfid = new TypedFieldId(type, false, 0);
        Mockito.when(wrapper.getValueVector()).thenReturn(new IntVector(MaterializedField.create("result", type), RootAllocatorFactory.newRoot(c)));
        Mockito.when(batch.getValueVectorId(new org.apache.drill.common.expression.SchemaPath("alpha", ExpressionPosition.UNKNOWN))).thenReturn(tfid);
        Mockito.when(batch.getValueAccessorById(IntVector.class, tfid.getFieldIds())).thenReturn(wrapper);
        getExpressionCode("1 + 1", batch);
    }

    @Test
    public void testSchemaExpression() throws Exception {
        final RecordBatch batch = Mockito.mock(RecordBatch.class);
        Mockito.when(batch.getValueVectorId(new org.apache.drill.common.expression.SchemaPath("alpha", ExpressionPosition.UNKNOWN))).thenReturn(new TypedFieldId(Types.optional(BIGINT), false, 0));
        getExpressionCode("1 + alpha", batch);
    }

    @Test(expected = ExpressionParsingException.class)
    public void testExprParseError() throws Exception {
        getExpressionCode("less than(1, 2)");
    }

    @Test
    public void testExprParseNoError() throws Exception {
        getExpressionCode("equal(1, 2)");
    }
}

