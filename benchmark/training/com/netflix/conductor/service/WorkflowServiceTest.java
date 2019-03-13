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
package com.netflix.conductor.service;


import com.netflix.conductor.common.metadata.workflow.RerunWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.SkipTaskRequest;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
import com.netflix.conductor.core.execution.ApplicationException;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import com.netflix.conductor.utility.TestUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WorkflowServiceTest {
    private WorkflowExecutor mockWorkflowExecutor;

    private ExecutionService mockExecutionService;

    private MetadataService mockMetadata;

    private WorkflowService workflowService;

    @Test(expected = ConstraintViolationException.class)
    public void testStartWorkflowNull() {
        try {
            workflowService.startWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("StartWorkflowRequest cannot be null"));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testStartWorkflowName() {
        try {
            Map<String, Object> input = new HashMap<>();
            input.put("1", "abc");
            workflowService.startWorkflow(null, 1, "abc", input);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Workflow name cannot be null or empty"));
            throw ex;
        }
    }

    @Test
    public void testStartWorkflow() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test");
        workflowDef.setVersion(1);
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName("w123");
        Map<String, Object> input = new HashMap<>();
        input.put("1", "abc");
        startWorkflowRequest.setInput(input);
        String workflowID = "w112";
        Mockito.when(mockMetadata.getWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(workflowDef);
        Mockito.when(mockWorkflowExecutor.startWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyMapOf(String.class, Object.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn(workflowID);
        Assert.assertEquals("w112", workflowService.startWorkflow(startWorkflowRequest));
    }

    @Test
    public void testStartWorkflowParam() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test");
        workflowDef.setVersion(1);
        Map<String, Object> input = new HashMap<>();
        input.put("1", "abc");
        String workflowID = "w112";
        Mockito.when(mockMetadata.getWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(workflowDef);
        Mockito.when(mockWorkflowExecutor.startWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyMapOf(String.class, Object.class), ArgumentMatchers.any(String.class))).thenReturn(workflowID);
        Assert.assertEquals("w112", workflowService.startWorkflow("test", 1, "c123", input));
    }

    @Test(expected = ApplicationException.class)
    public void testApplicationExceptionStartWorkflowMessageParam() {
        try {
            Mockito.when(mockMetadata.getWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(null);
            Map<String, Object> input = new HashMap<>();
            input.put("1", "abc");
            workflowService.startWorkflow("test", 1, "c123", input);
        } catch (ApplicationException ex) {
            String message = "No such workflow found by name: test, version: 1";
            Assert.assertEquals(message, ex.getMessage());
            throw ex;
        }
        Assert.fail("ApplicationException did not throw!");
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetWorkflowsNoName() {
        try {
            workflowService.getWorkflows("", "c123", true, true);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Workflow name cannot be null or empty"));
            throw ex;
        }
    }

    @Test
    public void testGetWorklfowsSingleCorrelationId() {
        Workflow workflow = new Workflow();
        workflow.setCorrelationId("c123");
        List<Workflow> workflowArrayList = new ArrayList<Workflow>() {
            {
                add(workflow);
            }
        };
        Mockito.when(mockExecutionService.getWorkflowInstances(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(workflowArrayList);
        Assert.assertEquals(workflowArrayList, workflowService.getWorkflows("test", "c123", true, true));
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
        Mockito.when(mockExecutionService.getWorkflowInstances(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(workflowArrayList);
        Assert.assertEquals(workflowMap, workflowService.getWorkflows("test", true, true, correlationIdList));
    }

    @Test
    public void testGetExecutionStatus() {
        Workflow workflow = new Workflow();
        workflow.setCorrelationId("c123");
        Mockito.when(mockExecutionService.getExecutionStatus(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        Assert.assertEquals(workflow, workflowService.getExecutionStatus("w123", true));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetExecutionStatusNoWorkflowId() {
        try {
            workflowService.getExecutionStatus("", true);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ApplicationException.class)
    public void testApplicationExceptionGetExecutionStatus() {
        try {
            Mockito.when(mockExecutionService.getExecutionStatus(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(null);
            workflowService.getExecutionStatus("w123", true);
        } catch (ApplicationException ex) {
            String message = "Workflow with Id: w123 not found.";
            Assert.assertEquals(message, ex.getMessage());
            throw ex;
        }
        Assert.fail("ApplicationException did not throw!");
    }

    @Test
    public void testDeleteWorkflow() {
        workflowService.deleteWorkflow("w123", true);
        Mockito.verify(mockExecutionService, Mockito.times(1)).removeWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidDeleteWorkflow() {
        try {
            workflowService.deleteWorkflow(null, true);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidPauseWorkflow() {
        try {
            workflowService.pauseWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidResumeWorkflow() {
        try {
            workflowService.resumeWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidSkipTaskFromWorkflow() {
        try {
            SkipTaskRequest skipTaskRequest = new SkipTaskRequest();
            workflowService.skipTaskFromWorkflow(null, null, skipTaskRequest);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(2, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId name cannot be null or empty."));
            Assert.assertTrue(messages.contains("TaskReferenceName cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidWorkflowNameGetRunningWorkflows() {
        try {
            workflowService.getRunningWorkflows(null, 123, null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Workflow name cannot be null or empty."));
            throw ex;
        }
    }

    @Test
    public void testGetRunningWorkflowsTime() {
        workflowService.getRunningWorkflows("test", 1, 100L, 120L);
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).getWorkflows(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void testGetRunningWorkflows() {
        workflowService.getRunningWorkflows("test", 1, null, null);
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).getRunningWorkflowIds(ArgumentMatchers.anyString());
    }

    @Test
    public void testDecideWorkflow() {
        workflowService.decideWorkflow("test");
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).decide(ArgumentMatchers.anyString());
    }

    @Test
    public void testPauseWorkflow() {
        workflowService.pauseWorkflow("test");
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).pauseWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testResumeWorkflow() {
        workflowService.resumeWorkflow("test");
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).resumeWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testSkipTaskFromWorkflow() {
        workflowService.skipTaskFromWorkflow("test", "testTask", null);
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).skipTaskFromWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(SkipTaskRequest.class));
    }

    @Test
    public void testRerunWorkflow() {
        RerunWorkflowRequest request = new RerunWorkflowRequest();
        workflowService.rerunWorkflow("test", request);
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).rerun(ArgumentMatchers.any(RerunWorkflowRequest.class));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRerunWorkflowNull() {
        try {
            workflowService.rerunWorkflow(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(2, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            Assert.assertTrue(messages.contains("RerunWorkflowRequest cannot be null."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRestartWorkflowNull() {
        try {
            workflowService.restartWorkflow(null, false);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRetryWorkflowNull() {
        try {
            workflowService.retryWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testResetWorkflowNull() {
        try {
            workflowService.resetWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testTerminateWorkflowNull() {
        try {
            workflowService.terminateWorkflow(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            throw ex;
        }
    }

    @Test
    public void testRerunWorkflowReturnWorkflowId() {
        RerunWorkflowRequest request = new RerunWorkflowRequest();
        String workflowId = "w123";
        Mockito.when(mockWorkflowExecutor.rerun(ArgumentMatchers.any(RerunWorkflowRequest.class))).thenReturn(workflowId);
        Assert.assertEquals(workflowId, workflowService.rerunWorkflow("test", request));
    }

    @Test
    public void testRestartWorkflow() {
        workflowService.restartWorkflow("w123", false);
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).rewind(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testRetryWorkflow() {
        workflowService.retryWorkflow("w123");
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).retry(ArgumentMatchers.anyString());
    }

    @Test
    public void testResetWorkflow() {
        workflowService.resetWorkflow("w123");
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).resetCallbacksForInProgressTasks(ArgumentMatchers.anyString());
    }

    @Test
    public void testTerminateWorkflow() {
        workflowService.terminateWorkflow("w123", "test");
        Mockito.verify(mockWorkflowExecutor, Mockito.times(1)).terminateWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testSearchWorkflows() {
        Workflow workflow = new Workflow();
        workflow.setCorrelationId("c123");
        WorkflowSummary workflowSummary = new WorkflowSummary(workflow);
        List<WorkflowSummary> listOfWorkflowSummary = new ArrayList<WorkflowSummary>() {
            {
                add(workflowSummary);
            }
        };
        SearchResult<WorkflowSummary> searchResult = new SearchResult<WorkflowSummary>(100, listOfWorkflowSummary);
        Mockito.when(mockExecutionService.search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyListOf(String.class))).thenReturn(searchResult);
        Assert.assertEquals(searchResult, workflowService.searchWorkflows(0, 100, "asc", "*", "*"));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidSizeSearchWorkflows() {
        try {
            workflowService.searchWorkflows(0, 6000, "asc", "*", "*");
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Cannot return more than 5000 workflows. Please use pagination."));
            throw ex;
        }
    }

    @Test
    public void searchWorkflowsByTasks() {
        workflowService.searchWorkflowsByTasks(0, 100, "asc", "*", "*");
        Mockito.verify(mockExecutionService, Mockito.times(1)).searchWorkflowByTasks(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyListOf(String.class));
    }
}

