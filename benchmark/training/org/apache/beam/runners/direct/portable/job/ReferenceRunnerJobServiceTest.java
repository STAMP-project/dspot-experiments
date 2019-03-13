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
package org.apache.beam.runners.direct.portable.job;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.apache.beam.model.jobmanagement.v1.JobApi.PrepareJobRequest;
import org.apache.beam.model.jobmanagement.v1.JobApi.PrepareJobResponse;
import org.apache.beam.model.jobmanagement.v1.JobServiceGrpc.JobServiceBlockingStub;
import org.apache.beam.model.pipeline.v1.Endpoints.ApiServiceDescriptor;
import org.apache.beam.model.pipeline.v1.RunnerApi.Pipeline;
import org.apache.beam.runners.core.construction.ArtifactServiceStager;
import org.apache.beam.runners.core.construction.ArtifactServiceStager.StagedFile;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.InProcessServerFactory;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.Struct;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.inprocess.InProcessChannelBuilder;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ReferenceRunnerJobService}.
 */
@RunWith(JUnit4.class)
public class ReferenceRunnerJobServiceTest {
    @Rule
    public TemporaryFolder runnerTemp = new TemporaryFolder();

    @Rule
    public TemporaryFolder clientTemp = new TemporaryFolder();

    private InProcessServerFactory serverFactory = InProcessServerFactory.create();

    private ReferenceRunnerJobService service;

    private GrpcFnServer<ReferenceRunnerJobService> server;

    private JobServiceBlockingStub stub;

    @Test
    public void testPrepareJob() throws Exception {
        PrepareJobResponse response = stub.prepare(PrepareJobRequest.newBuilder().setPipelineOptions(Struct.getDefaultInstance()).setPipeline(Pipeline.getDefaultInstance()).setJobName("myJobName").build());
        ApiServiceDescriptor stagingEndpoint = response.getArtifactStagingEndpoint();
        ArtifactServiceStager stager = ArtifactServiceStager.overChannel(InProcessChannelBuilder.forName(stagingEndpoint.getUrl()).build());
        String stagingSessionToken = response.getStagingSessionToken();
        File foo = writeTempFile("foo", "foo, bar, baz".getBytes(StandardCharsets.UTF_8));
        File bar = writeTempFile("spam", "spam, ham, eggs".getBytes(StandardCharsets.UTF_8));
        stager.stage(stagingSessionToken, ImmutableList.of(StagedFile.of(foo, foo.getName()), StagedFile.of(bar, bar.getName())));
        List<byte[]> tempDirFiles = readFlattenedFiles(runnerTemp.getRoot());
        Assert.assertThat(tempDirFiles, Matchers.hasItems(arrayEquals(Files.readAllBytes(foo.toPath())), arrayEquals(Files.readAllBytes(bar.toPath()))));
        // TODO: 'run' the job with some sort of noop backend, to verify state is cleaned up.
    }
}

