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


import CountAggregation.LongState;
import CountAggregation.LongStateType.INSTANCE;
import DataTypes.DOUBLE;
import DataTypes.FLOAT;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import DataTypes.UNDEFINED;
import io.crate.Streamer;
import io.crate.operation.aggregation.AggregationTest;
import io.crate.testing.SymbolMatchers;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.junit.Test;


public class CountAggregationTest extends AggregationTest {
    @Test
    public void testReturnType() throws Exception {
        // Return type is fixed to Long
        assertEquals(LONG, getCount().info().returnType());
    }

    @Test
    public void testDouble() throws Exception {
        Object[][] result = executeAggregation(DOUBLE, new Object[][]{ new Object[]{ 0.7 }, new Object[]{ 0.3 } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testFloat() throws Exception {
        Object[][] result = executeAggregation(FLOAT, new Object[][]{ new Object[]{ 0.7F }, new Object[]{ 0.3F } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testInteger() throws Exception {
        Object[][] result = executeAggregation(INTEGER, new Object[][]{ new Object[]{ 7 }, new Object[]{ 3 } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testLong() throws Exception {
        Object[][] result = executeAggregation(LONG, new Object[][]{ new Object[]{ 7L }, new Object[]{ 3L } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testShort() throws Exception {
        Object[][] result = executeAggregation(SHORT, new Object[][]{ new Object[]{ ((short) (7)) }, new Object[]{ ((short) (3)) } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testString() throws Exception {
        Object[][] result = executeAggregation(STRING, new Object[][]{ new Object[]{ "Youri" }, new Object[]{ "Ruben" } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testNormalizeWithNullLiteral() {
        assertThat(normalize("count", null, STRING), SymbolMatchers.isLiteral(0L));
        assertThat(normalize("count", null, UNDEFINED), SymbolMatchers.isLiteral(0L));
    }

    @Test
    public void testNoInput() throws Exception {
        // aka. COUNT(*)
        Object[][] result = executeAggregation(null, new Object[][]{ new Object[]{  }, new Object[]{  } });
        assertEquals(2L, result[0][0]);
    }

    @Test
    public void testStreaming() throws Exception {
        CountAggregation.LongState l1 = new CountAggregation.LongState(12345L);
        BytesStreamOutput out = new BytesStreamOutput();
        Streamer streamer = INSTANCE.streamer();
        streamer.writeValueTo(out, l1);
        StreamInput in = out.bytes().streamInput();
        CountAggregation.LongState l2 = ((CountAggregation.LongState) (streamer.readValueFrom(in)));
        assertEquals(l1.value, l2.value);
    }
}

