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


import java.util.Collections;
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
 * Tests for {@link ProcessEnvironmentFactory}.
 */
@RunWith(JUnit4.class)
public class ProcessEnvironmentFactoryTest {
    private static final ApiServiceDescriptor SERVICE_DESCRIPTOR = ApiServiceDescriptor.newBuilder().setUrl("service-url").build();

    private static final String COMMAND = "my-command";

    private static final Environment ENVIRONMENT = Environments.createProcessEnvironment("", "", ProcessEnvironmentFactoryTest.COMMAND, Collections.emptyMap());

    private static final ProcessEnvironmentFactoryTest.InspectibleIdGenerator ID_GENERATOR = new ProcessEnvironmentFactoryTest.InspectibleIdGenerator();

    @Mock
    private ProcessManager processManager;

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

    private ProcessEnvironmentFactory factory;

    @Test
    public void createsCorrectEnvironment() throws Exception {
        RemoteEnvironment handle = factory.createEnvironment(ProcessEnvironmentFactoryTest.ENVIRONMENT);
        Assert.assertThat(handle.getInstructionRequestHandler(), Matchers.is(client));
        Assert.assertThat(handle.getEnvironment(), Matchers.equalTo(ProcessEnvironmentFactoryTest.ENVIRONMENT));
        Mockito.verify(processManager).startProcess(ArgumentMatchers.eq(ProcessEnvironmentFactoryTest.ID_GENERATOR.currentId), ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.anyMap());
    }

    @Test
    public void destroysCorrectContainer() throws Exception {
        RemoteEnvironment handle = factory.createEnvironment(ProcessEnvironmentFactoryTest.ENVIRONMENT);
        handle.close();
        Mockito.verify(processManager).stopProcess(ProcessEnvironmentFactoryTest.ID_GENERATOR.currentId);
    }

    @Test
    public void createsMultipleEnvironments() throws Exception {
        Environment fooEnv = Environments.createProcessEnvironment("", "", "foo", Collections.emptyMap());
        RemoteEnvironment fooHandle = factory.createEnvironment(fooEnv);
        Assert.assertThat(fooHandle.getEnvironment(), Matchers.is(Matchers.equalTo(fooEnv)));
        Environment barEnv = Environments.createProcessEnvironment("", "", "bar", Collections.emptyMap());
        RemoteEnvironment barHandle = factory.createEnvironment(barEnv);
        Assert.assertThat(barHandle.getEnvironment(), Matchers.is(Matchers.equalTo(barEnv)));
    }

    private static class InspectibleIdGenerator implements IdGenerator {
        private IdGenerator generator = IdGenerators.incrementingLongs();

        String currentId;

        @Override
        public String getId() {
            currentId = generator.getId();
            return currentId;
        }
    }
}

