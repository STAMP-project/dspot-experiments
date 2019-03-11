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
package io.crate.execution.engine.collect.collectors;


import Literal.BOOLEAN_TRUE;
import io.crate.data.BatchIterator;
import io.crate.execution.dsl.phases.RoutedCollectPhase;
import io.crate.execution.engine.collect.stats.NodeStatsRequest;
import io.crate.execution.engine.collect.stats.TransportNodeStatsAction;
import io.crate.expression.InputFactory;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Reference;
import io.crate.metadata.TransactionContext;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.BatchIteratorTester;
import io.crate.testing.TestingHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NodeStatsTest extends CrateUnitTest {
    private RoutedCollectPhase collectPhase;

    private Collection<DiscoveryNode> nodes = new HashSet<>();

    private TransportNodeStatsAction transportNodeStatsAction = Mockito.mock(TransportNodeStatsAction.class);

    private TransactionContext txnCtx = CoordinatorTxnCtx.systemTransactionContext();

    private Reference idRef;

    private Reference nameRef;

    private Reference hostnameRef;

    @Test
    public void testNoRequestIfNotRequired() throws InterruptedException, ExecutionException, TimeoutException {
        List<Symbol> toCollect = new ArrayList<>();
        toCollect.add(idRef);
        Mockito.when(collectPhase.toCollect()).thenReturn(toCollect);
        BatchIterator iterator = NodeStats.newInstance(transportNodeStatsAction, collectPhase, nodes, txnCtx, new InputFactory(TestingHelpers.getFunctions()));
        iterator.loadNextBatch();
        // Because only id and name are selected, transportNodesStatsAction should be never used
        Mockito.verifyNoMoreInteractions(transportNodeStatsAction);
    }

    @Test
    public void testRequestsAreIssued() throws InterruptedException, ExecutionException, TimeoutException {
        List<Symbol> toCollect = new ArrayList<>();
        toCollect.add(idRef);
        toCollect.add(nameRef);
        Mockito.when(collectPhase.toCollect()).thenReturn(toCollect);
        BatchIterator iterator = NodeStats.newInstance(transportNodeStatsAction, collectPhase, nodes, txnCtx, new InputFactory(TestingHelpers.getFunctions()));
        iterator.loadNextBatch();
        Mockito.verifyNoMoreInteractions(transportNodeStatsAction);
    }

    @Test
    public void testRequestsIfRequired() throws InterruptedException, ExecutionException, TimeoutException {
        List<Symbol> toCollect = new ArrayList<>();
        toCollect.add(idRef);
        toCollect.add(hostnameRef);
        Mockito.when(collectPhase.toCollect()).thenReturn(toCollect);
        BatchIterator iterator = NodeStats.newInstance(transportNodeStatsAction, collectPhase, nodes, txnCtx, new InputFactory(TestingHelpers.getFunctions()));
        iterator.loadNextBatch();
        // Hostnames needs to be collected so requests need to be performed
        Mockito.verify(transportNodeStatsAction).execute(ArgumentMatchers.eq("nodeOne"), ArgumentMatchers.any(NodeStatsRequest.class), ArgumentMatchers.any(ActionListener.class), ArgumentMatchers.eq(TimeValue.timeValueMillis(3000L)));
        Mockito.verify(transportNodeStatsAction).execute(ArgumentMatchers.eq("nodeTwo"), ArgumentMatchers.any(NodeStatsRequest.class), ArgumentMatchers.any(ActionListener.class), ArgumentMatchers.eq(TimeValue.timeValueMillis(3000L)));
        Mockito.verifyNoMoreInteractions(transportNodeStatsAction);
    }

    @Test
    public void testNodeStatsIteratorContrat() throws Exception {
        List<Symbol> toCollect = new ArrayList<>();
        toCollect.add(idRef);
        Mockito.when(collectPhase.toCollect()).thenReturn(toCollect);
        Mockito.when(collectPhase.where()).thenReturn(BOOLEAN_TRUE);
        Mockito.when(collectPhase.orderBy()).thenReturn(new io.crate.analyze.OrderBy(Collections.singletonList(idRef), new boolean[]{ false }, new Boolean[]{ true }));
        List<Object[]> expectedResult = Arrays.asList(new Object[]{ "nodeOne" }, new Object[]{ "nodeTwo" });
        BatchIteratorTester tester = new BatchIteratorTester(() -> NodeStats.newInstance(transportNodeStatsAction, collectPhase, nodes, txnCtx, new InputFactory(getFunctions())));
        tester.verifyResultAndEdgeCaseBehaviour(expectedResult);
    }
}

