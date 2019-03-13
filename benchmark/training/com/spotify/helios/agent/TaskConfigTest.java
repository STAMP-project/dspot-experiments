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


import EndpointHealthCheck.HTTP;
import EndpointHealthCheck.TCP;
import ServiceRegistration.Endpoint;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.helios.common.descriptors.HealthCheck;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.PortMapping;
import com.spotify.helios.common.descriptors.ServiceEndpoint;
import com.spotify.helios.common.descriptors.ServicePorts;
import com.spotify.helios.serviceregistration.ServiceRegistration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TaskConfigTest {
    private static final String HOST = "HOST";

    private static final String IMAGE = "spotify:17";

    private static final String PORT_NAME = "default-port";

    private static final int EXTERNAL_PORT = 20000;

    private static final Set<String> CAP_ADDS = ImmutableSet.of("cap1", "cap2");

    private static final Set<String> CAP_DROPS = ImmutableSet.of("cap3", "cap4");

    private static final Map<String, String> LABELS = ImmutableMap.of("label", "value");

    private static final Job JOB = Job.newBuilder().setName("foobar").setCommand(Arrays.asList("foo", "bar")).setImage(TaskConfigTest.IMAGE).setVersion("4711").addPort(TaskConfigTest.PORT_NAME, PortMapping.of(8080, TaskConfigTest.EXTERNAL_PORT)).addRegistration(ServiceEndpoint.of("service", "http"), ServicePorts.of(TaskConfigTest.PORT_NAME)).setAddCapabilities(TaskConfigTest.CAP_ADDS).setDropCapabilities(TaskConfigTest.CAP_DROPS).setLabels(TaskConfigTest.LABELS).build();

    @Test
    public void testRegistrationWithHttpHealthCheck() throws Exception {
        final String path = "/health";
        final Job job = TaskConfigTest.JOB.toBuilder().setHealthCheck(HealthCheck.newHttpHealthCheck().setPath(path).setPort(TaskConfigTest.PORT_NAME).build()).build();
        final TaskConfig taskConfig = TaskConfig.builder().namespace("test").host(TaskConfigTest.HOST).job(job).build();
        final ServiceRegistration.Endpoint endpoint = taskConfig.registration().getEndpoints().get(0);
        Assert.assertEquals(path, endpoint.getHealthCheck().getPath());
        Assert.assertEquals(HTTP, endpoint.getHealthCheck().getType());
        Assert.assertEquals(TaskConfigTest.EXTERNAL_PORT, endpoint.getPort());
    }

    @Test
    public void testRegistrationWithTcpHealthCheck() throws Exception {
        final Job job = TaskConfigTest.JOB.toBuilder().setHealthCheck(HealthCheck.newTcpHealthCheck().setPort(TaskConfigTest.PORT_NAME).build()).build();
        final TaskConfig taskConfig = TaskConfig.builder().namespace("test").host(TaskConfigTest.HOST).job(job).build();
        final ServiceRegistration.Endpoint endpoint = taskConfig.registration().getEndpoints().get(0);
        Assert.assertEquals(TCP, endpoint.getHealthCheck().getType());
        Assert.assertEquals(TaskConfigTest.EXTERNAL_PORT, endpoint.getPort());
    }

    @Test
    public void testRegistrationWithoutHealthCheck() throws Exception {
        final TaskConfig taskConfig = TaskConfig.builder().namespace("test").host(TaskConfigTest.HOST).job(TaskConfigTest.JOB).build();
        final ServiceRegistration.Endpoint endpoint = taskConfig.registration().getEndpoints().get(0);
        Assert.assertNull(endpoint.getHealthCheck());
    }

    @Test
    public void testHostConfig() throws Exception {
        final TaskConfig taskConfig = TaskConfig.builder().namespace("test").host(TaskConfigTest.HOST).job(TaskConfigTest.JOB).build();
        final HostConfig hostConfig = taskConfig.hostConfig(Optional.absent());
        Assert.assertThat(ImmutableSet.copyOf(hostConfig.capAdd()), CoreMatchers.equalTo(TaskConfigTest.CAP_ADDS));
        Assert.assertThat(ImmutableSet.copyOf(hostConfig.capDrop()), CoreMatchers.equalTo(TaskConfigTest.CAP_DROPS));
    }

    @Test
    public void testRuntimeHostConfig() throws Exception {
        Job nvidiaRuntimeJob = TaskConfigTest.JOB.toBuilder().setRuntime("nvidia").build();
        final TaskConfig taskConfig = TaskConfig.builder().namespace("test").host(TaskConfigTest.HOST).job(nvidiaRuntimeJob).build();
        final HostConfig hostConfig = taskConfig.hostConfig(Optional.absent());
        Assert.assertThat(hostConfig.runtime(), CoreMatchers.equalTo("nvidia"));
    }

    @Test
    public void testContainerConfig() throws Exception {
        final TaskConfig taskConfig = TaskConfig.builder().namespace("test").host(TaskConfigTest.HOST).job(TaskConfigTest.JOB).build();
        final ImageInfo imageInfo = Mockito.mock(ImageInfo.class);
        final ContainerConfig containerConfig = taskConfig.containerConfig(imageInfo, Optional.absent());
        Assert.assertThat(ImmutableMap.copyOf(containerConfig.labels()), CoreMatchers.equalTo(TaskConfigTest.LABELS));
    }
}

