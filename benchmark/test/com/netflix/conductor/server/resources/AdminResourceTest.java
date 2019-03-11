package com.netflix.conductor.server.resources;


import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.service.AdminService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AdminResourceTest {
    @Mock
    private AdminService mockAdminService;

    @Mock
    private AdminResource adminResource;

    @Test
    public void testGetAllConfig() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("config1", "test");
        Mockito.when(mockAdminService.getAllConfig()).thenReturn(configs);
        Assert.assertEquals(configs, adminResource.getAllConfig());
    }

    @Test
    public void testView() throws Exception {
        Task task = new Task();
        task.setReferenceTaskName("test");
        List<Task> listOfTask = new ArrayList<>();
        listOfTask.add(task);
        Mockito.when(mockAdminService.getListOfPendingTask(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(listOfTask);
        Assert.assertEquals(listOfTask, adminResource.view("testTask", 0, 100));
    }

    @Test
    public void testRequeueSweep() {
        String workflowId = "w123";
        Mockito.when(mockAdminService.requeueSweep(ArgumentMatchers.anyString())).thenReturn(workflowId);
        Assert.assertEquals(workflowId, adminResource.requeueSweep(workflowId));
    }
}

