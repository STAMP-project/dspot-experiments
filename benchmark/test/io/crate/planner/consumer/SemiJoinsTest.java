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
package io.crate.planner.consumer;


import JoinType.ANTI;
import JoinType.SEMI;
import SemiJoins.Candidate;
import io.crate.action.sql.SessionContext;
import io.crate.analyze.MultiSourceSelect;
import io.crate.analyze.QueriedTable;
import io.crate.analyze.relations.QueriedRelation;
import io.crate.expression.symbol.Function;
import io.crate.expression.symbol.SelectSymbol;
import io.crate.expression.symbol.Symbol;
import io.crate.planner.operators.LogicalPlan;
import io.crate.planner.operators.LogicalPlannerTest;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.TestingHelpers;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SemiJoinsTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor executor;

    private SemiJoins semiJoins = new SemiJoins(TestingHelpers.getFunctions());

    @Test
    public void testGatherRewriteCandidatesSingle() throws Exception {
        Symbol query = asSymbol("a in (select 'foo')");
        assertThat(SemiJoins.gatherRewriteCandidates(query).size(), Matchers.is(1));
    }

    @Test
    public void testGatherRewriteCandidatesTwo() throws Exception {
        Symbol query = asSymbol("a in (select 'foo') and a = 'foo' and x not in (select 1)");
        List<SemiJoins.Candidate> candidates = SemiJoins.gatherRewriteCandidates(query);
        assertThat(candidates.size(), Matchers.is(2));
        assertThat(candidates.get(0).joinType, Matchers.is(SEMI));
        assertThat(candidates.get(0).function.info().ident().name(), Matchers.is("any_="));
        assertThat(candidates.get(1).joinType, Matchers.is(ANTI));
        assertThat(candidates.get(1).function.info().ident().name(), Matchers.is("op_not"));
    }

    @Test
    public void testMakeJoinConditionWith() throws Exception {
        QueriedTable relation = executor.analyze("select * from t1 where a in (select 'foo')");
        Function query = ((Function) (relation.querySpec().where().query()));
        SelectSymbol subquery = SemiJoins.getSubqueryOrNull(query.arguments().get(1));
        Symbol joinCondition = SemiJoins.makeJoinCondition(new SemiJoins.SemiJoinCandidate(query, subquery), relation.tableRelation());
        assertThat(joinCondition, TestingHelpers.isSQL("(doc.t1.a = 'foo')"));
    }

    @Test
    public void testSemiJoinRewriteOfWhereClause() throws Exception {
        QueriedRelation rel = executor.analyze("select * from t1 where a in (select 'foo') and x = 10");
        MultiSourceSelect semiJoin = ((MultiSourceSelect) (semiJoins.tryRewrite(rel, new io.crate.metadata.CoordinatorTxnCtx(SessionContext.systemSessionContext()))));
        assertThat(semiJoin.querySpec().where(), TestingHelpers.isSQL("true"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT doc.t1.a, doc.t1.x, doc.t1.i WHERE (doc.t1.x = 10)"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT 'foo'"));
        assertThat(semiJoin.joinPairs().get(0).condition(), TestingHelpers.isSQL("(doc.t1.a = S0..empty_row.'foo')"));
        assertThat(semiJoin.joinPairs().get(0).joinType(), Matchers.is(SEMI));
    }

    @Test
    public void testAntiJoinRewriteOfWhereClause() throws Exception {
        QueriedRelation rel = executor.analyze("select * from t1 where a not in (select 'foo') and x = 10");
        MultiSourceSelect antiJoin = ((MultiSourceSelect) (semiJoins.tryRewrite(rel, new io.crate.metadata.CoordinatorTxnCtx(SessionContext.systemSessionContext()))));
        assertThat(antiJoin.querySpec().where(), TestingHelpers.isSQL("true"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT doc.t1.a, doc.t1.x, doc.t1.i WHERE (doc.t1.x = 10)"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT 'foo'"));
        assertThat(antiJoin.joinPairs().get(0).condition(), TestingHelpers.isSQL("(doc.t1.a = S0..empty_row.'foo')"));
        assertThat(antiJoin.joinPairs().get(0).joinType(), Matchers.is(ANTI));
    }

    @Test
    public void testQueryWithOrIsNotRewritten() throws Exception {
        QueriedRelation relation = executor.analyze("select * from t1 where a in (select 'foo') or a = '10'");
        QueriedRelation semiJoin = semiJoins.tryRewrite(relation, new io.crate.metadata.CoordinatorTxnCtx(SessionContext.systemSessionContext()));
        assertThat(semiJoin, Matchers.nullValue());
    }

    @Test
    public void testDisabledByDefault() throws Exception {
        LogicalPlan logicalPlan = executor.logicalPlan("select * from t1 where a in (select 'foo')");
        assertThat(logicalPlan, LogicalPlannerTest.isPlan(TestingHelpers.getFunctions(), ("RootBoundary[a, x, i]\n" + (((((((("MultiPhase[\n" + "    subQueries[\n") + "        RootBoundary[\'foo\']\n") + "        OrderBy[\'foo\' ASC NULLS LAST]\n") + "        Collect[.empty_row | [\'foo\'] | All]\n") + "    ]\n") + "    FetchOrEval[a, x, i]\n") + "    Collect[doc.t1 | [_fetchid] | (a = ANY(SelectSymbol{string_array}))]\n") + "]\n"))));
    }

    @Test
    public void testWriteWithMultipleInClauses() throws Exception {
        QueriedRelation relation = executor.analyze(("select * from t1 " + (("where " + "   x in (select * from unnest([1, 2])) ") + "   and x not in (select 1)")));
        QueriedRelation semiJoins = this.semiJoins.tryRewrite(relation, new io.crate.metadata.CoordinatorTxnCtx(SessionContext.systemSessionContext()));
        assertThat(semiJoins, Matchers.instanceOf(MultiSourceSelect.class));
        MultiSourceSelect mss = ((MultiSourceSelect) (semiJoins));
        assertThat(mss.sources().size(), Matchers.is(3));
        assertThat(mss.joinPairs().get(0).joinType(), Matchers.is(SEMI));
        assertThat(mss.joinPairs().get(1).joinType(), Matchers.is(ANTI));
    }
}

