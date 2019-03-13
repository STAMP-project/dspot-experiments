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
package org.apache.beam.runners.fnexecution.jobsubmission;


import Endpoints.ApiServiceDescriptor;
import JobApi.PrepareJobRequest;
import JobApi.PrepareJobResponse;
import JobApi.RunJobRequest;
import JobApi.RunJobResponse;
import RunnerApi.Pipeline;
import java.util.ArrayList;
import org.apache.beam.model.jobmanagement.v1.JobApi;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.Struct;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link InMemoryJobService}.
 */
@RunWith(JUnit4.class)
public class InMemoryJobServiceTest {
    private static final String TEST_JOB_NAME = "test-job";

    private static final String TEST_JOB_ID = "test-job-id";

    private static final String TEST_RETRIEVAL_TOKEN = "test-staging-token";

    private static final Pipeline TEST_PIPELINE = Pipeline.getDefaultInstance();

    private static final Struct TEST_OPTIONS = Struct.getDefaultInstance();

    ApiServiceDescriptor stagingServiceDescriptor;

    @Mock
    JobInvoker invoker;

    @Mock
    JobInvocation invocation;

    InMemoryJobService service;

    @Test
    public void testPrepareIsSuccessful() {
        JobApi.PrepareJobRequest request = PrepareJobRequest.newBuilder().setJobName(InMemoryJobServiceTest.TEST_JOB_NAME).setPipeline(Pipeline.getDefaultInstance()).setPipelineOptions(Struct.getDefaultInstance()).build();
        InMemoryJobServiceTest.RecordingObserver<JobApi.PrepareJobResponse> recorder = new InMemoryJobServiceTest.RecordingObserver<>();
        service.prepare(request, recorder);
        Assert.assertThat(recorder.isSuccessful(), Is.is(true));
        Assert.assertThat(recorder.values, hasSize(1));
        JobApi.PrepareJobResponse response = recorder.values.get(0);
        Assert.assertThat(response.getArtifactStagingEndpoint(), IsNull.notNullValue());
        Assert.assertThat(response.getPreparationId(), IsNull.notNullValue());
    }

    @Test
    public void testJobSubmissionUsesJobInvokerAndIsSuccess() throws Exception {
        // prepare job
        JobApi.PrepareJobRequest prepareRequest = PrepareJobRequest.newBuilder().setJobName(InMemoryJobServiceTest.TEST_JOB_NAME).setPipeline(Pipeline.getDefaultInstance()).setPipelineOptions(Struct.getDefaultInstance()).build();
        InMemoryJobServiceTest.RecordingObserver<JobApi.PrepareJobResponse> prepareRecorder = new InMemoryJobServiceTest.RecordingObserver<>();
        service.prepare(prepareRequest, prepareRecorder);
        JobApi.PrepareJobResponse prepareResponse = prepareRecorder.values.get(0);
        // run job
        JobApi.RunJobRequest runRequest = RunJobRequest.newBuilder().setPreparationId(prepareResponse.getPreparationId()).setRetrievalToken(InMemoryJobServiceTest.TEST_RETRIEVAL_TOKEN).build();
        InMemoryJobServiceTest.RecordingObserver<JobApi.RunJobResponse> runRecorder = new InMemoryJobServiceTest.RecordingObserver<>();
        service.run(runRequest, runRecorder);
        Mockito.verify(invoker, Mockito.times(1)).invoke(InMemoryJobServiceTest.TEST_PIPELINE, InMemoryJobServiceTest.TEST_OPTIONS, InMemoryJobServiceTest.TEST_RETRIEVAL_TOKEN);
        Assert.assertThat(runRecorder.isSuccessful(), Is.is(true));
        Assert.assertThat(runRecorder.values, hasSize(1));
        JobApi.RunJobResponse runResponse = runRecorder.values.get(0);
        Assert.assertThat(runResponse.getJobId(), Is.is(InMemoryJobServiceTest.TEST_JOB_ID));
        Mockito.verify(invocation, Mockito.times(1)).addStateListener(ArgumentMatchers.any());
        Mockito.verify(invocation, Mockito.times(1)).start();
    }

    private static class RecordingObserver<T> implements StreamObserver<T> {
        ArrayList<T> values = new ArrayList<>();

        Throwable error = null;

        boolean isCompleted = false;

        @Override
        public void onNext(T t) {
            values.add(t);
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
        }

        @Override
        public void onCompleted() {
            isCompleted = true;
        }

        boolean isSuccessful() {
            return (isCompleted) && ((error) == null);
        }
    }
}

