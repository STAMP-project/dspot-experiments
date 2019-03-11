/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.cluster;


import Cluster.Role.LEADER;
import ConsensusModule.Configuration;
import ConsensusModule.Context;
import ConsensusModule.State.ACTIVE;
import ConsensusModule.State.INIT;
import ConsensusModule.State.SUSPENDED;
import EventCode.ERROR;
import io.aeron.archive.client.AeronArchive;
import io.aeron.cluster.service.ClusterMarkFile;
import io.aeron.security.DefaultAuthenticatorSupplier;
import io.aeron.status.ReadableCounter;
import java.util.concurrent.TimeUnit;
import org.agrona.collections.MutableLong;
import org.agrona.concurrent.CachedEpochClock;
import org.agrona.concurrent.SystemEpochClock;
import org.agrona.concurrent.status.AtomicCounter;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ConsensusModuleAgentTest {
    private static final String RESPONSE_CHANNEL_ONE = "aeron:udp?endpoint=localhost:11111";

    private static final String RESPONSE_CHANNEL_TWO = "aeron:udp?endpoint=localhost:22222";

    private final EgressPublisher mockEgressPublisher = Mockito.mock(EgressPublisher.class);

    private final LogPublisher mockLogPublisher = Mockito.mock(LogPublisher.class);

    private final Aeron mockAeron = Mockito.mock(Aeron.class);

    private final ConcurrentPublication mockResponsePublication = Mockito.mock(ConcurrentPublication.class);

    private final Counter mockTimedOutClientCounter = Mockito.mock(Counter.class);

    private final Context ctx = new ConsensusModule.Context().errorHandler(Throwable::printStackTrace).errorCounter(Mockito.mock(AtomicCounter.class)).moduleStateCounter(Mockito.mock(Counter.class)).commitPositionCounter(Mockito.mock(Counter.class)).controlToggleCounter(Mockito.mock(Counter.class)).clusterNodeCounter(Mockito.mock(Counter.class)).timedOutClientCounter(mockTimedOutClientCounter).idleStrategySupplier(NoOpIdleStrategy::new).aeron(mockAeron).clusterMemberId(0).serviceHeartbeatCounters(Mockito.mock(Counter.class)).epochClock(new SystemEpochClock()).authenticatorSupplier(new DefaultAuthenticatorSupplier()).clusterMarkFile(Mockito.mock(ClusterMarkFile.class)).archiveContext(new AeronArchive.Context()).logPublisher(mockLogPublisher).egressPublisher(mockEgressPublisher);

    @Test
    public void shouldLimitActiveSessions() {
        final CachedEpochClock clock = new CachedEpochClock();
        ctx.maxConcurrentSessions(1);
        ctx.epochClock(clock);
        final ConsensusModuleAgent agent = new ConsensusModuleAgent(ctx);
        final long correlationIdOne = 1L;
        agent.state(ACTIVE);
        agent.role(LEADER);
        agent.appendedPositionCounter(Mockito.mock(ReadableCounter.class));
        agent.onSessionConnect(correlationIdOne, 2, SEMANTIC_VERSION, ConsensusModuleAgentTest.RESPONSE_CHANNEL_ONE, new byte[0]);
        clock.update(1);
        agent.doWork();
        Mockito.verify(mockLogPublisher).appendSessionOpen(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        final long correlationIdTwo = 2L;
        agent.onSessionConnect(correlationIdTwo, 3, SEMANTIC_VERSION, ConsensusModuleAgentTest.RESPONSE_CHANNEL_TWO, new byte[0]);
        clock.update(2);
        agent.doWork();
        Mockito.verify(mockEgressPublisher).sendEvent(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ERROR), ArgumentMatchers.eq(SESSION_LIMIT_MSG));
    }

    @Test
    public void shouldCloseInactiveSession() {
        final CachedEpochClock clock = new CachedEpochClock();
        final long startMs = 7L;
        clock.update(startMs);
        ctx.epochClock(clock);
        final ConsensusModuleAgent agent = new ConsensusModuleAgent(ctx);
        final long correlationId = 1L;
        agent.state(ACTIVE);
        agent.role(LEADER);
        agent.appendedPositionCounter(Mockito.mock(ReadableCounter.class));
        agent.onSessionConnect(correlationId, 2, SEMANTIC_VERSION, ConsensusModuleAgentTest.RESPONSE_CHANNEL_ONE, new byte[0]);
        agent.doWork();
        Mockito.verify(mockLogPublisher).appendSessionOpen(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(startMs));
        final long timeMs = startMs + (TimeUnit.NANOSECONDS.toMillis(Configuration.sessionTimeoutNs()));
        clock.update(timeMs);
        agent.doWork();
        final long timeoutMs = timeMs + 1L;
        clock.update(timeoutMs);
        agent.doWork();
        Mockito.verify(mockTimedOutClientCounter).incrementOrdered();
        Mockito.verify(mockLogPublisher).appendSessionClose(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(timeoutMs));
        Mockito.verify(mockEgressPublisher).sendEvent(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ERROR), ArgumentMatchers.eq(SESSION_TIMEOUT_MSG));
    }

    @Test
    public void shouldCloseTerminatedSession() {
        final CachedEpochClock clock = new CachedEpochClock();
        final long startMs = 7L;
        clock.update(startMs);
        ctx.epochClock(clock);
        final ConsensusModuleAgent agent = new ConsensusModuleAgent(ctx);
        final long correlationId = 1L;
        agent.state(ACTIVE);
        agent.role(LEADER);
        agent.appendedPositionCounter(Mockito.mock(ReadableCounter.class));
        agent.onSessionConnect(correlationId, 2, SEMANTIC_VERSION, ConsensusModuleAgentTest.RESPONSE_CHANNEL_ONE, new byte[0]);
        agent.doWork();
        final ArgumentCaptor<ClusterSession> sessionCaptor = ArgumentCaptor.forClass(ClusterSession.class);
        Mockito.verify(mockLogPublisher).appendSessionOpen(sessionCaptor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(startMs));
        final long timeMs = startMs + 1;
        clock.update(timeMs);
        agent.doWork();
        agent.onServiceCloseSession(sessionCaptor.getValue().id());
        Mockito.verify(mockLogPublisher).appendSessionClose(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(timeMs));
        Mockito.verify(mockEgressPublisher).sendEvent(ArgumentMatchers.any(ClusterSession.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(ERROR), ArgumentMatchers.eq(SESSION_TERMINATED_MSG));
    }

    @Test
    public void shouldSuspendThenResume() {
        final CachedEpochClock clock = new CachedEpochClock();
        final MutableLong stateValue = new MutableLong();
        final Counter mockState = Mockito.mock(Counter.class);
        Mockito.when(mockState.get()).thenAnswer(( invocation) -> stateValue.value);
        Mockito.doAnswer(( invocation) -> {
            stateValue.value = invocation.getArgument(0);
            return null;
        }).when(mockState).set(ArgumentMatchers.anyLong());
        final MutableLong controlValue = new MutableLong(NEUTRAL.code());
        final Counter mockControlToggle = Mockito.mock(Counter.class);
        Mockito.when(mockControlToggle.get()).thenAnswer(( invocation) -> controlValue.value);
        Mockito.doAnswer(( invocation) -> {
            controlValue.value = invocation.getArgument(0);
            return null;
        }).when(mockControlToggle).set(ArgumentMatchers.anyLong());
        ctx.moduleStateCounter(mockState);
        ctx.controlToggleCounter(mockControlToggle);
        ctx.epochClock(clock);
        final ConsensusModuleAgent agent = new ConsensusModuleAgent(ctx);
        agent.appendedPositionCounter(Mockito.mock(ReadableCounter.class));
        Assert.assertThat(((int) (stateValue.get())), Is.is(INIT.code()));
        agent.state(ACTIVE);
        agent.role(LEADER);
        Assert.assertThat(((int) (stateValue.get())), Is.is(ACTIVE.code()));
        controlValue.value = SUSPEND.code();
        clock.update(1);
        agent.doWork();
        Assert.assertThat(((int) (stateValue.get())), Is.is(SUSPENDED.code()));
        Assert.assertThat(((int) (controlValue.get())), Is.is(NEUTRAL.code()));
        controlValue.value = RESUME.code();
        clock.update(2);
        agent.doWork();
        Assert.assertThat(((int) (stateValue.get())), Is.is(ACTIVE.code()));
        Assert.assertThat(((int) (controlValue.get())), Is.is(NEUTRAL.code()));
        final InOrder inOrder = Mockito.inOrder(mockLogPublisher);
        inOrder.verify(mockLogPublisher).appendClusterAction(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(ClusterAction.SUSPEND));
        inOrder.verify(mockLogPublisher).appendClusterAction(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.eq(ClusterAction.RESUME));
    }
}

