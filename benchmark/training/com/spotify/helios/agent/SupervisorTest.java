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


import Goal.UNDEPLOY;
import Supervisor.DEFAULT_SECONDS_TO_WAIT_BEFORE_KILL;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.SettableFuture;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerExit;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.PortMapping;
import com.spotify.helios.common.descriptors.TaskStatus;
import com.spotify.helios.serviceregistration.ServiceRegistrar;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SupervisorTest {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final String NAMESPACE = "helios-deadbeef";

    private static final String REPOSITORY = "spotify";

    private static final String TAG = "17";

    private static final String IMAGE = ((SupervisorTest.REPOSITORY) + ":") + (SupervisorTest.TAG);

    private static final String NAME = "foobar";

    private static final List<String> COMMAND = Arrays.asList("foo", "bar");

    private static final String VERSION = "4711";

    private static final Job JOB = Job.newBuilder().setName(SupervisorTest.NAME).setCommand(SupervisorTest.COMMAND).setImage(SupervisorTest.IMAGE).setVersion(SupervisorTest.VERSION).build();

    private static final Map<String, PortMapping> PORTS = Collections.emptyMap();

    private static final Map<String, String> ENV = ImmutableMap.of("foo", "17", "bar", "4711");

    private static final Set<String> EXPECTED_CONTAINER_ENV = ImmutableSet.of("foo=17", "bar=4711");

    private final ContainerInfo runningResponse = Mockito.mock(ContainerInfo.class);

    private final ContainerInfo stoppedResponse = Mockito.mock(ContainerInfo.class);

    @Mock
    public AgentModel model;

    @Mock
    public DockerClient docker;

    @Mock
    public RestartPolicy retryPolicy;

    @Mock
    public ServiceRegistrar registrar;

    @Mock
    public Sleeper sleeper;

    @Captor
    public ArgumentCaptor<ContainerConfig> containerConfigCaptor;

    @Captor
    public ArgumentCaptor<String> containerNameCaptor;

    @Captor
    public ArgumentCaptor<TaskStatus> taskStatusCaptor;

    private Supervisor sut;

    @Test
    public void verifySupervisorStartsAndStopsDockerContainer() throws Exception {
        final String containerId = "deadbeef";
        Mockito.when(docker.createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class))).thenReturn(ContainerCreation.builder().id(containerId).build());
        final ImageInfo imageInfo = Mockito.mock(ImageInfo.class);
        Mockito.when(docker.inspectImage(SupervisorTest.IMAGE)).thenReturn(imageInfo);
        // Have waitContainer wait forever.
        final SettableFuture<ContainerExit> waitFuture = SettableFuture.create();
        Mockito.when(docker.waitContainer(containerId)).thenAnswer(futureAnswer(waitFuture));
        // Start the job
        sut.setGoal(START);
        // Verify that the container is created
        Mockito.verify(docker, Mockito.timeout(30000)).createContainer(containerConfigCaptor.capture(), containerNameCaptor.capture());
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(SupervisorTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(SupervisorTest.JOB).setGoal(START).setState(CREATING).setContainerId(null).setEnv(SupervisorTest.ENV).build()));
        final ContainerConfig containerConfig = containerConfigCaptor.getValue();
        Assert.assertEquals(SupervisorTest.IMAGE, containerConfig.image());
        Assert.assertEquals(SupervisorTest.EXPECTED_CONTAINER_ENV, ImmutableSet.copyOf(containerConfig.env()));
        final String containerName = containerNameCaptor.getValue();
        Assert.assertEquals(SupervisorTest.JOB.getId().toShortString(), shortJobIdFromContainerName(containerName));
        // Verify that the container is started
        Mockito.verify(docker, Mockito.timeout(30000)).startContainer(ArgumentMatchers.eq(containerId));
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(SupervisorTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(SupervisorTest.JOB).setGoal(START).setState(STARTING).setContainerId(containerId).setEnv(SupervisorTest.ENV).build()));
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId))).thenReturn(runningResponse);
        Mockito.verify(docker, Mockito.timeout(30000)).waitContainer(containerId);
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(SupervisorTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(SupervisorTest.JOB).setGoal(START).setState(RUNNING).setContainerId(containerId).setEnv(SupervisorTest.ENV).build()));
        // Stop the job
        sut.setGoal(STOP);
        Mockito.verify(docker, Mockito.timeout(30000)).stopContainer(ArgumentMatchers.eq(containerId), ArgumentMatchers.eq(DEFAULT_SECONDS_TO_WAIT_BEFORE_KILL));
        // Change docker container state to stopped now that it was killed
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId))).thenReturn(stoppedResponse);
        // Verify that the pulling state is signalled
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(SupervisorTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(SupervisorTest.JOB).setGoal(START).setState(PULLING_IMAGE).setContainerId(null).setEnv(SupervisorTest.ENV).build()));
        // Verify that the STOPPING and STOPPED states are signalled
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(SupervisorTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(SupervisorTest.JOB).setGoal(STOP).setState(STOPPING).setContainerId(containerId).setEnv(SupervisorTest.ENV).build()));
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(SupervisorTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(SupervisorTest.JOB).setGoal(STOP).setState(STOPPED).setContainerId(containerId).setEnv(SupervisorTest.ENV).build()));
    }

    @Test
    public void verifySupervisorStopsDockerContainerWithConfiguredKillTime() throws Exception {
        final String containerId = "deadbeef";
        final Job longKillTimeJob = Job.newBuilder().setName(SupervisorTest.NAME).setCommand(SupervisorTest.COMMAND).setImage(SupervisorTest.IMAGE).setVersion(SupervisorTest.VERSION).setSecondsToWaitBeforeKill(30).build();
        mockTaskStatus(longKillTimeJob.getId());
        final Supervisor longKillTimeSupervisor = createSupervisor(longKillTimeJob);
        Mockito.when(docker.createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class))).thenReturn(ContainerCreation.builder().id(containerId).build());
        final ImageInfo imageInfo = Mockito.mock(ImageInfo.class);
        Mockito.when(docker.inspectImage(SupervisorTest.IMAGE)).thenReturn(imageInfo);
        // Have waitContainer wait forever.
        final SettableFuture<ContainerExit> waitFuture = SettableFuture.create();
        Mockito.when(docker.waitContainer(containerId)).thenAnswer(futureAnswer(waitFuture));
        // Start the job (so that a runner exists)
        longKillTimeSupervisor.setGoal(START);
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId))).thenReturn(runningResponse);
        // This is already verified above, but it works as a hack to wait for the model/docker state
        // to converge in such a way that a setGoal(STOP) will work. :|
        Mockito.verify(docker, Mockito.timeout(30000)).waitContainer(containerId);
        // Stop the job
        longKillTimeSupervisor.setGoal(STOP);
        Mockito.verify(docker, Mockito.timeout(30000)).stopContainer(ArgumentMatchers.eq(containerId), ArgumentMatchers.eq(longKillTimeJob.getSecondsToWaitBeforeKill()));
        // Change docker container state to stopped now that it was killed
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId))).thenReturn(stoppedResponse);
    }

    @Test
    public void verifySupervisorRestartsExitedContainer() throws Exception {
        final String containerId1 = "deadbeef1";
        final String containerId2 = "deadbeef2";
        final ContainerCreation createResponse1 = ContainerCreation.builder().id(containerId1).build();
        final ContainerCreation createResponse2 = ContainerCreation.builder().id(containerId2).build();
        Mockito.when(docker.createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class))).thenReturn(createResponse1);
        final ImageInfo imageInfo = Mockito.mock(ImageInfo.class);
        Mockito.when(docker.inspectImage(SupervisorTest.IMAGE)).thenReturn(imageInfo);
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId1))).thenReturn(runningResponse);
        final SettableFuture<ContainerExit> waitFuture1 = SettableFuture.create();
        final SettableFuture<ContainerExit> waitFuture2 = SettableFuture.create();
        Mockito.when(docker.waitContainer(containerId1)).thenAnswer(futureAnswer(waitFuture1));
        Mockito.when(docker.waitContainer(containerId2)).thenAnswer(futureAnswer(waitFuture2));
        // Start the job
        sut.setGoal(START);
        Mockito.verify(docker, Mockito.timeout(30000)).createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class));
        Mockito.verify(docker, Mockito.timeout(30000)).startContainer(ArgumentMatchers.eq(containerId1));
        Mockito.verify(docker, Mockito.timeout(30000)).waitContainer(containerId1);
        // Indicate that the container exited
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId1))).thenReturn(stoppedResponse);
        Mockito.when(docker.createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class))).thenReturn(createResponse2);
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId2))).thenReturn(runningResponse);
        waitFuture1.set(ContainerExit.create(1L));
        // Verify that the container was restarted
        Mockito.verify(docker, Mockito.timeout(30000)).createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class));
        Mockito.verify(docker, Mockito.timeout(30000)).startContainer(ArgumentMatchers.eq(containerId2));
        Mockito.verify(docker, Mockito.timeout(30000)).waitContainer(containerId2);
    }

    @Test
    public void verifyDockerExceptionSetsTaskStatusToFailed() throws Exception {
        verifyExceptionSetsTaskStatusToFailed(new DockerException("FAIL"));
    }

    @Test
    public void verifyRuntimeExceptionSetsTaskStatusToFailed() throws Exception {
        verifyExceptionSetsTaskStatusToFailed(new RuntimeException("FAIL"));
    }

    /**
     * Verifies a fix for a NPE that is thrown when the Supervisor receives a goal of UNDEPLOY for a
     * job with gracePeriod that has never been STARTed.
     */
    @Test
    public void verifySupervisorHandlesUndeployingOfNotRunningContainerWithGracePeriod() throws Exception {
        final int gracePeriod = 5;
        final Job job = SupervisorTest.JOB.toBuilder().setGracePeriod(gracePeriod).build();
        final Supervisor sut = createSupervisor(job);
        sut.setGoal(UNDEPLOY);
        // when the NPE was thrown, the model was never updated
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(job.getId()), ArgumentMatchers.argThat(Matchers.is(SupervisorTest.taskStatusWithState(TaskStatus.State.STOPPING))));
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(job.getId()), ArgumentMatchers.argThat(Matchers.is(SupervisorTest.taskStatusWithState(TaskStatus.State.STOPPED))));
        Mockito.verify(sleeper, Mockito.never()).sleep((gracePeriod * 1000));
    }
}

