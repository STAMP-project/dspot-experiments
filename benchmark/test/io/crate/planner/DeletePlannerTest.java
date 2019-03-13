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
package io.crate.planner;


import VersionInvalidException.ERROR_MSG;
import com.google.common.collect.Lists;
import io.crate.exceptions.VersionInvalidException;
import io.crate.expression.symbol.ParameterSymbol;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.TransactionContext;
import io.crate.planner.node.ddl.DeletePartitions;
import io.crate.planner.node.dml.DeleteById;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import io.crate.testing.TestingHelpers;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DeletePlannerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor e;

    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    @Test
    public void testDeletePlan() throws Exception {
        DeleteById plan = e.plan("delete from users where id = 1");
        assertThat(plan.table().ident().name(), Matchers.is("users"));
        assertThat(plan.docKeys().size(), Matchers.is(1));
        assertThat(plan.docKeys().getOnlyKey(), TestingHelpers.isDocKey(1L));
    }

    @Test
    public void testBulkDeletePartitionedTable() throws Exception {
        DeletePartitions plan = e.plan("delete from parted_pks where date = ?");
        List<Symbol> partitionSymbols = plan.partitions().get(0);
        assertThat(partitionSymbols, Matchers.contains(Matchers.instanceOf(ParameterSymbol.class)));
    }

    @Test
    public void testMultiDeletePlan() throws Exception {
        DeleteById plan = e.plan("delete from users where id in (1, 2)");
        assertThat(plan.docKeys().size(), Matchers.is(2));
        List<String> docKeys = Lists.newArrayList(plan.docKeys()).stream().map(( x) -> x.getId(txnCtx, e.functions(), Row.EMPTY, SubQueryResults.EMPTY)).collect(Collectors.toList());
        assertThat(docKeys, Matchers.containsInAnyOrder("1", "2"));
    }

    @Test
    public void testDeleteWhereVersionIsNullPredicate() throws Exception {
        expectedException.expect(VersionInvalidException.class);
        expectedException.expectMessage(ERROR_MSG);
        e.plan("delete from users where _version is null");
    }
}

