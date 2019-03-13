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
package io.crate.planner.node.management;


import io.crate.execution.engine.collect.stats.JobsLogs;
import io.crate.execution.jobs.kill.KillAllRequest;
import io.crate.execution.jobs.kill.KillResponse;
import io.crate.execution.jobs.kill.TransportKillAllNodeAction;
import io.crate.execution.jobs.kill.TransportKillJobsNodeAction;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.TestingRowConsumer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.TransportService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class KillPlanTest extends CrateDummyClusterServiceUnitTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testKillTaskCallsBroadcastOnTransportKillAllNodeAction() throws Exception {
        AtomicInteger broadcastCalls = new AtomicInteger(0);
        AtomicInteger nodeOperationCalls = new AtomicInteger(0);
        TransportKillAllNodeAction killAllNodeAction = new TransportKillAllNodeAction(Settings.EMPTY, new io.crate.execution.jobs.TasksService(Settings.EMPTY, clusterService, new JobsLogs(() -> false)), clusterService, Mockito.mock(TransportService.class)) {
            @Override
            public void broadcast(KillAllRequest request, ActionListener<Long> listener) {
                broadcastCalls.incrementAndGet();
            }

            @Override
            public CompletableFuture<KillResponse> nodeOperation(KillAllRequest request) {
                nodeOperationCalls.incrementAndGet();
                return super.nodeOperation(request);
            }
        };
        KillPlan killPlan = new KillPlan();
        killPlan.execute(killAllNodeAction, Mockito.mock(TransportKillJobsNodeAction.class), new TestingRowConsumer());
        assertThat(broadcastCalls.get(), Matchers.is(1));
        assertThat(nodeOperationCalls.get(), Matchers.is(0));
    }
}

