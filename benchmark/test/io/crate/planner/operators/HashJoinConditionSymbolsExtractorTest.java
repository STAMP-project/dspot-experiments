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
package io.crate.planner.operators;


import ArithmeticFunctions.Names.ADD;
import io.crate.analyze.relations.AnalyzedRelation;
import io.crate.expression.symbol.Symbol;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.T3;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class HashJoinConditionSymbolsExtractorTest extends CrateUnitTest {
    private static final SqlExpressions SQL_EXPRESSIONS = new SqlExpressions(T3.SOURCES);

    @Test
    public void testExtractFromTopEqCondition() {
        Symbol joinCondition = HashJoinConditionSymbolsExtractorTest.SQL_EXPRESSIONS.asSymbol("t1.x = t2.y");
        Map<AnalyzedRelation, List<Symbol>> symbolsPerRelation = HashJoinConditionSymbolsExtractor.extract(joinCondition);
        assertThat(symbolsPerRelation.get(T3.TR_1), Matchers.contains(SymbolMatchers.isField("x")));
        assertThat(symbolsPerRelation.get(T3.TR_2), Matchers.contains(SymbolMatchers.isField("y")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExtractFromNestedEqCondition() {
        Symbol joinCondition = HashJoinConditionSymbolsExtractorTest.SQL_EXPRESSIONS.asSymbol("t1.x > t2.y and t1.a = t2.b and not(t1.i = t2.i)");
        Map<AnalyzedRelation, List<Symbol>> symbolsPerRelation = HashJoinConditionSymbolsExtractor.extract(joinCondition);
        assertThat(symbolsPerRelation.get(T3.TR_1), Matchers.contains(SymbolMatchers.isField("a")));
        assertThat(symbolsPerRelation.get(T3.TR_2), Matchers.contains(SymbolMatchers.isField("b")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExtractSymbolsWithDuplicates() {
        Symbol joinCondition = HashJoinConditionSymbolsExtractorTest.SQL_EXPRESSIONS.asSymbol("t1.a = t2.b and t1.i + 1 = t2.i and t2.y = t1.i + 1");
        Map<AnalyzedRelation, List<Symbol>> symbolsPerRelation = HashJoinConditionSymbolsExtractor.extract(joinCondition);
        assertThat(symbolsPerRelation.get(T3.TR_1), Matchers.containsInAnyOrder(SymbolMatchers.isField("a"), SymbolMatchers.isFunction(ADD, SymbolMatchers.isField("i"), SymbolMatchers.isLiteral(1))));
        assertThat(symbolsPerRelation.get(T3.TR_2), Matchers.containsInAnyOrder(SymbolMatchers.isField("b"), SymbolMatchers.isField("i")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExtractRelationsOfFunctionsWithLiterals() {
        Symbol joinCondition = HashJoinConditionSymbolsExtractorTest.SQL_EXPRESSIONS.asSymbol("t1.a = t2.b and t1.i + 1 = t2.i and t2.y = 1 + t1.i");
        Map<AnalyzedRelation, List<Symbol>> symbolsPerRelation = HashJoinConditionSymbolsExtractor.extract(joinCondition);
        assertThat(symbolsPerRelation.get(T3.TR_1), Matchers.containsInAnyOrder(SymbolMatchers.isField("a"), SymbolMatchers.isFunction(ADD, SymbolMatchers.isField("i"), SymbolMatchers.isLiteral(1)), SymbolMatchers.isFunction(ADD, SymbolMatchers.isLiteral(1), SymbolMatchers.isField("i"))));
        assertThat(symbolsPerRelation.get(T3.TR_2), Matchers.containsInAnyOrder(SymbolMatchers.isField("b"), SymbolMatchers.isField("i"), SymbolMatchers.isField("y")));
    }
}

