/**
 * Copyright 2016 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.core.orchestration;


import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.events.EventExecution;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.TestDeciderService;
import com.netflix.conductor.dao.ExecutionDAO;
import com.netflix.conductor.dao.IndexDAO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ExecutionDAOFacadeTest {
    private ExecutionDAO executionDAO;

    private IndexDAO indexDAO;

    private ObjectMapper objectMapper;

    private ExecutionDAOFacade executionDAOFacade;

    @Test
    public void tesGetWorkflowById() throws Exception {
        Mockito.when(executionDAO.getWorkflow(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(new Workflow());
        Workflow workflow = executionDAOFacade.getWorkflowById("workflowId", true);
        Assert.assertNotNull(workflow);
        Mockito.verify(indexDAO, Mockito.never()).get(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.when(executionDAO.getWorkflow(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(null);
        InputStream stream = ExecutionDAOFacadeTest.class.getResourceAsStream("/test.json");
        byte[] bytes = IOUtils.toByteArray(stream);
        String jsonString = new String(bytes);
        Mockito.when(indexDAO.get(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(jsonString);
        workflow = executionDAOFacade.getWorkflowById("workflowId", true);
        Assert.assertNotNull(workflow);
        Mockito.verify(indexDAO, Mockito.times(1)).get(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testGetWorkflowsByCorrelationId() {
        Mockito.when(executionDAO.canSearchAcrossWorkflows()).thenReturn(true);
        Mockito.when(executionDAO.getWorkflowsByCorrelationId(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(Collections.singletonList(new Workflow()));
        List<Workflow> workflows = executionDAOFacade.getWorkflowsByCorrelationId("correlationId", true);
        Assert.assertNotNull(workflows);
        Assert.assertEquals(1, workflows.size());
        Mockito.verify(indexDAO, Mockito.never()).searchWorkflows(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.when(executionDAO.canSearchAcrossWorkflows()).thenReturn(false);
        List<String> workflowIds = new ArrayList<>();
        workflowIds.add("workflowId");
        SearchResult<String> searchResult = new SearchResult();
        searchResult.setResults(workflowIds);
        Mockito.when(indexDAO.searchWorkflows(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any())).thenReturn(searchResult);
        Mockito.when(executionDAO.getWorkflow("workflowId", true)).thenReturn(new Workflow());
        workflows = executionDAOFacade.getWorkflowsByCorrelationId("correlationId", true);
        Assert.assertNotNull(workflows);
        Assert.assertEquals(1, workflows.size());
    }

    @Test
    public void testRemoveWorkflow() {
        Mockito.when(executionDAO.getWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(new Workflow());
        executionDAOFacade.removeWorkflow("workflowId", false);
        Mockito.verify(indexDAO, Mockito.never()).updateWorkflow(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(indexDAO, Mockito.times(1)).removeWorkflow(ArgumentMatchers.anyString());
    }

    @Test
    public void testArchiveWorkflow() throws Exception {
        InputStream stream = TestDeciderService.class.getResourceAsStream("/test.json");
        Workflow workflow = objectMapper.readValue(stream, Workflow.class);
        Mockito.when(executionDAO.getWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(workflow);
        executionDAOFacade.removeWorkflow("workflowId", true);
        Mockito.verify(indexDAO, Mockito.times(1)).updateWorkflow(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(indexDAO, Mockito.never()).removeWorkflow(ArgumentMatchers.any());
    }

    @Test
    public void testAddEventExecution() {
        Mockito.when(executionDAO.addEventExecution(ArgumentMatchers.any())).thenReturn(false);
        boolean added = executionDAOFacade.addEventExecution(new EventExecution());
        Assert.assertFalse(added);
        Mockito.verify(indexDAO, Mockito.never()).addEventExecution(ArgumentMatchers.any());
        Mockito.when(executionDAO.addEventExecution(ArgumentMatchers.any())).thenReturn(true);
        added = executionDAOFacade.addEventExecution(new EventExecution());
        Assert.assertTrue(added);
        Mockito.verify(indexDAO, Mockito.times(1)).addEventExecution(ArgumentMatchers.any());
    }
}

