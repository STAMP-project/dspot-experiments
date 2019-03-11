package com.netflix.conductor.dao.es5.index;


import Status.FAILED;
import Type.complete_task;
import Workflow.WorkflowStatus.COMPLETED;
import Workflow.WorkflowStatus.RUNNING;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.events.EventExecution;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskExecLog;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.elasticsearch.ElasticSearchConfiguration;
import com.netflix.conductor.elasticsearch.EmbeddedElasticSearch;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestElasticSearchRestDAOV5 {
    private static final Logger logger = LoggerFactory.getLogger(TestElasticSearchRestDAOV5.class);

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMww");

    private static final String INDEX_NAME = "conductor";

    private static final String LOG_INDEX_PREFIX = "task_log";

    private static final String MSG_DOC_TYPE = "message";

    private static final String EVENT_DOC_TYPE = "event";

    private static ElasticSearchConfiguration configuration;

    private static RestClient restClient;

    private static RestHighLevelClient elasticSearchClient;

    private static ElasticSearchRestDAOV5 indexDAO;

    private static EmbeddedElasticSearch embeddedElasticSearch;

    private static ObjectMapper objectMapper;

    private Workflow workflow;

    private @interface HttpMethod {
        String GET = "GET";

        String POST = "POST";

        String PUT = "PUT";

        String HEAD = "HEAD";

        String DELETE = "DELETE";
    }

    @Test
    public void assertInitialSetup() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMww");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String taskLogIndex = "task_log_" + (dateFormat.format(new Date()));
        Assert.assertTrue("Index 'conductor' should exist", indexExists("conductor"));
        Assert.assertTrue((("Index '" + taskLogIndex) + "' should exist"), indexExists(taskLogIndex));
        Assert.assertTrue("Mapping 'workflow' for index 'conductor' should exist", doesMappingExist("conductor", "workflow"));
        Assert.assertTrue("Mapping 'task' for inndex 'conductor' should exist", doesMappingExist("conductor", "task"));
    }

    @Test
    public void testWorkflowCRUD() {
        String testWorkflowType = "testworkflow";
        String testId = "1";
        workflow.setWorkflowId(testId);
        workflow.setWorkflowType(testWorkflowType);
        // Create
        String workflowType = TestElasticSearchRestDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertNull("Workflow should not exist", workflowType);
        // Get
        TestElasticSearchRestDAOV5.indexDAO.indexWorkflow(workflow);
        workflowType = TestElasticSearchRestDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertEquals("Should have found our workflow type", testWorkflowType, workflowType);
        // Update
        String newWorkflowType = "newworkflowtype";
        String[] keyChanges = new String[]{ "workflowType" };
        String[] valueChanges = new String[]{ newWorkflowType };
        TestElasticSearchRestDAOV5.indexDAO.updateWorkflow(testId, keyChanges, valueChanges);
        workflowType = TestElasticSearchRestDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertEquals("Should have updated our new workflow type", newWorkflowType, workflowType);
        // Delete
        TestElasticSearchRestDAOV5.indexDAO.removeWorkflow(testId);
        workflowType = TestElasticSearchRestDAOV5.indexDAO.get(testId, "workflowType");
        Assert.assertNull("We should no longer have our workflow in the system", workflowType);
    }

    @Test
    public void testWorkflowSearch() {
        String workflowId = "search-workflow-id";
        workflow.setWorkflowId(workflowId);
        TestElasticSearchRestDAOV5.indexDAO.indexWorkflow(workflow);
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> searchIds = TestElasticSearchRestDAOV5.indexDAO.searchWorkflows("", (("workflowId:\"" + workflowId) + "\""), 0, 100, Collections.singletonList("workflowId:ASC")).getResults();
            assertEquals(1, searchIds.size());
            assertEquals(workflowId, searchIds.get(0));
        });
    }

    @Test
    public void testSearchRecentRunningWorkflows() {
        workflow.setWorkflowId("completed-workflow");
        workflow.setStatus(COMPLETED);
        TestElasticSearchRestDAOV5.indexDAO.indexWorkflow(workflow);
        String workflowId = "recent-running-workflow-id";
        workflow.setWorkflowId(workflowId);
        workflow.setStatus(RUNNING);
        workflow.setCreateTime(new Date().getTime());
        workflow.setUpdateTime(new Date().getTime());
        workflow.setEndTime(new Date().getTime());
        TestElasticSearchRestDAOV5.indexDAO.indexWorkflow(workflow);
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> searchIds = TestElasticSearchRestDAOV5.indexDAO.searchRecentRunningWorkflows(1, 0);
            assertEquals(1, searchIds.size());
            assertEquals(workflowId, searchIds.get(0));
        });
    }

    @Test
    public void testSearchArchivableWorkflows() throws IOException {
        String workflowId = "search-workflow-id";
        Long time = DateTime.now().minusDays(2).toDate().getTime();
        workflow.setWorkflowId(workflowId);
        workflow.setStatus(COMPLETED);
        workflow.setCreateTime(time);
        workflow.setUpdateTime(time);
        workflow.setEndTime(time);
        TestElasticSearchRestDAOV5.indexDAO.indexWorkflow(workflow);
        Assert.assertTrue(indexExists("conductor"));
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> searchIds = TestElasticSearchRestDAOV5.indexDAO.searchArchivableWorkflows("conductor", 1);
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
        TestElasticSearchRestDAOV5.indexDAO.addTaskExecutionLogs(logsToAdd);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<TaskExecLog> taskExecutionLogs = TestElasticSearchRestDAOV5.indexDAO.getTaskExecutionLogs("some-task-id");
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
        TestElasticSearchRestDAOV5.indexDAO.indexTask(task);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SearchResult<String> result = TestElasticSearchRestDAOV5.indexDAO.searchTasks((("correlationId='" + correlationId) + "'"), "*", 0, 10000, null);
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
        TestElasticSearchRestDAOV5.indexDAO.addMessage("some-queue", message);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SearchResponse searchResponse = searchObjectIdsViaExpression(((LOG_INDEX_PREFIX) + "*"), (("messageId='" + messageId) + "'"), 0, 10000, null, "*", MSG_DOC_TYPE);
            assertTrue("should return 1 or more search results", ((searchResponse.getHits().getTotalHits()) > 0));
            SearchHit searchHit = searchResponse.getHits().getAt(0);
            String resourcePath = String.format("/%s/%s/%s", searchHit.getIndex(), MSG_DOC_TYPE, searchHit.getId());
            Response response = TestElasticSearchRestDAOV5.restClient.performRequest(HttpMethod.GET, resourcePath);
            String responseBody = IOUtils.toString(response.getEntity().getContent());
            logger.info("responseBody: {}", responseBody);
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
            Map<String, Object> responseMap = TestElasticSearchRestDAOV5.objectMapper.readValue(responseBody, typeRef);
            Map<String, Object> source = ((Map<String, Object>) (responseMap.get("_source")));
            assertEquals("indexed message id should match", messageId, source.get("messageId"));
            assertEquals("indexed payload should match", "some-payload", source.get("payload"));
        });
        java.util.List<Message> messages = TestElasticSearchRestDAOV5.indexDAO.getMessages("some-queue");
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
        TestElasticSearchRestDAOV5.indexDAO.addEventExecution(eventExecution);
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            SearchResponse searchResponse = searchObjectIdsViaExpression(((LOG_INDEX_PREFIX) + "*"), (("messageId='" + messageId) + "'"), 0, 10000, null, "*", EVENT_DOC_TYPE);
            assertTrue("should return 1 or more search results", ((searchResponse.getHits().getTotalHits()) > 0));
            SearchHit searchHit = searchResponse.getHits().getAt(0);
            String resourcePath = String.format("/%s/%s/%s", searchHit.getIndex(), EVENT_DOC_TYPE, searchHit.getId());
            Response response = TestElasticSearchRestDAOV5.restClient.performRequest(HttpMethod.GET, resourcePath);
            String responseBody = IOUtils.toString(response.getEntity().getContent());
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
            Map<String, Object> responseMap = TestElasticSearchRestDAOV5.objectMapper.readValue(responseBody, typeRef);
            Map<String, Object> sourceMap = ((Map<String, Object>) (responseMap.get("_source")));
            assertEquals("indexed id should match", "some-id", sourceMap.get("id"));
            assertEquals("indexed message id should match", messageId, sourceMap.get("messageId"));
            assertEquals("indexed action should match", Type.complete_task.name(), sourceMap.get("action"));
            assertEquals("indexed event should match", "some-event", sourceMap.get("event"));
            assertEquals("indexed status should match", EventExecution.Status.COMPLETED.name(), sourceMap.get("status"));
        });
        java.util.List<EventExecution> events = TestElasticSearchRestDAOV5.indexDAO.getEventExecutions("some-event");
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(eventExecution, events.get(0));
    }
}

