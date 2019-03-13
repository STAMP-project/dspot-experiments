/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.execution.engine.aggregation.impl;


import DataTypes.BYTE;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import io.crate.operation.aggregation.AggregationTest;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import org.junit.Test;


public class SumAggregationTest extends AggregationTest {
    @Test
    public void testReturnType() throws Exception {
        DataType type = DataTypes.DOUBLE;
        assertEquals(type, getSum(type).info().returnType());
        type = DataTypes.FLOAT;
        assertEquals(type, getSum(type).info().returnType());
        type = DataTypes.LONG;
        assertEquals(type, getSum(type).info().returnType());
        assertEquals(type, getSum(INTEGER).info().returnType());
        assertEquals(type, getSum(SHORT).info().returnType());
        assertEquals(type, getSum(BYTE).info().returnType());
    }

    @Test
    public void testDouble() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, new Object[][]{ new Object[]{ 0.7 }, new Object[]{ 0.3 } });
        assertEquals(1.0, result[0][0]);
    }

    @Test
    public void testFloat() throws Exception {
        Object[][] result = executeAggregation(FLOAT, new Object[][]{ new Object[]{ 0.7F }, new Object[]{ 0.3F } });
        assertEquals(1.0F, result[0][0]);
    }

    @Test
    public void testLong() throws Exception {
        Object[][] result = executeAggregation(LONG, new Object[][]{ new Object[]{ 7L }, new Object[]{ 3L } });
        assertEquals(10L, result[0][0]);
    }

    @Test(expected = ArithmeticException.class)
    public void testLongOverflow() throws Exception {
        Object[][] result = executeAggregation(LONG, new Object[][]{ new Object[]{ Long.MAX_VALUE }, new Object[]{ 1 } });
    }

    @Test(expected = ArithmeticException.class)
    public void testLongUnderflow() throws Exception {
        Object[][] result = executeAggregation(LONG, new Object[][]{ new Object[]{ Long.MIN_VALUE }, new Object[]{ -1 } });
    }

    @Test
    public void testInteger() throws Exception {
        Object[][] result = executeAggregation(INTEGER, new Object[][]{ new Object[]{ 7 }, new Object[]{ 3 } });
        assertEquals(10L, result[0][0]);
    }

    @Test
    public void testShort() throws Exception {
        Object[][] result = executeAggregation(SHORT, new Object[][]{ new Object[]{ ((short) (7)) }, new Object[]{ ((short) (3)) } });
        assertEquals(10L, result[0][0]);
    }

    @Test
    public void testByte() throws Exception {
        Object[][] result = executeAggregation(BYTE, new Object[][]{ new Object[]{ ((byte) (7)) }, new Object[]{ ((byte) (3)) } });
        assertEquals(10L, result[0][0]);
    }

    @Test(expected = NullPointerException.class)
    public void testUnsupportedType() throws Exception {
        Object[][] result = executeAggregation(STRING, new Object[][]{ new Object[]{ "Youri" }, new Object[]{ "Ruben" } });
    }
}

