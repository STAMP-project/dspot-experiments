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


import BigArrays.NON_RECYCLING_INSTANCE;
import DataTypes.INTEGER;
import DataTypes.LONG;
import DataTypes.STRING;
import Version.CURRENT;
import io.crate.breaker.RamAccountingContext;
import io.crate.execution.engine.aggregation.AggregationFunction;
import io.crate.expression.symbol.Literal;
import io.crate.operation.aggregation.AggregationTest;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import java.util.Arrays;
import org.elasticsearch.common.breaker.NoopCircuitBreaker;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PercentileAggregationTest extends AggregationTest {
    private static final String NAME = "percentile";

    private PercentileAggregation singleArgPercentile;

    private PercentileAggregation arraysPercentile;

    @Test
    public void testReturnTypes() throws Exception {
        assertEquals(DataTypes.DOUBLE, singleArgPercentile.info().returnType());
        assertEquals(new ArrayType(DataTypes.DOUBLE), arraysPercentile.info().returnType());
    }

    @Test
    public void testAllTypesReturnSameResult() throws Exception {
        for (DataType valueType : DataTypes.NUMERIC_PRIMITIVE_TYPES) {
            Double[] fractions = new Double[]{ 0.5, 0.8 };
            Object[][] rowsWithSingleFraction = new Object[10][];
            Object[][] rowsWithFractionsArray = new Object[10][];
            for (int i = 0; i < (rowsWithSingleFraction.length); i++) {
                rowsWithSingleFraction[i] = new Object[]{ valueType.value(i), fractions[0] };
                rowsWithFractionsArray[i] = new Object[]{ valueType.value(i), fractions };
            }
            Object[][] result = execSingleFractionPercentile(valueType, rowsWithSingleFraction);
            assertEquals(4.5, result[0][0]);
            result = execArrayFractionPercentile(valueType, rowsWithFractionsArray);
            assertTrue(result[0][0].getClass().isArray());
            assertEquals(2, ((Object[]) (result[0][0])).length);
            assertEquals(4.5, ((Object[]) (result[0][0]))[0]);
            assertEquals(7.5, ((Object[]) (result[0][0]))[1]);
        }
    }

    @Test
    public void testNullPercentile() throws Exception {
        Object[][] result = execSingleFractionPercentile(INTEGER, new Object[][]{ new Object[]{ 1, null }, new Object[]{ 10, null } });
        assertTrue(((result[0][0]) == null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPercentile() throws Exception {
        Object[][] result = execSingleFractionPercentile(INTEGER, new Object[][]{ new Object[]{ 1, new Object[]{  } }, new Object[]{ 10, new Object[]{  } } });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMultiplePercentiles() throws Exception {
        Double[] fractions = new Double[]{ 0.25, null };
        Object[][] result = execSingleFractionPercentile(INTEGER, new Object[][]{ new Object[]{ 1, fractions }, new Object[]{ 10, fractions } });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativePercentile() throws Exception {
        Object[][] result = execSingleFractionPercentile(INTEGER, new Object[][]{ new Object[]{ 1, -1.2 }, new Object[]{ 10, -1.2 } });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooLargePercentile() throws Exception {
        Object[][] result = execSingleFractionPercentile(INTEGER, new Object[][]{ new Object[]{ 1, 1.5 }, new Object[]{ 10, 1.5 } });
    }

    @Test(expected = NullPointerException.class)
    public void testUnsupportedType() throws Exception {
        Object[][] result = execSingleFractionPercentile(STRING, new Object[][]{ new Object[]{ "Akira", 0.5 }, new Object[]{ "Tetsuo", 0.5 } });
    }

    @Test
    public void testNullInputValuesReturnNull() throws Exception {
        Object[][] result = execSingleFractionPercentile(LONG, new Object[][]{ new Object[]{ null, 0.5 }, new Object[]{ null, 0.5 } });
        assertEquals(result[0][0], null);
    }

    @Test
    public void testEmptyPercentileFuncWithEmptyRows() throws Exception {
        Object[][] result = execSingleFractionPercentile(INTEGER, new Object[][]{  });
        assertThat(result[0][0], Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testReduceStage() throws Exception {
        PercentileAggregation pa = singleArgPercentile;
        // state 1 -> state 2
        TDigestState state1 = TDigestState.createEmptyState();
        TDigestState state2 = new TDigestState(100, new double[]{ 0.5 });
        state2.add(20.0);
        TDigestState reducedState = pa.reduce(null, state1, state2);
        assertThat(reducedState.fractions()[0], Matchers.is(0.5));
        assertThat(reducedState.centroidCount(), Matchers.is(1));
        // state 2 -> state 1
        state1 = new TDigestState(100, new double[]{ 0.5 });
        state1.add(22.0);
        state1.add(20.0);
        state2 = new TDigestState(100, new double[]{ 0.5 });
        state2.add(21.0);
        reducedState = pa.reduce(null, state1, state2);
        assertThat(reducedState.fractions()[0], Matchers.is(0.5));
        assertThat(reducedState.centroidCount(), Matchers.is(3));
    }

    @Test
    public void testSingleItemFractionsArgumentResultsInArrayResult() {
        ArrayType doubleArray = new ArrayType(DataTypes.DOUBLE);
        AggregationFunction impl = ((AggregationFunction<?, ?>) (functions.getQualified(new io.crate.metadata.FunctionIdent(PercentileAggregationTest.NAME, Arrays.asList(LONG, doubleArray)))));
        RamAccountingContext memoryCtx = new RamAccountingContext("dummy", new NoopCircuitBreaker("dummy"));
        Object state = impl.newState(memoryCtx, CURRENT, NON_RECYCLING_INSTANCE);
        Literal<Object[]> fractions = Literal.of(new Object[]{ 0.95 }, doubleArray);
        impl.iterate(memoryCtx, state, Literal.of(10L), fractions);
        impl.iterate(memoryCtx, state, Literal.of(20L), fractions);
        Object result = impl.terminatePartial(memoryCtx, state);
        assertThat("result must be an array", result.getClass().isArray(), Matchers.is(true));
    }
}

