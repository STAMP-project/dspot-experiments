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
package io.crate.lucene;


import BooleanClause.Occur.MUST;
import BooleanClause.Occur.MUST_NOT;
import WhereClause.NO_MATCH;
import com.google.common.collect.ImmutableMap;
import io.crate.analyze.relations.AnalyzedRelation;
import io.crate.analyze.relations.TableRelation;
import io.crate.auth.user.User;
import io.crate.lucene.match.CrateRegexQuery;
import io.crate.metadata.Schemas;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.metadata.table.TestingTableInfo;
import io.crate.sql.tree.QualifiedName;
import io.crate.testing.SqlExpressions;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import java.util.Arrays;
import java.util.Map;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.PointInSetQuery;
import org.apache.lucene.search.PointRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.spatial.prefix.IntersectsPrefixTreeQuery;
import org.hamcrest.Matchers;
import org.junit.Test;


public class CommonQueryBuilderTest extends LuceneQueryBuilderTest {
    @Test
    public void testNoMatchWhereClause() throws Exception {
        Query query = convert(NO_MATCH.queryOrFallback());
        assertThat(query, Matchers.instanceOf(MatchNoDocsQuery.class));
    }

    @Test
    public void testWhereRefEqNullWithDifferentTypes() throws Exception {
        for (DataType type : DataTypes.PRIMITIVE_TYPES) {
            DocTableInfo tableInfo = TestingTableInfo.builder(new io.crate.metadata.RelationName(Schemas.DOC_SCHEMA_NAME, "test_primitive"), null).add("x", type).build();
            TableRelation tableRelation = new TableRelation(tableInfo);
            Map<QualifiedName, AnalyzedRelation> tableSources = ImmutableMap.of(new QualifiedName(tableInfo.ident().name()), tableRelation);
            SqlExpressions sqlExpressions = new SqlExpressions(tableSources, tableRelation, new Object[]{ null }, User.CRATE_USER);
            Query query = convert(sqlExpressions.normalize(sqlExpressions.asSymbol("x = ?")));
            // must always become a MatchNoDocsQuery
            // string: term query with null would cause NPE
            // int/numeric: rangeQuery from null to null would match all
            // bool:  term would match false too because of the condition in the eq query builder
            assertThat(query, Matchers.instanceOf(MatchNoDocsQuery.class));
        }
    }

    @Test
    public void testWhereRefEqRef() throws Exception {
        Query query = convert("name = name");
        assertThat(query, Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testLteQuery() throws Exception {
        Query query = convert("x <= 10");
        assertThat(query.toString(), Matchers.is("x:[-2147483648 TO 10]"));
    }

    @Test
    public void testNotEqOnNotNullableColumnQuery() throws Exception {
        Query query = convert("x != 10");
        assertThat(query, Matchers.instanceOf(BooleanQuery.class));
        assertThat(query.toString(), Matchers.is("+(+*:* -x:[10 TO 10])"));
        query = convert("not x = 10");
        assertThat(query, Matchers.instanceOf(BooleanQuery.class));
        assertThat(query.toString(), Matchers.is("+(+*:* -x:[10 TO 10])"));
    }

    @Test
    public void testEqOnTwoArraysBecomesGenericFunctionQuery() throws Exception {
        Query query = convert("y_array = [10, 20, 30]");
        assertThat(query, Matchers.instanceOf(BooleanQuery.class));
        BooleanQuery booleanQuery = ((BooleanQuery) (query));
        assertThat(booleanQuery.clauses().get(0).getQuery(), Matchers.instanceOf(PointInSetQuery.class));
        assertThat(booleanQuery.clauses().get(1).getQuery(), Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testEqOnTwoArraysBecomesGenericFunctionQueryAllValuesNull() throws Exception {
        Query query = convert("y_array = [null, null, null]");
        assertThat(query, Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testEqOnArrayWithTooManyClauses() throws Exception {
        Object[] values = new Object[2000];// should trigger the TooManyClauses exception

        Arrays.fill(values, 10L);
        Query query = convert("y_array = ?", new Object[]{ values });
        assertThat(query, Matchers.instanceOf(BooleanQuery.class));
        BooleanQuery booleanQuery = ((BooleanQuery) (query));
        assertThat(booleanQuery.clauses().get(0).getQuery(), Matchers.instanceOf(PointInSetQuery.class));
        assertThat(booleanQuery.clauses().get(1).getQuery(), Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testGteQuery() throws Exception {
        Query query = convert("x >= 10");
        assertThat(query.toString(), Matchers.is("x:[10 TO 2147483647]"));
    }

    @Test
    public void testWhereRefInSetLiteralIsConvertedToTermsQuery() throws Exception {
        Query query = convert("x in (1, 3)");
        assertThat(query, Matchers.instanceOf(PointInSetQuery.class));
    }

    @Test
    public void testWhereStringRefInSetLiteralIsConvertedToTermsQuery() throws Exception {
        Query query = convert("name in ('foo', 'bar')");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
    }

    /**
     * Make sure we still sport the fast Lucene regular
     * expression engine when not using PCRE features.
     */
    @Test
    public void testRegexQueryFast() throws Exception {
        Query query = convert("name ~ '[a-z]'");
        assertThat(query, Matchers.instanceOf(ConstantScoreQuery.class));
        ConstantScoreQuery scoreQuery = ((ConstantScoreQuery) (query));
        assertThat(scoreQuery.getQuery(), Matchers.instanceOf(RegexpQuery.class));
    }

    /**
     * When using PCRE features, switch to different
     * regex implementation on top of java.util.regex.
     */
    @Test
    public void testRegexQueryPcre() throws Exception {
        Query query = convert("name ~ \'\\D\'");
        assertThat(query, Matchers.instanceOf(CrateRegexQuery.class));
    }

    @Test
    public void testIdQuery() throws Exception {
        Query query = convert("_id = 'i1'");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
        query = convert("_id = 1");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
    }

    @Test
    public void testAnyEqArrayLiteral() throws Exception {
        Query query = convert("d = any([-1.5, 0.0, 1.5])");
        assertThat(query, Matchers.instanceOf(PointInSetQuery.class));
        query = convert("_id in ('test','test2')");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
        query = convert("_id in (1, 2)");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
        query = convert("_id = any (['test','test2'])");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
        query = convert("_id = any ([1, 2])");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
    }

    @Test
    public void testAnyEqArrayReference() throws Exception {
        Query query = convert("1.5 = any(d_array)");
        assertThat(query, Matchers.instanceOf(PointRangeQuery.class));
        assertThat(query.toString(), Matchers.startsWith("d_array"));
    }

    @Test
    public void testAnyGreaterAndSmaller() throws Exception {
        Query ltQuery = convert("1.5 < any(d_array)");
        assertThat(ltQuery.toString(), Matchers.is("d_array:[1.5000000000000002 TO Infinity]"));
        // d < ANY ([1.2, 3.5])
        Query ltQuery2 = convert("d < any ([1.2, 3.5])");
        assertThat(ltQuery2.toString(), Matchers.is("(d:[-Infinity TO 1.1999999999999997] d:[-Infinity TO 3.4999999999999996])~1"));
        // 1.5d <= ANY (d_array)
        Query lteQuery = convert("1.5 <= any(d_array)");
        assertThat(lteQuery.toString(), Matchers.is("d_array:[1.5 TO Infinity]"));
        // d <= ANY ([1.2, 3.5])
        Query lteQuery2 = convert("d <= any([1.2, 3.5])");
        assertThat(lteQuery2.toString(), Matchers.is("(d:[-Infinity TO 1.2] d:[-Infinity TO 3.5])~1"));
        // 1.5d > ANY (d_array)
        Query gtQuery = convert("1.5 > any(d_array)");
        assertThat(gtQuery.toString(), Matchers.is("d_array:[-Infinity TO 1.4999999999999998]"));
        // d > ANY ([1.2, 3.5])
        Query gtQuery2 = convert("d > any ([1.2, 3.5])");
        assertThat(gtQuery2.toString(), Matchers.is("(d:[1.2000000000000002 TO Infinity] d:[3.5000000000000004 TO Infinity])~1"));
        // 1.5d >= ANY (d_array)
        Query gteQuery = convert("1.5 >= any(d_array)");
        assertThat(gteQuery.toString(), Matchers.is("d_array:[-Infinity TO 1.5]"));
        // d >= ANY ([1.2, 3.5])
        Query gteQuery2 = convert("d >= any ([1.2, 3.5])");
        assertThat(gteQuery2.toString(), Matchers.is("(d:[1.2 TO Infinity] d:[3.5 TO Infinity])~1"));
    }

    @Test
    public void testNeqAnyOnArrayLiteral() throws Exception {
        Query neqQuery = convert("name != any (['a', 'b', 'c'])");
        assertThat(neqQuery, Matchers.instanceOf(BooleanQuery.class));
        BooleanClause booleanClause = clauses().get(1);
        assertThat(booleanClause.getOccur(), Matchers.is(MUST_NOT));
        assertThat(booleanClause.getQuery().toString(), Matchers.is("+name:a +name:b +name:c"));
    }

    @Test
    public void testLikeAnyOnArrayLiteral() throws Exception {
        Query likeQuery = convert("name like any (['a', 'b', 'c'])");
        assertThat(likeQuery, Matchers.instanceOf(BooleanQuery.class));
        BooleanQuery likeBQuery = ((BooleanQuery) (likeQuery));
        assertThat(likeBQuery.clauses().size(), Matchers.is(3));
        for (int i = 0; i < 2; i++) {
            // like --> ConstantScoreQuery with regexp-filter
            Query filteredQuery = getQuery();
            assertThat(filteredQuery, Matchers.instanceOf(WildcardQuery.class));
        }
    }

    @Test
    public void testNotLikeAnyOnArrayLiteral() throws Exception {
        Query notLikeQuery = convert("name not like any (['a', 'b', 'c'])");
        assertThat(notLikeQuery, Matchers.instanceOf(BooleanQuery.class));
        BooleanQuery notLikeBQuery = ((BooleanQuery) (notLikeQuery));
        assertThat(notLikeBQuery.clauses(), Matchers.hasSize(2));
        BooleanClause clause = notLikeBQuery.clauses().get(1);
        assertThat(clause.getOccur(), Matchers.is(MUST_NOT));
        assertThat(clauses(), Matchers.hasSize(3));
        for (BooleanClause innerClause : clauses()) {
            assertThat(innerClause.getOccur(), Matchers.is(MUST));
            assertThat(innerClause.getQuery(), Matchers.instanceOf(WildcardQuery.class));
        }
    }

    @Test
    public void testLessThanAnyOnArrayLiteral() throws Exception {
        Query ltQuery2 = convert("name < any (['a', 'b', 'c'])");
        assertThat(ltQuery2, Matchers.instanceOf(BooleanQuery.class));
        BooleanQuery ltBQuery = ((BooleanQuery) (ltQuery2));
        assertThat(ltBQuery.toString(), Matchers.is("(name:{* TO a} name:{* TO b} name:{* TO c})~1"));
    }

    @Test
    public void testSqlLikeToLuceneWildcard() throws Exception {
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("%\\\\%"), Matchers.is("*\\\\*"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("%\\\\_"), Matchers.is("*\\\\?"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("%\\%"), Matchers.is("*%"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("%me"), Matchers.is("*me"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("\\%me"), Matchers.is("%me"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("*me"), Matchers.is("\\*me"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("_me"), Matchers.is("?me"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("\\_me"), Matchers.is("_me"));
        assertThat(LikeQuery.convertSqlLikeToLuceneWildcard("?me"), Matchers.is("\\?me"));
    }

    /**
     * geo match tests below... error cases (wrong matchType, etc.) are not tests here because validation is done in the
     * analyzer
     */
    @Test
    public void testGeoShapeMatchWithDefaultMatchType() throws Exception {
        Query query = convert("match(shape, 'POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))')");
        assertThat(query, Matchers.instanceOf(IntersectsPrefixTreeQuery.class));
    }

    @Test
    public void testGeoShapeMatchDisJoint() throws Exception {
        Query query = convert("match(shape, 'POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))') using disjoint");
        assertThat(query, Matchers.instanceOf(ConstantScoreQuery.class));
        Query booleanQuery = getQuery();
        assertThat(booleanQuery, Matchers.instanceOf(BooleanQuery.class));
        BooleanClause existsClause = clauses().get(0);
        BooleanClause intersectsClause = clauses().get(1);
        assertThat(existsClause.getQuery(), Matchers.instanceOf(TermRangeQuery.class));
        assertThat(intersectsClause.getQuery(), Matchers.instanceOf(IntersectsPrefixTreeQuery.class));
    }

    @Test
    public void testLikeWithBothSidesReferences() throws Exception {
        Query query = convert("name like name");
        assertThat(query, Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testWhereInIsOptimized() throws Exception {
        Query query = convert("name in ('foo', 'bar')");
        assertThat(query, Matchers.instanceOf(TermInSetQuery.class));
        assertThat(query.toString(), Matchers.is("name:(bar foo)"));
    }

    @Test
    public void testIsNullOnObjectArray() throws Exception {
        Query query = convert("o_array IS NULL");
        assertThat(query.toString(), Matchers.is("+*:* -ConstantScore(_field_names:o_array)"));
        query = convert("o_array IS NOT NULL");
        assertThat(query, Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testRewriteDocReferenceInWhereClause() throws Exception {
        Query query = convert("_doc['name'] = 'foo'");
        assertThat(query, Matchers.instanceOf(TermQuery.class));
        assertThat(query.toString(), Matchers.is("name:foo"));
        query = convert("_doc = {\"name\"=\'foo\'}");
        assertThat(query, Matchers.instanceOf(GenericFunctionQuery.class));
    }

    @Test
    public void testMatchQueryTermMustNotBeNull() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot use NULL as query term in match predicate");
        convert("match(name, null)");
    }

    @Test
    public void testMatchQueryTermMustBeALiteral() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("queryTerm must be a literal");
        convert("match(name, name)");
    }

    @Test
    public void testRangeQueryForId() throws Exception {
        Query query = convert("_id > 'foo'");
        assertThat(query, Matchers.instanceOf(TermRangeQuery.class));
    }

    @Test
    public void testNiceErrorIsThrownOnInvalidTopLevelLiteral() {
        expectedException.expectMessage("Can't build query from symbol 'yes'");
        convert("'yes'");
    }

    @Test
    public void testRangeQueryForUid() throws Exception {
        Query query = convert("_uid > 'foo'");
        assertThat(query, Matchers.instanceOf(TermRangeQuery.class));
        TermRangeQuery rangeQuery = ((TermRangeQuery) (query));
        assertThat(rangeQuery.getField(), Matchers.is("_id"));
        assertThat(rangeQuery.getLowerTerm().utf8ToString(), Matchers.is("foo"));
    }

    @Test
    public void testIsNullOnGeoPoint() throws Exception {
        Query query = convert("point is null");
        assertThat(query.toString(), Matchers.is("+*:* -ConstantScore(DocValuesFieldExistsQuery [field=point])"));
    }

    @Test
    public void testIpRange() throws Exception {
        Query query = convert("addr between '192.168.0.1' and '192.168.0.255'");
        assertThat(query.toString(), Matchers.is("+addr:[192.168.0.1 TO ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff] +addr:[0:0:0:0:0:0:0:0 TO 192.168.0.255]"));
        query = convert("addr < 'fe80::1'");
        assertThat(query.toString(), Matchers.is("addr:[0:0:0:0:0:0:0:0 TO fe80:0:0:0:0:0:0:0]"));
    }

    @Test
    public void testAnyEqOnTimestampArrayColumn() {
        assertThat(convert("1129224512000 = ANY(ts_array)").toString(), Matchers.is("ts_array:[1129224512000 TO 1129224512000]"));
    }

    @Test
    public void testAnyNotEqOnTimestampColumn() {
        assertThat(convert("ts != ANY([1129224512000])").toString(), Matchers.is("+*:* -(+ts:[1129224512000 TO 1129224512000])"));
    }

    @Test
    public void testArrayAccessResultsInTermAndFunctionQuery() {
        assertThat(convert("ts_array[1] = 1129224512000").toString(), Matchers.is("+ts_array:[1129224512000 TO 1129224512000] #subscript(Ref{doc.users.ts_array, timestamp_array}, 1) = 1129224512000"));
        assertThat(convert("ts_array[1] >= 1129224512000").toString(), Matchers.is("+ts_array:[1129224512000 TO 9223372036854775807] #subscript(Ref{doc.users.ts_array, timestamp_array}, 1) >= 1129224512000"));
        assertThat(convert("ts_array[1] > 1129224512000").toString(), Matchers.is("+ts_array:[1129224512001 TO 9223372036854775807] #subscript(Ref{doc.users.ts_array, timestamp_array}, 1) > 1129224512000"));
        assertThat(convert("ts_array[1] <= 1129224512000").toString(), Matchers.is("+ts_array:[-9223372036854775808 TO 1129224512000] #subscript(Ref{doc.users.ts_array, timestamp_array}, 1) <= 1129224512000"));
        assertThat(convert("ts_array[1] < 1129224512000").toString(), Matchers.is("+ts_array:[-9223372036854775808 TO 1129224511999] #subscript(Ref{doc.users.ts_array, timestamp_array}, 1) < 1129224512000"));
    }

    @Test
    public void testObjectArrayAccessResultsInFunctionQuery() {
        assertThat(convert("o_array[1] = {x=1}").toString(), Matchers.is("subscript(Ref{doc.users.o_array, object_array}, 1) = {x=1}"));
    }

    @Test
    public void testMatchWithOperator() {
        assertThat(convert("match(tags, 'foo bar') using best_fields with (operator='and')").toString(), Matchers.is("+tags:foo +tags:bar"));
    }

    @Test
    public void testMultiMatchWithOperator() {
        assertThat(convert("match((tags, name), 'foo bar') using best_fields with (operator='and')").toString(), Matchers.is("(name:foo bar | (+tags:foo +tags:bar))"));
    }

    @Test
    public void testEqOnObjectPreFiltersOnKnownObjectLiteralContents() {
        // termQuery for obj.x; nothing for obj.z because it's missing in the mapping
        assertThat(convert("obj = {x=10, z=20}").toString(), Matchers.is("+obj.x:[10 TO 10] #Ref{doc.users.obj, object} = {x=10, z=20}"));
    }

    @Test
    public void testEqOnObjectDoesBoolTermQueryForContents() {
        assertThat(convert("obj = {x=10, y=20}").toString(), Matchers.is("+obj.x:[10 TO 10] +obj.y:[20 TO 20]"));
    }

    @Test
    public void testEqAnyOnNestedArray() {
        assertThat(convert("[1, 2] = any(o_array['xs'])").toString(), Matchers.is("+o_array.xs:{1 2} #any_=([1, 2], Ref{doc.users.o_array['xs'], integer_array_array})"));
    }

    @Test
    public void testGtAnyOnNestedArrayIsNotSupported() {
        expectedException.expectMessage("Cannot use any_> when the left side is an array");
        convert("[1, 2] > any(o_array['xs'])");
    }

    @Test
    public void testGteAnyOnNestedArrayIsNotSupported() {
        expectedException.expectMessage("Cannot use any_>= when the left side is an array");
        convert("[1, 2] >= any(o_array['xs'])");
    }

    @Test
    public void testLtAnyOnNestedArrayIsNotSupported() {
        expectedException.expectMessage("Cannot use any_< when the left side is an array");
        convert("[1, 2] < any(o_array['xs'])");
    }

    @Test
    public void testLteAnyOnNestedArrayIsNotSupported() {
        expectedException.expectMessage("Cannot use any_<= when the left side is an array");
        convert("[1, 2] <= any(o_array['xs'])");
    }

    @Test
    public void testAnyOnObjectArrayResultsInXY() {
        Query query = convert("{xs=[1, 1]} = ANY(o_array)");
        assertThat(query, Matchers.instanceOf(GenericFunctionQuery.class));
    }
}

