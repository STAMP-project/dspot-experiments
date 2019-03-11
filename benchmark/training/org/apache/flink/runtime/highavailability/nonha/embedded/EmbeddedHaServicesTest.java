/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.highavailability.nonha.embedded;


import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.leaderelection.LeaderContender;
import org.apache.flink.runtime.leaderelection.LeaderElectionService;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalListener;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the {@link EmbeddedHaServices}.
 */
public class EmbeddedHaServicesTest extends TestLogger {
    private EmbeddedHaServices embeddedHaServices;

    /**
     * Tests that exactly one JobManager is elected as the leader for a given job id.
     */
    @Test
    public void testJobManagerLeaderElection() throws Exception {
        JobID jobId1 = new JobID();
        JobID jobId2 = new JobID();
        LeaderContender leaderContender1 = Mockito.mock(LeaderContender.class);
        LeaderContender leaderContender2 = Mockito.mock(LeaderContender.class);
        LeaderContender leaderContenderDifferentJobId = Mockito.mock(LeaderContender.class);
        LeaderElectionService leaderElectionService1 = embeddedHaServices.getJobManagerLeaderElectionService(jobId1);
        LeaderElectionService leaderElectionService2 = embeddedHaServices.getJobManagerLeaderElectionService(jobId1);
        LeaderElectionService leaderElectionServiceDifferentJobId = embeddedHaServices.getJobManagerLeaderElectionService(jobId2);
        leaderElectionService1.start(leaderContender1);
        leaderElectionService2.start(leaderContender2);
        leaderElectionServiceDifferentJobId.start(leaderContenderDifferentJobId);
        ArgumentCaptor<UUID> leaderIdArgumentCaptor1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> leaderIdArgumentCaptor2 = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(leaderContender1, Mockito.atLeast(0)).grantLeadership(leaderIdArgumentCaptor1.capture());
        Mockito.verify(leaderContender2, Mockito.atLeast(0)).grantLeadership(leaderIdArgumentCaptor2.capture());
        TestCase.assertTrue(((leaderIdArgumentCaptor1.getAllValues().isEmpty()) ^ (leaderIdArgumentCaptor2.getAllValues().isEmpty())));
        Mockito.verify(leaderContenderDifferentJobId).grantLeadership(ArgumentMatchers.any(UUID.class));
    }

    /**
     * Tests that exactly one ResourceManager is elected as the leader.
     */
    @Test
    public void testResourceManagerLeaderElection() throws Exception {
        LeaderContender leaderContender1 = Mockito.mock(LeaderContender.class);
        LeaderContender leaderContender2 = Mockito.mock(LeaderContender.class);
        LeaderElectionService leaderElectionService1 = embeddedHaServices.getResourceManagerLeaderElectionService();
        LeaderElectionService leaderElectionService2 = embeddedHaServices.getResourceManagerLeaderElectionService();
        leaderElectionService1.start(leaderContender1);
        leaderElectionService2.start(leaderContender2);
        ArgumentCaptor<UUID> leaderIdArgumentCaptor1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> leaderIdArgumentCaptor2 = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(leaderContender1, Mockito.atLeast(0)).grantLeadership(leaderIdArgumentCaptor1.capture());
        Mockito.verify(leaderContender2, Mockito.atLeast(0)).grantLeadership(leaderIdArgumentCaptor2.capture());
        TestCase.assertTrue(((leaderIdArgumentCaptor1.getAllValues().isEmpty()) ^ (leaderIdArgumentCaptor2.getAllValues().isEmpty())));
    }

    /**
     * Tests the JobManager leader retrieval for a given job.
     */
    @Test
    public void testJobManagerLeaderRetrieval() throws Exception {
        final String address = "foobar";
        JobID jobId = new JobID();
        LeaderRetrievalListener leaderRetrievalListener = Mockito.mock(LeaderRetrievalListener.class);
        LeaderContender leaderContender = Mockito.mock(LeaderContender.class);
        Mockito.when(leaderContender.getAddress()).thenReturn(address);
        LeaderElectionService leaderElectionService = embeddedHaServices.getJobManagerLeaderElectionService(jobId);
        LeaderRetrievalService leaderRetrievalService = embeddedHaServices.getJobManagerLeaderRetriever(jobId);
        leaderRetrievalService.start(leaderRetrievalListener);
        leaderElectionService.start(leaderContender);
        ArgumentCaptor<UUID> leaderIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(leaderContender).grantLeadership(leaderIdArgumentCaptor.capture());
        final UUID leaderId = leaderIdArgumentCaptor.getValue();
        leaderElectionService.confirmLeaderSessionID(leaderId);
        Mockito.verify(leaderRetrievalListener).notifyLeaderAddress(ArgumentMatchers.eq(address), ArgumentMatchers.eq(leaderId));
    }

    /**
     * Tests the ResourceManager leader retrieval for a given job.
     */
    @Test
    public void testResourceManagerLeaderRetrieval() throws Exception {
        final String address = "foobar";
        LeaderRetrievalListener leaderRetrievalListener = Mockito.mock(LeaderRetrievalListener.class);
        LeaderContender leaderContender = Mockito.mock(LeaderContender.class);
        Mockito.when(leaderContender.getAddress()).thenReturn(address);
        LeaderElectionService leaderElectionService = embeddedHaServices.getResourceManagerLeaderElectionService();
        LeaderRetrievalService leaderRetrievalService = embeddedHaServices.getResourceManagerLeaderRetriever();
        leaderRetrievalService.start(leaderRetrievalListener);
        leaderElectionService.start(leaderContender);
        ArgumentCaptor<UUID> leaderIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(leaderContender).grantLeadership(leaderIdArgumentCaptor.capture());
        final UUID leaderId = leaderIdArgumentCaptor.getValue();
        leaderElectionService.confirmLeaderSessionID(leaderId);
        Mockito.verify(leaderRetrievalListener).notifyLeaderAddress(ArgumentMatchers.eq(address), ArgumentMatchers.eq(leaderId));
    }

    /**
     * Tests that concurrent leadership operations (granting and revoking) leadership leave the
     * system in a sane state.
     */
    @Test
    public void testConcurrentLeadershipOperations() throws Exception {
        final LeaderElectionService dispatcherLeaderElectionService = embeddedHaServices.getDispatcherLeaderElectionService();
        final ArrayBlockingQueue<UUID> offeredSessionIds = new ArrayBlockingQueue<>(2);
        final EmbeddedHaServicesTest.TestingLeaderContender leaderContender = new EmbeddedHaServicesTest.TestingLeaderContender(offeredSessionIds);
        dispatcherLeaderElectionService.start(leaderContender);
        final UUID oldLeaderSessionId = offeredSessionIds.take();
        Assert.assertThat(dispatcherLeaderElectionService.hasLeadership(oldLeaderSessionId), Matchers.is(true));
        embeddedHaServices.getDispatcherLeaderService().revokeLeadership().get();
        Assert.assertThat(dispatcherLeaderElectionService.hasLeadership(oldLeaderSessionId), Matchers.is(false));
        embeddedHaServices.getDispatcherLeaderService().grantLeadership();
        final UUID newLeaderSessionId = offeredSessionIds.take();
        Assert.assertThat(dispatcherLeaderElectionService.hasLeadership(newLeaderSessionId), Matchers.is(true));
        dispatcherLeaderElectionService.confirmLeaderSessionID(oldLeaderSessionId);
        dispatcherLeaderElectionService.confirmLeaderSessionID(newLeaderSessionId);
        Assert.assertThat(dispatcherLeaderElectionService.hasLeadership(newLeaderSessionId), Matchers.is(true));
        leaderContender.tryRethrowException();
    }

    private static final class TestingLeaderContender implements LeaderContender {
        private final BlockingQueue<UUID> offeredSessionIds;

        private final AtomicReference<Exception> occurredException;

        private TestingLeaderContender(BlockingQueue<UUID> offeredSessionIds) {
            this.offeredSessionIds = offeredSessionIds;
            occurredException = new AtomicReference<>(null);
        }

        @Override
        public void grantLeadership(UUID leaderSessionID) {
            offeredSessionIds.offer(leaderSessionID);
        }

        @Override
        public void revokeLeadership() {
        }

        @Override
        public String getAddress() {
            return "foobar";
        }

        @Override
        public void handleError(Exception exception) {
            occurredException.compareAndSet(null, exception);
        }

        public void tryRethrowException() throws Exception {
            ExceptionUtils.tryRethrowException(occurredException.get());
        }
    }
}

