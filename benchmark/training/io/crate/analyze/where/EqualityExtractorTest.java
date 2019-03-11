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
package io.crate.analyze.where;


import com.google.common.collect.ImmutableMap;
import io.crate.action.sql.SessionContext;
import io.crate.expression.eval.EvaluatingNormalizer;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.T3;
import io.crate.testing.TestingHelpers;
import java.util.List;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class EqualityExtractorTest extends CrateUnitTest {
    private CoordinatorTxnCtx coordinatorTxnCtx = new CoordinatorTxnCtx(SessionContext.systemSessionContext());

    private SqlExpressions expressions = new SqlExpressions(ImmutableMap.of(T3.T1, T3.TR_1), T3.TR_1);

    private EvaluatingNormalizer normalizer = EvaluatingNormalizer.functionOnlyNormalizer(TestingHelpers.getFunctions());

    private EqualityExtractor ee = new EqualityExtractor(normalizer);

    private ColumnIdent x = new ColumnIdent("x");

    private ColumnIdent i = new ColumnIdent("i");

    @Test
    public void testNoExtract2ColPKWithOr() throws Exception {
        Symbol query = query("x = 1 or i = 2");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertNull(matches);
    }

    @Test
    public void testNoExtractOnNotEqualsOnSinglePk() {
        Symbol query = query("x != 1");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertNull(matches);
    }

    @Test
    public void testExtract2ColPKWithAndAndNestedOr() throws Exception {
        Symbol query = query("x = 1 and (i = 2 or i = 3 or i = 4)");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(3));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(3)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(4))));
    }

    @Test
    public void testExtract2ColPKWithOrFullDistinctKeys() throws Exception {
        Symbol query = query("(x = 1 and i = 2) or (x = 3 and i =4)");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(2));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(3), SymbolMatchers.isLiteral(4))));
    }

    @Test
    public void testExtract2ColPKWithOrFullDuplicateKeys() throws Exception {
        Symbol query = query("(x = 1 and i = 2) or (x = 1 and i = 4)");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(2));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(4))));
    }

    @Test
    public void testExtractRoutingFromAnd() throws Exception {
        Symbol query = query("x = 1 and i = 2");
        List<List<Symbol>> matches = analyzeParentX(query);
        assertThat(matches.size(), Is.is(1));
        assertThat(matches, Matchers.contains(Matchers.contains(SymbolMatchers.isLiteral(1))));
    }

    @Test
    public void testExtractNoRoutingFromForeignOnly() throws Exception {
        Symbol query = query("i = 2");
        List<List<Symbol>> matches = analyzeParentX(query);
        assertNull(matches);
    }

    @Test
    public void testExtractRoutingFromOr() throws Exception {
        Symbol query = query("x = 1 or x = 2");
        List<List<Symbol>> matches = analyzeParentX(query);
        assertThat(matches.size(), Is.is(2));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(2))));
    }

    @Test
    public void testNoExtractSinglePKFromAnd() throws Exception {
        Symbol query = query("x = 1 and x = 2");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertNull(matches);
    }

    @Test
    public void testExtractRoutingFromNestedOr() throws Exception {
        Symbol query = query("x =1 or x =2 or x = 3 or x = 4");
        List<List<Symbol>> matches = analyzeParentX(query);
        assertThat(matches.size(), Is.is(4));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(3)), Matchers.contains(SymbolMatchers.isLiteral(4))));
    }

    @Test
    public void testExtractNoRoutingFromOrWithForeignColumn() throws Exception {
        Symbol query = query("x = 1 or i = 2");
        List<List<Symbol>> matches = analyzeParentX(query);
        assertNull(matches);
    }

    @Test
    public void testNoExtractSinglePKFromAndWithForeignColumn() throws Exception {
        Symbol query = query("x = 1 or (x = 2 and i = 2)");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertNull(matches);
    }

    @Test
    public void testExtract2ColPKFromNestedOrWithDuplicates() throws Exception {
        Symbol query = query("x = 1 and (i = 2 or i = 2 or i = 4)");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(2));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(4))));
    }

    @Test
    public void testNoExtract2ColPKFromAndEq1PartAnd2ForeignColumns() throws Exception {
        Symbol query = query("x = 1 and (i = 2 or a = 'a')");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertNull(matches);
    }

    /**
     * x=1 and (y=2 or ?)
     * and(x1, or(y2, ?)
     * <p>
     * x = 1 and (y=2 or x=3)
     * and(x1, or(y2, x3)
     * <p>
     * x=1 and (y=2 or y=3)
     * and(x1, or(or(y2, y3), y4))
     * <p>
     * branches: x1,
     * <p>
     * <p>
     * <p>
     * x=1 and (y=2 or F)
     * 1,2   1=1 and (2=2 or z=3) T
     */
    @Test
    public void testNoExtract2ColPKFromAndWithEq1PartAnd1ForeignColumnInOr() throws Exception {
        Symbol query = query("x = 1 and (i = 2 or a = 'a')");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertNull(matches);
    }

    @Test
    public void testExtract2ColPKFrom1PartAndOtherPart2EqOr() throws Exception {
        Symbol query = query("x = 1 and (i = 2 or i = 3)");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(2));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(3))));
    }

    @Test
    public void testNoExtract2ColPKFromOnly1Part() throws Exception {
        Symbol query = query("x = 1");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertNull(matches);
    }

    @Test
    public void testExtractSinglePKFromAnyEq() throws Exception {
        Symbol query = query("x = any([1, 2, 3])");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertThat(matches.size(), Is.is(3));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(3))));
    }

    @Test
    public void testExtract2ColPKFromAnyEq() throws Exception {
        Symbol query = query("i = 4 and x = any([1, 2, 3])");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(3));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(4)), Matchers.contains(SymbolMatchers.isLiteral(2), SymbolMatchers.isLiteral(4)), Matchers.contains(SymbolMatchers.isLiteral(3), SymbolMatchers.isLiteral(4))));
    }

    @Test
    public void testExtractSinglePKFromAnyEqInOr() throws Exception {
        Symbol query = query("x = any([1, 2, 3]) or x = any([4, 5, 3])");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertThat(matches.size(), Is.is(5));
        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(3)), Matchers.contains(SymbolMatchers.isLiteral(4)), Matchers.contains(SymbolMatchers.isLiteral(5))));
    }

    @Test
    public void testExtractSinglePKFromOrInAnd() throws Exception {
        Symbol query = query("(x = 1 or x = 2 or x = 3) and (x = 1 or x = 4 or x = 5)");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertThat(matches.size(), Is.is(1));
        assertThat(matches, Matchers.contains(Matchers.contains(SymbolMatchers.isLiteral(1))));
    }

    @Test
    public void testExtractSinglePK1FromAndAnyEq() throws Exception {
        Symbol query = query("x = any([1, 2, 3]) and x = any([4, 5, 3])");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertThat(matches, Is.is(Matchers.notNullValue()));
        assertThat(matches.size(), Is.is(1));// 3

        assertThat(matches, Matchers.contains(Matchers.contains(SymbolMatchers.isLiteral(3))));
    }

    @Test
    public void testExtract2ColPKFromAnyEqAnd() throws Exception {
        Symbol query = query("x = any([1, 2, 3]) and i = any([1, 2, 3])");
        List<List<Symbol>> matches = analyzeExactXI(query);
        assertThat(matches.size(), Is.is(9));// cartesian product: 3 * 3

        assertThat(matches, Matchers.containsInAnyOrder(Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(1), SymbolMatchers.isLiteral(3)), Matchers.contains(SymbolMatchers.isLiteral(2), SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(2), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(2), SymbolMatchers.isLiteral(3)), Matchers.contains(SymbolMatchers.isLiteral(3), SymbolMatchers.isLiteral(1)), Matchers.contains(SymbolMatchers.isLiteral(3), SymbolMatchers.isLiteral(2)), Matchers.contains(SymbolMatchers.isLiteral(3), SymbolMatchers.isLiteral(3))));
    }

    @Test
    public void testNoPKExtractionIfMatchIsPresent() throws Exception {
        Symbol query = query("x in (1, 2, 3) and match(a, 'Hello World')");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertThat(matches, Matchers.nullValue());
    }

    @Test
    public void testNoPKExtractionIfFunctionUsingPKIsPresent() throws Exception {
        Symbol query = query("x in (1, 2, 3) and substr(cast(x as string), 0) = 4");
        List<List<Symbol>> matches = analyzeExactX(query);
        assertThat(matches, Matchers.nullValue());
    }

    @Test
    public void testNoPKExtractionOnNotIn() {
        List<List<Symbol>> matches = analyzeExactX(query("x not in (1, 2, 3)"));
        assertThat(matches, Matchers.nullValue());
    }

    @Test
    public void testNoPKExtractionWhenColumnsOnBothSidesOfEqual() {
        List<List<Symbol>> matches = analyzeExactX(query("x = abs(x)"));
        assertThat(matches, Matchers.nullValue());
    }
}

