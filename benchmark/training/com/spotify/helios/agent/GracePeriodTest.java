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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.SettableFuture;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerExit;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.helios.TemporaryPorts;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.PortMapping;
import com.spotify.helios.common.descriptors.ServiceEndpoint;
import com.spotify.helios.common.descriptors.ServicePorts;
import com.spotify.helios.common.descriptors.TaskStatus;
import com.spotify.helios.serviceregistration.ServiceRegistrar;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
public class GracePeriodTest {
    final ExecutorService executor = Executors.newCachedThreadPool();

    static final TemporaryPorts TEMPORARY_PORTS = TemporaryPorts.create();

    static final String NAMESPACE = "helios-deadbeef";

    static final String REPOSITORY = "spotify";

    static final String TAG = "17";

    static final String IMAGE = ((GracePeriodTest.REPOSITORY) + ":") + (GracePeriodTest.TAG);

    static final String NAME = "foobar";

    static final List<String> COMMAND = Arrays.asList("foo", "bar");

    static final Integer EXTERNAL_PORT = GracePeriodTest.TEMPORARY_PORTS.localPort("external");

    static final Map<String, PortMapping> PORTS = ImmutableMap.of("bar", PortMapping.of(5000, GracePeriodTest.EXTERNAL_PORT));

    static final Map<ServiceEndpoint, ServicePorts> REGISTRATION = ImmutableMap.of(ServiceEndpoint.of("foo-service", "tcp"), ServicePorts.of("foo"), ServiceEndpoint.of("bar-service", "http"), ServicePorts.of("bar"));

    static final String VERSION = "4711";

    static final Integer GRACE_PERIOD = 60;

    static final long GRACE_PERIOD_MILLIS = TimeUnit.MILLISECONDS.convert(GracePeriodTest.GRACE_PERIOD, TimeUnit.SECONDS);

    static final Job JOB = Job.newBuilder().setName(GracePeriodTest.NAME).setCommand(GracePeriodTest.COMMAND).setImage(GracePeriodTest.IMAGE).setPorts(GracePeriodTest.PORTS).setRegistration(GracePeriodTest.REGISTRATION).setVersion(GracePeriodTest.VERSION).setGracePeriod(GracePeriodTest.GRACE_PERIOD).build();

    static final Map<String, String> ENV = ImmutableMap.of("foo", "17", "bar", "4711");

    static final Set<String> EXPECTED_CONTAINER_ENV = ImmutableSet.of("foo=17", "bar=4711");

    public final ContainerInfo runningResponse = Mockito.mock(ContainerInfo.class);

    public final ContainerInfo stoppedResponse = Mockito.mock(ContainerInfo.class);

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

    Supervisor sut;

    @Test
    public void verifySupervisorStartsAndStopsDockerContainer() throws Exception {
        final String containerId = "deadbeef";
        final ContainerCreation createResponse = ContainerCreation.builder().id(containerId).build();
        final SettableFuture<ContainerCreation> createFuture = SettableFuture.create();
        Mockito.when(docker.createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.any(String.class))).thenAnswer(futureAnswer(createFuture));
        final SettableFuture<Void> startFuture = SettableFuture.create();
        Mockito.doAnswer(futureAnswer(startFuture)).when(docker).startContainer(ArgumentMatchers.eq(containerId));
        final ImageInfo imageInfo = Mockito.mock(ImageInfo.class);
        Mockito.when(docker.inspectImage(GracePeriodTest.IMAGE)).thenReturn(imageInfo);
        final SettableFuture<ContainerExit> waitFuture = SettableFuture.create();
        Mockito.when(docker.waitContainer(containerId)).thenAnswer(futureAnswer(waitFuture));
        // Start the job
        sut.setGoal(START);
        // Verify that the pulling state is signalled
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(GracePeriodTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(GracePeriodTest.JOB).setGoal(START).setState(PULLING_IMAGE).setPorts(GracePeriodTest.PORTS).setContainerId(null).setEnv(GracePeriodTest.ENV).build()));
        // Verify that the container is created
        Mockito.verify(docker, Mockito.timeout(30000)).createContainer(containerConfigCaptor.capture(), containerNameCaptor.capture());
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(GracePeriodTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(GracePeriodTest.JOB).setGoal(START).setState(CREATING).setPorts(GracePeriodTest.PORTS).setContainerId(null).setEnv(GracePeriodTest.ENV).build()));
        createFuture.set(createResponse);
        final ContainerConfig containerConfig = containerConfigCaptor.getValue();
        Assert.assertEquals(GracePeriodTest.IMAGE, containerConfig.image());
        Assert.assertEquals(GracePeriodTest.EXPECTED_CONTAINER_ENV, ImmutableSet.copyOf(containerConfig.env()));
        final String containerName = containerNameCaptor.getValue();
        Assert.assertEquals(GracePeriodTest.JOB.getId().toShortString(), shortJobIdFromContainerName(containerName));
        // Verify that the container is started
        Mockito.verify(docker, Mockito.timeout(30000)).startContainer(ArgumentMatchers.eq(containerId));
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(GracePeriodTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(GracePeriodTest.JOB).setGoal(START).setState(STARTING).setPorts(GracePeriodTest.PORTS).setContainerId(containerId).setEnv(GracePeriodTest.ENV).build()));
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId))).thenReturn(runningResponse);
        startFuture.set(null);
        Mockito.verify(docker, Mockito.timeout(30000)).waitContainer(containerId);
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(GracePeriodTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(GracePeriodTest.JOB).setGoal(START).setState(RUNNING).setPorts(GracePeriodTest.PORTS).setContainerId(containerId).setEnv(GracePeriodTest.ENV).build()));
        // Stop the job
        final SettableFuture<Void> killFuture = SettableFuture.create();
        Mockito.doAnswer(futureAnswer(killFuture)).when(docker).killContainer(ArgumentMatchers.eq(containerId));
        executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // TODO (dano): Make Supervisor.stop() asynchronous
                sut.setGoal(STOP);
                return null;
            }
        });
        // Stop the container
        Mockito.verify(docker, Mockito.timeout(30000)).killContainer(ArgumentMatchers.eq(containerId));
        // Verify that Sleeper has been called and that datetime has increased by
        // GRACE_PERIOD number of milliseconds
        Mockito.verify(sleeper).sleep(GracePeriodTest.GRACE_PERIOD_MILLIS);
        // Change docker container state to stopped when it's killed
        Mockito.when(docker.inspectContainer(ArgumentMatchers.eq(containerId))).thenReturn(stoppedResponse);
        killFuture.set(null);
        // Verify that the stopping state is signalled
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(GracePeriodTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(GracePeriodTest.JOB).setGoal(STOP).setState(STOPPING).setPorts(GracePeriodTest.PORTS).setContainerId(containerId).setEnv(GracePeriodTest.ENV).build()));
        // Verify that the stopped state is signalled
        Mockito.verify(model, Mockito.timeout(30000)).setTaskStatus(ArgumentMatchers.eq(GracePeriodTest.JOB.getId()), ArgumentMatchers.eq(TaskStatus.newBuilder().setJob(GracePeriodTest.JOB).setGoal(STOP).setState(STOPPED).setPorts(GracePeriodTest.PORTS).setContainerId(containerId).setEnv(GracePeriodTest.ENV).build()));
    }
}

