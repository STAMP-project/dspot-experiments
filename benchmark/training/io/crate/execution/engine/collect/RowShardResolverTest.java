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
package io.crate.execution.engine.collect;


import com.google.common.collect.ImmutableList;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Functions;
import io.crate.metadata.TransactionContext;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Test;


public class RowShardResolverTest extends CrateUnitTest {
    private static final ColumnIdent ID_IDENT = new ColumnIdent("_id");

    private Functions functions = TestingHelpers.getFunctions();

    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testNoPrimaryKeyNoRouting() {
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.<ColumnIdent>of(), ImmutableList.<Symbol>of(), null, null);
        rowShardResolver.setNextRow(row());
        // auto-generated id, no special routing
        assertNotNull(rowShardResolver.id());
        assertNull(rowShardResolver.routing());
    }

    @Test
    public void testNoPrimaryKeyButRouting() {
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.<ColumnIdent>of(), ImmutableList.<Symbol>of(), RowShardResolverTest.ID_IDENT, new InputColumn(1));
        rowShardResolver.setNextRow(row(1, "hoschi"));
        // auto-generated id, special routing
        assertNotNull(rowShardResolver.id());
        assertThat(rowShardResolver.routing(), Is.is("hoschi"));
    }

    @Test
    public void testPrimaryKeyNoRouting() {
        List<Symbol> primaryKeySymbols = ImmutableList.<Symbol>of(new InputColumn(0), new InputColumn(1));
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.of(ci("id"), ci("foo")), primaryKeySymbols, null, null);
        rowShardResolver.setNextRow(row(1, "hoschi"));
        // compound encoded id, no special routing
        assertThat(rowShardResolver.id(), Is.is("AgExBmhvc2NoaQ=="));
        assertNull(rowShardResolver.routing());
    }

    @Test
    public void testPrimaryKeyAndRouting() {
        List<Symbol> primaryKeySymbols = ImmutableList.<Symbol>of(new InputColumn(0), new InputColumn(1));
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.of(ci("id"), ci("foo")), primaryKeySymbols, ci("foo"), new InputColumn(1));
        rowShardResolver.setNextRow(row(1, "hoschi"));
        // compound encoded id, special routing
        assertThat(rowShardResolver.id(), Is.is("AgZob3NjaGkBMQ=="));
        assertThat(rowShardResolver.routing(), Is.is("hoschi"));
    }

    @Test
    public void testMultipleRows() {
        List<Symbol> primaryKeySymbols = ImmutableList.<Symbol>of(new InputColumn(0), new InputColumn(1));
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.of(ci("id"), ci("foo")), primaryKeySymbols, ci("foo"), new InputColumn(1));
        rowShardResolver.setNextRow(row(1, "hoschi"));
        assertThat(rowShardResolver.id(), Is.is("AgZob3NjaGkBMQ=="));
        assertThat(rowShardResolver.routing(), Is.is("hoschi"));
        rowShardResolver.setNextRow(row(2, "galoschi"));
        assertThat(rowShardResolver.id(), Is.is("AghnYWxvc2NoaQEy"));
        assertThat(rowShardResolver.routing(), Is.is("galoschi"));
    }

    @Test
    public void testIdPrimaryKeyNull() {
        List<Symbol> primaryKeySymbols = ImmutableList.<Symbol>of(new InputColumn(2));
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.of(RowShardResolverTest.ID_IDENT), primaryKeySymbols, null, new InputColumn(1));
        rowShardResolver.setNextRow(row(1, "hoschi", null));
        // generated _id, special routing
        assertNotNull(rowShardResolver.id());
        assertThat(rowShardResolver.routing(), Is.is("hoschi"));
    }

    @Test
    public void testPrimaryKeyNullException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("A primary key value must not be NULL");
        List<Symbol> primaryKeySymbols = ImmutableList.<Symbol>of(new InputColumn(0));
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.of(ci("id")), primaryKeySymbols, null, null);
        rowShardResolver.setNextRow(row(new Object[]{ null }));
    }

    @Test
    public void testMultiPrimaryKeyNullException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("A primary key value must not be NULL");
        List<Symbol> primaryKeySymbols = ImmutableList.<Symbol>of(new InputColumn(1), new InputColumn(0));
        RowShardResolver rowShardResolver = new RowShardResolver(txnCtx, functions, ImmutableList.of(ci("id"), ci("foo")), primaryKeySymbols, null, new InputColumn(1));
        rowShardResolver.setNextRow(row(1, null));
    }
}

