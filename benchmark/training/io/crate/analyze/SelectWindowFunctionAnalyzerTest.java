/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.analyze;


import FrameBound.Type.FOLLOWING;
import FrameBound.Type.PRECEDING;
import WindowDefinition.DEFAULT_WINDOW_FRAME;
import WindowFrame.Type.RANGE;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.expression.symbol.Symbol;
import io.crate.expression.symbol.WindowFunction;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class SelectWindowFunctionAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    @Test
    public void testEmptyOverClause() {
        QueriedTable analysis = e.analyze("select avg(x) OVER () from t");
        List<Symbol> outputSymbols = analysis.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(WindowFunction.class));
        WindowFunction windowFunction = ((WindowFunction) (outputSymbols.get(0)));
        assertThat(windowFunction.arguments().size(), Is.is(1));
        WindowDefinition windowDefinition = windowFunction.windowDefinition();
        assertThat(windowDefinition.partitions().isEmpty(), Is.is(true));
        assertThat(windowDefinition.orderBy(), Is.is(Matchers.nullValue()));
        assertThat(windowDefinition.windowFrameDefinition(), Is.is(DEFAULT_WINDOW_FRAME));
    }

    @Test
    public void testOverWithPartitionByClause() {
        QueriedTable analysis = e.analyze("select avg(x) OVER (PARTITION BY x) from t");
        List<Symbol> outputSymbols = analysis.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(WindowFunction.class));
        WindowFunction windowFunction = ((WindowFunction) (outputSymbols.get(0)));
        assertThat(windowFunction.arguments().size(), Is.is(1));
        WindowDefinition windowDefinition = windowFunction.windowDefinition();
        assertThat(windowDefinition.partitions().size(), Is.is(1));
    }

    @Test
    public void testInvalidPartitionByField() {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column zzz unknown");
        e.analyze("select avg(x) OVER (PARTITION BY zzz) from t");
    }

    @Test
    public void testInvalidOrderByField() {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column zzz unknown");
        e.analyze("select avg(x) OVER (ORDER BY zzz) from t");
    }

    @Test
    public void testOnlyAggregatesAndWindowFunctionsAreAllowedWithOver() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("OVER clause was specified, but abs is neither a window nor an aggregate function.");
        e.analyze("select abs(x) OVER() from t");
    }

    @Test
    public void testOverWithOrderByClause() {
        QueriedTable analysis = e.analyze("select avg(x) OVER (ORDER BY x) from t");
        List<Symbol> outputSymbols = analysis.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(WindowFunction.class));
        WindowFunction windowFunction = ((WindowFunction) (outputSymbols.get(0)));
        assertThat(windowFunction.arguments().size(), Is.is(1));
        WindowDefinition windowDefinition = windowFunction.windowDefinition();
        assertThat(windowDefinition.orderBy().orderBySymbols().size(), Is.is(1));
    }

    @Test
    public void testOverWithPartitionAndOrderByClauses() {
        QueriedTable analysis = e.analyze("select avg(x) OVER (PARTITION BY x ORDER BY x) from t");
        List<Symbol> outputSymbols = analysis.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(WindowFunction.class));
        WindowFunction windowFunction = ((WindowFunction) (outputSymbols.get(0)));
        assertThat(windowFunction.arguments().size(), Is.is(1));
        WindowDefinition windowDefinition = windowFunction.windowDefinition();
        assertThat(windowDefinition.partitions().size(), Is.is(1));
        assertThat(windowDefinition.orderBy().orderBySymbols().size(), Is.is(1));
    }

    @Test
    public void testOverWithFrameDefinition() {
        QueriedTable analysis = e.analyze(("select avg(x) OVER (PARTITION BY x ORDER BY x " + "RANGE BETWEEN 5 PRECEDING AND 6 FOLLOWING) from t"));
        List<Symbol> outputSymbols = analysis.querySpec().outputs();
        assertThat(outputSymbols.size(), Is.is(1));
        assertThat(outputSymbols.get(0), Matchers.instanceOf(WindowFunction.class));
        WindowFunction windowFunction = ((WindowFunction) (outputSymbols.get(0)));
        assertThat(windowFunction.arguments().size(), Is.is(1));
        WindowFrameDefinition frameDefinition = windowFunction.windowDefinition().windowFrameDefinition();
        assertThat(frameDefinition.type(), Is.is(RANGE));
        validateRangeFrameDefinition(frameDefinition.start(), PRECEDING, 5L);
        validateRangeFrameDefinition(frameDefinition.end(), FOLLOWING, 6L);
    }
}

