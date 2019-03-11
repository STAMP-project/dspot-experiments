package com.netflix.conductor.server.resources;


import Task.Status.IN_PROGRESS;
import TaskResult.Status.COMPLETED;
import com.netflix.conductor.common.metadata.tasks.PollData;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskExecLog;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.run.SearchResult;
import com.netflix.conductor.common.run.TaskSummary;
import com.netflix.conductor.service.TaskService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TaskResourceTest {
    private TaskService mockTaskService;

    private TaskResource taskResource;

    @Test
    public void testPoll() throws Exception {
        Task task = new Task();
        task.setTaskType("SIMPLE");
        task.setWorkerId("123");
        task.setDomain("test");
        Mockito.when(mockTaskService.poll(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(task);
        Assert.assertEquals(task, taskResource.poll("SIMPLE", "123", "test"));
    }

    @Test
    public void testBatchPoll() throws Exception {
        Task task = new Task();
        task.setTaskType("SIMPLE");
        task.setWorkerId("123");
        task.setDomain("test");
        List<Task> listOfTasks = new ArrayList<>();
        listOfTasks.add(task);
        Mockito.when(mockTaskService.batchPoll(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(listOfTasks);
        Assert.assertEquals(listOfTasks, taskResource.batchPoll("SIMPLE", "123", "test", 1, 100));
    }

    @Test
    public void testGetInProgressTasks() throws Exception {
        Task task = new Task();
        task.setTaskType("SIMPLE");
        task.setWorkerId("123");
        task.setDomain("test");
        task.setStatus(IN_PROGRESS);
        List<Task> listOfTasks = new ArrayList<>();
        listOfTasks.add(task);
        Mockito.when(mockTaskService.getTasks(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(listOfTasks);
        Assert.assertEquals(listOfTasks, taskResource.getTasks("SIMPLE", "123", 123));
    }

    @Test
    public void testGetPendingTaskForWorkflow() {
        Task task = new Task();
        task.setTaskType("SIMPLE");
        task.setWorkerId("123");
        task.setDomain("test");
        task.setStatus(IN_PROGRESS);
        Mockito.when(mockTaskService.getPendingTaskForWorkflow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(task);
        Assert.assertEquals(task, taskResource.getPendingTaskForWorkflow("SIMPLE", "123"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        TaskResult taskResult = new TaskResult();
        taskResult.setStatus(COMPLETED);
        taskResult.setTaskId("123");
        Mockito.when(mockTaskService.updateTask(ArgumentMatchers.any(TaskResult.class))).thenReturn("123");
        Assert.assertEquals("123", taskResource.updateTask(taskResult));
    }

    @Test
    public void testAck() throws Exception {
        String acked = "true";
        Mockito.when(mockTaskService.ackTaskReceived(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(acked);
        Assert.assertEquals("true", taskResource.ack("123", "456"));
    }

    @Test
    public void testLog() {
        taskResource.log("123", "test log");
        Mockito.verify(mockTaskService, Mockito.times(1)).log(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testGetTaskLogs() {
        List<TaskExecLog> listOfLogs = new ArrayList<>();
        listOfLogs.add(new TaskExecLog("test log"));
        Mockito.when(mockTaskService.getTaskLogs(ArgumentMatchers.anyString())).thenReturn(listOfLogs);
        Assert.assertEquals(listOfLogs, taskResource.getTaskLogs("123"));
    }

    @Test
    public void testGetTask() throws Exception {
        Task task = new Task();
        task.setTaskType("SIMPLE");
        task.setWorkerId("123");
        task.setDomain("test");
        task.setStatus(IN_PROGRESS);
        Mockito.when(mockTaskService.getTask(ArgumentMatchers.anyString())).thenReturn(task);
        Assert.assertEquals(task, taskResource.getTask("123"));
    }

    @Test
    public void testRemoveTaskFromQueue() {
        taskResource.removeTaskFromQueue("SIMPLE", "123");
        Mockito.verify(mockTaskService, Mockito.times(1)).removeTaskFromQueue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testSize() {
        Map<String, Integer> map = new HashMap<>();
        map.put("test1", 1);
        map.put("test2", 2);
        List<String> list = new ArrayList<String>();
        list.add("test1");
        list.add("test2");
        Mockito.when(mockTaskService.getTaskQueueSizes(ArgumentMatchers.anyListOf(String.class))).thenReturn(map);
        Assert.assertEquals(map, taskResource.size(list));
    }

    @Test
    public void testAllVerbose() {
        Map<String, Long> map = new HashMap<>();
        map.put("queue1", 1L);
        map.put("queue2", 2L);
        Map<String, Map<String, Long>> mapOfMap = new HashMap<>();
        mapOfMap.put("queue", map);
        Map<String, Map<String, Map<String, Long>>> queueSizeMap = new HashMap<>();
        queueSizeMap.put("queue", mapOfMap);
        Mockito.when(mockTaskService.allVerbose()).thenReturn(queueSizeMap);
        Assert.assertEquals(queueSizeMap, taskResource.allVerbose());
    }

    @Test
    public void testQueueDetails() {
        Map<String, Long> map = new HashMap<>();
        map.put("queue1", 1L);
        map.put("queue2", 2L);
        Mockito.when(mockTaskService.getAllQueueDetails()).thenReturn(map);
        Assert.assertEquals(map, taskResource.all());
    }

    @Test
    public void testGetPollData() throws Exception {
        PollData pollData = new PollData("queue", "test", "w123", 100);
        List<PollData> listOfPollData = new ArrayList<>();
        listOfPollData.add(pollData);
        Mockito.when(mockTaskService.getPollData(ArgumentMatchers.anyString())).thenReturn(listOfPollData);
        Assert.assertEquals(listOfPollData, taskResource.getPollData("w123"));
    }

    @Test
    public void testGetAllPollData() {
        PollData pollData = new PollData("queue", "test", "w123", 100);
        List<PollData> listOfPollData = new ArrayList<>();
        listOfPollData.add(pollData);
        Mockito.when(mockTaskService.getAllPollData()).thenReturn(listOfPollData);
        Assert.assertEquals(listOfPollData, taskResource.getAllPollData());
    }

    @Test
    public void testRequeue() throws Exception {
        Mockito.when(mockTaskService.requeue()).thenReturn("1");
        Assert.assertEquals("1", taskResource.requeue());
    }

    @Test
    public void testRequeueTaskType() throws Exception {
        Mockito.when(mockTaskService.requeuePendingTask(ArgumentMatchers.anyString())).thenReturn("1");
        Assert.assertEquals("1", taskResource.requeuePendingTask("SIMPLE"));
    }

    @Test
    public void search() {
        Task task = new Task();
        task.setTaskType("SIMPLE");
        task.setWorkerId("123");
        task.setDomain("test");
        task.setStatus(IN_PROGRESS);
        TaskSummary taskSummary = new TaskSummary(task);
        ArrayList<TaskSummary> listOfTaskSummary = new ArrayList<TaskSummary>() {
            {
                add(taskSummary);
            }
        };
        SearchResult<TaskSummary> searchResult = new SearchResult<TaskSummary>(100, listOfTaskSummary);
        listOfTaskSummary.add(taskSummary);
        Mockito.when(mockTaskService.search(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(searchResult);
        Assert.assertEquals(searchResult, taskResource.search(0, 100, "asc", "*", "*"));
    }
}

