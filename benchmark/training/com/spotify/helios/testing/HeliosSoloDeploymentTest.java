/**
 * -
 * -\-\-
 * Helios Testing Library
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
package com.spotify.helios.testing;


import Goal.START;
import HeliosSoloDeployment.PROBE_IMAGE;
import Status.UP;
import TaskStatus.State.RUNNING;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.docker.client.shaded.com.google.common.collect.ImmutableList;
import com.spotify.docker.client.shaded.com.google.common.collect.ImmutableMap;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.Deployment;
import com.spotify.helios.common.descriptors.HostStatus;
import com.spotify.helios.common.descriptors.HostStatus.Status;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.TaskStatus;
import com.spotify.helios.common.protocol.JobUndeployResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static HeliosSoloDeployment.PROBE_IMAGE;


public class HeliosSoloDeploymentTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String CONTAINER_ID = "abc123";

    private static final String HOST1 = "host1";

    private static final String HOST2 = "host2";

    private static final Job JOB1 = Job.newBuilder().setCommand(ImmutableList.of("BOGUS1")).setImage("IMAGE").setName("NAME").setVersion("VERSION").build();

    private static final Job JOB2 = Job.newBuilder().setCommand(ImmutableList.of("BOGUS2")).setImage("IMAGE").setName("NAME").setVersion("VERSION").build();

    private static final JobId JOB_ID1 = HeliosSoloDeploymentTest.JOB1.getId();

    private static final JobId JOB_ID2 = HeliosSoloDeploymentTest.JOB2.getId();

    private static final TaskStatus TASK_STATUS1 = TaskStatus.newBuilder().setJob(HeliosSoloDeploymentTest.JOB1).setGoal(START).setState(RUNNING).setContainerId(HeliosSoloDeploymentTest.CONTAINER_ID).build();

    private static final TaskStatus TASK_STATUS2 = TaskStatus.newBuilder().setJob(HeliosSoloDeploymentTest.JOB2).setGoal(START).setState(RUNNING).build();

    private DockerClient dockerClient;

    private HeliosClient heliosClient;

    private ArgumentCaptor<ContainerConfig> containerConfig;

    @Test
    public void testDockerHostContainsLocalhost() throws Exception {
        buildHeliosSoloDeployment();
        boolean foundSolo = false;
        for (final ContainerConfig cc : containerConfig.getAllValues()) {
            if (cc.image().contains("helios-solo")) {
                Assert.assertThat(cc.hostConfig().binds(), Matchers.hasItem("/var/run/docker.sock:/var/run/docker.sock"));
                foundSolo = true;
            }
        }
        Assert.assertTrue("Could not find helios-solo container creation", foundSolo);
    }

    @Test
    public void testConfig() throws Exception {
        final String image = "helios-test";
        final String ns = "namespace";
        final String env = "stuff";
        final Config config = ConfigFactory.empty().withValue("helios.solo.profile", ConfigValueFactory.fromAnyRef("test")).withValue("helios.solo.profiles.test.image", ConfigValueFactory.fromAnyRef(image)).withValue("helios.solo.profiles.test.namespace", ConfigValueFactory.fromAnyRef(ns)).withValue("helios.solo.profiles.test.env.TEST", ConfigValueFactory.fromAnyRef(env));
        final HeliosSoloDeployment deployment = buildHeliosSoloDeployment(new HeliosSoloDeployment.Builder(null, config));
        Assert.assertEquals((ns + ".solo.local"), deployment.agentName());
        boolean foundSolo = false;
        for (final ContainerConfig cc : containerConfig.getAllValues()) {
            if (cc.image().contains(image)) {
                foundSolo = true;
                Assert.assertThat(cc.env(), Matchers.hasItem(("TEST=" + env)));
                Assert.assertThat(cc.env(), Matchers.hasItem((("HELIOS_NAME=" + ns) + ".solo.local")));
            }
        }
        Assert.assertTrue("Could not find helios-solo container creation", foundSolo);
    }

    @Test
    public void testConfigHasGoogleContainerRegistryCredentials() throws Exception {
        // generate a file that we will pretend contains GCR credentials, in order to verify that
        // HeliosSoloDeployment sets up the expected environment variables and volume binds
        // when this config value exists (and is a real file)
        final File credentialsFile = temporaryFolder.newFile("fake-credentials");
        final String credentialsPath = credentialsFile.getPath();
        final String image = "helios-test";
        final Config config = ConfigFactory.empty().withValue("helios.solo.profile", ConfigValueFactory.fromAnyRef("test")).withValue("helios.solo.profiles.test.image", ConfigValueFactory.fromAnyRef(image)).withValue("helios.solo.profiles.test.google-container-registry.credentials", ConfigValueFactory.fromAnyRef(credentialsPath));
        buildHeliosSoloDeployment(new HeliosSoloDeployment.Builder(null, config));
        ContainerConfig soloContainerConfig = null;
        for (final ContainerConfig cc : containerConfig.getAllValues()) {
            if (cc.image().contains(image)) {
                soloContainerConfig = cc;
            }
        }
        Assert.assertNotNull("Could not find helios-solo container creation", soloContainerConfig);
        Assert.assertThat(soloContainerConfig.env(), Matchers.hasItem(("HELIOS_AGENT_OPTS=--docker-gcp-account-credentials=" + credentialsPath)));
        final String credentialsParentPath = credentialsFile.getParent();
        Assert.assertThat(soloContainerConfig.hostConfig().binds(), Matchers.hasItem((((credentialsParentPath + ":") + credentialsParentPath) + ":ro")));
    }

    @Test
    public void testGoogleContainerRegistryCredentialsDoesntExist() throws Exception {
        final String image = "helios-test";
        final Config config = ConfigFactory.empty().withValue("helios.solo.profile", ConfigValueFactory.fromAnyRef("test")).withValue("helios.solo.profiles.test.image", ConfigValueFactory.fromAnyRef(image)).withValue("helios.solo.profiles.test.google-container-registry.credentials", ConfigValueFactory.fromAnyRef("/dev/null/foo/bar"));
        buildHeliosSoloDeployment(new HeliosSoloDeployment.Builder(null, config));
        ContainerConfig soloContainerConfig = null;
        for (final ContainerConfig cc : containerConfig.getAllValues()) {
            if (cc.image().contains(image)) {
                soloContainerConfig = cc;
            }
        }
        Assert.assertNotNull("Could not find helios-solo container creation", soloContainerConfig);
        Assert.assertThat(soloContainerConfig.env(), Matchers.not(Matchers.hasItem(Matchers.startsWith("HELIOS_AGENT_OPTS="))));
        Assert.assertThat(soloContainerConfig.hostConfig().binds(), Matchers.not(Matchers.hasItem(Matchers.startsWith("/dev/null"))));
    }

    @Test
    public void testDoesNotPullPresentProbeImage() throws Exception {
        Mockito.when(this.dockerClient.inspectImage(PROBE_IMAGE)).thenReturn(Mockito.mock(ImageInfo.class));
        buildHeliosSoloDeployment();
        Mockito.verify(this.dockerClient, Mockito.never()).pull(PROBE_IMAGE);
    }

    @Test
    public void testDoesPullAbsentProbeImage() throws Exception {
        Mockito.when(this.dockerClient.inspectImage(PROBE_IMAGE)).thenThrow(new com.spotify.docker.client.exceptions.ImageNotFoundException(PROBE_IMAGE));
        buildHeliosSoloDeployment();
        Mockito.verify(this.dockerClient).pull(PROBE_IMAGE);
    }

    @Test
    public void testUndeployLeftoverJobs() throws Exception {
        final HeliosSoloDeployment solo = buildHeliosSoloDeployment();
        final ListenableFuture<List<String>> hostsFuture = Futures.<List<String>>immediateFuture(ImmutableList.of(HeliosSoloDeploymentTest.HOST1, HeliosSoloDeploymentTest.HOST2));
        Mockito.when(heliosClient.listHosts()).thenReturn(hostsFuture);
        // These futures represent HostStatuses when the job is still deployed
        final ListenableFuture<HostStatus> statusFuture11 = Futures.immediateFuture(HostStatus.newBuilder().setStatus(UP).setStatuses(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID1, HeliosSoloDeploymentTest.TASK_STATUS1)).setJobs(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID1, Deployment.of(HeliosSoloDeploymentTest.JOB_ID1, START))).build());
        final ListenableFuture<HostStatus> statusFuture21 = Futures.immediateFuture(HostStatus.newBuilder().setStatus(UP).setStatuses(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID2, HeliosSoloDeploymentTest.TASK_STATUS2)).setJobs(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID2, Deployment.of(HeliosSoloDeploymentTest.JOB_ID2, START))).build());
        // These futures represent HostStatuses when the job is undeployed
        final ListenableFuture<HostStatus> statusFuture12 = Futures.immediateFuture(HostStatus.newBuilder().setStatus(UP).setStatuses(Collections.<JobId, TaskStatus>emptyMap()).setJobs(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID1, Deployment.of(HeliosSoloDeploymentTest.JOB_ID1, START))).build());
        final ListenableFuture<HostStatus> statusFuture22 = Futures.immediateFuture(HostStatus.newBuilder().setStatus(UP).setStatuses(Collections.<JobId, TaskStatus>emptyMap()).setJobs(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID2, Deployment.of(HeliosSoloDeploymentTest.JOB_ID2, START))).build());
        // noinspection unchecked
        Mockito.when(heliosClient.hostStatus(HeliosSoloDeploymentTest.HOST1)).thenReturn(statusFuture11);
        // noinspection unchecked
        Mockito.when(heliosClient.hostStatus(HeliosSoloDeploymentTest.HOST2)).thenReturn(statusFuture21);
        final ListenableFuture<JobUndeployResponse> undeployFuture1 = Futures.immediateFuture(new JobUndeployResponse(Status.OK, HeliosSoloDeploymentTest.HOST1, HeliosSoloDeploymentTest.JOB_ID1));
        final ListenableFuture<JobUndeployResponse> undeployFuture2 = Futures.immediateFuture(new JobUndeployResponse(Status.OK, HeliosSoloDeploymentTest.HOST2, HeliosSoloDeploymentTest.JOB_ID2));
        // when undeploy is called, respond correctly & patch the mock to return
        // the undeployed HostStatus
        Mockito.when(heliosClient.undeploy(HeliosSoloDeploymentTest.JOB_ID1, HeliosSoloDeploymentTest.HOST1)).thenAnswer(new Answer<ListenableFuture<JobUndeployResponse>>() {
            @Override
            public ListenableFuture<JobUndeployResponse> answer(final InvocationOnMock invocation) throws Throwable {
                Mockito.when(heliosClient.hostStatus(HeliosSoloDeploymentTest.HOST1)).thenReturn(statusFuture12);
                return undeployFuture1;
            }
        });
        Mockito.when(heliosClient.undeploy(HeliosSoloDeploymentTest.JOB_ID2, HeliosSoloDeploymentTest.HOST2)).thenAnswer(new Answer<ListenableFuture<JobUndeployResponse>>() {
            @Override
            public ListenableFuture<JobUndeployResponse> answer(final InvocationOnMock invocation) throws Throwable {
                Mockito.when(heliosClient.hostStatus(HeliosSoloDeploymentTest.HOST1)).thenReturn(statusFuture22);
                return undeployFuture2;
            }
        });
        solo.undeployLeftoverJobs();
        Mockito.verify(heliosClient).undeploy(HeliosSoloDeploymentTest.JOB_ID1, HeliosSoloDeploymentTest.HOST1);
        Mockito.verify(heliosClient).undeploy(HeliosSoloDeploymentTest.JOB_ID2, HeliosSoloDeploymentTest.HOST2);
    }

    @Test
    public void testUndeployLeftoverJobs_noLeftoverJobs() throws Exception {
        final HeliosSoloDeployment solo = buildHeliosSoloDeployment();
        final ListenableFuture<Map<JobId, Job>> jobsFuture = Futures.immediateFuture(Collections.<JobId, Job>emptyMap());
        Mockito.when(heliosClient.jobs()).thenReturn(jobsFuture);
        solo.undeployLeftoverJobs();
        // There should be no more calls to any HeliosClient methods.
        Mockito.verify(heliosClient, Mockito.never()).jobStatus(org.mockito.Matchers.any(JobId.class));
    }

    @Test
    public void testLogService() throws Exception {
        final InMemoryLogStreamFollower logStreamProvider = InMemoryLogStreamFollower.create();
        final HeliosSoloLogService logService = new HeliosSoloLogService(heliosClient, dockerClient, logStreamProvider);
        final ListenableFuture<List<String>> hostsFuture = Futures.<List<String>>immediateFuture(ImmutableList.of(HeliosSoloDeploymentTest.HOST1));
        Mockito.when(heliosClient.listHosts()).thenReturn(hostsFuture);
        final ListenableFuture<HostStatus> statusFuture = Futures.immediateFuture(HostStatus.newBuilder().setStatus(UP).setStatuses(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID1, HeliosSoloDeploymentTest.TASK_STATUS1)).setJobs(ImmutableMap.of(HeliosSoloDeploymentTest.JOB_ID1, Deployment.of(HeliosSoloDeploymentTest.JOB_ID1, START))).build());
        Mockito.when(heliosClient.hostStatus(HeliosSoloDeploymentTest.HOST1)).thenReturn(statusFuture);
        Mockito.when(dockerClient.logs(ArgumentMatchers.anyString(), org.mockito.Matchers.<DockerClient.LogsParam>anyVararg())).thenReturn(Mockito.mock(LogStream.class));
        logService.runOneIteration();
        Mockito.verify(dockerClient, Mockito.timeout(5000)).logs(ArgumentMatchers.eq(HeliosSoloDeploymentTest.CONTAINER_ID), org.mockito.Matchers.<DockerClient.LogsParam>anyVararg());
    }
}

