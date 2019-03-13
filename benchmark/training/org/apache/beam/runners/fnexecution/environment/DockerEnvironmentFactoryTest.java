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
package org.apache.beam.runners.fnexecution.environment;


import org.apache.beam.model.pipeline.v1.Endpoints.ApiServiceDescriptor;
import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.runners.core.construction.Environments;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.artifact.ArtifactRetrievalService;
import org.apache.beam.runners.fnexecution.control.FnApiControlClientPoolService;
import org.apache.beam.runners.fnexecution.control.InstructionRequestHandler;
import org.apache.beam.runners.fnexecution.logging.GrpcLoggingService;
import org.apache.beam.runners.fnexecution.provisioning.StaticGrpcProvisionService;
import org.apache.beam.sdk.fn.IdGenerator;
import org.apache.beam.sdk.fn.IdGenerators;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link DockerEnvironmentFactory}.
 */
@RunWith(JUnit4.class)
public class DockerEnvironmentFactoryTest {
    private static final ApiServiceDescriptor SERVICE_DESCRIPTOR = ApiServiceDescriptor.newBuilder().setUrl("service-url").build();

    private static final String IMAGE_NAME = "my-image";

    private static final Environment ENVIRONMENT = Environments.createDockerEnvironment(DockerEnvironmentFactoryTest.IMAGE_NAME);

    private static final String CONTAINER_ID = "e4485f0f2b813b63470feacba5fe9cb89699878c095df4124abd320fd5401385";

    private static final IdGenerator ID_GENERATOR = IdGenerators.incrementingLongs();

    @Mock
    private DockerCommand docker;

    @Mock
    private GrpcFnServer<FnApiControlClientPoolService> controlServiceServer;

    @Mock
    private GrpcFnServer<GrpcLoggingService> loggingServiceServer;

    @Mock
    private GrpcFnServer<ArtifactRetrievalService> retrievalServiceServer;

    @Mock
    private GrpcFnServer<StaticGrpcProvisionService> provisioningServiceServer;

    @Mock
    private InstructionRequestHandler client;

    private DockerEnvironmentFactory factory;

    @Test
    public void createsCorrectEnvironment() throws Exception {
        Mockito.when(docker.runImage(Mockito.eq(DockerEnvironmentFactoryTest.IMAGE_NAME), Mockito.any(), Mockito.any())).thenReturn(DockerEnvironmentFactoryTest.CONTAINER_ID);
        Mockito.when(docker.isContainerRunning(Mockito.eq(DockerEnvironmentFactoryTest.CONTAINER_ID))).thenReturn(true);
        RemoteEnvironment handle = factory.createEnvironment(DockerEnvironmentFactoryTest.ENVIRONMENT);
        Assert.assertThat(handle.getInstructionRequestHandler(), Matchers.is(client));
        Assert.assertThat(handle.getEnvironment(), Matchers.equalTo(DockerEnvironmentFactoryTest.ENVIRONMENT));
    }

    @Test
    public void destroysCorrectContainer() throws Exception {
        Mockito.when(docker.runImage(Mockito.eq(DockerEnvironmentFactoryTest.IMAGE_NAME), Mockito.any(), Mockito.any())).thenReturn(DockerEnvironmentFactoryTest.CONTAINER_ID);
        Mockito.when(docker.isContainerRunning(Mockito.eq(DockerEnvironmentFactoryTest.CONTAINER_ID))).thenReturn(true);
        RemoteEnvironment handle = factory.createEnvironment(DockerEnvironmentFactoryTest.ENVIRONMENT);
        handle.close();
        Mockito.verify(docker).killContainer(DockerEnvironmentFactoryTest.CONTAINER_ID);
    }

    @Test
    public void createsMultipleEnvironments() throws Exception {
        Mockito.when(docker.isContainerRunning(ArgumentMatchers.anyString())).thenReturn(true);
        Environment fooEnv = Environments.createDockerEnvironment("foo");
        RemoteEnvironment fooHandle = factory.createEnvironment(fooEnv);
        Assert.assertThat(fooHandle.getEnvironment(), Matchers.is(Matchers.equalTo(fooEnv)));
        Environment barEnv = Environments.createDockerEnvironment("bar");
        RemoteEnvironment barHandle = factory.createEnvironment(barEnv);
        Assert.assertThat(barHandle.getEnvironment(), Matchers.is(Matchers.equalTo(barEnv)));
    }
}

