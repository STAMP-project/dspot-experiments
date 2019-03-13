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


import WhereClause.MATCH_ALL;
import io.crate.analyze.relations.AbstractTableRelation;
import io.crate.analyze.relations.QueriedRelation;
import io.crate.exceptions.AmbiguousColumnAliasException;
import io.crate.sql.tree.QualifiedName;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.SymbolMatchers;
import io.crate.testing.T3;
import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SubSelectAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor executor;

    @Test
    public void testSimpleSubSelect() throws Exception {
        QueriedSelectRelation relation = analyze("select aliased_sub.x / aliased_sub.i from (select x, i from t1) as aliased_sub");
        assertThat(relation.fields(), Matchers.contains(SymbolMatchers.isField("(x / i)")));
        assertThat(relation.subRelation().fields(), Matchers.contains(SymbolMatchers.isField("x"), SymbolMatchers.isField("i")));
    }

    @Test
    public void testSimpleSubSelectWithMixedCases() throws Exception {
        QueriedRelation relation = analyze("select aliased_sub.A from (select a from t1) as aliased_sub");
        assertThat(relation.fields().size(), Matchers.is(1));
        assertThat(relation.fields().get(0), SymbolMatchers.isField("a"));
    }

    @Test
    public void testSubSelectWithoutAlias() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("subquery in FROM must have an alias");
        analyze("select id from (select a as id from t1)");
    }

    @Test
    public void testSubSelectWithNestedAlias() throws Exception {
        QueriedSelectRelation relation = analyze(("select tt.aa, (tt.xi + 1)" + " from (select (x + i) as xi, concat(a, a) as aa, i from t1) as tt"));
        assertThat(relation.fields().size(), Matchers.is(2));
        assertThat(relation.fields().get(0), SymbolMatchers.isField("aa"));
        assertThat(relation.fields().get(1), SymbolMatchers.isField("(xi + 1)"));
        assertThat(((QueriedTable<AbstractTableRelation>) (relation.subRelation())).tableRelation().tableInfo(), Matchers.is(T3.T1_INFO));
    }

    @Test
    public void testNestedSubSelect() throws Exception {
        QueriedRelation relation = analyze("select aliased_sub.a from (select nested_sub.a from (select a from t1) as nested_sub) as aliased_sub");
        assertThat(relation.fields().size(), Matchers.is(1));
        assertThat(relation.fields().get(0), SymbolMatchers.isField("a"));
    }

    @Test
    public void testSubSelectWithJoins() throws Exception {
        QueriedSelectRelation relation = analyze("select aliased_sub.a, aliased_sub.b from (select t1.a, t2.b from t1, t2) as aliased_sub");
        MultiSourceSelect mss = ((MultiSourceSelect) (relation.subRelation()));
        assertThat(mss.sources().size(), Matchers.is(2));
        assertThat(mss.fields().size(), Matchers.is(2));
        assertThat(mss.fields().get(0), SymbolMatchers.isField("a"));
        assertThat(mss.fields().get(1), SymbolMatchers.isField("b"));
    }

    @Test
    public void testSubSelectWithJoinsAmbiguousColumn() throws Exception {
        expectedException.expect(AmbiguousColumnAliasException.class);
        expectedException.expectMessage("Column alias \"i\" is ambiguous");
        analyze("select aliased_sub.i, aliased_sub.b from (select t1.i, t2.i, t2.b from t1, t2) as aliased_sub");
    }

    @Test
    public void testJoinOnSubSelects() throws Exception {
        MultiSourceSelect relation = analyze(("select * from " + ((((" (select a, i from t1 order by a limit 5) t1 " + "left join") + " (select b, i from t2 where b > 10) t2 ") + "on t1.i = t2.i where t1.a > 50 and t2.b > 100 ") + "limit 10")));
        assertThat(relation.querySpec(), TestingHelpers.isSQL("SELECT t1.a, t1.i, t2.b, t2.i LIMIT 10"));
        assertThat(relation.joinPairs().get(0).condition(), TestingHelpers.isSQL("(t1.i = t2.i)"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT t1.i, t1.a WHERE (t1.a > '50')"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT doc.t1.a, doc.t1.i ORDER BY doc.t1.a LIMIT 5"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT t2.b, t2.i WHERE (t2.b > '100')"));
    }

    @Test
    public void testJoinOnSubSelectsWithOrderByAndLimitNotPushedDown() throws Exception {
        MultiSourceSelect relation = analyze(("select * from " + ((((" (select a, i from t1 order by a limit 5) t1 " + "left join") + " (select b, i from t2 where b > 10) t2 ") + "on t1.i = t2.i where t1.a > 50 and t2.b > 100 ") + "order by 2 limit 10")));
        assertThat(relation.querySpec(), TestingHelpers.isSQL("SELECT t1.a, t1.i, t2.b, t2.i ORDER BY t1.i LIMIT 10"));
        assertThat(relation.joinPairs().get(0).condition(), TestingHelpers.isSQL("(t1.i = t2.i)"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT t1.i, t1.a WHERE (t1.a > '50')"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT doc.t1.a, doc.t1.i ORDER BY doc.t1.a LIMIT 5"));
        assertThat(querySpec(), TestingHelpers.isSQL("SELECT t2.b, t2.i WHERE (t2.b > '100')"));
    }

    @Test
    public void testJoinOnSubSelectsWithGlobalAggregationsAndLimitNotPushedDown() throws Exception {
        MultiSourceSelect relation = analyze(("select count(*) from " + ((((((" (select a, i from (" + "     select * from t1 order by a desc limit 5) a") + "  order by a limit 10) t1 ") + "join") + " (select b, i from t2 where b > 10) t2 ") + "on t1.i = t2.i ") + "order by 1 limit 10")));
        assertThat(relation.outputs(), Matchers.contains(SymbolMatchers.isFunction("count")));
        assertThat(relation.orderBy().orderBySymbols(), Matchers.contains(SymbolMatchers.isFunction("count")));
        assertThat(relation.limit(), SymbolMatchers.isLiteral(10L));
        assertThat(relation.joinPairs().get(0).condition(), SymbolMatchers.isFunction("op_=", SymbolMatchers.isField("i"), SymbolMatchers.isField("i")));
        QueriedRelation t1Sub = ((QueriedRelation) (relation.sources().get(new QualifiedName("t1"))));
        assertThat(t1Sub.outputs(), Matchers.contains(SymbolMatchers.isField("i")));
        assertThat(t1Sub.orderBy(), Matchers.nullValue());
        assertThat(t1Sub.limit(), Matchers.nullValue());
        QueriedRelation t1 = subRelation();
        assertThat(t1.orderBy().orderBySymbols(), Matchers.contains(SymbolMatchers.isField("a")));
        assertThat(t1.limit(), SymbolMatchers.isLiteral(10L));
        QueriedSelectRelation t2sub = ((QueriedSelectRelation) (relation.sources().get(new QualifiedName("t2"))));
        QueriedRelation t2 = t2sub.subRelation();
        assertThat(t2.where().query(), SymbolMatchers.isFunction("op_>", SymbolMatchers.isReference("b"), SymbolMatchers.isLiteral("10")));
    }

    @Test
    public void testJoinOnSubSelectsWithOrder() throws Exception {
        MultiSourceSelect relation = analyze(("select * from " + (((" (select a, i from t1 order by a) t1, " + " (select b, i from t2 where b > 10) t2 ") + "where t1.a > 50 and t2.b > 100 ") + "order by 2 limit 10")));
        assertThat(relation.outputs(), Matchers.contains(SymbolMatchers.isField("a"), SymbolMatchers.isField("i"), SymbolMatchers.isField("b"), SymbolMatchers.isField("i")));
        assertThat(relation.where(), Matchers.is(MATCH_ALL));
        assertThat(relation.orderBy().orderBySymbols(), Matchers.contains(SymbolMatchers.isField("i")));
        assertThat(relation.limit(), SymbolMatchers.isLiteral(10L));
        QueriedRelation t1 = ((QueriedSelectRelation) (relation.sources().get(new QualifiedName("t1"))));
        assertThat(t1.where().query(), SymbolMatchers.isFunction("op_>", SymbolMatchers.isField("a"), SymbolMatchers.isLiteral("50")));
        assertThat(t1.orderBy(), Matchers.nullValue());
        QueriedSelectRelation t2 = ((QueriedSelectRelation) (relation.sources().get(new QualifiedName("t2"))));
        assertThat(t2.where().query(), SymbolMatchers.isFunction("op_>", SymbolMatchers.isField("b"), SymbolMatchers.isLiteral("100")));
        assertThat(t2.subRelation().where().query(), SymbolMatchers.isFunction("op_>", SymbolMatchers.isReference("b"), SymbolMatchers.isLiteral("10")));
        assertThat(t2.orderBy(), Matchers.nullValue());
    }

    @Test
    public void testSubSelectWithOuterJoinAndAggregations() throws Exception {
        MultiSourceSelect relation = analyze(("select * from " + (((" (select max(a) ma, i from t1 group by i) t1 " + "left join") + " (select max(b) mb, i from t2 group by i having i > 10) t2 ") + "on t1.i = t2.i where t1.ma > 50 and t2.mb > 100")));
        assertThat(relation.outputs(), TestingHelpers.isSQL("t1.ma, t1.i, t2.mb, t2.i"));
        assertThat(relation.joinPairs().get(0).condition(), TestingHelpers.isSQL("(t1.i = t2.i)"));
        QueriedSelectRelation t1Sel = ((QueriedSelectRelation) (relation.sources().get(new QualifiedName("t1"))));
        assertThat(t1Sel.outputs(), TestingHelpers.isSQL("t1.i, t1.ma"));
        assertThat(t1Sel.groupBy(), TestingHelpers.isSQL(""));
        assertThat(t1Sel.having(), TestingHelpers.isSQL("(t1.ma > '50')"));
        assertThat(t1Sel.subRelation().groupBy(), TestingHelpers.isSQL("doc.t1.i"));
        assertThat(t1Sel.subRelation().having(), Matchers.nullValue());
        QueriedSelectRelation t2Sel = ((QueriedSelectRelation) (relation.sources().get(new QualifiedName("t2"))));
        assertThat(t2Sel.outputs(), TestingHelpers.isSQL("t2.mb, t2.i"));
        assertThat(t2Sel.groupBy(), TestingHelpers.isSQL(""));
        assertThat(t2Sel.having(), Matchers.nullValue());
        QueriedRelation t2 = t2Sel.subRelation();
        assertThat(t2.groupBy(), TestingHelpers.isSQL("doc.t2.i"));
        assertThat(t2.having(), TestingHelpers.isSQL("(doc.t2.i > 10)"));
    }

    @Test
    public void testPreserveAliasOnSubSelectInSelectList() throws Exception {
        QueriedSelectRelation relation = analyze(("SELECT " + (("   (select min(t1.x) from t1) as min_col," + "   (select 10) + (select 20) as add_subquery ") + "FROM (select * from t1) tt1")));
        assertThat(relation.fields(), Matchers.contains(SymbolMatchers.isField("min_col"), SymbolMatchers.isField("add_subquery")));
    }

    @Test
    public void testPreserveAliasesOnSubSelect() throws Exception {
        QueriedSelectRelation relation = analyze(("SELECT tt1.x as a1, min(tt1.x) as a2 " + ("FROM (select * from t1) as tt1 " + "GROUP BY a1")));
        assertThat(relation.fields().size(), Matchers.is(2));
        assertThat(relation.fields().get(0), SymbolMatchers.isField("a1"));
        assertThat(relation.fields().get(1), SymbolMatchers.isField("a2"));
        assertThat(((QueriedTable<AbstractTableRelation>) (relation.subRelation())).tableRelation().tableInfo(), Matchers.is(T3.T1_INFO));
    }

    @Test
    public void testPreserveMultipleAliasesOnSubSelect() throws Exception {
        QueriedSelectRelation relation = analyze(("SELECT tt1.i, i as ii, tt1.ii + 2, ii as iii, abs(x), abs(tt1.x) as absx " + "FROM (select i, i+1 as ii, x from t1) as tt1"));
        assertThat(relation.fields().size(), Matchers.is(6));
        assertThat(relation.fields().get(0), SymbolMatchers.isField("i"));
        assertThat(relation.fields().get(1), SymbolMatchers.isField("ii"));
        assertThat(relation.fields().get(2), SymbolMatchers.isField("(ii + 2)"));
        assertThat(relation.fields().get(3), SymbolMatchers.isField("iii"));
        assertThat(relation.fields().get(4), SymbolMatchers.isField("abs(x)"));
        assertThat(relation.fields().get(5), SymbolMatchers.isField("absx"));
        assertThat(((QueriedTable<AbstractTableRelation>) (relation.subRelation())).tableRelation().tableInfo(), Matchers.is(T3.T1_INFO));
    }
}

