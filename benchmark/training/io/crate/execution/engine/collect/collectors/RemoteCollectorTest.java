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


import io.crate.exceptions.JobKilledException;
import io.crate.execution.jobs.kill.TransportKillJobsNodeAction;
import io.crate.execution.jobs.transport.JobRequest;
import io.crate.execution.jobs.transport.JobResponse;
import io.crate.execution.jobs.transport.TransportJobAction;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.TestingRowConsumer;
import java.util.concurrent.atomic.AtomicInteger;
import org.elasticsearch.action.ActionListener;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;


public class RemoteCollectorTest extends CrateDummyClusterServiceUnitTest {
    private TransportJobAction transportJobAction;

    private TransportKillJobsNodeAction transportKillJobsNodeAction;

    private RemoteCollector remoteCollector;

    private TestingRowConsumer consumer;

    @Captor
    public ArgumentCaptor<ActionListener<JobResponse>> listenerCaptor;

    private AtomicInteger numBroadcastCalls;

    @Test
    public void testKillBeforeContextCreation() throws Exception {
        remoteCollector.kill(new InterruptedException("KILLED"));
        remoteCollector.doCollect();
        Mockito.verify(transportJobAction, Mockito.times(0)).execute(ArgumentMatchers.eq("remoteNode"), ArgumentMatchers.any(JobRequest.class), ArgumentMatchers.any(ActionListener.class));
        expectedException.expect(InterruptedException.class);
        consumer.getResult();
    }

    @Test
    public void testRemoteContextIsNotCreatedIfKillHappensBeforeCreateRemoteContext() throws Exception {
        remoteCollector.createLocalContext();
        remoteCollector.kill(new InterruptedException());
        remoteCollector.createRemoteContext();
        Mockito.verify(transportJobAction, Mockito.times(0)).execute(ArgumentMatchers.eq("remoteNode"), ArgumentMatchers.any(JobRequest.class), ArgumentMatchers.any(ActionListener.class));
        expectedException.expect(JobKilledException.class);
        consumer.getResult();
    }

    @Test
    public void testKillRequestsAreMadeIfCollectorIsKilledAfterRemoteContextCreation() throws Exception {
        remoteCollector.doCollect();
        Mockito.verify(transportJobAction, Mockito.times(1)).execute(ArgumentMatchers.eq("remoteNode"), ArgumentMatchers.any(JobRequest.class), listenerCaptor.capture());
        remoteCollector.kill(new InterruptedException());
        ActionListener<JobResponse> listener = listenerCaptor.getValue();
        listener.onResponse(new JobResponse());
        assertThat(numBroadcastCalls.get(), Matchers.is(1));
    }
}

