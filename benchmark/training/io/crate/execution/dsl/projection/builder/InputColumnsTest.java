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
package io.crate.execution.dsl.projection.builder;


import InputColumns.SourceSymbols;
import Literal.BOOLEAN_FALSE;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Symbol;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.testing.T3;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;


public class InputColumnsTest extends CrateUnitTest {
    @Test
    public void testNonDeterministicFunctionsReplacement() throws Exception {
        SqlExpressions sqlExpressions = new SqlExpressions(T3.SOURCES, T3.TR_1);
        Function fn1 = ((Function) (sqlExpressions.asSymbol("random()")));
        Function fn2 = ((Function) (sqlExpressions.asSymbol("random()")));
        List<Symbol> inputSymbols = Arrays.<Symbol>asList(BOOLEAN_FALSE, sqlExpressions.asSymbol("upper(a)"), fn1, fn2);
        Function newSameFn = ((Function) (sqlExpressions.asSymbol("random()")));
        Function newDifferentFn = ((Function) (sqlExpressions.asSymbol("random()")));
        InputColumns.SourceSymbols sourceSymbols = new InputColumns.SourceSymbols(inputSymbols);
        Symbol replaced1 = InputColumns.create(fn1, sourceSymbols);
        assertThat(replaced1, Matchers.is(Matchers.instanceOf(InputColumn.class)));
        assertThat(index(), Matchers.is(2));
        Symbol replaced2 = InputColumns.create(fn2, sourceSymbols);
        assertThat(replaced2, Matchers.is(Matchers.instanceOf(InputColumn.class)));
        assertThat(index(), Matchers.is(3));
        Symbol replaced3 = InputColumns.create(newSameFn, sourceSymbols);
        assertThat(replaced3, Matchers.is(Matchers.equalTo(newSameFn)));// not replaced

        Symbol replaced4 = InputColumns.create(newDifferentFn, sourceSymbols);
        assertThat(replaced4, Matchers.is(Matchers.equalTo(newDifferentFn)));// not replaced

    }
}

