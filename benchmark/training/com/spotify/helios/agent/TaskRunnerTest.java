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


import TaskRunner.NopListener;
import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerTimeoutException;
import com.spotify.docker.client.exceptions.ImageNotFoundException;
import com.spotify.docker.client.exceptions.ImagePullFailedException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ContainerState;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.helios.common.Clock;
import com.spotify.helios.common.HeliosRuntimeException;
import com.spotify.helios.common.descriptors.Job;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TaskRunnerTest {
    private static final String IMAGE = "spotify:17";

    private static final Job JOB = Job.newBuilder().setName("foobar").setCommand(Arrays.asList("foo", "bar")).setImage(TaskRunnerTest.IMAGE).setVersion("4711").build();

    private static final String HOST = "HOST";

    @Mock
    private DockerClient mockDocker;

    @Mock
    private StatusUpdater statusUpdater;

    @Mock
    private Clock clock;

    @Mock
    private ContainerDecorator containerDecorator;

    @Test
    public void test() throws Throwable {
        final TaskRunner tr = TaskRunner.builder().delayMillis(0).config(TaskConfig.builder().namespace("test").host(TaskRunnerTest.HOST).job(TaskRunnerTest.JOB).containerDecorators(ImmutableList.of(containerDecorator)).build()).docker(mockDocker).listener(new TaskRunner.NopListener()).build();
        tr.run();
        try {
            tr.resultFuture().get();
            Assert.fail("this should throw");
        } catch (Exception t) {
            Assert.assertTrue((t instanceof ExecutionException));
            Assert.assertEquals(HeliosRuntimeException.class, t.getCause().getClass());
        }
    }

    @Test
    public void testPullsAreSerializedWithOldDocker() throws Throwable {
        Assert.assertFalse("concurrent calls to docker.pull with a version where it causes issues", arePullsConcurrent("1.6.2"));
    }

    @Test
    public void testPullsAreConcurrentWithNewerDocker() throws Throwable {
        Assert.assertTrue("calls to docker.pull were unnecessarily serialized", arePullsConcurrent("1.9.0-rc1"));
    }

    @Test
    public void testPullTimeoutVariation() throws Throwable {
        Mockito.doThrow(new DockerTimeoutException("x", new URI("http://example.com"), null)).when(mockDocker).pull(TaskRunnerTest.IMAGE);
        Mockito.doThrow(new ImageNotFoundException("not found")).when(mockDocker).inspectImage(TaskRunnerTest.IMAGE);
        final TaskRunner tr = TaskRunner.builder().delayMillis(0).config(TaskConfig.builder().namespace("test").host(TaskRunnerTest.HOST).job(TaskRunnerTest.JOB).containerDecorators(ImmutableList.of(containerDecorator)).build()).docker(mockDocker).listener(new TaskRunner.NopListener()).build();
        tr.run();
        try {
            tr.resultFuture().get();
            Assert.fail("this should throw");
        } catch (Exception t) {
            Assert.assertTrue((t instanceof ExecutionException));
            Assert.assertEquals(ImagePullFailedException.class, t.getCause().getClass());
        }
    }

    @Test
    public void testContainerNotRunningVariation() throws Throwable {
        final TaskRunner.NopListener mockListener = Mockito.mock(NopListener.class);
        final ImageInfo mockImageInfo = Mockito.mock(ImageInfo.class);
        final ContainerCreation mockCreation = Mockito.mock(ContainerCreation.class);
        final HealthChecker mockHealthChecker = Mockito.mock(HealthChecker.class);
        final ContainerState stoppedState = Mockito.mock(ContainerState.class);
        Mockito.when(stoppedState.running()).thenReturn(false);
        Mockito.when(stoppedState.error()).thenReturn("container is a potato");
        final ContainerInfo stopped = Mockito.mock(ContainerInfo.class);
        Mockito.when(stopped.state()).thenReturn(stoppedState);
        Mockito.when(mockCreation.id()).thenReturn("potato");
        Mockito.when(mockDocker.inspectContainer(ArgumentMatchers.anyString())).thenReturn(stopped);
        Mockito.when(mockDocker.inspectImage(TaskRunnerTest.IMAGE)).thenReturn(mockImageInfo);
        Mockito.when(mockDocker.createContainer(ArgumentMatchers.any(ContainerConfig.class), ArgumentMatchers.anyString())).thenReturn(mockCreation);
        Mockito.when(mockHealthChecker.check(ArgumentMatchers.anyString())).thenReturn(false);
        final TaskRunner tr = TaskRunner.builder().delayMillis(0).config(TaskConfig.builder().namespace("test").host(TaskRunnerTest.HOST).job(TaskRunnerTest.JOB).containerDecorators(ImmutableList.of(containerDecorator)).build()).docker(mockDocker).listener(mockListener).healthChecker(mockHealthChecker).build();
        tr.run();
        try {
            tr.resultFuture().get();
            Assert.fail("this should throw");
        } catch (Exception t) {
            Assert.assertTrue((t instanceof ExecutionException));
            Assert.assertEquals(RuntimeException.class, t.getCause().getClass());
            Mockito.verify(mockListener).failed(t.getCause(), "container is a potato");
        }
    }
}

