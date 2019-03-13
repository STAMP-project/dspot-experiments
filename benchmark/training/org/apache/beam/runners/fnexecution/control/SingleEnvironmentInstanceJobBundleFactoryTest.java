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
package org.apache.beam.runners.fnexecution.control;


import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.model.pipeline.v1.RunnerApi.ExecutableStagePayload;
import org.apache.beam.runners.core.construction.Environments;
import org.apache.beam.runners.core.construction.JavaReadViaImpulse;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.runners.core.construction.graph.ExecutableStage;
import org.apache.beam.runners.core.construction.graph.GreedyPipelineFuser;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.data.GrpcDataService;
import org.apache.beam.runners.fnexecution.environment.EnvironmentFactory;
import org.apache.beam.runners.fnexecution.environment.RemoteEnvironment;
import org.apache.beam.runners.fnexecution.state.GrpcStateService;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link SingleEnvironmentInstanceJobBundleFactory}.
 */
@RunWith(JUnit4.class)
public class SingleEnvironmentInstanceJobBundleFactoryTest {
    @Mock
    private EnvironmentFactory environmentFactory;

    @Mock
    private InstructionRequestHandler instructionRequestHandler;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private GrpcFnServer<GrpcDataService> dataServer;

    private GrpcFnServer<GrpcStateService> stateServer;

    private JobBundleFactory factory;

    @Test
    public void closeShutsDownEnvironments() throws Exception {
        Pipeline p = Pipeline.create();
        p.apply("Create", Create.of(1, 2, 3));
        p.replaceAll(Collections.singletonList(JavaReadViaImpulse.boundedOverride()));
        ExecutableStage stage = GreedyPipelineFuser.fuse(PipelineTranslation.toProto(p)).getFusedStages().stream().findFirst().get();
        RemoteEnvironment remoteEnv = Mockito.mock(RemoteEnvironment.class);
        Mockito.when(remoteEnv.getInstructionRequestHandler()).thenReturn(instructionRequestHandler);
        Mockito.when(environmentFactory.createEnvironment(stage.getEnvironment())).thenReturn(remoteEnv);
        factory.forStage(stage);
        factory.close();
        Mockito.verify(remoteEnv).close();
    }

    @Test
    public void closeShutsDownEnvironmentsWhenSomeFail() throws Exception {
        Pipeline p = Pipeline.create();
        p.apply("Create", Create.of(1, 2, 3));
        p.replaceAll(Collections.singletonList(JavaReadViaImpulse.boundedOverride()));
        ExecutableStage firstEnvStage = GreedyPipelineFuser.fuse(PipelineTranslation.toProto(p)).getFusedStages().stream().findFirst().get();
        ExecutableStagePayload basePayload = ExecutableStagePayload.parseFrom(firstEnvStage.toPTransform("foo").getSpec().getPayload());
        Environment secondEnv = Environments.createDockerEnvironment("second_env");
        ExecutableStage secondEnvStage = ExecutableStage.fromPayload(basePayload.toBuilder().setEnvironment(secondEnv).build());
        Environment thirdEnv = Environments.createDockerEnvironment("third_env");
        ExecutableStage thirdEnvStage = ExecutableStage.fromPayload(basePayload.toBuilder().setEnvironment(thirdEnv).build());
        RemoteEnvironment firstRemoteEnv = Mockito.mock(RemoteEnvironment.class, "First Remote Env");
        RemoteEnvironment secondRemoteEnv = Mockito.mock(RemoteEnvironment.class, "Second Remote Env");
        RemoteEnvironment thirdRemoteEnv = Mockito.mock(RemoteEnvironment.class, "Third Remote Env");
        Mockito.when(environmentFactory.createEnvironment(firstEnvStage.getEnvironment())).thenReturn(firstRemoteEnv);
        Mockito.when(environmentFactory.createEnvironment(secondEnvStage.getEnvironment())).thenReturn(secondRemoteEnv);
        Mockito.when(environmentFactory.createEnvironment(thirdEnvStage.getEnvironment())).thenReturn(thirdRemoteEnv);
        Mockito.when(firstRemoteEnv.getInstructionRequestHandler()).thenReturn(instructionRequestHandler);
        Mockito.when(secondRemoteEnv.getInstructionRequestHandler()).thenReturn(instructionRequestHandler);
        Mockito.when(thirdRemoteEnv.getInstructionRequestHandler()).thenReturn(instructionRequestHandler);
        factory.forStage(firstEnvStage);
        factory.forStage(secondEnvStage);
        factory.forStage(thirdEnvStage);
        IllegalStateException firstException = new IllegalStateException("first stage");
        Mockito.doThrow(firstException).when(firstRemoteEnv).close();
        IllegalStateException thirdException = new IllegalStateException("third stage");
        Mockito.doThrow(thirdException).when(thirdRemoteEnv).close();
        try {
            factory.close();
            Assert.fail("Factory close should have thrown");
        } catch (IllegalStateException expected) {
            if (expected.equals(firstException)) {
                Assert.assertThat(ImmutableList.copyOf(expected.getSuppressed()), Matchers.contains(thirdException));
            } else
                if (expected.equals(thirdException)) {
                    Assert.assertThat(ImmutableList.copyOf(expected.getSuppressed()), Matchers.contains(firstException));
                } else {
                    throw expected;
                }

            Mockito.verify(firstRemoteEnv).close();
            Mockito.verify(secondRemoteEnv).close();
            Mockito.verify(thirdRemoteEnv).close();
        }
    }
}

