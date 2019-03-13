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
package org.apache.beam.runners.dataflow.worker;


import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.services.dataflow.model.LeaseWorkItemRequest;
import com.google.api.services.dataflow.model.MapTask;
import com.google.api.services.dataflow.model.SeqMapTask;
import com.google.api.services.dataflow.model.WorkItem;
import java.io.IOException;
import org.apache.beam.runners.dataflow.options.DataflowWorkerHarnessOptions;
import org.apache.beam.runners.dataflow.worker.logging.DataflowWorkerLoggingMDC;
import org.apache.beam.runners.dataflow.worker.testing.RestoreDataflowLoggingMDC;
import org.apache.beam.sdk.testing.RestoreSystemProperties;
import org.apache.beam.sdk.util.FastNanoClockAndSleeper;
import org.apache.beam.sdk.util.Transport;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for {@link DataflowWorkUnitClient}.
 */
@RunWith(JUnit4.class)
public class DataflowWorkUnitClientTest {
    private static final Logger LOG = LoggerFactory.getLogger(DataflowWorkUnitClientTest.class);

    @Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public TestRule restoreLogging = new RestoreDataflowLoggingMDC();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public FastNanoClockAndSleeper fastNanoClockAndSleeper = new FastNanoClockAndSleeper();

    @Mock
    private MockHttpTransport transport;

    @Mock
    private MockLowLevelHttpRequest request;

    private DataflowWorkerHarnessOptions pipelineOptions;

    private static final String PROJECT_ID = "TEST_PROJECT_ID";

    private static final String JOB_ID = "TEST_JOB_ID";

    private static final String WORKER_ID = "TEST_WORKER_ID";

    @Test
    public void testCloudServiceCall() throws Exception {
        WorkItem workItem = createWorkItem(DataflowWorkUnitClientTest.PROJECT_ID, DataflowWorkUnitClientTest.JOB_ID);
        Mockito.when(request.execute()).thenReturn(generateMockResponse(workItem));
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        Assert.assertEquals(Optional.of(workItem), client.getWorkItem());
        LeaseWorkItemRequest actualRequest = Transport.getJsonFactory().fromString(request.getContentAsString(), LeaseWorkItemRequest.class);
        Assert.assertEquals(DataflowWorkUnitClientTest.WORKER_ID, actualRequest.getWorkerId());
        Assert.assertEquals(ImmutableList.<String>of(DataflowWorkUnitClientTest.WORKER_ID, "remote_source", "custom_source"), actualRequest.getWorkerCapabilities());
        Assert.assertEquals(ImmutableList.<String>of("map_task", "seq_map_task", "remote_source_task"), actualRequest.getWorkItemTypes());
        Assert.assertEquals("1234", DataflowWorkerLoggingMDC.getWorkId());
    }

    @Test
    public void testCloudServiceCallMapTaskStagePropagation() throws Exception {
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        // Publish and acquire a map task work item, and verify we're now processing that stage.
        final String stageName = "test_stage_name";
        MapTask mapTask = new MapTask();
        mapTask.setStageName(stageName);
        WorkItem workItem = createWorkItem(DataflowWorkUnitClientTest.PROJECT_ID, DataflowWorkUnitClientTest.JOB_ID);
        workItem.setMapTask(mapTask);
        Mockito.when(request.execute()).thenReturn(generateMockResponse(workItem));
        Assert.assertEquals(Optional.of(workItem), client.getWorkItem());
        Assert.assertEquals(stageName, DataflowWorkerLoggingMDC.getStageName());
    }

    @Test
    public void testCloudServiceCallSeqMapTaskStagePropagation() throws Exception {
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        // Publish and acquire a seq map task work item, and verify we're now processing that stage.
        final String stageName = "test_stage_name";
        SeqMapTask seqMapTask = new SeqMapTask();
        seqMapTask.setStageName(stageName);
        WorkItem workItem = createWorkItem(DataflowWorkUnitClientTest.PROJECT_ID, DataflowWorkUnitClientTest.JOB_ID);
        workItem.setSeqMapTask(seqMapTask);
        Mockito.when(request.execute()).thenReturn(generateMockResponse(workItem));
        Assert.assertEquals(Optional.of(workItem), client.getWorkItem());
        Assert.assertEquals(stageName, DataflowWorkerLoggingMDC.getStageName());
    }

    @Test
    public void testCloudServiceCallNoWorkPresent() throws Exception {
        // If there's no work the service should return an empty work item.
        WorkItem workItem = new WorkItem();
        Mockito.when(request.execute()).thenReturn(generateMockResponse(workItem));
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        Assert.assertEquals(Optional.absent(), client.getWorkItem());
        LeaseWorkItemRequest actualRequest = Transport.getJsonFactory().fromString(request.getContentAsString(), LeaseWorkItemRequest.class);
        Assert.assertEquals(DataflowWorkUnitClientTest.WORKER_ID, actualRequest.getWorkerId());
        Assert.assertEquals(ImmutableList.<String>of(DataflowWorkUnitClientTest.WORKER_ID, "remote_source", "custom_source"), actualRequest.getWorkerCapabilities());
        Assert.assertEquals(ImmutableList.<String>of("map_task", "seq_map_task", "remote_source_task"), actualRequest.getWorkItemTypes());
    }

    @Test
    public void testCloudServiceCallNoWorkId() throws Exception {
        // If there's no work the service should return an empty work item.
        WorkItem workItem = createWorkItem(DataflowWorkUnitClientTest.PROJECT_ID, DataflowWorkUnitClientTest.JOB_ID);
        workItem.setId(null);
        Mockito.when(request.execute()).thenReturn(generateMockResponse(workItem));
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        Assert.assertEquals(Optional.absent(), client.getWorkItem());
        LeaseWorkItemRequest actualRequest = Transport.getJsonFactory().fromString(request.getContentAsString(), LeaseWorkItemRequest.class);
        Assert.assertEquals(DataflowWorkUnitClientTest.WORKER_ID, actualRequest.getWorkerId());
        Assert.assertEquals(ImmutableList.<String>of(DataflowWorkUnitClientTest.WORKER_ID, "remote_source", "custom_source"), actualRequest.getWorkerCapabilities());
        Assert.assertEquals(ImmutableList.<String>of("map_task", "seq_map_task", "remote_source_task"), actualRequest.getWorkItemTypes());
    }

    @Test
    public void testCloudServiceCallNoWorkItem() throws Exception {
        Mockito.when(request.execute()).thenReturn(generateMockResponse());
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        Assert.assertEquals(Optional.absent(), client.getWorkItem());
        LeaseWorkItemRequest actualRequest = Transport.getJsonFactory().fromString(request.getContentAsString(), LeaseWorkItemRequest.class);
        Assert.assertEquals(DataflowWorkUnitClientTest.WORKER_ID, actualRequest.getWorkerId());
        Assert.assertEquals(ImmutableList.<String>of(DataflowWorkUnitClientTest.WORKER_ID, "remote_source", "custom_source"), actualRequest.getWorkerCapabilities());
        Assert.assertEquals(ImmutableList.<String>of("map_task", "seq_map_task", "remote_source_task"), actualRequest.getWorkItemTypes());
    }

    @Test
    public void testCloudServiceCallMultipleWorkItems() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("This version of the SDK expects no more than one work item from the service");
        WorkItem workItem1 = createWorkItem(DataflowWorkUnitClientTest.PROJECT_ID, DataflowWorkUnitClientTest.JOB_ID);
        WorkItem workItem2 = createWorkItem(DataflowWorkUnitClientTest.PROJECT_ID, DataflowWorkUnitClientTest.JOB_ID);
        Mockito.when(request.execute()).thenReturn(generateMockResponse(workItem1, workItem2));
        WorkUnitClient client = new DataflowWorkUnitClient(pipelineOptions, DataflowWorkUnitClientTest.LOG);
        client.getWorkItem();
    }
}

