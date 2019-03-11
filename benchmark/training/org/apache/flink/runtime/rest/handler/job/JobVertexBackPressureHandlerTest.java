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
package org.apache.flink.runtime.rest.handler.job;


import JobIDPathParameter.KEY;
import VertexBackPressureStatus.DEPRECATED;
import VertexBackPressureStatus.OK;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.JobVertexBackPressureInfo;
import org.apache.flink.runtime.rest.messages.JobVertexMessageParameters;
import org.apache.flink.runtime.webmonitor.TestingRestfulGateway;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link JobVertexBackPressureHandler}.
 */
public class JobVertexBackPressureHandlerTest {
    /**
     * Job ID for which {@link OperatorBackPressureStats} exist.
     */
    private static final JobID TEST_JOB_ID_BACK_PRESSURE_STATS_AVAILABLE = new JobID();

    /**
     * Job ID for which {@link OperatorBackPressureStats} are not available.
     */
    private static final JobID TEST_JOB_ID_BACK_PRESSURE_STATS_ABSENT = new JobID();

    private TestingRestfulGateway restfulGateway;

    private JobVertexBackPressureHandler jobVertexBackPressureHandler;

    @Test
    public void testGetBackPressure() throws Exception {
        final Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(KEY, JobVertexBackPressureHandlerTest.TEST_JOB_ID_BACK_PRESSURE_STATS_AVAILABLE.toString());
        pathParameters.put(JobVertexIdPathParameter.KEY, new JobVertexID().toString());
        final HandlerRequest<EmptyRequestBody, JobVertexMessageParameters> request = new HandlerRequest(EmptyRequestBody.getInstance(), new JobVertexMessageParameters(), pathParameters, Collections.emptyMap());
        final CompletableFuture<JobVertexBackPressureInfo> jobVertexBackPressureInfoCompletableFuture = jobVertexBackPressureHandler.handleRequest(request, restfulGateway);
        final JobVertexBackPressureInfo jobVertexBackPressureInfo = jobVertexBackPressureInfoCompletableFuture.get();
        Assert.assertThat(jobVertexBackPressureInfo.getStatus(), Matchers.equalTo(OK));
        Assert.assertThat(jobVertexBackPressureInfo.getBackpressureLevel(), Matchers.equalTo(VertexBackPressureLevel.HIGH));
        Assert.assertThat(jobVertexBackPressureInfo.getSubtasks().stream().map(JobVertexBackPressureInfo.SubtaskBackPressureInfo::getRatio).collect(Collectors.toList()), Matchers.contains(1.0, 0.5, 0.1));
        Assert.assertThat(jobVertexBackPressureInfo.getSubtasks().stream().map(JobVertexBackPressureInfo.SubtaskBackPressureInfo::getBackpressureLevel).collect(Collectors.toList()), Matchers.contains(VertexBackPressureLevel.HIGH, VertexBackPressureLevel.LOW, VertexBackPressureLevel.OK));
        Assert.assertThat(jobVertexBackPressureInfo.getSubtasks().stream().map(JobVertexBackPressureInfo.SubtaskBackPressureInfo::getSubtask).collect(Collectors.toList()), Matchers.contains(0, 1, 2));
    }

    @Test
    public void testAbsentBackPressure() throws Exception {
        final Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(KEY, JobVertexBackPressureHandlerTest.TEST_JOB_ID_BACK_PRESSURE_STATS_ABSENT.toString());
        pathParameters.put(JobVertexIdPathParameter.KEY, new JobVertexID().toString());
        final HandlerRequest<EmptyRequestBody, JobVertexMessageParameters> request = new HandlerRequest(EmptyRequestBody.getInstance(), new JobVertexMessageParameters(), pathParameters, Collections.emptyMap());
        final CompletableFuture<JobVertexBackPressureInfo> jobVertexBackPressureInfoCompletableFuture = jobVertexBackPressureHandler.handleRequest(request, restfulGateway);
        final JobVertexBackPressureInfo jobVertexBackPressureInfo = jobVertexBackPressureInfoCompletableFuture.get();
        Assert.assertThat(jobVertexBackPressureInfo.getStatus(), Matchers.equalTo(DEPRECATED));
    }
}

