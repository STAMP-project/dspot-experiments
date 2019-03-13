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
package io.crate.expression.reference.sys.node;


import SysNodesTableInfo.Columns.CLUSTER_STATE_VERSION;
import SysNodesTableInfo.Columns.CONNECTIONS;
import SysNodesTableInfo.Columns.ID;
import SysNodesTableInfo.Columns.NAME;
import SysNodesTableInfo.Columns.OS;
import SysNodesTableInfo.Columns.PORT;
import com.google.common.collect.ImmutableSet;
import io.crate.execution.engine.collect.NestableCollectExpression;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.expressions.RowCollectExpressionFactory;
import io.crate.metadata.sys.SysNodesTableInfo;
import java.io.IOException;
import java.util.Collections;
import org.elasticsearch.common.transport.TransportAddress;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class NodeStatsContextFieldResolverTest {
    private NodeStatsContextFieldResolver resolver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TransportAddress postgresAddress;

    @Test
    public void testEmptyColumnIdents() {
        NodeStatsContext context = resolver.forTopColumnIdents(ImmutableSet.of());
        assertDefaultDiscoveryContext(context);
    }

    @Test
    public void testConnectionsHttpLookupAndExpression() {
        NodeStatsContext statsContext = resolver.forTopColumnIdents(Collections.singletonList(CONNECTIONS));
        RowCollectExpressionFactory<NodeStatsContext> expressionFactory = SysNodesTableInfo.expressions().get(CONNECTIONS);
        NestableCollectExpression<NodeStatsContext, ?> expression = expressionFactory.create();
        NestableCollectExpression http = ((NestableCollectExpression) (expression.getChild("http")));
        NestableCollectExpression open = ((NestableCollectExpression) (http.getChild("open")));
        open.setNextRow(statsContext);
        Assert.assertThat(open.value(), CoreMatchers.is(20L));
        NestableCollectExpression total = ((NestableCollectExpression) (http.getChild("total")));
        total.setNextRow(statsContext);
        Assert.assertThat(total.value(), CoreMatchers.is(30L));
    }

    @Test
    public void testNumberOfPSqlConnectionsCanBeRetrieved() {
        // tests the resolver and the expression
        NodeStatsContext statsContext = resolver.forTopColumnIdents(Collections.singletonList(CONNECTIONS));
        RowCollectExpressionFactory<NodeStatsContext> expressionFactory = SysNodesTableInfo.expressions().get(CONNECTIONS);
        NestableCollectExpression<NodeStatsContext, ?> expression = expressionFactory.create();
        NestableCollectExpression psql = ((NestableCollectExpression) (expression.getChild("psql")));
        NestableCollectExpression open = ((NestableCollectExpression) (psql.getChild("open")));
        open.setNextRow(statsContext);
        Assert.assertThat(open.value(), CoreMatchers.is(2L));
        NestableCollectExpression total = ((NestableCollectExpression) (psql.getChild("total")));
        total.setNextRow(statsContext);
        Assert.assertThat(total.value(), CoreMatchers.is(4L));
    }

    @Test
    public void testNumberOfTransportConnectionsCanBeRetrieved() {
        // tests the resolver and the expression
        NodeStatsContext statsContext = resolver.forTopColumnIdents(Collections.singletonList(CONNECTIONS));
        RowCollectExpressionFactory<NodeStatsContext> expressionFactory = SysNodesTableInfo.expressions().get(CONNECTIONS);
        NestableCollectExpression<NodeStatsContext, ?> expression = expressionFactory.create();
        NestableCollectExpression psql = ((NestableCollectExpression) (expression.getChild("transport")));
        NestableCollectExpression open = ((NestableCollectExpression) (psql.getChild("open")));
        open.setNextRow(statsContext);
        Assert.assertThat(open.value(), CoreMatchers.is(12L));
    }

    @Test
    public void testColumnIdentsResolution() {
        NodeStatsContext context = resolver.forTopColumnIdents(ImmutableSet.of(ID, NAME));
        Assert.assertThat(context.isComplete(), CoreMatchers.is(true));
        Assert.assertThat(context.id(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(context.name(), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(context.hostname(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testNoteStatsContextTimestampResolvedCorrectly() {
        NodeStatsContext context = resolver.forTopColumnIdents(ImmutableSet.of(OS));
        Assert.assertThat(context.timestamp(), Matchers.greaterThan(0L));
    }

    @Test
    public void testPSQLPortResolution() throws IOException {
        NodeStatsContext context = resolver.forTopColumnIdents(ImmutableSet.of(new ColumnIdent(PORT.name())));
        Assert.assertThat(context.isComplete(), CoreMatchers.is(true));
        Assert.assertThat(context.pgPort(), CoreMatchers.is(5432));
    }

    @Test
    public void testClusterStateVersion() throws IOException {
        NodeStatsContext context = resolver.forTopColumnIdents(ImmutableSet.of(new ColumnIdent(CLUSTER_STATE_VERSION.name())));
        Assert.assertThat(context.isComplete(), CoreMatchers.is(true));
        Assert.assertThat(context.clusterStateVersion(), CoreMatchers.is(1L));
    }

    @Test
    public void testResolveForNonExistingColumnIdent() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Cannot resolve NodeStatsContext field for \"dummy\" column ident.");
        resolver.forTopColumnIdents(ImmutableSet.of(ID, new ColumnIdent("dummy")));
    }
}

