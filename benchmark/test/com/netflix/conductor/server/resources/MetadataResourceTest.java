/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.server.resources;


import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.service.MetadataService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MetadataResourceTest {
    private MetadataResource metadataResource;

    private MetadataService mockMetadataService;

    @Test
    public void testCreateWorkflow() {
        WorkflowDef workflowDef = new WorkflowDef();
        metadataResource.create(workflowDef);
        Mockito.verify(mockMetadataService, Mockito.times(1)).registerWorkflowDef(ArgumentMatchers.any(WorkflowDef.class));
    }

    @Test
    public void testUpdateWorkflow() {
        WorkflowDef workflowDef = new WorkflowDef();
        List<WorkflowDef> listOfWorkflowDef = new ArrayList<>();
        listOfWorkflowDef.add(workflowDef);
        metadataResource.update(listOfWorkflowDef);
        Mockito.verify(mockMetadataService, Mockito.times(1)).updateWorkflowDef(ArgumentMatchers.anyList());
    }

    @Test
    public void testGetWorkflowDef() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test");
        workflowDef.setVersion(1);
        workflowDef.setDescription("test");
        Mockito.when(mockMetadataService.getWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(workflowDef);
        Assert.assertEquals(workflowDef, metadataResource.get("test", 1));
    }

    @Test
    public void testGetAllWorkflowDef() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("test");
        workflowDef.setVersion(1);
        workflowDef.setDescription("test");
        List<WorkflowDef> listOfWorkflowDef = new ArrayList<>();
        listOfWorkflowDef.add(workflowDef);
        Mockito.when(mockMetadataService.getWorkflowDefs()).thenReturn(listOfWorkflowDef);
        Assert.assertEquals(listOfWorkflowDef, metadataResource.getAll());
    }

    @Test
    public void testUnregisterWorkflowDef() throws Exception {
        metadataResource.unregisterWorkflowDef("test", 1);
        Mockito.verify(mockMetadataService, Mockito.times(1)).unregisterWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testRegisterListOfTaskDef() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("test");
        taskDef.setDescription("desc");
        List<TaskDef> listOfTaskDefs = new ArrayList<>();
        listOfTaskDefs.add(taskDef);
        metadataResource.registerTaskDef(listOfTaskDefs);
        Mockito.verify(mockMetadataService, Mockito.times(1)).registerTaskDef(listOfTaskDefs);
    }

    @Test
    public void testRegisterTaskDef() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("test");
        taskDef.setDescription("desc");
        metadataResource.registerTaskDef(taskDef);
        Mockito.verify(mockMetadataService, Mockito.times(1)).updateTaskDef(taskDef);
    }

    @Test
    public void testGetAllTaskDefs() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("test");
        taskDef.setDescription("desc");
        List<TaskDef> listOfTaskDefs = new ArrayList<>();
        listOfTaskDefs.add(taskDef);
        Mockito.when(mockMetadataService.getTaskDefs()).thenReturn(listOfTaskDefs);
        Assert.assertEquals(listOfTaskDefs, metadataResource.getTaskDefs());
    }

    @Test
    public void testGetTaskDef() {
        TaskDef taskDef = new TaskDef();
        taskDef.setName("test");
        taskDef.setDescription("desc");
        Mockito.when(mockMetadataService.getTaskDef(ArgumentMatchers.anyString())).thenReturn(taskDef);
        Assert.assertEquals(taskDef, metadataResource.getTaskDef("test"));
    }

    @Test
    public void testUnregisterTaskDef() {
        metadataResource.unregisterTaskDef("test");
        Mockito.verify(mockMetadataService, Mockito.times(1)).unregisterTaskDef(ArgumentMatchers.anyString());
    }
}

