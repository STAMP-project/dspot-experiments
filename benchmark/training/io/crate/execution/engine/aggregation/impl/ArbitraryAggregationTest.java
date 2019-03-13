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


import DataTypes.BOOLEAN;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import com.google.common.collect.ImmutableList;
import io.crate.expression.symbol.Literal;
import io.crate.metadata.FunctionImplementation;
import io.crate.metadata.SearchPath;
import io.crate.operation.aggregation.AggregationTest;
import io.crate.types.ObjectType;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ArbitraryAggregationTest extends AggregationTest {
    @Test
    public void testReturnType() throws Exception {
        FunctionImplementation arbitrary = functions.get(null, "arbitrary", ImmutableList.of(Literal.of(INTEGER, null)), SearchPath.pathWithPGCatalogAndDoc());
        assertEquals(INTEGER, arbitrary.info().returnType());
    }

    @Test
    public void testDouble() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ 0.8 }, new Object[]{ 0.3 } };
        Object[][] result = executeAggregation(DOUBLE, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test
    public void testFloat() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ 0.8F }, new Object[]{ 0.3F } };
        Object[][] result = executeAggregation(FLOAT, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test
    public void testInteger() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ 8 }, new Object[]{ 3 } };
        Object[][] result = executeAggregation(INTEGER, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test
    public void testLong() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ 8L }, new Object[]{ 3L } };
        Object[][] result = executeAggregation(LONG, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test
    public void testShort() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ ((short) (8)) }, new Object[]{ ((short) (3)) } };
        Object[][] result = executeAggregation(SHORT, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test
    public void testString() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ "Youri" }, new Object[]{ "Ruben" } };
        Object[][] result = executeAggregation(STRING, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test
    public void testBoolean() throws Exception {
        Object[][] data = new Object[][]{ new Object[]{ true }, new Object[]{ false } };
        Object[][] result = executeAggregation(BOOLEAN, data);
        assertThat(result[0][0], Matchers.isOneOf(data[0][0], data[1][0]));
    }

    @Test(expected = NullPointerException.class)
    public void testUnsupportedType() throws Exception {
        Object[][] result = executeAggregation(ObjectType.untyped(), new Object[][]{ new Object[]{ new Object() } });
    }
}

