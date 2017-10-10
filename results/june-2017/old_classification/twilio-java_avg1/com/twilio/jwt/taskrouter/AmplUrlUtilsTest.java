

package com.twilio.jwt.taskrouter;


/**
 * Test class for {@link UrlUtils}.
 */
public class AmplUrlUtilsTest {
    private static final java.lang.String WORKSPACE_SID = "WS123";

    private static final java.lang.String WORKER_SID = "WK123";

    private static final java.lang.String ACTIVITY_SID = "AC123";

    private static final java.lang.String TASK_SID = "TK123";

    private static final java.lang.String TASK_QUEUE_SID = "TQ123";

    private static final java.lang.String RESERVATION_SID = "WR123";

    @org.junit.Test
    public void testWorkspaces() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces", com.twilio.jwt.taskrouter.UrlUtils.workspaces());
    }

    @org.junit.Test
    public void testAllWorkspaces() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/**", com.twilio.jwt.taskrouter.UrlUtils.allWorkspaces());
    }

    @org.junit.Test
    public void testWorkspace() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123", com.twilio.jwt.taskrouter.UrlUtils.workspace(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testTaskQueues() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/TaskQueues", com.twilio.jwt.taskrouter.UrlUtils.taskQueues(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testAllTaskQueues() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/TaskQueues/**", com.twilio.jwt.taskrouter.UrlUtils.allTaskQueues(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testTaskQueue() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/TaskQueues/TQ123", com.twilio.jwt.taskrouter.UrlUtils.taskQueue(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.TASK_QUEUE_SID));
    }

    @org.junit.Test
    public void testTasks() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Tasks", com.twilio.jwt.taskrouter.UrlUtils.tasks(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testAllTasks() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Tasks/**", com.twilio.jwt.taskrouter.UrlUtils.allTasks(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testTask() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Tasks/TK123", com.twilio.jwt.taskrouter.UrlUtils.task(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.TASK_SID));
    }

    @org.junit.Test
    public void testActivities() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Activities", com.twilio.jwt.taskrouter.UrlUtils.activities(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testAllActivities() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Activities/**", com.twilio.jwt.taskrouter.UrlUtils.allActivities(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testActivity() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Activities/AC123", com.twilio.jwt.taskrouter.UrlUtils.activity(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.ACTIVITY_SID));
    }

    @org.junit.Test
    public void testWorkers() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers", com.twilio.jwt.taskrouter.UrlUtils.workers(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testAllWorkers() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**", com.twilio.jwt.taskrouter.UrlUtils.allWorkers(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID));
    }

    @org.junit.Test
    public void testWorker() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/WK123", com.twilio.jwt.taskrouter.UrlUtils.worker(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKER_SID));
    }

    @org.junit.Test
    public void testReservations() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/WK123/Reservations", com.twilio.jwt.taskrouter.UrlUtils.reservations(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKER_SID));
    }

    @org.junit.Test
    public void testAllReservations() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/WK123/Reservations/**", com.twilio.jwt.taskrouter.UrlUtils.allReservations(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKER_SID));
    }

    @org.junit.Test
    public void testReservation() {
        org.junit.Assert.assertEquals("https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/WK123/Reservations/WR123", com.twilio.jwt.taskrouter.UrlUtils.reservation(com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.WORKER_SID, com.twilio.jwt.taskrouter.AmplUrlUtilsTest.RESERVATION_SID));
    }
}

