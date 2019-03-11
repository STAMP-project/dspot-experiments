package com.netflix.conductor.common.run;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import org.junit.Assert;
import org.junit.Test;


public class TaskSummaryTest {
    @Test
    public void testJsonSerializing() throws Exception {
        ObjectMapper om = new ObjectMapper();
        Task task = new Task();
        TaskSummary taskSummary = new TaskSummary(task);
        String json = om.writeValueAsString(taskSummary);
        TaskSummary read = om.readValue(json, TaskSummary.class);
        Assert.assertNotNull(read);
    }
}

