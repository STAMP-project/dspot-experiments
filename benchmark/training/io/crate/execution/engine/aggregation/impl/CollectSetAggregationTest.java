/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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


import BigArrays.NON_RECYCLING_INSTANCE;
import DataTypes.BOOLEAN;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import Version.CURRENT;
import com.google.common.collect.ImmutableList;
import io.crate.execution.engine.aggregation.AggregationFunction;
import io.crate.expression.symbol.Literal;
import io.crate.metadata.FunctionImplementation;
import io.crate.metadata.SearchPath;
import io.crate.operation.aggregation.AggregationTest;
import io.crate.types.DataTypes;
import java.util.Set;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;


public class CollectSetAggregationTest extends AggregationTest {
    @Test
    public void testReturnType() throws Exception {
        FunctionImplementation collectSet = functions.get(null, "collect_set", ImmutableList.of(Literal.of(INTEGER, null)), SearchPath.pathWithPGCatalogAndDoc());
        assertEquals(new io.crate.types.SetType(DataTypes.INTEGER), collectSet.info().returnType());
    }

    @Test
    public void testDouble() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, new Object[][]{ new Object[]{ 0.7 }, new Object[]{ 0.3 }, new Object[]{ 0.3 } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains(0.7));
    }

    @Test
    public void testLongSerialization() throws Exception {
        AggregationFunction impl = ((AggregationFunction) (functions.get(null, "collect_set", ImmutableList.of(Literal.of(LONG, null)), SearchPath.pathWithPGCatalogAndDoc())));
        Object state = impl.newState(AggregationTest.ramAccountingContext, CURRENT, NON_RECYCLING_INSTANCE);
        BytesStreamOutput streamOutput = new BytesStreamOutput();
        impl.partialType().streamer().writeValueTo(streamOutput, state);
        Object newState = impl.partialType().streamer().readValueFrom(streamOutput.bytes().streamInput());
        assertEquals(state, newState);
    }

    @Test
    public void testFloat() throws Exception {
        Object[][] result = executeAggregation(FLOAT, new Object[][]{ new Object[]{ 0.7F }, new Object[]{ 0.3F }, new Object[]{ 0.3F } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains(0.7F));
    }

    @Test
    public void testInteger() throws Exception {
        Object[][] result = executeAggregation(INTEGER, new Object[][]{ new Object[]{ 7 }, new Object[]{ 3 }, new Object[]{ 3 } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains(7));
    }

    @Test
    public void testLong() throws Exception {
        Object[][] result = executeAggregation(LONG, new Object[][]{ new Object[]{ 7L }, new Object[]{ 3L }, new Object[]{ 3L } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains(7L));
    }

    @Test
    public void testShort() throws Exception {
        Object[][] result = executeAggregation(SHORT, new Object[][]{ new Object[]{ ((short) (7)) }, new Object[]{ ((short) (3)) }, new Object[]{ ((short) (3)) } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains(((short) (7))));
    }

    @Test
    public void testString() throws Exception {
        Object[][] result = executeAggregation(STRING, new Object[][]{ new Object[]{ "Youri" }, new Object[]{ "Ruben" }, new Object[]{ "Ruben" } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains("Youri"));
    }

    @Test
    public void testBoolean() throws Exception {
        Object[][] result = executeAggregation(BOOLEAN, new Object[][]{ new Object[]{ true }, new Object[]{ false }, new Object[]{ false } });
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertTrue(((Set) (result[0][0])).contains(true));
    }

    @Test
    public void testNullValue() throws Exception {
        Object[][] result = executeAggregation(STRING, new Object[][]{ new Object[]{ "Youri" }, new Object[]{ "Ruben" }, new Object[]{ null } });
        // null values currently ignored
        assertThat(result[0][0], IsInstanceOf.instanceOf(Set.class));
        assertEquals(2, ((Set) (result[0][0])).size());
        assertFalse(((Set) (result[0][0])).contains(null));
    }
}

