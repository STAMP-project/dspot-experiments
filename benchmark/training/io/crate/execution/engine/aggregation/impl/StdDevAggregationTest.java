/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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


import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.NUMERIC_PRIMITIVE_TYPES;
import DataTypes.SHORT;
import DataTypes.STRING;
import DataTypes.TIMESTAMP;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.crate.expression.symbol.Literal;
import io.crate.metadata.FunctionImplementation;
import io.crate.metadata.SearchPath;
import io.crate.operation.aggregation.AggregationTest;
import io.crate.types.DataType;
import java.util.Arrays;
import org.junit.Test;


public class StdDevAggregationTest extends AggregationTest {
    @Test
    public void testReturnType() throws Exception {
        for (DataType<?> type : Iterables.concat(NUMERIC_PRIMITIVE_TYPES, Arrays.asList(TIMESTAMP))) {
            // Return type is fixed to Double
            FunctionImplementation stddev = functions.get(null, "stddev", ImmutableList.of(Literal.of(type, null)), SearchPath.pathWithPGCatalogAndDoc());
            assertEquals(DOUBLE, stddev.info().returnType());
        }
    }

    @Test
    public void withNullArg() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, new Object[][]{ new Object[]{ null }, new Object[]{ null } });
        assertNull(result[0][0]);
    }

    @Test
    public void withSomeNullArgs() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, new Object[][]{ new Object[]{ 10.7 }, new Object[]{ 42.9 }, new Object[]{ 0.3 }, new Object[]{ null } });
        assertEquals(18.13455878212156, result[0][0]);
    }

    @Test
    public void testDouble() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, new Object[][]{ new Object[]{ 10.7 }, new Object[]{ 42.9 }, new Object[]{ 0.3 } });
        assertEquals(18.13455878212156, result[0][0]);
    }

    @Test
    public void testFloat() throws Exception {
        Object[][] result = executeAggregation(FLOAT, new Object[][]{ new Object[]{ 1.5F }, new Object[]{ 1.25F }, new Object[]{ 1.75F } });
        assertEquals(0.2041241452319315, result[0][0]);
    }

    @Test
    public void testInteger() throws Exception {
        Object[][] result = executeAggregation(INTEGER, new Object[][]{ new Object[]{ 7 }, new Object[]{ 3 } });
        assertEquals(2.0, result[0][0]);
    }

    @Test
    public void testLong() throws Exception {
        Object[][] result = executeAggregation(LONG, new Object[][]{ new Object[]{ 7L }, new Object[]{ 3L } });
        assertEquals(2.0, result[0][0]);
    }

    @Test
    public void testShort() throws Exception {
        Object[][] result = executeAggregation(SHORT, new Object[][]{ new Object[]{ ((short) (7)) }, new Object[]{ ((short) (3)) } });
        assertEquals(2.0, result[0][0]);
    }

    @Test
    public void testByte() throws Exception {
        Object[][] result = executeAggregation(SHORT, new Object[][]{ new Object[]{ ((short) (1)) }, new Object[]{ ((short) (1)) } });
        assertEquals(0.0, result[0][0]);
    }

    @Test(expected = NullPointerException.class)
    public void testUnsupportedType() throws Exception {
        Object[][] result = executeAggregation(STRING, new Object[][]{ new Object[]{ "Youri" }, new Object[]{ "Ruben" } });
    }
}

