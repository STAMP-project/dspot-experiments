package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class TasksInformationTest {
    @Test
    public void testUriGeneration() {
        TasksInformation action = new TasksInformation.Builder().build();
        Assert.assertEquals("_tasks", action.getURI(UNKNOWN));
    }

    @Test
    public void testUriGenerationSpecificTask() {
        TasksInformation action = new TasksInformation.Builder().task("node_id:task_id").build();
        Assert.assertEquals("_tasks/node_id:task_id", action.getURI(UNKNOWN));
    }
}

