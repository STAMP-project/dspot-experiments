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


import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionResponse;
import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.ServerFactory;
import org.apache.beam.runners.fnexecution.artifact.ArtifactRetrievalService;
import org.apache.beam.runners.fnexecution.data.GrpcDataService;
import org.apache.beam.runners.fnexecution.environment.EnvironmentFactory;
import org.apache.beam.runners.fnexecution.environment.EnvironmentFactory.Provider;
import org.apache.beam.runners.fnexecution.environment.RemoteEnvironment;
import org.apache.beam.runners.fnexecution.logging.GrpcLoggingService;
import org.apache.beam.runners.fnexecution.provisioning.JobInfo;
import org.apache.beam.runners.fnexecution.provisioning.StaticGrpcProvisionService;
import org.apache.beam.runners.fnexecution.state.GrpcStateService;
import org.apache.beam.sdk.fn.IdGenerator;
import org.apache.beam.sdk.fn.IdGenerators;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.Struct;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link DefaultJobBundleFactory}.
 */
@RunWith(JUnit4.class)
public class DefaultJobBundleFactoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private EnvironmentFactory envFactory;

    @Mock
    private RemoteEnvironment remoteEnvironment;

    @Mock
    private InstructionRequestHandler instructionHandler;

    @Mock
    GrpcFnServer<FnApiControlClientPoolService> controlServer;

    @Mock
    GrpcFnServer<GrpcLoggingService> loggingServer;

    @Mock
    GrpcFnServer<ArtifactRetrievalService> retrievalServer;

    @Mock
    GrpcFnServer<StaticGrpcProvisionService> provisioningServer;

    @Mock
    private GrpcFnServer<GrpcDataService> dataServer;

    @Mock
    private GrpcFnServer<GrpcStateService> stateServer;

    private final Environment environment = Environment.newBuilder().setUrn("dummy:urn").build();

    private final IdGenerator stageIdGenerator = IdGenerators.incrementingLongs();

    private final InstructionResponse instructionResponse = InstructionResponse.newBuilder().setInstructionId("instruction-id").build();

    private final Provider envFactoryProvider = (GrpcFnServer<FnApiControlClientPoolService> controlServiceServer,GrpcFnServer<GrpcLoggingService> loggingServiceServer,GrpcFnServer<ArtifactRetrievalService> retrievalServiceServer,GrpcFnServer<StaticGrpcProvisionService> provisioningServiceServer,ControlClientPool clientPool,IdGenerator idGenerator) -> envFactory;

    private final Map<String, EnvironmentFactory.Provider> envFactoryProviderMap = ImmutableMap.of(environment.getUrn(), envFactoryProvider);

    @Test
    public void createsCorrectEnvironment() throws Exception {
        try (DefaultJobBundleFactory bundleFactory = new DefaultJobBundleFactory(envFactoryProviderMap, stageIdGenerator, controlServer, loggingServer, retrievalServer, provisioningServer, dataServer, stateServer)) {
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environment));
            Mockito.verify(envFactory).createEnvironment(environment);
        }
    }

    @Test
    public void createsMultipleEnvironmentOfSingleType() throws Exception {
        ServerFactory serverFactory = ServerFactory.createDefault();
        Environment environmentA = Environment.newBuilder().setUrn("env:urn:a").setPayload(ByteString.copyFrom(new byte[1])).build();
        Environment environmentAA = Environment.newBuilder().setUrn("env:urn:a").setPayload(ByteString.copyFrom(new byte[2])).build();
        EnvironmentFactory envFactoryA = Mockito.mock(EnvironmentFactory.class);
        Mockito.when(envFactoryA.createEnvironment(environmentA)).thenReturn(remoteEnvironment);
        Mockito.when(envFactoryA.createEnvironment(environmentAA)).thenReturn(remoteEnvironment);
        EnvironmentFactory.Provider environmentProviderFactoryA = Mockito.mock(Provider.class);
        Mockito.when(environmentProviderFactoryA.createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(envFactoryA);
        Mockito.when(environmentProviderFactoryA.getServerFactory()).thenReturn(serverFactory);
        Environment environmentB = Environment.newBuilder().setUrn("env:urn:b").build();
        EnvironmentFactory envFactoryB = Mockito.mock(EnvironmentFactory.class);
        Mockito.when(envFactoryB.createEnvironment(environmentB)).thenReturn(remoteEnvironment);
        EnvironmentFactory.Provider environmentProviderFactoryB = Mockito.mock(Provider.class);
        Mockito.when(environmentProviderFactoryB.createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(envFactoryB);
        Mockito.when(environmentProviderFactoryB.getServerFactory()).thenReturn(serverFactory);
        Map<String, Provider> environmentFactoryProviderMap = ImmutableMap.of(environmentA.getUrn(), environmentProviderFactoryA, environmentB.getUrn(), environmentProviderFactoryB);
        try (DefaultJobBundleFactory bundleFactory = DefaultJobBundleFactory.create(JobInfo.create("testJob", "testJob", "token", Struct.getDefaultInstance()), environmentFactoryProviderMap)) {
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environmentA));
            Mockito.verify(environmentProviderFactoryA, Mockito.times(1)).createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(environmentProviderFactoryB, Mockito.times(0)).createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(envFactoryA, Mockito.times(1)).createEnvironment(environmentA);
            Mockito.verify(envFactoryA, Mockito.times(0)).createEnvironment(environmentAA);
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environmentAA));
            Mockito.verify(environmentProviderFactoryA, Mockito.times(2)).createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(environmentProviderFactoryB, Mockito.times(0)).createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(envFactoryA, Mockito.times(1)).createEnvironment(environmentA);
            Mockito.verify(envFactoryA, Mockito.times(1)).createEnvironment(environmentAA);
        }
    }

    @Test
    public void creatingMultipleEnvironmentFromMultipleTypes() throws Exception {
        ServerFactory serverFactory = ServerFactory.createDefault();
        Environment environmentA = Environment.newBuilder().setUrn("env:urn:a").build();
        EnvironmentFactory envFactoryA = Mockito.mock(EnvironmentFactory.class);
        Mockito.when(envFactoryA.createEnvironment(environmentA)).thenReturn(remoteEnvironment);
        EnvironmentFactory.Provider environmentProviderFactoryA = Mockito.mock(Provider.class);
        Mockito.when(environmentProviderFactoryA.createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(envFactoryA);
        Mockito.when(environmentProviderFactoryA.getServerFactory()).thenReturn(serverFactory);
        Environment environmentB = Environment.newBuilder().setUrn("env:urn:b").build();
        EnvironmentFactory envFactoryB = Mockito.mock(EnvironmentFactory.class);
        Mockito.when(envFactoryB.createEnvironment(environmentB)).thenReturn(remoteEnvironment);
        EnvironmentFactory.Provider environmentProviderFactoryB = Mockito.mock(Provider.class);
        Mockito.when(environmentProviderFactoryB.createEnvironmentFactory(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(envFactoryB);
        Mockito.when(environmentProviderFactoryB.getServerFactory()).thenReturn(serverFactory);
        Map<String, Provider> environmentFactoryProviderMap = ImmutableMap.of(environmentA.getUrn(), environmentProviderFactoryA, environmentB.getUrn(), environmentProviderFactoryB);
        try (DefaultJobBundleFactory bundleFactory = DefaultJobBundleFactory.create(JobInfo.create("testJob", "testJob", "token", Struct.getDefaultInstance()), environmentFactoryProviderMap)) {
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environmentB));
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environmentA));
        }
        Mockito.verify(envFactoryA).createEnvironment(environmentA);
        Mockito.verify(envFactoryB).createEnvironment(environmentB);
    }

    @Test
    public void closesEnvironmentOnCleanup() throws Exception {
        DefaultJobBundleFactory bundleFactory = new DefaultJobBundleFactory(envFactoryProviderMap, stageIdGenerator, controlServer, loggingServer, retrievalServer, provisioningServer, dataServer, stateServer);
        try (AutoCloseable unused = bundleFactory) {
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environment));
        }
        Mockito.verify(remoteEnvironment).close();
    }

    @Test
    public void cachesEnvironment() throws Exception {
        try (DefaultJobBundleFactory bundleFactory = new DefaultJobBundleFactory(envFactoryProviderMap, stageIdGenerator, controlServer, loggingServer, retrievalServer, provisioningServer, dataServer, stateServer)) {
            StageBundleFactory bf1 = bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environment));
            StageBundleFactory bf2 = bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environment));
            // NOTE: We hang on to stage bundle references to ensure their underlying environments are not
            // garbage collected. For additional safety, we print the factories to ensure the referernces
            // are not optimized away.
            System.out.println(("bundle factory 1:" + bf1));
            System.out.println(("bundle factory 1:" + bf2));
            Mockito.verify(envFactory).createEnvironment(environment);
            Mockito.verifyNoMoreInteractions(envFactory);
        }
    }

    @Test
    public void doesNotCacheDifferentEnvironments() throws Exception {
        Environment envFoo = Environment.newBuilder().setUrn("dummy:urn:another").build();
        RemoteEnvironment remoteEnvFoo = Mockito.mock(RemoteEnvironment.class);
        InstructionRequestHandler fooInstructionHandler = Mockito.mock(InstructionRequestHandler.class);
        Map<String, EnvironmentFactory.Provider> envFactoryProviderMapFoo = ImmutableMap.of(environment.getUrn(), envFactoryProvider, envFoo.getUrn(), envFactoryProvider);
        Mockito.when(envFactory.createEnvironment(envFoo)).thenReturn(remoteEnvFoo);
        Mockito.when(remoteEnvFoo.getInstructionRequestHandler()).thenReturn(fooInstructionHandler);
        // Don't bother creating a distinct instruction response because we don't use it here.
        Mockito.when(fooInstructionHandler.handle(ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(instructionResponse));
        try (DefaultJobBundleFactory bundleFactory = new DefaultJobBundleFactory(envFactoryProviderMapFoo, stageIdGenerator, controlServer, loggingServer, retrievalServer, provisioningServer, dataServer, stateServer)) {
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(environment));
            bundleFactory.forStage(DefaultJobBundleFactoryTest.getExecutableStage(envFoo));
            Mockito.verify(envFactory).createEnvironment(environment);
            Mockito.verify(envFactory).createEnvironment(envFoo);
            Mockito.verifyNoMoreInteractions(envFactory);
        }
    }
}

