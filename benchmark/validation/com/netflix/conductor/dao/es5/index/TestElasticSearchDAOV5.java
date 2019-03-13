package com.netflix.conductor.dao.es5.index;


import Status.FAILED;
import Type.complete_task;
import Workflow.WorkflowStatus.COMPLETED;
import Workflow.WorkflowStatus.RUNNING;
import com.netflix.conductor.common.metadata.events.EventExecution;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskExecLog;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.elasticsearch.ElasticSearchConfiguration;
import com.netflix.conductor.elasticsearch.EmbeddedElasticSearch;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestElasticSearchDAOV5 {
    private static final Logger logger = LoggerFactory.getLogger(TestElasticSearchDAOV5.class);

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMww");

    private static final String MSG_DOC_TYPE = "message";

    private static final String EVENT_DOC_TYPE = "event";

    private static final String LOG_INDEX_PREFIX = "task_log";

    private static ElasticSearchConfiguration configuration;

    private static Client elasticSearchClient;

    private static ElasticSearchDAOV5 indexDAO;

    private static EmbeddedElasticSearch embeddedElasticSearch;

    private Workflow workflow;

    @Test
    public void assertInitialSetup() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMww");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String taskLogIndex = "task_log_" + (dateFormat.format(new Date()));
        Assert.assertTrue("Index 'conductor' should exist", indexExists("conductor"));
        Assert.assertTrue((("Index '" + taskLogIndex) + "' should exist"), indexExists(taskLogIndex));
        Assert.assertTrue("Mapping 'workflow' for index 'conductor' should exist", doesMappingExist("conductor", "workflow"));
        Assert.assertTrue("Mapping 'task' for index 'conductor' should exist", doesMappingExist("conductor", "task"));
    }

    @Test
    public void testWorkflowCRUD() throws Exception {
        String testWorkflowType = "testworkflow";
        String testId = "1";
        workflow.setWorkflowId(testId);
        workflow.setWorkflowType(testWorkflowType);
        // Create
        String workflowType = TestElasticSearchDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertNull("Workflow should not exist", workflowType);
        // Get
        TestElasticSearchDAOV5.indexDAO.indexWorkflow(workflow);
        workflowType = TestElasticSearchDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertEquals("Should have found our workflow type", testWorkflowType, workflowType);
        // Update
        String newWorkflowType = "newworkflowtype";
        String[] keyChanges = new String[]{ "workflowType" };
        String[] valueChanges = new String[]{ newWorkflowType };
        TestElasticSearchDAOV5.indexDAO.updateWorkflow(testId, keyChanges, valueChanges);
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            String actualWorkflowType = TestElasticSearchDAOV5.indexDAO.get(testId, "workflowType");
            assertEquals("Should have updated our new workflow type", newWorkflowType, actualWorkflowType);
        });
        // Delete
        TestElasticSearchDAOV5.indexDAO.removeWorkflow(testId);
        workflowType = TestElasticSearchDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertNull("We should no longer have our workflow in the system", workflowType);
    }

    @Test
    public void testWorkflowSearch() {
        String workflowId = "search-workflow-id";
        workflow.setWorkflowId(workflowId);
        TestElasticSearchDAOV5.indexDAO.indexWorkflow(workflow);
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> searchIds = TestElasticSearchDAOV5.indexDAO.searchWorkflows("", (("workflowId:\"" + workflowId) + "\""), 0, 100, Collections.singletonList("workflowId:ASC")).getResults();
            assertEquals(1, searchIds.size());
            assertEquals(workflowId, searchIds.get(0));
        });
    }

    @Test
    public void testSearchRecentRunningWorkflows() {
        workflow.setWorkflowId("completed-workflow");
        workflow.setStatus(COMPLETED);
        TestElasticSearchDAOV5.indexDAO.indexWorkflow(workflow);
        String workflowId = "recent-running-workflow-id";
        workflow.setWorkflowId(workflowId);
        workflow.setStatus(RUNNING);
        workflow.setCreateTime(new Date().getTime());
        workflow.setUpdateTime(new Date().getTime());
        workflow.setEndTime(new Date().getTime());
        TestElasticSearchDAOV5.indexDAO.indexWorkflow(workflow);
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> searchIds = TestElasticSearchDAOV5.indexDAO.searchRecentRunningWorkflows(1, 0);
            assertEquals(1, searchIds.size());
            assertEquals(workflowId, searchIds.get(0));
        });
    }

    @Test
    public void testSearchArchivableWorkflows() {
        String workflowId = "search-workflow-id";
        workflow.setWorkflowId(workflowId);
        workflow.setStatus(COMPLETED);
        workflow.setCreateTime(new Date().getTime());
        workflow.setUpdateTime(new Date().getTime());
        workflow.setEndTime(new Date().getTime());
        TestElasticSearchDAOV5.indexDAO.indexWorkflow(workflow);
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> searchIds = TestElasticSearchDAOV5.indexDAO.searchArchivableWorkflows("conductor", 10);
            assertEquals(1, searchIds.size());
            assertEquals(workflowId, searchIds.get(0));
        });
    }

    @Test
    public void taskExecutionLogs() throws Exception {
        TaskExecLog taskExecLog1 = new TaskExecLog();
        taskExecLog1.setTaskId("some-task-id");
        long createdTime1 = LocalDateTime.of(2018, 11, 1, 6, 33, 22).toEpochSecond(ZoneOffset.UTC);
        taskExecLog1.setCreatedTime(createdTime1);
        taskExecLog1.setLog("some-log");
        TaskExecLog taskExecLog2 = new TaskExecLog();
        taskExecLog2.setTaskId("some-task-id");
        long createdTime2 = LocalDateTime.of(2018, 11, 1, 6, 33, 22).toEpochSecond(ZoneOffset.UTC);
        taskExecLog2.setCreatedTime(createdTime2);
        taskExecLog2.setLog("some-log");
        java.util.List<TaskExecLog> logsToAdd = Arrays.asList(taskExecLog1, taskExecLog2);
        TestElasticSearchDAOV5.indexDAO.addTaskExecutionLogs(logsToAdd);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<TaskExecLog> taskExecutionLogs = TestElasticSearchDAOV5.indexDAO.getTaskExecutionLogs("some-task-id");
            assertEquals(2, taskExecutionLogs.size());
        });
    }

    @Test
    public void indexTask() throws Exception {
        String correlationId = "some-correlation-id";
        Task task = new Task();
        task.setTaskId("some-task-id");
        task.setWorkflowInstanceId("some-workflow-instance-id");
        task.setTaskType("some-task-type");
        task.setStatus(FAILED);
        task.setInputData(new HashMap<String, Object>() {
            {
                put("input_key", "input_value");
            }
        });
        task.setCorrelationId(correlationId);
        task.setTaskDefName("some-task-def-name");
        task.setReasonForIncompletion("some-failure-reason");
        TestElasticSearchDAOV5.indexDAO.indexTask(task);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SearchResult<String> result = TestElasticSearchDAOV5.indexDAO.searchTasks((("correlationId='" + correlationId) + "'"), "*", 0, 10000, null);
            assertTrue("should return 1 or more search results", ((result.getResults().size()) > 0));
            assertEquals("taskId should match the indexed task", "some-task-id", result.getResults().get(0));
        });
    }

    @Test
    public void addMessage() {
        String messageId = "some-message-id";
        Message message = new Message();
        message.setId(messageId);
        message.setPayload("some-payload");
        message.setReceipt("some-receipt");
        TestElasticSearchDAOV5.indexDAO.addMessage("some-queue", message);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SearchResponse searchResponse = search(((LOG_INDEX_PREFIX) + "*"), (("messageId='" + messageId) + "'"), 0, 10000, "*", MSG_DOC_TYPE);
            assertEquals("search results should be length 1", searchResponse.getHits().getTotalHits(), 1);
            SearchHit searchHit = searchResponse.getHits().getAt(0);
            GetResponse response = TestElasticSearchDAOV5.elasticSearchClient.prepareGet(searchHit.getIndex(), MSG_DOC_TYPE, searchHit.getId()).get();
            assertEquals("indexed message id should match", messageId, response.getSource().get("messageId"));
            assertEquals("indexed payload should match", "some-payload", response.getSource().get("payload"));
        });
        java.util.List<Message> messages = TestElasticSearchDAOV5.indexDAO.getMessages("some-queue");
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals(message.getId(), messages.get(0).getId());
        Assert.assertEquals(message.getPayload(), messages.get(0).getPayload());
    }

    @Test
    public void addEventExecution() {
        String messageId = "some-message-id";
        EventExecution eventExecution = new EventExecution();
        eventExecution.setId("some-id");
        eventExecution.setMessageId(messageId);
        eventExecution.setAction(complete_task);
        eventExecution.setEvent("some-event");
        eventExecution.setStatus(EventExecution.Status.COMPLETED);
        TestElasticSearchDAOV5.indexDAO.addEventExecution(eventExecution);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SearchResponse searchResponse = search(((LOG_INDEX_PREFIX) + "*"), (("messageId='" + messageId) + "'"), 0, 10000, "*", EVENT_DOC_TYPE);
            assertEquals("search results should be length 1", searchResponse.getHits().getTotalHits(), 1);
            SearchHit searchHit = searchResponse.getHits().getAt(0);
            GetResponse response = TestElasticSearchDAOV5.elasticSearchClient.prepareGet(searchHit.getIndex(), EVENT_DOC_TYPE, searchHit.getId()).get();
            assertEquals("indexed message id should match", messageId, response.getSource().get("messageId"));
            assertEquals("indexed id should match", "some-id", response.getSource().get("id"));
            assertEquals("indexed status should match", EventExecution.Status.COMPLETED.name(), response.getSource().get("status"));
        });
        java.util.List<EventExecution> events = TestElasticSearchDAOV5.indexDAO.getEventExecutions("some-event");
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(eventExecution, events.get(0));
    }
}

