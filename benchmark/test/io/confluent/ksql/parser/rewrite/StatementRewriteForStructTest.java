/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.parser.rewrite;


import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.parser.KsqlParserTestUtil;
import io.confluent.ksql.parser.tree.CreateStreamAsSelect;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.CreateTableAsSelect;
import io.confluent.ksql.parser.tree.DereferenceExpression;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.parser.tree.FunctionCall;
import io.confluent.ksql.parser.tree.InsertInto;
import io.confluent.ksql.parser.tree.Query;
import io.confluent.ksql.parser.tree.QuerySpecification;
import io.confluent.ksql.parser.tree.Statement;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;


public class StatementRewriteForStructTest {
    private MetaStore metaStore;

    @Test
    public void shouldCreateCorrectFunctionCallExpression() {
        final String simpleQuery = "SELECT iteminfo->category->name, address->state FROM orders;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        final QuerySpecification querySpecification = StatementRewriteForStructTest.getQuerySpecification(statement);
        MatcherAssert.assertThat(querySpecification.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        final Expression col0 = getExpression();
        final Expression col1 = getExpression();
        MatcherAssert.assertThat(col0, IsInstanceOf.instanceOf(FunctionCall.class));
        MatcherAssert.assertThat(col1, IsInstanceOf.instanceOf(FunctionCall.class));
        MatcherAssert.assertThat(col0.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(FETCH_FIELD_FROM_STRUCT(ORDERS.ITEMINFO, 'CATEGORY'), 'NAME')"));
        MatcherAssert.assertThat(col1.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(ORDERS.ADDRESS, 'STATE')"));
    }

    @Test
    public void shouldNotCreateFunctionCallIfNotNeeded() {
        final String simpleQuery = "SELECT orderid FROM orders;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        final QuerySpecification querySpecification = StatementRewriteForStructTest.getQuerySpecification(statement);
        MatcherAssert.assertThat(querySpecification.getSelect().getSelectItems().size(), CoreMatchers.equalTo(1));
        final Expression col0 = getExpression();
        MatcherAssert.assertThat(col0, IsInstanceOf.instanceOf(DereferenceExpression.class));
        MatcherAssert.assertThat(col0.toString(), CoreMatchers.equalTo("ORDERS.ORDERID"));
    }

    @Test
    public void shouldCreateCorrectFunctionCallExpressionWithSubscript() {
        final String simpleQuery = "SELECT arraycol[0]->name as n0, mapcol['key']->name as n1 FROM nested_stream;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        final QuerySpecification querySpecification = StatementRewriteForStructTest.getQuerySpecification(statement);
        MatcherAssert.assertThat(querySpecification.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        final Expression col0 = getExpression();
        final Expression col1 = getExpression();
        MatcherAssert.assertThat(col0, IsInstanceOf.instanceOf(FunctionCall.class));
        MatcherAssert.assertThat(col1, IsInstanceOf.instanceOf(FunctionCall.class));
        MatcherAssert.assertThat(col0.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(NESTED_STREAM.ARRAYCOL[0], 'NAME')"));
        MatcherAssert.assertThat(col1.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(NESTED_STREAM.MAPCOL['key'], 'NAME')"));
    }

    @Test
    public void shouldCreateCorrectFunctionCallExpressionWithSubscriptWithExpressionIndex() {
        final String simpleQuery = "SELECT arraycol[CAST (item->id AS INTEGER)]->name as n0, mapcol['key']->name as n1 FROM nested_stream;";
        final Statement statement = KsqlParserTestUtil.buildSingleAst(simpleQuery, metaStore).getStatement();
        final QuerySpecification querySpecification = StatementRewriteForStructTest.getQuerySpecification(statement);
        MatcherAssert.assertThat(querySpecification.getSelect().getSelectItems().size(), CoreMatchers.equalTo(2));
        final Expression col0 = getExpression();
        final Expression col1 = getExpression();
        MatcherAssert.assertThat(col0, IsInstanceOf.instanceOf(FunctionCall.class));
        MatcherAssert.assertThat(col1, IsInstanceOf.instanceOf(FunctionCall.class));
        MatcherAssert.assertThat(col0.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(NESTED_STREAM.ARRAYCOL[CAST(FETCH_FIELD_FROM_STRUCT(NESTED_STREAM.ITEM, 'ID') AS INTEGER)], 'NAME')"));
        MatcherAssert.assertThat(col1.toString(), CoreMatchers.equalTo("FETCH_FIELD_FROM_STRUCT(NESTED_STREAM.MAPCOL['key'], 'NAME')"));
    }

    @Test
    public void shouldEnsureRewriteRequirementCorrectly() {
        MatcherAssert.assertThat("Query should be valid for rewrite for struct.", StatementRewriteForStruct.requiresRewrite(EasyMock.mock(Query.class)));
        MatcherAssert.assertThat("CSAS should be valid for rewrite for struct.", StatementRewriteForStruct.requiresRewrite(EasyMock.mock(CreateStreamAsSelect.class)));
        MatcherAssert.assertThat("CTAS should be valid for rewrite for struct.", StatementRewriteForStruct.requiresRewrite(EasyMock.mock(CreateTableAsSelect.class)));
        MatcherAssert.assertThat("Insert Into should be valid for rewrite for struct.", StatementRewriteForStruct.requiresRewrite(EasyMock.mock(InsertInto.class)));
    }

    @Test
    public void shouldFailTestIfStatementShouldBeRewritten() {
        MatcherAssert.assertThat("Incorrect rewrite requirement enforcement.", (!(StatementRewriteForStruct.requiresRewrite(EasyMock.mock(CreateTable.class)))));
    }
}

