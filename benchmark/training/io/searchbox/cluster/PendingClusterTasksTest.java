package io.searchbox.cluster;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class PendingClusterTasksTest {
    @Test
    public void testUriGeneration() {
        PendingClusterTasks action = new PendingClusterTasks.Builder().build();
        Assert.assertEquals("/_cluster/pending_tasks", action.getURI(UNKNOWN));
    }
}

