package com.netflix.conductor.service;


import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.dao.QueueDAO;
import com.netflix.conductor.utility.TestUtils;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;


public class TaskServiceTest {
    private TaskService taskService;

    private ExecutionService executionService;

    private QueueDAO queueDAO;

    @Test(expected = ConstraintViolationException.class)
    public void testPoll() {
        try {
            taskService.poll(null, null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskType cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testBatchPoll() {
        try {
            taskService.batchPoll(null, null, null, null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskType cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetTasks() {
        try {
            taskService.getTasks(null, null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskType cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetPendingTaskForWorkflow() {
        try {
            taskService.getPendingTaskForWorkflow(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(2, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowId cannot be null or empty."));
            Assert.assertTrue(messages.contains("TaskReferenceName cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testUpdateTask() {
        try {
            taskService.updateTask(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskResult cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testUpdateTaskInValid() {
        try {
            TaskResult taskResult = new TaskResult();
            taskService.updateTask(taskResult);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(2, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Workflow Id cannot be null or empty"));
            Assert.assertTrue(messages.contains("Task ID cannot be null or empty"));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testAckTaskReceived() {
        try {
            taskService.ackTaskReceived(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskId cannot be null or empty."));
            throw ex;
        }
    }

    @Test
    public void testAckTaskReceivedMissingWorkerId() {
        String ack = taskService.ackTaskReceived("abc", null);
        Assert.assertNotNull(ack);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testLog() {
        try {
            taskService.log(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetTaskLogs() {
        try {
            taskService.getTaskLogs(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetTask() {
        try {
            taskService.getTask(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskId cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRemoveTaskFromQueue() {
        try {
            taskService.removeTaskFromQueue(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(2, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskId cannot be null or empty."));
            Assert.assertTrue(messages.contains("TaskType cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetPollData() {
        try {
            taskService.getPollData(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskType cannot be null or empty."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRequeuePendingTask() {
        try {
            taskService.requeuePendingTask(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("TaskType cannot be null or empty."));
            throw ex;
        }
    }
}

