/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.agent;


import AgentModel.Listener;
import Reactor.Callback;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.PortMapping;
import com.spotify.helios.common.descriptors.Task;
import com.spotify.helios.common.descriptors.TaskStatus;
import com.spotify.helios.servicescommon.PersistentAtomicReference;
import com.spotify.helios.servicescommon.Reactor;
import com.spotify.helios.servicescommon.ReactorFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AgentTest {
    private static final Set<Integer> EMPTY_PORT_SET = Collections.emptySet();

    @Mock
    private AgentModel model;

    @Mock
    private SupervisorFactory supervisorFactory;

    @Mock
    private ReactorFactory reactorFactory;

    @Mock
    private Supervisor fooSupervisor;

    @Mock
    private Supervisor barSupervisor;

    @Mock
    private Reactor reactor;

    @Mock
    private PortAllocator portAllocator;

    @Mock
    private Reaper reaper;

    @Captor
    private ArgumentCaptor<Reactor.Callback> callbackCaptor;

    @Captor
    private ArgumentCaptor<AgentModel.Listener> listenerCaptor;

    @Captor
    private ArgumentCaptor<Long> timeoutCaptor;

    private static final Map<String, Integer> EMPTY_PORT_ALLOCATION = Collections.emptyMap();

    private final Map<JobId, Task> jobs = Maps.newHashMap();

    private final Map<JobId, Task> unmodifiableJobs = Collections.unmodifiableMap(jobs);

    private final Map<JobId, TaskStatus> jobStatuses = Maps.newHashMap();

    private final Map<JobId, TaskStatus> unmodifiableJobStatuses = Collections.unmodifiableMap(jobStatuses);

    private Agent sut;

    private Callback callback;

    private Listener listener;

    private PersistentAtomicReference<Map<JobId, Execution>> executions;

    private static final Job FOO_JOB = Job.newBuilder().setCommand(Arrays.asList("foo", "foo")).setImage("foo:4711").setName("foo").setVersion("17").setPorts(ImmutableMap.of("p1", PortMapping.of(4711), "p2", PortMapping.of(4712, 12345))).build();

    private static final Map<String, Integer> FOO_PORT_ALLOCATION = ImmutableMap.of("p1", 30000, "p2", 12345);

    private static final Set<Integer> FOO_PORT_SET = ImmutableSet.copyOf(AgentTest.FOO_PORT_ALLOCATION.values());

    private static final Job BAR_JOB = Job.newBuilder().setCommand(Arrays.asList("bar", "bar")).setImage("bar:5656").setName("bar").setVersion("63").build();

    private static final Map<String, Integer> BAR_PORT_ALLOCATION = ImmutableMap.of();

    @Test
    public void verifyReactorIsUpdatedWhenListenerIsCalled() throws Exception {
        startAgent();
        listener.tasksChanged(model);
        Mockito.verify(reactor, Mockito.times(2)).signal();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void verifyAgentRecoversState() throws Exception {
        configure(AgentTest.FOO_JOB, START);
        configure(AgentTest.BAR_JOB, STOP);
        executions.setUnchecked(ImmutableMap.of(AgentTest.BAR_JOB.getId(), Execution.of(AgentTest.BAR_JOB).withGoal(START).withPorts(AgentTest.EMPTY_PORT_ALLOCATION), AgentTest.FOO_JOB.getId(), Execution.of(AgentTest.FOO_JOB).withGoal(START).withPorts(AgentTest.EMPTY_PORT_ALLOCATION)));
        final String fooContainerId = "foo_container_id";
        final String barContainerId = "bar_container_id";
        final TaskStatus fooStatus = TaskStatus.newBuilder().setGoal(START).setJob(AgentTest.FOO_JOB).setContainerId(fooContainerId).setState(RUNNING).build();
        final TaskStatus barStatus = TaskStatus.newBuilder().setGoal(START).setJob(AgentTest.BAR_JOB).setContainerId(barContainerId).setState(RUNNING).build();
        jobStatuses.put(AgentTest.FOO_JOB.getId(), fooStatus);
        jobStatuses.put(AgentTest.BAR_JOB.getId(), barStatus);
        startAgent();
        Mockito.verify(portAllocator, Mockito.never()).allocate(ArgumentMatchers.anyMap(), ArgumentMatchers.anySet());
        Mockito.verify(supervisorFactory).create(ArgumentMatchers.eq(AgentTest.BAR_JOB), ArgumentMatchers.eq(barContainerId), ArgumentMatchers.eq(AgentTest.EMPTY_PORT_ALLOCATION), ArgumentMatchers.any(Supervisor.Listener.class));
        Mockito.verify(supervisorFactory).create(ArgumentMatchers.eq(AgentTest.FOO_JOB), ArgumentMatchers.eq(fooContainerId), ArgumentMatchers.eq(AgentTest.EMPTY_PORT_ALLOCATION), ArgumentMatchers.any(Supervisor.Listener.class));
        callback.run(false);
        Mockito.verify(fooSupervisor).setGoal(START);
        Mockito.verify(barSupervisor).setGoal(STOP);
        Mockito.when(fooSupervisor.isStarting()).thenReturn(true);
        Mockito.when(fooSupervisor.isStopping()).thenReturn(false);
        Mockito.when(fooSupervisor.isDone()).thenReturn(true);
        Mockito.when(barSupervisor.isStarting()).thenReturn(false);
        Mockito.when(barSupervisor.isStopping()).thenReturn(true);
        Mockito.when(barSupervisor.isDone()).thenReturn(true);
        callback.run(false);
        Mockito.verify(fooSupervisor, Mockito.atLeastOnce()).setGoal(START);
        Mockito.verify(barSupervisor, Mockito.atLeastOnce()).setGoal(STOP);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void verifyAgentRecoversStateAndStopsUndesiredSupervisors() throws Exception {
        final Map<JobId, Execution> newExecutions = Maps.newHashMap();
        newExecutions.put(AgentTest.FOO_JOB.getId(), Execution.of(AgentTest.FOO_JOB).withGoal(START).withPorts(AgentTest.EMPTY_PORT_ALLOCATION));
        executions.setUnchecked(newExecutions);
        configure(AgentTest.FOO_JOB, UNDEPLOY);
        startAgent();
        // Verify that the undesired supervisor was created
        Mockito.verify(portAllocator, Mockito.never()).allocate(ArgumentMatchers.anyMap(), ArgumentMatchers.anySet());
        Mockito.verify(supervisorFactory).create(ArgumentMatchers.eq(AgentTest.FOO_JOB), ArgumentMatchers.anyString(), ArgumentMatchers.eq(AgentTest.EMPTY_PORT_ALLOCATION), ArgumentMatchers.any(Supervisor.Listener.class));
        // ... and then stopped
        callback.run(false);
        Mockito.verify(fooSupervisor).setGoal(UNDEPLOY);
        Mockito.when(fooSupervisor.isStopping()).thenReturn(true);
        Mockito.when(fooSupervisor.isStarting()).thenReturn(false);
        Mockito.when(fooSupervisor.isDone()).thenReturn(true);
        // And not started again
        callback.run(false);
        Mockito.verify(fooSupervisor, Mockito.never()).setGoal(START);
    }

    @Test
    public void verifyAgentStartsSupervisors() throws Exception {
        startAgent();
        start(AgentTest.FOO_JOB);
        Mockito.verify(portAllocator).allocate(AgentTest.FOO_JOB.getPorts(), AgentTest.EMPTY_PORT_SET);
        Mockito.verify(supervisorFactory).create(ArgumentMatchers.eq(AgentTest.FOO_JOB), ArgumentMatchers.anyString(), ArgumentMatchers.eq(AgentTest.FOO_PORT_ALLOCATION), ArgumentMatchers.any(Supervisor.Listener.class));
        Mockito.verify(fooSupervisor).setGoal(START);
        Mockito.when(fooSupervisor.isStarting()).thenReturn(true);
        start(AgentTest.BAR_JOB);
        Mockito.verify(portAllocator).allocate(AgentTest.BAR_JOB.getPorts(), AgentTest.FOO_PORT_SET);
        Mockito.verify(supervisorFactory).create(ArgumentMatchers.eq(AgentTest.BAR_JOB), ArgumentMatchers.anyString(), ArgumentMatchers.eq(AgentTest.EMPTY_PORT_ALLOCATION), ArgumentMatchers.any(Supervisor.Listener.class));
        Mockito.verify(barSupervisor).setGoal(START);
        Mockito.when(barSupervisor.isStarting()).thenReturn(true);
        callback.run(false);
        Mockito.verify(fooSupervisor, Mockito.atLeastOnce()).setGoal(START);
        Mockito.verify(barSupervisor, Mockito.atLeastOnce()).setGoal(START);
    }

    @Test
    public void verifyAgentStopsAndRecreatesSupervisors() throws Exception {
        startAgent();
        // Verify that supervisor is started
        start(AgentTest.FOO_JOB);
        Mockito.verify(portAllocator).allocate(AgentTest.FOO_JOB.getPorts(), AgentTest.EMPTY_PORT_SET);
        Mockito.verify(fooSupervisor).setGoal(START);
        Mockito.when(fooSupervisor.isDone()).thenReturn(true);
        Mockito.when(fooSupervisor.isStopping()).thenReturn(false);
        Mockito.when(fooSupervisor.isStarting()).thenReturn(true);
        // Verify that removal of the job *doesn't* stop the supervisor
        badStop(AgentTest.FOO_JOB);
        // Stop should *not* have been called.
        Mockito.verify(fooSupervisor, Mockito.never()).setGoal(STOP);
        // Stop it the correct way
        stop(AgentTest.FOO_JOB);
        Mockito.verify(fooSupervisor, Mockito.atLeastOnce()).setGoal(UNDEPLOY);
        Mockito.when(fooSupervisor.isDone()).thenReturn(true);
        Mockito.when(fooSupervisor.isStopping()).thenReturn(true);
        Mockito.when(fooSupervisor.isStarting()).thenReturn(false);
        callback.run(false);
        // Verify that a new supervisor is created after the previous one is discarded
        start(AgentTest.FOO_JOB);
        Mockito.verify(portAllocator, Mockito.times(2)).allocate(AgentTest.FOO_JOB.getPorts(), AgentTest.EMPTY_PORT_SET);
        Mockito.verify(supervisorFactory, Mockito.times(2)).create(ArgumentMatchers.eq(AgentTest.FOO_JOB), ArgumentMatchers.anyString(), ArgumentMatchers.eq(AgentTest.FOO_PORT_ALLOCATION), ArgumentMatchers.any(Supervisor.Listener.class));
        Mockito.verify(fooSupervisor, Mockito.atLeast(2)).setGoal(START);
    }

    @Test
    public void verifyCloseDoesNotStopJobs() throws Exception {
        startAgent();
        start(AgentTest.FOO_JOB);
        sut.stopAsync().awaitTerminated();
        Mockito.verify(fooSupervisor).close();
        Mockito.verify(fooSupervisor).join();
        Mockito.verify(fooSupervisor, Mockito.never()).setGoal(STOP);
    }
}

