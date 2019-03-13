/**
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.server.resources;


import com.netflix.conductor.common.metadata.workflow.RerunWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.SkipTaskRequest;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.service.WorkflowService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class WorkflowResourceTest {
    @Mock
    private WorkflowService mockWorkflowService;

    private WorkflowResource workflowResource;

    @Test
    public void testStartWorkflow() {
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName("w123");
        Map<String, Object> input = new HashMap<>();
        input.put("1", "abc");
        startWorkflowRequest.setInput(input);
        String workflowID = "w112";
        Mockito.when(mockWorkflowService.startWorkflow(ArgumentMatchers.any(StartWorkflowRequest.class))).thenReturn(workflowID);
        Assert.assertEquals("w112", workflowResource.startWorkflow(startWorkflowRequest));
    }

    @Test
    public void testStartWorkflowParam() {
        Map<String, Object> input = new HashMap<>();
        input.put("1", "abc");
        String workflowID = "w112";
        Mockito.when(mockWorkflowService.startWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyMapOf(String.class, Object.class))).thenReturn(workflowID);
        Assert.assertEquals("w112", workflowResource.startWorkflow("test1", 1, "c123", input));
    }

    @Test
    public void getWorkflows() {
        Workflow workflow = new Workflow();
        workflow.setCorrelationId("123");
        ArrayList<Workflow> listOfWorkflows = new ArrayList<Workflow>() {
            {
                add(workflow);
            }
        };
        Mockito.when(mockWorkflowService.getWorkflows(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(listOfWorkflows);
        Assert.assertEquals(listOfWorkflows, workflowResource.getWorkflows("test1", "123", true, true));
    }

    @Test
    public void testGetWorklfowsMultipleCorrelationId() {
        Workflow workflow = new Workflow();
        workflow.setCorrelationId("c123");
        List<Workflow> workflowArrayList = new ArrayList<Workflow>() {
            {
                add(workflow);
            }
        };
        List<String> correlationIdList = new ArrayList<String>() {
            {
                add("c123");
            }
        };
        Map<String, List<Workflow>> workflowMap = new HashMap<>();
        workflowMap.put("c123", workflowArrayList);
        Mockito.when(mockWorkflowService.getWorkflows(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyListOf(String.class))).thenReturn(workflowMap);
        Assert.assertEquals(workflowMap, workflowResource.getWorkflows("test", true, true, correlationIdList));
    }

    @Test
    public void testGetExecutionStatus() {
        Workflow workflow = new Workflow();
        workflow.setCorrelationId("c123");
        Mockito.when(mockWorkflowService.getExecutionStatus(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        Assert.assertEquals(workflow, workflowResource.getExecutionStatus("w123", true));
    }

    @Test
    public void testDelete() {
        workflowResource.delete("w123", true);
        Mockito.verify(mockWorkflowService, Mockito.times(1)).deleteWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testGetRunningWorkflow() {
        List<String> listOfWorklfows = new ArrayList<String>() {
            {
                add("w123");
            }
        };
        Mockito.when(mockWorkflowService.getRunningWorkflows(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(listOfWorklfows);
        Assert.assertEquals(listOfWorklfows, workflowResource.getRunningWorkflow("w123", 1, 12L, 13L));
    }

    @Test
    public void testDecide() {
        workflowResource.decide("w123");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).decideWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testPauseWorkflow() {
        workflowResource.pauseWorkflow("w123");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).pauseWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testResumeWorkflow() {
        workflowResource.resumeWorkflow("test");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).resumeWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testSkipTaskFromWorkflow() {
        workflowResource.skipTaskFromWorkflow("test", "testTask", null);
        Mockito.verify(mockWorkflowService, Mockito.times(1)).skipTaskFromWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(SkipTaskRequest.class));
    }

    @Test
    public void testRerun() {
        RerunWorkflowRequest request = new RerunWorkflowRequest();
        workflowResource.rerun("test", request);
        Mockito.verify(mockWorkflowService, Mockito.times(1)).rerunWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.any(RerunWorkflowRequest.class));
    }

    @Test
    public void restart() {
        workflowResource.restart("w123", false);
        Mockito.verify(mockWorkflowService, Mockito.times(1)).restartWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRetry() {
        workflowResource.retry("w123");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).retryWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testResetWorkflow() {
        workflowResource.resetWorkflow("w123");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).resetWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testTerminate() {
        workflowResource.terminate("w123", "test");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).terminateWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testSearch() {
        workflowResource.search(0, 100, "asc", "*", "*");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).searchWorkflows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testSearchWorkflowsByTasks() {
        workflowResource.searchWorkflowsByTasks(0, 100, "asc", "*", "*");
        Mockito.verify(mockWorkflowService, Mockito.times(1)).searchWorkflowsByTasks(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}

